package com.mycca.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mycca.Models.State;
import com.mycca.R;
import com.mycca.Tools.Helper;

public class StatesAdapter extends BaseAdapter {

    private Context context;

    public StatesAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return Helper.getInstance().getStatelist().length;
    }

    @Override
    public Object getItem(int position) {
        return Helper.getInstance().getStatelist()[position];
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
            State state = Helper.getInstance().getStatelist()[position];
            TextView textView = convertView.findViewById(R.id.spinner_item);
            textView.setText(state.getName());
        }
        return convertView;
    }
}
