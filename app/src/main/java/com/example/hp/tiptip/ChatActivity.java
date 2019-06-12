package com.example.hp.tiptip;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private List<Msg> msgList = new ArrayList<Msg>();
    private EditText msg_chat;
    private Button send;
    private RecyclerView msgReView;
    private MsgAdapter adapter;
    private TextView friendId;

    private List<ChatRecord> chatRecordList = new ArrayList<ChatRecord>();

    Observer<List<IMMessage>> incomingMessageObserver =
            new Observer<List<IMMessage>>() {
                @Override
                public void onEvent(List<IMMessage> messages) {
                    // 处理新收到的消息，为了上传处理方便，SDK 保证参数 messages 全部来自同一个聊天对象。
                    for (IMMessage message : messages){
                        if (message.getMsgType() == MsgTypeEnum.text){

                            Msg msg = new Msg(message.getContent(), Msg.TYPE_RECEIVED);

                            msgList.add(msg);
                            // 当有新消息时，刷新ListView中的显示
                            adapter.notifyItemInserted(msgList.size() - 1);
                            // 将ListView定位到最后一行
                            msgReView.scrollToPosition(msgList.size() - 1);

                            ACache aCache = ACache.get(ChatActivity.this);
                            ChatRecord chatRecord = new ChatRecord();
                            String receiver_id = aCache.getAsString("userId");
                            chatRecord.setSenderId(friendId.getText().toString());
                            chatRecord.setReceiverId(receiver_id);
                            chatRecord.setContent(message.getContent());
                            chatRecordList.add(chatRecord);

                        }
                    }
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ACache aCache = ACache.get(ChatActivity.this);
        setCustomActionBar();
        initVariable();
        initMsgs(aCache.getAsString("userId"),friendId.getText().toString());
        NIMClient.getService(MsgServiceObserve.class)
                .observeReceiveMessage(incomingMessageObserver, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NIMClient.getService(MsgServiceObserve.class)
                .observeReceiveMessage(incomingMessageObserver, false);
    }

    private void setCustomActionBar(){
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT,Gravity.TOP);
        View mActionBarView = LayoutInflater.from(this).inflate(R.layout.chat_actionbar,null);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(mActionBarView,layoutParams);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    private void initVariable(){
        msg_chat =  findViewById(R.id.msg_chat);
        send =  findViewById(R.id.send);
        msgReView =  findViewById(R.id.msg_view);
        friendId = findViewById(R.id.chat_friendId);
        friendId.setText(getFriendId());

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = msg_chat.getText().toString();
                if (!"".equals(content)) {

                    sendTextIMMessage(friendId.getText().toString(),content);

                }
            }
        });
    }

    private String getFriendId(){
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        return bundle.getString("friendId");
    }

    private void sendTextIMMessage(String sessionId, final String mContent){
        IMMessage message = MessageBuilder.createTextMessage(sessionId,SessionTypeEnum.P2P,mContent);
        NIMClient.getService(MsgService.class).sendMessage(message, false).setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void param) {

                Msg msg = new Msg(mContent, Msg.TYPE_SENT);
                msgList.add(msg);
                // 当有新消息时，刷新ListView中的显示
                adapter.notifyItemInserted(msgList.size() - 1);
                // 将ListView定位到最后一行
                msgReView.scrollToPosition(msgList.size() - 1);
                // 清空输入框中的内容
                msg_chat.setText("");
                Toast.makeText(ChatActivity.this, "发送成功", Toast.LENGTH_SHORT).show();

                //先保存来填记录到List里
                ACache aCache = ACache.get(ChatActivity.this);
                ChatRecord chatRecord = new ChatRecord();
                String sender_id = aCache.getAsString("userId");
                chatRecord.setSenderId(sender_id);
                chatRecord.setReceiverId(friendId.getText().toString());
                chatRecord.setContent(mContent);
                chatRecordList.add(chatRecord);
            }

            @Override
            public void onFailed(int code) {
                Toast.makeText(ChatActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onException(Throwable exception) {
                Toast.makeText(ChatActivity.this, "异常...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initMsgs(String userId, String friendId) {
        DBHelper dbHelper = new DBHelper(ChatActivity.this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.rawQuery("select sender_id,receiver_id,content from chat_record_of_friend where sender_id ="+
                "'"+userId+"'"+" and receiver_id ="+"'"+friendId+"'"+" or sender_id ="+"'"+friendId+"'"+" and receiver_id ="+
                "'"+userId+"'"+" order by build_time asc;",null);
        while(c.moveToNext()){
            String content = c.getString(2);
            String sender = c.getString(0);
            int msgType = sender.equals(userId) ? Msg.TYPE_SENT : Msg.TYPE_RECEIVED;
            Msg msg = new Msg(content,msgType);
            msgList.add(msg);
        }
        c.close();
        db.close();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgReView.setLayoutManager(layoutManager);
        adapter = new MsgAdapter(msgList);
        msgReView.setAdapter(adapter);
        adapter.notifyItemInserted(msgList.size() - 1);
        msgReView.scrollToPosition(msgList.size() - 1);
    }


    public void goBackInfo(View view){
        if (chatRecordList.isEmpty()){
            this.finish();
        }else{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DBHelper dbHelper = new DBHelper(ChatActivity.this);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();

                    for(ChatRecord chatRecord : chatRecordList){
                        db.execSQL("insert into chat_record_of_friend(sender_id,receiver_id,content,content_type,build_time) " +
                                "values(?,?,?,'TEXT',datetime('now'))", new Object[]{chatRecord.getSenderId(),chatRecord.getReceiverId(),
                                chatRecord.getContent()});
                    }
                    db.close();
                }
            });
            this.finish();
        }
    }
}
