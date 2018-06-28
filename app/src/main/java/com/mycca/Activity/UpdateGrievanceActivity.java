package com.mycca.Activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;
import com.mycca.Adapter.GenericSpinnerAdapter;
import com.mycca.Adapter.RecyclerViewAdapterGrievanceUpdate;
import com.mycca.Adapter.RecyclerViewAdapterSelectedImages;
import com.mycca.CustomObjects.CustomImagePicker.Cropper.CropImage;
import com.mycca.CustomObjects.CustomImagePicker.Cropper.CropImageView;
import com.mycca.CustomObjects.CustomImagePicker.ImagePicker;
import com.mycca.CustomObjects.FabRevealMenu.FabListeners.OnFABMenuSelectedListener;
import com.mycca.CustomObjects.FabRevealMenu.FabModel.FABMenuItem;
import com.mycca.CustomObjects.FabRevealMenu.FabView.FABRevealMenu;
import com.mycca.CustomObjects.FancyAlertDialog.FancyAlertDialogType;
import com.mycca.CustomObjects.Progress.ProgressDialog;
import com.mycca.Listeners.OnConnectionAvailableListener;
import com.mycca.Models.GrievanceModel;
import com.mycca.Models.SelectedImageModel;
import com.mycca.Models.StatusModel;
import com.mycca.Notification.Constants;
import com.mycca.Notification.FirebaseNotificationHelper;
import com.mycca.Providers.GrievanceDataProvider;
import com.mycca.R;
import com.mycca.Tools.ConnectionUtility;
import com.mycca.Tools.DataSubmissionAndMail;
import com.mycca.Tools.FireBaseHelper;
import com.mycca.Tools.Helper;
import com.mycca.Tools.Preferences;
import com.mycca.Tools.VolleyHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class UpdateGrievanceActivity extends AppCompatActivity implements VolleyHelper.VolleyResponse, OnFABMenuSelectedListener {

    TextView textViewPensionerCode, textViewGrievanceString, textViewDateOfApplication;
    Spinner statusSpinner;
    EditText editTextMessage;
    Button update;
    FABRevealMenu fabMenu;
    ProgressDialog progressDialog;
    StatusModel[] statusArray;
    RecyclerView recyclerViewAttachments;
    DatabaseReference dbref = FireBaseHelper.getInstance(this).versionedDbRef;
    ArrayList<SelectedImageModel> attachmentModelArrayList;
    private ArrayList<FABMenuItem> items;
    RecyclerViewAdapterSelectedImages adapterSelectedImages;
    String TAG = "Update";
    GrievanceModel grievanceModel;
    TextView textViewAttachedFileCount;
    ArrayList<Uri> firebaseImageURLs;
    boolean isUploadedToFirebaseDatabase = false, isuploadedtoFirebaseStorage = false, isUploadedToServer = false;
    int counterUpload = 0;
    int counterServerImages = 0;
    int counterFirebaseImages;
    VolleyHelper volleyHelper;
    FloatingActionButton buttonAttachFile;
    ImagePicker imagePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_grievance);
        grievanceModel = GrievanceDataProvider.getInstance().selectedGrievance;
        bindViews();
        init();
        setLayoutData();
    }

    private void bindViews() {
        textViewPensionerCode = findViewById(R.id.textview_pensioner);
        textViewGrievanceString = findViewById(R.id.textview_grievance_type);
        textViewDateOfApplication = findViewById(R.id.textview_date);
        statusSpinner = findViewById(R.id.spinner_status);
        editTextMessage = findViewById(R.id.edittext_message);
        update = findViewById(R.id.button_update);
        update.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_update_black_24dp, 0, 0);
        progressDialog = Helper.getInstance().getProgressWindow(this, "Updating Grievance Details");
        recyclerViewAttachments = findViewById(R.id.recycler_view_update_grievance_attachments);
        buttonAttachFile = findViewById(R.id.button_attach_update_grievance);
        textViewAttachedFileCount = findViewById(R.id.textview_selected_file_count_update);
    }

    private void init() {
        if (grievanceModel.getGrievanceStatus() == 0) {
            statusArray = new StatusModel[]{new StatusModel(1, "Under Process"),
                    new StatusModel(2, "Resolved")};
        } else if (grievanceModel.getGrievanceStatus() == 1) {
            statusArray = new StatusModel[]{new StatusModel(2, "Resolved")};
        }
        GenericSpinnerAdapter<StatusModel> arrayAdapter = new GenericSpinnerAdapter<>(this, statusArray);
        statusSpinner.setAdapter(arrayAdapter);
        volleyHelper = new VolleyHelper(this, this);
        update.setOnClickListener(v -> {
            startGrievanceUpdate();
        });

        attachmentModelArrayList = new ArrayList<>();
        adapterSelectedImages = new RecyclerViewAdapterSelectedImages(attachmentModelArrayList, this);
        recyclerViewAttachments.setAdapter(adapterSelectedImages);
        recyclerViewAttachments.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        firebaseImageURLs = new ArrayList<>();
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
        buttonAttachFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();

            }
        });
    }

    private void hideKeyboard() {
        Helper.getInstance().hideKeyboardFrom(this);
    }

    private void startGrievanceUpdate() {
        GrievanceModel model = GrievanceDataProvider.getInstance().selectedGrievance;
        model.setGrievanceStatus((int) ((StatusModel) statusSpinner.getSelectedItem()).getStatusCode());
        model.setMessage(editTextMessage.getText().toString().trim());
        ConnectionUtility connectionUtility = new ConnectionUtility(new OnConnectionAvailableListener() {
            @Override
            public void OnConnectionAvailable() {
                Log.d(TAG, "version checked = " + Helper.versionChecked);
                if (!Helper.versionChecked) {
                    FireBaseHelper.getInstance(UpdateGrievanceActivity.this).checkForUpdate(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (Helper.getInstance().onLatestVersion(dataSnapshot, UpdateGrievanceActivity.this))
                                doUpdateOnInternetAvailable();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Helper.getInstance().showUpdateOrMaintenanceDialog(false, UpdateGrievanceActivity.this);
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

    public void setSelectedFileCount(int count) {
        String text = count + " Files Selected";
        textViewAttachedFileCount.setText(text);
    }

    private void showNoInternetConnectionDialog() {
        Helper.getInstance().showFancyAlertDialog(this,
                "No Internet Connection\nTurn on Internet Connection to Update grievance",
                "Update Grievance",
                "OK",
                null,
                null,
                null,
                FancyAlertDialogType.ERROR);
    }

    private void uploadAllImagesToFirebase() {
        if (attachmentModelArrayList.size() > 0) {
            progressDialog.setMessage("Uploading Files...\nPlease Wait");
            Log.d(TAG, "uploadAllImagesToFirebase: uploading");
            if (!progressDialog.isShowing()) progressDialog.show();
            counterFirebaseImages = 0;
            counterUpload = 0;

            for (SelectedImageModel imageModel : attachmentModelArrayList) {
                final UploadTask uploadTask = FireBaseHelper.getInstance(this).uploadFiles(
                        imageModel,
                        true,
                        counterFirebaseImages++,
                        FireBaseHelper.ROOT_GRIEVANCES,
                        grievanceModel.getPensionerIdentifier(),
                        String.valueOf(grievanceModel.getGrievanceType()),
                        FireBaseHelper.ROOT_BY_STAFF);

                if (uploadTask != null) {
                    uploadTask.addOnFailureListener(
                            exception -> {
                                Helper.getInstance().showErrorDialog("Files could not be uploaded\nTry Again", "Submission Error", this);
                                Log.d(TAG, "onFailure: " + exception.getMessage());
                                progressDialog.dismiss();
                            })
                            .addOnSuccessListener(
                                    taskSnapshot -> {
                                        // Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                                            firebaseImageURLs.add(uri);
                                            progressDialog.setMessage("Uploaded file " + (++counterUpload) + " / " + attachmentModelArrayList.size());
                                            Log.d(TAG, "onSuccess: counter = " + counterUpload + "size = " + attachmentModelArrayList.size());
                                            if (counterUpload == attachmentModelArrayList.size()) {
                                                isuploadedtoFirebaseStorage = true;
                                                doUpdateOnInternetAvailable();
                                            }
                                        });
                                    });
                }
            }
        } else {
            isuploadedtoFirebaseStorage = true;
            doUpdateOnInternetAvailable();
        }
    }

    private void doUpdateOnInternetAvailable() {
        Log.d(TAG, "doSubmissionOnInternetAvailable: \n Firebase = " + isUploadedToFirebaseDatabase + "\n" +
                "Server = " + isUploadedToServer);
        if (isuploadedtoFirebaseStorage) {
            if (isUploadedToFirebaseDatabase) {
                if (isUploadedToServer) {
                    Log.d(TAG, "doUpdateOnInternetAvailable: Data uploaded on server mail left");
                    progressDialog.dismiss();
                    //sendFinalMail();
                } else {
                    uploadImagesToServer();
                }
            } else {
                addImageDataToFirebaseDatabase();
            }

        } else {
            updateGrievanceDataOnFirebase();
        }
    }

    private void addImageDataToFirebaseDatabase() {
        Log.d(TAG, "addImageDataToFirebaseDatabase: ");
        AtomicInteger uriCounter = new AtomicInteger();
        Log.d(TAG, "addImageDataToFirebaseDatabase: size = " + attachmentModelArrayList.size() );
        for (int i = 0; i < attachmentModelArrayList.size(); i++) {

            Task<Void> task = FireBaseHelper.getInstance(this).uploadDataToFirebase(attachmentModelArrayList.get(i).getImageURI().toString(),
                    FireBaseHelper.ROOT_IMAGES_BY_STAFF,
                    grievanceModel.getReferenceNo(),
                    "Image" + i);
            Log.d(TAG, "addImageDataToFirebaseDatabase: Adding Task");
            if (task != null) {
                Log.d(TAG, "addImageDataToFirebaseDatabase: task not null");
                task.addOnFailureListener(
                        exception -> {
                            Log.d(TAG, "addImageDataToFirebaseDatabase: failure");
                            progressDialog.dismiss();
                            Helper.getInstance().showErrorDialog("Data could not be uploaded\nTry Again", "Submission Error", this);
                            Log.d(TAG, "onFailure: " + exception.getMessage());
                        })
                        .addOnSuccessListener(
                                taskSnapshot -> {
                                    uriCounter.incrementAndGet();
                                    Log.d(TAG, "onSuccess: counter = " + uriCounter + "size = " + attachmentModelArrayList.size());
                                    if (uriCounter.get() == attachmentModelArrayList.size()) {
                                        isUploadedToFirebaseDatabase = true;
                                        doUpdateOnInternetAvailable();
                                    }
                                });
            }
        }
    }

    private void initItems() {
        items = new ArrayList<>();
        items.add(new FABMenuItem("Add Image", AppCompatResources.getDrawable(this, R.drawable.ic_attach_file_white_24dp)));
        items.add(new FABMenuItem("Remove All", AppCompatResources.getDrawable(this, R.drawable.ic_close_24dp)));
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
//                File file = new File(imageUri.getPath());
//                Picasso.with(getContext()).load(imageUri).into(imageviewSelectedImage);
//                setupSelectedFile(file);
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

    private void setLayoutData() {
        textViewPensionerCode.setText(grievanceModel.getPensionerIdentifier());
        textViewGrievanceString.setText(Helper.getInstance().getGrievanceString(grievanceModel.getGrievanceType()));
        textViewDateOfApplication.setText(Helper.getInstance().formatDate(grievanceModel.getDate(), "MMM d, yyyy"));
        editTextMessage.setText(grievanceModel.getMessage() == null ? "" : grievanceModel.getMessage());
    }

    private void updateGrievanceDataOnFirebase() {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("grievanceStatus", (int) ((StatusModel) statusSpinner.getSelectedItem()).getStatusCode());
        hashMap.put("message", editTextMessage.getText().toString().trim());

        Task<Void> task = FireBaseHelper.getInstance(this).updateData(String.valueOf(grievanceModel.getGrievanceType()),
                hashMap,
                FireBaseHelper.ROOT_GRIEVANCES,
                Preferences.getInstance().getStaffPref(this, Preferences.PREF_STAFF_DATA).getState(),
                grievanceModel.getPensionerIdentifier());
        task.addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                uploadAllImagesToFirebase();
                //Toast.makeText(UpdateGrievanceActivity.this, "Successfully Updated", Toast.LENGTH_LONG).show();

            } else {
                progressDialog.dismiss();
                Helper.getInstance().showFancyAlertDialog(UpdateGrievanceActivity.this, "The app might be in maintenence. Please try again later.", "Unable to Update", "OK", null, null, null, FancyAlertDialogType.ERROR);
                Log.d(TAG, "onComplete: " + task1.toString());
            }
        });

    }

    private void showSuccessDialog() {
        GrievanceModel model = GrievanceDataProvider.getInstance().selectedGrievance;
        model.setExpanded(true);
        isUploadedToServer = isuploadedtoFirebaseStorage = isUploadedToFirebaseDatabase = false;

        String alertMessage = Helper.getInstance().getGrievanceCategory(model.getGrievanceType()) +
                " Grievance status of<br>" +
                "<b>" + model.getPensionerIdentifier() + "</b><br>" +
                "for<br>" +
                "<b>" + Helper.getInstance().getGrievanceString(model.getGrievanceType()) + "</b><br>" +
                "has been successfully updated to<br>" +
                "<b>" + Helper.getInstance().getStatusString(model.getGrievanceStatus()) + "</b>";

        notifyPensioner();
        setResult(Activity.RESULT_OK);
        Helper.getInstance().showFancyAlertDialog(UpdateGrievanceActivity.this, alertMessage, "Grievance Update", "OK", () -> {

            finishActivity(RecyclerViewAdapterGrievanceUpdate.REQUEST_UPDATE);
            finish();
        }, null, null, FancyAlertDialogType.SUCCESS);
    }

    private void uploadImagesToServer() {

        counterServerImages = 0;
        progressDialog.setMessage("Processing..");
        if (!progressDialog.isShowing()) progressDialog.show();
        int totalFilesToAttach = attachmentModelArrayList.size();
        String url = Helper.getInstance().getAPIUrl() + "uploadImage.php";

        if (totalFilesToAttach != 0) {
            try {
                DataSubmissionAndMail.getInstance().uploadImagesToServer(url,
                        firebaseImageURLs,
                        grievanceModel.getPensionerIdentifier() + "-" + grievanceModel.getGrievanceType(),
                        volleyHelper);
            } catch (Exception e) {
                e.printStackTrace();
                Helper.getInstance().showErrorDialog("Some Error Occured", "Error", this);
            }
        } else {
            isUploadedToServer = true;
            sendFinalMail();
        }
    }

    private void sendFinalMail() {

        progressDialog.setMessage("Almost Done..");
        progressDialog.show();
        String url = Helper.getInstance().getAPIUrl() + "sendUpdateEmail.php";
        Map<String, String> params = new HashMap<>();
        String pensionerCode = grievanceModel.getPensionerIdentifier().toString();

        params.put("pensionerCode", pensionerCode);
        params.put("pensionerEmail", grievanceModel.getEmail());
        params.put("grievanceType", Long.toString(grievanceModel.getGrievanceType()));
        params.put("grievanceSubType", Helper.getInstance().getGrievanceString((grievanceModel.getGrievanceType())));
        params.put("grievanceDetails", grievanceModel.getMessage());
        params.put("fileCount", attachmentModelArrayList.size() + "");
        DataSubmissionAndMail.getInstance().sendMail(params, "send_mail-" + pensionerCode, volleyHelper, url);
    }

    private void notifyPensioner() {

        FirebaseDatabase.getInstance().getReference().child("FCMServerKey").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String fcmKey = (String) dataSnapshot.getValue();
                Log.d(TAG, "Notification : Key recieved");
                getTokenAndSendNotification(fcmKey);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getTokenAndSendNotification(final String fcmKey) {
        dbref.child(FireBaseHelper.ROOT_TOKEN)
                .child(GrievanceDataProvider.getInstance().selectedGrievance.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String token = (String) dataSnapshot.getValue();
                        Log.d(TAG, "Notification : Token recieved");
                        sendNotification(fcmKey, token);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void sendNotification(String fcmKey, String token) {

        FirebaseNotificationHelper.initialize(fcmKey)
                .defaultJson(false, getJsonBody())
                .receiverFirebaseToken(token)
                .send();


    }

    private String getJsonBody() {

        String newStatus = Helper.getInstance().getStatusString(GrievanceDataProvider.getInstance().selectedGrievance.getGrievanceStatus());
        String type = Helper.getInstance().getGrievanceString(GrievanceDataProvider.getInstance().selectedGrievance.getGrievanceType());
        JSONObject jsonObjectData = new JSONObject();
        try {
            jsonObjectData.put(Constants.KEY_TITLE, "Grievance status updated");
            jsonObjectData.put(Constants.KEY_TEXT, "Your grievance for " + type + " is " + newStatus);
            jsonObjectData.put("pensionerCode", textViewPensionerCode.getText());
            jsonObjectData.put("grievanceType", GrievanceDataProvider.getInstance().selectedGrievance.getGrievanceType());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObjectData.toString();
    }

    @Override
    public void onBackPressed() {
        if (fabMenu != null && fabMenu.isShowing()) {
            fabMenu.closeMenu();
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public void onError(VolleyError volleyError) {

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
                        //updateGrievanceDataOnFirebase();
                    }
                } else {
                    Helper.getInstance().showErrorDialog("Files could not be uploaded\nTry Again", "Submission Error", this);
                    Log.d(TAG, "onResponse: Image = " + counterServerImages + " failed");
                    progressDialog.dismiss();
                }
            } else if (jsonObject.getString("action").equals("Sending Mail")) {
                if (jsonObject.get("result").equals(Helper.getInstance().SUCCESS)) {
                    progressDialog.dismiss();
                    showSuccessDialog();
                } else {
                    progressDialog.dismiss();
                    Helper.getInstance().showErrorDialog("Grievance Submission Failed<br>Try Again", "Submission Error", this);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: " + requestCode + " ," + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (imagePicker != null)
            imagePicker.onActivityResult(this, requestCode, resultCode, data);
    }

}