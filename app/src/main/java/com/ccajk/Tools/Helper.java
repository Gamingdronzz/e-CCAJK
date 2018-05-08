
package com.ccajk.Tools;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.ccajk.CustomObjects.FancyAlertDialog.FancyAlertDialog;
import com.ccajk.CustomObjects.FancyAlertDialog.FancyAlertDialogType;
import com.ccajk.CustomObjects.FancyAlertDialog.IFancyAlertDialogListener;
import com.ccajk.CustomObjects.FancyAlertDialog.Icon;
import com.ccajk.CustomObjects.Progress.ProgressDialog;
import com.ccajk.CustomObjects.ShowcaseView.GuideView;
import com.ccajk.Models.GrievanceType;
import com.ccajk.Models.LocationModel;
import com.ccajk.Models.State;
import com.ccajk.R;
import com.google.firebase.database.DatabaseReference;
import com.linchaolong.android.imagepicker.ImagePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/*
 * Created by hp on 09-02-2018.
 */


public class Helper {

    private static Helper _instance;
    public final String SUCCESS = "success";
    public final String Nil = "Nil";
    private final String TAG = "Helper";
    private String appMode;
    private boolean debugMode = true;
    public ArrayList<State> statelist;
    public ArrayList<LocationModel> allLocationModels;

    String[] statuslist = {"Submitted", "Under Process", "Resolved"};
    public String[] errorCodesList={"Error Code 1","Error Code 2","Error Code 3"};
    public String[] errorMessageList={"Message 1","Message 2","Message 3"};

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

