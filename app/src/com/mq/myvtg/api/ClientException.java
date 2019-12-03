package com.mq.myvtg.api;

public class ClientException extends Exception {
    public static final int ERR_API_GATEWAY = 1;
    public static final int ERR_API_BACKEND = 2;
    public static final int ERR_JSON_PARSING = 3;
    private int errType;
    private String userMsg;
    private String errCodeGateway = "";

    public ClientException(int errType, String debugMsg, String userMsg) {
        super(debugMsg);
        this.errType = errType;
        this.userMsg = userMsg;
    }

    public ClientException(int errType, String debugMsg, String userMsg, String errCodeGateway) {
        super(debugMsg);
        this.errType = errType;
        this.userMsg = userMsg;
        this.errCodeGateway = errCodeGateway;
    }

    public String getUserMsg() {
        switch (errType) {
            case ERR_API_BACKEND:
            case ERR_API_GATEWAY:
                return userMsg;
            case ERR_JSON_PARSING:
                return "The response format is not correct.";
            default:
                return null;
        }
    }

    public boolean needLogout() {
        return errCodeGateway.equalsIgnoreCase("S401");
        //return errCodeGateway.equalsIgnoreCase("S401") || errCodeGateway.equalsIgnoreCase("S204");
    }
}
