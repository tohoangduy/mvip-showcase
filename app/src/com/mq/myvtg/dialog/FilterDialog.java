package com.mq.myvtg.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.mq.myvtg.R;

import java.util.List;

public class FilterDialog extends Dialog implements RadioGroup.OnCheckedChangeListener {

    private List<String> mRanks, mTypes;
    private Context mContext;
    private DialogListener mListener;
    private int mSelectedRank, mSelectedType, rIdTitle = -1;

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (group.getId()) {
            case R.id.radioGroupRank:
                mSelectedRank = checkedId;
                break;
            case R.id.radioGroupType:
                mSelectedType = checkedId;
                break;
        }
    }

    public interface DialogListener {
        void ready(String rankSelected, String typeSelected);

        void cancelled();
    }

    public FilterDialog(Context context,
                        List<String> ranks,
                        int defaultRank,
                        List<String> types,
                        int defaultType,
                        DialogListener listener
    ) {
        super(context);
        mListener = listener;
        mContext = context;
        mRanks = ranks;
        mTypes = types;
        mSelectedRank = defaultRank;
        mSelectedType = defaultType;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get references
        setContentView(R.layout.filter_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        RadioGroup radioGroupRank = findViewById(R.id.radioGroupRank);
        RadioGroup radioGroupType = findViewById(R.id.radioGroupType);

        // set listeners
        radioGroupRank.setOnCheckedChangeListener(this);
        radioGroupType.setOnCheckedChangeListener(this);

        // inflate item to view with data
        for (int i = 0; i < mRanks.size(); ++i) {
            View itemView = LayoutInflater
                    .from(mContext)
                    .inflate(R.layout.radio_button_item, radioGroupRank, false);

            // separator line
            View line = LayoutInflater
                    .from(mContext)
                    .inflate(R.layout.line_horizontal, radioGroupRank, false);

            if (itemView != null) {
                RadioButton radioButton = itemView.findViewById(R.id.btnRadio);
                radioButton.setId(i);
                radioButton.setText(mRanks.get(i));
                if (i == mSelectedRank) radioButton.setChecked(true);

                radioGroupRank.addView(radioButton);
                if (i < mRanks.size() - 1) {
                    radioGroupRank.addView(line);
                }
            }
        }
        for (int i = 0; i < mTypes.size(); ++i) {
            View itemView = LayoutInflater
                    .from(mContext)
                    .inflate(R.layout.radio_button_item, radioGroupType, false);

            // separator line
            View lineType = LayoutInflater
                        .from(mContext)
                        .inflate(R.layout.line_horizontal, radioGroupType, false);

            if (itemView != null) {
                RadioButton radioButton = itemView.findViewById(R.id.btnRadio);
                radioButton.setId(i);
                radioButton.setText(mTypes.get(i));
                if (i == mSelectedType) radioButton.setChecked(true);
                radioGroupType.addView(radioButton);
                if (i < mTypes.size() - 1) {
                    radioGroupType.addView(lineType);
                }
            }
        }

        View buttonOK = findViewById(R.id.dialogOK);
        View buttonCancel = findViewById(R.id.dialogCancel);
        buttonOK.setOnClickListener(v -> {
            mListener.ready(
                    mRanks.get(mSelectedRank),
                    mTypes.get(mSelectedType)
            );
            FilterDialog.this.dismiss();
        });
        buttonCancel.setOnClickListener(v -> {
            mListener.cancelled();
            FilterDialog.this.dismiss();
        });

        if (rIdTitle == -1) {
            rIdTitle = R.string.choose_language;
        }
    }

    @Override
    public void show() {
        super.show();

        TextView title = findViewById(R.id.title);
        TextView tvNagetive = findViewById(R.id.tvNagetive);
        TextView tvPositive = findViewById(R.id.tvPositive);
        TextView tvRankGroup = findViewById(R.id.tvRankGroup);
        TextView tvTypeGroup = findViewById(R.id.tvTypeGroup);

        title.setText(rIdTitle);
        tvNagetive.setText(R.string.cancel);
        tvPositive.setText(R.string.ok);
        tvRankGroup.setText(R.string.rank);
        tvTypeGroup.setText(R.string.label_type);
    }

    public void setTitle(int rId) {
        rIdTitle = rId;
    }
}
