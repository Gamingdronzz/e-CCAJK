package com.ccajk.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.ccajk.Activity.PanAdhaarHistoryActivity;
import com.ccajk.R;

public class CheckFragment extends Fragment {
    int type;

    Button update, check;
    EditText pcode;

    public CheckFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check, container, false);
        Bundle bundle = this.getArguments();
        type = bundle.getInt("UploadType");
        init(view, type);
        return view;
    }

    private void init(View view, final int type) {
        pcode = view.findViewById(R.id.edittext_pcode);

        update = view.findViewById(R.id.btn_update_now);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new PanAdhaarUploadFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("UploadType", type);
                fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.contentPanel, fragment).commit();
            }
        });

        check = view.findViewById(R.id.btn_check_status);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), PanAdhaarHistoryActivity.class);
                intent.putExtra("UploadType", type);
                intent.putExtra("PensionerCode", pcode.getText().toString());
                startActivity(intent);
            }
        });
    }


}
