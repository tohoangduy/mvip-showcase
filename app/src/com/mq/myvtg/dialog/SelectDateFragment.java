package com.mq.myvtg.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.widget.DatePicker;

import com.mq.myvtg.util.LogUtil;

import java.util.Calendar;

public class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private SelectDateFragmentEvent callback;
    private long maxDate = -1;
    private String mValueInit;
    private long minDate = -1;

    public SelectDateFragment setOnSelectedDate(SelectDateFragmentEvent cb) {
        callback = cb;
        return this;
    }

    public void show(FragmentManager fragmentManager, String valueInit, String tag) {
        mValueInit = valueInit;
        super.show(fragmentManager, tag);
    }

    public interface SelectDateFragmentEvent {
        void onSelectedDate(String date);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();

        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        if (mValueInit != null && !mValueInit.isEmpty()) {
            String[] dateSplitted = mValueInit.split("/");
            if (dateSplitted.length == 3) {
                try {
                    dd = Integer.parseInt(dateSplitted[0]);
                    mm = Integer.parseInt(dateSplitted[1]);
                    yy = Integer.parseInt(dateSplitted[2]);
                } catch (Exception e) {
                    LogUtil.d(e.toString());
                }
            }
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, yy, mm - 1, dd);
        if (maxDate != -1) datePickerDialog.getDatePicker().setMaxDate(maxDate);
        if (minDate != -1) datePickerDialog.getDatePicker().setMinDate(minDate);

        return datePickerDialog;
    }

    public void onDateSet(DatePicker view, int yy, int mm, int dd) {
        populateSetDate(yy, mm + 1, dd);
    }

    public void populateSetDate(int year, int month, int day) {
        String dob = (day < 10 ? "0" + day : day) + "/" + (month < 10 ? "0" + month : month) + "/" + year;
        callback.onSelectedDate(dob);
    }

    public SelectDateFragment setMaxDate(long maxDate) {
        this.maxDate = maxDate;
        return this;
    }

    public SelectDateFragment setMinDate(long minDate) {
        this.minDate = minDate;
        return this;
    }
}