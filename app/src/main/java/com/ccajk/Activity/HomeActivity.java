package com.ccajk.Activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ccajk.R;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;

import java.util.HashMap;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    private SliderLayout mDemoSlider;
    private TextView welcomeText;
//    WebView webView;
//    final String removeLogo = "document.getElementsByClassName('logo')[0].style.display=\"none\"; ";
//    final String removeHead = "document.getElementsByClassName('head-r')[0].style.display=\"none\"; ";
//    final String removeIndicator = "document.getElementsByClassName('indicator')[0].style.display=\"none\"; ";
//    final String removeFont = "document.getElementsByClassName('font')[0].style.display=\"none\"; ";
//    final String removeFlexy = "document.getElementsByClassName('flexy-menu')[0].style.display=\"none\"; ";
//    final String removeWrapper = "document.getElementsByClassName('wrapper')[0].style.display=\"none\"; ";
//    final String removeMain = "document.getElementsByClassName('cont-main')[0].style.display=\"none\"; ";
//    final String removeFooter = "document.getElementsByClassName('article')[0].style.display=\"none\"; ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

//        webView = findViewById(R.id.slideshow);
//        setupWebview();
//        webView.loadUrl("http://ccajk.gov.in/index.php");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        SpannableStringBuilder builder = new SpannableStringBuilder();

        SpannableString str1= new SpannableString(getText(R.string.welcome_short));
        builder.append(str1);

        SpannableString str2= new SpannableString(Html.fromHtml("<b>Read More</b>"));
        str2.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), 0, str2.length(), 0);
        builder.append(str2);
        welcomeText = findViewById(R.id.textview_welcome_short);
        welcomeText.setText( builder, TextView.BufferType.SPANNABLE);


        NavigationView navigationView =  findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


setupSlider();
    }


    @Override

    protected void onStop() {

        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed

        mDemoSlider.stopAutoCycle();

        super.onStop();

    }



    @Override
    public void onSliderClick(BaseSliderView slider) {

        Toast.makeText(this,slider.getBundle().get("extra") + "",Toast.LENGTH_SHORT).show();

    }

    private void setupSlider()
    {
        mDemoSlider = (SliderLayout)findViewById(R.id.slider);
        HashMap<String,Integer> file_maps = new HashMap<String, Integer>();

        file_maps.put("Deptt. of Telecomminication",R.drawable.communication);

        file_maps.put("Swachh Bharat Abhiyan",R.drawable.swachhbharat);

        file_maps.put("Digital India",R.drawable.digitalindia);

        file_maps.put("Controller of Communication Accounts", R.drawable.cca);

        for(String name : file_maps.keySet()){

            TextSliderView textSliderView = new TextSliderView(this);
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .image(file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.FitCenterCrop)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra",name);
            mDemoSlider.addSlider(textSliderView);

        }






    }

  /*  private void setupWebview() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {

            @RequiresApi(Build.VERSION_CODES.O)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return super.shouldOverrideUrlLoading(view, request);
            }

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    Log.d("Url", url);
                    view.loadUrl(url);
                }
                return true;
            }

            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Toast.makeText(getApplicationContext(), "We are getting things fixed..", Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onPageFinished(WebView view, String url) {

            }
        });

        webView.setWebChromeClient
                (
                        new WebChromeClient() {
                            @Override
                            public void onProgressChanged(WebView view, int newProgress) {
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("javascript:");
                                stringBuilder.append("(function() { ");

                                stringBuilder.append(removeHead);
                                stringBuilder.append(removeFont);
                                stringBuilder.append(removeLogo);
                                stringBuilder.append(removeMain);
                                stringBuilder.append(removeIndicator);
                                stringBuilder.append(removeFlexy);
                                stringBuilder.append(removeWrapper);
                                stringBuilder.append(removeFooter);
                                stringBuilder.append("} ) ()");
                                view.loadUrl(stringBuilder.toString());

                            }

                        }
                );
    }*/


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            doExit();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void doExit() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                HomeActivity.this, R.style.MyAlertDialogStyle);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertDialog.setNegativeButton("No", null);
        alertDialog.setMessage("Do you want to exit?");
        alertDialog.setTitle("CCA JK");
        alertDialog.show();
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
