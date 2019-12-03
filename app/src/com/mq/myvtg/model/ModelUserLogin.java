package com.mq.myvtg.model;

import android.content.Context;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mq.myvtg.util.Const;
import com.mq.myvtg.util.SharePref;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelUserLogin {

    @JsonProperty("sessionId")
    public String sessionId="";

    @JsonProperty("username")
    public String username;

    @JsonProperty("token")
    public String token="";

//    @JsonProperty("expriredAt")
//    public long expriredAt = 0;

    public String json;

    public static void saveToCache(Context context, ModelUserLogin model) {
        SharePref.putString(context, Const.KEY_MODEL_USER_LOGIN, model.json);
//        SharePref.putLong(context, Const.KEY_MODEL_USER_LOGIN_EXPIRED_AT, model.expriredAt);
    }

    public static ModelUserLogin loadFromCache(Context context) {
        String str = SharePref.getString(context, Const.KEY_MODEL_USER_LOGIN, "");
//        long expiredAt = SharePref.getLong(context, Const.KEY_MODEL_USER_LOGIN_EXPIRED_AT, 0L);
        ObjectMapper mapper = new ObjectMapper();
        try {
            ModelUserLogin model = mapper.readValue(str, ModelUserLogin.class);
            model.json = str;
//            model.expriredAt = expiredAt;
            return model;
        } catch (Exception e) {
            return null;
        }
    }

//    public boolean isSessionExpired() {
//        long currentTime = new Date().getTime();
//        return currentTime - expriredAt > 10*60*1000; // Cache session are 10 minutes
//    }
}