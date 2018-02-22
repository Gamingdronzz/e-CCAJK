package com.ccajk.Tabs;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ccajk.Models.LocationModel;
import com.ccajk.R;
import com.ccajk.Tools.Helper;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
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

    private static final int REQUEST_CHECK_SETTINGS = 200;
    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private LatLng latLng;
    private int seekBarValue;
    private
    FusedLocationProviderClient mFusedLocationClient;

    private final String TAG = "Nearby";


    /*private ArrayList<LatLng> markers = new ArrayList<>();
    private ArrayList<String> names = new ArrayList<>();*/
    private ArrayList<LocationModel> locationModels = new ArrayList<>();
    IndicatorSeekBar seekBar;
    //private SeekBar seekBar;
    private final int LOCATION_REQUEST_CODE = 101;

    AppCompatActivity appCompatActivity;

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Log.v(TAG,"Updating My Location");
            for (Location location : locationResult.getLocations()) {
                placeMarkerOnMyLocation(location, locationResult);

            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_nearby_locations, container, false);
        init(view);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    public void setActivity(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }

    private void init(View view) {


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.getActivity());
        /*markers = helper.getMarkers();
        names = helper.getLocationNames();*/
        locationModels = Helper.getInstance().getLocationModels();

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
                //radius = seekBarValue + 15;

                //Toast.makeText(getContext(), "New Radius = " + radius, Toast.LENGTH_SHORT).show();
                if (mLastLocation != null) {
                    AnimateCamera(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), locationModels, getZoomValue(seekBarValue));
                }
            }
        });
      /*  seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBarValue = seekBar.getProgress();
                //radius = seekBarValue + 15;

                //Toast.makeText(getContext(), "New Radius = " + radius, Toast.LENGTH_SHORT).show();
                if (mLastLocation != null) {
                    AnimateCamera(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), locationModels, getZoomValue(seekBarValue));
                }

            }
        });*/

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        setMyMap(googleMap);
        createLocationRequest();
        checkForLocationPermissions();

        if (mLastLocation != null) {
            AnimateCamera(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), locationModels, 0);
        }
    }


    private void checkForLocationPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this.getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                Log.v(TAG,"Permission Granted");

                requestLocationUpdates();
            } else {
                requestLocationPermission();
                Log.v(TAG,"Permission Denied and requesting");
            }
        } else {
            Log.v(TAG,"Below M");
            requestLocationUpdates();
        }
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000); // two minute interval
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this.getActivity());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
                task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                        if(task.isSuccessful())
                        {
                            Log.v(TAG,"Task  Granted");
                        }
                    }
                });
        task.addOnSuccessListener(this.getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                Log.v(TAG,"Access  Granted");
            }
        });

        task.addOnFailureListener(this.getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    onLocationAcccessRequestFailure(e);
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    Log.v(TAG,"Access Denied");

                }
            }
        });
        Log.v(TAG,"Location Request Created");
    }

    private void setMyMap(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        Log.v(TAG,"Maps Set");
    }

    private void onLocationAcccessRequestFailure(Exception e) {
        try {
            // Show the dialog by calling startResolutionForResult(),
            // and check the result in onActivityResult().
            ResolvableApiException resolvable = (ResolvableApiException) e;
            resolvable.startResolutionForResult(this.getActivity(),
                    REQUEST_CHECK_SETTINGS);
        } catch (IntentSender.SendIntentException sendEx) {
            // Ignore the error.
        }
    }

    private void placeMarkerOnMyLocation(Location location, LocationResult locationResult) {
        Log.v(TAG, "Location: " + location.getLatitude() + " " + location.getLongitude());
        mLastLocation = location;
               /* if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }*/

        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        AnimateCamera(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), locationModels, getZoomValue(0));
        Log.v(TAG, "Animating through Callback ");
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdates() {

        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        mMap.setMyLocationEnabled(true);
        Log.v(TAG,"Requesting Location Updates");
    }

    private void AnimateCamera(LatLng focussedLocation, ArrayList<LocationModel> allLocations, int zoom) {
        Log.v(TAG, "In function");
        mMap.clear();
        Log.v(TAG, "Map Cleared");
        int i = 0;
        ArrayList<LatLng> filteredMarkers = filterMarkers(allLocations);
        if (filteredMarkers == null) {
            Log.v(TAG, "Map Cleared");
            Snackbar.make(this.getView(), "No Locations in nearby\nIncrease Radius", Snackbar.LENGTH_SHORT);
            return;
        }

        for (LatLng latlng :
                filteredMarkers) {
            mMap.addMarker(new MarkerOptions().position(latlng).title(allLocations.get(i++).getLocationName()));
        }


        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(focussedLocation)
                .zoom(zoom)                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to east
                .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        Log.v(TAG, "Animation Done");
    }

    private ArrayList<LatLng> filterMarkers(ArrayList<LocationModel> locationModels) {
        if (locationModels == null) {
            Snackbar.make(this.getView(), "Error 1", Snackbar.LENGTH_SHORT);
            return null;
        }
        ArrayList<LatLng> filteredLocations = new ArrayList<>();
        int length = locationModels.size();
        float[] results = new float[1];
        int radius = getRadius(seekBarValue);
        LatLng latLng;
        for (int i = 0; i < length; i++) {
            latLng = locationModels.get(i).getLocation();
            Location.distanceBetween(mLastLocation.getLatitude(), mLastLocation.getLongitude(), latLng.latitude, latLng.longitude, results);
            if (results[0] <= radius) {
                filteredLocations.add(latLng);
            }
        }
        return filteredLocations;
    }

    private int getZoomValue(int seekBarValue) {
        return 15 - seekBarValue;
    }

    private int getRadius(int seekBarValue) {
        Log.v(TAG, "Value = " + seekBarValue * 30000);
        return seekBarValue * 30000;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (permissions.length == 1 &&
                        permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestLocationUpdates();
                } else {

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
        Toast.makeText(this.getActivity(), "Going to My Location", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        //Toast.makeText(this.getActivity(), "Current location:\n" + location, Toast.LENGTH_LONG).show();
        Toast.makeText(this.getActivity(), "You are here", Toast.LENGTH_SHORT).show();

    }

}