package com.example.mac.cardbox.activity;

import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mac.cardbox.R;
import com.example.mac.cardbox.util.Constant;
import com.example.mac.cardbox.util.CurrentUserUtil;
import com.example.mac.cardbox.util.RandomIDUtil;

import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddOneSideBoxActivity extends AppCompatActivity implements  View.OnClickListener{

    private EditText et_BoxName;
    private EditText et_BoxSide;
    private RadioGroup rg_BoxType, rg_ifPublic;
    private RadioButton rb_boxtype,rb_ifPublic;
    private CardView fake_button_addBox;

    private static final String TAG = "AddOneSideBoxActivity";
    private static final String AddBoxUrl = "http://"+ Constant.Server_IP +":8080/CardBox-Server/AddBox";
    private static final int AddBoxSuccess_TAG = 1;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AddBoxSuccess_TAG:
                    finish();
                    break;
            }

            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_one_side_box);

        //自定义标题栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.addOneSideBox_toolbar);
        setSupportActionBar(toolbar);

        //basicActivity标题栏莫名其妙没颜色，用这句就解决啦
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("添加卡盒");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //初始化
        initView();

        //注册all监听器
        setOnclick();

        //两个RadioGroup的选择监听器
        rg_BoxType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                rb_boxtype = (RadioButton)findViewById(i);
            }
        });
        rg_ifPublic.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                rb_ifPublic = (RadioButton)findViewById(i);
            }
        });



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

    private void setOnclick() {
        fake_button_addBox.setOnClickListener(this);
    }

    private void initView() {
        et_BoxName = (EditText)findViewById(R.id.et_addOneSideBox_boxname);
        et_BoxSide = (EditText)findViewById(R.id.et_addOneSideBox_side);
        rg_BoxType = (RadioGroup)findViewById(R.id.radiogroup_addOneSideBox_boxtype);
        rg_ifPublic = (RadioGroup)findViewById(R.id.radiogroup_addOneSideBox_ifPublic);
        rb_boxtype = (RadioButton)findViewById(rg_BoxType.getCheckedRadioButtonId());
        rb_ifPublic = (RadioButton)findViewById(rg_ifPublic.getCheckedRadioButtonId());
        fake_button_addBox = (CardView)findViewById(R.id.fake_button_addBox);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fake_button_addBox:
                if(et_BoxName.getText().toString().equals("")) {
                    Snackbar.make(view,"卡盒名字还没填噢( ﾟдﾟ)つ",Snackbar.LENGTH_SHORT).show();
                } else {
                    AddOneSideBoxToServer();
                    //不能在这里finish哦，要去handler收到成功信号之后再finish~
                }
                break;
        }
    }

    private void AddOneSideBoxToServer() {
        //创建一个OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();

        //创建表单请求体
        /**
         * key值与服务器端controller中request.getParameter中的key一致
         */

        String ifPublic="";
        if (rb_ifPublic.getText().toString().equals("是")) {
            ifPublic = "公开";
        } else {
            ifPublic = "私有";
        }

        RequestBody formBody = new FormBody.Builder()
                .add("box_id", RandomIDUtil.getID())
                .add("box_name", et_BoxName.getText().toString())
                .add("user_account", CurrentUserUtil.getCurrentUser().getUser_account())
                .add("box_type",rb_boxtype.getText().toString())
                .add("box_create_time",String.valueOf(System.currentTimeMillis()))
                .add("box_side",et_BoxSide.getHint().toString())
                .add("box_authority",ifPublic)
                .build();

        //创建一个请求对象
        Request request = new Request.Builder()
                .url(AddBoxUrl)
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
                    Log.d(TAG, "onResponse: 卡盒添加成功啦");
                    Message msg = new Message();
                    msg.what = AddBoxSuccess_TAG;
                    handler.sendMessage(msg);
                }else{

                }
            }
        });
    }
}
