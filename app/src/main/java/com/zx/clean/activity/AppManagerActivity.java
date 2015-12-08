package com.zx.clean.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.zx.clean.R;
import com.zx.clean.adapter.ViewPagerAdapter;
import com.zx.clean.bean.AppInfo;
import com.zx.clean.fragment.AppManagerFragment;
import com.zx.clean.utils.ThemeSet;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.OnClick;

public class AppManagerActivity extends BaseActivity {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.tabLayout)
    TabLayout tabLayout;
    @InjectView(R.id.viewpager)
    ViewPager viewpager;

    private ViewPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeSet.onPreCreate(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        setupViewPager();

    }

    private void setupViewPager() {
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mAdapter.addFragment(new AppManagerFragment(AppManagerActivity.this, 0), "用户应用");
        mAdapter.addFragment(new AppManagerFragment(AppManagerActivity.this, 1), "系统应用");
        viewpager.setAdapter(mAdapter);
        tabLayout.setupWithViewPager(viewpager);
    }

    @OnClick(R.id.back)
    public void onBack(){
        finish();
    }
}
