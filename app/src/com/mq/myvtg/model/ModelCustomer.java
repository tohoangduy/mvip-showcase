package com.mq.myvtg.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelCustomer {

    @JsonProperty("address")
    public String address;
    @JsonProperty("birthdate")
    public String birthdate;
    @JsonProperty("notes")
    public String notes;
    @JsonProperty("sex")
    public String sex;
    @JsonProperty("idno")
    public String idno;
    @JsonProperty("bustype")
    public String bustype;
    @JsonProperty("addeduser")
    public String addeduser;
    @JsonProperty("idtype")
    public String idtype;
    @JsonProperty("addeddate")
    public String addeddate;
    @JsonProperty("province")
    public String province;
    @JsonProperty("correctus")
    public String correctus;
    @JsonProperty("custid")
    public String custid;
    @JsonProperty("name")
    public String name;
    @JsonProperty("isupdate")
    public String isupdate;
    @JsonProperty("vip")
    public String vip;
    @JsonProperty("status")
    public String status;
}
