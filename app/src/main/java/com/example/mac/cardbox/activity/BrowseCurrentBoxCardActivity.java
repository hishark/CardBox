package com.example.mac.cardbox.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.mac.cardbox.R;
import com.example.mac.cardbox.adapter.CardPagerAdapter;
import com.example.mac.cardbox.bean.Card;
import com.example.mac.cardbox.transform.ShadowTransformer;

import java.util.List;

public class BrowseCurrentBoxCardActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;
    private List<Card> cardList;

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

        mViewPager = (ViewPager) findViewById(R.id.curretboxcard_viewpager);
        //mViewPager2= (ViewPager) findViewById(R.id.viewPager2);
        mCardAdapter = new CardPagerAdapter(getApplicationContext());
        cardList = (List<Card>)getIntent().getSerializableExtra("AllCards");
        Card card[]= new Card[cardList.size()];
        for(int i=0;i<cardList.size();i++) {
            card[i] = new Card();
            card[i].setCard_front(cardList.get(i).getCard_front());
            card[i].setCard_back(cardList.get(i).getCard_back());
            card[i].setCard_marktype(cardList.get(i).getCard_marktype());
            mCardAdapter.addCardItem(card[i]);

        }

        /*mCardAdapter.addCardItem(new Card("我们追悼了过去的人，还要发愿：要自己和别人，都纯洁聪明勇猛向上。要除去虚伪的脸谱。要除去世上害己害人的昏迷和强暴。\n" +
                "我们追悼了过去的人，还要发愿：要除去于人生毫无意义的苦痛。要除去制造并赏玩别人苦痛的昏迷和强暴。\n" +
                "我们还要发愿：要人类都受正当的幸福。我们追悼了过去的人，还要发愿：要自己和别人，都纯洁聪明勇猛向上。要除去虚伪的脸谱。要除去世上害己害人的昏迷和强暴。\n" +
                "我们追悼了过去的人，还要发愿：要除去于人生毫无意义的苦痛。要除去制造并赏玩别人苦痛的昏迷和强暴。\n" +
                "我们还要发愿：要人类都受正当的幸福。我们追悼了过去的人，还要发愿：要自己和别人，都纯洁聪明勇猛向上。要除去虚伪的脸谱。要除去世上害己害人的昏迷和强暴。\n" +
                "我们追悼了过去的人，还要发愿：要除去于人生毫无意义的苦痛。要除去制造并赏玩别人苦痛的昏迷和强暴。\n" +
                "我们还要发愿：要人类都受正当的幸福。我们追悼了过去的人，还要发愿：要自己和别人，都纯洁聪明勇猛向上。要除去虚伪的脸谱。要除去世上害己害人的昏迷和强暴。\n" +
                "我们追悼了过去的人，还要发愿：要除去于人生毫无意义的苦痛。要除去制造并赏玩别人苦痛的昏迷和强暴。\n" +
                "我们还要发愿：要人类都受正当的幸福。啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊" +
                "啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊" +
                "啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊哈哈哈哈哈哈哈哈哈哈哈哈哈yiyiyaaaaaaaaaaaaaaaaaaaaaaaab","你好"));
        mCardAdapter.addCardItem(new Card("啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊","喂鲨鱼"));
        mCardAdapter.addCardItem(new Card("Bye","再见"));
        mCardAdapter.addCardItem(new Card("Jxnu","江西师范大学"));
        mCardAdapter.addCardItem(new Card("Ecnu","华东师范大学"));*/
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
