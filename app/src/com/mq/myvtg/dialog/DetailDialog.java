package com.mq.myvtg.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mq.myvtg.R;
import com.mq.myvtg.util.LogUtil;

public class DetailDialog extends Dialog {
    private static final String TAG = DetailDialog.class.getSimpleName();

    private String mess, title;
    private String buttonPositiveText = "";
    private View.OnClickListener buttonPositiveOnclick;
    private String buttonNegativeText = "";
    private View.OnClickListener buttonNegativeOnclick;

    public DetailDialog(Context context) {
        super(context);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_detail_application_dialog);
        setCancelable(false);
        if(getWindow()!=null)
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        TextView tvMess = findViewById(R.id.tv_message);
        tvMess.setText(mess);
        if (title != null && title.length() > 0) {
            TextView tvTitle = findViewById(R.id.title);
            tvTitle.setText(title);
        }

        Button btPositive = findViewById(R.id.btn_positive);
        btPositive.setVisibility(View.GONE);
        btPositive.setText(buttonPositiveText);
        btPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonPositiveOnclick != null) {
                    buttonPositiveOnclick.onClick(v);
                } else if (listener != null) {
                    listener.onOK(DetailDialog.this);
                } else {
                    LogUtil.w(TAG, "Positive button is clicked but there is no listener");
                }
            }
        });

        Button btNegative = findViewById(R.id.btn_negative);
        btNegative.setVisibility(View.GONE);
        btNegative.setText(buttonNegativeText);
        btNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonNegativeOnclick != null) {
                    buttonNegativeOnclick.onClick(v);
                    // FIXME: 8/3/2017 thừa code: vừa có listener vừa onClick() ở 2 btn Pos and Nega
                } else if (listener != null) {
                    listener.onCancel(DetailDialog.this);
                } else {
                    LogUtil.w(TAG, "Negative button is clicked but there is no listener");
                }
            }
        });

        ImageButton btnClose = findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if (listener != null) {
                    listener.onClose(DetailDialog.this);
                }
            }
        });
    }

    public DetailDialog setButtonPositive(String text, View.OnClickListener clickListener) {
        buttonPositiveText = text;
        buttonPositiveOnclick = clickListener;
        return this;
    }

    public DetailDialog setMess(String mess) {
        this.mess = mess;
        return this;
    }

    public DetailDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public DetailDialog setButtonNegative(String text, View.OnClickListener clickListener) {
        buttonNegativeText = text;
        buttonNegativeOnclick = clickListener;
        return this;
    }

    public DetailDialog setListener(Listener listener) {
        this.listener = listener;
        return this;
    }

    private Listener listener;

    public static abstract class Listener {
        public abstract void onOK(DetailDialog dlg);

        public void onCancel(DetailDialog dlg) {
            dlg.dismiss();
        }

        public void onClose(DetailDialog dlg) {
        }
    }

    @Override
    public void onBackPressed() {
        dismiss();
        if (listener != null) {
            listener.onClose(DetailDialog.this);
        }
    }
}
