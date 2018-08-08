package com.mycca.tools;

import android.content.Context;
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
import com.mycca.models.Contact;
import com.mycca.models.ContactBuilder;
import com.mycca.models.NewsModel;
import com.mycca.models.SelectedImageModel;

import java.util.ArrayList;
import java.util.HashMap;

public class FireBaseHelper {

    private static FireBaseHelper _instance;

    public DatabaseReference versionedDbRef;
    public DatabaseReference nonVersionedDbref;
    public DatabaseReference nonVersionedStateDbRef;
    private StorageReference storageReference;
    public FirebaseAuth mAuth;

    public final static String ROOT_GRIEVANCES = "Grievances";
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
    public final static String ROOT_REF_NUMBERS = "Reference Numbers";
    public final static String ROOT_BY_USER = "By User";
    static final String ROOT_PASSWORD = "password";
    public final static String ROOT_BY_STAFF = "By Staff";
    //public final static String ROOT_IMAGES_BY_STAFF = "Images By Staff";
    private final static String ROOT_STATE_DATA = "State Data";
    private final static String ROOT_REF_COUNT = "Reference Number Count";
    private final static String ROOT_APP_VERSION = "Latest Version";

    public final static int NONVERSIONED = 0;
    public final static int NONVERSIONED_STATEWISE = 1;
    public final static int VERSIONED = 2;

    public String version;
    private final String TAG = "firebaseHelper";

