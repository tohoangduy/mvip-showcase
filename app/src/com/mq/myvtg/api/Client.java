package com.mq.myvtg.api;

import android.content.Context;
import android.os.Build;

import com.datatheorem.android.trustkit.TrustKit;
import com.datatheorem.android.trustkit.config.DomainPinningPolicy;
import com.datatheorem.android.trustkit.pinning.PinningTrustManager;
import com.datatheorem.android.trustkit.pinning.SystemTrustManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.mq.myvtg.R;
import com.mq.myvtg.model.ModelSubAccountInfo;
import com.mq.myvtg.model.ModelSubInfo;
import com.mq.myvtg.model.ModelSubMainInfo;
import com.mq.myvtg.model.ModelUserLogin;
import com.mq.myvtg.util.Const;
import com.mq.myvtg.util.LogUtil;
import com.mq.myvtg.util.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.net.URI;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.X509TrustManager;

public class Client extends AsyncHttpClient {
    private static final String TAG = Client.class.getSimpleName();
    private static final int ONE_SECOND = 1000;         // in millisecond
    public static final int TIMEOUT = 15 * ONE_SECOND;
    public static final int RETRY_NUM = 3;
    public static final int PAGE_SIZE = 10;
    protected static final String URL_TIMEOUT = "https://google.com:81/";
    protected static final String TEXT_XML = "text/xml";
    protected static String CHAT_CALL_HOST_IPCC;

    public static String API_PREFIX;
    public static String APP_CODE;

    protected static String BASE_IP;
    protected static String BASE_URL;
    protected static String USER_ROUNTING;

    private static Client mInstance = null;

    public static final String ERR_CODE_API_GATEWAY_SUCCESS = "S200";
    public static final int ERR_CODE_GETWAY_SUCCESS = 0;
    public static final int ERR_CODE_BACKEND_SUCCESS = 0;
    public static final int ERR_CODE_BACKEND_SUCCESS_1000 = 1000;
    public static final int ERR_CODE_NEED_LOG_OUT = 401;

    // API name of Metfone VIP app
    public static final String WS_LOGIN = "loginScanVip";
    public static final String WS_SIGN_UP = "registerUserVip";
    public static final String WS_CONFIRM_REGISTER = "confirmRegisterVip";
    public static final String WS_FORGOT_PWD_GET_OTP = "forgotPassword";
    public static final String WS_FORGOT_PWD_CONFIRM = "confirmForgotPassword";
    public static final String WS_CHANGE_PWD = "changePassVip";
    public static final String WS_VERIFY_GIFT_CODE = "verifyGiftCode";
    public static final String WS_EXCHANGE_GIFT = "exchangeGift";
    public static final String WS_SAVE_SCAN_VIP = "doSaveScanVip";
    public static final String WS_GET_LIST_RANK = "getListRank";
    public static final String WS_GET_HIS_SCAN_VIP = "getHisScanVIP";
    public static final String WS_GET_SLIDE_SHOW = "getSlideShows";

