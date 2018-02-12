/*
package com.ccajk.Tools;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

*/
/**
 * Created by hp on 09-02-2018.
 *//*


public class Helper {

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
}
*/
