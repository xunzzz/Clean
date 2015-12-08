package com.zx.clean.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zx.clean.R;
import com.zx.clean.adapter.RubbishItemAdapter;
import com.zx.clean.bean.RubbishItemBean;
import com.zx.clean.service.CleanerService;
import com.zx.clean.utils.StorageUtil;
import com.zx.clean.utils.ThemeSet;
import com.zx.clean.views.ArcProgress;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.InjectView;
import butterknife.OnClick;

public class RubbishCleanActivity extends BaseActivity implements CleanerService.OnCleanActionListener {
    @InjectView(R.id.recyclerview)
    RecyclerView recyclerview;
    @InjectView(R.id.progress)
    ArcProgress progress;
    @InjectView(R.id.back)
    ImageButton back;
    @InjectView(R.id.advice)
    TextView advice;
    @InjectView(R.id.scan_completed)
    TextView scan_completed;
    @InjectView(R.id.rubbish_clear)
    TextView rubbish_clear;

    RubbishItemAdapter mAdapter;
    LinearLayoutManager manager;


    private List<RubbishItemBean> list = new ArrayList<>();

    private CleanerService mCleanerService;
    private boolean mAlreadyScanned = false;
    private boolean mAlreadyCleaned = true;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mCleanerService = ((CleanerService.CleanerServiceBinder) service).getService();
            mCleanerService.setmCleanListener(RubbishCleanActivity.this);

            if (!mCleanerService.isScanning() && !mAlreadyScanned) {
                mCleanerService.scanRubbish();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mCleanerService.setmCleanListener(null);
            mCleanerService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeSet.onPreCreate(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rubbish_clean_pre);
        setupRecyclerView();
        bindService(new Intent(RubbishCleanActivity.this, CleanerService.class), mServiceConnection
                , BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        unbindService(mServiceConnection);
        super.onDestroy();
    }

    private void setupRecyclerView() {
        manager = new LinearLayoutManager(RubbishCleanActivity.this);
        recyclerview.setLayoutManager(manager);
    }

    @OnClick(R.id.back)
    public void Onback() {
        finish();
    }
    @OnClick(R.id.rubbish_clear)
    public void OnClear(){
        if (!mCleanerService.isCleaning() && !mAlreadyCleaned){
            mCleanerService.cleanRubbish();
        }
    }

    @Override
    public void onScanStarted(Context context) {
        progress.setProgress(0);
        advice.setVisibility(View.GONE);
        progress.setBottomText("正在扫描");
    }

    @Override
    public void onScanProgressUpdated(Context context, int current, int max, int size) {
        BigDecimal b1 = new BigDecimal(current);
        BigDecimal b2 = new BigDecimal(max);
        double value = b1.divide(b2, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
        int currentProgress = (int) (value*100);
        progress.setProgress(currentProgress);
        scan_completed.setText(StorageUtil.convertStorage(size));
    }

    @Override
    public void onScanCompleted(Context context, List<RubbishItemBean> apps) {
        list = apps;
        advice.setVisibility(View.VISIBLE);
        progress.setBottomText("扫描完成");
        scan_completed.setText(StorageUtil.convertStorage(mCleanerService.getmCleanSize()));
        mAdapter = new RubbishItemAdapter(this, list);
        recyclerview.setAdapter(mAdapter);
        mAlreadyScanned = true;
        mAlreadyCleaned = false;
    }

    @Override
    public void onCleanStarted(Context context) {
        timer = null;
        timer = new Timer();
        progress.setProgress(100);
        advice.setVisibility(View.GONE);
        progress.setBottomText("正在清理");
    }
    private Timer timer;
    @Override
    public void onCleanProgressUpdated(Context context, int current) {

    }
    long size;
    @Override
    public void onCleanCompleted(Context context, final long cleanSize) {


        size = cleanSize;

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (progress.getProgress() > 0) {
                            progress.setProgress(progress.getProgress() - 1);
                            if (size > (cleanSize / 100)) {
                                size = size - cleanSize / 100;
                            }
                            scan_completed.setText(StorageUtil.convertStorage(size));
                        } else {
                            timer.cancel();
                            progress.setBottomText("清理完成");
                            scan_completed.setText("0MB");
                            mAdapter.updateAdapter(null);
                            Intent intent = new Intent(RubbishCleanActivity.this, CleanCompletedActivity.class);
                            intent.putExtra("cleanSize", StorageUtil.convertStorage(cleanSize));
                            intent.putExtra("appSize", list.size() + "");
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }
        }, 0, 30);
        mAlreadyCleaned = true;
    }
}
