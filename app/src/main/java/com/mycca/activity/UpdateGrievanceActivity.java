package com.mycca.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;
import com.mycca.adapter.GenericSpinnerAdapter;
import com.mycca.adapter.RecyclerViewAdapterGrievanceUpdate;
import com.mycca.adapter.RecyclerViewAdapterSelectedImages;
import com.mycca.custom.customImagePicker.cropper.CropImage;
import com.mycca.custom.customImagePicker.cropper.CropImageView;
import com.mycca.custom.customImagePicker.ImagePicker;
import com.mycca.custom.FabRevealMenu.FabListeners.OnFABMenuSelectedListener;
import com.mycca.custom.FabRevealMenu.FabModel.FABMenuItem;
import com.mycca.custom.FabRevealMenu.FabView.FABRevealMenu;
import com.mycca.custom.FancyAlertDialog.FancyAlertDialogType;
import com.mycca.custom.Progress.ProgressDialog;
import com.mycca.listeners.OnConnectionAvailableListener;
import com.mycca.models.GrievanceModel;
import com.mycca.models.SelectedImageModel;
import com.mycca.models.StatusModel;
import com.mycca.notification.Constants;
import com.mycca.notification.FirebaseNotificationHelper;
import com.mycca.R;
import com.mycca.tools.ConnectionUtility;
import com.mycca.tools.DataSubmissionAndMail;
import com.mycca.tools.FireBaseHelper;
import com.mycca.tools.Helper;
import com.mycca.tools.VolleyHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UpdateGrievanceActivity extends AppCompatActivity implements VolleyHelper.VolleyResponse, OnFABMenuSelectedListener {

    boolean isUploadedToFireBaseDatabase = false, isUploadedToFireBase = false, isUploadedToServer = false;
    int counterUpload = 0;
    int counterServerImages = 0;
    long status;
    String TAG = "UpdateGrievance", message;

    TextView textViewPensionerCode, textViewRefNo, textViewGrievanceString, textViewDateOfApplication, textViewAttachedFileCount;
    Spinner statusSpinner;
    EditText editTextMessage;
    Button update;
    FABRevealMenu fabMenu;
    ProgressDialog progressDialog;
    FloatingActionButton buttonAttachFile;
    ImagePicker imagePicker;
    RecyclerView recyclerViewAttachments;

    GrievanceModel grievanceModel;
    VolleyHelper volleyHelper;
    RecyclerViewAdapterSelectedImages adapterSelectedImages;
    DatabaseReference dbRef = FireBaseHelper.getInstance(this).versionedDbRef;
    ArrayList<SelectedImageModel> attachmentModelArrayList;
    private ArrayList<FABMenuItem> items;
    ArrayList<Uri> fireBaseImageURLs;
    StatusModel[] statusArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_grievance);
        grievanceModel = (GrievanceModel) Helper.getInstance().getObjectFromJson(getIntent().getStringExtra("Model"), GrievanceModel.class);
        bindViews();
        init();
        setLayoutData();
    }

    private void bindViews() {
        textViewPensionerCode = findViewById(R.id.textview_pensioner);
        textViewRefNo = findViewById(R.id.textview_reference_number);
        textViewGrievanceString = findViewById(R.id.textview_grievance_type);
        textViewDateOfApplication = findViewById(R.id.textview_date);
        statusSpinner = findViewById(R.id.spinner_status);
        editTextMessage = findViewById(R.id.edittext_message);
        update = findViewById(R.id.button_update);
        progressDialog = Helper.getInstance().getProgressWindow(this, getString(R.string.fetching_grievance));
        recyclerViewAttachments = findViewById(R.id.recycler_view_update_grievance_attachments);
        buttonAttachFile = findViewById(R.id.button_attach_update_grievance);
        textViewAttachedFileCount = findViewById(R.id.textview_selected_file_count_update);
    }

    private void initItems() {
        items = new ArrayList<>();
        items.add(new FABMenuItem(getString(R.string.add_image), AppCompatResources.getDrawable(this, R.drawable.ic_attach_file_white_24dp)));
        items.add(new FABMenuItem(getString(R.string.remove_all), AppCompatResources.getDrawable(this, R.drawable.ic_close_24dp)));
    }

    private void init() {
        if (grievanceModel.getGrievanceStatus() == 0) {
            statusArray = new StatusModel[]{new StatusModel(1, getString(R.string.under_process)),
                    new StatusModel(2, getString(R.string.resolved))};
        } else if (grievanceModel.getGrievanceStatus() == 1) {
            statusArray = new StatusModel[]{new StatusModel(2, getString(R.string.resolved))};
        }
        GenericSpinnerAdapter<StatusModel> arrayAdapter = new GenericSpinnerAdapter<>(this, statusArray);
        statusSpinner.setAdapter(arrayAdapter);
        volleyHelper = new VolleyHelper(this, this);
        update.setOnClickListener(v -> startGrievanceUpdate());

        attachmentModelArrayList = new ArrayList<>();
        adapterSelectedImages = new RecyclerViewAdapterSelectedImages(attachmentModelArrayList, this);
        recyclerViewAttachments.setAdapter(adapterSelectedImages);
        recyclerViewAttachments.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        fireBaseImageURLs = new ArrayList<>();
        setSelectedFileCount(0);
        initItems();
        fabMenu = findViewById(R.id.fabMenu_Update);
        try {
            if (buttonAttachFile != null && fabMenu != null) {
                //attach menu to fab
                //set menu items from arraylist
                fabMenu.setMenuItems(items);
                //attach menu to fab
                fabMenu.bindAnchorView(buttonAttachFile);
                //set menu item selection
                fabMenu.setOnFABMenuSelectedListener(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideKeyboard() {
        Helper.getInstance().hideKeyboardFrom(this);
    }

    private void startGrievanceUpdate() {
        ConnectionUtility connectionUtility = new ConnectionUtility(new OnConnectionAvailableListener() {
            @Override
            public void OnConnectionAvailable() {
                if (!Helper.versionChecked) {
                    FireBaseHelper.getInstance(UpdateGrievanceActivity.this).checkForUpdate(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (Helper.getInstance().onLatestVersion(dataSnapshot, UpdateGrievanceActivity.this))
                                doUpdateOnInternetAvailable();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Helper.getInstance().showMaintenanceDialog(UpdateGrievanceActivity.this);
                        }
                    });
                } else doUpdateOnInternetAvailable();
            }

            @Override
            public void OnConnectionNotAvailable() {
                showNoInternetConnectionDialog();
            }
        });
        connectionUtility.checkConnectionAvailability();
    }

    private void showNoInternetConnectionDialog() {
        Helper.getInstance().showFancyAlertDialog(this,
                getString(R.string.no_internet),
                getString(R.string.connect_to_internet),
                getString(R.string.ok),
                null,
                null,
                null,
                FancyAlertDialogType.ERROR);
    }

    private void doUpdateOnInternetAvailable() {
        Log.d(TAG, "doSubmissionOnInternetAvailable: \n Firebase = " + isUploadedToFireBaseDatabase + "\n" +
                "Server = " + isUploadedToServer);
        progressDialog.show();

        if (isUploadedToFireBase) {
            if (isUploadedToServer) {
                Log.d(TAG, "doUpdateOnInternetAvailable: Data uploaded on server");
                sendFinalMail();
            } else {
                uploadImagesToServer();
            }
        } else {
            updateGrievanceDataOnFirebase();
        }

    }

    private void updateGrievanceDataOnFirebase() {

        status = ((StatusModel) statusSpinner.getSelectedItem()).getStatusCode();
        message = editTextMessage.getText().toString().trim();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("grievanceStatus", status);
        hashMap.put("message", message);

        Task<Void> task = FireBaseHelper.getInstance(this).updateData(String.valueOf(grievanceModel.getGrievanceType()),
                hashMap,
                FireBaseHelper.ROOT_GRIEVANCES,
                grievanceModel.getState(),
                grievanceModel.getPensionerIdentifier());
        task.addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                uploadAllImagesToFirebase();
            } else {
                progressDialog.dismiss();
                Helper.getInstance().showMaintenanceDialog(UpdateGrievanceActivity.this);
                Log.d(TAG, "onComplete: " + task1.toString());
            }
        });

    }

    private void uploadAllImagesToFirebase() {
        if (attachmentModelArrayList.size() > 0) {
            progressDialog.setMessage(getString(R.string.uploading_files));
            Log.d(TAG, "uploadAllImagesToFirebase: uploading");
            counterUpload = 0;

            for (int i = 0; i < attachmentModelArrayList.size(); i++) {
                final UploadTask uploadTask = FireBaseHelper.getInstance(this).uploadFiles(
                        attachmentModelArrayList.get(i),
                        true,
                        i,
                        FireBaseHelper.ROOT_GRIEVANCES,
                        grievanceModel.getPensionerIdentifier(),
                        String.valueOf(grievanceModel.getGrievanceType()),
                        FireBaseHelper.ROOT_BY_STAFF);

                if (uploadTask != null) {
                    uploadTask.addOnFailureListener(exception -> onFailure(getString(R.string.file_not_uploaded)))
                            .addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                                fireBaseImageURLs.add(uri);
                                progressDialog.setMessage(String.format(getString(R.string.uploaded_file), String.valueOf(++counterUpload), String.valueOf(attachmentModelArrayList.size())));
                                if (counterUpload == attachmentModelArrayList.size()) {
                                    isUploadedToFireBase = true;
                                    doUpdateOnInternetAvailable();
                                }
                            }));
                }
            }
        } else {
            isUploadedToFireBase = true;
            doUpdateOnInternetAvailable();
        }
    }

    private void uploadImagesToServer() {

        counterServerImages = 0;
        progressDialog.setMessage(getString(R.string.processing));
        int totalFilesToAttach = attachmentModelArrayList.size();
        String url = Helper.getInstance().getAPIUrl() + "uploadImage.php/";

        if (totalFilesToAttach != 0) {
            try {
                DataSubmissionAndMail.getInstance().uploadImagesToServer(url,
                        fireBaseImageURLs,
                        grievanceModel.getPensionerIdentifier(),
                        DataSubmissionAndMail.UPDATE,
                        volleyHelper);
            } catch (Exception e) {
                e.printStackTrace();
                Helper.getInstance().showErrorDialog(getString(R.string.some_error), getString(R.string.try_again), this);
            }
        } else {
            isUploadedToServer = true;
            doUpdateOnInternetAvailable();
        }
    }

    private void sendFinalMail() {

        progressDialog.setMessage(getString(R.string.almost_done));
        String url = Helper.getInstance().getAPIUrl() + "sendUpdateGrievanceEmail.php/";
        Map<String, String> params = new HashMap<>();
        String pensionerCode = grievanceModel.getPensionerIdentifier();

        params.put("pensionerCode", pensionerCode);
        params.put("folder", DataSubmissionAndMail.UPDATE);
        params.put("pensionerEmail", grievanceModel.getEmail());
        params.put("status", Helper.getInstance().getStatusString(status));
        params.put("refNo", grievanceModel.getReferenceNo());
        params.put("grievanceType", Helper.getInstance().getGrievanceCategory(grievanceModel.getGrievanceType()));
        params.put("grievanceSubType", Helper.getInstance().getGrievanceString((grievanceModel.getGrievanceType())));
        params.put("message", message);
        params.put("fileCount", attachmentModelArrayList.size() + "");
        DataSubmissionAndMail.getInstance().sendMail(params, "send_mail-" + pensionerCode, volleyHelper, url);
    }

    private void revertUpdatesOnFireBase() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("grievanceStatus", grievanceModel.getGrievanceStatus());
        hashMap.put("message", grievanceModel.getMessage());

        Task<Void> task = FireBaseHelper.getInstance(this).updateData(String.valueOf(grievanceModel.getGrievanceType()),
                hashMap,
                FireBaseHelper.ROOT_GRIEVANCES,
                grievanceModel.getState(),
                grievanceModel.getPensionerIdentifier());
    }

    private void showSuccessDialog() {

        grievanceModel.setGrievanceStatus(status);
        grievanceModel.setMessage(message);
        grievanceModel.setExpanded(true);
        isUploadedToServer = isUploadedToFireBase = isUploadedToFireBaseDatabase = false;

        String alertMessage = String.format(getString(R.string.grievance_updation_success),
                grievanceModel.getPensionerIdentifier(),
                Helper.getInstance().getGrievanceString(grievanceModel.getGrievanceType()),
                Helper.getInstance().getStatusString(grievanceModel.getGrievanceStatus()));

        progressDialog.dismiss();
        notifyPensioner();
        setResult(Activity.RESULT_OK);
        Helper.getInstance().showFancyAlertDialog(UpdateGrievanceActivity.this, alertMessage, getString(R.string.update_grievances), getString(R.string.ok), () -> {

            finishActivity(RecyclerViewAdapterGrievanceUpdate.REQUEST_UPDATE);
            finish();
        }, null, null, FancyAlertDialogType.SUCCESS);
    }

    private void notifyPensioner() {

        FireBaseHelper.getInstance(this).nonVersionedDbref.child("FCMServerKey").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String fcmKey = (String) dataSnapshot.getValue();
                Log.d(TAG, "Notification : Key received");
                getTokenAndSendNotification(fcmKey);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getTokenAndSendNotification(final String fcmKey) {
        dbRef.child(FireBaseHelper.ROOT_TOKEN)
                .child(grievanceModel.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            String token = (String) dataSnapshot.getValue();
                            Log.d(TAG, "Notification : Token received");
                            sendNotification(fcmKey, token);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void sendNotification(String fcmKey, String token) {

        FirebaseNotificationHelper.initialize(fcmKey)
                .jsonBody(getJsonBody())
                .receiverFirebaseToken(token)
                .send();

        //        String tag = "Notify";
//        Map<String, String> header = new HashMap<>();
//
//        header.put("Content-Type", "application/json");
//        header.put("Authorization", "Key=" + fcmKey);
//        JSONObject params = new JSONObject();
//        try {
//            params.put("data", getJsonBody());
//            params.put("to", token);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        if (volleyHelper.countRequestsInFlight(tag) == 0)
//            volleyHelper.makeJsonRequest(Constants.FCM_URL, tag, params, header);
    }

    private String getJsonBody() {

        String newStatus = Helper.getInstance().getStatusString(grievanceModel.getGrievanceStatus());
        String type = Helper.getInstance().getGrievanceString(grievanceModel.getGrievanceType());
        JSONObject jsonObjectData = new JSONObject();
        try {
            jsonObjectData.put(Constants.KEY_TITLE, getString(R.string.notification_title));
            jsonObjectData.put(Constants.KEY_BODY, String.format(getString(R.string.notification_body), type, newStatus));
            jsonObjectData.put(Constants.KEY_CODE, textViewPensionerCode.getText());
            jsonObjectData.put(Constants.KEY_GTYPE, grievanceModel.getGrievanceType());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObjectData.toString();
    }

    private void showImageChooser() {

        imagePicker = Helper.getInstance().showImageChooser(imagePicker, this, true, new ImagePicker.Callback() {
            @Override
            public void onPickImage(Uri imageUri) {
                Log.d(TAG, "onPickImage: " + imageUri.getPath());

            }

            @Override
            public void onCropImage(Uri imageUri) {
                Log.d(TAG, "onCropImage: " + imageUri.getPath());
                int currentPosition = attachmentModelArrayList.size();
                attachmentModelArrayList.add(currentPosition, new SelectedImageModel(imageUri));
                adapterSelectedImages.notifyItemInserted(currentPosition);
                adapterSelectedImages.notifyDataSetChanged();
                Log.d(TAG, "onCropImage: Item inserted at " + currentPosition);
                setSelectedFileCount(currentPosition + 1);
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
                Log.d(TAG, "onPermissionDenied: Permission not given to choose textViewMessage");
            }
        });

    }

    private void removeAllSelectedImages() {
        if (attachmentModelArrayList == null || adapterSelectedImages == null) {
            return;
        }
        attachmentModelArrayList.clear();
        adapterSelectedImages.notifyDataSetChanged();
        setSelectedFileCount(0);
    }

    public void setSelectedFileCount(int count) {
        textViewAttachedFileCount.setText(String.format(getString(R.string.files_selected), String.valueOf(count)));
    }

    private void setLayoutData() {
        textViewPensionerCode.setText(grievanceModel.getPensionerIdentifier());
        textViewRefNo.setText(grievanceModel.getReferenceNo());
        textViewGrievanceString.setText(Helper.getInstance().getGrievanceString(grievanceModel.getGrievanceType()));
        textViewDateOfApplication.setText(Helper.getInstance().formatDate(grievanceModel.getDate(), Helper.DateFormat.DD_MM_YYYY));
        editTextMessage.setText(grievanceModel.getMessage() == null ? getResources().getString(R.string.n_a) : grievanceModel.getMessage());
    }

    private void onFailure(String message) {
        revertUpdatesOnFireBase();
        progressDialog.dismiss();
        Helper.getInstance().showErrorDialog(message, getString(R.string.some_error), this);
    }

    @Override
    public void onBackPressed() {
        if (fabMenu != null && fabMenu.isShowing()) {
            fabMenu.closeMenu();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onError(VolleyError volleyError) {
        volleyError.printStackTrace();
        onFailure(getString(R.string.grievance_updation_fail));
    }

    @Override
    public void onResponse(String str) {
        JSONObject jsonObject = Helper.getInstance().getJson(str);
        Log.d(TAG, jsonObject.toString());
        try {
            if (jsonObject.get("action").equals("Creating Image")) {
                counterServerImages++;
                if (jsonObject.get("result").equals(Helper.getInstance().SUCCESS)) {
                    if (counterServerImages == attachmentModelArrayList.size()) {
                        Log.d(TAG, "onResponse: Files uploaded");
                        isUploadedToServer = true;
                        doUpdateOnInternetAvailable();
                    }
                } else {
                    onFailure(getString(R.string.file_not_uploaded));
                }
            } else if (jsonObject.getString("action").equals("Sending Mail to user")) {
                if (jsonObject.get("result").equals(Helper.getInstance().SUCCESS)) {
                    showSuccessDialog();
                } else {
                    onFailure(getString(R.string.grievance_updation_fail));
                }
            }
        } catch (JSONException jse) {
            jse.printStackTrace();
            onFailure("Please Try Again");
        }
    }

    @Override
    public void onMenuItemSelected(View view, int id) {
        switch (id) {
            case R.string.add_image:
                showImageChooser();
                break;
            case R.string.remove_all:
                removeAllSelectedImages();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: " + requestCode + " ," + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (imagePicker != null)
            imagePicker.onActivityResult(this, requestCode, resultCode, data);
    }

    //    private void addImageDataToFirebaseDatabase() {
//
//        Log.d(TAG, "addImageDataToFireBaseDatabase: ");
//        AtomicInteger uriCounter = new AtomicInteger();
//        Log.d(TAG, "addImageDataToFireBaseDatabase: size = " + attachmentModelArrayList.size());
//        for (int i = 0; i < attachmentModelArrayList.size(); i++) {
//
//            Task<Void> task = FireBaseHelper.getInstance(this).uploadDataToFirebase(attachmentModelArrayList.get(i).getImageURI().toString(),
//                    FireBaseHelper.ROOT_IMAGES_BY_STAFF,
//                    grievanceModel.getReferenceNo(),
//                    "Image" + i);
//            Log.d(TAG, "addImageDataToFireBaseDatabase: Adding Task");
//            if (task != null) {
//                Log.d(TAG, "addImageDataToFireBaseDatabase: task not null");
//                task.addOnFailureListener(exception -> {
//                    Log.d(TAG, "addImageDataToFirebaseDatabase: failure");
//                    progressDialog.dismiss();
//                    Helper.getInstance().showErrorDialog("Data could not be uploaded\nTry Again", "Submission Error", this);
//                    Log.d(TAG, "onFailure: " + exception.getMessage());
//                })
//                        .addOnSuccessListener(taskSnapshot -> {
//                                    uriCounter.incrementAndGet();
//                                    Log.d(TAG, "onSuccess: counter = " + uriCounter + "size = " + attachmentModelArrayList.size());
//                                    if (uriCounter.get() == attachmentModelArrayList.size()) {
//                                        isUploadedToFireBaseDatabase = true;
//                                        doUpdateOnInternetAvailable();
//                                    }
//                                });
//            }
//        }
//    }
}