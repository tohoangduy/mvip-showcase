package com.mq.myvtg.fragment.account;

import android.annotation.SuppressLint;
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
import com.mq.myvtg.api.Client;
import com.mq.myvtg.api.soap.SoapResponse;
import com.mq.myvtg.base.BaseFrgmt;
import com.mq.myvtg.dialog.CustomDialog;
import com.mq.myvtg.util.UIHelper;
import com.mq.myvtg.util.Utils;

import java.util.HashMap;
import java.util.Map;

public class FrgmtChangePwd extends BaseFrgmt implements View.OnClickListener, TextWatcher {
    private View btnShowOldPassword, btnShowNewPassword, btnShowConfirmPassword, btnChangePwd, btnBack;
    private EditText edPhoneNumber, edOldPassword, edNewPassword, edNewPasswordConfirm;
    private View validatePhoneNumber, validateOldPassword, validateNewPassword, validateNewPasswordConfirm;

    public static FrgmtChangePwd newInstance() {
        return new FrgmtChangePwd();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.frgmt_change_pwd, container, false);

        TextView tvTitle = contentView.findViewById(R.id.tvTitle);
        tvTitle.setText(R.string.label_change_pwd);

        edPhoneNumber = contentView.findViewById(R.id.edPhoneNumber);
        edOldPassword = contentView.findViewById(R.id.edOldPassword);
        btnShowOldPassword = contentView.findViewById(R.id.btnShowOldPassword);
        edNewPassword = contentView.findViewById(R.id.edNewPassword);
        btnShowNewPassword = contentView.findViewById(R.id.btnShowNewPassword);
        edNewPasswordConfirm = contentView.findViewById(R.id.edNewPasswordConfirm);
        btnShowConfirmPassword = contentView.findViewById(R.id.btnShowConfirmPassword);
        btnChangePwd = contentView.findViewById(R.id.btnChangePwd);
        btnBack = contentView.findViewById(R.id.btnBack);
        validatePhoneNumber = contentView.findViewById(R.id.validatePhoneNumber);
        validateOldPassword = contentView.findViewById(R.id.validateOldPassword);
        validateNewPassword = contentView.findViewById(R.id.validateNewPassword);
        validateNewPasswordConfirm = contentView.findViewById(R.id.validateNewPasswordConfirm);

        // register listener
        btnShowOldPassword.setOnClickListener(this);
        btnShowNewPassword.setOnClickListener(this);
        btnShowConfirmPassword.setOnClickListener(this);
        btnChangePwd.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        edPhoneNumber.addTextChangedListener(this);
        edOldPassword.addTextChangedListener(this);
        edNewPassword.addTextChangedListener(this);
        edNewPasswordConfirm.addTextChangedListener(this);
        edNewPasswordConfirm.setOnEditorActionListener((TextView v1, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideSoftKeyboard();
                btnChangePwd.performClick();
                return true;
            }
            return false;
        });

        // init value on view
        edPhoneNumber.setText(Client.getInstance().userLogin.username);

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
            case R.id.btnChangePwd:
                if (validatePhoneNumber()
                        && validateOldPassword()
                        && validateNewPassword()
                        && validateConfirmNewPassword()
                        && isPwdDifferentNewPwd()
                        && isNewPwdSameConfirmNewPwd()
                ) {
                    doChangePwd();
                }
                break;

            case R.id.btnShowOldPassword:
                changeEyeIcon(edOldPassword, btnShowOldPassword);
                break;

            case R.id.btnShowNewPassword:
                changeEyeIcon(edNewPassword, btnShowNewPassword);
                break;

            case R.id.btnShowConfirmPassword:
                changeEyeIcon(edNewPasswordConfirm, btnShowConfirmPassword);
                break;

            case R.id.btnBack:
                goBack();
                break;
        }
    }

    private void doChangePwd() {
        Map<String, Object> params = new HashMap<>();
        params.put("isdn", edPhoneNumber.getText().toString().trim());
        params.put("locale", Utils.getLocale(getContext()));
        params.put("oldPassword", edOldPassword.getText().toString());
        params.put("newPassword", edNewPassword.getText().toString());

        Client.getInstance().request(getContext(), Client.WS_CHANGE_PWD, params, new SoapResponse(getContext()) {
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

    private void changeEyeIcon(EditText editText, View view) {
        if (editText.getInputType() == InputType.TYPE_CLASS_TEXT) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            view.setBackground(getResources().getDrawable(R.drawable.ic_eye_lock));
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            view.setBackground(getResources().getDrawable(R.drawable.ic_eye));
        }
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
                        resetForm();
                        dlg.dismiss();
                    }
                })
                .show();
    }

    private void resetForm() {
        edPhoneNumber.setText("");
        edOldPassword.setText("");
        edNewPassword.setText("");
        edNewPasswordConfirm.setText("");
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
            case R.id.edPhoneNumber:
                validatePhoneNumber();
                break;
            case R.id.edOldPassword:
                validateOldPassword();
                break;
            case R.id.edPassword:
                validateNewPassword();
                break;
            case R.id.edConfirmPassword:
                validateConfirmNewPassword();
                break;
        }
    }

    private boolean validatePhoneNumber() {
        boolean isEmpty = edPhoneNumber.getText().toString().trim().isEmpty();
        validatePhoneNumber.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        return !isEmpty;
    }

    private boolean validateOldPassword() {
        boolean isEmpty = edOldPassword.getText().toString().trim().isEmpty();
        validateOldPassword.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        return !isEmpty;
    }

    @SuppressLint("StringFormatInvalid")
    private boolean validateNewPassword() {
        boolean isEmpty = edNewPassword.getText().toString().trim().isEmpty();
        if (isEmpty) {
            TextView tvValidate = validateNewPassword.findViewById(R.id.tvValidate);
            tvValidate.setText(
                    String.format(
                            getString(R.string.s_is_required),
                            getString(R.string.label_new_pwd)
                    )
            );
        }
        validateNewPassword.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        return !isEmpty;
    }

    private boolean validateConfirmNewPassword() {
        boolean isEmpty = edNewPasswordConfirm.getText().toString().trim().isEmpty();
        if (isEmpty) {
            TextView tvValidate = validateNewPasswordConfirm.findViewById(R.id.tvValidate);
            tvValidate.setText(
                    String.format(
                            getString(R.string.s_is_required),
                            getString(R.string.label_new_pwd)
                    )
            );
        }
        validateNewPasswordConfirm.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        return !isEmpty;
    }

    private boolean isNewPwdSameConfirmNewPwd() {
        if (edNewPasswordConfirm.getText().toString().equals(edNewPassword.getText().toString())) {
            validateNewPasswordConfirm.setVisibility(View.GONE);
            return true;
        }
        TextView tvValidate = validateNewPasswordConfirm.findViewById(R.id.tvValidate);
        tvValidate.setText(R.string.password_and_confirm_password_not_same);
        validateNewPasswordConfirm.setVisibility(View.VISIBLE);
        return false;
    }

    private boolean isPwdDifferentNewPwd() {
        if (edOldPassword.getText().toString().equals(edNewPassword.getText().toString())) {
            TextView tvValidate = validateNewPassword.findViewById(R.id.tvValidate);
            tvValidate.setText(R.string.msg_change_pwd_same_pass);
            validateNewPassword.setVisibility(View.VISIBLE);
            return false;
        }

        validateNewPassword.setVisibility(View.GONE);
        return true;
    }
}
