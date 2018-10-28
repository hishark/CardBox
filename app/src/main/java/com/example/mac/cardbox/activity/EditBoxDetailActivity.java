package com.example.mac.cardbox.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.mac.cardbox.R;
import com.example.mac.cardbox.bean.Box;
import com.example.mac.cardbox.util.Constant;
import com.example.mac.cardbox.util.CurrentUserUtil;
import com.example.mac.cardbox.util.RandomIDUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditBoxDetailActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText et_BoxName;
    private EditText et_BoxSide;
    private RadioGroup rg_BoxType, rg_ifPublic;
    private RadioButton rb_boxtype,rb_ifPublic;
    private CardView fake_button_EditBox;
    private Box currentBox;

    private static final String TAG = "EditBoxDetailActivity";
    private static final String EditBoxUrl = "http://"+ Constant.Server_IP +":8080/CardBox-Server/UpdateBox";
    private static final int EditBoxSuccess_TAG = 1;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EditBoxSuccess_TAG:
                    Intent intent  = new Intent();
                    intent.putExtra("Update_boxname",et_BoxName.getText().toString());
                    intent.putExtra("Update_boxtype",rb_boxtype.getText().toString());
                    intent.putExtra("Update_boxname",et_BoxName.getText().toString());
                    String ifPublic="";
                    if(rb_ifPublic.getText().toString().equals("是")) {
                        ifPublic = "公开";
                    } else {
                        ifPublic = "私有";
                    }
                    intent.putExtra("Update_boxauthority",ifPublic);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
            }

            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_box_detail);

        //自定义标题栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_editboxdetail);
        setSupportActionBar(toolbar);

        //basicActivity标题栏莫名其妙没颜色，用这句就解决啦
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("修改卡盒");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        currentBox = (Box)getIntent().getSerializableExtra("editBox");

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
        fake_button_EditBox.setOnClickListener(this);
    }

    private void initView() {
        et_BoxName = (EditText)findViewById(R.id.et_EditBox_boxname);
        et_BoxSide = (EditText)findViewById(R.id.et_EditBox_side);
        rg_BoxType = (RadioGroup)findViewById(R.id.radiogroup_EditBox_boxtype);
        rg_ifPublic = (RadioGroup)findViewById(R.id.radiogroup_EditBox_ifPublic);

        et_BoxSide.setHint(currentBox.getBox_side());

        et_BoxName.setText(currentBox.getBox_name());

        String box_type = currentBox.getBox_type().toString();
        switch (box_type) {
            case "学习":
                rb_boxtype = (RadioButton)findViewById(R.id.radiobutton_EditBox_study);
                rb_boxtype.setChecked(true);
                break;
            case "工作":
                rb_boxtype = (RadioButton)findViewById(R.id.radiobutton_EditBox_work);
                rb_boxtype.setChecked(true);
                break;
            case "生活":
                rb_boxtype = (RadioButton)findViewById(R.id.radiobutton_EditBox_life);
                rb_boxtype.setChecked(true);
                break;
            case "娱乐":
                rb_boxtype = (RadioButton)findViewById(R.id.radiobutton_EditBox_entertain);
                rb_boxtype.setChecked(true);
                break;
            case "其他":
                rb_boxtype = (RadioButton)findViewById(R.id.radiobutton_EditBox_other);
                rb_boxtype.setChecked(true);
                break;
        }

        String ifPublic = currentBox.getBox_authority();
        switch (ifPublic) {
            case "公开":
                rb_ifPublic = (RadioButton)findViewById(R.id.radiobutton_EditBox_isPublic);
                rb_ifPublic.setChecked(true);
                break;
            case "私有":
                rb_ifPublic = (RadioButton)findViewById(R.id.radiobutton_EditBox_isNotPublic);
                rb_ifPublic.setChecked(true);
                break;
        }


        rb_boxtype = (RadioButton)findViewById(rg_BoxType.getCheckedRadioButtonId());
        rb_ifPublic = (RadioButton)findViewById(rg_ifPublic.getCheckedRadioButtonId());
        fake_button_EditBox = (CardView)findViewById(R.id.fake_button_EditBox);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fake_button_EditBox:
                if(et_BoxName.getText().toString().equals("")) {
                    Snackbar.make(view,"卡盒名字不能为空噢( ﾟдﾟ)つ",Snackbar.LENGTH_SHORT).show();
                } else {
                    UpdateBoxToServer();
                    //不能在这里finish哦，要去handler收到成功信号之后再finish~
                }
                break;
        }
    }

    private void UpdateBoxToServer() {
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

        //TODO:编辑盒子的设置感觉不需要更新时间啦，只要增加卡片的时候更新时间就好，不过先注释不删，说不定之后需要
        // .add("box_update_time",String.valueOf(System.currentTimeMillis()))
        RequestBody formBody = new FormBody.Builder()
                .add("box_id", currentBox.getBox_id())
                .add("box_name", et_BoxName.getText().toString())
                .add("box_type",rb_boxtype.getText().toString())
                .add("box_authority",ifPublic)
                .build();

        //创建一个请求对象
        Request request = new Request.Builder()
                .url(EditBoxUrl)
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
                    Log.d(TAG, "onResponse: 卡盒修改成功啦");
                    Message msg = new Message();
                    msg.what = EditBoxSuccess_TAG;
                    handler.sendMessage(msg);
                }else{

                }
            }
        });
    }
}
