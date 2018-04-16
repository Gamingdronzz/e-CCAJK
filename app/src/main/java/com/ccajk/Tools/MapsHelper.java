package com.ccajk.Tools;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.ccajk.Models.LocationModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by hp on 16-04-2018.
 */

public class MapsHelper {

    final String TAG = "MapsHelper";
    Context context;

    public MapsHelper(Context context) {
        this.context = context;
    }

    public void AnimateCamera(LatLng focussedLocation, ArrayList<LocationModel> allLocations, int zoom, GoogleMap mMap, Location mLastLocation, Context context, int value) {

        /*if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }*/
        Log.v(TAG, "In function");
        mMap.clear();
        Log.v(TAG, "Map Cleared");
        Circle circle = mMap.addCircle(new CircleOptions().center(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())).radius(getRadius(value) * 1000).strokeColor(Color.RED));
        circle.setVisible(true);
        getZoomLevel(circle);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(focussedLocation)
                .zoom(zoom)                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to east
                .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        Log.v(TAG, "Animation Done");

        int i = 0;
        ArrayList<LatLng> filteredMarkers = filterMarkers(allLocations,value,mLastLocation);
        if (filteredMarkers.size() == 0) {
            Toast.makeText(context, "No Locations nearby\nIncrease Radius", Toast.LENGTH_LONG).show();
            return;
        }

        for (LatLng latlng :
                filteredMarkers) {
            mMap.addMarker(new MarkerOptions().position(latlng).title(allLocations.get(i++).getLocationName()));
        }

        /*
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        */
    }


    private ArrayList<LatLng> filterMarkers(ArrayList<LocationModel> locationModels,int value,Location mLastLocation) {
        if (locationModels == null) {
            return null;
        }
        ArrayList<LatLng> filteredLocations = new ArrayList<>();
        int length = locationModels.size();
        float[] results = new float[1];
        double radius = getRadius(value) * 1000;

        for (int i = 0; i < length; i++) {
            LocationModel locationModel = locationModels.get(i);
            Location.distanceBetween(mLastLocation.getLatitude(), mLastLocation.getLongitude(), Double.valueOf(locationModel.getLatitude()), Double.valueOf(locationModel.getLongitude()), results);
            if (results[0] <= radius) {
                filteredLocations.add(new LatLng(Double.valueOf(locationModel.getLatitude()), Double.valueOf(locationModel.getLongitude())));
            }
        }
        return filteredLocations;
    }

    public double getRadius(int value) {
        double radius = Math.pow((value + 1), 2);
        Log.v(TAG, "Radius = " + radius);
        return radius;
    }

    private int getZoomLevel(Circle circle) {
        int zoomLevel = 1;
        if (circle != null) {
            double radius = circle.getRadius();
            double scale = radius / 500;
            zoomLevel = (int) (16 - Math.log(scale) / Math.log(2));
        }
        return zoomLevel;
    }
}
