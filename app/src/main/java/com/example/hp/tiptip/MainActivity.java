package com.example.hp.tiptip;

import android.content.Intent;
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
import com.netease.nimlib.sdk.auth.AuthService;

public class MainActivity extends AppCompatActivity implements NotificationFragment.OnFragmentInteractionListener,
        ContactFragment.OnFragmentInteractionListener,SettingFragment.OnFragmentInteractionListener{


    private NotificationFragment notificationFragment;
    private ContactFragment contactFragment;
    private SettingFragment settingFragment;
    private Fragment isFrament;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if (notificationFragment == null){
                        notificationFragment = new NotificationFragment();
                    }
                    switchContent(isFrament,notificationFragment);
                    return true;
                case R.id.navigation_dashboard:
                    if (contactFragment == null){
                        contactFragment = new ContactFragment();
                    }
                    switchContent(isFrament,contactFragment);
                    return true;
                case R.id.navigation_notifications:
                    if (settingFragment == null){
                        settingFragment = new SettingFragment();
                    }
                    switchContent(isFrament,settingFragment);
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
    }

    private void initeFragment(Bundle savedInstanceState){
        if(savedInstanceState == null){
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            if (notificationFragment == null){
                notificationFragment = new NotificationFragment();
            }
            isFrament = notificationFragment;
            ft.replace(R.id.container,notificationFragment).commit();
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
                break;
            case R.id.setting :
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onContactFragmentInteraction(Uri uri) {

    }

    @Override
    public void onNotificationFragmentInteraction(Uri uri) {

    }

    @Override
    public void onSettingFragmentInteraction(Uri uri) {

    }
}
