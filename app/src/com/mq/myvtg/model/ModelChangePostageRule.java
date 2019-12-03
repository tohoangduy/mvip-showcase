package com.mq.myvtg.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)

public class ModelChangePostageRule {
    @JsonProperty("score")
    public int score;

    @JsonProperty("money")
    public int money;
}
