package com.mycca.Activity;

import android.content.Intent;
import android.os.Bundle;

import com.mycca.CustomObjects.Onboarder.OnboarderActivity;
import com.mycca.CustomObjects.Onboarder.OnboarderPage;
import com.mycca.R;
import com.mycca.Tools.Preferences;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends OnboarderActivity {

    boolean fromSettings = false;
    List<OnboarderPage> onBoarderPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fromSettings = getIntent().getBooleanExtra("FromSettings", false);
        onBoarderPages = new ArrayList<>();

        try {
            OnboarderPage onboarderPage1 = new OnboarderPage("Welcome", "Welcome to Official Android Application of\n" +
                    "Controller of Communication Accounts", R.drawable.cca2);
            setOnboarderPageProperties(onboarderPage1);

            OnboarderPage onboarderPage2 = new OnboarderPage("Functions",
                    getResources().getString(R.string.tutorial_text1),
                    R.drawable.drawable_functions);
            setOnboarderPageProperties(onboarderPage2);

            OnboarderPage onboarderPage3 = new OnboarderPage("Tracking",
                    getResources().getString(R.string.tutorial_text2),
                    R.drawable.drawable_track);
            setOnboarderPageProperties(onboarderPage3);

            OnboarderPage onboarderPage4 = new OnboarderPage("Add Other Information",
                    getResources().getString(R.string.tutorial_text3),
                    R.drawable.drawable_update_info);
            setOnboarderPageProperties(onboarderPage4);

            OnboarderPage onboarderPage5 = new OnboarderPage("Google Sign In",
                    getResources().getString(R.string.tutorial_text4),
                    R.drawable.index);
            setOnboarderPageProperties(onboarderPage5);

            onBoarderPages.add(onboarderPage1);
            onBoarderPages.add(onboarderPage2);
            onBoarderPages.add(onboarderPage3);
            onBoarderPages.add(onboarderPage4);
            onBoarderPages.add(onboarderPage5);

            setOnboardPagesReady(onBoarderPages);

        } catch (Exception e) {
            e.printStackTrace();
            nextActions();
        }
    }

    public void setOnboarderPageProperties(OnboarderPage onboarderPage) {
        onboarderPage.setTitleColor(R.color.colorPrimary);
        onboarderPage.setDescriptionColor(R.color.colorOffWhite);
        onboarderPage.setBackgroundColor(R.color.colorLightBlack);
        onboarderPage.setDescriptionTextSize(20);
        onboarderPage.setTitleTextSize(25);
        onboarderPage.setMultilineDescriptionCentered(true);
    }

    public void nextActions() {
        startActivity(new Intent(IntroActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }

    @Override
    public void onSkipButtonPressed() {
        Preferences.getInstance().setTutorialPrefs(this);
        nextActions();
    }

    @Override
    public void onFinishButtonPressed() {
        //Preferences.getInstance().setBooleanPref(this, Preferences.PREF_HELP_ONBOARDER, false);
        Preferences.getInstance().setTutorialPrefs(this);
        nextActions();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (fromSettings)
            Preferences.getInstance().setTutorialPrefs(this);
    }
}
