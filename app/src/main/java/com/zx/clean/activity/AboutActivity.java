package com.zx.clean.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.zx.clean.R;

public class AboutActivity extends AppCompatActivity {

    private RecyclerView Nm;
    private RecyclerView m1;

    private String a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);



        initView(a);
        initView2();


        findViewById(R.id.nav_manage);
    }



    private void initView2() {


    }

    private void initView(String aaa) {

        initView2();
        initView2();








    }
}
