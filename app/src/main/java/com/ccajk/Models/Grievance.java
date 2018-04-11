package com.ccajk.Models;

import java.util.Date;

/**
 * Created by hp on 11-04-2018.
 */

public class Grievance {
    String pcode;
    String mobile;
    String grievanceType;
    String details;
    String submittedBy;
    String filename;
    String message;
    String state;
    long grievanceStatus;
    Date date;

    public Grievance(String pcode, String mobile, String grievanceType, String details, String submittedBy, String filename, String message, String state, long grievanceStatus, Date date) {
        this.pcode = pcode;
        this.mobile = mobile;
        this.grievanceType = grievanceType;
        this.details = details;
        this.submittedBy = submittedBy;
        this.filename = filename;
        this.message = message;
        this.state = state;
        this.grievanceStatus = grievanceStatus;
        this.date = date;
    }

    public Grievance() {

    }

    public String getPcode() {
        return pcode;
    }

    public void setPcode(String pcode) {
        this.pcode = pcode;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getGrievanceType() {
        return grievanceType;
    }

    public void setGrievanceType(String grievanceType) {
        this.grievanceType = grievanceType;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(String submittedBy) {
        this.submittedBy = submittedBy;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public long getGrievanceStatus() {
        return grievanceStatus;
    }

    public void setGrievanceStatus(long grievanceStatus) {
        this.grievanceStatus = grievanceStatus;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
