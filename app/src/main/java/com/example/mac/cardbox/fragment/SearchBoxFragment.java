package com.example.mac.cardbox.fragment;

import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.mac.cardbox.R;
import com.example.mac.cardbox.adapter.SearchBoxAdapter;
import com.example.mac.cardbox.bean.Box;
import com.example.mac.cardbox.bean.User;
import com.example.mac.cardbox.util.Constant;
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


public class SearchBoxFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private List<Box> mBoxList;
    private Spinner spinner;
    private ImageView img_searchBox_button;
    private TextView tv_warnInfo;
    private EditText et_searchBox;


    private String search_box_name;

    private static final String searchBoxByTypeUrl = "http://" + Constant.Server_IP + ":8080/CardBox-Server/SearchBoxByType";
    private static final int SEARCH_BY_ALLTYPE = 0;
    private static final int SEARCH_BY_STUDY = 1;
    private static final int SEARCH_BY_LIFE = 2;
    private static final int SearchSuccess_TAG = 8;
    private static final String TAG = "SearchBoxFragment";

    private int searchType = SEARCH_BY_ALLTYPE;

    private List<HashMap<String, Object>> boxlist=null;
    private HashMap<String, Object> box=null;
    private String types[] = new String[]{
            "所有","学习","生活"
    };

    private List<Box> changeHashMapToBox(List<HashMap<String, Object>> HashMaplist) {
        int length = HashMaplist.size();
        List<Box> boxList = new ArrayList<Box>();
        for(int i=0;i<length;i++) {
            Box box = new Box();
            box.setBox_name(HashMaplist.get(i).get("box_name").toString());
            box.setBox_create_time((Timestamp) HashMaplist.get(i).get("box_create_time"));
            box.setBox_id(HashMaplist.get(i).get("box_id").toString());
            box.setBox_love((Integer) HashMaplist.get(i).get("box_love"));
            box.setBox_side(HashMaplist.get(i).get("box_side").toString());
            box.setBox_type(HashMaplist.get(i).get("box_side").toString());
            box.setUser((User)HashMaplist.get(i).get("user"));

            boxList.add(box);
        }

        return boxList;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SearchSuccess_TAG:
                    List<HashMap<String, Object>> list = (List<HashMap<String, Object>>)msg.obj;
                    showAllSearchBox(changeHashMapToBox(list));
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search_box, container, false);

        //初始化
        initView();

        //通过关键词搜索用户
        searchUserByKeyword();

        return view;
    }

    private void showAllSearchBox(List<Box> boxList) {


        if (boxList.size()==0) {
            boxList.clear();
            tv_warnInfo.setVisibility(View.VISIBLE);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
            recyclerView.setLayoutManager(gridLayoutManager);
            SearchBoxAdapter searchBoxAdapter = new SearchBoxAdapter(boxList, getContext());
            recyclerView.setAdapter(searchBoxAdapter);
        } else {
            tv_warnInfo.setVisibility(View.INVISIBLE);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
            recyclerView.setLayoutManager(gridLayoutManager);
            SearchBoxAdapter searchBoxAdapter = new SearchBoxAdapter(boxList, getContext());
            recyclerView.setAdapter(searchBoxAdapter);
        }
    }

    private void searchUserByKeyword() {
        //spinner点击事件
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            /*
             * parent接收的是被选择的数据项所属的 Spinner对象，
             * view参数接收的是显示被选择的数据项的TextView对象
             * position接收的是被选择的数据项在适配器中的位置
             * id被选择的数据项的行号
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //TextView selectedItemName = (TextView)view;
                switch (position) {
                    case SEARCH_BY_ALLTYPE:
                        searchType = SEARCH_BY_ALLTYPE;
                        break;
                    case SEARCH_BY_STUDY:
                        searchType = SEARCH_BY_STUDY;
                        break;
                    case SEARCH_BY_LIFE:
                        searchType = SEARCH_BY_LIFE;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //search_button点击事件
        img_searchBox_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击搜索时判断搜索类型
                if(!et_searchBox.getText().toString().trim().equals("")) {
                    search_box_name = et_searchBox.getText().toString().trim();
                    ConnectServerToSearch(search_box_name,searchType);
                }else {
                    List<Box> boxList = new ArrayList<Box>();
                    showAllSearchBox(boxList);
                }
            }
        });
    }

    private void ConnectServerToSearch(String search_box_name, int search_type) {
        //开子线程访问服务器啦
        //实例化OkHttpClient
        OkHttpClient client = new OkHttpClient();
        //创建表单请求体
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("search_box_name", search_box_name);
        formBody.add("search_type", types[search_type]);

        //创建Request对象
        Request request = new Request.Builder()
                .url(searchBoxByTypeUrl)
                .post(formBody.build())
                .build();

        Log.d(TAG, "ConnectServerToSearch: 我终于不要担惊受怕啦呜呜呜呜呜");
        Log.d(TAG, "ConnectServerToSearch: 这下应该搞定了吧？？？");
        
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
                            Timestamp trueTime = new Timestamp(time);

                            //把时间put进daike
                            box.put("box_create_time",trueTime);

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
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView_searchBox);
        spinner = (Spinner)view.findViewById(R.id.spinner_searchBox);
        img_searchBox_button = (ImageView)view.findViewById(R.id.img_searchBox_button);
        tv_warnInfo = (TextView)view.findViewById(R.id.tv_searchBox_warnInfo);
        et_searchBox = (EditText)view.findViewById(R.id.et_searchBox);
    }

}
