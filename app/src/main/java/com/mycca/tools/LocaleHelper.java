package com.mycca.tools;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;

import java.util.Locale;

public class LocaleHelper {

    private static LocaleHelper _instance;

    public LocaleHelper() {
        _instance = this;
    }

    public static LocaleHelper getInstance() {
        if (_instance == null) {
            return new LocaleHelper();
        } else {
            return _instance;
        }
    }

    public void setLocale(Context context) {
        String lang = Preferences.getInstance().getStringPref(context, Preferences.PREF_LANGUAGE);
        CustomLogger.getInstance().logVerbose("Lang Key = " + lang, CustomLogger.Mask.LOCALE_HELPER);
        CustomLogger.getInstance().logVerbose("Build Version = " + Build.VERSION.SDK_INT, CustomLogger.Mask.LOCALE_HELPER);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            updateResources(context, lang);
        } else

            updateResourcesLegacy(context, lang);

    }

    @TargetApi(Build.VERSION_CODES.N)
    private void updateResources(Context context, String lang) {
        Configuration configuration = context.getResources().getConfiguration();
        Locale locale = new Locale(lang);
        LocaleList localeList = new LocaleList(locale);
        localeList.setDefault(localeList);
        configuration.setLocales(localeList);
        context.createConfigurationContext(configuration);
        CustomLogger.getInstance().logVerbose("Current Locale = " + configuration.getLocales());
    }

    @SuppressWarnings("deprecation")
    private void updateResourcesLegacy(Context context, String lang) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }
}
