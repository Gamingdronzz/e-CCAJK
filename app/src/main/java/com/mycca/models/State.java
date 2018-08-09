package com.mycca.models;

public class State {
    private String code;
    private String en;
    private String hi;
    private String database;
    private String mails;
    private boolean active;

    public State() {
    }

    public State(String code, String en, String hi, String database, String mails, boolean active) {
        this.code = code;
        this.en = en;
        this.hi = hi;
        this.database = database;
        this.mails = mails;
        this.active = active;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEn() {
        return en;
    }

    public void setEn(String en) {
        this.en = en;
    }

    public String getHi() {
        return hi;
    }

    public void setHi(String hi) {
        this.hi = hi;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getMails() {
        return mails;
    }

    public void setMails(String mails) {
        this.mails = mails;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
