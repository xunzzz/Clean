package com.zx.clean.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by zhangxun on 2015/11/24.
 */
public class AppInfo {
    private int app_icon;
    private String app_name;
    private String app_use_size;

    private Drawable appIcon;
    private String appName;
    private String packName;
    private String version;
    private int uid;
    private long pkgSize;

    /**
     * 应用程序可以被安装到不同的位置 , 手机内存 外部存储sd卡
     */


    private boolean inRom;

    private boolean userApp;


    public AppInfo(int app_icon, String app_name, String app_use_size) {
        this.app_icon = app_icon;
        this.app_name = app_name;
        this.app_use_size = app_use_size;
    }

    public AppInfo() {
    }


    public boolean isInRom() {
        return inRom;
    }

    public void setInRom(boolean inRom) {
        this.inRom = inRom;
    }

    public boolean isUserApp() {
        return userApp;
    }

    public void setUserApp(boolean userApp) {
        this.userApp = userApp;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public long getPkgSize() {
        return pkgSize;
    }

    public void setPkgSize(long pkgSize) {
        this.pkgSize = pkgSize;
    }

    public int getApp_icon() {
        return app_icon;
    }

    public void setApp_icon(int app_icon) {
        this.app_icon = app_icon;
    }

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public String getApp_use_size() {
        return app_use_size;
    }

    public void setApp_use_size(String app_use_size) {
        this.app_use_size = app_use_size;
    }
}
