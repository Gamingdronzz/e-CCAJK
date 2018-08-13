package com.mycca.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.mycca.models.StaffModel;
import com.mycca.models.State;


public class Preferences {

    private static Preferences _instance;

    public static final String PREF_STAFF_DATA = "staffData";
    public static final String PREF_STATE_DATA = "stateData";
    public static final String PREF_RECEIVE_NOTIFICATIONS = "receiveNotifications";
    public static final String PREF_LANGUAGE = "language";
    public static final String PREF_HELP_ONBOARDER = "onBoarder";
    public static final String PREF_CIRCLES = "circles";
    public static final String PREF_ACTIVE_CIRCLES = "activeCircles";
    public static final String PREF_WIFI = "wifi";
    public static final String PREF_GP = "gp";
    public static final String PREF_CONTACTS = "contacts";
    public static final String PREF_OFFICE_ADDRESS = "officeAddress";
    public static final String PREF_OFFICE_LABEL = "officeLabel";
    public static final String PREF_OFFICE_LAT = "officeLatitude";
    public static final String PREF_OFFICE_LONG = "officeLongitude";
    public static final String PREF_WEBSITE = "website";

    //    public static final String PREF_HELP_HOME = "home";
//    public static final String PREF_HELP_CONTACT = "contact";
//    public static final String PREF_HELP_INSPECTION = "inspection";
//    public static final String PREF_HELP_GRIEVANCE = "grievance";
//    public static final String PREF_HELP_LOCATOR = "locator";
//    public static final String PREF_HELP_UPDATE = "update";

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

    private static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }


    /* --------------------basic preferences---------------------- */

    public String getStringPref(Context context, String key) {
        String s = getSharedPreferences(context).getString(key, null);
        if (s == null) {
            if (key.equals(PREF_LANGUAGE))
                return "en";
        }
        return s;
    }

    public void setStringPref(Context context, String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public int getIntPref(Context context, String key) {
        return getSharedPreferences(context).getInt(key, -1);
    }

    public void setIntPref(Context context, String key, int value) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public boolean getBooleanPref(Context context, String key) {
        return getSharedPreferences(context).getBoolean(key, true);
    }

    public void setBooleanPref(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }


    /* --------------------custom preferences---------------------- */

    public void setModelPref(Context context, String key, Object value) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        Gson gson = new Gson();
        String json = gson.toJson(value);
        editor.putString(key, json);
        editor.apply();
    }

    public StaffModel getStaffPref(Context context) {
        Gson gson = new Gson();
        String json = getSharedPreferences(context).getString(PREF_STAFF_DATA, null);
        return gson.fromJson(json, StaffModel.class);
    }

    public State getStatePref(Context context) {
        Gson gson = new Gson();
        String json = getSharedPreferences(context).getString(PREF_STATE_DATA, null);
        if (json == null) {
            return new State("05", "Jammu & Kashmir", "Jammu & Kashmir", "cca-jammukashmir", "ccajk@nic.in", true);
        }
        return gson.fromJson(json, State.class);
    }

    public void clearStaffPrefs(Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.remove(PREF_STAFF_DATA);
        editor.apply();
    }

    public void clearOtherStateDataPrefs(Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.remove(PREF_OFFICE_ADDRESS);
        editor.remove(PREF_OFFICE_LABEL);
        editor.remove(PREF_OFFICE_LAT);
        editor.remove(PREF_OFFICE_LONG);
        editor.remove(PREF_WEBSITE);
        editor.apply();
    }

    public void clearTutorialPrefs(Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.remove(PREF_HELP_ONBOARDER);
        //        editor.remove(PREF_HELP_HOME);
//        editor.remove(PREF_HELP_INSPECTION);
//        editor.remove(PREF_HELP_UPDATE);
//        editor.remove(PREF_HELP_GRIEVANCE);
//        editor.remove(PREF_HELP_LOCATOR);
//        editor.remove(PREF_HELP_CONTACT);
        editor.apply();
    }

    public void setTutorialPrefs(Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(PREF_HELP_ONBOARDER, false);
        //        editor.putBoolean(PREF_HELP_HOME, false);
//        editor.putBoolean(PREF_HELP_INSPECTION, false);
//        editor.putBoolean(PREF_HELP_UPDATE, false);
//        editor.putBoolean(PREF_HELP_GRIEVANCE, false);
//        editor.putBoolean(PREF_HELP_LOCATOR, false);
//        editor.putBoolean(PREF_HELP_CONTACT, false);
        editor.apply();
    }
}
