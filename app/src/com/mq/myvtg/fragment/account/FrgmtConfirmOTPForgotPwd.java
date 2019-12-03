package com.mq.myvtg.fragment.account;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.mq.myvtg.R;
import com.mq.myvtg.api.Client;
import com.mq.myvtg.api.soap.SoapResponse;
import com.mq.myvtg.base.BaseFrgmt;
import com.mq.myvtg.dialog.CustomDialog;
import com.mq.myvtg.util.UIHelper;
import com.mq.myvtg.util.Utils;

import java.util.HashMap;
import java.util.Map;

public class FrgmtConfirmOTPForgotPwd extends BaseFrgmt
        implements View.OnClickListener, TextWatcher {

    View btnResend, btnConfirm, btnShowPassword, btnShowConfirmPassword,
            validateOTP, validateNewPassword, validatePasswordConfirm;
    EditText edNewPassword, edConfirmPassword, edOTP;

    String isdn;

    public FrgmtConfirmOTPForgotPwd(String isdn) {
        this.isdn = isdn;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.frgmt_confirm_otp_forgot_pwd, container, false);
        edOTP = contentView.findViewById(R.id.edOTP);
        btnResend = contentView.findViewById(R.id.btnResend);
        edNewPassword = contentView.findViewById(R.id.edNewPassword);
        edConfirmPassword = contentView.findViewById(R.id.edConfirmPassword);
        btnConfirm = contentView.findViewById(R.id.btnConfirm);
        btnShowPassword = contentView.findViewById(R.id.btnShowPassword);
        btnShowConfirmPassword = contentView.findViewById(R.id.btnShowConfirmPassword);
        validateOTP = contentView.findViewById(R.id.validateOTP);
        validateNewPassword = contentView.findViewById(R.id.validateNewPassword);
        validatePasswordConfirm = contentView.findViewById(R.id.validatePasswordConfirm);

        // init values
        TextView tvIsdn = contentView.findViewById(R.id.tvIsdn);
        tvIsdn.setText(isdn);
        TextView actTitle = getActivity().findViewById(R.id.actTitle);
        actTitle.setText(R.string.confirm);
        TextView tvValidateOTP = validateOTP.findViewById(R.id.tvValidate);
        tvValidateOTP.setText(String.format(
                getString(R.string.s_is_required),
                getString(R.string.input_otp)
        ));
        TextView tvValidateNewPwd = validateNewPassword.findViewById(R.id.tvValidate);
        tvValidateNewPwd.setText(String.format(
                getString(R.string.s_is_required),
                getString(R.string.label_new_pwd)
        ));
        TextView tvValidatePwdConfirm = validatePasswordConfirm.findViewById(R.id.tvValidate);
        tvValidatePwdConfirm.setText(String.format(
                getString(R.string.s_is_required),
                getString(R.string.confirm_password)
        ));

        // register listener
        btnResend.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        btnShowPassword.setOnClickListener(this);
        btnShowConfirmPassword.setOnClickListener(this);
        edOTP.addTextChangedListener(this);
        edNewPassword.addTextChangedListener(this);
        edConfirmPassword.addTextChangedListener(this);

        return contentView;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnResend:
                doGetOTP();
                break;

            case R.id.btnConfirm:
                if (validateOTP()
                        && validateNewPassword()
                        && validatePasswordConfirm()
                        && isPwdSamePwdConfirm()
                ) {
                    doChangePassword();
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

    private boolean isPwdSamePwdConfirm() {
        if (edConfirmPassword.getText().toString().equals(edNewPassword.getText().toString())) {
            validatePasswordConfirm.setVisibility(View.GONE);
            return true;
        }

        TextView tvValidate = validatePasswordConfirm.findViewById(R.id.tvValidate);
        tvValidate.setText(R.string.new_passord_not_match);
        validatePasswordConfirm.setVisibility(View.VISIBLE);

        return false;
    }

    private boolean validateOTP() {
        boolean isEmpty = edOTP.getText().toString().trim().isEmpty();
        validateOTP.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        return !isEmpty;
    }

    private boolean validateNewPassword() {
        boolean isEmpty = edNewPassword.getText().toString().trim().isEmpty();
        validateNewPassword.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        return !isEmpty;
    }

    private boolean validatePasswordConfirm() {
        boolean isEmpty = edConfirmPassword.getText().toString().trim().isEmpty();
        validatePasswordConfirm.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        return !isEmpty;
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
                UIHelper.hideProgress();
                showDialogFailure(uiMsg);
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

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        View viewFocused = contentView.findFocus();
        if (viewFocused == null) return;

        switch (viewFocused.getId()) {
            case R.id.edOTP:
                validateOTP();
                break;
            case R.id.edNewPassword:
                validateNewPassword();
                break;
            case R.id.edConfirmPassword:
                validatePasswordConfirm();
                break;
        }
    }
}
