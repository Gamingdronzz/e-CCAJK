package com.ccajk.Fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ccajk.Models.Grievance;
import com.ccajk.R;
import com.ccajk.Tools.FireBaseHelper;
import com.ccajk.Tools.Helper;
import com.ccajk.Tools.Preferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

import java.io.File;
import java.util.Date;
import java.util.List;

import easyfilepickerdialog.kingfisher.com.library.model.DialogConfig;
import easyfilepickerdialog.kingfisher.com.library.model.SupportFile;
import easyfilepickerdialog.kingfisher.com.library.view.FilePickerDialogFragment;


public class GrievanceFragment extends Fragment {

    String TAG = "Grievance";
    ImageView pcode, mob, details, type, submittedby, attach;
    TextView filename;
    AutoCompleteTextView pensionerCode, mobileNo;
    EditText grievanceDetails;
    Spinner grievanceType, grievanceSubmitedBy;
    Button submit, chooseFile;
    ImageButton remove;
    String[] list;
    String fileChosed, fileChosedPath,code;
    int gtype;

    public GrievanceFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grievance, container, false);
        Bundle bundle = this.getArguments();
        gtype = bundle.getInt("Category");
        init(view);
        return view;
    }

    private void init(View view) {

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
        if (gtype == Helper.getInstance().CATEGORY_PENSION)
            list = Helper.getInstance().getPensionGrievanceList();
        else
            list = Helper.getInstance().getGPFGrievanceList();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, list);
        grievanceType.setAdapter(arrayAdapter);

        grievanceSubmitedBy = view.findViewById(R.id.spinner_submitted_by);
        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, Helper.getInstance().submittedByList(gtype));
        grievanceSubmitedBy.setAdapter(arrayAdapter1);

        pensionerCode = view.findViewById(R.id.autocomplete_pcode);
        mobileNo = view.findViewById(R.id.autocomplete_mobile);
        grievanceDetails = view.findViewById(R.id.edittext_details);
        filename = view.findViewById(R.id.textview_file_name);

        chooseFile = view.findViewById(R.id.button_attach);
        chooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        remove = view.findViewById(R.id.btn_remove);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileChosed = null;
                fileChosedPath = null;
                filename.setText("");
                remove.setVisibility(View.GONE);
            }
        });

        submit = view.findViewById(R.id.button_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput())
                    confirmSubmission();
            }
        });
    }

    private void showFileChooser() {
        DialogConfig dialogConfig = new DialogConfig.Builder()
                .enableMultipleSelect(false) // default is false
                .enableFolderSelect(false) // default is false
                .initialDirectory(Environment.getExternalStorageDirectory().getAbsolutePath()) // default is sdcard
                .supportFiles(new SupportFile(".jpeg", 0), new SupportFile(".jpg", 0), new SupportFile(".pdf", 0)) // default is showing all file types.
                .build();

        new FilePickerDialogFragment.Builder()
                .configs(dialogConfig)
                .onFilesSelected(new FilePickerDialogFragment.OnFilesSelectedListener() {
                    @Override
                    public void onFileSelected(List<File> list) {
                        for (File file : list) {
                            if (file.length() / 1048576 > 5) {
                                Toast.makeText(getContext(), "Please Choose a file of 5mb or less", Toast.LENGTH_SHORT).show();
                                fileChosed = null;
                                fileChosedPath = null;
                                remove.setVisibility(View.GONE);
                            } else {
                                fileChosedPath = file.getAbsolutePath();
                                fileChosed = file.getName();
                                remove.setVisibility(View.VISIBLE);
                            }
                            filename.setText(fileChosed);
                        }
                    }
                })
                .build()
                .show(getActivity().getSupportFragmentManager(), null);
    }

    private boolean checkInput() {
        code=pensionerCode.getText().toString();
        if (code.trim().isEmpty()) {
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
        ppoNo.setText(ppoNo.getText() + " " + code);
        TextView mobNo = v.findViewById(R.id.textview_mobile_no);
        mobNo.setText(mobNo.getText() + " " + mobileNo.getText());
        TextView grievance = v.findViewById(R.id.textview_grievance_type);
        grievance.setText(grievance.getText() + " " + grievanceType.getSelectedItem());
        TextView gr_by = v.findViewById(R.id.textview_grievance_by);
        gr_by.setText(gr_by.getText() + " " + grievanceSubmitedBy.getSelectedItem());
        TextView details = v.findViewById(R.id.textview_grievance_details);
        details.setText(grievanceDetails.getText());
        TextView fileName = v.findViewById(R.id.textview_file_name);
        fileName.setText(fileChosed == null ? Helper.getInstance().Nil : fileChosed);
    }


    private void submitGrievance() {
        DatabaseReference dbref;
          if (gtype == Helper.getInstance().CATEGORY_PENSION) {
            dbref = FireBaseHelper.getInstance().databaseReference.child(FireBaseHelper.getInstance().ROOT_GRIEVANCE_PENSION);
            } else {
               dbref = FireBaseHelper.getInstance().databaseReference.child(FireBaseHelper.getInstance().ROOT_GRIEVANCE_GPF);
            }

        Date date= new Date();
        Grievance grievance= new Grievance(
                code,
                mobileNo.getText().toString(),
                grievanceType.getSelectedItem().toString(),
                grievanceDetails.getText().toString(),
                grievanceSubmitedBy.getSelectedItem().toString(),
                fileChosed,
                null,
                Preferences.getInstance().getPrefState(getContext()),
                0, date);

        dbref.child(code).push().setValue(grievance).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Unable to update", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
