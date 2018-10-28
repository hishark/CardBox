package com.example.mac.cardbox.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.mac.cardbox.R;
import com.example.mac.cardbox.adapter.CardPagerAdapter;
import com.example.mac.cardbox.bean.Card;
import com.example.mac.cardbox.transform.ShadowTransformer;

import java.util.Collections;
import java.util.List;

public class BrowseCurrentBoxCardActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;
    private List<Card> cardList;
    private List<Card> markcardList;
    private List<Card> unmarkcardList;

    private static final String TAG = "BrowseCurrentBoxCardAct";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_current_box_card);

        //自定义标题栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_currentBox_Card);
        setSupportActionBar(toolbar);

        //取消原有标题
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("");

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //初始化
        initView();
        boolean IsOneSideCard =getIntent().getBooleanExtra("IsOneSideCard",false);

        mViewPager = (ViewPager) findViewById(R.id.curretboxcard_viewpager);
        //mViewPager2= (ViewPager) findViewById(R.id.viewPager2);
        mCardAdapter = new CardPagerAdapter(getApplicationContext(),IsOneSideCard);
        unmarkcardList = (List<Card>)getIntent().getSerializableExtra("AllCards");
        markcardList = (List<Card>)getIntent().getSerializableExtra("AllMarkCards");
        String flag = getIntent().getStringExtra("flag");



        if(flag.equals("普通卡片")) {
            cardList = unmarkcardList;
            getSupportActionBar().setTitle("所有卡片");
        } else {
            cardList = markcardList;
            getSupportActionBar().setTitle("标记卡片");
        }

        showAllCards();


    }

    private void showAllCards() {
        mCardAdapter.removeAllCardItem();
        Card card[]= new Card[cardList.size()];
        for(int i=0;i<cardList.size();i++) {
            card[i] = new Card();
            card[i].setCard_front(cardList.get(i).getCard_front());
            card[i].setCard_back(cardList.get(i).getCard_back());
            card[i].setCard_marktype(cardList.get(i).getCard_marktype());
            card[i].setCard_id(cardList.get(i).getCard_id());
            mCardAdapter.addCardItem(card[i]);

        }
        mViewPager.setAdapter(mCardAdapter);
        mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);
        mCardShadowTransformer.enableScaling(true);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(3);
    }

    private void initView() {
        mViewPager = findViewById(R.id.curretboxcard_viewpager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_boxcard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_boxcard_random:
                Collections.shuffle(cardList);
                showAllCards();
                break;
        }
        return true;
    }
}
