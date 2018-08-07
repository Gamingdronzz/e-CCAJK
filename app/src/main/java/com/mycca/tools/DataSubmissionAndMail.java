package com.mycca.tools;

import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataSubmissionAndMail {

    public static final String SUBMIT="submit";
    public static final String UPDATE="update";

    private static DataSubmissionAndMail _instance;
    private String TAG="Mail and Image";

    private DataSubmissionAndMail() {
        _instance = this;
    }

    public static DataSubmissionAndMail getInstance() {
        if (_instance == null) {
            return new DataSubmissionAndMail();
        } else {
            return _instance;
        }
    }

    public void uploadImagesToServer(String url,ArrayList<Uri> firebaseImageURLs, String folderName, String uploadType,VolleyHelper volleyHelper) {
        Log.d("Data Submission", "uploadImagesToServer: Starting Upload");
        for (int i = 0; i < firebaseImageURLs.size(); i++) {

            Log.d("Data Submission", "uploadAllImagesToServer: Current = " + i);

            Map<String, String> params = new HashMap<>();
            params.put("folder",uploadType);
            params.put("pensionerCode", folderName);
            params.put("image", firebaseImageURLs.get(i).toString());
            params.put("imageName", "image-" + i);
            params.put("imageCount", i + "");
            if (volleyHelper.countRequestsInFlight("upload_image-" + i) == 0)
                volleyHelper.makeStringRequest(url, "upload_image-" + i, params);

        }
    }

    public void sendMail(Map<String, String> hashMap, String tag, VolleyHelper volleyHelper,String url) {
        if (volleyHelper.countRequestsInFlight(tag) == 0)
            volleyHelper.makeStringRequest(url, tag, hashMap);
        Log.d(TAG, "sendFinalMail: ");
    }


}