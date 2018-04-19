package com.ccajk.Fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ccajk.R;
import com.ccajk.Tabs.TabAllLocations;
import com.ccajk.Tabs.TabNearby;

/**
 * Created by balpreet on 4/20/2018.
 */

public class LocatorFragment extends Fragment {

    public TabLayout tabLayout;
    public ViewPager viewPager;
    Bundle args;
    public LocatorFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_locator_layout, container, false);
        bindViews(view);
        args = getArguments();
        return view;

    }

    private void bindViews(View view)
    {
        tabLayout = view.findViewById(R.id.tabs);
        viewPager = view.findViewById(R.id.viewpager);
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
    }

    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    TabNearby tabNearby = new TabNearby();
                    return tabNearby;
                case 1:
                    TabAllLocations tabAllLocations = new TabAllLocations();
                    return tabAllLocations;
            }
            return null;
        }

        @Override
        public int getCount() {
            return tabLayout.getTabCount();
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return "NEARBY";
                case 1:
                    return "ALL LOCATIONS";
            }
            return null;
        }
    }
}
