package com.zx.clean.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.OrangeGangsters.circularbarpager.library.CircularBar;
import com.github.OrangeGangsters.circularbarpager.library.CircularBarPager;
import com.zx.clean.R;
import com.zx.clean.adapter.MemoryInfoPagerAdapter;
import com.zx.clean.adapter.MemoryItemAdapter;
import com.zx.clean.bean.AppProcessInfo;
import com.zx.clean.service.CleanerService;
import com.zx.clean.service.CoreService;
import com.zx.clean.utils.StorageUtil;
import com.zx.clean.utils.ThemeSet;
import com.zx.clean.views.MemoryInfoView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by zhangxun on 2015/11/30.
 */
public class MemoryCleanActivity extends BaseActivity implements CoreService.OnCoreActionListener{

    @InjectView(R.id.circularBar)
    CircularBar circularBar;
    @InjectView(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @InjectView(R.id.advice)
    TextView advice;
    @InjectView(R.id.scan_completed)
    TextView scan_completed;
    @InjectView(R.id.action)
    TextView action;
    @InjectView(R.id.checkbox)
    CheckBox checkbox;

    private List<AppProcessInfo> list = new ArrayList<>();
    private List<AppProcessInfo> checkedList  = new ArrayList<AppProcessInfo>();
//    private static List<AppProcessInfo> checkedData  = new ArrayList<AppProcessInfo>();
    private CoreService mCoreService;
    private boolean mAlreadyScanned = false;
    private boolean mAlreadyCleaned = false;
    private MemoryItemAdapter mAdapter;
    private long clearSize;


    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mCoreService = ((CoreService.ProcessServiceBinder)service).getService();
            mCoreService.setmOnCoreActionListener(MemoryCleanActivity.this);

            if (!mCoreService.isScanning() && !mAlreadyScanned){
                mCoreService.scanRunProcess();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mCoreService.setmOnCoreActionListener(null);
            mCoreService = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeSet.onPreCreate(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_clean_pre);
        setupRecyclerView();
        bindService(new Intent(MemoryCleanActivity.this, CoreService.class), mServiceConnection, BIND_AUTO_CREATE);
//        circularBar.animateProgress(0, 100, 2000);
//        circularBar.setProgress(100);
        initCheckBox();
    }

    private void initCheckBox() {
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkbox.setText("取消");
                } else {
                    checkbox.setText("全选");
                }
                mAdapter.updateAdapter(isChecked);
            }
        });
    }

    @OnClick(R.id.back)
    public void Onback() {
        finish();
    }
    @OnClick(R.id.memory_clear)
    public void onMemoryClear(){
        if (mAlreadyScanned && !mCoreService.isScanning()){
            checkedList = mAdapter.getCheckeddata();
            if (checkedList != null){
                mCoreService.setCheckedList(checkedList);
                mCoreService.clearRunProcess();
            }else {
                Toast.makeText(MemoryCleanActivity.this, "请选择要清除的应用内存", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MemoryCleanActivity.this));
    }

    @Override
    public void onScanStarted(Context context) {
        circularBar.setProgress(0);
        advice.setVisibility(View.GONE);
        action.setText("正在扫描");
        checkbox.setVisibility(View.GONE);

    }

    @Override
    public void onScanProgressUpdated(Context context, int current, int max, int size) {
        BigDecimal b1 = new BigDecimal(current);
        BigDecimal b2 = new BigDecimal(max);
        double value = b1.divide(b2, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
        int currentProgress = (int) (value*100);
        Log.i("currentProgress", "currentProgress=======: " + currentProgress);
        circularBar.setProgress(currentProgress);
        scan_completed.setText(StorageUtil.convertStorage(size));
    }

    @Override
    public void onScanCompleted(Context context, List<AppProcessInfo> apps) {
        list = apps;
        circularBar.animateProgress(0, 100, 100);
        advice.setVisibility(View.VISIBLE);
        action.setText("扫描完成");
        checkbox.setVisibility(View.VISIBLE);
        mAdapter = new MemoryItemAdapter(MemoryCleanActivity.this, list);
        mRecyclerView.setAdapter(mAdapter);
        mAlreadyScanned = true;
        mAlreadyCleaned = false;
        clearSize = mCoreService.getmCleanSize();
    }

    @Override
    public void onCleanStarted(Context context) {
        advice.setVisibility(View.VISIBLE);
        action.setText("正在清理");
        checkbox.setVisibility(View.GONE);
    }

    @Override
    public void onCleanProgressUpdated(Context context, int current, int max, int size) {
        if (clearSize > size){
            clearSize = clearSize - size;
        }
        BigDecimal b1 = new BigDecimal(current);
        BigDecimal b2 = new BigDecimal(max);
        double value = b1.divide(b2, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
        int currentProgress = (int) (value*100);
        Log.i("currentProgress", "currentProgress=======: " + currentProgress);
        circularBar.setProgress(currentProgress);
        scan_completed.setText(StorageUtil.convertStorage(clearSize));
    }

    @Override
    public void onCleanCompleted(Context context, long cleanSize) {
        advice.setVisibility(View.VISIBLE);
        action.setText("清理完成");
        checkbox.setVisibility(View.VISIBLE);
        checkbox.setChecked(false);
        checkbox.setText("全选");
        mAlreadyCleaned = true;
        circularBar.setProgress(0);
        scan_completed.setText("0M");
        List<AppProcessInfo> checkedData = mCoreService.getCheckedData();


        if (checkedData != null && checkedData.size() > 0){
//            list.removeAll(checkedData);
//            mAdapter.updateAdapter(list);
            Intent intent = new Intent(MemoryCleanActivity.this, CleanCompletedActivity.class);
            intent.putExtra("cleanSize", StorageUtil.convertStorage(cleanSize));
            intent.putExtra("appSize", checkedData.size() + "");
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
    }
}
