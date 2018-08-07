package com.mycca.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mycca.adapter.RecyclerViewAdapterStates;
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

        recyclerView.addOnItemTouchListener(new RecyclerViewTouchListeners(this, recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Helper.getInstance().showReloadWarningDialog(StateSettingActivity.this, () -> changeState(position));
            }

            @Override
            public void onLongClick(View view, int position) {

            }

        }));
    }

    public void changeState(int position) {
        State state = Helper.getInstance().getStateList()[position];
        Preferences.getInstance().setStringPref(this,
                Preferences.PREF_STATE,
                state.getCircleCode());
        Helper.getInstance().reloadApp(this);
    }
}