package com.example.mac.cardbox.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.example.mac.cardbox.R;
import com.example.mac.cardbox.adapter.SendMessageAdapter;
import com.example.mac.cardbox.bean.Box;
import com.example.mac.cardbox.bean.User;
import com.example.mac.cardbox.bean.UserMessage;
import com.example.mac.cardbox.util.Constant;
import com.example.mac.cardbox.util.CurrentUserUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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


public class SendMessageFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private LinearLayout ll_NoMsgHint;


    private static final String TAG = "SendMessageFragment";
    private static final String GetSendMessageUrl = "http://" + Constant.Server_IP + ":8080/CardBox-Server/GetSendMessage";
    private static final int GetSendMessageSuccess_TAG = 1;
    private static final int SearchSuccess_TAG = 2;
    private String searchUserByAccountUrl = "http://" + Constant.Server_IP + ":8080/CardBox-Server/SearchUserByAccount";
    private String GetSendMessage_Receiver_Url = "http://" + Constant.Server_IP + ":8080/CardBox-Server/GetSendMessage_Receiver";

    private List<UserMessage> MessageList;
    private List<HashMap<String, Object>> sendMessageList=null;
    private HashMap<String, Object> sendMessage=null;

    private int flag=0;
    private List<User> receiver;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GetSendMessageSuccess_TAG:
                    List<HashMap<String, Object>> result = (List<HashMap<String, Object>>)msg.obj;
                    MessageList = changeHashMapToMessage(result);

                    if (MessageList.size()==0) {
                        ll_NoMsgHint.setVisibility(View.VISIBLE);
                    }

                    receiver = new ArrayList<>();
                    for (int i =0;i<MessageList.size();i++) {
                        User user = new User();
                        receiver.add(user);
                    }
                    for(int i=0;i<MessageList.size();i++) {
                        getUserByAccount(MessageList.get(i).getUser_receiver().getUser_account(),i);
                    }
                    break;
                case SearchSuccess_TAG:
                    HashMap<String,Object> hashMap = (HashMap<String,Object>)msg.obj;
                    User user = (User)hashMap.get("user");
                    int position = (Integer)hashMap.get("position");
                    receiver.set(position,user);
                    Log.d(TAG, "handleMessage: Flag="+flag);
                    if(flag==MessageList.size()-1) {
                        ShowAllSendMessage(MessageList,receiver);
                        ll_NoMsgHint.setVisibility(View.INVISIBLE);
                    } else {
                        flag++;
                    }
                    break;
            }
        }
    };

    private void ShowAllSendMessage(List<UserMessage> userMessages,List<User> receiver) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        SendMessageAdapter sendMessageAdapter = new SendMessageAdapter(MessageList,getContext(),receiver);
        recyclerView.setAdapter(sendMessageAdapter);
    }

    private List<UserMessage> changeHashMapToMessage(List<HashMap<String, Object>> HashMaplist) {
        int length = HashMaplist.size();
        List<UserMessage> messageList = new ArrayList<UserMessage>();
        for(int i=0;i<length;i++) {
            UserMessage msg = new UserMessage();
            msg.setMessage_content(HashMaplist.get(i).get("message_content").toString());
            msg.setMessage_send_time((Timestamp) HashMaplist.get(i).get("message_send_time"));
            msg.setUser_receiver((User)HashMaplist.get(i).get("user_receiver"));
            msg.setUser_sender((User)HashMaplist.get(i).get("user_sender"));

            messageList.add(msg);
        }
        return messageList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_send_message, container, false);

        recyclerView = view.findViewById(R.id.recyclerview_send_message);
        ll_NoMsgHint = view.findViewById(R.id.ll_NoSendHint);
        //GetSendMessage(CurrentUserUtil.getCurrentUser().getUser_account());

        return view;
    }

    @Override
    public void onResume() {
        GetSendMessage(CurrentUserUtil.getCurrentUser().getUser_account());
        super.onResume();
    }

    private void GetSendMessage(String user_account) {
        //开子线程访问服务器啦
        //实例化OkHttpClient
        OkHttpClient client = new OkHttpClient();
        //创建表单请求体
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("user_account", user_account);

        //创建Request对象
        Request request = new Request.Builder()
                .url(GetSendMessageUrl)
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
                    Log.d(TAG, "onResponse: 收到了啥"+temp);
                    try{
                        JSONArray jsonArray=new JSONArray(temp);
                        sendMessageList=new ArrayList<HashMap<String, Object>>();

                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject=(JSONObject)jsonArray.get(i);
                            sendMessage=new HashMap<String, Object>();

                            sendMessage.put("message_content",jsonObject.get("message_content"));

                            /**
                             * 拿到了User的json数据
                             * 利用Gson解析Json键值对
                             */
                            String senderResult = jsonObject.get("user_sender").toString();
                            Gson gson = new Gson();
                            User sender = gson.fromJson(senderResult, User.class);
                            sendMessage.put("user_sender",sender);

                            String receiverResult = jsonObject.get("user_receiver").toString();
                            Gson gson1 = new Gson();
                            User receiver= gson1.fromJson(receiverResult, User.class);
                            sendMessage.put("user_receiver",receiver);

                            //timeresult是从数据库插到的dpost_time字段，类型为timestamp
                            //从数据库读出来长这个样子：存到timeresult里面
                            //{"date":21,"day":6,"hours":23,"minutes":18,"month":3,"nanos":0,"seconds":0,"time":1524323880000,"timezoneOffset":-480,"year":118}
                            String timeresult = jsonObject.get("message_send_time").toString();

                            //利用fastJson——JSON取出timeresult里面的time字段，也就是13位的时间戳
                            long time = JSON.parseObject(timeresult).getLong("time");
                            Timestamp trueTime = new Timestamp(time);

                            //把时间put进daike
                            sendMessage.put("message_send_time",trueTime);



                            sendMessageList.add(sendMessage);
                        }

                    }catch (JSONException a){

                    }

                    //通过handler传递数据到主线程
                    Message msg = new Message();
                    msg.what = GetSendMessageSuccess_TAG;
                    msg.obj = sendMessageList;
                    handler.sendMessage(msg);
                } else {

                }


            }
        });
    }

    private void getUserByAccount(String user_account,final int position) {
        //开子线程访问服务器啦
        //实例化OkHttpClient
        OkHttpClient client = new OkHttpClient();
        //创建表单请求体
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("user_account", user_account);

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
                        msg.what = SearchSuccess_TAG;
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("user",user);
                        hashMap.put("position",position);
                        msg.obj = hashMap;
                        handler.sendMessage(msg);


                    } catch (JSONException a) {

                    }
                } else {

                }


            }
        });
    }


    //mybatis结果集出了点问题，不用这个方法了
    /*private void GetSendMessage_Receiver(String user_account) {
        //开子线程访问服务器啦
        //实例化OkHttpClient
        OkHttpClient client = new OkHttpClient();
        //创建表单请求体
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("user_account", user_account);
        //创建Request对象
        Request request = new Request.Builder()
                .url(GetSendMessage_Receiver_Url)
                .post(formBody.build())
                .build();

        *//**
     * Get的异步请求，不需要跟同步请求一样开启子线程
     * 但是回调方法还是在子线程中执行的
     * 所以要用到Handler传数据回主线程更新UI
     *//*
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
                    //Log.d(TAG, "onResponse: temp:" + temp);

                    //利用Gson解析服务器List转换成的json字符串
                    Gson gson = new Gson();
                    List<User> userList = gson.fromJson(temp, new TypeToken<List<User>>() {
                    }.getType());

                    Message msg = new Message();
                    msg.obj = userList;
                    msg.what = GetSendMessage_Receiver_Success_TAG;
                    handler.sendMessage(msg);
                } else {

                }


            }
        });
    }*/
}
