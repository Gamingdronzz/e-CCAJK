package com.ccajk.Tabs;


import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.ccajk.Tools.Helper;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


//Our class extending fragment
public class TabAllLocations extends Fragment {
    RecyclerView recyclerView;

    private ArrayList<LatLng> markers = new ArrayList<>();
    private ArrayList<String> names = new ArrayList<>();
    Helper helper = new Helper();
    RecyclerViewAdapterHotspotLocation adapter;
    ArrayList<LocationModel> allLocations = new ArrayList<>();


    //Overriden method onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_all_locations, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        markers = helper.getMarkers();
        names = helper.getLocationNames();

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

        //getCurrentLocation();

    }


    private ArrayList<LocationModel> getLocationList() {
        allLocations = new ArrayList<>();
        for (int i = 0; i < markers.size(); i++) {
            LatLng latLng=markers.get(i);
            allLocations.add(new LocationBuilder()
                    .setLatitude(latLng.latitude)
                    .setLongitude(latLng.longitude)
                    .setLocationName(names.get(i))
                    .createLocation());
        }
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


}