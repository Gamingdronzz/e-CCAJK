package com.ccajk.Tools;

import android.Manifest;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.ccajk.Models.LocationModel;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import static com.ccajk.Tabs.TabNearby.LOCATION_REQUEST_CODE;

/**
 * Created by balpreet on 4/15/2018.
 */

public class LocationManager {
    final String TAG = "LocationManager";
    Context context;

    public LocationManager(Context context) {
        this.context = context;
    }

    public boolean getLocationPermission(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                Log.v(TAG, "Permission Granted");
                return true;
            } else {
                return false;

            }
        } else {
            return true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestLocationPermission(Fragment fragment, int requestCode) {
        //ActivityCompat.requestPermissions(this.getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
        fragment.requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},requestCode);
    }

    public Task<LocationSettingsResponse> createLocationRequest(Context context,LocationRequest mLocationRequest) {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(context);
        Log.v(TAG, "Location Request Created");

        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        return task;


    }

    public void AnimateCamera(LatLng focussedLocation, ArrayList<LocationModel> allLocations, int zoom, GoogleMap mMap, Location mLastLocation, Context context, int seekBarValue) {
//        if (progressDialog.isShowing()) {
//            progressDialog.dismiss();
//        }
        Log.v(TAG, "In function");
        mMap.clear();
        Log.v(TAG, "Map Cleared");
        Circle circle = mMap.addCircle(new CircleOptions().center(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())).radius(getRadius(seekBarValue) * 1000).strokeColor(Color.RED));
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
        ArrayList<LatLng> filteredMarkers = filterMarkers(allLocations,seekBarValue,mLastLocation);
        if (filteredMarkers.size() == 0) {
            Toast.makeText(context, "No Locations nearby\nIncrease Radius", Toast.LENGTH_LONG).show();
            return;
        }

        for (LatLng latlng :
                filteredMarkers) {
            mMap.addMarker(new MarkerOptions().position(latlng).title(allLocations.get(i++).getLocationName()));
        }

//        if (progressDialog.isShowing()) {
//            progressDialog.dismiss();
//        }
    }

    private ArrayList<LatLng> filterMarkers(ArrayList<LocationModel> locationModels,int seekBarValue,Location mLastLocation) {
        if (locationModels == null) {
            return null;
        }
        ArrayList<LatLng> filteredLocations = new ArrayList<>();
        int length = locationModels.size();
        float[] results = new float[1];
        double radius = getRadius(seekBarValue) * 1000;

        for (int i = 0; i < length; i++) {
            LocationModel locationModel = locationModels.get(i);
            Location.distanceBetween(mLastLocation.getLatitude(), mLastLocation.getLongitude(), Double.valueOf(locationModel.getLatitude()), Double.valueOf(locationModel.getLongitude()), results);
            if (results[0] <= radius) {
                filteredLocations.add(new LatLng(Double.valueOf(locationModel.getLatitude()), Double.valueOf(locationModel.getLongitude())));
            }
        }
        return filteredLocations;
    }

    public double getRadius(int seekBarValue) {

        double radius = Math.pow((seekBarValue + 1), 2);
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
