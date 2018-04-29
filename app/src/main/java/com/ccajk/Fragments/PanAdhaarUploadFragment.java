package com.ccajk.Fragments;


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

import com.ccajk.CustomObjects.ProgressDialog;
import com.ccajk.Models.PanAdhaar;
import com.ccajk.Models.SelectedImageModel;
import com.ccajk.R;
import com.ccajk.Tools.FireBaseHelper;
import com.ccajk.Tools.Helper;
import com.ccajk.Tools.PopUpWindows;
import com.ccajk.Tools.Preferences;
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

import java.io.File;


public class PanAdhaarUploadFragment extends Fragment {

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

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButtonPensioner:
                        hint = "Pensioner Code";
                        inputPCode.setFilters(Helper.getInstance().limitInputLength(15));
                        break;
                    //TODO
                    //set place holder format
                    case R.id.radioButtonHR:
                        hint = "HR Number";
                        //inputPCode.setFilters(Helper.getInstance().limitInputLength(10));
                        break;
                }
                inputPCode.setText("");
                inputPCode.setError(null);
                textInputIdentifier.setHint(hint);
            }
        });

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
                if (checkInput())
                    confirmSubmission();
            }
        });
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
                Log.d(TAG, "onPermissionDenied: Permission not given to choose message");
            }
        });
    }

    private boolean checkInput() {
        pensionerCode = inputPCode.getText().toString();
        number = inputNumber.getText().toString();
        //If Pensioner code is empty
        if (pensionerCode.trim().length() < 15 && hint.equals("Pensioner Code")) {
            inputPCode.setError("Enter Valid Pensioner Code");
            inputPCode.requestFocus();
            return false;
        } else if (pensionerCode.trim().isEmpty() && hint.equals("HR Number")) {
            inputPCode.setError("Enter Valid HR Code");
            inputPCode.requestFocus();
            return false;
        }
        //If Aadhar Number is not complete
        else if ((root == FireBaseHelper.getInstance().ROOT_ADHAAR) && (number.length() < 12)) {
            inputNumber.setError("Invalid Aadhaar Number");
            inputNumber.requestFocus();
            return false;
        }
        //If PAN Number is not complete
        else if ((root == FireBaseHelper.getInstance().ROOT_PAN) && (number.length() < 10)) {
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

    private void confirmSubmission() {
        Helper.getInstance().hideKeyboardFrom(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_confirm_submission, null);
        loadValues(v);
        PopUpWindows.getInstance().getConfirmationDialog(getActivity(), v,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        uploadAdhaarOrPan();
                    }
                });
    }

    private void loadValues(View v) {
        TextView pNo = v.findViewById(R.id.textview_ppo_no);
        pNo.setText(hint + ": " + pensionerCode);
        TextView mobNo = v.findViewById(R.id.textview_mobile_no);
        if (root.equals(FireBaseHelper.getInstance().ROOT_PAN) || root.equals(FireBaseHelper.getInstance().ROOT_ADHAAR))
            mobNo.setText(root + " No: " + inputNumber.getText());
        else
            mobNo.setText("Applicant's Name: " + inputNumber.getText());
        v.findViewById(R.id.textview_email).setVisibility(View.GONE);
        v.findViewById(R.id.textview_grievance_type).setVisibility(View.GONE);
        v.findViewById(R.id.textview_grievance_by).setVisibility(View.GONE);
        v.findViewById(R.id.detail).setVisibility(View.GONE);
        v.findViewById(R.id.textview_grievance_details).setVisibility(View.GONE);
    }


    private void uploadAdhaarOrPan() {
        progressDialog.show();
        dbref = FireBaseHelper.getInstance().databaseReference.child(root);

        PanAdhaar panAdhaar = new PanAdhaar(pensionerCode,
                number,
                null,
                Preferences.getInstance().getStringPref(getContext(), Preferences.PREF_STATE));

        dbref.child(pensionerCode).setValue(panAdhaar).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    uploadFile();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Unable to Upload", Toast.LENGTH_SHORT).show();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void uploadFile() {
        UploadTask uploadTask = FireBaseHelper.getInstance().uploadFiles(
                imageModel,
                false,
                0,
                root,
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
                    Toast.makeText(getContext(), "Request sent for Upload", Toast.LENGTH_SHORT).show();
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Log.d(TAG, "onSuccess: " + downloadUrl);
                    progressDialog.dismiss();
                }
            });
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
}

 /*statusref.child(imagePensionerCode).addListenerForSingleValueEvent(new ValueEventListener(){
@Override
public void onDataChange(DataSnapshot dataSnapshot){
        counterFirebase=dataSnapshot.getChildrenCount();
        PanAdhaarStatus panAdhaarStatus=new PanAdhaarStatus(new Date(),null,null,0);
        statusref.child(imagePensionerCode).child(String.valueOf(counterFirebase+1)).setValue(panAdhaarStatus).addOnCompleteListener(new OnCompleteListener<Void>(){
@Override
public void onComplete(@NonNull Task<Void> task){
        if(task.isSuccessful()){
        Toast.makeText(PanAdhaarUploadActivity.this,"Success",Toast.LENGTH_SHORT).show();
        }else
        Toast.makeText(PanAdhaarUploadActivity.this,"Failure",Toast.LENGTH_SHORT).show();
        }
        });
        }

@Override
public void onCancelled(DatabaseError databaseError){

        }
        });*/
