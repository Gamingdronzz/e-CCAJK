package com.ccajk.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
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
import com.ccajk.Adapter.GrievanceAdapter;
import com.ccajk.Adapter.RecyclerViewAdapterSelectedImages;
import com.ccajk.CustomObjects.ProgressDialog;
import com.ccajk.Listeners.OnConnectionAvailableListener;
import com.ccajk.Models.GrievanceModel;
import com.ccajk.Models.GrievanceType;
import com.ccajk.Models.SelectedImageModel;
import com.ccajk.R;
import com.ccajk.Tools.ConnectionUtility;
import com.ccajk.Tools.FireBaseHelper;
import com.ccajk.Tools.Helper;
import com.ccajk.Tools.PopUpWindows;
import com.ccajk.Tools.Preferences;
import com.ccajk.Tools.VolleyHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.UploadTask;
import com.linchaolong.android.imagepicker.ImagePicker;
import com.linchaolong.android.imagepicker.cropper.CropImage;
import com.linchaolong.android.imagepicker.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class GrievanceFragment extends Fragment implements VolleyHelper.VolleyResponse {
    AutoCompleteTextView autoCompleteTextViewPensionerCode, inputEmail;
    AutoCompleteTextView inputMobile;
    TextInputLayout textInputIdentifier;
    RadioGroup radioGroup;
    EditText inputDetails;
    Spinner inputType, inputSubmittedBy;
    Button submit, buttonChooseFile;
    //ImageButton buttonRemove;
    TextView removeAll;
    LinearLayout radioLayout;
    ProgressDialog progressDialog;
    ImagePicker imagePicker;
    TextView textViewSelectedFileCount;

    ArrayList<GrievanceType> list = new ArrayList<>();
    String TAG = "GrievanceModel";
    String hint = "Pensioner Code";
    String code, type;
    int counterFirebase;
    GrievanceType grievanceType;
    int counterServerImages = 0;

//    ImageView imagePensionerCode;
//    ImageView imageMobile, imageEmail;
//    ImageView imageDetails;
//    ImageView imageType;
//    ImageView imageSubmittedBy;
    //ImageView imageviewSelectedImage;

    RecyclerView recyclerViewSelectedImages;
    RecyclerViewAdapterSelectedImages adapterSelectedImages;
    ArrayList<SelectedImageModel> selectedImageModelArrayList;
    VolleyHelper volleyHelper;

    public GrievanceFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grievance, container, false);
        Bundle bundle = this.getArguments();
        type = bundle.getString("Type");
        bindViews(view);
        init();
        //Helper.getInstance().showGuide(getContext(), buttonChooseFile, "Add File Button", "Click this button to attach images to your grievance\nYou can select multiple files as well");
        return view;
    }

    private void bindViews(View view) {
        //imagePensionerCode = view.findViewById(R.id.image_pcode);
        //imageMobile = view.findViewById(R.id.image_mobile);
        //imageEmail = view.findViewById(R.id.image_email);
        //imageDetails = view.findViewById(R.id.image_details);
        //imageType = view.findViewById(R.id.image_type);
        //imageSubmittedBy = view.findViewById(R.id.image_submitted_by);
        //imageviewSelectedImage = view.findViewById(R.id.imageview_selected_image);

        volleyHelper = new VolleyHelper(this, getContext());
        recyclerViewSelectedImages = view.findViewById(R.id.recycler_view_selected_images);

        radioLayout = view.findViewById(R.id.layout_radio);
        textInputIdentifier = view.findViewById(R.id.text_input_pensioner_code);
        autoCompleteTextViewPensionerCode = view.findViewById(R.id.autocomplete_pcode);
        inputMobile = view.findViewById(R.id.autocomplete_mobile);
        inputEmail = view.findViewById(R.id.autocomplete_email);
        inputType = view.findViewById(R.id.spinner_type);
        inputDetails = view.findViewById(R.id.edittext_details);
        inputSubmittedBy = view.findViewById(R.id.spinner_submitted_by);
        textViewSelectedFileCount = view.findViewById(R.id.textview_selected_file_count_grievance);

        //textViewFileName = view.findViewById(R.id.textview_file_name);

        radioGroup = view.findViewById(R.id.groupNumberType);
        buttonChooseFile = view.findViewById(R.id.button_attach);
        removeAll = view.findViewById(R.id.imageButton_removeAllFiles);
        submit = view.findViewById(R.id.button_submit);
    }

    private void init() {

        progressDialog = Helper.getInstance().getProgressWindow(getActivity(), "Please wait...");
        //imagePensionerCode.setImageDrawable(AppCompatResources.getDrawable(this.getContext(), R.drawable.ic_person_black_24dp));

//        imageMobile.setImageDrawable(AppCompatResources.getDrawable(this.getContext(), R.drawable.ic_phone_android_black_24dp));
//        imageEmail.setImageDrawable(AppCompatResources.getDrawable(this.getContext(), R.drawable.ic_email_black_24dp));
//        imageDetails.setImageDrawable(AppCompatResources.getDrawable(this.getContext(), R.drawable.ic_details_black_24dp));
//        imageType.setImageDrawable(AppCompatResources.getDrawable(this.getContext(), R.drawable.ic_sentiment_dissatisfied_black_24dp));
//        imageSubmittedBy.setImageDrawable(AppCompatResources.getDrawable(this.getContext(), R.drawable.ic_person_black_24dp));

        autoCompleteTextViewPensionerCode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_person_black_24dp, 0, 0, 0);
        inputEmail.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_email_black_24dp, 0, 0, 0);
        ;
        inputMobile.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_phone_android_black_24dp, 0, 0, 0);
        ;
        inputDetails.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_detail, 0, 0, 0);
        removeAll.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close_black_24dp, 0, 0, 0);

        textViewSelectedFileCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_attach_file_black_24dp, 0, 0, 0);

        if (type.equals(FireBaseHelper.getInstance().GRIEVANCE_PENSION)) {
            list = Helper.getInstance().getPensionGrievanceTypelist();
            radioLayout.setVisibility(View.GONE);
        } else {
            radioLayout.setVisibility(View.VISIBLE);
            list = Helper.getInstance().getGPFGrievanceTypelist();
        }

        GrievanceAdapter adapter = new GrievanceAdapter(getContext(), list);
        inputType.setAdapter(adapter);

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
                        //autoCompleteTextViewPensionerCode.setFilters(Helper.getInstance().limitInputLength(10));
                        break;
                    case R.id.radioButtonStaff:
                        hint = "Staff Number";
                        //autoCompleteTextViewPensionerCode.setFilters(Helper.getInstance().limitInputLength(12));
                }
                autoCompleteTextViewPensionerCode.setText("");
                autoCompleteTextViewPensionerCode.setError(null);
                textInputIdentifier.setHint(hint);
            }
        });

        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(getContext(), R.layout.simple_spinner, Helper.getInstance().submittedByList(type));
        inputSubmittedBy.setAdapter(arrayAdapter1);


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
                if (checkInput())
                    confirmSubmission();
            }
        });

        //removeSelectedFile();

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

    }

    private void removeAllSelectedImages() {
        if (selectedImageModelArrayList == null || adapterSelectedImages == null) {
            return;
        }
        selectedImageModelArrayList.clear();
        adapterSelectedImages.notifyDataSetChanged();
    }

    private void showImageChooser() {
        imagePicker = Helper.getInstance().showImageChooser(imagePicker, getActivity(), true, new ImagePicker.Callback() {
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
                Log.d(TAG, "onPermissionDenied: Permission not given to choose message");
            }
        });

    }

    public void setSelectedFileCount(int count) {
        textViewSelectedFileCount.setText(" = " + count);
    }

    private boolean checkInput() {
        code = autoCompleteTextViewPensionerCode.getText().toString();
        String email = inputEmail.getText().toString();
        grievanceType = (GrievanceType) inputType.getSelectedItem();

        if (code.length() < 15 && hint.equals("Pensioner Code")) {
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

    private void confirmSubmission() {
        Helper.getInstance().hideKeyboardFrom(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_confirm_submission, null);
        loadValues(v);
        PopUpWindows.getInstance().getConfirmationDialog(getActivity(), v,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        submitGrievanceToFirebase();
                    }
                });
    }

    private void loadValues(View v) {
        TextView ppoNo = v.findViewById(R.id.textview_ppo_no);
        ppoNo.setText(hint + ": " + code);
        TextView mobNo = v.findViewById(R.id.textview_mobile_no);
        mobNo.setText(mobNo.getText() + " " + inputMobile.getText());
        TextView email = v.findViewById(R.id.textview_email);
        email.setText(email.getText() + " " + inputEmail.getText());
        TextView grievance = v.findViewById(R.id.textview_grievance_type);
        grievance.setText(grievance.getText() + " " + grievanceType.getName());
        TextView gr_by = v.findViewById(R.id.textview_grievance_by);
        gr_by.setText(gr_by.getText() + " " + inputSubmittedBy.getSelectedItem());
        TextView details = v.findViewById(R.id.textview_grievance_details);
        details.setText(inputDetails.getText().toString().trim());
    }


    private void submitGrievanceToFirebase() {
        progressDialog.show();
        final DatabaseReference dbref;
        dbref = FireBaseHelper.getInstance().databaseReference.child(FireBaseHelper.getInstance().ROOT_GRIEVANCES);

        final GrievanceModel grievanceModel = new GrievanceModel(
                code,
                inputMobile.getText().toString(),
                grievanceType.getId(),
                inputDetails.getText().toString().trim(),
                inputSubmittedBy.getSelectedItem().toString(),
                inputEmail.getText().toString(),
                null,
                Preferences.getInstance().getStringPref(getContext(), Preferences.PREF_STATE),
                0, new Date());

        dbref.child(code).child(String.valueOf(grievanceType.getId())).setValue(grievanceModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    sendMailToServer();
                    //Toast.makeText(getActivity(), "Grievance Submitted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Unable to submit", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }

            }
        });
    }

    private void uploadAllImagesToFirebase() {
        if (selectedImageModelArrayList.size() > 0) {
            progressDialog.setMessage("Uploading images");
            progressDialog.show();

            counterFirebase = 0;
            for (SelectedImageModel imageModel : selectedImageModelArrayList) {
                final UploadTask uploadTask = FireBaseHelper.getInstance().uploadFiles(
                        imageModel,
                        true,
                        counterFirebase++,
                        FireBaseHelper.getInstance().ROOT_GRIEVANCES,
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
                                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                            Log.d(TAG, "onSuccess: " + downloadUrl);
                                            progressDialog.setMessage("Uploaded file " + counterFirebase + " / " + selectedImageModelArrayList.size());
                                            Log.d(TAG, "onSuccess: counter = " + counterFirebase + "size = " + selectedImageModelArrayList.size());
                                        }
                                    }
                            );
                }
            }
        } else {
            sendFinalMail();
        }
    }

    private void sendMailToServer() {
        progressDialog.setMessage("Sending Mail to Office\nPlease Wait..");
        ConnectionUtility connectionUtility = new ConnectionUtility(new OnConnectionAvailableListener() {
            @Override
            public void OnConnectionAvailable() {
                uploadAllImagesToServer();
            }

            @Override
            public void OnConnectionNotAvailable() {
                progressDialog.dismiss();
                Helper.getInstance().showAlertDialog(
                        getContext(),
                        "Internet Connection Not Available\n\nPlease Try Again",
                        "CCA JK",
                        "OK");
            }
        });
        connectionUtility.checkConnectionAvailability();

    }

    private void uploadAllImagesToServer() {
        counterServerImages = 0;
        progressDialog.show();
        String url = Helper.getInstance().getAPIUrl() + "uploadImage.php";
        int selectedFileCount = selectedImageModelArrayList.size();

        if (selectedFileCount != 0) {
            for (int i = 0; i < selectedImageModelArrayList.size(); i++) {
                try {
                    Log.d(TAG, "uploadAllImagesToServer: " + i);
                    SelectedImageModel selectedImageModel = selectedImageModelArrayList.get(i);
                    String image = Base64.encodeToString(Helper.getInstance().getByteArrayFromBitmapFile(selectedImageModel.getImageURI().getPath()), Base64.DEFAULT);
                    Map<String, String> params = new HashMap();
                    params.put("pensionerCode", autoCompleteTextViewPensionerCode.getText().toString());
                    params.put("image", image);
                    params.put("imageName", "image-" + i);
                    params.put("imageCount", i + "");
                    if (volleyHelper.countRequestsInFlight("upload_image-" + i) == 0)
                        volleyHelper.makeStringRequest(url, "upload_image-" + i, params);
                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
            }
        } else {
            sendFinalMail();
        }


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
        volleyError.printStackTrace();
        progressDialog.dismiss();
        Toast.makeText(getContext(), "Some Error Occured\nPlease be patient we are getting things fixed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(String str) {
        JSONObject jsonObject = Helper.getInstance().getJson(str);
        Log.d(TAG, jsonObject.toString());
        try {
            if (jsonObject.get("action").equals("Creating Image")) {
                if (jsonObject.get("result").equals(Helper.getInstance().SUCCESS)) {
                    counterServerImages++;
                    if (counterFirebase == selectedImageModelArrayList.size()) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onResponse: Files uploaded");
                        uploadAllImagesToFirebase();
                        sendFinalMail();
                    }
                }
            } else if (jsonObject.getString("action").equals("Sending Mail")) {
                if (jsonObject.get("result").equals(Helper.getInstance().SUCCESS)) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Grievance Submitted Succesfully", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (JSONException jse) {
            jse.printStackTrace();
        }
    }

    private void sendFinalMail() {
        String url = Helper.getInstance().getAPIUrl() + "sendMail.php";
        Map<String, String> params = new HashMap();
        String pensionerCode = autoCompleteTextViewPensionerCode.getText().toString();
        params.put("pensionerCode", pensionerCode);
        params.put("pensionerEmail", inputEmail.getText().toString());
        params.put("message", inputDetails.getText().toString());
        params.put("fileCount", selectedImageModelArrayList.size() + "");
        if (volleyHelper.countRequestsInFlight("send_mail-" + pensionerCode) == 0)
            volleyHelper.makeStringRequest(url, "send_mail-" + pensionerCode, params);
    }
}