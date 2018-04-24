package com.ccajk.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import com.ccajk.Models.LocationModel;
import com.ccajk.R;
import com.ccajk.Tabs.TabAllLocations;
import com.ccajk.Tabs.TabNearby;
import com.ccajk.Tools.FireBaseHelper;

import java.util.ArrayList;
import java.util.List;

import static com.ccajk.Tools.MyLocationManager.CONNECTION_FAILURE_RESOLUTION_REQUEST;
import static com.ccajk.Tools.MyLocationManager.LOCATION_REQUEST_CODE;

/**
 * Created by balpreet on 4/20/2018.
 */

public class LocatorFragment extends Fragment {

    public TabLayout tabLayout;
    public ViewPager viewPager;
    ArrayList<LocationModel> locationModelArrayList = new ArrayList<>();
    public static int int_items = 2;
    String TAG = "locator";

    public LocatorFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_locator_layout, container, false);
        if(savedInstanceState == null)
        {
            Log.d(TAG, "onCreateView: first time");
        }
        else
        {
            Log.d(TAG, "onCreateView: from restart");
        }
        String locatorType = getArguments().getString("Locator");
        locationModelArrayList = getLocations(locatorType);
        bindViews(view);
        Log.d(TAG, "onCreateView: " + locatorType);
        return view;

    }

    private ArrayList<LocationModel> getLocations(String locatorType) {
        return FireBaseHelper.getInstance().getLocationModels("jnk");
    }

    private void bindViews(View view) {
        tabLayout = view.findViewById(R.id.tab_locator);
        viewPager = view.findViewById(R.id.viewpager_locator);
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
    }

    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            //Bundle bundle = new Bundle();
            //bundle.putStringArray("AllLocations", (String[]) locationModelArrayList.toArray());
            switch (position) {
                case 0:
                    TabAllLocations tabAllLocations = new TabAllLocations();
                    //tabAllLocations.setArguments(bundle);
                    return tabAllLocations;
                case 1:
                    TabNearby tabNearby = new TabNearby();
                    //tabNearby.setArguments(bundle);
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
