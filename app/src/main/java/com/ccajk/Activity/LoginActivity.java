package com.ccajk.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.ccajk.R;

public class LoginActivity extends AppCompatActivity {

    private AutoCompleteTextView mEmailView;
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
        ppo=findViewById(R.id.image_ppo);
        ppo.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.ic_person_black_24dp));

        pwd=findViewById(R.id.image_pwd);
        pwd.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.ic_password));
        mEmailView = (AutoCompleteTextView) findViewById(R.id.ppo);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        Button mEmailSignInButton = (Button) findViewById(R.id.sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressView.setVisibility(View.VISIBLE);
                mProgressView.animate().setDuration(2000).alpha(1).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mProgressView.setVisibility(View.GONE);
                    }
                });
            }
        });

        mProgressView = findViewById(R.id.login_progress);

        Button skip = findViewById(R.id.skip_button);
        skip.setCompoundDrawablesWithIntrinsicBounds(null,null, AppCompatResources.getDrawable(this,R.drawable.ic_navigate_next_black_24dp),null);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }

    private void populateAutoComplete() {
    }
}
