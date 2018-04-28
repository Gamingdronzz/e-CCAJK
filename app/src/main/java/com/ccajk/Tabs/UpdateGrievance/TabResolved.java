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
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ccajk.Activity.MainActivity;
import com.ccajk.Adapter.RecyclerViewAdapterGrievanceUpdate;
import com.ccajk.CustomObjects.ProgressDialog;
import com.ccajk.Providers.GrievanceDataProvider;
import com.ccajk.R;
import com.ccajk.Tools.Helper;


//Our class extending fragment
public class TabResolved extends Fragment {

    final String TAG = "Resolved";

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
            textViewNoListInfo.setText("No Grievances Resolved");
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
        Log.d(TAG, "init: resolved");
        if (GrievanceDataProvider.getInstance().getResolvedGrievanceList() != null) {
            if (GrievanceDataProvider.getInstance().getResolvedGrievanceList().size() == 0)
                showEmptyListLayout(true);
            else {
                showEmptyListLayout(false);
                adapter = new RecyclerViewAdapterGrievanceUpdate(GrievanceDataProvider.getInstance().getResolvedGrievanceList(), (MainActivity) getActivity());
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            }
        }
        else
            showEmptyListLayout(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RecyclerViewAdapterGrievanceUpdate.REQUEST_UPDATE: {
                if (resultCode == Activity.RESULT_OK) {
                    //refresh();
                    if (GrievanceDataProvider.getInstance().selectedGrievance.getGrievanceStatus() == 2) {
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