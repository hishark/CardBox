package com.example.mac.cardbox.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.mac.cardbox.R;
import com.example.mac.cardbox.adapter.MyBoxDetailAdapter;
import com.example.mac.cardbox.adapter.MyOneSideBoxDetailAdapter;
import com.example.mac.cardbox.bean.Box;
import com.example.mac.cardbox.bean.BoxFavourite;
import com.example.mac.cardbox.bean.Card;
import com.example.mac.cardbox.bean.User;
import com.example.mac.cardbox.util.Constant;
import com.example.mac.cardbox.util.CurrentUserUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xujiaji.happybubble.BubbleDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
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

public class OthersBoxDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView toolbar_tv_boxname;
    private Box currentBox;
    private TextView tv_createTime,tv_updateTime,tv_boxType,tv_cardType,tv_boxname,tv_authority,tv_username;
    private FloatingActionButton fab_loveBox;
    private RecyclerView recyclerView;

    private List<HashMap<String, Object>> cardList=null;
    private HashMap<String, Object> card=null;

    private static final String TAG = "BoxDetailActivity";
    private static final String SearchCardByBoxIDUrl = "http://" + Constant.Server_IP + ":8080/CardBox-Server/SearchCardByBoxID";
    private static final String AddBoxToFavouriteUrl = "http://" + Constant.Server_IP + ":8080/CardBox-Server/AddBoxFavourite";
    private static final String DeleteFavouriteBoxUrl = "http://" + Constant.Server_IP + ":8080/CardBox-Server/DeleteFavouriteBox";

    private static final int SearchSuccess_TAG = 1;
    private static final int AddBoxToFavouriteSuccess_TAG = 2;
    private static final int FavouriteAlreadyExist_TAG = 3;
    private static final int DeleteFavouriteSuccess_TAG = 4;
    private static final int SearchFail_TAG = 5;

    private BoxFavourite box_favourite;
    private TextView tv_hint;

    BubbleDialog bubble1;
    View bubbleDialog;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SearchSuccess_TAG:
                    List<HashMap<String, Object>> list = (List<HashMap<String, Object>>)msg.obj;
                    tv_hint.setVisibility(View.INVISIBLE);
                    showAllSearchCard(changeHashMapToCard(list));
                    break;
                case SearchFail_TAG:
                    tv_hint.setVisibility(View.VISIBLE);
                    break;
                case AddBoxToFavouriteSuccess_TAG:
                    Snackbar.make(recyclerView,"已添加至你喜欢的卡盒列表",Snackbar.LENGTH_SHORT).show();
                    break;
                case FavouriteAlreadyExist_TAG:
                    Snackbar.make(recyclerView,"该卡盒已经在你的喜欢列表里啦~",Snackbar.LENGTH_SHORT)
                            .setAction("取消喜欢", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.d(TAG, "onClick: 准备删除");
                                    CancelFavouriteBox(box_favourite);
                                }
                            })
                            .setActionTextColor(Color.parseColor("#d75555"))
                            .show();
                    break;
                case DeleteFavouriteSuccess_TAG:
                    Snackbar.make(recyclerView,"已取消喜欢（；´д｀）ゞ",Snackbar.LENGTH_SHORT).show();
                    break;
            }

            super.handleMessage(msg);
        }
    };

    private void CancelFavouriteBox(BoxFavourite boxfavourite) {
        //创建一个OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();

        //创建表单请求体
        /**
         * key值与服务器端controller中request.getParameter中的key一致
         */


        RequestBody formBody = new FormBody.Builder()
                .add("box_id", boxfavourite.getBox().getBox_id())
                .add("user_account", boxfavourite.getUser().getUser_account())
                .build();

        //创建一个请求对象
        Request request = new Request.Builder()
                .url(DeleteFavouriteBoxUrl)
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
                    Log.d(TAG, "onResponse: 喜欢的卡盒记录删除成功啦");
                    Message msg = new Message();
                    msg.what = DeleteFavouriteSuccess_TAG;
                    handler.sendMessage(msg);
                } else {

                }
            }
        });
    }

    private void showAllSearchCard(List<Card> cardList) {
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        boolean IsOthers = true;

        if(currentBox.getBox_side().equals("双面")) {
            MyBoxDetailAdapter myBoxDetailAdapter = new MyBoxDetailAdapter(cardList, getApplicationContext(),IsOthers);
            recyclerView.setAdapter(myBoxDetailAdapter);
        } else {
            MyOneSideBoxDetailAdapter myOneSideBoxDetailAdapter = new MyOneSideBoxDetailAdapter(cardList, getApplicationContext(),IsOthers);
            recyclerView.setAdapter(myOneSideBoxDetailAdapter);
        }



    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_box_detail);

        //自定义标题栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_othersboxDetail);
        setSupportActionBar(toolbar);

        //取消原有标题
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //得到在MyCardBoxFragment选中的盒子
        currentBox = (Box) getIntent().getSerializableExtra("OthersBox");


        getSupportActionBar().setTitle(currentBox.getBox_name());

        //初始化
        initView();

        //集体设置点击事件
        setOnclick();

    }

    @Override
    protected void onResume() {
        //搜索出当前盒子的所有卡片~这个应该要放到onResume里面
        searchCardByBoxid(currentBox.getBox_id());
        super.onResume();
    }

    private List<Card> changeHashMapToCard(List<HashMap<String, Object>> HashMaplist) {
        int length = HashMaplist.size();
        List<Card> cardList = new ArrayList<Card>();
        for(int i=0;i<length;i++) {
            Card card = new Card();
            card.setBox((Box)HashMaplist.get(i).get("box"));
            card.setCard_back(HashMaplist.get(i).get("card_back").toString());
            card.setCard_front(HashMaplist.get(i).get("card_front").toString());
            card.setCard_create_time((Timestamp)HashMaplist.get(i).get("card_create_time"));
            card.setCard_id(HashMaplist.get(i).get("card_id").toString());
            card.setCard_marktype(HashMaplist.get(i).get("card_marktype").toString());
            card.setCard_type(HashMaplist.get(i).get("card_type").toString());
            cardList.add(card);
        }
        return cardList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_othersboxdetail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_othersboxdetail_browsecard:
                if(cardList==null) {
                    Snackbar.make(fab_loveBox,"当前盒子还没有卡片哦",Snackbar.LENGTH_SHORT).show();
                } else {

                    Intent intent2 = new Intent(OthersBoxDetailActivity.this,BrowseCurrentBoxCardActivity.class);
                    intent2.putExtra("AllCards",(Serializable)changeHashMapToCard(cardList));
                    intent2.putExtra("flag","普通卡片");
                    intent2.putExtra("IsOthers",true);
                    if(currentBox.getBox_side().equals("单面")) {
                        intent2.putExtra("IsOneSideCard",true);
                    }
                    startActivity(intent2);
                }

                break;
            case R.id.menu_othersboxdetail_boxinfo:

                String createTime = currentBox.getBox_create_time().toString();
                String updateTime = currentBox.getBox_update_time().toString();
                //添加卡片时时间会是2018-10-29 01:03:30.391，最后不止多了两位，所以直接0-19省事嘻嘻
                tv_createTime.setText(createTime.substring(0,19));
                Log.d(TAG, "onOptionsItemSelected: 为啥多两位"+updateTime);
                tv_updateTime.setText(updateTime.substring(0,19));
                tv_boxType.setText(currentBox.getBox_type());
                tv_cardType.setText(currentBox.getBox_side());
                tv_boxname.setText(currentBox.getBox_name());
                tv_authority.setText(currentBox.getBox_authority());
                tv_username.setText(currentBox.getUser().getUser_nickname());

                bubble1.addContentView(bubbleDialog);
                //这个方法是设置泡泡从哪个view冒出来的
                //bubble1.setClickedView(fab_addCard);
                bubble1.setClickedView(fab_loveBox);
                bubble1.calBar(true);
                bubble1.show();

                break;
            case R.id.menu_othersboxdetail_userinfo:
                Intent intent = new Intent(OthersBoxDetailActivity.this,OthersProfileActivity.class);
                intent.putExtra("selectedUser",currentBox.getUser());
                startActivity(intent);
                break;
        }
        return true;
    }



    private void setOnclick() {
        fab_loveBox.setOnClickListener(this);
    }

    private void initView() {
        fab_loveBox = findViewById(R.id.fab_othersboxdetail_loveBox);
        recyclerView = findViewById(R.id.recyclerview_othersboxdetail_);

        bubble1 = new BubbleDialog(this);
        bubbleDialog = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bubbledailog_othersboxinfo,null);
        bubbleDialog.setBackgroundColor(Color.WHITE);
        tv_createTime = bubbleDialog.findViewById(R.id.bubbledialog_myboxdetail_create_time);
        tv_updateTime = bubbleDialog.findViewById(R.id.bubbledialog_myboxdetail_update_time);
        tv_boxType = bubbleDialog.findViewById(R.id.bubbledialog_myboxdetail_box_type);
        tv_cardType = bubbleDialog.findViewById(R.id.bubbledialog_myboxdetail_card_type);
        tv_boxname = bubbleDialog.findViewById(R.id.bubbledialog_myboxdetail_boxname);
        tv_authority = bubbleDialog.findViewById(R.id.bubbledialog_myboxdetail_ifPublic);
        tv_username = bubbleDialog.findViewById(R.id.bubbledialog_otherboxdetail_username);
        tv_hint = findViewById(R.id.tv_othersboxdetail_hint);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_othersboxdetail_loveBox:
                box_favourite = new BoxFavourite();
                box_favourite.setBox(currentBox);
                box_favourite.setUser(CurrentUserUtil.getCurrentUser());
                AddBoxToFavourite(box_favourite);
                break;
        }
    }

    private void AddBoxToFavourite(BoxFavourite box_favourite) {
        //创建一个OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();

        //创建表单请求体
        /**
         * key值与服务器端controller中request.getParameter中的key一致
         */
        RequestBody formBody = new FormBody.Builder()
                .add("box_id", box_favourite.getBox().getBox_id())
                .add("user_account", box_favourite.getUser().getUser_account())
                .add("favourite_time", String.valueOf(System.currentTimeMillis()))
                .build();

        //创建一个请求对象
        Request request = new Request.Builder()
                .url(AddBoxToFavouriteUrl)
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
                if(response.isSuccessful()){
                    Log.d(TAG, "onResponse: 喜欢的卡盒添加成功啦");
                    Message msg = new Message();
                    msg.what = AddBoxToFavouriteSuccess_TAG;
                    handler.sendMessage(msg);
                }else{
                    Message msg = new Message();
                    msg.what = FavouriteAlreadyExist_TAG;
                    handler.sendMessage(msg);
                }
            }
        });
    }

    private void searchCardByBoxid(final String box_id) {
        //开子线程访问服务器啦
        //实例化OkHttpClient
        OkHttpClient client = new OkHttpClient();
        //创建表单请求体
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("box_id", box_id);

        //创建Request对象
        Request request = new Request.Builder()
                .url(SearchCardByBoxIDUrl)
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
                    Log.d(TAG, "onResponse: temp="+temp);
                    try{
                        JSONArray jsonArray=new JSONArray(temp);
                        cardList=new ArrayList<HashMap<String, Object>>();

                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject=(JSONObject)jsonArray.get(i);
                            Log.d(TAG, "onResponse: 怎么会没有user"+jsonObject.toString());

                            card=new HashMap<String, Object>();

                            card.put("card_id", jsonObject.get("card_id"));
                            card.put("card_type",jsonObject.get("card_type"));
                            card.put("card_front", jsonObject.get("card_front"));
                            card.put("card_back", jsonObject.get("card_back"));
                            card.put("card_marktype", jsonObject.get("card_marktype"));



                            //-------解析Box（Box里面有User,TimeStamp）-------
                            Box box = new Box();
                            //String boxresult = jsonObject.get("box").toString();
                            String boxresult = jsonObject.getString("box");

                            JsonObject boxJsonObject = new JsonParser().parse(boxresult).getAsJsonObject();
                            Log.d(TAG, "onResponse: 要成功了！"+boxJsonObject.toString());

                            Log.d(TAG, "onResponse: 看看这个再解析"+boxresult);


                            String userresult = boxJsonObject.get("user").toString();
                            Log.d(TAG, "onResponse: 看看用户"+userresult);
                            Gson gson = new Gson();
                            User user = gson.fromJson(userresult, User.class);
                            box.setUser(user);

                            String timeresult1 = boxJsonObject.get("box_create_time").toString();
                            //利用fastJson——JSON取出timeresult里面的time字段，也就是13位的时间戳
                            long time1 = JSON.parseObject(timeresult1).getLong("time");
                            Timestamp box_create_time = new Timestamp(time1);
                            box.setBox_create_time(box_create_time);

                            String timeresult2 = boxJsonObject.get("box_update_time").toString();
                            //利用fastJson——JSON取出timeresult里面的time字段，也就是13位的时间戳
                            long time2 = JSON.parseObject(timeresult2).getLong("time");
                            Timestamp box_update_time = new Timestamp(time2);
                            box.setBox_update_time(box_update_time);

                            //-------解析Box（Box里面有User,TimeStamp）-------

                            card.put("box",box);

                            //timeresult是从数据库插到的dpost_time字段，类型为timestamp
                            //从数据库读出来长这个样子：存到timeresult里面
                            //{"date":21,"day":6,"hours":23,"minutes":18,"month":3,"nanos":0,"seconds":0,"time":1524323880000,"timezoneOffset":-480,"year":118}
                            String timeresult = jsonObject.get("card_create_time").toString();
                            Log.d(TAG, "onResponse: 返回的时间串"+timeresult);
                            //利用fastJson——JSON取出timeresult里面的time字段，也就是13位的时间戳
                            long time = JSON.parseObject(timeresult).getLong("time");
                            Log.d(TAG, "onResponse: 创建时间"+timeresult);
                            Timestamp trueTime = new Timestamp(time);

                            //把时间put进daike
                            card.put("card_create_time",trueTime);

       

                            cardList.add(card);
                        }

                    }catch (JSONException a){
                        Log.d(TAG, "onResponse: 出现了异常噢耶"+a.getMessage());
                    }

                    //通过handler传递数据到主线程
                    Message msg = new Message();
                    msg.what = SearchSuccess_TAG;
                    msg.obj = cardList;
                    handler.sendMessage(msg);
                } else {
                    //通过handler传递数据到主线程
                    Message msg = new Message();
                    msg.what = SearchFail_TAG;
                    msg.obj = cardList;
                    handler.sendMessage(msg);
                }


            }
        });
    }
}
