package com.ccajk.Models;

public class LocationModelBuilder {
    private String latitude;
    private String longitude;
    private String locationName;
    private String stateId;
    private String district;

    public LocationModelBuilder setLatitude(String latitude) {
        this.latitude = latitude;
        return this;
    }

    public LocationModelBuilder setLongitude(String longitude) {
        this.longitude = longitude;
        return this;
    }

    public LocationModelBuilder setLocationName(String locationName) {
        this.locationName = locationName;
        return this;
    }

    public LocationModelBuilder setStateId(String state) {
        this.stateId = state;
        return this;
    }

    public LocationModelBuilder setDistrict(String district) {
        this.district = district;
        return this;
    }

    public LocationModel createLocationModel() {
        return new LocationModel(locationName, latitude, longitude, stateId, district);
    }
}