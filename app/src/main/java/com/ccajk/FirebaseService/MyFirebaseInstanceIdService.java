package com.ccajk.FirebaseService;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by hp on 08-05-2018.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
       String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("Messaging", "Refreshed token: " + refreshedToken);

        /* If you want to send messages to this application instance or
         manage this apps subscriptions on the server side, send the
        Instance ID token to your app server.*/

        //sendRegistrationToServer(refreshedToken);
    }

}
