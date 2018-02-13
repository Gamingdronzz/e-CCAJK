package com.ccajk.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ccajk.Adapter.RecyclerViewAdapterContacts;
import com.ccajk.Models.Contacts;
import com.ccajk.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactUsFragment extends Fragment {

    RecyclerView recyclerView;
    RecyclerViewAdapterContacts adapterContacts;
    ArrayList<Contacts> contactsArrayList = new ArrayList<>();

    public ContactUsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_us, container, false);
        recyclerView = view.findViewById(R.id.recyclerview_contacts);

        getContactsList();
        adapterContacts = new RecyclerViewAdapterContacts(contactsArrayList);
        recyclerView.setAdapter(adapterContacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    private void getContactsList() {
        contactsArrayList.add(new Contacts("Sh. Rajnish Kumar Jenaw ", "CCA\nccajk@nic.in", "2477280", "9419120080"));
        contactsArrayList.add(new Contacts("Sh Amanullah Tak", "JT.CCA\naman.tak@gov.in", "2477281", "9419120986"));
    }

}
