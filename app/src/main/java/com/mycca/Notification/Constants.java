package com.mycca.Notification;

/**
 * Created by prabhat on 14/2/18.
 */

public class Constants {
    public static final String TAG = "FcmNotificationBuilder";
    static final String SUCCESS_CODE = "success";
    static final String CONTENT_TYPE = "Content-Type";
    static final String APPLICATION_JSON = "application/json";
    static final String AUTHORIZATION = "Authorization";
    public static final String FCM_URL = "https://fcm.googleapis.com/fcm/send";
    // json related keys
    static final String KEY_TO = "to";
    public static final String KEY_TITLE = "title";
    public static final String KEY_TEXT = "text";
    static final String KEY_DATA = "data";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_UID = "uid";
    public static final String KEY_FCM_TOKEN = "fcm_token";
    public static final String KEY_NOTIFICATION = "notification";
}
