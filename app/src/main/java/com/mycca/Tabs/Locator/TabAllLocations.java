package com.mycca.Tabs.Locator;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.mycca.Adapter.RecyclerViewAdapterHotspotLocation;
import com.mycca.Listeners.ClickListener;
import com.mycca.Listeners.RecyclerViewTouchListeners;
import com.mycca.Models.LocationModel;
import com.mycca.Providers.LocationDataProvider;
import com.mycca.R;

import java.util.ArrayList;


//Our class extending fragment
public class TabAllLocations extends Fragment {

    String TAG = "All Locations";
    RadioGroup radioGroup;
    Spinner districtSpinner, stateSpinner;
    RecyclerView recyclerView;
    RecyclerViewAdapterHotspotLocation adapter;
    String[] locations;
    ArrayList<LocationModel> stateLocations = new ArrayList<>();
    ArrayList<LocationModel> filteredLocations = new ArrayList<>();

    String locatorType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_all_locations, container, false);
        locatorType = getArguments().getString("Locator");
        Log.d(TAG, "onCreateView: locator type = " + locatorType);
        init(view);
        return view;
    }

    private void init(View view) {

        /*radioGroup = view.findViewById(R.id.search);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioButtonName) {

                    ShowSearchByNameDialog();

                } else if (checkedId == R.id.radioButtonState) {

                    ShowSearchByStateDialog();
                }
            }
        });*/

        Button search = view.findViewById(R.id.btn_search_loc);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowSearchByNameDialog();
            }
        });

        Button sort = view.findViewById(R.id.button_sort_loc);
        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowSortDialog();
            }
        });

        stateLocations = LocationDataProvider.getInstance().getLocationModelArrayList(locatorType);
//        if (locatorType.equals(FireBaseHelper.getInstance().ROOT_GP)) {
//            Log.d(TAG, "init: GP");
//            stateLocations = LocationDataProvider.getInstance().getGpLocationModelArrayList();
//        } else {
//            Log.d(TAG, "init: Hotspot");
//            stateLocations = LocationDataProvider.getInstance().getHotspotLocationModelArrayList();
//        }
        adapter = new RecyclerViewAdapterHotspotLocation(stateLocations);

        locations = new String[stateLocations.size()];
        int i = 0;
        for (LocationModel locationModel : stateLocations) {
            locations[i] = locationModel.getLocationName();
            i++;
        }

        recyclerView = view.findViewById(R.id.recyclerview_locations);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        recyclerView.getItemAnimator().setAddDuration(1000);


        recyclerView.addOnItemTouchListener(new RecyclerViewTouchListeners(getContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //int pos = rv.getChildAdapterPosition(child);
                LocationModel location = stateLocations.get(position);
                Uri uri = Uri.parse("geo:0,0?q=" + (location.getLatitude() + "," + location.getLongitude() + "(" + Uri.encode(location.getLocationName()) + ")"));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                getContext().startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }

    private void ShowSortDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_sort_locations, (ViewGroup) getView(), false);
        final RadioGroup radioGroupSort = viewInflated.findViewById(R.id.radio_group_sort);
        builder.setView(viewInflated);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (radioGroupSort.getCheckedRadioButtonId() == R.id.rBName) {
                    LocationDataProvider.getInstance().sortByName(stateLocations);
                    //adapter.notifyDataSetChanged();
                }
                if (radioGroupSort.getCheckedRadioButtonId() == R.id.rBDistrict) {
                    LocationDataProvider.getInstance().sortByDistrict(stateLocations);
                    //adapter.notifyDataSetChanged();
                }
                adapter = new RecyclerViewAdapterHotspotLocation(stateLocations);
                recyclerView.setAdapter(adapter);
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

    private void ShowSearchByNameDialog() {

        //radioGroup.clearCheck();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_search_by_name, (ViewGroup) getView(), false);

        final AutoCompleteTextView input = viewInflated.findViewById(R.id.input);
        input.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_drawable_location, 0, 0, 0);
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

    private void FilterLocationsByName(String name) {
        filteredLocations = new ArrayList<>();
        for (LocationModel locationModel : stateLocations) {
            if (locationModel.getLocationName().equals(name))
                filteredLocations.add(locationModel);
        }
        if (filteredLocations.size() == 0) {
            Toast.makeText(getContext(), "No Locations found", Toast.LENGTH_SHORT).show();
            adapter = new RecyclerViewAdapterHotspotLocation(stateLocations);
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

/*private void ShowSearchByStateDialog() {
        radioGroup.clearCheck();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_search_by_state, (ViewGroup) getView(), false);

        stateSpinner = viewInflated.findViewById(R.id.spinnerState);
        CustomAdapter statesAdapter = new CustomAdapter(getContext(), FireBaseHelper.getInstance().statelist);
        stateSpinner.setAdapter(statesAdapter);

        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                State state = FireBaseHelper.getInstance().statelist.get(position);
                populateSpinnerDistrict(viewInflated, state.getId());
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


    private void populateSpinnerDistrict(View view, String stateId) {
        HashSet<String> districts = new HashSet<>();
        districtSpinner = view.findViewById(R.id.spinnerDistrict);
        for (LocationModel locationModel : stateLocations) {
            if (locationModel.getStateID().equals(stateId))
                districts.add(locationModel.getDistrict());
        }
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(
                getContext(), android.R.layout.simple_spinner_item, new ArrayList<String>(districts));
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        districtSpinner.setAdapter(adapterSpinner);
    }




    private void FilterLocationsByState(Object selectedItem, Object selectedItem1) {
        filteredLocations = new ArrayList<>();
        for (LocationModel locationModel : stateLocations) {
            if (locationModel.getStateID().equals(((State) selectedItem).getId())) {
                if (locationModel.getDistrict().equals(selectedItem1))
                    filteredLocations.add(locationModel);
            }
        }
        if (filteredLocations.size() == 0) {
            Toast.makeText(getContext(), "No Locations found", Toast.LENGTH_SHORT).show();
            adapter = new RecyclerViewAdapterHotspotLocation(stateLocations);
        } else {
            adapter = new RecyclerViewAdapterHotspotLocation(filteredLocations);
        }
        recyclerView.setAdapter(adapter);
    }
*/