package com.ccajk.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ccajk.Models.Contacts;
import com.ccajk.R;

import java.util.ArrayList;

/**
 * Created by hp on 13-02-2018.
 */

public class RecyclerViewAdapterContacts extends RecyclerView.Adapter<RecyclerViewAdapterContacts.ContactsViewHolder> {

    ArrayList<Contacts> contactsArrayList;

    public RecyclerViewAdapterContacts(ArrayList<Contacts> contactsArrayList) {
        this.contactsArrayList = contactsArrayList;
    }

    @Override
    public RecyclerViewAdapterContacts.ContactsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerViewAdapterContacts.ContactsViewHolder viewHolder = new ContactsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_contact, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ContactsViewHolder holder, int position) {
        Contacts contact = contactsArrayList.get(position);
        holder.name.setText("Name: " + contact.getName());
        holder.designation.setText("Designation: " + contact.getDesignation());
        holder.office.setText("Office: " + contact.getOffice());
        holder.mobile.setText("Mobile: " + contact.getMobile());
    }

    @Override
    public int getItemCount() {
        return contactsArrayList.size();
    }

    public class ContactsViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView designation;
        private TextView office;
        private TextView mobile;

        public ContactsViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textview_name);
            designation = itemView.findViewById(R.id.textview_designation);
            office = itemView.findViewById(R.id.textview_office);
            mobile = itemView.findViewById(R.id.textview_mobile);
        }
    }
}
