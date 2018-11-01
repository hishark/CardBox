package com.example.mac.cardbox.activity;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mac.cardbox.R;
import com.example.mac.cardbox.bean.User;
import com.example.mac.cardbox.bean.UserRelation;
import com.example.mac.cardbox.fragment.AboutFragment;
import com.example.mac.cardbox.fragment.MyCardBoxFragment;
import com.example.mac.cardbox.fragment.MyFavouriteBoxFragment;
import com.example.mac.cardbox.fragment.ReceiveMessageFragment;
import com.example.mac.cardbox.fragment.SearchBoxFragment;
import com.example.mac.cardbox.fragment.SearchBoxerFragment;
import com.example.mac.cardbox.fragment.SearchFragment;
import com.example.mac.cardbox.fragment.SendMessageFragment;
import com.example.mac.cardbox.util.Constant;
import com.example.mac.cardbox.util.CurrentUserUtil;
import com.example.mac.cardbox.util.qiniuyun.Auth;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainNavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private long LastClickTime;
    private static String searchUserByAccountUrl = "http://" + Constant.Server_IP + ":8080/CardBox-Server/SearchUserByAccount";
    private static String updateAvatarUrl="http://" + Constant.Server_IP + ":8080/CardBox-Server/updateUserAvatar";
    private static String updateNicknameUrl="http://" + Constant.Server_IP + ":8080/CardBox-Server/updateUserNickname";
    
    private static final String TAG = "MainNavigationActivity";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static String AccessKey = "HBckSDRko17AS-s_Ufbb29bYfFMKMV7opdRnx-2C";//此处填七牛云的AccessKey
    private static String SecretKey = "grNgIr009LWhQyfGvOGua8CPWFmlqfhySioKTrdk";//此处填七牛云的SecretKey
    private Uri imageUri;
    private Uri localUri = null;

    private static final int REQUEST_CAMERA = 0;
    private static final int REQUEST_PICTURE = 1;
    private static final int RESULT_CROP = 2;

    private TextView tv_nav_userNickname;
    private TextView tv_nav_userAccount;
    private CircleImageView img_nav_userAvatar;

    private MyCardBoxFragment myCardBoxFragment;
    private SearchFragment searchFragment;
    private MyFavouriteBoxFragment myFavouriteBoxFragment;
    private AboutFragment aboutFragment;
    private SendMessageFragment sendMessageFragment;
    private ReceiveMessageFragment receiveMessageFragment;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

            }

            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navigation);

        //初始化
        initView();

        //设置默认显示的碎片为主页
        setFirstFragment();

        //标题栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.mainNavigation_toolbar);
        setSupportActionBar(toolbar);

        //悬浮按钮
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        //抽屉
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //侧滑栏
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //把当前用户的昵称以及账号显示到侧滑栏顶部
        View headerView = navigationView.getHeaderView(0);
        tv_nav_userAccount = (TextView)headerView.findViewById(R.id.nav_UserAccount);
        tv_nav_userNickname = (TextView)headerView.findViewById(R.id.nav_UserNickName);
        img_nav_userAvatar = (CircleImageView)headerView.findViewById(R.id.nav_Useravatar);
        tv_nav_userAccount.setText(CurrentUserUtil.getCurrentUser().getUser_account());
        tv_nav_userNickname.setText(CurrentUserUtil.getCurrentUser().getUser_nickname());
        Log.d(TAG, "onCreate: "+CurrentUserUtil.getCurrentUser().getUser_avatar());
        Glide.with(getApplicationContext()).load(CurrentUserUtil.getCurrentUser().getUser_avatar()).into(img_nav_userAvatar);

        /*//点击头像
        img_nav_userAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"这里可以换头像啦",Toast.LENGTH_SHORT).show();
                String title = "选择获取图片方式";
                String[] items = new String[]{"拍照", "相册"};

                new AlertDialog.Builder(MainNavigationActivity.this)
                        .setTitle(title)
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                switch (which) {
                                    case REQUEST_CAMERA:
                                        //选择拍照
                                        pickImageFromCamera();
                                        break;
                                    case REQUEST_PICTURE:
                                        //选择相册
                                        pickImageFromPicture();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }).show();
            }
        });

        //点击昵称
        tv_nav_userNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowEditNicknameDialog();
            }
        });*/
    }

    private void ShowEditNicknameDialog() {
        //加载自定义的login.xml到程序中
        final LinearLayout ll=(LinearLayout)getLayoutInflater()
                .inflate(R.layout.layout_set_user_nickname, null);

        final EditText et_nickname;
        et_nickname = (EditText)ll.findViewById(R.id.dialog_edit_usernickname);
        et_nickname.setText(tv_nav_userNickname.getText().toString().trim());

        AlertDialog dialog = new AlertDialog.Builder(MainNavigationActivity.this).setTitle("提示信息")
                .setTitle("修改昵称")
                .setCancelable(true)
                .setView(ll)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nickname = et_nickname.getText().toString().trim();
                        tv_nav_userNickname.setText(nickname);
                        UpdateUserNickname(nickname);
                    }
                })
                .create();
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#DFA22E"));
    }

    private void initView() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();

        myCardBoxFragment = new MyCardBoxFragment();
        searchFragment = new SearchFragment();
    }

    /**
     * 激活系统图库，选择一张图片
     */
    private void pickImageFromPicture() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");//图片
        startActivityForResult(galleryIntent, REQUEST_PICTURE);
    }


    //拍照
    private void pickImageFromCamera() {

        Intent intent = new Intent();
        File file = getOutputMediaFile();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {  //针对Android7.0，需要通过FileProvider封装过的路径，提供给外部调用
            imageUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);//通过FileProvider创建一个content类型的Uri，进行封装
        } else { //7.0以下，如果直接拿到相机返回的intent值，拿到的则是拍照的原图大小，很容易发生OOM，所以我们同样将返回的地址，保存到指定路径，返回到Activity时，去指定路径获取，压缩图片

            imageUri = Uri.fromFile(file);

        }
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI
        startActivityForResult(intent, REQUEST_CAMERA);//启动拍照
    }


    /**
     * 建立保存头像的路径及名称
     */
    private File getOutputMediaFile() {

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getApplicationContext().getPackageName()
                + "/Files");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "getOutputMediaFile: 我居然是孔大哥！");
                return null;
            }
        }
        File mediaFile;
        String mImageName = "avatar.png";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CAMERA:
                    if (null != imageUri) {
                        localUri = imageUri;
                        performCrop(localUri);
                    }
                    break;
                case REQUEST_PICTURE:
                    localUri = data.getData();
                    performCrop(localUri);
                    break;
                case RESULT_CROP:
                    Bundle extras = data.getExtras();
                    Bitmap selectedBitmap = extras.getParcelable("data");
                    //判断返回值extras是否为空，为空则说明用户截图没有保存就返回了，此时应该用上一张图，
                    //否则就用用户保存的图
                    if (extras == null) {
                        // ci_avatar.setImageBitmap(mBitmap);
                        // storeImage(mBitmap);
                    } else {
                        img_nav_userAvatar.setImageBitmap(selectedBitmap);
                        //Glide.with(getApplicationContext()).load(selectedBitmap).into(ci_avatar);
                        storeImage(selectedBitmap);
                        uploadImgToQiNiu();
                    }
                    break;
            }
        }
    }


    private void uploadImgToQiNiu() {
        UploadManager uploadManager = new UploadManager();
        // 设置图片名字
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String key = "userAvatar(" + CurrentUserUtil.getCurrentUser().getUser_account() + ")" +sdf.format(new Date());
        String picPath = getOutputMediaFile().toString();
        Log.d("picPath: " , picPath);

        uploadManager.put(picPath, key, Auth.create(AccessKey, SecretKey).uploadToken("cardbox"), new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject res) {
                // info.error中包含了错误信息，可打印调试
                // 上传成功后将key值上传到自己的服务器
                if (info.isOK()) {
                    Log.i(TAG, "token===" + Auth.create(AccessKey, SecretKey).uploadToken("photo"));
                    String headpicPath = "http://pgtw51o4r.bkt.clouddn.com/" + key;
                    Log.d("completePath: " ,headpicPath);
                    addAvatarToServer(headpicPath);
                    CurrentUserUtil.getCurrentUser().setUser_avatar(headpicPath);
                }

            }
        }, null);
    }


    private void addAvatarToServer(String picPath) {
        //创建一个OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();

        //创建表单请求体
        /**
         * key值与服务器端controller中request.getParameter中的key一致
         */

        RequestBody formBody = new FormBody.Builder()
                .add("user_account", CurrentUserUtil.getCurrentUser().getUser_account())
                .add("user_avatar",picPath)
                .build();
        //创建一个请求对象
        Request request = new Request.Builder()
                .url(updateAvatarUrl)
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

                }else{

                }
            }
        });
    }

    private void UpdateUserNickname(String nickname) {
        //创建一个OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();

        //创建表单请求体
        /**
         * key值与服务器端controller中request.getParameter中的key一致
         */

        RequestBody formBody = new FormBody.Builder()
                .add("user_account", CurrentUserUtil.getCurrentUser().getUser_account())
                .add("user_nickname",nickname)
                .build();
        //创建一个请求对象
        Request request = new Request.Builder()
                .url(updateNicknameUrl)
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

                }else{

                }
            }
        });
    }

    //裁剪图片
    private void performCrop(Uri uri) {
        try {
            Intent intent = new Intent("com.android.camera.action.CROP");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                grantUriPermission("com.android.camera", uri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            intent.setDataAndType(uri, "image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", 300);
            intent.putExtra("outputY", 300);
            intent.putExtra("return-data", true);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, getOutputMediaFile().toString());
            startActivityForResult(intent, RESULT_CROP);
        } catch (ActivityNotFoundException anfe) {
            String errorMessage = "你的设备不支持裁剪行为！";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**
     *保存图像
     */

    private void storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d(TAG, "Error creating media file, check storage permissions: ");
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            Log.d(TAG, "storeImage: 没存进去吧");
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //距离上次按返回键如果超过2000ms就认为是误操作，toast提醒一下，并更新LastClickTime
            if ((System.currentTimeMillis() - LastClickTime) > 2000) {
                Toast.makeText(this, "再按一次“返回”退出应用程序", Toast.LENGTH_SHORT).show();
                LastClickTime = System.currentTimeMillis();
            } else {
                //退出程序
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.DotNavigation_share) {
            //TODO:分享app给别人
            Toast.makeText(this, "分享应用~", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    private void Log_Out(){
        //Toast.makeText(this, "You clicked to log out!", Toast.LENGTH_SHORT).show();
        User user = new User();
        CurrentUserUtil.setCurrentUser(user);

        editor.putBoolean("IsLogin",false);
        editor.apply();

        if(sharedPreferences.getBoolean("IsLogin",false)==true){
            Log.d(TAG, "handleMessage: IsLogin = true");
        }else{
            Log.d(TAG, "handleMessage: IsLogin = false");
        }

        Intent intent = new Intent(MainNavigationActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            Log_Out();
        } else if (id == R.id.nav_mycardbox) {
            myCardBoxFragment = new MyCardBoxFragment();
            replaceFragment(myCardBoxFragment);
            getSupportActionBar().setTitle("我的卡盒");
        } else if (id == R.id.nav_mylove) {
            myFavouriteBoxFragment = new MyFavouriteBoxFragment();
            replaceFragment(myFavouriteBoxFragment);
            getSupportActionBar().setTitle("我喜欢的卡盒");
        } else if (id == R.id.nav_search) {
            searchFragment = new SearchFragment();
            replaceFragment(searchFragment);
            getSupportActionBar().setTitle("搜索");
        } else if (id==R.id.nav_myfollow) {
            Intent intent = new Intent(MainNavigationActivity.this,UserRelationActivity.class);
            intent.putExtra("RelationType","Follow");
            intent.putExtra("SearchUser",CurrentUserUtil.getCurrentUser());
            startActivity(intent);
        }else if (id == R.id.nav_myfollower) {
            Intent intent = new Intent(MainNavigationActivity.this,UserRelationActivity.class);
            intent.putExtra("RelationType","Follower");
            intent.putExtra("SearchUser",CurrentUserUtil.getCurrentUser());
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            aboutFragment = new AboutFragment();
            replaceFragment(aboutFragment);
            getSupportActionBar().setTitle("关于");
        } else if (id == R.id.nav_sendBox) {
            sendMessageFragment = new SendMessageFragment();
            replaceFragment(sendMessageFragment);
            getSupportActionBar().setTitle("发件箱");
        } else if (id == R.id.nav_receiveBox) {
            receiveMessageFragment = new ReceiveMessageFragment();
            replaceFragment(receiveMessageFragment);
            getSupportActionBar().setTitle("收件箱");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    /**
     *  动态添加碎片
     */
    public void replaceFragment(Fragment fragment) {
        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        transaction.replace(R.id.frameLayout_MainContent, fragment);
        transaction.commit();
    }

    /**
     * 设置默认显示的碎片
     */
    private void setFirstFragment() {
        replaceFragment(myCardBoxFragment);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }


}
