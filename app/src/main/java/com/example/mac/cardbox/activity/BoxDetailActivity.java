package com.example.mac.cardbox.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

import com.example.mac.cardbox.R;

public class BoxDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton fake_button_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box_detail);

        //自定义标题栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_boxDetail);
        setSupportActionBar(toolbar);

        //取消原有标题
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //初始化
        initView();

        //集体设置点击事件
        setOnclick();

    }

    private void setOnclick() {
        fake_button_back.setOnClickListener(this);
    }

    private void initView() {
        fake_button_back = findViewById(R.id.fake_button_boxdetail_back);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.fake_button_boxdetail_back:
                finish();
                break;
        }
    }
}
