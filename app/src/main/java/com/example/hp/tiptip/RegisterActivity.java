package com.example.hp.tiptip;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText userId;
    private EditText userName;
    private EditText userPassword;
    private String registerUrl = Urls.REGISTER_URL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initEditText();
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initEditText(){
        userId = findViewById(R.id.registerUserId);
        userName = findViewById(R.id.registerUserName);
        userPassword = findViewById(R.id.registerUserPassword);
    }

    public void goBackLogin(View view){
       finish();
    }

    public void registerCheck(View v){

        if(userId.getText().toString().length() < 5){
            Toast.makeText(this, "账号长度应大于5位", Toast.LENGTH_SHORT).show();
            return;
        }
        OkHttpClient client = new OkHttpClient.Builder().build();

        RequestBody post = new FormBody.Builder()
                .add("userId",userId.getText().toString())
                .add("userName",userName.getText().toString())
                .add("userPassword",userPassword.getText().toString())
                .build();

        Request request = new Request.Builder()
                .url(registerUrl)
                .post(post)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RegisterActivity.this, "请求失败...", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch(responseData){
                            case "registerFail" :
                                Toast.makeText(RegisterActivity.this, "注册失败...", Toast.LENGTH_SHORT).show();
                                break;
                            case "registerSuccess" :
                                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                break;
                            case "idExist" :
                                Toast.makeText(RegisterActivity.this, "用户已存在", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
            }
        });
    }


}
