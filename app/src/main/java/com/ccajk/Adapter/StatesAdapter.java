package com.ccajk.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ccajk.Models.State;
import com.ccajk.R;

import java.util.ArrayList;

/**
 * Created by hp on 16-03-2018.
 */

public class StatesAdapter extends BaseAdapter {
    Context context;
    ArrayList<State> states;

    public StatesAdapter(Context context, ArrayList<State> states) {
        this.context = context;
        this.states = states;
    }

    @Override
    public int getCount() {
        return states.size();
    }

    @Override
    public Object getItem(int position) {
        return states.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.simple_spinner, parent, false);
        State state = states.get(position);
        TextView textView = convertView.findViewById(R.id.spinner_item);
        textView.setText(state.getName());
        return convertView;
    }
}
