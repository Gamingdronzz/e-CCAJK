package com.ccajk.Tools;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.TextInputLayout;
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
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.ccajk.Activity.MainActivity;
import com.ccajk.Activity.TrackResultActivity;
import com.ccajk.CustomObjects.ProgressDialog;
import com.ccajk.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


/**
 * Created by hp on 18-04-2018.
 */

public class PopUpWindows {

    private static PopUpWindows _instance;
    final String TAG = "Popup";
    String hint = "Pensioner Code";

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


    public void showLoginPopup(final MainActivity context, final View parent) {
        final ImageView ppo, pwd, close;

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
        close.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_close_black_24dp));
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

                Helper.getInstance().hideKeyboardFrom(context,parent);
                final String id = autoCompleteTextView.getText().toString();
                final String password = editText.getText().toString();
                if (!Helper.getInstance().checkInput(id)) {
                    Toast.makeText(context, "Please input User ID", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Helper.getInstance().checkInput(password)) {
                    Toast.makeText(context, "Please input Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                final ProgressDialog progressDialog = Helper.getInstance().getProgressWindow(context,"Logging In...");
                progressDialog.show();
                FireBaseHelper.getInstance().databaseReference.child(FireBaseHelper.getInstance().ROOT_STAFF).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot == null) {
                            Toast.makeText(context, "We are getting things fixed", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            return;
                        }
                        Log.d(TAG, "onDataChange: DataSnapshot = " + dataSnapshot);
                        Log.d(TAG, "onDataChange: Password = " + dataSnapshot.child(FireBaseHelper.getInstance().ROOT_PASSWORD).getValue());
                        if (dataSnapshot.getValue() == null) {
                            context.OnLoginFailure("No user found");
                            progressDialog.dismiss();
                        } else {
                            if (dataSnapshot.child(FireBaseHelper.getInstance().ROOT_PASSWORD).getValue().toString().equals(password)) {
                                long type = (long) dataSnapshot.child(FireBaseHelper.getInstance().ROOT_TYPE).getValue();
                                Log.d(TAG, "onDataChange: type: " + type);
                                context.OnLoginSuccesful(id,type);
                                progressDialog.dismiss();
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
        final TextInputLayout textInputLayout;
        View popupView = LayoutInflater.from(context).inflate(R.layout.dialog_track, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        editText = popupView.findViewById(R.id.edittext_pcode);
        textInputLayout = popupView.findViewById(R.id.text_input_layout);

        RadioGroup radioGroup = popupView.findViewById(R.id.groupNumberType);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButtonPensioner:
                        hint = "Pensioner Code";
                        editText.setFilters(Helper.getInstance().limitInputLength(15));
                        break;
                    //TODO
                    //set place holder format
                    case R.id.radioButtonHR:
                        hint = "HR Number";
                        editText.setFilters(Helper.getInstance().limitInputLength(10));
                        break;
                    case R.id.radioButtonStaff:
                        hint = "Staff Number";
                        editText.setFilters(Helper.getInstance().limitInputLength(12));
                }
                editText.setText("");
                textInputLayout.setHint(hint);
            }
        });

        Button track = popupView.findViewById(R.id.btn_check_status);
        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = editText.getText().toString().trim();
                if (code.length() < 15 && hint.equals("Pensioner Code")) {
                    Toast.makeText(context, "Invalid Pensioner code!", Toast.LENGTH_LONG).show();
                } else if (code.length() < 10 && hint.equals("HR Number")) {
                    Toast.makeText(context, "Invalid HR Number!", Toast.LENGTH_LONG).show();
                } else if (code.length() < 12 && hint.equals("Staff Number")) {
                    Toast.makeText(context, "Invalid Staff Number!", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(context, TrackResultActivity.class);
                    intent.putExtra("Code", editText.getText().toString());
                    context.startActivity(intent);
                }
                editText.requestFocus();
            }
        });

        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.update();
        popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }


    public AlertDialog.Builder getConfirmationDialog(Activity context, View view, DialogInterface.OnClickListener yes) {
        AlertDialog.Builder confirmDialog = new AlertDialog.Builder(context);
        confirmDialog.setView(view);
        confirmDialog.setTitle("Confirm Input Before Submission");
        confirmDialog.setPositiveButton("Upload", yes);
        confirmDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        confirmDialog.show();
        return confirmDialog;
    }

}
