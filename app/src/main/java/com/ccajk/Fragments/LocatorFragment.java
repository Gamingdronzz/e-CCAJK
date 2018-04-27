package com.ccajk.Fragments;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ccajk.CustomObjects.ProgressDialog;
import com.ccajk.Listeners.OnConnectionAvailableListener;
import com.ccajk.Models.LocationModel;
import com.ccajk.R;
import com.ccajk.Tabs.TabAllLocations;
import com.ccajk.Tabs.TabNearby;
import com.ccajk.Tools.ConnectionUtility;
import com.ccajk.Tools.FireBaseHelper;
import com.ccajk.Tools.Helper;
import com.ccajk.Tools.LocationDataProvider;
import com.ccajk.Tools.Preferences;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by balpreet on 4/20/2018.
 */

public class LocatorFragment extends Fragment {

    public TabLayout tabLayout;
    public ViewPager viewPager;
    ProgressDialog progressDialog;
    public ArrayList<LocationModel> locationModelArrayList = new ArrayList<>();

    public static int int_items = 2;
    String TAG = "locator";
    String locatorType;

    public LocatorFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_locator_layout, container, false);
        if (savedInstanceState == null) {
            Log.d(TAG, "onCreateView: first time");
        } else {
            Log.d(TAG, "onCreateView: from restart");
        }

        locatorType = getArguments().getString("Locator");
        bindViews(view);
        getLocations();
        return view;
    }

    private void bindViews(View view) {
        tabLayout = view.findViewById(R.id.tab_locator);
        viewPager = view.findViewById(R.id.viewpager_locator);
        progressDialog = Helper.getInstance().getProgressWindow(getActivity(), "Getting Locations...");
    }

    private void getLocations() {
        progressDialog.show();
        ConnectionUtility connectionUtility = new ConnectionUtility(new OnConnectionAvailableListener() {
            @Override
            public void OnConnectionAvailable() {
                fetchLocations();
            }

            @Override
            public void OnConnectionNotAvailable() {
                progressDialog.dismiss();
                Helper.getInstance().showAlertDialog(
                        getContext(),
                        "Internet Connection Not Available\nTo make full use of " + locatorType + " locator, please turn on your internet connection\n\nPress Ok after you turn on the location\nPress Cancel to go back",
                        locatorType + " Locator", "OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getLocations();
                            }
                        },
                        "Cancel");
            }
        });
        connectionUtility.CheckConnectionAvailability();

    }

    private void fetchLocations() {
        progressDialog.show();
        DatabaseReference databaseReference = FireBaseHelper.getInstance().databaseReference;
        databaseReference.child(locatorType)
                .child(Preferences.getInstance().getStringPref(getContext(), Preferences.PREF_STATE))
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot.getValue() != null) {
                            Log.d(TAG, "onChildAdded: " + dataSnapshot.getKey());
                            LocationModel location = dataSnapshot.getValue(LocationModel.class);
                            locationModelArrayList.add(location);
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setTabLayout();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setTabLayout() {
        Log.d(TAG, "setTabLayout: " + locatorType + " - " + locationModelArrayList.size());
        if (locatorType.equals(FireBaseHelper.getInstance().ROOT_GP)) {
            Log.d(TAG, "setTabLayout: GP");
            LocationDataProvider.getInstance().setGpLocationModelArrayList(locationModelArrayList);
        } else if (locatorType.equals(FireBaseHelper.getInstance().ROOT_HOTSPOTS)) {
            Log.d(TAG, "setTabLayout: Hotspot");
            LocationDataProvider.getInstance().setHotspotLocationModelArrayList(locationModelArrayList);
        }
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);


        progressDialog.dismiss();
    }

    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    TabAllLocations tabAllLocations = new TabAllLocations();
                    tabAllLocations.setArguments(getArguments());
                    return tabAllLocations;
                case 1:
                    TabNearby tabNearby = new TabNearby();
                    tabNearby.setArguments(getArguments());
                    return tabNearby;

            }
            return null;
        }

        @Override
        public int getCount() {
            return int_items;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 1:
                    return "NEARBY";
                case 0:
                    return "ALL LOCATIONS";
            }
            return null;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> allFragments = getChildFragmentManager().getFragments();

        for (Fragment frag : allFragments) {
            Log.d(TAG, "onRequestPermissionsResult: " + frag.toString());
            frag.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> allFragments = getChildFragmentManager().getFragments();

        for (Fragment frag : allFragments) {
            Log.d(TAG, "onActivityResult: " + frag.toString());
            frag.onActivityResult(requestCode, resultCode, data);
        }


    }
}
