package com.mq.myvtg.fragment.account;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.mq.myvtg.R;
import com.mq.myvtg.api.Client;
import com.mq.myvtg.api.soap.SoapResponse;
import com.mq.myvtg.base.BaseFrgmt;
import com.mq.myvtg.dialog.CustomDialog;
import com.mq.myvtg.util.Const;
import com.mq.myvtg.util.UIHelper;
import com.mq.myvtg.util.Utils;

import java.util.HashMap;
import java.util.Map;

public class FrgmtForgotPwdInputPhoneNumber extends BaseFrgmt implements View.OnClickListener, TextWatcher {

    View btnContinue, btnSignUp, validatePhoneNumber;
    EditText edPhoneNumber;

    public static FrgmtForgotPwdInputPhoneNumber newInstance() {
        return new FrgmtForgotPwdInputPhoneNumber();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint({"SetTextI18n", "StringFormatInvalid"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.frgmt_forgotpwd_input_phone_number, container, false);

        TextView actTitle = getActivity().findViewById(R.id.actTitle);
        actTitle.setText(R.string.forgot_password);

        // get references
        edPhoneNumber = contentView.findViewById(R.id.edPhoneNumber);
        btnContinue = contentView.findViewById(R.id.btnContinue);
        btnSignUp = contentView.findViewById(R.id.btnSignUp);
        validatePhoneNumber = contentView.findViewById(R.id.validatePhoneNumber);

        // register listener
        btnContinue.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
        edPhoneNumber.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                btnContinue.performClick();
                return true;
            }
            return false;
        });
        edPhoneNumber.addTextChangedListener(this);

        // init values
        TextView tvValidate = validatePhoneNumber.findViewById(R.id.tvValidate);
        tvValidate.setText(String.format(
                getString(R.string.s_is_required),
                getString(R.string.phone_number)
        ));

        return contentView;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnContinue:
                if (validate()) {
                    doGetOTP();
                } else {
                    validatePhoneNumber.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.btnSignUp:
                gotoSignUp();
                break;
        }
    }

    private void gotoSignUp() {
        getActivity().setResult(Const.GO_TO_SIGN_UP);
        getActivity().finish();
    }

    private boolean validate() {
        return !edPhoneNumber.getText().toString().trim().isEmpty();
    }

    private void doGetOTP() {
        String isdn = edPhoneNumber.getText().toString().trim();
        Map<String, Object> params = new HashMap<>();
        params.put("isdn", isdn);
        params.put("locale", Utils.getLocale(getContext()));

        Client.getInstance().request(getContext(), Client.WS_FORGOT_PWD_GET_OTP, params, new SoapResponse(getContext()) {
            @Override
            protected void success(String resValue, String uiMsg) {
                goNext(new FrgmtConfirmOTPForgotPwd(isdn));
            }

            @Override
            protected void failed(String reason, String uiMsg, int statusCode) {
                showDialogFailure(uiMsg);
            }
        });
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
    protected boolean onBackPressed() {
        getActivity().finish();
        return true;
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

        if (viewFocused.getId() == R.id.edPhoneNumber) {
            validatePhoneNumber.setVisibility(validate() ? View.GONE : View.VISIBLE);
        }
    }
}
