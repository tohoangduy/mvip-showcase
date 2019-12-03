package com.mq.myvtg.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mq.myvtg.R;

/**
 * Created by Tho.
 */

public class DlgFlashMessageToast {


    private DlgFlashMessage.Type type = DlgFlashMessage.Type.Success;
    private String message;
    private Toast toast;
    private View containerView;
    private Context context;

    public void show() {
        toast.show();
    }

    public void hide() {
        if (toast != null)
            toast.cancel();
    }

    public DlgFlashMessageToast setContext(Context context) {
        this.context = context;
        return this;
    }

    public DlgFlashMessageToast setType(DlgFlashMessage.Type type) {
        this.type = type;
        return this;
    }

    public DlgFlashMessageToast setMessage(String message) {
        this.message = message;
        return this;
    }


    public DlgFlashMessageToast init() {
        containerView = LayoutInflater.from(context).inflate(R.layout.dlg_flash_msg_toast, null, false);
        View container = containerView.findViewById(R.id.container);
        View layoutMsg = containerView.findViewById(R.id.layout_msg);
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast.cancel();
            }
        });


        ImageView icon = containerView.findViewById(R.id.img_icon);
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
        TextView msg = containerView.findViewById(R.id.tv_msg);
        msg.setText(message);
        toast = new Toast(context);
        toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.TOP, 0, context.getResources().getDimensionPixelSize(R.dimen.tab_height));
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(containerView);
        return this;
    }
}
