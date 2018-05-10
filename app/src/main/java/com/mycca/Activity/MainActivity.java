package com.mycca.Activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.mycca.CustomObjects.CustomDrawer.CardDrawerLayout;
import com.mycca.CustomObjects.FancyAlertDialog.FancyAlertDialogType;
import com.mycca.CustomObjects.FancyAlertDialog.IFancyAlertDialogListener;
import com.mycca.CustomObjects.ShowcaseView.GuideView;
import com.mycca.Fragments.AboutUsFragment;
import com.mycca.Fragments.BrowserFragment;
import com.mycca.Fragments.ContactUsFragment;
import com.mycca.Fragments.FeedbackFragment;
import com.mycca.Fragments.HomeFragment;
import com.mycca.Fragments.InspectionFragment;
import com.mycca.Fragments.LocatorFragment;
import com.mycca.Fragments.LoginFragment;
import com.mycca.Fragments.PanAdhaarUploadFragment;
import com.mycca.Fragments.SettingsFragment;
import com.mycca.Fragments.SubmitGrievanceFragment;
import com.mycca.Fragments.UpdateGrievanceFragment;
import com.mycca.R;
import com.mycca.Tools.FireBaseHelper;
import com.mycca.Tools.Helper;
import com.mycca.Tools.PopUpWindows;
import com.mycca.Tools.Preferences;

import java.util.ArrayList;
import java.util.List;

import shortbread.Shortcut;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_INVITE = 999;
    final int TYPE_ADMIN = 1;
    final int TYPE_STAFF = 2;
    String TAG = "MainActivity";
    FrameLayout frameLayout;
    NavigationView navigationView;
    CardDrawerLayout drawerLayout;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Helper.getInstance().setDebugMode(true);
        setupToolbar();
        bindViews();
        init();
        Log.d(TAG, "onCreate: created");
        ShowFragment("Home", new HomeFragment(), null);

    }

    public void showDrawer() {
        drawerLayout.openDrawer(Gravity.START);
        Helper.getInstance().showGuide(this, navigationView, "Navigation Menu", "This is Navigation Menu\nOpen this to perform various functions", new GuideView.GuideListener() {
            @Override
            public void onDismiss(View view) {
                drawerLayout.closeDrawer(Gravity.START);
            }
        });

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
                Helper.getInstance().hideKeyboardFrom(MainActivity.this);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        drawerLayout.setViewScale(Gravity.START, 0.85f);
        drawerLayout.setRadius(Gravity.START, 35);
        drawerLayout.setViewElevation(Gravity.START, 30);
        actionBarDrawerToggle.syncState();

        if (Preferences.getInstance().getStringPref(this, Preferences.PREF_STAFF_ID) != null) {
            if (Preferences.getInstance().getIntPref(this, Preferences.PREF_STAFF_TYPE) == TYPE_ADMIN)
                ManageNavigationView(true, true);
            else
                ManageNavigationView(true, false);
        } else {
            ManageNavigationView(false, false);
        }
        navigationView.setNavigationItemSelectedListener(this);

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
    @Shortcut(id = "hotspotNearby", icon = R.drawable.ic_wifi_black_24dp, shortLabel = "HotSpot Locations")
    public void ShowHotSpotLocations() {
        Log.d(TAG, "ShowHotSpotLocations: ");
        Bundle bundle = new Bundle();
        bundle.putString("Locator", FireBaseHelper.getInstance().ROOT_HOTSPOTS);
        ShowFragment("Wifi Hotspot Locations", new LocatorFragment(), bundle);
    }

    public void ShowFragment(String title, Fragment fragment, Bundle bundle) {
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setSubtitle("");
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentPlaceholder, fragment).commit();
        navigationView.setCheckedItem(R.id.navmenu_home);

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
            case R.id.navmenu_pension:
                fragment = new SubmitGrievanceFragment();
                bundle = new Bundle();
                bundle.putString("Type", FireBaseHelper.getInstance().GRIEVANCE_PENSION);
                ShowFragment("Pension Grievance Registeration", fragment, bundle);
                break;
            case R.id.navmenu_gpf:
                fragment = new SubmitGrievanceFragment();
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
                ShowFragment("Gram Panchayat Locations", new LocatorFragment(), bundle);
                break;
            case R.id.navmenu_login:
                ShowFragment("CCA JK", new LoginFragment(), null);
                PopUpWindows.getInstance().showLoginPopup(this, frameLayout);
                break;
            case R.id.navmenu_update_grievances:
                ShowFragment("Update Grievance Status", new UpdateGrievanceFragment(), null);
                break;
            case R.id.navmenu_inspection:
                ShowFragment("Inspection", new InspectionFragment(), null);
                break;
            case R.id.navmenu_feedback:
                ShowFragment("Feedback", new FeedbackFragment(), null);
                break;
            case R.id.navmenu_logout:
                logout();
                break;
            case R.id.navmenu_about_us:
                ShowFragment("About Us", new AboutUsFragment(), null);
                break;
            case R.id.action_settings:
                ShowFragment("Settings", new SettingsFragment(), null);
                break;
            case R.id.action_invite:
                showInviteIntent();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showInviteIntent() {
//        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
//                .setMessage(getString(R.string.invitation_message))
//                .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
//                .build();
//        startActivityForResult(intent, REQUEST_INVITE);


        Resources resources = getResources();

        Intent emailIntent = new Intent();
        emailIntent.setAction(Intent.ACTION_SEND);
        // Native email client doesn't currently support HTML, but it doesn't hurt to try in case they fix it
        //emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(resources.getString(R.string.share_email_native)));
        //emailIntent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.share_email_subject));
        emailIntent.setType("message/rfc822");

        PackageManager pm = getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");


        Intent openInChooser = Intent.createChooser(emailIntent, resources.getString(R.string.invitation_title));

        List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
        List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
        for (int i = 0; i < resInfo.size(); i++) {
            // Extract the label, append it, and repackage it in a LabeledIntent
            ResolveInfo ri = resInfo.get(i);
            String packageName = ri.activityInfo.packageName;
            if (packageName.contains("android.email")) {
                emailIntent.setPackage(packageName);
                //} else if(packageName.contains("twitter") || packageName.contains("facebook") || packageName.contains("mms") || packageName.contains("android.gm")) {
            } else if (packageName.contains("com.twitter.android") || packageName.contains("com.facebook")|| packageName.contains("mms")|| packageName.contains("messaging") || packageName.contains("com.whatsapp") || packageName.contains("com.google.android.gm")) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                if (packageName.contains("twitter")) {
                    intent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.invitation_deep_link));
                } else if (packageName.contains("facebook")) {
                    intent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.invitation_message_heading) + "\n\n" + resources.getString(R.string.invitation_deep_link));
