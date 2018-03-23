package com.ccajk.Models;

/**
 * Created by balpreet on 2/15/2018.
 */

public class LocationModel {

    private String LocationName;
    private Double Latitude;
    private Double Longitude;
    private String StateID;
    private String District;

    public LocationModel()
    {

    }
    public LocationModel(String locationName, Double latitude, Double longitude, String state, String district) {
        this.LocationName = locationName;
        this.Latitude = latitude;
        this.Longitude = longitude;
        this.StateID = state;
        this.District = district;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        this.Latitude = latitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        this.Longitude = longitude;
    }

    public String getLocationName() {
        return LocationName;
    }

    public void setLocationName(String locationName) {
        this.LocationName = locationName;
    }

    public String getStateID() {
        return StateID;
    }

    public void setStateID(String state) {
        this.StateID = state;
    }

    public String getDistrict() {
        return District;
    }

    public void setDistrict(String district) {
        this.District = district;
    }

}
