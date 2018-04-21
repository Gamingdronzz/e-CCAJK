package com.ccajk.Fragments;


import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ccajk.Adapter.RecyclerViewAdapterContacts;
import com.ccajk.Models.Contact;
import com.ccajk.Models.ContactBuilder;
import com.ccajk.R;
import com.ccajk.Tools.Helper;
import com.ccajk.Tools.Preferences;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactUsFragment extends Fragment {

    AppCompatTextView textViewOfficeAddress,textviewHeadingOfficeAddress;
    RecyclerView recyclerView;
    RecyclerViewAdapterContacts adapterContacts;
    ArrayList<Contact> contactArrayList;
    boolean isTab;
    public ContactUsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: done");
        View view = inflater.inflate(R.layout.fragment_contact_us, container, false);
        isTab = Helper.getInstance().isTab(this.getContext());
        bindViews(view);
        if(isTab)
        init(true);
        else
        {
            init(false);
        }

        return view;

    }

//    @Override
//    public void onConfigurationChanged(Configuration config) {
//        Log.d(TAG, "onConfigurationChanged: " + config.orientation);
//        super.onConfigurationChanged(config);
//        // Check for the rotation
//        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            init(true);
//        } else if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            if (isTab) {
//                init(true);
//            } else {
//                init(false);
//            }
//
//
//        }
//    }

    private void setupFragment()
    {

    }

    private void init(boolean isMultiColumn)
    {
        getContactsList(Preferences.getInstance().getPrefState(getContext()));

        adapterContacts = new RecyclerViewAdapterContacts(contactArrayList, getContext());
        recyclerView.setAdapter(adapterContacts);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        if(!isMultiColumn) {
            textviewHeadingOfficeAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ManageOfficeAddress();
                }
            });
            ManageOfficeAddress();
        }
        /*recyclerView.addOnItemTouchListener(new RecyclerViewTouchListeners(getContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                if(expandedPosition!=-1)
                {
                    Contact contact = contactArrayList.get(expandedPosition);
                    contact.setExpanded(false);
                }
                if(position == expandedPosition) {

                }
                else
                {
                    Contact contact = contactArrayList.get(position);
                    contact.setExpanded(!contact.isExpanded());
                    expandedPosition = position;
                    Log.d(TAG, "onClick: Changing expanded of " + position + "to " + contact.isExpanded());
                    adapterContacts.notifyDataSetChanged();
          }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));*/
    }

    private boolean ManageOfficeAddress()
    {
        if(textViewOfficeAddress.getVisibility() == View.GONE)
        {
            textViewOfficeAddress.setVisibility(View.VISIBLE);
            textviewHeadingOfficeAddress.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_arrow_drop_up_black_24dp,0);
            return false;
        }
        else
        {
            textViewOfficeAddress.setVisibility(View.GONE);
            textviewHeadingOfficeAddress.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_arrow_drop_down_black_24dp,0);
            return true;
        }
    }

    private void bindViews(View view)
    {
        recyclerView = view.findViewById(R.id.recyclerview_contacts);
        textViewOfficeAddress = view.findViewById(R.id.textview_office_address);
        textviewHeadingOfficeAddress = view.findViewById(R.id.textview_heading_office_address);
        textViewOfficeAddress.setText(getGeneralText(Preferences.getInstance().getPrefState(getContext())));
    }

    private String getGeneralText(String prefState) {
        return getResources().getString(R.string.contact_info);
    }

    private void getContactsList(String stateId) {
        contactArrayList = new ArrayList<>();
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
    }

}
