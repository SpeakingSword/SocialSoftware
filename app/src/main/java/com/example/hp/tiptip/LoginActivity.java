package com.example.hp.tiptip;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Handler;
import android.os.Message;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText userId;
    private EditText userPassword;
    private String loginUrl = Urls.LOGIN_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initEditText();
    }

    private void initEditText(){
        userId = findViewById(R.id.loginUserId);
        userPassword = findViewById(R.id.loginUserPassword);
    }

    public void loginCheck(View v){
        doServerLogin(loginUrl);
    }

    public void goToRegister(View v){
        startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
    }

    private void doServerLogin(String url){
        //初始化okhttp客户端
        OkHttpClient client = new OkHttpClient.Builder().build();
        //创建POST表单，获取username和password
        RequestBody post = new FormBody.Builder()
                .add("userId",userId.getText().toString())
                .add("userPassword",userPassword.getText().toString())
                .build();
        //开始请求，填入url和表单
        Request request = new Request.Builder()
                .url(url)
                .post(post)
                .build();
        //Toast.makeText(this, "已经填入表单和url", Toast.LENGTH_SHORT).show();
        Call call = client.newCall(request);
        //客户端回调
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                  runOnUiThread(new Runnable() {
                      @Override
                      public void run() {
                          Toast.makeText(LoginActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                      }
                  });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //请求成功的处理
                final String responseData = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String result = new String();
                        String token = new String();
                       try{
                           JSONObject jsonObject = new JSONObject(responseData);
                           result = jsonObject.getString("result");
                           token = jsonObject.getString("token");
                       }catch (JSONException e){
                           Log.e("TAG","error:"+e.getMessage());
                       }

                       switch (result){
                           case "firstGetToken" :
                           case "haveToken" :
                               ACache aCache = ACache.get(LoginActivity.this);
                               aCache.put("userId",userId.getText().toString());
                               aCache.put("token",token);
                               doIMLogin(userId.getText().toString(),token);
                               break;
                           case "wrongCode" :
                               Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                               break;
                           case "noSuchUser" :
                               Toast.makeText(LoginActivity.this, "用户不存在", Toast.LENGTH_SHORT).show();
                               break;
                           default:
                               break;
                       }
                    }
                });
            }
        });
    }

    private void doIMLogin(String userId, String token){
        LoginInfo info = new LoginInfo(userId,token); // config...
        RequestCallback<LoginInfo> callback =
                new RequestCallback<LoginInfo>() {
                    @Override
                    public void onSuccess(LoginInfo param) {
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onFailed(int code) {
                        if (code == 302) {
                            Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                        } else if (code == 408) {
                            Toast.makeText(LoginActivity.this, "登录超时", Toast.LENGTH_SHORT).show();
                        } else if (code == 415) {
                            Toast.makeText(LoginActivity.this, "未开网络", Toast.LENGTH_SHORT).show();
                        } else if (code == 416) {
                            Toast.makeText(LoginActivity.this, "连接有误，请稍后重试", Toast.LENGTH_SHORT).show();
                        } else if (code == 417) {
                            Toast.makeText(LoginActivity.this, "该账号已在另一端登录", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "未知错误，请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onException(Throwable exception) {
                        Toast.makeText(LoginActivity.this, "登录异常", Toast.LENGTH_SHORT).show();
                    }
                    // 可以在此保存LoginInfo到本地，下次启动APP做自动登录用
                };
        NIMClient.getService(AuthService.class).login(info)
                .setCallback(callback);
    }
}
