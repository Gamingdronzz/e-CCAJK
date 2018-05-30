package com.mycca.Tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.mycca.Models.StaffModel;

/**
 * Created by hp on 13-02-2018.
 */

public class Preferences {
    private static Preferences _instance;

    public static final String PREF_STAFF_DATA = "staffData";
    public static final String PREF_STATE = "state";
    public static final String PREF_RECIEVE_NOTIFICATIONS = "recieveNotifications";
    public static final String PREF_SHOW_ONBOARDER = "onboarder";
    public static final String PREF_SHOWCASE_HOME= "home";


    public Preferences() {
        _instance = this;
    }

    public static Preferences getInstance() {
        if (_instance == null) {
            return new Preferences();
        } else {
            return _instance;
        }
    }

    static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }


    public String getStringPref(Context context, String key) {
        String res = getSharedPreferences(context).getString(key, null);
        if (res == null) {
            if (key.equals(PREF_STATE)) {
                return "05";
            }
        }
        return res;
    }

    public void setStringPref(Context context, String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(key, value);
        editor.commit();
    }


    public int getIntPref(Context context, String key) {
        return getSharedPreferences(context).getInt(key, -1);
    }

    public void setIntPref(Context context, String key, int value) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public boolean getBooleanPref(Context context, String key) {
        return getSharedPreferences(context).getBoolean(key, true);
    }

    public void setBooleanPref(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public StaffModel getStaffPref(Context context, String key) {
        Gson gson = new Gson();
        String json = getSharedPreferences(context).getString(key, null);
        StaffModel staffModel = gson.fromJson(json, StaffModel.class);
        return staffModel;
    }

    public void setStaffPref(Context context, String key, StaffModel value) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        Gson gson = new Gson();
        String json = gson.toJson(value);
        editor.putString(key, json);
        editor.commit();
    }

    public void clearStaffPrefs(Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.remove(PREF_STAFF_DATA);
        editor.commit();
    }

}
