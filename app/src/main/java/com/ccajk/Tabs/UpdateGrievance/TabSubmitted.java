package com.ccajk.Tabs.UpdateGrievance;


import android.annotation.SuppressLint;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.ccajk.Activity.MainActivity;
import com.ccajk.Adapter.RecyclerViewAdapterGrievanceUpdate;
import com.ccajk.CustomObjects.ProgressDialog;
import com.ccajk.Listeners.OnConnectionAvailableListener;
import com.ccajk.Models.GrievanceModel;
import com.ccajk.R;
import com.ccajk.Tools.ConnectionUtility;
import com.ccajk.Tools.FireBaseHelper;
import com.ccajk.Tools.Helper;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.view.View.GONE;


//Our class extending fragment
public class TabSubmitted extends Fragment {

    final String TAG = "Submitted";
    RecyclerView recyclerView;
    ProgressDialog progressDialog;

    RecyclerViewAdapterGrievanceUpdate adapter;
    ArrayList<GrievanceModel> grievanceModelArrayList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_grievances, container, false);
        bindViews(view);
        init();
        fromFirebase();
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_browser, menu);
    }

    private void refresh() {
        progressDialog.show();
        ConnectionUtility connectionUtility = new ConnectionUtility(new OnConnectionAvailableListener() {
            @Override
            public void OnConnectionAvailable() {
                init();
                fromFirebase();
            }

            @Override
            public void OnConnectionNotAvailable() {
                progressDialog.dismiss();
                Helper.getInstance().showAlertDialog(getContext(),
                        "No Internet Connection\nPlease Turn on Internet",
                        "Grievance",
                        "OK");

            }
        });
        connectionUtility.checkConnectionAvailability();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_refresh_link: {
                refresh();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void bindViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_grievances);
        progressDialog = Helper.getInstance().getProgressWindow(getActivity(), "Fetching currently submitted Grievances\nPlease Wait..");
    }

    private void init() {
        if (grievanceModelArrayList == null) {
            grievanceModelArrayList = new ArrayList<>();
        } else {
            grievanceModelArrayList.clear();
            adapter.notifyDataSetChanged();
        }

        if (adapter == null) {
            adapter = new RecyclerViewAdapterGrievanceUpdate(grievanceModelArrayList, (MainActivity) getActivity());
        }
        if (recyclerView.getAdapter() == null) {
            recyclerView.setAdapter(adapter);
        }
        if (recyclerView.getLayoutManager() == null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
    }

    private void fromFirebase() {
        progressDialog.show();
        DatabaseReference dbref = FireBaseHelper.getInstance().databaseReference;
        dbref.child(FireBaseHelper.getInstance().ROOT_GRIEVANCES).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (Integer.valueOf(ds.child("grievanceStatus").getValue().toString()) == 0) {
                            GrievanceModel grievanceModel = ds.getValue(GrievanceModel.class);
                            Log.d(TAG, "onChildAdded: Model = " + grievanceModel.getDetails());
                            int pos = grievanceModelArrayList.size();
                            grievanceModelArrayList.add(pos, grievanceModel);
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
                progressDialog.dismiss();
                if (grievanceModelArrayList.size() == 0)
                    Helper.getInstance().showAlertDialog(getContext(),
                            "No New submitted grievances",
                            "Grievance",
                            "OK");
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
        switch (requestCode) {
            case RecyclerViewAdapterGrievanceUpdate.REQUEST_UPDATE: {
                if (resultCode == Activity.RESULT_OK) {
                    refresh();
                }
            }
        }
    }

}


