package com.mq.myvtg.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelPromotion extends ModelBase{

    @JsonProperty("code")
    public String code;

    @JsonProperty("shortDes")
    public String shortDes;

    @JsonProperty("iconUrl")
    public String iconUrl;

    @JsonProperty("publishDate")
    public long publishDate;

    @JsonProperty("actionType")
    public int actionType;

    public boolean canBuy() {
        return actionType == 1;
    }
}

