package com.mycca.Models;

/**
 * Created by hp on 16-03-2018.
 */

public class State {
    String id;
    String name;

    public State(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {

        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
