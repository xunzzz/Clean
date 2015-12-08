package com.zx.clean.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.zx.clean.R;

import java.util.Random;

public class SplashActivity extends AppCompatActivity {

    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mImageView = (ImageView) findViewById(R.id.image);

        initAnimation();

    }

    private void initAnimation() {
        int index = new Random().nextInt(2);
        if (index == 1){
            mImageView.setImageResource(R.drawable.entrance3);
        }else {
            mImageView.setImageResource(R.drawable.entrance2);
        }
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator alpha = new ObjectAnimator().ofFloat(mImageView, "alpha", 0.5f, 1f);
        alpha.setDuration(700);

        ObjectAnimator scaleX = new ObjectAnimator().ofFloat(mImageView, "scaleX", 1f, 1.1f);
        scaleX.setDuration(2000);
        scaleX.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator scaleY= new ObjectAnimator().ofFloat(mImageView, "scaleY", 1f, 1.1f);
        scaleY.setDuration(2000);
        scaleY.setInterpolator(new DecelerateInterpolator());

        set.play(scaleX).with(scaleY).after(alpha);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        set.start();
    }
}
