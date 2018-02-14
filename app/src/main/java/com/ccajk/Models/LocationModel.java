package com.ccajk.Models;

/**
 * Created by balpreet on 2/15/2018.
 */

public class LocationModel {

    private String locationName;
    private double longitude,latitude;

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public double getLongitude() {
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
    }
}
