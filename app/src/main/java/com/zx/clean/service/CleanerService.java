package com.zx.clean.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.StatFs;

import com.zx.clean.bean.RubbishItemBean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class CleanerService extends Service {

    private Method mGetPackageSizeInfoMethod, mFreeStorageAndNotifyMethod;
    private OnCleanActionListener mCleanListener;
    private boolean isScanning = false;
    private boolean isCleaning = false;
    private long mCleanSize = 0;


    public CleanerService() {
    }

    public static interface OnCleanActionListener {
        void onScanStarted(Context context);

        void onScanProgressUpdated(Context context, int current, int max, int size);

        void onScanCompleted(Context context, List<RubbishItemBean> apps);

        void onCleanStarted(Context context);

        void onCleanProgressUpdated(Context context, int current);

        void onCleanCompleted(Context context, long cleanSize);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class CleanerServiceBinder extends Binder {
        public CleanerService getService() {
            return CleanerService.this;
        }
    }

    private CleanerServiceBinder mBinder = new CleanerServiceBinder();

    @Override
    public void onCreate() {
        try {
            mGetPackageSizeInfoMethod = getPackageManager().getClass().getMethod(
                    "getPackageSizeInfo", String.class, IPackageStatsObserver.class);

            mFreeStorageAndNotifyMethod = getPackageManager().getClass().getMethod(
                    "freeStorageAndNotify", long.class, IPackageDataObserver.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private class TaskScan extends AsyncTask<Void, Integer, List<RubbishItemBean>> {

        private int mAppCount = 0;

        @Override
        protected List<RubbishItemBean> doInBackground(Void... params) {
            mCleanSize = 0;
            final List<ApplicationInfo> applicationInfos = getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
            publishProgress(0, applicationInfos.size(), (int)mCleanSize);

            final CountDownLatch countDownLatch = new CountDownLatch(applicationInfos.size());

            final List<RubbishItemBean> apps = new ArrayList<RubbishItemBean>();
            try {
                for (ApplicationInfo info : applicationInfos) {
                    mGetPackageSizeInfoMethod.invoke(getPackageManager(), info.packageName,
                            new IPackageStatsObserver.Stub() {
                                @Override
                                public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
                                    synchronized (apps) {
                                        if (succeeded && pStats.cacheSize > 0) {
                                            try {
                                                RubbishItemBean itemBean = new RubbishItemBean(
                                                        pStats.cacheSize
                                                        , pStats.packageName
                                                        , getPackageManager().getApplicationLabel(
                                                        getPackageManager().getApplicationInfo(
                                                                pStats.packageName, PackageManager.GET_META_DATA)).toString()
                                                        , getPackageManager().getApplicationIcon(pStats.packageName)
                                                );
                                                apps.add(itemBean);
                                                mCleanSize += pStats.cacheSize;
                                            } catch (PackageManager.NameNotFoundException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        publishProgress(++mAppCount, applicationInfos.size(), (int)mCleanSize);
                                        synchronized (countDownLatch) {
                                            countDownLatch.countDown();
                                        }
                                    }
                                }
                            });
                }
                countDownLatch.await();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return apps;
        }

        @Override
        protected void onPreExecute() {
            if (mCleanListener != null) {
                mCleanListener.onScanStarted(CleanerService.this);
            }
        }

        @Override
        protected void onPostExecute(List<RubbishItemBean> rubbishItemBeans) {
            if (mCleanListener != null) {
                mCleanListener.onScanCompleted(CleanerService.this, rubbishItemBeans);
            }
            isScanning = false;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (mCleanListener != null) {
                mCleanListener.onScanProgressUpdated(CleanerService.this, values[0], values[1], values[2]);
            }
        }
    }

    private class TaskClean extends AsyncTask<Void, Integer, Long>{

        @Override
        protected Long doInBackground(Void... params) {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            StatFs statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());

            try {
                mFreeStorageAndNotifyMethod.invoke(getPackageManager(), (long)statFs.getBlockCount() * (long)statFs.getBlockSize(),
                        new IPackageDataObserver.Stub() {
                            @Override
                            public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
                                countDownLatch.countDown();
                            }
                        });
                countDownLatch.await();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return mCleanSize;
        }

        @Override
        protected void onPreExecute() {
            if (mCleanListener != null){
                mCleanListener.onCleanStarted(CleanerService.this);
            }
        }

        @Override
        protected void onPostExecute(Long aLong) {
            mCleanSize = 0;

            if (mCleanListener != null){
                mCleanListener.onCleanCompleted(CleanerService.this, aLong);
            }

            isCleaning = false;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (mCleanListener != null){
                mCleanListener.onCleanProgressUpdated(CleanerService.this, values[0]);
            }
        }
    }

    public void scanRubbish(){
        isScanning = true;
        new TaskScan().execute();
    }

    public void cleanRubbish(){
        isCleaning = true;
        new TaskClean().execute();
    }


    public void setmCleanListener(OnCleanActionListener mCleanListener) {
        this.mCleanListener = mCleanListener;
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
}
