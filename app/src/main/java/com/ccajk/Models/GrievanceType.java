package com.ccajk.Models;

/**
 * Created by hp on 18-04-2018.
 */

public class GrievanceType {
    String name;
    int id;

    public GrievanceType(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
