package com.example.hp.tiptip;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ModifyUser_infoActivity extends AppCompatActivity {

    private EditText set_username;
    private RadioGroup set_sex;
    private RadioButton BtnFemale;
    private RadioButton BtnMale;
    private EditText set_birthday;
    private EditText set_phone;
    private EditText  set_email;
    private EditText set_sign;
    private Button btn_save;
    private  String sex;
    private String url =Urls.MODIFY_USER_INFO_URL;

    private String userName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_user_info);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_left_arrow);
        }
        set_username = (EditText) findViewById(R.id.set_username);
        set_birthday = (EditText) findViewById(R.id.set_birthday);
        set_phone =(EditText)  findViewById(R.id.set_phone);
        set_email = (EditText) findViewById(R.id.set_email);
        set_sign = (EditText) findViewById(R.id.set_sign);

        set_sex = (RadioGroup) findViewById(R.id.set_sex);
        BtnFemale =(RadioButton) findViewById(R.id.BtnFemale);
        //BtnMale  =  (RadioButton) findViewById(R.id.BtnMale);
        BtnFemale.setChecked(true);
        sendDate();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(ModifyUser_infoActivity.this,MainActivity.class));
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void sendDate(){

        btn_save = (Button) findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                userName = set_username.getText().toString().trim();
                if (TextUtils.isEmpty(userName)) {
                    Toast.makeText(ModifyUser_infoActivity.this, "请输入昵称", Toast.LENGTH_SHORT).show();
                } else {

                    int sexChoseId = set_sex.getCheckedRadioButtonId();
                    switch (sexChoseId) {
                        case R.id.BtnFemale:
                            sex = "Female";
                            break;
                        case R.id.BtnMale:
                            sex = "Male";
                            break;
                        default:
                            sex = "";
                            break;
                    }

                    ACache aCache = ACache.get(ModifyUser_infoActivity.this);
                    OkHttpClient Client = new OkHttpClient();
                    RequestBody Body = new FormBody.Builder()
                            .add("user_id",aCache.getAsString("userId"))
                            .add("username", set_username.getText().toString())
                            .add("sex", sex)
                            .add("birthday", set_birthday.getText().toString())
                            .add("phone", set_phone.getText().toString())
                            .add("email", set_email.getText().toString())
                            .add("sign", set_sign.getText().toString())
                            .build();
                    Request request = new Request.Builder()
                            // .addHeader("Content-Type", "text/html")
                            //.addHeader("charset",HTTP.UTF_8)
                            .url(url)
                            .post(Body)
                            .build();

                    Client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ModifyUser_infoActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ModifyUser_infoActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(ModifyUser_infoActivity.this,MainActivity.class));
                                    finish();
                                }
                            });

                        }
                    });


                }
            }

        });


    }
}
