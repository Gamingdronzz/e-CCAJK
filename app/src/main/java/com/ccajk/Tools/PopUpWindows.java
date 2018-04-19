package com.ccajk.Tools;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.content.res.AppCompatResources;
import android.util.Log;
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

import com.ccajk.Activity.MainActivity;
import com.ccajk.Activity.TrackResultActivity;
import com.ccajk.R;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import static com.ccajk.Tools.FireBaseHelper.ROOT_STAFF;

/**
 * Created by hp on 18-04-2018.
 */

public class PopUpWindows {

    private static PopUpWindows _instance;
    final String TAG = "Popup";

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


    public void showLoginPopup(final MainActivity context, View parent, final NavigationView navigationView) {
        final ImageView ppo, pwd;
        ImageButton close;
        final AutoCompleteTextView autoCompleteTextView;
        final EditText editText;


        View popupView = LayoutInflater.from(context).inflate(R.layout.dialog_login, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        ppo = popupView.findViewById(R.id.image_ppo);
        ppo.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_person_black_24dp));
        pwd = popupView.findViewById(R.id.image_pwd);
        pwd.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_password));
        autoCompleteTextView = popupView.findViewById(R.id.ppo);
        editText = popupView.findViewById(R.id.password);

        close = popupView.findViewById(R.id.close);
        close.setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.ic_close_black_24dp));
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
                //TODO
                //Show Progress bar before making the call to firebase
                final String id = autoCompleteTextView.getText().toString();
                final String password = editText.getText().toString();
                if (!Helper.getInstance().checkInput(id)) {
                    Toast.makeText(context,"Please input User ID",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Helper.getInstance().checkInput(password)) {
                    Toast.makeText(context,"Please input Password",Toast.LENGTH_SHORT).show();
                    return;
                }
                FireBaseHelper.getInstance().databaseReference.child(ROOT_STAFF).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot == null) {
                            Toast.makeText(context, "We are getting things fixed", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Log.d(TAG, "onDataChange: DataSnapshot = " + dataSnapshot);
                        Log.d(TAG, "onDataChange: Password = " + dataSnapshot.child(FireBaseHelper.getInstance().ROOT_PASSWORD).getValue());
                        if (dataSnapshot.getValue() == null) {
                            context.OnLoginFailure("No user found");
                        } else {
                            if (dataSnapshot.child(FireBaseHelper.getInstance().ROOT_PASSWORD).getValue().toString().equals(password)) {
                                long type = (long) dataSnapshot.child(FireBaseHelper.getInstance().ROOT_TYPE).getValue();
                                Log.d(TAG, "onDataChange: type: " + type);
                                context.OnLoginSuccesful(type);
                                popupWindow.dismiss();
                            } else {
                                context.OnLoginFailure("Password Mismatch");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
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
}
