package com.mycca.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.solver.widgets.Snapshot;
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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;
import com.mycca.R;
import com.mycca.activity.MainActivity;
import com.mycca.adapter.GenericSpinnerAdapter;
import com.mycca.adapter.RecyclerViewAdapterSelectedImages;
import com.mycca.custom.FabRevealMenu.FabListeners.OnFABMenuSelectedListener;
import com.mycca.custom.FabRevealMenu.FabModel.FABMenuItem;
import com.mycca.custom.FabRevealMenu.FabView.FABRevealMenu;
import com.mycca.custom.FancyAlertDialog.FancyAlertDialogType;
import com.mycca.custom.Progress.ProgressDialog;
import com.mycca.custom.customImagePicker.ImagePicker;
import com.mycca.custom.customImagePicker.cropper.CropImage;
import com.mycca.custom.customImagePicker.cropper.CropImageView;
import com.mycca.listeners.OnConnectionAvailableListener;
import com.mycca.models.GrievanceModel;
import com.mycca.models.GrievanceType;
import com.mycca.models.SelectedImageModel;
import com.mycca.models.State;
import com.mycca.tools.ConnectionUtility;
import com.mycca.tools.CustomLogger;
import com.mycca.tools.DataSubmissionAndMail;
import com.mycca.tools.FireBaseHelper;
import com.mycca.tools.Helper;
import com.mycca.tools.VolleyHelper;

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
    String code, type, email, refNo, prefix;

    MainActivity mainActivity;
    GrievanceType grievanceType;
    GrievanceModel grievanceModel;
    State state;
    VolleyHelper volleyHelper;
    GrievanceType[] list;
    ArrayList<Uri> firebaseImageURLs;
    private ArrayList<FABMenuItem> items;
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
        progressDialog = Helper.getInstance().getProgressWindow(mainActivity, getString(R.string.please_wait));
        initItems();

        inputPensionerCode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_person_black_24dp, 0, 0, 0);
        inputEmail.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_email_black_24dp, 0, 0, 0);
        inputMobile.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_phone_android_black_24dp, 0, 0, 0);
        inputDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_detail, 0, 0, 0);

        GenericSpinnerAdapter<State> statesAdapter = new GenericSpinnerAdapter<>(getContext(), Helper.getInstance().getStateListJK());
        spinnerCircle.setAdapter(statesAdapter);

        if (type.equals(getString(R.string.pension))) {
            list = Helper.getInstance().getPensionGrievanceTypeList();
            radioLayout.setVisibility(View.GONE);
            prefix = "PEN";
        } else {
            radioLayout.setVisibility(View.VISIBLE);
            list = Helper.getInstance().getGPFGrievanceTypeList();
            prefix = "GPF";
        }
        GenericSpinnerAdapter<GrievanceType> adapter = new GenericSpinnerAdapter<>(getContext(), list);
        spinnerInputType.setAdapter(adapter);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radioButtonPensioner:
                    hint = getString(R.string.p_code);
                    inputPensionerCode.setFilters(Helper.getInstance().limitInputLength(15));
                    break;
                case R.id.radioButtonHR:
                    hint = getString(R.string.hr_num);
                    inputPensionerCode.setFilters(new InputFilter[]{});
                    break;
                case R.id.radioButtonStaff:
                    hint = getString(R.string.staff_num);
                    inputPensionerCode.setFilters(new InputFilter[]{});
            }
            inputPensionerCode.setText("");
            inputPensionerCode.setError(null);
            textInputIdentifier.setHint(hint);
        });

        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(mainActivity, R.layout.simple_spinner, submittedByList(type));
        spinnerInputSubmittedBy.setAdapter(arrayAdapter1);


        final FABRevealMenu fabMenu = view.findViewById(R.id.fabMenu_submit_grievance);
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
        //        new Handler().post(() -> {
