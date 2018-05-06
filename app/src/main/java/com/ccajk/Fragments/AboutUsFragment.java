package com.ccajk.Fragments;

import android.os.Bundle;
import android.provider.Browser;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ccajk.Activity.MainActivity;
import com.ccajk.R;

/**
 * Created by balpreet on 5/6/2018.
 */

public class AboutUsFragment extends Fragment {
    LinearLayout linearLayoutLogo;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_us, container, false);
        linearLayoutLogo = view.findViewById(R.id.layout_logo);
        linearLayoutLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getActivity();
                BrowserFragment fragment = new BrowserFragment();
                Bundle bundle = new Bundle();
                bundle.putString("url", "http://gamingdronzz.com");
                mainActivity.ShowFragment("Gaming Dronzz",fragment,bundle);

            }
        });
        return view;
    }
}
