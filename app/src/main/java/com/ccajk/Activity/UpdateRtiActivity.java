package com.ccajk.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.ccajk.Adapter.RecyclerViewAdapterRTIUpdate;
import com.ccajk.CustomObjects.ProgressDialog;
import com.ccajk.Models.RtiModel;
import com.ccajk.R;
import com.ccajk.Tools.FireBaseHelper;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class UpdateRtiActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    RecyclerView recyclerView;
    RecyclerViewAdapterRTIUpdate adapter;
    ArrayList<RtiModel> rtiModelArrayList;

    String TAG = "Update";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_rti);
        init();
    }


    public void init() {
        recyclerView = findViewById(R.id.recyclerview_rti);
        rtiModelArrayList = new ArrayList<>();
        adapter = new RecyclerViewAdapterRTIUpdate(rtiModelArrayList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getRtiData();
    }

    private void getRtiData() {

        DatabaseReference dbref = FireBaseHelper.getInstance().databaseReference;
        dbref.child(FireBaseHelper.getInstance().ROOT_RTI).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue() != null) {
                    if (Integer.valueOf(dataSnapshot.child("status").getValue().toString()) < 2) {
                        RtiModel rtiModel = dataSnapshot.getValue(RtiModel.class);
                        Log.d(TAG, "onChildAdded: " + rtiModel.getName());
                        rtiModelArrayList.add(rtiModel);
                        adapter.notifyDataSetChanged();
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
