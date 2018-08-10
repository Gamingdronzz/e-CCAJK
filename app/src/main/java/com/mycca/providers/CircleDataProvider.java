package com.mycca.providers;

import com.mycca.models.State;

import java.util.ArrayList;

public class CircleDataProvider {

    private static CircleDataProvider _instance;
    private State[] states;

    private CircleDataProvider() {
        _instance = this;
    }

    public static CircleDataProvider getInstance() {
        if (_instance == null) {
            return new CircleDataProvider();
        } else {
            return _instance;
        }
    }

    public State[] getStates() {
        return states;
    }

    public void setStates(ArrayList<State> arrayList) {
        states = new State[arrayList.size()];
        int i = 0;
        for (State state : arrayList) {
            states[i++] = state;
        }
    }
}
