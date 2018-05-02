package com.ccajk.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.ccajk.R;
import com.ccajk.Tools.FireBaseHelper;
import com.ccajk.Tools.Helper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedbackFragment extends Fragment {

    private Button btnRateApplication, btnSuggestion, btnReportIssue;
    private TextInputEditText etSuggestion, etCauseOfIssue;
    private Spinner spinnerErrorType;

    public FeedbackFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);
        setHasOptionsMenu(true);
        bindViews(view);
        return view;
    }

    private void bindViews(View view) {
        etSuggestion = view.findViewById(R.id.et_feedback_submit_suggestion);
        etCauseOfIssue = view.findViewById(R.id.et_feedback_report_issue);
        spinnerErrorType = view.findViewById(R.id.spinner_feedback_error_types);
        btnRateApplication = view.findViewById(R.id.btn_feedback_rate_application);
        btnSuggestion = view.findViewById(R.id.btn_feedback_submit_advice);
        btnReportIssue = view.findViewById(R.id.btn_feedback_report_issue);
    }

    private void init(View view) {
        btnSuggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task task = FireBaseHelper.getInstance().uploadDataToFirebase(
                        FireBaseHelper.getInstance().ROOT_SUGGESTIONS,
                        etSuggestion.getText().toString().trim()
                );
                task.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Thanks for your Valuable Suggestion", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        btnReportIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task task = FireBaseHelper.getInstance().uploadDataToFirebase(
                        FireBaseHelper.getInstance().ROOT_ERROR_REPORT,
                        spinnerErrorType.getSelectedItem().toString(),
                        etCauseOfIssue.getText().toString().trim());

                task.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Thankyou for Reporting", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

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
        if (Helper.getInstance().isDebugMode()) {
            Toast.makeText(getContext(), "App is in debug Mode\nCannot Rate Application", Toast.LENGTH_SHORT).show();
        } else {
            //TODO
            //Show play store app page
            //showPlayStoreAppPage();
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
