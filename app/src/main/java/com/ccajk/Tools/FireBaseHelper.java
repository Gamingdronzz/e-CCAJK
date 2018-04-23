package com.ccajk.Tools;

import android.net.Uri;

import com.ccajk.Models.Contact;
import com.ccajk.Models.ContactBuilder;
import com.ccajk.Models.LocationModel;
import com.ccajk.Models.State;
import com.ccajk.Providers.DummyLocationProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;


/**
 * Created by hp on 06-03-2018.
 */

public class FireBaseHelper {

    private static FireBaseHelper _instance;
    private ArrayList<LocationModel> locationModels;

    public DatabaseReference databaseReference;
    public StorageReference storageReference;
    public final String ROOT_ADHAAR = "Aadhaar";
    public final String ROOT_PAN = "Pan";
    public final String ROOT_LIFE = "Life Certificate";
    public final String ROOT_RE_MARRIAGE = "Re-Marriage Certificate";
    public final String ROOT_RE_EMPLOYMENT = "Re-Employment Certificate";
    public final String ROOT_GRIEVANCES = "Grievances";
    public final String ROOT_HOTSPOTS = "Hotspots";
    public final String ROOT_GP = "GP Locations";
    public final String ROOT_PASSWORD = "password";
    public final String ROOT_TYPE = "type";
    public final String ROOT_STAFF = "Staff";

    public final String GRIEVANCE_PENSION = "Pension Grievance";
    public final String GRIEVANCE_GPF = "GPF Grievance";

    private final String TAG = "firebaseHelper";
    //public final String ROOT_ADHAAR_STATUS = "Adhaar-Status";
    //public final String ROOT_PAN_STATUS = "Pan-Status";

    public ArrayList<State> statelist;

    public FireBaseHelper() {
        _instance = this;
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        statelist = getStatelist();
    }

    public static FireBaseHelper getInstance() {
        if (_instance == null) {
            return new FireBaseHelper();
        } else {
            return _instance;
        }
    }

    public UploadTask uploadFile(String rootFolder, String subFolder, String subType, String filePath, String filename) {
        StorageReference sref;
        if (subType != null) {
            sref = FireBaseHelper.getInstance().storageReference.child(rootFolder + "/" + subFolder + "/" + subType + "/" + filename);
        } else {
            sref = FireBaseHelper.getInstance().storageReference.child(rootFolder + "/" + subFolder);
        }

        Uri file = Uri.fromFile(new File(filePath));
        UploadTask uploadTask = sref.putFile(file);
        return uploadTask;
    }

    public ArrayList<LocationModel> getLocationModels(String stateId) {
        DummyLocationProvider dummyLocationProvider = new DummyLocationProvider();
        dummyLocationProvider.storeLocations("jnk");
        return dummyLocationProvider.getLocations();
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

    public ArrayList<State> getStatelist() {
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
    }


}
