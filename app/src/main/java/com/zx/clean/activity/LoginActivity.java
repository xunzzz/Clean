package com.zx.clean.activity;

import android.os.Bundle;

import com.zx.clean.R;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

}