package com.example.mac.cardbox.activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mac.cardbox.R;
import com.example.mac.cardbox.bean.Card;
import com.example.mac.cardbox.util.Constant;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
//TODO:255个的字数限制下次统一加一下~
public class SelectedOneSideCardActivity extends AppCompatActivity {

    private Card card;
    private EditText et_front;

    private static final String DeleteCardByIdUrl = "http://" + Constant.Server_IP + ":8080/CardBox-Server/DeleteCardById";
    private static final String UpdateCardByIdUrl = "http://" + Constant.Server_IP + ":8080/CardBox-Server/UpdateCard";
    private static final String TAG = "SelectedCardActivity";
    private static final int DeleteCardSuccess_TAG = 0;
    private static final int UpdateCardSuccess_TAG = 1;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DeleteCardSuccess_TAG:
                    Toast.makeText(SelectedOneSideCardActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case UpdateCardSuccess_TAG:
                    Toast.makeText(SelectedOneSideCardActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
            }

            super.handleMessage(msg);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_oneside_card);

        //自定义标题栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_selectedOneSideCard);
        setSupportActionBar(toolbar);

        //取消原有标题
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
        //得到在MyCardBoxFragment选中的盒子
        card = (Card) getIntent().getSerializableExtra("selectedCard");

        //初始化
        initView();
    }

    private void initView() {
        et_front = findViewById(R.id.selectedOneSideCard_front);

        et_front.setText(card.getCard_front());
        et_front.setSelection(card.getCard_front().length());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_seletedcard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_selectedcard_edit:
                AlertDialog dialog = new AlertDialog.Builder(SelectedOneSideCardActivity.this).setTitle("提示信息")
                        .setMessage("确认修改？")
                        .setCancelable(true)
                        .setNegativeButton("我想想", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("是的", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UpdateCurrentCard(card.getCard_id(),et_front.getText().toString(),"");
                            }
                        }).create();
                dialog.show();
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#DFA22E"));
                break;
            case R.id.menu_selectedcard_delete:
                AlertDialog dialog1 = new AlertDialog.Builder(SelectedOneSideCardActivity.this).setTitle("提示信息")
                        .setMessage("确认删除？")
                        .setCancelable(true)
                        .setNegativeButton("我想想", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("是的", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DeleteCurrentCard(card.getCard_id());
                            }
                        }).create();
                dialog1.show();
                dialog1.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#DFA22E"));

                break;
        }
        return true;
    }

    private void DeleteCurrentCard(String card_id) {
        //创建一个OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();

        //创建表单请求体
        /**
         * key值与服务器端controller中request.getParameter中的key一致
         */
        RequestBody formBody = new FormBody.Builder()
                .add("card_id", card_id)
                .build();

        //创建一个请求对象
        Request request = new Request.Builder()
                .url(DeleteCardByIdUrl)
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
                    Log.d(TAG, "onResponse: 卡片删除成功啦");
                    Message msg = new Message();
                    msg.what = DeleteCardSuccess_TAG;
                    handler.sendMessage(msg);
                } else {
                    Log.d(TAG, "onResponse: Wrong！");
                }
            }
        });
    }

    private void UpdateCurrentCard(String card_id,String front_content, String back_content) {
        //创建一个OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();

        //创建表单请求体
        /**
         * key值与服务器端controller中request.getParameter中的key一致
         */

        RequestBody formBody = new FormBody.Builder()
                .add("card_id", card_id)
                .add("card_front",front_content)
                .add("card_back",back_content)
                .build();

        //创建一个请求对象
        Request request = new Request.Builder()
                .url(UpdateCardByIdUrl)
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
                    Log.d(TAG, "onResponse: 卡片更新成功啦");
                    Message msg = new Message();
                    msg.what = UpdateCardSuccess_TAG;
                    handler.sendMessage(msg);
                }else{
                    Log.d(TAG, "onResponse: 卡片更新失败啦");
                }
            }
        });
    }
}
