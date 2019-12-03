package com.mq.myvtg.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mq.myvtg.R;
import com.mq.myvtg.base.BaseFrgmtActivity;
import com.mq.myvtg.fragment.account.FrgmtConfirmOTPSignUp;

public class ActivitySignUpConfirmOTP extends BaseFrgmtActivity {

    public static final int ACT_RESULT_ID = 100;

    public ActivitySignUpConfirmOTP() {
        contentViewId = R.layout.fragment_activity_title;
    }

    @Override
    protected Fragment getFirstFragment() {
        String partnerCode = getIntent().getStringExtra("partnerCode");
        String isdn = getIntent().getStringExtra("isdn");
        String passwordReg = getIntent().getStringExtra("passwordReg");

        FrgmtConfirmOTPSignUp frgmtConfirmOTPSignUp = FrgmtConfirmOTPSignUp.newInstance();
        Bundle bundle = new Bundle();
        bundle.putString("partnerCode", partnerCode);
        bundle.putString("isdn", isdn);
        bundle.putString("passwordReg", passwordReg);
        frgmtConfirmOTPSignUp.setArguments(bundle);

        return frgmtConfirmOTPSignUp;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView actTitle = findViewById(R.id.actTitle);
        actTitle.setText(R.string.confirm);
        View btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> {
            if (isFirstFragment()) {
                finish();
            } else {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
