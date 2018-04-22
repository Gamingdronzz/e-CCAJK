package com.ccajk.Fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ccajk.Adapter.RecyclerViewAdapterContacts;
import com.ccajk.Models.Contact;
import com.ccajk.R;
import com.ccajk.Tools.FireBaseHelper;
import com.ccajk.Tools.Helper;
import com.ccajk.Tools.Preferences;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactUsFragment extends Fragment {

    AppCompatTextView textViewOfficeAddress, textviewHeadingOfficeAddress,textviewContactPersonHeading;
    LinearLayout officeAddressLayout;
    AppCompatButton compatButtonLocateOnMap;
    RecyclerView recyclerView;
    RecyclerViewAdapterContacts adapterContacts;
    ArrayList<Contact> contactArrayList;
    boolean isTab;

    public ContactUsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: done");
        View view = inflater.inflate(R.layout.fragment_contact_us, container, false);
        isTab = Helper.getInstance().isTab(this.getContext());
        bindViews(view);
        if (isTab)
            init(true);
        else {
            init(false);
        }

        return view;

    }

    private void bindViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerview_contacts);
        textViewOfficeAddress = view.findViewById(R.id.textview_office_address);
        officeAddressLayout = view.findViewById(R.id.linear_layout_office_Address_Area);
        textviewHeadingOfficeAddress = view.findViewById(R.id.textview_heading_office_address);
        textviewContactPersonHeading = view.findViewById(R.id.textview_contact_person_heading);

        textviewContactPersonHeading.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_person_black_24dp,0,0,0);

        textViewOfficeAddress.setText(getGeneralText(Preferences.getInstance().getPrefState(getContext())));
        compatButtonLocateOnMap = view.findViewById(R.id.button_locate_on_map);
        compatButtonLocateOnMap.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_drawable_location,0,0,0);
        compatButtonLocateOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = "32.707500,74.874217";
                //Uri gmmIntentUri = Uri.parse("geo:"+location+"?q="+location+"(Office of\nController of Communication Accounts,\nJammu and Kashmir)");
                Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?q="+location+"(Office of CCA, JK)");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                //mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
                else
                {
                    Toast.makeText(getContext(),"No Map Application Installed",Toast.LENGTH_SHORT).show();
                }

            }
        });
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

    private void init(boolean isMultiColumn) {

        contactArrayList = FireBaseHelper.getInstance().getContactsList(Preferences.getInstance().getPrefState(getContext()));
        adapterContacts = new RecyclerViewAdapterContacts(contactArrayList, getContext());
        recyclerView.setAdapter(adapterContacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (!isMultiColumn) {
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

    private boolean ManageOfficeAddress() {
        if (officeAddressLayout.getVisibility() == View.GONE) {
            officeAddressLayout.setVisibility(View.VISIBLE);
            textviewHeadingOfficeAddress.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_office, 0, R.drawable.ic_arrow_drop_up_black_24dp, 0);
            return false;
        } else {
            officeAddressLayout.setVisibility(View.GONE);
            textviewHeadingOfficeAddress.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_office, 0, R.drawable.ic_arrow_drop_down_black_24dp, 0);
            return true;
        }
    }


    private String getGeneralText(String prefState) {
        return getResources().getString(R.string.contact_info);
    }

}
