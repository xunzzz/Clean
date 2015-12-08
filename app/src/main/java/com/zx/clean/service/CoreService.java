package com.zx.clean.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;

import com.zx.clean.R;
import com.zx.clean.bean.AppProcessInfo;
import com.zx.clean.bean.RubbishItemBean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CoreService extends Service {

    private Context mContext;

    ActivityManager activityManager = null;
    List<AppProcessInfo> list = null;
    public static List<AppProcessInfo> checkedList  = new ArrayList<AppProcessInfo>();
    private static List<AppProcessInfo> checkedData  = new ArrayList<AppProcessInfo>();
    PackageManager packageManager = null;
    OnCoreActionListener mOnCoreActionListener;


    private boolean isScanning = false;
    private boolean isCleaning = false;
    private long mCleanSize = 0;

    public CoreService() {
    }

    public static interface OnCoreActionListener {
        void onScanStarted(Context context);

        void onScanProgressUpdated(Context context, int current, int max, int size);

        void onScanCompleted(Context context, List<AppProcessInfo> apps);

        void onCleanStarted(Context context);

        void onCleanProgressUpdated(Context context, int current, int max, int size);

        void onCleanCompleted(Context context, long cleanSize);

    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private ProcessServiceBinder mBinder = new ProcessServiceBinder();

    public class ProcessServiceBinder extends Binder {
        public CoreService getService() {
            return CoreService.this;
        }
    }

    @Override
    public void onCreate() {
        mContext = getApplicationContext();
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        packageManager = mContext.getPackageManager();
    }

    private class TaskScan extends AsyncTask<Void, Integer, List<AppProcessInfo>> {

        private int mAppCount = 0;

        @Override
        protected List<AppProcessInfo> doInBackground(Void... params) {
            mCleanSize = 0;
            list = new ArrayList<AppProcessInfo>();

            ApplicationInfo appInfo = null;
            AppProcessInfo mAppProcessInfo = null;

            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
            publishProgress(0, runningAppProcesses.size(), (int) mCleanSize);
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
//                mAppProcessInfo = new AppProcessInfo(runningAppProcessInfo.uid
//                        , runningAppProcessInfo.pid, runningAppProcessInfo.processName);
                mAppProcessInfo = new AppProcessInfo();
                mAppProcessInfo.uid = runningAppProcessInfo.uid;
                mAppProcessInfo.pid = runningAppProcessInfo.pid;
                mAppProcessInfo.processName = runningAppProcessInfo.processName;

                try {
                    appInfo = packageManager.getApplicationInfo(runningAppProcessInfo.processName, 0);

                    if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                        mAppProcessInfo.isSystem = true;
                    } else {
                        mAppProcessInfo.isSystem = false;
                    }

                    Drawable icon = appInfo.loadIcon(packageManager);
                    String appName = appInfo.loadLabel(packageManager).toString();
                    mAppProcessInfo.icon = icon;
                    mAppProcessInfo.appName = appName;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    if (runningAppProcessInfo.processName.indexOf(":") != -1) {
                        appInfo = getApplicationInfo(runningAppProcessInfo.processName.split(":")[0]);
                        if (appInfo != null) {
                            Drawable icon = appInfo.loadIcon(packageManager);
                            mAppProcessInfo.icon = icon;
                        } else {
                            mAppProcessInfo.icon = mContext.getResources().getDrawable(R.mipmap.ic_launcher);
                        }

                    } else {
                        mAppProcessInfo.icon = mContext.getResources().getDrawable(R.mipmap.ic_launcher);
                    }
                    mAppProcessInfo.isSystem = true;
                    mAppProcessInfo.appName = runningAppProcessInfo.processName;
                }

                int memorySize = activityManager.getProcessMemoryInfo(
                        new int[]{runningAppProcessInfo.pid})[0].getTotalPrivateDirty() * 1024;

                mAppProcessInfo.memory = memorySize;
                mCleanSize += memorySize;
                publishProgress(++mAppCount, runningAppProcesses.size(), (int) mCleanSize);
                list.add(mAppProcessInfo);
            }
            return list;
        }

        @Override
        protected void onPreExecute() {
            if (mOnCoreActionListener != null) {
                mOnCoreActionListener.onScanStarted(CoreService.this);
            }
        }

        @Override
        protected void onPostExecute(List<AppProcessInfo> appProcessInfos) {
            if (mOnCoreActionListener != null) {
                mOnCoreActionListener.onScanCompleted(CoreService.this, appProcessInfos);
            }
            isScanning = false;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (mOnCoreActionListener != null) {
                mOnCoreActionListener.onScanProgressUpdated(CoreService.this, values[0], values[1], values[2]);
            }
        }
    }

    private ApplicationInfo getApplicationInfo(String processName) {
        if (processName == null) {
            return null;
        }
        List<ApplicationInfo> installedApplications = packageManager.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (ApplicationInfo info : installedApplications) {
            if (processName.equals(info.processName)) {
                return info;
            }
        }
        return null;
    }


    private class MemoryClean extends AsyncTask<Void, Integer, Long> {

        private int cleanCount = 0;

        @Override
        protected Long doInBackground(Void... params) {
            long beforeMemory = 0;
            long endMemory = 0;

            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(memoryInfo);
            beforeMemory = memoryInfo.availMem;


            /*List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
            cleanCount = runningAppProcesses.size();
            publishProgress(cleanCount, runningAppProcesses.size(), 0);
            for (ActivityManager.RunningAppProcessInfo info : runningAppProcesses) {
                int memorySize = activityManager.getProcessMemoryInfo(new int[]{info.pid})[0].getTotalPrivateDirty() * 1024;
                publishProgress(--cleanCount, runningAppProcesses.size(), memorySize);
                killBackgroundProcesses(info.processName);
            }
            activityManager.getMemoryInfo(memoryInfo);
            endMemory = memoryInfo.availMem;*/
            checkedData.clear();
            checkedData.addAll(checkedList);

            cleanCount = checkedList.size();
            publishProgress(cleanCount, checkedList.size(), 0);
            for (AppProcessInfo info : checkedList){
                int memorySize = activityManager.getProcessMemoryInfo(new int[]{info.pid})[0].getTotalPrivateDirty() * 1024;
                publishProgress(--cleanCount, checkedList.size(), memorySize);
                killBackgroundProcesses(info.processName);
            }
            checkedList.clear();
            activityManager.getMemoryInfo(memoryInfo);
            endMemory = memoryInfo.availMem;



            return endMemory - beforeMemory;
        }

        @Override
        protected void onPreExecute() {
            if (mOnCoreActionListener != null) {
                mOnCoreActionListener.onCleanStarted(CoreService.this);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (mOnCoreActionListener != null) {
                mOnCoreActionListener.onCleanProgressUpdated(CoreService.this, values[0], values[1], values[2]);
            }
        }

        @Override
        protected void onPostExecute(Long l) {
            if (mOnCoreActionListener != null) {
                mOnCoreActionListener.onCleanCompleted(CoreService.this, l);
            }
            isCleaning = false;
        }
    }

    private void killBackgroundProcesses(String processName) {
        String packageName = null;
        try {
            if (processName.indexOf(":") == -1) {
                packageName = processName;
            } else {
                packageName = processName.split(":")[0];
            }

            activityManager.killBackgroundProcesses(packageName);
            Method forceStopPackage = activityManager.getClass().getDeclaredMethod("forceStopPackage", String.class);
            forceStopPackage.setAccessible(true);
            forceStopPackage.invoke(activityManager, packageName);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


    }


    public void scanRunProcess() {
        isScanning = true;

        new TaskScan().execute();
    }

    public void clearRunProcess() {
        isCleaning = true;

        new MemoryClean().execute();
    }

    public boolean isScanning() {
        return isScanning;
    }

    public boolean isCleaning() {
        return isCleaning;
    }

    public long getmCleanSize() {
        return mCleanSize;
    }

    public void setCheckedList(List<AppProcessInfo> checkedList) {
        this.checkedList = checkedList;
    }

    public List<AppProcessInfo> getCheckedData() {
        return checkedData;
    }

    public void setmOnCoreActionListener(OnCoreActionListener mOnCoreActionListener) {
        this.mOnCoreActionListener = mOnCoreActionListener;
    }
}
