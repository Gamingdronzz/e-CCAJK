package com.ccajk.Models;

import com.ccajk.Tools.Helper;

/**
 * Created by hp on 07-03-2018.
 */

public class District {

    String name;
    Helper.States state;

    public District(String name, Helper.States state) {
        this.name = name;
        this.state = state;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Helper.States getState() {
        return state;
    }

    public void setState(Helper.States state) {
        this.state = state;
    }
}
