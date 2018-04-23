package com.ccajk.Fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.content.res.AppCompatResources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ccajk.R;
import com.ccajk.Tools.Helper;
import com.linchaolong.android.imagepicker.ImagePicker;
import com.linchaolong.android.imagepicker.cropper.CropImage;
import com.linchaolong.android.imagepicker.cropper.CropImageView;
import com.squareup.picasso.Picasso;

import java.io.File;


public class RTIFragment extends Fragment {

    ImageView imageName, imageSubject, imagePhone, imageviewSelectedImage;
    TextInputEditText inputName, inputSubject, inputPhone;
    TextView textViewFilename;
    Button attach, submit;

    String fileChosed, fileChosedPath, root;
    ImagePicker imagePicker;

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
        textViewFilename = view.findViewById(R.id.textview_file_name);
        imageviewSelectedImage = view.findViewById(R.id.imageview_selected_image);
        attach = view.findViewById(R.id.button_attach);
        submit = view.findViewById(R.id.button_submit);
    }

    private void init() {

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
    }

    private boolean checkInput() {
        if (inputName.getText().toString().isEmpty()) {
            inputName.setError("Enter Valid Name");
            inputName.requestFocus();
            return false;
        } else if (inputPhone.getText().toString().length() < 10) {
            inputPhone.setError("Invalid Phone No.");
            inputPhone.requestFocus();
            return false;
        }else if(inputSubject.getText().toString().isEmpty()){
            inputSubject.setError("Add Subject");
            inputSubject.requestFocus();
            return false;
        }
        return true;
    }

    private void confirmSubmission() {

    }

    private void showImageChooser() {
        imagePicker = Helper.getInstance().showImageChooser(imagePicker, getActivity(), false, new ImagePicker.Callback() {
            @Override
            public void onPickImage(Uri imageUri) {
                File file = new File(imageUri.getPath());
                setupSelectedFile(file);
                Picasso.with(getContext()).load(imageUri).into(imageviewSelectedImage);
            }

            @Override
            public void onCropImage(Uri imageUri) {

            }

            @Override
            public void cropConfig(CropImage.ActivityBuilder builder) {
                builder
                        .setMultiTouchEnabled(false)
                        .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setRequestedSize(540, 960)
                        .setAspectRatio(9, 16);
            }

            @Override
            public void onPermissionDenied(int requestCode, String[] permissions,
                                           int[] grantResults) {
            }
        });
    }

    private void setupSelectedFile(File file) {
        if (file.length() / 1048576 > 1) {
            Helper.getInstance().showAlertDialog(getContext(), "You have selected a file larger than 1 MB\nPlease choose a file of smaller size\n\nThe selection you just made will not be processed", "Choose File", "OK");
        } else {
            fileChosedPath = file.getAbsolutePath();
            fileChosed = file.getName();
            textViewFilename.setText(fileChosed);
        }
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
