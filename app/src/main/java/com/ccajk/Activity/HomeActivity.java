package com.ccajk.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import com.ccajk.Fragments.BrowserFragment;
import com.ccajk.Fragments.ContactUsFragment;
import com.ccajk.Fragments.GrievanceFragment;
import com.ccajk.Fragments.HomeFragment;
import com.ccajk.Fragments.HotspotLocationFragment;
import com.ccajk.Fragments.InspectionFragment;
import com.ccajk.Fragments.PanAdhaarUploadFragment;
import com.ccajk.R;
import com.ccajk.Tools.Helper;
import com.ccajk.Tools.PopUpWindows;
import com.ccajk.Tools.Preferences;

import java.util.List;

import shortbread.Shortcut;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String TAG = "Home";
    FrameLayout frameLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        frameLayout = findViewById(R.id.fragmentPlaceholder);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        };
        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        navigationView = findViewById(R.id.nav_view);
        if (Preferences.getInstance().getSignedIn(this)) {
            navigationView.getMenu().findItem(R.id.staff_login).setVisible(false);
            navigationView.getMenu().findItem(R.id.staff_panel).setVisible(true);
        } else {
            navigationView.getMenu().findItem(R.id.staff_panel).setVisible(false);
            navigationView.getMenu().findItem(R.id.staff_login).setVisible(true);
        }
        navigationView.setNavigationItemSelectedListener(this);

        ShowFragment("Home", new HomeFragment(), null);

    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragmentPlaceholder);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (f instanceof HomeFragment) {
            doExit();
        } else if (f instanceof BrowserFragment) {
            if (((BrowserFragment) f).canGoBack()) {
                ((BrowserFragment) f).goBack();
            } else {
                ShowFragment("Home", new HomeFragment(), null);
            }
        } else {
            ShowFragment("Home", new HomeFragment(), null);
        }
    }


    /*@Override
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
    }*/
    @Shortcut(id = "hotspotNearby", icon = R.drawable.ic_wifi, shortLabel = "HotSpot Locations")
    public void ShowHotSpotLocations() {
        ShowFragment("Wifi Hotspot Locations", new HotspotLocationFragment(), null);
    }

    public void ShowFragment(String title, Fragment fragment, Bundle bundle) {
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setSubtitle("");
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentPlaceholder, fragment).commit();
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Fragment fragment;
        Bundle bundle;
        switch (id) {
            case R.id.navmenu_home:
                ShowFragment("Home", new HomeFragment(), null);
                break;
            case R.id.navmenu_contact_us:
                ShowFragment("Contact Us", new ContactUsFragment(), null);
                break;
            case R.id.navmenu_hotspot_locator:
                ShowFragment("Wifi Hotspot Locations", new HotspotLocationFragment(), null);
                break;
            case R.id.navmenu_rti:
                /*Intent intent = new Intent(HomeActivity.this, BrowserActivity.class);
                intent.putExtra("url", "https://rtionline.gov.in/");
                intent.putExtra("title", "RTI");
                startActivity(intent);
                */
                fragment = new BrowserFragment();
                bundle = new Bundle();
                bundle.putString("url", "https://rtionline.gov.in/");
                ShowFragment("RTI", fragment, bundle);
                break;
            case R.id.navmenu_inspection:
                ShowFragment("Inspection", new InspectionFragment(), null);
                break;
            case R.id.navmenu_pension:
                fragment = new GrievanceFragment();
                bundle = new Bundle();
                bundle.putInt("Category", Helper.getInstance().CATEGORY_PENSION);
                ShowFragment("Pension Grievance Registeration", fragment, bundle);
                break;
            case R.id.navmenu_gpf:
                fragment = new GrievanceFragment();
                bundle = new Bundle();
                bundle.putInt("Category", Helper.getInstance().CATEGORY_GPF);
                ShowFragment("GPF Grievance Registeration", fragment, bundle);
                break;
            case R.id.navmenu_aadhaar:
                fragment = new PanAdhaarUploadFragment();
                bundle = new Bundle();
                bundle.putInt("UploadType", Helper.getInstance().UPLOAD_TYPE_ADHAAR);
                ShowFragment("Upload Aadhar", fragment, bundle);
                break;
            case R.id.navmenu_pan:
                fragment = new PanAdhaarUploadFragment();
                bundle = new Bundle();
                bundle.putInt("UploadType", Helper.getInstance().UPLOAD_TYPE_PAN);
                ShowFragment("Upload PAN", fragment, bundle);
                break;
            case R.id.navmenu_tracking:
                PopUpWindows.getInstance().showTrackWindow(this, frameLayout);
                break;
            case R.id.navmenu_login:
                PopUpWindows.getInstance().showLoginPopup(this, frameLayout);
                break;
            case R.id.navmenu_logout:
                logout();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        Preferences.getInstance().clearPrefs(this);
        navigationView.getMenu().findItem(R.id.staff_login).setVisible(true);
        navigationView.getMenu().findItem(R.id.staff_panel).setVisible(false);
        ShowFragment("Home", new HomeFragment(), null);
    }


    private void doExit() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                HomeActivity.this, R.style.MyAlertDialogStyle);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        })
                .setNegativeButton("No", null)
                .setMessage("Do you want to exit?")
                .setTitle("CCA JK")
                .show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult()", Integer.toString(resultCode));
        List<Fragment> allFragments = getSupportFragmentManager().getFragments();
        for (Fragment frag : allFragments) {
            frag.onActivityResult(requestCode, resultCode, data);
        }
        return;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> allFragments = getSupportFragmentManager().getFragments();

        for (Fragment frag : allFragments) {
            Log.d(TAG, "onRequestPermissionsResult: " + frag.toString());
            frag.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
