package com.mq.myvtg.activity;

import android.os.Bundle;
import android.os.SystemClock;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mq.myvtg.R;
import com.mq.myvtg.util.UIHelper;

public abstract class ActivityBlank extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_activity);

        // Making notification bar transparent
        UIHelper.setOverlayStatusBar(this.getWindow(), true);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = getFirstFragment();
        ft.add(R.id.fragment_container, fragment, fragment.getClass().getSimpleName());
        ft.addToBackStack(fragment.getClass().getSimpleName());
        ft.commit();
    }

    protected abstract Fragment getFirstFragment();

    private long mLastClickTime = 0;

    public void goNext(Fragment fragment) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
//        ft.setCustomAnimations(
//                R.anim.alpha_show,
//                R.anim.alpha_hide,
//                R.anim.alpha_show,
//                R.anim.alpha_hide);
        ft.replace(R.id.fragment_container, fragment, fragment.getClass().getSimpleName());
        ft.addToBackStack(fragment.getClass().getSimpleName());
        ft.commit();
    }

    public void popTo(String name) {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 1) {
            fm.popBackStack(name, 0);
        }
    }

    @Override
    public void onBackPressed() {
        return;
    }
}

