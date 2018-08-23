package com.mycca.fragments;


import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mycca.R;
import com.mycca.activity.KypUploadActivity;


public class KYPFragment extends Fragment {

    LinearLayout download, submit;
    Activity activity;

    public KYPFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kyp, container, false);
        bindViews(view);
        init();
        return view;
    }

    private void bindViews(View view) {
        download = view.findViewById(R.id.ll_download_kyp);
        submit = view.findViewById(R.id.ll_submit_kyp);
    }

    private void init() {
        activity = getActivity();
        download.setOnClickListener(v -> downloadForm());

        submit.setOnClickListener(v -> startActivity(new Intent(getActivity(), KypUploadActivity.class)));
    }

    private void downloadForm() {

        String url = "https://firebasestorage.googleapis.com/v0/b/cca-jk.appspot.com/o/AppFiles%2FKYP%2Fkyp.pdf?alt=media&token=7ef8f473-817c-4152-9d8d-3b103af59809";
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("Know Your Pensioner");
        request.setTitle("KYP Form");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "kyp");

        DownloadManager manager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        if (manager != null) {
            manager.enqueue(request);
        }
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


}