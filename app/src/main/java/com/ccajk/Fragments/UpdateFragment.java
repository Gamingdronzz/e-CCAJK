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

import com.ccajk.Activity.GrievanceUpdateActivity;
import com.ccajk.Adapter.RecyclerViewAdapterSelectedImages;
import com.ccajk.Models.RtiModel;
import com.ccajk.Models.SelectedImageModel;
import com.ccajk.R;
import com.ccajk.Tools.FireBaseHelper;
import com.ccajk.Tools.Helper;
import com.ccajk.Tools.PopUpWindows;
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


public class UpdateFragment extends Fragment {


    TextView updatePensionGrievance,updateGPFGrievance,updateRTIStatus;


    public UpdateFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update, container, false);
        bindViews(view);
        init();
        return view;
    }

    void bindViews(View view) {
        updatePensionGrievance = view.findViewById(R.id.update_pension_grievance);
        updateGPFGrievance = view.findViewById(R.id.update_gpf_grievance);
        updateRTIStatus = view.findViewById(R.id.update_rti_status);
    }

    private void init() {
        updatePensionGrievance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), GrievanceUpdateActivity.class);
                startActivity(intent);
            }
        });

        updateGPFGrievance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        updateRTIStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }
}
