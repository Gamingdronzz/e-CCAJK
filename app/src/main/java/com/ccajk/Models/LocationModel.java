package com.ccajk.Models;

/**
 * Created by balpreet on 2/15/2018.
 */

public class LocationModel {

    private String locationName;
    private String latitude;
    private String longitude;
    private String stateId;
    private String district;

    public LocationModel(String locationName, String latitude, String longitude, String state, String district) {
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.stateId = state;
        this.district = district;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getStateId() {
        return stateId;
    }

    public void setStateId(String state) {
        this.stateId = state;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

}
