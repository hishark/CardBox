package com.example.mac.cardbox.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.mac.cardbox.R;
import com.example.mac.cardbox.bean.Box;

public class BoxDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView toolbar_tv_boxname;
    private Box currentBox;

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

        //得到选中的盒子
        currentBox = (Box)getIntent().getSerializableExtra("Box");

        getSupportActionBar().setTitle(currentBox.getBox_name());


        //初始化
        initView();

        //集体设置点击事件
        setOnclick();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_boxdetail,menu);
        return true;
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
    }

    private void initView() {


    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {

        }
    }
}
