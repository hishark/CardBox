package com.example.mac.cardbox.adapter;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.text.TextPaint;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mac.cardbox.R;
import com.example.mac.cardbox.bean.Card;

import java.util.ArrayList;
import java.util.List;



public class CardPagerAdapter extends PagerAdapter implements CardAdapter {

    private List<CardView> mViews;
    private List<Card> mData;
    private float mBaseElevation;
    private Context context;

    private FrameLayout mFlContainer[];
    private LinearLayout mFlCardBack[];
    private LinearLayout mFlCardFront[];
    private  ImageButton bookmark[];

    private AnimatorSet mRightOutSet[]; // 右出动画
    private AnimatorSet mLeftInSet[]; // 左入动画

    private boolean mIsShowBack[]={false};
    private static final String TAG = "CardPagerAdapter";

    private View mCurrentView;

    private boolean isNeedAdapta = true;



    @Override
    public void setPrimaryItem(final ViewGroup container, final int position, Object object) {
        Log.d(TAG, "setPrimaryItem: ?咋的1");
        mCurrentView = (View)object;

        mFlContainer = new FrameLayout[getCount()];
        mFlCardFront = new LinearLayout[getCount()];
        mFlCardBack = new LinearLayout[getCount()];
        bookmark = new ImageButton[getCount()];
        if(mIsShowBack.length==1) {
            mIsShowBack = new boolean[getCount()];
        }

        mRightOutSet = new AnimatorSet[getCount()];
        mLeftInSet = new AnimatorSet[getCount()];

        mFlContainer[position] = (CardView)mCurrentView.findViewById(R.id.cardView);
        mFlCardBack[position] = (LinearLayout)mCurrentView.findViewById(R.id.ll_back);
        mFlCardFront[position] = (LinearLayout)mCurrentView.findViewById(R.id.ll_front);
        setAnimators(position);
        setCameraDistance(position);

        mCurrentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: >"+position);
                flipCardInAdapter(position);
            }
        });
        Log.d(TAG, "setPrimaryItem: ?咋的2");


        //点击标签
        bookmark[position]= mCurrentView.findViewById(R.id.currentbox_Card_bookmark);

        bookmark[position].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: "+position);
                if (bookmark[position].getContentDescription().equals("未标记")) {
                    bookmark[position].setBackgroundResource(R.drawable.ic_bookmark_02_yellow);
                    bookmark[position].setContentDescription("已标记");
                    Log.d(TAG, "onClick: "+bookmark[position].getContentDescription().toString());
                } else {
                    bookmark[position].setBackgroundResource(R.drawable.ic_bookmark_02_white);
                    bookmark[position].setContentDescription("未标记");
                    Log.d(TAG, "onClick: "+bookmark[position].getContentDescription().toString());
                }
            }
        });


        super.setPrimaryItem(container, position, object);
    }

    /*public View getPrimaryItem() {
        return mCurrentView;
    }*/

    public CardPagerAdapter(Context con) {
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
        context = con;

    }

    public void addCardItem(Card item) {
        mViews.add(null);
        mData.add(item);
    }

    protected void autoMatchFont(final TextView view){
        view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                float vWidth = v.getWidth();
                //LogUtil.d("vWidth=" + vWidth);
                TextPaint paint = view.getPaint();
                String text = view.getText().toString();
                float textLen = paint.measureText(text);
                //LogUtil.d("textLen=" + textLen);
                float oldSize = view.getTextSize();
                //LogUtil.d("oldSize=" + oldSize);
                if (textLen != vWidth && isNeedAdapta){
                    isNeedAdapta = false;
                    float size = vWidth * oldSize / textLen;
                    //LogUtil.d("size=" + size);
                    view.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
                }
            }
        });
    }


    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    //整个滑动过程中会不停的调用
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.current_card_item, container, false);
        container.addView(view);
        bind(mData.get(position), view);
        CardView cardView = (CardView)view.findViewById(R.id.cardView);
        Log.d(TAG, "instantiateItem: "+position);
        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }
        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        mViews.set(position, cardView);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        Log.d(TAG, "destroyItem: "+position);
        mViews.set(position, null);
    }

    private void bind(Card item, View view) {
        TextView frontContent = (TextView) view.findViewById(R.id.card_frontContent);
        TextView backContent = (TextView) view.findViewById(R.id.card_backContent);

        frontContent.setText(item.getCard_front());
        backContent.setText(item.getCard_back());


    }

    // 设置动画
    private void setAnimators(final int position) {
        mRightOutSet[position]= (AnimatorSet) AnimatorInflater.loadAnimator(context,R.animator.anim_out);
        if (mRightOutSet[position] == null) {
            Log.d(TAG, "setAnimators: ??????????"+position);
        }else{
            Log.d(TAG, "setAnimators: 惨还是我惨"+mRightOutSet[position].toString());
        }
        mLeftInSet[position] = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.anim_in);

        // 设置点击事件
        mRightOutSet[position].addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if(mFlContainer[position]!=null) {
                    mFlContainer[position].setClickable(false);
                }
            }
        });
        mLeftInSet[position].addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if(mFlContainer[position]!=null) {
                    mFlContainer[position].setClickable(true);
                }
            }
        });
    }
    // 改变视角距离, 贴近屏幕
    private void setCameraDistance(int position) {
        int distance = 16000;
        float scale = context.getResources().getDisplayMetrics().density * distance;
        mFlCardFront[position].setCameraDistance(scale);
        mFlCardBack[position].setCameraDistance(scale);
    }

    // 翻转卡片
    public void flipCardInAdapter(int position) {
        Log.d(TAG, "flipCardInAdapter: 进来了flip");
        // 正面朝上
        if (!mIsShowBack[position]) {
            if (mRightOutSet[position] != null) {
                mRightOutSet[position].setTarget(mFlCardFront[position]);
                mLeftInSet[position].setTarget(mFlCardBack[position]);
                mRightOutSet[position].start();
                mLeftInSet[position].start();
                mIsShowBack[position] = true;
            }
            Log.d(TAG, "flipCardInAdapter: "+position+","+mIsShowBack[position]);
        } else { // 背面朝上
            mRightOutSet[position].setTarget(mFlCardBack[position]);
            mLeftInSet[position].setTarget(mFlCardFront[position]);
            mRightOutSet[position].start();
            mLeftInSet[position].start();
            mIsShowBack[position]= false;
            Log.d(TAG, "flipCardInAdapter: "+position+","+mIsShowBack[position]);
        }
    }

}
