package com.mycca.Models;

/**
 * Created by hp on 14-05-2018.
 */

public class StaffModel {
    String id;
    String password;
    long type;
    String state;

    public StaffModel() {
    }

    public StaffModel(String id, String password, long type, String state) {
        this.id = id;
        this.password = password;
        this.type = type;
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
