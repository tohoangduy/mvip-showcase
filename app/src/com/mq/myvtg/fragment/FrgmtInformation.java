package com.mq.myvtg.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mq.myvtg.R;
import com.mq.myvtg.api.Client;
import com.mq.myvtg.api.soap.SoapResponse;
import com.mq.myvtg.base.BaseFrgmt;
import com.mq.myvtg.dialog.CustomDialog;
import com.mq.myvtg.model.dto.CustomerVIP;
import com.mq.myvtg.util.UIHelper;
import com.mq.myvtg.util.Utils;

import java.util.HashMap;
import java.util.Map;

public class FrgmtInformation extends BaseFrgmt implements View.OnClickListener {

    private CustomerVIP mCustomerVIP;

    public FrgmtInformation(CustomerVIP customerVIP) {
        super();
        mCustomerVIP = customerVIP;
    }

    public static FrgmtInformation newInstance(CustomerVIP customerVIP) {
        return new FrgmtInformation(customerVIP);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.frgmt_information, container, false);

        TextView tvTitle = contentView.findViewById(R.id.tvTitle);
        tvTitle.setText(R.string.subscriber_infor_title);

        // get references
        View btnBack = contentView.findViewById(R.id.btnBack);
        View btnSave = contentView.findViewById(R.id.btnSave);
        TextView tvFullname = contentView.findViewById(R.id.tvFullname);
        TextView tvRankType = contentView.findViewById(R.id.tvRankType);
        TextView tvPhoneNumber = contentView.findViewById(R.id.tvPhoneNumber);
        TextView tvWebsite = contentView.findViewById(R.id.tvWebsite);

        // set listeners
        btnBack.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        // init show values on views
        tvFullname.setText(mCustomerVIP.cusName);
        tvRankType.setText(mCustomerVIP.rank);
        tvPhoneNumber.setText(mCustomerVIP.contact);
        tvWebsite.setText(mCustomerVIP.url);

        return contentView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                goBack();
                break;

            case R.id.btnSave:
                showDialogConfirm();
                break;
        }
    }

    private void showDialogConfirm() {
        new CustomDialog(getContext())
                .hideHeader()
                .setMess(getContext().getString(R.string.are_you_sure))
                .setButtonNegative("Cancel", null)
                .setButtonPositive("Save", null)
                .setListener(new CustomDialog.Listener() {
                    @Override
                    public void onOK(CustomDialog dlg) {
                        UIHelper.showProgress(getContext());
                        // call service doSaveScanVip to save customer VIP information
                        doSaveCustomerVIPInfo();
                        dlg.dismiss();
                    }

                    @Override
                    public void onClose(CustomDialog dlg) {
                        super.onClose(dlg);
                        if (dlg != null)
                            dlg.dismiss();
                    }
                })
                .show();
    }

    private void doSaveCustomerVIPInfo() {
        Map<String, Object> params = new HashMap<>();
        params.put("rank", mCustomerVIP.rank);
        params.put("expiredDate", mCustomerVIP.expiredDate);
        params.put("cusName", mCustomerVIP.cusName);
        params.put("contact", mCustomerVIP.contact);
        params.put("url", mCustomerVIP.url);
        params.put("isdn", mCustomerVIP.contact);
        params.put("isdnPartner", Client.getInstance().userLogin.username);
        params.put("locale", Utils.getLocale(getContext()));

        Client.getInstance().request(getContext(), Client.WS_SAVE_SCAN_VIP, params, new SoapResponse(getContext()) {
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

    private void showDialogSuccess(String msg) {
        UIHelper.hideProgress();
        new CustomDialog(getContext())
                .hideButtonNegative()
                .hideHeader()
                .setMess(msg)
                .setListener(new CustomDialog.Listener() {
                    @Override
                    public void onOK(CustomDialog dlg) {
                        goBack(); // back to scan qr code screen
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
