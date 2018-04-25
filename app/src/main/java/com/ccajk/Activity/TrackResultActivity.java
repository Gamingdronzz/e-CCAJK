package com.ccajk.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.ccajk.Adapter.RecyclerViewAdapterTracking;
import com.ccajk.CustomObjects.ProgressDialog;
import com.ccajk.Models.Grievance;
import com.ccajk.R;
import com.ccajk.Tools.FireBaseHelper;
import com.ccajk.Tools.Helper;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TrackResultActivity extends AppCompatActivity {

    RecyclerView recyclerViewTrack;
    TextView textView;
    ArrayList<Grievance> grievanceArrayList;
    RecyclerViewAdapterTracking adapterTracking;
    DatabaseReference dbref;
    String pensionerCode;
    ProgressDialog progressDialog;
    final String TAG = "Track";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_result);
        init();
    }

   private void init() {
       progressDialog = Helper.getInstance().getProgressWindow(this, "Checking for Applied Grievances\n\nPlease Wait...");
       progressDialog.show();

       dbref = FireBaseHelper.getInstance().databaseReference;
        pensionerCode = getIntent().getStringExtra("Code");
        Log.d(TAG, "init: pcode = " + pensionerCode);
        grievanceArrayList = new ArrayList<>();
        getGrievances();
        adapterTracking = new RecyclerViewAdapterTracking(grievanceArrayList);
        textView = findViewById(R.id.textview_tracking);
        recyclerViewTrack = findViewById(R.id.recyclerview_tracking);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewTrack.setLayoutManager(linearLayoutManager);
        recyclerViewTrack.setAdapter(adapterTracking);
    }


    private void getGrievances() {
        dbref.child(FireBaseHelper.getInstance().ROOT_GRIEVANCES).child(pensionerCode)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        int size = grievanceArrayList.size();
                        grievanceArrayList.add(size,dataSnapshot.getValue(Grievance.class));
                        adapterTracking.notifyItemInserted(size);
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
        dbref.child(FireBaseHelper.getInstance().ROOT_GRIEVANCES).child(pensionerCode)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        progressDialog.dismiss();
                        if (grievanceArrayList.size() == 0)
                            textView.setText("No Grievances Registered");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}