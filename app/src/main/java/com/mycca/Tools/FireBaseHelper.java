package com.mycca.Tools;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mycca.Models.Contact;
import com.mycca.Models.ContactBuilder;
import com.mycca.Models.NewsModel;
import com.mycca.Models.SelectedImageModel;

import java.util.ArrayList;
import java.util.HashMap;

public class FireBaseHelper {

    private static FireBaseHelper _instance;

    public DatabaseReference versionedDbRef;
    private DatabaseReference unVersionedDbRef;
    private DatabaseReference unVersionedStateDbRef;
    private StorageReference storageReference;
    public FirebaseAuth mAuth;

    public final static String ROOT_GRIEVANCES = "Grievances";
    private final static String ROOT_APP_VERSION = "Latest Version";
    public final static String ROOT_ADHAAR = "Aadhaar";
    public final static String ROOT_PAN = "Pan";
    public final static String ROOT_LIFE = "Life Certificate";
    public final static String ROOT_RE_MARRIAGE = "Re-Marriage Certificate";
    public final static String ROOT_RE_EMPLOYMENT = "Re-Employment Certificate";
    public final static String ROOT_WIFI = "Wifi Locations";
    public final static String ROOT_GP = "GP Locations";
    public final static String ROOT_STAFF = "Staff Login";
    public final static String ROOT_INSPECTION = "Inspection";
    public final static String ROOT_NEWS = "Latest News";
    public final static String ROOT_SUGGESTIONS = "Suggestions";
    public final static String ROOT_TOKEN = "Tokens";
    public final static String ROOT_SLIDER = "Slider Data";
    private final static String ROOT_STATE_DATA = "State Data";
    private final static String ROOT_REF_COUNT = "Reference Number Count";

    public final static String GRIEVANCE_PENSION = "Pension";
    public final static String GRIEVANCE_GPF = "GPF";
    public final static int UNVERSIONED = 0;
    public final static int UNVERSIONED_STATEWISE = 1;
    public final static int VERSIONED = 2;

    public String version;
    private final String TAG = "firebaseHelper";

    //public GrievanceModel selectedGrievance;
    //public final String ROOT_ADHAAR_STATUS = "Adhaar-Status";
    //public final String ROOT_PAN_STATUS = "Pan-Status";

    public FireBaseHelper(Context context) {
        _instance = this;

        version = String.valueOf(Helper.getInstance().getAppVersion(context));
        if (version.equals("-1"))
            version = "5";

        unVersionedDbRef = FirebaseDatabase.getInstance().getReference();
        versionedDbRef = FirebaseDatabase.getInstance().getReference().child(version);
        unVersionedStateDbRef = FirebaseDatabase.getInstance().getReference()
                .child(ROOT_STATE_DATA)
                .child(Preferences.getInstance().getStringPref(context, Preferences.PREF_STATE));

        storageReference = FirebaseStorage.getInstance().getReference().child(version);
        mAuth = FirebaseAuth.getInstance();
    }

    public static FireBaseHelper getInstance(Context context) {
        if (_instance == null) {
            return new FireBaseHelper(context);
        } else {
            return _instance;
        }
    }

    public static void resetInstance() {
        _instance = null;
    }

