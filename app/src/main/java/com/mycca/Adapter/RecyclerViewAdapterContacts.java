package com.mycca.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mycca.Models.Contact;
import com.mycca.R;

import java.util.ArrayList;

public class RecyclerViewAdapterContacts extends RecyclerView.Adapter<RecyclerViewAdapterContacts.ContactsViewHolder> {

    private ArrayList<Contact> contactArrayList;
    private Context context;
    private String na;

    public RecyclerViewAdapterContacts(ArrayList<Contact> contactArrayList, Context context) {
        this.contactArrayList = contactArrayList;
        this.context = context;
        na = context.getResources().getString(R.string.n_a);
    }

    @NonNull
    @Override
    public RecyclerViewAdapterContacts.ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContactsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_contact, parent, false), new ViewClickListener());
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsViewHolder holder, int position) {
        Contact contact = contactArrayList.get(position);

        holder.viewClickListener.setPosition(position);
        holder.name.setText(contact.getName());
        holder.designation.setText(contact.getDesignation());
        holder.email.setText(contact.getEmail()==null ? na : contact.getEmail());
        holder.office.setText(contact.getOfficeContact()==null? na : contact.getOfficeContact());
        holder.mobile.setText(contact.getMobileContact()==null ? na : contact.getMobileContact());
        if (contact.isExpanded()) {
            holder.linearLayoutExpandableArea.setVisibility(View.VISIBLE);
            holder.name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up_black_24dp, 0);
        } else {
            holder.linearLayoutExpandableArea.setVisibility(View.GONE);
            holder.name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down_black_24dp, 0);
        }

    }

    @Override
    public int getItemCount() {
        return contactArrayList.size();
    }

    class ContactsViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayoutExpandableArea;
        //,linearLayoutMobile,linearLayoutOffice,linearLayoutEMail;
        private TextView name;
        private TextView designation;
        private TextView mobile, email, office;
        private ViewClickListener viewClickListener;

        ContactsViewHolder(View itemView, ViewClickListener viewClickListener) {
            super(itemView);
            name = itemView.findViewById(R.id.textview_name);
            designation = itemView.findViewById(R.id.textview_designation);
            office = itemView.findViewById(R.id.tv_contact_office);
            mobile = itemView.findViewById(R.id.tv_contact_mobile);
            email = itemView.findViewById(R.id.tv_contact_email);


            email.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(context, R.drawable.ic_mail_outline_black_24dp), null, null, null);
            office.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(context, R.drawable.ic_landline), null, null, null);
            mobile.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(context, R.drawable.ic_phone_android_black_24dp), null, null, null);

            linearLayoutExpandableArea = itemView.findViewById(R.id.expandable_area_contact);
            linearLayoutExpandableArea.setVisibility(View.GONE);
            this.viewClickListener = viewClickListener;

            mobile.setOnClickListener(v -> {
                String number = mobile.getText().toString();
                Log.v("Adapter", "Contact = " + number);
                if (number.equals(na)) {
                    Toast.makeText(v.getContext(), "Contact details not available for this person", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + number));
                    v.getContext().startActivity(intent);
                }
            });

            office.setOnClickListener(v -> {
                String number = office.getText().toString();
                Log.v("Adapter", "Contact = " + number);
                if (number.equals(na)) {
                    Toast.makeText(v.getContext(), "Contact details not available for this person", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + "0191" + number));
                    v.getContext().startActivity(intent);
                }
            });

            email.setOnClickListener(v -> {
                String mail = email.getText().toString();
                Log.v("Adapter", "Contact = " + mail);
                if (mail.equals(na)) {
                    Toast.makeText(v.getContext(), "Email not available", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:"));
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{mail});
                    if (intent.resolveActivity(context.getPackageManager()) != null) {
                        v.getContext().startActivity(intent);
                    }

                }
            });
            itemView.setOnClickListener(viewClickListener);
        }
    }

    class ViewClickListener implements View.OnClickListener {
        public void setPosition(int position) {
            this.position = position;
        }

        private int position;


        @Override
        public void onClick(View v) {
            Contact contact = contactArrayList.get(position);
            contact.setExpanded(!contact.isExpanded());
            notifyItemChanged(position);
        }
    }
}
