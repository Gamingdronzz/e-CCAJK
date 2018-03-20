package com.ccajk.Activity;


import android.content.DialogInterface;
import android.os.Bundle;
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
import com.ccajk.Models.PanAdhaarStatus;
import com.ccajk.R;
import com.ccajk.Tools.FireBaseHelper;
import com.ccajk.Tools.Helper;
import com.ccajk.Tools.Prefrences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class PanAdhaarUploadActivity extends AppCompatActivity {

    private static final String TAG = "PanAdhaarUpload";
    ImageView pcode, cardImage, attach;
    AutoCompleteTextView pensionerCode, number;
    DatabaseReference dbref, statusref;
    Button upload, chooseFile;
    int type;
    long count;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pan_adhaar_upload);
        type = getIntent().getIntExtra("UploadType", -1);
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

        number = findViewById(R.id.autocomplete_number);
        if (type == Helper.getInstance().UPLOAD_TYPE_ADHAAR) {
            number.setHint("Aadhaar Number");
            number.setInputType(InputType.TYPE_CLASS_NUMBER);
            number.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
        } else {
            number.setHint("PAN Number");
            number.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        }

        chooseFile = findViewById(R.id.button_attach);
        chooseFile.setText(type == Helper.getInstance().UPLOAD_TYPE_ADHAAR ? "Select Aadhaar File" : "Select PAN File");

        upload = findViewById(R.id.button_upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput())
                    confirmSubmission();
            }
        });
    }

    private boolean checkInput() {
        String trimmed=number.getText().toString().replaceAll("\\s","");
        if (pensionerCode.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Pensioner Code required", Toast.LENGTH_SHORT).show();
            pensionerCode.requestFocus();
            return false;
        } else if ((type == Helper.getInstance().UPLOAD_TYPE_ADHAAR) && (trimmed.length() < 16)) {
            Toast.makeText(this, "Enter a Valid Aadhaar Number", Toast.LENGTH_SHORT).show();
            number.requestFocus();
            return false;
        } else if ((type == Helper.getInstance().UPLOAD_TYPE_PAN) && (trimmed.length() < 10)) {
            Toast.makeText(this, "Enter a Valid Pan Number", Toast.LENGTH_SHORT).show();
            number.requestFocus();
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
        TextView ppoNo = v.findViewById(R.id.textview_ppo_no);
        ppoNo.setText(ppoNo.getText() + " " + pensionerCode.getText());
        TextView mobNo = v.findViewById(R.id.textview_mobile_no);
        mobNo.setText((type == Helper.getInstance().UPLOAD_TYPE_ADHAAR ? "Aadhaar No: " : "PAN No: ") + number.getText());
        v.findViewById(R.id.textview_grievance_type).setVisibility(View.GONE);
        v.findViewById(R.id.textview_grievance_by).setVisibility(View.GONE);
        v.findViewById(R.id.detail).setVisibility(View.GONE);
        v.findViewById(R.id.textview_grievance_details).setVisibility(View.GONE);
        TextView fileName = v.findViewById(R.id.textview_file_name);
        fileName.setText(fileName.getText() + " File Name");
    }

    private void uploadAdhaarOrPan() {
        final String code = pensionerCode.getText().toString();
        if (type == Helper.getInstance().UPLOAD_TYPE_ADHAAR) {
            dbref = FireBaseHelper.getInstance().databaseReference.child(FireBaseHelper.getInstance().ROOT_ADHAAR);
            statusref = FireBaseHelper.getInstance().databaseReference.child(FireBaseHelper.getInstance().ROOT_ADHAAR_STATUS);
        } else {
            dbref = FireBaseHelper.getInstance().databaseReference.child(FireBaseHelper.getInstance().ROOT_PAN);
            statusref = FireBaseHelper.getInstance().databaseReference.child(FireBaseHelper.getInstance().ROOT_PAN_STATUS);
        }

        final PanAdhaar panAdhaar = new PanAdhaar(code, number.getText().toString(), "FileName", Prefrences.getInstance().getPrefState(this));

        dbref.child(code).setValue(panAdhaar).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    statusref.child(code).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            count = dataSnapshot.getChildrenCount();
                            PanAdhaarStatus panAdhaarStatus = new PanAdhaarStatus(new Date(), null, null, "Request Submitted");
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
                    });
                } else {
                    Toast.makeText(PanAdhaarUploadActivity.this, "Unable to update", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}




