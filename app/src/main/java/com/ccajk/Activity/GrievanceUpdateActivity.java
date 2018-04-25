package com.ccajk.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.ccajk.CustomObjects.ProgressDialog;
import com.ccajk.Models.Grievance;
import com.ccajk.Models.RtiModel;
import com.ccajk.R;
import com.ccajk.Tools.FireBaseHelper;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class GrievanceUpdateActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    DatabaseReference dbref;
    ArrayList<Grievance> grievanceArrayList;
    ArrayList<RtiModel> rtiModelArrayList;

    String TAG = "Update";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grievance_update);
        init();
        getData();
    }

    private void init() {
        //progressDialog = Helper.getInstance().getProgressWindow(this, "Checking for Applied Grievances\n\nPlease Wait...");
        //progressDialog.show();
        dbref = FireBaseHelper.getInstance().databaseReference;
    }

    private void getData() {
        grievanceArrayList = new ArrayList<>();
        dbref.child(FireBaseHelper.getInstance().ROOT_GRIEVANCES).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (Integer.valueOf(ds.child("grievanceStatus").getValue().toString()) < 2) {
                            Grievance grievance = ds.getValue(Grievance.class);
                            Log.d(TAG, "onChildAdded: " + grievance.getDetails());
                            grievanceArrayList.add(grievance);
                        }
                    }
                    Log.d(TAG, "onChildAdded: " + grievanceArrayList);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getRtiData() {
        rtiModelArrayList = new ArrayList<>();
        dbref.child(FireBaseHelper.getInstance().ROOT_RTI).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue() != null) {
                    if (Integer.valueOf(dataSnapshot.child("status").getValue().toString()) < 2) {
                        RtiModel rtiModel = dataSnapshot.getValue(RtiModel.class);
                        Log.d(TAG, "onChildAdded: " + rtiModel.getName());
                        rtiModelArrayList.add(rtiModel);
                    }
                }
                Log.d(TAG, "onChildAdded: " + rtiModelArrayList);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
