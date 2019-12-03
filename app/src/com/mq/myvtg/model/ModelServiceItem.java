package com.mq.myvtg.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelServiceItem extends ModelBase {

    @JsonProperty("code")
    public String code;

    // API wsGetServices trả về "shortDes"
    @JsonProperty("shortDes")
    private String shortDes;
    public void setShortDes(String shortDes) {
        this.shortDes = shortDes;
    }

    // API wsGetCurrentUsedSubServices trả về "des"
    @JsonProperty("des")
    private String des;
    public void setDes(String des) {
        this.des = des;
    }

    @JsonProperty("iconUrl")
    public String iconUrl;

    @JsonProperty("isMultPlan")
    public boolean isMultPlan;

    @JsonProperty("isRegisterAble")
    public Integer isRegisterAble;
    @JsonProperty("state")
    public int state; //0 - register, 1 - un-register, 2 - Processing...
    public boolean alreadyRegistered;

    public String getShortDes() {
        return shortDes != null ? shortDes : des;
    }

    public String getName() {
        return name == null ? "" : name;
    }
}
