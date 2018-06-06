package com.mycca.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mycca.Activity.MainActivity;
import com.mycca.R;

/**
 * Created by balpreet on 5/6/2018.
 */

public class AboutUsFragment extends Fragment {
    ImageView imageViewLogo;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_us, container, false);
        imageViewLogo = view.findViewById(R.id.img_about_logo);
        imageViewLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getActivity();
                BrowserFragment fragment = new BrowserFragment();
                Bundle bundle = new Bundle();
                bundle.putString("url", "http://gamingdronzz.com");
                mainActivity.showFragment("Gaming Dronzz", fragment, bundle);

            }
        });
        return view;
    }
}
