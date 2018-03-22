package com.ccajk.Tools;

import com.ccajk.Models.LocationModel;
import com.ccajk.Models.State;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by hp on 06-03-2018.
 */

public class FireBaseHelper {

    private static FireBaseHelper _instance;
    private ArrayList<LocationModel> locationModels;

    public DatabaseReference databaseReference;
    public final String ROOT_PENSIONERS = "Pensioners";
    public final String ROOT_ADHAAR = "Adhaar";
    public final String ROOT_ADHAAR_STATUS = "Adhaar-Status";
    public final String ROOT_PAN = "Pan";
    public final String ROOT_PAN_STATUS = "Pan-Status";

    public ArrayList<State> statelist;

    public FireBaseHelper() {
        _instance = this;
        databaseReference = FirebaseDatabase.getInstance().getReference();
        statelist = getStatelist();

    }

    public static FireBaseHelper getInstance() {
        if (_instance == null) {
            return new FireBaseHelper();
        } else {
            return _instance;
        }
    }

    public String getStatusString(long status) {
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
        AddLocations(stateId);
        return locationModels;
    }


    public void AddLocations(String stateId) {
        locationModels = new ArrayList<>();
        locationModels.add(new LocationModel("Sangrampur", "32.7400343", "74.7403159", stateId, "jammu"));
        locationModels.add(new LocationModel("Sohal", "32.4938192", "75.2548692", stateId, "jammu"));
        locationModels.add(new LocationModel("Sidhra", "32.7604934", "74.8989541", stateId, "jammu"));
        locationModels.add(new LocationModel("Sumb", "32.52839", "75.120054", stateId, "samba"));
        locationModels.add(new LocationModel("Trilokpur", "32.7148855", "74.752726", stateId, "jammu"));
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
