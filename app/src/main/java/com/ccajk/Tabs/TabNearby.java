package com.ccajk.Tabs;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ccajk.Models.LocationModel;
import com.ccajk.R;
import com.ccajk.Tools.FireBaseHelper;
import com.ccajk.Tools.Helper;
import com.ccajk.Tools.LocationManager;
import com.ccajk.Tools.Preferences;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.IndicatorSeekBarType;
import com.warkiz.widget.IndicatorType;
import com.warkiz.widget.TickType;

import java.util.ArrayList;

//Our class extending fragment
public class TabNearby extends Fragment implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, OnMapReadyCallback {

    public static final int LOCATION_REQUEST_CODE = 101;
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 102;
    private TextView kilometres;
    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private LatLng latLng;
    private int seekBarValue;
    private FusedLocationProviderClient mFusedLocationClient;
    private final String TAG = "Nearby";
    private ArrayList<LocationModel> locationModels = new ArrayList<>();
    IndicatorSeekBar seekBar;
    ProgressDialog progressDialog;
    LocationManager locationManager;

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Log.v(TAG, "Updating My Location");
            if(progressDialog.isShowing())
            {
                progressDialog.dismiss();
            }
            for (Location location : locationResult.getLocations()) {
                placeMarkerOnMyLocation(location);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_nearby_locations, container, false);
        init(view);
        return view;
    }


    private void init(View view) {
        kilometres = view.findViewById(R.id.nearby);
        locationManager = new LocationManager(view.getContext());
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.getActivity());
        locationModels = FireBaseHelper.getInstance().getLocationModels(Preferences.getInstance().getPrefState(getContext()));
        progressDialog = Helper.getInstance().getProgressDialog(view.getContext(), "");

        seekBar = view.findViewById(R.id.seekBar);
        seekBar.getBuilder()
                .setMax(3)
                .setMin(0)
                .setProgress(0)
                .setSeekBarType(IndicatorSeekBarType.DISCRETE_TICKS)
                .setTickType(TickType.OVAL)
                .setTickNum(1)
                .setBackgroundTrackSize(2)//dp size
                .setProgressTrackSize(3)//dp size
                .setIndicatorType(IndicatorType.CIRCULAR_BUBBLE)
                .setIndicatorColor(getResources().getColor(R.color.colorAccent))
                .build();

        seekBar.setOnSeekChangeListener(new IndicatorSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(IndicatorSeekBar seekBar, int progress, float progressFloat, boolean fromUserTouch) {
            }

            @Override
            public void onSectionChanged(IndicatorSeekBar seekBar, int thumbPosOnTick, String textBelowTick, boolean fromUserTouch) {
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar, int thumbPosOnTick) {
            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                seekBarValue = seekBar.getProgress();
                kilometres.setText("WITHIN " + locationManager.getRadius(seekBarValue) + " KM");
                if (mLastLocation != null) {
                    locationManager.AnimateCamera(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), locationModels, getZoomValue(seekBarValue),mMap,mLastLocation,getActivity(),seekBarValue);
                }
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000); // two minute interval
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        setMyMap(googleMap);

        ManageLocation();

        if (mLastLocation != null) {
           locationManager.AnimateCamera(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), locationModels, 0,mMap,mLastLocation,getActivity(),seekBarValue);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void ManageLocation() {
        Log.v(TAG, "Checking for location permission");
        if (locationManager.getLocationPermission(this.getActivity())) {
            Log.v(TAG, "Permission Available\nChecking for location on or off");
            Task<LocationSettingsResponse> task = locationManager.createLocationRequest(getActivity(),mLocationRequest);
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
            task.addOnSuccessListener(this.getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    Log.v(TAG, "On Task Success");
                    // All location settings are satisfied. The client can initialize
                    // location requests here.
                    // ...

                }
            });

            task.addOnFailureListener(this.getActivity(), new OnFailureListener() {
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
            locationManager.requestLocationPermission(getParentFragment(), LOCATION_REQUEST_CODE);

        }
    }

    private void setMyMap(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        Log.v(TAG, "Maps Set");
    }




    @SuppressLint("MissingPermission")
    private void requestLocationUpdates() {
        progressDialog.setMessage("Getting Current Location");
        progressDialog.show();
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        mMap.setMyLocationEnabled(true);
    }


    private void onLocationAcccessRequestFailure(Exception e) {
        Log.v(TAG, "Request Failure Further process");
        try {
            // Show the dialog by calling startResolutionForResult(),
            // and check the result in onActivityResult().
            ResolvableApiException resolvable = (ResolvableApiException) e;
            resolvable.startResolutionForResult(this.getActivity(),
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);
        } catch (IntentSender.SendIntentException sendEx) {
            // Ignore the error.
        }
    }

    private void placeMarkerOnMyLocation(Location location) {
        Log.v(TAG, "Location: " + location.getLatitude() + " " + location.getLongitude());
        mLastLocation = location;
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        locationManager.AnimateCamera(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), locationModels, getZoomValue(0),mMap,mLastLocation,getActivity(),seekBarValue);
        Log.v(TAG, "Animating through Callback ");
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }







    private int getZoomValue(int seekBarValue) {
        return 14 - seekBarValue;
    }



    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        for (String s
                : permissions) {
            Log.v(TAG, "Premissions = " + s);
        }
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ManageLocation();
                } else {
                    ShowDialogOnPermissionDenied();
                }
                return;
            }
        }
    }


    @Override
    public boolean onMyLocationButtonClick() {
        if (mLastLocation == null) {
            Toast.makeText(this.getActivity(), "Please Enable Location First", Toast.LENGTH_LONG).show();
            return false;
        }
        //Toast.makeText(this.getActivity(), "Going to My Location", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        //Toast.makeText(this.getActivity(), "Current location:\n" + location, Toast.LENGTH_LONG).show();
        Toast.makeText(this.getActivity(), "You are here", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, Integer.toString(resultCode));

        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case CONNECTION_FAILURE_RESOLUTION_REQUEST:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        Log.v(TAG, "Resolution success");
                        requestLocationUpdates();
                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        // The user was asked to change settings, but chose not to
                        Log.v(TAG, "Resolution denied");
                        ShowDialogOnLocationOff();
                        break;
                    }
                    default: {
                        Log.v(TAG, "User unable to do anything");
                        break;
                    }
                }
                break;
        }
    }

    private void ShowDialogOnPermissionDenied() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                this.getActivity(), R.style.MyAlertDialogStyle);
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
                .setMessage("Location Permission denied !\nHotspot Locator will not work without location access.\n\nDo you want to grant location acces ?")
                .setTitle("CCA JK")
                .show();
    }

    private void ShowDialogOnLocationOff() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                this.getActivity(), R.style.MyAlertDialogStyle);
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
                .setMessage("Location not turned on!\nHotspot Locator will not show nearby locations without location access.\n\nDo you want to turn location on ?")
                .setTitle("CCA JK")
                .show();
    }
}