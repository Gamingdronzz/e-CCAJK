package com.mycca.models;

import java.util.Date;

public class InspectionModel {

    private String staffId;
    private String staffEmail;
    private String locationName;
    private String state;
    private double latitude;
    private double longitude;
    private Date date;

    public InspectionModel() { }

    public InspectionModel(String staffId, String locationName, double latitude, double longitude, Date date) {

        this.staffId = staffId;
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
    }

    public InspectionModel(String staffId, String staffEmail, String locationName, double latitude, double longitude, Date date) {
        this.staffId = staffId;
        this.staffEmail = staffEmail;
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
    }

    public String getStaffId() {
        return staffId;
    }

    public String getStaffEmail() {
        return staffEmail;
    }

    public String getLocationName() {
        return locationName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Date getDate() {
        return date;
    }
}
