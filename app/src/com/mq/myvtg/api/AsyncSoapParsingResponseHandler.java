package com.mq.myvtg.api;

import android.os.Message;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.mq.myvtg.util.LogUtil;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.SSLException;

/**
 * Created by duyth on 21/11/2019.
 */
public abstract class AsyncSoapParsingResponseHandler extends AsyncHttpResponseHandler {
    private static final String TAG = AsyncSoapParsingResponseHandler.class.getSimpleName();

    private static final int SUCCESS = 1000;
    private static final int FAILURE = 1001;

    public AsyncSoapParsingResponseHandler() {
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
                        String strResponse = Arrays.toString(responseBody);
                        msg = obtainMessage(FAILURE, new Object[]{status.getStatusCode(), strResponse, null});
                    } catch (Exception e) {
                        LogUtil.e(getClass().getSimpleName(), "sendResponseMessage parse response json failed. " + LogUtil.getThrowableString(e));
                        msg = obtainMessage(FAILURE, new Object[]{status.getStatusCode(), null, e});
                    }
                    sendMessage(msg);
                } else {
                    try {
                        String strResponse = new String(responseBody);
                        strResponse = strResponse.replaceAll("&quot;", "\"");
                        strResponse = strResponse.replaceAll("&lt;", "<");
                        strResponse = strResponse.replaceAll("&gt;", ">");
                        strResponse = strResponse.replaceAll("\n", "");
                        strResponse = strResponse.replaceAll("&amp;", "&");
                        LogUtil.v(TAG, " -- RESPONSE: " + strResponse);
                        List<String> resValue = parseXML(strResponse);
                        Message msg = obtainMessage(SUCCESS, resValue);
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
                try {
                    List<String> resSucess = (List<String>) message.obj;
                    success(resSucess.get(0), resSucess.get(1));
                } catch (Exception e) {
                    LogUtil.d(e.getMessage());
                }
                break;

            case FAILURE:
                Object[] response = (Object[]) message.obj;
                String resValue = null;
                Throwable throwable = null;
                if (response[1] instanceof XMLReader) {
                    try {
                        resValue = (String) response[1];
                    } catch (ClassCastException e) {
                        LogUtil.d(e.getMessage());
                    }
                }
                if (response[2] instanceof Throwable) {
                    throwable = (Throwable) response[2];
                }
                handleFailure((int) response[0], resValue, throwable);
                break;
        }
    }

    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        String strResponse = Arrays.toString(responseBody);
        LogUtil.v(TAG, " -- parseJson: " + strResponse);
        List<String> resValue = Arrays.asList("", "");
        try {
            resValue = parseXML(strResponse);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        success(resValue.get(0), resValue.get(1));
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        handleFailure(statusCode, responseBody == null ? "Timeout error!" : Arrays.toString(responseBody), error);
    }

    private void handleFailure(int code, String response, Throwable throwable) {
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
            String message = response.substring(
                    response.indexOf("<description>") + "<description>".length(),
                    response.indexOf("</description>")
            );
            String reason = "Error code: " + code + " message: " + message;
            failed(reason, null, code);
        } catch (Exception e) {
            String str = response == null ? "" : response;
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

    public boolean onFailedWithResponse(int code, String response) {
        return false;
    }

    public boolean onTimeout() {
        return true;
    }

    public boolean onConnectionError() {
        return true;
    }

    private boolean handleException(int code, Throwable throwable) {
        LogUtil.e("handleException statusCode: " + code + " " + LogUtil.getThrowableString(throwable));

        if (isConnectionError(throwable) && onConnectionError()) {
            // already handled by derrived class
            LogUtil.e(getClass().getSimpleName(), LogUtil.getThrowableString(throwable));
            return true;
        }

        // already handled by derrived class
        LogUtil.e(getClass().getSimpleName(), LogUtil.getThrowableString(throwable));
        return isTimeout(throwable) && onTimeout();
    }

    /**
     * This function to parse xml string of response which response have format of service of Metfone VIP application
     *
     * @param xmlString - String xml need to parse informations
     * @return A list contain 2 strings
     * the first string is value response,
     * the second string is message to show on UI
     * @throws ClientException
     */
    protected abstract List<String> parseXML(String xmlString) throws ClientException;

    protected abstract void success(String resValue, String uiMsg);

    protected abstract void failed(String reason, String uiMsg, int statusCode);
}
