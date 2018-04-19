
package com.ccajk.Tools;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ccajk.Models.GrievanceType;
import com.ccajk.Models.LocationModel;
import com.ccajk.R;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Random;

/*
 * Created by hp on 09-02-2018.
 */


public class Helper {
    private static Helper _instance;

    public final String Nil = "Nil";
    private final String TAG = "Helper";

    public ArrayList<LocationModel> allLocationModels;

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

    public String getGrievanceString(int id) {
        switch (id) {
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

    public String StatusString(long status) {
        switch ((int) status) {
            case 0:
                return "Grievance Submitted";
            case 1:
                return "Grievance resolution in process";
            case 2:
                return "Unable to resolve";
            case 3:
                return "Grievance resolved";
        }
        return null;
    }

    public String[] submittedByList(String type) {
        String first;
        if (type == FireBaseHelper.getInstance().ROOT_GRIEVANCE_PENSION)
            first = "Pensioner";
        else
            first = "GPF Benificiary";
        return new String[]{first, "Other"};
    }


    public PopupWindow getProgressWindow(final Activity context,String message) {
        TextView textViewMessage;
        View popupView = LayoutInflater.from(context).inflate(R.layout.custom_progress_dialog, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        textViewMessage = popupView.findViewById(R.id.textView_Progress_Message);
        textViewMessage.setText(message);
        //popupWindow.setFocusable(true);
        popupWindow.update();
        return  popupWindow;
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


    /**
     * calculates the distance between two locations in MILES
     */
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