    public static final String WS_GET_SERVICES = "wsGetServices";
    public static final String WS_GET_SERVICES_BY_GROUP = "wsGetServicesByGroup";
    public static final String WS_GET_CURRENT_USED_SERVICES = "wsGetCurrentUsedServices";
    public static final String WS_GET_CURRENT_USED_SUB_SERVICES = "wsGetCurrentUsedSubServices";
    public static final String WS_GET_SERVICE_DETAIL = "wsGetServiceDetail";
    public static final String WS_GET_SUBINFO = "wsGetSubInfo";
    public static final String WS_GET_CAREERS = "wsGetCareers";
    public static final String WS_GET_HOBBIES = "wsGetHobbies";
    public static final String WS_UPDATE_SUB_INFO = "wsUpdateSubInfoNew";
    public static final String WS_GET_SUB_MAIN_INFO = "wsGetSubMainInfo";
    public static final String WS_GET_SUB_ACCOUNT_INFO = "wsGetSubAccountInfoNew";
    public static final String WS_GET_CALL_ACCOUNT_DETAIL = "wsGetCallAccountDetail";
    public static final String WS_GET_ACCOUNT_DETAIL = "wsGetAccountsOcsDetail";
    public static final String WS_GET_DATA_ACCOUNT_DETAIL = "wsGetDataAccountDetail";
    public static final String WS_GET_NEAREST_STORES = "wsGetNearestStores";
    public static final String WS_FIND_STORE_BY_ADDR = "wsFindStoreByAddr";
    public static final String WS_GET_PROVINCES = "wsGetProvinces";
    public static final String WS_GET_DISTRICTS = "wsGetDistricts";
    public static final String WS_GET_HOT_NEWS = "wsGetHotNews";
    public static final String WS_GET_ALL_DATA_PACKAGES = "wsGetAllDataPackages";
    public static final String WS_GET_DATA_VOLUME_LEVEL_TO_BUY = "wsGetDataVolumeLevelToBuy";
    public static final String WS_GET_DATA_PACKAGE_INFO = "wsGetDataPackageInfo";
    public static final String WS_DO_BUY_DATA = "wsDoBuyData";
    public static final String WS_DO_ACTION_SERVICE = "wsDoActionService";
    public static final String WS_DO_ACTION_BY_WEBSERVICE = "wsDoActionByWebService";
    public static final String WS_DO_RECHARGE = "wsTopUp";
    public static final String WS_GET_POSTAGE_INFO = "wsGetPostageInfo";
    public static final String WS_GET_POSTAGE_DETAIL_INFO = "wsGetPostageDetailInfo";
    //for WS_GET_POSTAGE_DETAIL_INFO
    public static final String SORT_TYPE_DESC = "desc";
    public static final String SORT_TYPE_ASC = "asc";
    public static final String WS_GET_SERVICE_INFO = "wsGetServiceInfo";
    public static final String WS_GET_ADVANCED_SERVICE_INFO = "wsGetAdvancedServiceInfo";
    public static final String WS_GET_SERVICE_FUNCTION_PAGE = "wsGetServiceFuntionPage";
    public static final String WS_FIND_ISDN_TO_BUY = "wsFindIsdnToBuy";
    public static final String WS_REGISTER_BUY_ISDN = "wsRegisterBuyIsdn";
    public static final String WS_DO_BUY_ISDN = "wsDoBuyIsdn";

    public static final String WS_GET_NEW_PROMOTIONS = "wsGetNewPromotions";
    public static final String WS_GET_PROMOTION_INFO = "wsGetPromotionInfo";
    public static final String WS_GET_ALL_APPS = "wsGetAllApps";
    public static final String WS_GET_NEWS_DETAIL = "wsGetNewsDetail";
    public static final String WS_DO_SEND_EMAIL = "wsDoSendEmail";
    public static final String WS_GET_CHANGE_POSTAGE_RULE = "wsGetChangePostageRule";
    public static final String WS_GET_NEWS = "wsGetNews";

    public static final String WS_GET_EXPERIENCE_LINK_3G_4G = "wsGetExperienceLink3G4G";

    public static final String WS_DO_LOCK_CALL_GO = "wsDoLockCallGoIsdn";
    public static final String WS_DO_CHANGE_SIM = "wsDoChangeSIM";

    public static final String WS_GET_APP_COFIG = "wsGetAppConfig";
    public static final String WS_DETACH_IP = "wsDetachIP";
    public static final String WS_CHECK_FORCE_UPDATE_SERVICE = "wsCheckForceUpdateApp";

    //update information
    public static final String WS_DO_LOGIN_BY_GET_CODE = "wsDoLoginByGetCode";
    public static final String WS_VERIFY_BY_CODE = "wsVerifyByCode";
    public static final String WS_GET_PROFILE_BY_ISDN = "wsGetProfileByIsdn";
    public static final String WS_UPDATE_INFOR_BY_ISDN = "wsUpdateCustomerInfo";

    public static final String WS_SEARCH_NICE_NUMBER = "wsSearchNiceNumber";
    public static final String WS_RESERVE_NUMBER = "wsReserveNumber";

    public static final String WS_GENERATE_QR_CODE = "wsGenerateQRCode";

