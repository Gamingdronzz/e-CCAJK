package com.ccajk.Models;

/**
 * Created by hp on 19-03-2018.
 */

public class PanAdhaar {
    String pcode;
    String number;
    String filename;
    String state;

    public String getPcode() {
        return pcode;
    }

    public void setPcode(String pcode) {
        this.pcode = pcode;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public PanAdhaar() {
    }

    public PanAdhaar(String pcode, String number, String filename, String state) {
        this.pcode = pcode;
        this.number = number;
        this.filename = filename;
        this.state = state;

    }
}
