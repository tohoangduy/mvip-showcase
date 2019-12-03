package com.mq.myvtg.api.soap;

import android.content.Context;

import com.mq.myvtg.R;
import com.mq.myvtg.api.AsyncSoapParsingResponseHandler;
import com.mq.myvtg.api.Client;
import com.mq.myvtg.api.ClientException;
import com.mq.myvtg.util.LogUtil;
import com.mq.myvtg.util.UIHelper;

import java.util.ArrayList;
import java.util.List;

public abstract class SoapResponse extends AsyncSoapParsingResponseHandler {
    private static final String GW_RES_CODE_TAG_NAME = "error";
    private static final String WS_RES_CODE_TAG_NAME = "errorCode";
    private static final String WS_RES_MESS_TAG_NAME = "description";
    private static final String WS_RES_VALUE_TAG_NAME = "return";

    private Context context;

    public SoapResponse(Context context) {
        this.context = context;
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
    @Override
    public List<String> parseXML(String xmlString) throws ClientException {
        int errorCodeGW = Client.ERR_CODE_GETWAY_SUCCESS;
        int errorCodeWS = Client.ERR_CODE_BACKEND_SUCCESS;
        List<String> valueParsed = new ArrayList<>();

        String openGWCodeTag = "<" + GW_RES_CODE_TAG_NAME + ">";
        String closeGWCodeTag = "</" + GW_RES_CODE_TAG_NAME + ">";
        if (xmlString.contains(openGWCodeTag)) {
            String strErrorCode = xmlString.substring(
                    xmlString.indexOf(openGWCodeTag) + openGWCodeTag.length(),
                    xmlString.indexOf(closeGWCodeTag)
            );
            errorCodeGW = Integer.parseInt(strErrorCode);
        }

        String openWSCodeTag = "<" + WS_RES_CODE_TAG_NAME + ">";
        String closeWSCodeTag = "</" + WS_RES_CODE_TAG_NAME + ">";
        String strErrorCode = "";
        if (xmlString.contains(openWSCodeTag)) {
            strErrorCode = xmlString.substring(
                    xmlString.indexOf(openWSCodeTag) + openWSCodeTag.length(),
                    xmlString.indexOf(closeWSCodeTag)
            );
            errorCodeWS = Integer.parseInt(strErrorCode);
        }

        String openWSMessTag = "<" + WS_RES_MESS_TAG_NAME + ">";
        String closeWSMessTag = "</" + WS_RES_MESS_TAG_NAME + ">";
        String errorMessage = "";
        if (xmlString.contains(openWSMessTag)) {
            errorMessage = xmlString.substring(
                    xmlString.indexOf(openWSMessTag) + openWSMessTag.length(),
                    xmlString.indexOf(closeWSMessTag)
            );

            if (errorCodeGW != Client.ERR_CODE_GETWAY_SUCCESS) {
                String dbgMsg = "Error from API Gateway, errorCode: " + errorCodeGW + ", errorMessage: " + errorMessage;
                LogUtil.e("SoapResponse", dbgMsg);
                throw new ClientException(ClientException.ERR_API_GATEWAY, dbgMsg, errorMessage);
            } else if (errorCodeWS != Client.ERR_CODE_BACKEND_SUCCESS) {
                String dbgMsg = "Error from ws, errorCode: " + errorCodeWS + ", errorMessage: " + errorMessage;
                LogUtil.e("SoapResponse", dbgMsg);
                throw new ClientException(ClientException.ERR_API_BACKEND, dbgMsg, errorMessage);
            }
        }

        String openResValueTag = "<" + WS_RES_VALUE_TAG_NAME + ">";
        String closeResValueTag = "</" + WS_RES_VALUE_TAG_NAME + ">";

        String resValue = "";
        if (xmlString.contains(openResValueTag)) {
            resValue = xmlString.substring(
                    xmlString.lastIndexOf(openResValueTag) + openResValueTag.length(),
                    xmlString.lastIndexOf(closeResValueTag)
            );

            resValue = resValue.replace(openWSCodeTag + strErrorCode + closeWSCodeTag, "");
            resValue = resValue.replace(openWSMessTag + errorMessage + closeWSMessTag, "");
        }

        valueParsed.add(resValue);
        valueParsed.add(errorMessage);

        return valueParsed;
    }

    @Override
    public boolean onTimeout() {
        UIHelper.showDialogFailure(context, context.getString(R.string.cannot_connect_to_server));
        return true;
    }

    @Override
    public boolean onConnectionError() {
        UIHelper.showDialogFailure(context, context.getString(R.string.notify_network_error));
        return true;
    }
}
