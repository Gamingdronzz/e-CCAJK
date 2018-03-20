package com.ccajk.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.ccajk.Fragments.CheckFragment;
import com.ccajk.Fragments.ContactUsFragment;
import com.ccajk.Fragments.GrievanceFragment;
import com.ccajk.Fragments.HomeFragment;
import com.ccajk.Fragments.HotspotLocationFragment;
import com.ccajk.Fragments.InspectionFragment;
import com.ccajk.Fragments.StatisticsFragment;
import com.ccajk.Interfaces.ILoginProcessor;
import com.ccajk.R;
import com.ccajk.Tools.Helper;
import com.ccajk.Tools.Prefrences;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ILoginProcessor {

    String TAG = "firebase";
    FrameLayout frameLayout;
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
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = findViewById(R.id.nav_view);
        if (Prefrences.getInstance().getSignedIn(this)) {
            navigationView.getMenu().findItem(R.id.staff_login).setVisible(false);
            navigationView.getMenu().findItem(R.id.staff_other).setVisible(true);
        } else {
            navigationView.getMenu().findItem(R.id.staff_other).setVisible(false);
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
        }/* else {
            doExit();
        }*/ else if (f instanceof HomeFragment) {
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

   /* @Override
       public boolean onPrepareOptionsMenu(Menu menu) {

        if (Prefrences.getInstance().getSignedIn(this) == false) {
            MenuItem login = menu.add("Login");
            login.setIcon(AppCompatResources.getDrawable(this, R.drawable.ic_logout_24dp));
            login.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            login.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                *//*Intent intent=new Intent(HomeActivity.this,LoginActivity.class);
                startActivity(intent);
                *//*
                    showLoginPopup();
                    return true;
                }
            });
        } else {
            MenuItem logout = menu.add("Logout");
            logout.setIcon(AppCompatResources.getDrawable(this, R.drawable.ic_logout_24dp));
            logout.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            logout.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    confirmLogout();
                    return true;
                }
            });
        }
        return super.onPrepareOptionsMenu(menu);
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
                fragment = new CheckFragment();
                bundle = new Bundle();
                bundle.putInt("UploadType", Helper.getInstance().UPLOAD_TYPE_ADHAAR);
                getSupportActionBar().setTitle("Upload Aadhaar");
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.contentPanel, fragment).commit();
                break;
            case R.id.navmenu_pan:
                fragment = new CheckFragment();
                bundle = new Bundle();
                bundle.putInt("UploadType", Helper.getInstance().UPLOAD_TYPE_PAN);
                getSupportActionBar().setTitle("Upload PAN");
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.contentPanel, fragment).commit();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
        DatabaseReference dbref = databaseReference.child("user").child(pensionerCode);
        Log.d(TAG, "RequestLogin: ");
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: " + dataSnapshot.toString());
                if (dataSnapshot == null) {
                    OnUserNotExist();
                }
                if (dataSnapshot != null) {
                    String dbpassword = dataSnapshot.child("password").getValue().toString();
                    if (dbpassword.equals(password)) {
                        OnLoginSuccesful(dataSnapshot);
                    } else {
                        OnLoginFailure();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Error");
                OnUserNotExist();
            }
        });
    }

    @Override
    public void OnLoginSuccesful(DataSnapshot dataSnapshot) {
        String username = dataSnapshot.child("name").getValue().toString();
        String ppo = dataSnapshot.child("ppo").getValue().toString();
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
        Prefrences.getInstance().setSignedIn(this, true);
        Prefrences.getInstance().setPpo(this, ppo);
    }

}
