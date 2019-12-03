package com.mq.myvtg.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by nguyen.dang.tho on 6/19/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)

public class ModelNews extends ModelBase {
    @JsonProperty("id")
    public String id;
    @JsonProperty("iconUrl")
    public String iconUrl;
    @JsonProperty("publishDate")
    // timestamp 13 digits
    public long publishDate;

    public ModelNews(String id, String name, String iconUrl) {
        this.id = id;
        this.name = name;
        this.iconUrl = iconUrl;
    }

    public ModelNews() {
    }
}
