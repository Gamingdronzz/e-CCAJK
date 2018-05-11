package com.mycca.Fragments;


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
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.UploadTask;
import com.linchaolong.android.imagepicker.ImagePicker;
import com.linchaolong.android.imagepicker.cropper.CropImage;
import com.linchaolong.android.imagepicker.cropper.CropImageView;
import com.mycca.Adapter.RecyclerViewAdapterSelectedImages;
import com.mycca.CustomObjects.FancyAlertDialog.FancyAlertDialogType;
import com.mycca.CustomObjects.FancyAlertDialog.IFancyAlertDialogListener;
import com.mycca.CustomObjects.Progress.ProgressDialog;
import com.mycca.Listeners.OnConnectionAvailableListener;
import com.mycca.Models.InspectionModel;
import com.mycca.Models.SelectedImageModel;
import com.mycca.R;
import com.mycca.Tools.ConnectionUtility;
import com.mycca.Tools.DataSubmissionAndMail;
import com.mycca.Tools.FireBaseHelper;
import com.mycca.Tools.Helper;
import com.mycca.Tools.MyLocationManager;
import com.mycca.Tools.Preferences;
import com.mycca.Tools.VolleyHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.mycca.Tools.MyLocationManager.CONNECTION_FAILURE_RESOLUTION_REQUEST;
import static com.mycca.Tools.MyLocationManager.LOCATION_REQUEST_CODE;

/**
 * A simple {@link Fragment} subclass.
 */
public class InspectionFragment extends Fragment implements VolleyHelper.VolleyResponse {

    private static final String TAG = "Inspection";
    String staffId;
    int count;
    boolean isCurrentLocationFound = false;
    Double latitude, longitude;

    AppCompatTextView textViewAddImage, textLocationCoordinates, textViewSelectedFileCount;
    EditText editTextLocationName;
    Button upload;
    AppCompatTextView removeAll;
    ImagePicker imagePicker;
    ProgressDialog progressDialog;
    View.OnClickListener getCoordinatesListener;

    Location mLastLocation;
    MyLocationManager myLocationManager;
    LocationCallback mLocationCallback;

    RecyclerView recyclerViewSelectedImages;
    RecyclerViewAdapterSelectedImages adapterSelectedImages;
    ArrayList<SelectedImageModel> selectedImageModelArrayList;

