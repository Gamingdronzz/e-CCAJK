package com.ccajk.Activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.ccajk.Listeners.OnConnectionAvailableListener;
import com.ccajk.Models.AppVersionModel;
import com.ccajk.Models.GrievanceModel;
import com.ccajk.R;
import com.ccajk.Tools.ConnectionUtility;
import com.ccajk.Tools.FireBaseHelper;
import com.ccajk.Tools.Helper;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {

    ImageView imageView;
    AppCompatImageButton imageButton;
    TextView gd;
    boolean showWelcomeScreen = false;
    DatabaseReference dbref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        bindVIews();
        Helper.getInstance().setDebugMode(false);
        checkForUpdate();
        //ShowNextActivity();
        StartAnimations();
    }

    private void bindVIews() {
        imageView = findViewById(R.id.logo);
        dbref = FireBaseHelper.getInstance().databaseReference;
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

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
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

    private void checkForUpdate() {
        ConnectionUtility connectionUtility = new ConnectionUtility(new OnConnectionAvailableListener() {
            @Override
            public void OnConnectionAvailable() {
                dbref.child(FireBaseHelper.getInstance().ROOT_APP_VERSION)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                AppVersionModel model = dataSnapshot.getValue(AppVersionModel.class);
                                int currentAppVersion = getAppVersion();
                                Log.d("Version", "onDataChange: current = " + currentAppVersion);
                                Log.d("Version","available = " + model.getCurrentReleaseVersion());
                                if (currentAppVersion == -1) {
                                    LoadNextActivity();
                                } else if (currentAppVersion == model.getCurrentReleaseVersion()) {
                                    LoadNextActivity();
                                } else {
                                    Helper.getInstance().showAlertDialog(getApplicationContext(),
                                            "A new version of the application is available on the play Store\n\nUpdate to continue using the application",
                                            "Update",
                                            "OK");
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }

            @Override
            public void OnConnectionNotAvailable() {
                LoadNextActivity();
            }
        });
        connectionUtility.checkConnectionAvailability();

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
}
