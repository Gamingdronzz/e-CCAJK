package com.mycca.models;

import java.util.Date;

public class InspectionModel {

    private String staffId;
    private String locationName;
    private double latitude;
    private double longitute;
    private Date date;

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitute() {
        return longitute;
    }

    public void setLongitute(double longitute) {
        this.longitute = longitute;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public InspectionModel() {

    }

    public InspectionModel(String staffId, String locationName, double latitude, double longitute, Date date) {

        this.staffId = staffId;
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitute = longitute;
        this.date = date;
    }
}
