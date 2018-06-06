package com.mycca.Activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.mycca.CustomObjects.CustomDrawer.CardDrawerLayout;
import com.mycca.CustomObjects.FancyAlertDialog.FancyAlertDialogType;
import com.mycca.CustomObjects.FancyAlertDialog.IFancyAlertDialogListener;
import com.mycca.CustomObjects.FancyShowCase.FancyShowCaseQueue;
import com.mycca.CustomObjects.Progress.ProgressDialog;
import com.mycca.Fragments.AboutUsFragment;
import com.mycca.Fragments.AddNewsFragment;
import com.mycca.Fragments.BrowserFragment;
import com.mycca.Fragments.ContactUsFragment;
import com.mycca.Fragments.FeedbackFragment;
import com.mycca.Fragments.HomeFragment;
import com.mycca.Fragments.InspectionFragment;
import com.mycca.Fragments.LatestNewsFragment;
import com.mycca.Fragments.LocatorFragment;
import com.mycca.Fragments.LoginFragment;
import com.mycca.Fragments.PanAdhaarUploadFragment;
import com.mycca.Fragments.SettingsFragment;
import com.mycca.Fragments.SubmitGrievanceFragment;
import com.mycca.Fragments.UpdateGrievanceFragment;
import com.mycca.Models.StaffModel;
import com.mycca.Providers.GrievanceDataProvider;
import com.mycca.R;
import com.mycca.Tools.FireBaseHelper;
import com.mycca.Tools.Helper;
import com.mycca.Tools.Preferences;

import java.util.ArrayList;
import java.util.List;

