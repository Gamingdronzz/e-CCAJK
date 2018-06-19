package com.mycca.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mycca.Adapter.RecyclerViewAdapterTracking;
import com.mycca.CustomObjects.FancyAlertDialog.FancyAlertDialogType;
import com.mycca.CustomObjects.Progress.ProgressDialog;
import com.mycca.Listeners.ClickListener;
import com.mycca.Listeners.OnConnectionAvailableListener;
import com.mycca.Listeners.RecyclerViewTouchListeners;
import com.mycca.Models.GrievanceModel;
import com.mycca.R;
import com.mycca.Tools.ConnectionUtility;
import com.mycca.Tools.FireBaseHelper;
import com.mycca.Tools.Helper;

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
    long grievanceType = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_grievance_result);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Track Grievance");
        init();
    }

    private void init() {

        grievanceModelArrayList = new ArrayList<>();
        adapterTracking = new RecyclerViewAdapterTracking(grievanceModelArrayList, this);
        //adapterTracking.setHasStableIds(true);

        textView = findViewById(R.id.textview_tracking);
        textView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_exclamation, 0, 0);

        recyclerViewTrack = findViewById(R.id.recyclerview_tracking);
        recyclerViewTrack.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTrack.setAdapter(adapterTracking);
        recyclerViewTrack.addOnItemTouchListener(new RecyclerViewTouchListeners(this, recyclerViewTrack, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Log.d(TAG, "onClick: " + position);
                grievanceModelArrayList.get(position).setExpanded(!grievanceModelArrayList.get(position).getExpanded());
                //grievanceModelArrayList.get(position).setHighlighted(false);
                adapterTracking.notifyItemChanged(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        ManageNoGrievanceLayout(true);
        textView.setText(getResources().getString(R.string.no_grievance));

        progressDialog = Helper.getInstance().getProgressWindow(this, "Checking for Applied Grievances\n\nPlease Wait...");
        progressDialog.show();

        ConnectionUtility connectionUtility = new ConnectionUtility(new OnConnectionAvailableListener() {
            @Override
            public void OnConnectionAvailable() {
                getGrievancesOnConnectionAvailable();
            }

            @Override
            public void OnConnectionNotAvailable() {
                onConnectionNotAvailable();
                ManageNoGrievanceLayout(true);
                textView.setText(getResources().getString(R.string.no_internet));
            }
        });
        connectionUtility.checkConnectionAvailability();
    }

    private void onConnectionNotAvailable() {
        progressDialog.dismiss();
        Helper.getInstance().showFancyAlertDialog(this,
                "No Internet Connection\nTurn on Internet Connection First",
                "Track Grievance",
                "OK",
                null,
                null,
                null,
                FancyAlertDialogType.ERROR);
    }

    private void getGrievancesOnConnectionAvailable() {

        progressDialog.show();
        dbref = FireBaseHelper.getInstance(this).databaseReference;
        pensionerCode = getIntent().getStringExtra("Code");
        try {
            grievanceType = getIntent().getLongExtra("grievanceType", -1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        getGrievances();
    }

    private void getGrievances() {
        Log.d(TAG, "getGrievances for: " + pensionerCode);

        dbref.child(FireBaseHelper.ROOT_GRIEVANCES).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot1, String s) {
                try {
                    Log.d(TAG, "state key:" + dataSnapshot1.getKey());
                    dbref.child(FireBaseHelper.ROOT_GRIEVANCES).child(dataSnapshot1.getKey()).child(pensionerCode)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue() != null) {
                                        Log.d(TAG, "Datasnapshot: " + dataSnapshot);
                                        ManageNoGrievanceLayout(false);
                                        getGrievanceOfPensioner(dataSnapshot1.getKey());
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                    ManageNoGrievanceLayout(true);
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
        dbref.child(FireBaseHelper.ROOT_GRIEVANCES).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Dismissed Progress");
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getGrievanceOfPensioner(String key) {

        dbref.child(FireBaseHelper.ROOT_GRIEVANCES)
                .child(key)
                .child(pensionerCode)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Log.d(TAG, "grievance key:" + dataSnapshot.getKey());
                        try {
                            //int size = grievanceModelArrayList.size();
                            GrievanceModel model = dataSnapshot.getValue(GrievanceModel.class);

                            if (grievanceType != -1) {
                                if (model.getGrievanceType() == grievanceType) {
                                    model.setExpanded(true);
                                    model.setHighlighted(true);
                                }
                            }
                            grievanceModelArrayList.add(model);
                            Log.d(TAG, "arraylist size:" + grievanceModelArrayList.size());
                            adapterTracking.notifyDataSetChanged();
                        } catch (DatabaseException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        Log.d(TAG, "ChildChanged\n" + dataSnapshot);
                        GrievanceModel model = dataSnapshot.getValue(GrievanceModel.class);
                        int counter = 0;
                        for (GrievanceModel gm : grievanceModelArrayList) {
                            if (gm.getGrievanceType() == model.getGrievanceType()) {
                                grievanceModelArrayList.remove(gm);
                                model.setExpanded(true);
                                model.setHighlighted(true);
                                grievanceModelArrayList.add(counter, model);
                                break;
                            }
                            counter++;
                        }
                        adapterTracking.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                        progressDialog.dismiss();
                    }
                });
    }

    private void ManageNoGrievanceLayout(boolean show) {
        if (show) {
            recyclerViewTrack.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
        } else {
            recyclerViewTrack.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);
        }
    }
}