    public static final String WS_GET_LIST_NOTI = "wsGetListNotificationByIsdn";
    public static final String WS_UPDATE_DEVICE_TOKEN = "wsUpdateDeviceToken";
    public static final String WS_LOG_CLICK_NOTI = "wsLogClickNotification";
    public static final String WS_GET_POPUP_SERVICE = "wsGetPopUpServices";
    public static final String WS_DO_REDEEM = "wsDoRedeem";
    public static final String WS_VALIDATE_REDEEM = "wsValidateRedeem";

    public static void initUrl(Context context) {
        BASE_IP = "http://";
        BASE_URL = BASE_IP + "/";
        USER_ROUNTING = BASE_URL + "UserRouting";
        CHAT_CALL_HOST_IPCC = context.getString(R.string.chat_call_host_ipcc);
        API_PREFIX = context.getString(R.string.api_prefix);
        APP_CODE = context.getString(R.string.app_code);
    }

    public static Client getInstance() {
        if (mInstance == null) {
            mInstance = new Client();
        }
        return mInstance;
    }

    @Override
    public void addHeader(String header, String value) {
        if (value == null) {
            return;
        }
        super.addHeader(header, value);
    }


    private X509TrustManager baselineTrustManager = SystemTrustManager.getInstance();

    private X509TrustManager getTrustManager(final String serverHostname) {
        if (Build.VERSION.SDK_INT < 17) {
            // No pinning validation at all for API level before 17
            // Because X509TrustManagerExtensions is not available
            return baselineTrustManager;
        }

        // Get the pinning policy for this hostname
        DomainPinningPolicy serverConfig = TrustKit.getInstance().getConfiguration().getPolicyForHostname(serverHostname);
        if (serverConfig == null) {
            return new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    throw new CertificateException("Hostname " + serverHostname + " is not valid");
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
        }
        return new PinningTrustManager(serverHostname, serverConfig, baselineTrustManager);
    }

    private SSLSocketFactory sslSocketFactory;

    private void pinSSL(String url) {
        if (sslSocketFactory == null) {
            try {
                String host = new URI(url).getHost();
                X509TrustManager tm = getTrustManager(host);
                sslSocketFactory = new MySSLSocketFactory(tm);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e("Client", "[pinSSL] " + LogUtil.getThrowableString(e));
                return;
            }
        }
        setSSLSocketFactory(sslSocketFactory);
    }

    private void preRequest(String url) {
        if (Const.ENABLE_SSL_PINNING) {
            pinSSL(url);
        }
        setTimeout(TIMEOUT);
    }

    public void initUrlFake(Context context, boolean isShake) {
//        if (isShake) {
//            BASE_IP = "https://apigw.viettelglobal.net/";
//        } else
//            BASE_IP = context.getString(R.string.server_url);
//        CHAT_CALL_HOST = context.getString(R.string.chat_call_host);
//        BASE_URL = BASE_IP + "CoreService/";
//
//        USER_ROUNTING = BASE_URL + "UserRouting";
//        API_PREFIX = context.getString(R.string.api_prefix);
//        APP_CODE = context.getString(R.string.app_code);
    }


    public boolean isLoginAuto = false;
    public boolean isBackground = false;
    public ModelUserLogin userLogin;
    public ModelSubInfo subInfo;
    public ModelSubMainInfo subMainInfo;
    public ModelSubAccountInfo subAccountInfo;
    public String lang = Const.LANG_EN;

    private void onLogout() {
        userLogin = null;
        subMainInfo = null;
        subInfo = null;
    }

    public boolean isTraTruoc() {
        return getSubType() == ModelSubMainInfo.SUBTYPE_TRATRUOC;
    }

    public String getIsdn() {
        return userLogin != null ? userLogin.username == null ? "" : userLogin.username : "";
    }

    public int getSubType() {
        return subMainInfo == null ? 0 : subMainInfo.subType;
    }

    private Client() {
    }

    private void logParams(Map<String, Object> params) {
        if (params.containsKey("token")) {
            LogUtil.d(" -- token: " + params.get("token"));
        }
        JSONObject jo = new JSONObject(params);
        LogUtil.d(" -- Params: " + jo.toString());
    }

