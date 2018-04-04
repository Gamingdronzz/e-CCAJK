package com.ccajk.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.ccajk.R;

public class SplashActivity extends AppCompatActivity {

    ImageView imageView;
    TextView gd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        bindVIews();
        //ShowNextActivity();
        StartAnimations();
    }

    private void bindVIews() {
        imageView = findViewById(R.id.logo);
        //gd = findViewById(R.id.designedBy);

    }

    private void StartAnimations() {
        final Animation animationAlpha = AnimationUtils.loadAnimation(this, R.anim.alpha);
        final Animation animationScale = AnimationUtils.loadAnimation(this, R.anim.scale);
        final Animation animationBounce = AnimationUtils.loadAnimation(this, R.anim.bounce);

        animationAlpha.reset();
        animationScale.reset();
        animationBounce.reset();

        imageView.clearAnimation();
        imageView.startAnimation(animationScale);

        //gd.clearAnimation();
        //gd.startAnimation(animationBounce);

        animationScale.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                LoadNextActivity();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void LoadNextActivity() {
        Intent intent = new Intent();
        /*if (Preferences.getInstance().getSignedIn(this))
            intent.setClass(getApplicationContext(), HomeActivity.class);
        else
            intent.setClass(getApplicationContext(), RegOrLoginActivity.class);*/
        intent.setClass(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