    boolean isUploadedToFirebase = false, isUploadedToServer = false;
    ArrayList<Uri> firebaseImageURLs;
    int counterFirebaseImages;
    int counterUpload = 0;
    int counterServerImages = 0;
    VolleyHelper volleyHelper;

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
        volleyHelper = new VolleyHelper(this, getContext());
        getCoordinatesListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocationCoordinates();
            }
        };

        textViewAddImage = view.findViewById(R.id.textview_add_inspection_image);
        textViewAddImage.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_add_circle_black_24dp, 0, 0);
        removeAll = view.findViewById(R.id.imageButton_removeAllFiles);
        removeAll.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_remove_circle_black_24dp, 0, 0);
        removeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                removeAllSelectedImages();

            }
        });
        textViewAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });

        editTextLocationName = view.findViewById(R.id.edittext_current_location_name);
        textLocationCoordinates = view.findViewById(R.id.textview_current_location_coordinates);
        textLocationCoordinates.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_location_black_24dp, 0, R.drawable.ic_refresh_black_24dp, 0);
        textLocationCoordinates.setOnClickListener(getCoordinatesListener);
        textViewSelectedFileCount = view.findViewById(R.id.textview_selected_image_count_inspection);


        recyclerViewSelectedImages = view.findViewById(R.id.recycler_view_selected_images_inspection);


        upload = view.findViewById(R.id.button_upload);
        upload.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_file_upload_black_24dp, 0, 0, 0);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSubmission();
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
        adapterSelectedImages = new RecyclerViewAdapterSelectedImages(selectedImageModelArrayList, this);
        recyclerViewSelectedImages.setAdapter(adapterSelectedImages);
        //recyclerViewSelectedImages.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerViewSelectedImages.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void doSubmission() {
        ConnectionUtility connectionUtility = new ConnectionUtility(new OnConnectionAvailableListener() {
            @Override
            public void OnConnectionAvailable() {
                if (editTextLocationName.getText().toString().trim().isEmpty())
                    editTextLocationName.setError("Location Name Required");
                else if (!isCurrentLocationFound)
                    Toast.makeText(getContext(), "Please set current location coordinates first", Toast.LENGTH_LONG).show();
                else if (selectedImageModelArrayList.size() == 0)
                    Toast.makeText(getContext(), "No Images Added", Toast.LENGTH_LONG).show();
                else {
                    doSubmissionOnInternetAvailable();
                }

            }

            @Override
            public void OnConnectionNotAvailable() {
                showNoInternetConnectionDialog("No Internet Connection\nPlease turn on internet connection before submitting Inspection");
            }
        });
        connectionUtility.checkConnectionAvailability();
    }

    private void showNoInternetConnectionDialog(String message) {
        Helper.getInstance().showFancyAlertDialog(this.getActivity(),
                message,
                "Inspection",
                "OK",
                null,
                null,
                null,
                FancyAlertDialogType.ERROR);
    }

    private void doSubmissionOnInternetAvailable() {
        Log.d(TAG, "doSubmissionOnInternetAvailable: \n Firebase = " + isUploadedToFirebase + "\n" +
                "Server = " + isUploadedToServer);
        if (isUploadedToFirebase) {
            if (isUploadedToServer) {
                sendFinalMail();
            } else {
                uploadImagesToServer();
            }
        } else {
            uploadInspectionDataToFirebase();
        }
    }

    private void removeAllSelectedImages() {
        if (selectedImageModelArrayList == null || adapterSelectedImages == null) {
            return;
        }
        selectedImageModelArrayList.clear();
        adapterSelectedImages.notifyDataSetChanged();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void getLocationCoordinates() {
        progressDialog.show();

        ConnectionUtility connectionUtility = new ConnectionUtility(new OnConnectionAvailableListener() {
            @Override
            public void OnConnectionAvailable() {


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
                                progressDialog.dismiss();
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
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            }

            @Override
            public void OnConnectionNotAvailable() {
                showNoInternetConnectionDialog("No Internet Connection\nPlease turn on internet connection before getting location coordinates");
                progressDialog.dismiss();
            }
        });
        connectionUtility.checkConnectionAvailability();
    }


    private void showCoordinates(Location location) {
        isCurrentLocationFound = true;
        progressDialog.dismiss();
        mLastLocation = location;
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        myLocationManager.cleanUp();

        Log.d(TAG, "getLocationCoordinates: " + latitude + "," + longitude);
        textLocationCoordinates.setText("Current Location\n" + location.getLatitude() + " , " + location.getLongitude());
        progressDialog.dismiss();

    }

    private void showImageChooser() {
        imagePicker = Helper.getInstance().showImageChooser(imagePicker, getActivity(), true, new ImagePicker.Callback() {
            @Override
            public void onPickImage(Uri imageUri) {
                Log.d(TAG, "onPickImage: " + imageUri.getPath());
            }

            @Override
            public void onCropImage(Uri imageUri) {
                Log.d(TAG, "onCropImage: " + imageUri.getPath());
                int currentPosition = selectedImageModelArrayList.size();
                selectedImageModelArrayList.add(currentPosition, new SelectedImageModel(imageUri));
                adapterSelectedImages.notifyItemInserted(currentPosition);
                adapterSelectedImages.notifyDataSetChanged();
                setSelectedFileCount(currentPosition + 1);
                Log.d(TAG, "onCropImage: Item inserted at " + currentPosition);

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
                Log.d(TAG, "onPermissionDenied: Permission not given to choose textViewMessage");
            }
        });

    }

    private void uploadInspectionDataToFirebase() {

        Date date = new Date();
        String locName = editTextLocationName.getText().toString().trim();
        final String key = locName.replaceAll("\\s", "-") + "-" +
                Helper.getInstance().formatDate(date, "dd-MM-yy");

        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        staffId = Preferences.getInstance().getStringPref(getContext(), Preferences.PREF_STAFF_ID);
        InspectionModel inspectionModel = new InspectionModel(staffId, locName, latitude, longitude, new Date());

        Task task = FireBaseHelper.getInstance(getContext()).uploadDataToFirebase(
                FireBaseHelper.getInstance(getContext()).ROOT_INSPECTION,
                inspectionModel,
                getContext(),
                key);
        task.addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    uploadInspectionFiles(key);
                }
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showNoInternetConnectionDialog("Some Error Occured\nError 3\nPlease Try Again");

            }
        });
    }

    private void sendFinalMail() {
        progressDialog.setMessage("Almost Done..");
        progressDialog.show();
        String url;

        url = Helper.getInstance().getAPIUrl(Preferences.getInstance().getBooleanPref(getContext(), Preferences.PREF_DEBUG_MODE))
                + "sendInspectionEmail.php";

        Map<String, String> params = new HashMap();

        params.put("locationName", editTextLocationName.getText().toString());
        params.put("staffID", staffId);
        params.put("location", mLastLocation.getLatitude() + "," + mLastLocation.getLongitude());
        params.put("fileCount", selectedImageModelArrayList.size() + "");

        DataSubmissionAndMail.getInstance().sendMail(params, "send_inspection_mail-" + staffId, volleyHelper, url);
    }

    private void uploadInspectionFiles(String key) {
        firebaseImageURLs = new ArrayList<>();
        UploadTask uploadTask;
        counterFirebaseImages = 0;
        counterUpload = 0;
        for (SelectedImageModel imageModel : selectedImageModelArrayList) {
            uploadTask = FireBaseHelper.getInstance(getContext()).uploadFiles(
                    imageModel,
                    true,
                    counterFirebaseImages++,
                    FireBaseHelper.getInstance(getContext()).ROOT_INSPECTION,
                    Preferences.getInstance().getStringPref(getContext(), Preferences.PREF_STATE),
                    key);

            if (uploadTask != null) {
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getContext(), "Unable to Upload file", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onFailure: " + exception.getMessage());
                        progressDialog.dismiss();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        firebaseImageURLs.add(downloadUrl);
                        Log.d(TAG, "onSuccess: " + downloadUrl);
                        progressDialog.setMessage("Uploaded file " + (++counterUpload) + "/" + selectedImageModelArrayList.size());
                        if (counterUpload == selectedImageModelArrayList.size()) {
                            isUploadedToFirebase = true;
                            doSubmission();
                        }
                    }
                });
            }
        }
    }

    private void uploadImagesToServer() {

        counterServerImages = 0;
        progressDialog.setMessage("Processing..");
        progressDialog.show();
        int totalFilesToAttach = selectedImageModelArrayList.size();
        String url = Helper.getInstance().getAPIUrl(Preferences.getInstance().getBooleanPref(getContext(), Preferences.PREF_DEBUG_MODE))
                + "uploadImage.php";

        if (totalFilesToAttach != 0) {
            try {
                DataSubmissionAndMail.getInstance().uploadImagesToServer(url,
                        firebaseImageURLs,
                        editTextLocationName.getText().toString(),
                        volleyHelper);
            } catch (Exception e) {
                e.printStackTrace();
                showNoInternetConnectionDialog("Error 1\nPlease report this issue through feedback section");
            }
        } else {
            isUploadedToServer = true;
            doSubmission();
        }
    }

    public void setSelectedFileCount(int count) {
        textViewSelectedFileCount.setText("Selected Files = " + count);
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
                        progressDialog.show();
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
                        progressDialog.dismiss();
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
                    progressDialog.dismiss();
                }
                break;
            }
            default: {
                if (imagePicker != null)
                    imagePicker.onRequestPermissionsResult(this.getActivity(), requestCode, permissions, grantResults);
            }

        }

    }


    @Override
    public void onError(VolleyError volleyError) {
        volleyError.printStackTrace();
        progressDialog.dismiss();
        Toast.makeText(getContext(), "Some Error Occured\nPlease be patient we are getting things fixed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(String str) {
        JSONObject jsonObject = Helper.getInstance().getJson(str);
        Log.d(TAG, jsonObject.toString());
        try {
            if (jsonObject.get("action").equals("Creating Image")) {
                counterServerImages++;
                if (jsonObject.get("result").equals(Helper.getInstance().SUCCESS)) {
                    if (counterServerImages == selectedImageModelArrayList.size()) {
                        Log.d(TAG, "onResponse: Files uploaded");
                        isUploadedToServer = true;
                        doSubmission();
                    }
                } else {
                    Log.d(TAG, "onResponse: Image = " + counterServerImages + " failed");
                    progressDialog.dismiss();
                }
            } else if (jsonObject.getString("action").equals("Sending Mail")) {
                if (jsonObject.get("result").equals(Helper.getInstance().SUCCESS)) {
                    progressDialog.dismiss();
                    StringBuilder alertMessage = new StringBuilder();

                    alertMessage.append("Inspection data for");
                    alertMessage.append("<br><b>" + editTextLocationName.getText() + "</b><br>");
                    alertMessage.append("containing <b>" + selectedImageModelArrayList.size() + "</b> images ");
                    alertMessage.append(" has been succesfully submitted");



                    Helper.getInstance().showFancyAlertDialog(getActivity(), alertMessage.toString(), "Inspection", "OK", new IFancyAlertDialogListener() {
                        @Override
                        public void OnClick() {
                        }
                    }, null, null, FancyAlertDialogType.SUCCESS);
                    //Toast.makeText(getContext(), "Inspection Data Submitted Succesfully", Toast.LENGTH_SHORT).show();
                    isUploadedToServer = isUploadedToFirebase = false;
                } else {
                    progressDialog.dismiss();
                    Helper.getInstance().showFancyAlertDialog(getActivity(), "Inspection Submission Failed\nTry Again",
                            " Inspection", "OK", new IFancyAlertDialogListener() {
                        @Override
                        public void OnClick() {
                        }
                    }, null, null, FancyAlertDialogType.ERROR);
                    //Toast.makeText(getContext(), "Inspection Submission Failed\nTry Again", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (JSONException jse) {
            jse.printStackTrace();
        }
    }
}

