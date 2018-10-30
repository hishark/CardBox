package com.example.mac.cardbox.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mac.cardbox.R;
import com.example.mac.cardbox.adapter.FragmentAdapter;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {
    private View view;

    /**
     * TabLayout初尝试
     */
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private FragmentAdapter adapter;
    private List<Fragment> mFragments;
    private List<String> mTitles;
    private String[] titles = new String[]{"搜索盒子","搜索盒主"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);

        //初始化
        initView();

        //初始化TabLayout
        initTabLayout();

        return view;
    }

    /**
     * TabLayout标签页初始化
     */
    private void initTabLayout() {

        mTitles = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            mTitles.add(titles[i]);
        }
        mFragments = new ArrayList<>();
        mFragments.add(new SearchBoxFragment());
        mFragments.add(new SearchBoxerFragment());
        adapter = new FragmentAdapter(getActivity().getSupportFragmentManager(), mFragments, mTitles);
        mViewPager.setAdapter(adapter);//给ViewPager设置适配器
        mTabLayout.setupWithViewPager(mViewPager);//将TabLayout和ViewPager关联起来
        mTabLayout.getTabAt(0).setIcon(R.drawable.ic_tab_searchbox_32_yellow_01);
        mTabLayout.getTabAt(1).setIcon(R.drawable.ic_tab_searchboxer_32_yellow_01);
    }

    private void initView() {
        mTabLayout = (TabLayout)view.findViewById(R.id.search_tab_layout);
        mViewPager = (ViewPager)view.findViewById(R.id.search_Viewpager);
    }

}
