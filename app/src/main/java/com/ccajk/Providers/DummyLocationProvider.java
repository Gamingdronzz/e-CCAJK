package com.ccajk.Providers;

import com.ccajk.Models.LocationModel;

import java.util.ArrayList;

/**
 * Created by hp on 23-03-2018.
 */


public class DummyLocationProvider {
    ArrayList<LocationModel> locationModels;
    public DummyLocationProvider()
    {
        locationModels = new ArrayList<>();
    }




    public void storeLocations(String StateID) {
//        locationModels.add(new LocationModel("Sangrampur","32.7400343", "74.7403159", StateID, "jammu"));
//        locationModels.add(new LocationModel("Sohal", "32.4938192", "75.2548692", StateID, "jammu"));
//        locationModels.add(new LocationModel("Sidhra", "32.7604934", "74.8989541", StateID, "jammu"));
//        locationModels.add(new LocationModel("Sumb", "32.52839", "75.120054", StateID, "samba"));
//        locationModels.add(new LocationModel("Trilokpur", "32.7148855", "74.752726", StateID, "jammu"));
    }


    public ArrayList<LocationModel> getLocations() {
        return locationModels;
    }
}
