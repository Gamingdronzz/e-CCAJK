package com.mycca.models;

public class PanAdhaar {
    private String pensionerIdentifier;
    private String cardNumOrUserName;
    private String filename;
    private String state;

    public String getPensionerIdentifier() {
        return pensionerIdentifier;
    }

    public void setPensionerIdentifier(String pensionerIdentifier) {
        this.pensionerIdentifier = pensionerIdentifier;
    }

    public String getCardNumOrUserName() {
        return cardNumOrUserName;
    }

    public void setCardNumOrUserName(String cardNumOrUserName) {
        this.cardNumOrUserName = cardNumOrUserName;
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

    public PanAdhaar(String pensionerIdentifier, String cardNumOrUserName, String filename, String state) {
        this.pensionerIdentifier = pensionerIdentifier;
        this.cardNumOrUserName = cardNumOrUserName;
        this.filename = filename;
        this.state = state;
    }

    public PanAdhaar() {
    }

}
