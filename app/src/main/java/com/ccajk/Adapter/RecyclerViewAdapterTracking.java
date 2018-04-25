package com.ccajk.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ccajk.Models.Grievance;
import com.ccajk.R;
import com.ccajk.Tools.FireBaseHelper;
import com.ccajk.Tools.Helper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by hp on 20-03-2018.
 */

public class RecyclerViewAdapterTracking extends RecyclerView.Adapter<RecyclerViewAdapterTracking.TrackViewHolder> {

    ArrayList<Grievance> grievances;

    public RecyclerViewAdapterTracking(ArrayList<Grievance> grievances) {
        this.grievances = grievances;
    }

    @Override
    public TrackViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TrackViewHolder viewHolder = new TrackViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_track_result, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TrackViewHolder holder, int position) {
        Grievance grievance = grievances.get(position);
        SimpleDateFormat dt = new SimpleDateFormat("MMM d, yyyy");

        holder.grievanceType.setText(getGrievanceType(grievance.getGrievanceType()));
        Log.d("RecyclerView", "onBindViewHolder: "+getGrievanceType(grievance.getGrievanceType()));
        holder.grievanceApplied.setText("");
        holder.grievanceApplied.setText(Helper.getInstance().getGrievanceString((int) grievance.getGrievanceType()));
        holder.date.setText("");
        holder.date.setText(dt.format(grievance.getDate()));
        holder.status.setText("");
        holder.status.setText(Helper.getInstance().getStatusString(grievance.getGrievanceStatus()));
        holder.message.setText("");
        if (grievance.getMessage() != null)
            holder.message.setText(grievance.getMessage());
        else
            holder.message.setText("Nil");
    }

    @Override
    public int getItemCount() {
        return grievances.size();
    }

    public String getGrievanceType(long value) {
        if (value < 100)
            return FireBaseHelper.getInstance().GRIEVANCE_PENSION;
        else
            return FireBaseHelper.getInstance().GRIEVANCE_GPF;
    }

    public class TrackViewHolder extends RecyclerView.ViewHolder {

        public TextView grievanceType, grievanceApplied, date, status, message;

        public TrackViewHolder(View itemView) {
            super(itemView);
            grievanceType = itemView.findViewById(R.id.textview_grievance_type);
            grievanceApplied = itemView.findViewById(R.id.textview_grievance);
            date = itemView.findViewById(R.id.textview_date);
            status = itemView.findViewById(R.id.textview_result);
            message = itemView.findViewById(R.id.textview_message);
        }
    }
}