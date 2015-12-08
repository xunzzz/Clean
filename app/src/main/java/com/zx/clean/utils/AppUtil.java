package com.zx.clean.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Created by zhangxun on 2015/11/24.
 */
public class AppUtil {

    /**
     * 获取可用内存
     * @param context
     * @return
     */
    public static long getAvailMemory(Context context){
        //获取android当前可用内存大小
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo.availMem;
    }

    public static long getTotalMemory(Context context){
        //系统内存信息文件
        String file = "/proc/meminfo";
        String memInfo;
        String[] strs;
        long memory = 0;

        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader, 8192);
            //读取meminfo第一行，系统内存大小
            memInfo = bufferedReader.readLine();
            strs = memInfo.split("\\s+");
            for (String str : strs){
                Log.d("AppUtil", str + "\t");
            }
            //获取系统总内存，单位KB
            memory = Integer.valueOf(strs[1]).intValue();
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return memory * 1024;
    }

}
