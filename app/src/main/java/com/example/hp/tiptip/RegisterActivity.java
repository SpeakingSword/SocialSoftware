package com.example.hp.tiptip;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    private String registerUrl = "http://10.0.2.2:8080/TipTip/registerAction";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initEditText();
    }

    private void initEditText(){
        userId = findViewById(R.id.registerUserId);
        userName = findViewById(R.id.registerUserName);
        userPassword = findViewById(R.id.registerUserPassword);
    }

    public void registerCheck(View v){
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
                            default:
                                break;
                        }
                    }
                });
            }
        });
    }
}