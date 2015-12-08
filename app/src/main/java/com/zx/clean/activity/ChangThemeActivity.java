package com.zx.clean.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zx.clean.R;
import com.zx.clean.adapter.ThemeRecycleViewAdapter;
import com.zx.clean.bean.ItemTheme;
import com.zx.clean.utils.PreferenceManager;
import com.zx.clean.utils.ThemeSet;
import com.zx.clean.views.ArcProgress;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class ChangThemeActivity extends BaseActivity implements ThemeRecycleViewAdapter.OnItemClickListener{

    @InjectView(R.id.recycleView)
    RecyclerView recycleView;
    @InjectView(R.id.top_rl)
    RelativeLayout top_rl;
    @InjectView(R.id.toolbar_rl)
    RelativeLayout toolbar_rl;
    @InjectView(R.id.circle_rl)
    RelativeLayout circle_rl;
    @InjectView(R.id.arc_store)
    ArcProgress arc_store;
    @InjectView(R.id.arc_process)
    ArcProgress arc_process;
    @InjectView(R.id.consume)
    TextView consume;
    @InjectView(R.id.back)
    ImageButton back;




    private ThemeRecycleViewAdapter adapter;
    private LinearLayoutManager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeSet.onPreCreate(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chang_theme);
        setupRecycleView();
    }


    private void setupRecycleView() {
        adapter = new ThemeRecycleViewAdapter(ChangThemeActivity.this);
        adapter.setItemClickListener(this);
        manager = new LinearLayoutManager(ChangThemeActivity.this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recycleView.setLayoutManager(manager);
        recycleView.setAdapter(adapter);
    }


    @Override
    public void onItemClick(View v, int position) {
        if (adapter != null){
            freshColor(adapter.getCheckedItem());
        }
    }

    private void freshColor(ItemTheme checkedItem) {
        top_rl.setBackgroundColor(getResources().getColor(checkedItem.getColor()));
        toolbar_rl.setBackgroundColor(getResources().getColor(checkedItem.getColor()));
        circle_rl.setBackgroundColor(getResources().getColor(checkedItem.getColor()));
        arc_store.setUnfinishedStrokeColor(getResources().getColor(checkedItem.getColorDark()));
        arc_process.setUnfinishedStrokeColor(getResources().getColor(checkedItem.getColorDark()));
    }
    @OnClick(R.id.consume)
    public void onConsume(){
        if (adapter != null && adapter.getCheckedItem() != null){
            PreferenceManager.setCurrentTheme(ChangThemeActivity.this, adapter.getCheckedItem().getTheme());
//            Intent intent = new Intent(ChangThemeActivity.this, MainActivity.class);
//            startActivity(intent);
            EventBus.getDefault().post(adapter.getCheckedItem());
            finish();
        }else {
            finish();
        }
    }
    @OnClick(R.id.back)
    public void onBack(){
        finish();
    }



}
