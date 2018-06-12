package com.mycca.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.mycca.Activity.MainActivity;
import com.mycca.CustomObjects.Progress.ProgressDialog;
import com.mycca.Listeners.OnConnectionAvailableListener;
import com.mycca.Models.LocationModel;
import com.mycca.Providers.LocationDataProvider;
import com.mycca.R;
import com.mycca.Tabs.Locator.TabAllLocations;
import com.mycca.Tabs.Locator.TabNearby;
import com.mycca.Tools.ConnectionUtility;
import com.mycca.Tools.Helper;
import com.mycca.Tools.Preferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LocatorFragment extends Fragment {

    public TabLayout tabLayout;
    public ViewPager viewPager;
    ProgressDialog progressDialog;
    public ArrayList<LocationModel> locationModelArrayList = new ArrayList<>();

    public final static int INT_LOCATOR_TAB_ITEMS = 2;
    String TAG = "locator";
    TextView textViewLocatorInfo;
    String locatorType;
    RelativeLayout relativeLayoutNoInternet;
    LinearLayout linearLayoutTab;
    ImageButton imageButtonRefresh;
    MainActivity activity;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    public LocatorFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_locator_layout, container, false);

        if (getArguments() != null)
            locatorType = getArguments().getString("Locator");
        bindViews(view);
        init();
        return view;
    }

    private void bindViews(View view) {
        tabLayout = view.findViewById(R.id.tab_locator);
        viewPager = view.findViewById(R.id.viewpager_locator);
        relativeLayoutNoInternet = view.findViewById(R.id.layout_no_internet_locator_fragment);
        linearLayoutTab = view.findViewById(R.id.linear_layout_locator_fragment);
        textViewLocatorInfo = view.findViewById(R.id.textview_locator_info);
        imageButtonRefresh = view.findViewById(R.id.image_btn_refresh_tab_all);
    }

    private void init() {

        activity = (MainActivity) getActivity();
        manageNoLocationLayout(true);

        progressDialog = Helper.getInstance().getProgressWindow(activity, "Getting Locations...");
        imageButtonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkConnection(false);
            }
        });
        textViewLocatorInfo.setText(textViewLocatorInfo.getText() + "\nTurn On Internet and Refresh");

        //fetch from local storage
        locationModelArrayList = getLocationsFromLocalStorage();
        if (locationModelArrayList == null) {
            checkConnection(false);
        } else {
            checkConnection(true);
        }
    }

    private void setTabLayout() {
        setData();
        manageNoLocationLayout(false);
        final MyAdapter adapter = new MyAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected: selected = " + position);

                Fragment fragment = adapter.getCurrentFragment();
                if (fragment instanceof TabNearby) {
                    ((TabNearby) fragment).startLocationProcess();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        progressDialog.dismiss();
    }

    public void setData() {
        LocationDataProvider.getInstance().setLocationModelArrayList(locatorType, locationModelArrayList);
    }

    private void manageNoLocationLayout(boolean show) {
        if (show) {
            relativeLayoutNoInternet.setVisibility(View.VISIBLE);
            linearLayoutTab.setVisibility(View.GONE);
        } else {
            relativeLayoutNoInternet.setVisibility(View.GONE);
            linearLayoutTab.setVisibility(View.VISIBLE);
        }
    }

    private ArrayList<LocationModel> getLocationsFromLocalStorage() {

        String json = readFromFile();
        ArrayList<LocationModel> arrayList;
        try {
            Gson gson = new Gson();
            Type collectionType = new TypeToken<ArrayList<LocationModel>>() {
            }.getType();
            arrayList = gson.fromJson(json, collectionType);
            if (arrayList != null)
                Log.d(TAG, "Getting Locations From Local Storage: size = " + arrayList.size());
            return arrayList;
        } catch (JsonParseException jpe) {
            jpe.printStackTrace();
        }
        return null;
    }

    private void addLocationsToLocalStorage(ArrayList<LocationModel> locationModels) {
        try {
            Gson gson = new Gson();
            String jsonObject = gson.toJson(locationModels);
            Log.d(TAG, "adding LocationsToLocalStorage: ");
            writeToFile(jsonObject);
        } catch (JsonParseException jpe) {
            jpe.printStackTrace();
        }
    }

    private void checkConnection(boolean checkNetwork) {
        progressDialog.show();
        ConnectionUtility connectionUtility = new ConnectionUtility(new OnConnectionAvailableListener() {
            @Override
            public void OnConnectionAvailable() {
                if (locationModelArrayList == null) {
                    Log.d(TAG, "init: No Locations in Local Storage");
                    fetchLocationsFromFirebase();
                } else {
                    Log.d(TAG, "init: Locations found in local storage");
                    checkNewLocationsinFirebase();
                }
            }

            @Override
            public void OnConnectionNotAvailable() {
                if (locationModelArrayList == null) {
                    manageNoLocationLayout(true);
                    progressDialog.dismiss();
                } else {
                    setTabLayout();
                }
            }
        });

        if (!checkNetwork) {
            connectionUtility.checkConnectionAvailability();
        } else {
            String networkClass = connectionUtility.getNetworkClass(getContext());
            if (networkClass.equals(connectionUtility._2G)) {
                setTabLayout();
            } else {
                connectionUtility.checkConnectionAvailability();
            }
        }

    }

    private void checkNewLocationsinFirebase() {
        databaseReference.child(locatorType)
                .child(Preferences.getInstance().getStringPref(getContext(), Preferences.PREF_STATE))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            if (locationModelArrayList.size() == dataSnapshot.getChildrenCount()) {
                                Log.d(TAG, "init: same amount of locations in firebase");
                                setTabLayout();
                            } else {
                                Log.d(TAG, "init: new locations in firebase");
                                fetchLocationsFromFirebase();
                            }
                        } catch (DatabaseException dbe) {
                            dbe.printStackTrace();
                            setTabLayout();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                    }
                });
    }

    private void fetchLocationsFromFirebase() {

        locationModelArrayList = new ArrayList<>();
        //DatabaseReference databaseReference = FireBaseHelper.getInstance(getContext()).databaseReference;
        databaseReference.child(locatorType)
                .child(Preferences.getInstance().getStringPref(getContext(), Preferences.PREF_STATE))
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot.getValue() != null) {
                            try {
                                Log.d(TAG, "onChildAdded: " + dataSnapshot.getKey());
                                LocationModel location = dataSnapshot.getValue(LocationModel.class);
                                locationModelArrayList.add(location);
                            } catch (DatabaseException dbe) {
                                dbe.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        databaseReference.child(locatorType)
                .child(Preferences.getInstance().getStringPref(getContext(), Preferences.PREF_STATE))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: got locations from firebase");
                        setTabLayout();
                        if (locationModelArrayList.size() > 0)
                            addLocationsToLocalStorage(locationModelArrayList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void writeToFile(String jsonObject) {

        String filename = locatorType + " " + Preferences.getInstance().getStringPref(getContext(), Preferences.PREF_STATE);
        FileOutputStream outputStream;
        try {
            outputStream = activity.openFileOutput(filename + ".json", Context.MODE_PRIVATE);
            outputStream.write(jsonObject.getBytes());
            Log.d(TAG, "writeToFile: ");
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String readFromFile() {

        String filename = locatorType + " " + Preferences.getInstance().getStringPref(getContext(), Preferences.PREF_STATE);
        try {
            File file = new File(activity.getFilesDir(), filename + ".json");
            Log.d(TAG, "readFromFile: file path = " + file.getPath());
            FileInputStream fin = activity.openFileInput(file.getName());
            int size = fin.available();
            byte[] buffer = new byte[size];
            fin.read(buffer);
            Log.d(TAG, "readFromFile: ");
            fin.close();
            return new String(buffer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //    private void showTutorial() {
//
//        final FancyShowCaseView fancyShowCaseView2 = new FancyShowCaseView.Builder(activity)
//                .title("Tap on location to open in Google Maps")
//                .focusOn(viewPager)
//                .focusCircleRadiusFactor(.6)
//                .titleStyle(R.style.FancyShowCaseDefaultTitleStyle, Gravity.BOTTOM | Gravity.CENTER)
//                .build();
//        final FancyShowCaseView fancyShowCaseView1 = new FancyShowCaseView.Builder(activity)
//                .title("Click to search location")
//                .focusRectAtPosition(Resources.getSystem().getDisplayMetrics().widthPixels / 4, Resources.getSystem().getDisplayMetrics().heightPixels / 12 * 11, Resources.getSystem().getDisplayMetrics().widthPixels / 2, 100)
//                .fitSystemWindows(true)
//                .build();
//        final FancyShowCaseView fancyShowCaseView3 = new FancyShowCaseView.Builder(activity)
//                .title("Click to view locations districtwise")
//                .focusRectAtPosition(Resources.getSystem().getDisplayMetrics().widthPixels * 3 / 4, Resources.getSystem().getDisplayMetrics().heightPixels / 12 * 11, Resources.getSystem().getDisplayMetrics().widthPixels / 2, 100)
//                .fitSystemWindows(true)
//                .build();
//        final FancyShowCaseView fancyShowCaseView4 = new FancyShowCaseView.Builder(activity)
//                .title("-------->\nSwipe to view nearby hotspots")
//                .focusCircleAtPosition(Resources.getSystem().getDisplayMetrics().widthPixels * 3 / 4, Resources.getSystem().getDisplayMetrics().heightPixels / 6, 150)
//                .build();
//
//        activity.mQueue = new FancyShowCaseQueue()
//                .add(fancyShowCaseView2)
//                .add(fancyShowCaseView1)
//                .add(fancyShowCaseView3)
//                .add(fancyShowCaseView4);
//
//        activity.mQueue.setCompleteListener(new com.mycca.CustomObjects.FancyShowCase.OnCompleteListener() {
//            @Override
//            public void onComplete() {
//                activity.mQueue = null;
//            }
//        });
//
//        activity.mQueue.show();
//    }

    class MyAdapter extends FragmentPagerAdapter {
        private Fragment mCurrentFragment;

        Fragment getCurrentFragment() {
            return mCurrentFragment;
        }

        MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    TabAllLocations tabAllLocations = new TabAllLocations();
                    tabAllLocations.setArguments(getArguments());
                    return tabAllLocations;
                case 1:
                    TabNearby tabNearby = new TabNearby();
                    tabNearby.setArguments(getArguments());
                    return tabNearby;

            }
            return null;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (getCurrentFragment() != object) {
                mCurrentFragment = ((Fragment) object);
            }
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public int getCount() {
            return INT_LOCATOR_TAB_ITEMS;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 1:
                    return "NEARBY";
                case 0:
                    return "ALL LOCATIONS";
            }
            return null;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> allFragments = getChildFragmentManager().getFragments();

        for (Fragment frag : allFragments) {
            Log.d(TAG, "onRequestPermissionsResult: " + frag.toString());
            frag.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> allFragments = getChildFragmentManager().getFragments();

        for (Fragment frag : allFragments) {
            Log.d(TAG, "onActivityResult: " + frag.toString());
            frag.onActivityResult(requestCode, resultCode, data);
        }


    }
}
