package com.mq.myvtg.fragment;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mq.myvtg.R;
import com.mq.myvtg.api.Client;
import com.mq.myvtg.api.soap.SoapResponse;
import com.mq.myvtg.base.BaseFrgmt;
import com.mq.myvtg.dialog.CustomDialog;
import com.mq.myvtg.model.dto.Gift;
import com.mq.myvtg.util.UIHelper;
import com.mq.myvtg.util.Utils;
import com.mq.myvtg.util.XMLPullParserHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrgmtInputGiftCode extends BaseFrgmt implements View.OnClickListener, TextWatcher {

    EditText edGiftCode;
    View btnVerify, validateGiftCode;

    private Gift mGift;

    public static FrgmtInputGiftCode newInstance() {
        return new FrgmtInputGiftCode();
    }

    @SuppressLint("StringFormatInvalid")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.frgmt_input_gift_code, container, false);

        // get references
        edGiftCode = contentView.findViewById(R.id.edGiftCode);
        btnVerify = contentView.findViewById(R.id.btnVerify);
        validateGiftCode = contentView.findViewById(R.id.validateGiftCode);
        TextView tvTitle = contentView.findViewById(R.id.tvTitle);
        TextView tvValidate = contentView.findViewById(R.id.tvValidate);
        tvTitle.setText(R.string.verify_code);

        // set listeners
        edGiftCode.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                btnVerify.performClick();
                return true;
            }
            return false;
        });
        edGiftCode.addTextChangedListener(this);
        contentView.findViewById(R.id.btnBack).setOnClickListener(this);
        btnVerify.setOnClickListener(this);

        // init values
        tvValidate.setText(String.format(
                getString(R.string.s_is_required),
                getString(R.string.gift_code)
        ));

        return contentView;
    }

    private void doVerifyGiftCode() {
        UIHelper.showProgress(getContext());

        Map<String, Object> params = new HashMap<>();
        params.put("giftCode", edGiftCode.getText().toString().trim());
        params.put("locale", Utils.getLocale(getContext()));

        Client.getInstance().request(getContext(), Client.WS_VERIFY_GIFT_CODE, params, new SoapResponse(getContext()) {
            @Override
            protected void success(String resValue, String uiMsg) {
                XMLPullParserHandler<Gift> parser = new XMLPullParserHandler<>();
                List<Gift> gifts = parser.parse(resValue, Gift.class);
                if (gifts != null && gifts.size() > 0) {
                    mGift = gifts.get(0);
                    mGift.giftCode = edGiftCode.getText().toString().trim();
                }
                UIHelper.hideProgress();
                goNext(FrgmtExchangeGift.newInstance(mGift));
            }

            @Override
            protected void failed(String reason, String uiMsg, int statusCode) {
                UIHelper.hideProgress();
                showDialogFailure(uiMsg);
            }
        });
    }

    private boolean validateGiftCode() {
        boolean isRmpty = edGiftCode.getText().toString().trim().isEmpty();
        validateGiftCode.setVisibility(isRmpty ? View.VISIBLE : View.GONE);
        return !isRmpty;
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                goBack();
                break;
            case R.id.btnVerify:
                if (validateGiftCode()) {
                    doVerifyGiftCode();
                }
                break;
        }
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

        if (viewFocused.getId() == R.id.edGiftCode) {
            validateGiftCode();
        }
    }
}