    public FireBaseHelper(Context context) {
        _instance = this;

        version = String.valueOf(Helper.getInstance().getAppVersion(context));
        if (version.equals("-1"))
            version = "5";

        nonVersionedDbref = FirebaseDatabase.getInstance().getReference();
        versionedDbRef = FirebaseDatabase.getInstance().getReference().child(version);
        nonVersionedStateDbRef = FirebaseDatabase.getInstance().getReference()
                .child(ROOT_STATE_DATA);

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

    public void addTokenOnFirebaseDatabase() {
        CustomLogger.getInstance().logDebug("addTokenOnFirebaseDatabase: ");
        DatabaseReference dbref = versionedDbRef;
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
            if (mAuth.getCurrentUser() != null) {
                dbref.child(ROOT_TOKEN).child(mAuth.getCurrentUser().getUid()).
                        setValue(instanceIdResult.getToken()).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        CustomLogger.getInstance().logDebug("Token added");
                    }
                });
            }
        });
    }

    public Task<Void> uploadDataToFirebase(Object model, String... params) {
        Task<Void> task;
        DatabaseReference dbref = versionedDbRef;

        for (String key : params) {
            CustomLogger.getInstance().logDebug("Firebase Helper child : " + key);
            dbref = dbref.child(key);
        }
        CustomLogger.getInstance().logDebug("uploadDataToFirebase: loop ended");
        task = dbref.setValue(model);
        CustomLogger.getInstance().logDebug("uploadDataToFirebase: setting value");
        return task;
    }

    public Task<Void> uploadDataToFirebase(Object model, String root) {

        DatabaseReference dbref = versionedDbRef.child(root);
        Task<Void> task = null;

        switch (root) {
            case ROOT_NEWS:
                NewsModel newsModel = (NewsModel) model;
                if (newsModel.getKey() == null) {
                    CustomLogger.getInstance().logDebug("news key null");
                    String key = dbref.push().getKey();
                    newsModel.setKey(key);
                    task = dbref.child(key).setValue(newsModel);
                }
                break;
            case ROOT_SUGGESTIONS:
                task = dbref.push().setValue(model);
                break;
            default:
                task = dbref.setValue(model);
        }

        return task;
    }

    public Task<Void> updateData(String key, HashMap<String, Object> hashMap, String... params) {
        DatabaseReference dbref = versionedDbRef;
        Task<Void> task;
        for (String param : params) {
            dbref = dbref.child(param);
        }
        task = dbref.child(key).updateChildren(hashMap);
        return task;
    }

    Task<Void> updatePassword(String pwd, String staffId) {
        DatabaseReference dbref = nonVersionedDbref.child(ROOT_STAFF).child(staffId);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(ROOT_PASSWORD, pwd);
        return dbref.updateChildren(hashMap);
    }

    public void getDataFromFirebase(ChildEventListener childEventListener, int versioned, String... params) {
        DatabaseReference dbref;
        if (versioned == VERSIONED)
            dbref = versionedDbRef;
        else if (versioned == NONVERSIONED)
            dbref = nonVersionedDbref;
        else
            dbref = nonVersionedStateDbRef;

        CustomLogger.getInstance().logDebug("getting DataFromFirebase: ");
        for (String key : params) {
            CustomLogger.getInstance().logDebug("Firebase key : " + key);
            dbref = dbref.child(key);
        }

        dbref.addChildEventListener(childEventListener);
    }

    public void getDataFromFirebase(ValueEventListener valueEventListener, int versioned, boolean singleValueEvent, String... params) {
        DatabaseReference dbref;
        if (versioned == VERSIONED)
            dbref = versionedDbRef;
        else if (versioned == NONVERSIONED_STATEWISE)
            dbref = nonVersionedStateDbRef;
        else
            dbref = nonVersionedDbref;

        for (String key : params) {
            CustomLogger.getInstance().logDebug("Firebase Helper key : " + key);
            dbref = dbref.child(key);
        }
        if (singleValueEvent)
            dbref.addListenerForSingleValueEvent(valueEventListener);
        else
            dbref.addValueEventListener(valueEventListener);
    }

    public void checkForUpdate(ValueEventListener valueEventListener) {
        versionedDbRef.child(FireBaseHelper.ROOT_APP_VERSION)
                .addListenerForSingleValueEvent(valueEventListener);
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

    public Task<Uri> getFileFromFirebase(String path) {
        return storageReference.child(path).getDownloadUrl();
    }

    public void getReferenceNumber(Transaction.Handler handler, String circleCode) {
        CustomLogger.getInstance().logDebug("getReferenceNumber: ");
        DatabaseReference dbref;
        dbref = nonVersionedStateDbRef.child(circleCode).child(ROOT_REF_COUNT);
        dbref.runTransaction(handler);
    }

    public ArrayList<Contact> getContactsList() {
        ArrayList<Contact> contactArrayList = new ArrayList<>();
        contactArrayList.add(new ContactBuilder().setName("Sh. Rajnish Kumar Jenaw ").setDesignation("CCA").setEmail("ccajk@nic.in").setOfficeContact("2477280").setMobileContact("9419120080").createContact());
        contactArrayList.add(new ContactBuilder().setName("Sh Amanullah Tak").setDesignation("JT.CCA").setEmail("aman.tak@gov.in").setOfficeContact("2477281").setMobileContact("9419120986").createContact());
        contactArrayList.add(new ContactBuilder().setName("Smt. Veena Gupta").setDesignation("ACCA (Spectrum/Pension)").setEmail("accajk@nic.in").setOfficeContact("2477283").setMobileContact("9419120332").createContact());
        contactArrayList.add(new ContactBuilder().setName("Sh Jagdish Bunkar").setDesignation("ACCA (USO,LF I)").setOfficeContact("2477284").setMobileContact("9858431983").createContact());
        contactArrayList.add(new ContactBuilder().setName("Sh. Kartar Nath").setDesignation("AO (Admin,Cash/GPF)").setEmail("cao.adminjk@nic.in").setOfficeContact("2475858").setMobileContact("9419120984").createContact());
        contactArrayList.add(new ContactBuilder().setName("Sh Ram Asra").setDesignation("AAO(Pension)").setOfficeContact("2477285").setMobileContact("9855502925").createContact());
        contactArrayList.add(new ContactBuilder().setName("Sh Sanjeev Kumar").setDesignation("AAO(Cash/GPF)").setOfficeContact("2477283").setMobileContact("9469503844").createContact());
        contactArrayList.add(new ContactBuilder().setName("Sh. Vinod Pandita").setDesignation("Cashier").setOfficeContact("2479268").setMobileContact("9419178910").createContact());
        contactArrayList.add(new ContactBuilder().setName("Sh. Sardaroo Ram").setDesignation("Diary & Despatch").setOfficeContact("2477283").createContact());
        contactArrayList.add(new ContactBuilder().setName("Smt. Barkha Dhar").setDesignation("USO").setOfficeContact("2479548").createContact());
        contactArrayList.add(new ContactBuilder().setName("Sh. M.L. Sharma").setDesignation("Consultant(Spectrum)").setOfficeContact("2477285").createContact());
        contactArrayList.add(new ContactBuilder().setName("Sh. R.S Sharma ").setDesignation("Consultant(LF)").setOfficeContact("2477282").createContact());
        contactArrayList.add(new ContactBuilder().setName("Sh. D.D Sharma").setDesignation("Consultant(LF)").setOfficeContact("2477282").createContact());
        contactArrayList.add(new ContactBuilder().setName("Sh. Ram Lal").setDesignation("Consultant(Admin)").setOfficeContact("2477282").createContact());
        contactArrayList.add(new ContactBuilder().setName("Smt. Nirmal Sharma").setDesignation("Consultant(Pen)").setOfficeContact("2477284").createContact());
        contactArrayList.add(new ContactBuilder().setName("Sh Neeraj Koul").setDesignation("Consultant (Pension)").setOfficeContact("2477284").setMobileContact("9419286585").createContact());
        return contactArrayList;
    }


}
