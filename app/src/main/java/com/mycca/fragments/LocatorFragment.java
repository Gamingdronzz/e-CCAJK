package com.mycca.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
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
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.mycca.R;
import com.mycca.activity.MainActivity;
import com.mycca.custom.Progress.ProgressDialog;
import com.mycca.listeners.OnConnectionAvailableListener;
import com.mycca.models.LocationModel;
import com.mycca.providers.LocationDataProvider;
import com.mycca.tabs.locator.TabAllLocations;
import com.mycca.tabs.locator.TabNearby;
import com.mycca.tools.ConnectionUtility;
import com.mycca.tools.CustomLogger;
import com.mycca.tools.FireBaseHelper;
import com.mycca.tools.Helper;
import com.mycca.tools.IOHelper;
import com.mycca.tools.Preferences;

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

        progressDialog = Helper.getInstance().getProgressWindow(activity, getString(R.string.please_wait));
        imageButtonRefresh.setOnClickListener(v -> checkConnection(false));
        String noInternet = textViewLocatorInfo.getText() + "\n" + getString(R.string.refresh);
        textViewLocatorInfo.setText(noInternet);

        //fetch from local storage
        getLocationsFromLocalStorage();

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
                CustomLogger.getInstance().logDebug( "onPageSelected: selected = " + position);

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

    private void getLocationsFromLocalStorage() {

        IOHelper.getInstance().readFromFile(locatorType, getContext(),false, jsonObject -> {
            String json = (String) jsonObject;
            try {
                Gson gson = new Gson();
                Type collectionType = new TypeToken<ArrayList<LocationModel>>() {
                }.getType();
                locationModelArrayList = gson.fromJson(json, collectionType);
                if (locationModelArrayList == null) {
                    checkConnection(false);
                } else {
                    checkConnection(true);
                }
            } catch (JsonParseException jpe) {
                jpe.printStackTrace();
                checkConnection(false);
            }
        });

    }

    private void addLocationsToLocalStorage(ArrayList<LocationModel> locationModels) {
        try {
            String jsonObject = Helper.getInstance().getJsonFromObject(locationModels);
            CustomLogger.getInstance().logDebug( "Json: " + jsonObject);
            CustomLogger.getInstance().logDebug( "adding LocationsToLocalStorage: ");
            IOHelper.getInstance().writeToFile(jsonObject, locatorType, false,getContext());
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
                    CustomLogger.getInstance().logDebug( "init: No Locations in Local Storage");
                    fetchLocationsFromFirebase();
                } else {
                    CustomLogger.getInstance().logDebug( "init: Locations found in local storage");
                    checkNewLocationsInFirebase();
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

    private void checkNewLocationsInFirebase() {

        ValueEventListener vel = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (locationModelArrayList.size() == dataSnapshot.getChildrenCount()) {
                        CustomLogger.getInstance().logDebug( "init: same amount of locations in firebase");
                        setTabLayout();
                    } else {
                        CustomLogger.getInstance().logDebug( "init: new locations in firebase");
                        fetchLocationsFromFirebase();
                    }
                } catch (DatabaseException dbe) {
                    dbe.printStackTrace();
                    setTabLayout();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                CustomLogger.getInstance().logDebug( "onCancelled: " + databaseError.getMessage());
            }
        };
        FireBaseHelper.getInstance(activity).getDataFromFirebase(vel,
                FireBaseHelper.NONVERSIONED_STATEWISE, false,
                Preferences.getInstance().getStatePref(activity, Preferences.PREF_STATE_DATA).getCode(),
                locatorType);
    }

    private void fetchLocationsFromFirebase() {

        locationModelArrayList = new ArrayList<>();
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue() != null) {
                    try {
                        CustomLogger.getInstance().logDebug( "onChildAdded: " + dataSnapshot.getKey());
                        LocationModel location = dataSnapshot.getValue(LocationModel.class);
                        locationModelArrayList.add(location);
                    } catch (DatabaseException dbe) {
                        dbe.printStackTrace();
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CustomLogger.getInstance().logDebug( "onDataChange: got locations from firebase");
                setTabLayout();
                if (locationModelArrayList.size() > 0)
                    addLocationsToLocalStorage(locationModelArrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        FireBaseHelper.getInstance(activity).getDataFromFirebase(childEventListener,
                FireBaseHelper.NONVERSIONED_STATEWISE,
                Preferences.getInstance().getStatePref(activity, Preferences.PREF_STATE_DATA).getCode(),
                locatorType);
        FireBaseHelper.getInstance(activity).getDataFromFirebase(valueEventListener,
                FireBaseHelper.NONVERSIONED_STATEWISE, true,
                Preferences.getInstance().getStatePref(activity, Preferences.PREF_STATE_DATA).getCode(),
                locatorType);
    }

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
                    return getString(R.string.nearby);
                case 0:
                    return getString(R.string.all_locations);
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
            CustomLogger.getInstance().logDebug( "onRequestPermissionsResult: " + frag.toString());
            frag.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> allFragments = getChildFragmentManager().getFragments();

        for (Fragment frag : allFragments) {
            CustomLogger.getInstance().logDebug( "onActivityResult: " + frag.toString());
            frag.onActivityResult(requestCode, resultCode, data);
        }


    }
}
