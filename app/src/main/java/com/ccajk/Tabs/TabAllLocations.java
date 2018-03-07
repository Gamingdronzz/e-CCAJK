package com.ccajk.Tabs;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.ccajk.Adapter.RecyclerViewAdapterHotspotLocation;
import com.ccajk.Models.LocationModel;
import com.ccajk.R;
import com.ccajk.Tools.Helper;

import java.util.ArrayList;


//Our class extending fragment
public class TabAllLocations extends Fragment {

    String TAG = "All Locations";
    RadioGroup radioGroup;
    Spinner districtSpinner, stateSpinner;
    RecyclerView recyclerView;
    RecyclerViewAdapterHotspotLocation adapter;
    String[] locations;
    ArrayList<LocationModel> allLocations = new ArrayList<>();
    ArrayList<LocationModel> filteredLocations = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_all_locations, container, false);
        init(view);
        return view;
    }

    private void init(View view) {

        radioGroup = view.findViewById(R.id.search);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioButtonName) {

                    ShowSearchByNameDialog();

                } else if (checkedId == R.id.radioButtonState) {

                    ShowSearchByStateDialog();
                }
            }
        });

        allLocations = new ArrayList<>();
        allLocations = Helper.getInstance().getLocationModels();
        adapter = new RecyclerViewAdapterHotspotLocation(allLocations);

        locations = new String[allLocations.size()];
        int i = 0;
        for (LocationModel locationModel : allLocations) {
            locations[i] = locationModel.getLocationName();
            i++;
        }

        recyclerView = view.findViewById(R.id.recyclerview_locations);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.getItemAnimator().setAddDuration(1000);
        /*getLocationList();*/

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && gestureDetector.onTouchEvent(e)) {
                    int pos = rv.getChildAdapterPosition(child);
                    LocationModel location = allLocations.get(pos);
                    // String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%s", lat, log ,Uri.encode(list.get(pos)));
                    Uri uri = Uri.parse("geo:0,0?q=" + (location.getLocation().latitude + "," + location.getLocation().longitude + "(" + Uri.encode(location.getLocationName()) + ")"));
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    getContext().startActivity(intent);
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });

    }


    private void ShowSearchByNameDialog() {

        radioGroup.clearCheck();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_search_by_name, (ViewGroup) getView(), false);

        final AutoCompleteTextView input = viewInflated.findViewById(R.id.input);
        ArrayAdapter<String> actAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_dropdown_item_1line, locations);
        input.setAdapter(actAdapter);

        builder.setView(viewInflated);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = input.getText().toString();
                FilterLocationsByName(name);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }


    private void ShowSearchByStateDialog() {
        radioGroup.clearCheck();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_search_by_state, (ViewGroup) getView(), false);

        stateSpinner = viewInflated.findViewById(R.id.spinnerState);
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(
                getContext(), android.R.layout.simple_spinner_item, Helper.stateList);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(adapterSpinner);

        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                populateSpinnerDistrict(viewInflated, Helper.States.values()[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        builder.setView(viewInflated);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FilterLocationsByState(stateSpinner.getSelectedItem(), districtSpinner.getSelectedItem());
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }


    private void populateSpinnerDistrict(View view, Helper.States state) {

        districtSpinner = view.findViewById(R.id.spinnerDistrict);
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(
                getContext(), android.R.layout.simple_spinner_item, Helper.getInstance().getDistrictsOfState(state));
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        districtSpinner.setAdapter(adapterSpinner);
    }

    private void FilterLocationsByName(String name) {
        filteredLocations = new ArrayList<>();
        for (LocationModel locationModel : allLocations) {
            if (locationModel.getLocationName().equals(name))
                filteredLocations.add(locationModel);
        }
        if (filteredLocations.size() == 0) {
            Toast.makeText(getContext(), "No Locations found", Toast.LENGTH_SHORT).show();
            adapter = new RecyclerViewAdapterHotspotLocation(allLocations);
        } else {
            adapter = new RecyclerViewAdapterHotspotLocation(filteredLocations);
        }
        recyclerView.setAdapter(adapter);
    }


    private void FilterLocationsByState(Object selectedItem, Object selectedItem1) {
        filteredLocations = new ArrayList<>();
        for (LocationModel locationModel : allLocations) {
            if (locationModel.getState().equals(selectedItem)) {
                if (locationModel.getDistrict().equals(selectedItem1))
                    filteredLocations.add(locationModel);
            }
        }
        if (filteredLocations.size() == 0) {
            Toast.makeText(getContext(), "No Locations found", Toast.LENGTH_SHORT).show();
            adapter = new RecyclerViewAdapterHotspotLocation(allLocations);
        } else {
            adapter = new RecyclerViewAdapterHotspotLocation(filteredLocations);
        }
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        // Check for the rotation
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this.getContext(), "LANDSCAPE", Toast.LENGTH_SHORT).show();
            setupGridLayout(true);

        } else if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this.getContext(), "PORTRAIT", Toast.LENGTH_SHORT).show();
            /*if (isTab) {
                setupGridLayout(true);
            } else {
                setupGridLayout(false);
            }*/
        }
    }


    private void setupGridLayout(boolean multiColumn) {
        if (multiColumn) {
            GridLayoutManager manager = new GridLayoutManager(this.getContext(), 2);
           /* manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {

                }
            });*/
            recyclerView.setLayoutManager(manager);
        } else {
            GridLayoutManager manager = new GridLayoutManager(this.getContext(), 1);
            recyclerView.setLayoutManager(manager);
        }
    }


}