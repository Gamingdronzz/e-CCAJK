package com.mycca.Tabs.UpdateGrievance;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mycca.Activity.MainActivity;
import com.mycca.Adapter.RecyclerViewAdapterGrievanceUpdate;
import com.mycca.CustomObjects.Progress.ProgressDialog;
import com.mycca.Providers.GrievanceDataProvider;
import com.mycca.R;
import com.mycca.Tools.Helper;


//Our class extending fragment
public class TabSubmitted extends Fragment {

    final String TAG = "Submitted";

    RecyclerView recyclerView;
    RelativeLayout relativeLayoutEmptyList;
    TextView textViewNoListInfo;
    ProgressDialog progressDialog;

    RecyclerViewAdapterGrievanceUpdate adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_grievances, container, false);
        bindViews(view);
        showEmptyListLayout(true);
        init();
        return view;
    }

    private void showEmptyListLayout(boolean show) {
        if (show) {
            relativeLayoutEmptyList.setVisibility(View.VISIBLE);
            textViewNoListInfo.setText("No New Grievances Submitted");
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
        Log.d(TAG, "init: submitted");
        if (GrievanceDataProvider.getInstance().getSubmittedGrievanceList() != null) {
            if (GrievanceDataProvider.getInstance().getSubmittedGrievanceList().size() == 0)
                showEmptyListLayout(true);
            else {
                showEmptyListLayout(false);
                adapter = new RecyclerViewAdapterGrievanceUpdate(GrievanceDataProvider.getInstance().getSubmittedGrievanceList(), (MainActivity) getActivity());
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            }
        } else
            showEmptyListLayout(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RecyclerViewAdapterGrievanceUpdate.REQUEST_UPDATE: {
                if (resultCode == Activity.RESULT_OK) {
                    if (GrievanceDataProvider.getInstance().selectedGrievance.getGrievanceStatus() == 0) {
                        GrievanceDataProvider.getInstance().updateLists();
                    }
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
                                           String permissions[], int[] grantResults) {

    }


}


  /* if (grievanceModelArrayList == null) {
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
        }*/


    /*private void fromFirebase() {
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

    }*/