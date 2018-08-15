package com.mycca.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mycca.R;
import com.mycca.listeners.DownloadCompleteListener;
import com.mycca.listeners.OnConnectionAvailableListener;
import com.mycca.providers.CircleDataProvider;
import com.mycca.tools.ConnectionUtility;
import com.mycca.tools.CustomLogger;
import com.mycca.tools.Helper;
import com.mycca.tools.FireBaseHelper;
import com.mycca.tools.Preferences;

public class SplashActivity extends AppCompatActivity {

    ImageView imageView;
    TextView tvSplashVersion;
    int currentAppVersion;
    String currentVersionName;
    private String TAG = "Splash";
    //boolean animDone = false;
    //boolean checksDone = false;
    Animation animationScale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Helper.getInstance().setLocale(this);
        setContentView(R.layout.activity_splash);
        bindViews();
        init();
        StartAnimations();
    }

    private void bindViews() {
        imageView = findViewById(R.id.logo);
        tvSplashVersion = findViewById(R.id.tv_splash_version);
    }

    private void init() {
        Helper.versionChecked = false;
        Helper.dataChecked = false;

        currentAppVersion = Helper.getInstance().getAppVersion(this);
        if (currentAppVersion == -1)
            currentAppVersion = 6;

        currentVersionName = getAppVersionName();
        String text;
        if (currentVersionName.equals(""))
            text = getString(R.string.n_a);
        else
            text = currentVersionName;
        tvSplashVersion.setText(String.format(getString(R.string.version), text));

        CustomLogger.getInstance().logDebug(TAG + " " + currentAppVersion + ": " + currentVersionName);
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
                ConnectionUtility connectionUtility = new ConnectionUtility(new OnConnectionAvailableListener() {
                    @Override
                    public void OnConnectionAvailable() {
                        CustomLogger.getInstance().logDebug(TAG + " Connection Available");
                        checkForNewVersion();
                    }

                    @Override
                    public void OnConnectionNotAvailable() {
                        CustomLogger.getInstance().logDebug(TAG + " Connection Not Available");
                        if (Preferences.getInstance().getIntPref(SplashActivity.this, Preferences.PREF_CIRCLES) != -1)
                            CircleDataProvider.getInstance().setCircleData(false, getApplicationContext(),null);
                        else
                            CustomLogger.getInstance().logDebug("Circle data not available");
                        LoadNextActivity();
                    }
                });
                connectionUtility.checkConnectionAvailability();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
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

    private void checkForNewVersion() {
        CustomLogger.getInstance().logDebug(TAG + " Checking version");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (Helper.getInstance().onLatestVersion(dataSnapshot, SplashActivity.this))
                    checkCircles();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                checkOtherStateData();
            }
        };
        FireBaseHelper.getInstance().getDataFromFireBase(null, valueEventListener, true, FireBaseHelper.ROOT_APP_VERSION);
    }

    public void checkCircles() {
        CustomLogger.getInstance().logDebug(TAG + " Checking Circles");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    long circleCount = (long) dataSnapshot.getValue();
                    if (circleCount == Preferences.getInstance().getIntPref(SplashActivity.this, Preferences.PREF_CIRCLES)) {
                        CustomLogger.getInstance().logDebug("No new data");
                        checkActiveCircles();
                    } else {
                        CustomLogger.getInstance().logDebug("New data available");
                        CircleDataProvider.getInstance().setCircleData(true, getApplicationContext(),null);
                        checkOtherStateData();
                    }
                } else {
                    CustomLogger.getInstance().logDebug("Data null...checking other state data");
                    checkOtherStateData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                checkOtherStateData();
            }
        };
        FireBaseHelper.getInstance().getDataFromFireBase(null, valueEventListener, true, FireBaseHelper.ROOT_CIRCLE_COUNT);
    }

    private void checkActiveCircles() {

        CustomLogger.getInstance().logDebug(TAG + " Checking Active Circles");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    long activeCount = (long) dataSnapshot.getValue();
                    if (activeCount == Preferences.getInstance().getIntPref(SplashActivity.this, Preferences.PREF_ACTIVE_CIRCLES)) {
                        CustomLogger.getInstance().logDebug("No new data");
                        Helper.dataChecked = true;
                        CircleDataProvider.getInstance().setCircleData(false, getApplicationContext(),null);
                    } else {
                        CustomLogger.getInstance().logDebug("New data available");
                        CircleDataProvider.getInstance().setCircleData(true, getApplicationContext(),null);
                    }
                } else {
                    CustomLogger.getInstance().logDebug("Data null...checking other state data");
                }
                checkOtherStateData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                checkOtherStateData();
            }
        };
        FireBaseHelper.getInstance().getDataFromFireBase(null, valueEventListener, true, FireBaseHelper.ROOT_ACTIVE_COUNT);

    }

    private void checkOtherStateData() {
        if (Preferences.getInstance().getStringPref(this, Preferences.PREF_OFFICE_ADDRESS) == null ||
                Preferences.getInstance().getStringPref(this, Preferences.PREF_WEBSITE) == null ||
                Preferences.getInstance().getStringPref(this, Preferences.PREF_OFFICE_LABEL) == null) {

            CustomLogger.getInstance().logDebug("Other state Preferences null");
            getOtherData();
        } else {
            CustomLogger.getInstance().logDebug("Other state Preferences not null");
            LoadNextActivity();
        }
    }

    private void getOtherData() {
        FireBaseHelper.getInstance().getOtherStateData(this, new DownloadCompleteListener() {
            @Override
            public void onDownloadSuccess() {
                LoadNextActivity();
            }

            @Override
            public void onDownloadFailure() {
                LoadNextActivity();
            }
        });
    }

}