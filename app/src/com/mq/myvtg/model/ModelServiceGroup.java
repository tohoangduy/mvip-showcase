package com.mq.myvtg.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelServiceGroup {
    @JsonProperty("groupName")
    public String groupName;

    @JsonProperty("groupCode")
    public String groupCode;

    @JsonProperty("services")
    public List<ModelServiceItem> services;
}
