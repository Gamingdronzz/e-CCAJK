package com.mycca.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.mycca.Activity.IntroActivity;
import com.mycca.Activity.MainActivity;
import com.mycca.Activity.StateSettingActivity;
import com.mycca.CustomObjects.FancyAlertDialog.FancyAlertDialogType;
import com.mycca.R;
import com.mycca.Tools.FireBaseHelper;
import com.mycca.Tools.Helper;
import com.mycca.Tools.Preferences;

public class SettingsFragment extends Fragment {

    FirebaseAuth mAuth;
    private Switch switchNotification;
    LinearLayout parentLayout, layoutChangeState, layoutSignInOut, layoutChangePwd;
    private TextView tvCurrentState, tvSignOut, tvAccount, tvHelp, tvChangePwd;
    MainActivity activity;

    public SettingsFragment() {

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        mAuth = FireBaseHelper.getInstance(getContext()).mAuth;
        bindViews(view);
        init();
        return view;
    }


    private void bindViews(View view) {

        parentLayout = view.findViewById(R.id.layout_settings);

        switchNotification = view.findViewById(R.id.switch_settings_notifications);
        switchNotification.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_notifications_none_black_24dp, 0, 0, 0);

        layoutChangeState = view.findViewById(R.id.layout_settings_change_state);
        tvCurrentState = view.findViewById(R.id.tv_settings_curent_state);
        TextView tvChangeState = view.findViewById(R.id.tv_settings_change_state);
        tvChangeState.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_place_black_24dp, 0, R.drawable.ic_keyboard_arrow_right_black_24dp, 0);

        tvHelp = view.findViewById(R.id.tv_settings_view_help);
        tvHelp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_live_help_black_24dp, 0, 0, 0);

        tvChangePwd=view.findViewById(R.id.tv_settings_change_password);
        tvChangePwd.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_password,0,0,0);
        layoutChangePwd = view.findViewById(R.id.layout_settings_change_password);
        layoutSignInOut = view.findViewById(R.id.layout_settings_sign_in_out);
        tvSignOut = view.findViewById(R.id.tv_settings_sign_in_out);
        tvAccount = view.findViewById(R.id.tv_settings_account);
        manageSignOut();

    }

    private void init() {

        activity = (MainActivity) getActivity();
        switchNotification.setChecked(Preferences.getInstance().getBooleanPref(getContext(), Preferences.PREF_RECEIVE_NOTIFICATIONS));
        switchNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Preferences.getInstance().setBooleanPref(getContext(), Preferences.PREF_RECEIVE_NOTIFICATIONS, true);
            } else {
                Preferences.getInstance().setBooleanPref(getContext(), Preferences.PREF_RECEIVE_NOTIFICATIONS, false);
            }
        });

        tvHelp.setOnClickListener(v -> {
            Preferences.getInstance().clearTutorialPrefs(getContext());
            startActivity(new Intent(activity, IntroActivity.class).putExtra("FromSettings", true));
        });

        String text = "Current State: " + Helper.getInstance().getStateName(
                Preferences.getInstance().getStringPref(activity, Preferences.PREF_STATE));
        tvCurrentState.setText(text);
        layoutChangeState.setOnClickListener(v -> {
            Intent intent = new Intent(activity, StateSettingActivity.class);
            startActivity(intent);
            activity.finish();
        });

        layoutSignInOut.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() != null) {
                Helper.getInstance().showFancyAlertDialog(getActivity(),
                        "Sign out from Google?",
                        "Sign Out",
                        "OK",
                        () -> {
                            activity.signOutFromGoogle();
                            Helper.getInstance().showFancyAlertDialog(getActivity(), "", "Signed Out", "OK", () -> {

                            }, null, null, FancyAlertDialogType.SUCCESS);
                            manageSignOut();
                        },
                        "Cancel",
                        () -> {

                        },
                        FancyAlertDialogType.WARNING);
            } else {
                activity.signInWithGoogle();
            }
        });

        if (Preferences.getInstance().getStaffPref(activity, Preferences.PREF_STAFF_DATA) != null)
            layoutChangePwd.setVisibility(View.VISIBLE);
        else
            layoutChangePwd.setVisibility(View.GONE);
        layoutChangePwd.setOnClickListener(v -> {
            Helper.getInstance().showChangePasswordWindow(activity, parentLayout);
        });
    }

    public void manageSignOut() {
        if (mAuth.getCurrentUser() == null) {
            tvSignOut.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_drawbale_login_24dp, 0, 0, 0);
            tvSignOut.setText(getResources().getString(R.string.sign_in));
            tvAccount.setVisibility(View.INVISIBLE);
        } else {
            tvSignOut.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_logout, 0, 0, 0);
            tvSignOut.setText(getResources().getString(R.string.sign_out));
            String user = "Signed In: " + mAuth.getCurrentUser().getEmail();
            tvAccount.setVisibility(View.VISIBLE);
            tvAccount.setText(user);
        }
    }

}
