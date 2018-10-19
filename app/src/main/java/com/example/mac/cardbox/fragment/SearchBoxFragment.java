package com.example.mac.cardbox.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mac.cardbox.R;
import com.example.mac.cardbox.adapter.SearchBoxAdapter;
import com.example.mac.cardbox.bean.Box;

import java.util.ArrayList;
import java.util.List;


public class SearchBoxFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private List<Box> mBoxList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search_box, container, false);

        //初始化
        initView();

        //给recyclerView设置适配器
        setRecyclerViewAdapter();

        return view;
    }

    private void setRecyclerViewAdapter() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),3);
        recyclerView.setLayoutManager(gridLayoutManager);
        SearchBoxAdapter searchBoxAdapter = new SearchBoxAdapter(mBoxList);
        recyclerView.setAdapter(searchBoxAdapter);
    }

    private void initView() {
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView_searchBox);
        mBoxList = new ArrayList<Box>();
        Box box1 = new Box();
        box1.setBox_name("英语单词");
        Box box2 = new Box();
        box2.setBox_name("日语单词");
        Box box3 = new Box();
        box3.setBox_name("法语单词");
        Box box4 = new Box();
        box4.setBox_name("德语单词");
        mBoxList.add(box1);
        mBoxList.add(box2);
        mBoxList.add(box3);
        mBoxList.add(box4);
    }

}
