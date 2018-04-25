package com.ccajk.Models;

import java.util.Date;

/**
 * Created by hp on 24-04-2018.
 */

public class RtiModel {
    String name;
    String mobile;
    String subjectMatter;
    String state;
    long status;
    String message;
    Date date;

    public RtiModel() {
    }

    public RtiModel(String name, String mobile, String subjectMatter, String state, long status, String message, Date date) {

        this.name = name;
        this.mobile = mobile;
        this.subjectMatter = subjectMatter;
        this.state = state;
        this.status = status;
        this.message = message;
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSubjectMatter() {
        return subjectMatter;
    }

    public void setSubjectMatter(String subjectMatter) {
        this.subjectMatter = subjectMatter;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
