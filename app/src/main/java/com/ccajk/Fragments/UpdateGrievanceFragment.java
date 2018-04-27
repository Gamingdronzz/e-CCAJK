package com.ccajk.Fragments;


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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ccajk.CustomObjects.ProgressDialog;
import com.ccajk.Listeners.OnConnectionAvailableListener;
import com.ccajk.R;
import com.ccajk.Tabs.UpdateGrievance.TabResolved;
import com.ccajk.Tabs.UpdateGrievance.TabSubmitted;
import com.ccajk.Tabs.UpdateGrievance.TabUnderProcess;
import com.ccajk.Tools.ConnectionUtility;
import com.ccajk.Tools.Helper;

import java.util.List;


public class UpdateGrievanceFragment extends Fragment {
    public TabLayout tabLayout;
    public ViewPager viewPager;
    ProgressDialog progressDialog;

    public final static int INT_UPDATE_GRIEVANCE_TAB_ITEMS = 3;
    RelativeLayout relativeLayoutNoLocation;
    LinearLayout linearLayoutTab;
    String TAG = "UpdateGrievanceFragment";
    ImageButton imageButtonRefresh;

    public UpdateGrievanceFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update, container, false);
        bindViews(view);
        showNoInternetConnectionLayout(true);
        init();
        return view;
    }

    void bindViews(View view) {
        tabLayout = view.findViewById(R.id.tab_update_grievances);
        viewPager = view.findViewById(R.id.viewpager_update_grievance);
        relativeLayoutNoLocation = view.findViewById(R.id.layout_no_internet_update_grievance_fragment);
        linearLayoutTab = view.findViewById(R.id.linear_layout_update_grievance_fragment);
        imageButtonRefresh = view.findViewById(R.id.image_btn_refresh_update_grievance_fragment);
        imageButtonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init();
            }
        });
        progressDialog = Helper.getInstance().getProgressWindow(getActivity(), "Checking for Intenet Connectivity...");
    }

    private void init() {
        progressDialog.show();
        ConnectionUtility connectionUtility = new ConnectionUtility(new OnConnectionAvailableListener() {
            @Override
            public void OnConnectionAvailable() {
                setTabLayout();
                showNoInternetConnectionLayout(false);
                progressDialog.dismiss();
            }

            @Override
            public void OnConnectionNotAvailable() {
                progressDialog.dismiss();
                showNoInternetConnectionLayout(true);
            }
        });
        connectionUtility.checkConnectionAvailability();
    }

    private void showNoInternetConnectionLayout(boolean show) {
        if (show) {
            relativeLayoutNoLocation.setVisibility(View.VISIBLE);
            linearLayoutTab.setVisibility(View.GONE);
        } else {
            relativeLayoutNoLocation.setVisibility(View.GONE);
            linearLayoutTab.setVisibility(View.VISIBLE);
        }
    }

    private void setTabLayout() {

        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
        viewPager.setOffscreenPageLimit(3);
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
                    TabSubmitted tabSubmitted = new TabSubmitted();
                    return tabSubmitted;
                case 1:
                    TabUnderProcess tabUnderProcess = new TabUnderProcess();
                    return tabUnderProcess;
                case 2:
                    TabResolved tabResolved = new TabResolved();
                    return tabResolved;
            }
            return null;
        }

        @Override
        public int getCount() {
            return INT_UPDATE_GRIEVANCE_TAB_ITEMS;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return "SUBMITTED";
                case 1:
                    return "UNDER PROCESS";
                case 2:
                    return "RESOLVED";

            }
            return null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> allFragments = getChildFragmentManager().getFragments();

        for (Fragment frag : allFragments) {
            Log.d(TAG, "onRequestPermissionsResult: " + frag.toString());
            frag.onActivityResult(requestCode, resultCode, data);
        }
    }
}
