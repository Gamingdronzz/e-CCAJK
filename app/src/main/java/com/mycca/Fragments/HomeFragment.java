package com.mycca.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.mycca.Activity.AboutUsActivity;
import com.mycca.Activity.MainActivity;
import com.mycca.R;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    private SliderLayout mDemoSlider;
    private TextView welcomeText, ccaDeskText;
    View view;
    final String TAG = "HomeFragment";
    MenuItem item;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);
        bindViews(view);
        init();
        //setHasOptionsMenu(true);

        //imageButtonLogout = view.findViewById(R.id.logout);
        //imageButtonLogout.setBackground(AppCompatResources.getDrawable(this.getContext(),R.drawable.ic_logout_24dp));

        // Helper.getInstance().addLocations(300);


        return view;
    }
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.menu_home, menu);
//        item = menu.findItem(R.id.action_settings);
//       /* Helper.getInstance().showGuide(getContext(),item.getActionView(), "Settings Button", "Click this to open Settings", new GuideView.GuideListener() {
//            @Override
//            public void onDismiss(View view) {
//                MainActivity mainActivity = (MainActivity) getActivity();
//                mainActivity.showDrawer();
//            }
//        });*/
//    }

//    @Override
//    public void onPrepareOptionsMenu(Menu menu) {
//        super.onPrepareOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_settings:
//                ((MainActivity)getActivity()).ShowFragment("Settings",new SettingsFragment(),null);
//                break;
//            case R.id.action_invite:
//                Toast.makeText(getContext(), "Invitation", Toast.LENGTH_SHORT).show();
//                break;
//            default:
//                break;
//        }
//
//        return true;
//    }


    private void bindViews(View view) {
        welcomeText = view.findViewById(R.id.textview_welcome_short);
        ccaDeskText = view.findViewById(R.id.textview_cca_desk);
    }

    private void init() {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        SpannableString str1 = new SpannableString(getText(R.string.welcome_short));
        builder.append(str1);
        SpannableString str2 = new SpannableString(Html.fromHtml("<b>Read More</b>"));
        str2.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), 0, str2.length(), 0);
        builder.append(str2);

        welcomeText.setText(builder, TextView.BufferType.SPANNABLE);
        welcomeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeFragment.this.getActivity(), AboutUsActivity.class);
                intent.putExtra("Text", getString(R.string.welcome_full));
                intent.putExtra("Title", "Welcome to CCA JK");
                startActivity(intent);
            }
        });


        SpannableStringBuilder builder2 = new SpannableStringBuilder();
        SpannableString string1 = new SpannableString(getText(R.string.from_cca_desk_short));
        builder2.append(string1);
        SpannableString string2 = new SpannableString(Html.fromHtml("<b>Read More</b>"));
        string2.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), 0, string2.length(), 0);
        builder2.append(string2);

        ccaDeskText.setText(builder2, TextView.BufferType.SPANNABLE);
        ccaDeskText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeFragment.this.getActivity(), AboutUsActivity.class);
                intent.putExtra("Text", getString(R.string.from_cca_desk));
                intent.putExtra("Title", "From CCA's Desk");
                startActivity(intent);
            }
        });

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
