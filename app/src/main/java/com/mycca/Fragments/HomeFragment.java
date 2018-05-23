package com.mycca.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.mycca.Activity.MainActivity;
import com.mycca.Adapter.RecyclerViewAdapterNews;
import com.mycca.Models.NewsModel;
import com.mycca.R;
import com.mycca.Tools.FireBaseHelper;
import com.mycca.Tools.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeFragment extends Fragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    private SliderLayout mDemoSlider;
    TextView tvLatestNews;
    //private TextView welcomeText, ccaDeskText;
    View view;
    final String TAG = "HomeFragment";

    RecyclerView recyclerView;
    RecyclerViewAdapterNews adapterNews;
    ArrayList<NewsModel> newsModelArrayList;

    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);
        bindViews(view);
        init();
        //Helper.getInstance().updateLocations();
        return view;
    }

    private void bindViews(View view) {
        //welcomeText = view.findViewById(R.id.textview_welcome_short);
        //ccaDeskText = view.findViewById(R.id.textview_cca_desk);
        tvLatestNews = view.findViewById(R.id.tv_home_latest_news);
        recyclerView = view.findViewById(R.id.recycler_view_home_latest_news);
    }

    private void init() {
//        SpannableStringBuilder builder = new SpannableStringBuilder();
//        SpannableString str1 = new SpannableString(getText(R.string.welcome_short));
//        builder.append(str1);
//        SpannableString str2 = new SpannableString(Html.fromHtml("<b>Read More</b>"));
//        str2.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), 0, str2.length(), 0);
//        builder.append(str2);
//
//        welcomeText.setText(builder, TextView.BufferType.SPANNABLE);
//        welcomeText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(HomeFragment.this.getActivity(), AboutUsActivity.class);
//                intent.putExtra("Text", getString(R.string.welcome_full));
//                intent.putExtra("Title", "Welcome to CCA JK");
//                startActivity(intent);
//            }
//        });
//
//
//        SpannableStringBuilder builder2 = new SpannableStringBuilder();
//        SpannableString string1 = new SpannableString(getText(R.string.from_cca_desk_short));
//        builder2.append(string1);
//        SpannableString string2 = new SpannableString(Html.fromHtml("<b>Read More</b>"));
//        string2.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), 0, string2.length(), 0);
//        builder2.append(string2);
//
//        ccaDeskText.setText(builder2, TextView.BufferType.SPANNABLE);
//        ccaDeskText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(HomeFragment.this.getActivity(), AboutUsActivity.class);
//                intent.putExtra("Text", getString(R.string.from_cca_desk));
//                intent.putExtra("Title", "From CCA's Desk");
//                startActivity(intent);
//            }
//        });

        tvLatestNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).ShowFragment("Latest News", new LatestNewsFragment(), null);
            }
        });

        newsModelArrayList = new ArrayList<>();
        adapterNews = new RecyclerViewAdapterNews(newsModelArrayList, getActivity(), true);

        recyclerView.setAdapter(adapterNews);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true);
        //linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        getNews();
        setupSlider();
    }

    private void loadWebSite(String name) {

        BrowserFragment browserFragment = new BrowserFragment();
        MainActivity mainActivity = (MainActivity) getActivity();


        Bundle bundle = new Bundle();
        switch (name) {
            case "Digital India":
                bundle.putString("url", "http://www.digitalindia.gov.in");
                mainActivity.ShowFragment(name, browserFragment, bundle);
                break;
            case "Swachh Bharat Abhiyan":
                bundle.putString("url", "https://swachhbharat.mygov.in");
                mainActivity.ShowFragment(name, browserFragment, bundle);
                break;
            case "Controller of Communication Accounts":
                bundle.putString("url", "http://ccajk.gov.in");
                mainActivity.ShowFragment(name, browserFragment, bundle);
                break;
        }

    }

    private void getNews() {
        DatabaseReference dbref = FireBaseHelper.getInstance(getContext()).databaseReference;
        dbref.child(FireBaseHelper.ROOT_NEWS)
                .child(Preferences.getInstance().getStringPref(getContext(), Preferences.PREF_STATE))
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        NewsModel newsModel = dataSnapshot.getValue(NewsModel.class);
                        newsModelArrayList.add(newsModel);
                        adapterNews.notifyDataSetChanged();
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

    private void setupSlider() {
        mDemoSlider = view.findViewById(R.id.slider);
        HashMap<String, Integer> file_maps = new HashMap<String, Integer>();

        file_maps.put("Deptt. of Telecomminication", R.drawable.communication);

        file_maps.put("Swachh Bharat Abhiyan", R.drawable.swachhbharat);

        file_maps.put("Digital India", R.drawable.digitalindia);

        file_maps.put("Controller of Communication Accounts", R.drawable.cca);

        for (String name : file_maps.keySet()) {

            TextSliderView textSliderView = new TextSliderView(getContext());
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .image(file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.FitCenterCrop)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", name);
            mDemoSlider.addSlider(textSliderView);

        }


    }

    @Override
    public void onStop() {
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

        final String name = (String) slider.getBundle().get("extra");
        loadWebSite(name);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
