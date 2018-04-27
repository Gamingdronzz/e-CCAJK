package com.ccajk.Activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ccajk.Adapter.RecyclerViewAdapterGrievanceUpdate;
import com.ccajk.CustomObjects.ProgressDialog;
import com.ccajk.Listeners.ClickListener;
import com.ccajk.Listeners.OnConnectionAvailableListener;
import com.ccajk.Listeners.RecyclerViewTouchListeners;
import com.ccajk.Models.GrievanceModel;
import com.ccajk.R;
import com.ccajk.Tools.ConnectionUtility;
import com.ccajk.Tools.FireBaseHelper;
import com.ccajk.Tools.Helper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class UpdateGrievanceActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    LinearLayout linearLayout;
    TextView tvPensioner, tvGrievance, tvDate, tvNoData;
    Spinner statusSpinner;
    EditText editTextMessage;
    Button update;
    ProgressDialog progressDialog;

    RecyclerViewAdapterGrievanceUpdate adapter;
    ArrayList<GrievanceModel> grievanceModelArrayList;

    String TAG = "Update";
    GrievanceModel grievanceModel;
    boolean recyclerVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_grievance);
        bindViews();
        init();
    }

    @Override
    public void onBackPressed() {
        if (recyclerVisible)
            super.onBackPressed();
        else
            setRecyclerVisiblity(true);
    }

    public void setRecyclerVisiblity(boolean visible) {
        if (visible) {
            recyclerView.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.VISIBLE);
            recyclerVisible = true;
            linearLayout.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            tvNoData.setVisibility(View.GONE);
            recyclerVisible = false;
            linearLayout.setVisibility(View.VISIBLE);
        }
    }

    private void bindViews() {
        linearLayout = findViewById(R.id.layout_grievance_update);
        recyclerView = findViewById(R.id.recyclerview_grievance);
        tvNoData = findViewById(R.id.textview_no_data_msg);
        tvPensioner = findViewById(R.id.textview_pensioner);
        tvGrievance = findViewById(R.id.textview_grievance_type);
        tvDate = findViewById(R.id.textview_date);
        statusSpinner = findViewById(R.id.spinner_status);
        editTextMessage = findViewById(R.id.edittext_message);
        update = findViewById(R.id.button_update);
    }

    private void init() {

        setRecyclerVisiblity(true);

        progressDialog = Helper.getInstance().getProgressWindow(this, "Checking for Applied Grievances\n\nPlease Wait...");
        progressDialog.show();

        ArrayAdapter arrayAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item,
                Helper.getInstance().getStatusList());
        statusSpinner.setAdapter(arrayAdapter);

        recyclerView.addOnItemTouchListener(
                new RecyclerViewTouchListeners(this, recyclerView, new ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        grievanceModel = grievanceModelArrayList.get(position);
                        setRecyclerVisiblity(false);
                        setLayoutData();
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editTextMessage.getText().toString().trim();
                int status = statusSpinner.getSelectedItemPosition();
                updateData(message, status);
                progressDialog.show();
            }
        });

        grievanceModelArrayList = new ArrayList<>();
        adapter = new RecyclerViewAdapterGrievanceUpdate(grievanceModelArrayList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getData();
    }

    private void setLayoutData() {
        tvPensioner.setText(grievanceModel.getPensionerIdentifier());
        tvGrievance.setText(Helper.getInstance().getGrievanceString(grievanceModel.getGrievanceType()));
        tvDate.setText(Helper.getInstance().formatDate(grievanceModel.getDate()));
        statusSpinner.setSelection((int) grievanceModel.getGrievanceStatus());
        editTextMessage.setText("");
    }

    private void getData() {

        ConnectionUtility connectionUtility = new ConnectionUtility(new OnConnectionAvailableListener() {
            @Override
            public void OnConnectionAvailable() {
                fromFirebase();
            }

            @Override
            public void OnConnectionNotAvailable() {
                tvNoData.setText("Internet Connection Not Available");
                progressDialog.dismiss();
            }
        });
        connectionUtility.CheckConnectionAvailability();

    }

    private void fromFirebase() {
        DatabaseReference dbref = FireBaseHelper.getInstance().databaseReference;
        dbref.child(FireBaseHelper.getInstance().ROOT_GRIEVANCES).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (Integer.valueOf(ds.child("grievanceStatus").getValue().toString()) < 2) {
                            GrievanceModel grievanceModel = ds.getValue(GrievanceModel.class);
                            Log.d(TAG, "onChildAdded: " + grievanceModel.getDetails());
                            grievanceModelArrayList.add(grievanceModel);
                            adapter.notifyDataSetChanged();
                        }
                    }
                    Log.d(TAG, "onChildAdded: " + grievanceModelArrayList);
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
        dbref.child(FireBaseHelper.getInstance().ROOT_GRIEVANCES).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                if (grievanceModelArrayList.size() == 0)
                    tvNoData.setText("ALL GRIEVANCES RESOLVED");
                else
                    tvNoData.setText("");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void updateData(String message, int status) {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("grievanceStatus", status);
        hashMap.put("message", message);

        DatabaseReference dbref = FireBaseHelper.getInstance().databaseReference;
        dbref.child(FireBaseHelper.getInstance().ROOT_GRIEVANCES).child(grievanceModel.getPensionerIdentifier())
                .child(String.valueOf(grievanceModel.getGrievanceType())).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful())
                    Toast.makeText(UpdateGrievanceActivity.this, "Successfully Updated", Toast.LENGTH_LONG).show();
            }
        });
    }
}
