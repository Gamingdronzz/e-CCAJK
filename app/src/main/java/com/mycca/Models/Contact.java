package com.mycca.Models;

/**
 * Created by balpreet on 2/13/2018.
 */

public class Contact {
    public static final String NA="Not Available";
    private String name;
    private String designation;
    private String officeContact;
    private String mobileContact;
    private String email;
    private String stateId;

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    private boolean isExpanded;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getOfficeContact() {
        return officeContact;
    }

    public void setOfficeContact(String officeContact) {
        this.officeContact = officeContact;
    }

    public String getMobileContact() {
        return mobileContact;
    }

    public void setMobileContact(String mobileContact) {
        this.mobileContact = mobileContact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStateId() {
        return stateId;
    }

    public void setStateId(String stateId) {
        this.stateId = stateId;
    }

    public Contact(String name, String designation, String email, String officeContact, String mobileContact, String stateId) {

        this.name = name;
        this.email=email;
        this.designation = designation;
        this.officeContact = officeContact;
        this.mobileContact = mobileContact;
        this.stateId=stateId;
    }


}
