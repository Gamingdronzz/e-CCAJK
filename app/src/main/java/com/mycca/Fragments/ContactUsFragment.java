package com.mycca.Fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mycca.Activity.MainActivity;
import com.mycca.Adapter.RecyclerViewAdapterContacts;
import com.mycca.CustomObjects.FancyShowCase.FancyShowCaseQueue;
import com.mycca.CustomObjects.FancyShowCase.FancyShowCaseView;
import com.mycca.CustomObjects.FancyShowCase.FocusShape;
import com.mycca.Models.Contact;
import com.mycca.R;
import com.mycca.Tools.FireBaseHelper;
import com.mycca.Tools.Helper;
import com.mycca.Tools.Preferences;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class ContactUsFragment extends Fragment {

    AppCompatTextView textViewOfficeAddress, textviewHeadingOfficeAddress, textviewContactPersonHeading;
    LinearLayout officeAddressLayout;
    AppCompatButton compatButtonLocateOnMap;
    RecyclerView recyclerView;
    RecyclerViewAdapterContacts adapterContacts;
    ArrayList<Contact> contactArrayList;
    boolean isTab;
    MainActivity activity;

    public ContactUsFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container,
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
        if (Preferences.getInstance().getBooleanPref(getContext(), Preferences.PREF_HELP_CONTACT)) {
            showTutorial();
            Preferences.getInstance().setBooleanPref(getContext(), Preferences.PREF_HELP_CONTACT, false);
        }
        return view;

    }

    private void bindViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerview_contacts);
        textViewOfficeAddress = view.findViewById(R.id.textview_office_address);
        officeAddressLayout = view.findViewById(R.id.linear_layout_office_Address_Area);
        textviewHeadingOfficeAddress = view.findViewById(R.id.textview_heading_office_address);
        textviewContactPersonHeading = view.findViewById(R.id.textview_contact_person_heading);

        textviewContactPersonHeading.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_person_black_24dp, 0, 0, 0);

        textViewOfficeAddress.setText(getGeneralText(Preferences.getInstance().getStringPref(getContext(), Preferences.PREF_STATE)));
        compatButtonLocateOnMap = view.findViewById(R.id.button_locate_on_map);
        compatButtonLocateOnMap.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_drawable_location, 0, 0, 0);

    }

    private void init(boolean isMultiColumn) {

        activity = (MainActivity) getActivity();
        contactArrayList = FireBaseHelper.getInstance(getContext()).getContactsList(Preferences.getInstance().getStringPref(getContext(), Preferences.PREF_STATE));
        adapterContacts = new RecyclerViewAdapterContacts(contactArrayList, getContext());
        recyclerView.setAdapter(adapterContacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        compatButtonLocateOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = "32.707500,74.874217";
                Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?q=" + location + "(Office of CCA, JK)");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                if (mapIntent.resolveActivity(activity.getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    Toast.makeText(activity, "No Map Application Installed", Toast.LENGTH_SHORT).show();
                }

            }
        });

        if (!isMultiColumn) {
            textviewHeadingOfficeAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ManageOfficeAddress();
                }
            });
        }
        ManageOfficeAddress();

    }

    private void ManageOfficeAddress() {
        if (officeAddressLayout.getVisibility() == View.GONE) {
            officeAddressLayout.setVisibility(View.VISIBLE);
            textviewHeadingOfficeAddress.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_office, 0, R.drawable.ic_arrow_drop_up_black_24dp, 0);
        } else {
            officeAddressLayout.setVisibility(View.GONE);
            textviewHeadingOfficeAddress.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_office, 0, R.drawable.ic_arrow_drop_down_black_24dp, 0);
        }
    }

    private void showTutorial() {

        contactArrayList.get(0).setExpanded(true);
        adapterContacts.notifyItemChanged(0);

        final FancyShowCaseView fancyShowCaseView1 = new FancyShowCaseView.Builder(activity)
                .title("Touch to open office address")
                .focusOn(textviewHeadingOfficeAddress)
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .build();

        final FancyShowCaseView fancyShowCaseView2 = new FancyShowCaseView.Builder(activity)
                .title("Tap on any contact to open or close contact information")
                .focusOn(recyclerView)
                .focusCircleRadiusFactor(.8)
                .titleStyle(R.style.FancyShowCaseDefaultTitleStyle, Gravity.TOP | Gravity.CENTER)
                .build();

        final FancyShowCaseView fancyShowCaseView3 = new FancyShowCaseView.Builder(activity)
                .title("Tap on phone numbers to make call or on email to compose email")
                .focusOn(recyclerView)
                .focusCircleRadiusFactor(.6)
                .titleStyle(R.style.FancyShowCaseDefaultTitleStyle, Gravity.TOP | Gravity.CENTER)
                .build();

        activity.mQueue = new FancyShowCaseQueue()
                .add(fancyShowCaseView1)
                .add(fancyShowCaseView2)
                .add(fancyShowCaseView3);

        activity.mQueue.setCompleteListener(new com.mycca.CustomObjects.FancyShowCase.OnCompleteListener() {
            @Override
            public void onComplete() {
                activity.mQueue = null;
                contactArrayList.get(0).setExpanded(false);
                adapterContacts.notifyItemChanged(0);
            }
        });

        activity.mQueue.show();
    }

    private String getGeneralText(String prefState) {
        return getResources().getString(R.string.contact_info);
    }

}
