package com.mycca.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.mycca.Adapter.RecyclerViewAdapterStates;
import com.mycca.Listeners.ClickListener;
import com.mycca.Listeners.RecyclerViewTouchListeners;
import com.mycca.Models.State;
import com.mycca.R;
import com.mycca.Tools.Helper;
import com.mycca.Tools.Preferences;

public class StateSettingActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state_setting);

        recyclerView = findViewById(R.id.recycler_view_state_setting);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerViewAdapterStates adapter = new RecyclerViewAdapterStates();
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerViewTouchListeners(this, recyclerView,
                new ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        State state = Helper.getInstance().getStatelist()[position];
                        Preferences.getInstance().setStringPref(StateSettingActivity.this,
                                Preferences.PREF_STATE,
                                state.getCircleCode());
                        Log.d("Settings", "onClick: " + state.getName() + " " + state.getCircleCode());
                        finish();
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));
    }
}