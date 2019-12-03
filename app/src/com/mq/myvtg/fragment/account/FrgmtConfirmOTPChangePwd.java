package com.mq.myvtg.fragment.account;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.mq.myvtg.R;
import com.mq.myvtg.api.Client;
import com.mq.myvtg.api.soap.SoapResponse;
import com.mq.myvtg.base.BaseFrgmt;
import com.mq.myvtg.base.BaseFrgmtActivity;
import com.mq.myvtg.dialog.CustomDialog;
import com.mq.myvtg.util.Const;
import com.mq.myvtg.util.UIHelper;
import com.mq.myvtg.util.Utils;

import java.util.HashMap;
import java.util.Map;

public class FrgmtConfirmOTPChangePwd extends BaseFrgmt
        implements View.OnClickListener {

    View btnResend, btnConfirm, btnShowPassword, btnShowConfirmPassword;
    EditText edNewPassword, edConfirmPassword, edOTP;

    String isdn;

    public FrgmtConfirmOTPChangePwd(String isdn) {
        this.isdn = isdn;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        doGetOTP();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.frgmt_confirm_otp_change_pwd, container, false);
        edOTP = contentView.findViewById(R.id.edOTP);
        btnResend = contentView.findViewById(R.id.btnResend);
        edNewPassword = contentView.findViewById(R.id.edNewPassword);
        edConfirmPassword = contentView.findViewById(R.id.edConfirmPassword);
        btnConfirm = contentView.findViewById(R.id.btnConfirm);
        btnShowPassword = contentView.findViewById(R.id.btnShowPassword);
        btnShowConfirmPassword = contentView.findViewById(R.id.btnShowConfirmPassword);
        TextView tvIsdn = contentView.findViewById(R.id.tvIsdn);
        tvIsdn.setText(isdn);

        // register listener
        btnResend.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        btnShowPassword.setOnClickListener(this);
        btnShowConfirmPassword.setOnClickListener(this);

        return contentView;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnResend:
                doGetOTP();
                break;

            case R.id.btnConfirm:
                String validateErr = validate();
                if (validateErr.isEmpty()) {
                    doChangePassword();
                } else {
                    UIHelper.informWarning(getContext(), validateErr);
                }
                break;

            case R.id.btnShowPassword:
                changeEyeIcon(edNewPassword, btnShowPassword);
                break;

            case R.id.btnShowConfirmPassword:
                changeEyeIcon(edConfirmPassword, btnShowConfirmPassword);
                break;
        }
    }

    private String validate() {
        String err = "";
        Context context = getContext();
        if (edOTP.getText().toString().trim().isEmpty()) {
            err = context.getResources().getString(R.string.msg_warning_enter_otp);
        } else if (edNewPassword.getText().toString().length() < Const.PASSWORD_LENGTH) {
            err = context.getString(R.string.msd_invalid_password_length, String.valueOf(Const.PASSWORD_LENGTH));
        } else if (!edConfirmPassword.getText().toString().equals(edNewPassword.getText().toString())) {
            err = context.getString(R.string.password_and_confirm_password_not_same);
        }
        return err;
    }

    private void changeEyeIcon(EditText editText, View view) {
        if (editText.getInputType() == InputType.TYPE_CLASS_TEXT) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            view.setBackground(getResources().getDrawable(R.drawable.ic_eye_lock));
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            view.setBackground(getResources().getDrawable(R.drawable.ic_eye));
        }
    }

    private void doChangePassword() {
        Map<String, Object> params = new HashMap<>();
        params.put("isdn", isdn);
        params.put("locale", Utils.getLocale(getContext()));
        params.put("otp", edOTP.getText().toString().trim());
        params.put("newPass", edNewPassword.getText().toString().trim());
        params.put("confirmPass", edConfirmPassword.getText().toString().trim());

        Client.getInstance().request(getContext(), Client.WS_FORGOT_PWD_CONFIRM, params, new SoapResponse(getContext()) {
            @Override
            protected void success(String resValue, String uiMsg) {
                showDialogSuccess(uiMsg);
            }

            @Override
            protected void failed(String reason, String uiMsg, int statusCode) {
                showDialogFailure(uiMsg);
            }
        });
    }

    private void doGetOTP() {
        UIHelper.showProgress(getContext());

        Map<String, Object> params = new HashMap<>();
        params.put("isdn", isdn);
        params.put("locale", Utils.getLocale(getContext()));

        Client.getInstance().request(getContext(), Client.WS_FORGOT_PWD_GET_OTP, params, new SoapResponse(getContext()) {
            @Override
            protected void success(String resValue, String uiMsg) {
                UIHelper.hideProgress();
            }

            @Override
            protected void failed(String reason, String uiMsg, int statusCode) {
                showDialogFailure(uiMsg);
                UIHelper.hideProgress();
            }
        });
    }

    private void showDialogSuccess(String msg) {
        UIHelper.hideProgress();
        new CustomDialog(getContext())
                .hideButtonNegative()
                .hideHeader()
                .setMess(msg)
                .setListener(new CustomDialog.Listener() {
                    @Override
                    public void onOK(CustomDialog dlg) {
                        getActivity().finish();
                        dlg.dismiss();
                    }
                })
                .show();
    }

    private void showDialogFailure(String msg) {
        UIHelper.hideProgress();
        new CustomDialog(getContext())
                .hideButtonNegative()
                .hideHeader()
                .setMess(msg)
                .setBtnPositiveColor(CustomDialog.ColorButton.RED)
                .setListener(new CustomDialog.Listener() {
                    @Override
                    public void onOK(CustomDialog dlg) {
                        dlg.dismiss();
                    }
                })
                .show();
    }
}
