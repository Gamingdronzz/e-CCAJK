package com.ccajk.Fragments;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ccajk.R;
import com.ccajk.Tools.Helper;
import com.ccajk.Tools.LocationManager;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.linchaolong.android.imagepicker.ImagePicker;
import com.linchaolong.android.imagepicker.cropper.CropImage;
import com.linchaolong.android.imagepicker.cropper.CropImageView;

import static com.ccajk.Tools.LocationManager.CONNECTION_FAILURE_RESOLUTION_REQUEST;
import static com.ccajk.Tools.LocationManager.LOCATION_REQUEST_CODE;

/**
 * A simple {@link Fragment} subclass.
 */
public class InspectionFragment extends Fragment {

    private static final String TAG = "Inspection";
    //    ImageButton choose, location;
    TextView textChoose, textLocation;
    Button upload;
    ImagePicker imagePicker;

    Double latitude, longitude;
    Location mLastLocation;
    LocationManager locationManager;
    FusedLocationProviderClient mFusedLocationClient;
    LocationCallback mLocationCallback;
    LocationRequest mLocationRequest;
    PopupWindow progressDialog;
    boolean isCurrentLocationFound = false;

    public InspectionFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inspection, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        textChoose = view.findViewById(R.id.textview_choose);
        textChoose.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_add_circle_black_24dp,0,0);
        textChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });

        textLocation = view.findViewById(R.id.textview_location);
        textLocation.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_add_location_black_24dp,0,R.drawable.ic_refresh_black_24dp,0);
        textLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocationCoordinates();
            }
        });

        upload = view.findViewById(R.id.button_upload);
        upload.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_file_upload_black_24dp, 0, 0);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isCurrentLocationFound)
                {
                    uploadInspectionDataToCloud();
                }
                else
                {
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
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000); // two minute interval
        mLocationRequest.setFastestInterval(2000);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.getActivity());
        locationManager = new LocationManager(this, mLocationCallback, mFusedLocationClient, mLocationRequest);
        progressDialog = Helper.getInstance().getProgressWindow(getActivity(),
                "Getting Current Location Coordinates\nPlease Wait...");
    }

    private void uploadInspectionDataToCloud()
    {

    }
    private void manageProgressDialog()
    {
        if(progressDialog.isShowing())
        {
            progressDialog.dismiss();
        }
        else
        {
            progressDialog.showAtLocation(getView(), Gravity.START,0,0);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void getLocationCoordinates() {
        manageProgressDialog();
        if (locationManager.checkForLocationPermission()) {
            Task<LocationSettingsResponse> task = locationManager.ManageLocation();
            if (task != null) {
                task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                        Log.v(TAG, "On Task Complete");
                        if (task.isSuccessful()) {
                            Log.v(TAG, "Task is Successful");
                            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                            locationManager.requestLocationUpdates();

                        } else {
                            Log.v(TAG, "Task is not Successful");
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
                            locationManager.onLocationAcccessRequestFailure(e);
                        }
                    }
                });

            }
        } else {
            locationManager.requestLocationPermission(this, LOCATION_REQUEST_CODE);
        }
//        if (mLastLocation == null)
//            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private void showCoordinates(Location location) {
        isCurrentLocationFound = true;
        manageProgressDialog();
        mLastLocation = location;
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        Log.d(TAG, "getLocationCoordinates: " + latitude + "," + longitude);
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        textLocation.setText(location.getLatitude() + " , " + location.getLongitude());
        progressDialog.dismiss();
    }

    private void showImageChooser() {

        imagePicker = new ImagePicker();
        imagePicker.setTitle("Select Image");
        imagePicker.setCropImage(false);
        imagePicker.startChooser(this.getActivity(), new ImagePicker.Callback() {
            @Override
            public void onPickImage(Uri imageUri) {
                Log.d(TAG, "onPickImage: " + imageUri.getPath());
            }

            @Override
            public void onCropImage(Uri imageUri) {
                Log.d(TAG, "onCropImage: " + imageUri.getPath());

            }

            @Override
            public void cropConfig(CropImage.ActivityBuilder builder) {
                builder
                        .setMultiTouchEnabled(false)
                        .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setRequestedSize(540, 960)
                        .setAspectRatio(9, 16);
            }

            @Override
            public void onPermissionDenied(int requestCode, String[] permissions,
                                           int[] grantResults) {
                Log.d(TAG, "onPermissionDenied: Permission not given to choose message");
            }
        });

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
                        locationManager.requestLocationUpdates();
                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        Log.v(TAG, "Resolution denied");
                        locationManager.ShowDialogOnLocationOff("Location not turned on! Inspection will not show nearby locations without location access. Do you want to turn location on ?");
                        break;
                    }
                    default: {
                        Log.v(TAG, "User unable to do anything");
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
                    locationManager.ShowDialogOnPermissionDenied("Location Permission denied !\nInspection will not work without location access.\n\nDo you want to grant location acces ?");
                }
                break;
            }
            default: {
                if (imagePicker != null)
                    imagePicker.onRequestPermissionsResult(this.getActivity(), requestCode, permissions, grantResults);
            }

        }

    }

}

