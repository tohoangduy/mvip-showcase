package com.mq.myvtg.observer;

import com.mq.myvtg.util.LogUtil;

import java.util.HashMap;
import java.util.Map;

public final class ObserverManager {
    private static HashMap<String, ObserverObj> mObs = new HashMap<>();

    private ObserverManager() {
    }

    public static void register(ObserverObj obj, String id) {
        if (!existed(id)) {
            throw new IllegalArgumentException("The id " + id + " must be declared in ObserverId class");
        }
        mObs.put(id, obj);
    }

    public static void unregister(String observerId) {
        mObs.remove(observerId);
    }

    public static void resgiter(ObserverObj obj, String id, boolean unregisterIfExisted) {
        if (unregisterIfExisted) {
            unregister(id);
        }
        register(obj, id);
    }

    public final class ObserverId {
        public static final String ACTIVITY_MAIN = "ACTIVITY_MAIN";
        public static final String ACTIVITY_HOME = "ACTIVITY_HOME";
        public static final String ACTIVITY_APPS = "ACTIVITY_APPS";
        public static final String ACTIVITY_FIRST = "ACTIVITY_FIRST";
        public static final String ACTIVITY_LOGO = "ACTIVITY_LOGO";
        public static final String ACTIVITY_LOGIN = "ACTIVITY_LOGIN";
    }

    public static boolean existed(String id) {
        switch (id) {
            case ObserverId.ACTIVITY_MAIN:
            case ObserverId.ACTIVITY_HOME:
            case ObserverId.ACTIVITY_APPS:
            case ObserverId.ACTIVITY_FIRST:
            case ObserverId.ACTIVITY_LOGO:
            case ObserverId.ACTIVITY_LOGIN:
                return true;
            default:
                return false;
        }
    }

    /**
     * Notify to all observers
     */
    public static void notifyAll(Object... params) {
        for (Map.Entry<String, ObserverObj> e : mObs.entrySet()) {
            try {
                e.getValue().notification(params);
            } catch (Throwable ex) {
                LogUtil.e("ObserverManager", "notification to " + e.getKey() + " -> " + LogUtil.getThrowableString(ex));
            }
        }
    }

    /**
     * Notify to a specific observer
     */
    public static void notifyOne(String observerId, Object... params) {
        if (mObs.containsKey(observerId)) {
            try {
                mObs.get(observerId).notification(params);
            } catch (Throwable e) {
                LogUtil.e("ObserverManager", "notification to " + observerId + " -> " + LogUtil.getThrowableString(e));
            }
        }
    }

    public interface ObserverObj {
        void notification(Object... params);
    }
}
