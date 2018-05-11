package com.mycca.Fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.mycca.Activity.UpdateGrievanceActivity;
import com.mycca.Adapter.RecyclerViewAdapterGrievanceUpdate;
import com.mycca.CustomObjects.FancyAlertDialog.FancyAlertDialogType;
import com.mycca.CustomObjects.FancyAlertDialog.IFancyAlertDialogListener;
import com.mycca.R;
import com.mycca.Tools.FireBaseHelper;
import com.mycca.Tools.Helper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mycca.Tools.Preferences;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedbackFragment extends Fragment {

    String TAG = "feedback";
    private Button btnRateApplication, btnSuggestion, btnReportIssue;
    private TextInputEditText etSuggestion;
    //etCauseOfIssue;
    //private Spinner spinnerErrorType;

    public FeedbackFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);
        setHasOptionsMenu(true);
        bindViews(view);
        init();
        return view;
    }

    private void bindViews(View view) {
        etSuggestion = view.findViewById(R.id.et_feedback_submit_suggestion);
        //etCauseOfIssue = view.findViewById(R.id.et_feedback_report_issue);
        //spinnerErrorType = view.findViewById(R.id.spinner_feedback_error_types);
        btnRateApplication = view.findViewById(R.id.btn_feedback_rate_application);
        btnSuggestion = view.findViewById(R.id.btn_feedback_submit_advice);
        //btnReportIssue = view.findViewById(R.id.btn_feedback_report_issue);
    }

    private void init() {

//        spinnerErrorType.setAdapter(new ArrayAdapter(getContext(),
//                android.R.layout.simple_spinner_item,
//                Helper.getInstance().errorCodesList));

        btnSuggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etSuggestion.getText().toString().trim().isEmpty()) {
                    Task task = FireBaseHelper.getInstance(getContext()).uploadDataToFirebase(
                            FireBaseHelper.getInstance(getContext()).ROOT_SUGGESTIONS,
                            etSuggestion.getText().toString().trim(),
                            getContext()
                    );
                    task.addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Helper.getInstance().showFancyAlertDialog(getActivity(),"Your suggestion means a lot to us!<br><br><b>Thank you</b>" , "Advice", "OK", new IFancyAlertDialogListener() {
                                    @Override
                                    public void OnClick() {
                                    }
                                }, null, null, FancyAlertDialogType.SUCCESS);
                            } else {
                                Log.d(TAG, task.getResult().toString());
                            }
                        }
                    });
                } else
                    etSuggestion.setError("No Suggestion!");
            }
        });

//        btnReportIssue.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Task task = FireBaseHelper.getInstance(getContext()).uploadDataToFirebase(
//                        FireBaseHelper.getInstance(getContext()).ROOT_ERROR_REPORT,
//                        null,
//                        getContext(),
//                        spinnerErrorType.getSelectedItem().toString(),
//                        Helper.getInstance().errorMessageList[spinnerErrorType.getSelectedItemPosition()],
//                        etCauseOfIssue.getText().toString().trim());
//
//                task.addOnCompleteListener(new OnCompleteListener() {
//                    @Override
//                    public void onComplete(@NonNull Task task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(getContext(), "Thankyou for Reporting", Toast.LENGTH_SHORT).show();
//                        }else {
//                            Log.d(TAG, task.getResult().toString());
//                        }
//                    }
//                });
//            }
//        });

        btnRateApplication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateApplication();
            }
        });
    }


    private void LoadActivity(Class cl) {
        Intent intent = new Intent();
        intent.setClass(getContext(), cl);
        startActivity(intent);
    }

    private void rateApplication() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(Helper.getInstance().getPlayStoreURL())));
        } catch (android.content.ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_refresh_link:
//                if (progressBar.getVisibility() == View.GONE)
//                    webView.reload();
//                break;
//            default:
//                break;
//        }

        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //inflater.inflate(R.menu.menu_browser, menu);
    }


}
