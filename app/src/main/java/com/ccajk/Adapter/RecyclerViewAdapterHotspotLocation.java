package com.ccajk.Adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ccajk.Models.LocationModel;
import com.ccajk.R;

import java.util.ArrayList;

/**
 * Created by hp on 15-09-2017.
 */

public class RecyclerViewAdapterHotspotLocation extends RecyclerView.Adapter<RecyclerViewAdapterHotspotLocation.MyViewHolder> {

    ArrayList<LocationModel> locationArray;

    public RecyclerViewAdapterHotspotLocation(ArrayList<LocationModel> locationArray) {
        //setHasStableIds(true);
        this.locationArray = locationArray;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_locations, parent, false));
        return myViewHolder;

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String location = locationArray.get(position).getLocationName();
        String district = locationArray.get(position).getDistrict();
        String stateId = locationArray.get(position).getStateID();

        //holder.textViewLocationName.setText(location + "  (" + FireBaseHelper.getInstance().getState(stateId) + ")");

        holder.textViewLocationName.setText(Html.fromHtml("<u>"+location+"</u>"));
        holder.textViewDistrict.setText(district);

    }

    @Override
    public int getItemCount() {
        return locationArray.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewLocationName;
        public  TextView textViewDistrict;
        private TextView textViewGetDirections;


        public MyViewHolder(View itemView) {
            super(itemView);
            this.textViewLocationName = itemView.findViewById(R.id.textview_location_name);
            textViewDistrict = itemView.findViewById(R.id.textview_location_district);
            textViewGetDirections = itemView.findViewById(R.id.textview_get_directions);
            textViewGetDirections.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_directions_black_24dp,0,0);
            //this.textViewDistrict = itemView.findViewById(R.id.state);
        }
    }
}
