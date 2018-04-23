package com.ccajk.Fragments;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ccajk.Adapter.RecyclerViewAdapterSelectedImages;
import com.ccajk.CustomObjects.ProgressDialog;
import com.ccajk.Models.SelectedImageModel;
import com.ccajk.R;
import com.ccajk.Tools.Helper;
import com.ccajk.Tools.MyLocationManager;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.linchaolong.android.imagepicker.ImagePicker;
import com.linchaolong.android.imagepicker.cropper.CropImage;
import com.linchaolong.android.imagepicker.cropper.CropImageView;

import java.util.ArrayList;

import static com.ccajk.Tools.MyLocationManager.CONNECTION_FAILURE_RESOLUTION_REQUEST;
import static com.ccajk.Tools.MyLocationManager.LOCATION_REQUEST_CODE;

/**
 * A simple {@link Fragment} subclass.
 */
public class InspectionFragment extends Fragment {

    private static final String TAG = "Inspection";
    //    ImageButton choose, location;
    TextView textChoose, textLocation,textViewSelectedFileCount;
    Button upload;
    ImagePicker imagePicker;

    Double latitude, longitude;
    Location mLastLocation;
    MyLocationManager myLocationManager;
    LocationCallback mLocationCallback;
    ProgressDialog progressDialog;
    boolean isCurrentLocationFound = false;
    View.OnClickListener getCoordinatesListener;

    RecyclerView recyclerViewSelectedImages;
    RecyclerViewAdapterSelectedImages adapterSelectedImages;
    ArrayList<SelectedImageModel> selectedImageModelArrayList;

    public InspectionFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inspection, container, false);
        init(view);
        getLocationCoordinates();
        return view;
    }

    private void init(View view) {
        getCoordinatesListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocationCoordinates();
            }
        };
        textChoose = view.findViewById(R.id.textview_add_inspection_image);
        textChoose.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_add_circle_black_24dp, 0, 0);
        textChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });

        textLocation = view.findViewById(R.id.textview_current_location);
        textLocation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_location_black_24dp, 0, R.drawable.ic_refresh_black_24dp, 0);
        textLocation.setOnClickListener(getCoordinatesListener);
        textViewSelectedFileCount = view.findViewById(R.id.textview_selected_image_count_inspection);


        recyclerViewSelectedImages = view.findViewById(R.id.recycler_view_selected_images_inspection);


        upload = view.findViewById(R.id.button_upload);
        upload.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_file_upload_black_24dp, 0, 0);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCurrentLocationFound) {
                    uploadInspectionDataToCloud();
                } else {
                    Log.d(TAG, "onClick: Please set current location coordinates first");
                }
            }
        });


        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    showCoordinates(location);
                }
            }
        };
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(2000); // two minute interval
//        mLocationRequest.setFastestInterval(2000);

//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.getActivity());
        //myLocationManager = new MyLocationManager(this, mLocationCallback, mFusedLocationClient, mLocationRequest);
        myLocationManager = new MyLocationManager(this, mLocationCallback);
        progressDialog = Helper.getInstance().getProgressWindow(getActivity(),
                "Getting Current Location Coordinates\nPlease Wait...");

        selectedImageModelArrayList = new ArrayList<>();
        adapterSelectedImages = new RecyclerViewAdapterSelectedImages(selectedImageModelArrayList,this);
        recyclerViewSelectedImages.setAdapter(adapterSelectedImages);
        recyclerViewSelectedImages.setLayoutManager(new GridLayoutManager(getContext(),3));
    }

    private void uploadInspectionDataToCloud() {

    }
