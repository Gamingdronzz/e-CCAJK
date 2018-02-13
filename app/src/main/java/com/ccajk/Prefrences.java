package com.ccajk;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by hp on 13-02-2018.
 */

public class Prefrences {
    static final String PREF_LEAVE_APP="leaveApp";

    static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setLeaveApp(Context context, boolean leave) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(PREF_LEAVE_APP,leave);
        editor.commit();
    }

    public static Boolean getLeaveApp(Context context) {
        return getSharedPreferences(context).getBoolean(PREF_LEAVE_APP,false);
    }
}
