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

public class AddFriendActivity extends AppCompatActivity {
    private String addFriendUrl = Urls.addFriendUrl;
    private String userId;
    private EditText friendId;
    private ACache aCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        initVariables();
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initVariables(){
        friendId = findViewById(R.id.inputFriendId);
        aCache = ACache.get(AddFriendActivity.this);
        userId = aCache.getAsString("userId");
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

    public void addFriendCheck(View view){
        OkHttpClient client = new OkHttpClient.Builder().build();

        RequestBody post = new FormBody.Builder()
                .add("userId",userId)
                .add("friendId",friendId.getText().toString())
                .build();

        Request request = new Request.Builder()
                .url(addFriendUrl)
                .post(post)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AddFriendActivity.this, "操作异常：请重新添加", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (responseData){
                            case "addFriendSuccess" :
                                Toast.makeText(AddFriendActivity.this, "添加好友成功", Toast.LENGTH_SHORT).show();
                                 break;
                            case "noSuchId" :
                                Toast.makeText(AddFriendActivity.this, "用户不存在", Toast.LENGTH_SHORT).show();
                                break;
                            case "friendExist" :
                                Toast.makeText(AddFriendActivity.this, "好友已存在", Toast.LENGTH_SHORT).show();
                                break;
                            case "addFriendFail" :
                                Toast.makeText(AddFriendActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
            }
        });
    }
}
