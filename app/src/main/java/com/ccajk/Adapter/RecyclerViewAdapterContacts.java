package com.ccajk.Adapter;

import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ccajk.Models.Contact;
import com.ccajk.R;

import java.util.ArrayList;

/**
 * Created by hp on 13-02-2018.
 */

public class RecyclerViewAdapterContacts extends RecyclerView.Adapter<RecyclerViewAdapterContacts.ContactsViewHolder> {

    ArrayList<Contact> contactArrayList;

    public RecyclerViewAdapterContacts(ArrayList<Contact> contactArrayList) {
        this.contactArrayList = contactArrayList;
    }

    @Override
    public RecyclerViewAdapterContacts.ContactsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerViewAdapterContacts.ContactsViewHolder viewHolder = new ContactsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_contact, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ContactsViewHolder holder, int position) {
        Contact contact = contactArrayList.get(position);
        holder.name.setText("Name: " + contact.getName());
        holder.designation.setText("Designation: " + contact.getDesignation());
        holder.office.setText("Office: " + contact.getOfficeContact());
        holder.mobile.setText(contact.getMobileContact());
    }

    @Override
    public int getItemCount() {
        return contactArrayList.size();
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

            mobile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String number = mobile.getText().toString();
                    Log.v("Adapter","Contact = " + number);
                    if (number == null || number.equals("")) {
                        Toast.makeText(v.getContext(), "Contact details not available for this person", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + number));
                    v.getContext().startActivity(intent);
                }
            });
        }
    }
}
