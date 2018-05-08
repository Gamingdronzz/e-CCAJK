package com.mycca.Tools;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.mycca.CustomObjects.FancyAlertDialog.FancyAlertDialogType;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.Task;

/**
 * Created by balpreet on 4/15/2018.
 */

public class MyLocationManager {

    public interface LocationCallBack {

    }

    final String TAG = "MyLocationManager";
    public static final int LOCATION_REQUEST_CODE = 101;
    public static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 102;

    Fragment context;
    LocationRequest mLocationRequest;
    LocationCallback mLocationCallback;
    FusedLocationProviderClient mFusedLocationClient;

    public MyLocationManager(Fragment context, LocationCallback locationCallback) {
        this.context = context;
        this.mLocationCallback = locationCallback;
        this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context.getActivity());
        this.mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000); // two minute interval
        mLocationRequest.setFastestInterval(2000);
    }

    @SuppressLint("NewApi")
    public Task<LocationSettingsResponse> ManageLocation() {
        Log.v(TAG, "Checking for location permission");
        if (checkForLocationPermission()) {
            Log.v(TAG, "Permission Available\nChecking for location on or off");
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            Task<LocationSettingsResponse> task = createLocationRequest();
            return task;
        } else {
            Log.v(TAG, "Permission not Available");
            if (context.getParentFragment() != null)
                requestLocationPermission(context.getParentFragment(), LOCATION_REQUEST_CODE);
            else
                requestLocationPermission(context, LOCATION_REQUEST_CODE);
            return null;
        }
    }

    private boolean checkForLocationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context.getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

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
        fragment.requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, requestCode);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestLocationPermission(Activity activity, int requestCode) {
        activity.requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, requestCode);
    }


    public Task<LocationSettingsResponse> createLocationRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(context.getActivity());
        Log.v(TAG, "Location Request Created");

        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        return task;
    }

    @SuppressLint("MissingPermission")
    public void requestLocationUpdates(GoogleMap mMap) {
        //progressDialog.setMessage("Getting Current Location");
        //progressDialog.show();
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        mMap.setMyLocationEnabled(true);
    }


    @SuppressLint("MissingPermission")
    public void requestLocationUpdates() {
        //progressDialog.setMessage("Getting Current Location");
        //progressDialog.show();
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

    }

    public void onLocationAcccessRequestFailure(Exception e) {
        Log.v(TAG, "Request Failure Further process");
        try {
            // Show the dialog by calling startResolutionForResult(),
            // and check the result in onActivityResult().
            ResolvableApiException resolvable = (ResolvableApiException) e;
            resolvable.startResolutionForResult(context.getActivity(),
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);
        } catch (IntentSender.SendIntentException sendEx) {
            // Ignore the error.
        }
    }

    public void cleanUp() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    public void ShowDialogOnPermissionDenied(String message) {

        Helper.getInstance().showFancyAlertDialog(context.getActivity(),
                message,
                "Location",
                "OK",
                null,
                null,
                null,
                FancyAlertDialogType.WARNING);
    }

    public void ShowDialogOnLocationOff(String message) {
        Helper.getInstance().showFancyAlertDialog(context.getActivity(),
                message,
                "Location",
                "OK",
                null,
                null,
                null,
                FancyAlertDialogType.WARNING);
    }


}