package com.zx.clean.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zhangxun on 2015/11/30.
 */
public class MemoryInfoPagerAdapter extends PagerAdapter{

    private Context mContext;
    private View[] mViews;

    public MemoryInfoPagerAdapter(Context mContext, View[] mViews) {
        this.mContext = mContext;
        this.mViews = mViews;
    }

    @Override
    public int getCount() {
        return mViews.length;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View currentView = mViews[position];
        ((ViewPager) container).addView(currentView);
        return currentView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return false;
    }
}
