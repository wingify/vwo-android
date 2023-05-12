package com.vwo.mobile.meg;

import java.util.ArrayList;

/**
 * The class that holds the weight received from the server for advance allocation.
 */
public class Weight {

    private final ArrayList<Float> range;

    private final String campaign;

    public Weight(String campaign, ArrayList<Float> range) {
        this.campaign = campaign;
        this.range = range;
    }

    public String getCampaign() {
        return campaign;
    }

    public float getRangeStart() {
        return range.get(0);
    }

    public float getRangeEnd() {
        return range.get(1);
    }

    public ArrayList<Float> getRange() {
        return range;
    }

}
