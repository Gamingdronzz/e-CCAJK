package com.ccajk.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ccajk.Activity.MainActivity;
import com.ccajk.CustomObjects.ProgressDialog;
import com.ccajk.R;
import com.ccajk.Tools.FireBaseHelper;
import com.ccajk.Tools.Helper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {


    ImageView close;

    AutoCompleteTextView completeTextViewUserID;
    EditText editTextPassword;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
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
        FireBaseHelper.getInstance().databaseReference.child(FireBaseHelper.getInstance().ROOT_STAFF).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null) {
                    Toast.makeText(getContext(), "We are getting things fixed", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return;
                }
                Log.d(TAG, "onDataChange: DataSnapshot = " + dataSnapshot);
                Log.d(TAG, "onDataChange: Password = " + dataSnapshot.child(FireBaseHelper.getInstance().ROOT_PASSWORD).getValue());
                if (dataSnapshot.getValue() == null) {
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.OnLoginFailure("No user found");
                    progressDialog.dismiss();
                } else {
                    if (dataSnapshot.child(FireBaseHelper.getInstance().ROOT_PASSWORD).getValue().toString().equals(password)) {
                        long type = (long) dataSnapshot.child(FireBaseHelper.getInstance().ROOT_TYPE).getValue();
                        Log.d(TAG, "onDataChange: type: " + type);
                        MainActivity mainActivity = (MainActivity) getActivity();
                        mainActivity.OnLoginSuccesful(id, type);
                        progressDialog.dismiss();
                    } else {
                        MainActivity mainActivity = (MainActivity) getActivity();
                        mainActivity.OnLoginFailure("Password Mismatch");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
