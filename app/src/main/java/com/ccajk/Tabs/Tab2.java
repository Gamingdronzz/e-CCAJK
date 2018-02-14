package com.ccajk.Tabs;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.content.res.Configuration;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ccajk.Adapter.RecyclerViewAdapterHotspotLocation;
import com.ccajk.Models.LocationBuilder;
import com.ccajk.Models.LocationModel;
import com.ccajk.R;

import java.util.ArrayList;


//Our class extending fragment
public class Tab2 extends Fragment implements LocationListener {
    RecyclerView recyclerView;

    //Button getLocation;
    LocationManager locationManager;
    RecyclerViewAdapterHotspotLocation adapter;
    ArrayList<LocationModel> allLocations = new ArrayList<>();

    private final int LOCATION_REQUEST_CODE = 101;

    //Overriden method onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_2, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        allLocations = new ArrayList<>();
        allLocations = getLocationList();


        adapter = new RecyclerViewAdapterHotspotLocation(allLocations);

        recyclerView = view.findViewById(R.id.recyclerview_locations);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.getItemAnimator().setAddDuration(1000);
        getLocationList();

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && gestureDetector.onTouchEvent(e)) {
                    int pos = rv.getChildAdapterPosition(child);
                    LocationModel location = (LocationModel) allLocations.get(pos);
                    // String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%s", lat, log ,Uri.encode(list.get(pos)));
                    Uri uri = Uri.parse("geo:0,0?q=" + (location.getLatitude() + "," + location.getLongitude() + "(" + Uri.encode(location.getLocationName()) + ")"));
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    getContext().startActivity(intent);
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

       /* getLocation = view.findViewById(R.id.btn_location);
        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();

            }
        });*/

        getCurrentLocation();

    }


    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this.getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
        } else {
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 2f, this);
        }


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
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5f, this);
            }
        }
    }

    private ArrayList<LocationModel> getLocationList() {
        allLocations = new ArrayList<>();

        allLocations.add(new LocationBuilder()
                .setLatitude(32.7253156)
                .setLatitude(74.8412983)
                .setLocationName("NCC Directorate J&K")
                .createLocation());
        return allLocations;
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        // Check for the rotation
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this.getContext(), "LANDSCAPE", Toast.LENGTH_SHORT).show();
            setupGridLayout(true);

        } else if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this.getContext(), "PORTRAIT", Toast.LENGTH_SHORT).show();
            /*if (isTab) {
                setupGridLayout(true);
            } else {
                setupGridLayout(false);
            }*/


        }
    }


    private void setupGridLayout(boolean multiColumn) {
        if (multiColumn) {
            GridLayoutManager manager = new GridLayoutManager(this.getContext(), 2);
           /* manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {

                }
            });*/
            recyclerView.setLayoutManager(manager);
        } else {
            GridLayoutManager manager = new GridLayoutManager(this.getContext(), 1);
            recyclerView.setLayoutManager(manager);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

        }
    }


}