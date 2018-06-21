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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
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
    EditText editTextUsername, editTextPassword;

    FirebaseAuth mAuth;

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

        editTextUsername = view.findViewById(R.id.edittext_username);
        editTextUsername.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_person_black_24dp, 0, 0, 0);

        editTextPassword = view.findViewById(R.id.edittext_password);
        editTextPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_password, 0, 0, 0);

        Button signin = view.findViewById(R.id.sign_in_button);
        signin.setOnClickListener(v -> tryLogin());
    }

    private void tryLogin() {
        Helper.getInstance().hideKeyboardFrom(getActivity());

        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();

        if (!Helper.getInstance().checkInput(username)) {
            Toast.makeText(getContext(), "Please input Username", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Helper.getInstance().checkInput(password)) {
            Toast.makeText(getContext(), "Please input Password", Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog progressDialog = Helper.getInstance().getProgressWindow(getActivity(), "Logging In...");
        progressDialog.show();

        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference()
                .child(FireBaseHelper.ROOT_STAFF)
                .child(username);
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                if (dataSnapshot.getValue() == null) {
                    mainActivity.OnLoginFailure("No user found");
                } else if (dataSnapshot.child("password").getValue().equals(password)) {
                    try {
                        StaffModel staffModel = dataSnapshot.getValue(StaffModel.class);
                        mainActivity.OnLoginSuccessful(staffModel);
                    } catch (DatabaseException dbe){
                        dbe.printStackTrace();
                        mainActivity.OnLoginFailure("Some Error Occurred");
                    }
                } else {
                    Log.d("login", "password mismatch");
                    mainActivity.OnLoginFailure("Password Mismatch");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
