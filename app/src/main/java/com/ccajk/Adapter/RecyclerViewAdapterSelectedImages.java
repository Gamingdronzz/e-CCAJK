package com.ccajk.Adapter;




import android.support.v4.app.Fragment;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ccajk.Fragments.GrievanceFragment;
import com.ccajk.Fragments.InspectionFragment;
import com.ccajk.Models.SelectedImageModel;
import com.ccajk.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by hp on 13-02-2018.
 */

public class RecyclerViewAdapterSelectedImages extends RecyclerView.Adapter<RecyclerViewAdapterSelectedImages.SelectedImageViewHolder> {

    ArrayList<SelectedImageModel> selectedImageModelArrayList;
    Fragment fragment;


    public RecyclerViewAdapterSelectedImages(ArrayList<SelectedImageModel> selectedImageModelArrayList, Fragment fragment) {
        this.selectedImageModelArrayList = selectedImageModelArrayList;
        this.fragment = fragment;
    }

    @Override
    public RecyclerViewAdapterSelectedImages.SelectedImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerViewAdapterSelectedImages.SelectedImageViewHolder viewHolder = new SelectedImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_selected_image, parent, false),new RemoveClickListener());
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SelectedImageViewHolder holder, int position) {
        SelectedImageModel selectedImageModel = selectedImageModelArrayList.get(position);
        holder.removeClickListener.setPosition(position);
        //holder.selectedImageTitle.setText(selectedImageModel.getSelectedImageName());
        holder.selectedImageTitle.setText("File-"+(position+1));
        Picasso.with(fragment.getContext()).load(selectedImageModel.getImageURI()).into(holder.selectedImage);
    }

    @Override
    public int getItemCount() {
        return selectedImageModelArrayList.size();
    }

    public class SelectedImageViewHolder extends RecyclerView.ViewHolder {

        private TextView selectedImageTitle;
        ImageButton overlayRemoveImage;
        private ImageView selectedImage;
        private RemoveClickListener removeClickListener;

        public SelectedImageViewHolder(View itemView,RemoveClickListener removeClickListener) {
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

            if(fragment instanceof InspectionFragment)
            {
                InspectionFragment inspectionFragment = (InspectionFragment) fragment;
                inspectionFragment.setSelectedFileCount(selectedImageModelArrayList.size());
            }
            else if(fragment instanceof GrievanceFragment)
            {
                GrievanceFragment grievanceFragment = (GrievanceFragment) fragment;
                grievanceFragment.setSelectedFileCount(selectedImageModelArrayList.size());
            }

        }
    }
}
