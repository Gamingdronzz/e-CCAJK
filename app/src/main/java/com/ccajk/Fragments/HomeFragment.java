package com.ccajk.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.content.res.AppCompatResources;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ccajk.Activity.AboutUsActivity;
import com.ccajk.Activity.BrowserActivity;
import com.ccajk.R;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    private SliderLayout mDemoSlider;
    private TextView welcomeText, ccaDeskText;
    View view;
    private ImageButton imageButtonLogout;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        imageButtonLogout = view.findViewById(R.id.logout);
        imageButtonLogout.setBackground(AppCompatResources.getDrawable(this.getContext(),R.drawable.ic_logout_24dp));

        SpannableStringBuilder builder = new SpannableStringBuilder();
        SpannableString str1 = new SpannableString(getText(R.string.welcome_short));
        builder.append(str1);
        SpannableString str2 = new SpannableString(Html.fromHtml("<b>Read More</b>"));
        str2.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), 0, str2.length(), 0);
        builder.append(str2);
        welcomeText = view.findViewById(R.id.textview_welcome_short);
        welcomeText.setText(builder, TextView.BufferType.SPANNABLE);
        welcomeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeFragment.this.getActivity(), AboutUsActivity.class);
                startActivity(intent);
            }
        });


        SpannableStringBuilder builder2 = new SpannableStringBuilder();
        SpannableString string1 = new SpannableString(getText(R.string.from_cca_desk_short));
        builder2.append(string1);
        SpannableString string2 = new SpannableString(Html.fromHtml("<b>Read More</b>"));
        string2.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), 0, string2.length(), 0);
        builder2.append(string2);
        ccaDeskText = view.findViewById(R.id.textview_cca_desk);
        ccaDeskText.setText(builder2, TextView.BufferType.SPANNABLE);

        setupSlider();
        return view;
    }

    @Override
    public void onStop() {
        mDemoSlider.stopAutoCycle();
        //getActivity().getSupportFragmentManager().popBackStack();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

        final String name = (String) slider.getBundle().get("extra");
        loadWebSite(name);
    }

    private void loadWebSite(String name) {
        Intent intent = new Intent(HomeFragment.this.getActivity(), BrowserActivity.class);
        switch (name) {
            case "Digital India":
                intent.putExtra("url", "http://www.digitalindia.gov.in/");
                intent.putExtra("title", "Digital India");
                startActivity(intent);
                break;
            case "Swachh Bharat Abhiyan":
                intent.putExtra("url", "https://swachhbharat.mygov.in");
                intent.putExtra("title", "Swachh Bharat");
                startActivity(intent);
                break;
            case "Controller of Communication Accounts":
                intent.putExtra("url", "http://ccajk.gov.in/");
                intent.putExtra("title", "CCA J&K");
                startActivity(intent);
                break;
        }

    }

    private void setupSlider() {
        mDemoSlider = (SliderLayout) view.findViewById(R.id.slider);
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
