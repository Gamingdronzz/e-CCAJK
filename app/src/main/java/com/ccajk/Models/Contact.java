package com.ccajk.Models;

/**
 * Created by balpreet on 2/13/2018.
 */

public class Contact {
    private String name;
    private String designation;
    private String officeContact;

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

    public Contact(String name, String designation, String officeContact, String mobileContact) {

        this.name = name;
        this.designation = designation;
        this.officeContact = officeContact;
        this.mobileContact = mobileContact;
    }

    private String mobileContact;
}
