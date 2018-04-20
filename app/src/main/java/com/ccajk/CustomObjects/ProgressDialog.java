package com.ccajk.CustomObjects;

/**
 * Created by balpreet on 4/20/2018.
 */


import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ccajk.R;

public class ProgressDialog {

    private View view;
    private TextView msg;
    private ProgressBar progressBar;
    private RelativeLayout parent;
    private AlertDialog.Builder builder;
    private Dialog dialog;
    boolean isVisible = false;

    public ProgressDialog(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.progress_dialog_layout, null,false);
        init();
    }

    void init() {
        msg = view.findViewById(R.id.msg);
        progressBar =  view.findViewById(R.id.loader);
        parent =  view.findViewById(R.id.parent_progress_layout);
        builder = new AlertDialog.Builder(view.getContext());
    }

    public void light(String message) {
        setBackgroundColor(view.getResources().getColor(R.color.colorWhite));
        setProgressColor(view.getResources().getColor(R.color.colorBlack));
        setMessageColor(view.getResources().getColor(R.color.colorBlack));
        setMessage(message);
        show();
    }

    public void dark(String message) {
        setBackgroundColor(view.getResources().getColor(R.color.colorBlack));
        setProgressColor(view.getResources().getColor(R.color.colorWhite));
        setMessageColor(view.getResources().getColor(R.color.colorWhite));
        setMessage(message);
        show();
    }

    public ProgressDialog setBackgroundDrawable(Drawable drawable) {
        parent.setBackgroundDrawable(drawable);
        parent.setPadding(30, 30, 30, 30);
        return this;
    }

    public ProgressDialog setBackgroundColor(int color) {
        parent.setBackgroundColor(color);
        return this;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ProgressDialog setProgressColor(int color) {
        progressBar.setIndeterminateTintList(ColorStateList.valueOf(color));
        return this;
    }

    public boolean isShowing() {
        return isVisible;
    }

    public ProgressDialog setMessage(String message) {
        msg.setText(message);
        return this;
    }

    public ProgressDialog setMessageColor(int color) {
        msg.setTextColor(color);
        return this;
    }

    public void show() {

        if (view.getParent() == null)
            builder.setView(view);
        dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
        isVisible = true;
    }

    public void dismiss() {
        dialog.dismiss();
        isVisible = false;
        if (view.getParent() != null)
            ((ViewGroup) view.getParent()).removeView(view);
    }
}

