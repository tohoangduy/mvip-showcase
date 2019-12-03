package com.mq.myvtg.model;

import android.content.Context;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mq.myvtg.api.Client;
import com.mq.myvtg.model.cache.ModelCache;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelSubAccountInfo extends ModelCache {
    private static final String KEY = Client.WS_GET_SUB_ACCOUNT_INFO;
    @JsonProperty("name")
    public String name;

    @JsonProperty("dataPkgName")
    public String dataPkgName;

    @JsonProperty("dataVolume")
    public int dataVolume;

    // trả trước
    @JsonProperty("mainAcc")
    public Double mainAcc;

    @JsonProperty("proAcc")
    public Double proAcc;
    // trả sau
    @JsonProperty("prePost")
    public Double prePost;

    @JsonProperty("debPost")
    public Double debPost;


    public static ModelSubAccountInfo loadFromCache(Context context) {
        return (ModelSubAccountInfo) ModelCache.loadFromCache(context, KEY, ModelSubAccountInfo.class);
    }

    @Override
    protected String getKey() {
        return KEY;
    }
}
