package com.ccajk.Tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by hp on 13-02-2018.
 */

public class Preferences {
    private static Preferences _instance;

    public static final String PREF_STAFF_ID = "staffId";
    public static final String PREF_STAFF_TYPE = "staffType";
    public static final String PREF_STATE = "state";

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


    public String getStringPref(Context context,String key)
    {
        String res = getSharedPreferences(context).getString(key,null);
        if(res == null)
        {
            if(key.equals(PREF_STATE))
            {
                return "jnk";
            }
        }
        return res;
    }

    public void setStringPref(Context context,String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(key, value);
        editor.commit();
    }


    public int getIntPref(Context context,String key)
    {
        return getSharedPreferences(context).getInt(key,-1);
    }

    public void setIntPref(Context context,String key, int value) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void clearPrefs(Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.remove(PREF_STAFF_ID);
        editor.remove(PREF_STAFF_TYPE);
        editor.commit();
    }

}
