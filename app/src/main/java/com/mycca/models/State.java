package com.mycca.models;

public class State {
    private String circleCode;
    private String name;

    public State(String circleCode, String name) {
        this.circleCode = circleCode;
        this.name = name;
    }

    public String getCircleCode() {
        return circleCode;
    }

    public void setCircleCode(String circleCode) {
        this.circleCode = circleCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
