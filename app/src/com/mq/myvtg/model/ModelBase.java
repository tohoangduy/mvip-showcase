package com.mq.myvtg.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by nguyen.dang.tho on 7/10/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelBase {
    @JsonProperty("name")
    public String name;
}
