package com.simon.cient.util;

import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

/**
 * Created by simon on 2016/8/16.
 */
public class ServerContext {
    public static final String MSG = "message";
    public static final String STATUS_CODE = "status";
    public static final String DATA = "data";
    public static final String USER_INFO = "userInfo";
    public static final String DEV_MSG = "developerMessage";
    public static final String MORE_INFO = "moreInfo";
    public static final String IP="120.25.152.172";

    public static final Integer SIGN_UP_STATUS = 1;
    public static final Integer SIGN_IN_STATUS = 2;
    public static final Integer SIGN_OUT_STATUS = 3;

    public static final String DAYU_URL_SANDBOX = "http://gw.api.tbsandbox.com/router/rest";
    public static final String DAYU_URL_REAL = "http://gw.api.taobao.com/router/rest";
    public static final String DAYU_APP_KEY = "23460263";
    public static final String DAYU_APP_SECRET = "b9deb39cba2cc72249b535f9435b938d";

    public static final String JIGUANG_APP_KEY = "2cb1b6f5ee6c596abe813e49";
    public static final String JIGUANG_MASTER_SECRET = "aa81152ecac5776f2ff6db91";

    public static final Integer AUDIT_RESULT_WAIT = 0;
    public static final Integer AUDIT_RESULT_SUCCESS = 1;
    public static final Integer AUDIT_RESULT_REFUSED = 2;
    public static final Integer AUDIT_RESULT_RESUBMIT = 3;

}
