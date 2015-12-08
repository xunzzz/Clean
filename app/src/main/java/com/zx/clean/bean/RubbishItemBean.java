package com.zx.clean.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by zhangxun on 2015/11/26.
 */
public class RubbishItemBean {
    private long mCacheSize;
    private String mPackageName, mApplicationName;
    private Drawable mIcon;

    public RubbishItemBean(long mCacheSize, String mPackageName, String mApplicationName, Drawable mIcon) {
        this.mCacheSize = mCacheSize;
        this.mPackageName = mPackageName;
        this.mApplicationName = mApplicationName;
        this.mIcon = mIcon;
    }

    public long getmCacheSize() {
        return mCacheSize;
    }

    public String getmPackageName() {
        return mPackageName;
    }

    public String getmApplicationName() {
        return mApplicationName;
    }

    public Drawable getmIcon() {
        return mIcon;
    }
}
