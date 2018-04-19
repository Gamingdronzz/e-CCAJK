package com.ccajk.Unused;

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

import com.ccajk.Activity.MainActivity;
import com.ccajk.R;

public class LoginActivity extends AppCompatActivity  {

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
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    private void populateAutoComplete() {
    }



    class LoginTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: ");
            //RequestLogin(mPensionerCode.getText().toString(), mPasswordView.getText().toString());
            return null;
        }

    }
}
