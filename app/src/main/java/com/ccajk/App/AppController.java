package com.ccajk.App;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import shortbread.Shortbread;

/**
 * Created by balpreet on 4/14/2018.
 */

public class AppController extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        Shortbread.create(this);
    }
}
