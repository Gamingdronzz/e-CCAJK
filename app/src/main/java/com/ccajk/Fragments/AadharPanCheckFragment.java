package com.ccajk.Fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ccajk.Activity.PanAdhaarHistoryActivity;
import com.ccajk.Activity.PanAdhaarUploadActivity;
import com.ccajk.R;
import com.ccajk.Tools.FireBaseHelper;
import com.ccajk.Tools.Helper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class AadharPanCheckFragment extends Fragment {
    int type;
    String typeName;
    Button upload, details, check;
    TextInputEditText pcode;
    TextView message;
    LinearLayout linearLayout;
    LinearLayout linearLayoutButton;
    DatabaseReference dbref, numberRef;
    ProgressDialog progressDialog;
    public AadharPanCheckFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_aadhar_pan_check, container, false);
        Bundle bundle = this.getArguments();
        type = bundle.getInt("UploadType");
        init(view, type);
        return view;
    }

    private void init(View view, final int type) {
        pcode = view.findViewById(R.id.edittext_pcode);
        linearLayout = view.findViewById(R.id.layout2);
        message = view.findViewById(R.id.textView_message);
        progressDialog = new ProgressDialog(view.getContext());
        linearLayoutButton = view.findViewById(R.id.button_layout);
        upload = view.findViewById(R.id.btn_upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PanAdhaarUploadActivity.class);
                intent.putExtra("UploadType", type);
                intent.putExtra("PensionerCode", pcode.getText().toString());
                startActivity(intent);
            }
        });

        details = view.findViewById(R.id.btn_view);
        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PanAdhaarHistoryActivity.class);
                intent.putExtra("UploadType", type);
                intent.putExtra("PensionerCode", pcode.getText().toString());
                startActivity(intent);
            }
        });

        check = view.findViewById(R.id.btn_check_status);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pcode.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getContext(), "Please enter a pensioner code", Toast.LENGTH_SHORT).show();
                } else {
                    check.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);
                    checkStatus();
                }
            }
        });

      ManageLayouts();

    }

    private void ManageLayouts()
    {
        linearLayoutButton.setVisibility(View.GONE);
        upload.setVisibility(View.GONE);
        details.setVisibility(View.GONE);
        message.setVisibility(View.GONE);
    }

    private void checkStatus() {
        progressDialog.show();
        if (type == Helper.getInstance().UPLOAD_TYPE_ADHAAR) {
            dbref = FireBaseHelper.getInstance().databaseReference.child(FireBaseHelper.getInstance().ROOT_ADHAAR_STATUS).child(pcode.getText().toString());
            numberRef = FireBaseHelper.getInstance().databaseReference.child(FireBaseHelper.getInstance().ROOT_ADHAAR).child(pcode.getText().toString());
            typeName = "Aadhaar";
            progressDialog.setMessage("Checking Aadhar Verification Status\nPlease Wait...");
        } else {
            dbref = FireBaseHelper.getInstance().databaseReference.child(FireBaseHelper.getInstance().ROOT_PAN_STATUS).child(pcode.getText().toString());
            numberRef = FireBaseHelper.getInstance().databaseReference.child(FireBaseHelper.getInstance().ROOT_PAN).child(pcode.getText().toString());
            typeName = "PAN";
            progressDialog.setMessage("Checking PAN Verification Status\nPlease Wait...");
        }
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() > 0) {
                    dbref.child(String.valueOf(dataSnapshot.getChildrenCount())).child("status").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            long status = (long) dataSnapshot.getValue();
                            setMessageAndAction(status);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d( "onCancelled: ",databaseError.getMessage());
                            setMessageAndAction(100);
                        }
                    });
                } else {
                    setMessageAndAction(-1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setMessageAndAction(long status) {
       ManageLayouts();
        message.setVisibility(View.VISIBLE);
        switch ((int) status) {
            case -1:
                message.setText("Your " + typeName + " Number is not updated");
                linearLayoutButton.setVisibility(View.VISIBLE);
                upload.setVisibility(View.VISIBLE);

                break;
            case 0:
            case 1:
                message.setText("Your " + typeName + " Number Updation is under process");
                linearLayoutButton.setVisibility(View.VISIBLE);
                details.setVisibility(View.VISIBLE);
                break;
            case 2:
                message.setText("Your " + typeName + " Number Updation Failed");
                linearLayoutButton.setVisibility(View.VISIBLE);
                upload.setVisibility(View.VISIBLE);
                details.setVisibility(View.VISIBLE);
                break;
            case 3:
                numberRef.child("number").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String num = (String) dataSnapshot.getValue();
                        message.setText("Your " + typeName + " Number is updated\n\n" + typeName + " = " + num);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                break;
            case 100:
                message.setText("Some Error Occured. Please Try Again");
                check.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.GONE);
        }

        progressDialog.dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();
        upload.setVisibility(View.GONE);
        details.setVisibility(View.GONE);
        linearLayout.setVisibility(View.GONE);
        check.setVisibility(View.VISIBLE);
    }
}
