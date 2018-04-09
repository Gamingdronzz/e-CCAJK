package com.ccajk.Tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by hp on 13-02-2018.
 */

public class Preferences {
    private static Preferences _instance;

    static final String PREF_SIGNED_IN = "signedIn";
    static final String PREF_PPO = "ppo";
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

    public void setSignedIn(Context context, boolean signedin) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(PREF_SIGNED_IN, signedin);
        editor.commit();
    }

    public Boolean getSignedIn(Context context) {
        return getSharedPreferences(context).getBoolean(PREF_SIGNED_IN, false);
    }

    public void setPpo(Context context, String ppo) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_PPO, ppo);
        editor.commit();
    }

    public String getPpo(Context context) {
        return getSharedPreferences(context).getString(PREF_PPO, null);
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
        editor.remove(PREF_SIGNED_IN);
        editor.remove(PREF_PPO);
        editor.commit();
    }

}
