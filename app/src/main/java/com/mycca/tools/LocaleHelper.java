package com.mycca.tools;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;

public class LocaleHelper {

//    private static LocaleHelper _instance;
//
//    private LocaleHelper() {
//        _instance = this;
//    }
//
//    public static LocaleHelper getInstance() {
//        if (_instance == null) {
//            return new LocaleHelper();
//        } else {
//            return _instance;
//        }
//    }

    public static Context setLocale(Context context) {
        String lang = Preferences.getInstance().getStringPref(context, Preferences.PREF_LANGUAGE);
        CustomLogger.getInstance().logVerbose("Lang Key = " + lang, CustomLogger.Mask.LOCALE_HELPER);
        CustomLogger.getInstance().logVerbose("Build Version = " + Build.VERSION.SDK_INT, CustomLogger.Mask.LOCALE_HELPER);
        return updateResources(context, lang);
    }

    private static Context updateResources(Context context, String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        if (Build.VERSION.SDK_INT >= 25) {
            config.setLocale(locale);
            context = context.createConfigurationContext(config);
        } else {
            config.setLocale(locale);
            res.updateConfiguration(config, res.getDisplayMetrics());
        }
        return context;
    }
}