//    private void manageProgressDialog()
//    {
//        if(progressDialog.isShowing())
//        {
//            progressDialog.dismiss();
//        }
//        else
//        {
//            progressDialog.show();
//        }
//    }

    @TargetApi(Build.VERSION_CODES.M)
    private void getLocationCoordinates() {
        showProgressDialog();
//        if (myLocationManager.checkForLocationPermission()) {
        Task<LocationSettingsResponse> task = myLocationManager.ManageLocation();
        if (task != null) {
            task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                @Override
                public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                    Log.v(TAG, "On Task Complete");
                    if (task.isSuccessful()) {
                        Log.v(TAG, "Task is Successful");

                        myLocationManager.requestLocationUpdates();

                    } else {
                        Log.v(TAG, "Task is not Successful");
                        dismissProgressDialog();
                    }
                }
            });
            task.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    Log.v(TAG, "On Task Success");
                }
            });

            task.addOnFailureListener(getActivity(), new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.v(TAG, "On Task Failed");
                    if (e instanceof ResolvableApiException) {
                        myLocationManager.onLocationAcccessRequestFailure(e);
                        dismissProgressDialog();
                    }
                }
            });

        }
//        } else {
//            myLocationManager.requestLocationPermission(this, LOCATION_REQUEST_CODE);
//        }
//        if (mLastLocation == null)
//            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private void showCoordinates(Location location) {
        isCurrentLocationFound = true;
        dismissProgressDialog();
        mLastLocation = location;
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        myLocationManager.cleanUp();

        Log.d(TAG, "getLocationCoordinates: " + latitude + "," + longitude);

        textLocation.setText(location.getLatitude() + " , " + location.getLongitude());
        progressDialog.dismiss();
    }

    private void showImageChooser() {
        imagePicker = Helper.getInstance().showImageChooser(imagePicker, getActivity(),true, new ImagePicker.Callback() {
            @Override
            public void onPickImage(Uri imageUri) {
                Log.d(TAG, "onPickImage: " + imageUri.getPath());
            }

            @Override
            public void onCropImage(Uri imageUri) {
                Log.d(TAG, "onCropImage: " + imageUri.getPath());
                int currentPosition = selectedImageModelArrayList.size();
                selectedImageModelArrayList.add(currentPosition,new SelectedImageModel(imageUri));
                adapterSelectedImages.notifyItemInserted(currentPosition);
                adapterSelectedImages.notifyDataSetChanged();
                setSelectedFileCount(currentPosition+1);
                Log.d(TAG, "onCropImage: Item inserted at " + currentPosition );

            }

            @Override
            public void cropConfig(CropImage.ActivityBuilder builder) {
                builder
                        .setMultiTouchEnabled(false)
                        .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setRequestedSize(720, 1280)
                        .setAspectRatio(9, 16);
            }

            @Override
            public void onPermissionDenied(int requestCode, String[] permissions,
                                           int[] grantResults) {
                Log.d(TAG, "onPermissionDenied: Permission not given to choose message");
            }
        });

    }

    private void dismissProgressDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private void showProgressDialog() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: " + requestCode + " ," + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (imagePicker != null)
            imagePicker.onActivityResult(this.getActivity(), requestCode, resultCode, data);

        switch (requestCode) {

            case CONNECTION_FAILURE_RESOLUTION_REQUEST: {
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        Log.v(TAG, "Resolution success");
                        myLocationManager.requestLocationUpdates();
                        showProgressDialog();
                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        Log.v(TAG, "Resolution denied");
                        myLocationManager.ShowDialogOnLocationOff("Location not turned on! Inspection will not show nearby locations without location access\nTurn Location on and Refresh");
                        progressDialog.dismiss();
                        break;
                    }
                    default: {
                        Log.v(TAG, "User unable to do anything");
                        dismissProgressDialog();
                        break;
                    }
                }
                break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: " + "Inspection");

        switch (requestCode) {

            case LOCATION_REQUEST_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocationCoordinates();

                } else {
                    myLocationManager.ShowDialogOnPermissionDenied("Location Permission denied !\nInspection will not work without location access.");
                    dismissProgressDialog();
                }
                break;
            }
            default: {
                if (imagePicker != null)
                    imagePicker.onRequestPermissionsResult(this.getActivity(), requestCode, permissions, grantResults);
            }

        }

    }

    public void setSelectedFileCount(int count)
    {
        textViewSelectedFileCount.setText("Selected Files = " + count);
    }

}

