package com.ccajk.Models;

/**
 * Created by hp on 12-03-2018.
 */

public class Grievance {
    int grievanceId;
    String type;

    public Grievance(int id, String type) {
        this.grievanceId = id;
        this.type = type;
    }

    public int getGrievanceId() {
        return grievanceId;
    }

    public void setGrievanceId(int grievanceId) {
        this.grievanceId = grievanceId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
