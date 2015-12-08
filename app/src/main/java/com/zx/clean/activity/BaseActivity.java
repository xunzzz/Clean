package com.zx.clean.activity;

import android.support.v7.app.AppCompatActivity;

import com.zx.clean.utils.MyActivityManager;

import butterknife.ButterKnife;

/**
 * Created by zhangxun on 2015/11/20.
 */
public class BaseActivity extends AppCompatActivity{

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        MyActivityManager.getInstance().addActivity(this);
        injectViews();
    }
    protected void injectViews() {
        ButterKnife.inject(this);
    }
}
