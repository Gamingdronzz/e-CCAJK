package com.mycca.Adapter;


import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mycca.Activity.UpdateGrievanceActivity;
import com.mycca.Fragments.SubmitGrievanceFragment;
import com.mycca.Fragments.InspectionFragment;
import com.mycca.Models.SelectedImageModel;
import com.mycca.R;

import java.util.ArrayList;


public class RecyclerViewAdapterSelectedImages extends RecyclerView.Adapter<RecyclerViewAdapterSelectedImages.SelectedImageViewHolder> {

    private ArrayList<SelectedImageModel> selectedImageModelArrayList;
    private Fragment fragment;
    private AppCompatActivity appCompatActivity;


    public RecyclerViewAdapterSelectedImages(ArrayList<SelectedImageModel> selectedImageModelArrayList, Fragment fragment) {
        this.selectedImageModelArrayList = selectedImageModelArrayList;
        this.fragment = fragment;
    }

    public RecyclerViewAdapterSelectedImages(ArrayList<SelectedImageModel> selectedImageModelArrayList, AppCompatActivity appCompatActivity) {
        this.selectedImageModelArrayList = selectedImageModelArrayList;
        this.appCompatActivity = appCompatActivity;
    }

    @NonNull
    @Override
    public RecyclerViewAdapterSelectedImages.SelectedImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SelectedImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_selected_image, parent, false), new RemoveClickListener());
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedImageViewHolder holder, int position) {
        SelectedImageModel selectedImageModel = selectedImageModelArrayList.get(position);
        holder.removeClickListener.setPosition(position);
        String filename = "File-" + (position + 1);
        holder.selectedImageTitle.setText(filename);
        if (fragment != null) {
            Glide.with(fragment.getContext()).load(selectedImageModel.getImageURI()).into(holder.selectedImage);
        }
        if (appCompatActivity != null) {
            Glide.with(appCompatActivity.getBaseContext()).load(selectedImageModel.getImageURI()).into(holder.selectedImage);
        }
    }

    @Override
    public int getItemCount() {
        return selectedImageModelArrayList.size();
    }

    class SelectedImageViewHolder extends RecyclerView.ViewHolder {

        private TextView selectedImageTitle;
        ImageButton overlayRemoveImage;
        private ImageButton selectedImage;
        private RemoveClickListener removeClickListener;

        SelectedImageViewHolder(View itemView, RemoveClickListener removeClickListener) {
            super(itemView);
            selectedImageTitle = itemView.findViewById(R.id.textview_selected_image_name);
            //selectedImageTitle.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_close_black_24dp,0);
            selectedImage = itemView.findViewById(R.id.imageview_selected_image);
            overlayRemoveImage = itemView.findViewById(R.id.imageview_overlay_remove_button);
            //overlayRemoveImage.setImageDrawable(AppCompatResources.getDrawable(fragment.getContext(),R.drawable.ic_close_black_24dp));
            this.removeClickListener = removeClickListener;
            overlayRemoveImage.setOnClickListener(removeClickListener);
        }
    }

    class RemoveClickListener implements View.OnClickListener {
        public void setPosition(int position) {
            this.position = position;
        }

        private int position;

        @Override
        public void onClick(View v) {
            Log.d("Adapter", "onClick: Item removed at " + position);
            selectedImageModelArrayList.remove(position);
            notifyItemRemoved(position);
            notifyDataSetChanged();
            if (fragment != null) {
                if (fragment instanceof InspectionFragment) {
                    InspectionFragment inspectionFragment = (InspectionFragment) fragment;
                    inspectionFragment.setSelectedFileCount(selectedImageModelArrayList.size());
                } else if (fragment instanceof SubmitGrievanceFragment) {
                    SubmitGrievanceFragment submitGrievanceFragment = (SubmitGrievanceFragment) fragment;
                    submitGrievanceFragment.setSelectedFileCount(selectedImageModelArrayList.size());
                }
            }

            if(appCompatActivity != null)
            {
                UpdateGrievanceActivity updateGrievanceActivity = (UpdateGrievanceActivity) appCompatActivity;
                updateGrievanceActivity.setSelectedFileCount(selectedImageModelArrayList.size());
            }

        }
    }
}
