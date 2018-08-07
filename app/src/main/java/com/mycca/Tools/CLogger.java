package com.mycca.Tools;

import android.util.Log;

public class CLogger {

    private static CLogger _instance;
    private final String TAG = "Custom Logger";
    private static boolean logEnabled = true;

    private CLogger() {
        _instance = this;
    }

    public static CLogger getInstance() {
        if (_instance == null) {
            return new CLogger();
        } else {
            return _instance;
        }
    }

    public void logVerbose(String message) {
        if (logEnabled)
            Log.v(TAG, "logDebug: " + message);
    }

    public void logVerbose(String tag, String message) {
        if (logEnabled)
            Log.v(tag, "logDebug: " + message);
    }

    public void logDebug(String message) {
        if (logEnabled)
            Log.d(TAG, "logDebug: " + message);
    }

    public void logDebug(String tag, String message) {
        if (logEnabled)
            Log.d(tag, "logDebug: " + message);
    }
}
