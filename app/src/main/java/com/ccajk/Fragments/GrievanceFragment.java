package com.ccajk.Fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ccajk.Adapter.GrievancAdapter;
import com.ccajk.Adapter.RecyclerViewAdapterSelectedImages;
import com.ccajk.CustomObjects.ProgressDialog;
import com.ccajk.Models.Grievance;
import com.ccajk.Models.GrievanceType;
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

import java.util.ArrayList;
import java.util.Date;


public class GrievanceFragment extends Fragment {
    AutoCompleteTextView inputIdentifier, inputMobile, inputEmail;
    TextInputLayout textInputIdentifier;
    RadioGroup radioGroup;
    EditText inputDetails;
    Spinner inputType, inputSubmittedBy;
    Button submit, buttonChooseFile;
    //ImageButton buttonRemove;
    LinearLayout radioLayout;
    ProgressDialog progressDialog;
    ImagePicker imagePicker;
    TextView textViewSelectedFileCount;

    ArrayList<GrievanceType> list = new ArrayList<>();
    String TAG = "Grievance";
    String hint = "Pensioner Code";
    String code, type;
    int count;
    GrievanceType grievanceType;

    ImageView imagePensionerCode;
    ImageView imageMobile, imageEmail;
    ImageView imageDetails;
    ImageView imageType;
    ImageView imageSubmittedBy;
    //ImageView imageviewSelectedImage;

