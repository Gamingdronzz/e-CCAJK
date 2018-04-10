package com.ccajk.Activity;


import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ccajk.Models.PanAdhaar;
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

public class PanAdhaarUploadActivity extends AppCompatActivity {

    private static final String TAG = "PanAdhaarUpload";
    ImageView pcode, cardImage, attach;
    TextView filename;
    AutoCompleteTextView pensionerCode, number;
    DatabaseReference dbref;
    Button upload, chooseFile;
    String code, fileChosed;
    int type;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pan_adhaar_upload);
        type = getIntent().getIntExtra("UploadType", -1);
        code = getIntent().getStringExtra("PensionerCode");
        getSupportActionBar().setTitle((type == Helper.getInstance().UPLOAD_TYPE_ADHAAR ? "Aadhaar Updation" : "Pan Updation"));
        init();
    }

    private void init() {
        pcode = findViewById(R.id.image_pcode);
        pcode.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_person_black_24dp));
        cardImage = findViewById(R.id.image_number);
        cardImage.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_card_black_24dp));
        attach = findViewById(R.id.image_attach);
        attach.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_attach_file_black_24dp));

        pensionerCode = findViewById(R.id.autocomplete_pcode);
        pensionerCode.setText(code);

        number = findViewById(R.id.autocomplete_number);
        if (type == Helper.getInstance().UPLOAD_TYPE_ADHAAR) {
            number.setHint("Aadhaar Number");
            number.setInputType(InputType.TYPE_CLASS_NUMBER);
            number.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
        } else {
            number.setHint("PAN Number");
            number.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        }

        filename = findViewById(R.id.textview_file_name);
        chooseFile = findViewById(R.id.button_attach);
        chooseFile.setText(type == Helper.getInstance().UPLOAD_TYPE_ADHAAR ? "Select Aadhaar File" : "Select PAN File");
        chooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        upload = findViewById(R.id.button_upload);
        upload.setOnClickListener(new View.OnClickListener() {
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
                                Toast.makeText(PanAdhaarUploadActivity.this, "Please Choose a file of 5mb or less", Toast.LENGTH_SHORT).show();
                                fileChosed = null;
                            } else {
                                filename.setText(file.getName());
                                fileChosed = file.getAbsolutePath();
                            }
                        }
                    }
                })
                .build()
                .show(getSupportFragmentManager(), null);
    }

    private boolean checkInput() {
        String trimmed = number.getText().toString().replaceAll("\\s", "");

        //If Pensioner code is empty
        if (pensionerCode.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Pensioner Code required", Toast.LENGTH_SHORT).show();
            pensionerCode.requestFocus();
            return false;


        }
        //If Aadhar number is not complete
        else if ((type == Helper.getInstance().UPLOAD_TYPE_ADHAAR) && (trimmed.length() < 16)) {
            Toast.makeText(this, "Enter a Valid Aadhaar Number", Toast.LENGTH_SHORT).show();
            number.requestFocus();
            return false;
        }
        //If PAN number is not complete
        else if ((type == Helper.getInstance().UPLOAD_TYPE_PAN) && (trimmed.length() < 10)) {
            Toast.makeText(this, "Enter a Valid Pan Number", Toast.LENGTH_SHORT).show();
            number.requestFocus();
            return false;
        } else if (fileChosed == null) {
            Toast.makeText(this, "Select a file", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void confirmSubmission() {
        AlertDialog.Builder confirmDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_confirm_submission, null);
        confirmDialog.setView(v);
        loadValues(v);
        confirmDialog.setTitle("Confirm Input Before Submission");
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
        pNo.setText(pNo.getText() + " " + pensionerCode.getText());
        TextView mobNo = v.findViewById(R.id.textview_mobile_no);
        mobNo.setText((type == Helper.getInstance().UPLOAD_TYPE_ADHAAR ? "Aadhaar No: " : "PAN No: ") + number.getText());
        v.findViewById(R.id.textview_grievance_type).setVisibility(View.GONE);
        v.findViewById(R.id.textview_grievance_by).setVisibility(View.GONE);
        v.findViewById(R.id.detail).setVisibility(View.GONE);
        v.findViewById(R.id.textview_grievance_details).setVisibility(View.GONE);
        TextView fileName = v.findViewById(R.id.textview_file_name);
        fileName.setText(fileName.getText() + fileChosed);
    }

    private void uploadAdhaarOrPan() {

        if (type == Helper.getInstance().UPLOAD_TYPE_ADHAAR) {
            dbref = FireBaseHelper.getInstance().databaseReference.child(FireBaseHelper.getInstance().ROOT_ADHAAR);
            //statusref = FireBaseHelper.getInstance().databaseReference.child(FireBaseHelper.getInstance().ROOT_ADHAAR_STATUS);
        } else {
            dbref = FireBaseHelper.getInstance().databaseReference.child(FireBaseHelper.getInstance().ROOT_PAN);
            //statusref = FireBaseHelper.getInstance().databaseReference.child(FireBaseHelper.getInstance().ROOT_PAN_STATUS);
        }

        final PanAdhaar panAdhaar = new PanAdhaar(code, number.getText().toString(), "FileName", Preferences.getInstance().getPrefState(this), new Date(), 0, null);

        dbref.child(code).setValue(panAdhaar).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(PanAdhaarUploadActivity.this, "Success", Toast.LENGTH_SHORT).show();
                   /* statusref.child(code).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            count = dataSnapshot.getChildrenCount();
                            PanAdhaarStatus panAdhaarStatus = new PanAdhaarStatus(new Date(), null, null, 0);
                            statusref.child(code).child(String.valueOf(count + 1)).setValue(panAdhaarStatus).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                    Toast.makeText(PanAdhaarUploadActivity.this, "Unable to update", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}




