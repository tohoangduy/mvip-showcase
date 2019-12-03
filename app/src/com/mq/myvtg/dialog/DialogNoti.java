package com.mq.myvtg.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.mq.myvtg.R;

public class DialogNoti extends Dialog {

    private OnOkClickedListener listener;
    private String strContent;

    public DialogNoti(@NonNull Context context, OnOkClickedListener listener, String strContent) {
        super(context);
        this.listener = listener;
        this.strContent = strContent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dlg_noti);
        TextView tvOk = findViewById(R.id.btn_ok);
        TextView tvContent = findViewById(R.id.tv_content);
        if (strContent != null) {
            tvContent.setText(strContent);
        }
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                listener.onOkClicked();
            }
        });
    }

    public interface OnOkClickedListener {
        void onOkClicked();
    }
}
