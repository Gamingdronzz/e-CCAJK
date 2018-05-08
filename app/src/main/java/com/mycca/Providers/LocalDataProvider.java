package com.mycca.Providers;

/**
 * Created by balpreet on 5/3/2018.
 */

public class LocalDataProvider {
    private int HotspotLocationCount = 198;
    private int GPLocationCount = 162;

    public int getHotspotLocationCount() {
        return HotspotLocationCount;
    }

    public void setHotspotLocationCount(int hotspotLocationCount) {
        HotspotLocationCount = hotspotLocationCount;
    }

    public int getGPLocationCount() {
        return GPLocationCount;
    }

    public void setGPLocationCount(int GPLocationCount) {
        this.GPLocationCount = GPLocationCount;
    }
}
