package com.ccajk.Activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.ccajk.Fragments.ContactUsFragment;
import com.ccajk.Fragments.HomeFragment;
import com.ccajk.R;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


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

       /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Fragment fragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.contentPanel, fragment).commit();

    }


    @Override

    protected void onStop() {



        super.onStop();

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
            doExit();
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
        Fragment fragment;
        switch (id) {
            case R.id.navmenu_home:
                getSupportActionBar().setTitle("Home");

                fragment = new HomeFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.contentPanel, fragment).commit();
                break;

            case R.id.navmenu_contact_us:
                getSupportActionBar().setTitle("Contact Us");
                fragment = new ContactUsFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.contentPanel, fragment).commit();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

}