    public String getPlayStoreURL(){return "http://play.google.com/store/apps/details?id=com.ccajk";}

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
        if (debugMode) {
            appMode = "debug";
        } else {
            appMode = "release";
        }
    }


    public String getConnectionCheckURL() {
        return "https://www.google.co.in/";
    }

    public String getAPIUrl() {
        if (debugMode) {
            return "http://jknccdirectorate.com/api/cca/debug/v1/";
        } else {
            return "http://jknccdirectorate.com/api/cca/release/v1/";
        }

    }

    /*public ArrayList<State> getStatelist() {
              statelist = new ArrayList<>();
              statelist.add(new State("anp", "Andhra Pradesh"));
              statelist.add(new State("jnk", "Jammu and Kashmir"));
              statelist.add(new State("pnb", "Punjab"));
              return statelist;
          }


          public String getState(String stateId) {
              for (State s : statelist) {
                  if (s.getId() == stateId)
                      return s.getName();
              }
              return null;
          }*/

    public ArrayList<GrievanceType> getPensionGrievanceTypelist() {
        ArrayList<GrievanceType> types = new ArrayList<>();
        types.add(new GrievanceType("Change of PDA", 0));
        types.add(new GrievanceType("Correction in PPO", 1));
        types.add(new GrievanceType("Wrong Fixation of Pension", 2));
        types.add(new GrievanceType("Non Updation of DA", 3));
        types.add(new GrievanceType("Non Payment of Monthly Pension", 4));
        types.add(new GrievanceType("Non Payment of Medical Allowance", 5));
        types.add(new GrievanceType("Non Starting of Family Pension", 6));
        types.add(new GrievanceType("Non Revision as per Latest CPC", 7));
        types.add(new GrievanceType("Request for CGIES", 8));
        types.add(new GrievanceType("Excess/Short Payment", 9));
        types.add(new GrievanceType("Enhancement of Pension on Attaining 75/80", 10));
        types.add(new GrievanceType("Other Pension Grievance", 11));
        return types;
    }

    public ArrayList<GrievanceType> getGPFGrievanceTypelist() {
        ArrayList<GrievanceType> types = new ArrayList<>();
        types.add(new GrievanceType("GPF Final Payment not received", 100));
        types.add(new GrievanceType("Correction in the Name", 101));
        types.add(new GrievanceType("Change of Nomination", 102));
        types.add(new GrievanceType("GPF Account not transfered", 103));
        types.add(new GrievanceType("Details of GPF Deposit A/C Slip", 104));
        types.add(new GrievanceType("Non Payment of GPF Withdrawal", 105));
        types.add(new GrievanceType("Other GPF Grievance", 106));
        return types;
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
            return FireBaseHelper.getInstance().GRIEVANCE_PENSION;
        else
            return FireBaseHelper.getInstance().GRIEVANCE_GPF;
    }

    public String[] getStatusList() {
        return statuslist;
    }

    public String getStatusString(long status) {
        return getStatusList()[(int) status];
    }

    public String[] submittedByList(String type) {
        String first;
        if (type == FireBaseHelper.getInstance().GRIEVANCE_PENSION)
            first = "Pensioner";
        else
            first = "GPF Benificiary";
        return new String[]{first, "Other"};
    }

    public String formatDate(Date date, String format) {
        SimpleDateFormat dt = new SimpleDateFormat(format);
        return dt.format(date);
    }

    public InputFilter[] limitInputLength(int length) {
        return new InputFilter[]{new InputFilter.LengthFilter(length)};
    }

    public ProgressDialog getProgressWindow(final Activity context, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        return progressDialog;
    }

    public void showGuide(Context context, View view, String title, String message) {
        new GuideView.Builder(context)
                .setTitle(title)
                .setContentText(message)
                .setGravity(GuideView.Gravity.auto) //optional
                .setDismissType(GuideView.DismissType.anywhere) //optional - default GuideView.DismissType.targetView
                .setTargetView(view)
                .setContentTextSize(14)//optional
                .setTitleTextSize(16)//optional
                .build()
                .show();
    }

    public void showGuide(Context context, View view, String title, String message, GuideView.GuideListener guideListener) {
        new GuideView.Builder(context)
                .setTitle(title)
                .setContentText(message)
                .setGravity(GuideView.Gravity.auto) //optional
                .setDismissType(GuideView.DismissType.anywhere) //optional - default GuideView.DismissType.targetView
                .setTargetView(view)
                .setContentTextSize(14)//optional
                .setTitleTextSize(16)//optional
                .setGuideListener(guideListener)
                .build()
                .show();
    }

    public void hideKeyboardFrom(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)
                activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
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
        switch (fancyAlertDialogType)
        {
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

//    public void showAlertDialog(Context context, String message, String title, String positiveButtonText,
//                                DialogInterface.OnClickListener positiveButtonOnClickListener,
//                                String negativeButtonText) {
//
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
//                context, R.style.MyAlertDialogStyle);
//        alertDialog.setPositiveButton(positiveButtonText, positiveButtonOnClickListener)
//                .setNegativeButton(negativeButtonText, null)
//                .setMessage(message)
//                .setTitle(title)
//                .show();
//    }

    public String getAppMode() {
        return appMode;
    }

    public void setAppMode(String appMode) {
        this.appMode = appMode;
    }

//    public void showAlertDialog(Context context, String message, String title, String neutralButtonText) {
//        if (title == null) {
//            title = "CCA JK";
//        }
//        if (message == null) {
//            Log.d(TAG, "showAlertDialog: Message cant be null");
//            return;
//        }
//        if (neutralButtonText == null) {
//            neutralButtonText = "OK";
//        }
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
//                context, R.style.MyAlertDialogStyle);
//        alertDialog.setNeutralButton(neutralButtonText, null)
//                .setMessage(message)
//                .setTitle(title)
//                .show();
//    }

//    public void showAlertDialog(Context context, String message, String title, String neutralButtonText, DialogInterface.OnClickListener neutralButtonClickListener) {
//        if (title == null) {
//            title = "CCA JK";
//        }
//        if (message == null) {
//            Log.d(TAG, "showAlertDialog: Message cant be null");
//            return;
//        }
//        if (neutralButtonText == null) {
//            neutralButtonText = "OK";
//        }
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
//                context, R.style.MyAlertDialogStyle);
//        alertDialog.setNeutralButton(neutralButtonText, neutralButtonClickListener)
//                .setMessage(message)
//                .setTitle(title)
//                .show();
//    }

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

    public byte[] getByteArrayFromBitmap(Bitmap image) {
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

//    public byte[] getByteArrayFromFilePath(String path) {
//        if (path != null) {
//
//            File file = new File(path);
//            try {
//                byte[] data = FileUtils.readFileToByteArray(file);//Convert any file, image or video into byte array
//                Log.v(TAG, "getByteArrayFromFile: " + " File = " + file.getName() + "\nLength = " + data.length + "Array =   " + data.toString());
//                return data;
//            } catch (IOException e) {
//                Log.d(TAG, e.toString());
//            }
//
//
//        }
//        return null;
//    }

    public byte[] getByteArrayFromBitmapFile(String path) {
        if (path != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            return getByteArrayFromBitmap(bitmap);
        }
        return null;

    }

    public Bitmap createBitmapFromByteArray(byte[] array) {
        if (array != null) {
            Bitmap bmp = BitmapFactory.decodeByteArray(array, 0, array.length);
            return bmp;
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

    public boolean checkInput(String input) {

        Log.d(TAG, "checkInput: = " + input);
        boolean result;
        if (input == null || input.trim().isEmpty()) {
            result = false;
        } else {
            result = true;
        }
        Log.d(TAG, "checkInput: result = " + result);
        return result;
    }



    public void showSnackBar(CharSequence message, View view) {
        Snackbar.make(view.findViewById(R.id.fragmentPlaceholder), message, Snackbar.LENGTH_INDEFINITE)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.v(TAG, "Yes Clicked");
                    }
                })
                .show();
    }


    public void addLocations(int value) {
        Random random = new Random();
        double maxLongitude = 32.8, minLongitude = 32.1;
        double maxLatitude = 74.5, minLatitude = 75.5;
        for (int i = 0; i < value; i++) {
            double randomLongitude = minLatitude + random.nextDouble() * (maxLatitude - minLatitude);
            double randomLatitude = minLongitude + random.nextDouble() * (maxLongitude - minLongitude);
            DatabaseReference databaseReference = FireBaseHelper.getInstance().databaseReference;
            databaseReference.child("Locations").child("Location" + "-" + i).child("Latitude").setValue(randomLatitude);
            databaseReference.child("Locations").child("Location" + "-" + i).child("Longitude").setValue(randomLongitude);
            databaseReference.child("Locations").child("Location" + "-" + i).child("StateID").setValue("jnk");
            databaseReference.child("Locations").child("Location" + "-" + i).child("District").setValue("jammu");
            databaseReference.child("Locations").child("Location" + "-" + i).child("LocationName").setValue("Location-" + i);
            Log.d("Helper", "Adding Location = " + randomLatitude + " : " + randomLongitude);
        }
    }

    public void remove() {
        DatabaseReference databaseReference = FireBaseHelper.getInstance().databaseReference;
        databaseReference.child("Locations").removeValue();
    }

    public ArrayList<LocationModel> getAllLocations() {
        //TODO
        //Fetch locations models from local memory here
        return allLocationModels;
    }

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

