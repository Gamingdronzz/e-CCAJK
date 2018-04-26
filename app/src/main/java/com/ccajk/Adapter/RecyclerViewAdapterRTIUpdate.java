package com.ccajk.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ccajk.Models.RtiModel;
import com.ccajk.R;

import java.util.ArrayList;

/**
 * Created by hp on 13-02-2018.
 */

public class RecyclerViewAdapterRTIUpdate extends RecyclerView.Adapter<RecyclerViewAdapterGrievanceUpdate.GrievanaceUpdateViewHolder> {

    ArrayList<RtiModel> rtiModelArrayList;
    Context context;


    public RecyclerViewAdapterRTIUpdate(ArrayList<RtiModel> rtiModelArrayList, Context context) {
        this.rtiModelArrayList = rtiModelArrayList;
        this.context = context;
    }

    @Override
    public RecyclerViewAdapterGrievanceUpdate.GrievanaceUpdateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerViewAdapterGrievanceUpdate.GrievanaceUpdateViewHolder viewHolder = new RecyclerViewAdapterGrievanceUpdate.GrievanaceUpdateViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_updation_item, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapterGrievanceUpdate.GrievanaceUpdateViewHolder holder, int position) {

        RtiModel rtiModel = rtiModelArrayList.get(position);
        holder.pensioner.setText(rtiModel.getName());
        holder.grievanceType.setText(rtiModel.getMobile());
    }

    @Override
    public int getItemCount() {
        return rtiModelArrayList.size();
    }

}
