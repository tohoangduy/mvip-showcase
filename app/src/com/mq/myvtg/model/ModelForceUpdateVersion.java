package com.mq.myvtg.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelForceUpdateVersion {

    @JsonProperty("forceUpgrade")
    public String forceUpgrade;
    @JsonProperty("recommendUpgrade")
    public String recommendUpgrade;
    @JsonProperty("currVersion")
    public String currVersion;
    @JsonProperty("newVersion")
    public String newVersion;
    @JsonProperty("updateInfo")
    public String updateInfo;
}

