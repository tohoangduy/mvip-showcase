package com.mq.myvtg.model.cache;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mq.myvtg.util.SharePref;

public abstract class ModelCache {
    public String json;
    private Boolean isCacheEnable = true;

    public Boolean getCacheEnable() {
        return isCacheEnable;
    }

    public void setCacheEnable(Boolean cacheEnable) {
        isCacheEnable = cacheEnable;
    }

    public void saveToCache(Context context) {
        if (context != null)
            SharePref.putString(context, getKey(), json);
    }

    public ModelCache loadModelFromCache(Context context) {
        String json = SharePref.getString(context, getKey(), null);
        try {
            ObjectMapper mapper = new ObjectMapper();
            ModelCache model = mapper.readValue(json, this.getClass());
            model.json = json;
            return model;
        } catch (Exception e) {
            return null;
        }
    }

    public static ModelCache loadFromCache(Context context, String key, Class<?> cls) {
        String json = SharePref.getString(context, key, null);
        try {
            ObjectMapper mapper = new ObjectMapper();
            ModelCache model = (ModelCache) mapper.readValue(json, cls);
            model.json = json;
            return model;
        } catch (Exception e) {
            return null;
        }
    }

    protected abstract String getKey();
}
