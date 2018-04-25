package com.ccajk.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ccajk.CustomObjects.ProgressDialog;
import com.ccajk.Models.RtiModel;
import com.ccajk.R;
import com.ccajk.Tools.FireBaseHelper;
import com.ccajk.Tools.Helper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;

public class RtiResultActivity extends AppCompatActivity {

    TextView textView, textViewName, textViewDate, textViewStatus, textViewSubject;
    LinearLayout linearLayout;
    ProgressDialog progressDialog;

    DatabaseReference dbref;
    String key;
    RtiModel rtiModel;
    final String TAG = "TrackRTI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rti_result);
        init();
    }

    private void init() {
        progressDialog = Helper.getInstance().getProgressWindow(this, "Fetching RTI Info\n\nPlease Wait...");
        progressDialog.show();

        textView = findViewById(R.id.textview_tracking);
        textViewName = findViewById(R.id.textview_name);
        textViewDate = findViewById(R.id.textview_date);
        textViewStatus = findViewById(R.id.textview_result);
        textViewSubject = findViewById(R.id.textview_subject);
        linearLayout = findViewById(R.id.layout_rti_info);

        dbref = FireBaseHelper.getInstance().databaseReference;
        key = getIntent().getStringExtra("Key");

        getRtiInfo();

    }

    private void getRtiInfo() {
        dbref.child(FireBaseHelper.getInstance().ROOT_RTI).child(key)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        progressDialog.dismiss();
                        if (dataSnapshot.getValue() == null)
                            textView.setText("RTI Application for this name and mobile number is not filed");
                        else {
                            rtiModel = new RtiModel();
                            rtiModel = dataSnapshot.getValue(RtiModel.class);
                            showInfo();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void showInfo() {
        SimpleDateFormat dt = new SimpleDateFormat("MMM d, yyyy");
        linearLayout.setVisibility(View.VISIBLE);
        textViewName.setText(rtiModel.getName());
        textViewDate.setText(dt.format(rtiModel.getDate()));
        textViewSubject.setText(rtiModel.getSubjectMatter());
        textViewStatus.setText(Helper.getInstance().getStatusString(rtiModel.getStatus()));
    }

}
