package com.ccajk.Tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by hp on 13-02-2018.
 */

public class Prefrences {
    static final String PREF_SIGNED_IN="signedIn";
    static final String PREF_PPO="ppo";
    static final String PREF_USER_NAME="username";

    static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setSignedIn(Context context, boolean signedin) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(PREF_SIGNED_IN,signedin);
        editor.commit();
    }

    public static Boolean getSignedIn(Context context) {
        return getSharedPreferences(context).getBoolean(PREF_SIGNED_IN,false);
    }

    public static void setPpo(Context context, String ppo) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_PPO,ppo);
        editor.commit();
    }

    public static String getPpo(Context context) {
        return getSharedPreferences(context).getString(PREF_PPO,null);
    }

    public static void setUsername(Context context, String username) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_USER_NAME,username);
        editor.commit();
    }

    public static String getUsername(Context context) {
        return getSharedPreferences(context).getString(PREF_USER_NAME,null);
    }



}
