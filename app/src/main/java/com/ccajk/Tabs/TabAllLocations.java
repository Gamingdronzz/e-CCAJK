package com.ccajk.Tabs;


import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.ccajk.Adapter.RecyclerViewAdapterHotspotLocation;
import com.ccajk.Models.LocationModel;
import com.ccajk.R;

import java.util.ArrayList;


public class TabAllLocations extends Fragment {

    String TAG = "All Locations";
    RecyclerView recyclerView;
    RecyclerViewAdapterHotspotLocation adapter;
    ArrayList<LocationModel> allLocations = new ArrayList<>();
    ArrayList<LocationModel> filteredLocations = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_all_locations, container, false);
        init(view);
        return view;
    }

    private void init(View view) {

        recyclerView = view.findViewById(R.id.recyclerview_locations);
        Button search = view.findViewById(R.id.btn_search_loc);

    }


    private void ShowSearchByNameDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_search_by_name, (ViewGroup) getView(), false);

        final AutoCompleteTextView input = viewInflated.findViewById(R.id.input);
        ArrayAdapter<String> actAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_dropdown_item_1line,(String[]) allLocations.toArray());
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



    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this.getContext(), "LANDSCAPE", Toast.LENGTH_SHORT).show();
            setupGridLayout(true);

        } else if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this.getContext(), "PORTRAIT", Toast.LENGTH_SHORT).show();
        }
    }


    private void setupGridLayout(boolean multiColumn) {
        if (multiColumn) {
            GridLayoutManager manager = new GridLayoutManager(this.getContext(), 2);
            recyclerView.setLayoutManager(manager);
        } else {
            GridLayoutManager manager = new GridLayoutManager(this.getContext(), 1);
            recyclerView.setLayoutManager(manager);
        }
    }


}


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
        });*/ /*private void ShowSearchByStateDialog() {
        radioGroup.clearCheck();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_search_by_state, (ViewGroup) getView(), false);

        stateSpinner = viewInflated.findViewById(R.id.spinnerState);
        GrievancAdapter statesAdapter = new GrievancAdapter(getContext(), FireBaseHelper.getInstance().statelist);
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