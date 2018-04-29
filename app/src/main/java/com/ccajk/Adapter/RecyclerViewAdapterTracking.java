package com.ccajk.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ccajk.Models.GrievanceModel;
import com.ccajk.R;
import com.ccajk.Tools.Helper;

import java.util.ArrayList;

/**
 * Created by hp on 20-03-2018.
 */

public class RecyclerViewAdapterTracking extends RecyclerView.Adapter<RecyclerViewAdapterTracking.TrackViewHolder> {

    ArrayList<GrievanceModel> grievanceModels;

    public RecyclerViewAdapterTracking(ArrayList<GrievanceModel> grievanceModels) {
        this.grievanceModels = grievanceModels;
    }

    @Override
    public TrackViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TrackViewHolder viewHolder = new TrackViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_track_grievance_item, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TrackViewHolder holder, int position) {
        GrievanceModel grievanceModel = grievanceModels.get(position);

        holder.grievanceType.setText(Helper.getInstance().getGrievanceCategory(grievanceModel.getGrievanceType()));
        holder.grievanceApplied.setText("");
        holder.grievanceApplied.setText(Helper.getInstance().getGrievanceString((int) grievanceModel.getGrievanceType()));
        holder.date.setText("");
        holder.date.setText(Helper.getInstance().formatDate(grievanceModel.getDate(),"MMM d, yyyy"));
        holder.status.setText("");
        holder.status.setText(Helper.getInstance().getStatusList()[(int) grievanceModel.getGrievanceStatus()]);
        holder.message.setText("");
        if(grievanceModel.isExpanded())
        {
            holder.linearLayoutExpandableArea.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.linearLayoutExpandableArea.setVisibility(View.GONE);
        }
        if (grievanceModel.getMessage() != null)
            holder.message.setText(grievanceModel.getMessage());
        else
            holder.message.setText("Nil");
    }

    @Override
    public int getItemCount() {
        return grievanceModels.size();
    }

    public class TrackViewHolder extends RecyclerView.ViewHolder {

        public TextView grievanceType, grievanceApplied, date, status, message;
        public LinearLayout linearLayoutExpandableArea;

        public TrackViewHolder(View itemView) {
            super(itemView);
            grievanceType = itemView.findViewById(R.id.textview_grievance_type);
            grievanceApplied = itemView.findViewById(R.id.textview_grievance);
            date = itemView.findViewById(R.id.textview_date);
            status = itemView.findViewById(R.id.textview_result);
            message = itemView.findViewById(R.id.textview_message);
            linearLayoutExpandableArea = itemView.findViewById(R.id.expandable_area_track_grievance);
        }
    }
}