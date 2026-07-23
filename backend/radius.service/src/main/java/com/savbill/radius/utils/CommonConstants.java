package com.savbill.radius.utils;

public class CommonConstants {

    public static final Integer DB_PAGE_SIZE = 10;
    public static final Integer DISP_PAGE_SIZE = 10;

    public static final Integer SORT_ORDER_ASC = 1;
    public static final Integer SORT_ORDER_DESC = 2;

    public static final String USER_ADMIN = "Admin";
    public static final String TOTAL = "TOTAL";
    public static final String DOWNLOAD = "DOWNLOAD";
    public static final String UPLOAD = "UPLOAD";


    public static String PLAN_STAGE_ACTIVE = "ACTIVE";
    public static String PLAN_STAGE_FUTURE = "FUTURE";
    public static String PLAN_STAGE_EXPIRED = "EXPIRED";

    public static String PLAN_INACTIVE = "InActive";
    public static String CUST_SUSPEND = "Suspend";

    public static String CUST_ACTIVE = "Active";

    public static final String CUST_INACTIVE = "InActive";

    public static String AUTHENTICATION_TYPE_INDEPENDENT = "independent";

    public static String AUTHENTICATION_TYPE_DEPENDENT = "dependent";

    public static final String ANONYMOUS_USER = "anonymoususer";
    public static final String DEVICE_DRIVER_SAVBILL = "Savbill BSS";
    public static final String CUST_IP_MAPPING_LIST = "custIpMappingList";

    public static final String RADIUS_SYSTEM_CONFIGURATION_NOTIFICATION = "radius_quota_notification_configuration";

    public static final String ACTIVE_STATUS = "Active";


    public static final String AUTH_TYPE_EAP_TLS = "EAP-TLS";
    public static final String AUTH_TYPE_EAP_TTLS = "EAP-TTLS";
    public static final String AUTH_TYPE_PAP = "PAP";
    public static final String AUTH_TYPE_CHAP = "CHAP";

    //Nokia attributes
    public static final String FRAMED_IP_ADDRESS_DOWN = "framed-ip-address-down";
    public static final String ALC_IPV6_ADDRESS_DOWN = "alc-ipv6-address-down";
    public static final String DELEGATED_IPV6_PREFIX_DOWN = "delegated-ipv6-prefix-down";


    public interface EVENTCONSTANTS {
        public static final String VOLUME_BOOSTER_EXPIRE = "VOLUME_BOOSTER_EXPIRE";
        public static final String QUOTA_BOOSTER_EXPIRE = "QUOTA_BOOSTER_EXPIRE";
        public static final String CHANGE_PLAN = "CHANGE_PLAN";

        public static final String PLAN_EXPIRE = "PLAN_EXPIRE";

        public static final String NEW_VOLUME_BOOSTER = "NEW_VOLUME_BOOSTER";
        public static final String CUSTOMER_STATUS_ACTIVE = "CUSTOMER_STATUS_ACTIVE";
        public static final String CUSTOMER_STATUS_INACTIVE = "CUSTOMER_STATUS_INACTIVE";
        public static final String CUSTOMER_STATUS_SUSPEND = "CUSTOMER_STATUS_SUSPEND";
        public static final String CUSTOMER_STATUS_TERMINATE = "CUSTOMER_STATUS_TERMINATE";
        public static final String CUSTOMER_LOGOUT = "CUSTOMER_LOGOUT";

        public static final String UPDATE_USAGE = "UPDATE_USAGE";

        public static final String QUOTA_RESET = "QUOTA_RESET";
        public static final String RE_AUTH = "RE_AUTH";
        public static final String START_COA = "START_COA";
        public static final String PLAN_QOS_UPDATE = "PLAN_QOS_UPDATE";
        public static final String MAC_REMOVE = "MAC_REMOVE";
        public static final String CUSTOMER_STATUS_HOLD = "SERVICE_STATUS_HOLD";

    }

    public interface CoaDmResonContant {
        public static final String PLAN_EXPIRE = "PLAN_EXPIRE";
        public static final String PLAN_QUOTA_EXHAUST = "PLAN_QUOTA_EXHAUST";
        public static final String TIME_BASE_POLICY_CHANGE = "TIME_BASE_POLICY_CHANGE";
        public static final String QUOTA_RESET = "QUOTA_RESET";

        public static final String TERMINATE_SESSION = "TERMINATE_SESSION";
    }

    public interface AuthResponseEvent {
        public static final String CONCURRENCY_FAIL = "CONCURRENCY_FAIL";
        public static final String QUOTA_CONSUMED = "QUOTA_CONSUMED";
        public static final String PLAN_EXPIRE = "PLAN_EXPIRE";
        public static final String WRONG_PASSWORD = "WRONG_PASSWORD";
        public static final String WRONG_USERNAME = "WRONG_USERNAME";
        public static final String DYNAMIC_VALIDATION_FAIL = "DYNAMIC_VALIDATION_FAIL";
    }

    public interface SoapConstant {
        public static final String IP_NOT_AVAILABLE_IN_SESSION = "IP is not available in session table";
        public static final String USER_NOT_AVAILABLE = "Username Not exists :";
        public static final String CUSTOMERQUATASUCCESS = "Quota Successfully found for Customer:";
        public static final String CUSTOMERQUATANOTFOUND = "Quota NOT found for Customer:";

    }
}
