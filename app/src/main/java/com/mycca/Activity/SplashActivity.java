package com.mycca.Activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycca.CustomObjects.FancyAlertDialog.FancyAlertDialogType;
import com.mycca.CustomObjects.FancyAlertDialog.IFancyAlertDialogListener;
import com.mycca.Listeners.OnConnectionAvailableListener;
import com.mycca.Models.AppVersionModel;
import com.mycca.R;
import com.mycca.Tools.ConnectionUtility;
import com.mycca.Tools.FireBaseHelper;
import com.mycca.Tools.Helper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {

    ImageView imageView;
    AppCompatImageButton imageButton;
    TextView tvSplashVersion;
    boolean showWelcomeScreen = false;
    DatabaseReference dbref;
    int currentAppVersion;
    String currentVersionName;
    private String TAG = "Splash";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Helper.getInstance().setDebugMode(true);
        currentAppVersion = getAppVersion();
        currentVersionName = getAppVersionName();
        bindVIews();
        StartAnimations();
    }

    private void bindVIews() {
        imageView = findViewById(R.id.logo);
        dbref = FireBaseHelper.getInstance().databaseReference;
        tvSplashVersion = findViewById(R.id.tv_splash_version);
        tvSplashVersion.setText("Version - " + currentVersionName);
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
                checkForUpdate();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void checkForUpdate() {
        if (!Helper.getInstance().isDebugMode()) {
                ConnectionUtility connectionUtility = new ConnectionUtility(new OnConnectionAvailableListener() {
                    @Override
                    public void OnConnectionAvailable() {
                        try {
                            dbref.child(FireBaseHelper.getInstance().ROOT_APP_VERSION)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            checkVersion(dataSnapshot);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                                        }
                                    });
                        } catch (DatabaseException dbe) {
                            dbe.printStackTrace();
                        }
                    }

                    @Override
                    public void OnConnectionNotAvailable() {
                        Log.d(TAG, "OnConnectionNotAvailable: ");
                        LoadNextActivity();
                    }
                });
                connectionUtility.checkConnectionAvailability();
            } else
                LoadNextActivity();
    }

    private void checkVersion(DataSnapshot dataSnapshot) {
        AppVersionModel model = dataSnapshot.getValue(AppVersionModel.class);

        Log.d(TAG, "onDataChange: current Version = " + currentAppVersion);
        Log.d(TAG, "available Version = " + model.getCurrentReleaseVersion());
        if (currentAppVersion == -1) {
            LoadNextActivity();
        } else if (currentAppVersion == model.getCurrentReleaseVersion()) {
            LoadNextActivity();
        } else {
            Helper.getInstance().showFancyAlertDialog(this,
                    "A new version of the application is available on Google Play Store\n\nUpdate to continue using the application",
                    "Location",
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
        }
    }

    private void showGooglePlayStore()
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(Helper.getInstance().getPlayStoreURL()));
        startActivity(intent);

    }

    private void LoadNextActivity() {
        Intent intent = new Intent();
        if (showWelcomeScreen) {
            //intent.setClass(getApplicationContext(), WelcomeScreen.class);
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