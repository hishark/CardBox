package com.example.mac.cardbox.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import com.example.mac.cardbox.R;
/**
 * 本来想用手机验证码注册，后面一想其实无所谓吧，直接用户名密码登陆注册嘻嘻
 */
public class LoginActivity extends AppCompatActivity {

    private CardView pretend_button_login;
    private TextView tv_clickToSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();

        /**
         * 给登录设置一个点击事件，点击后阴影变深，伪装成Button哈哈哈哈
         */
        pretend_button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pretend_button_login.setCardElevation(10);
            }
        });

        /**
         * 点击[没有账号？注册一个]跳转到注册界面
         */
        tv_clickToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
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
