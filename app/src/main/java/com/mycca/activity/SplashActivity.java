package com.mycca.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.mycca.BuildConfig;
import com.mycca.R;
import com.mycca.listeners.DownloadCompleteListener;
import com.mycca.listeners.OnConnectionAvailableListener;
import com.mycca.providers.CircleDataProvider;
import com.mycca.tools.ConnectionUtility;
import com.mycca.tools.CustomLogger;
import com.mycca.tools.FireBaseHelper;
import com.mycca.tools.Helper;
import com.mycca.tools.Preferences;

import java.util.Locale;

public class SplashActivity extends AppCompatActivity {

    ImageView imageView;
    TextView tvSplashVersion;
    private Trace mTrace;
    int currentAppVersion;
    String currentVersionName;
    private String TAG = "Splash";
    private String VERSION_TRACE = "VERSION_trace";
    private String CIRCLE_TRACE = "CIRCLE_trace";
    private String ACTIVE_CIRCLE_TRACE = "ACTIVE_trace";
    private String OTHER_TRACE = "OTHER_trace";
    Animation animationScale;

    private String LATEST_VERSION = "latest_version";
    private String WELCOME_MESSAGE = "welcome_message";

    private enum VersionCheckState {
        IDLE,
        STARTED,
        COMPLETE
    }

    private VersionCheckState versionCheckState = VersionCheckState.IDLE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale();
        setContentView(R.layout.activity_splash);
        bindViews();
        init();
        StartAnimations();
    }

    public void setLocale() {

        String lang = Preferences.getInstance().getStringPref(this, Preferences.PREF_LANGUAGE);
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Resources resources = getApplicationContext().getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

    private void bindViews() {
        imageView = findViewById(R.id.logo);
        tvSplashVersion = findViewById(R.id.tv_splash_version);
    }

    private void init() {
        Helper.versionChecked = false;
        FirebaseRemoteConfig.getInstance();

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

        CustomLogger.getInstance().logDebug(TAG + " " + currentAppVersion + ": " + currentVersionName, CustomLogger.Mask.SPLASH_ACTIVITY);
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
                        CustomLogger.getInstance().logDebug(TAG + " Connection Available", CustomLogger.Mask.SPLASH_ACTIVITY);
                        checkForNewVersion();
                    }

                    @Override
                    public void OnConnectionNotAvailable() {
                        CustomLogger.getInstance().logDebug(TAG + " Connection Not Available", CustomLogger.Mask.SPLASH_ACTIVITY);
                        if (Preferences.getInstance().getIntPref(SplashActivity.this, Preferences.PREF_CIRCLES) != -1)
                            CircleDataProvider.getInstance().setCircleData(false, getApplicationContext(), null);
                        else
                            CustomLogger.getInstance().logDebug("Circle data not available", CustomLogger.Mask.SPLASH_ACTIVITY);
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
        mTrace.stop();
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
        mTrace = FirebasePerformance.getInstance().newTrace(VERSION_TRACE);
        mTrace.start();

        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        long cacheExpiration = 3600; // 1 hour in seconds.
        // If your app is using developer mode, cacheExpiration is set to 0, so each fetch will
        // retrieve values from the service.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SplashActivity.this, "Fetch Succeeded Latest Version = " + mFirebaseRemoteConfig.getString(LATEST_VERSION + "\nWelcome Message = " + WELCOME_MESSAGE),
                                    Toast.LENGTH_SHORT).show();
                            mTrace.stop();
                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
                            Toast.makeText(SplashActivity.this, "Fetch Failed",
                                    Toast.LENGTH_SHORT).show();
                            mTrace.stop();
                        }
                    }
                });


//        versionCheckState = VersionCheckState.STARTED;
//        CustomLogger.getInstance().logDebug(TAG + " Checking version", CustomLogger.Mask.SPLASH_ACTIVITY);
//        ValueEventListener valueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (Helper.getInstance().onLatestVersion(dataSnapshot, SplashActivity.this))
//                    checkCircles();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                checkOtherStateData();
//            }
//        };
//        FireBaseHelper.getInstance().getDataFromFireBase(null, valueEventListener, true, FireBaseHelper.ROOT_APP_VERSION);
    }

    public void checkCircles() {
        mTrace.stop();
        mTrace = FirebasePerformance.getInstance().newTrace(CIRCLE_TRACE);
        mTrace.start();

        CustomLogger.getInstance().logDebug(TAG + " Checking Circles", CustomLogger.Mask.SPLASH_ACTIVITY);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    long circleCount = (long) dataSnapshot.getValue();
                    if (circleCount == Preferences.getInstance().getIntPref(SplashActivity.this, Preferences.PREF_CIRCLES)) {
                        CustomLogger.getInstance().logDebug("No new data", CustomLogger.Mask.SPLASH_ACTIVITY);
                        checkActiveCircles();
                    } else {
                        CustomLogger.getInstance().logDebug("New data available", CustomLogger.Mask.SPLASH_ACTIVITY);
                        CircleDataProvider.getInstance().setCircleData(true, getApplicationContext(), null);
                        checkOtherStateData();
                    }
                } else {
                    CustomLogger.getInstance().logDebug("Data null...checking other state data", CustomLogger.Mask.SPLASH_ACTIVITY);
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
        mTrace.stop();
        mTrace = FirebasePerformance.getInstance().newTrace(ACTIVE_CIRCLE_TRACE);
        mTrace.start();

        CustomLogger.getInstance().logDebug(TAG + " Checking Active Circles", CustomLogger.Mask.SPLASH_ACTIVITY);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    long activeCount = (long) dataSnapshot.getValue();
                    if (activeCount == Preferences.getInstance().getIntPref(SplashActivity.this, Preferences.PREF_ACTIVE_CIRCLES)) {
                        CustomLogger.getInstance().logDebug("No new data", CustomLogger.Mask.SPLASH_ACTIVITY);
                        CircleDataProvider.getInstance().setCircleData(false, getApplicationContext(), null);
                    } else {
                        CustomLogger.getInstance().logDebug("New data available", CustomLogger.Mask.SPLASH_ACTIVITY);
                        CircleDataProvider.getInstance().setCircleData(true, getApplicationContext(), null);
                    }
                } else {
                    CustomLogger.getInstance().logDebug("Data null...checking other state data", CustomLogger.Mask.SPLASH_ACTIVITY);
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
        mTrace.stop();
        mTrace = FirebasePerformance.getInstance().newTrace(OTHER_TRACE);
        mTrace.start();

        if (Preferences.getInstance().getStringPref(this, Preferences.PREF_OFFICE_ADDRESS) == null ||
                Preferences.getInstance().getStringPref(this, Preferences.PREF_WEBSITE) == null ||
                Preferences.getInstance().getStringPref(this, Preferences.PREF_OFFICE_LABEL) == null) {

            CustomLogger.getInstance().logDebug("Other state Preferences null", CustomLogger.Mask.SPLASH_ACTIVITY);
            getOtherData();
        } else {
            CustomLogger.getInstance().logDebug("Other state Preferences not null", CustomLogger.Mask.SPLASH_ACTIVITY);
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