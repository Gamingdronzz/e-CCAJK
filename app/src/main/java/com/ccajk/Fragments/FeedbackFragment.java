package com.ccajk.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.ccajk.Activity.SubmitSuggestionActivity;
import com.ccajk.R;
import com.ccajk.Tools.Helper;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedbackFragment extends Fragment {



    private Button btnRateApplication,btnSuggestion,btnReportIssue;



    public FeedbackFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);
        setHasOptionsMenu(true);
        bindViews(view);
        return view;
    }

    private void bindViews(View view)
    {
        btnRateApplication = view.findViewById(R.id.btn_feedback_rate_application);
        btnSuggestion = view.findViewById(R.id.btn_feedback_submit_advice);
        btnReportIssue = view.findViewById(R.id.btn_feedback_report_issue);
    }

    private void init(View view) {
        btnReportIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //LoadActivity();
            }
        });

        btnSuggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //LoadActivity(SubmitSuggestionActivity.class);
            }
        });

        btnRateApplication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateApplication();
            }
        });
    }

    private void LoadActivity(Class cl)
    {
        Intent intent = new Intent();
        intent.setClass(getContext(),cl);
        startActivity(intent);
    }

    private void rateApplication()
    {
        if(Helper.getInstance().isDebugMode())
        {
            Toast.makeText(getContext(), "App is in debug Mode\nCannot Rate Application", Toast.LENGTH_SHORT).show();
        }
        else
        {
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
