package com.mycca.firebaseService;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.mycca.tools.FireBaseHelper;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("Messaging", "Refreshed token: " + refreshedToken);

        FireBaseHelper.getInstance(this).setToken();
    }

}
