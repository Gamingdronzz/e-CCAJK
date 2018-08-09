
package com.mycca.tools;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.mycca.R;
import com.mycca.activity.SplashActivity;
import com.mycca.activity.TrackGrievanceResultActivity;
import com.mycca.app.AppController;
import com.mycca.custom.FancyAlertDialog.FancyAlertDialog;
import com.mycca.custom.FancyAlertDialog.FancyAlertDialogType;
import com.mycca.custom.FancyAlertDialog.IFancyAlertDialogListener;
import com.mycca.custom.FancyAlertDialog.Icon;
import com.mycca.custom.Progress.ProgressDialog;
import com.mycca.custom.customImagePicker.ImagePicker;
import com.mycca.models.GrievanceType;
import com.mycca.models.State;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/*
 * Created by hp on 09-02-2018.
 */


public class Helper {

    public static boolean versionChecked = false;
    private static Helper _instance;
    public final String SUCCESS = "success";
    private final String TAG = "Helper";
    private String hint = "Pensioner Code";

    private State stateList[] = {
            new State("05", "Jammu & Kashmir"),
            new State("100", "Haryana")
    };

    private State stateListJK[] = {new State("05", "Jammu & Kashmir")};

    private GrievanceType pensionGrievanceTypes[] = {
            new GrievanceType(AppController.getResourses().getString(R.string.change_of_pda), 0),
            new GrievanceType(AppController.getResourses().getString(R.string.correction_in_ppo), 1),
            new GrievanceType(AppController.getResourses().getString(R.string.wrong_fixation), 2),
            new GrievanceType(AppController.getResourses().getString(R.string.non_updation_da), 3),
            new GrievanceType(AppController.getResourses().getString(R.string.non_payment_monthly), 4),
            new GrievanceType(AppController.getResourses().getString(R.string.non_payment_medical), 5),
            new GrievanceType(AppController.getResourses().getString(R.string.non_starting_pension), 6),
            new GrievanceType(AppController.getResourses().getString(R.string.non_revision), 7),
            new GrievanceType(AppController.getResourses().getString(R.string.request_cgies), 8),
            new GrievanceType(AppController.getResourses().getString(R.string.excess_short_payment), 9),
            new GrievanceType(AppController.getResourses().getString(R.string.enhancement_on_75_80), 10),
            new GrievanceType(AppController.getResourses().getString(R.string.other_pension_gr), 11)
    };

    private GrievanceType gpfGrievanceTypes[] = {
            new GrievanceType(AppController.getResourses().getString(R.string.gpf_final_not_received), 100),
            new GrievanceType(AppController.getResourses().getString(R.string.correction_name), 101),
            new GrievanceType(AppController.getResourses().getString(R.string.change_nomination), 102),
            new GrievanceType(AppController.getResourses().getString(R.string.gpf_acc_not_transferred), 103),
            new GrievanceType(AppController.getResourses().getString(R.string.details_of_gpf_deposit), 104),
            new GrievanceType(AppController.getResourses().getString(R.string.non_payment_gpf_withdrawal), 105),
            new GrievanceType(AppController.getResourses().getString(R.string.other_gpf_gr), 106)
    };

    public Helper() {
        _instance = this;
    }

    public static Helper getInstance() {
        if (_instance == null) {
            return new Helper();
        } else {
            return _instance;
        }
    }

    public String getPlayStoreURL() {
        return "market://details?id=com.mycca";
    }

    String getConnectionCheckURL() {
        return "https://www.google.co.in/";
    }

    public String getAPIUrl() {
        boolean debugMode = true;
        if (debugMode) {
            return "http://jknccdirectorate.com/api/cca/debug/v1/";
        } else {
            return "http://jknccdirectorate.com/api/cca/release/v1/";
        }

    }

    public State[] getStateList() {
        return stateList;
    }

    public State[] getStateListJK() {
        return stateListJK;
    }

    public String getStateName(String stateId) {
        for (State s : stateList) {
            if (s.getCircleCode().equals(stateId))
                return s.getName();
        }
        return null;
    }

    public GrievanceType[] getPensionGrievanceTypeList() {
        return pensionGrievanceTypes;
    }

    public GrievanceType[] getGPFGrievanceTypeList() {
        return gpfGrievanceTypes;
    }

