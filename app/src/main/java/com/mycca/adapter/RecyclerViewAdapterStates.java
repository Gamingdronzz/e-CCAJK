package com.mycca.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mycca.R;
import com.mycca.providers.CircleDataProvider;
import com.mycca.tools.Preferences;


public class RecyclerViewAdapterStates extends RecyclerView.Adapter<RecyclerViewAdapterStates.StatesViewHolder> {

    private Context context;

    public RecyclerViewAdapterStates(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public StatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StatesViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_states, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StatesViewHolder holder, int position) {
        if (Preferences.getInstance().getStringPref(context, Preferences.PREF_LANGUAGE).equals("hi"))
            holder.textView.setText(CircleDataProvider.getInstance().getCircleData()[position].getHi());
        else
            holder.textView.setText(CircleDataProvider.getInstance().getCircleData()[position].getEn());
    }

    @Override
    public int getItemCount() {
        return CircleDataProvider.getInstance().getCircleData().length;
    }

    class StatesViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        StatesViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
        }
    }

}
