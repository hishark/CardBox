package com.example.mac.cardbox.adapter;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.text.TextPaint;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mac.cardbox.R;
import com.example.mac.cardbox.bean.Card;
import com.example.mac.cardbox.util.Constant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class CardPagerAdapter extends PagerAdapter implements CardAdapter {

    private List<CardView> mViews;
    private List<Card> mData;
    private float mBaseElevation;
    private Context context;
    private boolean IsOneSideCard;

    private FrameLayout mFlContainer[];
    private LinearLayout mFlCardBack[];
    private LinearLayout mFlCardFront[];
    private  ImageButton bookmark[];
    private String bookmarkState[];

    private AnimatorSet mRightOutSet[]; // 右出动画
    private AnimatorSet mLeftInSet[]; // 左入动画

    private boolean mIsShowBack[]={false};
    private static final String TAG = "CardPagerAdapter";
    private static final int UpdateCardSuccess_TAG = 1;

    private View mCurrentView;

    private boolean isNeedAdapta = true;


    private static final String UpdateCardMarktypeUrl = "http://"+ Constant.Server_IP +":8080/CardBox-Server/UpdateCardMarktype";

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UpdateCardSuccess_TAG:
                    break;
            }

            super.handleMessage(msg);
        }
    };


    @Override
    public void setPrimaryItem(final ViewGroup container, final int position, Object object) {
        mCurrentView = (View)object;

        mFlContainer = new FrameLayout[getCount()];
        mFlCardFront = new LinearLayout[getCount()];
        mFlCardBack = new LinearLayout[getCount()];
        bookmark = new ImageButton[getCount()];
        if(mIsShowBack.length==1) {
            mIsShowBack = new boolean[getCount()];
        }

        bookmark[position]= mCurrentView.findViewById(R.id.currentbox_Card_bookmark);

        bookmarkState = new String[getCount()];
        bookmarkState[position] = mData.get(position).getCard_marktype();
        //标记初始化
        if(bookmarkState[position].equals("已标记")) {
            bookmark[position].setBackgroundResource(R.drawable.ic_bookmark_02_yellow);
        } else {
            bookmark[position].setBackgroundResource(R.drawable.ic_bookmark_02_white);
        }
        Log.d(TAG, "setPrimaryItem: "+bookmarkState[position]);

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
                if(IsOneSideCard) {
                    //Toast.makeText(context, "你是一张单面卡片，不要调皮", Toast.LENGTH_SHORT).show();
                    Snackbar.make(view,"你是一张单面卡片，不要调皮",Snackbar.LENGTH_SHORT).show();
                } else {
                    flipCardInAdapter(position);

                }
            }
        });

        bookmark[position].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: "+position);
                if (bookmarkState[position].equals("未标记")) {
                    bookmark[position].setBackgroundResource(R.drawable.ic_bookmark_02_yellow);
                    mData.get(position).setCard_marktype("已标记");
                    //去把服务器那边的也给改了~
                    UpdateCardToServer(mData.get(position).getCard_marktype(),mData.get(position).getCard_id());
                    
                    bookmarkState[position] = "已标记";
                    Log.d(TAG, "setPrimaryItem: "+bookmarkState[position]);

                } else {
                    bookmarkState[position] = "未标记";
                    mData.get(position).setCard_marktype("未标记");
                    //去把服务器那边的也给改了~
                    UpdateCardToServer(mData.get(position).getCard_marktype(),mData.get(position).getCard_id());
                    bookmark[position].setBackgroundResource(R.drawable.ic_bookmark_02_white);
                    Log.d(TAG, "setPrimaryItem: "+bookmarkState[position]);

                }
            }
        });


        super.setPrimaryItem(container, position, object);
    }

    private void UpdateCardToServer(String card_marktype,String card_id) {
        //创建一个OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();

        //创建表单请求体
        /**
         * key值与服务器端controller中request.getParameter中的key一致
         */

        RequestBody formBody = new FormBody.Builder()
                .add("card_marktype",card_marktype)
                .add("card_id", card_id)
                .build();

        //创建一个请求对象
        Request request = new Request.Builder()
                .url(UpdateCardMarktypeUrl)
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
                    Log.d(TAG, "onResponse: 卡片标记情况更新成功啦");
                    Message msg = new Message();
                    msg.what = UpdateCardSuccess_TAG;
                    handler.sendMessage(msg);
                }else{

                }
            }
        });
    }

    /*public View getPrimaryItem() {
        return mCurrentView;
    }*/

    public CardPagerAdapter(Context con,boolean flag) {
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
        context = con;
        IsOneSideCard = flag;

    }

    public void addCardItem(Card item) {
        mViews.add(null);
        mData.add(item);
    }

    public void removeAllCardItem() {
        mData.clear();
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
        //销毁的时候判断一下~~如果离开的时候是背面朝上，那么等销毁之后再回来的话用户看到的就是正面朝上（因为刷新啦），为了防止出现假翻，所以置未false初始化
        if(mIsShowBack[position] == true) {
            mIsShowBack[position] = false;
        }
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
