package com.mq.myvtg.util;

import android.content.Context;

import com.mq.myvtg.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtil {

    public class DateFormat {
        public static final String yyyyMMdd = "yyyy/MM/dd";
        public static final String ddMMyyyy = "dd/MM/yyyy";
        public static final String dt_pattern = "kk:mm:ss dd/MM/yyyy";
    }

    public static String date2String(Date date, String format) {
        // default: does not change time zone
        return date2String(date, format, false);
    }
    public static String date2String(long timestamp, String format) {
        // default: does not change time zone
        return date2String(new Date(timestamp), format, false);
    }

    public static String getTodayString(String pattern) {
        return date2String(new Date(), pattern);
    }

    public static String getTodayString() {
        return date2String(new Date(), DateFormat.ddMMyyyy);
    }

    public static String secondToString(long seconds, String pattern) {
        return milliSecondToString(seconds*100, pattern);
    }

    public static String milliSecondToString(long milliSeconds, String pattern) {
        if (pattern == null) {
            pattern = DateFormat.dt_pattern;
        }
        return android.text.format.DateFormat.format(pattern, milliSeconds).toString();
    }

    public static String ymdToStr(int y, int m, int d) {
        return String.format(Locale.US, "%02d/%02d/%04d", d, m, y);
    }

    public static String mdToStr(int m, int d) {
        return String.format(Locale.US, "%02d/%02d", d, m);
    }


    public static String durationToString(Context context, int seconds) {
        String strHour = context.getString(R.string.str_time_hour);
        String strMinute = context.getString(R.string.str_time_minute);
        String strSecond = context.getString(R.string.str_time_second);
        int h = seconds / 3600;
        int m = seconds % 3600;
        int s = m % 60;
        m = m / 60;
        String str = "";
        if (h > 0) {
            str = h + " " + strHour;
        }
        if (str.length() > 0) {
            str = str + " ";
        }
        if (m > 0 || (m == 0 && h > 0)) {
            str = str + m + " " + strMinute;
        }
        if (str.length() > 0) {
            str = str + " ";
        }
        str = str + s + " " + strSecond;
        return str;
    }

    public static int getDateUnit(Date date, int unit) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(unit);
    }

    public static String date2String(Date date, String format, boolean changeTimeZone){
        String result = "";
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (changeTimeZone) {
            calendar.add(Calendar.HOUR, getTimeZoneOffset());
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        result = sdf.format(calendar.getTime());
        return result;
    }

    public static Date string2Date(String dateString, String format) {
        Date result = null;
        if(dateString == null){
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        try {
            result = sdf.parse(dateString);
        } catch (ParseException e) {
            //result = new Date();
            LogUtil.w("string2Date " + LogUtil.getThrowableString(e));
            return null;
        }

        return result;
    }

    public static int getTimeZoneOffset() {
        int offset = 0;
        offset = Calendar.getInstance().get(Calendar.ZONE_OFFSET)/ 3600000;
        return offset;
    }

    public static Date getCurrentDate(){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.add(Calendar.HOUR,-getTimeZoneOffset());
        return calendar.getTime();
    }

    public static int getPartOfDate(Date date, int part){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        switch (part){
            case Calendar.DATE:
                return cal.get(part);
            case Calendar.MONTH:
                return cal.get(part) + 1;
            case Calendar.YEAR:
                return cal.get(part);
            default:
                return -1;
        }
    }

    public static Date getFirstDateOfThisWeek() {
        Calendar cal = Calendar.getInstance();
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int daysBefore = 0;
        switch (dayOfWeek) {
            case Calendar.MONDAY:
                daysBefore = 0;
                break;
            case Calendar.TUESDAY:
                daysBefore = 1;
                break;
            case Calendar.WEDNESDAY:
                daysBefore = 2;
                break;
            case Calendar.THURSDAY:
                daysBefore = 3;
                break;
            case Calendar.FRIDAY:
                daysBefore = 4;
                break;
            case Calendar.SATURDAY:
                daysBefore = 5;
                break;
            case Calendar.SUNDAY:
                daysBefore = 6;
                break;
        }
        cal.add(Calendar.DAY_OF_WEEK, -daysBefore);
        return cal.getTime();
    }

    public static Date getFirstDateOfThisMonth() {
        Calendar cal = Calendar.getInstance();
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        cal.add(Calendar.DAY_OF_MONTH, -dayOfMonth+1);
        return cal.getTime();
    }

    public static Date getFirstDateOfLastMonth() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    public static Date getLastDateOfLastMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, 1);
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }
}
