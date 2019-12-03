package com.mq.myvtg.fragment.account;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.mq.myvtg.R;
import com.mq.myvtg.activity.ActivitySignUpConfirmOTP;
import com.mq.myvtg.api.Client;
import com.mq.myvtg.api.soap.SoapResponse;
import com.mq.myvtg.base.BaseFrgmt;
import com.mq.myvtg.dialog.CustomDialog;
import com.mq.myvtg.util.UIHelper;
import com.mq.myvtg.util.Utils;

import java.util.HashMap;
import java.util.Map;

public class FrgmtSignUp extends BaseFrgmt implements View.OnClickListener, TextWatcher {
    private View btnSignIn, btnSignUp, btnShowPassword, btnShowConfirmPassword;
    private EditText edPhoneNumber, edPartnerCode, edPassword, edConfirmPassword;
    private View validatePhoneNumber, validatePartnerCode, validatePassword, validatePasswordConfirm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.frgmt_signup, container, false);
        btnShowPassword = contentView.findViewById(R.id.btnShowPassword);
        btnShowConfirmPassword = contentView.findViewById(R.id.btnShowConfirmPassword);
        edPhoneNumber = contentView.findViewById(R.id.edPhoneNumber);
        edPartnerCode = contentView.findViewById(R.id.edPartnerCode);
        edPassword = contentView.findViewById(R.id.edPassword);
        edConfirmPassword = contentView.findViewById(R.id.edConfirmPassword);
        btnSignUp = contentView.findViewById(R.id.btnSignUp);
        btnSignIn = contentView.findViewById(R.id.btnSignIn);
        btnSignIn = contentView.findViewById(R.id.btnSignIn);
        validatePhoneNumber = contentView.findViewById(R.id.validatePhoneNumber);
        validatePartnerCode = contentView.findViewById(R.id.validatePartnerCode);
        validatePassword = contentView.findViewById(R.id.validatePassword);
        validatePasswordConfirm = contentView.findViewById(R.id.validatePasswordConfirm);

        // register listener
        btnSignUp.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);
        btnShowPassword.setOnClickListener(this);
        btnShowConfirmPassword.setOnClickListener(this);
        edPhoneNumber.addTextChangedListener(this);
        edPartnerCode.addTextChangedListener(this);
        edPassword.addTextChangedListener(this);
        edConfirmPassword.addTextChangedListener(this);
        edConfirmPassword.setOnEditorActionListener((TextView v1, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideSoftKeyboard();
                btnSignUp.performClick();
                return true;
            }
            return false;
        });

        return contentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        hideSoftKeyboardDelay();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSignUp:
                if (validatePhoneNumber()
                        && validatePartnerCode()
                        && validatePassword()
                        && validateConfirmPassword()
                        && isPwdSamePwdConfirm()
                ) {
                    doRequestOTP();
                }
                break;

            case R.id.btnSignIn:
                goSignIn();
                break;

            case R.id.btnShowPassword:
                changeEyeIcon(edPassword, btnShowPassword);
                break;

            case R.id.btnShowConfirmPassword:
                changeEyeIcon(edConfirmPassword, btnShowConfirmPassword);
                break;
        }
    }

    private void doRequestOTP() {
        UIHelper.showProgress(getContext());

        Map<String, Object> params = new HashMap<>();
        params.put("partnerCode", edPartnerCode.getText().toString().trim());
        params.put("isdn", edPhoneNumber.getText().toString().trim());
        params.put("passwordReg", edPassword.getText().toString());
        params.put("locale", Utils.getLocale(getContext()));

        Client.getInstance().request(getContext(), Client.WS_SIGN_UP, params, new SoapResponse(getContext()) {
            @Override
            protected void success(String resValue, String uiMsg) {
                Intent intentConfirmOTPSignUp = new Intent(getContext(), ActivitySignUpConfirmOTP.class);
                intentConfirmOTPSignUp.putExtra("partnerCode", edPartnerCode.getText().toString().trim());
                intentConfirmOTPSignUp.putExtra("isdn", edPhoneNumber.getText().toString().trim());
                intentConfirmOTPSignUp.putExtra("passwordReg", edPassword.getText().toString());

                UIHelper.hideProgress();
                startActivityForResult(intentConfirmOTPSignUp, ActivitySignUpConfirmOTP.ACT_RESULT_ID);
            }

            @Override
            protected void failed(String reason, String uiMsg, int statusCode) {
                UIHelper.hideProgress();
                showDialogFailure(uiMsg);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ActivitySignUpConfirmOTP.ACT_RESULT_ID) {
            backTo(FrgmtLogin.class.getSimpleName());
        }
    }

    private boolean isPwdSamePwdConfirm() {
        if (edConfirmPassword.getText().toString().equals(edPassword.getText().toString())) {
            validatePasswordConfirm.setVisibility(View.GONE);
            return true;
        }

        TextView tvValidate = validatePasswordConfirm.findViewById(R.id.tvValidate);
        tvValidate.setText(R.string.new_passord_not_match);
        validatePasswordConfirm.setVisibility(View.VISIBLE);

        return false;
    }

    private void goSignIn() {
        goBack();
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

    @Override
    public void performChangeLanguage() {
        super.performChangeLanguage();

        TextView tvTitle = contentView.findViewById(R.id.tvTitle);
        tvTitle.setText(R.string.label_signup);
        edPhoneNumber.setHint(R.string.phone_number);
        edPartnerCode.setHint(R.string.partner_code);
        edPassword.setHint(R.string.label_password);
        edConfirmPassword.setHint(R.string.confirm_password);
        TextView tvSignUp = contentView.findViewById(R.id.tvSignUp);
        tvSignUp.setText(R.string.label_signup);
        TextView tvDontHaveAccount = contentView.findViewById(R.id.tvDontHaveAccount);
        tvDontHaveAccount.setText(R.string.already_have_an_account);
        ((TextView) btnSignIn).setText(R.string.sign_in);
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
            case R.id.edPhoneNumber:
                validatePhoneNumber();
                break;
            case R.id.edPartnerCode:
                validatePartnerCode();
                break;
            case R.id.edPassword:
                validatePassword();
                break;
            case R.id.edConfirmPassword:
                validateConfirmPassword();
                break;
        }
    }

    @SuppressLint("StringFormatInvalid")
    private boolean validatePhoneNumber() {
        boolean isEmpty = edPhoneNumber.getText().toString().trim().isEmpty();
        TextView tvValidate = validatePhoneNumber.findViewById(R.id.tvValidate);
        tvValidate.setText(
                String.format(
                        getString(R.string.s_is_required),
                        getString(R.string.phone_number)
                )
        );
        validatePhoneNumber.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        return !isEmpty;
    }

    private boolean validatePartnerCode() {
        boolean isEmpty = edPartnerCode.getText().toString().trim().isEmpty();
        TextView tvValidate = validatePartnerCode.findViewById(R.id.tvValidate);
        tvValidate.setText(
                String.format(
                        getString(R.string.s_is_required),
                        getString(R.string.partner_code)
                )
        );
        validatePartnerCode.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        return !isEmpty;
    }

    private boolean validatePassword() {
        boolean isEmpty = edPassword.getText().toString().trim().isEmpty();
        TextView tvValidate = validatePassword.findViewById(R.id.tvValidate);
        tvValidate.setText(
                String.format(
                        getString(R.string.s_is_required),
                        getString(R.string.label_password)
                )
        );
        validatePassword.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        return !isEmpty;
    }

    private boolean validateConfirmPassword() {
        boolean isEmpty = edConfirmPassword.getText().toString().trim().isEmpty();
        TextView tvValidate = validatePasswordConfirm.findViewById(R.id.tvValidate);
        tvValidate.setText(
                String.format(
                        getString(R.string.s_is_required),
                        getString(R.string.confirm_password)
                )
        );
        validatePasswordConfirm.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        return !isEmpty;
    }

    private void showDialogFailure(String msg) {
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
