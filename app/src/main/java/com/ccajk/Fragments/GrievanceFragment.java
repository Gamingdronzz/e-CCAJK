package com.ccajk.Fragments;


import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.content.res.AppCompatResources;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ccajk.Adapter.GrievancAdapter;
import com.ccajk.Models.Grievance;
import com.ccajk.Models.GrievanceType;
import com.ccajk.R;
import com.ccajk.Tools.FireBaseHelper;
import com.ccajk.Tools.Helper;
import com.ccajk.Tools.PopUpWindows;
import com.ccajk.Tools.Preferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import easyfilepickerdialog.kingfisher.com.library.model.DialogConfig;
import easyfilepickerdialog.kingfisher.com.library.model.SupportFile;
import easyfilepickerdialog.kingfisher.com.library.view.FilePickerDialogFragment;


public class GrievanceFragment extends Fragment {

    TextView textViewFileName;
    AutoCompleteTextView inputPCode, inputMobile;
    EditText inputDetails;
    Spinner inputType, inputSubmittedBy;
    Button submit, buttonChooseFile;
    ImageButton buttonRemove;
    PopupWindow progressDialog;

    ArrayList<GrievanceType> list = new ArrayList<>();
    String TAG = "Grievance";
    String fileChosed, fileChosedPath, pcode, root;
    GrievanceType grievanceType;

    public GrievanceFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grievance, container, false);
        Bundle bundle = this.getArguments();
        root = bundle.getString("Root");
        init(view);
        return view;
    }

    private void init(View view) {

        progressDialog = Helper.getInstance().getProgressWindow(getActivity(), "Please wait...");

        ImageView imagePensionerCode = view.findViewById(R.id.image_pcode);
        imagePensionerCode.setImageDrawable(AppCompatResources.getDrawable(this.getContext(), R.drawable.ic_person_black_24dp));
        ImageView imageMobile = view.findViewById(R.id.image_mobile);
        imageMobile.setImageDrawable(AppCompatResources.getDrawable(this.getContext(), R.drawable.ic_phone_android_black_24dp));
        ImageView imageDetails = view.findViewById(R.id.image_details);
        imageDetails.setImageDrawable(AppCompatResources.getDrawable(this.getContext(), R.drawable.ic_details_black_24dp));
        ImageView imageType = view.findViewById(R.id.image_type);
        imageType.setImageDrawable(AppCompatResources.getDrawable(this.getContext(), R.drawable.ic_sentiment_dissatisfied_black_24dp));
        ImageView imageSubmittedBy = view.findViewById(R.id.image_submitted_by);
        imageSubmittedBy.setImageDrawable(AppCompatResources.getDrawable(this.getContext(), R.drawable.ic_person_black_24dp));
        ImageView imageAttach = view.findViewById(R.id.image_attach);
        imageAttach.setImageDrawable(AppCompatResources.getDrawable(this.getContext(), R.drawable.ic_attach_file_black_24dp));

        inputType = view.findViewById(R.id.spinner_type);
        if (root.equals(FireBaseHelper.getInstance().ROOT_GRIEVANCE_PENSION))
            list = Helper.getInstance().getPensionGrievanceTypelist();
        else
            list = Helper.getInstance().getGPFGrievanceTypelist();
        GrievancAdapter adapter = new GrievancAdapter(getContext(), list);
        inputType.setAdapter(adapter);

        inputSubmittedBy = view.findViewById(R.id.spinner_submitted_by);
        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, Helper.getInstance().submittedByList(root));
        inputSubmittedBy.setAdapter(arrayAdapter1);

        inputPCode = view.findViewById(R.id.autocomplete_pcode);
        inputMobile = view.findViewById(R.id.autocomplete_mobile);
        inputDetails = view.findViewById(R.id.edittext_details);
        textViewFileName = view.findViewById(R.id.textview_file_name);

        buttonChooseFile = view.findViewById(R.id.button_attach);
        buttonChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        buttonRemove = view.findViewById(R.id.btn_remove);
        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileChosed = null;
                fileChosedPath = null;
                textViewFileName.setText("");
                buttonRemove.setVisibility(View.GONE);
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
                                buttonRemove.setVisibility(View.GONE);
                            } else {
                                fileChosedPath = file.getAbsolutePath();
                                fileChosed = file.getName();
                                Log.d(TAG, "onFileSelected: " + fileChosedPath);
                                buttonRemove.setVisibility(View.VISIBLE);
                            }
                            textViewFileName.setText(fileChosed);
                        }
                    }
                })
                .build()
                .show(getActivity().getSupportFragmentManager(), null);
    }

    private boolean checkInput() {
        pcode = inputPCode.getText().toString();
        grievanceType = (GrievanceType) inputType.getSelectedItem();

        if (pcode.isEmpty()) {
            inputPCode.setError("Pensioner Code required");
            inputPCode.requestFocus();
            return false;
        } else if (inputMobile.getText().toString().isEmpty()) {
            inputMobile.setError("Mobile No required");
            inputMobile.requestFocus();
            return false;
        } else if (inputDetails.getText().toString().trim().isEmpty()) {
           inputDetails.setError( "Add details");
            inputDetails.requestFocus();
            return false;
        }
        return true;
    }

    private void confirmSubmission() {
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_confirm_submission, null);

        AlertDialog.Builder confirmDialog = PopUpWindows.getInstance().getConfirmationDialog(getActivity(), v);
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
        ppoNo.setText(ppoNo.getText() + " " + pcode);
        TextView mobNo = v.findViewById(R.id.textview_mobile_no);
        mobNo.setText(mobNo.getText() + " " + inputMobile.getText());
        TextView grievance = v.findViewById(R.id.textview_grievance_type);
        grievance.setText(grievance.getText() + " " + grievanceType.getName());
        TextView gr_by = v.findViewById(R.id.textview_grievance_by);
        gr_by.setText(gr_by.getText() + " " + inputSubmittedBy.getSelectedItem());
        TextView details = v.findViewById(R.id.textview_grievance_details);
        details.setText(inputDetails.getText().toString().trim());
        TextView fileName = v.findViewById(R.id.textview_file_name);
        fileName.setText(fileChosed == null ? Helper.getInstance().Nil : fileChosed);
    }


    private void submitGrievance() {
        progressDialog.showAtLocation(getView(), Gravity.CENTER,0,0);
        final DatabaseReference dbref;
        dbref = FireBaseHelper.getInstance().databaseReference.child(FireBaseHelper.getInstance().ROOT_GRIEVANCES);

        final Grievance grievance = new Grievance(
                pcode,
                inputMobile.getText().toString(),
                grievanceType.getId(),
                inputDetails.getText().toString().trim(),
                inputSubmittedBy.getSelectedItem().toString(),
                fileChosed,
                null,
                Preferences.getInstance().getPrefState(getContext()),
                0, new Date());

        dbref.child(pcode).child(String.valueOf(grievanceType.getId())).setValue(grievance).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                if (fileChosed != null) {
                                    uploadFile();
                                } else {
                                    Toast.makeText(getActivity(), "Grievance Submitted", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Unable to submit", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }

                        }
                    });
    }

    private void uploadFile() {
        UploadTask uploadTask;

        uploadTask = FireBaseHelper.getInstance().uploadFile(root, pcode, fileChosedPath, String.valueOf(grievanceType.getId()));

        if (uploadTask != null) {
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getContext(), "Unable to buttonUpload file", Toast.LENGTH_SHORT).show();
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
