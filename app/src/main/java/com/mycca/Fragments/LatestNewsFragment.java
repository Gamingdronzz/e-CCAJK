package com.mycca.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.mycca.Adapter.RecyclerViewAdapterNews;
import com.mycca.Models.NewsModel;
import com.mycca.R;
import com.mycca.Tools.FireBaseHelper;

import java.util.ArrayList;


public class LatestNewsFragment extends Fragment {


    RecyclerView recyclerView;
    RecyclerViewAdapterNews adapterNews;
    ArrayList<NewsModel> newsModelArrayList;

    public LatestNewsFragment() {

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_latest_news, container, false);
        bindViews(view);
        init();
        return view;
    }

    private void bindViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_latest_news);
    }

    private void init() {
        newsModelArrayList = new ArrayList<>();
        adapterNews = new RecyclerViewAdapterNews(newsModelArrayList, (AppCompatActivity) getActivity(), false);

        recyclerView.setAdapter(adapterNews);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        getNews();
    }

    private void getNews() {
        DatabaseReference dbref = FireBaseHelper.getInstance(getContext()).versionedDbRef;
        dbref.child(FireBaseHelper.ROOT_NEWS)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot.getValue() != null) {
                            try {
                                NewsModel newsModel = dataSnapshot.getValue(NewsModel.class);
                                newsModelArrayList.add(newsModel);
                                adapterNews.notifyDataSetChanged();
                            } catch (DatabaseException dbe) {
                                dbe.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
