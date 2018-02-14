package com.ccajk.Models;

public class LocationBuilder {
    private String locationName;
    private double longitude;
    private double latitude;

    public LocationBuilder setLocationName(String locationName) {
        this.locationName = locationName;
        return this;
    }

    public LocationBuilder setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public LocationBuilder setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public LocationModel createLocation() {
        return new LocationModel(locationName, longitude, latitude);
    }
}