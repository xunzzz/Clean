package com.zx.clean.fragment;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zx.clean.R;
import com.zx.clean.adapter.AppManagerItemAdapter;
import com.zx.clean.bean.AppInfo;
import com.zx.clean.utils.StorageUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by zhangxun on 2015/11/24.
 */
public class AppManagerFragment extends Fragment {

    @InjectView(R.id.recyclerview)
    RecyclerView recyclerView;
    @InjectView(R.id.app_install_count)
    TextView app_install_count;
    @InjectView(R.id.app_install_total_memory)
    TextView app_install_total_memory;
    @InjectView(R.id.progress)
    ProgressBar progress;
    @InjectView(R.id.header_rl)
    RelativeLayout header_rl;


    private Context mContext;
    private static final String ARG_POSITION = "position";
    private int position;// 0:用户应用，1 系统应用
    private ArrayList<AppInfo> list = new ArrayList<>();
    ArrayList<AppInfo> userAppInfos = null;
    ArrayList<AppInfo> systemAppInfos = null;
    AsyncTask<Void, Integer, List<AppInfo>> task;
    AppManagerItemAdapter itemAdapter;


    private Method mGetPackageSizeInfoMethod;

    public AppManagerFragment(Context mContext, int position) {
        this.mContext = mContext;
        this.position = position;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_manager, container, false);
        ButterKnife.inject(this, view);
        setupRecyclerView(recyclerView);
        try {
            mGetPackageSizeInfoMethod = mContext.getPackageManager().getClass().getMethod(
                    "getPackageSizeInfo", String.class, IPackageStatsObserver.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onPause() {
        super.onPause();
        task.cancel(true);
    }

    private void initData() {


        task = new AsyncTask<Void, Integer, List<AppInfo>>() {
            private int mAppCount = 0;

            @Override
            protected List<AppInfo> doInBackground(Void... params) {
                PackageManager packageManager = mContext.getPackageManager();
                List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
                publishProgress(0, packageInfos.size());
                list.clear();
                for (PackageInfo packageInfo : packageInfos) {
                    publishProgress(++mAppCount, packageInfos.size());
                    final AppInfo appInfo = new AppInfo();
                    Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);
                    appInfo.setAppIcon(icon);

                    int flags = packageInfo.applicationInfo.flags;
                    int uid = packageInfo.applicationInfo.uid;

                    appInfo.setUid(uid);

                    if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                        appInfo.setUserApp(false);//系统应用
                    } else {
                        appInfo.setUserApp(true);//用户应用
                    }
                    String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                    appInfo.setAppName(appName);

                    String packname = packageInfo.packageName;
                    appInfo.setPackName(packname);
                    String version = packageInfo.versionName;
                    appInfo.setVersion(version);
                    //获取应用占用的内存？？
                    try {
                        mGetPackageSizeInfoMethod.invoke(mContext.getPackageManager(), new Object[]{
                                packname,
                                new IPackageStatsObserver.Stub() {
                                    @Override
                                    public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
                                        synchronized (appInfo) {
                                            appInfo.setPkgSize(pStats.cacheSize + pStats.codeSize + pStats.dataSize);
                                        }
                                    }
                                }
                        });
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    list.add(appInfo);
                }
                return list;
            }

            @Override
            protected void onPreExecute() {
                progress.setVisibility(View.VISIBLE);
                header_rl.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                super.onPreExecute();
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
            }

            @Override
            protected void onPostExecute(List<AppInfo> appInfos) {
                super.onPostExecute(appInfos);

                progress.setVisibility(View.GONE);
                header_rl.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);

                userAppInfos = new ArrayList<>();
                systemAppInfos = new ArrayList<>();
                long userAllSize = 0;
                for (AppInfo appInfo : appInfos) {
                    if (appInfo.isUserApp()) {
                        userAllSize += appInfo.getPkgSize();
                        userAppInfos.add(appInfo);
                    } else {
                        systemAppInfos.add(appInfo);
                    }

                    if (position == 0) {//用户应用
                        app_install_count.setText("已安装" + userAppInfos.size() + "款应用");
                        app_install_total_memory.setVisibility(View.VISIBLE);
                        app_install_total_memory.setText("共" + StorageUtil.convertStorage(userAllSize));
                        itemAdapter = new AppManagerItemAdapter(mContext, userAppInfos);
                        recyclerView.setAdapter(itemAdapter);
                    } else {//系统应用
                        app_install_count.setText("系统自带" + systemAppInfos.size() + "款应用，请谨慎卸载！");
                        app_install_total_memory.setVisibility(View.GONE);
                        itemAdapter = new AppManagerItemAdapter(mContext, systemAppInfos);
                        recyclerView.setAdapter(itemAdapter);
                    }
                }
            }
        };

        task.execute();

    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    }

}
