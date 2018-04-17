package com.ccajk.Tools;

import android.net.Uri;

import com.ccajk.Models.LocationModel;
import com.ccajk.Models.State;
import com.ccajk.Providers.DummyLocationProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by hp on 06-03-2018.
 */

public class FireBaseHelper {

    private static FireBaseHelper _instance;
    private ArrayList<LocationModel> locationModels;

    public DatabaseReference databaseReference;
    public StorageReference storageReference;
    public final String ROOT_PENSIONERS = "Pensioners";
    public final String ROOT_ADHAAR = "Adhaar";
    public final String ROOT_PAN = "Pan";
    public final String ROOT_GRIEVANCE_PENSION="Pension Grievance";
    public final String ROOT_GRIEVANCE_GPF="GPF Grievance";
    //public final String ROOT_ADHAAR_STATUS = "Adhaar-Status";
    //public final String ROOT_PAN_STATUS = "Pan-Status";

    public ArrayList<State> statelist;

    public FireBaseHelper() {
        _instance = this;
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        statelist = getStatelist();
    }

    public static FireBaseHelper getInstance() {
        if (_instance == null) {
            return new FireBaseHelper();
        } else {
            return _instance;
        }
    }

    public UploadTask uploadFile(String rootFolder, String subFolder, String filePath, String subType) {
        StorageReference sref;
        if(subType!=null) {
            sref = FireBaseHelper.getInstance().storageReference.child(rootFolder + "/" + subFolder + "/" + subType);
        }
        else
        {
            sref = FireBaseHelper.getInstance().storageReference.child(rootFolder + "/" + subFolder);
        }

        Uri file = Uri.fromFile(new File(filePath));
        UploadTask uploadTask = sref.putFile(file);
        return uploadTask;
    }

    public String getAdhaarPanStatusString(long status) {
        switch ((int)status) {
            case 0:
                return "Request Submitted";
            case 1:
                return "Request Processing";
            case 2:
                return "Updation Failed";
            case 3:
                return "Updation Successful";
        }
        return null;
    }

    public ArrayList<LocationModel> getLocationModels(String stateId) {
        DummyLocationProvider dummyLocationProvider = new DummyLocationProvider();
        dummyLocationProvider.storeLocations("jnk");
        return dummyLocationProvider.getLocations();
    }


    public ArrayList<State> getStatelist() {
        statelist = new ArrayList<>();
        statelist.add(new State("anp", "Andhra Pradesh"));
        statelist.add(new State("jnk", "Jammu and Kashmir"));
        statelist.add(new State("pnb", "Punjab"));
        return statelist;
    }


    public String getState(String stateId) {
        for (State s : statelist) {
            if (s.getId() == stateId)
                return s.getName();
        }
        return null;
    }

}
