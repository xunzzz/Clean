package com.zx.clean.utils;

import android.app.Activity;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangxun on 2015/11/23.
 */
public class MyActivityManager {

    public List<Activity> activityList = new ArrayList<>();

    public static MyActivityManager activityManager;

    private MyActivityManager() {
    }

    public static MyActivityManager getInstance(){
        if (activityManager == null){
            activityManager = new MyActivityManager();
        }
        return activityManager;
    }

    public void addActivity(Activity activity){
        activityList.add(activity);
    }

    public void removeActivity(Activity activity){
        activityList.remove(activity);
    }

    public void finishAllActivity(){
        if (activityList != null && activityList.size() > 0){
            for (Activity activity : activityList){
                activity.finish();
            }
        }
    }

    public void exit(Context context){
        finishAllActivity();
        System.exit(0);
    }


}
