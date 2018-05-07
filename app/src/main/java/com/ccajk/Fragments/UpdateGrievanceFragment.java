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
import com.ccajk.Models.GrievanceModel;
import com.ccajk.Providers.GrievanceDataProvider;
import com.ccajk.R;
import com.ccajk.Tabs.UpdateGrievance.TabResolved;
import com.ccajk.Tabs.UpdateGrievance.TabSubmitted;
import com.ccajk.Tabs.UpdateGrievance.TabUnderProcess;
import com.ccajk.Tools.ConnectionUtility;
import com.ccajk.Tools.FireBaseHelper;
import com.ccajk.Tools.Helper;
import com.ccajk.Tools.Preferences;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class UpdateGrievanceFragment extends Fragment {

    RelativeLayout relativeLayoutNoInternet;
    LinearLayout linearLayoutTab;
    public TabLayout tabLayout;
    public ViewPager viewPager;
    ImageButton imageButtonRefresh;
    ProgressDialog progressDialog;

    public final static int INT_UPDATE_GRIEVANCE_TAB_ITEMS = 3;
    ArrayList<GrievanceModel> allGrievances;
    ArrayList<GrievanceModel> submittedGrievances;
    ArrayList<GrievanceModel> processingGrievances;
    ArrayList<GrievanceModel> resolvedGrievances;
    String TAG = "UpdateGrievanceFragment";


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

    private void showNoInternetConnectionLayout(boolean show) {
        if (show) {
            relativeLayoutNoInternet.setVisibility(View.VISIBLE);
            linearLayoutTab.setVisibility(View.GONE);
        } else {
            relativeLayoutNoInternet.setVisibility(View.GONE);
            linearLayoutTab.setVisibility(View.VISIBLE);
        }
    }

    void bindViews(View view) {
        tabLayout = view.findViewById(R.id.tab_update_grievances);
        viewPager = view.findViewById(R.id.viewpager_update_grievance);
        relativeLayoutNoInternet = view.findViewById(R.id.layout_no_internet_update_grievance_fragment);
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
        checkConnection();
    }

    private void checkConnection() {
        ConnectionUtility connectionUtility = new ConnectionUtility(new OnConnectionAvailableListener() {
            @Override
            public void OnConnectionAvailable() {
                if (GrievanceDataProvider.getInstance().getAllGrievanceList() == null) {
                    progressDialog.setMessage("Getting Grievances");
                    getData();
                } else
                    progressDialog.dismiss();
                setTabLayout();

                showNoInternetConnectionLayout(false);
            }

            @Override
            public void OnConnectionNotAvailable() {
                progressDialog.dismiss();
                showNoInternetConnectionLayout(true);
            }
        });
        connectionUtility.checkConnectionAvailability();
    }

    private void getData() {
        allGrievances = new ArrayList<>();
        submittedGrievances = new ArrayList<>();
        processingGrievances = new ArrayList<>();
        resolvedGrievances = new ArrayList<>();
        DatabaseReference dbref = FireBaseHelper.getInstance().databaseReference;
        dbref.child(FireBaseHelper.getInstance().ROOT_GRIEVANCES)
                .child(Preferences.getInstance().getStringPref(getContext(), Preferences.PREF_STATE))
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot.getValue() != null) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                GrievanceModel grievanceModel = ds.getValue(GrievanceModel.class);
                                allGrievances.add(grievanceModel);
                                if (grievanceModel.getGrievanceStatus() == 0) {
                                    submittedGrievances.add(grievanceModel);
                                } else if (grievanceModel.getGrievanceStatus() == 1) {
                                    processingGrievances.add(grievanceModel);
                                } else {
                                    resolvedGrievances.add(grievanceModel);
                                }
                            }
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
        dbref.child(FireBaseHelper.getInstance().ROOT_GRIEVANCES)
                .child(Preferences.getInstance().getStringPref(getContext(), Preferences.PREF_STATE))
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                GrievanceDataProvider.getInstance().setAllGrievanceList(allGrievances);
                GrievanceDataProvider.getInstance().setSubmittedGrievanceList(submittedGrievances);
                GrievanceDataProvider.getInstance().setProcessingGrievanceList(processingGrievances);
                GrievanceDataProvider.getInstance().setResolvedGrievanceList(resolvedGrievances);
                setTabLayout();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
