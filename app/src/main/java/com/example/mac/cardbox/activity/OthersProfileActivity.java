package com.example.mac.cardbox.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.example.mac.cardbox.R;
import com.example.mac.cardbox.adapter.MyBoxAdapter;
import com.example.mac.cardbox.adapter.OthersProfileBoxAdapter;
import com.example.mac.cardbox.bean.Box;
import com.example.mac.cardbox.bean.BoxFavourite;
import com.example.mac.cardbox.bean.User;
import com.example.mac.cardbox.bean.UserRelation;
import com.example.mac.cardbox.util.Constant;
import com.example.mac.cardbox.util.CurrentUserUtil;
import com.google.gson.Gson;

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
import okhttp3.RequestBody;
import okhttp3.Response;

public class OthersProfileActivity extends AppCompatActivity {

    private User currentUser;
    private ImageView userAvatar;
    private RecyclerView recyclerView;
    private FloatingActionButton fab_follow;
    private ImageButton button_sendMsg;
    private TextView tv_followcount;
    private TextView tv_followercount;
    private TextView tv_boxcount;
    private LinearLayout ll_follow, ll_follower;
    private EditText dialog_et_Message;
    private ImageButton dialog_button_sendMsg;
    private AlertDialog dialog;

    private static final String searchBoxByUserAccountUrl = "http://" + Constant.Server_IP + ":8080/CardBox-Server/searchBoxByUserAccount";
    private static final String AddUserRelationUrl = "http://" + Constant.Server_IP + ":8080/CardBox-Server/AddUserRelation";
    private static final String DeleteUserRelationUrl = "http://" + Constant.Server_IP + ":8080/CardBox-Server/DeleteUserRelation";
    private static final String GetFollowCountUrl = "http://" + Constant.Server_IP + ":8080/CardBox-Server/GetFollowCount";
    private static final String GetFollowerCountUrl = "http://" + Constant.Server_IP + ":8080/CardBox-Server/GetFollowerCount";
    private static final String AddUserMessageUrl = "http://" + Constant.Server_IP + ":8080/CardBox-Server/AddUserMessage";

    private static final String TAG = "OthersProfileActivity";
    private static final int SearchBoxSuccess_TAG = 1;
    private static final int SearchBoxFail_TAG = 2;
    private static final int AddUserRelationSuccess_TAG = 3;
    private static final int AddUserRelationFail_TAG = 4;
    private static final int DeleteUserRelationSuccess_TAG = 5;
    private static final int GetFollowCountSuccess_TAG = 6;
    private static final int GetFollowerCountSuccess_TAG = 7;
    private static final int AddUserMessageSuccess_TAG = 8;
    private static final int AddUserMessageFail_TAG = 9;

