package com.zx.clean.activity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.zx.clean.R;
import com.zx.clean.utils.ThemeSet;

import butterknife.InjectView;
import butterknife.OnClick;

public class CleanCompletedActivity extends BaseActivity {
    @InjectView(R.id.clean_size)
    TextView clean_size;
    @InjectView(R.id.clean_app_size)
    TextView clean_app_size;

    String cleanSize;
    String appSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeSet.onPreCreate(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean_completed);
        setStatusBarColor();
        getBundle();
        initView();
    }
    private void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            CleanCompletedActivity.this.getWindow().setStatusBarColor(getResources().getColor(R.color.white_light));
        }
    }
    private void initView() {
        clean_size.setText("共清理"+cleanSize+"垃圾");
        clean_app_size.setText("共清理"+appSize+"款应用垃圾");
    }

    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null){
            return;
        }
        cleanSize = bundle.getString("cleanSize", "0");
        appSize = bundle.getString("appSize", "0");
    }

    @OnClick(R.id.consume)
    public void OnConsume(){
        finish();
    }

}
