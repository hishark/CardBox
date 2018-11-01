package com.example.mac.cardbox.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.example.mac.cardbox.R;
import com.example.mac.cardbox.adapter.SearchBoxerAdapter;
import com.example.mac.cardbox.adapter.UserRelationAdapter;
import com.example.mac.cardbox.bean.User;
import com.example.mac.cardbox.util.Constant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserRelationActivity extends AppCompatActivity {

    private String RelationType;
    private List<User> followUserList;
    private List<User> followerUserList;
    private RecyclerView recyclerView;
    private User searchUser;
    private LinearLayout ll_NoFollow;
    private LinearLayout ll_NoFollower;

    private static final String GetAllFollowUrl = "http://" + Constant.Server_IP + ":8080/CardBox-Server/GetAllFollow";
    private static final String GetAllFollowerUrl = "http://" + Constant.Server_IP + ":8080/CardBox-Server/GetAllFollower";
    private static final String TAG = "UserRelationActivity";
    private static final int GetAllFollowSuccess_TAG = 1;
    private static final int GetAllFollowerSuccess_TAG = 2;
    private static final int GetAllFollowFail_TAG = 3;
    private static final int GetAllFollowerFail_TAG = 4;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GetAllFollowSuccess_TAG:
                    followUserList = (List<User>) msg.obj;
                    if(followUserList.size()!=0) {
                        showAllUser(followUserList);
                        ll_NoFollow.setVisibility(View.INVISIBLE);
                    } else {
                        ll_NoFollow.setVisibility(View.VISIBLE);
                        showAllUser(followUserList);
                    }
                    break;
                case GetAllFollowerSuccess_TAG:
                    followerUserList = (List<User>) msg.obj;
                    //判断一下用户列表不为空再展示，为空就调侃一哈
                    if (followerUserList.size()!=0) {
                        ll_NoFollower.setVisibility(View.INVISIBLE);
                        showAllUser(followerUserList);
                    } else {
                        ll_NoFollower.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_relation);

        //自定义标题栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_UserRelation);
        setSupportActionBar(toolbar);

        //取消原有标题
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //初始化
        initView();




    }

    @Override
    protected void onResume() {
        //判断是关注还是粉丝,然后再设置标题显示内容，搜索相应用户数据
        RelationType = getIntent().getStringExtra("RelationType");
        if(RelationType.equals("Follow")) {
            getSupportActionBar().setTitle("关注");
            searchUser = (User)getIntent().getSerializableExtra("SearchUser");
            GetAllFollow(searchUser.getUser_account());
        } else {
            getSupportActionBar().setTitle("粉丝");
            searchUser = (User)getIntent().getSerializableExtra("SearchUser");
            GetAllFollower(searchUser.getUser_account());
        }
        super.onResume();
    }

    private void initView() {
        recyclerView = findViewById(R.id.recyclerview_UserRelationList);
        ll_NoFollow = findViewById(R.id.userrelation_noFollow);
        ll_NoFollower = findViewById(R.id.userrelation_noFollower);
    }

    private void showAllUser(List<User> userList) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        UserRelationAdapter userRelationAdapter = new UserRelationAdapter(userList, getApplicationContext());
        recyclerView.setAdapter(userRelationAdapter);
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_boxdetail, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    private void GetAllFollow(String user_account) {
        //开子线程访问服务器啦
        //实例化OkHttpClient
        OkHttpClient client = new OkHttpClient();
        //创建表单请求体
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("user_account", user_account);

        //创建Request对象
        Request request = new Request.Builder()
                .url(GetAllFollowUrl)
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

                    //利用Gson解析服务器List转换成的json字符串
                    Gson gson = new Gson();
                    List<User> userList = gson.fromJson(temp, new TypeToken<List<User>>() {
                    }.getType());

                    Message msg = new Message();
                    msg.obj = userList;
                    msg.what = GetAllFollowSuccess_TAG;
                    handler.sendMessage(msg);
                } else {

                }


            }
        });
    }

    private void GetAllFollower(String user_account) {
        //开子线程访问服务器啦
        //实例化OkHttpClient
        OkHttpClient client = new OkHttpClient();
        //创建表单请求体
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("user_account", user_account);

        //创建Request对象
        Request request = new Request.Builder()
                .url(GetAllFollowerUrl)
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

                    //利用Gson解析服务器List转换成的json字符串
                    Gson gson = new Gson();
                    List<User> userList = gson.fromJson(temp, new TypeToken<List<User>>() {
                    }.getType());

                    Message msg = new Message();
                    msg.obj = userList;
                    msg.what = GetAllFollowerSuccess_TAG;
                    Log.d(TAG, "onResponse: 啊成功");
                    handler.sendMessage(msg);
                } else {

                }


            }
        });
    }
}
