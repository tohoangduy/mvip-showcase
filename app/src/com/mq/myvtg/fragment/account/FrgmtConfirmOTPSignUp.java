package com.mq.myvtg.fragment.account;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

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

public class FrgmtConfirmOTPSignUp extends BaseFrgmt
        implements View.OnClickListener, TextWatcher {

    EditText edOTP;
    View btnResend, btnConfirm, validateOTP;
    Map<String, Object> params = new HashMap<>();

    public static FrgmtConfirmOTPSignUp newInstance() {
        return new FrgmtConfirmOTPSignUp();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get data from Activity Login, fragment FrgmtSignUp
        Bundle bundle = getArguments();
        if (bundle != null) {
            params.put("partnerCode", bundle.getString("partnerCode"));
            params.put("isdn", bundle.getString("isdn"));
            params.put("passwordReg", bundle.getString("passwordReg"));
        }
        params.put("locale", Utils.getLocale(getContext()));
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.frgmt_confirm_otp_sign_up, container, false);

        // get references
        edOTP = contentView.findViewById(R.id.edOTP);
        btnResend = contentView.findViewById(R.id.btnResend);
        btnConfirm = contentView.findViewById(R.id.btnConfirm);
        validateOTP = contentView.findViewById(R.id.validateOTP);
        TextView tvIsdn = contentView.findViewById(R.id.tvIsdn);
        TextView tvValidate = validateOTP.findViewById(R.id.tvValidate);

        // register listener
        btnResend.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        edOTP.setOnEditorActionListener((TextView v1, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                btnConfirm.performClick();
                return true;
            }
            return false;
        });
        edOTP.addTextChangedListener(this);

        // init values
        tvIsdn.setText(params.get("isdn").toString());
        tvValidate.setText(String.format(
                getString(R.string.s_is_required),
                getString(R.string.input_otp)
        ));

        return contentView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnResend:
                requestOTP();
                break;

            case R.id.btnConfirm:
                if (validateOTP()) {
                    doConfirmRegister();
                }
                break;
        }
    }

    private boolean validateOTP() {
        if (edOTP.getText().toString().trim().isEmpty()) {
            validateOTP.setVisibility(View.VISIBLE);
            return false;
        }

        validateOTP.setVisibility(View.GONE);
        return true;
    }

    private void doConfirmRegister() {
        UIHelper.showProgress(getContext());

        Map<String, Object> paramsConfirmResgister = new HashMap<>();
        paramsConfirmResgister.putAll(params);
        paramsConfirmResgister.remove("partnerCode");
        paramsConfirmResgister.remove("passwordReg");
        paramsConfirmResgister.put("otp", edOTP.getText().toString().trim());

        Client.getInstance().request(getContext(), Client.WS_CONFIRM_REGISTER, paramsConfirmResgister, new SoapResponse(getContext()) {
            @Override
            protected void success(String resValue, String uiMsg) {
                UIHelper.hideProgress();
                showDialogSuccess(uiMsg);
            }

            @Override
            protected void failed(String reason, String uiMsg, int statusCode) {
                UIHelper.hideProgress();
                showDialogFailure(uiMsg);
            }
        });
    }

    private void requestOTP() {
        UIHelper.showProgress(getContext());
        Client.getInstance().request(getContext(), Client.WS_SIGN_UP, params, new SoapResponse(getContext()) {
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
        new CustomDialog(getContext())
                .hideButtonNegative()
                .hideHeader()
                .setMess(msg)
                .setListener(new CustomDialog.Listener() {
                    @Override
                    public void onOK(CustomDialog dlg) {
                        dlg.dismiss();
                        getActivity().setResult(ActivitySignUpConfirmOTP.ACT_RESULT_ID);
                        getActivity().finish();
                    }
                })
                .show();
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

        if (viewFocused.getId() == R.id.edOTP) {
            validateOTP();
        }
    }
}