    public String getGrievanceString(long id) {
        switch ((int) id) {
            case 0:
                return AppController.getResourses().getString(R.string.change_of_pda);
            case 1:
                return AppController.getResourses().getString(R.string.correction_in_ppo);
            case 2:
                return AppController.getResourses().getString(R.string.wrong_fixation);
            case 3:
                return AppController.getResourses().getString(R.string.non_updation_da);
            case 4:
                return AppController.getResourses().getString(R.string.non_payment_monthly);
            case 5:
                return AppController.getResourses().getString(R.string.non_payment_medical);
            case 6:
                return AppController.getResourses().getString(R.string.non_starting_pension);
            case 7:
                return AppController.getResourses().getString(R.string.non_revision);
            case 8:
                return AppController.getResourses().getString(R.string.request_cgies);
            case 9:
                return AppController.getResourses().getString(R.string.excess_short_payment);
            case 10:
                return AppController.getResourses().getString(R.string.enhancement_on_75_80);
            case 11:
                return AppController.getResourses().getString(R.string.other_pension_gr);
            case 100:
                return AppController.getResourses().getString(R.string.gpf_final_not_received);
            case 101:
                return AppController.getResourses().getString(R.string.correction_name);
            case 102:
                return AppController.getResourses().getString(R.string.change_nomination);
            case 103:
                return AppController.getResourses().getString(R.string.gpf_acc_not_transferred);
            case 104:
                return AppController.getResourses().getString(R.string.details_of_gpf_deposit);
            case 105:
                return AppController.getResourses().getString(R.string.non_payment_gpf_withdrawal);
            case 106:
                return AppController.getResourses().getString(R.string.other_gpf_gr);
        }
        return null;
    }

    public String getGrievanceCategory(long id) {
        if (id < 100)
            return AppController.getResourses().getString(R.string.pension);
        else
            return AppController.getResourses().getString(R.string.gpf);
    }


    public String getStatusString(long status) {
        switch ((int) status) {
            case 0:
                return AppController.getResourses().getString(R.string.submitted);
            case 1:
                return AppController.getResourses().getString(R.string.under_process);
            case 2:
                return AppController.getResourses().getString(R.string.resolved);
        }
        return null;
    }

    public String formatDate(Date date, String format) {
        SimpleDateFormat dt = new SimpleDateFormat(format, Locale.getDefault());
        return dt.format(date);
    }

    public class DateFormat {
        public static final String DD_MM_YYYY = "dd MMM, yyyy";
    }

    public InputFilter[] limitInputLength(int length) {
        return new InputFilter[]{new InputFilter.LengthFilter(length)};
    }

    public boolean onLatestVersion(DataSnapshot dataSnapshot, final Activity activity) {
        long newVersion;
        int version = getAppVersion(activity);
        if (dataSnapshot.getValue() == null) {
            CustomLogger.getInstance().logDebug("onLatestVersion: Data snapshot null");
            showMaintenanceDialog(activity);
            return false;
        }
        try {
            newVersion = (long) dataSnapshot.getValue();
        } catch (Exception e) {
            e.printStackTrace();
            showMaintenanceDialog(activity);
            return false;
        }

        if (version == -1 || newVersion == version) {
            versionChecked = true;
            return true;
        } else {
            showUpdateDialog(activity);
        }
        return false;
    }

