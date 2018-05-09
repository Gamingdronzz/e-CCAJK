package com.mycca.Models;

/**
 * Created by hp on 16-03-2018.
 */

public class State {
    String circleCode;
    String name;

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
