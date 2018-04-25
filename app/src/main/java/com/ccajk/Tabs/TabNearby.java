package com.ccajk.Tabs;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ccajk.CustomObjects.ProgressDialog;
import com.ccajk.Models.LocationModel;
import com.ccajk.R;
import com.ccajk.Tools.FireBaseHelper;
import com.ccajk.Tools.Helper;
import com.ccajk.Tools.MapsHelper;
import com.ccajk.Tools.MyLocationManager;
import com.ccajk.Tools.Preferences;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
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

import static com.ccajk.Tools.MyLocationManager.CONNECTION_FAILURE_RESOLUTION_REQUEST;
import static com.ccajk.Tools.MyLocationManager.LOCATION_REQUEST_CODE;


//Our class extending fragment
public class TabNearby extends Fragment implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, OnMapReadyCallback {


    private int seekBarValue;
    private final String TAG = "Nearby";
    private ArrayList<LocationModel> locationModels = new ArrayList<>();

    TextView kilometres;
    IndicatorSeekBar seekBar;
    ProgressDialog progressDialog;
    MyLocationManager locationManager;
    MapsHelper mapsHelper;
    ImageButton buttonRefresh;
    RelativeLayout relativeLayoutNoLocation;

    private GoogleMap mMap;
    private Location mLastLocation;
    private LatLng latLng;
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Log.v(TAG, "Updating My Location");
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            for (Location location : locationResult.getLocations()) {
                mLastLocation = location;
                placeMarkerOnMyLocation(location);
                locationManager.cleanUp();
                manageNoLocationLayout(false);
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

        kilometres = view.findViewById(R.id.textview_range);
        relativeLayoutNoLocation = view.findViewById(R.id.layout_no_location);

        buttonRefresh = view.findViewById(R.id.image_btn_refresh);
        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: managing");
                //locationManager.ManageLocation();
                onMapReady(mMap);
            }
        });
        manageNoLocationLayout(true);

        locationManager = new MyLocationManager(this, mLocationCallback);
        mapsHelper = new MapsHelper(view.getContext());
        locationModels = FireBaseHelper.getInstance().getLocationModels(Preferences.getInstance().getPrefState(getContext()));
        progressDialog = Helper.getInstance().getProgressWindow(getActivity(), "");

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

            @SuppressLint("MissingPermission")
            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                seekBarValue = seekBar.getProgress();
                kilometres.setText("WITHIN " + mapsHelper.getRadius(seekBarValue) + " KM");
                if (mLastLocation != null) {
                    mapsHelper.AnimateCamera(locationModels, getZoomValue(seekBarValue), mMap, mLastLocation, seekBarValue);
                } else {
                    locationManager.ManageLocation();
                }
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void manageNoLocationLayout(boolean show) {
        if (show)
            relativeLayoutNoLocation.setVisibility(View.VISIBLE);
        else
            relativeLayoutNoLocation.setVisibility(View.GONE);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        setMyMap(googleMap);

        Task<LocationSettingsResponse> task = locationManager.ManageLocation();
        if (task != null) {
            task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                @Override
                public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                    Log.v(TAG, "On Task Complete");
                    if (task.isSuccessful()) {
                        Log.v(TAG, "Task is Successful");
                        locationManager.requestLocationUpdates(mMap);
                        manageNoLocationLayout(false);


                    } else {
                        Log.v(TAG, "Task is not Successful");
                    }
                }
            });
            task.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    Log.v(TAG, "On Task Success");
                    // All location settings are satisfied. The client can initialize
                    // location requests here.
                    // ...

                }
            });

            task.addOnFailureListener(getActivity(), new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.v(TAG, "On Task Failed");
                    if (e instanceof ResolvableApiException) {
                        locationManager.onLocationAcccessRequestFailure(e);
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.

                    }
                }
            });
        }


        if (mLastLocation != null) {
            mapsHelper.AnimateCamera(locationModels, 0, mMap, mLastLocation, seekBarValue);
        }

    }

    private void setMyMap(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        Log.v(TAG, "Maps Set");
    }

    private void placeMarkerOnMyLocation(Location location) {
        Log.v(TAG, "Location: " + location.getLatitude() + " " + location.getLongitude());
        mLastLocation = location;
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        mapsHelper.AnimateCamera(locationModels, getZoomValue(0), mMap, mLastLocation, seekBarValue);
        Log.v(TAG, "Animating through Callback ");
    }

    private int getZoomValue(int seekBarValue) {
        return 14 - seekBarValue;
    }

    @Override
    public boolean onMyLocationButtonClick() {
        if (mLastLocation == null) {
            Toast.makeText(this.getActivity(), "Please Enable Location and Internet", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this.getActivity(), "You are here", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        for (String s : permissions) {
            Log.v(TAG, "Premissions = " + s);
        }
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationManager.ManageLocation();
                    mMap.setMyLocationEnabled(true);
                } else {
                    locationManager.ShowDialogOnPermissionDenied("Location Permission denied !\nHotspot Locator will not work without location access.\n\nDo you want to grant location acces ?");
                }
                return;
            }
        }
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
                        locationManager.requestLocationUpdates(mMap);
                        manageNoLocationLayout(false);
                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        // The user was asked to change settings, but chose not to
                        Log.v(TAG, "Resolution denied");
                        Helper.getInstance().showAlertDialog(
                                getContext(),
                                "Location not turned on! Hotspot Locator will not show nearby locations without location access.",
                                "CCA JK",
                                "OK");
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

}

     /*@TargetApi(Build.VERSION_CODES.M)
    private void ManageLocation() {
        Log.v(TAG, "Checking for location permission");
        if (locationManager.checkForLocationPermission()) {
            Log.v(TAG, "Permission Available\nChecking for location on or off");
            Task<LocationSettingsResponse> task = locationManager.createLocationRequest();
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
*/
   /* private void ShowDialogOnPermissionDenied() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                this.getActivity(), R.style.MyAlertDialogStyle);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                locationManager.ManageLocation();
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
                locationManager.ManageLocation();
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
    }*/



