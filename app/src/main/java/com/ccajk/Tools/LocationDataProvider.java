package com.ccajk.Tools;

import com.ccajk.Models.LocationModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by balpreet on 4/27/2018.
 */

public class LocationDataProvider {

    private static LocationDataProvider _instance;
    public LocationDataProvider() {
        _instance = this;
    }

    public static LocationDataProvider getInstance() {
        if (_instance == null) {
            return new LocationDataProvider();
        } else {
            return _instance;
        }
    }

    private ArrayList<LocationModel> hotspotLocationModelArrayList;

    public ArrayList<LocationModel> getHotspotLocationModelArrayList() {
        return hotspotLocationModelArrayList;
    }

    public void setHotspotLocationModelArrayList(ArrayList<LocationModel> hotspotLocationModelArrayList) {
        this.hotspotLocationModelArrayList = hotspotLocationModelArrayList;
        sort();

    }

    public ArrayList<LocationModel> getGpLocationModelArrayList() {
        return gpLocationModelArrayList;
    }

    public void setGpLocationModelArrayList(ArrayList<LocationModel> gpLocationModelArrayList) {
        this.gpLocationModelArrayList = gpLocationModelArrayList;
        sort();
    }

    private ArrayList<LocationModel> gpLocationModelArrayList;

    private void sort()
    {
        Collections.sort(this.hotspotLocationModelArrayList, new Comparator<LocationModel>() {
            @Override
            public int compare(LocationModel o1, LocationModel o2) {
                return o1.getLocationName().compareToIgnoreCase(o2.getLocationName());
            }
        });
    }
}
