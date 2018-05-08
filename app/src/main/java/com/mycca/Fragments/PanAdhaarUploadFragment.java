package com.mycca.Fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.content.res.AppCompatResources;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.mycca.CustomObjects.FancyAlertDialog.FancyAlertDialogType;
import com.mycca.CustomObjects.Progress.ProgressDialog;
import com.mycca.Listeners.OnConnectionAvailableListener;
import com.mycca.Models.PanAdhaar;
import com.mycca.Models.SelectedImageModel;
import com.mycca.R;
import com.mycca.Tools.ConnectionUtility;
import com.mycca.Tools.DataSubmissionAndMail;
import com.mycca.Tools.FireBaseHelper;
import com.mycca.Tools.Helper;
import com.mycca.Tools.PopUpWindows;
import com.mycca.Tools.Preferences;
import com.mycca.Tools.VolleyHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.UploadTask;
import com.linchaolong.android.imagepicker.ImagePicker;
import com.linchaolong.android.imagepicker.cropper.CropImage;
import com.linchaolong.android.imagepicker.cropper.CropImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class PanAdhaarUploadFragment extends Fragment implements VolleyHelper.VolleyResponse {

    ImageView imagePensionerCode, imageNumber, imageviewSelectedImage;
    TextView textViewFileName;
    TextInputLayout textInputIdentifier, textInputNumber;
    AutoCompleteTextView inputPCode, inputNumber;
    Button buttonUpload, buttonChooseFile;
    RadioGroup radioGroup;
    LinearLayout linearLayout;
    ProgressDialog progressDialog;

    private static final String TAG = "PanAdhaarUpload";
    String pensionerCode, number, fileChosedPath, root;
    SelectedImageModel imageModel;
    String hint = "Pensioner Code";
    ImagePicker imagePicker;
    DatabaseReference dbref;

    boolean isUploadedToFirebase = false, isUploadedToServer = false;
    ArrayList<Uri> firebaseImageURLs;
    VolleyHelper volleyHelper;

    public PanAdhaarUploadFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_adhaar_pan_upload, container, false);
        root = this.getArguments().getString("Root");
        bindViews(view);
        init();
        return view;
    }

    private void bindViews(View view) {
        linearLayout = view.findViewById(R.id.layout_radio_group);
        imagePensionerCode = view.findViewById(R.id.image_pcode);
        imageNumber = view.findViewById(R.id.image_number);
        radioGroup = view.findViewById(R.id.groupNumberType);
        textInputIdentifier = view.findViewById(R.id.text_input_pensioner_code);
        textInputNumber = view.findViewById(R.id.text_number);
        inputPCode = view.findViewById(R.id.autocomplete_pcode);
        inputNumber = view.findViewById(R.id.autocomplete_number);
        textViewFileName = view.findViewById(R.id.textview_filename);
        imageviewSelectedImage = view.findViewById(R.id.imageview_selected_image);
        buttonChooseFile = view.findViewById(R.id.button_attach);
        buttonUpload = view.findViewById(R.id.button_upload);
    }

    private void init() {
        progressDialog = Helper.getInstance().getProgressWindow(getActivity(), "Please Wait...");
        volleyHelper = new VolleyHelper(this,getContext());
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
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
            }
        });

        if (root.equals(FireBaseHelper.getInstance().ROOT_ADHAAR)) {

            inputNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
            inputNumber.setFilters(Helper.getInstance().limitInputLength(12));
            textInputNumber.setHint(root + " Number");
        } else if (root.equals(FireBaseHelper.getInstance().ROOT_PAN)) {

            inputNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10), new InputFilter() {
                @Override
                public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                    if (source.equals("")) { // for backspace
                        return source;
                    }
                    if (source.toString().matches("[a-zA-Z0-9]+")) {
                        return source;
                    }
                    return "";
                }
            }});
            textInputNumber.setHint(root + " Number");
        } else {
            linearLayout.setVisibility(View.GONE);
            textInputNumber.setHint("Applicant's Name");
        }

        imagePensionerCode.setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.ic_person_black_24dp));
        imageNumber.setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.ic_card_black_24dp));

        buttonChooseFile.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_attach_file_black_24dp, 0, 0, 0);
        buttonChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInputBeforeSubmission())
                    showConfirmSubmissionDialog();
            }
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
                File file = new File(imageUri.getPath());
                Picasso.with(getContext()).load(imageUri).into(imageviewSelectedImage);
                imageModel = new SelectedImageModel(imageUri);
                textViewFileName.setError(null);
                textViewFileName.setText(root + ".jpg");
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

    private boolean checkInputBeforeSubmission() {
        pensionerCode = inputPCode.getText().toString();
        number = inputNumber.getText().toString();
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
        else if ((root == FireBaseHelper.getInstance().ROOT_ADHAAR) && (number.length() != 12)) {
            inputNumber.setError("Invalid Aadhaar Number");
            inputNumber.requestFocus();
            return false;
        }
        //If PAN Number is not complete
        else if ((root == FireBaseHelper.getInstance().ROOT_PAN) && (number.length() != 10)) {
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
            textViewFileName.setText("NO FILE SELECTED");
            return false;
        }
        return true;
    }

    private void showConfirmSubmissionDialog() {
        Helper.getInstance().hideKeyboardFrom(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_confirm_submission, null);
        loadValues(v);
        PopUpWindows.getInstance().getConfirmationDialog(getActivity(), v,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        uploadAdhaarOrPan();
                        doSubmission();
                    }
                });
    }

    private void doSubmission() {
        ConnectionUtility connectionUtility = new ConnectionUtility(new OnConnectionAvailableListener() {
            @Override
            public void OnConnectionAvailable() {
                doSubmissionOnInternetAvailable();
            }

            @Override
            public void OnConnectionNotAvailable() {
                showNoInternetConnectionDialog();
            }
        });
        connectionUtility.checkConnectionAvailability();
    }

    private void showNoInternetConnectionDialog()
    {
        Helper.getInstance().showFancyAlertDialog(this.getActivity(),
                "No Internet Connection\nPlease turn on internet connection before updating " + root,
                "Update " + root,
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
            uploadDataToFirebase();
        }
    }

    private void loadValues(View v) {

        TextView pensionerHeading = v.findViewById(R.id.textview_pensioner_code_confirm);
        pensionerHeading.setText(hint);

        TextView pensionerValue = v.findViewById(R.id.textview_pensioner_code_confirm_value);
        pensionerValue.setText(pensionerCode);

//        TextView pNo = v.findViewById(R.id.textview_pensioner_code_confirm_value);
//        pNo.setText(hint + ": " + pensionerCode);
        TextView heading = v.findViewById(R.id.textview_mobile_no);
        TextView value = v.findViewById(R.id.textview_mobile_value);
        value.setText(inputNumber.getText());
        if (root.equals(FireBaseHelper.getInstance().ROOT_PAN) || root.equals(FireBaseHelper.getInstance().ROOT_ADHAAR))
            heading.setText(root + " No:");
        else
            heading.setText("Applicant's Name: ");
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
        PanAdhaar panAdhaar = new PanAdhaar(pensionerCode,
                number,
                null,
                Preferences.getInstance().getStringPref(getContext(), Preferences.PREF_STATE));

        Task task = FireBaseHelper.getInstance().uploadDataToFirebase(root, panAdhaar,getContext());

        task.addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    uploadAllImagesToFirebase();
                    //Toast.makeText(getActivity(), "Grievance Submitted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Unable to submit\nPlease Try Again", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void uploadAllImagesToFirebase() {
        UploadTask uploadTask = FireBaseHelper.getInstance().uploadFiles(
                imageModel,
                false,
                0,
                root,
                Preferences.getInstance().getStringPref(getContext(), Preferences.PREF_STATE),
                pensionerCode);

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
                    isUploadedToFirebase = true;
                    Log.d(TAG, "onSuccess: " + downloadUrl);
                    doSubmission();
                }
            });
        }
    }


    private void uploadImagesToServer() {

        progressDialog.setMessage("Processing..");
        progressDialog.show();


        try {
            DataSubmissionAndMail.getInstance().uploadImagesToServer(firebaseImageURLs,
                    pensionerCode,
                    volleyHelper);
        } catch (Exception e) {
            e.printStackTrace();
            Helper.getInstance().showFancyAlertDialog(
                    getActivity(),
                    "Error 1\nPlease report this issue through feedback section", root, "OK", null, null, null, FancyAlertDialogType.ERROR);
        }
    }


    private void sendFinalMail() {


        progressDialog.setMessage("Almost Done..");
        progressDialog.show();
        String url = Helper.getInstance().getAPIUrl() + "sendInfoUpdateEmail.php";
        Map<String, String> params = new HashMap();

        params.put("pensionerCode", pensionerCode);
        params.put("personType", hint);
        params.put("updateType",root);
        params.put("fieldName",textInputNumber.getHint().toString());
        params.put("value",inputNumber.getText().toString());

        DataSubmissionAndMail.getInstance().sendMail(params, "send_mail-" + pensionerCode, volleyHelper,url);
//        if (volleyHelper.countRequestsInFlight("send_mail-" + pensionerCode) == 0)
//            volleyHelper.makeStringRequest(url, "send_mail-" + pensionerCode, params);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: " + requestCode + " ," + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (imagePicker != null)
            imagePicker.onActivityResult(this.getActivity(), requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: " + "Inspection");

        switch (requestCode) {
            default: {
                if (imagePicker != null)
                    imagePicker.onRequestPermissionsResult(this.getActivity(), requestCode, permissions, grantResults);
            }

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
                if (jsonObject.get("result").equals(Helper.getInstance().SUCCESS)) {
                        Log.d(TAG, "onResponse: Files uploaded");
                        isUploadedToServer = true;
                        doSubmission();
                } else {
                    Log.d(TAG, "onResponse: Image upload failed");
                    progressDialog.dismiss();
                }
            } else if (jsonObject.getString("action").equals("Sending Mail")) {
                if (jsonObject.get("result").equals(Helper.getInstance().SUCCESS)) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), root + " Update " + root + " Request Submitted Succesfully", Toast.LENGTH_SHORT).show();
                    isUploadedToServer = isUploadedToFirebase = false;
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Grievance Submission Failed\nTry Again", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (JSONException jse) {
            jse.printStackTrace();
        }
    }
}