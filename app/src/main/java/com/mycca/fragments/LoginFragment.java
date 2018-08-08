package com.mycca.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import com.mycca.R;
import com.mycca.activity.MainActivity;
import com.mycca.custom.Progress.ProgressDialog;
import com.mycca.models.StaffModel;
import com.mycca.tools.CustomLogger;
import com.mycca.tools.FireBaseHelper;
import com.mycca.tools.Helper;

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
            Toast.makeText(getContext(), getString(R.string.input_username), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Helper.getInstance().checkInput(password)) {
            Toast.makeText(getContext(), getString(R.string.input_password), Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog progressDialog = Helper.getInstance().getProgressWindow(getActivity(), getString(R.string.signing_in));
        progressDialog.show();

        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference()
                .child(FireBaseHelper.ROOT_STAFF)
                .child(username);
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                if (dataSnapshot.getValue() == null) {
                    mainActivity.OnLoginFailure(getString(R.string.no_user_found));
                } else if (dataSnapshot.child("password").getValue().equals(password)) {
                    try {
                        StaffModel staffModel = dataSnapshot.getValue(StaffModel.class);
                        mainActivity.OnLoginSuccessful(staffModel);
                    } catch (DatabaseException dbe){
                        dbe.printStackTrace();
                        mainActivity.OnLoginFailure(getString(R.string.some_error));
                    }
                } else {
                    CustomLogger.getInstance().logDebug("password mismatch");
                    mainActivity.OnLoginFailure(getString(R.string.password_mismatch));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
