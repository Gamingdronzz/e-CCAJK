package com.mycca.Adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mycca.Activity.UpdateGrievanceActivity;
import com.mycca.Models.GrievanceModel;
import com.mycca.Providers.GrievanceDataProvider;
import com.mycca.R;
import com.mycca.Tools.Helper;

import java.util.ArrayList;

public class RecyclerViewAdapterGrievanceUpdate extends RecyclerView.Adapter<RecyclerViewAdapterGrievanceUpdate.GrievanaceUpdateViewHolder> {

    private ArrayList<GrievanceModel> grievanceModelArrayList;
    private AppCompatActivity appCompatActivity;
    private String TAG = "Grievance";

    public static final int REQUEST_UPDATE = 299;


    public RecyclerViewAdapterGrievanceUpdate(ArrayList<GrievanceModel> grievanceModelArrayList, AppCompatActivity appCompatActivity) {
        this.grievanceModelArrayList = grievanceModelArrayList;
        this.appCompatActivity = appCompatActivity;
    }

    @NonNull
    @Override
    public RecyclerViewAdapterGrievanceUpdate.GrievanaceUpdateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerViewAdapterGrievanceUpdate.GrievanaceUpdateViewHolder viewHolder = new GrievanaceUpdateViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_grievance_item, parent, false),
                        new CustomClickListener(),
                        new CustomUpdateClickListener());
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GrievanaceUpdateViewHolder holder, int position) {
        GrievanceModel grievanceModel = grievanceModelArrayList.get(position);

        holder.customClickListener.setPosition(position);
        holder.customUpdateClickListener.setPosition(position);
        holder.textViewPensionerCode.setText(grievanceModel.getPensionerIdentifier());
        holder.textViewGrievanceType.setText(Helper.getInstance().getGrievanceCategory(grievanceModel.getGrievanceType()));
        holder.textViewGrievanceSubtype.setText(Helper.getInstance().getGrievanceString(grievanceModel.getGrievanceType()));
        holder.textViewPensionerMobile.setText(grievanceModel.getMobile());
        holder.textViewDateOfGrievance.setText(Helper.getInstance().formatDate(grievanceModel.getDate(), "MMM d, yyyy"));
        holder.textViewStatus.setText(Helper.getInstance().getStatusString(grievanceModel.getGrievanceStatus()));

        if (grievanceModel.getExpanded()) {
            holder.expandableArea.setVisibility(View.VISIBLE);
            holder.textViewGrievanceSubtype.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up_black_24dp, 0);
        } else {
            holder.textViewGrievanceSubtype.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down_black_24dp, 0);
            holder.expandableArea.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return grievanceModelArrayList.size();
    }

    static class GrievanaceUpdateViewHolder extends RecyclerView.ViewHolder {

        TextView textViewPensionerCode;
        TextView textViewGrievanceType;
        TextView textViewGrievanceSubtype;
        LinearLayout expandableArea;
        TextView textViewStatus;
        TextView textViewPensionerMobile, textViewDateOfGrievance;
        AppCompatButton updateGrievance;

        CustomClickListener customClickListener;
        CustomUpdateClickListener customUpdateClickListener;


        GrievanaceUpdateViewHolder(View itemView, CustomClickListener customClickListener, CustomUpdateClickListener customUpdateClickListener) {
            super(itemView);
            textViewPensionerCode = itemView.findViewById(R.id.textview_pensioner);
            textViewGrievanceType = itemView.findViewById(R.id.textview_grievance_type);

            textViewGrievanceSubtype = itemView.findViewById(R.id.textview_grievance_subtype);
            expandableArea = itemView.findViewById(R.id.expandable_layout_grievance);
            textViewStatus = itemView.findViewById(R.id.textview_status_text);
            textViewStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_inspection, 0, 0, 0);
            textViewPensionerMobile = itemView.findViewById(R.id.textview_pensioner_mobile);
            textViewPensionerMobile.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_phone_android_black_24dp, 0, 0, 0);

            textViewDateOfGrievance = itemView.findViewById(R.id.textview_date_of_grievance);
            textViewDateOfGrievance.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_calendar, 0, 0, 0);

            updateGrievance = itemView.findViewById(R.id.button_update_grievance);
            updateGrievance.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_update_black_24dp, 0, 0, 0);

            this.customClickListener = customClickListener;
            this.customUpdateClickListener = customUpdateClickListener;
            itemView.setOnClickListener(this.customClickListener);
            updateGrievance.setOnClickListener(this.customUpdateClickListener);

        }
    }

    class CustomClickListener implements View.OnClickListener {

        private int position;

        public void setPosition(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            grievanceModelArrayList.get(position).setExpanded(!grievanceModelArrayList.get(position).getExpanded());
            Log.d(TAG, "onClick: " + position);
            notifyItemChanged(position);
        }
    }

    class CustomUpdateClickListener implements View.OnClickListener {
        private int position;

        public void setPosition(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            GrievanceModel grievanceModel = grievanceModelArrayList.get(position);
            Intent intent = new Intent(appCompatActivity, UpdateGrievanceActivity.class);
            GrievanceDataProvider.getInstance().selectedGrievance = grievanceModel;
            appCompatActivity.startActivityForResult(intent, REQUEST_UPDATE);
        }
    }


}

//class CustomSpinnerItemSelectedListener implements Spinner.OnItemSelectedListener {
//    private int position;
//
//    public void setPosition(int position) {
//        this.position = position;
//    }
//
//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        Log.d(TAG, "onItemSelected: " + position);
//        GrievanceModel grievanceModel = grievanceModelArrayList.get(this.position);
//        grievanceModel.setGrievanceStatus(position);
//        notifyItemChanged(position);
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {
//
//    }
//}
//
//class CustomEditTextListener implements TextWatcher {
//    private int position;
//
//    public void setPosition(int position) {
//        this.position = position;
//    }
//
//    @Override
//    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//    }
//
//    @Override
//    public void onTextChanged(CharSequence s, int start, int before, int count) {
//        Log.d(TAG, "afterTextChanged: " + s);
//        GrievanceModel grievanceModel = grievanceModelArrayList.get(position);
//        grievanceModel.setMessage(s.toString());
//        notifyItemChanged(position);
//    }
//
//    @Override
//    public void afterTextChanged(Editable s) {
//
//    }
//}
