package com.zx.clean.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.zx.clean.R;
import com.zx.clean.bean.ItemTheme;
import com.zx.clean.bean.SDCardInfo;
import com.zx.clean.utils.AppUtil;
import com.zx.clean.utils.MyActivityManager;
import com.zx.clean.utils.DrawerNavigationItemListener;
import com.zx.clean.utils.StorageUtil;
import com.zx.clean.utils.ThemeSet;
import com.zx.clean.views.ArcProgress;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, DrawerNavigationItemListener {
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.header_rl)
    RelativeLayout header_rl;
    @InjectView(R.id.arc_store)
    ArcProgress arc_store;
    @InjectView(R.id.arc_left_text)
    TextView arc_left_text;
    @InjectView(R.id.arc_process)
    ArcProgress arc_process;
    @InjectView(R.id.nav_view)
    NavigationView navigationView;
    @InjectView(R.id.drawer_layout)
    DrawerLayout drawer;
    @InjectView(R.id.app_manage)
    MaterialRippleLayout app_manage;


    private int selectedItemId;
    private Timer timer;
    private Timer timer2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeSet.onPreCreate(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolbar();
        setupDrawer();

        EventBus.getDefault().register(this);
    }

    @OnClick(R.id.app_manage)
    public void onAppManage(){
        Intent intent = new Intent(MainActivity.this, AppManagerActivity.class);
        startActivity(intent);
    }
    @OnClick(R.id.rubbish_clear)
    public void onRubbishClear(){
        Intent intent = new Intent(MainActivity.this, RubbishCleanActivity.class);
        startActivity(intent);
    }
    @OnClick(R.id.memory_accelerate)
    public void onMemory_accelerate(){
        Intent intent = new Intent(MainActivity.this, MemoryCleanActivity.class);
        startActivity(intent);
    }


    private void setupDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                drawerColse(selectedItemId);
                invalidateOptionsMenu();
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
    }


    public void onEventMainThread(ItemTheme itemTheme) {
        toolbar.setBackgroundColor(getResources().getColor(itemTheme.getColor()));
        header_rl.setBackgroundColor(getResources().getColor(itemTheme.getColor()));
        arc_store.setUnfinishedStrokeColor(getResources().getColor(itemTheme.getColorDark()));
        arc_process.setUnfinishedStrokeColor(getResources().getColor(itemTheme.getColorDark()));
        setStatusBarColor(itemTheme.getColor());
    }

    private void setStatusBarColor(int colorDark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            MainActivity.this.getWindow().setStatusBarColor(getResources().getColor(colorDark));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fillData();
    }

    private void fillData() {
        timer = null;
        timer2 = null;
        timer = new Timer();
        timer2 = new Timer();

        long l = AppUtil.getAvailMemory(MainActivity.this);
        long y = AppUtil.getTotalMemory(MainActivity.this);
        final double x = ((y - l) / (double) y) * 100;

        arc_process.setProgress(0);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (arc_process.getProgress() >= (int) x) {
                            timer.cancel();
                        } else {
                            arc_process.setProgress(arc_process.getProgress() + 1);
                        }
                    }
                });
            }
        }, 50, 20);

        SDCardInfo mSDCardInfo = StorageUtil.getSDCardInfo();
        SDCardInfo mSystemIfo = StorageUtil.getSystemSpaceInfo(MainActivity.this);

        long nAvailaBlock;
        long totalBlocks;
        if (mSDCardInfo != null){
            nAvailaBlock = mSDCardInfo.free + mSystemIfo.free;
            totalBlocks = mSDCardInfo.total + mSystemIfo.total;
        }else {
            nAvailaBlock = mSystemIfo.free;
            totalBlocks = mSystemIfo.total;
        }

        final double percentStore = ((totalBlocks - nAvailaBlock) / (double) totalBlocks) * 100;
        arc_left_text.setText(StorageUtil.convertStorage(totalBlocks - nAvailaBlock) + "/"
                + StorageUtil.convertStorage(totalBlocks));
        arc_store.setProgress(0);
        timer2.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (arc_store.getProgress() >= percentStore){
                            timer2.cancel();
                        }else {
                            arc_store.setProgress(arc_store.getProgress() + 1);
                        }
                    }
                });
            }
        }, 50, 20);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            MyActivityManager.getInstance().exit(MainActivity.this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            selectedItemId = R.id.nav_home;
        } else if (id == R.id.nav_settings) {
            selectedItemId = R.id.nav_settings;
        } else if (id == R.id.nav_change_theme) {
            selectedItemId = R.id.nav_change_theme;
        } else if (id == R.id.nav_tools) {
            selectedItemId = R.id.nav_tools;
        }
        /*else if (id == R.id.nav_share) {
            selectedItemId = R.id.nav_share;
        } else if (id == R.id.nav_send) {
            selectedItemId = R.id.nav_send;
        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void drawerColse(int id) {
        switch (id){
            case R.id.nav_change_theme :
                startActivity(new Intent(MainActivity.this, ChangThemeActivity.class));
                break;
        }
        selectedItemId = 0;
    }
}
