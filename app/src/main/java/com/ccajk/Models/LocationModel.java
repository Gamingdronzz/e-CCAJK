package com.ccajk.Models;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by balpreet on 2/15/2018.
 */

public class LocationModel {

    private String locationName;
    private LatLng location;
    private String state;
    private String district;
    //private double longitude,latitude;

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
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

    public LocationModel(String locationName, LatLng location, String state,String district) {
        this.locationName = locationName;
        this.location = location;
        this.state=state;
        this.district=district;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }
}
