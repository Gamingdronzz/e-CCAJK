package com.ccajk.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.ccajk.Models.PanAdhaarStatus;
import com.ccajk.R;
import com.ccajk.Tools.FireBaseHelper;
import com.ccajk.Tools.Helper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class PanAdhaarHistoryActivity extends AppCompatActivity {

    String pcode, TAG = "History";
    int type;

    DatabaseReference dbref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pcode = getIntent().getStringExtra("PensionerCode");
        type = getIntent().getIntExtra("UploadType", -1);
        init();
        setContentView(R.layout.activity_pan_adhaar_history);
    }

    private void init() {
        getHistory();
    }

    private void getHistory() {
        if (type == Helper.getInstance().UPLOAD_TYPE_ADHAAR)
            dbref = FireBaseHelper.getInstance().databaseReference.child("Adhaar-Status").child(pcode);
        else
            dbref = FireBaseHelper.getInstance().databaseReference.child("Pan-Status").child(pcode);

        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount()>0) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        PanAdhaarStatus panAdhaarStatus = dataSnapshot1.getValue(PanAdhaarStatus.class);
                        Log.d(TAG, panAdhaarStatus.getAppliedDate().toString());
                    }
                } else
                    Log.d(TAG, "NO records found");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