    private void doGET(String url, AsyncJsonParsingResponseHandler response) {
        LogUtil.d("GET: " + url);
        preRequest(url);
        get(url, null, response);
    }

    private void doPOST(Context context, String url, Map<String, Object> param, AsyncJsonParsingResponseHandler response) {
        LogUtil.d("POST: " + url);
        logParams(param);
        ObjectMapper mapper = new ObjectMapper();
        try {
            preRequest(url);
            String strParams = mapper.writeValueAsString(param);
            HttpEntity entity = param == null ? null : new StringEntity(strParams, "UTF-8");
            post(context, url, entity, "application/json", response);
        } catch (Exception e) {
            String failed = LogUtil.getThrowableString(e);
            response.failed(failed, null, -1);
        }
    }

    private void doPostSOAP(Context context, String url, String wsName, Map<String, Object> param, AsyncSoapParsingResponseHandler response) {
        logParams(param);
        try {
            preRequest(url);
            String strParams = createXMLRequest(wsName, param);
            LogUtil.d("Request: " + strParams);
            HttpEntity entity = param == null ? null : new StringEntity(strParams, "UTF-8");
            post(context, url, entity, TEXT_XML, response);
        } catch (Exception e) {
            LogUtil.e(e.toString());
            response.failed(e.toString(), e.getMessage(), 1);
        }
    }

    public void request(Context context, String wsName, Map<String, Object> apiParams, AsyncSoapParsingResponseHandler response) {
        try {
            doPostSOAP(context, BASE_URL + wsName, wsName, apiParams, response);
        } catch (Exception e) {
            response.failed(e.getMessage(), null, -1);
        }
    }

    private void doPOST(Context context, String apiCode, Map<String, Object> apiParams, String[] requiredParams, AsyncJsonParsingResponseHandler response) {
        String msg = checkRequiredParams(apiParams, requiredParams);
        if (msg != null) {
            response.failed(msg, null, 0);
        } else {
            Map<String, Object> postParams = createPostParams(apiParams, apiCode);
            doPOST(context, USER_ROUNTING, postParams, response);
        }
    }

    private void testTimeOut(Context context, AsyncJsonParsingResponseHandler response) {
        doPOST(context, URL_TIMEOUT, null, response);
    }

    ////////////////////////////////////////////////////
    public void autoLogin(Context context, String isdn, AsyncJsonParsingResponseHandler response) {
        String url = BASE_URL + "UserLoginAuto";
        Map<String, Object> params = new HashMap<>();
        params.put("username", isdn);
        params.put("prefix", API_PREFIX);
        params.put("appCode", APP_CODE);
        params.put("clientOS", "1");
        doPOST(context, url, params, response);
    }

    public void changePassword(Context context, String passwordOld, String passwordNew, String isdn, AsyncJsonParsingResponseHandler response) {
        String url = BASE_URL + "UserChangePassword";
        Map<String, Object> params = new HashMap<>();
        params.put("username", isdn);
        params.put("passold", passwordOld);
        params.put("passnew", passwordNew);
        params.put("isEncrypt", "1");
//        params.put("prefix", API_PREFIX);
//        params.put("appCode", APP_CODE);
        doPOST(context, url, params, response);
    }

    public void forgotPassword(Context context, String isdn, String otp, AsyncJsonParsingResponseHandler response) {
        String url = BASE_URL + "UserForgotPassword";
        Map<String, Object> params = new HashMap<>();
        params.put("username", isdn);
        params.put("otp", otp);
//        params.put("prefix", API_PREFIX);
//        params.put("appCode", APP_CODE);
        doPOST(context, url, params, response);
    }

    public void register(Context context, String isdn, String pwd, String otp, AsyncJsonParsingResponseHandler response) {
        String url = BASE_URL + "UserRegister";
        Map<String, Object> params = new HashMap<>();
        params.put("username", isdn);
        params.put("password", pwd);
        params.put("prefix", API_PREFIX);
        params.put("appCode", APP_CODE);
        params.put("otp", otp);
//        params.put("appCode", APP_CODE);
        doPOST(context, url, params, response);
    }

