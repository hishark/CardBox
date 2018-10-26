package com.example.mac.cardbox.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mac.cardbox.R;
import com.example.mac.cardbox.bean.Box;
import com.example.mac.cardbox.util.Constant;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BoxDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView toolbar_tv_boxname;
    private Box currentBox;
    private TextView tv_createTime,tv_updateTime,tv_boxType,tv_cardType,tv_boxname,tv_authority;

    private static final String TAG = "BoxDetailActivity";
    private static final String DeleteBoxUrl = "http://" + Constant.Server_IP + ":8080/CardBox-Server/DeleteBox";
    private static final int ClickToEdit = 1;
    private static final int DeleteBoxSuccess_TAG = 2;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DeleteBoxSuccess_TAG:
                    finish();
                    break;
            }

            super.handleMessage(msg);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box_detail);

        //自定义标题栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_boxDetail);
        setSupportActionBar(toolbar);

        //取消原有标题
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //得到在MyCardBoxFragment选中的盒子
        currentBox = (Box) getIntent().getSerializableExtra("Box");

        getSupportActionBar().setTitle(currentBox.getBox_name());


        //初始化
        initView();

        //集体设置点击事件
        setOnclick();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_boxdetail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_boxdetail_edit:
                Intent intent = new Intent(BoxDetailActivity.this, EditBoxDetailActivity.class);
                intent.putExtra("editBox", currentBox);
                startActivityForResult(intent, ClickToEdit);
                break;
            case R.id.menu_boxdetail_delete:
                AlertDialog dialog = new AlertDialog.Builder(BoxDetailActivity.this).setTitle("提示信息")
                        .setMessage("你真的要删掉这个盒子呀？")
                        .setCancelable(true)
                        .setNegativeButton("我想想", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("是滴", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //删除这个盒子
                                deleteBoxFromServer(currentBox.getBox_id().toString());
                            }
                        }).create();
                dialog.show();
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#DFA22E"));
                break;
        }
        return true;
    }

    private void deleteBoxFromServer(String box_id) {

        //创建一个OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();

        //创建表单请求体
        /**
         * key值与服务器端controller中request.getParameter中的key一致
         */


        RequestBody formBody = new FormBody.Builder()
                .add("box_id", box_id)
                .build();

        //创建一个请求对象
        Request request = new Request.Builder()
                .url(DeleteBoxUrl)
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
                    Log.d(TAG, "onResponse: 卡盒删除成功啦");
                    Message msg = new Message();
                    msg.what = DeleteBoxSuccess_TAG;
                    handler.sendMessage(msg);
                } else {

                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case ClickToEdit:
                if (resultCode == RESULT_OK) {
                    String newBoxName = data.getStringExtra("Update_boxname");
                    getSupportActionBar().setTitle(newBoxName);
                    tv_boxname.setText(newBoxName);
                    currentBox.setBox_name(newBoxName);

                    String newBoxType = data.getStringExtra("Update_boxtype");
                    tv_boxType.setText(newBoxType);
                    currentBox.setBox_type(newBoxType);

                    String ifPublic = data.getStringExtra("Update_boxauthority");
                    tv_authority.setText(ifPublic);
                    currentBox.setBox_authority(ifPublic);
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setOnclick() {
    }

    private void initView() {
        tv_createTime = findViewById(R.id.myboxdetail_create_time);
        tv_updateTime = findViewById(R.id.myboxdetail_update_time);
        tv_boxType = findViewById(R.id.myboxdetail_box_type);
        tv_cardType = findViewById(R.id.myboxdetail_card_type);
        tv_boxname = findViewById(R.id.myboxdetail_boxname);
        tv_authority = findViewById(R.id.myboxdetail_ifPublic);

        String createTime = currentBox.getBox_create_time().toString();
        String updateTime = currentBox.getBox_update_time().toString();
        tv_createTime.setText(createTime.substring(0,createTime.length()-2));
        tv_updateTime.setText(updateTime.substring(0,updateTime.length()-2));
        tv_boxType.setText(currentBox.getBox_type());
        tv_cardType.setText(currentBox.getBox_side());
        tv_boxname.setText(currentBox.getBox_name());
        tv_authority.setText(currentBox.getBox_authority());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }
}
