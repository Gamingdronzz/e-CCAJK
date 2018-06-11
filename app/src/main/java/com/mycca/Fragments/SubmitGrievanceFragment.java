package com.mycca.Fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.mycca.CustomObjects.FancyAlertDialog.IFancyAlertDialogListener;
import com.mycca.CustomObjects.FancyShowCase.FancyShowCaseQueue;
import com.mycca.CustomObjects.FancyShowCase.FancyShowCaseView;
import com.mycca.CustomObjects.FancyShowCase.FocusShape;
import com.mycca.CustomObjects.Progress.ProgressDialog;
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


public class SubmitGrievanceFragment extends Fragment implements VolleyHelper.VolleyResponse {

    View view;
    AutoCompleteTextView autoCompleteTextViewPensionerCode, inputEmail, inputMobile;
    TextInputLayout textInputIdentifier;
    RadioGroup radioGroup;
    EditText inputDetails;
    Spinner spinnerInputType, spinnerInputSubmittedBy, spinnerCircle;
    Button submit, buttonChooseFile;
    //FloatingActionButton buttonAttachFile;
    TextView removeAll, textViewSelectedFileCount;
    LinearLayout radioLayout;
    ProgressDialog progressDialog;
    ImagePicker imagePicker;

    boolean isUploadedToFirebase = false, isUploadedToServer = false;
    int counterUpload = 0;
    int counterServerImages = 0;
    int counterFirebaseImages;
    String TAG = "GrievanceModel";
    String hint = "Pensioner Code";
    String code, type, email;
    View menuClearForm;

    MainActivity mainActivity;
    GrievanceType grievanceType;
    State state;
    VolleyHelper volleyHelper;
    ArrayList<GrievanceType> list = new ArrayList<>();
    ArrayList<Uri> firebaseImageURLs;
    Uri downloadUrl;
    ArrayList<SelectedImageModel> selectedImageModelArrayList;
    RecyclerView recyclerViewSelectedImages;
    RecyclerViewAdapterSelectedImages adapterSelectedImages;

    private int RC_IMAGE_PICKER = 106;

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
        bindViews(view);
        setHasOptionsMenu(true);
        init();

