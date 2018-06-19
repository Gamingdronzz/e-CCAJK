package com.mycca.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mycca.R;
import com.mycca.Tools.Helper;


public class RecyclerViewAdapterStates extends RecyclerView.Adapter<RecyclerViewAdapterStates.StatesViewHolder> {

    @NonNull
    @Override
    public StatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StatesViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_states, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StatesViewHolder holder, int position) {
        holder.textView.setText(Helper.getInstance().getStatelist()[position].getName());
    }

    @Override
    public int getItemCount() {
        return Helper.getInstance().getStatelist().length;
    }

    class StatesViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        StatesViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
        }
    }

}
