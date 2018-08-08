package com.mycca.firebaseService;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.mycca.tools.CustomLogger;
import com.mycca.tools.FireBaseHelper;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        CustomLogger.getInstance().logDebug("Refreshed token: " + refreshedToken);

        FireBaseHelper.getInstance(this).setToken();
    }

}
