package com.ccajk.Models;

import java.util.Date;

/**
 * Created by hp on 19-03-2018.
 */

public class PanAdhaarStatus {
    Date appliedDate;
    Date processingDate;
    Date resultDate;
    String status;

    public Date getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(Date applied) {
        this.appliedDate = applied;
    }

    public Date getProcessingDate() {
        return processingDate;
    }

    public void setProcessingDate(Date processing) {
        this.processingDate = processing;
    }

    public Date getResultDate() {
        return resultDate;
    }

    public void setResultDate(Date result) {
        this.resultDate = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public PanAdhaarStatus() {
    }

    public PanAdhaarStatus(Date appliedDate, Date processingDate, Date resultDate, String status) {

        this.appliedDate = appliedDate;
        this.processingDate = processingDate;
        this.resultDate = resultDate;
        this.status = status;
    }
}
