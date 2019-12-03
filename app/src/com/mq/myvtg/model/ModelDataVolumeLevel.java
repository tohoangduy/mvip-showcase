package com.mq.myvtg.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelDataVolumeLevel{
    @JsonProperty("volume")
    public int volume;

    @JsonProperty("price")
    public int price;

}
