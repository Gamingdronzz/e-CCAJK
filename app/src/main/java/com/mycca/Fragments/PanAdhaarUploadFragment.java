package com.mycca.Fragments;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.content.res.AppCompatResources;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;
import com.mycca.Activity.MainActivity;
import com.mycca.Adapter.GenericSpinnerAdapter;
import com.mycca.CustomObjects.BarCode.BarcodeCaptureActivity;
import com.mycca.CustomObjects.CustomImagePicker.Cropper.CropImage;
import com.mycca.CustomObjects.CustomImagePicker.Cropper.CropImageView;
import com.mycca.CustomObjects.CustomImagePicker.ImagePicker;
import com.mycca.CustomObjects.FabRevealMenu.FabListeners.OnFABMenuSelectedListener;
import com.mycca.CustomObjects.FabRevealMenu.FabModel.FABMenuItem;
import com.mycca.CustomObjects.FabRevealMenu.FabView.FABRevealMenu;
import com.mycca.CustomObjects.FancyAlertDialog.FancyAlertDialogType;
import com.mycca.CustomObjects.Progress.ProgressDialog;
import com.mycca.Listeners.OnConnectionAvailableListener;
import com.mycca.Models.PanAdhaar;
import com.mycca.Models.SelectedImageModel;
import com.mycca.Models.State;
import com.mycca.R;
import com.mycca.Tools.ConnectionUtility;
import com.mycca.Tools.DataSubmissionAndMail;
import com.mycca.Tools.FireBaseHelper;
import com.mycca.Tools.Helper;
import com.mycca.Tools.VolleyHelper;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class PanAdhaarUploadFragment extends Fragment implements VolleyHelper.VolleyResponse, OnFABMenuSelectedListener {

    private static final int RC_BARCODE_CAPTURE = 9001;
    ImageView imageviewSelectedImage;
    TextView textViewFileName;
    Spinner spinnerCircle;
    TextInputLayout textInputIdentifier, textInputNumber;
    TextInputEditText inputPCode, inputNumber;
    Button buttonUpload;
    FloatingActionButton fab;
    RadioGroup radioGroup;
    LinearLayout linearLayout;
    ProgressDialog progressDialog;

    private static final String TAG = "PanAdhaarUpload";
    public static final String AADHAAR_DATA_TAG = "PrintLetterBarcodeData", AADHAR_UID_ATTR = "uid";
    private String pensionerCode, number, root, hint = "Pensioner Code";
    private boolean isUploadedToFirebase = false, isUploadedToServer = false;
    private ArrayList<FABMenuItem> items;
    SelectedImageModel imageModel;
    ImagePicker imagePicker;
    ArrayList<Uri> firebaseImageURLs;
    Uri downloadUrl;
    VolleyHelper volleyHelper;
    State state;
    MainActivity mainActivity;
    private static final int MY_CAMERA_REQUEST_CODE = 300;


    public PanAdhaarUploadFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_adhaar_pan_upload, container, false);
        if (this.getArguments() != null)
            root = this.getArguments().getString("Root");
        bindViews(view);
        init(view);
        return view;
    }

    private void bindViews(View view) {
        linearLayout = view.findViewById(R.id.layout_radio_group);
        radioGroup = view.findViewById(R.id.groupNumberType);
        textInputIdentifier = view.findViewById(R.id.text_input_pensioner_code);
        textInputNumber = view.findViewById(R.id.text_number);
        inputPCode = view.findViewById(R.id.et_pan_adhaar_pcode);
        inputNumber = view.findViewById(R.id.et_pan_adhaar_number);
        spinnerCircle = view.findViewById(R.id.spinner_pan_adhaar_circle);
        textViewFileName = view.findViewById(R.id.textview_filename);
        imageviewSelectedImage = view.findViewById(R.id.imageview_selected_image);
        fab = view.findViewById(R.id.fab_aadhar_pan);
        buttonUpload = view.findViewById(R.id.button_upload);
    }

    private void init(View view) {
        mainActivity = (MainActivity) getActivity();
        progressDialog = Helper.getInstance().getProgressWindow(getActivity(), "Please Wait...");
        volleyHelper = new VolleyHelper(this, getContext());
        initItems();

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radioButtonPensioner:
                    hint = "Pensioner Code";
                    inputPCode.setFilters(Helper.getInstance().limitInputLength(15));
                    break;

                case R.id.radioButtonHR:
                    hint = "HR Number";
                    inputPCode.setFilters(new InputFilter[]{});
                    break;
            }
            inputPCode.setText("");
            inputPCode.setError(null);
            textInputIdentifier.setHint(hint);
        });

        switch (root) {
            case FireBaseHelper.ROOT_ADHAAR:

                inputNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
                inputNumber.setFilters(Helper.getInstance().limitInputLength(12));
                textInputNumber.setHint(root + " Number");
                break;
            case FireBaseHelper.ROOT_PAN:

                inputNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10), (source, start, end, dest, dstart, dend) -> {
                    if (source.equals("")) { // for backspace
                        return source;
                    }
                    if (source.toString().matches("[a-zA-Z0-9]+")) {
                        return source;
                    }
                    return "";
                }});
                textInputNumber.setHint(root + " Number");
                break;
            default:
                linearLayout.setVisibility(View.GONE);
                textInputNumber.setHint("Applicant's Name");
                break;
        }

        inputPCode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_person_black_24dp, 0, 0, 0);
        inputNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_card_black_24dp, 0, 0, 0);

        final FABRevealMenu fabMenu = view.findViewById(R.id.fabMenu_pan_aadhar);
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


        GenericSpinnerAdapter<State> statesAdapter = new GenericSpinnerAdapter<>(getContext(), Helper.getInstance().getStateListJK());
        spinnerCircle.setAdapter(statesAdapter);

        buttonUpload.setOnClickListener(v -> {
            if (checkInputBeforeSubmission())
                showConfirmSubmissionDialog();
        });

        firebaseImageURLs = new ArrayList<>();
    }

    private void showImageChooser() {
        imagePicker = Helper.getInstance().showImageChooser(imagePicker, getActivity(), true, new ImagePicker.Callback() {
            @Override
            public void onPickImage(Uri imageUri) {

            }

            @Override
            public void onCropImage(Uri imageUri) {
                Glide.with(mainActivity).load(imageUri).into(imageviewSelectedImage);
                imageModel = new SelectedImageModel(imageUri);
                textViewFileName.setError(null);
                String text = root + ".jpg";
                textViewFileName.setText(text);
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

    private void initItems() {
        items = new ArrayList<>();

        items.add(new FABMenuItem("Scan Aadhaar Card", AppCompatResources.getDrawable(mainActivity, R.drawable.aadhaar_logo)));
        items.add(new FABMenuItem("Add Image", AppCompatResources.getDrawable(mainActivity, R.drawable.ic_attach_file_white_24dp)));
        items.add(new FABMenuItem("Remove Image", AppCompatResources.getDrawable(mainActivity, R.drawable.ic_close_24dp)));
    }

    private boolean checkInputBeforeSubmission() {
        pensionerCode = inputPCode.getText().toString();
        number = inputNumber.getText().toString();
        state = (State) spinnerCircle.getSelectedItem();
        //If Pensioner code is empty
        if (pensionerCode.trim().length() != 15 && hint.equals("Pensioner Code")) {
            inputPCode.setError("Enter Valid Pensioner Code");
            inputPCode.requestFocus();
            return false;
        } else if (pensionerCode.trim().isEmpty() && hint.equals("HR Number")) {
            inputPCode.setError("Enter Valid HR Code");
            inputPCode.requestFocus();
            return false;
        }
        //If Aadhar Number is not complete
        else if ((root.equals(FireBaseHelper.ROOT_ADHAAR)) && (number.length() != 12)) {
            inputNumber.setError("Invalid Aadhaar Number");
            inputNumber.requestFocus();
            return false;
        }
        //If PAN Number is not complete
        else if ((root.equals(FireBaseHelper.ROOT_PAN)) && (number.length() != 10)) {
            inputNumber.setError("Invalid Pan Number");
            inputNumber.requestFocus();
            return false;
        } else if (number.isEmpty()) {
            inputNumber.setError("Invalid Name");
            inputNumber.requestFocus();
            return false;
        }
        //if no file selected
        else if (imageModel == null) {
            textViewFileName.setError("");
            textViewFileName.setText(getResources().getString(R.string.no_image));
            return false;
        }
        return true;
    }

    private void showConfirmSubmissionDialog() {
        Helper.getInstance().hideKeyboardFrom(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_confirm_submission, null);
        loadValues(v);
        Helper.getInstance().getConfirmationDialog(getActivity(), v,
                (dialog, which) -> doSubmission());
    }

    private void doSubmission() {
        ConnectionUtility connectionUtility = new ConnectionUtility(new OnConnectionAvailableListener() {
            @Override
            public void OnConnectionAvailable() {
                Log.d(TAG, "version checked = " + Helper.versionChecked);
                if (!Helper.versionChecked) {
                    FireBaseHelper.getInstance(getContext()).checkForUpdate(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (Helper.getInstance().onLatestVersion(dataSnapshot, getActivity()))
                                doSubmissionOnInternetAvailable();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Helper.getInstance().showUpdateOrMaintenanceDialog(false, getActivity());
                        }
                    });
                } else
                    doSubmissionOnInternetAvailable();
            }

            @Override
            public void OnConnectionNotAvailable() {
                showNoInternetConnectionDialog();
            }
        });
        connectionUtility.checkConnectionAvailability();
    }

    private void showNoInternetConnectionDialog() {
        Helper.getInstance().showErrorDialog(
                "No Internet Connection\nPlease turn on internet connection before updating " + root,
                "Update " + root,
                getActivity());
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
            uploadDataToFirebase();
        }
    }

    private void loadValues(View v) {

        TextView pensionerHeading = v.findViewById(R.id.textview_pensioner_code_confirm);
        pensionerHeading.setText(hint);

        TextView pensionerValue = v.findViewById(R.id.textview_pensioner_code_confirm_value);
        pensionerValue.setText(pensionerCode);

        TextView circle = v.findViewById(R.id.textview_circle_value);
        circle.setText(state.getName());
        TextView heading = v.findViewById(R.id.textview_mobile_no);
        String text;
        if (root.equals(FireBaseHelper.ROOT_PAN) || root.equals(FireBaseHelper.ROOT_ADHAAR))
            text = root + " No:";
        else
            text = "Applicant's Name: ";
        heading.setText(text);

        TextView value = v.findViewById(R.id.textview_mobile_value);
        value.setText(number);
        v.findViewById(R.id.textview_email).setVisibility(View.GONE);
        v.findViewById(R.id.textview_email_value).setVisibility(View.GONE);
        v.findViewById(R.id.textview_grievance_type).setVisibility(View.GONE);
        v.findViewById(R.id.textview_grievance_value).setVisibility(View.GONE);
        v.findViewById(R.id.textview_grievance_by).setVisibility(View.GONE);
        v.findViewById(R.id.textview_submitted_by_value).setVisibility(View.GONE);
        v.findViewById(R.id.detail).setVisibility(View.GONE);
        v.findViewById(R.id.textview_grievance_details).setVisibility(View.GONE);
    }

    private void uploadDataToFirebase() {
        progressDialog.show();
        PanAdhaar panAadharModel = new PanAdhaar(pensionerCode,
                number,
                null,
                state.getCircleCode());

        Task<Void> task = FireBaseHelper.getInstance(getContext()).uploadDataToFirebase(panAadharModel,
                root,
                panAadharModel.getState(),
                panAadharModel.getPensionerIdentifier());

        task.addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                uploadAllImagesToFirebase();
            } else {
                progressDialog.dismiss();
                Helper.getInstance().showUpdateOrMaintenanceDialog(false, getActivity());
            }
        });
    }

    private void uploadAllImagesToFirebase() {
        UploadTask uploadTask = FireBaseHelper.getInstance(getContext()).uploadFiles(
                imageModel,
                false,
                0,
                root,
                pensionerCode);

        if (uploadTask != null) {
            uploadTask.addOnFailureListener(exception -> {
                Helper.getInstance().showErrorDialog("Unable to Upload file", "Update Error", getActivity());
                Log.d(TAG, "onFailure: " + exception.getMessage());
                progressDialog.dismiss();
            }).addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                downloadUrl = uri;
                Log.d(TAG, "onSuccess: " + downloadUrl);
                firebaseImageURLs.add(downloadUrl);
                isUploadedToFirebase = true;
                doSubmission();
            }));
        }
    }

    private void uploadImagesToServer() {

        progressDialog.setMessage("Processing..");
        String url = Helper.getInstance().getAPIUrl() + "uploadImage.php/";

        try {
            DataSubmissionAndMail.getInstance().uploadImagesToServer(url,
                    firebaseImageURLs,
                    pensionerCode,
                    DataSubmissionAndMail.SUBMIT,
                    volleyHelper);
        } catch (Exception e) {
            e.printStackTrace();
            Helper.getInstance().showErrorDialog("Some Error Occured", "Update Error", getActivity());
        }
    }

    private void sendFinalMail() {

        progressDialog.setMessage("Almost Done..");
        String url = Helper.getInstance().getAPIUrl() + "sendInfoUpdateEmail.php/";
        Map<String, String> params = new HashMap<>();

        params.put("pensionerCode", pensionerCode);
        params.put("folder", DataSubmissionAndMail.SUBMIT);
        params.put("personType", hint);
        params.put("updateType", root);
        params.put("fieldName", textInputNumber.getHint().toString());
        params.put("value", inputNumber.getText().toString());

        DataSubmissionAndMail.getInstance().sendMail(params, "send_mail-" + pensionerCode, volleyHelper, url);
    }

    public void scanNow() {
        // we need to check if the user has granted the camera permissions
        // otherwise scanner will not work
        Log.d(TAG, "scanNow: ");
//        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
//                == PackageManager.PERMISSION_DENIED) {
//            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
//            return;
//        }
//        IntentIntegrator integrator = new IntentIntegrator(getActivity());
//        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
//        integrator.setPrompt("Scan Aadhar card QR Code");
//        integrator.setResultDisplayDuration(500);
//        integrator.setCameraId(0);  // Use a specific camera of the device
//        integrator.initiateScan();

        Intent intent = new Intent(mainActivity, BarcodeCaptureActivity.class);
        intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
        intent.putExtra(BarcodeCaptureActivity.UseFlash, false);

        startActivityForResult(intent, RC_BARCODE_CAPTURE);
    }

    protected void processScannedData(String scanData) {
        Log.d("Aadhar Card Scan", scanData);

        XmlPullParserFactory pullParserFactory;
        try {
            pullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = pullParserFactory.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new StringReader(scanData));

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) {
                    Log.d("AadharPan", "Start document");
                } else if (eventType == XmlPullParser.START_TAG && AADHAAR_DATA_TAG.equals(parser.getName())) {
                    // extract data from tag
                    String uid = parser.getAttributeValue(null, AADHAR_UID_ATTR);
                    inputNumber.setText(uid);

                } else if (eventType == XmlPullParser.END_TAG) {
                    Log.d("AadharPan", "End tag " + parser.getName());

                } else if (eventType == XmlPullParser.TEXT) {
                    Log.d("AadharPan", "Text " + parser.getText());

                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: " + requestCode + " ," + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Log.d(TAG, "Barcode read: " + barcode.displayValue);
                    processScannedData(barcode.displayValue);
                } else {
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {
                Log.d(TAG, String.format(getString(R.string.barcode_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        } else if (imagePicker != null)
            imagePicker.onActivityResult(this.getActivity(), requestCode, resultCode, data);

        //        //retrieve QR Code scan result
//        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//
//        if (scanningResult != null) {
//            //we have a result
//            String scanContent = scanningResult.getContents();
//            String scanFormat = scanningResult.getFormatName();
//
//            // process received data
//            if (scanContent != null && !scanContent.isEmpty()) {
//                processScannedData(scanContent);
//            } else {
//                Toast toast = Toast.makeText(getContext(), "Scan Cancelled", Toast.LENGTH_SHORT);
//                toast.show();
//            }
//
//        } else {
//            Toast toast = Toast.makeText(getContext(), "No scan data received!", Toast.LENGTH_SHORT);
//            toast.show();
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: " + "PanAadhar " + requestCode);

        switch (requestCode) {
            case (MY_CAMERA_REQUEST_CODE): {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    scanNow();
                } else {
                    Log.d(TAG, "onRequestPermissionsResult: Permission Denied");
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
        progressDialog.dismiss();
        Helper.getInstance().showErrorDialog("Some Error Occurred.", "Please try Again", mainActivity);
    }

    @Override
    public void onResponse(String str) {
        JSONObject jsonObject = Helper.getInstance().getJson(str);
        Log.d(TAG, jsonObject.toString());
        try {
            if (jsonObject.get("action").equals("Creating Image")) {
                if (jsonObject.get("result").equals(Helper.getInstance().SUCCESS)) {
                    Log.d(TAG, "onResponse: Files uploaded");
                    isUploadedToServer = true;
                    doSubmission();
                } else {
                    Log.d(TAG, "onResponse: Image upload failed");
                    Helper.getInstance().showErrorDialog("File Uploading Failed", "Update Error", getActivity());
                    progressDialog.dismiss();
                }
            } else if (jsonObject.getString("action").equals("Sending Mail")) {
                if (jsonObject.get("result").equals(Helper.getInstance().SUCCESS)) {
                    progressDialog.dismiss();
                    isUploadedToServer = isUploadedToFirebase = false;
                    String alertMessage = (root + " update request for<br>") +
                            "<b>" + pensionerCode + "</b>" +
                            "<br>has been submitted successfully";
                    Helper.getInstance().showFancyAlertDialog(getActivity(), alertMessage, root
                                    + " Update", "OK", () -> {
                            },
                            null, null, FancyAlertDialogType.SUCCESS);

                } else {
                    progressDialog.dismiss();
                    Helper.getInstance().showErrorDialog(root + " update request failed",
                            "Update Error",
                            getActivity());
                }
            }
        } catch (JSONException jse) {
            jse.printStackTrace();
            Helper.getInstance().showErrorDialog("Please Try Again", "Some Error Occurred", mainActivity);
            progressDialog.dismiss();
        }
    }

    @Override
    public void onMenuItemSelected(View view, int id) {
        switch (items.get(id).getTitle()) {
            case "Scan Aadhaar Card":
                scanNow();
                break;
            case "Add Image":
                showImageChooser();
                break;
            case "Remove Image":
                imageviewSelectedImage.setImageResource(0);
                textViewFileName.setText(getResources().getString(R.string.no_image));
                break;
        }
    }
}