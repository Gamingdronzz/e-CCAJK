package com.ccajk.Fragments;

import android.annotation.SuppressLint;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ccajk.CustomObjects.ProgressDialog;
import com.ccajk.Listeners.OnConnectionAvailableListener;
import com.ccajk.Models.LocationModel;
import com.ccajk.R;
import com.ccajk.Tabs.TabAllLocations;
import com.ccajk.Tabs.TabNearby;
import com.ccajk.Tools.ConnectionUtility;
import com.ccajk.Tools.FireBaseHelper;
import com.ccajk.Tools.Helper;
import com.ccajk.Providers.LocationDataProvider;
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

    public final static int INT_LOCATOR_TAB_ITEMS = 2;
    String TAG = "locator";
    TextView textViewLocatorInfo;
    String locatorType;
    RelativeLayout relativeLayoutNoLocation;
    LinearLayout linearLayoutTab;
    ImageButton imageButtonRefresh;
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
        manageNoLocationLayout(true);
        getLocations();
        return view;
    }

    private void bindViews(View view) {
        tabLayout = view.findViewById(R.id.tab_locator);
        viewPager = view.findViewById(R.id.viewpager_locator);
        relativeLayoutNoLocation = view.findViewById(R.id.layout_no_location_lcoator_fragment);
        linearLayoutTab = view.findViewById(R.id.linear_layout_locator_fragment);
        textViewLocatorInfo = view.findViewById(R.id.textview_locator_info);
        imageButtonRefresh = view.findViewById(R.id.image_btn_refresh_tab_all);
        progressDialog = Helper.getInstance().getProgressWindow(getActivity(), "Getting Locations...");

        imageButtonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocations();
            }
        });
        textViewLocatorInfo.setText(textViewLocatorInfo.getText() + "\nTurn On Internet and Refresh");
    }

    private void manageNoLocationLayout(boolean show) {
        if (show)
        {
            relativeLayoutNoLocation.setVisibility(View.VISIBLE);
            linearLayoutTab.setVisibility(View.GONE);
        }
        else
        {
            relativeLayoutNoLocation.setVisibility(View.GONE);
            linearLayoutTab.setVisibility(View.VISIBLE);
        }
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
//                Helper.getInstance().showAlertDialog(
//                        getContext(),
//                        "Internet Connection Not Available\nTo make full use of " + locatorType + " locator, please turn on your internet connection\n\nPress Ok after you turn on the location\nPress Cancel to go back",
//                        locatorType + " Locator", "OK",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                getLocations();
//                            }
//                        },
//                        "Cancel");
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
                            LocationModel location = dataSnapshot.getValue(LocationModel.class);
                            Log.d(TAG, "onChildAdded: " + location.getLocationName());
                            Log.d(TAG, "onChildAdded: " + location.getLatitude() + ":" + location.getLongitude());
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
            LocationDataProvider.getInstance().setGpLocationModelArrayList(locationModelArrayList);
        } else if (locatorType.equals(FireBaseHelper.getInstance().ROOT_HOTSPOTS)) {
            LocationDataProvider.getInstance().setHotspotLocationModelArrayList(locationModelArrayList);
        }

        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
        manageNoLocationLayout(false);
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
            return INT_LOCATOR_TAB_ITEMS;
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
