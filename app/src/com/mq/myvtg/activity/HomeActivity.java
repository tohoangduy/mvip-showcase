package com.mq.myvtg.activity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.mq.myvtg.R;
import com.mq.myvtg.base.BaseFrgmtActivity;
import com.mq.myvtg.fragment.FrgmtHome;
import com.mq.myvtg.observer.ObserverManager;

public class HomeActivity extends BaseFrgmtActivity {

    public HomeActivity() {
        contentViewId = R.layout.fragment_activity_home;
    }

    @Override
    protected Fragment getFirstFragment() {
        return FrgmtHome.newInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ObserverManager.register(this, ObserverManager.ObserverId.ACTIVITY_HOME);
    }
}

