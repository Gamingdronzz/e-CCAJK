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

public class TabResolved extends Fragment {

    final String TAG = "Resolved";

    RecyclerView recyclerView;
    RelativeLayout relativeLayoutEmptyList;
    TextView textViewNoListInfo;
    ProgressDialog progressDialog;

    RecyclerViewAdapterGrievanceUpdate adapter;
    ArrayList<GrievanceModel> resolvedGrievances;

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
            recyclerView.setVisibility(View.GONE);
            if(getActivity()!=null && isAdded())
                textViewNoListInfo.setText(R.string.no_grievances_resolved);
        } else {
            relativeLayoutEmptyList.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void bindViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_grievances);
        relativeLayoutEmptyList = view.findViewById(R.id.layout_empty_list);
        textViewNoListInfo = view.findViewById(R.id.textview_info_tab_grievances);
        progressDialog = Helper.getInstance().getProgressWindow(getActivity(), "Fetching resolved Grievances\nPlease Wait..");
    }

    private void init() {
        progressDialog.show();
        resolvedGrievances = new ArrayList<>();
        Log.d(TAG, "init: " + resolvedGrievances);
        adapter = new RecyclerViewAdapterGrievanceUpdate(resolvedGrievances, (MainActivity) getActivity(), true);
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
                            if (grievanceModel.getGrievanceStatus() == 2) {
                                resolvedGrievances.add(grievanceModel);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                ArrayList<GrievanceModel> temp2=new ArrayList<>();
                Log.d(TAG, "\nonChildChanged: Arraylist" + resolvedGrievances);
                if (dataSnapshot.getValue() != null) {
                    String code=dataSnapshot.getKey();
                    for(GrievanceModel model:resolvedGrievances){
                        if(model.getPensionerIdentifier().equals(code))
                            temp2.add(model);
                    }
                    resolvedGrievances.removeAll(temp2);
                    Log.d(TAG, "\nonChildChanged: Arraylist after removal " + resolvedGrievances);
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        GrievanceModel grievanceModel = ds.getValue(GrievanceModel.class);
                        if (grievanceModel != null) {
                            if (grievanceModel.getGrievanceStatus() == 2)
                                resolvedGrievances.add(grievanceModel);
                        }
                    }
                    Log.d(TAG, "\nonChildChanged: Arraylist after addition " + resolvedGrievances);
                    adapter = new RecyclerViewAdapterGrievanceUpdate(resolvedGrievances, (MainActivity) getActivity(), false);
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
                if (resolvedGrievances.size() > 0) {
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

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {

    }


}