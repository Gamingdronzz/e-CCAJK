package com.ccajk.Models;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by balpreet on 2/15/2018.
 */

public class LocationModel {

    private String locationName;
    private LatLng location;
    //private double longitude,latitude;

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    /*public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public LocationModel(String locationName, double longitude, double latitude) {

        this.locationName = locationName;
        this.longitude = longitude;
        this.latitude = latitude;
    }*/

    public LocationModel(String locationName, LatLng location) {
        this.locationName = locationName;
        this.location = location;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }
}
