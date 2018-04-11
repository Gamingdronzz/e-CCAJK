package com.ccajk.Models;

import java.util.Date;

/**
 * Created by hp on 19-03-2018.
 */

public class PanAdhaar {
    String pcode;
    String number;
    String filename;
    String state;
    String msg;
    Date uploadDate;
    long status;

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

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

    public long getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    public PanAdhaar(String pcode, String number, String filename, String state, long status, String msg,Date uploadDate) {
        this.pcode = pcode;
        this.number = number;
        this.filename = filename;
        this.state = state;
        this.status = status;
        this.msg=msg;
        this.uploadDate = uploadDate;
    }

    public PanAdhaar() {
    }

}
