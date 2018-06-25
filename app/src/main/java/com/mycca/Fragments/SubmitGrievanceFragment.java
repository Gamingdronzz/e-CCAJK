package com.mycca.Fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;
import com.mycca.Activity.MainActivity;
import com.mycca.Adapter.GrievanceAdapter;
import com.mycca.Adapter.RecyclerViewAdapterSelectedImages;
import com.mycca.Adapter.StatesAdapter;
import com.mycca.CustomObjects.CustomImagePicker.Cropper.CropImage;
import com.mycca.CustomObjects.CustomImagePicker.Cropper.CropImageView;
import com.mycca.CustomObjects.CustomImagePicker.ImagePicker;
import com.mycca.CustomObjects.FancyAlertDialog.FancyAlertDialogType;
import com.mycca.CustomObjects.FancyShowCase.FancyShowCaseQueue;
import com.mycca.CustomObjects.FancyShowCase.FancyShowCaseView;
import com.mycca.CustomObjects.FancyShowCase.FocusShape;
import com.mycca.CustomObjects.Progress.ProgressDialog;
import com.mycca.CustomObjects.FabRevealMenu.FabListeners.OnFABMenuSelectedListener;
import com.mycca.CustomObjects.FabRevealMenu.FabModel.FABMenuItem;
import com.mycca.CustomObjects.FabRevealMenu.FabView.FABRevealMenu;
import com.mycca.Listeners.OnConnectionAvailableListener;
import com.mycca.Models.GrievanceModel;
import com.mycca.Models.GrievanceType;
import com.mycca.Models.SelectedImageModel;
import com.mycca.Models.State;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class SubmitGrievanceFragment extends Fragment implements VolleyHelper.VolleyResponse, OnFABMenuSelectedListener {

    View view, menuClearForm;
    TextInputEditText inputPensionerCode, inputEmail, inputMobile, inputDetails;
    TextInputLayout textInputIdentifier;
    RadioGroup radioGroup;
    Spinner spinnerInputType, spinnerInputSubmittedBy, spinnerCircle;
    Button submit;
    FloatingActionButton buttonChooseFile;
    TextView textViewSelectedFileCount;
    LinearLayout radioLayout;
    ProgressDialog progressDialog;
    ImagePicker imagePicker;

    boolean isUploadedToFirebase = false, isUploadedToServer = false;
    int counterUpload = 0;
    int counterServerImages = 0;
    int counterFirebaseImages;
    String TAG = "Grievance";
    String hint = "Pensioner Code";
    String code, type, email, refNo;

    MainActivity mainActivity;
    GrievanceType grievanceType;
    State state;
    VolleyHelper volleyHelper;
    ArrayList<GrievanceType> list = new ArrayList<>();
    ArrayList<Uri> firebaseImageURLs;
    private ArrayList<FABMenuItem> items;
    Uri downloadUrl;
    ArrayList<SelectedImageModel> selectedImageModelArrayList;
    RecyclerView recyclerViewSelectedImages;
    RecyclerViewAdapterSelectedImages adapterSelectedImages;

    public SubmitGrievanceFragment() {

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_grievance, container, false);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            type = bundle.getString("Type");
        }
        bindViews();
        setHasOptionsMenu(true);
        init();
        return view;
    }


    private void bindViews() {

        volleyHelper = new VolleyHelper(this, getContext());
        recyclerViewSelectedImages = view.findViewById(R.id.recycler_view_selected_images);

        radioLayout = view.findViewById(R.id.layout_radio);
        textInputIdentifier = view.findViewById(R.id.text_input_pensioner_code);
        inputPensionerCode = view.findViewById(R.id.et_grievance_pcode);
        inputMobile = view.findViewById(R.id.et_grievance_mobile);
        inputEmail = view.findViewById(R.id.et_grievance_email);
        spinnerCircle = view.findViewById(R.id.spinner_grievance_circle);
        spinnerInputType = view.findViewById(R.id.spinner_type);
        inputDetails = view.findViewById(R.id.et_grievance_details);
        spinnerInputSubmittedBy = view.findViewById(R.id.spinner_submitted_by);
        textViewSelectedFileCount = view.findViewById(R.id.textview_selected_file_count_grievance);

        radioGroup = view.findViewById(R.id.groupNumberType);
        buttonChooseFile = view.findViewById(R.id.button_attach);
        submit = view.findViewById(R.id.button_submit);
    }

    private void init() {

        mainActivity = (MainActivity) getActivity();
        progressDialog = Helper.getInstance().getProgressWindow(mainActivity, "Please wait...");
        initItems();

        inputPensionerCode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_person_black_24dp, 0, 0, 0);
        inputEmail.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_email_black_24dp, 0, 0, 0);
        inputMobile.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_phone_android_black_24dp, 0, 0, 0);
        inputDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_detail, 0, 0, 0);

        StatesAdapter statesAdapter = new StatesAdapter(getContext());
        spinnerCircle.setAdapter(statesAdapter);

        if (type.equals(FireBaseHelper.GRIEVANCE_PENSION)) {
            list = Helper.getInstance().getPensionGrievanceTypelist();
            radioLayout.setVisibility(View.GONE);
        } else {
            radioLayout.setVisibility(View.VISIBLE);
            list = Helper.getInstance().getGPFGrievanceTypelist();
        }
        GrievanceAdapter adapter = new GrievanceAdapter(getContext(), list);
        spinnerInputType.setAdapter(adapter);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radioButtonPensioner:
                    hint = "Pensioner Code";
                    inputPensionerCode.setFilters(Helper.getInstance().limitInputLength(15));
                    break;
                case R.id.radioButtonHR:
                    hint = "HR Number";
                    inputPensionerCode.setFilters(new InputFilter[]{});
                    break;
                case R.id.radioButtonStaff:
                    hint = "Staff Number";
                    inputPensionerCode.setFilters(new InputFilter[]{});
            }
            inputPensionerCode.setText("");
            inputPensionerCode.setError(null);
            textInputIdentifier.setHint(hint);
        });

        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(mainActivity, R.layout.simple_spinner, Helper.getInstance().submittedByList(type));
        spinnerInputSubmittedBy.setAdapter(arrayAdapter1);


        final FABRevealMenu fabMenu = view.findViewById(R.id.fabMenu);
        try {
            if (buttonChooseFile != null && fabMenu != null) {
                //attach menu to fab
                mainActivity.setFabRevealMenu(fabMenu);
                //set menu items from arraylist
                fabMenu.setMenuItems(items);
                //attach menu to fab
                fabMenu.bindAnchorView(buttonChooseFile);
                //set menu item selection
                fabMenu.setOnFABMenuSelectedListener(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        submit.setOnClickListener(v -> {
            if (checkInputBeforeSubmission())
                showConfirmSubmissionDialog();
        });

        selectedImageModelArrayList = new ArrayList<>();
        adapterSelectedImages = new RecyclerViewAdapterSelectedImages(selectedImageModelArrayList, this);
        recyclerViewSelectedImages.setAdapter(adapterSelectedImages);
        recyclerViewSelectedImages.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        firebaseImageURLs = new ArrayList<>();
        setSelectedFileCount(0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear_form_data:
                clearFormData();
                break;
            default:
                break;
        }

        return true;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_form, menu);
        new Handler().post(() -> {

            menuClearForm = view.findViewById(R.id.action_clear_form_data);
            // SOME OF YOUR TASK AFTER GETTING VIEW REFERENCE
            if (Preferences.getInstance().getBooleanPref(getContext(), Preferences.PREF_HELP_GRIEVANCE)) {
                showTutorial();
                Preferences.getInstance().setBooleanPref(getContext(), Preferences.PREF_HELP_GRIEVANCE, false);
            }
        });

    }


    private void showTutorial() {

        final FancyShowCaseView fancyShowCaseView1 = new FancyShowCaseView.Builder(mainActivity)
                .focusOn(view.findViewById(R.id.button_attach))
                .focusShape(FocusShape.CIRCLE)
                .title("Add images using this button")
                .fitSystemWindows(true)
                .build();

        final FancyShowCaseView fancyShowCaseView2 = new FancyShowCaseView.Builder(mainActivity)
                .focusOn(menuClearForm)
                .focusShape(FocusShape.CIRCLE)
                .focusCircleRadiusFactor(2)
                .title("Click to clear form data")
                .build();

        mainActivity.setmQueue(new FancyShowCaseQueue()
                .add(fancyShowCaseView1)
                .add(fancyShowCaseView2));
        mainActivity.getmQueue().setCompleteListener(() -> mainActivity.setmQueue(null));
        mainActivity.getmQueue().show();
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

    public void setSelectedFileCount(int count) {
        String text = count + " Files Selected";
        textViewSelectedFileCount.setText(text);
    }

    private void removeAllSelectedImages() {
        if (selectedImageModelArrayList == null || adapterSelectedImages == null) {
            return;
        }
        selectedImageModelArrayList.clear();
        adapterSelectedImages.notifyDataSetChanged();
        setSelectedFileCount(0);
    }

    private boolean checkInputBeforeSubmission() {
        code = inputPensionerCode.getText().toString();
        email = inputEmail.getText().toString();
        state = (State) spinnerCircle.getSelectedItem();
        grievanceType = (GrievanceType) spinnerInputType.getSelectedItem();

        if (code.length() != 15 && hint.equals("Pensioner Code")) {
            inputPensionerCode.setError("Enter Valid Pensioner Code");
            inputPensionerCode.requestFocus();
            return false;
        } else if (code.trim().isEmpty() && hint.equals("HR Number")) {
            inputPensionerCode.setError("Enter Valid HR Number");
            inputPensionerCode.requestFocus();
            return false;
        } else if (code.trim().isEmpty() && hint.equals("Staff Number")) {
            inputPensionerCode.setError("Enter Valid Staff Number");
            inputPensionerCode.requestFocus();
            return false;
        } else if (inputMobile.getText().toString().length() < 10) {
            inputMobile.setError("Enter Valid Mobile No");
            inputMobile.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("Enter Valid Email");
            inputEmail.requestFocus();
            return false;
        } else if (inputDetails.getText().toString().trim().isEmpty()) {
            inputDetails.setError("Add details");
            inputDetails.requestFocus();
            return false;
        }
        return true;
    }

    private void showConfirmSubmissionDialog() {
        Helper.getInstance().hideKeyboardFrom(mainActivity);
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_confirm_submission, null);
        loadValues(v);
        Helper.getInstance().getConfirmationDialog(mainActivity, v,
                (dialog, which) -> doSubmission());
    }

    private void loadValues(View v) {
        TextView pensionerHeading = v.findViewById(R.id.textview_pensioner_code_confirm);
        pensionerHeading.setText(hint);

        TextView pensionerValue = v.findViewById(R.id.textview_pensioner_code_confirm_value);
        pensionerValue.setText(code);

        TextView mobNo = v.findViewById(R.id.textview_mobile_value);
        mobNo.setText(inputMobile.getText());
        TextView emailValue = v.findViewById(R.id.textview_email_value);
        emailValue.setText(email);
        TextView circle = v.findViewById(R.id.textview_circle_value);
        circle.setText(state.getName());
        TextView grievance = v.findViewById(R.id.textview_grievance_value);
        grievance.setText(grievanceType.getName());
        TextView gr_by = v.findViewById(R.id.textview_submitted_by_value);
        gr_by.setText(spinnerInputSubmittedBy.getSelectedItem().toString());
        TextView details = v.findViewById(R.id.textview_grievance_details);
        details.setText(inputDetails.getText().toString().trim());
    }

    private void doSubmission() {
        progressDialog.setMessage("Please Wait..");
        progressDialog.show();
        ConnectionUtility connectionUtility = new ConnectionUtility(new OnConnectionAvailableListener() {
            @Override
            public void OnConnectionAvailable() {
                Log.d(TAG, "version checked =" + Helper.versionChecked);
                if (!Helper.versionChecked) {
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
                progressDialog.dismiss();
                Helper.getInstance().showErrorDialog(
                        "No Internet Connection\nPlease turn on internet connection before submitting " + type + " Grievance",
                        "Submit Grievance",
                        mainActivity);
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
            uploadDataToFirebase();
        }
    }

    private void uploadDataToFirebase() {
        progressDialog.setMessage("Preparing your grievance for submission");
        progressDialog.show();
        // final DatabaseReference dbref;
        //   dbref = FireBaseHelper.getInstance(getContext()).databaseReference.child(FireBaseHelper.getInstance(getContext()).ROOT_GRIEVANCES);
        refNo = getReferenceNumber();

        GrievanceModel grievanceModel = new GrievanceModel(code,
                grievanceType.getId(),
                refNo,
                inputDetails.getText().toString().trim(),
                email,
                inputMobile.getText().toString(),
                spinnerInputSubmittedBy.getSelectedItem().toString(),
                0, state.getCircleCode(),
                FireBaseHelper.getInstance(getContext()).mAuth.getCurrentUser().getUid(),
                new Date());

        try {

            Task<Void> task = FireBaseHelper.getInstance(getContext()).uploadDataToFirebase(
                    grievanceModel,
                    FireBaseHelper.ROOT_GRIEVANCES,
                    grievanceModel.getState(),
                    grievanceModel.getPensionerIdentifier(),
                    String.valueOf(grievanceModel.getGrievanceType()));

            task.addOnCompleteListener(task1 -> {
                if (task1.isSuccessful()) {
                    uploadAllImagesToFirebase();
                } else {
                    progressDialog.dismiss();
                    Helper.getInstance().showUpdateOrMaintenanceDialog(false, mainActivity);
                }
            });
        } catch (DatabaseException dbe) {
            dbe.printStackTrace();
        }
    }

    private String getReferenceNumber() {
        return "12345";
    }

    private void uploadAllImagesToFirebase() {
        if (selectedImageModelArrayList.size() > 0) {

            progressDialog.setMessage("Uploading Files..\nBe patient");
            progressDialog.show();
            counterFirebaseImages = 0;
            counterUpload = 0;

            for (SelectedImageModel imageModel : selectedImageModelArrayList) {
                final UploadTask uploadTask = FireBaseHelper.getInstance(getContext()).uploadFiles(
                        imageModel,
                        true,
                        counterFirebaseImages++,
                        FireBaseHelper.ROOT_GRIEVANCES,
                        state.getCircleCode(),
                        code,
                        String.valueOf(grievanceType.getId()));

                if (uploadTask != null) {
                    uploadTask.addOnFailureListener(
                            exception -> {
                                Helper.getInstance().showErrorDialog("Files could not be uploaded\nTry Again", "Submission Error", mainActivity);
                                Log.d(TAG, "onFailure: " + exception.getMessage());
                                progressDialog.dismiss();
                            })
                            .addOnSuccessListener(
                                    taskSnapshot -> {
                                        // Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                                            downloadUrl = uri;
                                            Log.d(TAG, "onSuccess: " + downloadUrl);
                                            firebaseImageURLs.add(downloadUrl);
                                            progressDialog.setMessage("Uploaded file " + (++counterUpload) + " / " + selectedImageModelArrayList.size());
                                            Log.d(TAG, "onSuccess: counter = " + counterUpload + "size = " + selectedImageModelArrayList.size());
                                            if (counterUpload == selectedImageModelArrayList.size()) {
                                                isUploadedToFirebase = true;
                                                doSubmission();
                                            }
                                        });
                                    });
                }
            }
        } else {
            isUploadedToFirebase = true;
            doSubmission();
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
                        code,
                        volleyHelper);
            } catch (Exception e) {
                e.printStackTrace();
                Helper.getInstance().showErrorDialog("Some Error Occured", "Error", mainActivity);
            }
        } else {
            isUploadedToServer = true;
            doSubmission();
        }
    }

    private void sendFinalMail() {

        progressDialog.setMessage("Almost Done..");
        progressDialog.show();
        String url = Helper.getInstance().getAPIUrl() + "sendGrievanceEmail.php";
        Map<String, String> params = new HashMap<>();
        String pensionerCode = inputPensionerCode.getText().toString();

        params.put("pensionerCode", pensionerCode);
        params.put("personType", hint);
        params.put("pensionerMobileNumber", inputMobile.getText().toString());
        params.put("pensionerEmail", inputEmail.getText().toString());
        params.put("grievanceType", type);
        params.put("grievanceSubType", Helper.getInstance().getGrievanceString(((GrievanceType) spinnerInputType.getSelectedItem()).getId()));
        params.put("grievanceDetails", inputDetails.getText().toString());
        params.put("grievanceSubmittedBy", spinnerInputSubmittedBy.getSelectedItem().toString());
        params.put("fileCount", selectedImageModelArrayList.size() + "");

        DataSubmissionAndMail.getInstance().sendMail(params, "send_mail-" + pensionerCode, volleyHelper, url);
    }

    private void clearFormData() {
        inputPensionerCode.setText("");
        inputMobile.setText("");
        inputEmail.setText("");
        spinnerInputType.setSelection(0);
        spinnerInputSubmittedBy.setSelection(0);
        inputDetails.setText("");
        removeAllSelectedImages();
    }

    private void initItems() {
        items = new ArrayList<>();
        items.add(new FABMenuItem("Add Image", AppCompatResources.getDrawable(mainActivity, R.drawable.ic_attach_file_white_24dp)));
        items.add(new FABMenuItem("Remove All", AppCompatResources.getDrawable(mainActivity, R.drawable.ic_close_24dp)));
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
                    Helper.getInstance().showErrorDialog("Files could not be uploaded\nTry Again", "Submission Error", mainActivity);
                    Log.d(TAG, "onResponse: Image = " + counterServerImages + " failed");
                    progressDialog.dismiss();
                }
            } else if (jsonObject.getString("action").equals("Sending Mail")) {
                if (jsonObject.get("result").equals(Helper.getInstance().SUCCESS)) {
                    progressDialog.dismiss();
                    String alertMessage = type +
                            " Grievance for<br>" +
                            "<b>" + grievanceType.getName() + "</b><br>" +
                            "with Reference Number: <b>" + refNo + "</b><br>"+
                            " has been succesfully submitted";

                    Helper.getInstance().showFancyAlertDialog(mainActivity, alertMessage, "Grievance Submission", "OK", () -> {

                    }, null, null, FancyAlertDialogType.SUCCESS);
                    isUploadedToServer = isUploadedToFirebase = false;
                } else {
                    progressDialog.dismiss();
                    Helper.getInstance().showErrorDialog("Grievance Submission Failed<br>Try Again", "Submission Error", mainActivity);

                }
            }
        } catch (JSONException jse) {
            jse.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: " + requestCode + " ," + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (imagePicker != null)
            imagePicker.onActivityResult(mainActivity, requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: " + "Inspection");

        switch (requestCode) {
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
        Helper.getInstance().showErrorDialog("Some Error Occured Please be patient we are getting things fixed", "Submission Error", mainActivity);
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
