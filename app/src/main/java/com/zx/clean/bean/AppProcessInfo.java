package com.zx.clean.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by zhangxun on 2015/11/27.
 */
public class AppProcessInfo implements Comparable<AppProcessInfo>{
    public String appName;
    public String processName;
    /**
     * 用户进程的id
     */
    public int uid;
    /**
     * 进程pid，0代表无
     */
    public int pid;
    public Drawable icon;
    /**
     * 占用的内存
     */
    public long memory;
    /**
     * 占用的内存
     */
    public String cpu;
    /**
     * 进程的状态，其中S表示休眠，R表示正在运行，Z表示僵死状态，N表示该进程优先值是负数.
     */
    public String status;
    /**
     * 当前使用的线程数.
     */
    public String threadsCount;
    public boolean checked = true;
    /**
     * 是否是系统进程.
     */
    public boolean isSystem;

//    public AppProcessInfo(int uid, int pid, String appName) {
//        this.uid = uid;
//        this.pid = pid;
//        this.appName = appName;
//    }

    @Override
    public int compareTo(AppProcessInfo another) {
        if (this.processName.compareTo(another.processName) == 0) {
            if (this.memory < another.memory) {
                return 1;
            } else if (this.memory == another.memory) {
                return 0;
            } else {
                return -1;
            }
        } else {
            return this.processName.compareTo(another.processName);
        }
    }
}
