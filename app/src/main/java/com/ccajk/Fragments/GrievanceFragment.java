package com.ccajk.Fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.content.res.AppCompatResources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ccajk.R;
import com.ccajk.Tools.Helper;


public class GrievanceFragment extends Fragment {

    ImageView pcode, mob, details, type, submittedby, attach;
    AutoCompleteTextView pensionerCode, mobileNo;
    EditText grievanceDetails;
    Spinner grievanceType, grievanceSubmitedBy;
    Button submit;

    String[] list = {"Type 1", "Type 2", "Type 3"};

    public GrievanceFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grievance, container, false);
        Bundle bundle = this.getArguments();
        int gtype = bundle.getInt("Category");
        init(view, gtype);
        return view;
    }

    private void init(View view, final int gtype) {

        pcode = view.findViewById(R.id.image_pcode);
        pcode.setImageDrawable(AppCompatResources.getDrawable(this.getContext(), R.drawable.ic_person_black_24dp));
        mob = view.findViewById(R.id.image_mobile);
        mob.setImageDrawable(AppCompatResources.getDrawable(this.getContext(), R.drawable.ic_phone_android_black_24dp));
        details = view.findViewById(R.id.image_details);
        details.setImageDrawable(AppCompatResources.getDrawable(this.getContext(), R.drawable.ic_details_black_24dp));
        type = view.findViewById(R.id.image_type);
        type.setImageDrawable(AppCompatResources.getDrawable(this.getContext(), R.drawable.ic_sentiment_dissatisfied_black_24dp));
        submittedby = view.findViewById(R.id.image_submitted_by);
        submittedby.setImageDrawable(AppCompatResources.getDrawable(this.getContext(), R.drawable.ic_person_black_24dp));
        attach = view.findViewById(R.id.image_attach);
        attach.setImageDrawable(AppCompatResources.getDrawable(this.getContext(), R.drawable.ic_attach_file_black_24dp));

        grievanceType = view.findViewById(R.id.spinner_type);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, list);
        grievanceType.setAdapter(arrayAdapter);

        grievanceSubmitedBy = view.findViewById(R.id.spinner_submitted_by);
        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, Helper.getInstance().submittedByList(gtype));
        grievanceSubmitedBy.setAdapter(arrayAdapter1);

        pensionerCode = view.findViewById(R.id.autocomplete_pcode);
        mobileNo = view.findViewById(R.id.autocomplete_mobile);
        grievanceDetails = view.findViewById(R.id.edittext_details);

        submit = view.findViewById(R.id.button_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput())
                    confirmSubmission();
            }
        });
    }

    private boolean checkInput() {
        if (pensionerCode.getText().toString().trim().isEmpty()) {
            Toast.makeText(this.getContext(), "Pensioner Code required", Toast.LENGTH_SHORT).show();
            pensionerCode.requestFocus();
            return false;
        } else if (mobileNo.getText().toString().trim().isEmpty()) {
            Toast.makeText(this.getContext(), "Mobile No required", Toast.LENGTH_SHORT).show();
            mobileNo.requestFocus();
            return false;
        } else if (grievanceDetails.getText().toString().trim().isEmpty()) {
            Toast.makeText(this.getContext(), "Please add some detail", Toast.LENGTH_SHORT).show();
            grievanceDetails.requestFocus();
            return false;
        }
        return true;
    }

    private void confirmSubmission() {
        AlertDialog.Builder confirmDialog = new AlertDialog.Builder(this.getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_confirm_submission, null);
        confirmDialog.setView(v);
        loadValues(v);
        confirmDialog.setTitle("Confirm Input Before Submission");
        confirmDialog.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                submitGrievance();
            }
        });
        confirmDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        confirmDialog.show();
    }

    private void loadValues(View v) {
        TextView ppoNo = v.findViewById(R.id.textview_ppo_no);
        ppoNo.setText(ppoNo.getText() + " " + pensionerCode.getText());
        TextView mobNo = v.findViewById(R.id.textview_mobile_no);
        mobNo.setText(mobNo.getText() + " " + mobileNo.getText());
        TextView grievance = v.findViewById(R.id.textview_grievance_type);
        grievance.setText(grievance.getText() + " " + grievanceType.getSelectedItem());
        TextView gr_by = v.findViewById(R.id.textview_grievance_by);
        gr_by.setText(gr_by.getText() + " " + grievanceSubmitedBy.getSelectedItem());
        TextView details = v.findViewById(R.id.textview_grievance_details);
        details.setText(grievanceDetails.getText());
        TextView fileName = v.findViewById(R.id.textview_file_name);
        fileName.setText(fileName.getText() + " File Name");
    }


    private void submitGrievance() {
    }

}
