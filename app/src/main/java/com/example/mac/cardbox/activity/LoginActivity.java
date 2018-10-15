package com.example.mac.cardbox.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.transition.Explode;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.mac.cardbox.R;
/**
 * 本来想用手机验证码注册，后面一想其实无所谓吧，直接用户名密码登陆注册嘻嘻
 */
public class LoginActivity extends AppCompatActivity {

    private CardView pretend_button_login;
    private TextView tv_clickToSignUp;
    //云服务器47.106.148.107
    private String SearchUserUrl = "http://192.168.137.1:8080/CardBox-Server/Search_User";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //activity切换动画，放在这备用，登录注册界面由于要finish所以效果不太好，用到之后的activity里吧
        /*Slide slide1 = new Slide();
        slide1.setSlideEdge(Gravity.END);
        slide1.setDuration(100);
        getWindow().setEnterTransition(slide1);

        Slide slide2 = new Slide();
        slide2.setSlideEdge(Gravity.END);
        slide2.setDuration(100);
        getWindow().setExitTransition(slide2);*/

        setContentView(R.layout.activity_login);

        /**
         * 隐藏标题栏
         */
        getSupportActionBar().hide();

        initView();

        /**
         * 点击[没有账号？注册一个]跳转到注册界面
         */
        tv_clickToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
                //startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this).toBundle());
                finish();


            }
        });

        /**
         * 控制图标大小（用svg之后就不需要这么做啦，不过代码留着以备后用）
         */
        /*TextInputEditText et_username = (TextInputEditText) findViewById(R.id.TextInputEditText_login_username);
        Drawable draw_username = getResources().getDrawable(R.drawable.username02_128_yellow);
        draw_username.setBounds(0, 0, 100, 100);//第一0是距左边距离，第二0是距上边距离，第三第四分别是长宽（单位为像素）
        et_username.setCompoundDrawables(draw_username, null, null, null);//只放左边*/

    }

    private void initView() {
        pretend_button_login = (CardView)findViewById(R.id.pretend_Button_login);
        tv_clickToSignUp = (TextView)findViewById(R.id.tv_clickToSignUp);
    }
}