import shortbread.Shortcut;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final int TYPE_ADMIN = 1;
    final int TYPE_STAFF = 2;
    private static final int RC_SIGN_IN = 420;
    String TAG = "MainActivity";

    FrameLayout frameLayout;
    NavigationView navigationView;
    CardDrawerLayout drawerLayout;
    Toolbar toolbar;
    Fragment fragment;
    Bundle bundle;
    String title;

    public FancyShowCaseQueue mQueue;
    StaffModel staffModel;
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolbar();
        bindViews();
        init();
        showFragment("Home", new HomeFragment(), null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                showFragment("Settings", new SettingsFragment(), null);
                break;
            case R.id.action_invite:
                showInviteIntent();
                break;
            case R.id.action_about_us:
                showFragment("About Us", new AboutUsFragment(), null);
                break;
            case R.id.action_feedback:
                showFragment("Feedback", new FeedbackFragment(), null);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.navmenu_home:
                showFragment("Home", new HomeFragment(), null);
                break;
            case R.id.navmenu_visit_cca_website:
                fragment = new BrowserFragment();
                bundle = new Bundle();
                bundle.putString("url", "http://ccajk.gov.in");
                showFragment("CCA J&K", fragment, bundle);
                break;
            case R.id.navmenu_pension:
                fragment = new SubmitGrievanceFragment();
                bundle = new Bundle();
                bundle.putString("Type", FireBaseHelper.GRIEVANCE_PENSION);
                title = "Register Pension Grievance";
                if (checkCurrentUser()) {
                    showFragment(title, fragment, bundle);
                }
                break;
            case R.id.navmenu_gpf:
                fragment = new SubmitGrievanceFragment();
                bundle = new Bundle();
                bundle.putString("Type", FireBaseHelper.GRIEVANCE_GPF);
                title = "Register GPF Grievance";
                if (checkCurrentUser()) {
                    showFragment(title, fragment, bundle);
                }
                break;
            case R.id.navmenu_aadhaar:
                fragment = new PanAdhaarUploadFragment();
                bundle = new Bundle();
                bundle.putString("Root", FireBaseHelper.ROOT_ADHAAR);
                title = "Upload Aadhar";
                if (checkCurrentUser()) {
                    showFragment(title, fragment, bundle);
                }
                break;
            case R.id.navmenu_pan:
                fragment = new PanAdhaarUploadFragment();
                bundle = new Bundle();
                bundle.putString("Root", FireBaseHelper.ROOT_PAN);
                title = "Upload PAN";
                if (checkCurrentUser()) {
                    showFragment(title, fragment, bundle);
                }
                break;
            case R.id.navmenu_life_certificate:
                fragment = new PanAdhaarUploadFragment();
                bundle = new Bundle();
                bundle.putString("Root", FireBaseHelper.ROOT_LIFE);
                title = "Upload Life Certificate";
                if (checkCurrentUser()) {
                    showFragment(title, fragment, bundle);
                }
                break;
            case R.id.navmenu_remarriage_certificate:
                fragment = new PanAdhaarUploadFragment();
                bundle = new Bundle();
                bundle.putString("Root", FireBaseHelper.ROOT_RE_MARRIAGE);
                title = "Upload Re-Marriage Certificate";
                if (checkCurrentUser()) {
                    showFragment(title, fragment, bundle);
                }
                break;
            case R.id.navmenu_reemployment:
                fragment = new PanAdhaarUploadFragment();
                bundle = new Bundle();
                bundle.putString("Root", FireBaseHelper.ROOT_RE_EMPLOYMENT);
                title = "Upload Re-Employment Certificate";
                if (checkCurrentUser()) {
                    showFragment(title, fragment, bundle);
                }
                break;
            case R.id.navmenu_tracking:
                Helper.getInstance().showTrackWindow(this, frameLayout);
                break;
            case R.id.navmenu_contact_us:
                showFragment("Contact Us", new ContactUsFragment(), null);
                break;
            case R.id.navmenu_latest_news:
                fragment = new LatestNewsFragment();
                showFragment("Latest News", fragment, null);
                break;
            case R.id.navmenu_hotspot_locator:
                bundle = new Bundle();
                bundle.putString("Locator", FireBaseHelper.ROOT_HOTSPOTS);
                showFragment("Wifi Hotspot Locations", new LocatorFragment(), bundle);
                break;
            case R.id.navmenu_gp_locator:
                bundle = new Bundle();
                bundle.putString("Locator", FireBaseHelper.ROOT_GP);
                showFragment("Gram Panchayat Locations", new LocatorFragment(), bundle);
                break;
            case R.id.navmenu_login:
                showFragment("CCA JK", new LoginFragment(), null);
                break;
            case R.id.navmenu_update_grievances:
                fragment = new UpdateGrievanceFragment();
                title = "Update Grievance Status";
                if (checkCurrentUser()) {
                    showFragment(title, fragment, null);
                }
                break;
            case R.id.navmenu_inspection:
                fragment = new InspectionFragment();
                title = "Inspection";
                if (checkCurrentUser()) {
                    showFragment(title, fragment, null);
                }
                break;
            case R.id.navmenu_add_news:
                fragment = new AddNewsFragment();
                title = "Add Latest News";
                if (checkCurrentUser()) {
                    showFragment(title, fragment, null);
                }
                break;
            case R.id.navmenu_logout:
                logout();
                break;
            /*case R.id.navmenu_settings:
                showFragment("Settings", new SettingsFragment(), null);
                break;
            case R.id.navmenu_invite:
                showInviteIntent();
                break;
            case R.id.navmenu_feedback:
                showFragment("Feedback", new FeedbackFragment(), null);
                break;
            case R.id.navmenu_about_us:
                showFragment("About Us", new AboutUsFragment(), null);
                break;*/
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Shortcut(id = "hotspotNearby", icon = R.drawable.ic_wifi_black_24dp, shortLabel = "HotSpot Locations")
    public void ShowHotSpotLocations() {
        Log.d(TAG, "ShowHotSpotLocations: ");
        Bundle bundle = new Bundle();
        bundle.putString("Locator", FireBaseHelper.ROOT_HOTSPOTS);
        showFragment("Wifi Hotspot Locations", new LocatorFragment(), bundle);
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void bindViews() {
        frameLayout = findViewById(R.id.fragmentPlaceholder);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        progressDialog = Helper.getInstance().getProgressWindow(this, "Signing in...");
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

        staffModel = Preferences.getInstance().getStaffPref(this, Preferences.PREF_STAFF_DATA);
        if (staffModel != null) {
            if (staffModel.getType() == TYPE_ADMIN)
                ManageNavigationView(true, true);
            else
                ManageNavigationView(true, false);
        } else {
            ManageNavigationView(false, false);
        }
        navigationView.setNavigationItemSelectedListener(this);

        mAuth = FireBaseHelper.getInstance(this).mAuth;
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    public void showFragment(String title, Fragment fragment, Bundle bundle) {
        this.fragment = null;
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setSubtitle("");
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentPlaceholder, fragment).commit();
        navigationView.setCheckedItem(R.id.navmenu_home);
    }

    private boolean checkCurrentUser() {
        if (mAuth.getCurrentUser() == null) {
            showAuthDialog(true);
            return false;
        } else
            return true;
    }

    public void showAuthDialog(boolean skipped) {
        if (skipped) {
            Helper.getInstance().showFancyAlertDialog(this,
                    "Please Sign in with google to access this app feature.",
                    "Login with google",
                    "Sign in",
                    new IFancyAlertDialogListener() {
                        @Override
                        public void OnClick() {
                            signInWithGoogle();
                        }
                    },
                    "Cancel",
                    new IFancyAlertDialogListener() {
                        @Override
                        public void OnClick() {

                        }
                    },
                    FancyAlertDialogType.WARNING);
        } else {
            Helper.getInstance().showFancyAlertDialog(this,
                    "Most App functions require you to authenticate yourself with google. Please Sign in to access all app features.",
                    "Login with google",
                    "Sign in",
                    new IFancyAlertDialogListener() {
                        @Override
                        public void OnClick() {
                            signInWithGoogle();
                        }
                    },
                    "Skip",
                    new IFancyAlertDialogListener() {
                        @Override
                        public void OnClick() {
                        }
                    },
                    FancyAlertDialogType.WARNING
            );
        }
    }

    public void signInWithGoogle() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void signOutFromGoogle() {
        mAuth.signOut();
        mGoogleSignInClient.signOut();
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        Log.d(TAG, "firebaseAuthWithGoogle: " + credential.getSignInMethod());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {

                            Log.d(TAG, "signInWithCredential:success");
                            FireBaseHelper.getInstance(MainActivity.this).setToken();
                            Helper.getInstance().showFancyAlertDialog(MainActivity.this, "", "Sign in Successful", "OK",
                                    new IFancyAlertDialogListener() {
                                        @Override
                                        public void OnClick() {
                                            if (fragment != null) {
                                                showFragment(title, fragment, bundle);
                                            }
                                        }
                                    },
                                    null, null, FancyAlertDialogType.SUCCESS);

                            Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragmentPlaceholder);
                            if (f instanceof SettingsFragment)
                                ((SettingsFragment) f).manageSignOut();
                            else if (f instanceof HomeFragment)
                                ((HomeFragment) f).setupWelcomeBar();

                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Helper.getInstance().showFancyAlertDialog(MainActivity.this, "Please try Again", "Unable to Sign in", "OK", null, null, null, FancyAlertDialogType.ERROR);
                        }

                    }
                });
    }

    private void logout() {
        Helper.getInstance().showFancyAlertDialog(this,
                "Do you want to logout?",
                "MY CCA JK",
                "YES",
                new IFancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                        showFragment("Home", new HomeFragment(), null);
                        Preferences.getInstance().clearStaffPrefs(MainActivity.this);
                        ManageNavigationView(false, false);
                        GrievanceDataProvider.getInstance().setAllGrievanceList(null);
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

    private void showInviteIntent() {

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
        List<LabeledIntent> intentList = new ArrayList<>();
        for (int i = 0; i < resInfo.size(); i++) {
            // Extract the label, append it, and repackage it in a LabeledIntent
            ResolveInfo ri = resInfo.get(i);
            String packageName = ri.activityInfo.packageName;
            if (packageName.contains("android.email")) {
                emailIntent.setPackage(packageName);
            } else if (packageName.contains("com.twitter.android") || packageName.contains("com.facebook") || packageName.contains("mms") || packageName.contains("messaging") || packageName.contains("com.whatsapp") || packageName.contains("com.google.android.gm")) {

                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");

                if (packageName.contains("twitter")) {
                    intent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.invitation_deep_link));
                } else if (packageName.contains("facebook")) {
                    intent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.invitation_message_heading) + "\n\n" + resources.getString(R.string.invitation_deep_link));
                } else if (packageName.contains("mms") || packageName.contains("messaging") || packageName.contains("whatsapp")) {
                    intent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.invitation_message_heading) + "\n\n" + resources.getString(R.string.invitation_deep_link));
                } else if (packageName.contains("android.gm")) { // If Gmail shows up twice, try removing this else-if clause and the reference to "android.gm" above
                    intent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.invitation_message));
                    intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(resources.getString(R.string.invitation_message_heading) + "<br><br>" + resources.getString(R.string.invitation_deep_link)));
                    intent.setType("message/rfc822");
                } else {
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

    public void OnLoginSuccessful(StaffModel staffModel) {
        this.staffModel = staffModel;
        Toast.makeText(this, "Succesfully Logged In", Toast.LENGTH_SHORT).show();
        Preferences.getInstance().setStaffPref(this, Preferences.PREF_STAFF_DATA, staffModel);
        if (staffModel.getType() == TYPE_ADMIN) {
            ManageNavigationView(true, true);
        } else {
            ManageNavigationView(true, false);
        }

        showFragment("Home", new HomeFragment(), null);
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
    public void onBackPressed() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragmentPlaceholder);

        if (mQueue != null) {
            Helper.getInstance().showFancyAlertDialog(this,
                    "Skip CCA Tutorial Messages?",
                    "CCA Help",
                    "SKIP", new IFancyAlertDialogListener() {
                        @Override
                        public void OnClick() {
                            mQueue.cancel(true);
                            Preferences.getInstance().setTutorialPrefs(MainActivity.this);
                        }
                    },
                    "CANCEL", new IFancyAlertDialogListener() {
                        @Override
                        public void OnClick() {

                        }
                    },
                    FancyAlertDialogType.WARNING);

        } else if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);

        } else if (f instanceof HomeFragment) {
            doExit();
        } else if (f instanceof BrowserFragment) {
            if (((BrowserFragment) f).canGoBack()) {
                ((BrowserFragment) f).goBack();
            } else {
                ((BrowserFragment) f).stopLoading();
                showFragment("Home", new HomeFragment(), null);
            }
        } else {
            showFragment("Home", new HomeFragment(), null);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult()", Integer.toString(resultCode));
        List<Fragment> allFragments = getSupportFragmentManager().getFragments();

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                progressDialog.show();
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "signed in: " + account.getEmail());
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
                progressDialog.dismiss();
            }
        } else {
            for (Fragment frag : allFragments) {
                frag.onActivityResult(requestCode, resultCode, data);
            }
        }
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
