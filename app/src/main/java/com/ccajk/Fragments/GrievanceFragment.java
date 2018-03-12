package com.ccajk.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.content.res.AppCompatResources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.ccajk.R;


public class GrievanceFragment extends Fragment {

    TextView heading;
    private ImageView ppo, mob, details, type, submittedby, attach;
    Spinner grievanceType, grievanceSubmitedBy;

    String[] list = {"Type 1", "Type 2", "Type 3"};

    public GrievanceFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grievance, container, false);
        Bundle bundle = this.getArguments();
        int gtype = bundle.getInt("Category");
        init(view, gtype);
        return view;
    }

    private void init(View view, int gtype) {
        heading = view.findViewById(R.id.heading);
        if (gtype == 0)
            heading.setText("Register Pension Grievance");
        else
            heading.setText("Register GPF Grievance");

        ppo = view.findViewById(R.id.image_ppo);
        ppo.setImageDrawable(AppCompatResources.getDrawable(this.getContext(), R.drawable.ic_person_black_24dp));

        mob = view.findViewById(R.id.image_mobile);
        mob.setImageDrawable(AppCompatResources.getDrawable(this.getContext(), R.drawable.ic_phone_android_black_24dp));

        details = view.findViewById(R.id.image_details);
        details.setImageDrawable(AppCompatResources.getDrawable(this.getContext(), R.drawable.ic_details_black_24dp));

        type = view.findViewById(R.id.image_type);
        type.setImageDrawable(AppCompatResources.getDrawable(this.getContext(), R.drawable.ic_sentiment_dissatisfied_black_24dp));

        grievanceType = view.findViewById(R.id.spinner_type);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, list);
        grievanceType.setAdapter(arrayAdapter);


        submittedby = view.findViewById(R.id.image_submitted_by);
        submittedby.setImageDrawable(AppCompatResources.getDrawable(this.getContext(), R.drawable.ic_person_black_24dp));

        grievanceSubmitedBy = view.findViewById(R.id.spinner_submitted_by);
        grievanceSubmitedBy.setPrompt("Grievance Submitted By");
        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, list);
        grievanceSubmitedBy.setAdapter(arrayAdapter1);

        attach = view.findViewById(R.id.image_attach);
        attach.setImageDrawable(AppCompatResources.getDrawable(this.getContext(), R.drawable.ic_attach_file_black_24dp));
    }

}
