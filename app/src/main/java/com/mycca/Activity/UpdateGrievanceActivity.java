package com.mycca.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mycca.Adapter.RecyclerViewAdapterGrievanceUpdate;
import com.mycca.CustomObjects.FancyAlertDialog.FancyAlertDialogType;
import com.mycca.CustomObjects.Progress.ProgressDialog;
import com.mycca.Listeners.OnConnectionAvailableListener;
import com.mycca.Models.GrievanceModel;
import com.mycca.Notification.Constants;
import com.mycca.Notification.FirebaseNotificationHelper;
import com.mycca.Providers.GrievanceDataProvider;
import com.mycca.R;
import com.mycca.Tools.ConnectionUtility;
import com.mycca.Tools.FireBaseHelper;
import com.mycca.Tools.Helper;
import com.mycca.Tools.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class UpdateGrievanceActivity extends AppCompatActivity {

    TextView textViewPensionerCode, textViewGrievanceString, textViewDateOfApplication;
    Spinner statusSpinner;
    EditText editTextMessage;
    Button update;
    ProgressDialog progressDialog;

    DatabaseReference dbref = FireBaseHelper.getInstance(this).databaseReference;

    String TAG = "Update";
    GrievanceModel grievanceModel;


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
        update.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_update_black_24dp, 0, 0);
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
                        showNoInternetConnectionDialog();
                    }
                });
                connectionUtility.checkConnectionAvailability();

            }
        });


    }

    private void showNoInternetConnectionDialog() {
        Helper.getInstance().showFancyAlertDialog(this,
                "No Internet Connection\nTurn on Internet Connection to Update grievance",
                "Update Grievance",
                "OK",
                null,
                null,
                null,
                FancyAlertDialogType.ERROR);
    }


    private void setLayoutData() {
        textViewPensionerCode.setText(grievanceModel.getPensionerIdentifier());
        textViewGrievanceString.setText(Helper.getInstance().getGrievanceString(grievanceModel.getGrievanceType()));
        textViewDateOfApplication.setText(Helper.getInstance().formatDate(grievanceModel.getDate(), "MMM d, yyyy"));
        statusSpinner.setSelection((int) grievanceModel.getGrievanceStatus());
        editTextMessage.setText(grievanceModel.getMessage() == null ? "" : grievanceModel.getMessage());
    }


    private void updateGrievance(final String message, final int status) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("grievanceStatus", status);
        hashMap.put("message", message);


        try {
            dbref.child(FireBaseHelper.ROOT_GRIEVANCES)
                    .child(Preferences.getInstance().getStringPref(this, Preferences.PREF_STATE))
                    .child(grievanceModel.getPensionerIdentifier())
                    .child(String.valueOf(grievanceModel.getGrievanceType()))
                    .updateChildren(hashMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                Toast.makeText(UpdateGrievanceActivity.this, "Successfully Updated", Toast.LENGTH_LONG).show();

                                GrievanceDataProvider.getInstance().selectedGrievance.setGrievanceStatus(status);
                                GrievanceDataProvider.getInstance().selectedGrievance.setMessage(message);
                                GrievanceDataProvider.getInstance().selectedGrievance.setExpanded(false);

                                notifyPensioner();
                                setResult(Activity.RESULT_OK);
                                finishActivity(RecyclerViewAdapterGrievanceUpdate.REQUEST_UPDATE);
                                finish();
                            } else {
                                Log.d(TAG, "onComplete: " + task.toString());
                            }
                        }
                    });
        } catch (DatabaseException dbe) {
            dbe.printStackTrace();
        }
    }


    private void notifyPensioner() {

        dbref.child("FCMServerKey").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String fcmKey = (String) dataSnapshot.getValue();
                Log.d(TAG, "Notification : Key recieved");
                getTokenAndSendNotification(fcmKey);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getTokenAndSendNotification(final String fcmKey) {
        dbref.child(FireBaseHelper.getInstance(this).ROOT_TOKEN)
                .child(GrievanceDataProvider.getInstance().selectedGrievance.getPensionerIdentifier())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String token = (String) dataSnapshot.getValue();
                        Log.d(TAG, "Notification : Token recieved");
                        sendNotification(fcmKey, token);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void sendNotification(String fcmKey, String token) {

        FirebaseNotificationHelper.initialize(fcmKey)
                .defaultJson(false, getJsonBody())

                .receiverFirebaseToken(token)
                .send();



    }

    private String getJsonBody() {

        String newStatus = Helper.getInstance().getStatusString(GrievanceDataProvider.getInstance().selectedGrievance.getGrievanceStatus());
        String type = Helper.getInstance().getGrievanceString(GrievanceDataProvider.getInstance().selectedGrievance.getGrievanceType());
        JSONObject jsonObjectData = new JSONObject();
        try {
            jsonObjectData.put(Constants.KEY_TITLE,"Grievance status updated");
            jsonObjectData.put(Constants.KEY_TEXT,"Your grievance for " + type + " is " + newStatus);
            jsonObjectData.put("pensionerCode", textViewPensionerCode.getText());
            jsonObjectData.put("grievanceType", GrievanceDataProvider.getInstance().selectedGrievance.getGrievanceType());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return jsonObjectData.toString();
    }


}
