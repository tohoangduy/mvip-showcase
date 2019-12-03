package com.mq.myvtg.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.mq.myvtg.R;

public class DlgConfirmYesNo extends Dialog {
    private TextView tvTitle;
    private TextView tvBtnYes;
    private TextView tvBtnNo;
    private TextView tvContent;
    private View line2;

    private OnDlgConfirmListener listener;

    public static abstract class OnDlgConfirmListener {
        abstract protected void confirmedYes(DlgConfirmYesNo dlg);
        protected void confirmedNo(DlgConfirmYesNo dlg) {}
    }

    public DlgConfirmYesNo(Context context, OnDlgConfirmListener listener) {
        super(context);
        this.listener = listener;
        init();
    }

    private void init() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dlg_confirm_yes_no);
        setCancelable(false);
        tvTitle = findViewById(R.id.title);
        tvBtnYes = findViewById(R.id.btn_yes);
        tvBtnNo = findViewById(R.id.btn_no);
        tvContent = findViewById(R.id.content);
        line2 = findViewById(R.id.line2);

        tvBtnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (listener != null) {
                    listener.confirmedYes(DlgConfirmYesNo.this);
                }
            }
        });

        tvBtnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (listener != null) {
                    listener.confirmedNo(DlgConfirmYesNo.this);
                }
            }
        });
    }

    public DlgConfirmYesNo setTitle(String title) {
        if (title != null && title.length() > 0) {
            tvTitle.setText(title);
            tvTitle.setVisibility(View.VISIBLE);
        } else {
            tvTitle.setVisibility(View.GONE);
        }
        return this;
    }

    public DlgConfirmYesNo setContentMessage(String content) {
        tvContent.setText(content);
        return this;
    }

    public DlgConfirmYesNo setButtonYes(String label) {
        tvBtnYes.setText(label);
        return this;
    }

    public DlgConfirmYesNo setButtonNo(String label) {
        tvBtnNo.setText(label);
        return this;
    }

    public DlgConfirmYesNo hideButtonNo() {
        tvBtnNo.setVisibility(View.GONE);
        line2.setVisibility(View.GONE);
        tvBtnYes.setBackgroundResource(R.drawable.btn_custom_dlg_bottom_state);
        return this;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dismiss();
        if (listener != null) {
            listener.confirmedNo(DlgConfirmYesNo.this);
        }
    }

    public void setTitleGravity(int gravity) {
        tvTitle.setGravity(gravity);
    }
}
