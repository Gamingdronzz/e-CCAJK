package com.ccajk.Fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ccajk.R;
import com.ccajk.Tabs.TabAllLocations;
import com.ccajk.Tabs.TabNearby;

import java.util.List;

public class HotspotLocationFragment extends Fragment {

    public TabLayout tabLayout;
    public ViewPager viewPager;
    public static int int_items = 2;


    public HotspotLocationFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hotspot_tab_layout, container, false);
        tabLayout = view.findViewById(R.id.tabs);
        viewPager = view.findViewById(R.id.viewpager);

        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

        return view;
    }

    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new TabNearby();
                case 1:
                    return new TabAllLocations();
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
                case 0:
                    return "NEARBY";
                case 1:
                    return "ALL HOTSPOTS";
            }
            return null;
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {


        List<Fragment> nearby = getChildFragmentManager().getFragments();
        for (Fragment frag :
                nearby) {
            frag.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
//                if (permissions.length == 1 &&
//                        permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
//                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    //createLocationRequest();
//                    //requestLocationUpdates();
//                } else {
//
//                }
        return;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult()", Integer.toString(resultCode));


        List<Fragment> nearby = getChildFragmentManager().getFragments();
        for (Fragment frag :
                nearby) {
            frag.onActivityResult(requestCode, resultCode, data);
        }
//                if (permissions.length == 1 &&
//                        permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
//                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    //createLocationRequest();
//                    //requestLocationUpdates();
//                } else {
//
//                }
        return;
    }

}
