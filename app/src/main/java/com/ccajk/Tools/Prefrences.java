package com.ccajk.Tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by hp on 13-02-2018.
 */

public class Prefrences {
    private static Prefrences _instance;
    static final String PREF_REGISTERED="Registered";
    static final String PREF_SIGNED_IN="signedIn";
    static final String PREF_PPO="ppo";
    static final String PREF_USER_NAME="username";

    public Prefrences() {
        _instance = this;
    }

    public static Prefrences getInstance() {
        if (_instance == null) {
            return new Prefrences();
        } else {
            return _instance;
        }
    }

    static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setRegisterd(Context context,boolean reg){
        SharedPreferences.Editor editor=getSharedPreferences(context).edit();
        editor.putBoolean(PREF_REGISTERED,reg);
        editor.commit();
    }

    public Boolean getRegistered(Context context){
        return getSharedPreferences(context).getBoolean(PREF_REGISTERED,false);
    }

    public  void setSignedIn(Context context, boolean signedin) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(PREF_SIGNED_IN,signedin);
        editor.commit();
    }

    public Boolean getSignedIn(Context context) {
        return getSharedPreferences(context).getBoolean(PREF_SIGNED_IN,false);
    }

    public  void setPpo(Context context, String ppo) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_PPO,ppo);
        editor.commit();
    }

    public  String getPpo(Context context) {
        return getSharedPreferences(context).getString(PREF_PPO,null);
    }

    public  void setUsername(Context context, String username) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_USER_NAME,username);
        editor.commit();
    }

    public String getUsername(Context context) {
        return getSharedPreferences(context).getString(PREF_USER_NAME,null);
    }



}
