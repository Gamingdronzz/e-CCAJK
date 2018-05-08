package com.mycca.Adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mycca.Models.GrievanceModel;
import com.mycca.R;
import com.mycca.Tools.Helper;

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

        holder.textViewGrievanceType.setText(Helper.getInstance().getGrievanceCategory(grievanceModel.getGrievanceType()));
        holder.textViewgrievance.setText("");
        holder.textViewgrievance.setText(Helper.getInstance().getGrievanceString((int) grievanceModel.getGrievanceType()));
        holder.textViewDate.setText("");
        holder.textViewDate.setText(Helper.getInstance().formatDate(grievanceModel.getDate(), "MMM d, yyyy"));
        holder.textViewStatus.setText("");
        holder.textViewStatus.setText(Html.fromHtml("Status : <b>" + Helper.getInstance().getStatusList()[(int) grievanceModel.getGrievanceStatus()]+ "</b>"));
        holder.textViewMessage.setText("");
        if (grievanceModel.getExpanded()) {
            holder.linearLayoutExpandableArea.setVisibility(View.VISIBLE);
            holder.textViewgrievance.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_arrow_drop_up_black_24dp,0);
        } else {
            holder.linearLayoutExpandableArea.setVisibility(View.GONE);
            holder.textViewgrievance.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_arrow_drop_down_black_24dp,0);
        }
        if (grievanceModel.getMessage() != null && !grievanceModel.getMessage().isEmpty())
            holder.textViewMessage.setText(grievanceModel.getMessage());
        else
            holder.textViewMessage.setText("Nil");
    }

    @Override
    public int getItemCount() {
        return grievanceModels.size();
    }

    public class TrackViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewGrievanceType, textViewgrievance, textViewDate, textViewStatus, textViewMessage;
        public LinearLayout linearLayoutExpandableArea;

        public TrackViewHolder(View itemView) {
            super(itemView);
            textViewgrievance = itemView.findViewById(R.id.textview_grievance);

            textViewGrievanceType = itemView.findViewById(R.id.textview_grievance_type);
            textViewGrievanceType.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_grievance,0,0, 0);

            textViewDate = itemView.findViewById(R.id.textview_date);
            textViewDate.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_calendar, 0,0,0);
            textViewStatus = itemView.findViewById(R.id.textview_result);
            textViewStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_inspection, 0);
            textViewMessage = itemView.findViewById(R.id.textview_message);
            textViewMessage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_message, 0,0,0);
            linearLayoutExpandableArea = itemView.findViewById(R.id.expandable_area_track_grievance);
        }
    }
}