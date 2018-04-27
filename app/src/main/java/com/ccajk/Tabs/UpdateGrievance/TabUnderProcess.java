package com.ccajk.Tabs.UpdateGrievance;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.ccajk.Adapter.RecyclerViewAdapterGrievanceUpdate;
import com.ccajk.Models.GrievanceModel;
import com.ccajk.R;
import com.ccajk.Tools.FireBaseHelper;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.view.View.GONE;


//Our class extending fragment
public class TabUnderProcess extends Fragment {

    final String TAG = "Submitted";
    RecyclerView recyclerView;
    ProgressBar progressBar;

    RecyclerViewAdapterGrievanceUpdate adapter;
    ArrayList<GrievanceModel> grievanceModelArrayList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_grievances, container, false);
        bindViews(view);
        init();
        fromFirebase();
        return view;
    }

    private void bindViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_grievances);
        progressBar  = view.findViewById(R.id.progress_grievances);
    }

    private void init() {
        grievanceModelArrayList = new ArrayList<>();
        adapter = new RecyclerViewAdapterGrievanceUpdate(grievanceModelArrayList, getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void fromFirebase() {
        DatabaseReference dbref = FireBaseHelper.getInstance().databaseReference;
        dbref.child(FireBaseHelper.getInstance().ROOT_GRIEVANCES).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (Integer.valueOf(ds.child("grievanceStatus").getValue().toString()) == 1) {
                            GrievanceModel grievanceModel = ds.getValue(GrievanceModel.class);
                            Log.d(TAG, "onChildAdded: Model = " + grievanceModel.getDetails());
                            int pos = grievanceModelArrayList.size();
                            grievanceModelArrayList.add(pos,grievanceModel);
                            adapter.notifyItemInserted(pos);
                        }
                    }
                    Log.d(TAG, "onChildAdded: List" + grievanceModelArrayList);
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
        dbref.child(FireBaseHelper.getInstance().ROOT_GRIEVANCES).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(GONE);
//                if (grievanceModelArrayList.size() == 0)
//                    tvNoData.setText("ALL GRIEVANCES RESOLVED");
//                else
//                    tvNoData.setText("");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

}


