package com.example.hp.tiptip;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;

public class Message extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
    }

    public void logoutCheck(View view){
            doLogout();
    }

    private void doLogout(){
        NIMClient.getService(AuthService.class).logout();
        startActivity(new Intent(Message.this,Login.class));
        finish();
    }
}
