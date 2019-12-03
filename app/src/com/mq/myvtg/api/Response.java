package com.mq.myvtg.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.mq.myvtg.model.ModelDoAction;
import com.mq.myvtg.model.cache.ModelCache;
import com.mq.myvtg.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO: sau này sẽ thay tất cả các class ResponseXXX bằng 1 class này thôi
 */

public abstract class Response extends AsyncJsonParsingResponseHandler {
    public enum ResponseType {
        List, Object, Action, Default, Gson, GsonMetfone
    }

    private ResponseType type;
    private Class<?> cls;

    public Response(ResponseType type, Class<?> cls) {
        this.cls = cls;
        this.type = type;
    }

    @Override
    protected Object parseJson(JSONObject jsonObject) throws ClientException {
        switch (type) {
            case Gson:
                return parseJsonByGson(jsonObject);
            case List:
                return parseJsonArray(jsonObject);
            case Object:
                return parseJsonObject(jsonObject);
            case Action:
                return parseJsonDoAction(jsonObject);
            case Default:
                return parseJsonDefault(jsonObject);
            case GsonMetfone:
                return parseJsonDoMetfone(jsonObject);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Object parseJsonByGson(JSONObject jsonObject) throws ClientException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            checkResponseError(jsonObject);
            Object jo = jsonObject.getJSONObject("result").opt("wsResponse");
            String wsResponse = jo instanceof JSONArray ? jo.toString() : jo.toString();

            if (jo instanceof JSONArray) {
                return new Gson().fromJson("{\"ojbList\":" + wsResponse + "}", cls);
            }

            return new Gson().fromJson(wsResponse, cls);
        } catch (JSONException e) {
            throw new ClientException(ClientException.ERR_JSON_PARSING, LogUtil.getThrowableString(e), null);
        }
    }

    @SuppressWarnings("unchecked")
    private Object parseJsonArray(JSONObject jsonObject) throws ClientException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            checkResponseError(jsonObject);
            JSONArray ja = jsonObject.getJSONObject("result").optJSONArray("wsResponse");
            List list = new ArrayList<>();
            if (ja == null) {
                return list;
            }
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                list.add(mapper.readValue(jo.toString(), cls));
            }
            return list;
        } catch (JSONException | IOException e) {
            throw new ClientException(ClientException.ERR_JSON_PARSING, LogUtil.getThrowableString(e), null);
        }
    }

    @SuppressWarnings("unchecked")
    private Object parseJsonObject(JSONObject jsonObject) throws ClientException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            if (!checkResponseError(jsonObject)) {
                return null;
            }
            JSONObject jo = jsonObject.getJSONObject("result").optJSONObject("wsResponse");
            String jsonStr = jo.toString();
            Object model = mapper.readValue(jsonStr, cls);
            if ((model instanceof ModelCache) && (((ModelCache) model).getCacheEnable())) {
                ((ModelCache) model).json = jsonStr;
            }

//            if (model instanceof ModelSubMainInfo || model instanceof ModelSubInfo ||
//                model instanceof ModelSubAccountInfo) {
//                // Hiện giờ chỉ 2 API getSubMainInfo và getSubInfo cần cache dữ liệu nên chỉ có 2
//                // class Model tương ứng cần lưu lại nội dung json để save vào SharePreference.
//                // Sau này có thêm API nào cần cache dữ liệu thì add thêm class Model vào đây.
//                // Xử lý save cache trong class Fragment, sau khi request API thành công.
//                // Những API nào cần cache dữ liệu thì phải xem trong tài liệu spec.
//                // Nhớ tham khảo cách làm của 2 API trên.
//                ((ModelCache)model).json = jsonStr;
//            }
            return model;
        } catch (JSONException | IOException e) {
            throw new ClientException(ClientException.ERR_JSON_PARSING, LogUtil.getThrowableString(e), null);
        }
    }

    @SuppressWarnings("unchecked")
    private Object parseJsonDoAction(JSONObject jsonObject) throws ClientException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            checkResponseError(jsonObject);
            JSONObject jo = jsonObject.optJSONObject("result");
            if (jo == null) {
                return null;
            }
            return mapper.readValue(jo.toString(), ModelDoAction.class);
        } catch (JSONException | IOException e) {
            throw new ClientException(ClientException.ERR_JSON_PARSING, LogUtil.getThrowableString(e), null);
        }
    }

    private Object parseJsonDefault(JSONObject jsonObject) throws ClientException {
        try {
            checkResponseError(jsonObject);
            return null;
        } catch (JSONException e) {
            throw new ClientException(ClientException.ERR_JSON_PARSING, LogUtil.getThrowableString(e), null);
        }
    }

    private Object parseJsonDoMetfone(JSONObject jsonObject) throws ClientException {
        try {
            ObjectMapper mapper = new ObjectMapper();
//            if (!checkResponseError(jsonObject)) {
//                return null;
//            }
            JSONObject jo = jsonObject.getJSONObject("result");
            String jsonStr = jo.toString();
            Object model = mapper.readValue(jsonStr, cls);
            if ((model instanceof ModelCache) && (((ModelCache) model).getCacheEnable())) {
                ((ModelCache) model).json = jsonStr;
            }
            return model;
        } catch (JSONException | IOException e) {
            throw new ClientException(ClientException.ERR_JSON_PARSING, LogUtil.getThrowableString(e), null);
        }
    }
}
