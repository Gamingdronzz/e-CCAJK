package com.ccajk.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    Context context;

    public RecyclerViewAdapterContacts(ArrayList<Contact> contactArrayList, Context context) {
        this.contactArrayList = contactArrayList;
        this.context = context;
    }

    @Override
    public RecyclerViewAdapterContacts.ContactsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerViewAdapterContacts.ContactsViewHolder viewHolder = new ContactsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_contact, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ContactsViewHolder holder, int position) {
        Contact contact = contactArrayList.get(position);
        holder.name.setText(contact.getName());
        holder.designation.setText(contact.getDesignation());

        holder.email.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(context, R.drawable.ic_mail_outline_black_24dp), null, null, null);
        holder.email.setText("\t" + contact.getEmail());

        holder.office.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(context, R.drawable.ic_landline), null, null, null);
        holder.office.setText("\t" + contact.getOfficeContact());

        holder.mobile.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(context, R.drawable.ic_phone_android_black_24dp), null, null, null);
        holder.mobile.setText("\t" + contact.getMobileContact());

    }

    @Override
    public int getItemCount() {
        return contactArrayList.size();
    }

    public class ContactsViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView designation;
        private Button mobile, email, office;

        public ContactsViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textview_name);
            designation = itemView.findViewById(R.id.textview_designation);
            office = itemView.findViewById(R.id.button_office);
            mobile = itemView.findViewById(R.id.textview_mobile);
            email = itemView.findViewById(R.id.button_email);

            mobile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String number = mobile.getText().toString();
                    Log.v("Adapter", "Contact = " + number);
                    if (number.equals("\t"+Contact.NA)) {
                        Toast.makeText(v.getContext(), "Contact details not available for this person", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + number));
                        v.getContext().startActivity(intent);
                    }
                }
            });

            office.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String number = office.getText().toString();
                    Log.v("Adapter", "Contact = " + number);
                    if (number.equals("\t"+Contact.NA)) {
                        Toast.makeText(v.getContext(), "Contact details not available for this person", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + "0191" + number));
                        v.getContext().startActivity(intent);
                    }
                }
            });

            email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String mail = email.getText().toString();
                    Log.v("Adapter", "Contact = " + mail);
                    if (mail.equals("\t"+Contact.NA)) {
                        Toast.makeText(v.getContext(), "Email not available", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:"));
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{mail});
                        v.getContext().startActivity(intent);
                    }
                }
            });
        }
    }
}
