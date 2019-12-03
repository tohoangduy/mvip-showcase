package com.mq.myvtg.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mq.myvtg.R;
import com.mq.myvtg.util.LogUtil;

public class CustomDialog extends Dialog {
    private static final String TAG = CustomDialog.class.getSimpleName();

    public enum ColorButton {
        GREEN,
        RED
    }

    public static boolean isShowing = false;

    private String mess, title;
    private String buttonPositiveText = "";
    private View.OnClickListener buttonPositiveOnclick;
    private String buttonNegativeText = "";
    private View.OnClickListener buttonNegativeOnclick;
    private View btPositive, btNegative;
    private boolean isHideHeader, isHideBtnX = false;
    private int bgPositiveBtn = R.drawable.version_2_background_button_login;

    public CustomDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        setCancelable(false);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        findViewById(R.id.dialogHeader).setVisibility(isHideHeader ? View.GONE : View.VISIBLE);

        TextView tvMess = findViewById(R.id.tv_message);
        tvMess.setText(mess);
        if (title != null && title.length() > 0) {
            TextView tvTitle = findViewById(R.id.title);
            tvTitle.setText(title);
        }

        btPositive = findViewById(R.id.btn_positive);
        btPositive.setBackground(getContext().getResources().getDrawable(bgPositiveBtn));
        TextView label_btn_positive = btPositive.findViewById(R.id.label_btn_positive);
        if (!buttonPositiveText.isEmpty()) {
            label_btn_positive.setText(buttonPositiveText);
        }
        btPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonPositiveOnclick != null) {
                    buttonPositiveOnclick.onClick(v);
                } else if (listener != null) {
                    listener.onOK(CustomDialog.this);
                } else {
                    LogUtil.w(TAG, "Positive button is clicked but there is no listener");
                }
            }
        });

        btNegative = findViewById(R.id.btn_negative);
        if (isHideButtonNegative) {
            btNegative.setVisibility(View.GONE);
        }
        if (!buttonNegativeText.isEmpty()) {
            TextView label_btn_negative = btNegative.findViewById(R.id.label_btn_negative);
            label_btn_negative.setText(buttonNegativeText);
        }
        btNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonNegativeOnclick != null) {
                    buttonNegativeOnclick.onClick(v);
                    // FIXME: 8/3/2017 thừa code: vừa có listener vừa onClick() ở 2 btn Pos and Nega
                } else if (listener != null) {
                    listener.onCancel(CustomDialog.this);
                } else {
                    LogUtil.w(TAG, "Negative button is clicked but there is no listener");
                }
            }
        });

        ImageButton btnClose = findViewById(R.id.btn_close);
        if (!isHideBtnX) {
            btnClose.setVisibility(View.VISIBLE);
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                    if (listener != null) {
                        listener.onClose(CustomDialog.this);
                    }
                }
            });
        }
    }

    private boolean isHideButtonNegative = false;

    public CustomDialog hideButtonNegative() {
        isHideButtonNegative = true;
        return this;
    }

    public CustomDialog hideHeader() {
        isHideHeader = true;
        return this;
    }

    public CustomDialog hideXButton() {
        isHideBtnX = true;
        return this;
    }

    public CustomDialog setBtnPositiveColor(ColorButton color) {
        switch (color) {
            case GREEN:
                bgPositiveBtn = R.drawable.version_2_background_button_login;
                break;
            case RED:
                bgPositiveBtn = R.drawable.bg_btn_red;
                break;
        }

        return this;
    }

    public CustomDialog setButtonPositive(String text, View.OnClickListener clickListener) {
        buttonPositiveText = text;
        buttonPositiveOnclick = clickListener;
        return this;
    }

    public CustomDialog setMess(String mess) {
        this.mess = mess;
        return this;
    }

    public CustomDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public CustomDialog setButtonNegative(String text, View.OnClickListener clickListener) {
        buttonNegativeText = text;
        buttonNegativeOnclick = clickListener;
        return this;
    }

    public CustomDialog setListener(Listener listener) {
        this.listener = listener;
        return this;
    }

    private Listener listener;

    public static abstract class Listener {
        public abstract void onOK(CustomDialog dlg);

        public void onCancel(CustomDialog dlg) {
            dlg.dismiss();
        }

        public void onClose(CustomDialog dlg) {
        }
    }

    @Override
    public void onBackPressed() {
        dismiss();
        if (listener != null) {
            listener.onClose(CustomDialog.this);
        }
    }
}
