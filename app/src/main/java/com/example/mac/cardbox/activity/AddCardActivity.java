package com.example.mac.cardbox.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.mac.cardbox.R;
import com.example.mac.cardbox.adapter.AddCardAdapter;
import com.example.mac.cardbox.adapter.MyBoxDetailAdapter;
import com.example.mac.cardbox.bean.Box;
import com.example.mac.cardbox.bean.Card;
import com.example.mac.cardbox.util.Constant;
import com.example.mac.cardbox.util.CurrentUserUtil;
import com.example.mac.cardbox.util.RandomIDUtil;

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

public class AddCardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Box currentBox;
    private List<Card> addList;
    private AddCardAdapter addCardAdapter;
    private LinearLayoutManager linearLayoutManager;
    private static final String TAG = "AddCardActivity";
    private static final int AddCardSuccess_TAG = 2;
    private HashMap<String,Object> result = new HashMap<>();
    private static final String AddCardUrl = "http://"+ Constant.Server_IP +":8080/CardBox-Server/AddCard";


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    result = (HashMap<String,Object>)msg.obj;
                    int position = (int)result.get("position");
                    String front = result.get("front").toString();
                    String back = result.get("back").toString();
                    addList.get(position).setCard_front(front);
                    addList.get(position).setCard_back(back);
                    addCardAdapter.notifyDataSetChanged();
                    Log.d(TAG, "handleMessage: 看看全部数据"+addList.toString());
                    break;
                case AddCardSuccess_TAG:
                    finish();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void AddCardToServer(Card card) {
        //创建一个OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();

        //创建表单请求体
        /**
         * key值与服务器端controller中request.getParameter中的key一致
         */
        RequestBody formBody = new FormBody.Builder()
                .add("card_id", card.getCard_id())
                .add("box_id", card.getBox().getBox_id())
                .add("card_type", card.getCard_type())
                .add("card_front",card.getCard_front())
                .add("card_back",card.getCard_back())
                .add("card_create_time",String.valueOf(System.currentTimeMillis()))
                .add("card_marktype",card.getCard_marktype())
                .build();

        //创建一个请求对象
        Request request = new Request.Builder()
                .url(AddCardUrl)
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
                    Log.d(TAG, "onResponse: 卡片添加成功啦");
                    Message msg = new Message();
                    msg.what = AddCardSuccess_TAG;
                    handler.sendMessage(msg);
                }else{
                    Log.d(TAG, "onResponse: 卡片添加失败啦");

                }
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);
        //自定义标题栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_addcard);
        setSupportActionBar(toolbar);

        //取消原有标题
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("添加卡片");

        //初始化
        initView();

        currentBox = (Box) getIntent().getSerializableExtra("currentBox");

        showAddList();
    }

    private void addBlankCard() {
        Card blankCard = new Card();
        blankCard.setCard_id(RandomIDUtil.getID());
        blankCard.setBox(currentBox);
        blankCard.setCard_marktype("未标记");
        blankCard.setCard_front("");
        blankCard.setCard_back("");
        blankCard.setCard_type("双面");
        blankCard.setCard_create_time(new Timestamp(System.currentTimeMillis()));
        addList.add(0,blankCard);
        //addList.add(blankCard);
    }

    private void showAddList() {
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        addBlankCard();
        addCardAdapter = new AddCardAdapter(addList, getApplicationContext(),handler);
        recyclerView.setAdapter(addCardAdapter);

    }

    private void initView() {
        recyclerView = findViewById(R.id.recyclerview_addCard);
        addList = new ArrayList<>();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_addcard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_addcard_addOne:
                addBlankCard();
                addCardAdapter.notifyDataSetChanged();
                break;
            case R.id.menu_addcard_checkAll:
                for(int i=0;i<addList.size();i++) {
                    AddCardToServer(addList.get(i));
                }
                break;
        }
        return true;
    }
}
