package com.ccajk.Models;

/**
 * Created by balpreet on 5/2/2018.
 */

public class AppVersionModel {

    private int CurrentReleaseVersion;

    public int getCurrentReleaseVersion() {
        return CurrentReleaseVersion;
    }

    public void setCurrentReleaseVersion(int currentReleaseVersion) {
        CurrentReleaseVersion = currentReleaseVersion;
    }

    public AppVersionModel(int currentReleaseVersion) {

        CurrentReleaseVersion = currentReleaseVersion;
    }
}
