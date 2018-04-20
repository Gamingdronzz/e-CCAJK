package com.ccajk.Tabs;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ccajk.Models.LocationModel;
import com.ccajk.R;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.IndicatorSeekBarType;
import com.warkiz.widget.IndicatorType;
import com.warkiz.widget.TickType;

import java.util.ArrayList;

public class TabNearby extends Fragment {

    private final String TAG = "Nearby";
    private TextView kilometres, msg;
    ImageButton refresh;
    private int seekBarValue;
    private ArrayList<LocationModel> allLocations = new ArrayList<>();
    IndicatorSeekBar seekBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_nearby_locations, container, false);
        init(view);
        return view;
    }


    private void init(View view) {

        kilometres = view.findViewById(R.id.textview_range);
        msg = view.findViewById(R.id.textview_location_msg);
        refresh = view.findViewById(R.id.image_btn_refresh);

        seekBar = view.findViewById(R.id.seekBar);
        seekBar.getBuilder()
                .setMax(3)
                .setMin(0)
                .setProgress(0)
                .setSeekBarType(IndicatorSeekBarType.DISCRETE_TICKS)
                .setTickType(TickType.OVAL)
                .setTickNum(1)
                .setBackgroundTrackSize(2)//dp size
                .setProgressTrackSize(3)//dp size
                .setIndicatorType(IndicatorType.CIRCULAR_BUBBLE)
                .setIndicatorColor(getResources().getColor(R.color.colorAccent))
                .build();

        seekBar.setOnSeekChangeListener(new IndicatorSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(IndicatorSeekBar seekBar, int progress, float progressFloat, boolean fromUserTouch) {
            }

            @Override
            public void onSectionChanged(IndicatorSeekBar seekBar, int thumbPosOnTick, String textBelowTick, boolean fromUserTouch) {
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar, int thumbPosOnTick) {
            }

            @SuppressLint("MissingPermission")
            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

            }
        });


    }



}