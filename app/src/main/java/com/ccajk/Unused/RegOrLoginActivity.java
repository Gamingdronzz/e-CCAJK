package com.ccajk.Unused;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.view.View;
import android.widget.Button;

import com.ccajk.Activity.MainActivity;
import com.ccajk.R;

public class RegOrLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_or_login);
        init();
    }

    public void init() {
        Button login = findViewById(R.id.sign_in_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newActivity(LoginActivity.class);
            }
        });

        Button register = findViewById(R.id.register_button);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newActivity(RegisterActivity.class);
            }
        });

        Button skip = findViewById(R.id.skip_button);
        skip.setCompoundDrawablesWithIntrinsicBounds(null, null, AppCompatResources.getDrawable(this, R.drawable.ic_navigate_next_black_24dp), null);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newActivity(MainActivity.class);
                finish();
            }
        });
    }

    public void newActivity(Class cls) {
        Intent intent = new Intent(RegOrLoginActivity.this, cls);
        startActivity(intent);
    }
}
