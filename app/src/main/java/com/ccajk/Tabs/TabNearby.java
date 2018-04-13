package com.ccajk.Tabs;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ccajk.Activity.HomeActivity;
import com.ccajk.Models.LocationModel;
import com.ccajk.R;
import com.ccajk.Tools.FireBaseHelper;
import com.ccajk.Tools.Helper;
import com.ccajk.Tools.Preferences;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
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

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Log.v(TAG, "Updating My Location");
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
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.getActivity());
        locationModels = FireBaseHelper.getInstance().getLocationModels(Preferences.getInstance().getPrefState(getContext()));
        progressDialog = Helper.getInstance().getProgressDialog(view.getContext(),"");

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
                kilometres.setText("WITHIN " + getRadius(seekBarValue) + " KM");
                if (mLastLocation != null) {
                    AnimateCamera(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), locationModels, getZoomValue(seekBarValue));
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
            AnimateCamera(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), locationModels, 0);
        }
    }

    private void ManageLocation() {
        Log.v(TAG, "Checking for location permission");
        if (Helper.getInstance().checkForLocationPermissions(this.getActivity())) {
            Log.v(TAG, "Permission Available\nChecking for location on or off");
            createLocationRequest();
        } else {
            Log.v(TAG, "Permission not Available");
            requestLocationPermission();

        }
    }

    private void setMyMap(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        Log.v(TAG, "Maps Set");
    }

    public void createLocationRequest(

    ) {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this.getActivity());
        Log.v(TAG, "Location Request Created");

        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
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
    }


    private void requestLocationPermission() {
        //ActivityCompat.requestPermissions(this.getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
        getParentFragment().requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdates() {
        progressDialog.setMessage("Getting Current Location");
        progressDialog.show();
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        mMap.setMyLocationEnabled(true);
    }


    private void onLocationAcccessRequestFailure(Exception e) {
        Log.v(TAG,"Request Failure Further process");
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
        AnimateCamera(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), locationModels, getZoomValue(0));
        Log.v(TAG, "Animating through Callback ");
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    public int getZoomLevel(Circle circle) {
        int zoomLevel = 1;
        if (circle != null){
            double radius = circle.getRadius();
            double scale = radius / 500;
            zoomLevel =(int) (16 - Math.log(scale) / Math.log(2));
        }
        return zoomLevel;
    }
    private void AnimateCamera(LatLng focussedLocation, ArrayList<LocationModel> allLocations, int zoom) {
        if(progressDialog.isShowing())
        {
            progressDialog.dismiss();
        }
        Log.v(TAG, "In function");
        mMap.clear();
        Log.v(TAG, "Map Cleared");
        Circle circle = mMap.addCircle(new CircleOptions().center(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())).radius(getRadius(seekBarValue)*1000).strokeColor(Color.RED));
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
        ArrayList<LatLng> filteredMarkers = filterMarkers(allLocations);
        if (filteredMarkers.size() == 0) {

            Toast.makeText(this.getActivity(), "No Locations nearby\nIncrease Radius", Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.show();
        for (LatLng latlng :
                filteredMarkers) {
            mMap.addMarker(new MarkerOptions().position(latlng).title(allLocations.get(i++).getLocationName()));
        }

        if(progressDialog.isShowing())
        {
            progressDialog.dismiss();
        }
    }

    private ArrayList<LatLng> filterMarkers(ArrayList<LocationModel> locationModels) {
        if (locationModels == null) {
            Snackbar.make(this.getView(), "Error 1", Snackbar.LENGTH_SHORT);
            return null;
        }
        ArrayList<LatLng> filteredLocations = new ArrayList<>();
        int length = locationModels.size();
        float[] results = new float[1];
        double radius = getRadius(seekBarValue) * 1000;

        for (int i = 0; i < length; i++) {
            LocationModel locationModel = locationModels.get(i);
            Location.distanceBetween(mLastLocation.getLatitude(), mLastLocation.getLongitude(), Double.valueOf(locationModel.getLatitude()), Double.valueOf(locationModel.getLongitude()), results);
            if (results[0] <= radius ) {
                filteredLocations.add(new LatLng(Double.valueOf(locationModel.getLatitude()), Double.valueOf(locationModel.getLongitude())));
            }
        }
        return filteredLocations;
    }

    private int getZoomValue(int seekBarValue) {
        return 14 - seekBarValue;
    }

    private double getRadius(int seekBarValue) {

        double radius = Math.pow((seekBarValue + 1), 2);
        Log.v(TAG, "Radius = " + radius);
        return  radius;
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
                        Log.v(TAG,"Resolution success");
                        requestLocationUpdates();
                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        // The user was asked to change settings, but chose not to
                        Log.v(TAG,"Resolution denied");
                        ShowDialogOnLocationOff();
                        break;
                    }
                    default: {
                        Log.v(TAG,"User unable to do anything");
                        break;
                    }
                }
                break;
        }
    }

    private void ShowDialogOnPermissionDenied()
    {
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

    private void ShowDialogOnLocationOff()
    {
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