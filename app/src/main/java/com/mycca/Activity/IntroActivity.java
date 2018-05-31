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

    List<OnboarderPage> onboarderPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onboarderPages = new ArrayList<>();

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

        onboarderPages.add(onboarderPage1);
        onboarderPages.add(onboarderPage2);
        onboarderPages.add(onboarderPage3);
        onboarderPages.add(onboarderPage4);
        onboarderPages.add(onboarderPage5);

        setOnboardPagesReady(onboarderPages);
    }

    public void setOnboarderPageProperties(OnboarderPage onboarderPage) {
        onboarderPage.setTitleColor(R.color.colorPrimary);
        onboarderPage.setDescriptionColor(R.color.colorOffWhite);
        onboarderPage.setBackgroundColor(R.color.colorLightBlack);
        onboarderPage.setDescriptionTextSize(20);
        onboarderPage.setTitleTextSize(25);
        onboarderPage.setMultilineDescriptionCentered(true);
    }

    @Override
    public void onSkipButtonPressed() {
        Preferences.getInstance().setBooleanPref(this,Preferences.PREF_HELP_ONBOARDER,false);
        startActivity(new Intent(IntroActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void onFinishButtonPressed() {
        Preferences.getInstance().setBooleanPref(this,Preferences.PREF_HELP_ONBOARDER,false);
        startActivity(new Intent(IntroActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
