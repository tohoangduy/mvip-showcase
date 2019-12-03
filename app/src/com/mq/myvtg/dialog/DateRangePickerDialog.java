package com.mq.myvtg.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mq.myvtg.R;
import com.mq.myvtg.util.DateUtil;

import java.util.Date;

public class DateRangePickerDialog extends Dialog
        implements View.OnClickListener {

    TextView tvStartDate, tvEndDate;
    private DialogListener mListener;
    private SelectDateFragment calendarStart, calendarEnd;
    private Activity mActivity;

    public interface DialogListener {
        void onCompleted(String dateFrom, String dateTo);

        void onCancelled();
    }

    public DateRangePickerDialog(Activity activity, DialogListener listener) {
        super(activity);
        mActivity = activity;
        mListener = listener;
        calendarStart = new SelectDateFragment()
                .setOnSelectedDate(date -> tvStartDate.setText(date))
                .setMaxDate(new Date().getTime());

        calendarEnd = new SelectDateFragment()
                .setOnSelectedDate(date -> tvEndDate.setText(date))
                .setMaxDate(new Date().getTime());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.date_range_picker_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // get references
        View buttonOK = findViewById(R.id.dialogOK);
        View buttonCancel = findViewById(R.id.dialogCancel);
        View dpStart = findViewById(R.id.dpStart);
        View dpEnd = findViewById(R.id.dpEnd);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);

        // set listeners
        dpStart.setOnClickListener(this);
        dpEnd.setOnClickListener(this);
        buttonOK.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialogOK:
                mListener.onCompleted(tvStartDate.getText().toString(), tvEndDate.getText().toString());
                dismiss();
                break;

            case R.id.dialogCancel:
                mListener.onCancelled();
                dismiss();
                break;

            case R.id.dpStart:
                calendarStart.setMaxDate(
                        DateUtil.string2Date(tvEndDate.getText().toString().trim(), DateUtil.DateFormat.ddMMyyyy).getTime()
                ).show(
                        mActivity.getFragmentManager(),
                        tvStartDate.getText().toString().trim(),
                        "dpStart"
                );
                break;

            case R.id.dpEnd:
                calendarEnd.setMinDate(
                        DateUtil.string2Date(tvStartDate.getText().toString().trim(), DateUtil.DateFormat.ddMMyyyy).getTime()
                ).show(
                        mActivity.getFragmentManager(),
                        tvEndDate.getText().toString().trim(),
                        "dpEnd"
                );
                break;
        }
    }

    public void show(String fromDate, String toDate) {
        super.show();

        // set values init
        tvStartDate.setText(fromDate);
        tvEndDate.setText(toDate);
    }
}
