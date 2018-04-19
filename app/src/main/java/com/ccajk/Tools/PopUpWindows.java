package com.ccajk.Tools;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.content.res.AppCompatResources;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.ccajk.Activity.TrackResultActivity;
import com.ccajk.R;

/**
 * Created by hp on 18-04-2018.
 */

public class PopUpWindows {

    private static PopUpWindows _instance;

    public PopUpWindows() {
        _instance = this;
    }

    public static PopUpWindows getInstance() {
        if (_instance == null) {
            return new PopUpWindows();
        } else {
            return _instance;
        }
    }


    public void showLoginPopup(final Activity context, View parent, final NavigationView navigationView) {
        ImageView ppo, pwd;
        ImageButton close;
        final AutoCompleteTextView autoCompleteTextView;
        final EditText editText;
        final View mProgressView;

        View popupView = LayoutInflater.from(context).inflate(R.layout.dialog_login, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        ppo = popupView.findViewById(R.id.image_ppo);
        ppo.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_person_black_24dp));
        pwd = popupView.findViewById(R.id.image_pwd);
        pwd.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_password));
        autoCompleteTextView = popupView.findViewById(R.id.ppo);
        editText = popupView.findViewById(R.id.password);
        mProgressView = popupView.findViewById(R.id.login_progress);
        close = popupView.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        Button signin = popupView.findViewById(R.id.sign_in_button);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FireBaseHelper.getInstance().Login(autoCompleteTextView.getText().toString(),
                        editText.getText().toString(),context,navigationView);

            }
        });

        popupWindow.setFocusable(true);
        popupWindow.update();
        popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }


    public void showTrackWindow(final Activity context, View parent) {
        final EditText editText;
        View popupView = LayoutInflater.from(context).inflate(R.layout.dialog_track, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        editText = popupView.findViewById(R.id.edittext_pcode);

        Button track = popupView.findViewById(R.id.btn_check_status);
        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().isEmpty())
                    Toast.makeText(context, "Empty code!", Toast.LENGTH_LONG).show();
                else {
                    Intent intent = new Intent(context, TrackResultActivity.class);
                    intent.putExtra("pensionerCode", editText.getText().toString());
                    context.startActivity(intent);
                }

            }
        });


        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.update();
        popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }


    public AlertDialog.Builder getConfirmationDialog(Activity context, View view) {
        AlertDialog.Builder confirmDialog = new AlertDialog.Builder(context);
        confirmDialog.setView(view);
        confirmDialog.setTitle("Confirm Input Before Submission");
        return confirmDialog;
    }



    /*@Override
    public void RequestLogin(final String pensionerCode, final String password) {

        changePrefrences(pensionerCode, "Name");
        *//*DatabaseReference dbref = databaseReference.child("user").child(pensionerCode);
        Log.d(TAG, "RequestLogin: ");
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: " + dataSnapshot.toString());
                if (dataSnapshot == null) {
                    OnUserNotExist();
                }
                else if (dataSnapshot != null) {
                    if(dataSnapshot.child("password").exists()) {
                        String dbpassword = dataSnapshot.child("password").getValue().toString();
                        if (dbpassword.equals(password)) {
                            OnLoginSuccesful(dataSnapshot);
                        } else {
                            OnLoginFailure();
                        }
                    }
                    else
                    {
                        OnLoginFailure();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Error");
                OnUserNotExist();
            }
        });*//*
    }

    @Override
    public void OnLoginSuccesful(DataSnapshot dataSnapshot) {
        String username = dataSnapshot.child("name").getValue().toString();
        String ppo = dataSnapshot.child("emp_id").getValue().toString();
        changePrefrences(ppo, username);
    }

    @Override
    public void OnLoginFailure() {

    }

    @Override
    public void OnUserNotExist() {
        Log.d(TAG, "User does not exist");
    }

    private void changePrefrences(String ppo, String user) {
        Preferences.getInstance().setSignedIn(this, true);
        Preferences.getInstance().setPpo(this, ppo);
        navigationView.getMenu().findItem(R.id.staff_login).setVisible(false);
        navigationView.getMenu().findItem(R.id.staff_panel).setVisible(true);
    }
*/
}
