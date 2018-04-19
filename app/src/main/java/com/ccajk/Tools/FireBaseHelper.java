package com.ccajk.Tools;

import android.content.Context;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.widget.Toast;

import com.ccajk.Models.LocationModel;
import com.ccajk.Models.State;
import com.ccajk.Providers.DummyLocationProvider;
import com.ccajk.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    public final String ROOT_ADHAAR = "Aadhaar";
    public final String ROOT_PAN = "Pan";
    public final String ROOT_GRIEVANCES = "Grievances";
    public final String ROOT_GRIEVANCE_PENSION = "Pension Grievance";
    public final String ROOT_GRIEVANCE_GPF = "GPF Grievance";
    public final String ROOT_PASSWORD = "password";
    public final String ROOT_TYPE = "type";
    public static final String ROOT_STAFF = "Staff";
    private final String TAG = "firebaseHelper";

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
        if (subType != null) {
            sref = FireBaseHelper.getInstance().storageReference.child(rootFolder + "/" + subFolder + "/" + subType);
        } else {
            sref = FireBaseHelper.getInstance().storageReference.child(rootFolder + "/" + subFolder);
        }

        Uri file = Uri.fromFile(new File(filePath));
        UploadTask uploadTask = sref.putFile(file);
        return uploadTask;
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
