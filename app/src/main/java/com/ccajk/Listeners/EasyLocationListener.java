package com.ccajk.Listeners;

/**
 * Created by balpreet on 4/20/2018.
 */


/**
 * Callback that can be implemented in order to listen for events
 */

public interface EasyLocationListener {
    void locationOn();

    void onPositionChanged();

    void locationCancelled();
}