    public void login(Context context, String isdn, String pwd, AsyncJsonParsingResponseHandler response) {
        String url = BASE_URL + "UserLogin";
        Map<String, Object> params = new HashMap<>();
        params.put("username", isdn);
        params.put("password", pwd);
        params.put("prefix", API_PREFIX);
        params.put("appCode", APP_CODE);
        params.put("isEncrypt", "1");
        params.put("clientOS", "1");
        doPOST(context, url, params, response);
    }

    public void login(Context context, String isdn, String pwd, AsyncSoapParsingResponseHandler response) {
        String url = BASE_URL + WS_LOGIN;
        Map<String, Object> params = new HashMap<>();
        params.put("isdn", isdn);
        params.put("passwordLogin", pwd);
        params.put("locale", Utils.getLocale(context));
        doPostSOAP(context, url, WS_LOGIN, params, response);
    }

    public void logout(Context context, AsyncJsonParsingResponseHandler response) {
        String url = BASE_URL + "UserLogout";
        Map<String, Object> params = new HashMap<>();
        params.put("username", getIsdn());
        params.put("sessionId", userLogin.sessionId);
        params.put("language", lang);
        doPOST(context, url, params, response);
        // Mặc định logout lúc nào cũng thành công nên sau khi call API thì clear data luôn
        onLogout();
    }

    public void getOtp(String isdn, AsyncJsonParsingResponseHandler response) {
        String url = BASE_URL + "getOtp?username=" + isdn;
        doGET(url, response);
    }

    public void loginOtp(Context context, String isdn, String otp, AsyncJsonParsingResponseHandler response) {
        String url = BASE_URL + "UserLoginOtp";
        Map<String, Object> params = new HashMap<>();
        params.put("username", isdn);
        params.put("otp", otp);
        params.put("prefix", API_PREFIX);
        params.put("appCode", APP_CODE);
        params.put("clientOS", "1");
        doPOST(context, url, params, response);
    }

    // send request via API Gateway
    public void request(Context context, String apiCode, Map<String, Object> apiParams, AsyncJsonParsingResponseHandler response) {
        try {
            String[] requiredParams = getRequiredParams(apiCode);
            apiParams = getBaseApiParam(context, apiParams);
            if (apiParams.containsKey("test_timeout")) {
                testTimeOut(context, response);
            } else {
                doPOST(context, apiCode, apiParams, requiredParams, response);
            }
        } catch (Exception e) {
            response.failed(e.getMessage(), null, -1);
        }
    }

    // TODO: 2019-08-06 request auth_token IPCC - HC

    public void reqAuthTokenIPCC(Context context, HashMap<String, Object> apiParam, JsonHttpResponseHandler response) {
        ObjectMapper mapper = new ObjectMapper();
        HttpEntity entity = null;
        try {
            String strParams = mapper.writeValueAsString(apiParam);
            entity = apiParam == null ? null : new StringEntity(strParams, "UTF-8");
            put(context, CHAT_CALL_HOST_IPCC + "/v2/api_auth", entity, "application/json", response);
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
        }

        LogUtil.e(CHAT_CALL_HOST_IPCC + "/v2/api_auth  --- params: " + apiParam.toString());
    }


    private static final String OtpOutside = "OtpOutside";

    public void addOtpOutsideApiParam(HashMap<String, Object> apiParams, String otp) {
        if (apiParams != null) {
            apiParams.put(OtpOutside, otp);
        }
    }

    private Map<String, Object> getBaseApiParam(Context context, Map<String, Object> apiParams) {
        if (apiParams == null) {
            apiParams = new HashMap<>();
        }
        apiParams.put("isdn", getIsdn());
        apiParams.put("language", lang);
        return apiParams;
    }

    private Map<String, Object> createPostParams(Map<String, Object> apiParams, String apiCode) {
        Map<String, Object> params = new HashMap<>();
        params.put("wsCode", apiCode);
        if (userLogin != null) {
            params.put("sessionId", userLogin.sessionId);
            params.put("token", userLogin.token);
            params.put("language", lang);
        } else {
            LogUtil.e(TAG, "createPostParams failed, userLogin null");
        }
        if (apiParams.containsKey(OtpOutside)) {
            String otp = (String) apiParams.remove(OtpOutside);
            params.put("otp", otp);
        }
        params.put("wsRequest", apiParams);
        return params;
    }

