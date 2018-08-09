package com.mycca.fragments;


import android.content.Intent;

import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.volley.VolleyError;
import com.mycca.R;
import com.mycca.tools.CustomLogger;
import com.mycca.tools.Helper;
import com.mycca.tools.VolleyHelper;

import org.json.JSONObject;


public class KYPFragment extends Fragment implements VolleyHelper.VolleyResponse {

    public KYPFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kyp, container, false);

        bindViews(view);
        init(view);
        return view;
    }

    private void bindViews(View view) {

    }

    private void init(View view) {

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


    }

    @Override
    public void onError(VolleyError volleyError) {
        Helper.getInstance().showErrorDialog(getString(R.string.try_again), getString(R.string.some_error), getActivity());
    }

    @Override
    public void onResponse(String str) {
        JSONObject jsonObject = Helper.getInstance().getJson(str);
        CustomLogger.getInstance().logDebug(jsonObject.toString());

    }
}