
package com.mycca.Tools;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.util.Base64;
import android.util.Log;
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
import com.mycca.Activity.TrackGrievanceResultActivity;
import com.mycca.CustomObjects.CustomImagePicker.ImagePicker;
import com.mycca.CustomObjects.FancyAlertDialog.FancyAlertDialog;
import com.mycca.CustomObjects.FancyAlertDialog.FancyAlertDialogType;
import com.mycca.CustomObjects.FancyAlertDialog.IFancyAlertDialogListener;
import com.mycca.CustomObjects.FancyAlertDialog.Icon;
import com.mycca.CustomObjects.Progress.ProgressDialog;
import com.mycca.Models.GrievanceType;
import com.mycca.Models.State;
import com.mycca.Models.StatusModel;
import com.mycca.R;

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

    private StatusModel[] statuslist = {
            new StatusModel(0, "Submitted"),
            new StatusModel(1, "Under Process"),
            new StatusModel(2, "Resolved")
    };

    private State stateList[] = {
            new State("05", "Jammu & Kashmir"),
            new State("100", "Haryana")
    };

    private State stateListJK[] = {new State("05", "Jammu & Kashmir")};

    private GrievanceType pensionGrievanceTypes[] = {
            new GrievanceType("Change of PDA", 0),
            new GrievanceType("Correction in PPO", 1),
            new GrievanceType("Wrong Fixation of Pension", 2),
            new GrievanceType("Non Updation of DA", 3),
            new GrievanceType("Non Payment of Monthly Pension", 4),
            new GrievanceType("Non Payment of Medical Allowance", 5),
            new GrievanceType("Non Starting of Family Pension", 6),
            new GrievanceType("Non Revision as per Latest CPC", 7),
            new GrievanceType("Request for CGIES", 8),
            new GrievanceType("Excess/Short Payment", 9),
            new GrievanceType("Enhancement of Pension on Attaining 75/80", 10),
            new GrievanceType("Other Pension Grievance", 11)
    };

    private GrievanceType gpfGrievanceTypes[] = {
            new GrievanceType("GPF Final Payment not received", 100),
            new GrievanceType("Correction in the Name", 101),
            new GrievanceType("Change of Nomination", 102),
            new GrievanceType("GPF Account not transferred", 103),
            new GrievanceType("Details of GPF Deposit A/C Slip", 104),
            new GrievanceType("Non Payment of GPF Withdrawal", 105),
            new GrievanceType("Other GPF Grievance", 106)
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

    public State[] getStatelist() {
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

    public GrievanceType[] getPensionGrievanceTypelist() {
        return pensionGrievanceTypes;
    }

    public GrievanceType[] getGPFGrievanceTypelist() {
        return gpfGrievanceTypes;
    }

    public String getGrievanceString(long id) {
        switch ((int) id) {
            case 0:
                return "Change of PDA";
            case 1:
                return "Correction in PPO";
            case 2:
                return "Wrong Fixation of Pension";
            case 3:
                return "Non Updation of DA";
            case 4:
                return "Non Payment of Monthly Pension";
            case 5:
                return "Non Payment of Medical Allowance";
            case 6:
                return "Non Starting of Family Pension";
            case 7:
                return "Non Revision as per Latest CPC";
            case 8:
                return "Request for CGIES";
            case 9:
                return "Excess/Short Payment";
            case 10:
                return "Enhancement of Pension on Attaining 75/80";
            case 11:
                return "Other Pension Grievance";
            case 100:
                return "GPF Final Payment not received";
            case 101:
                return "Correction in the Name";
            case 102:
                return "Change of Nomination";
            case 103:
                return "GPF Account not transfered";
            case 104:
                return "Details of GPF Deposit A/C Slip";
            case 105:
                return "Non Payment of GPF Withdrawal";
            case 106:
                return "Other GPF Grievance";
        }
        return null;
    }

    public String getGrievanceCategory(long id) {
        if (id < 100)
            return FireBaseHelper.GRIEVANCE_PENSION;
        else
            return FireBaseHelper.GRIEVANCE_GPF;
    }

    public StatusModel[] getStatusList() {
        return statuslist;
    }

    public String getStatusString(long status) {
        switch ((int) status) {
            case 0:
                return "Submitted";
            case 1:
                return "Under Process";
            case 2:
                return "Resolved";
        }
        return null;
    }

    public String[] submittedByList(String type) {
        String first;
        if (type.equals(FireBaseHelper.GRIEVANCE_PENSION))
            first = "Pensioner";
        else
            first = "GPF Beneficiary";
        return new String[]{first, "Other"};
    }

    public String formatDate(Date date, String format) {
        SimpleDateFormat dt = new SimpleDateFormat(format, Locale.ENGLISH);
        return dt.format(date);
    }

    public InputFilter[] limitInputLength(int length) {
        return new InputFilter[]{new InputFilter.LengthFilter(length)};
    }

    public boolean onLatestVersion(DataSnapshot dataSnapshot, final Activity activity) {
        long newVersion;
        int version = getAppVersion(activity);
        if (dataSnapshot.getValue() == null) {
            Log.d(TAG, "onLatestVersion: Data snapshot null");
            showUpdateOrMaintenanceDialog(false, activity);
            return false;
        }
        try {
            newVersion = (long) dataSnapshot.getValue();
        } catch (Exception e) {
            e.printStackTrace();
            showUpdateOrMaintenanceDialog(false, activity);
            return false;
        }

        if (version == -1 || newVersion == version) {
            versionChecked = true;
            return true;
        } else {
            showUpdateOrMaintenanceDialog(true, activity);
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

    public boolean checkInput(String input) {

        Log.d(TAG, "checkInput: = " + input);
        boolean result;
        result = !(input == null || input.trim().isEmpty());
        Log.d(TAG, "checkInput: result = " + result);
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
        imagePicker.setTitle("Select Image");
        imagePicker.setCropImage(cropimage);
        imagePicker.startChooser(activity, callback);
        return imagePicker;
    }

    public boolean isTab(Context context) {
        boolean isTab = (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
        Log.d(TAG, "Tab = " + isTab);
        return isTab;
    }

    public void showFancyAlertDialog(Activity activity,
                                     String message,
                                     String title,
                                     String positiveButtonText, IFancyAlertDialogListener positiveButtonOnClickListener,
                                     String negativeButtonText, IFancyAlertDialogListener negativeButtonOnClickListener,
                                     FancyAlertDialogType fancyAlertDialogType) {
        if (title == null) {
            title = "CCA JK";
        }
        if (message == null) {
            Log.d(TAG, "showFancyAlertDialog: Message cant be null");
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

    public void showUpdateOrMaintenanceDialog(boolean updateAvailable, final Activity activity) {
        if (updateAvailable) {
            Helper.getInstance().showFancyAlertDialog(activity,
                    "A new version of the application is available on Google Play Store\n\nUpdate to continue using the application",
                    "My CCA",
                    "Update",
                    () -> {
                        showGooglePlayStore(activity);
                        activity.finish();
                    },
                    "Cancel",
                    activity::finish,
                    FancyAlertDialogType.WARNING);
        } else {
            Helper.getInstance().showFancyAlertDialog(activity,
                    "The Application is in maintenance\nPlease wait for a while\n\nThank you for your patience",
                    "My CCA",
                    "OK",
                    activity::finish,
                    null, null,
                    FancyAlertDialogType.WARNING);
        }
    }

    public void showErrorDialog(String message, String title, Activity activity) {
        showFancyAlertDialog(activity,
                message,
                title,
                "OK",
                null,
                null,
                null,
                FancyAlertDialogType.ERROR);
    }

    public class DateFormat {
        public static final String DD_MM_YYYY = "dd MMM, yyyy";
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
                    hint = "Pensioner Code";
                    editText.setFilters(Helper.getInstance().limitInputLength(15));
                    break;
                case R.id.radioButtonHR:
                    hint = "HR Number";
                    editText.setFilters(new InputFilter[]{});
                    break;
                case R.id.radioButtonStaff:
                    hint = "Staff Number";
                    editText.setFilters(new InputFilter[]{});
            }
            editText.setText("");
            textInputLayout.setHint(hint);
        });

        Button track = popupView.findViewById(R.id.btn_check_status);
        track.setOnClickListener(v -> {
            String code = editText.getText().toString().trim();
            if (code.length() != 15 && hint.equals("Pensioner Code")) {
                Toast.makeText(context, "Invalid Pensioner code!", Toast.LENGTH_LONG).show();
            } else if (code.trim().isEmpty() && hint.equals("HR Number")) {
                Toast.makeText(context, "Invalid HR Number!", Toast.LENGTH_LONG).show();
            } else if (code.trim().isEmpty() && hint.equals("Staff Number")) {
                Toast.makeText(context, "Invalid Staff Number!", Toast.LENGTH_LONG).show();
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
                Toast.makeText(context, "Enter Old Password", Toast.LENGTH_LONG).show();
            } else if (newPwd.isEmpty()) {
                Toast.makeText(context, "Enter New Password", Toast.LENGTH_LONG).show();
            } else if (!confirmPwd.equals(newPwd)) {
                Toast.makeText(context, "Confirm password not matching new password", Toast.LENGTH_LONG).show();
            } else  if (FireBaseHelper.getInstance(context).mAuth.getCurrentUser() == null)
                showErrorDialog("Try again after Sign in", "Please Sign in with google first", context);
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

        ProgressDialog progressDialog = Helper.getInstance().getProgressWindow(context, "Please Wait...");
        progressDialog.show();
        String staffId = Preferences.getInstance().getStaffPref(context, Preferences.PREF_STAFF_DATA).getId();

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: " + dataSnapshot.getValue());
                if (dataSnapshot.getValue().equals(oldPwd)) {
                    Task<Void> task = FireBaseHelper.getInstance(context).updatePassword(newPwd, staffId);
                    task.addOnCompleteListener(task1 -> {
                        progressDialog.dismiss();
                        if (task1.isSuccessful()) {
                            showFancyAlertDialog(context,
                                    "", "Password Changed Successfully",
                                    "OK", () -> {
                                    },
                                    null, null, FancyAlertDialogType.SUCCESS);
                        } else {
                                showErrorDialog("Try again", "Password could not be changed", context);
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    showErrorDialog("Incorrect old password", "Password could not be changed", context);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showUpdateOrMaintenanceDialog(false, context);
            }
        };
        FireBaseHelper.getInstance(context).getDataFromFirebase(valueEventListener,
                FireBaseHelper.NONVERSIONED, true,
                FireBaseHelper.ROOT_STAFF, staffId, FireBaseHelper.ROOT_PASSWORD);
    }

    public void getConfirmationDialog(Activity context, View view, DialogInterface.OnClickListener yes) {
        AlertDialog.Builder confirmDialog = new AlertDialog.Builder(context);
        confirmDialog.setView(view);
        confirmDialog.setPositiveButton("Confirm", yes);
        confirmDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        confirmDialog.show();
    }

    public JSONObject getJson(String input) {
        try {
            try {
                return new JSONObject(input.substring(input.indexOf("{"), input.indexOf("}") + 1));
            } catch (JSONException jse) {
                jse.printStackTrace();
                Log.v("Helper", "Error creating json");
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
        Log.d("Helper", "Byte Array = " + inter.toString());
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
                .setAction("OK", v -> Log.v(TAG, "Yes Clicked"))
                .show();
    }


    /*public void addLocations(int value) {
        Random random = new Random();
        double maxLongitude = 32.8, minLongitude = 32.1;
        double maxLatitude = 74.5, minLatitude = 75.5;
        for (int i = 0; i < value; i++) {
            double randomLongitude = minLatitude + random.nextDouble() * (maxLatitude - minLatitude);
            double randomLatitude = minLongitude + random.nextDouble() * (maxLongitude - minLongitude);
            DatabaseReference versionedDbRef = FireBaseHelper.getInstance().versionedDbRef;
            versionedDbRef.child("Locations").child("Location" + "-" + i).child("Latitude").setValue(randomLatitude);
            versionedDbRef.child("Locations").child("Location" + "-" + i).child("Longitude").setValue(randomLongitude);
            versionedDbRef.child("Locations").child("Location" + "-" + i).child("StateID").setValue("jnk");
            versionedDbRef.child("Locations").child("Location" + "-" + i).child("District").setValue("jammu");
            versionedDbRef.child("Locations").child("Location" + "-" + i).child("LocationName").setValue("Location-" + i);
            Log.d("Helper", "Adding Location = " + randomLatitude + " : " + randomLongitude);
        }
    }
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

    public static double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 3958.75; // in miles, change to 6371 for kilometer output

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double dist = earthRadius * c;
        Log.v("Helper", "Distance between coordinates = " + dist);

        return dist; // output distance, in MILES
    }


}

