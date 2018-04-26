package com.ccajk.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ccajk.Activity.UpdateGrievanceActivity;
import com.ccajk.Activity.UpdateRtiActivity;
import com.ccajk.R;


public class UpdateFragment extends Fragment {


    TextView updateGrievance,updateRTIStatus;


    public UpdateFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update, container, false);
        bindViews(view);
        init();
        return view;
    }

    void bindViews(View view) {
        updateGrievance = view.findViewById(R.id.update_grievance_status);
        updateRTIStatus = view.findViewById(R.id.update_rti_status);
    }

    private void init() {
        updateGrievance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UpdateGrievanceActivity.class);
                startActivity(intent);
            }
        });


        updateRTIStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UpdateRtiActivity.class);
                startActivity(intent);
            }
        });


    }
}
