package com.example.mac.cardbox.fragment;

import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.example.mac.cardbox.R;
import com.example.mac.cardbox.adapter.ReceiveMessageAdapter;
import com.example.mac.cardbox.adapter.SendMessageAdapter;
import com.example.mac.cardbox.bean.User;
import com.example.mac.cardbox.bean.UserMessage;
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

public class ReceiveMessageFragment extends Fragment {

    private View view;
    private static final String TAG = "ReceiveMessageFragment";
    private static final String GetReceiveMessageUrl = "http://" + Constant.Server_IP + ":8080/CardBox-Server/GetReceiveMessage";
    private static final int GetReceiveMessageSuccess_TAG = 1;
    private List<UserMessage> MessageList;
    private List<HashMap<String, Object>> receiveMessageList=null;
    private HashMap<String, Object> receiveMessage=null;
    private RecyclerView recyclerView;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GetReceiveMessageSuccess_TAG:
                    List<HashMap<String, Object>> result = (List<HashMap<String, Object>>)msg.obj;
                    MessageList = changeHashMapToMessage(result);
                    ShowAllSendMessage(MessageList);
                    break;

            }
        }
    };

    private void ShowAllSendMessage(List<UserMessage> userMessages) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        ReceiveMessageAdapter receiveMessageAdapter = new ReceiveMessageAdapter(MessageList,getContext());
        recyclerView.setAdapter(receiveMessageAdapter);
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
        view =  inflater.inflate(R.layout.fragment_receive_message, container, false);

        recyclerView = view.findViewById(R.id.recyclerview_receive_message);

        GetReceiveMessage(CurrentUserUtil.getCurrentUser().getUser_account());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void GetReceiveMessage(String user_account) {
        //开子线程访问服务器啦
        //实例化OkHttpClient
        OkHttpClient client = new OkHttpClient();
        //创建表单请求体
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("user_account", user_account);

        //创建Request对象
        Request request = new Request.Builder()
                .url(GetReceiveMessageUrl)
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
                        receiveMessageList=new ArrayList<HashMap<String, Object>>();

                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject=(JSONObject)jsonArray.get(i);
                            receiveMessage=new HashMap<String, Object>();

                            receiveMessage.put("message_content",jsonObject.get("message_content"));

                            /**
                             * 拿到了User的json数据
                             * 利用Gson解析Json键值对
                             */
                            String senderResult = jsonObject.get("user_sender").toString();
                            Gson gson = new Gson();
                            User sender = gson.fromJson(senderResult, User.class);
                            receiveMessage.put("user_sender",sender);

                            String receiverResult = jsonObject.get("user_receiver").toString();
                            Gson gson1 = new Gson();
                            User receiver= gson1.fromJson(receiverResult, User.class);
                            receiveMessage.put("user_receiver",receiver);

                            //timeresult是从数据库插到的dpost_time字段，类型为timestamp
                            //从数据库读出来长这个样子：存到timeresult里面
                            //{"date":21,"day":6,"hours":23,"minutes":18,"month":3,"nanos":0,"seconds":0,"time":1524323880000,"timezoneOffset":-480,"year":118}
                            String timeresult = jsonObject.get("message_send_time").toString();

                            //利用fastJson——JSON取出timeresult里面的time字段，也就是13位的时间戳
                            long time = JSON.parseObject(timeresult).getLong("time");
                            Timestamp trueTime = new Timestamp(time);

                            //把时间put进daike
                            receiveMessage.put("message_send_time",trueTime);



                            receiveMessageList.add(receiveMessage);
                        }

                    }catch (JSONException a){

                    }

                    //通过handler传递数据到主线程
                    Message msg = new Message();
                    msg.what = GetReceiveMessageSuccess_TAG;
                    msg.obj = receiveMessageList;
                    handler.sendMessage(msg);
                } else {

                }


            }
        });
    }

}
