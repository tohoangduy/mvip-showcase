package com.mq.myvtg.model;

import android.content.Context;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mq.myvtg.api.Client;
import com.mq.myvtg.model.cache.ModelCache;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelSubMainInfo extends ModelCache {
    public static final int SUBTYPE_TRATRUOC = 1;
    public static final int SUBTYPE_TRASAU = 2;

    private static final String KEY = Client.WS_GET_SUB_MAIN_INFO;

    @JsonProperty("name")
    public String name;

    @JsonProperty("subType")
    public int subType;

    @JsonProperty("language")
    public String language;

    @JsonProperty("avatar_url")
    public String avatar_url;

    @JsonProperty("subId")
    public long subId;

    public boolean isTraTruoc() {
        return subType == SUBTYPE_TRATRUOC;
    }

    public String toString() {
        return "ModelSubMainInfo (" + name + ", " + (isTraTruoc()?"trả trước":"trả sau") + ")";
    }
    public static ModelSubMainInfo loadFromCache(Context context) {
        return (ModelSubMainInfo) ModelCache.loadFromCache(context, KEY, ModelSubMainInfo.class);
    }

    @Override
    protected String getKey() {
        return KEY;
    }
}
