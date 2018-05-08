package com.mycca.Models;

/**
 * Created by hp on 19-03-2018.
 */

public class PanAdhaar {
    String pensionerIdentifier;
    String cardNumOrUserName;
    String filename;
    String state;

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
