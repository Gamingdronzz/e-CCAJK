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

import com.mycca.Activity.MainActivity;
import com.mycca.Adapter.RecyclerViewAdapterGrievanceUpdate;
import com.mycca.CustomObjects.Progress.ProgressDialog;
import com.mycca.Providers.GrievanceDataProvider;
import com.mycca.R;
import com.mycca.Tools.Helper;

public class TabUnderProcess extends Fragment {

    final String TAG = "UnderProcess";

    RecyclerView recyclerView;
    RelativeLayout relativeLayoutEmptyList;
    TextView textViewNoListInfo;
    ProgressDialog progressDialog;

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
        Log.d(TAG, "init: processing");
        if (GrievanceDataProvider.getInstance().getProcessingGrievanceList() != null) {
            if (GrievanceDataProvider.getInstance().getProcessingGrievanceList().size() == 0)
                showEmptyListLayout(true);
            else {
                showEmptyListLayout(false);
                adapter = new RecyclerViewAdapterGrievanceUpdate(GrievanceDataProvider.getInstance().getProcessingGrievanceList(), (MainActivity) getActivity(), false);
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
                    //refresh();
                    if (GrievanceDataProvider.getInstance().selectedGrievance.getGrievanceStatus() == 1) {
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
                                           @NonNull String permissions[], @NonNull int[] grantResults) {

    }

}