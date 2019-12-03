package com.mq.myvtg.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelMainPackage {

    @JsonProperty("name")
    public String name = "";

    @JsonProperty("code")
    public String code;

    @JsonProperty("volume")
    public String volume;

    @JsonProperty("volumeStr")
    public String volumeStr;

    @JsonProperty("price")
    public double price;

    @JsonProperty("expiredDate")
    public String expiredDate;

    @JsonProperty("expiredDateStr")
    public String expiredDateStr;

    @JsonProperty("pakagesToBuy")
    public List<ModelDataVolumeLevel> pakagesToBuy;

}
