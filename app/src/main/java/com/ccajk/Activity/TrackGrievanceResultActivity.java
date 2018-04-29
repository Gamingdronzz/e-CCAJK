package com.ccajk.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ccajk.Adapter.RecyclerViewAdapterTracking;
import com.ccajk.CustomObjects.ProgressDialog;
import com.ccajk.Listeners.ClickListener;
import com.ccajk.Listeners.RecyclerViewTouchListeners;
import com.ccajk.Models.GrievanceModel;
import com.ccajk.R;
import com.ccajk.Tools.FireBaseHelper;
import com.ccajk.Tools.Helper;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TrackGrievanceResultActivity extends AppCompatActivity {

    RecyclerView recyclerViewTrack;
    TextView textView;
    ArrayList<GrievanceModel> grievanceModelArrayList;
    RecyclerViewAdapterTracking adapterTracking;
    DatabaseReference dbref;
    String pensionerCode;
    ProgressDialog progressDialog;
    final String TAG = "Track";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_grievance_result);
        init();
    }

   private void init() {
       progressDialog = Helper.getInstance().getProgressWindow(this, "Checking for Applied Grievances\n\nPlease Wait...");
       progressDialog.show();

       dbref = FireBaseHelper.getInstance().databaseReference;
        pensionerCode = getIntent().getStringExtra("Code");
        Log.d(TAG, "init: pcode = " + pensionerCode);
        grievanceModelArrayList = new ArrayList<>();
        getGrievances();
        adapterTracking = new RecyclerViewAdapterTracking(grievanceModelArrayList);
        textView = findViewById(R.id.textview_tracking);
        recyclerViewTrack = findViewById(R.id.recyclerview_tracking);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewTrack.setLayoutManager(linearLayoutManager);
        recyclerViewTrack.setAdapter(adapterTracking);

        recyclerViewTrack.addOnItemTouchListener(new RecyclerViewTouchListeners(this, recyclerViewTrack, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Log.d(TAG, "onClick: " + position);
                grievanceModelArrayList.get(position).setExpanded(!grievanceModelArrayList.get(position).isExpanded());
                adapterTracking.notifyItemChanged(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }


    private void getGrievances() {
        dbref.child(FireBaseHelper.getInstance().ROOT_GRIEVANCES).child(pensionerCode)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        //int size = grievanceModelArrayList.size();
                        grievanceModelArrayList.add(dataSnapshot.getValue(GrievanceModel.class));
                        //adapterTracking.notifyItemInserted(size);
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
                        if (grievanceModelArrayList.size() == 0)
                            textView.setText("No Grievances Registered");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}