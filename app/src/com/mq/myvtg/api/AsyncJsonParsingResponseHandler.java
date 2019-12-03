package com.mq.myvtg.api;

import android.os.Message;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.mq.myvtg.util.LogUtil;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLException;

@SuppressWarnings("deprecation")
public abstract class AsyncJsonParsingResponseHandler extends JsonHttpResponseHandler {
    private static final String TAG = AsyncJsonParsingResponseHandler.class.getSimpleName();

    private static final int SUCCESS = 1000;
    private static final int FAILURE = 1001;

    public AsyncJsonParsingResponseHandler() {
        super();
    }

    @Override
    public void sendResponseMessage(HttpResponse response) throws IOException {
        // do nothing
        if (!Thread.currentThread().isInterrupted()) {
            StatusLine status = response.getStatusLine();
            byte[] responseBody;
            responseBody = getResponseData(response.getEntity());
            // additional cancellation check as getResponseData() can take non-zero time to process
            if (!Thread.currentThread().isInterrupted()) {
                if (status.getStatusCode() >= 300) {
                    Message msg;
                    try {
                        Object jsonResponse = parseResponse(responseBody);
                        msg = obtainMessage(FAILURE, new Object[]{status.getStatusCode(), jsonResponse, null});
                    } catch (Exception e) {
                        //LogUtil.e(getClass().getSimpleName(), "sendResponseMessage parse response json failed. " + LogUtil.getThrowableString(e));
                        msg = obtainMessage(FAILURE, new Object[]{status.getStatusCode(), null, e});
                    }
                    sendMessage(msg);
                } else {
                    try {
                        Object jsonResponse = parseResponse(responseBody);
                        //LogUtil.v(TAG, " -- parseJson: " + jsonResponse.toString());
                        Object modelData = parseJson((JSONObject) jsonResponse);
                        Message msg = obtainMessage(SUCCESS, modelData);
                        sendMessage(msg);
                    } catch (Exception e) {
                        //LogUtil.e(getClass().getSimpleName(), "sendResponseMessage success but parse response json failed. " + LogUtil.getThrowableString(e));
                        Message msg = obtainMessage(FAILURE, new Object[]{status.getStatusCode(), null, e});
                        sendMessage(msg);
                    }
                }
            }
        }
    }

    @Override
    protected void handleMessage(Message message) {
        super.handleMessage(message);
        switch (message.what) {
            case SUCCESS:
                success(message.obj);
                break;

            case FAILURE:
                Object[] response = (Object[]) message.obj;
                JSONObject jsonObject = null;
                Throwable throwable = null;
                if (response[1] instanceof JSONObject) {
                    jsonObject = (JSONObject) response[1];
                }
                if (response[2] instanceof Throwable) {
                    throwable = (Throwable) response[2];
                }
                handleFailure((int) response[0], jsonObject, throwable);
                break;
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        failed(responseString, responseString, statusCode);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        handleFailure(statusCode, errorResponse, throwable);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
        handleFailure(statusCode, null, throwable);
    }

    private void handleFailure(int code, JSONObject response, Throwable throwable) {
        if (handleException(code, throwable)) {
            return;
        }

        if (onFailedWithResponse(code, response)) {
            return;
        }

        if (throwable instanceof ClientException) {
            if (((ClientException) throwable).needLogout()) {
                code = Client.ERR_CODE_NEED_LOG_OUT;
            }
            String uiMsg = ((ClientException) throwable).getUserMsg();
            failed(LogUtil.getThrowableString(throwable), uiMsg, code);
            return;
        }

        try {
            String message = response.getString("message");
            String reason = "Error code: " + code + " message: " + message;
            failed(reason, null, code);
        } catch (Exception e) {
            String str = response == null ? "" : response.toString();
            String message = LogUtil.getThrowableString(throwable);
            String reason = message + " " + str;
            if (reason.trim().length() == 0) {
                reason = "statusCode: " + code;
            }
            failed(reason, null, code);
        }
    }

    public static boolean isTimeout(Throwable throwable) {
        return (throwable instanceof SocketTimeoutException ||
                throwable instanceof SSLException ||
                throwable instanceof ConnectTimeoutException);
    }

    public static boolean isConnectionError(Throwable throwable) {
        return throwable instanceof HttpHostConnectException ||
                throwable instanceof UnknownHostException;
    }

    public boolean onFailedWithResponse(int code, JSONObject response) {
        return false;
    }

    public boolean onTimeout() {
        return false;
    }

    public boolean onConnectionError() {
        return false;
    }

    private boolean handleException(int code, Throwable throwable) {
        //LogUtil.e("handleException statusCode: " + code + " " + LogUtil.getThrowableString(throwable));

        if (isConnectionError(throwable) && onConnectionError()) {
            // already handled by derrived class
            //LogUtil.e(getClass().getSimpleName(), LogUtil.getThrowableString(throwable));
            return true;
        }

        // already handled by derrived class
        //LogUtil.e(getClass().getSimpleName(), LogUtil.getThrowableString(throwable));
        return isTimeout(throwable) && onTimeout();
    }


    protected Boolean checkResponseError(JSONObject jsonObject) throws ClientException, JSONException {
        // Check error code from API Gateway
        String errCodeGateway = jsonObject.getString("errorCode");
        if (errCodeGateway.equals(Client.ERR_CODE_API_GATEWAY_SUCCESS)) {
            JSONObject result = null;
            try {
                result = jsonObject.optJSONObject("result");
            } catch (Exception ex) {
                if (result == null) {
                    LogUtil.d("The Result is null");
                    return false;
                }
            }

            if (result == null) return false;

            // Check error code from Backend
            int errCodeBackend = result.getInt("errorCode");
            if (errCodeBackend == Client.ERR_CODE_BACKEND_SUCCESS || errCodeBackend == Client.ERR_CODE_BACKEND_SUCCESS_1000) {
                // success --> do nothing
            } else {
                String errorMessage = null, userMsg = null;
                try {
                    errorMessage = result.getString("errorMessage");
                } catch (Exception e) {
                    try {
                        errorMessage = result.getString("message");
                    } catch (Exception ignored) {
                    }
                }
                try {
                    userMsg = result.getString("userMsg");
                } catch (Exception ignored) {
                }
                String dbgMsg = "Error from Backend, errorCode: " + errCodeBackend + ", errorMessage: " + errorMessage + ", userMsg: " + userMsg;
                //LogUtil.e(TAG, dbgMsg);
                throw new ClientException(ClientException.ERR_API_BACKEND, dbgMsg, userMsg);
            }
        } else {
            String errorMessage = null;
            try {
                errorMessage = jsonObject.getString("errorMessage");
            } catch (Exception ignored) {
            }
            String dbgMsg = "Error from API Gateway, errorCode: " + errCodeGateway + ", errorMessage: " + errorMessage;
            //LogUtil.e(TAG, dbgMsg);
            throw new ClientException(ClientException.ERR_API_GATEWAY, dbgMsg, errorMessage, errCodeGateway);
        }

        return true;
    }

    protected abstract Object parseJson(JSONObject jsonObject) throws ClientException;

    protected abstract void success(Object model);

    protected abstract void failed(String reason, String uiMsg, int statusCode);
}
