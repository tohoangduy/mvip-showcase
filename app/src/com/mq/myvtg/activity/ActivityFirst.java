package com.mq.myvtg.activity;

import androidx.fragment.app.Fragment;

import com.mq.myvtg.fragment.FrgmtSplashScreen;

public class ActivityFirst extends ActivityBlank {
    @Override
    protected Fragment getFirstFragment() {
        return FrgmtSplashScreen.newInstance();
    }

}
