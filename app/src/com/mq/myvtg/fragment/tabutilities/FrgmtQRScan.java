package com.mq.myvtg.fragment.tabutilities;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.zxing.Result;
import com.mq.myvtg.R;
import com.mq.myvtg.base.BaseFrgmt;
import com.mq.myvtg.dialog.CustomDialog;

import zxing.library.DecodeCallback;
import zxing.library.ZXingFragment;

public class FrgmtQRScan extends BaseFrgmt {
    private ZXingFragment mXf;
    private FrgmtQRScan.ScanQRListener qrListener;
    private boolean isFlashAvailable = false;

    public static FrgmtQRScan newInstance() {
        return new FrgmtQRScan();
    }

    public interface ScanQRListener {
        void onScanDone(String content);
        String getTitle();
        String getMessage();
    }

    public FrgmtQRScan setQRListener(FrgmtQRScan.ScanQRListener listener) {
        this.qrListener = listener;
        return this;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isFlashAvailable = getActivity().getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!isFlashAvailable) {
            showNoFlashError();
        }
    }

    private void showNoFlashError() {
        Toast.makeText(getActivity(), "Flash not available in this device...", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.frgmt_qr_scan, container, false);

        // get references
        ImageView btnClose = contentView.findViewById(R.id.btnClose);
        CheckBox btnFlash = contentView.findViewById(R.id.btnFlash);

        // set listeners
        btnClose.setOnClickListener(v -> goBack());
        btnFlash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switchFlashLight(isChecked);
            }
        });

        initScanner();
        return contentView;
    }

    public void showDialogScanFailure(String msg) {
        new CustomDialog(getContext())
                .hideButtonNegative()
                .hideHeader()
                .setMess(msg)
                .setBtnPositiveColor(CustomDialog.ColorButton.RED)
                .setListener(new CustomDialog.Listener() {
                    @Override
                    public void onOK(CustomDialog dlg) {
                        restartScanning();
                        dlg.dismiss();
                    }
                })
                .show();
    }

    private void switchFlashLight(boolean status) {
        try {
            if (isFlashAvailable) {
                mXf.getCameraManager().setTorch(status);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected boolean isShowBtnBack() {
        return true;
    }

    @Override
    protected int getIconBtnBack() {
        return R.drawable.bg_btn_state_close;
    }

    @Override
    protected String getTitleString() {
        return qrListener.getTitle();
    }

    private void initScanner() {
        mXf = new ZXingFragment();
        mXf.setDecodeCallback(new DecodeCallback(){

            @Override
            public void handleBarcode(Result result, Bitmap barcode, float scaleFactor) {
                beep();
                String content = result.getText();
//                mXf.stopScan();
//                goBack();
                if (qrListener != null) {
                    qrListener.onScanDone(content);
                }
            }
        });
        getChildFragmentManager().beginTransaction().replace(R.id.scan_container, mXf).commit();
    }

    private void beep() {
        MediaPlayer mp = MediaPlayer.create(getActivity(), R.raw.beep);
        mp.start();
    }

    public void restartScanning() {
        mXf.restartScanning();
    }

    public void stopScan() {
        mXf.stopScan();
        goBack();
    }
}
