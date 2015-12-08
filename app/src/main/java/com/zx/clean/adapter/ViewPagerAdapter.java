package com.zx.clean.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangxun on 2015/11/24.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter{

    private final List<Fragment> mFragments = new ArrayList<>();
    private final List<String> mFragmentTitles = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment, String title){
        mFragments.add(fragment);
        mFragmentTitles.add(title);
    }

    @Override
    public Fragment getItem(int position) {
        if (mFragments != null && mFragments.size() > 0){
            return mFragments.get(position);
        }
        return null;
    }

    @Override
    public int getCount() {
        if (mFragments != null && mFragments.size() > 0){
            return mFragments.size();
        }
        return 0;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (mFragmentTitles != null && mFragmentTitles.size() > 0){
            return mFragmentTitles.get(position);
        }
        return super.getPageTitle(position);
    }
}
