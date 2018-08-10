package com.mycca.tools;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mycca.models.NewsModel;
import com.mycca.models.SelectedImageModel;

import java.util.HashMap;

public class NewFireBaseHelper {

    private static NewFireBaseHelper mInstance;

    public final static String ROOT_FCM_KEY = "FCMServerKey";
    public final static String ROOT_APP_VERSION = "Latest Version";
    public final static String ROOT_SLIDER = "Slider Data";
    public final static String ROOT_CIRCLE_DATA = "Circle Data";
    public final static String ROOT_CIRCLE_COUNT = "Circle Data Count";
    public final static String ROOT_ACTIVE_COUNT = "Circle Data Count Active";
    public final static String ROOT_NEWS = "Latest News";
    public final static String ROOT_SUGGESTIONS = "Suggestions";
    public final static String ROOT_TOKEN = "Tokens";
    public final static String ROOT_GRIEVANCES = "Grievances";
    public final static String ROOT_REF_COUNT = "Reference Number Count";
    public final static String ROOT_REF_NUMBERS = "Reference Numbers";
    public final static String ROOT_ADHAAR = "Aadhaar";
    public final static String ROOT_PAN = "Pan";
    public final static String ROOT_LIFE = "Life Certificate";
    public final static String ROOT_RE_MARRIAGE = "Re-Marriage Certificate";
    public final static String ROOT_RE_EMPLOYMENT = "Re-Employment Certificate";
    public final static String ROOT_STAFF = "Staff Data";
    public final static String ROOT_PASSWORD = "password";
    public final static String ROOT_INSPECTION = "Inspection";
    public final static String ROOT_WEBSITE = "Website";
    public final static String ROOT_OFFICE_ADDRESS = "Office Address";
    public final static String ROOT_OFFICE_COORDINATES = "Office Coordinates";
    public final static String ROOT_CONTACTS = "Contact Persons";
    public final static String ROOT_CONTACTS_COUNT = "Contact Persons Count";
    public final static String ROOT_WIFI = "Wifi Locations";
    public final static String ROOT_WIFI_COUNT = "Wifi Locations Count";
    public final static String ROOT_GP = "GP Locations";
    public final static String ROOT_GP_COUNT = "GP Locations Count";
    public final static String ROOT_BY_USER = "By User";
    public final static String ROOT_BY_STAFF = "By Staff";

    public NewFireBaseHelper() {
        mInstance = this;
    }

    public static NewFireBaseHelper getInstance() {
        if (mInstance == null) {
            return new NewFireBaseHelper();
        } else {
            return mInstance;
        }
    }

    public FirebaseAuth getAuth() {
        return FirebaseAuth.getInstance();
    }

    public DatabaseReference getDatabaseReference(String stateReference) {
        DatabaseReference databaseReference;
        if (stateReference != null) {
            String url = "https://" + stateReference + ".firebaseio.com/";
            databaseReference = FirebaseDatabase.getInstance(url).getReference();

        } else
            databaseReference = FirebaseDatabase.getInstance().getReference();
        return databaseReference;
    }

    public StorageReference getStorageReference(String stateReference) {
        StorageReference storageReference;
        if (stateReference != null) {
            String url = "gs://" + stateReference;
            storageReference = FirebaseStorage.getInstance(url).getReference();
        } else
            storageReference = FirebaseStorage.getInstance().getReference();
        return storageReference;
    }


    public void addTokenOnFireBase(String stateInstance) {

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
            if (getAuth().getCurrentUser() != null) {
                getDatabaseReference(stateInstance).child(ROOT_TOKEN).
                        child(getAuth().getCurrentUser().getUid()).
                        setValue(instanceIdResult.getToken()).
                        addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                CustomLogger.getInstance().logDebug("Token added");
                            }
                        });
            }
        });
    }

    public void getReferenceNumber(Transaction.Handler handler, String stateInstance) {
        CustomLogger.getInstance().logDebug("getReferenceNumber: ");
        DatabaseReference dbref = getDatabaseReference(stateInstance);
        dbref = dbref.child(ROOT_REF_COUNT);
        dbref.runTransaction(handler);
    }

    public Task<Void> uploadDataToFireBase(String stateInstance, Object model, String... params) {
        Task<Void> task;
        DatabaseReference databaseReference = getDatabaseReference(stateInstance);

        for (String key : params) {
            CustomLogger.getInstance().logDebug("child : " + key);
            databaseReference = databaseReference.child(key);
        }
        task = databaseReference.setValue(model);
        return task;
    }

    public Task<Void> pushOnFireBase(Object model, String root) {

        Task<Void> task = null;
        switch (root) {
            case ROOT_NEWS:
                NewsModel newsModel = (NewsModel) model;
                if (newsModel.getKey() == null) {
                    CustomLogger.getInstance().logDebug("news key null");
                    String key = getDatabaseReference(null).child(root).push().getKey();
                    newsModel.setKey(key);
                    task = getDatabaseReference(null).child(root).child(key).setValue(newsModel);
                }
                break;
            case ROOT_SUGGESTIONS:
                task = getDatabaseReference(null).child(root).push().setValue(model);
                break;
        }
        return task;
    }

    public Task<Void> updateData(String stateInstance, String key, HashMap<String, Object> hashMap, String... params) {
        DatabaseReference databaseReference = getDatabaseReference(stateInstance);
        Task<Void> task;
        for (String param : params) {
            databaseReference = databaseReference.child(param);
        }
        task = databaseReference.child(key).updateChildren(hashMap);
        return task;
    }

    public void getDataFromFireBase(String stateInstance, ChildEventListener childEventListener, String... params) {

        DatabaseReference dbref = getDatabaseReference(stateInstance);
        CustomLogger.getInstance().logDebug("getting DataFromFirebase: ");
        for (String key : params) {
            CustomLogger.getInstance().logDebug("Firebase key : " + key);
            dbref = dbref.child(key);
        }
        dbref.addChildEventListener(childEventListener);
    }

    public void getDataFromFireBase(String stateInstance, ValueEventListener valueEventListener, boolean singleValueEvent, String... params) {

        DatabaseReference dbref = getDatabaseReference(stateInstance);
        for (String key : params) {
            CustomLogger.getInstance().logDebug("Firebase Helper key : " + key);
            dbref = dbref.child(key);
        }
        if (singleValueEvent)
            dbref.addListenerForSingleValueEvent(valueEventListener);
        else
            dbref.addValueEventListener(valueEventListener);
    }

    public void removeListener(String stateInstance, ChildEventListener childEventListener) {
        getDatabaseReference(stateInstance).removeEventListener(childEventListener);
    }

    public void removeListener(String stateInstance, ValueEventListener valueEventListener) {
        getDatabaseReference(stateInstance).removeEventListener(valueEventListener);
    }

    public UploadTask uploadFiles(String stateInstance, SelectedImageModel imageFile, boolean multiple, int count, String... params) {
        StorageReference sRef;
        StringBuilder sb = new StringBuilder();
        for (String param : params)
            sb.append(param).append("/");
        if (multiple) {
            sRef = getStorageReference(stateInstance).child(sb + "File" + count);
        } else {
            sRef = getStorageReference(stateInstance).child(sb.toString());
        }
        return sRef.putFile(imageFile.getImageURI());
    }

    public Task<Uri> getFileFromStorage(String stateInstance, String path) {
        return getStorageReference(stateInstance).child(path).getDownloadUrl();
    }

}
