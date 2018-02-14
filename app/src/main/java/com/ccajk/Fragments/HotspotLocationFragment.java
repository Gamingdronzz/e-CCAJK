package com.ccajk.Fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ccajk.Adapter.RecyclerViewAdapterHotspotLocation;
import com.ccajk.R;

import java.util.ArrayList;

public class HotspotLocationFragment extends Fragment implements LocationListener {

    RecyclerView recyclerView;
    Button getLocation;
    LocationManager locationManager;
    RecyclerViewAdapterHotspotLocation adapter;
    ArrayList<String> list = new ArrayList<>();
    ArrayList<Double> latitude = new ArrayList<>();
    ArrayList<Double> longitude = new ArrayList<>();
    private final int LOCATION_REQUEST_CODE = 101;

    public HotspotLocationFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hotspot_location, container, false);

        list = new ArrayList<>();
        latitude = getLatList();
        longitude = getLongList();

        adapter = new RecyclerViewAdapterHotspotLocation(list);

        recyclerView = view.findViewById(R.id.locations);
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
                    double lat = latitude.get(pos);
                    double log = longitude.get(pos);
                    // String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%s", lat, log ,Uri.encode(list.get(pos)));
                    Uri uri = Uri.parse("geo:0,0?q=" + lat + "," + log + "(" + Uri.encode(list.get(pos)) + ")");
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

        getLocation = view.findViewById(R.id.btn_location);
        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();

            }
        });
        return view;
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this.getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
        }
        else
        {
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5f,this);
        }


    }

    private ArrayList<Double> getLatList() {
        latitude.add(32.7253156);
        latitude.add(32.712113);
        latitude.add(34.0621045);
        latitude.add(32.7002194);
        latitude.add(32.7051315);
        latitude.add(32.7051315);
        latitude.add(33.76753);
        latitude.add(32.9837178);
        latitude.add(32.7857434);
        return latitude;
    }

    private ArrayList<Double> getLongList() {
        longitude.add(74.8412983);
        longitude.add(74.8622234);
        longitude.add(74.8019077);
        longitude.add(74.8644794);
        longitude.add(74.8631721);
        longitude.add(74.8631721);
        longitude.add(74.1000921);
        longitude.add(75.7103464);
        longitude.add(74.9214256);
        return longitude;
    }

    private ArrayList<String> getLocationList() {
        list.add("NCC Directorate, J&K");
        adapter.notifyItemInserted(0);
        list.add("Group Headquarters, Jammu");
        adapter.notifyItemInserted(1);
        list.add("Group Headquarters, Srinagar");
        adapter.notifyItemInserted(2);
        list.add("2 J&K Battalion NCC Jammu");
        adapter.notifyItemInserted(3);
        list.add("2 J&K Girls Battalion NCC Jammu");
        adapter.notifyItemInserted(4);
        list.add("1 J&K Armed Squadron NCC Jammu");
        adapter.notifyItemInserted(5);
        list.add("1 J&K Artillery Battalion NCC  Poonch");
        adapter.notifyItemInserted(6);
        list.add("2 J&K (I) Coy  NCC Bhaderwah");
        adapter.notifyItemInserted(7);
        list.add("JD (I) Coy NCC  SS Nagrota ");
        adapter.notifyItemInserted(8);
        return list;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("latitude ", String.valueOf(location.getLatitude()));
        Log.d("longitude ", String.valueOf(location.getLongitude()));
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
}
