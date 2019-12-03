package com.mq.myvtg.model;

import android.content.Context;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mq.myvtg.api.Client;
import com.mq.myvtg.util.UIHelper;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelDoAction {
    @JsonProperty("errorCode")
    public int errorCode;

    @JsonProperty("message")
    public String message;

    @JsonProperty("userMsg")
    public String userMsg;

    @JsonProperty("wsResponse")
    public String wsResponse;

    public boolean isSuccess() {
        return (errorCode == Client.ERR_CODE_BACKEND_SUCCESS) || (errorCode == Client.ERR_CODE_BACKEND_SUCCESS_1000);
    }

    public void showUserMsg(Context context) {
        if (userMsg == null) {
            return;
        }
        if (isSuccess()) {
            UIHelper.informSuccess(context, userMsg);
        } else {
            UIHelper.informError(context, userMsg);
        }
    }
}
