package com.ccajk.Tabs;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.ccajk.Models.LocationModel;
import com.ccajk.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

//Our class extending fragment
public class TabNearby extends Fragment implements LocationListener, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback {
    private GoogleMap mMap;
    private final int LOCATION_REQUEST_CODE = 101;
    LocationManager locationManager;
    ArrayList<LocationModel> allLocations = new ArrayList<>();
    SeekBar seekBar;
    AppCompatActivity appCompatActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_nearby_locations, container, false);
        init(view);
        return view;
    }

    public void setActivity(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }

    private void init(View view) {
      /*  Button button = view.findViewById(R.id.openMaps);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MapsActivity.class);
                v.getContext().startActivity(intent);
            }
        });*/

        seekBar = view.findViewById(R.id.seekBar);
        seekBar.setMax(3);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int radius = seekBar.getProgress() + 15;
                Toast.makeText(getContext(), "New Radius = " + radius, Toast.LENGTH_SHORT).show();
                ArrayList<LatLng> markers = new ArrayList<>();
                markers.add(new LatLng(32.7253156, 74.84129833));
                markers.add(new LatLng(34.0621045, 74.8019077));

                ArrayList<String> names = new ArrayList<>();
                names.add("JK NCC Directorate");
                names.add("Group Headquarters");
                AnimateCamera(markers, names, radius);

            }
        });

        getCurrentLocation();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    private void getCurrentLocation() {

        if (ContextCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

            } else {
                requestLocationPermission();
            }
        } else {
            AccessLocation();
        }
    }

    @SuppressLint("MissingPermission")
    private void AccessLocation() {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 2f, this);
        Log.d("Nearby", "Location Requested");
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            requestLocationPermission();
        }
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

        ArrayList<LatLng> markers = new ArrayList<>();
        markers.add(new LatLng(32.7253156, 74.84129833));
        markers.add(new LatLng(32.712113, 74.862223));
        markers.add(new LatLng(34.0621045, 74.8019077));

        ArrayList<String> names = new ArrayList<>();
        names.add("JK NCC Directorate");
        names.add("JK NCC Directorate Srinagar");
        names.add("Group Headquarters");

        AnimateCamera(markers, names, 17);
    }


    private void AnimateCamera(ArrayList<LatLng> markers, ArrayList<String> names, int zoom) {
        mMap.clear();
        int i = 0;
        for (LatLng latlng :
                markers) {
            mMap.addMarker(new MarkerOptions().position(latlng).title(names.get(i++)));
        }
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(markers.get(0))      // Sets the center of the map to Mountain View
                .zoom(zoom)                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to east
                .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("latitude ", String.valueOf(location.getLatitude()));
        Log.d("longitude ", String.valueOf(location.getLongitude()));

        float[] result = new float[1];
        for (int i = 0; i < allLocations.size(); i++) {
            Location.distanceBetween(location.getLatitude(), location.getLongitude(), allLocations.get(i).getLatitude(), allLocations.get(i).getLongitude(), result);
            if (result[0] < 2000) {
                //Log.v("Hotspot", "Distance " + i + " is " + result[0]);
            }
            /*if (Helper.distance(location.getLatitude(), location.getLongitude(), latitude.get(i), longitude.get(i)) < 2000) { // if distance < 0.1 miles we take allLocations as equal

            }*/
        }


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
                    mMap.setMyLocationEnabled(true);
                    AccessLocation();
                }
                 else {

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this.getActivity(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this.getActivity(), "Current location:\n" + location, Toast.LENGTH_LONG).show();

    }
}