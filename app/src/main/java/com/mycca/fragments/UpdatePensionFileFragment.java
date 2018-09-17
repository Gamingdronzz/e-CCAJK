package com.mycca.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import com.mycca.R;
import com.mycca.adapter.GenericSpinnerAdapter;
import com.mycca.providers.CircleDataProvider;


public class UpdatePensionFileFragment extends Fragment {

    Spinner spinnerCircles, spinnerFileStatus;
    TextInputEditText etPensionerCode, etMobile;
    Button submit;

    public UpdatePensionFileFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_pension_file, container, false);
        bindViews(view);
        init();
        return view;
    }

    private void bindViews(View view) {
        spinnerCircles = view.findViewById(R.id.spinner_update_pension_circles);
        spinnerFileStatus = view.findViewById(R.id.spinner_update_pension_status);
        etPensionerCode = view.findViewById(R.id.et_update_pension_pcode);
        etMobile = view.findViewById(R.id.et_update_pension_mobile);
        submit = view.findViewById(R.id.button_update_pension);
    }

    private void init() {
        etPensionerCode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_person_black_24dp, 0, 0, 0);
        etMobile.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_phone_android_black_24dp, 0, 0, 0);

        GenericSpinnerAdapter statesAdapter = new GenericSpinnerAdapter<>(getContext(), CircleDataProvider.getInstance().getActiveCircleData());
        spinnerCircles.setAdapter(statesAdapter);

        submit.setOnClickListener(v -> {

        });
    }
}
