package com.mycca.tools;

import android.util.Log;

public class CustomLogger {

    private static CustomLogger _instance;
    private final String TAG = "Custom Logger";
    private static boolean logEnabled = true;

    private CustomLogger() {
        _instance = this;
    }

    public static CustomLogger getInstance() {
        if (_instance == null) {
            return new CustomLogger();
        } else {
            return _instance;
        }
    }

    public void logVerbose(String message) {
        if (logEnabled)
            Log.v(TAG, "Verbose: " + message);
    }

    public void logVerbose(String tag, String message) {
        if (logEnabled)
            Log.v(tag, "Verbose: " + message);
    }

    public void logDebug(String message) {
        if (logEnabled)
            Log.d(TAG, "Debug: " + message);
    }

    public void logDebug(String tag, String message) {
        if (logEnabled)
            Log.d(tag, "Debug: " + message);
    }

    public void logWarn(String message) {
        if (logEnabled)
            Log.w(TAG, "warning: " + message);
    }

    public void logWarn(String tag, String message) {
        if (logEnabled)
            Log.w(tag, "warning: " + message);
    }
}
