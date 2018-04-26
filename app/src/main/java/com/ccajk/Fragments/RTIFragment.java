package com.ccajk.Fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ccajk.Adapter.RecyclerViewAdapterSelectedImages;
import com.ccajk.CustomObjects.ProgressDialog;
import com.ccajk.Models.RtiModel;
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


public class RTIFragment extends Fragment {

    ImageView imageName, imageSubject, imagePhone, imageviewSelectedImage;
    TextInputEditText inputName, inputSubject, inputPhone;
    TextView textViewSelectedFileCount;
    Button attach, submit;
    ProgressDialog progressDialog;

    String TAG = "RTI";
    String name, mobile, subject, root;
    int count;
    ImagePicker imagePicker;

    RecyclerView recyclerViewSelectedImages;
    RecyclerViewAdapterSelectedImages adapterSelectedImages;
    ArrayList<SelectedImageModel> selectedImageModelArrayList;

    public RTIFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rti, container, false);
        bindViews(view);
        init();
        return view;
    }

    void bindViews(View view) {
        imageName = view.findViewById(R.id.image_name);
        imageSubject = view.findViewById(R.id.image_subject);
        imagePhone = view.findViewById(R.id.image_phone);
        inputName = view.findViewById(R.id.edittext_name);
        inputPhone = view.findViewById(R.id.edittext_phone);
        inputSubject = view.findViewById(R.id.edittext_subject_matter);
        textViewSelectedFileCount = view.findViewById(R.id.textview_selected_file_count_grievance);
        recyclerViewSelectedImages = view.findViewById(R.id.recycler_view_selected_images);
        attach = view.findViewById(R.id.button_attach);
        submit = view.findViewById(R.id.button_submit);
    }

    private void init() {

        progressDialog = Helper.getInstance().getProgressWindow(getActivity(), "Please wait...");

        imageName.setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.ic_person_black_24dp));
        imagePhone.setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.ic_phone_android_black_24dp));
        imageSubject.setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.ic_subject_black_24dp));

        attach.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_attach_file_black_24dp, 0, 0, 0);
        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()) {
                    confirmSubmission();
                }
            }
        });

        selectedImageModelArrayList = new ArrayList<>();
        adapterSelectedImages = new RecyclerViewAdapterSelectedImages(selectedImageModelArrayList, this);
        recyclerViewSelectedImages.setAdapter(adapterSelectedImages);
        recyclerViewSelectedImages.setLayoutManager(new GridLayoutManager(getContext(), 4));

    }

    private boolean checkInput() {
        name = inputName.getText().toString().trim();
        mobile = inputPhone.getText().toString();
        subject = inputSubject.getText().toString().trim();

        if (name.isEmpty()) {
            inputName.setError("Enter Valid Name");
            inputName.requestFocus();
            return false;
        } else if (mobile.length() < 10) {
            inputPhone.setError("Invalid Phone No.");
            inputPhone.requestFocus();
            return false;
        } else if (subject.isEmpty()) {
            inputSubject.setError("Add Subject");
            inputSubject.requestFocus();
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
                        submitRTI();
                    }
                });
    }

    private void loadValues(View v) {
        TextView ppoNo = v.findViewById(R.id.textview_ppo_no);
        ppoNo.setText("Name" + ": " + name);
        TextView mobNo = v.findViewById(R.id.textview_mobile_no);
        mobNo.setText(mobNo.getText() + " " + mobile);
        ((TextView)v.findViewById(R.id.detail)).setText("Subject:");
        TextView details = v.findViewById(R.id.textview_grievance_details);
        details.setText(subject);

        v.findViewById(R.id.textview_email).setVisibility(View.GONE);
        v.findViewById(R.id.textview_grievance_type).setVisibility(View.GONE);
        v.findViewById(R.id.textview_grievance_by).setVisibility(View.GONE);
    }

    private void submitRTI() {
        progressDialog.show();
        final DatabaseReference dbref;
        dbref = FireBaseHelper.getInstance().databaseReference.child(FireBaseHelper.getInstance().ROOT_RTI);

        final RtiModel rtiModel = new RtiModel(
                name,
                mobile,
                subject,
                Preferences.getInstance().getStringPref(getContext(),Preferences.PREF_STATE),
                0,null,
                new Date());

        final String key = name.replaceAll("\\s", "-") + "-" + mobile;
        dbref.child(key).setValue(rtiModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (selectedImageModelArrayList.size() != 0) {
                        uploadFile(key);
                    } else {
                        Toast.makeText(getActivity(), "RTI application Submitted", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                } else {
                    Toast.makeText(getActivity(), "Unable to submit", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }

            }
        });
    }

    private void uploadFile(String key) {
        count = 0;
        for (SelectedImageModel imageModel : selectedImageModelArrayList) {
            UploadTask  uploadTask = FireBaseHelper.getInstance().uploadFiles(imageModel,
                    true,
                    count++,
                    FireBaseHelper.getInstance().ROOT_RTI,
                    key
            );

            if (uploadTask != null) {
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getContext(), "Unable to Upload files", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        progressDialog.setMessage("Uploading file " + count + "/" + selectedImageModelArrayList.size());
                        if (count == selectedImageModelArrayList.size()) {
                            Toast.makeText(getActivity(), "RTI Application Submitted", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        }
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
                textViewSelectedFileCount.setText("Selected Files = " + (currentPosition + 1));
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (imagePicker != null)
            imagePicker.onActivityResult(this.getActivity(), requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            default: {
                if (imagePicker != null)
                    imagePicker.onRequestPermissionsResult(this.getActivity(), requestCode, permissions, grantResults);
            }

        }

    }
}
