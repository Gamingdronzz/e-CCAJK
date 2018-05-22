package com.mycca.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mycca.CustomObjects.FancyAlertDialog.FancyAlertDialogType;
import com.mycca.CustomObjects.Progress.ProgressDialog;
import com.mycca.Listeners.OnConnectionAvailableListener;
import com.mycca.Models.NewsModel;
import com.mycca.R;
import com.mycca.Tools.ConnectionUtility;
import com.mycca.Tools.FireBaseHelper;
import com.mycca.Tools.Helper;
import com.mycca.Tools.Preferences;

import java.util.Date;

public class AddNewsFragment extends Fragment {

    TextInputEditText textTitle, textDescription;
    Button add;
    ProgressDialog progressDialog;

    public AddNewsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_news, container, false);
        bindViews(view);
        init();
        return view;
    }

    private void bindViews(View view) {
        textTitle = view.findViewById(R.id.text_add_news_headline);
        textDescription = view.findViewById(R.id.text_add_news_description);
        add = view.findViewById(R.id.button_add_news);
        progressDialog = Helper.getInstance().getProgressWindow(getActivity(), "Please Wait...");
    }

    private void init() {
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()) {
                    progressDialog.show();
                    checkConnection();
                }
            }
        });
    }

    private boolean checkInput() {

        if (textTitle.getText().toString().trim().isEmpty()) {
            textTitle.setError("Add Headline");
            textTitle.requestFocus();
            return false;
        } else if (textDescription.getText().toString().trim().isEmpty()) {
            textDescription.setError("Add Description");
            textDescription.requestFocus();
            return false;
        } else {
            return true;
        }

    }

    private void checkConnection() {
        ConnectionUtility connectionUtility = new ConnectionUtility(new OnConnectionAvailableListener() {
            @Override
            public void OnConnectionAvailable() {
                addNewsToFireBase();
            }

            @Override
            public void OnConnectionNotAvailable() {
                progressDialog.dismiss();
                Helper.getInstance().showFancyAlertDialog(getActivity(), "Please connect to internet", "No Internet", "OK", null, null, null, FancyAlertDialogType.ERROR);
            }
        });
        connectionUtility.checkConnectionAvailability();

    }

    public void addNewsToFireBase() {
        NewsModel newsModel = new NewsModel(
                new Date(),
                textTitle.getText().toString(),
                textDescription.getText().toString(),
                Preferences.getInstance().getStaffPref(getContext(), Preferences.PREF_STAFF_DATA).getState()
        );

        Task task = FireBaseHelper.getInstance(getContext()).uploadDataToFirebase(
                FireBaseHelper.ROOT_NEWS,
                newsModel
        );
        task.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    Helper.getInstance().showFancyAlertDialog(getActivity(), "", "News Added", "OK", null, null, null, FancyAlertDialogType.SUCCESS);
                } else {
                    Helper.getInstance().showFancyAlertDialog(getActivity(), "The app might be in maintenence. Please try again later.", "Unable to add", "OK", null, null, null, FancyAlertDialogType.ERROR);

                }
            }
        });
    }
}