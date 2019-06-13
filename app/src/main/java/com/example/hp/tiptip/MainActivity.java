package com.example.hp.tiptip;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MessageFragment.OnFragmentInteractionListener,
        ContactFragment.OnFragmentInteractionListener,User_infoFragment.OnFragmentInteractionListener{


    private MessageFragment messageFragment;
    private ContactFragment contactFragment;
    private User_infoFragment userInfoFragment;
    private Fragment isFrament;

    Observer<List<IMMessage>> incomingMessageObserver =
            new Observer<List<IMMessage>>() {
                @Override
                public void onEvent(List<IMMessage> messages) {
                    // 处理新收到的消息，为了上传处理方便，SDK 保证参数 messages 全部来自同一个聊天对象。
                    for (IMMessage message : messages){
                        if (message.getMsgType() == MsgTypeEnum.text){

                            ACache aCache = ACache.get(MainActivity.this);
                            String receiver_id = aCache.getAsString("userId");
                            String original_id = message.getFromAccount();
                            String head = (original_id.charAt(0)+"").toUpperCase();
                            String body = (original_id.substring(1));
                            String sender_id = head+body;
                            String content = message.getContent();

                            DBHelper dbHelper = new DBHelper(MainActivity.this);
                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                            db.execSQL("insert into chat_record_of_friend(sender_id,receiver_id,content,content_type,build_time) " +
                                    "values(?,?,?,'TEXT',datetime('now'))",new Object[]{sender_id,receiver_id,content});

                            db.close();
                            messageFragment.setRecentContactList();
                        }
                    }
                }
            };

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_message:
                    if (messageFragment == null){
                        messageFragment = new MessageFragment();
                    }
                    switchContent(isFrament, messageFragment);
                    return true;
                case R.id.navigation_contact:
                    if (contactFragment == null){
                        contactFragment = new ContactFragment();
                    }
                    switchContent(isFrament,contactFragment);
                    return true;
                case R.id.navigation_user_info:
                    if (userInfoFragment == null){
                        userInfoFragment = new User_infoFragment();
                    }
                    switchContent(isFrament, userInfoFragment);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initeFragment(savedInstanceState);
        BottomNavigationView navigation =  findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        NIMClient.getService(MsgServiceObserve.class)
                .observeReceiveMessage(incomingMessageObserver, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NIMClient.getService(MsgServiceObserve.class)
                .observeReceiveMessage(incomingMessageObserver, false);
    }

    private void initeFragment(Bundle savedInstanceState){
        if(savedInstanceState == null){
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            if (messageFragment == null){
                messageFragment = new MessageFragment();
            }
            isFrament = messageFragment;
            ft.replace(R.id.container, messageFragment).commit();
        }
    }

    private void switchContent(Fragment form, Fragment to){
        if (isFrament != to){
            isFrament = to;
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            if (!to.isAdded()){
                ft.hide(form).add(R.id.container,to).commit();
            }else{
                ft.hide(form).show(to).commit();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.addFriend :
                startActivity(new Intent(MainActivity.this,AddFriendActivity.class));
                break;
            case R.id.existApp :
                NIMClient.getService(AuthService.class).logout();
                MainActivity.this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onContactFragmentInteraction(Uri uri) {

    }

    @Override
    public void onMessageFragmentInteraction(Uri uri) {


    }

    @Override
    public void onUser_infoFragmentInteraction(Uri uri) {

    }

}