//                } else if(packageName.contains("mms")) {
//                    intent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.share_sms));
                } else if(packageName.contains("mms") || packageName.contains("messaging") || packageName.contains("whatsapp")) {
                    intent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.invitation_message_heading) + "\n\n" + resources.getString(R.string.invitation_deep_link));
                } else if (packageName.contains("android.gm")) { // If Gmail shows up twice, try removing this else-if clause and the reference to "android.gm" above

                    intent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.invitation_message));
                    intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(resources.getString(R.string.invitation_message_heading) + "<br><br>" + resources.getString(R.string.invitation_deep_link)));
                    intent.setType("message/rfc822");
                }
                else
                {

                    intent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.invitation_message_heading) + "\n\n" + resources.getString(R.string.invitation_deep_link));
                }

                intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
            }
        }

        // convert intentList to array
        LabeledIntent[] extraIntents = intentList.toArray(new LabeledIntent[intentList.size()]);

        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
        startActivity(openInChooser);
    }

    private void logout() {
        ShowFragment("Home", new HomeFragment(), null);
        Preferences.getInstance().clearPrefs(this);
        Toast.makeText(this, "Logged Out", Toast.LENGTH_LONG).show();
        ManageNavigationView(false, false);

    }

    private void doExit() {

        Helper.getInstance().showFancyAlertDialog(this,
                "Do you want to exit?",
                "MY CCA JK",
                "YES",
                new IFancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                        finish();
                    }
                },
                "NO",
                new IFancyAlertDialogListener() {
                    @Override
                    public void OnClick() {

                    }
                },
                FancyAlertDialogType.WARNING);
    }

    public void OnLoginFailure(String message) {
        Helper.getInstance().showFancyAlertDialog(this,
                message,
                "Login",
                "OK",
                null,
                null,
                null,
                FancyAlertDialogType.ERROR);
        //Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void OnLoginSuccesful(String staffId, long type) {
        Log.d(TAG, "OnLoginSuccesful: ");
        Toast.makeText(this, "Succesfully Logged In", Toast.LENGTH_SHORT).show();
        Preferences.getInstance().setStringPref(this, Preferences.PREF_STAFF_ID, staffId);
        if (type == TYPE_ADMIN) {
            ManageNavigationView(true, true);
            Preferences.getInstance().setIntPref(this, Preferences.PREF_STAFF_TYPE, TYPE_ADMIN);
        } else {
            ManageNavigationView(true, false);
            Preferences.getInstance().setIntPref(this, Preferences.PREF_STAFF_TYPE, TYPE_STAFF);
        }

        ShowFragment("Home", new HomeFragment(), null);
    }

    public void ManageNavigationView(boolean signedIn, boolean admin) {
        MenuItem menuItem = navigationView.getMenu().findItem(R.id.staff_panel);
        if (signedIn) {
            navigationView.getMenu().findItem(R.id.staff_login).setVisible(false);
            menuItem.setVisible(true);
            if (admin) {
                menuItem.getSubMenu().findItem(R.id.navmenu_inspection).setVisible(true);
            } else {
                menuItem.getSubMenu().findItem(R.id.navmenu_inspection).setVisible(false);
            }
        } else {
            navigationView.getMenu().findItem(R.id.staff_login).setVisible(true);
            menuItem.setVisible(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult()", Integer.toString(resultCode));

//        if (requestCode == REQUEST_INVITE) {
//            if (resultCode == RESULT_OK) {
//                // Get the invitation IDs of all sent messages
//                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
//                for (String id : ids) {
//                    Log.d(TAG, "onActivityResult: sent invitation " + id);
//                    return;
//                }
//            } else {
//                // Sending failed or it was canceled, show failure message to the user
//                // ...
//                return;
//            }
//        }
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

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }
}
