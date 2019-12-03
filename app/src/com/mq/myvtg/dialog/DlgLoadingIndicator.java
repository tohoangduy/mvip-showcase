package com.mq.myvtg.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

import com.mq.myvtg.R;

public class DlgLoadingIndicator extends Dialog {
    private AnimationDrawable animation;

    public DlgLoadingIndicator(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dlg_loading_indicator);
        setCancelable(false);
        getWindow().setBackgroundDrawableResource(R.drawable.transparent);
        ImageView loadingImage = findViewById(R.id.login_loading);
        animation = (AnimationDrawable) loadingImage.getBackground();
    }

    @Override
    public void show() {
        try {
            super.show();
            animation.start();
        }catch (Exception ignored) {

        }
    }

    @Override
    public void dismiss() {
        try {
            super.dismiss();
            if (animation != null) {
                animation.stop();
            }
        }catch (Exception ignored){

        }
    }

    @Override
    public void cancel() {
        super.cancel();
        if (animation != null) {
            animation.stop();
        }
    }
}
