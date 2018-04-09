package com.ccajk.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.ccajk.Fragments.AadharPanCheckFragment;
import com.ccajk.Fragments.ContactUsFragment;
import com.ccajk.Fragments.GrievanceFragment;
import com.ccajk.Fragments.HomeFragment;
import com.ccajk.Fragments.HotspotLocationFragment;
import com.ccajk.Fragments.InspectionFragment;
import com.ccajk.Fragments.StatisticsFragment;
import com.ccajk.Fragments.TrackFragment;
import com.ccajk.Interfaces.ILoginProcessor;
import com.ccajk.R;
import com.ccajk.Tools.Helper;
import com.ccajk.Tools.Preferences;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ILoginProcessor {

    String TAG = "firebase";
    FrameLayout frameLayout;
    NavigationView navigationView;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        frameLayout = findViewById(R.id.contentPanel);
       /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/
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

        Fragment fragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.contentPanel, fragment).commit();

    }


    @Override

    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.contentPanel);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else if (f instanceof HomeFragment) {
            doExit();
        } else {
            getSupportActionBar().setTitle("Home");
            getSupportFragmentManager().beginTransaction().replace(R.id.contentPanel, new HomeFragment()).commit();
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment;
        Bundle bundle;
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
            case R.id.navmenu_hotspot_locator:
                getSupportActionBar().setTitle("Wifi Hotspot Locations");
                fragment = new HotspotLocationFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.contentPanel, fragment).commit();
                break;
            case R.id.navmenu_rti:
                Intent intent = new Intent(HomeActivity.this, BrowserActivity.class);
                intent.putExtra("url", "https://rtionline.gov.in/");
                intent.putExtra("title", "RTI");
                startActivity(intent);
                break;
            case R.id.navmenu_statistics:
                getSupportActionBar().setTitle("Statistics");
                fragment = new StatisticsFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.contentPanel, fragment).commit();
                break;
            case R.id.navmenu_inspection:
                getSupportActionBar().setTitle("Inspection");
                fragment = new InspectionFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.contentPanel, fragment).commit();
                break;
            case R.id.navmenu_pension:
                bundle = new Bundle();
                bundle.putInt("Category", Helper.getInstance().CATEGORY_PENSION);
                getSupportActionBar().setTitle("Pension Grievance Registeration");
                fragment = new GrievanceFragment();
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.contentPanel, fragment).commit();
                break;
            case R.id.navmenu_gpf:
                bundle = new Bundle();
                bundle.putInt("Category", Helper.getInstance().CATEGORY_GPF);
                getSupportActionBar().setTitle("GPF Grievance Registeration");
                fragment = new GrievanceFragment();
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.contentPanel, fragment).commit();
                break;
            case R.id.navmenu_aadhaar:
                fragment = new AadharPanCheckFragment();
                bundle = new Bundle();
                bundle.putInt("UploadType", Helper.getInstance().UPLOAD_TYPE_ADHAAR);
                getSupportActionBar().setTitle("Upload Aadhaar");
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.contentPanel, fragment).commit();
                break;
            case R.id.navmenu_pan:
                fragment = new AadharPanCheckFragment();
                bundle = new Bundle();
                bundle.putInt("UploadType", Helper.getInstance().UPLOAD_TYPE_PAN);
                getSupportActionBar().setTitle("Upload PAN");
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.contentPanel, fragment).commit();
                break;
            case R.id.navmenu_tracking:
                getSupportActionBar().setTitle("Track Grievance");
                fragment = new TrackFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.contentPanel, fragment).commit();
                break;
            case R.id.navmenu_login:
                showLoginPopup();
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
    }

    private void showLoginPopup() {
        ImageView ppo, pwd;
        ImageButton close;
        final AutoCompleteTextView autoCompleteTextView;
        final EditText editText;
        final View mProgressView;

        View popupView = LayoutInflater.from(HomeActivity.this).inflate(R.layout.dialog_login, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        ppo = popupView.findViewById(R.id.image_ppo);
        ppo.setImageDrawable(AppCompatResources.getDrawable(HomeActivity.this, R.drawable.ic_person_black_24dp));
        pwd = popupView.findViewById(R.id.image_pwd);
        pwd.setImageDrawable(AppCompatResources.getDrawable(HomeActivity.this, R.drawable.ic_password));
        autoCompleteTextView = popupView.findViewById(R.id.ppo);
        editText = popupView.findViewById(R.id.password);
        mProgressView = popupView.findViewById(R.id.login_progress);
        close = popupView.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        Button signin = popupView.findViewById(R.id.sign_in_button);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestLogin(autoCompleteTextView.getText().toString(), editText.getText().toString());

                mProgressView.setVisibility(View.VISIBLE);
                mProgressView.animate().setDuration(2000).alpha(1).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mProgressView.setVisibility(View.GONE);
                    }
                });
            }
        });

        popupWindow.setFocusable(true);
        popupWindow.update();
        popupWindow.showAtLocation(frameLayout, Gravity.CENTER, 0, 0);

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
    public void RequestLogin(final String pensionerCode, final String password) {

        changePrefrences(pensionerCode,"Name");
        /*DatabaseReference dbref = databaseReference.child("user").child(pensionerCode);
        Log.d(TAG, "RequestLogin: ");
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: " + dataSnapshot.toString());
                if (dataSnapshot == null) {
                    OnUserNotExist();
                }
                else if (dataSnapshot != null) {
                    if(dataSnapshot.child("password").exists()) {
                        String dbpassword = dataSnapshot.child("password").getValue().toString();
                        if (dbpassword.equals(password)) {
                            OnLoginSuccesful(dataSnapshot);
                        } else {
                            OnLoginFailure();
                        }
                    }
                    else
                    {
                        OnLoginFailure();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Error");
                OnUserNotExist();
            }
        });*/
    }

    @Override
    public void OnLoginSuccesful(DataSnapshot dataSnapshot) {
        String username = dataSnapshot.child("name").getValue().toString();
        String ppo = dataSnapshot.child("emp_id").getValue().toString();
        changePrefrences(ppo, username);
    }

    @Override
    public void OnLoginFailure() {

    }

    @Override
    public void OnUserNotExist() {
        Log.d(TAG, "User does not exist");
    }

    private void changePrefrences(String ppo, String user) {
        Preferences.getInstance().setSignedIn(this, true);
        Preferences.getInstance().setPpo(this, ppo);
        navigationView.getMenu().findItem(R.id.staff_login).setVisible(false);
        navigationView.getMenu().findItem(R.id.staff_panel).setVisible(true);
    }

}
