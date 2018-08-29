package com.mycca.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.UploadTask;
import com.mycca.R;
import com.mycca.adapter.GenericSpinnerAdapter;
import com.mycca.custom.FancyAlertDialog.FancyAlertDialogType;
import com.mycca.custom.Progress.ProgressDialog;
import com.mycca.custom.customImagePicker.ImagePicker;
import com.mycca.custom.customImagePicker.cropper.CropImage;
import com.mycca.custom.customImagePicker.cropper.CropImageView;
import com.mycca.models.Circle;
import com.mycca.models.SelectedImageModel;
import com.mycca.providers.CircleDataProvider;
import com.mycca.tools.CustomLogger;
import com.mycca.tools.DataSubmissionAndMail;
import com.mycca.tools.FireBaseHelper;
import com.mycca.tools.Helper;
import com.mycca.tools.VolleyHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class KypUploadActivity extends AppCompatActivity implements VolleyHelper.VolleyResponse {

    ImageView imageView;
    SelectedImageModel imageModel;
    Button add, submit;
    Spinner circles;
    ImagePicker imagePicker;
    ProgressDialog progressDialog;
    VolleyHelper volleyHelper;
    ArrayList<Uri> firebaseImageURLs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kyp_upload);
        bindViews();
        init();
    }

    private void bindViews() {
        imageView = findViewById(R.id.imageView_kyp);
        add = findViewById(R.id.add_kyp);
        submit = findViewById(R.id.submit_kyp);
        circles = findViewById(R.id.spinner_kyp_circle);
    }

    private void init() {
        progressDialog = new ProgressDialog(this);
        volleyHelper = new VolleyHelper(this, this);

        GenericSpinnerAdapter<Circle> statesAdapter = new GenericSpinnerAdapter<>(this,
                CircleDataProvider.getInstance().getActiveCircleData());
        circles.setAdapter(statesAdapter);

        add.setOnClickListener(v -> showImageChooser());

        submit.setOnClickListener(v -> {
            if (imageModel != null)
                uploadImageOnFirebase();
            else
                Toast.makeText(this, getString(R.string.add_kyp_image), Toast.LENGTH_LONG).show();
        });
    }

    public void otp() {
       // OTPManager otpManager = new OTPManager(mainActivity, mobile, this::doSubmissionOnInternetAvailable);
        //otpManager.sendSMS();
    }

    private void uploadImageOnFirebase() {
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
        Circle circle = (Circle) circles.getSelectedItem();
        UploadTask uploadTask = FireBaseHelper.getInstance().uploadFiles(circle.getCode(),
                imageModel,
                false,
                0,
                FireBaseHelper.ROOT_KYP,
                FireBaseHelper.getInstance().getAuth().getUid());

        if (uploadTask != null) {
            uploadTask.addOnFailureListener(exception -> {
                progressDialog.dismiss();
                Helper.getInstance().showErrorDialog(getString(R.string.file_not_uploaded), getString(R.string.file_upload_error), this);
                CustomLogger.getInstance().logDebug("onFailure: " + exception.getMessage());
            }).addOnSuccessListener(taskSnapshot ->
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(this::uploadImageToServer));
        }
    }

    private void uploadImageToServer(Uri uri) {

        firebaseImageURLs.add(uri);
        DataSubmissionAndMail.getInstance().uploadImagesToServer(firebaseImageURLs,
                FireBaseHelper.getInstance().getAuth().getUid(),
                DataSubmissionAndMail.SUBMIT,
                volleyHelper);
    }

    private void sendMail() {

    }

    private void showImageChooser() {
        imagePicker = Helper.getInstance().showImageChooser(imagePicker, this, true, new ImagePicker.Callback() {
            @Override
            public void onPickImage(Uri imageUri) {
                CustomLogger.getInstance().logDebug("onPickImage: " + imageUri.getPath());

            }

            @Override
            public void onCropImage(Uri imageUri) {
                CustomLogger.getInstance().logDebug("onCropImage: " + imageUri.getPath());
                imageModel = new SelectedImageModel(imageUri);
                Glide.with(KypUploadActivity.this).load(imageUri).into(imageView);
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
                CustomLogger.getInstance().logDebug("onPermissionDenied: Permission not given to choose textViewMessage");
            }
        });

    }

    @Override
    public void onError(VolleyError volleyError) {
        Helper.getInstance().showErrorDialog(getString(R.string.try_again), getString(R.string.some_error), this);
    }

    @Override
    public void onResponse(String str) {
        JSONObject jsonObject = Helper.getInstance().getJson(str);
        CustomLogger.getInstance().logDebug(jsonObject.toString());
        try {
            if (jsonObject.get("action").equals("Creating Image")) {
                if (jsonObject.get("result").equals(volleyHelper.SUCCESS)) {
                    CustomLogger.getInstance().logDebug("onResponse: Files uploaded");
                    sendMail();
                } else {
                    CustomLogger.getInstance().logDebug("onResponse: Image upload failed");
                    progressDialog.dismiss();
                    Helper.getInstance().showErrorDialog(getString(R.string.file_not_uploaded), getString(R.string.file_upload_error), this);
                }
            } else if (jsonObject.getString("action").equals("Sending Mail")) {
                progressDialog.dismiss();
                if (jsonObject.get("result").equals(volleyHelper.SUCCESS)) {
                    Helper.getInstance().showMessage(this,
                            getString(R.string.kyp_upload_success),
                            getString(R.string.success),
                            FancyAlertDialogType.SUCCESS);

                } else {
                    Helper.getInstance().showErrorDialog(getString(R.string.kyp_upload_fail),
                            getString(R.string.failure),
                            this);
                }
            }
        } catch (JSONException jse) {
            jse.printStackTrace();
            Helper.getInstance().showErrorDialog(getString(R.string.some_error), getString(R.string.try_again), this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (imagePicker != null)
            imagePicker.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (imagePicker != null)
            imagePicker.onRequestPermissionsResult(this, requestCode, permissions, grantResults);

    }
}
