package com.ccajk.Tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by hp on 13-02-2018.
 */

public class Preferences {
    private static Preferences _instance;

    static final String PREF_STAFF_ID = "staffId";
    static final String PREF_STAFF_TYPE = "staffType";
    static final String PREF_STATE = "state";

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

    public void setStaffId(Context context, String staffId) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_STAFF_ID, staffId);
        editor.commit();
    }

    public String getStaffId(Context context) {
        return getSharedPreferences(context).getString(PREF_STAFF_ID, null);
    }

    public void setStaffType(Context context, int type) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(PREF_STAFF_TYPE, type);
        editor.commit();
    }

    public int getStaffType(Context context) {
        return getSharedPreferences(context).getInt(PREF_STAFF_TYPE, -1);
    }

    public void setPrefState(Context context, String state) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_STATE, state);
        editor.commit();
    }

    public String getPrefState(Context context) {
        return getSharedPreferences(context).getString(PREF_STATE, "jnk");
    }

    public void clearPrefs(Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.remove(PREF_STAFF_ID);
        editor.remove(PREF_STAFF_TYPE);
        editor.commit();
    }

}
