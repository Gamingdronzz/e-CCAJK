package com.ccajk.Models;

/**
 * Created by hp on 19-03-2018.
 */

public class PanAdhaar {
    String pensionerIdentifier;
    String number;
    String filename;
    String state;
    //String msg;
    //Date uploadDate;
    //long status;

    public String getPensionerIdentifier() {
        return pensionerIdentifier;
    }

    public void setPensionerIdentifier(String pensionerIdentifier) {
        this.pensionerIdentifier = pensionerIdentifier;
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

   /* public long getStatus() {
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

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }
*/
    public PanAdhaar(String pensionerIdentifier, String number, String filename, String state) {
        this.pensionerIdentifier = pensionerIdentifier;
        this.number = number;
        this.filename = filename;
        this.state = state;
    }

    public PanAdhaar() {
    }

}
