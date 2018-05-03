package com.ccajk.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ccajk.Adapter.RecyclerViewAdapterGrievanceUpdate;
import com.ccajk.CustomObjects.ProgressDialog;
import com.ccajk.Listeners.OnConnectionAvailableListener;
import com.ccajk.Models.GrievanceModel;
import com.ccajk.Providers.GrievanceDataProvider;
import com.ccajk.R;
import com.ccajk.Tools.ConnectionUtility;
import com.ccajk.Tools.FireBaseHelper;
import com.ccajk.Tools.Helper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

public class UpdateGrievanceActivity extends AppCompatActivity {

    TextView textViewPensionerCode, textViewGrievanceString, textViewDateOfApplication;
    Spinner statusSpinner;
    EditText editTextMessage;
    Button update;
    ProgressDialog progressDialog;

    String TAG = "Update";
    GrievanceModel grievanceModel;
    Intent resultIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_grievance);

        grievanceModel = GrievanceDataProvider.getInstance().selectedGrievance;
        bindViews();
        init();
        setLayoutData();

    }

    private void bindViews() {
        textViewPensionerCode = findViewById(R.id.textview_pensioner);
        textViewGrievanceString = findViewById(R.id.textview_grievance_type);
        textViewDateOfApplication = findViewById(R.id.textview_date);
        statusSpinner = findViewById(R.id.spinner_status);
        editTextMessage = findViewById(R.id.edittext_message);
        update = findViewById(R.id.button_update);
        update.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_update_black_24dp,0,0,0);
        progressDialog = Helper.getInstance().getProgressWindow(this, "Updating Grievance Details");
    }

    private void init() {

        ArrayAdapter arrayAdapter = new ArrayAdapter(this,
                R.layout.simple_spinner,
                Helper.getInstance().getStatusList());
        statusSpinner.setAdapter(arrayAdapter);


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectionUtility connectionUtility = new ConnectionUtility(new OnConnectionAvailableListener() {
                    @Override
                    public void OnConnectionAvailable() {
                        String message = editTextMessage.getText().toString().trim();
                        int status = statusSpinner.getSelectedItemPosition();
                        updateGrievance(message, status);
                        progressDialog.show();
                    }

                    @Override
                    public void OnConnectionNotAvailable() {
                        Helper.getInstance().showAlertDialog(
                                getBaseContext(),
                                "Internet Connection Not Available\nTurn on internet to update",
                                "No Internet Connection",
                                "OK");
                    }
                });
                connectionUtility.checkConnectionAvailability();

            }
        });


    }

    private void setLayoutData() {
        textViewPensionerCode.setText(grievanceModel.getPensionerIdentifier());
        textViewGrievanceString.setText(Helper.getInstance().getGrievanceString(grievanceModel.getGrievanceType()));
        textViewDateOfApplication.setText(Helper.getInstance().formatDate(grievanceModel.getDate(), "MMM d, yyyy"));
        statusSpinner.setSelection((int) grievanceModel.getGrievanceStatus());
        editTextMessage.setText("");
    }


    private void updateGrievance(final String message, final int status) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("grievanceStatus", status);
        hashMap.put("message", message);

        DatabaseReference dbref = FireBaseHelper.getInstance().databaseReference;
        dbref.child(FireBaseHelper.getInstance().ROOT_GRIEVANCES).child(grievanceModel.getPensionerIdentifier())
                .child(String.valueOf(grievanceModel.getGrievanceType())).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(UpdateGrievanceActivity.this, "Successfully Updated", Toast.LENGTH_LONG).show();

                    GrievanceDataProvider.getInstance().selectedGrievance.setGrievanceStatus(status);
                    GrievanceDataProvider.getInstance().selectedGrievance.setMessage(message);
                    GrievanceDataProvider.getInstance().selectedGrievance.setExpanded(false);

                    setResult(Activity.RESULT_OK);
                    finishActivity(RecyclerViewAdapterGrievanceUpdate.REQUEST_UPDATE);
                    finish();
                }

            }
        });
    }
}
