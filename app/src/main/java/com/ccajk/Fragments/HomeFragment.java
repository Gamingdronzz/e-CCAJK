package com.ccajk.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ccajk.Activity.HomeActivity;
import com.ccajk.Prefrences;
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
    private TextView welcomeText;
    View view;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);


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
                ((HomeActivity) getActivity()).getSupportActionBar().setTitle("About Us");
                Fragment fragment;
                fragment = new AboutUsFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.contentPanel, fragment).commit();
            }
        });

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
        if (Prefrences.getLeaveApp(getContext()) == false) {
            final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                    .setTitle("CCA JK")
                    .setMessage("You are about to leave the application and open the link in external browser")
                    .setPositiveButton("Allow", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Prefrences.setLeaveApp(getContext(), true);
                            loadWebSite(name);
                        }
                    })
                    .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            alertDialog.show();
        } else {
            loadWebSite(name);
        }
    }

    private void loadWebSite(String name) {
        Intent intent;
        switch (name) {
            case "Digital India":
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.digitalindia.gov.in/"));
                startActivity(intent);
                break;
            case "Swachh Bharat Abhiyan":
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://swachhbharat.mygov.in"));
                startActivity(intent);
                break;
            case "Controller of Communication Accounts":
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://ccajk.gov.in/"));
                startActivity(intent);
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
