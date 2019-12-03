package com.mq.myvtg.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.HashMap;
import java.util.Map;

public class FrgmtExchangeGift extends BaseFrgmt implements View.OnClickListener {

    private Gift mGift;

    public FrgmtExchangeGift(Gift gift) {
        super();
        mGift = gift;
    }

    public static FrgmtExchangeGift newInstance(Gift gift) {
        return new FrgmtExchangeGift(gift);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.frgmt_show_info_gift_verifyed, container, false);

        // get references
        TextView tvTitle = contentView.findViewById(R.id.tvTitle);
        tvTitle.setText(R.string.subscriber_infor_title);
        View btnExchangeGift = contentView.findViewById(R.id.btnExchangeGift);

        // set listeners
        contentView.findViewById(R.id.btnBack).setOnClickListener(this);
        btnExchangeGift.setOnClickListener(this);

        // show value to views
        if (contentView != null && mGift != null) {
            getTextView(R.id.tvIsdnValue).setText(mGift.isdn);
            getTextView(R.id.tvGiftNameValue).setText(mGift.giftName);
            getTextView(R.id.tvDateValue).setText(mGift.dateTime);
            getTextView(R.id.tvPartnerNameValue).setText(mGift.partnerName);
            getTextView(R.id.tvDiscountRateValue).setText(mGift.discountRate);
        }

        return contentView;
    }

    private void doExchangeGift() {
        UIHelper.showProgress(getContext());

        Map<String, Object> params = new HashMap<>();
        params.put("isdn", mGift.isdn);
        params.put("isdnPartner", Client.getInstance().userLogin.username);
        params.put("giftCode", mGift.giftCode);
        params.put("locale", Utils.getLocale(getContext()));

        Client.getInstance().request(getContext(), Client.WS_EXCHANGE_GIFT, params, new SoapResponse(getContext()) {
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

    private TextView getTextView(int viewId) {
        View view = contentView.findViewById(viewId);
        return view instanceof TextView ? (TextView) view : null;
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
                        goBack();
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                goBack();
                break;
            case R.id.btnExchangeGift:
                doExchangeGift();
                break;
        }
    }
}
