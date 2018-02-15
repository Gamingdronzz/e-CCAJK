package com.ccajk.Tabs;



import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ccajk.Activity.MapsActivity;
import com.ccajk.R;

//Our class extending fragment
public class TabNearby extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_nearby_locations, container, false);
        init(view);
        return view;
    }

    private void init(View view)
    {
        Button button = view.findViewById(R.id.openMaps);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MapsActivity.class);
                v.getContext().startActivity(intent);
            }
        });
    }
}