package com.example.mac.cardbox.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.mac.cardbox.R;
import com.example.mac.cardbox.bean.User;
import com.example.mac.cardbox.util.CurrentUserUtil;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private long LastClickTime;
    private String searchUserByAccountUrl = "http://192.168.137.1:8080/CardBox-Server/SearchUserByAccount";
    private static final String TAG = "MainActivity";
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
        setContentView(R.layout.activity_main);
        String test = CurrentUserUtil.getCurrentUser().getUser_account();
        Toast.makeText(MainActivity.this, test, Toast.LENGTH_SHORT).show();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_add:
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

                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();

                break;
            case R.id.item_remove:
                Toast.makeText(this, "You clicked ...!", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
