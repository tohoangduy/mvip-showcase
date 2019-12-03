package com.mq.myvtg.util;

public final class Const {
    public static final String MVIP_GW_USER = "addba2e908c412ca";
    public static final String MVIP_GW_PASS = "523cc9765677493c4a2fe6ef8b80d222";
    public static final String LANG_KH = "km";
    public static final String LANG_EN = "en";

    public static final class LANGUAGES {
        public static final String KHMER = "ភាសាខ្មែរ";
        public static final String ENLISH = "English";
    }

    public static final String FONT_ROBOTO_BLACK = "Roboto-Black.ttf";
    public static final String FONT_ROBOTO_BOLD = "Roboto-Bold.ttf";
    public static final String FONT_ROBOTO_MEDIUM = "Roboto-Medium.ttf";
    public static final String FONT_ROBOTO_REGULAR = "Roboto-Regular.ttf";
    public static final String FONT_ROBOTO_ITALIC = "Roboto-Italic.ttf";
    public static final String FONT_ROBOTO_LIGHT = "Roboto-Light.ttf";
    public static final String FONT_ROBOTO_LIGHTITALIC = "Roboto-LightItalic.ttf";
    public static final String FONT_ROBOTO_THIN = "Roboto-Thin.ttf";
    public static final String FONT_PHET = "Phetsarath_OT.ttf";


    public static final double INVALID_LAT = 11.562108 ;
    public static final double INVALID_LNG = 104.888535;
    public static final float MAP_ZOOM = 16.0f;
    public static final int CACHE_EXPIRATION = 24 * 60 * 60 * 1000;// 24h

    // key

    public static final String KEY_SKIP_INTRO = "KEY_SKIP_INTRO";
    public static final String KEY_CURRENT_LANG = "KEY_CURRENT_LANG";
    public static final String KEY_AUTO_LOGIN = "KEY_AUTO_LOGIN";
    public static final String KEY_SKIP_SPLASH = "KEY_SKIP_SPLASH";
    public static final String KEY_LOG_OUT = "KEY_LOG_OUT";
    public static final String KEY_PROVINCE = "KEY_PROVINCE";
    public static final String KEY_DISTRICT = "KEY_DISTRICT";
    public static final String KEY_MODEL_USER_LOGIN = "KEY_MODEL_USER_LOGIN";
    public static final String KEY_MODEL_USER_LOGIN_EXPIRED_AT = "KEY_MODEL_USER_LOGIN_EXPIRED_AT";
    public static final String KEY_MODEL_SUB_INFO = "KEY_MODEL_SUB_INFO";
    public static final String KEY_USER_AVATAR = "KEY_USER_AVATAR";
    public static final String KEY_CHANGE_DATA_PKG_SUCCESS = "KEY_CHANGE_DATA_PKG_SUCCESS";
    public static final String KEY_BUY_ISDN_SUCCESS = "KEY_BUY_ISDN_SUCCESS";
    public static final String KEY_CURRENT_USER_NAME = "KEY_CURRENT_USER_NAME";
    public static final String SMS_SENDER = "1111";
    public static final String KEY_EDIT_INFO_SUCCESS = "KEY_EDIT_INFO_SUCCESS";
    // scheme
    public static final String GOOGLE_PLAY_STORE = "market://details?id=%s";

    //noti
    public static final String KEY_RECEIVER_CODE_NOTI = "KEY_RECEIVER_CODE_NOTI";
    public static final String KEY_DEVICE_TOKEN = "KEY_DEVICE_TOKEN";
    public static final String KEY_SERVICE_CODE = "KEY_SERVICE_CODE";
    public static final String KEY_NOTI_TYPE = "KEY_NOTI_TYPE";
    public static final String KEY_NOTI_BODY = "KEY_NOTI_BODY";
    public static final String KEY_NOTI_ID = "KEY_NOTI_ID";
    public static final String KEY_NOTI_REDEEM_TYPE = "KEY_NOTI_ID";

