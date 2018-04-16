package com.ccajk.Fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.content.res.AppCompatResources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ccajk.R;
import com.ccajk.Tools.Helper;
import com.ccajk.Tools.LocationManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
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
    ImageButton choose, location;
    TextView textChoose, textLocation;
    Button upload;
    ImagePicker imagePicker;

    Double latitude, longitude;
    Location mLastLocation;
    LocationManager locationManager;
    FusedLocationProviderClient mFusedLocationClient;
    LocationCallback mLocationCallback;

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
        textChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });
        choose = view.findViewById(R.id.button_choose);
        choose.setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.ic_add_circle_black_24dp));
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });

        textLocation = view.findViewById(R.id.textview_location);

        location = view.findViewById(R.id.button_location);
        location.setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.ic_add_location_black_24dp));
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocationCoordinates();
            }
        });
        upload = view.findViewById(R.id.button_upload);
        upload.setCompoundDrawablesWithIntrinsicBounds(null, AppCompatResources.getDrawable(getContext(), R.drawable.ic_file_upload_black_24dp), null, null);

    }

    @SuppressLint("MissingPermission")
    private void getLocationCoordinates() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    showCoordinates(location);
                }
            }
        };
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000); // two minute interval
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.getActivity());

        locationManager = new LocationManager(this, mLocationCallback, mFusedLocationClient, mLocationRequest);
        locationManager.ManageLocation();
        if (mLastLocation == null)
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private void showCoordinates(Location location) {
        mLastLocation = location;
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Log.d(TAG, "getLocationCoordinates: " + latitude + "," + longitude);
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private void showImageChooser() {

        imagePicker = new ImagePicker();
        imagePicker.setTitle("Select Image");
        imagePicker.setCropImage(true);
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
                        .setGuidelines(CropImageView.Guidelines.OFF)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setRequestedSize(960, 540)
                        .setAspectRatio(16, 9);
            }

            @Override
            public void onPermissionDenied(int requestCode, String[] permissions,
                                           int[] grantResults) {
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: " + requestCode + " ," + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (imagePicker != null) {
            //Helper.getInstance().showSnackBar("Not null",this.getView());
            Log.d(TAG, "Not null");
            imagePicker.onActivityResult(this.getActivity(), requestCode, resultCode, data);
        }
        else
        {
            Helper.getInstance().showSnackBar("null",this.getView());
        }
        switch (requestCode) {

            case CONNECTION_FAILURE_RESOLUTION_REQUEST: {
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        Log.v(TAG, "Resolution success");
                        locationManager.requestLocationUpdates();
                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        // The user was asked to change settings, but chose not to
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
        Log.d(TAG, "onRequestPermissionsResult: "+"Inspection");
        imagePicker.onRequestPermissionsResult(this.getActivity(), requestCode, permissions, grantResults);
        switch (requestCode) {

            case LOCATION_REQUEST_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationManager.ManageLocation();

                } else {
                    locationManager.ShowDialogOnPermissionDenied("Location Permission denied !\nInspection will not work without location access.\n\nDo you want to grant location acces ?");
                }
                break;
            }

//            case CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE:
//                imagePicker.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
//                break;
        }

    }

}

