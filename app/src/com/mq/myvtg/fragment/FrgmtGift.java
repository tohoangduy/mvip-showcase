package com.mq.myvtg.fragment;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mq.myvtg.R;
import com.mq.myvtg.api.Client;
import com.mq.myvtg.api.soap.SoapResponse;
import com.mq.myvtg.base.BaseFrgmt;
import com.mq.myvtg.dialog.CustomDialog;
import com.mq.myvtg.fragment.tabutilities.FrgmtQRScan;
import com.mq.myvtg.model.dto.Gift;
import com.mq.myvtg.util.LogUtil;
import com.mq.myvtg.util.UIHelper;
import com.mq.myvtg.util.Utils;
import com.mq.myvtg.util.XMLPullParserHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrgmtGift extends BaseFrgmt implements View.OnClickListener {

    private Gift mGift = new Gift();
    FrgmtQRScan frgmtQRScan;

    public static Fragment getInstance() {
        return new FrgmtGift();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.frgmt_gift, container, false);

//        View actView = getActivity().findViewById(R.id.scrollContainer);
//        if (actView != null) {
//            actView.setBackgroundColor(getResources().getColor(R.color.white));
//        }
        TextView tvTitle = contentView.findViewById(R.id.tvTitle);
        tvTitle.setText(R.string.gift);

        contentView.findViewById(R.id.btnVerifyCode).setOnClickListener(this);
        contentView.findViewById(R.id.btnScanCode).setOnClickListener(this);
        contentView.findViewById(R.id.btnBack).setOnClickListener(this);

        return contentView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnVerifyCode:
                goNext(FrgmtInputGiftCode.newInstance());
                break;
            case R.id.btnScanCode:
                frgmtQRScan = FrgmtQRScan.newInstance().setQRListener(new FrgmtQRScan.ScanQRListener() {
                    @Override
                    public void onScanDone(String content) {
                        LogUtil.d(content);
                        if (content.contains("\n")) {
                            LogUtil.e("QR code incorrect");
                            frgmtQRScan.showDialogScanFailure(getString(R.string.cannot_detect_qrcode));
                        } else {
                            mGift.giftCode = content.trim();
                            doVerifyGiftCode();
                        }
                    }

                    @Override
                    public String getTitle() {
                        return null;
                    }

                    @Override
                    public String getMessage() {
                        return null;
                    }
                });
                String[] perms = new String[]{Manifest.permission.CAMERA};
                requestPermissionIfNeeded(perms,
                        () -> new Handler().postDelayed(
                                () -> goNext(frgmtQRScan)
                                , 200)
                        , null);
                break;
            case R.id.btnBack:
                goBack();
                break;
        }
    }

    private void doVerifyGiftCode() {
        UIHelper.showProgress(getContext());

        Map<String, Object> params = new HashMap<>();
        params.put("giftCode", mGift.giftCode);
        params.put("locale", Utils.getLocale(getContext()));

        Client.getInstance().request(getContext(), Client.WS_VERIFY_GIFT_CODE, params, new SoapResponse(getContext()) {
            @Override
            protected void success(String resValue, String uiMsg) {
                XMLPullParserHandler<Gift> parser = new XMLPullParserHandler<>();
                List<Gift> gifts = parser.parse(resValue, Gift.class);
                gifts.get(0).giftCode = mGift.giftCode;
                if (gifts != null && gifts.size() > 0) {
                    mGift = gifts.get(0);
                    goNext(FrgmtExchangeGift.newInstance(mGift));
                }
                UIHelper.hideProgress();
            }

            @Override
            protected void failed(String reason, String uiMsg, int statusCode) {
                UIHelper.hideProgress();
                showDialogFailure(uiMsg);
            }
        });
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
