package com.example.hp.tiptip;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Friend_infoActivity extends AppCompatActivity {
    private TextView friendId;
    private TextView friendName;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
                JSONObject jsonObject = (JSONObject) msg.obj;
                try {
                    friendName.setText(friendName.getText().toString()+jsonObject.getString("friendName"));
                }catch (JSONException e){
                    Log.d("TAG", "handleMessage: "+e.getMessage());
                }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_info);
        initVariable();

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        showFriendInfo();
    }

    void initVariable(){
        friendId = findViewById(R.id.friendId);
        friendName = findViewById(R.id.friendName);
    }

    private void showFriendInfo(){
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        friendId.setText(friendId.getText().toString()+bundle.getString("friendId"));

        OkHttpClient client = new OkHttpClient.Builder().build();

        RequestBody post = new FormBody.Builder()
                .add("friendId", bundle.getString("friendId"))
                .build();

        Request request = new Request.Builder()
                .url(Urls.GET_FRIEND_INFO_URL)
                .post(post)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Friend_infoActivity.this, "请求失败...", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Message msg = new Message();
                            msg.obj = new JSONObject(responseData.toString());
                            handler.sendMessage(msg);
                        }catch (JSONException e){
                            Log.d("TAG", "onResponse: "+e.getMessage());
                        }
                    }
                });
            }
        });
    }
}
