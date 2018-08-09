package com.mycca.tools;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class NewFireBaseHelper {

    private static NewFireBaseHelper mInstance;

    private DatabaseReference defaultDbRef;
    private DatabaseReference stateDbRef;
    private StorageReference defaultStorageRef;
    private StorageReference stateStorageRef;
    private FirebaseAuth auth;

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
    public final static String ROOT_WEBSITE="Website";
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
        defaultDbRef = FirebaseDatabase.getInstance().getReference();
        defaultStorageRef = FirebaseStorage.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
    }

    public static NewFireBaseHelper getInstance() {
        if (mInstance == null) {
            return new NewFireBaseHelper();
        } else {
            return mInstance;
        }
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public DatabaseReference getDefaultDbRef() {
        return defaultDbRef;
    }

    public DatabaseReference getStateDbRef() {
        return stateDbRef;
    }

    public StorageReference getDefaultStorageRef() {
        return defaultStorageRef;
    }

    public StorageReference getStateStorageRef() {
        return stateStorageRef;
    }

    public void setStateDbRef(String stateReference) {
        String url = "https://" + stateReference + ".firebaseio.com/";
        stateDbRef = FirebaseDatabase.getInstance(url).getReference();
    }

    public void setStateStorageRef(String stateReference) {
        String url = "gs://" + stateReference;
        stateStorageRef = FirebaseStorage.getInstance(url).getReference();
    }
}
