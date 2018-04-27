package com.ccajk.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ccajk.Activity.UpdateGrievanceActivity;
import com.ccajk.Models.GrievanceModel;
import com.ccajk.R;
import com.ccajk.Tools.FireBaseHelper;
import com.ccajk.Tools.Helper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;

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
        RecyclerViewAdapterGrievanceUpdate.GrievanaceUpdateViewHolder viewHolder = new GrievanaceUpdateViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_grievance_item, parent, false),
                        context,
                        new CustomClickListener(),
                        new CustomSpinnerItemSelectedListener(),
                        new CustomEditTextListener(),
                        new CustomUpdateClickListener());
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(GrievanaceUpdateViewHolder holder, int position) {
        GrievanceModel grievanceModel = grievanceModelArrayList.get(position);

        holder.customClickListener.setPosition(position);
        holder.customEditTextListener.setPosition(position);
        holder.spinnerItemSelectedListener.setPosition(position);
        holder.customUpdateClickListener.setPosition(position);
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

        public Button updateGrievance;

        CustomClickListener customClickListener;
        CustomEditTextListener customEditTextListener;
        CustomSpinnerItemSelectedListener spinnerItemSelectedListener;
        CustomUpdateClickListener customUpdateClickListener;



        public GrievanaceUpdateViewHolder
                (View itemView,Context context,
                 CustomClickListener customClickListener,
                 CustomSpinnerItemSelectedListener customSpinnerItemSelectedListener,
                 CustomEditTextListener customEditTextListener,
                 CustomUpdateClickListener customUpdateClickListener) {
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
            updateGrievance = itemView.findViewById(R.id.button_update_grievance);


            this.customClickListener = customClickListener;
            this.customEditTextListener  = customEditTextListener;
            this.spinnerItemSelectedListener = customSpinnerItemSelectedListener;
            this.customUpdateClickListener = customUpdateClickListener;

            itemView.setOnClickListener(customClickListener);
            spinnerUpdateStatus.setOnItemSelectedListener(customSpinnerItemSelectedListener);
            textInputEditTextMessage.addTextChangedListener(customEditTextListener);
            updateGrievance.setOnClickListener(customUpdateClickListener);

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

    class CustomSpinnerItemSelectedListener implements Spinner.OnItemSelectedListener
    {
        private int position;

        public void setPosition(int position)
        {
            this.position = position;
        }

//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            GrievanceModel grievanceModel = grievanceModelArrayList.get(this.position);
//            grievanceModel.setGrievanceStatus(position);
//        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            GrievanceModel grievanceModel = grievanceModelArrayList.get(this.position);
            grievanceModel.setGrievanceStatus(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
    class CustomEditTextListener implements TextWatcher
    {
        private int position;

        public void setPosition(int position)
        {
            this.position = position;
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            GrievanceModel grievanceModel = grievanceModelArrayList.get(position);
            grievanceModel.setMessage(s.toString());
        }
    }

    class CustomUpdateClickListener implements  View.OnClickListener
    {
        private int position;

        public void setPosition(int position)
        {
            this.position = position;
        }
        @Override
        public void onClick(View v) {
            GrievanceModel grievanceModel = grievanceModelArrayList.get(position);
            String status = Helper.getInstance().getStatusString(grievanceModel.getGrievanceStatus());
            String message = grievanceModel.getMessage();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("grievanceStatus", status);
            hashMap.put("message", message);

            DatabaseReference dbref = FireBaseHelper.getInstance().databaseReference;
            dbref.child(FireBaseHelper.getInstance().ROOT_GRIEVANCES).child(grievanceModel.getPensionerIdentifier())
                    .child(String.valueOf(grievanceModel.getGrievanceType())).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                        Toast.makeText(context, "Successfully Updated", Toast.LENGTH_LONG).show();
                }
            });
        }
    }


}
