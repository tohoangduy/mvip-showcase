package com.mq.myvtg.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.mq.myvtg.R;

import java.util.List;

public class SpinnerDialog extends Dialog implements RadioGroup.OnCheckedChangeListener {

    private List<String> mDataset;
    private Context mContext;
    private DialogListener mListener;
    private int mSelectedItem, rIdTitle;

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        mSelectedItem = checkedId;
    }

    public interface DialogListener {
        void ready(int n);
        void cancelled();
    }

    public SpinnerDialog(Context context, List<String> list, int selectedIndex, DialogListener listener) {
        super(context);
        mListener = listener;
        mContext = context;
        mDataset = list;
        mSelectedItem = selectedIndex;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.spinner_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(this);
        radioGroup.addView(
                LayoutInflater.from(mContext).inflate(R.layout.line_horizontal, radioGroup, false)
        );
        for (int i = 0; i < mDataset.size(); ++i) {
            View itemView = LayoutInflater
                    .from(mContext)
                    .inflate(R.layout.radio_button_item, radioGroup, false);

            // separator line
            View line = LayoutInflater
                    .from(mContext)
                    .inflate(R.layout.line_horizontal, radioGroup, false);

            if (itemView != null) {
                RadioButton radioButton = itemView.findViewById(R.id.btnRadio);
                radioButton.setId(i);
                radioButton.setText(mDataset.get(i));
                if (i == mSelectedItem) radioButton.setChecked(true);
                radioGroup.addView(radioButton);
                radioGroup.addView(line);
            }
        }

        View buttonOK = findViewById(R.id.dialogOK);
        View buttonCancel = findViewById(R.id.dialogCancel);
        buttonOK.setOnClickListener(v -> {
            mListener.ready(mSelectedItem);
            SpinnerDialog.this.dismiss();
        });
        buttonCancel.setOnClickListener(v -> {
            mListener.cancelled();
            SpinnerDialog.this.dismiss();
        });

        rIdTitle = R.string.choose_language;
    }

    @Override
    public void show() {
        super.show();

        TextView title = findViewById(R.id.title);
        TextView tvNagetive = findViewById(R.id.tvNagetive);
        TextView tvPositive = findViewById(R.id.tvPositive);

        title.setText(rIdTitle);
        tvNagetive.setText(R.string.cancel);
        tvPositive.setText(R.string.ok);
    }

    public void setTitle(int rId) {
        rIdTitle = rId;
    }
}
