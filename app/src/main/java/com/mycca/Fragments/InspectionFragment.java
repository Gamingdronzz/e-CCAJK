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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.mycca.Activity.MainActivity;
import com.mycca.Adapter.RecyclerViewAdapterSelectedImages;
import com.mycca.CustomObjects.CustomImagePicker.Cropper.CropImage;
import com.mycca.CustomObjects.CustomImagePicker.Cropper.CropImageView;
import com.mycca.CustomObjects.CustomImagePicker.ImagePicker;
import com.mycca.CustomObjects.CustomProgressButton.CircularProgressButton;
import com.mycca.CustomObjects.FancyAlertDialog.FancyAlertDialogType;
import com.mycca.CustomObjects.FancyShowCase.FancyShowCaseQueue;
import com.mycca.CustomObjects.FancyShowCase.FancyShowCaseView;
import com.mycca.CustomObjects.FancyShowCase.FocusShape;
import com.mycca.CustomObjects.Progress.ProgressDialog;
import com.mycca.CustomObjects.fabrevealmenu.listeners.OnFABMenuSelectedListener;
import com.mycca.CustomObjects.fabrevealmenu.model.FABMenuItem;
import com.mycca.CustomObjects.fabrevealmenu.view.FABRevealMenu;
import com.mycca.Listeners.OnConnectionAvailableListener;
import com.mycca.Models.InspectionModel;
import com.mycca.Models.SelectedImageModel;
import com.mycca.Models.StaffModel;
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
    ImageView imgLoc;
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

        if (Preferences.getInstance().getBooleanPref(getContext(), Preferences.PREF_HELP_INSPECTION)) {
            showTutorial();
            Preferences.getInstance().setBooleanPref(getContext(), Preferences.PREF_HELP_INSPECTION, false);
        } else {
            getLocationCoordinates();
        }
        return view;
    }

    private void bindViews(View view) {

        fab = view.findViewById(R.id.fab_inspection);
        //imgLoc = view.findViewById(R.id.img_inspection_location);
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
        progressDialog = Helper.getInstance().getProgressWindow(mainActivity,
                "Getting Current Location Coordinates\nPlease Wait...");

        getCoordinatesListener = v -> getLocationCoordinates();

        circularProgressButton.setOnClickListener(getCoordinatesListener);
        circularProgressButton.setIndeterminateProgressMode(true);

        final FABRevealMenu fabMenu = view.findViewById(R.id.fabMenu);
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
        items.add(new FABMenuItem("Add Image", AppCompatResources.getDrawable(mainActivity, R.drawable.ic_attach_file_white_24dp)));
        items.add(new FABMenuItem("Remove All", AppCompatResources.getDrawable(mainActivity, R.drawable.ic_close_24dp)));
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void getLocationCoordinates() {
        mLastLocation = null;
        circularProgressButton.setProgress(1);
        ConnectionUtility connectionUtility = new ConnectionUtility(new OnConnectionAvailableListener() {
            @Override
            public void OnConnectionAvailable() {


                Task<LocationSettingsResponse> task = myLocationManager.ManageLocation();
                if (task != null) {
                    task.addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Log.v(TAG, "Task is Successful\nRequesting Location Update");
                            myLocationManager.requestLocationUpdates();

                        } else {
                            Log.v(TAG, "Task UnSuccessful");
                            circularProgressButton.setProgress(0);
                        }
                    });
                    task.addOnSuccessListener(mainActivity, locationSettingsResponse -> Log.v(TAG, "On Task Success"));

                    task.addOnFailureListener(mainActivity, e -> {
                        Log.v(TAG, "On Task Failed");
                        circularProgressButton.setProgress(0);
                        circularProgressButton.setIdleText(Html.fromHtml("Current Location Not Found!<br>Touch to Refresh").toString());
                        if (e instanceof ResolvableApiException) {
                            myLocationManager.onLocationAcccessRequestFailure(e);
                        }
                    });
                }
            }

            @Override
            public void OnConnectionNotAvailable() {
                circularProgressButton.setProgress(0);
                Helper.getInstance().showErrorDialog("No Internet Connection\nPlease turn on internet connection before getting location coordinates", "Inspection", mainActivity);
            }
        });
        connectionUtility.checkConnectionAvailability();
    }

    private void showCoordinates(final Location location) {
        isCurrentLocationFound = true;
        mLastLocation = location;
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        myLocationManager.cleanUp();

        Log.d(TAG, "getLocationCoordinates: " + latitude + "," + longitude);
        circularProgressButton.setProgress(0);
        circularProgressButton.setIdleText(Html.fromHtml("Current Location<br><b>" + location.getLatitude() + " , " + location.getLongitude() + "</b>").toString());
        //textLocationCoordinates.setText(Html.fromHtml("Current Location\n<b>" + location.getLatitude() + " , " + location.getLongitude() + "</b>"));
    }

    private void showImageChooser() {
        imagePicker = Helper.getInstance().showImageChooser(imagePicker, mainActivity, true, new ImagePicker.Callback() {
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

    private void removeAllSelectedImages() {
        if (selectedImageModelArrayList == null || adapterSelectedImages == null) {
            return;
        }
        selectedImageModelArrayList.clear();
        adapterSelectedImages.notifyDataSetChanged();
        textViewSelectedFileCount.setText(getResources().getString(R.string.no_image));
    }

    private void doSubmission() {
        ConnectionUtility connectionUtility = new ConnectionUtility(new OnConnectionAvailableListener() {
            @Override
            public void OnConnectionAvailable() {
                Log.d(TAG, "version checked =" + Helper.versionChecked);
                if (editTextLocationName.getText().toString().trim().isEmpty())
                    editTextLocationName.setError("Location Name Required");
                else if (!isCurrentLocationFound)
                    Toast.makeText(getContext(), "Please set current location coordinates first", Toast.LENGTH_LONG).show();
                else if (selectedImageModelArrayList.size() == 0)
                    Toast.makeText(getContext(), "No Images Added", Toast.LENGTH_LONG).show();
                else if (!Helper.versionChecked) {
                    FireBaseHelper.getInstance(getContext()).checkForUpdate(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (Helper.getInstance().onLatestVersion(dataSnapshot, mainActivity))
                                doSubmissionOnInternetAvailable();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Helper.getInstance().showUpdateOrMaintenanceDialog(false, mainActivity);
                        }
                    });
                } else
                    doSubmissionOnInternetAvailable();
            }

            @Override
            public void OnConnectionNotAvailable() {
                Helper.getInstance().showErrorDialog("No Internet Connection\nPlease turn on internet connection before submitting Inspection", "Inspection", mainActivity);
            }
        });
        connectionUtility.checkConnectionAvailability();
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

    private void uploadInspectionDataToFirebase() {

        Date date = new Date();
        String locName = editTextLocationName.getText().toString().trim();
        final String key = locName.replaceAll("\\s", "-") + "-" +
                Helper.getInstance().formatDate(date, "dd-MM-yy");

        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        staffModel = Preferences.getInstance().getStaffPref(getContext(), Preferences.PREF_STAFF_DATA);
        InspectionModel inspectionModel = new InspectionModel(staffModel.getId(), locName, latitude, longitude, new Date());

        Task<Void> task = FireBaseHelper.getInstance(getContext()).uploadDataToFirebase(
                FireBaseHelper.ROOT_INSPECTION,
                inspectionModel,
                staffModel.getState(),
                key);
        task.addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                uploadInspectionFiles(key);
            }
        });
        task.addOnFailureListener(e -> {
            progressDialog.dismiss();
            Helper.getInstance().showUpdateOrMaintenanceDialog(false, mainActivity);
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
                    Helper.getInstance().showErrorDialog("Files could not be uploaded\nTry Again", "Inspection", mainActivity);
                    Log.d(TAG, "onFailure: " + exception.getMessage());
                    progressDialog.dismiss();
                }).addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                    downloadUrl = uri;
                    Log.d(TAG, "onSuccess: " + downloadUrl);
                    firebaseImageURLs.add(downloadUrl);
                    progressDialog.setMessage("Uploaded file " + (++counterUpload) + " / " + selectedImageModelArrayList.size());
                    Log.d(TAG, "onSuccess: counter = " + counterUpload + "size = " + selectedImageModelArrayList.size());
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
        progressDialog.setMessage("Processing..");
        progressDialog.show();
        int totalFilesToAttach = selectedImageModelArrayList.size();
        String url = Helper.getInstance().getAPIUrl() + "uploadImage.php";

        if (totalFilesToAttach != 0) {
            try {
                DataSubmissionAndMail.getInstance().uploadImagesToServer(url,
                        firebaseImageURLs,
                        editTextLocationName.getText().toString(),
                        volleyHelper);
            } catch (Exception e) {
                e.printStackTrace();
                Helper.getInstance().showErrorDialog("Some Error Occurred.\nPlease try Again", "Inspection", mainActivity);
            }
        } else {
            isUploadedToServer = true;
            doSubmission();
        }
    }

    private void sendFinalMail() {
        progressDialog.setMessage("Almost Done..");
        progressDialog.show();
        String url;

        url = Helper.getInstance().getAPIUrl() + "sendInspectionEmail.php";

        Map<String, String> params = new HashMap<>();

        params.put("locationName", editTextLocationName.getText().toString());
        params.put("staffID", staffModel.getId());
        params.put("location", mLastLocation.getLatitude() + "," + mLastLocation.getLongitude());
        params.put("fileCount", selectedImageModelArrayList.size() + "");

        DataSubmissionAndMail.getInstance().sendMail(params, "send_inspection_mail-" + staffModel.getId(), volleyHelper, url);
    }

    public void setSelectedFileCount(int count) {
        String text = "Selected Files = " + count;
        textViewSelectedFileCount.setText(text);
    }

    private void showTutorial() {

        final FancyShowCaseView fancyShowCaseView1 = new FancyShowCaseView.Builder(mainActivity)
                .title("Click to refresh location")
                .focusOn(circularProgressButton)
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .build();

        mainActivity.setmQueue(new FancyShowCaseQueue()
                .add(fancyShowCaseView1));

        mainActivity.getmQueue().setCompleteListener(() -> {
            mainActivity.setmQueue(null);
            getLocationCoordinates();
        });

        mainActivity.getmQueue().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: " + requestCode + " ," + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (imagePicker != null)
            imagePicker.onActivityResult(mainActivity, requestCode, resultCode, data);

        switch (requestCode) {

            case CONNECTION_FAILURE_RESOLUTION_REQUEST: {
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        Log.v(TAG, "Resolution success");
                        myLocationManager.requestLocationUpdates();
                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        Log.v(TAG, "Resolution denied");
                        myLocationManager.ShowDialogOnLocationOff("Location not turned on! Inspection will not show nearby locations without location access\nTurn Location on and Refresh");
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
                    imagePicker.onRequestPermissionsResult(mainActivity, requestCode, permissions, grantResults);
            }

        }

    }

    @Override
    public void onError(VolleyError volleyError) {
        volleyError.printStackTrace();
        progressDialog.dismiss();
        Toast.makeText(getContext(), "Some Error Occurred\nPlease be patient we are getting things fixed", Toast.LENGTH_SHORT).show();
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
                    Helper.getInstance().showErrorDialog("Files could not be uploaded\nTry Again", "Inspection", mainActivity);
                    progressDialog.dismiss();
                }
            } else if (jsonObject.getString("action").equals("Sending Mail")) {

                if (jsonObject.get("result").equals(Helper.getInstance().SUCCESS)) {

                    progressDialog.dismiss();
                    String alertMessage = "Inspection data for" +
                            "<br><b>" + editTextLocationName.getText() + "</b><br>" +
                            "containing <b>" + selectedImageModelArrayList.size() + "</b> images " +
                            " has been successfully submitted";


                    Helper.getInstance().showFancyAlertDialog(mainActivity, alertMessage, "Inspection", "OK", () -> {
                    }, null, null, FancyAlertDialogType.SUCCESS);
                    isUploadedToServer = isUploadedToFirebase = false;

                } else {
                    progressDialog.dismiss();
                    Helper.getInstance().showErrorDialog("Inspection Submission Failed\nTry Again", "Inspection", mainActivity);
                }
            }
        } catch (JSONException jse) {
            jse.printStackTrace();
        }
    }


    @Override
    public void onMenuItemSelected(View view, int id) {
        switch (items.get(id).getTitle()) {
            case "Add Image":
                showImageChooser();
                break;
            case "Remove All":
                removeAllSelectedImages();
                break;
        }
    }
}

