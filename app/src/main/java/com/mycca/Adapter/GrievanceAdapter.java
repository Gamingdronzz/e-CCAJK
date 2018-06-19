package com.mycca.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mycca.Models.GrievanceType;
import com.mycca.R;

import java.util.ArrayList;

public class GrievanceAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<GrievanceType> types;

    public GrievanceAdapter(Context context, ArrayList<GrievanceType> types) {
        this.context = context;
        this.types = types;
    }

    @Override
    public int getCount() {
        return types.size();
    }

    @Override
    public Object getItem(int position) {
        return types.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            convertView = inflater.inflate(R.layout.simple_spinner, parent, false);
            GrievanceType type = types.get(position);
            TextView textView = convertView.findViewById(R.id.spinner_item);
            textView.setText(type.getName());
        }
        return convertView;
    }
}
