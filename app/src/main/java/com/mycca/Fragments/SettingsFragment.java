package com.mycca.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.mycca.Activity.StateSettingActivity;
import com.mycca.R;
import com.mycca.Tools.Helper;
import com.mycca.Tools.Preferences;

public class SettingsFragment extends Fragment {

    private Switch switchNotification;
    LinearLayout layoutChangeState;
    private TextView tvChangeState, tvCurrentState;

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
        switchNotification.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_notifications_none_black_24dp, 0, 0, 0);

        layoutChangeState = view.findViewById(R.id.layout_settings_change_state);
        tvCurrentState = view.findViewById(R.id.tv_settings_curent_state);
        tvChangeState = view.findViewById(R.id.tv_settings_change_state);
        tvChangeState.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_place_black_24dp, 0, R.drawable.ic_keyboard_arrow_right_black_24dp, 0);


    }


    private void init() {

        switchNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getContext(), "Notifications on", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Notifications off", Toast.LENGTH_SHORT).show();
                }
            }
        });

        layoutChangeState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), StateSettingActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        String circleCode = Preferences.getInstance().getStringPref(getContext(), Preferences.PREF_STATE);
        Log.d("Settings", "onResume: " + circleCode);
        tvCurrentState.setText("Current State: " + Helper.getInstance().getStateName(circleCode));
    }
}
