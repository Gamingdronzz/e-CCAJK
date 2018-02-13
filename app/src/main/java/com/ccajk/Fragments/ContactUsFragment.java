package com.ccajk.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ccajk.Adapter.RecyclerViewAdapterContacts;
import com.ccajk.Models.Contact;
import com.ccajk.Models.ContactBuilder;
import com.ccajk.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactUsFragment extends Fragment {

    RecyclerView recyclerView;
    RecyclerViewAdapterContacts adapterContacts;
    ArrayList<Contact> contactArrayList = new ArrayList<>();

    public ContactUsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_us, container, false);
        recyclerView = view.findViewById(R.id.recyclerview_contacts);

        getContactsList();
        adapterContacts = new RecyclerViewAdapterContacts(contactArrayList);
        recyclerView.setAdapter(adapterContacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    private void getContactsList() {
        contactArrayList.add(new ContactBuilder().setName("Sh. Rajnish Kumar Jenaw ").setDesignation("CCA\nccajk@nic.in").setOfficeContact("2477280").setMobileContact("9419120080").createContact());
        contactArrayList.add(new ContactBuilder().setName("Sh Amanullah Tak").setDesignation("JT.CCA\naman.tak@gov.in").setOfficeContact("2477281").setMobileContact("9419120986").createContact());
    }

}
