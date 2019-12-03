package com.mq.myvtg.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mq.myvtg.R;
import com.mq.myvtg.base.BaseFrgmtActivity;
import com.mq.myvtg.fragment.account.FrgmtForgotPwdInputPhoneNumber;

public class ActivityForgotPwd extends BaseFrgmtActivity {

    public ActivityForgotPwd() {
        contentViewId = R.layout.fragment_activity_title;
    }

    @Override
    protected Fragment getFirstFragment() {
        return FrgmtForgotPwdInputPhoneNumber.newInstance();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> {
            if (isFirstFragment()) {
                finish();
            } else {
                onBackPressed();
            }
        });
    }
}
