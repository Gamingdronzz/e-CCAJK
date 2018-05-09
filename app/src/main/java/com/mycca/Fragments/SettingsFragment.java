package com.mycca.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.mycca.R;

public class SettingsFragment extends Fragment {

    private Switch switchNotification;
    private TextView tvChangeState;

    public SettingsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        bindViews(view);
        init();
        return view;
    }

    private void bindViews(View view) {

        switchNotification = view.findViewById(R.id.switch_settings_notifications);
        tvChangeState=view.findViewById(R.id.tv_settings_change_state);
    }

    private void init() {

        switchNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Toast.makeText(getContext(), "Notifications on", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getContext(), "Notifications off", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvChangeState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
