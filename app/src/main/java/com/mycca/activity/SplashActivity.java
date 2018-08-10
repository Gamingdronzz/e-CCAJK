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
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.mycca.R;
import com.mycca.listeners.OnConnectionAvailableListener;
import com.mycca.models.State;
import com.mycca.providers.CircleDataProvider;
import com.mycca.tools.ConnectionUtility;
import com.mycca.tools.CustomLogger;
import com.mycca.tools.Helper;
import com.mycca.tools.IOHelper;
import com.mycca.tools.NewFireBaseHelper;
import com.mycca.tools.Preferences;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    ImageView imageView;
    TextView tvSplashVersion;
    int currentAppVersion;
    String currentVersionName;
    private String TAG = "Splash";
    long circleCount;
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

        CustomLogger.getInstance().logDebug("onCreate: " + currentAppVersion + ": " + currentVersionName);
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
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                ConnectionUtility connectionUtility = new ConnectionUtility(new OnConnectionAvailableListener() {
                    @Override
                    public void OnConnectionAvailable() {
                        checkForNewVersion();
                    }

                    @Override
                    public void OnConnectionNotAvailable() {
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
        NewFireBaseHelper.getInstance().getDataFromFireBase(null, valueEventListener, true, NewFireBaseHelper.ROOT_APP_VERSION);
    }

    public void checkCircles() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    circleCount = (long) dataSnapshot.getValue();
                    if (circleCount == Preferences.getInstance().getIntPref(SplashActivity.this, Preferences.PREF_CIRCLES))
                        checkActiveCircles();
                    else
                        getCircleData();
                } else
                    checkOtherStateData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                checkOtherStateData();
            }
        };
        NewFireBaseHelper.getInstance().getDataFromFireBase(null, valueEventListener, true, NewFireBaseHelper.ROOT_CIRCLE_COUNT);
    }

    private void checkActiveCircles() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Helper.dataChecked = true;
                    long activeCount = (long) dataSnapshot.getValue();
                    if (activeCount == Preferences.getInstance().getIntPref(SplashActivity.this, Preferences.PREF_ACTIVE_CIRCLES)) {
                        checkOtherStateData();
                    } else
                        getCircleData();
                } else checkOtherStateData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                checkOtherStateData();
            }
        };
        NewFireBaseHelper.getInstance().getDataFromFireBase(null, valueEventListener, true, NewFireBaseHelper.ROOT_ACTIVE_COUNT);

    }

    private void checkOtherStateData() {
        if (Preferences.getInstance().getStringPref(this, Preferences.PREF_OFFICE_ADDRESS) == null ||
                Preferences.getInstance().getStringPref(this, Preferences.PREF_WEBSITE) == null ||
                Preferences.getInstance().getStringPref(this, Preferences.PREF_OFFICE_LABEL) == null) {

            getOtherData();
        } else {
                LoadNextActivity();
        }
    }

    private void getCircleData() {
        ArrayList<State> stateArrayList = new ArrayList<>();

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    try {
                        State state = ds.getValue(State.class);
                        stateArrayList.add(state);
                    } catch (DatabaseException | NullPointerException e) {
                        CustomLogger.getInstance().logDebug(e.getMessage());
                    }
                }
                Gson gson = new Gson();
                IOHelper.getInstance().writeToFile(gson.toJson(stateArrayList), "Circle Data", true, SplashActivity.this);
                CircleDataProvider.getInstance().setStates(stateArrayList);
                Helper.dataChecked = true;
                checkOtherStateData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                checkOtherStateData();
            }
        };

        NewFireBaseHelper.getInstance().getDataFromFireBase(null, valueEventListener, true, NewFireBaseHelper.ROOT_CIRCLE_DATA);
    }

    private void getOtherData() {
        State state = Preferences.getInstance().getStatePref(this, Preferences.PREF_STATE_DATA);

        ValueEventListener valueEventListener3 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    Preferences.getInstance().setStringPref(SplashActivity.this, Preferences.PREF_OFFICE_LABEL,
                            (String) dataSnapshot.child("label").getValue());
                    Preferences.getInstance().setStringPref(SplashActivity.this, Preferences.PREF_OFFICE_LAT,
                            (String) dataSnapshot.child("latitude").getValue());
                    Preferences.getInstance().setStringPref(SplashActivity.this, Preferences.PREF_OFFICE_LONG,
                            (String) dataSnapshot.child("longitude").getValue());
                } catch (DatabaseException | NullPointerException e) {
                    e.printStackTrace();
                }
                    LoadNextActivity();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                    LoadNextActivity();
            }
        };

        ValueEventListener valueEventListener2 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    Preferences.getInstance().setStringPref(SplashActivity.this, Preferences.PREF_WEBSITE, (String) dataSnapshot.getValue());
                    NewFireBaseHelper.getInstance().getDataFromFireBase(state.getDatabase(), valueEventListener3, true, NewFireBaseHelper.ROOT_OFFICE_COORDINATES);
                } catch (DatabaseException | NullPointerException e) {
                    Preferences.getInstance().setStringPref(SplashActivity.this, Preferences.PREF_WEBSITE, getString(R.string.n_a));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Preferences.getInstance().setStringPref(SplashActivity.this, Preferences.PREF_WEBSITE, getString(R.string.n_a));
                LoadNextActivity();
            }
        };

        ValueEventListener valueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    Preferences.getInstance().setStringPref(SplashActivity.this, Preferences.PREF_OFFICE_ADDRESS, (String) dataSnapshot.getValue());
                    NewFireBaseHelper.getInstance().getDataFromFireBase(state.getDatabase(), valueEventListener2, true, NewFireBaseHelper.ROOT_WEBSITE);
                } catch (DatabaseException | NullPointerException e) {
                    Preferences.getInstance().setStringPref(SplashActivity.this, Preferences.PREF_OFFICE_ADDRESS, getString(R.string.n_a));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Preferences.getInstance().setStringPref(SplashActivity.this, Preferences.PREF_OFFICE_ADDRESS, getString(R.string.n_a));
                LoadNextActivity();
            }
        };

        NewFireBaseHelper.getInstance().getDataFromFireBase(state.getDatabase(), valueEventListener1, true, NewFireBaseHelper.ROOT_OFFICE_ADDRESS);
    }

}