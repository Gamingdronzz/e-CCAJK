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
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ccajk.Models.PanAdhaar;
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
import java.util.List;

import easyfilepickerdialog.kingfisher.com.library.model.DialogConfig;
import easyfilepickerdialog.kingfisher.com.library.model.SupportFile;
import easyfilepickerdialog.kingfisher.com.library.view.FilePickerDialogFragment;


public class PanAdhaarUploadFragment extends Fragment {

    TextView textViewFileName;
    AutoCompleteTextView inputPCode, inputNumber;
    Button buttonUpload, buttonChooseFile;
    ProgressDialog progressDialog;

    DatabaseReference dbref;
    private static final String TAG = "PanAdhaarUpload";
    String pensionerCode, fileChosed, fileChosedPath, uploadType;
    int type;

    public PanAdhaarUploadFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pan_adhaar_upload, container, false);
        type = this.getArguments().getInt("UploadType");
        init(view);
        return view;
    }

    private void init(View view) {
        uploadType = (type == Helper.getInstance().UPLOAD_TYPE_PAN ?
                FireBaseHelper.getInstance().ROOT_PAN : FireBaseHelper.getInstance().ROOT_ADHAAR);

        progressDialog = Helper.getInstance().getProgressDialog(this.getActivity(), "Please Wait...");

        ImageView imagePensionerCode = view.findViewById(R.id.image_pcode);
        imagePensionerCode.setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.ic_person_black_24dp));
        ImageView imageNumber = view.findViewById(R.id.image_number);
        imageNumber.setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.ic_card_black_24dp));
        ImageView imageAttach = view.findViewById(R.id.image_attach);
        imageAttach.setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.ic_attach_file_black_24dp));


        inputNumber = view.findViewById(R.id.autocomplete_number);
        if (type == Helper.getInstance().UPLOAD_TYPE_ADHAAR) {
            //inputNumber.setHint("Aadhaar");
            inputNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
            inputNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
        } else {
            //inputNumber.setHint("PAN");
            inputNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        }

        inputPCode = view.findViewById(R.id.autocomplete_pcode);
        textViewFileName = view.findViewById(R.id.textview_file_name);
        buttonChooseFile = view.findViewById(R.id.button_attach);
        buttonChooseFile.setText(type == Helper.getInstance().UPLOAD_TYPE_ADHAAR ? "Select Aadhaar File" : "Select PAN File");
        buttonChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        buttonUpload = view.findViewById(R.id.button_upload);
        buttonUpload.setOnClickListener(new View.OnClickListener() {
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
                                Toast.makeText(getContext(), "Please Choose a file of 5mb or less", Toast.LENGTH_SHORT).show();
                                fileChosed = null;
                                fileChosedPath = null;
                            } else {
                                fileChosedPath = file.getAbsolutePath();
                                fileChosed = file.getName();
                                textViewFileName.setText(fileChosed);
                            }
                        }
                    }
                })
                .build()
                .show(getActivity().getSupportFragmentManager(), null);
    }

    private boolean checkInput() {
        pensionerCode = inputPCode.getText().toString();
        String trimmed = inputNumber.getText().toString().replaceAll("\\s", "");

        //If Pensioner imagePensionerCode is empty
        if (pensionerCode.trim().isEmpty()) {
            Toast.makeText(getContext(), "Pensioner Code required", Toast.LENGTH_SHORT).show();
            inputPCode.requestFocus();
            return false;
        }
        //If Aadhar Number is not complete
        else if ((type == Helper.getInstance().UPLOAD_TYPE_ADHAAR) && (trimmed.length() < 16)) {
            Toast.makeText(getContext(), "Enter a Valid Aadhaar Number", Toast.LENGTH_SHORT).show();
            inputNumber.requestFocus();
            return false;
        }
        //If PAN Number is not complete
        else if ((type == Helper.getInstance().UPLOAD_TYPE_PAN) && (trimmed.length() < 10)) {
            Toast.makeText(getContext(), "Enter a Valid Pan Number", Toast.LENGTH_SHORT).show();
            inputNumber.requestFocus();
            return false;
        }
        //if no file selected
        else if (fileChosed == null) {
            Toast.makeText(getContext(), "Select a file", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void confirmSubmission() {
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_confirm_submission, null);

        AlertDialog.Builder confirmDialog = PopUpWindows.getInstance().getConfirmationDialog(getActivity(), v);
        loadValues(v);

        confirmDialog.setPositiveButton("UPLOAD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                uploadAdhaarOrPan();
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
        TextView pNo = v.findViewById(R.id.textview_ppo_no);
        pNo.setText(pNo.getText() + " " + pensionerCode);
        TextView mobNo = v.findViewById(R.id.textview_mobile_no);
        mobNo.setText((type == Helper.getInstance().UPLOAD_TYPE_ADHAAR ? "Aadhaar No: " : "PAN No: ") + inputNumber.getText());
        TextView fileName = v.findViewById(R.id.textview_file_name);
        fileName.setText(fileChosed);
        v.findViewById(R.id.textview_grievance_type).setVisibility(View.GONE);
        v.findViewById(R.id.textview_grievance_by).setVisibility(View.GONE);
        v.findViewById(R.id.detail).setVisibility(View.GONE);
        v.findViewById(R.id.textview_grievance_details).setVisibility(View.GONE);
    }

    private void uploadAdhaarOrPan() {

        progressDialog.show();
        dbref = FireBaseHelper.getInstance().databaseReference.child(uploadType);
        //statusref = FireBaseHelper.getInstance().databaseReference.child(FireBaseHelper.getInstance().ROOT_PAN_STATUS);

        PanAdhaar panAdhaar = new PanAdhaar(pensionerCode, inputNumber.getText().toString(), fileChosed, Preferences.getInstance().getPrefState(getContext()));

        dbref.child(pensionerCode).setValue(panAdhaar).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    uploadFile();
                    /* statusref.child(imagePensionerCode).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            count = dataSnapshot.getChildrenCount();
                            PanAdhaarStatus panAdhaarStatus = new PanAdhaarStatus(new Date(), null, null, 0);
                            statusref.child(imagePensionerCode).child(String.valueOf(count + 1)).setValue(panAdhaarStatus).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(PanAdhaarUploadActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                    } else
                                        Toast.makeText(PanAdhaarUploadActivity.this, "Failure", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });*/
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Unable to Upload", Toast.LENGTH_SHORT).show();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void uploadFile() {
        UploadTask uploadTask;

        uploadTask = FireBaseHelper.getInstance().uploadFile(uploadType, pensionerCode, fileChosedPath, null);

        if (uploadTask != null) {
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getContext(), "Unable to Upload file", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onFailure: " + exception.getMessage());
                    progressDialog.dismiss();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getContext(), "Request sent for Upload", Toast.LENGTH_SHORT).show();
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Log.d(TAG, "onSuccess: " + downloadUrl);
                    progressDialog.dismiss();
                }
            });
        }
    }
}
