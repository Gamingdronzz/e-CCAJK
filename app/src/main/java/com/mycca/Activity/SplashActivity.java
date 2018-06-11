package com.mycca.Activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mycca.Listeners.OnConnectionAvailableListener;
import com.mycca.R;
import com.mycca.Tools.ConnectionUtility;
import com.mycca.Tools.FireBaseHelper;
import com.mycca.Tools.Helper;
import com.mycca.Tools.Preferences;

public class SplashActivity extends AppCompatActivity {

    ImageView imageView;
    TextView tvSplashVersion;
    DatabaseReference dbref;
    int currentAppVersion;
    String currentVersionName;
    private String TAG = "Splash";

    Animation animationScale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        bindVIews();
        init();
        StartAnimations();
    }

    private void init() {
        Helper.versionChecked=false;
        currentAppVersion = Helper.getInstance().getAppVersion(this);
        currentVersionName = getAppVersionName();
        if (currentVersionName.equals(""))
            tvSplashVersion.setText("Version - N/A");
        else
            tvSplashVersion.setText("Version - " + currentVersionName);
        Log.d(TAG, "onCreate: " + currentAppVersion + ": " + currentVersionName);
        dbref = FireBaseHelper.getInstance(this).databaseReference;
    }

    private void bindVIews() {
        imageView = findViewById(R.id.logo);
        tvSplashVersion = findViewById(R.id.tv_splash_version);
    }

    private void StartAnimations() {
        final Animation animationAlpha = AnimationUtils.loadAnimation(this, R.anim.alpha);
        animationScale = AnimationUtils.loadAnimation(this, R.anim.scale);
        final Animation animationBounce = AnimationUtils.loadAnimation(this, R.anim.bounce);

        animationAlpha.reset();
        animationScale.reset();
        animationBounce.reset();

        imageView.clearAnimation();
        imageView.startAnimation(animationScale);

        animationScale.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                checkForUpdate();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void checkForUpdate() {

        ConnectionUtility connectionUtility = new ConnectionUtility(new OnConnectionAvailableListener() {
            @Override
            public void OnConnectionAvailable() {

                Log.d(TAG, "version checked =" + Helper.versionChecked);
                FireBaseHelper.getInstance(SplashActivity.this).checkForUpdate(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (Helper.getInstance().onLatestVersion(dataSnapshot, SplashActivity.this))
                            LoadNextActivity();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        LoadNextActivity();
                    }
                });
            }

            @Override
            public void OnConnectionNotAvailable() {
                Log.d(TAG, "OnConnectionNotAvailable: ");
                LoadNextActivity();
            }
        });
        connectionUtility.checkConnectionAvailability();

    }

    private void LoadNextActivity() {
        Intent intent = new Intent();
        if (Preferences.getInstance().getBooleanPref(this, Preferences.PREF_HELP_ONBOARDER)) {
            intent.setClass(getApplicationContext(), IntroActivity.class);
        } else {
            intent.setClass(getApplicationContext(), MainActivity.class);
        }
        startActivity(intent);
        finish();
    }

    private String getAppVersionName() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

//    private void onLatestVersion(DataSnapshot dataSnapshot) {
//        if (dataSnapshot.getValue() == null) {
//            Log.d(TAG, "onLatestVersion: Data snapshot null");
//            ShowUpdateDialog(false);
//            return;
//        }
//        long version = (long) dataSnapshot.getValue();
//
//        Log.d(TAG, "onDataChange: current Version = " + currentAppVersion);
//        Log.d(TAG, "available Version = " + version);
//
//        if (currentAppVersion == -1 || currentAppVersion == version) {
//            LoadNextActivity();
//        } else {
//            ShowUpdateDialog(true);
//        }
//    }

//    private int getAppVersion() {
//        try {
//            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
//            return packageInfo.versionCode;
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//        return -1;
//    }


}