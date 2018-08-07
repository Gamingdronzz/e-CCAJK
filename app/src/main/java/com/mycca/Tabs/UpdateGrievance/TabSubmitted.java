package com.mycca.Tabs.UpdateGrievance;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mycca.activity.MainActivity;
import com.mycca.adapter.RecyclerViewAdapterGrievanceUpdate;
import com.mycca.custom.Progress.ProgressDialog;
import com.mycca.Models.GrievanceModel;
import com.mycca.R;
import com.mycca.Tools.FireBaseHelper;
import com.mycca.Tools.Helper;
import com.mycca.Tools.Preferences;

import java.util.ArrayList;

public class TabSubmitted extends Fragment {

    final String TAG = "Submitted";

    RecyclerView recyclerView;
    RelativeLayout relativeLayoutEmptyList;
    TextView textViewNoListInfo;
    ProgressDialog progressDialog;
    ArrayList<GrievanceModel> submittedGrievances;

    RecyclerViewAdapterGrievanceUpdate adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_grievances, container, false);
        bindViews(view);
        showEmptyListLayout(true);
        init();
        return view;
    }

    private void showEmptyListLayout(boolean show) {
        if (show) {
            recyclerView.setVisibility(View.GONE);
            relativeLayoutEmptyList.setVisibility(View.VISIBLE);
            if (getActivity() != null && isAdded())
                textViewNoListInfo.setText(R.string.no_grievances_submitted);
        } else {
            relativeLayoutEmptyList.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void bindViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_grievances);
        relativeLayoutEmptyList = view.findViewById(R.id.layout_empty_list);
        textViewNoListInfo = view.findViewById(R.id.textview_info_tab_grievances);
        progressDialog = Helper.getInstance().getProgressWindow(getActivity(), "Fetching currently submitted Grievances\nPlease Wait..");
    }

    private void init() {
        progressDialog.show();
        submittedGrievances = new ArrayList<>();
        Log.d(TAG, "init: " + submittedGrievances);
        adapter = new RecyclerViewAdapterGrievanceUpdate(submittedGrievances, (MainActivity) getActivity(), false);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        getData();
    }

    private void getData() {

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        GrievanceModel grievanceModel = ds.getValue(GrievanceModel.class);
                        if (grievanceModel != null && grievanceModel.isSubmissionSuccess()) {
                            if (grievanceModel.getGrievanceStatus() == 0) {
                                submittedGrievances.add(grievanceModel);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                ArrayList<GrievanceModel> temp=new ArrayList<>();
                Log.d(TAG, "\nonChildChanged: Arraylist: " + submittedGrievances);
                if (dataSnapshot.getValue() != null) {
                    String code = dataSnapshot.getKey();
                    for (GrievanceModel model : submittedGrievances) {
                        if (model.getPensionerIdentifier().equals(code))
                            temp.add(model);
                    }
                    submittedGrievances.removeAll(temp);
                    Log.d(TAG, "\nonChildChanged: Arraylist after removal " + submittedGrievances);
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        GrievanceModel grievanceModel = ds.getValue(GrievanceModel.class);
                        if (grievanceModel != null) {
                            if (grievanceModel.getGrievanceStatus() == 0)
                                submittedGrievances.add(grievanceModel);
                        }
                    }
                    Log.d(TAG, "\nonChildChanged: Arraylist after addition " + submittedGrievances);
                    adapter = new RecyclerViewAdapterGrievanceUpdate(submittedGrievances, (MainActivity) getActivity(), false);
                    recyclerView.setAdapter(adapter);
                }
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
        };

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                if (submittedGrievances.size() > 0) {
                    showEmptyListLayout(false);
                } else
                    showEmptyListLayout(true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        FireBaseHelper.getInstance(getActivity()).getDataFromFirebase(childEventListener,
                FireBaseHelper.VERSIONED,
                FireBaseHelper.ROOT_GRIEVANCES,
                Preferences.getInstance().getStaffPref(getContext(), Preferences.PREF_STAFF_DATA).getState());
        FireBaseHelper.getInstance(getActivity()).getDataFromFirebase(valueEventListener,
                FireBaseHelper.VERSIONED,
                false,
                FireBaseHelper.ROOT_GRIEVANCES,
                Preferences.getInstance().getStaffPref(getContext(), Preferences.PREF_STAFF_DATA).getState());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RecyclerViewAdapterGrievanceUpdate.REQUEST_UPDATE: {
                if (resultCode == Activity.RESULT_OK) {
                    init();
                }

            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: back to tab : Activity= " + getActivity());
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {

    }
}