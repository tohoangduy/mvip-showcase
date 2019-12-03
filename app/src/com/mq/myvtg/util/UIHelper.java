package com.mq.myvtg.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mq.myvtg.R;
import com.mq.myvtg.dialog.CustomDialog;
import com.mq.myvtg.dialog.DlgConfirmYesNo;
import com.mq.myvtg.dialog.DlgFlashMessage;
import com.mq.myvtg.dialog.DlgFlashMessageToast;
import com.mq.myvtg.dialog.DlgLoadingIndicator;
import com.mq.myvtg.model.ModelForceUpdateVersion;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UIHelper {
    private static final String TAG = UIHelper.class.getSimpleName();
    public static final float LDPI = 0.75f;
    public static final float MDPI = 1.0f;
    public static final float HDPI = 1.5f;
    public static final float XHDPI = 2.0f;
    public static final float XXHDPI = 3.0f;
    public static final float XXXHDPI = 4.0f;

    private static final String STATIC_MAP_URL = "https://maps.googleapis.com/maps/api/staticmap";
    private static final String STATIC_MAP_KEY = "AIzaSyBZnxHbYbEYSYb9Y0WuDmoyzuNMvnNWUG0";
    private static Context mContext;

    public static void removeContext() {
        mContext = null;
        if (dlgFlashMessageToast != null) dlgFlashMessageToast.hide();
        dlgFlashMessageToast = null;
    }

    public static void logDensity(Context context) {
        mContext = context;
        float density = mContext.getResources().getDisplayMetrics().density;
        String densityStr;
        if (density == LDPI) {
            densityStr = "ldpi";
        } else if (density == MDPI) {
            densityStr = "mdpi";
        } else if (density == HDPI) {
            densityStr = "hdpi";
        } else if (density == XHDPI) {
            densityStr = "xhdpi";
        } else if (density == XXHDPI) {
            densityStr = "xxhdpi";
        } else if (density == XXXHDPI) {
            densityStr = "xxxhdpi";
        } else {
            densityStr = "unknown";
        }
        LogUtil.d("Density " + density + " " + densityStr);
    }

    public static void logScreenSizePx(WindowManager wm) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displaymetrics);
        LogUtil.d("Screen size in pixel: " + displaymetrics.widthPixels + " x " + displaymetrics.heightPixels);

        float scale = displaymetrics.density;
        int wdp = (int) ((displaymetrics.widthPixels / scale) + 0.5f);
        int hdp = (int) ((displaymetrics.heightPixels / scale) + 0.5f);
        LogUtil.d("Screen size in dp: " + wdp + " x " + hdp);
    }

    public static void getScreenSize(WindowManager wm, int[] size) {
        if (wm == null || size == null || size.length < 2) {
            return;
        }
        DisplayMetrics displaymetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displaymetrics);
        size[0] = displaymetrics.widthPixels;
        size[1] = displaymetrics.heightPixels;
    }

    /**
     * Convert from dip to pixel
     */
    public static int convertDiptoPx(Context context, int dip) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((dip * scale) + 0.5f);
    }

    /**
     * Convert from pixel to dip
     */
    public static int convertPxtoDip(Context context, int pixel) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((pixel / scale) + 0.5f);
    }
    public static int dpToPx(float dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    public static void setTextSizeDp(int dpValue, TextView textView) {
        Resources res = textView.getContext().getResources();
        int dp1 = res.getDimensionPixelSize(R.dimen.dimen1dp);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, dp1 * dpValue);
    }

    public static void showToast(Context context, String content) {
        if (context != null && content != null && content.length() > 0) {
            Toast.makeText(context, content, Toast.LENGTH_LONG).show();
        }
    }


    public static void showPopup(Context context, String content) {
        mContext = context;
        DlgConfirmYesNo dlg = new DlgConfirmYesNo(mContext, null);
        dlg.setContentMessage(content);
        dlg.setTitle(null);
        dlg.hideButtonNo();
        dlg.show();
    }

    public static void askYesNo(Context context, String content, final Runnable runIfYes) {
        new CustomDialog(context)
                .hideHeader()
                .setMess(content)
                .setButtonNegative("No", null)
                .setButtonPositive("Yes", null)
                .setListener(new CustomDialog.Listener() {
                    @Override
                    public void onOK(CustomDialog dlg) {
                        if (runIfYes != null) {
                            runIfYes.run();
                        }
                        dlg.dismiss();
                    }

                    @Override
                    public void onClose(CustomDialog dlg) {
                        super.onClose(dlg);
                        if (dlg != null)
                            dlg.dismiss();
                    }
                })
                .show();
    }

    public static void showDialogFailure(Context context, String msg) {
        hideProgress();
        new CustomDialog(context)
                .hideButtonNegative()
                .hideHeader()
                .setMess(msg)
                .setBtnPositiveColor(CustomDialog.ColorButton.RED)
                .setListener(new CustomDialog.Listener() {
                    @Override
                    public void onOK(CustomDialog dlg) {
                        dlg.dismiss();
                    }
                })
                .show();
    }

    public static void notifyUpdateVersion(final Activity activity, ModelForceUpdateVersion forceVersion) {
        if (activity == null || forceVersion == null) {
            return;
        }
        String msg = forceVersion.updateInfo;
        DlgConfirmYesNo dlg = new DlgConfirmYesNo(activity, new DlgConfirmYesNo.OnDlgConfirmListener() {
            @Override
            protected void confirmedYes(DlgConfirmYesNo dlg) {
                Utils.openWeb(activity, activity.getString(R.string.url_google_play_app));
                activity.finish();
            }
        });
        dlg.setContentMessage(msg);
        dlg.setTitle(activity.getString(R.string.title_new_update));
        dlg.setButtonYes(activity.getString(R.string.label_btn_update));
        dlg.setButtonNo(activity.getString(R.string.label_btn_later));
        dlg.setTitle(activity.getString(R.string.title_new_update));
        try {
            dlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (NullPointerException ignored) {}

        if ("true".equalsIgnoreCase(forceVersion.forceUpgrade)) {
            dlg.hideButtonNo();
            dlg.show();
        } else if ("true".equalsIgnoreCase(forceVersion.recommendUpgrade)) {
            dlg.show();
        } else return;
    }

    public static void askYes(Context context, String content, final Runnable runIfYes) {
        mContext = context;
        DlgConfirmYesNo dlg = new DlgConfirmYesNo(mContext, new DlgConfirmYesNo.OnDlgConfirmListener() {
            @Override
            protected void confirmedYes(DlgConfirmYesNo dlg) {
                if (runIfYes != null) {
                    runIfYes.run();
                }
            }
        });
        dlg.setContentMessage(content);
        dlg.setTitle(context.getResources().getString(R.string.title_new_update));
        dlg.hideButtonNo();
        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dlg.show();
    }


    public static boolean isAppRunning(Context context) {
        mContext = context;
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        // get the info from the currently running task
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);

        String packageName = mContext.getPackageName();
        ComponentName componentInfo = taskInfo.get(0).topActivity;
        //LogUtil.d("current task", "package: " + componentInfo.getPackageName() + " class: " + componentInfo.getClassName());

        //if app is running
        //do the implementation for if your app is running
        return componentInfo.getPackageName().equalsIgnoreCase(packageName);
    }

    public static boolean isAppInstalled(Context context, String packageUri) {
        mContext = context;
        PackageManager pm = mContext.getPackageManager();
        try {
            pm.getPackageInfo(packageUri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static boolean isUrl(String str) {
        return str != null && (URLUtil.isHttpUrl(str) || URLUtil.isHttpsUrl(str));
    }

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static void setEnableView(View view, boolean enable) {
        if (!enable) {
            view.setAnimation(AlphaVisibility.invisible());
            view.setEnabled(false);
        } else {
            view.setAnimation(AlphaVisibility.visible());
            view.setEnabled(true);
        }
    }

    public static void call(Context context, String number) {
        //mContext = context;
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + number));
        context.startActivity(callIntent);
    }

    public static void hideSoftKeyboard(Context context, List<EditText> editTexts) {
        mContext = context;
        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager == null) return;
        for (EditText editText : editTexts) {
            inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    public static void hideSoftKeyboard(Context context, EditText editText) {
        mContext = context;
        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null)
            inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public static void showSoftKeyboard(Context context, EditText editText) {
        mContext = context;
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    private static DlgLoadingIndicator mDlg = null;

    public static void showProgress(Context context) {
        try {
            hideProgress();
            mDlg = new DlgLoadingIndicator(context);
            mDlg.show();
        } catch (Throwable ignored) {
        }
    }

    public static void hideProgress() {
        if (mDlg != null) {
            mDlg.dismiss();
            mDlg = null;
        }
    }


    @SuppressLint("SimpleDateFormat")
    public static String getDateText(Context context, String time, int type_in, int type_out) {
        mContext = context;
        SimpleDateFormat format = new SimpleDateFormat(mContext.getString(type_in));
        try {
            Date date = format.parse(time);
            SimpleDateFormat formatter = new SimpleDateFormat(mContext.getString(type_out));
            return formatter.format(date.getTime());
        } catch (Exception e) {
            LogUtil.w(TAG, "getDateText " + LogUtil.getThrowableString(e));
        }
        return "";
    }

    public static String getLocationName(Context context, double lat, double lng) {
        mContext = context;
        Geocoder geocoder = new Geocoder(mContext, Locale.US);
        String name = "";
        try {
            List<Address> listAddresses = geocoder.getFromLocation(lat, lng, 1);
            if ((listAddresses != null) && listAddresses.size() > 0) {
                for (int i = 0; i < listAddresses.size(); i++) {
                    Address address = listAddresses.get(i);
                    String localCity = address.getLocality() != null ? address.getLocality() : "";
                    String country = address.getCountryName() != null ? address.getCountryName() : "";
                    if (localCity.length() > 0) {
                        name = localCity + ", " + country;
                    } else {
                        String subLocalCity = address.getSubLocality() != null ? address.getSubLocality() : "";
                        if (subLocalCity.length() > 0) {
                            name = subLocalCity + ", " + country;
                        } else {
                            name = country;
                        }
                    }
                    if (localCity.length() > 0 && country.length() > 0) {
                        return name;
                    }
                }
            }
        } catch (IOException e) {
            LogUtil.w(TAG, "getLocationName " + LogUtil.getThrowableString(e));
        }
        return name;
    }

    public static void setTextViewUnderline(TextView textView) {
        textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    public static boolean shouldUseJapanese() {
        Locale locale = Resources.getSystem().getConfiguration().locale;
        return (locale != null && (locale.equals(Locale.JAPAN) || locale.equals(Locale.JAPANESE)));
    }

    public static boolean isLatLngValid(double lat, double lng) {
        return lat != Const.INVALID_LAT && lng != Const.INVALID_LNG;
    }

    public static String THOUSAND_DOT = ",";
    public static String DECIMAL_DOT = ".";

    public static String formatCurrency(Context context, double amt) {
        if (context == null) {
            return "";
        }
        DecimalFormat formatter = new DecimalFormat(context.getString(R.string.format_currency_double));
        //String out = formatter.format(amt).replace(THOUSAND_DOT, DECIMAL_DOT);
        String out = formatter.format(amt);
        if (amt < 1000) {
            if (out.contains(",")) out = out.replace(".", "@").replace(",", "#");
            else out = out.replace(".", "#").replace(",", "@");

        }else if (amt >= 1000) {
            if (out.indexOf(".") < 4) out = out.replace(".", "@").replace(",", "#");
            else out = out.replace(".", "#").replace(",", "@");
        }

        out = out.replace("@", THOUSAND_DOT).replace("#", DECIMAL_DOT);
        String unit = context.getString(R.string.currency_unit);

        return out + " " + unit;
    }

    public static String formatCurrencyHome(Context context, double amt) {
        if (context == null) {
            return "";
        }
        DecimalFormat formatter = new DecimalFormat(context.getString(R.string.format_currency_double));

        //String out = formatter.format(amt).replace(THOUSAND_DOT, DECIMAL_DOT);
//        String out = formatter.format(Math.floor(amt));
        if (amt >= 1000000) amt = Math.floor(amt);

        String out = formatter.format(amt);
        if (amt < 1000) {
            if (out.contains(",")) out = out.replace(".", "@").replace(",", "#");
            else out = out.replace(".", "#").replace(",", "@");

        }else if (amt >= 1000) {
            if (out.indexOf(".") < 4) out = out.replace(".", "@").replace(",", "#");
            else out = out.replace(".", "#").replace(",", "@");
        }
        out = out.replace("@", THOUSAND_DOT).replace("#", DECIMAL_DOT);
        // String unit = context.getString(R.string.currency_unit);

        return out;
    }

    public static String formatCurrencyEnterUnit(Context context, double amt) {
        if (context == null) {
            return "";
        }
        DecimalFormat formatter = new DecimalFormat(context.getString(R.string.format_currency_integer));
        //String out = formatter.format(amt).replace(THOUSAND_DOT, DECIMAL_DOT);
        String out = formatter.format(amt);
        String unit = context.getString(R.string.currency_unit);

        return out + System.getProperty("line.separator") + unit;
    }

    public static CharSequence formatCurrency(Context context, double amt, int valueTxtSize, int unitTXtSize) {
        if (context == null) {
            return new SpannableString("");
        }
        DecimalFormat formatter = new DecimalFormat(context.getString(R.string.format_currency_double));
        String out = formatter.format(amt);
        if (amt < 1000) {
            if (out.contains(",")) out = out.replace(".", "@").replace(",", "#");
            else out = out.replace(".", "#").replace(",", "@");

        }else if (amt >= 1000) {
            if (out.indexOf(".") < 4) out = out.replace(".", "@").replace(",", "#");
            else out = out.replace(".", "#").replace(",", "@");
        }
        out = out.replace("@", THOUSAND_DOT).replace("#", DECIMAL_DOT);
        String unit = context.getString(R.string.currency_unit);


        SpannableString spanValue = new SpannableString(out);
        spanValue.setSpan(new AbsoluteSizeSpan(valueTxtSize), 0, out.length(), 0);
        SpannableString spanUnit = new SpannableString(unit);
        spanUnit.setSpan(new AbsoluteSizeSpan(unitTXtSize), 0, unit.length(), 0);

        return TextUtils.concat(spanValue, " ", spanUnit);
    }

    public static CharSequence formatCurrencyEnterUnit(Context context, double amt, int valueTxtSize, int unitTXtSize) {
        if (context == null) {
            return new SpannableString("");
        }
        DecimalFormat formatter = new DecimalFormat(context.getString(R.string.format_currency_double));
        String out = formatter.format(amt);
        if (amt < 1000) {
            if (out.contains(",")) out = out.replace(".", "@").replace(",", "#");
            else out = out.replace(".", "#").replace(",", "@");

        }else if (amt >= 1000) {
            if (out.indexOf(".") < 4) out = out.replace(".", "@").replace(",", "#");
            else out = out.replace(".", "#").replace(",", "@");
        }
        out = out.replace("@", THOUSAND_DOT).replace("#", DECIMAL_DOT);
        String unit = context.getString(R.string.currency_unit);


        SpannableString spanValue = new SpannableString(out);
        spanValue.setSpan(new AbsoluteSizeSpan(valueTxtSize), 0, out.length(), 0);
        SpannableString spanUnit = new SpannableString(unit);
        spanUnit.setSpan(new AbsoluteSizeSpan(unitTXtSize), 0, unit.length(), 0);

        return TextUtils.concat(spanValue, System.getProperty("line.separator"), spanUnit);
    }

    public static String formatCurrencyShort(Context context, long amt) {
        if (context == null) {
            return "";
        }
        String s = "";
        if (amt >= 1000 && amt < 999999) {
            amt = amt / 1000;
            s = "K";
        } else if (amt >= 1000000) {
            amt = amt / 1000000;
            s = "M";
        } else {
            // skip
        }
        DecimalFormat formatter = new DecimalFormat(context.getString(R.string.format_currency_double));
        String out = formatter.format(amt);
        if (amt < 1000) {
            if (out.contains(",")) out = out.replace(".", "@").replace(",", "#");
            else out = out.replace(".", "#").replace(",", "@");

        }else if (amt >= 1000) {
            if (out.indexOf(".") < 4) out = out.replace(".", "@").replace(",", "#");
            else out = out.replace(".", "#").replace(",", "@");
        }

        out = out.replace("@", THOUSAND_DOT).replace("#", DECIMAL_DOT);
        if (s.length() > 0) {
            out += s;
        }
        String unit = context.getString(R.string.currency_unit);
        return out + " " + unit;
    }

    public static String formatMB(Context context, int amt) {
        DecimalFormat formatter = new DecimalFormat(context.getString(R.string.format_currency_double));
        String out = formatter.format(amt);
        if (amt < 1000) {
            if (out.contains(",")) out = out.replace(".", "@").replace(",", "#");
            else out = out.replace(".", "#").replace(",", "@");

        }else if (amt >= 1000) {
            if (out.indexOf(".") < 4) out = out.replace(".", "@").replace(",", "#");
            else out = out.replace(".", "#").replace(",", "@");
        }

        out = out.replace("@", THOUSAND_DOT).replace("#", DECIMAL_DOT);
        return out + " MB";
    }

    public static String formatMBHome(Context context, int amt) {
        DecimalFormat formatter = new DecimalFormat(context.getString(R.string.format_currency_double));
        String out = formatter.format(amt);
        if (amt < 1000) {
            if (out.contains(",")) out = out.replace(".", "@").replace(",", "#");
            else out = out.replace(".", "#").replace(",", "@");

        }else if (amt >= 1000) {
            if (out.indexOf(".") < 4) out = out.replace(".", "@").replace(",", "#");
            else out = out.replace(".", "#").replace(",", "@");
        }

        out = out.replace("@", THOUSAND_DOT).replace("#", DECIMAL_DOT);
        return out;
    }

    public static CharSequence formatMB(int iValue, int valueTxtSize, int unitTXtSize) {
        String value = String.valueOf(iValue);
        String unit = "MB";

        SpannableString spanValue = new SpannableString(value);
        spanValue.setSpan(new AbsoluteSizeSpan(valueTxtSize), 0, value.length(), 0);
        SpannableString spanUnit = new SpannableString(unit);
        spanUnit.setSpan(new AbsoluteSizeSpan(unitTXtSize), 0, unit.length(), 0);

        return TextUtils.concat(spanValue, " ", spanUnit);
    }

    public static CharSequence formatMBEnterUnit(int iValue, int valueTxtSize, int unitTXtSize) {
        String value = String.valueOf(iValue);
        String unit = "MB";

        SpannableString spanValue = new SpannableString(value);
        spanValue.setSpan(new AbsoluteSizeSpan(valueTxtSize), 0, value.length(), 0);
        SpannableString spanUnit = new SpannableString(unit);
        spanUnit.setSpan(new AbsoluteSizeSpan(unitTXtSize), 0, unit.length(), 0);

        return TextUtils.concat(spanValue, System.getProperty("line.separator"), spanUnit);
    }

    public static int fromStringToInteger(String s) {
        String digits = s.replaceAll("[^0-9]", "");
        return s.length() == 0 ? 0 : Integer.valueOf(digits);
    }

    public static long fromStringToLong(String s) {
        String digits = s.replaceAll("[^0-9]", "");
        return s.length() == 0 ? 0 : Long.valueOf(digits);
    }

    public static void informSuccess(Context context, String message) {
        informMessage(context, message, DlgFlashMessage.Type.Success, null);
    }

    public static void informSuccess(Context context, String message, final Runnable runAfterDismiss) {
        informMessage(context, message, DlgFlashMessage.Type.Success, runAfterDismiss);
    }

    public static void informWarning(Context context, String message) {
        informMessage(context, message, DlgFlashMessage.Type.Warning, null);
    }

    public static void informError(Context context, String message) {
        informMessage(context, message, DlgFlashMessage.Type.Error, null);
    }

    public static void informError(Context context, String message, final Runnable runAfterDismiss) {
        informMessage(context, message, DlgFlashMessage.Type.Error, runAfterDismiss);
    }

    //    private static void informMessage(Context context, String message, DlgFlashMessage.Type type, final Runnable runAfterDismiss) {
//        if (context == null) {
//            return;
//        }
//        if(message==null) message="";
//        try{
//
//            new DlgFlashMessage(context)
//                    .setType(type)
//                    .setMessage(message)
//                    .setBelowHeader(true)
//                    .setListener(new DlgFlashMessage.Listener() {
//                        @Override
//                        public void onDismiss() {
//                            if (runAfterDismiss != null) {
//                                runAfterDismiss.run();
//                            }
//                        }
//                    })
//                    .show();
//        }catch (Exception e){
//
//        }
//
//    }
    private static DlgFlashMessageToast dlgFlashMessageToast;

    private static void informMessage(Context context, String message, DlgFlashMessage.Type type, Runnable runAfterDismiss) {
        mContext = context;
        if (mContext == null) {
            return;
        }
        if (dlgFlashMessageToast != null) {
            dlgFlashMessageToast.hide();
            dlgFlashMessageToast = null;
        }
        if (message == null) message = "";
        message = message.trim();
        if (message.equalsIgnoreCase("null") || message.length() == 0) {
            return;
        }
        try {

            dlgFlashMessageToast = new DlgFlashMessageToast()
                    .setContext(mContext)
                    .setType(type)
                    .setMessage(message)
                    .init();
            dlgFlashMessageToast.show();
            if (runAfterDismiss != null) runAfterDismiss.run();
        } catch (Exception e) {

        }

    }

    public static void setOverlayStatusBar(Window w, boolean isDark) {
        if (Build.VERSION.SDK_INT >= 21) {
            w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            w.setStatusBarColor(Color.TRANSPARENT);
            int lFlags = w.getDecorView().getSystemUiVisibility();
            if (Build.VERSION.SDK_INT >= 23) {
                w.getDecorView().setSystemUiVisibility(isDark ? (lFlags & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) : (lFlags | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR));
            }
        }
    }

    public static void setImageUrl(Context context, ImageView imageView, String url, int defaultResId) {
        mContext = context;
        if (imageView == null) return;
        if (mContext == null) {
            imageView.setImageResource(defaultResId);
            return;
        }
        if (url != null && url.length() > 0) {
            if (imageView.getDrawable() != null) {
                Glide.with(mContext).
                        load(url)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(imageView.getDrawable())
                        .error(imageView.getDrawable())
                        .into(imageView);
            } else {
                Glide.with(mContext).
                        load(url)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.place_hole_image)
                        .error(defaultResId)
                        .into(imageView);
            }


//            Picasso.with(context)
//                    .load(url)
//                    .placeholder(R.drawable.place_hole_image)
//                    .noFade()
//                    .error(defaultResId)
//                    .into(imageView);
        } else {
            imageView.setImageResource(defaultResId);
        }
    }

    public static void setImageUrlCircle(Context context, ImageView imageView, String url, int defaultResId) {
        mContext = context;
        if (imageView == null) return;
        if (mContext == null) {
            imageView.setImageResource(defaultResId);
            return;
        }
        if (url != null && url.length() > 0) {
            if (imageView.getDrawable() != null) {
                Glide.with(mContext)
                        .load(url)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(imageView.getDrawable())
                        .error(imageView.getDrawable())
                        .into(imageView);
            } else {
                Glide.with(mContext)
                        .load(url)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.place_hole_image_circle)
                        .error(defaultResId)
                        .into(imageView);
            }

//            Picasso.with(context)
//                    .load(url)
//                    .placeholder(R.drawable.place_hole_image_circle)
//                    .noFade()
//                    .error(defaultResId)
//                    .into(imageView);
        } else {
            imageView.setImageResource(defaultResId);
        }
    }

    // tạo chữ gạch chân
    public static void setTextUnderLine(Button button, String text) {
        SpannableString content = new SpannableString(text);
        content.setSpan(new UnderlineSpan(), 0, text.length(), 0);
        button.setText(content);
    }

    public static void prepareWebViewHighPerformance(WebView webView) {
        webView.setBackgroundColor(webView.getResources().getColor(R.color.white));
        webView.setBackgroundResource(R.color.white);
        if (Build.VERSION.SDK_INT >= 19) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            //  webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }
    public static String getTextFromHtml(String htmlSource){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
           return Html.fromHtml(htmlSource, Html.FROM_HTML_MODE_COMPACT).toString();
        }else {
            return Html.fromHtml(htmlSource).toString();
        }
    }
}
