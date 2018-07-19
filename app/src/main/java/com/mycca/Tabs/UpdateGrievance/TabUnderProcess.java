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
import com.mycca.Activity.MainActivity;
import com.mycca.Adapter.RecyclerViewAdapterGrievanceUpdate;
import com.mycca.CustomObjects.Progress.ProgressDialog;
import com.mycca.Models.GrievanceModel;
import com.mycca.R;
import com.mycca.Tools.FireBaseHelper;
import com.mycca.Tools.Helper;
import com.mycca.Tools.Preferences;

import java.util.ArrayList;

public class TabUnderProcess extends Fragment {

    final String TAG = "UnderProcess";

    RecyclerView recyclerView;
    RelativeLayout relativeLayoutEmptyList;
    TextView textViewNoListInfo;
    ProgressDialog progressDialog;

    RecyclerViewAdapterGrievanceUpdate adapter;
    ArrayList<GrievanceModel> processingGrievances;

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
            relativeLayoutEmptyList.setVisibility(View.VISIBLE);
            textViewNoListInfo.setText(R.string.no_grievances_processing);
            recyclerView.setVisibility(View.GONE);
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
        processingGrievances = new ArrayList<>();
        Log.d(TAG, "init: " + processingGrievances);
        adapter = new RecyclerViewAdapterGrievanceUpdate(processingGrievances, (MainActivity) getActivity(), false);
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
                            if (grievanceModel.getGrievanceStatus() == 1) {
                                processingGrievances.add(grievanceModel);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                processingGrievances = new ArrayList<>();
                Log.d(TAG, "ChildChanged: ");
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        GrievanceModel grievanceModel = ds.getValue(GrievanceModel.class);
                        if (grievanceModel != null && grievanceModel.isSubmissionSuccess()) {
                            if (grievanceModel.getGrievanceStatus() == 1) {
                                processingGrievances.add(grievanceModel);
                            }
                        }
                    }
                    adapter = new RecyclerViewAdapterGrievanceUpdate(processingGrievances, (MainActivity) getActivity(), false);
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
                if (processingGrievances.size() > 0) {
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
                    //refresh();
                    init();
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {

    }

}