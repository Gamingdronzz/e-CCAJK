package com.ccajk.Tools;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.ccajk.R;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by balpreet on 4/15/2018.
 */

public class LocationManager {
    final String TAG = "LocationManager";
    public static final int LOCATION_REQUEST_CODE = 101;
    public static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 102;

    Fragment context;
    LocationRequest mLocationRequest;
    LocationCallback mLocationCallback;
    FusedLocationProviderClient mFusedLocationClient;

    public LocationManager(Fragment context, LocationCallback locationCallback, FusedLocationProviderClient mFusedLocationClient, LocationRequest mLocationRequest) {
        this.context = context;
        this.mLocationCallback = locationCallback;
        this.mFusedLocationClient = mFusedLocationClient;
        this.mLocationRequest = mLocationRequest;
    }

    public boolean getLocationPermission() {
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


    public Task<LocationSettingsResponse> createLocationRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(context.getActivity());
        Log.v(TAG, "Location Request Created");

        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        return task;
    }


    @SuppressLint("NewApi")
    public void ManageLocation() {
        Log.v(TAG, "Checking for location permission");
        if (getLocationPermission()) {
            Log.v(TAG, "Permission Available\nChecking for location on or off");
            Task<LocationSettingsResponse> task = createLocationRequest();
            task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                @Override
                public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                    Log.v(TAG, "On Task Complete");
                    if (task.isSuccessful()) {
                        Log.v(TAG, "Task is Successful");
                        requestLocationUpdates();
                    } else {
                        Log.v(TAG, "Task is not Successful");
                    }
                }
            });
            task.addOnSuccessListener(context.getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    Log.v(TAG, "On Task Success");
                    // All location settings are satisfied. The client can initialize
                    // location requests here.
                    // ...

                }
            });

            task.addOnFailureListener(context.getActivity(), new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.v(TAG, "On Task Failed");
                    if (e instanceof ResolvableApiException) {
                        onLocationAcccessRequestFailure(e);
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.

                    }
                }
            });
        } else {
            Log.v(TAG, "Permission not Available");
            if (context.getParentFragment() != null)
                requestLocationPermission(context.getParentFragment(), LOCATION_REQUEST_CODE);
            else
                requestLocationPermission(context, LOCATION_REQUEST_CODE);
        }
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

    public void ShowDialogOnPermissionDenied(String message) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                context.getActivity(), R.style.MyAlertDialogStyle);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ManageLocation();
            }
        })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setMessage(message)
                .setTitle("CCA JK")
                .show();
    }

    public void ShowDialogOnLocationOff(String message) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                context.getActivity(), R.style.MyAlertDialogStyle);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ManageLocation();
            }
        })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setMessage(message)
                .setTitle("CCA JK")
                .show();
    }

}
