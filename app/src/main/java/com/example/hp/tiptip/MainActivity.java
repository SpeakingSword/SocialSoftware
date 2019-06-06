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

public class MainActivity extends AppCompatActivity implements MessageFragment.OnFragmentInteractionListener,
        ContactFragment.OnFragmentInteractionListener,User_infoFragment.OnFragmentInteractionListener{


    private MessageFragment messageFragment;
    private ContactFragment contactFragment;
    private User_infoFragment userInfoFragment;
    private Fragment isFrament;

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
            case R.id.setting :
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
