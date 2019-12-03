package com.mq.myvtg.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mq.myvtg.model.cache.ModelCacheExt;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelSubInfo extends ModelCacheExt {

    @JsonProperty("name")
    public String name;

    @JsonProperty("birthday")
    public long birthday;

    @JsonProperty("job")
    public String job;

    @JsonProperty("hobby")
    public String hobby;

    @JsonProperty("email")
    public String email;

    @JsonProperty("lastSyncTime")
    public long lastSyncTime;

}
