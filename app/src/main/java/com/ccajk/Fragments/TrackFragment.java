package com.ccajk.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.ccajk.Models.Grievance;
import com.ccajk.R;
import com.ccajk.Tools.FireBaseHelper;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class TrackFragment extends Fragment {

    Button track;
    CheckBox gpfCheckbox, pensionCheckBox;
    TextInputEditText editTextPCode;

    public TrackFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_track, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        editTextPCode = view.findViewById(R.id.edittext_pcode);
        gpfCheckbox = view.findViewById(R.id.checkBoxGPF);
        pensionCheckBox = view.findViewById(R.id.checkBoxPension);

        track = view.findViewById(R.id.btn_check_status);
        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),nextclass.class);
                intent.putExtra("pensionerCode",editTextPCode.getText().toString());
                if (gpfCheckbox.isChecked()) {
                   intent.putExtra("gpf",true);
                }
                if (pensionCheckBox.isChecked()) {
                    intent.putExtra("pension",true);
                }
                if (!gpfCheckbox.isChecked() && !pensionCheckBox.isChecked()) {
                    Toast.makeText(getContext(), "Please select one", Toast.LENGTH_LONG).show();
                    return;
                }
                startActivity(intent);
            }
        });


    }

    /*private void addPensionGrievane() {
        dbref.child(FireBaseHelper.getInstance().ROOT_GRIEVANCE_PENSION).child(pensionerCode)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        grievances.add(dataSnapshot.getValue(Grievance.class));
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

    private void addGpfGrievance() {
        dbref.child(FireBaseHelper.getInstance().ROOT_GRIEVANCE_GPF).child(pensionerCode)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        grievances.add(dataSnapshot.getValue(Grievance.class));
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
    }*/

}
