package com.example.mac.cardbox.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.example.mac.cardbox.R;
import com.example.mac.cardbox.adapter.MyBoxAdapter;
import com.example.mac.cardbox.adapter.MyFavouriteBoxAdapter;
import com.example.mac.cardbox.bean.Box;
import com.example.mac.cardbox.bean.User;
import com.example.mac.cardbox.util.Constant;
import com.example.mac.cardbox.util.CurrentUserUtil;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyFavouriteBoxFragment extends Fragment {

    private View view;
    private static final String TAG = "MyFavouriteBoxFragment";
    private RecyclerView recyclerView;
    private List<Box> boxList;
    private List<HashMap<String, Object>> boxlist=null;
    private HashMap<String, Object> box=null;
    private static final String GetFavouriteBoxUrl =  "http://" + Constant.Server_IP + ":8080/CardBox-Server/GetFavouriteBox";
    private static final int SearchSuccess_TAG = 1;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SearchSuccess_TAG:
                    List<HashMap<String, Object>> list = (List<HashMap<String, Object>>)msg.obj;
                    showAllSearchBox(changeHashMapToBox(list));
                    Log.d(TAG, "handleMessage: hello");
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_favourite_box, container, false);

        initView();

        return view;
    }

    @Override
    public void onResume() {
        //得到所有喜欢的卡盒
        GetFavouriteBox(CurrentUserUtil.getCurrentUser().getUser_account());
        super.onResume();
    }

    private List<Box> changeHashMapToBox(List<HashMap<String, Object>> HashMaplist) {
        int length = HashMaplist.size();
        List<Box> boxList = new ArrayList<Box>();
        for(int i=0;i<length;i++) {
            Box box = new Box();
            box.setBox_name(HashMaplist.get(i).get("box_name").toString());
            box.setBox_create_time((Timestamp) HashMaplist.get(i).get("box_create_time"));
            box.setBox_update_time((Timestamp) HashMaplist.get(i).get("box_update_time"));
            box.setBox_id(HashMaplist.get(i).get("box_id").toString());
            box.setBox_love((Integer) HashMaplist.get(i).get("box_love"));
            box.setBox_side(HashMaplist.get(i).get("box_side").toString());
            box.setBox_type(HashMaplist.get(i).get("box_type").toString());
            box.setBox_authority(HashMaplist.get(i).get("box_authority").toString());
            box.setUser((User)HashMaplist.get(i).get("user"));
            box.setBox_cardnum((Integer) HashMaplist.get(i).get("box_cardnum"));

            boxList.add(box);
        }

        return boxList;
    }

    private void showAllSearchBox(List<Box> boxList) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        MyFavouriteBoxAdapter myFavouriteBoxAdapter = new MyFavouriteBoxAdapter(boxList, getContext());
        recyclerView.setAdapter(myFavouriteBoxAdapter);
    }

    private void GetFavouriteBox(String user_account) {
        //开子线程访问服务器啦
        //实例化OkHttpClient
        OkHttpClient client = new OkHttpClient();
        //创建表单请求体
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("user_account", user_account);

        //创建Request对象
        Request request = new Request.Builder()
                .url(GetFavouriteBoxUrl)
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
                    //从服务器取到Json键值对{“key”:“value”}
                    String temp = response.body().string();
                    try{
                        JSONArray jsonArray=new JSONArray(temp);
                        boxlist=new ArrayList<HashMap<String, Object>>();

                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject=(JSONObject)jsonArray.get(i);
                            box=new HashMap<String, Object>();

                            box.put("box_id", jsonObject.get("box_id"));
                            box.put("box_name",jsonObject.get("box_name"));
                            box.put("box_type", jsonObject.get("box_type"));
                            box.put("box_love", jsonObject.get("box_love"));
                            box.put("box_side", jsonObject.get("box_side"));
                            box.put("box_authority", jsonObject.get("box_authority"));
                            box.put("box_cardnum",jsonObject.get("box_cardnum"));

                            /**
                             * 拿到了User的json数据
                             * 利用Gson解析Json键值对
                             */
                            String userresult = jsonObject.get("user").toString();
                            Log.d(TAG, "onResponse: "+userresult);
                            Gson gson = new Gson();
                            User user = gson.fromJson(userresult, User.class);
                            Log.d(TAG, "onResponse: "+user.getUser_nickname());
                            box.put("user",user);

                            //timeresult是从数据库插到的dpost_time字段，类型为timestamp
                            //从数据库读出来长这个样子：存到timeresult里面
                            //{"date":21,"day":6,"hours":23,"minutes":18,"month":3,"nanos":0,"seconds":0,"time":1524323880000,"timezoneOffset":-480,"year":118}
                            String timeresult = jsonObject.get("box_create_time").toString();

                            //利用fastJson——JSON取出timeresult里面的time字段，也就是13位的时间戳
                            long time = JSON.parseObject(timeresult).getLong("time");
                            Log.d(TAG, "onResponse: 创建时间"+timeresult);
                            Timestamp trueTime = new Timestamp(time);

                            //把时间put进daike
                            box.put("box_create_time",trueTime);

                            String timeresult2 = jsonObject.get("box_update_time").toString();
                            Log.d(TAG, "onResponse: 更新时间"+timeresult2);

                            //利用fastJson——JSON取出timeresult里面的time字段，也就是13位的时间戳
                            long time2 = JSON.parseObject(timeresult2).getLong("time");
                            Timestamp trueTime2 = new Timestamp(time2);

                            //把时间put进daike
                            box.put("box_update_time",trueTime2);


                            //将13位时间戳转换为年月日时分秒！
                            /*Long time1 = 1524323880000L;
                            SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date= new Date(time1);
                            String d = format.format(date);*/

                            boxlist.add(box);
                        }

                    }catch (JSONException a){

                    }

                    //通过handler传递数据到主线程
                    Message msg = new Message();
                    msg.what = SearchSuccess_TAG;
                    msg.obj = boxlist;
                    handler.sendMessage(msg);
                } else {

                }


            }
        });
    }

    private void initView() {
        recyclerView =  view.findViewById(R.id.recyclerview_myloveBox);
    }

}
