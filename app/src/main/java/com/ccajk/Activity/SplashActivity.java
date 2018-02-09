package com.ccajk.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.ccajk.Activity.HomeActivity;
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
        gd = findViewById(R.id.designedBy);

    }

    private void StartAnimations() {
        final Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        final Animation animationBounce = AnimationUtils.loadAnimation(this, R.anim.bounce);


        anim.reset();
        animationBounce.reset();


        imageView.clearAnimation();
        imageView.startAnimation(anim);

        gd.clearAnimation();
        gd.startAnimation(animationBounce);

        anim.setAnimationListener(new Animation.AnimationListener() {
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
        intent.setClass(getApplicationContext(), HomeActivity.class);

        startActivity(intent);
        finish();

    }
}
