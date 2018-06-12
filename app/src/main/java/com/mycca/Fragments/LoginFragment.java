package com.mycca.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mycca.Activity.MainActivity;
import com.mycca.CustomObjects.Progress.ProgressDialog;
import com.mycca.Models.StaffModel;
import com.mycca.R;
import com.mycca.Tools.FireBaseHelper;
import com.mycca.Tools.Helper;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {


    ImageView close;
    MainActivity mainActivity;
    AutoCompleteTextView completeTextViewUserID;
    EditText editTextPassword;

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
        completeTextViewUserID = view.findViewById(R.id.autocomplete_user_id);
        completeTextViewUserID.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_person_black_24dp, 0, 0, 0);
        editTextPassword = view.findViewById(R.id.edittext_password);
        editTextPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_password, 0, 0, 0);

        Button signin = view.findViewById(R.id.sign_in_button);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryLogin();
            }
        });
    }


    private void tryLogin() {
        Helper.getInstance().hideKeyboardFrom(getActivity());
        final String id = completeTextViewUserID.getText().toString();
        final String password = editTextPassword.getText().toString();
        if (!Helper.getInstance().checkInput(id)) {
            Toast.makeText(getContext(), "Please input User ID", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Helper.getInstance().checkInput(password)) {
            Toast.makeText(getContext(), "Please input Password", Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog progressDialog = Helper.getInstance().getProgressWindow(getActivity(), "Logging In...");
        progressDialog.show();

        FirebaseDatabase.getInstance().getReference()
                .child(FireBaseHelper.ROOT_STAFF)
                .child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null) {
                    Toast.makeText(getContext(), "We are getting things fixed", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return;
                }
                if (dataSnapshot.getValue() == null) {
                    mainActivity.OnLoginFailure("No user found");
                    progressDialog.dismiss();
                } else {
                    StaffModel staffModel=dataSnapshot.getValue(StaffModel.class);
                    if (staffModel.getPassword().equals(password)) {
                        mainActivity.OnLoginSuccessful(staffModel);
                        progressDialog.dismiss();
                    } else {
                        mainActivity.OnLoginFailure("Password Mismatch");
                        progressDialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
