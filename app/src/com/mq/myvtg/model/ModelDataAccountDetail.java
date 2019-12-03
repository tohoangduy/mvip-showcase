package com.mq.myvtg.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mq.myvtg.model.cache.ModelCacheExt;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelDataAccountDetail extends ModelCacheExt {
//    private static final String KEY = Client.WS_GET_DATA_ACCOUNT_DETAIL;

    @JsonProperty("totalVolume")
    public String totalVolume;

    @JsonProperty("totalVolumeStr")
    public String totalVolumeStr;

//    @JsonProperty("expiredDateStr")
//    public String expiredDateStr;
//
//    @JsonProperty("expiredDate")
//    public String expiredDate;

    @JsonProperty("dataPackages")
    public List<ModelMainPackage> dataPackages;


//    public static ModelDataAccountDetail loadFromCache(Context context) {
//        return (ModelDataAccountDetail) ModelCache.loadFromCache(context, KEY, ModelDataAccountDetail.class);
//    }
//
//    @Override
//    protected String getKey() {
//        return KEY;
//    }
}
