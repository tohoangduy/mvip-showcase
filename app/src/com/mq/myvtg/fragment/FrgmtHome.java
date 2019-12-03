package com.mq.myvtg.fragment;


import android.Manifest;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mq.myvtg.R;
import com.mq.myvtg.adapter.SlideShowAdapter;
import com.mq.myvtg.api.Client;
import com.mq.myvtg.api.soap.SoapResponse;
import com.mq.myvtg.base.BaseFrgmt;
import com.mq.myvtg.base.BaseFrgmtActivity;
import com.mq.myvtg.base.MyVTGApp;
import com.mq.myvtg.base.SmoothLayoutManager;
import com.mq.myvtg.dialog.CustomDialog;
import com.mq.myvtg.dialog.SpinnerDialog;
import com.mq.myvtg.fragment.tabutilities.FrgmtQRScan;
import com.mq.myvtg.model.dto.CustomerVIP;
import com.mq.myvtg.util.Const;
import com.mq.myvtg.util.LogUtil;
import com.mq.myvtg.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FrgmtHome extends BaseFrgmt implements View.OnClickListener {

    private RecyclerView rcSlideShow;
    private LinearLayout layoutDots;
    private SpinnerDialog dialogLanguage;
    private boolean stopAutoScroll = false;
    private int totalItems = 0;
    private List<Uri> datasetSlideShow = new ArrayList<>();
    private SlideShowAdapter slideShowAdapter;
    private FrgmtQRScan frgmtQRScan;

    public static FrgmtHome newInstance() {
        return new FrgmtHome();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        fetchSlideShow();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.fragment_home, container, false);

        //get references
        View btnQrCode = contentView.findViewById(R.id.btnQrCode);
        View btnTranscantion = contentView.findViewById(R.id.btnTranscantion);
        View btnSetting = contentView.findViewById(R.id.btnSetting);
        View btnGift = contentView.findViewById(R.id.btnGift);
        View btnSelectLanguage = contentView.findViewById(R.id.btnSelectLanguage);
        TextView labelLanguage = contentView.findViewById(R.id.labelLanguage);
        TextView tvIsdn = contentView.findViewById(R.id.tvIsdn);
        tvIsdn.setText(Client.getInstance().userLogin.username);
        layoutDots = contentView.findViewById(R.id.layoutDots);

        rcSlideShow = contentView.findViewById(R.id.rcSlideShow);
        rcSlideShow.setHasFixedSize(true);

        // use slide in asset folder
        datasetSlideShow.add(Uri.fromFile(new File("//android_asset/slide1.png")));
        datasetSlideShow.add(Uri.fromFile(new File("//android_asset/slide2.png")));
        datasetSlideShow.add(Uri.fromFile(new File("//android_asset/slide3.png")));
        totalItems = datasetSlideShow.size();

        rcSlideShow.setLayoutManager(
                new SmoothLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
                        .setSpeedOfSmooth(SmoothLayoutManager.X_200)
        );
        slideShowAdapter = new SlideShowAdapter(getContext(), datasetSlideShow);
        rcSlideShow.setAdapter(slideShowAdapter);

        // register listener
        btnQrCode.setOnClickListener(this);
        btnTranscantion.setOnClickListener(this);
        btnSetting.setOnClickListener(this);
        btnGift.setOnClickListener(this);
        btnSelectLanguage.setOnClickListener(this);

        List<String> languages = ((BaseFrgmtActivity) Objects.requireNonNull(getActivity())).languages;
        int selectedLang; // default is khmer language
        String savedLanguage = Utils.getLanguage(getContext());
        selectedLang = languages.indexOf(savedLanguage);
        if (selectedLang < 0) {
            selectedLang = languages.indexOf(Const.LANGUAGES.KHMER);
        }
        labelLanguage.setText(languages.get(selectedLang));
        dialogLanguage = new SpinnerDialog(getContext(), languages, selectedLang, new SpinnerDialog.DialogListener() {

            @Override
            public void ready(int n) {
                Log.d(TAG, "item selected is " + n);
                labelLanguage.setText(languages.get(n));
                setLang(languages.get(n));
            }

            @Override
            public void cancelled() {
                Log.d(TAG, "popup cancelled");
            }
        });

        autoScroll();

        return contentView;
    }

    private void addBottomDots(int currentPage) {
        TextView[] dots = new TextView[slideShowAdapter.getItemCount()];

        layoutDots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(getContext());
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.dot_inactive));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(15,0,15,0);
            dots[i].setLayoutParams(params);
            layoutDots.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(getResources().getColor(R.color.dot_active));
    }

    private void fetchSlideShow() {
        Map<String, Object> params = new HashMap<>();
        params.put("locale", Utils.getLocale(Objects.requireNonNull(getContext())));

        Client.getInstance().request(getContext(), Client.WS_GET_SLIDE_SHOW, params, new SoapResponse(getContext()) {
            @Override
            protected void success(String resValue, String uiMsg) {
                String[] slideBase64List = parseSlides(resValue);

                if (slideBase64List.length > 0) {
                    datasetSlideShow.clear();
                    for (String url : slideBase64List) {
                        datasetSlideShow.add(Uri.parse("https://" + url));
                    }
                    slideShowAdapter.notifyDataSetChanged();
                }
            }

            @Override
            protected void failed(String reason, String uiMsg, int statusCode) {
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

    private String[] parseSlides(String input) {
        String valueParsed = input.replaceAll("<lstImage>", "");
        return valueParsed.split("</lstImage>");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnQrCode:
                String[] perms = new String[]{Manifest.permission.CAMERA};
                frgmtQRScan = FrgmtQRScan.newInstance().setQRListener(new FrgmtQRScan.ScanQRListener() {
                    @Override
                    public void onScanDone(String content) {
                        LogUtil.d(content);

                        // parse VIP customer information
                        String[] info = content.split("\n");
                        CustomerVIP customerVIP = new CustomerVIP();
                        try {
                            customerVIP.cusName = info[0].substring(info[0].indexOf(":") + 1).trim();
                            customerVIP.rank = info[1].substring(info[1].indexOf(":") + 1).trim();
                            customerVIP.contact = info[2].substring(info[2].indexOf(":") + 1).trim();
                            customerVIP.expiredDate = info[3].substring(info[3].indexOf(":") + 1).replace(" ", "");
                            customerVIP.url = info[4].trim();

                            // show customer information scanned
                            goNext(FrgmtInformation.newInstance(customerVIP));
                        } catch (ArrayIndexOutOfBoundsException e) {
                            LogUtil.e("QR code incorrect");
                            frgmtQRScan.showDialogScanFailure(getString(R.string.cannot_detect_qrcode));
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
                requestPermissionIfNeeded(
                        perms,
                        () -> new Handler().postDelayed(() -> goNext(frgmtQRScan), 200)
                        , null
                );
                break;
            case R.id.btnTranscantion:
                goNext(FrgmtSearchInformation.newInstance());
                break;
            case R.id.btnSetting:
                goNext(FrgmtSetting.newInstance());
                break;
            case R.id.btnSelectLanguage:
                if (dialogLanguage != null) {
                    dialogLanguage.show();
                }
                break;
            case R.id.btnGift:
                goNext(FrgmtGift.getInstance());
                break;
        }
    }

    @Override
    public void performChangeLanguage() {
        super.performChangeLanguage();
        List<Integer> textViewIds = Arrays.asList(
                R.id.tvWelcome,
                R.id.tvQrCode,
                R.id.tvTranscantion,
                R.id.tvSetting
        );
        List<Integer> stringIds = Arrays.asList(
                R.string.welcome,
                R.string.qr_code,
                R.string.transaction_infor,
                R.string.setting,
                R.string.label_password
        );

        for (int i = 0; i < textViewIds.size(); ++i) {
            TextView textView = contentView.findViewById(textViewIds.get(i));
            textView.setText(stringIds.get(i));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        stopAutoScroll = false;
        autoScroll();
    }

    @Override
    public void onPause() {
        stopAutoScroll = true;
        super.onPause();
    }

    private void setLang(String lang) {
        ((MyVTGApp) Objects.requireNonNull(getActivity()).getApplication()).changeLang(lang);
    }

    private void autoScroll() {
        addBottomDots(0);
        final int delayTime = 5000;
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            int count = 0;

            @Override
            public void run() {
                if (count == totalItems)
                    count = 0;
                if (count < totalItems) {
                    addBottomDots(count);
                    rcSlideShow.smoothScrollToPosition(count++);
                    if (!stopAutoScroll) {
                        handler.postDelayed(this, delayTime);
                    }
                }
            }
        };

        handler.postDelayed(runnable, delayTime);
    }
}
