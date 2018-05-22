package com.mycca.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mycca.Models.NewsModel;
import com.mycca.R;
import com.mycca.Tools.Helper;

public class NewsActivity extends AppCompatActivity {

    TextView headline, date, description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        getSupportActionBar().setTitle("Latest from CCA");
        bindViews();
        init();
    }

    private void init() {
        String json = getIntent().getStringExtra("News");
        NewsModel newsModel = new Gson().fromJson(json, NewsModel.class);
        Log.d("News", json);

        headline.setText(newsModel.getHeadline());
        description.setText(newsModel.getDescription());
        description.setMovementMethod(new ScrollingMovementMethod());
        date.setText(Helper.getInstance().formatDate(newsModel.getDate(), "dd MMM,yyyy"));
    }

    private void bindViews() {
        headline = findViewById(R.id.textview_news_activity_headline);
        date = findViewById(R.id.textview_news_activity_date);
        description = findViewById(R.id.textview_news_activity_detail);
    }
}