//
//            menuClearForm = view.findViewById(R.id.action_clear_form_data);
//            // SOME OF YOUR TASK AFTER GETTING VIEW REFERENCE
//            if (Preferences.getInstance().getBooleanPref(getContext(), Preferences.PREF_HELP_GRIEVANCE)) {
//                showTutorial();
//                Preferences.getInstance().setBooleanPref(getContext(), Preferences.PREF_HELP_GRIEVANCE, false);
//            }
//        });

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
                CustomLogger.getInstance().logDebug( "onCropImage: Item inserted at " + currentPosition);
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
                CustomLogger.getInstance().logDebug( "onPermissionDenied: Permission not given to choose textViewMessage");
            }
        });

    }

    private String[] submittedByList(String type) {
        String first;
        if (type.equals(getString(R.string.pension)))
            first = getString(R.string.pensioner);
        else
            first = getString(R.string.gpf_benificiary);
        return new String[]{first, getString(R.string.other)};
    }

    public void setSelectedFileCount(int count) {
        textViewSelectedFileCount.setText(String.format(getString(R.string.files_selected), String.valueOf(count)));
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

        if (code.length() != 15 && hint.equals(getString(R.string.p_code))) {
            inputPensionerCode.setError(getString(R.string.invalid_p_code));
            inputPensionerCode.requestFocus();
            return false;
        } else if (code.trim().isEmpty() && hint.equals(getString(R.string.hr_num))) {
            inputPensionerCode.setError(getString(R.string.invalid_hr_num));
            inputPensionerCode.requestFocus();
            return false;
        } else if (code.trim().isEmpty() && hint.equals(getString(R.string.staff_num))) {
            inputPensionerCode.setError(getString(R.string.invalid_staff_num));
            inputPensionerCode.requestFocus();
            return false;
        } else if (inputMobile.getText().toString().length() < 10) {
            inputMobile.setError(getString(R.string.invalid_mobile));
            inputMobile.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError(getString(R.string.invalid_email));
            inputEmail.requestFocus();
            return false;
        } else if (inputDetails.getText().toString().trim().isEmpty()) {
            inputDetails.setError(getString(R.string.empty_detail));
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
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
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
                Helper.getInstance().showErrorDialog(
                        getString(R.string.connect_to_internet),
                        getString(R.string.no_internet),
                        mainActivity);
            }
        });
        connectionUtility.checkConnectionAvailability();
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
            uploadDataToFirebase();
        }
    }

    private void uploadDataToFirebase() {
        progressDialog.setMessage(getString(R.string.processing));

        Transaction.Handler handler = new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                CustomLogger.getInstance().logDebug( "doTransaction: " + mutableData.getValue());
                long count = 0;
                if (mutableData.getValue() != null) {
                    count = (long) mutableData.getValue();
                }

                // Set value and report transaction success
                mutableData.setValue(++count);
                CustomLogger.getInstance().logDebug( "count= " + count);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if (b) {
                    refNo = prefix + "-" + state.getCircleCode() + "-" + dataSnapshot.getValue();
                    uploadData();
                } else {
                    progressDialog.dismiss();
                    CustomLogger.getInstance().logDebug( "database error: " +
                            "Details" + databaseError.getDetails() +
                            "Message = " + databaseError.getMessage() +
                            "Code = " + databaseError.getCode() +
                            "Sapshot = " + dataSnapshot.toString());
                     ;
                    Helper.getInstance().showMaintenanceDialog(mainActivity);
                }
            }

        };
        FireBaseHelper.getInstance(mainActivity).getReferenceNumber(handler, state.getCircleCode());
    }

    private void uploadData() {
        grievanceModel = new GrievanceModel(code,
                grievanceType.getId(),
                refNo,
                inputDetails.getText().toString().trim(),
                email,
                inputMobile.getText().toString(),
                spinnerInputSubmittedBy.getSelectedItem().toString(),
                0, state.getCircleCode(),
                FireBaseHelper.getInstance(getContext()).mAuth.getCurrentUser().getUid(),
                new Date());

        Task<Void> task = FireBaseHelper.getInstance(getContext()).uploadDataToFirebase(
                grievanceModel,
                FireBaseHelper.ROOT_GRIEVANCES,
                grievanceModel.getState(),
                grievanceModel.getPensionerIdentifier(),
                String.valueOf(grievanceType.getId()));

        task.addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                uploadAllImagesToFirebase();
            } else  {
                progressDialog.dismiss();
                Helper.getInstance().showMaintenanceDialog(mainActivity);
                Log.d(TAG, "uploadData: Failed Message= " + task1.getException().getMessage());
                Log.d(TAG, "uploadData: Failed Cause= " + task1.getException().getCause());
                Log.d(TAG, "uploadData: Failed Stack= " + task1.getException().getStackTrace());
                Log.d(TAG, "uploadData: Failed Result= " + task1.getResult());
            }
        });

    }

    private void uploadAllImagesToFirebase() {
        if (selectedImageModelArrayList.size() > 0) {

            progressDialog.setMessage(getString(R.string.uploading_files));
            counterFirebaseImages = 0;
            counterUpload = 0;

            for (SelectedImageModel imageModel : selectedImageModelArrayList) {
                final UploadTask uploadTask = FireBaseHelper.getInstance(getContext()).uploadFiles(
                        imageModel,
                        true,
                        counterFirebaseImages++,
                        FireBaseHelper.ROOT_GRIEVANCES,
                        code,
                        String.valueOf(grievanceType.getId()),
                        FireBaseHelper.ROOT_BY_USER);

                if (uploadTask != null) {
                    uploadTask.addOnFailureListener(
                            exception -> {
                                Helper.getInstance().showErrorDialog(getString(R.string.file_not_uploaded), type, mainActivity);
                                CustomLogger.getInstance().logDebug( "onFailure: " + exception.getMessage());
                                progressDialog.dismiss();
                            })
                            .addOnSuccessListener(
                                    taskSnapshot -> {
                                        // Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                                            firebaseImageURLs.add(uri);
                                            progressDialog.setMessage(String.format(getString(R.string.uploaded_file), String.valueOf(++counterUpload), String.valueOf(selectedImageModelArrayList.size())));
                                            CustomLogger.getInstance().logDebug( "onSuccess: counter = " + counterUpload + "size = " + selectedImageModelArrayList.size());
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
        progressDialog.setMessage(getString(R.string.processing));
        int totalFilesToAttach = selectedImageModelArrayList.size();
        String url = Helper.getInstance().getAPIUrl() + "uploadImage.php/";

        if (totalFilesToAttach != 0) {
            try {
                DataSubmissionAndMail.getInstance().uploadImagesToServer(url,
                        firebaseImageURLs,
                        code,
                        DataSubmissionAndMail.SUBMIT,
                        volleyHelper);
            } catch (Exception e) {
                e.printStackTrace();
                Helper.getInstance().showErrorDialog(getString(R.string.try_again), getString(R.string.some_error), mainActivity);
            }
        } else {
            isUploadedToServer = true;
            doSubmission();
        }
    }

    private void sendFinalMail() {

        progressDialog.setMessage(getString(R.string.almost_done));
        String url = Helper.getInstance().getAPIUrl() + "sendGrievanceEmail.php/";
        Map<String, String> params = new HashMap<>();
        String pensionerCode = inputPensionerCode.getText().toString();

        params.put("pensionerCode", pensionerCode);
        params.put("personType", hint);
        params.put("folder", DataSubmissionAndMail.SUBMIT);
        params.put("pensionerMobileNumber", inputMobile.getText().toString());
        params.put("pensionerEmail", inputEmail.getText().toString());
        params.put("grievanceType", type);
        params.put("refNo", refNo);
        params.put("grievanceSubType", Helper.getInstance().getGrievanceString(((GrievanceType) spinnerInputType.getSelectedItem()).getId()));
        params.put("grievanceDetails", inputDetails.getText().toString());
        params.put("grievanceSubmittedBy", spinnerInputSubmittedBy.getSelectedItem().toString());
        params.put("fileCount", selectedImageModelArrayList.size() + "");

        DataSubmissionAndMail.getInstance().sendMail(params, "send_mail-" + pensionerCode, volleyHelper, url);
    }

    private void setSubmissionSuccessForGrievance() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("submissionSuccess", true);
        FireBaseHelper.getInstance(mainActivity).updateData(
                String.valueOf(grievanceType.getId()),
                hashMap,
                FireBaseHelper.ROOT_GRIEVANCES,
                grievanceModel.getState(),
                grievanceModel.getPensionerIdentifier()
        );

        FireBaseHelper.getInstance(mainActivity).uploadDataToFirebase(grievanceModel.getPensionerIdentifier(),
                FireBaseHelper.ROOT_REF_NUMBERS,
                refNo);
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
        items.add(new FABMenuItem(0,getString(R.string.add_image), AppCompatResources.getDrawable(mainActivity, R.drawable.ic_attach_file_white_24dp)));
        items.add(new FABMenuItem(1,getString(R.string.remove_all), AppCompatResources.getDrawable(mainActivity, R.drawable.ic_close_24dp)));
    }

    @Override
    public void onResponse(String str) {
        JSONObject jsonObject = Helper.getInstance().getJson(str);
        CustomLogger.getInstance().logDebug( jsonObject.toString());
        try {
            if (jsonObject.get("action").equals("Creating Image")) {
                counterServerImages++;
                if (jsonObject.get("result").equals(Helper.getInstance().SUCCESS)) {
                    if (counterServerImages == selectedImageModelArrayList.size()) {
                        CustomLogger.getInstance().logDebug( "onResponse: Files uploaded");
                        isUploadedToServer = true;
                        doSubmission();
                    }
                } else {
                    progressDialog.dismiss();
                    Helper.getInstance().showErrorDialog(getString(R.string.file_not_uploaded), type, mainActivity);
                    CustomLogger.getInstance().logDebug( "onResponse: Image = " + counterServerImages + " failed");
                }
            } else if (jsonObject.getString("action").equals("Sending Mail")) {
                if (jsonObject.get("result").equals(Helper.getInstance().SUCCESS)) {

                    progressDialog.dismiss();
                    Helper.getInstance().showFancyAlertDialog(mainActivity,
                            String.format(getString(R.string.grievance_submission_success), type, grievanceType.getName(), refNo),
                            type,
                            getString(R.string.ok), () -> {
                            },
                            null, null,
                            FancyAlertDialogType.SUCCESS);
                    isUploadedToServer = isUploadedToFirebase = false;
                    setSubmissionSuccessForGrievance();
                } else {
                    progressDialog.dismiss();
                    Helper.getInstance().showErrorDialog(getString(R.string.grievance_submission_fail), type, mainActivity);

                }
            }
        } catch (JSONException jse) {
            jse.printStackTrace();
            Helper.getInstance().showErrorDialog(getString(R.string.try_again), getString(R.string.some_error), mainActivity);
            progressDialog.dismiss();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        CustomLogger.getInstance().logDebug( "onActivityResult: " + requestCode + " ," + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (imagePicker != null)
            imagePicker.onActivityResult(mainActivity, requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        CustomLogger.getInstance().logDebug( "onRequestPermissionsResult: " + "Inspection");

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
        Helper.getInstance().showErrorDialog(getString(R.string.try_again), getString(R.string.some_error), mainActivity);
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
//                .focusOn(view.findViewById(R.id.button_attach))
//                .focusShape(FocusShape.CIRCLE)
//                .title("Add images using this button")
//                .fitSystemWindows(true)
//                .build();
//
//        final FancyShowCaseView fancyShowCaseView2 = new FancyShowCaseView.Builder(mainActivity)
//                .focusOn(menuClearForm)
//                .focusShape(FocusShape.CIRCLE)
//                .focusCircleRadiusFactor(2)
//                .title("Click to clear form data")
//                .build();
//
//        mainActivity.setmQueue(new FancyShowCaseQueue()
//                .add(fancyShowCaseView1)
//                .add(fancyShowCaseView2));
//        mainActivity.getmQueue().setCompleteListener(() -> mainActivity.setmQueue(null));
//        mainActivity.getmQueue().show();
//    }