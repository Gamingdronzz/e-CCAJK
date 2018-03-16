package com.ccajk.Fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.content.res.AppCompatResources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ccajk.R;

public class AdhaarUploadFragment extends Fragment {

    ImageView pcode, adhaar, attach;
    AutoCompleteTextView pensionerCode, adhaarNo;
    Button upload;

    public AdhaarUploadFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_adhaar_upload, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        pcode = view.findViewById(R.id.image_pcode);
        pcode.setImageDrawable(AppCompatResources.getDrawable(this.getContext(), R.drawable.ic_person_black_24dp));
        adhaar = view.findViewById(R.id.image_adhaar);
        adhaar.setImageDrawable(AppCompatResources.getDrawable(this.getContext(), R.drawable.ic_card_black_24dp));
        attach = view.findViewById(R.id.image_attach);
        attach.setImageDrawable(AppCompatResources.getDrawable(this.getContext(), R.drawable.ic_attach_file_black_24dp));

        pensionerCode = view.findViewById(R.id.autocomplete_pcode);
        adhaarNo = view.findViewById(R.id.autocomplete_adhaar);

        upload = view.findViewById(R.id.button_upload);
        upload.setOnClickListener(new View.OnClickListener() {
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
        } else if (adhaarNo.getText().toString().trim().isEmpty()) {
            Toast.makeText(this.getContext(), "Aadhaar No required", Toast.LENGTH_SHORT).show();
            adhaarNo.requestFocus();
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
        confirmDialog.setPositiveButton("UPLOAD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                uploadAdhaar();
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
        mobNo.setText("Aadhaar No: " + adhaarNo.getText());
        v.findViewById(R.id.detail).setVisibility(View.GONE);
        v.findViewById(R.id.textview_grievance_type).setVisibility(View.GONE);
        v.findViewById(R.id.textview_grievance_by).setVisibility(View.GONE);
        v.findViewById(R.id.textview_grievance_details).setVisibility(View.GONE);
        TextView fileName = v.findViewById(R.id.textview_file_name);
        fileName.setText(fileName.getText() + " File Name");
    }

    private void uploadAdhaar() {
    }
}