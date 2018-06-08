package com.mycca.Activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mycca.CustomObjects.FancyAlertDialog.FancyAlertDialogType;
import com.mycca.CustomObjects.FancyAlertDialog.IFancyAlertDialogListener;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        bindVIews();
        init();
        StartAnimations();
    }

    private void init() {
        currentAppVersion = getAppVersion();
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
        final Animation animationScale = AnimationUtils.loadAnimation(this, R.anim.scale);
        final Animation animationBounce = AnimationUtils.loadAnimation(this, R.anim.bounce);

        animationAlpha.reset();
        animationScale.reset();
        animationBounce.reset();

        imageView.clearAnimation();
        imageView.startAnimation(animationScale);

        Thread thread = new Thread() {
            public void run() {
                super.run();
                try {
                    checkForUpdate();
                } catch (Exception e1) {
                    e1.printStackTrace();
                    LoadNextActivity();
                } finally {
                }
            }
        };
        thread.start();

        animationScale.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //checkForUpdate();
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
                try {
                    dbref.child(FireBaseHelper.ROOT_APP_VERSION)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    checkVersion(dataSnapshot);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    LoadNextActivity();
                                }
                            });
                } catch (DatabaseException dbe) {
                    dbe.printStackTrace();
                    LoadNextActivity();
                }
            }

            @Override
            public void OnConnectionNotAvailable() {
                Log.d(TAG, "OnConnectionNotAvailable: ");
                LoadNextActivity();
            }
        });
        connectionUtility.checkConnectionAvailability();

    }

    private void checkVersion(DataSnapshot dataSnapshot) {
        if (dataSnapshot.getValue() == null) {
            Log.d(TAG, "checkVersion: Data snapshot null");
            ShowUpdateDialog(false);
            return;
        }
        long version = (long) dataSnapshot.getValue();

        Log.d(TAG, "onDataChange: current Version = " + currentAppVersion);
        Log.d(TAG, "available Version = " + version);

        if (currentAppVersion == -1 || currentAppVersion == version) {
            LoadNextActivity();
        } else {
            ShowUpdateDialog(true);
        }
    }

    private void ShowUpdateDialog(boolean updateAvailable) {
        if (updateAvailable) {
            Helper.getInstance().showFancyAlertDialog(this,
                    "A new version of the application is available on Google Play Store\n\nUpdate to continue using the application",
                    "My CCA",
                    "Update",
                    new IFancyAlertDialogListener() {
                        @Override
                        public void OnClick() {
                            showGooglePlayStore();
                            finish();
                        }
                    },
                    "Cancel",
                    new IFancyAlertDialogListener() {
                        @Override
                        public void OnClick() {
                            finish();
                        }
                    },
                    FancyAlertDialogType.WARNING);
        } else {
            Helper.getInstance().showFancyAlertDialog(this,
                    "The Application is in maintenance\nPlease wait for a while\n\nThank you for your paitence",
                    "My CCA",
                    "OK",
                    new IFancyAlertDialogListener() {
                        @Override
                        public void OnClick() {
                            finish();
                        }
                    },
                    null, null,
                    FancyAlertDialogType.WARNING);
        }
    }

    private void showGooglePlayStore() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(Helper.getInstance().getPlayStoreURL()));
        startActivity(intent);

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

    private int getAppVersion() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
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

}