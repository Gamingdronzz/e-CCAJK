package com.ccajk.Tools;

import com.ccajk.Models.LocationModel;
import com.ccajk.Models.State;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by hp on 06-03-2018.
 */

public class FireBaseHelper {

    private static FireBaseHelper _instance;
    private ArrayList<LocationModel> locationModels;

    public DatabaseReference databaseReference;
    public final String ROOT_ADHAAR = "Adhaar";
    public final String ROOT_ADHAAR_STATUS = "Adhaar-Status";
    public final String ROOT_PAN = "Pan";
    public final String ROOT_PAN_STATUS = "Pan-Status";

    public ArrayList<State> statelist;

    public FireBaseHelper() {
        _instance = this;
        databaseReference = FirebaseDatabase.getInstance().getReference();
        statelist = getStatelist();

    }

    public static FireBaseHelper getInstance() {
        if (_instance == null) {
            return new FireBaseHelper();
        } else {
            return _instance;
        }
    }

    public ArrayList<LocationModel> getLocationModels(String stateId) {
        AddLocations(stateId);
        return locationModels;
    }


    public void AddLocations(String stateId) {
        locationModels = new ArrayList<>();
        locationModels.add(new LocationModel("Sangrampur", "32.7400343", "74.7403159", stateId, "jammu"));
        locationModels.add(new LocationModel("Sohal", "32.4938192", "75.2548692", stateId, "jammu"));
        locationModels.add(new LocationModel("Sidhra", "32.7604934", "74.8989541", stateId, "jammu"));
        locationModels.add(new LocationModel("Sumb", "32.52839", "75.120054", stateId, "samba"));
        locationModels.add(new LocationModel("Trilokpur", "32.7148855", "74.752726", stateId, "jammu"));

    }


    public ArrayList<State> getStatelist() {
        statelist = new ArrayList<>();
        statelist.add(new State("anp", "Andhra Pradesh"));
                    /*"Arunachal Pradesh",
                    "Assam",
                    "Bihar",
                    "Chhattisgarh",
                    "Goa",
                    "Gujarat",
                    "Haryana",
                    "Himachal Pradesh",*/
        statelist.add(new State("jnk", "Jammu and Kashmir"));
                    /*"Jharkhand",
                    "Karnataka",
                    "Kerala",
                    "Madya Pradesh",
                    "Maharashtra",
                    "Manipur",
                    "Meghalaya",
                    "Mizoram",
                    "Nagaland",
                    "Orissa",*/
        statelist.add(new State("pnb", "Punjab"));
                    /*"Rajasthan",
                    "Sikkim",
                    "Tamil Nadu",
                    "Tripura",
                    "Uttaranchal",
                    "Uttar Pradesh",
                    "West Bengal"*/
        return statelist;
    }


    public String getState(String stateId) {
        for (State s : statelist) {
            if (s.getId() == stateId)
                return s.getName();
        }
        return null;
    }

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
}
