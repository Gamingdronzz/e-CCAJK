package com.mycca.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.google.gson.JsonParseException;
import com.mycca.R;
import com.mycca.models.NewsModel;
import com.mycca.tools.CustomLogger;
import com.mycca.tools.Helper;

public class NewsActivity extends AppCompatActivity {
    TextView headline, date, description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(getString(R.string.latest_from_cca));
        bindViews();
        init();
    }

    private void init() {
        String json = getIntent().getStringExtra("News");
        try {

            NewsModel newsModel = (NewsModel) Helper.getInstance().getObjectFromJson(json, NewsModel.class);
            CustomLogger.getInstance().logDebug(json, CustomLogger.Mask.NEWS_ACTIVITY);

            headline.setText(newsModel.getHeadline());
            description.setText(newsModel.getDescription());
            description.setMovementMethod(new ScrollingMovementMethod());
            date.setText(Helper.getInstance().formatDate(newsModel.getDateAdded(), Helper.DateFormat.DD_MM_YYYY));

        } catch (JsonParseException jpe) {
            jpe.printStackTrace();
            headline.setText(getResources().getString(R.string.n_a));
            description.setText(getResources().getString(R.string.n_a));
            date.setText(getResources().getString(R.string.n_a));

        }
    }

    private void bindViews() {
        headline = findViewById(R.id.textview_news_activity_headline);
        date = findViewById(R.id.textview_news_activity_date);
        description = findViewById(R.id.textview_news_activity_detail);
    }
}
