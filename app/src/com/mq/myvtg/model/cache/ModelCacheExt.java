package com.mq.myvtg.model.cache;

import android.content.Context;

import com.google.gson.Gson;
import com.mq.myvtg.util.Const;
import com.mq.myvtg.util.LogUtil;
import com.mq.myvtg.util.SharePref;

import java.util.Date;

/**
 * Created by LuatNC on 7/3/2017.
 */

public class ModelCacheExt {
    private long cacheTime;
    private int cacheValidTime = Const.CACHE_EXPIRATION;
    public String language = "";

    // Child class should be override this method to change status
    protected static Boolean isCacheEnable() {
        return true;
    }

    public long getCacheTime() {
        return cacheTime;
    }

    public void setCacheTime(long cacheTime) {
        this.cacheTime = cacheTime;
    }

    protected Boolean isCacheValid() {
        boolean isValid = new Date().getTime() <= (cacheTime + cacheValidTime);
        return isValid;
    }

    public Boolean isCacheValid(String currentLang) {
        boolean isValid = new Date().getTime() <= (cacheTime + cacheValidTime);
        isValid = isValid && language.equalsIgnoreCase(currentLang);
        return isValid;
    }

    public void setCacheInvalidTime(int cacheValidTime) {
        this.cacheValidTime = cacheValidTime;
    }

    public void saveModelToCache(Context context, Class<?> cls) {
        if (!isCacheEnable()) return;
        cacheTime = new Date().getTime();
        ParamsSave paramsSave = new ParamsSave(context, cls.getName(), this);
        SharePref.putString(paramsSave.context, paramsSave.className, new Gson().toJson(paramsSave.data));
    }

    public static ModelCacheExt loadModelFromCache(Context context, Class<?> cls) {
        try {
            if (!isCacheEnable()) return null;

            ModelCacheExt modelCacheExt = (ModelCacheExt) new Gson().fromJson(SharePref.getString(context, cls.getName(), null), cls);
            if (modelCacheExt == null) return null;
            if (!modelCacheExt.isCacheValid()) {
                clearCache(context, cls);
                return null;
            }

            return modelCacheExt;
        } catch (Exception e) {
            LogUtil.d(cls.getName());
            return null;
        }
    }

    public static void clearCache(Context context, Class<?> cls) {
        try {
            SharePref.remove(context, new String[]{cls.getName()});
        } catch (Exception e) {
            LogUtil.d("clearCache(): " + e.getMessage());
        }
    }

    class ParamsSave {
        public ParamsSave(Context context, String className, ModelCacheExt data) {
            this.context = context;
            this.className = className;
            this.data = data;
        }

        public Context context;
        public String className = "";
        public ModelCacheExt data;
    }
}
