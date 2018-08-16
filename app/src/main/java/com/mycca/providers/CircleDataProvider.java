package com.mycca.providers;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mycca.listeners.DownloadCompleteListener;
import com.mycca.models.State;
import com.mycca.tools.CustomLogger;
import com.mycca.tools.Helper;
import com.mycca.tools.IOHelper;
import com.mycca.tools.FireBaseHelper;
import com.mycca.tools.Preferences;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CircleDataProvider {

    private static CircleDataProvider _instance;
    private State[] states;
    private State[] activeStates;
    private int activeCount;
    private int stateCount;

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

    public void setCircleData(Boolean fromFirebase, Context context, DownloadCompleteListener downloadCompleteListener) {
        if (fromFirebase) {
            getCircleDataFromFireBase(context, downloadCompleteListener);
        } else {
            IOHelper.getInstance().readFromFile(context, "Circle Data", null,
                    jsonObject -> {
                        Gson gson = new Gson();
                        Type collectionType = new TypeToken<ArrayList<State>>() {
                        }.getType();
                        ArrayList<State> stateArrayList = gson.fromJson(jsonObject.toString(), collectionType);
                        setArrayLists(stateArrayList,
                                Preferences.getInstance().getIntPref(context, Preferences.PREF_ACTIVE_CIRCLES));
                    });
        }
    }

    private void setArrayLists(ArrayList<State> arrayList, int activeCount) {
        int i = 0, j = 0;
        if (arrayList != null) {
            states = new State[arrayList.size()];
            activeStates = new State[activeCount];

            for (State state : arrayList) {
                states[i++] = state;
                if (state.isActive())
                    activeStates[j++] = state;
            }
        }
    }

    public State[] getActiveCircleData() {
        return activeStates;
    }

    public State[] getCircleData() {
        return states;
    }

    public State getStateFromCode(String code){
        for(State state : activeStates)
            if(state.getCode().equals(code))
                return state;
        return null;
    }

    public void getCircleDataFromFireBase(Context context, DownloadCompleteListener downloadCompleteListener) {

        ArrayList<State> stateArrayList = new ArrayList<>();

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CustomLogger.getInstance().logDebug("Got circle data from firebase");
                activeCount = 0;
                stateCount = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    try {
                        State state = ds.getValue(State.class);
                        stateArrayList.add(state);
                        stateCount++;
                        if (state.isActive()) {
                            activeCount++;
                        }
                    } catch (DatabaseException | NullPointerException e) {
                        CustomLogger.getInstance().logDebug(e.getMessage());
                    }
                }

                setArrayLists(stateArrayList, activeCount);
                Helper.dataChecked = true;

                IOHelper.getInstance().writeToFile(context, new Gson().toJson(stateArrayList),
                        "Circle Data", null,
                        success -> {
                            if (success) {
                                CustomLogger.getInstance().logDebug("Write circle Success..Setting Preferences = " + stateCount + "," + activeCount);
                                Preferences.getInstance().setIntPref(context, Preferences.PREF_CIRCLES, stateCount);
                                Preferences.getInstance().setIntPref(context, Preferences.PREF_ACTIVE_CIRCLES, activeCount);
                                if (downloadCompleteListener != null)
                                    downloadCompleteListener.onDownloadSuccess();
                            } else {
                                CustomLogger.getInstance().logDebug("Write circle Failed");
                                if (downloadCompleteListener != null)
                                    downloadCompleteListener.onDownloadFailure();
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (downloadCompleteListener != null)
                    downloadCompleteListener.onDownloadFailure();
            }
        };

        FireBaseHelper.getInstance().getDataFromFireBase(null, valueEventListener, false, FireBaseHelper.ROOT_CIRCLE_DATA);

    }

}
