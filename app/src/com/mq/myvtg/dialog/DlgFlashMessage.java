package com.mq.myvtg.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mq.myvtg.R;
import com.mq.myvtg.util.UIHelper;

public class DlgFlashMessage extends Dialog {
    public enum Type { Success, Error, Warning }

    private Type type = Type.Success;
    private String message;
    private boolean belowHeader = true;
    private Listener listener;

    public DlgFlashMessage(Context context) {
        super(context, R.style.DlgFlash_Animation);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dlg_flash_msg);
        setCancelable(true);
        getWindow().getAttributes().windowAnimations = R.style.DlgFlash_Animation;
    }

    public interface Listener {
        void onDismiss();
    }

    public DlgFlashMessage setListener(Listener listener) {
        this.listener = listener;
        return this;
    }

    public DlgFlashMessage setType(Type type) {
        this.type = type;
        return this;
    }

    public DlgFlashMessage setMessage(String message) {
        this.message = message;
        return this;
    }

    public DlgFlashMessage setBelowHeader(boolean b) {
        this.belowHeader = b;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        Resources res = getContext().getResources();
        int m = res.getDimensionPixelSize(R.dimen.dimen10dp);
        int headerH = res.getDimensionPixelSize(R.dimen.header_height);
        View container = findViewById(R.id.container);
        View layoutMsg = findViewById(R.id.layout_msg);
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        RelativeLayout.LayoutParams lp1 = (RelativeLayout.LayoutParams) layoutMsg.getLayoutParams();
        if (belowHeader) {
            lp1.setMargins(m, headerH + m, m, m);
        } else {
            lp1.setMargins(m, m, m, m);
        }
        ImageView icon = findViewById(R.id.img_icon);
        switch (type) {
            case Success:
                layoutMsg.setBackgroundResource(R.drawable.bg_flash_msg_s);
                icon.setImageResource(R.drawable.ic_dlg_flash_success);
                break;
            case Warning:
                layoutMsg.setBackgroundResource(R.drawable.bg_flash_msg_w);
                icon.setImageResource(R.drawable.ic_dlg_flash_error);
                break;
            case Error:
                layoutMsg.setBackgroundResource(R.drawable.bg_flash_msg_e);
                icon.setImageResource(R.drawable.ic_dlg_flash_error);
                break;
        }
        TextView msg = findViewById(R.id.tv_msg);
        msg.setText(message);
        Window window = getWindow();
        if (window != null) {
            WindowManager wm = window.getWindowManager();
            DisplayMetrics displaymetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(displaymetrics);
            window.setBackgroundDrawableResource(R.drawable.transparent);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.TOP | Gravity.CENTER;
            UIHelper.setOverlayStatusBar(window, false);
        }

        if (listener != null) {
            setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    listener.onDismiss();
                }
            });
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, 3000);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dismiss();
    }
}
