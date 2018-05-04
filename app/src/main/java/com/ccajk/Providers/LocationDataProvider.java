package com.ccajk.Providers;

import com.ccajk.Models.LocationModel;
import com.ccajk.Tools.FireBaseHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by balpreet on 4/27/2018.
 */

public class LocationDataProvider {

    private static LocationDataProvider _instance;
    private ArrayList<LocationModel> hotspotLocationModelArrayList;
    private ArrayList<LocationModel> gpLocationModelArrayList;

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

    public ArrayList<LocationModel> getLocationModelArrayList(String type) {
        if (type.equals(FireBaseHelper.getInstance().ROOT_GP)) {
            return gpLocationModelArrayList;
        } else {
            return hotspotLocationModelArrayList;
        }
    }

    public void setLocationModelArrayList(String type, ArrayList<LocationModel> arrayList) {
        sortByName(arrayList);
        if (type.equals(FireBaseHelper.getInstance().ROOT_GP)) {
            gpLocationModelArrayList = arrayList;
        } else {
            hotspotLocationModelArrayList = arrayList;
        }
    }

    public void sortByName(ArrayList<LocationModel> arrayList) {
        Collections.sort(arrayList, new Comparator<LocationModel>() {
            @Override
            public int compare(LocationModel o1, LocationModel o2) {
                return (o1.getLocationName().toLowerCase().compareTo(o2.getLocationName().toLowerCase()));
            }
        });
    }

    public void sortByDistrict(ArrayList<LocationModel> arrayList) {
        Collections.sort(arrayList, new Comparator<LocationModel>() {
            @Override
            public int compare(LocationModel o1, LocationModel o2) {
                return (o1.getDistrict().toLowerCase().compareTo(o2.getDistrict().toLowerCase()));
            }
        });
    }
}
