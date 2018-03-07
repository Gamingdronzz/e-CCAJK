package com.ccajk.Models;

import com.ccajk.Tools.Helper;

/**
 * Created by hp on 07-03-2018.
 */

public class DistrictBuilder {
    String name;
    Helper.States state;

    public DistrictBuilder setName(String Name) {
        this.name = Name;
        return this;
    }

    public DistrictBuilder setState(Helper.States state) {
        this.state=state;
        return this;
    }

    public District createDistrict() {
        return new District(name,state);
    }
}
