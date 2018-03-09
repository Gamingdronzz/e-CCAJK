package com.ccajk.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.ccajk.Interfaces.ILoginProcessor;
import com.ccajk.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements ILoginProcessor {

    private static final String TAG = "Login";
    private AutoCompleteTextView mPensionerCode;
    private EditText mPasswordView;
    private ImageView ppo, pwd;
    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();

    }

    private void init() {
        ppo = findViewById(R.id.image_ppo);
        ppo.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_person_black_24dp));

        pwd = findViewById(R.id.image_pwd);
        pwd.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_password));

        mPensionerCode = findViewById(R.id.ppo);
        populateAutoComplete();
        mPasswordView = findViewById(R.id.password);

        Button SignInButton = findViewById(R.id.sign_in_button);
        SignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LoginTask().execute();
            }
        });

        mProgressView = findViewById(R.id.login_progress);

        Button skip = findViewById(R.id.skip_button);
        skip.setCompoundDrawablesWithIntrinsicBounds(null, null, AppCompatResources.getDrawable(this, R.drawable.ic_navigate_next_black_24dp), null);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    private void populateAutoComplete() {
    }

    @Override
    public void RequestLogin(String PensionerCode, final String password) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dbref = databaseReference.child("user").child(PensionerCode);
        Log.d(TAG, "RequestLogin: ");
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: " + dataSnapshot.toString());
                if (dataSnapshot == null) {
                    OnUserNotExist();
                }
                if (dataSnapshot != null) {
                    String dbpassword = dataSnapshot.child("password").getValue().toString();
                    if (dbpassword.equals(password)) {
                        OnLoginSuccesful(dataSnapshot);
                    } else {
                        OnLoginFailure();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Error");
                OnUserNotExist();
            }
        });
    }

    @Override
    public void OnLoginSuccesful(DataSnapshot dataSnapshot) {

    }

    @Override
    public void OnLoginFailure() {

    }

    @Override
    public void OnUserNotExist() {

    }

    class LoginTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: ");
            RequestLogin(mPensionerCode.getText().toString(), mPasswordView.getText().toString());
            return null;
        }

    }
}
