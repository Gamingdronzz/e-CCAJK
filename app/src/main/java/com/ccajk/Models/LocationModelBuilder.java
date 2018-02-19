package com.ccajk.Models;

import com.google.android.gms.maps.model.LatLng;

public class LocationModelBuilder {
    private String locationName;
    private LatLng location;

    public LocationModelBuilder setLocationName(String locationName) {
        this.locationName = locationName;
        return this;
    }

    public LocationModelBuilder setLocation(LatLng location) {
        this.location = location;
        return this;
    }

    public LocationModel createLocationModel() {
        return new LocationModel(locationName, location);
    }
}