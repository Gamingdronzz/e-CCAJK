package com.ccajk.Fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.content.res.AppCompatResources;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class PanAdhaarUploadFragment extends Fragment {

    private static final String TAG = "PanAdhaarUpload";
    ImageView pcode, cardImage, attach;
    AutoCompleteTextView pensionerCode, number;
    DatabaseReference dbref, statusref;
    Button upload, chooseFile;
    int type;
    long count;

    public PanAdhaarUploadFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pan_adhaar_upload, container, false);
        Bundle bundle = this.getArguments();
        type = bundle.getInt("UploadType");
        init(view);
        return view;
    }

    private void init(View view) {
        pcode = view.findViewById(R.id.image_pcode);
        pcode.setImageDrawable(AppCompatResources.getDrawable(this.getContext(), R.drawable.ic_person_black_24dp));
        cardImage = view.findViewById(R.id.image_number);
        cardImage.setImageDrawable(AppCompatResources.getDrawable(this.getContext(), R.drawable.ic_card_black_24dp));
        attach = view.findViewById(R.id.image_attach);
        attach.setImageDrawable(AppCompatResources.getDrawable(this.getContext(), R.drawable.ic_attach_file_black_24dp));

        pensionerCode = view.findViewById(R.id.autocomplete_pcode);

        number = view.findViewById(R.id.autocomplete_number);
        if (type == Helper.getInstance().UPLOAD_TYPE_ADHAAR) {
            number.setHint("Aadhaar Number");
            number.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
        } else {
            number.setHint("PAN Number");
            number.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
        }

        chooseFile = view.findViewById(R.id.button_attach);
        chooseFile.setText(type == Helper.getInstance().UPLOAD_TYPE_ADHAAR ? "Select Aadhaar File" : "Select PAN File");

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
        } else if (number.getText().toString().trim().isEmpty()) {
            if (type == Helper.getInstance().UPLOAD_TYPE_ADHAAR)
                Toast.makeText(this.getContext(), "Aadhaar No required", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this.getContext(), "PAN No required", Toast.LENGTH_SHORT).show();
            number.requestFocus();
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
            dbref = FireBaseHelper.getInstance().databaseReference.child("Adhaar");
            statusref = FireBaseHelper.getInstance().databaseReference.child("Adhaar-Status");
        } else {
            dbref = FireBaseHelper.getInstance().databaseReference.child("Pan");
            statusref = FireBaseHelper.getInstance().databaseReference.child("Pan-Status");
        }

        final PanAdhaar panAdhaar = new PanAdhaar(code, number.getText().toString(), "FileName", Prefrences.getInstance().getPrefState(getContext()));

        dbref.child(code).setValue(panAdhaar).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getContext(), "Failure", Toast.LENGTH_SHORT).show();
            }
        });
        statusref.child(code).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                count = dataSnapshot.getChildrenCount();
                PanAdhaarStatus panAdhaarStatus=new PanAdhaarStatus(new Date(),new Date(),new Date(),"Submitted");
                statusref.child(code).child(String.valueOf(count+1)).setValue(panAdhaarStatus).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}