    private List<HashMap<String, Object>> boxlist = null;
    private HashMap<String, Object> box = null;
    private List<HashMap<String, Object>> list;
    private List<Box> boxes;
    private OthersProfileBoxAdapter othersProfileBoxAdapte;
    private UserRelation userRelation;
    private int followCount;
    private int followerCount;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SearchBoxSuccess_TAG:
                    list = (List<HashMap<String, Object>>) msg.obj;
                    boxes = changeHashMapToBox(list);
                    showAllSearchBox(boxes);
                    tv_boxcount.setText(String.valueOf(boxes.size()));
                    Log.d(TAG, "handleMessage: hello");
                    break;
                case SearchBoxFail_TAG:
                    break;
                case AddUserRelationSuccess_TAG:
                    Snackbar.make(recyclerView, "关注成功", BaseTransientBottomBar.LENGTH_SHORT).show();
                    //成功关注之后火速更新顶上的数字
                    initTopView();
                    break;
                case AddUserRelationFail_TAG:
                    Snackbar.make(recyclerView, "你已经关注TA啦~", BaseTransientBottomBar.LENGTH_SHORT)
                            .setAction("取消关注", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    userRelation = new UserRelation();
                                    userRelation.setUser_follow(CurrentUserUtil.getCurrentUser());
                                    userRelation.setUser_befollowed(currentUser);
                                    CancelUserRelation(userRelation);
                                }
                            })
                            .setActionTextColor(Color.parseColor("#f2c062"))
                            .show();
                    break;
                case DeleteUserRelationSuccess_TAG:
                    Snackbar.make(recyclerView, "已取消关注（；´д｀）ゞ", BaseTransientBottomBar.LENGTH_SHORT).show();
                    //取消关注之后火速更新顶上的数字
                    initTopView();
                    break;
                case GetFollowCountSuccess_TAG:
                    followCount = (int) msg.obj;
                    tv_followcount.setText(String.valueOf(followCount));
                    break;
                case GetFollowerCountSuccess_TAG:
                    followerCount = (int) msg.obj;
                    tv_followercount.setText(String.valueOf(followerCount));
                    break;
                case AddUserMessageSuccess_TAG:
                    dialog.dismiss();
                    Snackbar.make(recyclerView, "邮件发送成功~慢慢等TA的回信吧(o゜▽゜)o☆", Snackbar.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_profile);
        //自定义标题栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_othersProfile);
        setSupportActionBar(toolbar);

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar_othersProfile);

        //取消原有标题
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();

        currentUser = (User) getIntent().getSerializableExtra("selectedUser");
        Glide.with(getApplicationContext()).load(currentUser.getUser_avatar()).into(userAvatar);

        getSupportActionBar().setTitle(currentUser.getUser_nickname());

        searchAllBox(currentUser.getUser_account());


        //点击星星关注用户
        fab_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:点击白色按钮然后星星变色有时间再来搞，现在赶着做完呢先搁置
                //fab_follow.setImageResource(R.mipmap.follow_png_128);
                userRelation = new UserRelation();
                userRelation.setUser_follow(CurrentUserUtil.getCurrentUser());
                userRelation.setUser_befollowed(currentUser);
                AddUserRelation(userRelation);
            }
        });

        //点击信封给该用户发私信
        button_sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowSendMsgDialog();
            }
        });

        //查看关注人列表
        ll_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OthersProfileActivity.this, UserRelationActivity.class);
                intent.putExtra("RelationType", "Follow");
                intent.putExtra("SearchUser", currentUser);
                startActivity(intent);
            }
        });

        //查看粉丝列表
        ll_follower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OthersProfileActivity.this, UserRelationActivity.class);
                intent.putExtra("RelationType", "Follower");
                intent.putExtra("SearchUser", currentUser);
                startActivity(intent);
            }
        });
    }

    private void ShowSendMsgDialog() {
        //加载自定义的login.xml到程序中
        final LinearLayout ll = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_send_message, null);

        dialog_et_Message = ll.findViewById(R.id.et_sendmessage_Msg);
        dialog_button_sendMsg = ll.findViewById(R.id.button_sendmessage_Send);

        dialog = new AlertDialog.Builder(OthersProfileActivity.this)
                .setTitle("发送邮件")
                .setCancelable(true)
                .setView(ll)
                .create();
        dialog.show();

        dialog_button_sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog_et_Message.getText().toString().length() == 0) {
                    Snackbar.make(v, "发空邮件是会挨打的", Snackbar.LENGTH_SHORT).show();
                } else {
                    AddUserMessage(CurrentUserUtil.getCurrentUser(), currentUser, dialog_et_Message.getText().toString().trim());
                }
            }
        });
    }

    private void AddUserMessage(User sender, User receriver, String message) {
        //创建一个OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();

        //创建表单请求体
        /**
         * key值与服务器端controller中request.getParameter中的key一致
         */
        RequestBody formBody = new FormBody.Builder()
                .add("user_sender_account", sender.getUser_account())
                .add("user_receiver_account", receriver.getUser_account())
                .add("message_content", message)
                .add("message_send_time", String.valueOf(System.currentTimeMillis()))
                .build();

        Log.d(TAG, "AddUserMessage: 发送人和接收人：" + sender.getUser_account() + "," + receriver.getUser_account());

        //创建一个请求对象
        Request request = new Request.Builder()
                .url(AddUserMessageUrl)
                .post(formBody)
                .build();

        /**
         * Get的异步请求，不需要跟同步请求一样开启子线程
         * 但是回调方法还是在子线程中执行的
         * 所以要用到Handler传数据回主线程更新UI
         */
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            //回调的方法执行在子线程
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: 邮件发送成功");
                    Message msg = new Message();
                    msg.what = AddUserMessageSuccess_TAG;
                    handler.sendMessage(msg);
                } else {
                    Message msg = new Message();
                    msg.what = AddUserMessageFail_TAG;
                    handler.sendMessage(msg);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        //初始化用户社交信息
        initTopView();
        super.onResume();
    }

    //初始化用户社交信息
    private void initTopView() {
        GetFollowCount(currentUser.getUser_account());
        GetFollowerCount(currentUser.getUser_account());
    }

    private void GetFollowCount(String user_account) {
        //开子线程访问服务器啦
        //实例化OkHttpClient
        OkHttpClient client = new OkHttpClient();
        //创建表单请求体
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("user_account", user_account);

        //创建Request对象
        Request request = new Request.Builder()
                .url(GetFollowCountUrl)
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
                        int count = Integer.parseInt(jsonObject.get("FollowCount").toString());
                        Log.d(TAG, "onResponse: count=" + count);
                        Message msg = new Message();
                        msg.what = GetFollowCountSuccess_TAG;
                        msg.obj = count;
                        handler.sendMessage(msg);

                    } catch (JSONException a) {
                    }
                } else {
                }
            }
        });
    }

    private void GetFollowerCount(String user_account) {
        //开子线程访问服务器啦
        //实例化OkHttpClient
        OkHttpClient client = new OkHttpClient();
        //创建表单请求体
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("user_account", user_account);

        //创建Request对象
        Request request = new Request.Builder()
                .url(GetFollowerCountUrl)
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
                        int count = Integer.parseInt(jsonObject.get("FollowerCount").toString());
                        Log.d(TAG, "onResponse: count=" + count);
                        Message msg = new Message();
                        msg.what = GetFollowerCountSuccess_TAG;
                        msg.obj = count;
                        handler.sendMessage(msg);

                    } catch (JSONException a) {
                    }
                } else {
                }
            }
        });
    }

    private void showAllSearchBox(List<Box> boxList) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        othersProfileBoxAdapte = new OthersProfileBoxAdapter(boxList, getApplicationContext());
        recyclerView.setAdapter(othersProfileBoxAdapte);
    }

    private List<Box> changeHashMapToBox(List<HashMap<String, Object>> HashMaplist) {
        int length = HashMaplist.size();
        List<Box> boxList = new ArrayList<Box>();
        for (int i = 0; i < length; i++) {
            Box box = new Box();
            box.setBox_name(HashMaplist.get(i).get("box_name").toString());
            box.setBox_create_time((Timestamp) HashMaplist.get(i).get("box_create_time"));
            box.setBox_update_time((Timestamp) HashMaplist.get(i).get("box_update_time"));
            box.setBox_id(HashMaplist.get(i).get("box_id").toString());
            box.setBox_love((Integer) HashMaplist.get(i).get("box_love"));
            box.setBox_side(HashMaplist.get(i).get("box_side").toString());
            box.setBox_type(HashMaplist.get(i).get("box_type").toString());
            box.setBox_authority(HashMaplist.get(i).get("box_authority").toString());
            box.setUser((User) HashMaplist.get(i).get("user"));
            box.setBox_cardnum((Integer) HashMaplist.get(i).get("box_cardnum"));

            boxList.add(box);
        }

        return boxList;
    }

    private void initView() {
        userAvatar = findViewById(R.id.img_othersprofile_avatar);
        recyclerView = findViewById(R.id.recyclerview_othersProfile_boxlist);
        fab_follow = findViewById(R.id.fab_othersprofile_follow);
        button_sendMsg = findViewById(R.id.othersProfile_sendMsg);
        tv_followcount = findViewById(R.id.tv_othersFollowCount);
        tv_followercount = findViewById(R.id.tv_othersFollowerCount);
        tv_boxcount = findViewById(R.id.tv_othersProfile_boxnum);
        ll_follow = findViewById(R.id.ll_othersprofile_follow);
        ll_follower = findViewById(R.id.ll_othersprofile_follower);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    private void searchAllBox(String user_account) {
        //开子线程访问服务器啦
        //实例化OkHttpClient
        OkHttpClient client = new OkHttpClient();
        //创建表单请求体
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("user_account", user_account);

        //创建Request对象
        Request request = new Request.Builder()
                .url(searchBoxByUserAccountUrl)
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
                    //从服务器取到Json键值对{“key”:“value”}
                    String temp = response.body().string();
                    try {
                        JSONArray jsonArray = new JSONArray(temp);
                        boxlist = new ArrayList<HashMap<String, Object>>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                            box = new HashMap<String, Object>();

                            box.put("box_id", jsonObject.get("box_id"));
                            box.put("box_name", jsonObject.get("box_name"));
                            box.put("box_type", jsonObject.get("box_type"));
                            box.put("box_love", jsonObject.get("box_love"));
                            box.put("box_side", jsonObject.get("box_side"));
                            box.put("box_authority", jsonObject.get("box_authority"));
                            box.put("box_cardnum", jsonObject.get("box_cardnum"));

                            /**
                             * 拿到了User的json数据
                             * 利用Gson解析Json键值对
                             */
                            String userresult = jsonObject.get("user").toString();
                            Log.d(TAG, "onResponse: " + userresult);
                            Gson gson = new Gson();
                            User user = gson.fromJson(userresult, User.class);
                            Log.d(TAG, "onResponse: " + user.getUser_nickname());
                            box.put("user", user);

                            //timeresult是从数据库插到的dpost_time字段，类型为timestamp
                            //从数据库读出来长这个样子：存到timeresult里面
                            //{"date":21,"day":6,"hours":23,"minutes":18,"month":3,"nanos":0,"seconds":0,"time":1524323880000,"timezoneOffset":-480,"year":118}
                            String timeresult = jsonObject.get("box_create_time").toString();

                            //利用fastJson——JSON取出timeresult里面的time字段，也就是13位的时间戳
                            long time = JSON.parseObject(timeresult).getLong("time");
                            Log.d(TAG, "onResponse: 创建时间" + timeresult);
                            Timestamp trueTime = new Timestamp(time);

                            //把时间put进daike
                            box.put("box_create_time", trueTime);

                            String timeresult2 = jsonObject.get("box_update_time").toString();
                            Log.d(TAG, "onResponse: 更新时间" + timeresult2);

                            //利用fastJson——JSON取出timeresult里面的time字段，也就是13位的时间戳
                            long time2 = JSON.parseObject(timeresult2).getLong("time");
                            Timestamp trueTime2 = new Timestamp(time2);

                            //把时间put进daike
                            box.put("box_update_time", trueTime2);


                            //将13位时间戳转换为年月日时分秒！
                            /*Long time1 = 1524323880000L;
                            SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date= new Date(time1);
                            String d = format.format(date);*/

                            boxlist.add(box);
                        }

                    } catch (JSONException a) {

                    }

                    //通过handler传递数据到主线程
                    Message msg = new Message();
                    msg.what = SearchBoxSuccess_TAG;
                    msg.obj = boxlist;
                    handler.sendMessage(msg);
                } else {
                    Message msg = new Message();
                    msg.what = SearchBoxFail_TAG;
                    handler.sendMessage(msg);
                }


            }
        });
    }

    private void AddUserRelation(UserRelation userRelation) {
        //创建一个OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();

        //创建表单请求体
        /**
         * key值与服务器端controller中request.getParameter中的key一致
         */
        RequestBody formBody = new FormBody.Builder()
                .add("user_follow_account", userRelation.getUser_follow().getUser_account())
                .add("user_befollowed_account", userRelation.getUser_befollowed().getUser_account())
                .add("follow_time", String.valueOf(System.currentTimeMillis()))
                .build();

        //创建一个请求对象
        Request request = new Request.Builder()
                .url(AddUserRelationUrl)
                .post(formBody)
                .build();

        /**
         * Get的异步请求，不需要跟同步请求一样开启子线程
         * 但是回调方法还是在子线程中执行的
         * 所以要用到Handler传数据回主线程更新UI
         */
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            //回调的方法执行在子线程
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: 关注成功");
                    Message msg = new Message();
                    msg.what = AddUserRelationSuccess_TAG;
                    handler.sendMessage(msg);
                } else {
                    Message msg = new Message();
                    msg.what = AddUserRelationFail_TAG;
                    handler.sendMessage(msg);
                }
            }
        });
    }

    private void CancelUserRelation(UserRelation userRelation) {
        //创建一个OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();

        //创建表单请求体
        /**
         * key值与服务器端controller中request.getParameter中的key一致
         */


        RequestBody formBody = new FormBody.Builder()
                .add("user_follow_account", userRelation.getUser_follow().getUser_account())
                .add("user_befollowed_account", userRelation.getUser_befollowed().getUser_account())
                .build();

        //创建一个请求对象
        Request request = new Request.Builder()
                .url(DeleteUserRelationUrl)
                .post(formBody)
                .build();

        /**
         * Get的异步请求，不需要跟同步请求一样开启子线程
         * 但是回调方法还是在子线程中执行的
         * 所以要用到Handler传数据回主线程更新UI
         */
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            //回调的方法执行在子线程
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: 关注记录删除成功啦");
                    Message msg = new Message();
                    msg.what = DeleteUserRelationSuccess_TAG;
                    handler.sendMessage(msg);
                } else {

                }
            }
        });
    }
}