        return view;
    }


    private void bindViews(View view) {

        volleyHelper = new VolleyHelper(this, getContext());
        recyclerViewSelectedImages = view.findViewById(R.id.recycler_view_selected_images);

        radioLayout = view.findViewById(R.id.layout_radio);
        textInputIdentifier = view.findViewById(R.id.text_input_pensioner_code);
        autoCompleteTextViewPensionerCode = view.findViewById(R.id.autocomplete_pcode);
        inputMobile = view.findViewById(R.id.autocomplete_mobile);
        inputEmail = view.findViewById(R.id.autocomplete_email);
        spinnerCircle = view.findViewById(R.id.spinner_grievance_circle);
        spinnerInputType = view.findViewById(R.id.spinner_type);
        inputDetails = view.findViewById(R.id.edittext_details);
        spinnerInputSubmittedBy = view.findViewById(R.id.spinner_submitted_by);
        textViewSelectedFileCount = view.findViewById(R.id.textview_selected_file_count_grievance);

        //textViewFileName = view.findViewById(R.id.textview_file_name);

        radioGroup = view.findViewById(R.id.groupNumberType);
        buttonChooseFile = view.findViewById(R.id.button_attach);
        removeAll = view.findViewById(R.id.imageButton_removeAllFiles);
        submit = view.findViewById(R.id.button_submit);
    }

    private void init() {

        mainActivity = (MainActivity) getActivity();
        progressDialog = Helper.getInstance().getProgressWindow(mainActivity, "Please wait...");

        autoCompleteTextViewPensionerCode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_person_black_24dp, 0, 0, 0);
        inputEmail.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_email_black_24dp, 0, 0, 0);
        inputMobile.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_phone_android_black_24dp, 0, 0, 0);
        inputDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_detail, 0, 0, 0);
        removeAll.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close_black_24dp, 0, 0, 0);
        textViewSelectedFileCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_attach_file_black_24dp, 0, 0, 0);

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

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButtonPensioner:
                        hint = "Pensioner Code";
                        autoCompleteTextViewPensionerCode.setFilters(Helper.getInstance().limitInputLength(15));
                        break;
                    //TODO
                    //set place holder format
                    case R.id.radioButtonHR:
                        hint = "HR Number";
                        autoCompleteTextViewPensionerCode.setFilters(new InputFilter[]{});
                        break;
                    case R.id.radioButtonStaff:
                        hint = "Staff Number";
                        autoCompleteTextViewPensionerCode.setFilters(new InputFilter[]{});
                }
                autoCompleteTextViewPensionerCode.setText("");
                autoCompleteTextViewPensionerCode.setError(null);
                textInputIdentifier.setHint(hint);
            }
        });

        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(mainActivity, R.layout.simple_spinner, Helper.getInstance().submittedByList(type));
        spinnerInputSubmittedBy.setAdapter(arrayAdapter1);


        buttonChooseFile.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_attach_file_black_24dp, 0, 0, 0);
        buttonChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInputBeforeSubmission())
                    showConfirmSubmissionDialog();
            }
        });

        selectedImageModelArrayList = new ArrayList<>();
        adapterSelectedImages = new RecyclerViewAdapterSelectedImages(selectedImageModelArrayList, this);
        recyclerViewSelectedImages.setAdapter(adapterSelectedImages);
        recyclerViewSelectedImages.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        removeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAllSelectedImages();
            }
        });

        firebaseImageURLs = new ArrayList<>();

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
        new Handler().post(new Runnable() {
            @Override
            public void run() {

                menuClearForm = view.findViewById(R.id.action_clear_form_data);
                // SOME OF YOUR TASK AFTER GETTING VIEW REFERENCE
                if (Preferences.getInstance().getBooleanPref(getContext(), Preferences.PREF_HELP_GRIEVANCE)) {
                    showTutorial();
                    Preferences.getInstance().setBooleanPref(getContext(), Preferences.PREF_HELP_GRIEVANCE, false);
                }
            }
        });

    }


    private void showTutorial() {

        final FancyShowCaseView fancyShowCaseView1 = new FancyShowCaseView.Builder(mainActivity)
                //.focusCircleAtPosition(Resources.getSystem().getDisplayMetrics().widthPixels - 200, Resources.getSystem().getDisplayMetrics().heightPixels - 250, 100)
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

        mainActivity.mQueue = new FancyShowCaseQueue()
                .add(fancyShowCaseView1)
                .add(fancyShowCaseView2);
        mainActivity.mQueue.setCompleteListener(new com.mycca.CustomObjects.FancyShowCase.OnCompleteListener() {
            @Override
            public void onComplete() {
                mainActivity.mQueue = null;
            }
        });
        mainActivity.mQueue.show();
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
        textViewSelectedFileCount.setText(" = " + count);
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
        code = autoCompleteTextViewPensionerCode.getText().toString();
        email = inputEmail.getText().toString();
        state = (State) spinnerCircle.getSelectedItem();
        grievanceType = (GrievanceType) spinnerInputType.getSelectedItem();

        if (code.length() != 15 && hint.equals("Pensioner Code")) {
            autoCompleteTextViewPensionerCode.setError("Enter Valid Pensioner Code");
            autoCompleteTextViewPensionerCode.requestFocus();
            return false;
        } else if (code.trim().isEmpty() && hint.equals("HR Number")) {
            autoCompleteTextViewPensionerCode.setError("Enter Valid HR Number");
            autoCompleteTextViewPensionerCode.requestFocus();
            return false;
        } else if (code.trim().isEmpty() && hint.equals("Staff Number")) {
            autoCompleteTextViewPensionerCode.setError("Enter Valid Staff Number");
            autoCompleteTextViewPensionerCode.requestFocus();
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
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doSubmission();
                    }
                });
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
                Helper.getInstance().showFancyAlertDialog(mainActivity,
                        "No Internet Connection\nPlease turn on internet connection before submitting " + type + " Grievance",
                        "Submit Grievance",
                        "OK",
                        null,
                        null,
                        null,
                        FancyAlertDialogType.ERROR);

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

        final GrievanceModel grievanceModel = new GrievanceModel(
                FireBaseHelper.getInstance(getContext()).mAuth.getCurrentUser().getUid(),
                code,
                inputMobile.getText().toString(),
                grievanceType.getId(),
                inputDetails.getText().toString().trim(),
                spinnerInputSubmittedBy.getSelectedItem().toString(),
                email, null, state.getCircleCode(), 0, new Date());

        try {

            Task task = FireBaseHelper.getInstance(getContext()).uploadDataToFirebase(
                    FireBaseHelper.ROOT_GRIEVANCES,
                    grievanceModel);

            task.addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        uploadAllImagesToFirebase();
                    } else {
                        progressDialog.dismiss();
                        Helper.getInstance().showUpdateOrMaintenanceDialog(false, mainActivity);
                    }
                }
            });
        } catch (DatabaseException dbe) {
            dbe.printStackTrace();
        }
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
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(getContext(), "Unable to Upload file", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "onFailure: " + exception.getMessage());
                                    progressDialog.dismiss();
                                }
                            })
                            .addOnSuccessListener(
                                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            // Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    downloadUrl = uri;
                                                    Log.d(TAG, "onSuccess: " + downloadUrl);
                                                    firebaseImageURLs.add(downloadUrl);
                                                    progressDialog.setMessage("Uploaded file " + (++counterUpload) + " / " + selectedImageModelArrayList.size());
                                                    Log.d(TAG, "onSuccess: counter = " + counterUpload + "size = " + selectedImageModelArrayList.size());
                                                    if (counterUpload == selectedImageModelArrayList.size()) {
                                                        isUploadedToFirebase = true;
                                                        doSubmission();
                                                    }
                                                }
                                            });
                                        }
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
                Helper.getInstance().showFancyAlertDialog(mainActivity,
                        "Some Error Occured<br>Please try Again",
                        "Track Grievance",
                        "OK",
                        null,
                        null,
                        null,
                        FancyAlertDialogType.ERROR);

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
        Map<String, String> params = new HashMap();
        String pensionerCode = autoCompleteTextViewPensionerCode.getText().toString();

        params.put("pensionerCode", pensionerCode);
        params.put("personType", hint);
        params.put("pensionerMobileNumber", inputMobile.getText().toString());
        params.put("pensionerEmail", inputEmail.getText().toString());
        params.put("grievanceType", type);
        params.put("grievanceSubType", Helper.getInstance().getGrievanceString(spinnerInputType.getSelectedItemPosition()));
        params.put("grievanceDetails", inputDetails.getText().toString());
        params.put("grievanceSubmittedBy", spinnerInputSubmittedBy.getSelectedItem().toString());
        params.put("fileCount", selectedImageModelArrayList.size() + "");

        DataSubmissionAndMail.getInstance().sendMail(params, "send_mail-" + pensionerCode, volleyHelper, url);
    }

    private void clearFormData() {
        autoCompleteTextViewPensionerCode.setText("");
        inputMobile.setText("");
        inputEmail.setText("");
        spinnerInputType.setSelection(0);
        spinnerInputSubmittedBy.setSelection(0);
        inputDetails.setText("");
        removeAllSelectedImages();
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
                    String alertMessage = type +
                            " Grievance for<br>" +
                            "<b>" + grievanceType.getName() + "</b><br>" +
                            " has been succesfully submitted";

                    Helper.getInstance().showFancyAlertDialog(mainActivity, alertMessage, "Grievance Submission", "OK", new IFancyAlertDialogListener() {
                        @Override
                        public void OnClick() {

                        }
                    }, null, null, FancyAlertDialogType.SUCCESS);
                    //Toast.makeText(getContext(), "Grievance Submitted Succesfully", Toast.LENGTH_SHORT).show();
                    isUploadedToServer = isUploadedToFirebase = false;
                } else {
                    progressDialog.dismiss();
                    Helper.getInstance().showFancyAlertDialog(mainActivity, "Grievance Submission Failed<br>Try Again", "Grievance Submission", "OK", new IFancyAlertDialogListener() {
                        @Override
                        public void OnClick() {

                        }
                    }, null, null, FancyAlertDialogType.ERROR);
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

        /*if (resultCode == Activity.RESULT_OK && requestCode == RC_IMAGE_PICKER) {
            ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);

            for (String s :
                    returnValue) {
                Log.d(TAG, "onActivityResult: " + s);
            }
        }*/
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
        Helper.getInstance().showFancyAlertDialog(mainActivity, "Some Error Occured Please be patient we are getting things fixed", "Grievance Submission", "OK", new IFancyAlertDialogListener() {
            @Override
            public void OnClick() {

            }
        }, null, null, FancyAlertDialogType.ERROR);
        //Toast.makeText(getContext(), "Some Error Occured\nPlease be patient we are getting things fixed", Toast.LENGTH_SHORT).show();
    }


}
