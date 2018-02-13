package com.ccajk.Models;

/**
 * Created by hp on 13-02-2018.
 */

public class Contacts {
    String name;
    String designation;
    String office;
    String mobile;

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

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Contacts(String name, String designation, String office, String mobile) {

        this.name = name;
        this.designation = designation;
        this.office = office;
        this.mobile = mobile;
    }
}
