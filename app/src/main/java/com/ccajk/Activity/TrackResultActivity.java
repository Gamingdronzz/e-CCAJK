package com.ccajk.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ccajk.Adapter.RecyclerViewAdapterTracking;
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
    ArrayList<Grievance> grievances;
    RecyclerViewAdapterTracking adapterTracking;
    DatabaseReference dbref;
    String pensionerCode;
    PopupWindow progressDialog;
    final String TAG = "Track";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_result);
        init();

    }

    private void init() {
        progressDialog = Helper.getInstance().getProgressWindow(this, "Please Wait...");
        if(progressDialog == null)
        {
            Log.d(TAG, "init: null");
        }
        else
        {
            progressDialog.showAtLocation(findViewById(R.id.parent_track_result), Gravity.CENTER,0,0);
        }

        dbref = FireBaseHelper.getInstance().databaseReference;
        pensionerCode = getIntent().getStringExtra("pensionerCode");
        grievances = new ArrayList<>();
        getGrievances();
        adapterTracking = new RecyclerViewAdapterTracking(grievances);
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
                        grievances.add(dataSnapshot.getValue(Grievance.class));
                        adapterTracking.notifyDataSetChanged();
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
                        if (grievances.size() == 0)
                            textView.setText("No Grievances Registered");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}