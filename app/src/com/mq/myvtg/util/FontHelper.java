package com.mq.myvtg.util;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;
import java.util.Map;

public class FontHelper {
    private Map<String, Typeface> cache = new HashMap<>();

    private static FontHelper instance = null;
    private FontHelper() {}
    public static FontHelper getInstance() {
        if (instance == null) {
            instance = new FontHelper();
        }
        return instance;
    }

    public Typeface getTypeFace(Context context, String name) {
        if (!cache.containsKey(name)) {
            Typeface typeface = Typeface.createFromAsset(context.getAssets(), name);
            cache.put(name, typeface);
        }
        return cache.get(name);
    }
}
