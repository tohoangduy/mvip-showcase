package com.mq.myvtg.base;

import android.app.Activity;
import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;

import androidx.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.datatheorem.android.trustkit.TrustKit;
import com.mq.myvtg.R;
import com.mq.myvtg.api.Client;
import com.mq.myvtg.observer.ObserverManager;
import com.mq.myvtg.util.Const;
import com.mq.myvtg.util.LogUtil;
import com.mq.myvtg.util.SharePref;
import com.mq.myvtg.util.Utils;

import java.util.Locale;

import io.fabric.sdk.android.Fabric;

public class MyVTGApp extends MultiDexApplication {
    private static final String TAG = MyVTGApp.class.getSimpleName();
    private Locale locale = null;
    private static MyVTGApp instance;

    public static Application getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Fabric.with(this, new Crashlytics());
        try {
            TrustKit.initializeWithNetworkSecurityConfiguration(this, R.xml.network_security_config);
        } catch (Throwable throwable) {
            LogUtil.e(TAG, "TrustKit initialize failed. " + LogUtil.getThrowableString(throwable));
        }
        String lang = Utils.getLanguage(this);
        locale = getLocale(lang);
        changeLang(lang);
        Client.initUrl(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!locale.getLanguage().equals(newConfig.locale.getLanguage())) {
            Resources res = getBaseContext().getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Locale.setDefault(locale);
            Configuration conf = new Configuration(newConfig);
            if (Build.VERSION.SDK_INT >= 17) {
                conf.setLocale(locale);
            } else {
                conf.locale = locale;
            }
            res.updateConfiguration(conf, dm);
        }
    }

    public void changeLang(String lang) {
        if (lang == null) {
            return;
        }
        switch (lang) {
            case Const.LANGUAGES.KHMER:
            case Const.LANGUAGES.ENLISH:
                break;
            default:
                LogUtil.e("Only support these languages: " + Const.LANG_EN + ", " + Const.LANG_KH);
                return;
        }
        Client.getInstance().lang = lang;
        Resources res = getBaseContext().getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration config = res.getConfiguration();
        SharePref.putString(this, Const.KEY_CURRENT_LANG, lang);
        locale = getLocale(lang);
        Locale.setDefault(locale);
        Configuration conf = new Configuration(config);
        if (Build.VERSION.SDK_INT >= 17) {
            conf.setLocale(locale);
        } else {
            conf.locale = locale;
        }
        res.updateConfiguration(conf, dm);
        ObserverManager.notifyAll(Const.ObsNotiId.CHANGE_LANGUAGE, lang);
    }

    private Locale getLocale(String lang) {
        if (lang == null) {
            lang = Utils.getLanguage(this);
        }
        String localeLang = Const.LANG_KH;
        switch (lang) {
            case Const.LANGUAGES.ENLISH:
                localeLang = Const.LANG_EN;
                break;
        }
        locale = new Locale(localeLang);
        return locale;
    }

    public void updateResourceLocale(Activity activity) {
        Resources res1 = activity.getResources();
        DisplayMetrics dm = res1.getDisplayMetrics();
        Configuration config = res1.getConfiguration();
        Configuration conf = new Configuration(config);
        if (Build.VERSION.SDK_INT >= 17) {
            conf.setLocale(locale);
        } else {
            conf.locale = locale;
        }
        res1.updateConfiguration(conf, dm);
    }

}
