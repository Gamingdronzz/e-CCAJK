package com.ccajk.Adapter;

import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ccajk.Models.GrievanceModel;
import com.ccajk.R;
import com.ccajk.Tools.Helper;

import java.util.ArrayList;

import butterknife.OnClick;

/**
 * Created by hp on 13-02-2018.
 */

public class RecyclerViewAdapterGrievanceUpdate extends RecyclerView.Adapter<RecyclerViewAdapterGrievanceUpdate.GrievanaceUpdateViewHolder> {

    ArrayList<GrievanceModel> grievanceModelArrayList;
    Context context;


    public RecyclerViewAdapterGrievanceUpdate(ArrayList<GrievanceModel> grievanceModelArrayList, Context context) {
        this.grievanceModelArrayList = grievanceModelArrayList;
        this.context = context;
    }

    @Override
    public RecyclerViewAdapterGrievanceUpdate.GrievanaceUpdateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerViewAdapterGrievanceUpdate.GrievanaceUpdateViewHolder viewHolder = new GrievanaceUpdateViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_grievance_item, parent, false),context,new CustomClickListener());
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(GrievanaceUpdateViewHolder holder, int position) {
        GrievanceModel grievanceModel = grievanceModelArrayList.get(position);

        holder.customClickListener.setPosition(position);
        holder.pensioner.setText(grievanceModel.getPensionerIdentifier());
        holder.grievanceType.setText(Helper.getInstance().getGrievanceCategory(grievanceModel.getGrievanceType()));
        holder.grievanceSubtype.setText(Helper.getInstance().getGrievanceString(grievanceModel.getGrievanceType()));
        holder.textViewPensionerMobile.setText(grievanceModel.getMobile());
        holder.textViewDateOfGrievance.setText(Helper.getInstance().formatDate(grievanceModel.getDate()));

        if(grievanceModel.isExpanded())
        {
            holder.expandableArea.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.expandableArea.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return grievanceModelArrayList.size();
    }

    public static class GrievanaceUpdateViewHolder extends RecyclerView.ViewHolder {

        public TextView pensioner;
        public TextView grievanceType;
        public TextView grievanceSubtype;
        public LinearLayout expandableArea;

        public TextView textViewPensionerMobile,textViewDateOfGrievance;
        Spinner spinnerUpdateStatus;
        TextInputEditText textInputEditTextMessage;

        CustomClickListener customClickListener;



        public GrievanaceUpdateViewHolder(View itemView,Context context,CustomClickListener customClickListener) {
            super(itemView);
            pensioner = itemView.findViewById(R.id.textview_pensioner);
            grievanceType = itemView.findViewById(R.id.textview_grievance_type);
            grievanceSubtype = itemView.findViewById(R.id.textview_grievance_subtype);
            expandableArea = itemView.findViewById(R.id.expandable_layout_grievance);

            textViewPensionerMobile = itemView.findViewById(R.id.textview_pensioner_mobile);
            textViewDateOfGrievance = itemView.findViewById(R.id.textview_date_of_grievance);
            spinnerUpdateStatus = itemView.findViewById(R.id.spinner_update_status);
            spinnerUpdateStatus.setAdapter(new ArrayAdapter(context,
                    android.R.layout.simple_spinner_dropdown_item,
                    Helper.getInstance().getStatusList()));
            textInputEditTextMessage = itemView.findViewById(R.id.editTextGrievanceMessage);
            this.customClickListener = customClickListener;
            itemView.setOnClickListener(customClickListener);

//            pensioner.setOnClickListener(customClickListener);
//            grievanceSubtype.setOnClickListener(customClickListener);
//            grievanceType.setOnClickListener(customClickListener);
//            textViewPensionerMobile.setOnClickListener(customClickListener);
//            textViewDateOfGrievance.setOnClickListener(customClickListener);
        }
    }

    class CustomClickListener implements View.OnClickListener{


        private int position;

        public void setPosition(int position)
        {
            this.position = position;
        }
        @Override
        public void onClick(View v) {
            grievanceModelArrayList.get(position).setExpanded(!grievanceModelArrayList.get(position).isExpanded());
            notifyItemChanged(position);
        }
    }


}