    public int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void showGooglePlayStore(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getPlayStoreURL()));
        activity.startActivity(intent);

    }

    public String getJsonFromObject(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    public Object getObjectFromJson(String json, Type type) {
        Gson gson = new Gson();
        return gson.fromJson(json, type);
    }

    public ProgressDialog getProgressWindow(final Activity context, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        return progressDialog;
    }

    public void setLocale(Context context) {
        Locale locale;
        String lang = Preferences.getInstance().getStringPref(context, Preferences.PREF_LANGUAGE);
        if (lang != null)
            locale = new Locale(lang);
        else
            locale = new Locale("en");
        Locale.setDefault(locale);
        Resources resources = context.getApplicationContext().getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

    public boolean checkInput(String input) {

        CustomLogger.getInstance().logDebug("checkInput: = " + input);
        boolean result;
        result = !(input == null || input.trim().isEmpty());
        CustomLogger.getInstance().logDebug("checkInput: result = " + result);
        return result;
    }

    public void hideKeyboardFrom(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)
                activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View focusedView = activity.getCurrentFocus();
        if (inputMethodManager != null && focusedView != null) {
            inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }
    }

    public ImagePicker showImageChooser(ImagePicker imagePicker, Activity activity, boolean cropimage, ImagePicker.Callback callback) {
        if (imagePicker == null) {
            imagePicker = new ImagePicker();
        }
        imagePicker.setTitle(AppController.getResourses().getString(R.string.pick_image_intent_chooser_title));
        imagePicker.setCropImage(cropimage);
        imagePicker.startChooser(activity, callback);
        return imagePicker;
    }

    public boolean isTab(Context context) {
        boolean isTab = (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
        CustomLogger.getInstance().logDebug("Tab = " + isTab);
        return isTab;
    }

    public void showFancyAlertDialog(Activity activity,
                                     String message,
                                     String title,
                                     String positiveButtonText, IFancyAlertDialogListener positiveButtonOnClickListener,
                                     String negativeButtonText, IFancyAlertDialogListener negativeButtonOnClickListener,
                                     FancyAlertDialogType fancyAlertDialogType) {
        if (title == null) {
            title = AppController.getResourses().getString(R.string.app_name);
        }
        if (message == null) {
            CustomLogger.getInstance().logDebug("showFancyAlertDialog: Message cant be null");
            return;
        }
        int bgColor = 1;
        int icon = 1;
        switch (fancyAlertDialogType) {
            case ERROR:
                bgColor = Color.parseColor("#aa0000");
                icon = R.drawable.ic_sentiment_dissatisfied_black_24dp;
                break;
            case SUCCESS:
                bgColor = Color.parseColor("#00aa00");
                icon = R.drawable.ic_check_black_24dp;
                break;
            case WARNING:
                bgColor = Color.parseColor("#E2AB04");
                icon = R.drawable.ic_error_outline_black_24dp;
                break;
        }

        new FancyAlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setBackgroundColor(bgColor)
                .setNegativeBtnText(negativeButtonText)
                .setPositiveBtnText(positiveButtonText)
                .isCancellable(false)
                .setPositiveBtnBackground(bgColor)
                .setIcon(icon, Icon.Visible)
                .OnNegativeClicked(negativeButtonOnClickListener)
                .OnPositiveClicked(positiveButtonOnClickListener)
                .build();
    }

    public void showUpdateDialog(final Activity activity) {
        showFancyAlertDialog(activity,
                AppController.getResourses().getString(R.string.update_available),
                AppController.getResourses().getString(R.string.app_name),
                AppController.getResourses().getString(R.string.update),
                () -> {
                    showGooglePlayStore(activity);
                    activity.finish();
                },
                AppController.getResourses().getString(android.R.string.cancel),
                activity::finish,
                FancyAlertDialogType.WARNING);

    }

    public void showMaintenanceDialog(Activity activity) {
        showFancyAlertDialog(activity,
                AppController.getResourses().getString(R.string.app_maintenance),
                AppController.getResourses().getString(R.string.app_name),
                AppController.getResourses().getString(R.string.ok),
                activity::finish,
                null, null,
                FancyAlertDialogType.WARNING);
    }

    public void showErrorDialog(String message, String title, Activity activity) {
        showFancyAlertDialog(activity,
                message,
                title,
                AppController.getResourses().getString(R.string.ok),
                null,
                null,
                null,
                FancyAlertDialogType.ERROR);
    }

    public void showReloadWarningDialog(Activity context, IFancyAlertDialogListener positiveButtonOnClickListener) {
        showFancyAlertDialog(context,
                AppController.getResourses().getString(R.string.reload_req),
                AppController.getResourses().getString(R.string.reload_app),
                AppController.getResourses().getString(R.string.reload),
                positiveButtonOnClickListener,
                AppController.getResourses().getString(android.R.string.cancel),
                () -> {
                },
                FancyAlertDialogType.WARNING);
    }

    public void reloadApp(Activity activity) {
        Intent intent = new Intent(activity, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    public void showTrackWindow(final Activity context, View parent) {
        final EditText editText;
        final TextInputLayout textInputLayout;
        View popupView = LayoutInflater.from(context).inflate(R.layout.dialog_track_grievance, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        editText = popupView.findViewById(R.id.edittext_pcode);
        textInputLayout = popupView.findViewById(R.id.text_input_layout);

        RadioGroup radioGroup = popupView.findViewById(R.id.groupNumberType);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radioButtonPensioner:
                    hint = AppController.getResourses().getString(R.string.p_code);
                    editText.setFilters(Helper.getInstance().limitInputLength(15));
                    break;
                case R.id.radioButtonHR:
                    hint = AppController.getResourses().getString(R.string.hr_num);
                    editText.setFilters(new InputFilter[]{});
                    break;
                case R.id.radioButtonStaff:
                    hint = AppController.getResourses().getString(R.string.staff_num);
                    editText.setFilters(new InputFilter[]{});
            }
            editText.setText("");
            textInputLayout.setHint(hint);
        });

        Button track = popupView.findViewById(R.id.btn_check_status);
        track.setOnClickListener(v -> {
            String code = editText.getText().toString().trim();
            if (code.length() != 15 && hint.equals(AppController.getResourses().getString(R.string.p_code))) {
                Toast.makeText(context,
                        AppController.getResourses().getString(R.string.invalid_p_code),
                        Toast.LENGTH_LONG).show();
            } else if (code.trim().isEmpty() && hint.equals(AppController.getResourses().getString(R.string.hr_num))) {
                Toast.makeText(context,
                        AppController.getResourses().getString(R.string.invalid_hr_num),
                        Toast.LENGTH_LONG).show();
            } else if (code.trim().isEmpty() && hint.equals(AppController.getResourses().getString(R.string.staff_num))) {
                Toast.makeText(context,
                        AppController.getResourses().getString(R.string.invalid_staff_num),
                        Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(context, TrackGrievanceResultActivity.class);
                intent.putExtra("Code", editText.getText().toString());
                context.startActivity(intent);
            }
            editText.requestFocus();
        });

        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.update();
        popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }

    public void showChangePasswordWindow(final Activity context, View parent) {

        final EditText editTextOld, editTextNew, editTextConfirm;
        View popupView = LayoutInflater.from(context).inflate(R.layout.dialog_change_password, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        editTextOld = popupView.findViewById(R.id.edittext_old_pwd);
        editTextNew = popupView.findViewById(R.id.edittext_new_pwd);
        editTextConfirm = popupView.findViewById(R.id.edittext_confirm_new_pwd);

        Button change = popupView.findViewById(R.id.btn_change_pwd);
        change.setOnClickListener(v -> {
            String oldPwd = editTextOld.getText().toString();
            String newPwd = editTextNew.getText().toString();
            String confirmPwd = editTextConfirm.getText().toString();
            if (oldPwd.isEmpty()) {
                Toast.makeText(context, AppController.getResourses().getString(R.string.empty_old), Toast.LENGTH_LONG).show();
            } else if (newPwd.isEmpty()) {
                Toast.makeText(context, AppController.getResourses().getString(R.string.empty_new), Toast.LENGTH_LONG).show();
            } else if (!confirmPwd.equals(newPwd)) {
                Toast.makeText(context, AppController.getResourses().getString(R.string.new_not_matching), Toast.LENGTH_LONG).show();
            } else if (FireBaseHelper.getInstance(context).mAuth.getCurrentUser() == null)
                showErrorDialog(AppController.getResourses().getString(R.string.try_after_signin),
                        AppController.getResourses().getString(R.string.google_signin_first), context);
            else {
                changePassword(oldPwd, newPwd, context);
                popupWindow.dismiss();
            }

        });

        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.update();
        popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }

    private void changePassword(String oldPwd, String newPwd, Activity context) {

        ProgressDialog progressDialog = Helper.getInstance().getProgressWindow(context, AppController.getResourses().getString(R.string.please_wait));
        progressDialog.show();
        String staffId = Preferences.getInstance().getStaffPref(context, Preferences.PREF_STAFF_DATA).getId();

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CustomLogger.getInstance().logDebug("onDataChange: " + dataSnapshot.getValue());
                if (dataSnapshot.getValue() != null && dataSnapshot.getValue().equals(oldPwd)) {
                    Task<Void> task = FireBaseHelper.getInstance(context).updatePassword(newPwd, staffId);
                    task.addOnCompleteListener(task1 -> {
                        progressDialog.dismiss();
                        if (task1.isSuccessful()) {
                            showFancyAlertDialog(context,
                                    "", AppController.getResourses().getString(R.string.password_change_success),
                                    AppController.getResourses().getString(R.string.ok), () -> {
                                    }, null, null, FancyAlertDialogType.SUCCESS);
                        } else {
                            showErrorDialog(AppController.getResourses().getString(R.string.try_again),
                                    AppController.getResourses().getString(R.string.password_change_fail), context);
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    showErrorDialog(AppController.getResourses().getString(R.string.incorrect_old),
                            AppController.getResourses().getString(R.string.password_change_fail), context);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showMaintenanceDialog(context);
            }
        };
        FireBaseHelper.getInstance(context).getDataFromFirebase(valueEventListener,
                FireBaseHelper.NONVERSIONED, true,
                FireBaseHelper.ROOT_STAFF, staffId, FireBaseHelper.ROOT_PASSWORD);
    }

    public void getConfirmationDialog(Activity context, View view, DialogInterface.OnClickListener yes) {
        AlertDialog.Builder confirmDialog = new AlertDialog.Builder(context);
        confirmDialog.setView(view);
        confirmDialog.setPositiveButton(AppController.getResourses().getString(R.string.confirm), yes);
        confirmDialog.setNegativeButton(AppController.getResourses().getString(android.R.string.cancel), (dialog, which) -> dialog.dismiss());
        confirmDialog.show();
    }

    public JSONObject getJson(String input) {
        try {
            try {
                return new JSONObject(input.substring(input.indexOf("{"), input.indexOf("}") + 1));
            } catch (JSONException jse) {
                jse.printStackTrace();
                CustomLogger.getInstance().logVerbose("getJson: Error creating json");
                return null;
            }
        } catch (StringIndexOutOfBoundsException sioobe) {
            sioobe.printStackTrace();
            return null;
        }
    }

    private byte[] getByteArrayFromBitmap(Bitmap image) {
        if (image == null) {
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public byte[] getByteArrayFromFile(Bitmap image) {
        if (image == null) {
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public Bitmap getBitmapFromString(String value) {
        byte[] inter = Base64.decode(value, 0);
        CustomLogger.getInstance().logDebug("Byte Array = " + inter.toString());
        return BitmapFactory.decodeByteArray(inter, 0, inter.length);
    }

    public byte[] getByteArrayFromBitmapFile(String path) {
        if (path != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            return getByteArrayFromBitmap(bitmap);
        }
        return null;

    }

    public Bitmap createBitmapFromByteArray(byte[] array) {
        if (array != null) {
            return BitmapFactory.decodeByteArray(array, 0, array.length);
        }
        return null;
    }

    public Bitmap getBitmapFromByteArray(byte[] value) {
        if (value != null) {
            return BitmapFactory.decodeByteArray(value, 0, value.length);
        }
        return null;
    }

    public Bitmap getBitmapFromResource(Context context, int res) {
        return BitmapFactory.decodeResource(context.getResources(), res);
    }

    public void showSnackBar(CharSequence message, View view) {
        Snackbar.make(view.findViewById(R.id.fragmentPlaceholder), message, Snackbar.LENGTH_INDEFINITE)
                .setAction("OK", v ->CustomLogger.getInstance().logVerbose("Yes Clicked"))
                .show();
    }


   /*
    public void remove() {
        DatabaseReference versionedDbRef = FireBaseHelper.getInstance().versionedDbRef;
        versionedDbRef.child("Locations").removeValue();
    }*/

   /* public void updateLocations() {
        DatabaseReference versionedDbRef = FirebaseDatabase.getInstance().getReference().child(FireBaseHelper.ROOT_WIFI).child("05");
        versionedDbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                HashMap<String,Object> hashMap=new HashMap<>();
                hashMap.put("StateID", "05");
                FirebaseDatabase.getInstance().getReference().child(FireBaseHelper.ROOT_WIFI).child("05")
                        .child(dataSnapshot.getKey()).updateChildren(hashMap);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }*/

}

