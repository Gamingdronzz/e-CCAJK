package com.ccajk.Unused;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ccajk.R;
import com.ccajk.Tools.FireBaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by hp on 20-03-2018.
 */

public class RecyclerViewAdapterHistory extends RecyclerView.Adapter<RecyclerViewAdapterHistory.HistoryViewHolder> {

    ArrayList<PanAdhaarStatus> panAdhaarStatusArrayList;

    public RecyclerViewAdapterHistory(ArrayList<PanAdhaarStatus> panAdhaarStatusArrayList) {
        this.panAdhaarStatusArrayList = panAdhaarStatusArrayList;
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        HistoryViewHolder viewHolder = new HistoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_upload_history, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {
        PanAdhaarStatus panAdhaarStatus = panAdhaarStatusArrayList.get(position);
        SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy");

        holder.applied.setText("");
        holder.applied.setText(dt.format(panAdhaarStatus.getAppliedDate()));
        holder.status.setText("");
        holder.status.setText(FireBaseHelper.getInstance().getAdhaarPanStatusString(panAdhaarStatus.getStatus()));

        holder.processed.setText("");
        if (panAdhaarStatus.getProcessingDate() == null)
            holder.processed.setText("Not Yet Processed");
        else
            holder.processed.setText(dt.format(panAdhaarStatus.getProcessingDate()));

        holder.result.setText("");
        if (panAdhaarStatus.getResultDate() == null)
            holder.result.setText("Not Available");
        else
            holder.result.setText(dt.format(panAdhaarStatus.getResultDate()));

    }

    @Override
    public int getItemCount() {
        return panAdhaarStatusArrayList.size();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {

        public TextView applied, processed, result, status;

        public HistoryViewHolder(View itemView) {
            super(itemView);
            applied = itemView.findViewById(R.id.textview_applied);
            processed = itemView.findViewById(R.id.textview_processing);
            result = itemView.findViewById(R.id.textview_result);
            status = itemView.findViewById(R.id.textview_status);
        }
    }
}