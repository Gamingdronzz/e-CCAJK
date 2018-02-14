
package com.ccajk.Tools;
/*
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
*/


import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/*
 * Created by hp on 09-02-2018.
 */


public class Helper {

    /*
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    User user;
    Grievance grievance;
    ArrayList<User> users;
    ArrayList<Grievance> grievances;

    public DatabaseReference userReference() {
        return databaseReference.child("users");
    }

    public DatabaseReference userChildReference(String id) {
        return databaseReference.child("users").child(id);
    }

    public ArrayList<User> getUsers() {
        DatabaseReference dbref = databaseReference.child("users");
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users = new ArrayList();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    users.add(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return users;
    }

    public User myReference(String id) {
        DatabaseReference dbref = databaseReference.child("user").child(id);
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return user;
    }

    public ArrayList<Grievance> getGrievances() {
        DatabaseReference dbref = databaseReference.child("grievance");
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                grievances = new ArrayList();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Grievance grievance = snapshot.getValue(Grievance.class);
                    grievances.add(grievance);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return grievances;
    }

    public ArrayList<Grievance> getMyGrievances(String userId) {
        Query query = databaseReference.child("grievance").orderByChild("id").equalTo(userId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                grievances = new ArrayList();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Grievance grievance = snapshot.getValue(Grievance.class);
                    grievances.add(grievance);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return grievances;
    }

    public Grievance getGrievanceById(String id) {
        DatabaseReference dbref = databaseReference.child("grievances").child(id);
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                grievance = dataSnapshot.getValue(Grievance.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return grievance;
    }

    public void updateValue(String parent, String child, String key, String value) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(parent + "/" + child + "/" + key, value);
        databaseReference.updateChildren(hashMap);
    }

    */

    /** calculates the distance between two locations in MILES */
    public static double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 3958.75; // in miles, change to 6371 for kilometer output

        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double dist = earthRadius * c;
        Log.v("Helper","Distance between coordinates = " + dist);

        return dist; // output distance, in MILES
    }
}

