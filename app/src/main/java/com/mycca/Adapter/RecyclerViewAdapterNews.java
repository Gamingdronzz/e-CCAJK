package com.mycca.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mycca.Activity.NewsActivity;
import com.mycca.Models.NewsModel;
import com.mycca.R;
import com.mycca.Tools.Helper;

import java.util.ArrayList;

/**
 * Created by hp on 13-02-2018.
 */

public class RecyclerViewAdapterNews extends RecyclerView.Adapter<RecyclerViewAdapterNews.NewsViewHolder> {

    ArrayList<NewsModel> newsModelArrayList;
    Activity context;
    boolean home;


    public RecyclerViewAdapterNews(ArrayList<NewsModel> newsModels, Activity context, boolean home) {
        this.newsModelArrayList = newsModels;
        this.context = context;
        this.home = home;
    }

    @Override
    public RecyclerViewAdapterNews.NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerViewAdapterNews.NewsViewHolder viewHolder;
        if (home)
            viewHolder = new NewsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_home_latest_news, parent, false), new ViewClickListener());
        else
            viewHolder = new NewsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_latest_news, parent, false), new ViewClickListener());
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NewsViewHolder holder, int position) {
        NewsModel newsModel = newsModelArrayList.get(position);

        holder.viewClickListener.setPosition(position);
        holder.headline.setText(newsModel.getHeadline());
        holder.date.setText(Helper.getInstance().formatDate(newsModel.getDate(), "dd-MM-yy"));
        holder.description.setText(newsModel.getDescription());

    /*    if (contact.isExpanded()) {
            holder.linearLayoutExpandableArea.setVisibility(View.VISIBLE);
            holder.name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up_black_24dp, 0);
        } else {
            holder.linearLayoutExpandableArea.setVisibility(View.GONE);
            holder.name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down_black_24dp, 0);
        }
*/
    }

    @Override
    public int getItemCount() {
        return newsModelArrayList.size();
    }

    class NewsViewHolder extends RecyclerView.ViewHolder {

        private TextView headline, date, description;
        private ViewClickListener viewClickListener;
        private RelativeLayout relativeLayouthome;

        public NewsViewHolder(View itemView, ViewClickListener viewClickListener) {
            super(itemView);
            headline = itemView.findViewById(R.id.textview_news_headline);
            date = itemView.findViewById(R.id.textview_news_date);
            description = itemView.findViewById(R.id.textview_news_detail);
            if(home)
            {
                Log.d("News", "NewsViewHolder: setting");
                relativeLayouthome = itemView.findViewById(R.id.relativelayout_news_home);
                Display display = context.getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                relativeLayouthome.setLayoutParams(new FrameLayout.LayoutParams(size.x-10, ViewGroup.LayoutParams.WRAP_CONTENT));
            }

            this.viewClickListener = viewClickListener;
            itemView.setOnClickListener(viewClickListener);
        }
    }

    class ViewClickListener implements View.OnClickListener {
        private int position;

        public void setPosition(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            NewsModel newsModel = newsModelArrayList.get(position);
            Gson gson = new Gson();
            String json = gson.toJson(newsModel);
            Intent intent = new Intent(context, NewsActivity.class);
            intent.putExtra("News", json);
            context.startActivity(intent);
        }
    }
}