    RecyclerView recyclerViewSelectedImages;
    RecyclerViewAdapterSelectedImages adapterSelectedImages;
    ArrayList<SelectedImageModel> selectedImageModelArrayList;


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
        return view;
    }

    private void bindViews(View view) {
        imagePensionerCode = view.findViewById(R.id.image_pcode);
        imageMobile = view.findViewById(R.id.image_mobile);
        imageEmail = view.findViewById(R.id.image_email);
        imageDetails = view.findViewById(R.id.image_details);
        imageType = view.findViewById(R.id.image_type);
        imageSubmittedBy = view.findViewById(R.id.image_submitted_by);
        //imageviewSelectedImage = view.findViewById(R.id.imageview_selected_image);
        recyclerViewSelectedImages = view.findViewById(R.id.recycler_view_selected_images);

        radioLayout = view.findViewById(R.id.layout_radio);
        textInputIdentifier = view.findViewById(R.id.text_input_code);
        inputIdentifier = view.findViewById(R.id.autocomplete_pcode);
        inputMobile = view.findViewById(R.id.autocomplete_mobile);
        inputEmail = view.findViewById(R.id.autocomplete_email);
        inputType = view.findViewById(R.id.spinner_type);
        inputDetails = view.findViewById(R.id.edittext_details);
        inputSubmittedBy = view.findViewById(R.id.spinner_submitted_by);
        textViewSelectedFileCount = view.findViewById(R.id.textview_selected_file_count_grievance);

        //textViewFileName = view.findViewById(R.id.textview_file_name);

        radioGroup = view.findViewById(R.id.groupNumberType);
        buttonChooseFile = view.findViewById(R.id.button_attach);
        //buttonRemove = view.findViewById(R.id.btn_remove);
        submit = view.findViewById(R.id.button_submit);
    }

    private void init() {

        progressDialog = Helper.getInstance().getProgressWindow(getActivity(), "Please wait...");
        imagePensionerCode.setImageDrawable(AppCompatResources.getDrawable(this.getContext(), R.drawable.ic_person_black_24dp));
        imageMobile.setImageDrawable(AppCompatResources.getDrawable(this.getContext(), R.drawable.ic_phone_android_black_24dp));
        imageEmail.setImageDrawable(AppCompatResources.getDrawable(this.getContext(), R.drawable.ic_email_black_24dp));
        imageDetails.setImageDrawable(AppCompatResources.getDrawable(this.getContext(), R.drawable.ic_details_black_24dp));
        imageType.setImageDrawable(AppCompatResources.getDrawable(this.getContext(), R.drawable.ic_sentiment_dissatisfied_black_24dp));
        imageSubmittedBy.setImageDrawable(AppCompatResources.getDrawable(this.getContext(), R.drawable.ic_person_black_24dp));

        if (type.equals(FireBaseHelper.getInstance().GRIEVANCE_PENSION)) {
            list = Helper.getInstance().getPensionGrievanceTypelist();
            radioLayout.setVisibility(View.GONE);
        } else {
            radioLayout.setVisibility(View.VISIBLE);
            list = Helper.getInstance().getGPFGrievanceTypelist();
        }

        GrievancAdapter adapter = new GrievancAdapter(getContext(), list);
        inputType.setAdapter(adapter);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButtonPensioner:
                        hint = "Pensioner Code";
                        inputIdentifier.setFilters(Helper.getInstance().limitInputLength(15));
                        break;
                    //TODO
                    //set place holder format
                    case R.id.radioButtonHR:
                        hint = "HR Number";
                        inputIdentifier.setFilters(Helper.getInstance().limitInputLength(10));
                        break;
                    case R.id.radioButtonStaff:
                        hint = "Staff Number";
                        inputIdentifier.setFilters(Helper.getInstance().limitInputLength(12));
                }
                inputIdentifier.setText("");
                inputIdentifier.setError(null);
                textInputIdentifier.setHint(hint);
            }
        });

        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, Helper.getInstance().submittedByList(type));
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
        recyclerViewSelectedImages.setLayoutManager(new GridLayoutManager(getContext(), 4));

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
        textViewSelectedFileCount.setText("Selected Files = " + count);
    }

    private boolean checkInput() {
        code = inputIdentifier.getText().toString();
        String email = inputEmail.getText().toString();
        grievanceType = (GrievanceType) inputType.getSelectedItem();

        if (code.length() < 15 && hint.equals("Pensioner Code")) {
            inputIdentifier.setError("Enter Valid Pensioner Code");
            inputIdentifier.requestFocus();
            return false;
        } else if (code.length() < 10 && hint.equals("HR Number")) {
            inputIdentifier.setError("Enter Valid HR Number");
            inputIdentifier.requestFocus();
            return false;
        } else if (code.length() < 12 && hint.equals("Staff Number")) {
            inputIdentifier.setError("Enter Valid Staff Number");
            inputIdentifier.requestFocus();
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
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_confirm_submission, null);
        loadValues(v);
        PopUpWindows.getInstance().getConfirmationDialog(getActivity(), v,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        submitGrievance();
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


    private void submitGrievance() {
        progressDialog.show();
        final DatabaseReference dbref;
        dbref = FireBaseHelper.getInstance().databaseReference.child(FireBaseHelper.getInstance().ROOT_GRIEVANCES);

        final Grievance grievance = new Grievance(
                code,
                inputMobile.getText().toString(),
                grievanceType.getId(),
                inputDetails.getText().toString().trim(),
                inputSubmittedBy.getSelectedItem().toString(),
                inputEmail.getText().toString(),
                null,
                Preferences.getInstance().getPrefState(getContext()),
                0, new Date());

        dbref.child(code).child(String.valueOf(grievanceType.getId())).setValue(grievance).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (selectedImageModelArrayList.size() != 0) {
                        uploadFile();
                    } else {
                        Toast.makeText(getActivity(), "Grievance Submitted", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                } else {
                    Toast.makeText(getActivity(), "Unable to submit", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }

            }
        });
    }

    private void uploadFile() {
        UploadTask uploadTask;
        count = 0;
        for (SelectedImageModel imageModel : selectedImageModelArrayList) {
            uploadTask = FireBaseHelper.getInstance().uploadFile(FireBaseHelper.getInstance().ROOT_GRIEVANCES,
                    code,
                    String.valueOf(grievanceType.getId()),
                    imageModel,
                    count++);

            if (uploadTask != null) {
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getContext(), "Unable to buttonUpload file", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onFailure: " + exception.getMessage());
                        progressDialog.dismiss();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Log.d(TAG, "onSuccess: " + downloadUrl);
                        progressDialog.setMessage("Uploading file " + count + "/" + selectedImageModelArrayList.size());
                        if (count == selectedImageModelArrayList.size()) {
                            Toast.makeText(getActivity(), "Grievance Submitted", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
            }
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


  /*private void removeSelectedFile() {
        fileChosed = null;
        fileChosedPath = null;
        textViewFileName.setText("");
        buttonRemove.setVisibility(View.GONE);
        imageviewSelectedImage.setImageDrawable(null);
    }

    private void setupSelectedFile(File file) {
        if (file.length() / 1048576 > 1) {
            Helper.getInstance().showAlertDialog(getContext(), "You have selected a file larger than 1 MB\nPlease choose a file of smaller size\n\nThe selection you just made will not be processed", "Choose File", "OK");
            removeSelectedFile();
        } else {
            fileChosedPath = file.getAbsolutePath();
            fileChosed = file.getName();
            Log.d(TAG, "onFileSelected: " + fileChosedPath);
            buttonRemove.setVisibility(View.VISIBLE);
            textViewFileName.setText(fileChosed);
        }
    }*/


        /*buttonRemove.setImageDrawable(AppCompatResources.getDrawable(this.getContext(), R.drawable.ic_close_black_24dp));
        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSelectedFile();

            }
        });*/


/*    private void showFileChooser() {
        DialogConfig dialogConfig = new DialogConfig.Builder()
                .enableMultipleSelect(false) // default is false
                .enableFolderSelect(false) // default is false
                .initialDirectory(Environment.getExternalStorageDirectory().getAbsolutePath()) // default is sdcard
                .supportFiles(new SupportFile(".jpeg", 0), new SupportFile(".jpg", 0), new SupportFile(".pdf", 0)) // default is showing all file types.
                .build();

        new FilePickerDialogFragment.Builder()
                .configs(dialogConfig)
                .onFilesSelected(new FilePickerDialogFragment.OnFilesSelectedListener() {
                    @Override
                    public void onFileSelected(List<File> list) {
                        for (File file : list) {
                            setupSelectedFile(file);
                        }
                    }
                })
                .build()
                .show(getActivity().getSupportFragmentManager(), null);
    }*/