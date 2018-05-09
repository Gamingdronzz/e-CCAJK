package com.mycca.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mycca.R;
import com.mycca.Tools.Helper;

/**
 * Created by hp on 09-05-2018.
 */

public class RecyclerViewAdapterStates extends RecyclerView.Adapter<RecyclerViewAdapterStates.StatesViewHolder> {

    @Override
    public StatesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        StatesViewHolder statesViewHolder = new StatesViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_states, parent, false));
        return statesViewHolder;
    }

    @Override
    public void onBindViewHolder(StatesViewHolder holder, int position) {
        holder.textView.setText(Helper.getInstance().getStatelist()[position].getName());
    }

    @Override
    public int getItemCount() {
        return Helper.getInstance().getStatelist().length;
    }

    public class StatesViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public StatesViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
        }
    }

}