    public void setToken() {
        Log.d(TAG, "setToken: ");
        DatabaseReference dbref = versionedDbRef;
        if (mAuth.getCurrentUser() != null) {
            Log.d(TAG, "setToken: user found");
            dbref.child(ROOT_TOKEN).child(mAuth.getCurrentUser().getUid()).
                    setValue(FirebaseInstanceId.getInstance().getToken())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Token added");
                        }
                    });
        }
    }

    public Task<Void> uploadDataToFirebase(Object model, String... params) {
        Task<Void> task;
        DatabaseReference dbref = versionedDbRef;

        for (String key :
                params) {
            Log.d(TAG, "Firebase Helper Uploading Data to : " + key);
            dbref = dbref.child(key);
        }

        task = dbref.setValue(model);
        return task;
    }

    public Task<Void> uploadDataToFirebase(Object model, String root) {

        DatabaseReference dbref = versionedDbRef.child(root);
        Task<Void> task = null;

        switch (root) {
            case ROOT_NEWS:
                NewsModel newsModel = (NewsModel) model;
                if (newsModel.getKey() == null) {
                    Log.d(TAG, "news key null");
                    String key = dbref.push().getKey();
                    newsModel.setKey(key);
                    task = dbref.child(key).setValue(newsModel);
                }
                break;
            case ROOT_SUGGESTIONS:
                task = dbref.push().setValue(model);
                break;
        }

        return task;
    }

    public Task<Void> updateNews(Object model, String root) {
        DatabaseReference dbref = versionedDbRef.child(root);
        Task<Void> task;
        NewsModel newsModel = (NewsModel) model;
        Log.d(TAG, "non null news key : " + newsModel.getKey());
        HashMap<String, Object> result = new HashMap<>();
        result.put("headline", newsModel.getHeadline());
        result.put("description", newsModel.getDescription());
        task = dbref.child(newsModel.getKey()).updateChildren(result);
        return task;
    }

    public UploadTask uploadFiles(SelectedImageModel imageFile, boolean multiple, int count, String... params) {
        StorageReference sref;
        StringBuilder sb = new StringBuilder();
        for (String param : params)
            sb.append(param).append("/");
        if (multiple) {
            sref = storageReference.child(sb + "File" + count);
        } else {
            sref = storageReference.child(sb.toString());
        }
        return sref.putFile(imageFile.getImageURI());
    }

    public void checkForUpdate(ValueEventListener valueEventListener) {
        versionedDbRef.child(FireBaseHelper.ROOT_APP_VERSION)
                .addListenerForSingleValueEvent(valueEventListener);
    }

    public void getDataFromFirebase(ChildEventListener childEventListener, int versioned, String... params) {
        DatabaseReference dbref;
        if (versioned == VERSIONED)
            dbref = versionedDbRef;
        else if (versioned == UNVERSIONED)
            dbref = unVersionedDbRef;
        else
            dbref = unVersionedStateDbRef;

        Log.d(TAG, "getting DataFromFirebase: ");
        for (String key : params) {
            Log.d(TAG, "Firebase Helper Uploading Data to : " + key);
            dbref = dbref.child(key);
        }

        dbref.addChildEventListener(childEventListener);
    }

    public void getDataFromFirebase(ValueEventListener valueEventListener, int versioned, boolean singleValueEvent, String... params) {
        DatabaseReference dbref;
        if (versioned == VERSIONED)
            dbref = versionedDbRef;
        else if (versioned == UNVERSIONED)
            dbref = unVersionedDbRef;
        else
            dbref = unVersionedStateDbRef;

        for (String key : params) {
            Log.d(TAG, "Firebase Helper Uploading Data to : " + key);
            dbref = dbref.child(key);
        }
        if (singleValueEvent)
            dbref.addListenerForSingleValueEvent(valueEventListener);
        else
            dbref.addValueEventListener(valueEventListener);
    }

    public void getReferenceNumber() {
        Log.d(TAG, "getReferenceNumber: ");
        DatabaseReference dbref;
        dbref = unVersionedStateDbRef.child(ROOT_REF_COUNT);
        dbref.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Log.d(TAG, "doTransaction: " + mutableData.getValue());
                long count = 0;
                if (mutableData.getValue() != null) {
                    count = (long) mutableData.getValue();
                }

                // Set value and report transaction success
                mutableData.setValue(++count);
                Log.d(TAG, "count= " + count);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                Log.d(TAG, "onComplete: DatabaseError " + databaseError);
                Log.d(TAG, "onComplete: Datasnaphot  " + dataSnapshot.getValue());
                Log.d(TAG, "onComplete: booleAN " + b);
            }

        });

    }

    public Task<Uri> getFileFromFirebase(String path) {
        return storageReference.child(path).getDownloadUrl();
    }

    public ArrayList<Contact> getContactsList(String stateId) {
        ArrayList<Contact> contactArrayList = new ArrayList<>();
        contactArrayList.add(new ContactBuilder().setName("Sh. Rajnish Kumar Jenaw ").setDesignation("CCA").setEmail("ccajk@nic.in").setOfficeContact("2477280").setMobileContact("9419120080").setStateId(stateId).createContact());
        contactArrayList.add(new ContactBuilder().setName("Sh Amanullah Tak").setDesignation("JT.CCA").setEmail("aman.tak@gov.in").setOfficeContact("2477281").setMobileContact("9419120986").setStateId(stateId).createContact());
        contactArrayList.add(new ContactBuilder().setName("Smt. Veena Gupta").setDesignation("ACCA (Spectrum/Pension)").setEmail("accajk@nic.in").setOfficeContact("2477283").setMobileContact("9419120332").setStateId(stateId).createContact());
        contactArrayList.add(new ContactBuilder().setName("Sh Jagdish Bunkar").setDesignation("ACCA (USO,LF I)").setOfficeContact("2477284").setMobileContact("9858431983").setStateId(stateId).createContact());
        contactArrayList.add(new ContactBuilder().setName("Sh. Kartar Nath").setDesignation("AO (Admin,Cash/GPF)").setEmail("cao.adminjk@nic.in").setOfficeContact("2475858").setStateId(stateId).setMobileContact("9419120984").createContact());
        contactArrayList.add(new ContactBuilder().setName("Sh Ram Asra").setDesignation("AAO(Pension)").setOfficeContact("2477285").setMobileContact("9855502925").createContact());
        contactArrayList.add(new ContactBuilder().setName("Sh Sanjeev Kumar").setDesignation("AAO(Cash/GPF)").setOfficeContact("2477283").setMobileContact("9469503844").setStateId(stateId).createContact());
        contactArrayList.add(new ContactBuilder().setName("Sh. Vinod Pandita").setDesignation("Cashier").setOfficeContact("2479268").setMobileContact("9419178910").setStateId(stateId).createContact());
        contactArrayList.add(new ContactBuilder().setName("Sh. Sardaroo Ram").setDesignation("Diary & Despatch").setOfficeContact("2477283").setStateId(stateId).createContact());
        contactArrayList.add(new ContactBuilder().setName("Smt. Barkha Dhar").setDesignation("USO").setOfficeContact("2479548").setStateId(stateId).createContact());
        contactArrayList.add(new ContactBuilder().setName("Sh. M.L. Sharma").setDesignation("Consultant(Spectrum)").setOfficeContact("2477285").setStateId(stateId).createContact());
        contactArrayList.add(new ContactBuilder().setName("Sh. R.S Sharma ").setDesignation("Consultant(LF)").setOfficeContact("2477282").setStateId(stateId).createContact());
        contactArrayList.add(new ContactBuilder().setName("Sh. D.D Sharma").setDesignation("Consultant(LF)").setOfficeContact("2477282").setStateId(stateId).createContact());
        contactArrayList.add(new ContactBuilder().setName("Sh. Ram Lal").setDesignation("Consultant(Admin)").setOfficeContact("2477282").setStateId(stateId).createContact());
        contactArrayList.add(new ContactBuilder().setName("Smt. Nirmal Sharma").setDesignation("Consultant(Pen)").setOfficeContact("2477284").setStateId(stateId).createContact());
        contactArrayList.add(new ContactBuilder().setName("Sh Neeraj Koul").setDesignation("Consultant (Pension)").setOfficeContact("2477284").setStateId(stateId).setMobileContact("9419286585").createContact());
        return contactArrayList;
    }
}
