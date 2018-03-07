
package com.ccajk.Tools;

import android.util.Log;

import com.ccajk.Models.LocationModel;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/*
 * Created by hp on 09-02-2018.
 */


public class Helper {
    private static Helper _instance;

    public static Helper getInstance() {
        if (_instance == null) {
            return new Helper();
        } else {
            return _instance;
        }
    }

    public Helper() {
        _instance = this;
        if (getLocationModels() == null) {
            AddLocations();
        }
    }

    private ArrayList<LocationModel> locationModels;

    public ArrayList<LocationModel> getLocationModels() {
        return locationModels;
    }

    public void setLocationModels(ArrayList<LocationModel> locationModels) {
        this.locationModels = locationModels;
    }

    public void addLocation(LocationModel locationModel) {
        if (locationModels == null) {
            locationModels = new ArrayList<LocationModel>();
        }

        locationModels.add(locationModel);
    }

    /**
     * calculates the distance between two locations in MILES
     */
    public static double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 3958.75; // in miles, change to 6371 for kilometer output

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double dist = earthRadius * c;
        Log.v("Helper", "Distance between coordinates = " + dist);

        return dist; // output distance, in MILES
    }

    public void AddLocations() {
        if (locationModels == null) {
            locationModels = new ArrayList<>();
        }
        locationModels.add(new LocationModel("Sangrampur", new LatLng(32.7400343, 74.7403159), getState(States.JammuKashmir), ""));
        locationModels.add(new LocationModel("Sohal", new LatLng(32.4938192, 75.2548692), getState(States.JammuKashmir), ""));
        locationModels.add(new LocationModel("Sidhra", new LatLng(32.7604934, 74.8989541), getState(States.JammuKashmir), ""));
        locationModels.add(new LocationModel("Sumb", new LatLng(32.52839, 75.120054), getState(States.JammuKashmir), ""));
        locationModels.add(new LocationModel("Trilokpur", new LatLng(32.7148855, 74.752726), getState(States.JammuKashmir), ""));

    }


    public enum States {
        AndhraPradesh,
        ArunachalPradesh,
        Assam,
        Bihar,
        Chhattisgarh,
        Goa,
        Gujarat,
        Haryana,
        HimachalPradesh,
        JammuKashmir,
        Jharkhand,
        Karnataka,
        Kerala,
        MadyaPradesh,
        Maharashtra,
        Manipur,
        Meghalaya,
        Mizoram,
        Nagaland,
        Orissa,
        Punjab,
        Rajasthan,
        Sikkim,
        TamilNadu,
        Tripura,
        Uttaranchal,
        UttarPradesh,
        WestBengal
    }

    public String[] states =
            {
                    "Andhra Pradesh",
                    "Arunachal Pradesh",
                    "Assam",
                    "Bihar",
                    "Chhattisgarh",
                    "Goa",
                    "Gujarat",
                    "Haryana",
                    "Himachal Pradesh",
                    "Jammu and Kashmir",
                    "Jharkhand",
                    "Karnataka",
                    "Kerala",
                    "Madya Pradesh",
                    "Maharashtra",
                    "Manipur",
                    "Meghalaya",
                    "Mizoram",
                    "Nagaland",
                    "Orissa",
                    "Punjab",
                    "Rajasthan",
                    "Sikkim",
                    "Tamil Nadu",
                    "Tripura",
                    "Uttaranchal",
                    "Uttar Pradesh",
                    "West Bengal"
            };

    public String getState(States state) {
        return states[state.ordinal()];
    }

}