    public static final String KEY_SERVICE_CODE_BG = "serviceCode";
    public static final String KEY_NOTI_TYPE_BG = "notificationType";
    public static final String KEY_NOTI_ID_BG = "notificationId";
    public static final String KEY_NOTI_IMAGE_BG = "image";

    //format date
    public static final String FORMAT_TYPE_1 = "yyyy-MM-dd HH:mm";
    public static final String FORMAT_TYPE_2 = "yyyy/MM/dd";


    // request code
    public static final int REQUEST_CODE_CONTACTS = 1;
    public static final int REQUEST_CODE_CAMERA = 2;
    public static final int REQUEST_CODE_GALLERY = 3;


    public class ObsNotiId {
        public static final int HIDE_TABBAR = 1;
        public static final int SHOW_TABBAR = 2;
        public static final int REQUEST_PERMISSIONS = 3;
        public static final int CHANGE_LANGUAGE = 4;
        public static final int CHANGE_TAB = 5;
        public static final int REFRESH_HOME_INFO = 6;
        public static final int REFRESH_AVATAR = 7;
    }

    // functions name
    public static final String CHARGE_HIS = "charge_his";
    public static final String RECHARGE = "recharge";
    public static final String DATA_PURCHASE = "data_purchase";
    public static final String ISHARE = "i_share";
    public static final String AIRTIME = "airtime";
    public static final String DATA_PLANS = "data_plans";
    public static final String FIND_A_STORE = "find_a_store";
    public static final String NUMBER_PURCHASE = "number_purchase";
    public static final String CHANGE_NUMBER = "change_number";
    public static final String RESTORE_NUMBER = "restore_number";
    public static final String CHANGE_SIM = "change_sim";
    public static final String LOCK_SIM = "lock_sim";


    // validation for signup/login

    public static final int OTP_LENGTH = 6;
    public static final int PASSWORD_LENGTH = 9;
    public static final int MAX_PHONE_NUMBER_LENGTH = 14;
    public static final int SERIAL_NUMBER_LENGTH = 32;//19
    public static final int SCRATCH_CODE_LENGTH = 32;//13
    public static final int EMAIL_LENGTH = 100;

    // use for phone number
    public static final String[] UNUSED_PHONE_CHARS = {"+", "-", " "};
    public static final String[] COUNTRY_CODES = {"855", "00855","+855","0"};
    public static final String REPLACE_CODES = "";

    public static final Boolean SUPPORT_MANY_RECHARGE_METHODS = false;
    public static final int MIN_SDK_TO_CHANGE_FONT = 15;

    public static final String MARKET_NAME_ALL = "all";
    public static final String MARKET_NAME_UNITEL = "unitel";
    public static final String MARKET_NAME_HALOTEL = "halotel";
    public static final String MARKET_NAME_NATCOM = "natcom";
    public static final String MARKET_NAME_METFONE = "metfone";
    public static final String MARKET_NAME_LUMITEL = "lumitel";
    public static final String MARKET_NAME_TIMORE = "timore";
    public static final String MARKET_NAME_MOVITEL = "movitel";

    public static final String MARKET_NAME = MARKET_NAME_METFONE;

    //sign up login flow
    public static final boolean ENCRYPT_PW_LOGIN = false;
    public static final boolean IS_LOGIN_AFTER_SIGN_UP = false;
    public static final boolean IS_AUTO_LOGIN_IN_3G_NETWORK = true;

    public static final boolean ENABLE_SSL_PINNING = false;
    public static final boolean IS_SCAN_CODE_ENABLE=false;
    public static final boolean ENABLE_IPCC = false;
    public static final String APP_VERSION = "1.1.0";
    public static final String APP_VERSION_CHECK_UPDATE = APP_VERSION.substring(0, 3);

    public static final int GO_TO_SIGN_UP = 101;
    public static final String HOTLINE_NUMBER = "0976097097";
}
