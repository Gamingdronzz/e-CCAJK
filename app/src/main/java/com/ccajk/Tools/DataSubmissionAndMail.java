package com.ccajk.Tools;

import android.net.Uri;
import android.util.Log;

import com.ccajk.Models.GrievanceModel;
import com.ccajk.Models.InspectionModel;
import com.ccajk.Models.PanAdhaar;
import com.ccajk.Models.SelectedImageModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hp on 01-05-2018.
 */

public class DataSubmissionAndMail {

    private static DataSubmissionAndMail _instance;

    public DataSubmissionAndMail() {
        _instance = this;
    }

    public static DataSubmissionAndMail getInstance() {
        if (_instance == null) {
            return new DataSubmissionAndMail();
        } else {
            return _instance;
        }
    }


    public void uploadImagesToServer(ArrayList<Uri> firebaseImageURLs, String folderName, VolleyHelper volleyHelper) throws Exception {
        Log.d("Data Submission", "uploadImagesToServer: Starting Upload");
        String url = Helper.getInstance().getAPIUrl() + "uploadImage.php";
        for (int i = 0; i < firebaseImageURLs.size(); i++) {

            Log.d("Data Submission", "uploadAllImagesToServer: Current = " + i);

            Map<String, String> params = new HashMap();
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
    }
}
