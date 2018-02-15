package com.ccajk.Tabs;



import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.ccajk.Activity.MapsActivity;
import com.ccajk.Models.LocationModel;
import com.ccajk.R;

import java.util.ArrayList;

//Our class extending fragment
public class TabNearby extends Fragment implements LocationListener {

    private final int LOCATION_REQUEST_CODE = 101;
    LocationManager locationManager;
    ArrayList<LocationModel> allLocations = new ArrayList<>();
    SeekBar seekBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_nearby_locations, container, false);
        init(view);
        return view;
    }

    private void init(View view)
    {
        Button button = view.findViewById(R.id.openMaps);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MapsActivity.class);
                v.getContext().startActivity(intent);
            }
        });

        seekBar=view.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(getContext(),"radius = "+String.valueOf(seekBar.getProgress()+10),Toast.LENGTH_SHORT).show();

            }
        });
        getCurrentLocation();
    }


    private void getCurrentLocation() {


        if (ContextCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                requestLocationPermission();

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            AccessLocation();
        }


    }

    @SuppressLint("MissingPermission")
    private void AccessLocation()
    {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 2f, this);
        Log.d("Nearby","Location Requested");
    }

    private void requestLocationPermission()
    {
        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("latitude ", String.valueOf(location.getLatitude()));
        Log.d("longitude ", String.valueOf(location.getLongitude()));

        float[] result = new float[1];
        for (int i = 0; i < allLocations.size(); i++) {
            Location.distanceBetween(location.getLatitude(), location.getLongitude(),allLocations.get(i).getLatitude(), allLocations.get(i).getLongitude(), result);
            if (result[0] < 2000) {
                //Log.v("Hotspot", "Distance " + i + " is " + result[0]);
            }
            /*if (Helper.distance(location.getLatitude(), location.getLongitude(), latitude.get(i), longitude.get(i)) < 2000) { // if distance < 0.1 miles we take allLocations as equal

            }*/
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AccessLocation();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
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
}