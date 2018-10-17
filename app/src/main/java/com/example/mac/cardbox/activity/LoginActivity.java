package com.example.mac.cardbox.activity;

import android.app.ActionBar;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.transition.Explode;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mac.cardbox.R;
import com.example.mac.cardbox.bean.User;
import com.example.mac.cardbox.util.CurrentUserUtil;
import com.example.mac.cardbox.util.NetworkUtil;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 本来想用手机验证码注册，后面一想其实无所谓吧，直接用户名密码登陆注册嘻嘻
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private TextInputEditText et_login_username;
    private EditText et_login_password;
    private CardView pretend_button_login;
    private TextView tv_clickToSignUp;
    private CheckBox cb_rememberPassword;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean isRemember;

    //云服务器47.106.148.107
    private String searchUserByAccountUrl = "http://192.168.137.1:8080/CardBox-Server/SearchUserByAccount";
    private static final String TAG = "LoginActivity";
    private static final int AccountIsExist_TAG = 1;
    private static final int AccountIsNotExist_TAG = 2;
    private static final int AutoLogin_TAG = 3;

    //存放上一次点击手机返回键的时间
    private long LastClickTime;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AccountIsExist_TAG:
                    User user = (User)msg.obj;
                    CurrentUserUtil.setCurrentUser(user);
                    editor.putBoolean("IsLogin",true);
                    editor.apply();

                    if(sharedPreferences.getBoolean("IsLogin",false)==true){
                        Log.d(TAG, "handleMessage: IsLogin = true");
                    }else{
                        Log.d(TAG, "handleMessage: IsLogin = false");
                    }

                    Log.d(TAG, "handleMessage: 得到user啦"+user.getUser_account());
                    verifyPassword(user);
                    break;
                case AccountIsNotExist_TAG:
                    ShowAccountIsNotExistDialog();
                    break;
                /*case AutoLogin_TAG:
                    User AutoLoginUser = (User)msg.obj;
                    CurrentUserUtil.setCurrentUser(AutoLoginUser);
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;*/
            }
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        /*sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if(sharedPreferences.getBoolean("IsLogin",false) == true){
            Log.d(TAG, "veriftLoginState: 这里出了点问题 没法启动活动");

            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            Toast.makeText(this, "为啥跳转不了活动呢", Toast.LENGTH_SHORT).show();
            finish();
        }else{
            Toast.makeText(this, "你还没登录啦", Toast.LENGTH_SHORT).show();
        }*/

        //activity切换动画，放在这备用，登录注册界面由于要finish所以效果不太好，用到之后的activity里吧
        /*Slide slide1 = new Slide();
        slide1.setSlideEdge(Gravity.END);
        slide1.setDuration(100);
        getWindow().setEnterTransition(slide1);

        Slide slide2 = new Slide();
        slide2.setSlideEdge(Gravity.END);
        slide2.setDuration(100);
        getWindow().setExitTransition(slide2);*/



        //初始化
        initView();

        /**
         * 如果是从注册界面过来，就接收从注册页面传来的账号密码~
         */
        Intent intent = getIntent();
        String signup_username = intent.getStringExtra("userAccount");
        String signup_password = intent.getStringExtra("userPassword");
        et_login_username.setText(signup_username);
        et_login_password.setText(signup_password);

        //看看有没有存好的用户信息
        GetUserStateFromSharedPre();

        //隐藏标题栏
        getSupportActionBar().hide();


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

        //注册监听器
        pretend_button_login.setOnClickListener(this);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK) {
            //距离上次按返回键如果超过2000ms就认为是误操作，toast提醒一下，并更新LastClickTime
            if((System.currentTimeMillis() - LastClickTime) > 2000) {
                Toast.makeText(this, "再按一次“返回”退出应用程序", Toast.LENGTH_SHORT).show();
                LastClickTime = System.currentTimeMillis();
            }else{
                //退出程序
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void GetUserStateFromSharedPre(){
        //Log.d(TAG, "GetUserStateFromSharedPre: 得到状态的方法我进来了");
        isRemember = sharedPreferences.getBoolean("remember_password",false);
        //如果选中了记住密码
        if(isRemember) {
            //就把账号密码设置到输入框内
            String account = sharedPreferences.getString("useraccount","");
            String password = sharedPreferences.getString("userpassword","");
            //Log.d(TAG, "GetUserStateFromSharedPre: account "+account);
            //Log.d(TAG, "GetUserStateFromSharedPre: password "+password);
            et_login_username.setText(account);
            et_login_password.setText(password);
            cb_rememberPassword.setChecked(true);
            Log.d(TAG, "GetUserStateFromSharedPre: 我进来了呀！");



        }else{
            Log.d(TAG, "GetUserStateFromSharedPre: 为啥没记住呢");
        }

    }

    private void verifyPassword(User user) {
        //密码正确，登录成功
        if(et_login_password.getText().toString().trim().equals(user.getUser_password())){
            //把账号密码存到sharedPreferences里去
            if(cb_rememberPassword.isChecked()){
                editor.putBoolean("remember_password",true);
                editor.putString("useraccount",et_login_username.getText().toString().trim());
                editor.putString("userpassword",et_login_password.getText().toString().trim());

                Log.d(TAG, "verifyPassword: 存进sp啦！");
            }else{
                Log.d(TAG, "verifyPassword: 没选中checkbox哦");
                editor.clear();
            }
            //apply一定要放到这里！！放上面的话clear没有apply上去啊大哥！！
            editor.apply();
            ShowLoginSuccessDialog();
        }else{
            //密码错误，弹窗警告
            ShowPasswordWrongDialog();
        }
    }

    private void ShowPasswordWrongDialog() {
        AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this).setTitle("提示信息")
                .setMessage("密码错啦！")
                .setCancelable(true)
                .setPositiveButton("我想想", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#DFA22E"));
    }

    private void ShowLoginSuccessDialog() {
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        //progressDialog.setTitle("提示信息");
        progressDialog.setMessage("登录中");
        progressDialog.setCancelable(true);
        progressDialog.show();

        TimerTask task = new TimerTask(){

            public void run(){

                //execute the task
                progressDialog.dismiss();
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }

        };

        Timer timer = new Timer();
        timer.schedule(task, 1000);
    }

    private void ShowAccountIsNotExistDialog() {
        AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this).setTitle("提示信息")
                .setMessage("账号不存在，输错啦？")
                .setCancelable(false)
                .setPositiveButton("好像是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#DFA22E"));
    }

    private void initView() {
        pretend_button_login = (CardView)findViewById(R.id.pretend_Button_login);
        tv_clickToSignUp = (TextView)findViewById(R.id.tv_clickToSignUp);
        et_login_username = (TextInputEditText)findViewById(R.id.TextInputEditText_login_username);
        et_login_password = (EditText)findViewById(R.id.TextInputEditText_login_password);
        cb_rememberPassword = (CheckBox)findViewById(R.id.cb_remember_password);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreferences.edit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pretend_Button_login:
                ClickToLogIn();
                break;
        }
    }

    /**
     * 登录有三种情况
     * 1. 账号不存在！
     * 2. 密码错误！
     * 3. 没有网！
     *
     * 先判断有没有网，没网就弹窗，有网就判断账号存不存在，不存在的话就弹窗警告。如果存在的话再判断密码正不正确。
     * 密码正确就登录成功，密码错误就弹窗登录失败。
     *
     */
    private void ClickToLogIn() {
        //Toast.makeText(this, "you clicked the button", Toast.LENGTH_SHORT).show();
        String username = et_login_username.getText().toString().trim();
        String password = et_login_password.getText().toString().trim();

        if (NetworkUtil.isNetworkAvailable(getApplicationContext()) == false) {
            Log.d(TAG, "addNewUserToMySQL: 没有网哦");
            ShowNoNetworkDialog();
        } else {
            Log.d(TAG, "addNewUserToMySQL: 有网有网");
            verifyAccountIfExist(username);
        }
    }

    private void verifyAccountIfExist(String username){
        Log.d(TAG, "verifyAccountIfExist: 进来啦");
        //开子线程访问服务器啦
        //实例化OkHttpClient
        OkHttpClient client = new OkHttpClient();
        //创建表单请求体
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("user_account", username);

        //创建Request对象
        Request request = new Request.Builder()
                .url(searchUserByAccountUrl)
                .post(formBody.build())
                .build();

        /**
         * Get的异步请求，不需要跟同步请求一样开启子线程
         * 但是回调方法还是在子线程中执行的
         * 所以要用到Handler传数据回主线程更新UI
         */
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            //回调的方法执行在子线程
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = "";
                if (response.isSuccessful()) {
                    //从服务器取到Json键值对
                    String temp = response.body().string();
                    Log.d(TAG, "onResponse: temp:"+temp);
                    try {
                        JSONObject jsonObject = new JSONObject(temp);
                        int count = Integer.parseInt(jsonObject.get("SearchUserSum").toString());
                        Message msg = new Message();
                        if(count==1){
                            //利用Gson解析User
                            String userresult = jsonObject.get("SearchUser").toString();
                            Gson gson = new Gson();
                            User user = gson.fromJson(userresult, User.class);

                            //通过handler传递数据到主线程
                            msg.what = AccountIsExist_TAG;
                            msg.obj = user;
                            handler.sendMessage(msg);
                        }else{
                            msg.what = AccountIsNotExist_TAG;
                            handler.sendMessage(msg);
                        }


                    } catch (JSONException a) {

                    }
                } else {

                }


            }
        });
    }

    private void ShowNoNetworkDialog() {
        AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this).setTitle("提示信息")
                .setMessage("没有网哦o(≧口≦)o")
                .setCancelable(false)
                .setPositiveButton("好滴我去连个WIFI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();

        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#DFA22E"));
    }

    private void AutoLogin(String username) {
        Log.d(TAG, "AutoLogin: 进来啦");
        //开子线程访问服务器啦
        //实例化OkHttpClient
        OkHttpClient client = new OkHttpClient();
        //创建表单请求体
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("user_account", username);

        //创建Request对象
        Request request = new Request.Builder()
                .url(searchUserByAccountUrl)
                .post(formBody.build())
                .build();

        /**
         * Get的异步请求，不需要跟同步请求一样开启子线程
         * 但是回调方法还是在子线程中执行的
         * 所以要用到Handler传数据回主线程更新UI
         */
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            //回调的方法执行在子线程
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = "";
                if (response.isSuccessful()) {
                    //从服务器取到Json键值对
                    String temp = response.body().string();
                    Log.d(TAG, "onResponse: temp:" + temp);
                    try {
                        JSONObject jsonObject = new JSONObject(temp);
                        Message msg = new Message();

                        //利用Gson解析User
                        String userresult = jsonObject.get("SearchUser").toString();
                        Gson gson = new Gson();
                        User user = gson.fromJson(userresult, User.class);

                        //通过handler传递数据到主线程
                        msg.what = AutoLogin_TAG;
                        msg.obj = user;
                        handler.sendMessage(msg);


                    } catch (JSONException a) {

                    }
                } else {

                }


            }
        });
    }
}
