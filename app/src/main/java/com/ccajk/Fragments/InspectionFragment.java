package com.ccajk.Fragments;


import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.content.res.AppCompatResources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.ccajk.R;

import java.io.File;
import java.util.List;

import easyfilepickerdialog.kingfisher.com.library.model.DialogConfig;
import easyfilepickerdialog.kingfisher.com.library.model.SupportFile;
import easyfilepickerdialog.kingfisher.com.library.view.FilePickerDialogFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class InspectionFragment extends Fragment {

    private static final String TAG = "Inspection";
    ImageButton choose, location;
    Button upload;

    public InspectionFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inspection, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        choose = view.findViewById(R.id.button_choose);
        choose.setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.ic_add_circle_black_24dp));
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilePicker();
            }
        });

        location = view.findViewById(R.id.button_location);
        location.setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.ic_add_location_black_24dp));

        upload = view.findViewById(R.id.button_upload);
        upload.setCompoundDrawablesWithIntrinsicBounds(null, AppCompatResources.getDrawable(getContext(), R.drawable.ic_file_upload_black_24dp), null, null);

    }

    private void showFilePicker() {

        DialogConfig dialogConfig = new DialogConfig.Builder()
                .enableMultipleSelect(false) // default is false
                .enableFolderSelect(false) // default is false
                .initialDirectory(Environment.getExternalStorageDirectory().getAbsolutePath()) // default is sdcard
                .supportFiles( new SupportFile(".jpg", 0),new SupportFile(".png", 0), new SupportFile(".pdf", 0)) // default is showing all file types.
                .build();

        new FilePickerDialogFragment.Builder()
                .configs(dialogConfig)
                .onFilesSelected(new FilePickerDialogFragment.OnFilesSelectedListener() {
                    @Override
                    public void onFileSelected(List<File> list) {
                        Log.e(TAG, "total Selected file: " + list.size());
                        for (File file : list) {
                            Log.e(TAG, "Selected file: " + file.getAbsolutePath());
                        }
                    }
                })
                .build()
                .show(getChildFragmentManager(), null);



    }

}
