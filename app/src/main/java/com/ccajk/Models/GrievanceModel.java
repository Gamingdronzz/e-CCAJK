package com.ccajk.Models;

import java.util.Date;

/**
 * Created by hp on 11-04-2018.
 */

public class GrievanceModel {

    String details;
    String email;
    long grievanceStatus;
    long grievanceType;
    boolean expanded;
    String mobile;
    String pensionerIdentifier;
    String state;
    String submittedBy;
    String message;
    Date date;


    public GrievanceModel() {

    }

    public GrievanceModel(String pensionerIdentifier, String mobile, long grievanceType, String details, String submittedBy, String email, String message, String state, long grievanceStatus, Date date) {
        this.pensionerIdentifier = pensionerIdentifier;
        this.mobile = mobile;
        this.grievanceType = grievanceType;
        this.details = details;
        this.submittedBy = submittedBy;
        this.email = email;
        this.message = message;
        this.state = state;
        this.grievanceStatus = grievanceStatus;
        this.date = date;
    }

    public boolean getExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public String getPensionerIdentifier() {
        return pensionerIdentifier;
    }

    public void setPensionerIdentifier(String pensionerIdentifier) {
        this.pensionerIdentifier = pensionerIdentifier;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public long getGrievanceType() {
        return grievanceType;
    }

    public void setGrievanceType(long grievanceType) {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
