package com.mycca.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.mycca.R;

public class SettingsFragment extends Fragment {

    private Switch notificationSwitch;

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

        notificationSwitch = view.findViewById(R.id.switch_settings_notifications);
    }

    private void init() {

        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(getContext(), "Checked=" + isChecked, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