    private String checkRequiredParams(Map<String, Object> params, String[] requiredParams) {
        if (params == null) {
            return "Missing params";
        }
        for (String p : requiredParams) {
            if (!params.containsKey(p)) {
                return "Missing param " + p;
            }
        }
        return null;
    }

    private String[] getRequiredParams(String apiCode) {
        switch (apiCode) {
            case WS_GET_SERVICES:
            case WS_GET_CURRENT_USED_SERVICES:
            case WS_GET_CURRENT_USED_SUB_SERVICES:
            case WS_GET_SUB_MAIN_INFO:
            case WS_GET_HOT_NEWS:
            case WS_GET_ALL_DATA_PACKAGES:
            case WS_REGISTER_BUY_ISDN:
            case WS_VALIDATE_REDEEM:
            case WS_DO_REDEEM:
                return new String[]{"isdn", "language"};
            case WS_GET_SERVICES_BY_GROUP:
                return new String[]{"isdn", "language", "serviceGroupId"};
            case WS_GET_SERVICE_DETAIL:
                return new String[]{"isdn", "language", "serviceCode"};
            case WS_GET_SUBINFO:
            case WS_GET_SUB_ACCOUNT_INFO:
            case WS_GET_CALL_ACCOUNT_DETAIL:
            case WS_GET_ACCOUNT_DETAIL:
            case WS_GET_DATA_ACCOUNT_DETAIL:
            case WS_DO_LOCK_CALL_GO:
                return new String[]{"isdn", "language", "subType"};
            case WS_GET_CAREERS:
            case WS_GET_HOBBIES:
                return new String[]{"language"};
            case WS_UPDATE_SUB_INFO:
                return new String[]{"isdn", "jobId", "hobbyId", "email"};
            case WS_GET_NEAREST_STORES:
                return new String[]{"isdn", "language", "longitude", "latitude"};
            case WS_FIND_STORE_BY_ADDR:
                return new String[]{"isdn", "language", "longitude", "latitude", "provinceId", "districtId"};
            case WS_GET_PROVINCES:
                return new String[]{};
            case WS_GET_DISTRICTS:
                return new String[]{"provinceId"};
            case WS_GET_POSTAGE_INFO:
                return new String[]{"isdn", "language", "subType", "startDate", "endDate"};
            case WS_GET_POSTAGE_DETAIL_INFO:
                return new String[]{"isdn", "language", "subType", "startDate", "endDate", "postType", "sort"};
            case WS_GET_SERVICE_INFO:
                return new String[]{"isdn", "language", "subType", "serviceCode"};
            case WS_DO_ACTION_SERVICE:
                return new String[]{"isdn", "language", "actionType", "serviceCode"};
            case WS_DO_ACTION_BY_WEBSERVICE:
                return new String[]{"isdn", "language", "actionType", "webServiceCode"};
            case WS_FIND_ISDN_TO_BUY:
                return new String[]{"subType", "minPrice", "maxPrice", "isdnPattern", "pageSize", "pageNum"};
            case WS_GET_DATA_VOLUME_LEVEL_TO_BUY:
                return new String[]{"isdn", "packageCode"};
            case WS_GET_DATA_PACKAGE_INFO:
                return new String[]{"isdn", "packageCode", "language"};
            case WS_DO_BUY_DATA:
                return new String[]{"isdn", "subType", "packageCode", "price", "volume"};
            case WS_GET_PROMOTION_INFO:
                return new String[]{"isdn", "language", "packageCode"};
            case WS_GET_NEW_PROMOTIONS:
                return new String[]{"isdn", "language", "pageSize", "pageNum"};
            case WS_GET_ALL_APPS:
                return new String[]{"language"};
            case WS_GET_NEWS_DETAIL:
                return new String[]{"newsId"};
            case WS_DO_RECHARGE:
                return new String[]{"isdn", "language", "desIsdn", "serial"};
            case WS_DO_SEND_EMAIL:
                return new String[]{"isdn", "fromAddr", "subject", "content", "attachment", "fileName"};
            case WS_GET_NEWS:
                return new String[]{"isdn", "language", "pageSize", "pageNum"};
            case WS_GET_CHANGE_POSTAGE_RULE:
                return new String[]{};
            case WS_DO_BUY_ISDN:
                return new String[]{"isdn", "language", "sub_id", "isdnToBuy", "serialOfKit", "isdnOfKit", "price"};
            case WS_GET_EXPERIENCE_LINK_3G_4G:
                return new String[]{"language"};
            case WS_GET_ADVANCED_SERVICE_INFO:
                return new String[]{"isdn", "language", "serviceCode"};
            case WS_GET_SERVICE_FUNCTION_PAGE:
                return new String[]{"isdn", "language", "serviceCode", "functionPageCode"};
            case WS_DO_CHANGE_SIM:
                return new String[]{"isdn", "language", "subType", "serialOfKit", "isdnOfKit"};
            case WS_SEARCH_NICE_NUMBER:
                return new String[]{"isdn", "language", "typeService", "typeNumber", "conditionNumber", "numberRecord"};
            case WS_RESERVE_NUMBER:
                return new String[]{"isdnFrm", "customerIsdn", "email", "language"};
            case WS_GENERATE_QR_CODE:
                return new String[]{};
            case WS_GET_LIST_NOTI:
                return new String[]{"isdn", "pageSize", "pageNum"};
            case WS_LOG_CLICK_NOTI:
                return new String[]{"isdn", "notificationId"};
            case WS_DO_LOGIN_BY_GET_CODE:
                return new String[]{"isdn", "language"};
            case WS_VERIFY_BY_CODE:
                return new String[]{"isdn", "code", "token", "language"};
            case WS_GET_PROFILE_BY_ISDN:
                return new String[]{"isdn", "token"};
            case WS_UPDATE_INFOR_BY_ISDN:
                return new String[]{"isdn", "name", "idType", "idNo", "sex", "birthDay", "address", "lstImage"};
            case WS_GET_APP_COFIG:
                return new String[]{};
            case WS_DETACH_IP:
                return new String[]{"ip"};
            case WS_UPDATE_DEVICE_TOKEN:
                return new String[]{"isdn", "token", "os"};
            case WS_CHECK_FORCE_UPDATE_SERVICE:
                return new String[]{"version"};
            default:
                throw new IllegalArgumentException("Unknown apiCode " + apiCode);
        }
    }

