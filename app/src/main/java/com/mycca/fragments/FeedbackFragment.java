package com.mycca.fragments;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mycca.activity.MainActivity;
import com.mycca.custom.FancyAlertDialog.FancyAlertDialogType;
import com.mycca.listeners.OnConnectionAvailableListener;
import com.mycca.R;
import com.mycca.tools.ConnectionUtility;
import com.mycca.tools.FireBaseHelper;
import com.mycca.tools.Helper;

public class FeedbackFragment extends Fragment {

    String TAG = "feedback";
    private Button btnRateApplication, btnSuggestion;
    private TextInputEditText etSuggestion;
    Activity activity;

    public FeedbackFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);
        setHasOptionsMenu(true);
        bindViews(view);
        init();
        return view;
    }

    private void bindViews(View view) {
        etSuggestion = view.findViewById(R.id.et_feedback_submit_suggestion);
        btnRateApplication = view.findViewById(R.id.btn_feedback_rate_application);
        btnSuggestion = view.findViewById(R.id.btn_feedback_submit_advice);
    }

    private void init() {

        activity = getActivity();
        btnSuggestion.setOnClickListener(v -> {
            if (!etSuggestion.getText().toString().trim().isEmpty()) {
                submitSuggestion();
            } else
                etSuggestion.setError(getString(R.string.no_suggestion));
        });

        btnRateApplication.setOnClickListener(v -> rateApplication());
    }

    private void rateApplication() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(Helper.getInstance().getPlayStoreURL())));
        } catch (android.content.ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + activity.getPackageName())));
        }
    }

    private void submitSuggestion() {
        ConnectionUtility connectionUtility = new ConnectionUtility(new OnConnectionAvailableListener() {
            @Override
            public void OnConnectionAvailable() {
                Log.d(TAG, "version checked= " + Helper.versionChecked);
                if (!Helper.versionChecked) {
                    FireBaseHelper.getInstance(getContext()).checkForUpdate(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (Helper.getInstance().onLatestVersion(dataSnapshot, activity))
                                submit();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Helper.getInstance().showMaintenanceDialog(activity);
                        }
                    });
                } else
                    submit();
            }

            @Override
            public void OnConnectionNotAvailable() {
                Helper.getInstance().showFancyAlertDialog(getActivity(),
                        getString(R.string.connect_to_internet),
                        getString(R.string.no_internet),
                        getString(R.string.ok),
                        null,
                        null,
                        null,
                        FancyAlertDialogType.ERROR);
            }
        });
        connectionUtility.checkConnectionAvailability();
    }

    private void submit() {
        Task<Void> task = FireBaseHelper.getInstance(getContext()).uploadDataToFirebase(
                etSuggestion.getText().toString().trim(),
                FireBaseHelper.ROOT_SUGGESTIONS);

        task.addOnCompleteListener((Task<Void> task1) -> {

            if (task1.isSuccessful()) {
                Helper.getInstance().showFancyAlertDialog(getActivity(), "", getString(R.string.thanks_for_feedback), getString(R.string.ok), () -> {
                        },
                        null, null, FancyAlertDialogType.SUCCESS);
            } else {
                if (FireBaseHelper.getInstance(getContext()).mAuth.getCurrentUser() == null) {
                    Helper.getInstance().showFancyAlertDialog(activity, getString(R.string.suggestion_sign_in),
                            getString(R.string.sign_in_with_google),
                            getString(R.string.sign_in),
                            () -> ((MainActivity) activity).signInWithGoogle(),
                            getString(android.R.string.cancel),
                            () -> {

                            },
                            FancyAlertDialogType.ERROR);
                } else {
                    Helper.getInstance().showMaintenanceDialog(activity);
                }

            }
        });
    }
}