package com.mycca.fragments;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;
import com.mycca.R;
import com.mycca.activity.MainActivity;
import com.mycca.adapter.RecyclerViewAdapterSelectedImages;
import com.mycca.custom.CustomProgressButton.CircularProgressButton;
import com.mycca.custom.FabRevealMenu.FabListeners.OnFABMenuSelectedListener;
import com.mycca.custom.FabRevealMenu.FabModel.FABMenuItem;
import com.mycca.custom.FabRevealMenu.FabView.FABRevealMenu;
import com.mycca.custom.FancyAlertDialog.FancyAlertDialogType;
import com.mycca.custom.Progress.ProgressDialog;
import com.mycca.custom.customImagePicker.ImagePicker;
import com.mycca.custom.customImagePicker.cropper.CropImage;
import com.mycca.custom.customImagePicker.cropper.CropImageView;
import com.mycca.listeners.OnConnectionAvailableListener;
import com.mycca.models.InspectionModel;
import com.mycca.models.SelectedImageModel;
import com.mycca.models.StaffModel;
import com.mycca.tools.ConnectionUtility;
import com.mycca.tools.CustomLogger;
import com.mycca.tools.DataSubmissionAndMail;
import com.mycca.tools.FireBaseHelper;
import com.mycca.tools.Helper;
import com.mycca.tools.MyLocationManager;
import com.mycca.tools.Preferences;
import com.mycca.tools.VolleyHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.mycca.tools.MyLocationManager.CONNECTION_FAILURE_RESOLUTION_REQUEST;
import static com.mycca.tools.MyLocationManager.LOCATION_REQUEST_CODE;

public class InspectionFragment extends Fragment implements VolleyHelper.VolleyResponse, OnFABMenuSelectedListener {

    private static final String TAG = "Inspection";
    boolean isCurrentLocationFound = false, isUploadedToFirebase = false, isUploadedToServer = false;
    Double latitude, longitude;
    int counterFirebaseImages;
    int counterUpload = 0;
    int counterServerImages = 0;

    AppCompatTextView textViewSelectedFileCount;
    CircularProgressButton circularProgressButton;
    EditText editTextLocationName;
    Button upload;
    FloatingActionButton fab;
    ImagePicker imagePicker;
    ProgressDialog progressDialog;
    View.OnClickListener getCoordinatesListener;
    RecyclerView recyclerViewSelectedImages;
    RecyclerViewAdapterSelectedImages adapterSelectedImages;

    MainActivity mainActivity;
    Location mLastLocation;
    MyLocationManager myLocationManager;
    LocationCallback mLocationCallback;
    VolleyHelper volleyHelper;
    StaffModel staffModel;

    ArrayList<SelectedImageModel> selectedImageModelArrayList;
    ArrayList<Uri> firebaseImageURLs;
    Uri downloadUrl;
    private ArrayList<FABMenuItem> items;

    public InspectionFragment() {

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inspection, container, false);
        bindViews(view);
        init(view);

