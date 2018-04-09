
package com.ccajk.Tools;

import android.support.annotation.NonNull;
import android.util.Log;

import com.ccajk.Models.LocationModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Random;

/*
 * Created by hp on 09-02-2018.
 */


public class Helper {
    private static Helper _instance;

    public final int CATEGORY_PENSION=0;
    public final int CATEGORY_GPF=1;
    public final int UPLOAD_TYPE_ADHAAR=0;
    public final int UPLOAD_TYPE_PAN=1;

    public ArrayList<LocationModel> allLocationModels;

    public String[] PGlist = {
            "Change of PDA",
            "Correction in PPO",
            "Wrong Fixation of Pension",
            "Non Updation of DA",
            "Non Payment of Monthly Pension",
            "Non Payment of Medical Allowance",
            "Non Starting of Family Pension",
            "Non Revision as per Latest CPC",
            "Rrequest for CGIES",
            "Excess/Short Payment",
            "Enhancement of Pension on Attaining 75/80",
            "Others"
    };

    public String[] GPFlist = {
            "GPF Final Payment not received",
            "Correction in the Name",
            "Change of Nomination",
            "GPF Account not transfered",
            "Details of GPF Deposit A/C Slip",
            "Non Payment of GPF Withdrawal",
            "Others"
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

    public ArrayList<LocationModel> getAllLocations()
    {
        //TODO
        //Fetch locations models from local memory here
        return allLocationModels;
    }


    public String[] getPensionGrievanceList()
    {
        return PGlist;
    }
    public String[] getGPFGrievanceList()
    {
        return GPFlist;
    }

    public String[] submittedByList(int gtype) {
        String first;
        if (gtype == 0)
            first = "Pensioner";
        else
            first = "GPF Benificiary";
        return new String[]{first, "Other"};
    }

    class CompletionListener implements OnCompleteListener<Void>
    {

        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if(task.isSuccessful())
            {
                Log.d("Completion",task.toString());
            }
        }
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

    public void addLocations(int value)
    {
        Random random = new Random();
        double maxLongitude = 32.8,minLongitude = 32.1;
        double maxLatitude = 74.5, minLatitude = 75.5;
        for(int i=10;i<value+10;i++)
        {
            double randomLongitude = minLatitude + random.nextDouble() * (maxLatitude - minLatitude);
            double randomLatitude  = minLongitude + random.nextDouble() * (maxLongitude - minLongitude);
            DatabaseReference databaseReference = FireBaseHelper.getInstance().databaseReference;
            databaseReference.child("Locations").child("Location"+"-"+i).child("Latitude").setValue(randomLatitude).addOnCompleteListener(new CompletionListener());
            databaseReference.child("Locations").child("Location"+"-"+i).child("Longitude").setValue(randomLongitude);
            databaseReference.child("Locations").child("Location"+"-"+i).child("StateID").setValue("jnk");
            databaseReference.child("Locations").child("Location"+"-"+i).child("District").setValue("jammu");
            databaseReference.child("Locations").child("Location"+"-"+i).child("LocationName").setValue("Location-"+i);
            Log.d("Helper","Adding Location = " + randomLatitude + " : " + randomLongitude );
        }
    }



}

