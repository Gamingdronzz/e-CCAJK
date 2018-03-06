package com.ccajk.Models;

import com.google.android.gms.maps.model.LatLng;

public class LocationModelBuilder {
    private String locationName;
    private LatLng location;
    private String state;
    private String district;

    public LocationModelBuilder setLocationName(String locationName) {
        this.locationName = locationName;
        return this;
    }

    public LocationModelBuilder setLocation(LatLng location) {
        this.location = location;
        return this;
    }

    public LocationModelBuilder setState(String state) {
        this.state = state;
        return this;
    }

    public LocationModelBuilder setDistrict(String district) {
        this.district = district;
        return this;
    }

    public LocationModel createLocationModel() {
        return new LocationModel(locationName, location,state,district);
    }
}