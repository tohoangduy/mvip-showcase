package com.mq.myvtg.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.mq.myvtg.R;

public class DlgItemPicker extends Dialog {
    private NumberPicker numberPicker;
    private TextView tvTitle;

    public interface Listener {
        void onOk(int num, String value);
    }

    public DlgItemPicker(Context context, Listener listener) {
        super(context);
        init(listener, null);
    }

    public DlgItemPicker(Context context, Listener listener, String[] data) {
        super(context);
        init(listener, data);
    }

    public void setData(String[] data) {
        setupPicker(data);
    }

    public void setTitle(String title) {
        if (title != null && title.length() > 0) {
            tvTitle.setText(title);
            tvTitle.setVisibility(View.VISIBLE);
        } else {
            tvTitle.setVisibility(View.GONE);
        }
    }

    public void setInitialValue(int index) {
        numberPicker.setValue(index);
    }

    private void init(final Listener listener, String[] data) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dlg_item_picker);
        setCancelable(false);
        tvTitle = findViewById(R.id.title);
        numberPicker = findViewById(R.id.number_picker);
        View btnOk = findViewById(R.id.btn_right);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value = numberPicker.getValue();
                String str = numberPicker.getDisplayedValues()[value];
                listener.onOk(value, str);
                dismiss();
            }
        });
        View btnCancel = findViewById(R.id.btn_left);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        if (data != null) {
            setupPicker(data);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dismiss();
    }

    private void setupPicker(String[] data) {
        numberPicker.setDisplayedValues(data);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(data.length - 1);
    }
}
