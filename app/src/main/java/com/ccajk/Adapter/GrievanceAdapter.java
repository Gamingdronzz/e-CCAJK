package com.ccajk.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ccajk.Models.GrievanceType;
import com.ccajk.R;

import java.util.ArrayList;

/**
 * Created by hp on 16-03-2018.
 */

public class GrievanceAdapter extends BaseAdapter {
    Context context;
    ArrayList<GrievanceType> types;

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
        convertView = inflater.inflate(R.layout.simple_spinner, parent, false);
        GrievanceType type=types.get(position);
        TextView textView = convertView.findViewById(R.id.spinner_item);
        textView.setText(type.getName());
        return convertView;
    }
}
