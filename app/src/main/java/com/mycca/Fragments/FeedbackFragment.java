package com.mycca.Fragments;


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
import com.mycca.Activity.MainActivity;
import com.mycca.CustomObjects.FancyAlertDialog.FancyAlertDialogType;
import com.mycca.Listeners.OnConnectionAvailableListener;
import com.mycca.R;
import com.mycca.Tools.ConnectionUtility;
import com.mycca.Tools.FireBaseHelper;
import com.mycca.Tools.Helper;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedbackFragment extends Fragment {

    String TAG = "feedback";
    private Button btnRateApplication, btnSuggestion, btnReportIssue;
    private TextInputEditText etSuggestion;
    Activity activity;
    //etCauseOfIssue;
    //private Spinner spinnerErrorType;

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
                etSuggestion.setError("No Suggestion!");
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
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (Helper.getInstance().onLatestVersion(dataSnapshot, activity))
                                submit();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Helper.getInstance().showUpdateOrMaintenanceDialog(false, activity);
                        }
                    });
                } else
                    submit();
            }

            @Override
            public void OnConnectionNotAvailable() {
                Helper.getInstance().showFancyAlertDialog(getActivity(),
                        "No Internet Connection\nPlease turn on internet connection before submitting ",
                        "My CCA",
                        "OK",
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
                Helper.getInstance().showFancyAlertDialog(getActivity(), "Your suggestion means a lot to us!<br><br><b>Thank you</b>", "Advice", "OK", () -> {},
                        null, null, FancyAlertDialogType.SUCCESS);
            } else {
                if (FireBaseHelper.getInstance(getContext()).mAuth.getCurrentUser() == null) {
                    Helper.getInstance().showFancyAlertDialog(activity, "You cannot submit suggestion without signing in",
                            "Sign in with google",
                            "Sign in",
                            () -> ((MainActivity) activity).signInWithGoogle(),
                            "Cancel",
                            () -> {

                            },
                            FancyAlertDialogType.ERROR);
                } else {
                    Helper.getInstance().showUpdateOrMaintenanceDialog(false, activity);
                }

            }
        });
    }
}
