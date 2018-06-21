package com.mycca.Models;

public class StaffModel {

    private String id;
    private String password;
    private long type;
    private String state;
    private String name;
    private String email;

    public StaffModel() {
    }

    public StaffModel(String id, String password, long type, String state, String name) {
        this.id = id;
        this.password = password;
        this.type = type;
        this.state = state;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
