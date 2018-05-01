package com.ccajk.Tools;

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


    public Task uploadDataToFirebase(String root, Object model, String... params) {
        DatabaseReference dbref = FireBaseHelper.getInstance().databaseReference;
        Task task = null;

        if (root.equals(FireBaseHelper.getInstance().ROOT_GRIEVANCES)) {
            GrievanceModel grievanceModel = (GrievanceModel) model;
            task = dbref.child(root)
                    .child(grievanceModel.getPensionerIdentifier())
                    .child(String.valueOf(grievanceModel.getGrievanceType()))
                    .setValue(grievanceModel);

        } else if (root.equals(FireBaseHelper.getInstance().ROOT_INSPECTION)) {
            InspectionModel inspectionModel = (InspectionModel) model;
            task = dbref.child(root)
                    .child(params[0])
                    .child(params[1])
                    .setValue(inspectionModel);

        } else {
            PanAdhaar panAdhaar = (PanAdhaar) model;
            task = dbref.child(root)
                    .child(panAdhaar.getPensionerIdentifier())
                    .setValue(panAdhaar);
        }
        return task;
    }


    public UploadTask uploadFilesToFirebase(SelectedImageModel imageFile, boolean multiple, int count, String... params) {
        StorageReference sref;
        StringBuilder sb = new StringBuilder();
        for (String param : params)
            sb.append(param + "/");
        if (multiple) {
            sref = FireBaseHelper.getInstance().storageReference.child(sb + "File" + count);
        } else {
            sref = FireBaseHelper.getInstance().storageReference.child(sb.toString());
        }
        UploadTask uploadTask = sref.putFile(imageFile.getImageURI());
        return uploadTask;
    }


    public void uploadImagesToServer(ArrayList<URL> firebaseImageURLs, String folderName, VolleyHelper volleyHelper) {

        String url = Helper.getInstance().getAPIUrl() + "uploadImage.php";
        for (int i = 0; i < firebaseImageURLs.size(); i++) {
            try {
                Log.d("Data Submission", "uploadAllImagesToServer: Current = " + i);

                Map<String, String> params = new HashMap();
                params.put("pensionerCode", folderName);
                params.put("image", firebaseImageURLs.get(i).toString());
                params.put("imageName", "image-" + i);
                params.put("imageCount", i + "");
                if (volleyHelper.countRequestsInFlight("upload_image-" + i) == 0)
                    volleyHelper.makeStringRequest(url, "upload_image-" + i, params);
            } catch (Exception e) {
                e.printStackTrace();
               /* Helper.getInstance().showAlertDialog(
                        getContext(),
                        "Error 1\nPlease report this issue through feedback section",
                        "Submission Error",
                        "OK");*/

            }
        }
    }


    public void sendMail(HashMap<String, String> hashMap, String tag, VolleyHelper volleyHelper) {
        String url = Helper.getInstance().getAPIUrl() + "sendMail.php";
        if (volleyHelper.countRequestsInFlight(tag) == 0)
            volleyHelper.makeStringRequest(url, tag, hashMap);
    }
}
