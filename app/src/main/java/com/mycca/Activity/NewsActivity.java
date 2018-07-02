package com.mycca.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.mycca.Models.NewsModel;
import com.mycca.R;
import com.mycca.Tools.Helper;

public class NewsActivity extends AppCompatActivity {
    //private AnimatedDismissableCard animatedCard;
    TextView headline, date, description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        //MyNewAnimatedCardBinding binding = DataBindingUtil.setContentView(R.layout.activity_news);
        //animatedCard = new AnimatedDismissableCard(this, binding.yourViewGroupLayout);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Latest from CCA");
        bindViews();
        init();
    }

    private void init() {
        String json = getIntent().getStringExtra("News");
        try {

            NewsModel newsModel = new Gson().fromJson(json, NewsModel.class);
            Log.d("News", json);

            headline.setText(newsModel.getHeadline());
            description.setText(newsModel.getDescription());
            description.setMovementMethod(new ScrollingMovementMethod());
            date.setText(Helper.getInstance().formatDate(newsModel.getDate(), Helper.DateFormat.DD_MM_YYYY));

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
