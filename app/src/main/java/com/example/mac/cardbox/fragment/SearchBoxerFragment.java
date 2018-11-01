package com.example.mac.cardbox.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mac.cardbox.R;
import com.example.mac.cardbox.adapter.SearchBoxAdapter;
import com.example.mac.cardbox.adapter.SearchBoxerAdapter;
import com.example.mac.cardbox.bean.User;
import com.example.mac.cardbox.util.Constant;
import com.example.mac.cardbox.util.CurrentUserUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchBoxerFragment extends Fragment {

    private View view;
    private EditText et_searchBoxer;
    private ImageButton img_searchBoxer_button;
    private Spinner spinner;
    private RecyclerView recyclerView;
    private TextView tv_warnInfo;

    private String search_user_nickname;
    private String search_user_account;

    private String searchAllUserByNicknameUrl = "http://" + Constant.Server_IP + ":8080/CardBox-Server/SearchAllUserByNickName";
    private String searchAllUserByAccountUrl = "http://" + Constant.Server_IP + ":8080/CardBox-Server/SearchAllUserByAccount";
    private String searchUserByAccountUrl = "http://" + Constant.Server_IP + ":8080/CardBox-Server/SearchUserByAccount";


    private static final String TAG = "SearchBoxerFragment";
    private static final int SEARCH_BY_NICKNAME = 0;
    private static final int SEARCH_BY_ACCOUNT = 1;

    private static final int SearchAllUserByNickname_TAG = 10;
    private static final int SearchAllUserByAccount_TAG = 11;

    private int searchType = SEARCH_BY_NICKNAME;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SearchAllUserByNickname_TAG:
                    showAllSearchUser((List<User>) msg.obj);
                    break;
                case SearchAllUserByAccount_TAG:
                    User user = (User) msg.obj;
                    List<User> userList = new ArrayList<User>();
                    if(user!=null) {
                        userList.add(user);
                    }
                    showAllSearchUser(userList);
                    break;
            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search_boxer, container, false);

        //初始化
        initView();

        //通过关键词搜索用户
        searchUserByKeyword();

        et_searchBoxer.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {


                if (keyCode == EditorInfo.IME_ACTION_SEND
                        || keyCode == EditorInfo.IME_ACTION_DONE
                        || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                    //处理事件
                    //点击搜索时判断搜索类型
                    switch (searchType) {
                        case SEARCH_BY_NICKNAME:
                            if(!et_searchBoxer.getText().toString().trim().equals("")) {
                                search_user_nickname = et_searchBoxer.getText().toString().trim();
                                ConnectServerToSearchByName(search_user_nickname);
                            }else {
                                List<User> userList = new ArrayList<User>();
                                showAllSearchUser(userList);
                            }
                            //search_user_nickname = et_searchBoxer.getText().toString().trim();
                            //ConnectServerToSearchByName(search_user_nickname);
                            break;
                        case SEARCH_BY_ACCOUNT:
                            search_user_account = et_searchBoxer.getText().toString().trim();
                            ConnectServerToSearchByAccount(search_user_account);
                    }
                }
                return false;
            }
        });

        return view;
    }

    private void showAllSearchUser(List<User> userList) {


        if (userList.size()==0) {
            userList.clear();
            tv_warnInfo.setVisibility(View.VISIBLE);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
            recyclerView.setLayoutManager(gridLayoutManager);
            SearchBoxerAdapter searchBoxerAdapter = new SearchBoxerAdapter(userList, getContext());
            recyclerView.setAdapter(searchBoxerAdapter);
        } else if (userList.size() == 1) {
            tv_warnInfo.setVisibility(View.INVISIBLE);
            //TODO:这里本想设置成1是想让一个用户处于最中央会好看，可是老没办法垂直居中，暂时搁置，先改成3
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
            recyclerView.setLayoutManager(gridLayoutManager);
            SearchBoxerAdapter searchBoxerAdapter = new SearchBoxerAdapter(userList, getContext());
            recyclerView.setAdapter(searchBoxerAdapter);
        } else {
            tv_warnInfo.setVisibility(View.INVISIBLE);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
            recyclerView.setLayoutManager(gridLayoutManager);
            SearchBoxerAdapter searchBoxerAdapter = new SearchBoxerAdapter(userList, getContext());
            recyclerView.setAdapter(searchBoxerAdapter);
        }
    }


    private void searchUserByKeyword() {
        //search_user_nickname = et_searchBoxer.getText().toString().trim();
        //Log.d(TAG, "searchUserByKeyword: "+search_user_nickname);

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
                    case SEARCH_BY_NICKNAME:
                        searchType = SEARCH_BY_NICKNAME;
                        break;
                    case SEARCH_BY_ACCOUNT:
                        searchType = SEARCH_BY_ACCOUNT;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //search_button点击事件
        img_searchBoxer_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击搜索时判断搜索类型
                switch (searchType) {
                    case SEARCH_BY_NICKNAME:
                        if(!et_searchBoxer.getText().toString().trim().equals("")) {
                            search_user_nickname = et_searchBoxer.getText().toString().trim();
                            ConnectServerToSearchByName(search_user_nickname);
                        }else {
                            List<User> userList = new ArrayList<User>();
                            showAllSearchUser(userList);
                        }

                        break;
                    case SEARCH_BY_ACCOUNT:
                        search_user_account = et_searchBoxer.getText().toString().trim();
                        ConnectServerToSearchByAccount(search_user_account);
                }
            }
        });
    }

    private void ConnectServerToSearchByName(final String user_nickname) {
        //开子线程访问服务器啦
        //实例化OkHttpClient
        OkHttpClient client = new OkHttpClient();
        //创建表单请求体
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("nickname_keyword", user_nickname);
        Log.d(TAG, "searchUserByNickName: " + user_nickname);
        //创建Request对象
        Request request = new Request.Builder()
                .url(searchAllUserByNicknameUrl)
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

                    //利用Gson解析服务器List转换成的json字符串
                    Gson gson = new Gson();
                    List<User> userList = gson.fromJson(temp, new TypeToken<List<User>>() {
                    }.getType());

                    Message msg = new Message();
                    msg.obj = userList;
                    msg.what = SearchAllUserByNickname_TAG;
                    handler.sendMessage(msg);
                } else {

                }


            }
        });
    }

    private void ConnectServerToSearchByAccount(final String user_account) {
        //开子线程访问服务器啦
        //实例化OkHttpClient
        OkHttpClient client = new OkHttpClient();
        //创建表单请求体
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("user_account", user_account);
        Log.d(TAG, "searchUserByAccount: " + user_account);
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
                    JSONObject jsonObject = null;
                    String userresult = "";
                    try {
                        jsonObject = new JSONObject(temp);
                        userresult = jsonObject.get("SearchUser").toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    Log.d(TAG, "onResponse: temp:" + temp);

                    //利用Gson解析服务器List转换成的json字符串
                    Gson gson = new Gson();
                    User user = gson.fromJson(userresult, User.class);

                    Message msg = new Message();
                    msg.obj = user;
                    msg.what = SearchAllUserByAccount_TAG;
                    handler.sendMessage(msg);
                } else {

                }


            }
        });
    }

    private void initView() {
        et_searchBoxer = (EditText) view.findViewById(R.id.et_searchBoxer);
        img_searchBoxer_button =  view.findViewById(R.id.img_searchBoxer_button);
        spinner = (Spinner) view.findViewById(R.id.spinner_searchBoxer);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_searchBoxer);
        tv_warnInfo = (TextView) view.findViewById(R.id.tv_searchBoxer_warnInfo);
    }

}
