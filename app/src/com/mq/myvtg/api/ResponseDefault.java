package com.mq.myvtg.api;


import com.mq.myvtg.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class ResponseDefault extends AsyncJsonParsingResponseHandler {
    @Override
    protected Object parseJson(JSONObject jsonObject) throws ClientException {
        try {
            checkResponseError(jsonObject);
            return null;
        } catch (JSONException e) {
            throw new ClientException(ClientException.ERR_JSON_PARSING, LogUtil.getThrowableString(e), null);
        }
    }
}
