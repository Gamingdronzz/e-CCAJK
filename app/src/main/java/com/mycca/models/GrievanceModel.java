package com.mycca.models;

import java.util.Date;

public class GrievanceModel {

    private String details;
    private String email;
    private String mobile;
    private String pensionerIdentifier;
    private String state;
    private String submittedBy;
    private String message;
    private String uid;
    private String referenceNo;
    private Date date;
    private long grievanceStatus;
    private long grievanceType;
    private boolean expanded;
    private boolean highlighted = false;
    private boolean submissionSuccess;

    public GrievanceModel() {

    }

    public GrievanceModel(String pensionerIdentifier, long grievanceType, String referenceNo,
                          String details, String email, String mobile, String submittedBy,
                          long grievanceStatus, String state, String uid, Date date) {
        this.details = details;
        this.email = email;
        this.mobile = mobile;
        this.pensionerIdentifier = pensionerIdentifier;
        this.state = state;
        this.submittedBy = submittedBy;
        this.uid = uid;
        this.referenceNo = referenceNo;
        this.date = date;
        this.grievanceStatus = grievanceStatus;
        this.grievanceType = grievanceType;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    public boolean isExpanded() {
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

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }

    public boolean isSubmissionSuccess() {
        return submissionSuccess;
    }

    public void setSubmissionSuccess(boolean submissionSuccess) {
        this.submissionSuccess = submissionSuccess;
    }
}
