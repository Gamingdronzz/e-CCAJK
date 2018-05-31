package com.mycca.Tabs.Locator;


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
import com.mycca.Activity.MainActivity;
import com.mycca.CustomObjects.FancyAlertDialog.FancyAlertDialogType;
import com.mycca.CustomObjects.FancyShowCase.FancyShowCaseQueue;
import com.mycca.CustomObjects.FancyShowCase.FancyShowCaseView;
import com.mycca.CustomObjects.FancyShowCase.FocusShape;
import com.mycca.CustomObjects.IndicatorSeekBar.IndicatorSeekBar;
import com.mycca.CustomObjects.IndicatorSeekBar.IndicatorSeekBarType;
import com.mycca.CustomObjects.IndicatorSeekBar.IndicatorType;
import com.mycca.CustomObjects.IndicatorSeekBar.TickType;
import com.mycca.CustomObjects.Progress.ProgressDialog;
import com.mycca.Models.LocationModel;
import com.mycca.Providers.LocationDataProvider;
import com.mycca.R;
import com.mycca.Tools.Helper;
import com.mycca.Tools.MapsHelper;
import com.mycca.Tools.MyLocationManager;
import com.mycca.Tools.Preferences;

import java.util.ArrayList;

import static com.mycca.Tools.MyLocationManager.CONNECTION_FAILURE_RESOLUTION_REQUEST;
import static com.mycca.Tools.MyLocationManager.LOCATION_REQUEST_CODE;


//Our class extending fragment

public class TabNearby extends Fragment implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, OnMapReadyCallback {


    private int seekBarValue;
    private final String TAG = "Nearby";
    String locatorType;
    private ArrayList<LocationModel> locationModels = new ArrayList<>();

    TextView kilometres;
    IndicatorSeekBar seekBar;
    ProgressDialog progressDialog;
    public MyLocationManager locationManager;
    MapsHelper mapsHelper;
    ImageButton buttonRefresh;
    RelativeLayout relativeLayoutNoLocation;

    private GoogleMap mMap;
    private Location mLastLocation;
    private LatLng latLng;
    View mapView;
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Log.v(TAG, "Updating My Location");

            progressDialog.dismiss();

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
        locatorType = getArguments().getString("Locator");
        Log.d(TAG, "onCreateView: tabnearby created");
        bindViews(view);
        init();
        if (Preferences.getInstance().getBooleanPref(getContext(), Preferences.PREF_HELP_NEARBY)) {
            showTutorial();
            Preferences.getInstance().setBooleanPref(getContext(), Preferences.PREF_HELP_NEARBY, false);
        }
        return view;
    }

    private void init() {
        Log.d(TAG, "init: tabnearby init");
        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: managing");
                //locationManager.ManageLocation();
                startLocationProcess();
            }
        });
        manageNoLocationLayout(true);
        locationManager = new MyLocationManager(this, mLocationCallback);

        locationModels = LocationDataProvider.getInstance().getLocationModelArrayList(locatorType);
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
                .setIndicatorColor(getResources().getColor(R.color.colorAccentLight))
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
                }
            }
        });

    }

    private void bindViews(View view) {
        Log.d(TAG, "bindViews: ");
        kilometres = view.findViewById(R.id.textview_range);
        relativeLayoutNoLocation = view.findViewById(R.id.layout_no_location);
        mapsHelper = new MapsHelper(view.getContext());
        buttonRefresh = view.findViewById(R.id.image_btn_refresh);
        progressDialog = Helper.getInstance().getProgressWindow(getActivity(), "");
        seekBar = view.findViewById(R.id.seekBar);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();

    }

    private void manageNoLocationLayout(boolean show) {
        if (show)
            relativeLayoutNoLocation.setVisibility(View.VISIBLE);
        else
            relativeLayoutNoLocation.setVisibility(View.GONE);
    }

    public void startLocationProcess() {
        Task<LocationSettingsResponse> task = locationManager.ManageLocation();
        if (task != null) {
            task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                @Override
                public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                    Log.v(TAG, "On Task Complete");
                    if (task.isSuccessful()) {
                        Log.v(TAG, "Task is Successful");
                        progressDialog.setMessage("Getting Current Location Coordinates");
                        progressDialog.show();
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

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        setMyMap(googleMap);


    }

    private void setMyMap(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        Log.v(TAG, "Maps Set");

        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("4"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom


        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);

        rlp.addRule(RelativeLayout.ALIGN_PARENT_END, 0);
        rlp.addRule(RelativeLayout.ALIGN_END, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        rlp.setMargins(20, 25, 0, 0);
    }

    private void showTutorial() {
        final FancyShowCaseView fancyShowCaseView1 = new FancyShowCaseView.Builder(getActivity())
                .title("Change radius for searching nearby hotspots")
                .focusOn(seekBar)
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .focusCircleRadiusFactor(.5)
                .build();

        ((MainActivity) getActivity()).mQueue = new FancyShowCaseQueue().add(fancyShowCaseView1);

        ((MainActivity) getActivity()).mQueue.setCompleteListener(new com.mycca.CustomObjects.FancyShowCase.OnCompleteListener() {
            @Override
            public void onComplete() {
                ((MainActivity) getActivity()).mQueue = null;
            }
        });

        ((MainActivity) getActivity()).mQueue.show();
    }

    private void placeMarkerOnMyLocation(Location location) {
        Log.v(TAG, "Location: " + location.getLatitude() + " " + location.getLongitude());
        mLastLocation = location;
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        mapsHelper.AnimateCamera(locationModels, getZoomValue(seekBarValue), mMap, mLastLocation, seekBarValue);
        Log.v(TAG, "Animating through Callback ");
    }

    private int getZoomValue(int seekBarValue) {
        return 12 - seekBarValue;
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

        Log.d(TAG, "onRequestPermissionsResult: rc = " + requestCode + " l rc = " + LOCATION_REQUEST_CODE + "result = " + grantResults[0]);
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Task<LocationSettingsResponse> task = locationManager.ManageLocation();
                    if (task != null) {
                        task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                            @Override
                            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                                Log.v(TAG, "On Task Complete");
                                if (task.isSuccessful()) {
                                    Log.v(TAG, "Task is Successful");
                                    progressDialog.setMessage("Getting Current Location Coordinates");
                                    progressDialog.show();
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
                        mMap.setMyLocationEnabled(true);
                    } else {
                        locationManager.ShowDialogOnPermissionDenied("Location Permission denied !\nHotspot Locator will not work without location access.\n\nDo you want to grant location acces ?");
                    }
                    return;
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "Result Code = " + Integer.toString(resultCode) + "Request code = " + requestCode + " connection code = " + CONNECTION_FAILURE_RESOLUTION_REQUEST);
        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);

        switch (requestCode) {
            case CONNECTION_FAILURE_RESOLUTION_REQUEST:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        Log.v(TAG, "Resolution success");
                        locationManager.requestLocationUpdates(mMap);
                        manageNoLocationLayout(false);
                        progressDialog.setMessage("Turning Location On\nPlease wait..");
                        progressDialog.show();
                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        // The user was asked to change settings, but chose not to
                        Log.v(TAG, "Resolution denied");
                        Helper.getInstance().showFancyAlertDialog(getActivity(),
                                "LOCATION OFF!\nUnable to get nearby locations without location access.",
                                "Nearby Locations",
                                "OK",
                                null,
                                null,
                                null,
                                FancyAlertDialogType.ERROR);

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

