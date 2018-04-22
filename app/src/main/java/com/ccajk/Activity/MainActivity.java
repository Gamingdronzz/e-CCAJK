package com.ccajk.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.ccajk.CustomObjects.CardDrawerLayout;
import com.ccajk.Fragments.BrowserFragment;
import com.ccajk.Fragments.ContactUsFragment;
import com.ccajk.Fragments.GrievanceFragment;
import com.ccajk.Fragments.HomeFragment;
import com.ccajk.Fragments.InspectionFragment;
import com.ccajk.Fragments.LocatorFragment;
import com.ccajk.Fragments.PanAdhaarUploadFragment;
import com.ccajk.R;
import com.ccajk.Tools.FireBaseHelper;
import com.ccajk.Tools.PopUpWindows;
import com.ccajk.Tools.Preferences;

import java.util.List;

import shortbread.Shortcut;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String TAG = "MainActivity";
    FrameLayout frameLayout;
    NavigationView navigationView;
    CardDrawerLayout drawerLayout;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolbar();
        bindViews();
        init();
        ShowFragment("Home", new HomeFragment(), null);

    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void bindViews() {
        frameLayout = findViewById(R.id.fragmentPlaceholder);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
    }

    private void init() {
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name) {
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
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        drawerLayout.setViewScale(Gravity.START, 0.85f);
        drawerLayout.setRadius(Gravity.START, 35);
        drawerLayout.setViewElevation(Gravity.START, 30);
        actionBarDrawerToggle.syncState();


        if (Preferences.getInstance().getSignedIn(this)) {
            navigationView.getMenu().findItem(R.id.staff_login).setVisible(false);
            navigationView.getMenu().findItem(R.id.staff_panel).setVisible(true);
        } else {
            navigationView.getMenu().findItem(R.id.staff_panel).setVisible(false);
            navigationView.getMenu().findItem(R.id.staff_login).setVisible(true);
        }
        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragmentPlaceholder);

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (f instanceof HomeFragment) {
            doExit();
        } else if (f instanceof BrowserFragment) {
            if (((BrowserFragment) f).canGoBack()) {
                ((BrowserFragment) f).goBack();
            } else {
                ((BrowserFragment) f).stopLoading();
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
        Bundle bundle = new Bundle();
        bundle.putString("Locator", FireBaseHelper.getInstance().ROOT_HOTSPOTS);
        ShowFragment("Wifi Hotspot Locations", new LocatorFragment(), bundle);
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
            case R.id.navmenu_visit_cca_website:
                fragment = new BrowserFragment();
                bundle = new Bundle();
                bundle.putString("url", "http://ccajk.gov.in");
                ShowFragment("CCA J&K", fragment, bundle);
                break;
            case R.id.navmenu_contact_us:
                ShowFragment("Contact Us", new ContactUsFragment(), null);
                break;
            case R.id.navmenu_hotspot_locator:
                bundle = new Bundle();
                bundle.putString("Locator", FireBaseHelper.getInstance().ROOT_HOTSPOTS);
                ShowFragment("Wifi Hotspot Locations", new LocatorFragment(), bundle);
                break;
            case R.id.navmenu_gp_locator:
                bundle = new Bundle();
                bundle.putString("Locator", FireBaseHelper.getInstance().ROOT_GP);
                ShowFragment("GP Locations", new LocatorFragment(), bundle);
                break;
            case R.id.navmenu_rti:
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
                bundle.putString("Type", FireBaseHelper.getInstance().GRIEVANCE_PENSION);
                ShowFragment("Pension Grievance Registeration", fragment, bundle);
                break;
            case R.id.navmenu_gpf:
                fragment = new GrievanceFragment();
                bundle = new Bundle();
                bundle.putString("Type", FireBaseHelper.getInstance().GRIEVANCE_GPF);
                ShowFragment("GPF Grievance Registeration", fragment, bundle);
                break;
            case R.id.navmenu_aadhaar:
                fragment = new PanAdhaarUploadFragment();
                bundle = new Bundle();
                bundle.putString("Root", FireBaseHelper.getInstance().ROOT_ADHAAR);
                ShowFragment("Upload Aadhar", fragment, bundle);
                break;
            case R.id.navmenu_pan:
                fragment = new PanAdhaarUploadFragment();
                bundle = new Bundle();
                bundle.putString("Root", FireBaseHelper.getInstance().ROOT_PAN);
                ShowFragment("Upload PAN", fragment, bundle);
                break;
            case R.id.navmenu_life_certificate:
                fragment = new PanAdhaarUploadFragment();
                bundle = new Bundle();
                bundle.putString("Root", FireBaseHelper.getInstance().ROOT_LIFE);
                ShowFragment("Upload Life Certificate", fragment, bundle);
                break;
            case R.id.navmenu_remarriage_certificate:
                fragment = new PanAdhaarUploadFragment();
                bundle = new Bundle();
                bundle.putString("Root", FireBaseHelper.getInstance().ROOT_RE_MARRIAGE);
                ShowFragment("Upload Re-Marriage Certificate", fragment, bundle);
                break;
            case R.id.navmenu_reemployment:
                fragment = new PanAdhaarUploadFragment();
                bundle = new Bundle();
                bundle.putString("Root", FireBaseHelper.getInstance().ROOT_RE_EMPLOYMENT);
                ShowFragment("Upload Re-Employment Certificate", fragment, bundle);
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

        drawerLayout.closeDrawer(GravityCompat.START);
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
                MainActivity.this, R.style.MyAlertDialogStyle);
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

    public void OnLoginFailure(String message)

    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void OnLoginSuccesful(long type) {
        Log.d(TAG, "OnLoginSuccesful: ");
        Toast.makeText(this, "Succesfully Logged In", Toast.LENGTH_SHORT).show();
        Preferences.getInstance().setSignedIn(this, true);
        navigationView.getMenu().findItem(R.id.staff_login).setVisible(false);
        navigationView.getMenu().findItem(R.id.staff_panel).setVisible(true);

        //TODO
        //implement admin or staff type based code here
    }
}
