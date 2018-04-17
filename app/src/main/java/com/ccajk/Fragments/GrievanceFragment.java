package com.ccajk.Fragments;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.content.res.AppCompatResources;
import android.util.Log;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Date;
import java.util.List;

import easyfilepickerdialog.kingfisher.com.library.model.DialogConfig;
import easyfilepickerdialog.kingfisher.com.library.model.SupportFile;
import easyfilepickerdialog.kingfisher.com.library.view.FilePickerDialogFragment;


public class GrievanceFragment extends Fragment {

    ImageView pcode, mob, details, type, submittedby, attach;
    TextView filename;
    AutoCompleteTextView pensionerCode, mobileNo;
    EditText grievanceDetails;
    Spinner grievanceType, grievanceSubmitedBy;
    Button submit, chooseFile;
    ImageButton remove;
    ProgressDialog progressDialog;

    String TAG = "Grievance";
    String[] list;
    String fileChosed, fileChosedPath, code, typeofGrievance, replacedGrievance, category;
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
        if (gtype == Helper.getInstance().CATEGORY_PENSION)
            category = FireBaseHelper.getInstance().ROOT_GRIEVANCE_PENSION;
        else
            category = FireBaseHelper.getInstance().ROOT_GRIEVANCE_GPF;

        progressDialog = new ProgressDialog(this.getContext());
        progressDialog.setMessage("Please Wait...");

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
                            if (file.length() / 1048576 > 1) {
                                Toast.makeText(getContext(), "Please Choose a file of 1mb or less", Toast.LENGTH_SHORT).show();
                                fileChosed = null;
                                fileChosedPath = null;
                                remove.setVisibility(View.GONE);
                            } else {
                                fileChosedPath = file.getAbsolutePath();
                                fileChosed = file.getName();
                                Log.d(TAG, "onFileSelected: " + fileChosedPath);
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
        code = pensionerCode.getText().toString();
        typeofGrievance = grievanceType.getSelectedItem().toString();
        replacedGrievance = typeofGrievance.replaceAll("/", "-");

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
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_confirm_submission, null);

        AlertDialog.Builder confirmDialog = Helper.getInstance().getConfirmationDialog(getActivity(),v);
        loadValues(v);

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
        grievance.setText(grievance.getText() + " " + typeofGrievance);
        TextView gr_by = v.findViewById(R.id.textview_grievance_by);
        gr_by.setText(gr_by.getText() + " " + grievanceSubmitedBy.getSelectedItem());
        TextView details = v.findViewById(R.id.textview_grievance_details);
        details.setText(grievanceDetails.getText());
        TextView fileName = v.findViewById(R.id.textview_file_name);
        fileName.setText(fileChosed == null ? Helper.getInstance().Nil : fileChosed);
    }


    private void submitGrievance() {
        progressDialog.show();
        final DatabaseReference dbref;
        dbref = FireBaseHelper.getInstance().databaseReference.child(category);

        final Grievance grievance = new Grievance(
                code,
                mobileNo.getText().toString(),
                typeofGrievance,
                grievanceDetails.getText().toString(),
                grievanceSubmitedBy.getSelectedItem().toString(),
                fileChosed,
                null,
                Preferences.getInstance().getPrefState(getContext()),
                0, new Date());

        dbref.child(code).child(replacedGrievance).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: "+ dataSnapshot);
                if (dataSnapshot.getValue() == null) {
                    dbref.child(code).child(replacedGrievance).setValue(grievance).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                if (fileChosed != null) {
                                    uploadFile();
                                }
                                else {
                                    Toast.makeText(getActivity(), "Grievance Submitted", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Unable to submit", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }

                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "Grievance Already Submitted", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void uploadFile() {
        UploadTask uploadTask;

        uploadTask = FireBaseHelper.getInstance().uploadFile(category, code, fileChosedPath, replacedGrievance);

        if (uploadTask != null) {
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getContext(), "Unable to upload file", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onFailure: " + exception.getMessage());
                    progressDialog.dismiss();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getActivity(), "Grievance Submitted", Toast.LENGTH_SHORT).show();
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Log.d(TAG, "onSuccess: " + downloadUrl);
                    progressDialog.dismiss();
                }
            });
        }
    }

}
