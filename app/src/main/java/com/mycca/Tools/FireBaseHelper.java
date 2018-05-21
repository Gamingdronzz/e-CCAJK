package com.mycca.Tools;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mycca.Models.Contact;
import com.mycca.Models.ContactBuilder;
import com.mycca.Models.GrievanceModel;
import com.mycca.Models.InspectionModel;
import com.mycca.Models.PanAdhaar;
import com.mycca.Models.SelectedImageModel;

import java.util.ArrayList;


/**
 * Created by hp on 06-03-2018.
 */

public class FireBaseHelper {

    private static FireBaseHelper _instance;

    public DatabaseReference databaseReference;
    public StorageReference storageReference;
    public FirebaseAuth mAuth;

    public final static String ROOT_GRIEVANCES = "Grievances";
    public final static String ROOT_APP_VERSION = "Latest Version";
    public final static String ROOT_ADHAAR = "Aadhaar";
    public final static String ROOT_PAN = "Pan";
    public final static String ROOT_LIFE = "Life Certificate";
    public final static String ROOT_RE_MARRIAGE = "Re-Marriage Certificate";
    public final static String ROOT_RE_EMPLOYMENT = "Re-Employment Certificate";
    public final static String ROOT_HOTSPOTS = "Wifi Locations";
    public final static String ROOT_GP = "GP Locations";
    public final static String ROOT_STAFF = "Staff Login";
    public final static String ROOT_INSPECTION = "Inspection";
    public final static String ROOT_SUGGESTIONS = "Suggestions";
    public final static String ROOT_TOKEN = "Tokens";

    public final static String GRIEVANCE_PENSION = "Pension";
    public final static String GRIEVANCE_GPF = "GPF";
    public String version;
    private final String TAG = "firebaseHelper";

    //public GrievanceModel selectedGrievance;
    //public final String ROOT_ADHAAR_STATUS = "Adhaar-Status";
    //public final String ROOT_PAN_STATUS = "Pan-Status";

    public FireBaseHelper(Context context) {
        _instance = this;
        try {
            version = String.valueOf(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            version = "2";
            e.printStackTrace();
        }
        databaseReference = FirebaseDatabase.getInstance().getReference().child(version);
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

    public void setToken() {
        Log.d(TAG, "setToken: ");
        DatabaseReference dbref = databaseReference;
        if (mAuth.getCurrentUser() != null) {
            Log.d(TAG, "setToken: user found");
            dbref.child(ROOT_TOKEN).child(mAuth.getCurrentUser().getUid()).
                    setValue(FirebaseInstanceId.getInstance().getToken())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Token added");
                            }
                        }
                    });
        }
    }

    public Task uploadDataToFirebase(String root, Object model, String... params) {
        DatabaseReference dbref = databaseReference.child(root);
        Task task;

        if (root.equals(ROOT_SUGGESTIONS)) {
            task = dbref.push().setValue(model);
        } else if (root.equals(ROOT_GRIEVANCES)) {

            GrievanceModel grievanceModel = (GrievanceModel) model;
            task = dbref.child(grievanceModel.getState())
                    .child(grievanceModel.getPensionerIdentifier())
                    .child(String.valueOf(grievanceModel.getGrievanceType()))
                    .setValue(grievanceModel);

        } else if (root.equals(ROOT_INSPECTION)) {
            InspectionModel inspectionModel = (InspectionModel) model;
            task = dbref.child(params[0])
                    .child(params[1])
                    .setValue(inspectionModel);

        } else {
            PanAdhaar panAdhaar = (PanAdhaar) model;
            task = dbref.child(panAdhaar.getState())
                    .child(panAdhaar.getPensionerIdentifier())
                    .setValue(panAdhaar);
        }
        return task;
    }


    public UploadTask uploadFiles(SelectedImageModel imageFile, boolean multiple, int count, String... params) {
        StorageReference sref;
        StringBuilder sb = new StringBuilder();
        for (String param : params)
            sb.append(param + "/");
        if (multiple) {
            sref = storageReference.child(sb + "File" + count);
        } else {
            sref = storageReference.child(sb.toString());
        }
        UploadTask uploadTask = sref.putFile(imageFile.getImageURI());
        return uploadTask;
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
