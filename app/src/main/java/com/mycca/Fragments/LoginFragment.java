package com.mycca.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mycca.Activity.MainActivity;
import com.mycca.CustomObjects.Progress.ProgressDialog;
import com.mycca.Models.StaffModel;
import com.mycca.R;
import com.mycca.Tools.FireBaseHelper;
import com.mycca.Tools.Helper;

public class LoginFragment extends Fragment {

    MainActivity mainActivity;
    TextView textViewUserID;
    EditText editTextPassword;

    FirebaseAuth mAuth;
    boolean found = false;

    public LoginFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        init(view);
        return view;
    }

    private void init(View view) {

        mainActivity = (MainActivity) getActivity();
        mAuth = FireBaseHelper.getInstance(getContext()).mAuth;

        textViewUserID = view.findViewById(R.id.tv_user_id);
        textViewUserID.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_person_black_24dp, 0, 0, 0);
        textViewUserID.setText(mAuth.getCurrentUser().getEmail());

        editTextPassword = view.findViewById(R.id.edittext_password);
        editTextPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_password, 0, 0, 0);

        Button signin = view.findViewById(R.id.sign_in_button);
        signin.setOnClickListener(v -> tryLogin());
    }

    private void tryLogin() {
        Helper.getInstance().hideKeyboardFrom(getActivity());

        final String password = editTextPassword.getText().toString();

        if (!Helper.getInstance().checkInput(password)) {
            Toast.makeText(getContext(), "Please input Password", Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog progressDialog = Helper.getInstance().getProgressWindow(getActivity(), "Logging In...");
        progressDialog.show();

        final DatabaseReference dbref = FirebaseDatabase.getInstance().getReference()
                .child(FireBaseHelper.ROOT_STAFF);

        final ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
                    Log.d("login", "id: " + dataSnapshot.child("id").getValue());
                    Log.d("login", "current user: " + mAuth.getCurrentUser().getEmail());
                    Log.d("login", "found: " + found);
                    if (dataSnapshot.child("id").getValue().equals(mAuth.getCurrentUser().getEmail())) {
                        progressDialog.dismiss();
                        found = true;
                        if (dataSnapshot.child("password").getValue().equals(password)) {
                            Log.d("login", "password match");
                            StaffModel staffModel = dataSnapshot.getValue(StaffModel.class);
                            mainActivity.OnLoginSuccessful(staffModel);
                        } else {
                            Log.d("login", "password mismatch");
                            mainActivity.OnLoginFailure("Password Mismatch");
                        }
                        dbref.removeEventListener(this);
                    }
                } catch (DatabaseException dbe) {
                    progressDialog.dismiss();
                    mainActivity.OnLoginFailure("Some Error Occurred. Try Again");
                    dbe.printStackTrace();
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
        };
        dbref.addChildEventListener(childEventListener);
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                if (!found)
                    mainActivity.OnLoginFailure("No user found");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
