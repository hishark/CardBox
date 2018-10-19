package com.example.mac.cardbox.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.mac.cardbox.R;
import com.example.mac.cardbox.bean.User;
import com.example.mac.cardbox.util.Constant;
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

public class SplashActivity extends AppCompatActivity {

    private static final int GO_LOGIN = 1;//去登录页
    private static final String TAG = "SplashActivity";
    private String searchUserByAccountUrl = "http://"+ Constant.Server_IP +":8080/CardBox-Server/SearchUserByAccount";
    private static final int AutoLogin_TAG = 2;

    /**
     * 跳转判断
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO_LOGIN://去登录页
                    Intent intent2 = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent2);
                    finish();
                    break;
                case AutoLogin_TAG:
                    User AutoLoginUser = (User)msg.obj;
                    CurrentUserUtil.setCurrentUser(AutoLoginUser);
                    Intent intent = new Intent(SplashActivity.this, MainNavigationActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());




        if (sp.getBoolean("IsLogin",false) == true)//自动登录判断，SharePrefences中有数据，则跳转到主页，没数据则跳转到登录页
        {
            AutoLogin(sp.getString("useraccount",""));
        } else {
            handler.sendEmptyMessageAtTime(GO_LOGIN, 2000);
        }
    }

    private void AutoLogin(String username) {
        Log.d(TAG, "AutoLogin: 进来啦");
        //开子线程访问服务器啦
        //实例化OkHttpClient
        OkHttpClient client = new OkHttpClient();
        //创建表单请求体
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("user_account", username);

        //创建Request对象
        Request request = new Request.Builder()
                .url(searchUserByAccountUrl)
                .post(formBody.build())
                .build();

        /**
         * Get的异步请求，不需要跟同步请求一样开启子线程
         * 但是回调方法还是在子线程中执行的
         * 所以要用到Handler传数据回主线程更新UI
         */
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            //回调的方法执行在子线程
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = "";
                if (response.isSuccessful()) {
                    //从服务器取到Json键值对
                    String temp = response.body().string();
                    Log.d(TAG, "onResponse: temp:" + temp);
                    try {
                        JSONObject jsonObject = new JSONObject(temp);
                        Message msg = new Message();

                        //利用Gson解析User
                        String userresult = jsonObject.get("SearchUser").toString();
                        Gson gson = new Gson();
                        User user = gson.fromJson(userresult, User.class);

                        //通过handler传递数据到主线程
                        msg.what = AutoLogin_TAG;
                        msg.obj = user;
                        handler.sendMessage(msg);


                    } catch (JSONException a) {

                    }
                } else {

                }


            }
        });
    }
}