        //        if (Preferences.getInstance().getBooleanPref(getContext(), Preferences.PREF_HELP_INSPECTION)) {
//            showTutorial();
//            Preferences.getInstance().setBooleanPref(getContext(), Preferences.PREF_HELP_INSPECTION, false);
//        } else
        getLocationCoordinates();
        return view;
    }

    private void bindViews(View view) {

        fab = view.findViewById(R.id.fab_inspection);
        editTextLocationName = view.findViewById(R.id.edittext_current_location_name);
        circularProgressButton = view.findViewById(R.id.textview_current_location_coordinates);
        recyclerViewSelectedImages = view.findViewById(R.id.recycler_view_selected_images_inspection);
        upload = view.findViewById(R.id.button_upload);
        textViewSelectedFileCount = view.findViewById(R.id.textview_selected_image_count_inspection);
    }

    private void init(View view) {
        mainActivity = (MainActivity) getActivity();
        initItems();
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    showCoordinates(location);
                }
            }
        };

        myLocationManager = new MyLocationManager(this, mLocationCallback);
        volleyHelper = new VolleyHelper(this, getContext());
        selectedImageModelArrayList = new ArrayList<>();
        adapterSelectedImages = new RecyclerViewAdapterSelectedImages(selectedImageModelArrayList, this);
        recyclerViewSelectedImages.setAdapter(adapterSelectedImages);
        recyclerViewSelectedImages.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        progressDialog = Helper.getInstance().getProgressWindow(mainActivity, getString(R.string.please_wait));

        getCoordinatesListener = v -> getLocationCoordinates();

        circularProgressButton.setOnClickListener(getCoordinatesListener);
        circularProgressButton.setIndeterminateProgressMode(true);

        final FABRevealMenu fabMenu = view.findViewById(R.id.fabMenu_inspection);
        try {
            if (fab != null && fabMenu != null) {
                mainActivity.setFabRevealMenu(fabMenu);
                fabMenu.setMenuItems(items);
                fabMenu.bindAnchorView(fab);
                fabMenu.setOnFABMenuSelectedListener(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        upload.setOnClickListener(v -> doSubmission());

    }

    private void initItems() {
        items = new ArrayList<>();
        items.add(new FABMenuItem(0,getString(R.string.add_image), AppCompatResources.getDrawable(mainActivity, R.drawable.ic_attach_file_white_24dp)));
        items.add(new FABMenuItem(1,getString(R.string.remove_all), AppCompatResources.getDrawable(mainActivity, R.drawable.ic_close_24dp)));
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void getLocationCoordinates() {
        mLastLocation = null;
        circularProgressButton.setProgress(1);
        getLocation();
    }

    private void getLocation() {
        Task<LocationSettingsResponse> task = myLocationManager.ManageLocation();
        if (task != null) {
            task.addOnCompleteListener(task1 -> {
                if (task1.isSuccessful()) {
                    CustomLogger.getInstance().logDebug( "Task is Successful\nRequesting Location Update");
                    myLocationManager.requestLocationUpdates();

                } else {
                    CustomLogger.getInstance().logDebug( "Task UnSuccessful");
                    circularProgressButton.setProgress(0);
                }
            });
            task.addOnSuccessListener(mainActivity, locationSettingsResponse -> CustomLogger.getInstance().logDebug( "On Task Success"));

            task.addOnFailureListener(mainActivity, e -> {
                CustomLogger.getInstance().logDebug( "On Task Failed");
                circularProgressButton.setProgress(0);
                circularProgressButton.setIdleText(getString(R.string.location_not_found));
                if (e instanceof ResolvableApiException) {
                    myLocationManager.onLocationAcccessRequestFailure(e);
                }
            });
        }
    }

    private void showCoordinates(final Location location) {
        isCurrentLocationFound = true;
        mLastLocation = location;
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        myLocationManager.cleanUp();

        CustomLogger.getInstance().logDebug( "getLocationCoordinates: " + latitude + "," + longitude);
        circularProgressButton.setProgress(0);
        circularProgressButton.setIdleText(String.format(getString(R.string.current_location), String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude())));
    }

    private void showImageChooser() {
        imagePicker = Helper.getInstance().showImageChooser(imagePicker, mainActivity, true, new ImagePicker.Callback() {
            @Override
            public void onPickImage(Uri imageUri) {
                CustomLogger.getInstance().logDebug( "onPickImage: " + imageUri.getPath());
            }

            @Override
            public void onCropImage(Uri imageUri) {
                CustomLogger.getInstance().logDebug( "onCropImage: " + imageUri.getPath());
                int currentPosition = selectedImageModelArrayList.size();
                selectedImageModelArrayList.add(currentPosition, new SelectedImageModel(imageUri));
                adapterSelectedImages.notifyItemInserted(currentPosition);
                adapterSelectedImages.notifyDataSetChanged();
                setSelectedFileCount(currentPosition + 1);
                CustomLogger.getInstance().logDebug( "onCropImage: Item inserted at " + currentPosition);

            }

            @Override
            public void cropConfig(CropImage.ActivityBuilder builder) {
                builder
                        .setMultiTouchEnabled(false)
                        .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setRequestedSize(720, 1280);
            }

            @Override
            public void onPermissionDenied(int requestCode, String[] permissions,
                                           int[] grantResults) {
                CustomLogger.getInstance().logDebug( "onPermissionDenied: Permission not given to choose textViewMessage");
            }
        });

    }

    private void removeAllSelectedImages() {
        if (selectedImageModelArrayList == null || adapterSelectedImages == null) {
            return;
        }
        selectedImageModelArrayList.clear();
        adapterSelectedImages.notifyDataSetChanged();
        textViewSelectedFileCount.setText(getResources().getString(R.string.no_image));
    }

    private void doSubmission() {
        if (editTextLocationName.getText().toString().trim().isEmpty()) {
            editTextLocationName.setError(getString(R.string.location_req));
            return;
        } else if (!isCurrentLocationFound) {
            Toast.makeText(getContext(), getString(R.string.coordinates_req), Toast.LENGTH_LONG).show();
            return;
        } else if (selectedImageModelArrayList.size() == 0) {
            Toast.makeText(getContext(), getString(R.string.image_req), Toast.LENGTH_LONG).show();
            return;
        }

        ConnectionUtility connectionUtility = new ConnectionUtility(new OnConnectionAvailableListener() {
            @Override
            public void OnConnectionAvailable() {
                CustomLogger.getInstance().logDebug( "version checked =" + Helper.versionChecked);
                if (!Helper.versionChecked) {
                    FireBaseHelper.getInstance(getContext()).checkForUpdate(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (Helper.getInstance().onLatestVersion(dataSnapshot, mainActivity))
                                doSubmissionOnInternetAvailable();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Helper.getInstance().showMaintenanceDialog(mainActivity);
                        }
                    });
                } else
                    doSubmissionOnInternetAvailable();
            }

            @Override
            public void OnConnectionNotAvailable() {
                progressDialog.dismiss();
                Helper.getInstance().showErrorDialog(getString(R.string.no_internet), getString(R.string.inspection), mainActivity);
            }
        });
        connectionUtility.checkConnectionAvailability();
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
    }

    private void doSubmissionOnInternetAvailable() {
        CustomLogger.getInstance().logDebug( "doSubmissionOnInternetAvailable: \n Firebase = " + isUploadedToFirebase + "\n" +
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

    private void uploadInspectionDataToFirebase() {

        Date date = new Date();
        String locName = editTextLocationName.getText().toString().trim();
        final String key = locName.replaceAll("\\s", "-") + "-" +
                Helper.getInstance().formatDate(date, Helper.DateFormat.DD_MM_YYYY);

        staffModel = Preferences.getInstance().getStaffPref(getContext(), Preferences.PREF_STAFF_DATA);
        InspectionModel inspectionModel = new InspectionModel(staffModel.getId(), locName, latitude, longitude, new Date());

        Task<Void> task = FireBaseHelper.getInstance(getContext()).uploadDataToFirebase(
                inspectionModel,
                FireBaseHelper.ROOT_INSPECTION,
                staffModel.getState(),
                key);
        task.addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                uploadInspectionFiles(key);
            }
        });
        task.addOnFailureListener(e -> {
            progressDialog.dismiss();
            Helper.getInstance().showMaintenanceDialog(mainActivity);
        });
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
                    FireBaseHelper.ROOT_INSPECTION,
                    staffModel.getState(),
                    key);

            if (uploadTask != null) {
                uploadTask.addOnFailureListener(exception -> {
                    Helper.getInstance().showErrorDialog(getString(R.string.file_not_uploaded), getString(R.string.inspection), mainActivity);
                    CustomLogger.getInstance().logDebug( "onFailure: " + exception.getMessage());
                    progressDialog.dismiss();
                }).addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                    downloadUrl = uri;
                    firebaseImageURLs.add(downloadUrl);
                    progressDialog.setMessage(String.format(getString(R.string.uploaded_file),String.valueOf(++counterUpload),String.valueOf(selectedImageModelArrayList.size())));
                    if (counterUpload == selectedImageModelArrayList.size()) {
                        isUploadedToFirebase = true;
                        doSubmission();
                    }
                }));
            }
        }
    }

    private void uploadImagesToServer() {

        counterServerImages = 0;
        progressDialog.setMessage(getString(R.string.processing));
        int totalFilesToAttach = selectedImageModelArrayList.size();
        String url = Helper.getInstance().getAPIUrl() + "uploadImage.php/";

        if (totalFilesToAttach != 0) {
            try {
                DataSubmissionAndMail.getInstance().uploadImagesToServer(url,
                        firebaseImageURLs,
                        editTextLocationName.getText().toString(),
                        DataSubmissionAndMail.SUBMIT,
                        volleyHelper);
            } catch (Exception e) {
                e.printStackTrace();
                Helper.getInstance().showErrorDialog(getString(R.string.some_error), getString(R.string.inspection), mainActivity);
            }
        } else {
            isUploadedToServer = true;
            doSubmission();
        }
    }

    private void sendFinalMail() {
        progressDialog.setMessage(getString(R.string.almost_done));
        String url;

        url = Helper.getInstance().getAPIUrl() + "sendInspectionEmail.php/";

        Map<String, String> params = new HashMap<>();

        params.put("locationName", editTextLocationName.getText().toString());
        params.put("staffID", staffModel.getId());
        params.put("folder", DataSubmissionAndMail.SUBMIT);
        params.put("location", mLastLocation.getLatitude() + "," + mLastLocation.getLongitude());
        params.put("fileCount", selectedImageModelArrayList.size() + "");

        DataSubmissionAndMail.getInstance().sendMail(params, "send_inspection_mail-" + staffModel.getId(), volleyHelper, url);
    }

    public void setSelectedFileCount(int count) {
        textViewSelectedFileCount.setText(String.format(getString(R.string.files_selected), String.valueOf(count)));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        CustomLogger.getInstance().logDebug( "onActivityResult: " + requestCode + " ," + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (imagePicker != null)
            imagePicker.onActivityResult(mainActivity, requestCode, resultCode, data);

        switch (requestCode) {

            case CONNECTION_FAILURE_RESOLUTION_REQUEST: {
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        CustomLogger.getInstance().logDebug( "Resolution success");
                        myLocationManager.requestLocationUpdates();
                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        CustomLogger.getInstance().logDebug( "Resolution denied");
                        myLocationManager.ShowDialogOnLocationOff(getString(R.string.msg_no_location));
                        break;
                    }
                    default: {
                        CustomLogger.getInstance().logDebug( "User unable to do anything");
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
        CustomLogger.getInstance().logDebug( "onRequestPermissionsResult: " + "Inspection");

        switch (requestCode) {

            case LOCATION_REQUEST_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocationCoordinates();

                } else {
                    myLocationManager.ShowDialogOnPermissionDenied(getString(R.string.msg_no_location));
                    progressDialog.dismiss();
                }
                break;
            }
            default: {
                if (imagePicker != null)
                    imagePicker.onRequestPermissionsResult(mainActivity, requestCode, permissions, grantResults);
            }

        }

    }

    @Override
    public void onError(VolleyError volleyError) {
        volleyError.printStackTrace();
        progressDialog.dismiss();
        Helper.getInstance().showErrorDialog(getString(R.string.try_again), getString(R.string.some_error), mainActivity);
    }

    @Override
    public void onResponse(String str) {
        JSONObject jsonObject = Helper.getInstance().getJson(str);
        CustomLogger.getInstance().logDebug( jsonObject.toString());
        try {
            if (jsonObject.get("action").equals("Creating Image")) {
                counterServerImages++;
                if (jsonObject.get("result").equals(volleyHelper.SUCCESS)) {
                    if (counterServerImages == selectedImageModelArrayList.size()) {
                        CustomLogger.getInstance().logDebug( "onResponse: Files uploaded");
                        isUploadedToServer = true;
                        doSubmission();
                    }
                } else {
                    CustomLogger.getInstance().logDebug( "onResponse: Image = " + counterServerImages + " failed");
                    Helper.getInstance().showErrorDialog(getString(R.string.file_not_uploaded), getString(R.string.inspection), mainActivity);
                    progressDialog.dismiss();
                }
            } else if (jsonObject.getString("action").equals("Sending Mail")) {

                if (jsonObject.get("result").equals(volleyHelper.SUCCESS)) {

                    progressDialog.dismiss();
                    String alertMessage = String.format(getString(R.string.inspection_success),
                            editTextLocationName.getText());

                    Helper.getInstance().showFancyAlertDialog(mainActivity, alertMessage, getString(R.string.inspection),
                            getString(R.string.ok), () -> {
                            }, null, null, FancyAlertDialogType.SUCCESS);
                    isUploadedToServer = isUploadedToFirebase = false;

                } else {
                    progressDialog.dismiss();
                    Helper.getInstance().showErrorDialog(getString(R.string.inspection_success), getString(R.string.inspection), mainActivity);
                }
            }
        } catch (JSONException jse) {
            jse.printStackTrace();
            Helper.getInstance().showErrorDialog(getString(R.string.try_again), getString(R.string.some_error), mainActivity);
            progressDialog.dismiss();
        }
    }


    @Override
    public void onMenuItemSelected(View view, int id) {
        switch (items.get(id).getId()) {
            case 0:
                showImageChooser();
                break;
            case 1:
                removeAllSelectedImages();
                break;
        }
    }
}


//    private void showTutorial() {
//
//        final FancyShowCaseView fancyShowCaseView1 = new FancyShowCaseView.Builder(mainActivity)
//                .title("Click to refresh location")
//                .focusOn(circularProgressButton)
//                .focusShape(FocusShape.ROUNDED_RECTANGLE)
//                .build();
//
//        mainActivity.setmQueue(new FancyShowCaseQueue()
//                .add(fancyShowCaseView1));
//
//        mainActivity.getmQueue().setCompleteListener(() -> {
//            mainActivity.setmQueue(null);
//            getLocationCoordinates();
//        });
//
//        mainActivity.getmQueue().show();
//    }
