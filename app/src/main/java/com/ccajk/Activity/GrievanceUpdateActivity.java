package com.ccajk.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import com.ccajk.Adapter.RecyclerViewAdapterTracking;
import com.ccajk.CustomObjects.ProgressDialog;
import com.ccajk.R;
import com.ccajk.Tools.FireBaseHelper;
import com.ccajk.Tools.Helper;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class GrievanceUpdateActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    DatabaseReference dbref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grievance_update);
        init();
    }

    private void init() {
        progressDialog = Helper.getInstance().getProgressWindow(this, "Checking for Applied Grievances\n\nPlease Wait...");
        progressDialog.show();

        dbref = FireBaseHelper.getInstance().databaseReference;
    }
}
