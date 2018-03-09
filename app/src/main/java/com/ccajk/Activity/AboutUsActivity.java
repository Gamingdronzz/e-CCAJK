package com.ccajk.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.ccajk.R;

public class AboutUsActivity extends AppCompatActivity {

    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        init();
    }

    private void init() {
        getSupportActionBar().setTitle(getIntent().getStringExtra("Title"));
        textView=findViewById(R.id.display);
        textView.setText(getIntent().getStringExtra("Text"));
    }
}
