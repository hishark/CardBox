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
import android.widget.TextView;
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

public class SelectedOthersCardActivity extends AppCompatActivity {

    private Card card;
    private TextView tv_front,tv_back;

    private static final String TAG = "SelectedCardActivity";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_otherscard);

        //自定义标题栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_selectedOthersCard);
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
        tv_front = findViewById(R.id.selectedOthersCard_front);
        tv_back = findViewById(R.id.selectedOthersCard_back);

        tv_front.setText(card.getCard_front());
        tv_back.setText(card.getCard_back());
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

}
