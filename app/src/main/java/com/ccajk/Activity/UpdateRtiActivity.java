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

import com.ccajk.Adapter.RecyclerViewAdapterRTIUpdate;
import com.ccajk.CustomObjects.ProgressDialog;
import com.ccajk.Listeners.ClickListener;
import com.ccajk.Listeners.RecyclerViewTouchListeners;
import com.ccajk.Models.RtiModel;
import com.ccajk.R;
import com.ccajk.Tools.FireBaseHelper;
import com.ccajk.Tools.Helper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;

public class UpdateRtiActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    LinearLayout linearLayout;
    TextView tvName, tvMobile, tvDate;
    Spinner statusSpinner;
    EditText editTextMessage;
    Button update;
    ProgressDialog progressDialog;

    RecyclerViewAdapterRTIUpdate adapter;
    ArrayList<RtiModel> rtiModelArrayList;

    String TAG = "Update";
    RtiModel rtiModel;
    boolean recyclerVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_rti);
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
            recyclerVisible = true;
            linearLayout.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            recyclerVisible = false;
            linearLayout.setVisibility(View.VISIBLE);
        }
    }

    private void bindViews() {
        linearLayout = findViewById(R.id.layout_rti_update);
        recyclerView = findViewById(R.id.recyclerview_rti);
        tvName = findViewById(R.id.textview_name);
        tvMobile = findViewById(R.id.textview_mobile);
        tvDate = findViewById(R.id.textview_date);
        statusSpinner = findViewById(R.id.spinner_status);
        editTextMessage = findViewById(R.id.edittext_message);
        update = findViewById(R.id.button_update);
    }

    public void init() {
        setRecyclerVisiblity(true);

        ArrayAdapter arrayAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item,
                Helper.getInstance().getStatusList());
        statusSpinner.setAdapter(arrayAdapter);

        recyclerView.addOnItemTouchListener(
                new RecyclerViewTouchListeners(this, recyclerView, new ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        rtiModel = rtiModelArrayList.get(position);
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
            }
        });

        rtiModelArrayList = new ArrayList<>();
        adapter = new RecyclerViewAdapterRTIUpdate(rtiModelArrayList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getRtiData();
    }

    private void setLayoutData() {
        tvName.setText(rtiModel.getName());
        tvMobile.setText(rtiModel.getMobile());
        tvDate.setText(Helper.getInstance().formatDate(rtiModel.getDate()));
        statusSpinner.setSelection((int) rtiModel.getStatus());
        editTextMessage.setText("");
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

    private void updateData(String message, int status) {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        hashMap.put("message", message);

        String key = rtiModel.getName().replaceAll("\\s", "-") + "-" + rtiModel.getMobile();
        DatabaseReference dbref = FireBaseHelper.getInstance().databaseReference;
        dbref.child(FireBaseHelper.getInstance().ROOT_RTI).child(key)
                .updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    Toast.makeText(UpdateRtiActivity.this, "Successfully Updated", Toast.LENGTH_LONG).show();
            }
        });
    }
}
