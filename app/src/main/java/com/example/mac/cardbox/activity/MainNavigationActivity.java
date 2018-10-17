package com.example.mac.cardbox.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.mac.cardbox.R;
import com.example.mac.cardbox.bean.User;
import com.example.mac.cardbox.util.Constant;
import com.example.mac.cardbox.util.CurrentUserUtil;

public class MainNavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private long LastClickTime;
    private String searchUserByAccountUrl = "http://" + Constant.Local_Server_IP + ":8080/CardBox-Server/SearchUserByAccount";
    private static final String TAG = "MainNavigationActivity";
    private static final int AutoLogin_TAG = 1;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;



    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

            }

            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navigation);

        //标题栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //悬浮按钮
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //抽屉
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //侧滑栏
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //。。。
        String test = CurrentUserUtil.getCurrentUser().getUser_account();
        Toast.makeText(MainNavigationActivity.this, test, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onCreate: "+test);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();


    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //距离上次按返回键如果超过2000ms就认为是误操作，toast提醒一下，并更新LastClickTime
            if ((System.currentTimeMillis() - LastClickTime) > 2000) {
                Toast.makeText(this, "再按一次“返回”退出应用程序", Toast.LENGTH_SHORT).show();
                LastClickTime = System.currentTimeMillis();
            } else {
                //退出程序
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.DotNavigation_logout) {
            Toast.makeText(this, "You clicked to log out!", Toast.LENGTH_SHORT).show();
            User user = new User();
            CurrentUserUtil.setCurrentUser(user);

            editor.putBoolean("IsLogin",false);
            editor.apply();

            if(sharedPreferences.getBoolean("IsLogin",false)==true){
                Log.d(TAG, "handleMessage: IsLogin = true");
            }else{
                Log.d(TAG, "handleMessage: IsLogin = false");
            }

            Intent intent = new Intent(MainNavigationActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void Log_Out(){
        //Toast.makeText(this, "You clicked to log out!", Toast.LENGTH_SHORT).show();
        User user = new User();
        CurrentUserUtil.setCurrentUser(user);

        editor.putBoolean("IsLogin",false);
        editor.apply();

        if(sharedPreferences.getBoolean("IsLogin",false)==true){
            Log.d(TAG, "handleMessage: IsLogin = true");
        }else{
            Log.d(TAG, "handleMessage: IsLogin = false");
        }

        Intent intent = new Intent(MainNavigationActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            Log_Out();
        } else if (id == R.id.nav_mycardbox) {

        } else if (id == R.id.nav_mylove) {

        } else if (id == R.id.nav_search) {

        } else if (id == R.id.nav_notification) {

        } else if (id == R.id.nav_about) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
