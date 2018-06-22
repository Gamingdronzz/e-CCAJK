package com.mycca.Fragments;


import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.mycca.Activity.MainActivity;
import com.mycca.Adapter.RecyclerViewAdapterNews;
import com.mycca.CustomObjects.FancyShowCase.FancyShowCaseQueue;
import com.mycca.CustomObjects.FancyShowCase.FancyShowCaseView;
import com.mycca.CustomObjects.FancyShowCase.FocusShape;
import com.mycca.CustomObjects.GravitySnapHelper.GravitySnapHelper;
import com.mycca.Models.NewsModel;
import com.mycca.R;
import com.mycca.Tools.FireBaseHelper;
import com.mycca.Tools.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeFragment extends Fragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    SliderLayout mDemoSlider;
    RecyclerView recyclerView;
    TextView tvLatestNews, tvUserName, tvVisit;
    ImageButton moveRight, moveLeft;
    LinearLayoutManager linearLayoutManager;
    //private TextView welcomeText, ccaDeskText;
    View view;
    final String TAG = "HomeFragment";
    RecyclerViewAdapterNews adapterNews;
    ArrayList<NewsModel> newsModelArrayList;
    MainActivity activity;

    public HomeFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);
        bindViews(view);
        init();
        if (Preferences.getInstance().getBooleanPref(getContext(), Preferences.PREF_HELP_HOME)) {
            showTutorial();
            Preferences.getInstance().setBooleanPref(getContext(), Preferences.PREF_HELP_HOME, false);
        }
        return view;
    }

    private void bindViews(View view) {
        //welcomeText = view.findViewById(R.id.textview_welcome_short);
        //ccaDeskText = view.findViewById(R.id.textview_cca_desk);
        mDemoSlider = view.findViewById(R.id.slider_home);
        tvLatestNews = view.findViewById(R.id.tv_home_latest_news);
        recyclerView = view.findViewById(R.id.recycler_view_home_latest_news);
        moveRight = view.findViewById(R.id.img_btn_move_right);
        moveLeft = view.findViewById(R.id.img_btn_move_left);
        tvUserName = view.findViewById(R.id.tv_home_username);
        tvVisit = view.findViewById(R.id.tv_home_visit);
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
//                Intent intent = new Intent(HomeFragment.this.activity, AboutUsActivity.class);
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
//                Intent intent = new Intent(HomeFragment.this.activity, AboutUsActivity.class);
//                intent.putExtra("Text", getString(R.string.from_cca_desk));
//                intent.putExtra("Title", "From CCA's Desk");
//                startActivity(intent);
//            }
//        });

        activity = (MainActivity) getActivity();
        tvVisit.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_drawable_location, 0, R.drawable.ic_keyboard_arrow_right_black_24dp, 0);
        tvLatestNews.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_news_icon, 0, R.drawable.ic_keyboard_arrow_right_black_24dp, 0);

        tvVisit.setOnClickListener(v -> {
            String location = "32.707500,74.874217";
            Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?q=" + location + "(Office of CCA, JK)");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            if (mapIntent.resolveActivity(activity.getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                Toast.makeText(getContext(), "No Map Application Installed", Toast.LENGTH_SHORT).show();
            }
        });

        tvLatestNews.setOnClickListener(v -> activity.showFragment("Latest News", new LatestNewsFragment(), null));

        newsModelArrayList = new ArrayList<>();
        adapterNews = new RecyclerViewAdapterNews(newsModelArrayList, activity, true);

        recyclerView.setAdapter(adapterNews);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        SnapHelper snapHelperStart = new GravitySnapHelper(Gravity.START);
        snapHelperStart.attachToRecyclerView(recyclerView);

        moveRight.setOnClickListener(v -> {
            if (linearLayoutManager.findLastVisibleItemPosition() - 1 >= 0)
                recyclerView.smoothScrollToPosition(linearLayoutManager.findLastVisibleItemPosition() - 1);
        });

        moveLeft.setOnClickListener(v -> recyclerView.smoothScrollToPosition(linearLayoutManager.findFirstVisibleItemPosition() + 1));

        getNews();
        setupWelcomeBar();
        setupSlider();
    }

    public void setupWelcomeBar() {
        FirebaseUser user = FireBaseHelper.getInstance(getContext()).mAuth.getCurrentUser();
        if (user != null) {
            tvUserName.setVisibility(View.VISIBLE);
            String username = "Hello " + user.getDisplayName();
            tvUserName.setText(username);
        } else
            tvUserName.setVisibility(View.GONE);
    }

    private void loadWebSite(String name) {

        BrowserFragment browserFragment = new BrowserFragment();

        Bundle bundle = new Bundle();
        switch (name) {
            case "Digital India":
                bundle.putString("url", "http://www.digitalindia.gov.in");
                activity.showFragment(name, browserFragment, bundle);
                break;
            case "Swachh Bharat Abhiyan":
                bundle.putString("url", "https://swachhbharat.mygov.in");
                activity.showFragment(name, browserFragment, bundle);
                break;
            case "Controller of Communication Accounts":
                bundle.putString("url", "http://ccajk.gov.in");
                activity.showFragment(name, browserFragment, bundle);
                break;
        }

    }

    private void getNews() {
        DatabaseReference dbref = FireBaseHelper.getInstance(getContext()).databaseReference;
        dbref.child(FireBaseHelper.ROOT_NEWS)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot.getValue() != null) {
                            NewsModel newsModel = dataSnapshot.getValue(NewsModel.class);
                            newsModelArrayList.add(newsModel);
                            adapterNews.notifyDataSetChanged();
                            recyclerView.smoothScrollToPosition(newsModelArrayList.size() - 1);
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

    private void setupSlider() {

        HashMap<String, Integer> file_maps = new HashMap<>();

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

    private void showTutorial() {

        final FancyShowCaseView fancyShowCaseView1 = new FancyShowCaseView.Builder(activity)
                .title("Tap on images to open respective websites. Tap anywhere to continue")
                .focusOn(mDemoSlider)
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .fitSystemWindows(true)
                .build();

        final FancyShowCaseView fancyShowCaseView2 = new FancyShowCaseView.Builder(activity)
                .title("Tap on news to view in detail")
                .focusOn(recyclerView)
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .titleStyle(R.style.FancyShowCaseDefaultTitleStyle, Gravity.BOTTOM | Gravity.CENTER)
                .fitSystemWindows(true)
                .build();

        final FancyShowCaseView fancyShowCaseView3 = new FancyShowCaseView.Builder(activity)
                .title("Open Main Menu from here")
                .focusCircleAtPosition(0, 0, 200)
                .build();

        final FancyShowCaseView fancyShowCaseView4 = new FancyShowCaseView.Builder(activity)
                .title("Touch here to open Secondary Menu")
                .focusCircleAtPosition(Resources.getSystem().getDisplayMetrics().widthPixels, 0, 200)
                .build();

        activity.setmQueue(new FancyShowCaseQueue()
                .add(fancyShowCaseView1)
                .add(fancyShowCaseView2)
                .add(fancyShowCaseView3)
                .add(fancyShowCaseView4));
        activity.getmQueue().setCompleteListener(() -> {
            activity.setmQueue(null);
            if (FireBaseHelper.getInstance(getContext()).mAuth.getCurrentUser() == null)
                activity.showAuthDialog(false);
        });

        activity.getmQueue().show();
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
