package com.example.mac.cardbox.activity;

import android.app.ActivityOptions;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Network;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.transition.PathMotion;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mac.cardbox.R;
import com.example.mac.cardbox.util.Constant;
import com.example.mac.cardbox.util.NetworkUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView pretend_Button_signup;
    private TextInputEditText et_signup_username;
    private EditText et_sign_up_password;
    private EditText et_sign_up_assure_password;
    private TextInputLayout layout_sign_up_username;
    private TextInputLayout layout_sign_up_password;
    private TextInputLayout layout_sign_up_assure_password;
    private String getAllUserUrl = "http://"+ Constant.Server_IP +":8080/CardBox-Server/User_GetAll";
    private String insertUserUrl = "http://"+ Constant.Server_IP +":8080/CardBox-Server/Add_User";
    private String searchUserByAccountUrl = "http://"+ Constant.Server_IP +":8080/CardBox-Server/SearchUserByAccount";

    private static final String TAG = "SignUpActivity";
    private static final int User_Add_Success_TAG = 1;
    private static final int User_Add_Fail_TAG = 2;
    private static final int UserIsExist_TAG = 3;
    private static final int UserIsNotExist_TAG = 4;
    private long LastClickTime;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case User_Add_Success_TAG:
                    ShowSuccessDialog(msg.obj.toString());
                    break;
                case UserIsExist_TAG:
                    ShowUserIsExistDialog();
                    break;
            }
        }
    };


    private void ShowUserIsExistDialog() {
        et_signup_username.setError("账号已存在");
    }

    private void ShowNoNetworkDialog() {
        AlertDialog dialog = new AlertDialog.Builder(SignUpActivity.this).setTitle("提示信息")
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

    private void ShowSuccessDialog(String message) {
        Log.d(TAG, "handleMessage: " + message);

        AlertDialog dialog = new AlertDialog.Builder(SignUpActivity.this).setTitle("提示信息")
                .setMessage("注册成功！")
                .setCancelable(false)
                .setPositiveButton("登录", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                        intent.putExtra("userAccount",et_signup_username.getText().toString().trim());
                        intent.putExtra("userPassword", et_sign_up_password.getText().toString().trim());
                        startActivity(intent);
                        finish();
                    }
                }).create();
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#DFA22E"));
    }

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

        setContentView(R.layout.activity_sign_up);

        //隐藏标题栏
        getSupportActionBar().hide();

        //初始化
        initView();

        //错误处理
        handleError();

        //注册监听器
        pretend_Button_signup.setOnClickListener(this);

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

    /**
     * TODO：这里要去服务器的数据库里查询用户名是否已经存在，若存在就提示“用户名已存在”。
     */
    private void assureUserAccountUnique(String username) {

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
                    try {
                        JSONObject jsonObject = new JSONObject(temp);
                        int count = Integer.parseInt(jsonObject.get("SearchUserSum").toString());
                        Log.d(TAG, "onResponse: " + count);
                        //通过handler传递数据到主线程
                        Message msg = new Message();

                        if (count == 1) {
                            msg.what = UserIsExist_TAG;
                            msg.obj = count;
                            handler.sendMessage(msg);
                        } else {
                            msg.what = UserIsNotExist_TAG;
                            handler.sendMessage(msg);
                        }
                    } catch (JSONException a) {

                    }
                } else {

                }


            }
        });

    }

    /**
     * 错误处理
     */
    private void handleError() {
        handleErrorForUsername();
        handleErrorForPassword();
        handleErrorForAssurePassword();
    }

    /**
     * 确认密码的错误处理
     */
    private void handleErrorForAssurePassword() {
        et_sign_up_assure_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!et_sign_up_assure_password.getText().toString().trim().equals(et_sign_up_password.getText().toString().trim())) {
                    layout_sign_up_assure_password.setError("密码不匹配");
                } else {
                    layout_sign_up_assure_password.setErrorEnabled(false);
                }
            }
        });
    }

    /**
     * 输入密码的错误处理
     */
    private void handleErrorForPassword() {
        et_sign_up_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_sign_up_password.getText().length() < 6) {
                    layout_sign_up_password.setError("密码长度不能少于6位");
                } else {
                    layout_sign_up_password.setErrorEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 输入用户名的错误处理
     */
    private void handleErrorForUsername() {

        et_signup_username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            //输入用户名的同时就进行判断处理
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String username = et_signup_username.getText().toString().trim();
                if (username.length() > layout_sign_up_username.getCounterMaxLength()) {
                    et_signup_username.setError("账号太长啦，短一点~");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String username = et_signup_username.getText().toString().trim();

                if (username.length() == 0) {
                    Log.d(TAG, "afterTextChanged: amazing,what");
                    et_signup_username.setError("账号不可为空");
                }
            }
        });
    }

    /**
     * 视图初始化
     */
    private void initView() {
        pretend_Button_signup = (CardView) findViewById(R.id.pretend_Button_signup);
        et_signup_username = (TextInputEditText) findViewById(R.id.TextInputEditText_signup_username);
        et_sign_up_password = (EditText) findViewById(R.id.EditText_signup_password);
        et_sign_up_assure_password = (EditText) findViewById(R.id.EditText_signup_assure_password);
        layout_sign_up_username = (TextInputLayout) findViewById(R.id.TextInputLayout_signup_username);
        layout_sign_up_password = (TextInputLayout) findViewById(R.id.TextInputLayout_signup_password);
        layout_sign_up_assure_password = (TextInputLayout) findViewById(R.id.TextInputLayout_signup_assure_password);
    }

    /**
     * 点击事件处理汇总
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pretend_Button_signup:
                ClickToSignUp();
                break;
            default:
                break;
        }
    }

    private void ClickToSignUp() {
        boolean usernameIsOK = false;
        boolean passwordIsOk = false;
        boolean assurepasswordIsOK = false;

        String username = et_signup_username.getText().toString().trim();
        String password = et_sign_up_password.getText().toString().trim();
        String assure_password = et_sign_up_assure_password.getText().toString().trim();

        assureUserAccountUnique(username);

        //用户名
        if (username.length() == 0) {
            et_signup_username.setError("账号不可为空");
        } else if (username.length() > layout_sign_up_username.getCounterMaxLength()) {
            et_signup_username.setError("账号太长啦，短一点~");
        }/*else if(usernameIsExist(username)==true){
            et_signup_username.setError("账号已存在");
        }*/ else if (onlyCharAndNum(username) == false) {
            et_signup_username.setError("账号只允许含有英文和数字");
        } else {
            usernameIsOK = true;
        }

        //输入密码
        if (password.length() < 6) {
            layout_sign_up_password.setError("密码长度不能少于6位");
        } else {
            layout_sign_up_password.setErrorEnabled(false);
            passwordIsOk = true;
        }

        //确认密码
        if (!assure_password.equals(password)) {
            layout_sign_up_assure_password.setError("密码不匹配");
        } else {
            layout_sign_up_assure_password.setErrorEnabled(false);
            assurepasswordIsOK = true;
        }

        //如果一切都没有毛病，就注册成功啦
        if (usernameIsOK && passwordIsOk && assurepasswordIsOK) {
            //TODO：往数据库的用户表里添加一条数据！并转个圈圈等待2s
            Log.d(TAG, "ClickToSignUp: 到这啦！");
            //添加到数据库
            addNewUserToMySQL(username, password);

            //Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
            //startActivity(intent);
            //startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            //finish();
        }
    }

    private boolean onlyCharAndNum(String username) {
        boolean flag = true;
        int length = username.length();
        char c[] = username.toCharArray();
        for (int i = 0; i < length; i++) {
            if (c[i] >= '0' && c[i] <= '9' || c[i] >= 'a' && c[i] <= 'z' || c[i] >= 'A' && c[i] <= 'Z') {
                continue;
            } else {
                flag = false;
                break;
            }
        }
        return flag;
    }

    private void addNewUserToMySQL(String userAccount, String userPassword) {
        //开子线程访问服务器啦
        //实例化OkHttpClient
        OkHttpClient client = new OkHttpClient();
        //创建表单请求体
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("user_account", userAccount);
        formBody.add("user_password", userPassword);

        //创建Request对象
        Request request = new Request.Builder()
                .url(insertUserUrl)
                .post(formBody.build())
                .build();

        if (NetworkUtil.isNetworkAvailable(getApplicationContext()) == false) {
            Log.d(TAG, "addNewUserToMySQL: 没有网哦");
            ShowNoNetworkDialog();
        } else {
            Log.d(TAG, "addNewUserToMySQL: 有网有网");
        }

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
                    try {
                        JSONObject jsonObject = new JSONObject(temp);
                        result = jsonObject.get("UserAddSignal").toString();
                        Log.d(TAG, "onResponse: " + result);
                        //通过handler传递数据到主线程
                        Message msg = new Message();
                        msg.what = User_Add_Success_TAG;
                        msg.obj = result;
                        handler.sendMessage(msg);
                    } catch (JSONException a) {

                    }
                } else {

                }


            }
        });
    }
}
