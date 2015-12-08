package com.zx.clean.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import com.zx.clean.bean.SDCardInfo;

import java.io.File;

/**
 * Created by zhangxun on 2015/11/24.
 */
public class StorageUtil {

    /**
     * 获取SDCard的总计大小于剩余大小
     * @return
     */
    public static SDCardInfo getSDCardInfo(){
        if (Environment.isExternalStorageRemovable()){
            String sDcString = Environment.getExternalStorageState();
            if (sDcString.equals(Environment.MEDIA_MOUNTED)){
                File pathFile = Environment.getExternalStorageDirectory();

                try {
                    StatFs statFs = new StatFs(pathFile.getPath());

                    //获取SDCard上BLOCK总数
                    long nToalBlocks = statFs.getBlockCount();

                    //获取SDCard上每个BLOCK的SIZE
                    long nBlockSize = statFs.getBlockSize();

                    //获取可供程序使用BLOCK的数量
                    long nAvailaBlock = statFs.getAvailableBlocks();

                    //获取剩下的所有BLOCK的数量（包括预留的一般程序无法使用的块）
                    long nFreeBlock = statFs.getFreeBlocks();

                    SDCardInfo info = new SDCardInfo();
                    //计算SDCard总容量大小MB
                    info.total = nToalBlocks * nBlockSize;

                    //计算SDCard剩余大小MB
                    info.free = nAvailaBlock * nBlockSize;

                    return info;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 获取系统总共存储信息
     * @param context
     * @return
     */
    public static SDCardInfo getSystemSpaceInfo(Context context){
        File path = Environment.getDataDirectory();

        SDCardInfo info = null;
        try {
            StatFs statFs = new StatFs(path.getPath());
            long blockSize = statFs.getBlockSize();
            long totalBlocks = statFs.getBlockCount();
            long availableBlocks = statFs.getAvailableBlocks();

            info = new SDCardInfo();
            info.total = totalBlocks * blockSize;
            info.free = availableBlocks * blockSize;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }

    /**
     * 转换 G M K B
     * @param size
     * @return
     */
    public static String convertStorage(long size){
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        if (size >= gb){
            return String.format("%.1f GB", (float)size / gb);
        }else if (size >= mb){
            float f = (float) size / mb;
            return String.format( f > 100 ? "%.0f MB" : "%.1f MB", f);
        }else if (size >= kb){
            float f = (float) size / kb;
            return String.format( f > 100 ? "%.0f KB" : "%.1f KB", f);
        }else {
            return String.format("%df B", size);
        }
    }

}
