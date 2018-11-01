package com.example.mac.cardbox.activity;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mac.cardbox.R;
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

public class EditUserProfileActivity extends AppCompatActivity implements View.OnClickListener{
    private CircleImageView img_avatar;
    private EditText et_username;
    private EditText et_useraccount;
    private EditText et_userintro;
    private Button fake_button_edit;

    private static final String AccessKey = "HBckSDRko17AS-s_Ufbb29bYfFMKMV7opdRnx-2C";//此处填七牛云的AccessKey
    private static final String SecretKey = "grNgIr009LWhQyfGvOGua8CPWFmlqfhySioKTrdk";//此处填七牛云的SecretKey
    private static final String updateAvatarUrl="http://" + Constant.Server_IP + ":8080/CardBox-Server/updateUserAvatar";
    private static final String updateUserInfoUrl="http://" + Constant.Server_IP + ":8080/CardBox-Server/updateUserInfo";

    private static final String TAG = "EditUserProfileActivity";
    private Uri imageUri;
    private Uri localUri = null;

    private static final int REQUEST_CAMERA = 0;
    private static final int REQUEST_PICTURE = 1;
    private static final int RESULT_CROP = 2;
    private static final int UpdateUserInfoSuccess_TAG = 3;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UpdateUserInfoSuccess_TAG:
                    CurrentUserUtil.getCurrentUser().setUser_nickname(et_username.getText().toString());
                    CurrentUserUtil.getCurrentUser().setUser_intro(et_userintro.getText().toString());
                    Intent intent = new Intent(EditUserProfileActivity.this,MainNavigationActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }

            super.handleMessage(msg);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

        //自定义标题栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_edituserinfo);
        setSupportActionBar(toolbar);

        //自定义标题栏
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("编辑资料");

        //初始化
        initView();

        initUserInfo();

        //集体设置点击事件
        setOnclick();



    }

    private void initUserInfo() {
        Glide.with(getApplicationContext())
                .load(CurrentUserUtil.getCurrentUser().getUser_avatar())
                .into(img_avatar);
        et_username.setText(CurrentUserUtil.getCurrentUser().getUser_nickname());
        et_useraccount.setHint(CurrentUserUtil.getCurrentUser().getUser_account());
        et_userintro.setText(CurrentUserUtil.getCurrentUser().getUser_intro());
    }

    private void setOnclick() {
        img_avatar.setOnClickListener(this);
        fake_button_edit.setOnClickListener(this);
    }

    private void initView() {
        img_avatar = findViewById(R.id.img_editUserInfo_avatar);
        et_username = findViewById(R.id.et_EditUserInfo_username);
        et_userintro = findViewById(R.id.et_EditUserInfo_userintro);
        et_useraccount = findViewById(R.id.et_EditUserInfo_useraccount);
        fake_button_edit = findViewById(R.id.fake_button_EditUserInfo);
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_editUserInfo_avatar:
                //Toast.makeText(getApplicationContext(),"这里可以换头像啦",Toast.LENGTH_SHORT).show();
                String[] items = new String[]{ "相册"};

                new AlertDialog.Builder(EditUserProfileActivity.this)
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                switch (which) {
                                    /*case REQUEST_CAMERA:
                                        //选择拍照
                                        pickImageFromCamera();
                                        break;*/
                                    case 0:
                                        //选择相册
                                        pickImageFromPicture();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }).show();
                break;
            case R.id.fake_button_EditUserInfo:
                UpdateUserInfo(et_username.getText().toString(),et_userintro.getText().toString());
                break;
        }
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
                        img_avatar.setImageBitmap(selectedBitmap);
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

    private void UpdateUserInfo(String nickname,String intro) {
        //创建一个OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();

        //创建表单请求体
        /**
         * key值与服务器端controller中request.getParameter中的key一致
         */

        Log.d(TAG, "UpdateUserInfo: "+nickname);

        RequestBody formBody = new FormBody.Builder()
                .add("user_account", CurrentUserUtil.getCurrentUser().getUser_account())
                .add("user_nickname",nickname)
                .add("user_intro",intro)
                .build();
        //创建一个请求对象
        Request request = new Request.Builder()
                .url(updateUserInfoUrl)
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
                    Message msg = new Message();
                    Log.d(TAG, "onResponse: 咋了又");
                    msg.what = UpdateUserInfoSuccess_TAG;
                    handler.sendMessage(msg);
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
}
