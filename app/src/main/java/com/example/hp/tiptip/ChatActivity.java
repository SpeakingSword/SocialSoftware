package com.example.hp.tiptip;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
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

                        }
                    }
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setCustomActionBar();
        initVariable();
        //initMsgs();
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
                ActionBar.LayoutParams.MATCH_PARENT,Gravity.CENTER);
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgReView.setLayoutManager(layoutManager);
        adapter = new MsgAdapter(msgList);
        msgReView.setAdapter(adapter);

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

   /* private void initMsgs() {
        Msg msg1 = new Msg("Hello guy.", Msg.TYPE_RECEIVED);
        msgList.add(msg1);
        Msg msg2 = new Msg("Hello. Who is that?", Msg.TYPE_SENT);
        msgList.add(msg2);
        Msg msg3 = new Msg("This is Tom. Nice talking to you. ", Msg.TYPE_RECEIVED);
        msgList.add(msg3);
    }
    */

    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    */

    public void goBackInfo(View view){
        this.finish();
    }
}