    private String createXMLRequest(String wsName, Map<String, Object> params) {
        return createXMLRequest(wsName, params, false);
    }

    private String createXMLRequest(String wsName, Map<String, Object> params, boolean insertToRawData) {
        StringBuilder envelope = new StringBuilder(
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.bccsgw.viettel.com/\" >");
        envelope.append("<soapenv:Header/>");
        envelope.append("<soapenv:Body>");
        envelope.append("<web:gwOperation>");
        envelope.append("<Input>");
        envelope.append("<wscode>" + wsName + "</wscode>");
        envelope.append("<username>" + Const.MVIP_GW_USER + "</username>");
        envelope.append("<password>" + Const.MVIP_GW_PASS + "</password>");

        if (params != null) {
            String paramsString = convertMap2String(params);

            if (insertToRawData) {
                envelope.append("<rawData><![CDATA[");
                envelope.append("<ws:" + wsName + ">");
                envelope.append(paramsString);
                envelope.append("</ws:" + wsName + ">");
                envelope.append("]]></rawData>");
            } else {
                envelope.append(paramsString);
            }
        }

        envelope.append("</Input>");
        envelope.append("</web:gwOperation>");
        envelope.append("</soapenv:Body>");
        envelope.append("</soapenv:Envelope>");
        return envelope.toString();
    }

    private String convertMap2String(Map<String, Object> objectMap) {
        String result = "";

        for (Map.Entry<String, Object> entry : objectMap.entrySet()) {
            String k = entry.getKey();
            Object v = entry.getValue();
            result = result.concat(
                    String.format("<param name=\"%s\" value=\"%s\"/>", k, v.toString())
            );
        }

        return result;
    }
}

