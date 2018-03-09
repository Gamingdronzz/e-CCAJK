package com.ccajk.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.ccajk.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText mCode,mMobile;
    private ImageView ppo, mob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }

    private void init() {
        ppo=findViewById(R.id.image_ppo);
        ppo.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.ic_person_black_24dp));

        mob=findViewById(R.id.image_mobile);
        mob.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.ic_phone_android_black_24dp));

        mCode= findViewById(R.id.edittext_ppo);
        mMobile = findViewById(R.id.edittext_mobile);
        Button mRegisterButton = findViewById(R.id.register_button);

        Button skip = findViewById(R.id.skip_button);
        skip.setCompoundDrawablesWithIntrinsicBounds(null,null, AppCompatResources.getDrawable(this,R.drawable.ic_navigate_next_black_24dp),null);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}
