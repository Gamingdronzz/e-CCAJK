package com.ccajk.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ccajk.Models.Grievance;
import com.ccajk.R;
import com.ccajk.Tools.Helper;

import java.util.ArrayList;

/**
 * Created by hp on 13-02-2018.
 */

public class RecyclerViewAdapterGrievanceUpdate extends RecyclerView.Adapter<RecyclerViewAdapterGrievanceUpdate.GrievanaceUpdateViewHolder> {

    ArrayList<Grievance> grievanceArrayList;
    Context context;


    public RecyclerViewAdapterGrievanceUpdate(ArrayList<Grievance> grievanceArrayList, Context context) {
        this.grievanceArrayList = grievanceArrayList;
        this.context = context;
    }

    @Override
    public RecyclerViewAdapterGrievanceUpdate.GrievanaceUpdateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerViewAdapterGrievanceUpdate.GrievanaceUpdateViewHolder viewHolder = new GrievanaceUpdateViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_updation_item, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(GrievanaceUpdateViewHolder holder, int position) {
        Grievance grievance = grievanceArrayList.get(position);

        holder.pensioner.setText(grievance.getPensionerIdentifier());
        holder.grievanceType.setText(
                Helper.getInstance().getGrievanceString(grievance.getGrievanceType())
                        + "\n(" + Helper.getInstance().getGrievanceCategory(grievance.getGrievanceType())
                        + ")");
    }

    @Override
    public int getItemCount() {
        return grievanceArrayList.size();
    }

    public static class GrievanaceUpdateViewHolder extends RecyclerView.ViewHolder {

        public TextView pensioner;
        public TextView grievanceType;

        public GrievanaceUpdateViewHolder(View itemView) {
            super(itemView);
            pensioner = itemView.findViewById(R.id.textview_pensioner);
            grievanceType = itemView.findViewById(R.id.textview_grievance_type);
        }
    }


}
