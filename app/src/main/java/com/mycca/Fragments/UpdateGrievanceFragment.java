package com.mycca.Fragments;


import android.content.Intent;
import android.content.res.Resources;
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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mycca.Activity.MainActivity;
import com.mycca.CustomObjects.FancyShowCase.FancyShowCaseQueue;
import com.mycca.CustomObjects.FancyShowCase.FancyShowCaseView;
import com.mycca.CustomObjects.Progress.ProgressDialog;
import com.mycca.Listeners.OnConnectionAvailableListener;
import com.mycca.Models.GrievanceModel;
import com.mycca.Providers.GrievanceDataProvider;
import com.mycca.R;
import com.mycca.Tabs.UpdateGrievance.TabResolved;
import com.mycca.Tabs.UpdateGrievance.TabSubmitted;
import com.mycca.Tabs.UpdateGrievance.TabUnderProcess;
import com.mycca.Tools.ConnectionUtility;
import com.mycca.Tools.FireBaseHelper;
import com.mycca.Tools.Helper;
import com.mycca.Tools.Preferences;

import java.util.ArrayList;
import java.util.List;


public class UpdateGrievanceFragment extends Fragment {

    public TabLayout tabLayout;
    public ViewPager viewPager;
    RelativeLayout relativeLayoutNoInternet;
    LinearLayout linearLayoutTab;
    ImageButton imageButtonRefresh;
    ProgressDialog progressDialog;
    MainActivity activity;

    public final static int INT_UPDATE_GRIEVANCE_TAB_ITEMS = 3;
    String TAG = "UpdateGrievanceFragment";
    ArrayList<GrievanceModel> allGrievances;
    ArrayList<GrievanceModel> submittedGrievances;
    ArrayList<GrievanceModel> processingGrievances;
    ArrayList<GrievanceModel> resolvedGrievances;


    public UpdateGrievanceFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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
        imageButtonRefresh.setOnClickListener(v -> init());
    }

    private void init() {
        activity = (MainActivity) getActivity();
        progressDialog = Helper.getInstance().getProgressWindow(activity, "Checking for Intenet Connectivity...");
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
        DatabaseReference dbref = FireBaseHelper.getInstance(getContext()).versionedDbRef;
        dbref.child(FireBaseHelper.ROOT_GRIEVANCES)
                .child(Preferences.getInstance().getStaffPref(getContext(), Preferences.PREF_STAFF_DATA).getState())
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
        dbref.child(FireBaseHelper.ROOT_GRIEVANCES)
                .child(Preferences.getInstance().getStaffPref(getContext(), Preferences.PREF_STAFF_DATA).getState())
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
        final MyAdapter adapter = new MyAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);
        if (Preferences.getInstance().getBooleanPref(getContext(), Preferences.PREF_HELP_UPDATE)) {
            showTutorial();
            Preferences.getInstance().setBooleanPref(getContext(), Preferences.PREF_HELP_UPDATE, false);
        }
    }

    private void showTutorial() {

        final FancyShowCaseView fancyShowCaseView1 = new FancyShowCaseView.Builder(activity)
                .title("These are submitted grievances")
                .focusCircleAtPosition(Resources.getSystem().getDisplayMetrics().widthPixels / 6, Resources.getSystem().getDisplayMetrics().heightPixels / 6, 150)
                .build();

        final FancyShowCaseView fancyShowCaseView2 = new FancyShowCaseView.Builder(activity)
                .title("-------->\nSwipe to view Greivances Under process")
                .focusCircleAtPosition(Resources.getSystem().getDisplayMetrics().widthPixels / 2, Resources.getSystem().getDisplayMetrics().heightPixels / 6, 150)
                .build();

        final FancyShowCaseView fancyShowCaseView3 = new FancyShowCaseView.Builder(activity)
                .title("-------->\nSwipe again to view Resolved Greivances")
                .focusCircleAtPosition(Resources.getSystem().getDisplayMetrics().widthPixels * 5 / 6, Resources.getSystem().getDisplayMetrics().heightPixels / 6, 150)
                .build();

        activity.setmQueue(new FancyShowCaseQueue()
                .add(fancyShowCaseView1)
                .add(fancyShowCaseView2)
                .add(fancyShowCaseView3));

        activity.getmQueue().setCompleteListener(() -> activity.setmQueue(null));

        activity.getmQueue().show();
    }

    class MyAdapter extends FragmentPagerAdapter {

        MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new TabSubmitted();
                case 1:
                    return new TabUnderProcess();
                case 2:
                    return new TabResolved();
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
