package com.mycca.fragments;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.mycca.R;
import com.mycca.activity.IntroActivity;
import com.mycca.activity.MainActivity;
import com.mycca.activity.StateSettingActivity;
import com.mycca.custom.FancyAlertDialog.FancyAlertDialogType;
import com.mycca.tools.Helper;
import com.mycca.tools.FireBaseHelper;
import com.mycca.tools.Preferences;

import java.util.Locale;

public class SettingsFragment extends Fragment {

    FirebaseAuth mAuth;
    private Switch switchNotification;
    ScrollView parentLayout;
    LinearLayout layoutChangeState, layoutSignInOut, layoutChangePwd, layoutChangeLang;
    private TextView tvCurrentState;
    private TextView tvSignOut;
    private TextView tvAccount;
    private TextView tvHelp;
    private TextView tvCurrentLang;
    MainActivity activity;

    public SettingsFragment() {

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        mAuth = FireBaseHelper.getInstance().getAuth();
        bindViews(view);
        init();
        return view;
    }


    private void bindViews(View view) {

        parentLayout = view.findViewById(R.id.layout_settings);

        switchNotification = view.findViewById(R.id.switch_settings_notifications);
        switchNotification.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_notifications_none_black_24dp, 0, 0, 0);

        layoutChangeState = view.findViewById(R.id.layout_settings_change_state);
        tvCurrentState = view.findViewById(R.id.tv_settings_current_state);
        TextView tvChangeState = view.findViewById(R.id.tv_settings_change_state);
        tvChangeState.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_place_black_24dp, 0, 0, 0);

        layoutChangeLang = view.findViewById(R.id.layout_settings_change_lang);
        tvCurrentLang = view.findViewById(R.id.tv_settings_language);
        TextView tvChangeLang = view.findViewById(R.id.tv_settings_change_lang);
        tvChangeLang.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_language_black_24dp, 0, 0, 0);

        tvHelp = view.findViewById(R.id.tv_settings_view_help);
        tvHelp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_live_help_black_24dp, 0, 0, 0);

        TextView tvChangePwd = view.findViewById(R.id.tv_settings_change_password);
        tvChangePwd.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_password, 0, 0, 0);
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

        String stateName;
        if(Preferences.getInstance().getStringPref(activity,Preferences.PREF_LANGUAGE).equals("hi"))
            stateName= Preferences.getInstance().getStatePref(activity).getHi();
        else
            stateName=Preferences.getInstance().getStatePref(activity).getEn();
        tvCurrentState.setText(String.format(getString(R.string.current_state), stateName));
        layoutChangeState.setOnClickListener(v -> {
            Intent intent = new Intent(activity, StateSettingActivity.class);
            startActivity(intent);
        });

        Locale loc = Locale.getDefault();
        tvCurrentLang.setText(String.format(getString(R.string.language), loc.getDisplayLanguage(loc)));
        layoutChangeLang.setOnClickListener(v -> showLanguageDialog());

        tvHelp.setOnClickListener(v -> {
            Preferences.getInstance().clearTutorialPrefs(getContext());
            startActivity(new Intent(activity, IntroActivity.class).putExtra("FromSettings", true));
        });


        layoutSignInOut.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() != null) {
                Helper.getInstance().showFancyAlertDialog(getActivity(),
                        getString(R.string.sign_out_from_google),
                        getString(R.string.sign_out),
                        getString(R.string.ok),
                        () -> {
                            activity.signOutFromGoogle();
                            Helper.getInstance().showFancyAlertDialog(getActivity(), "", getString(R.string.signed_out), getString(R.string.ok), () -> {
                            }, null, null, FancyAlertDialogType.SUCCESS);
                            manageSignOut();
                        },
                        getString(R.string.cancel),
                        () -> {
                        },
                        FancyAlertDialogType.WARNING);
            } else {
                activity.signInWithGoogle();
            }
        });

        if (Preferences.getInstance().getStaffPref(activity) != null)
            layoutChangePwd.setVisibility(View.VISIBLE);
        else
            layoutChangePwd.setVisibility(View.GONE);
        layoutChangePwd.setOnClickListener(v -> Helper.getInstance().showChangePasswordWindow(activity, parentLayout));
    }

    private void showLanguageDialog() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_select_language, (ViewGroup) getView(), false);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setView(v)
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                })
                .setPositiveButton(getString(R.string.select), (dialog, which) -> {
                    final RadioGroup rg = v.findViewById(R.id.radio_group_language);
                    Helper.getInstance().showReloadWarningDialog(activity, () -> {
                        if (rg.getCheckedRadioButtonId() == R.id.rBEnglish)
                            Preferences.getInstance().setStringPref(getContext(), Preferences.PREF_LANGUAGE, "en");
                        if (rg.getCheckedRadioButtonId() == R.id.rBHindi)
                            Preferences.getInstance().setStringPref(getContext(), Preferences.PREF_LANGUAGE, "hi");
                        Helper.getInstance().reloadApp(activity);
                    });
                });
        builder.show();
    }

    public void manageSignOut() {
        if (mAuth.getCurrentUser() == null) {
            tvSignOut.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_drawbale_login_24dp, 0, 0, 0);
            tvSignOut.setText(getString(R.string.sign_in));
            tvAccount.setVisibility(View.INVISIBLE);
        } else {
            tvSignOut.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_logout, 0, 0, 0);
            tvSignOut.setText(getString(R.string.sign_out));
            String user = String.format(getString(R.string.signed_in), mAuth.getCurrentUser().getEmail());
            tvAccount.setVisibility(View.VISIBLE);
            tvAccount.setText(user);
        }
    }

}
