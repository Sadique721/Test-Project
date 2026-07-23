package com.savbill.radius.aaa.constant;

public class AAAConstant {
    private AAAConstant() {

    }

    public static final String SERVERCACHING = "serverConfig";
    // public static final String CLIENTCACHING = "clientConfig";
    // public static final String CLIENTGROUPCACHING = "clientGroupConfig";
    // public static final String CLIENTREPLY = "clientReply";
    // public static final String GROUPPOLICY = "grouppolicy";
    // public static final String ACCTPROFILECONFIG = "acctprofile";
    // public static final String AUTHPROFILECONFIG = "authprofile";
    // public static final String SERVERATTRIBUTE = "serverAttribe";
    //public static final String RADIUSPOLICY = "radiusPolicy";
    // public static final String PROXYSERVER = "proxyServer";
    //public static final String COADMPROFILECONFIG = "coadmprofile";

    public static final String PERMISSIONS_CACHE = "permissionsCache";

    public static final String SERVERCACHING_CACHE = "serverConfigCache";
    public static final String AUTHPROFILECONFIG_CACHE = "authProfileCache";
    public static final String ACCTPROFILECONFIG_CACHE = "acctProfileCache";
    public static final String VLANMANAGEMENT_CACHE = "vlanManagementCache";
    public static final String DYNAAUTHPROFILECONFIG_CACHE = "dynaauthProfileCache";
    public static final String DBMAPPING_CACHE = "dbMappingCache";
    public static final String FAULTY_MAC_CACHE = "faultyMacCache";
    public static final String COADMPROFILECONFIGCACHE = "coadmprofileCache";
    public static final String CLIENTDETAILCACHE = "clientdetailCache";
    public static final String PROXYCACHE = "proxyCache";

    public static final String DBNAME = "jdbc:apache:commons:dbcp:maindatabase";
    public static final String POOLNAME = "maindatabase";
    public static final String RADIUSNAME = "radiusname";
    public static final String DBNAMECOL = "dbcolumnname";

    public static final String AUTHPORT = "authPort";
    public static final String ACCTPORT = "acctPort";
    public static final String SERVERIP = "serverip";
    public static final String SHAREDKEY = "sharedkey";
    public static final String CLIENTIP = "clientip";
    public static final String CLIENTID = "clientid";
    public static final String CLIENTTIMEOUT = "timeout";
    public static final String ALLOWALL = "0.0.0.0";
    public static final String WEBSERVICEIP = "webserviceIP";
    public static final String WEBSERVICEPORT = "webservicePort";
    public static final String ACCTPROFILECACHE = "acctprofilecache";


    public static final String REPLYMSG = "Authentication Success";
    public static final String REPLYMSG_USERNOTFOUND = "User/MAC Not Found";
    public static final String REPLYMSG_PASSWORDFAIL = "Password Validation Fail.";
    public static final String REPLYMSG_NOTACTIVE = "Customer Account is :";
    public static final String REPLYMSG_CUSTOMER_EXPIRED = "Customer is Expired";
    public static final String REPLYMSG_PLANEXPIRED = "Customer dont have any Valid Plan";
    public static final String REPLYMSG_BASE_PLANEXPIRED = "Customer Base Plan is Expired";
    public static final String REPLYMSG_BLOCKER = "Customer is Blocked/Dunned";
    public static final String REPLYMSG_QUOTA = "Quota Consumed";
    public static final String REPLYMSG_CUSTOMER_QUOTA = "Customer Quota is Exhausted";
    public static final String REPLYMSG_TIME_QUOTA = "Time Quota Consumed";
    public static final String REPLYMSG_VOLUME_QUOTA = "Volume Quota Consumed";

    public static final String CONCURRENCY_CHECK_FAIL = "Concurrency Check Fail";


    public static final String REPLYMSG_UNKNOWN = "Internal Error";


    public static final String STATUS = "Active";

    public static final String FAILLIMITCOUNT = "FAILLIMITCOUNT";
    public static final String PASSWORDCHANGEVALIDITY = "PASSWORDCHANGEVALIDITY";
    public static final String PROFILEMANDATORY = "PROFILEMANDATORY";


    public static final int ACCESS_REQUEST = 1;
    public static final int ACCESS_ACCEPT = 2;
    public static final int ACCESS_REJECT = 3;
    public static final int ACCOUNTING_REQUEST = 4;
    public static final int ACCOUNTING_RESPONSE = 5;
    public static final int ACCOUNTING_STATUS = 6;
    public static final int PASSWORD_REQUEST = 7;
    public static final int PASSWORD_ACCEPT = 8;
    public static final int PASSWORD_REJECT = 9;
    public static final int ACCOUNTING_MESSAGE = 10;
    public static final int ACCESS_CHALLENGE = 11;
    public static final int STATUS_SERVER = 12;
    public static final int STATUS_CLIENT = 13;
    public static final int DISCONNECT_REQUEST = 40;
    public static final int DISCONNECT_ACK = 41;
    public static final int DISCONNECT_NAK = 42;
    public static final int COA_REQUEST = 43;
    public static final int COA_ACK = 44;
    public static final int COA_NAK = 45;
    public static final int STATUS_REQUEST = 46;
    public static final int STATUS_ACCEPT = 47;
    public static final int STATUS_REJECT = 48;
    public static final int RESERVED = 255;


    public static final String CDRID = "CDRID";
    public static final String USER_NAME = "USER_NAME";
    public static final String NAS_IP_ADDRESS = "NAS_IP_ADDRESS";
    public static final String NAS_PORT = "NAS_PORT";
    public static final String SERVICE_TYPE = "SERVICE_TYPE";
    public static final String FRAMED_PROTOCOL = "FRAMED_PROTOCOL";
    public static final String FRAMED_IP_ADDRESS = "FRAMED_IP_ADDRESS";
    public static final String FRAMED_IP_NETMASK = "FRAMED_IP_NETMASK";
    public static final String FRAMED_ROUTING = "FRAMED_ROUTING";
    public static final String FILTER_ID = "FILTER_ID";
    public static final String FRAMED_MTU = "FRAMED_MTU";
    public static final String FRAMED_COMPRESSION = "FRAMED_COMPRESSION";
    public static final String LOGIN_IP_HOST = "LOGIN_IP_HOST";
    public static final String LOGIN_SERVICE = "LOGIN_SERVICE";
    public static final String LOGIN_TCP_PORT = "LOGIN_TCP_PORT";
    public static final String CALLBACK_NUMBER = "CALLBACK_NUMBER";
    public static final String CALLBACK_ID = "CALLBACK_ID";
    public static final String FRAMED_ROUTE = "FRAMED_ROUTE";
    public static final String FRAMED_IPX_NETWORK = "FRAMED_IPX_NETWORK";
    public static final String CLASSRAD = "CLASSRAD";
    public static final String VENDOR_SPECIFIC = "VENDOR_SPECIFIC";
    public static final String SESSION_TIMEOUT = "SESSION_TIMEOUT";
    public static final String IDLE_TIMEOUT = "IDLE_TIMEOUT";
    public static final String TERMINATION_ACTION = "TERMINATION_ACTION";
    public static final String CALLED_STATION_ID = "CALLED_STATION_ID";
    public static final String CALLING_STATION_ID = "CALLING_STATION_ID";
    public static final String NAS_IDENTIFIER = "NAS_IDENTIFIER";
    public static final String PROXY_STATE = "PROXY_STATE";
    public static final String LOGIN_LAT_SERVICE = "LOGIN_LAT_SERVICE";
    public static final String LOGIN_LAT_NODE = "LOGIN_LAT_NODE";
    public static final String LOGIN_LAT_GROUP = "LOGIN_LAT_GROUP";
    public static final String FRAMED_APPLETALK_LINK = "FRAMED_APPLETALK_LINK";
    public static final String FRAMED_APPLETALK_NETWORK = "FRAMED_APPLETALK_NETWORK";
    public static final String FRAMED_APPLETALK_ZONE = "FRAMED_APPLETALK_ZONE";
    public static final String ACCT_STATUS_TYPE = "ACCT_STATUS_TYPE";
    public static final String ACCT_DELAY_TIME = "ACCT_DELAY_TIME";
    public static final String ACCT_INPUT_OCTETS = "ACCT_INPUT_OCTETS";
    public static final String ACCT_OUTPUT_OCTETS = "ACCT_OUTPUT_OCTETS";
    public static final String ACCT_SESSION_ID = "ACCT_SESSION_ID";
    public static final String ACCT_AUTHENTIC = "ACCT_AUTHENTIC";
    public static final String ACCT_SESSION_TIME = "ACCT_SESSION_TIME";
    public static final String ACCT_INPUT_PACKETS = "ACCT_INPUT_PACKETS";
    public static final String ACCT_OUTPUT_PACKETS = "ACCT_OUTPUT_PACKETS";
    public static final String ACCT_TERMINATE_CAUSE = "ACCT_TERMINATE_CAUSE";
    public static final String ACCT_MULTI_SESSION_ID = "ACCT_MULTI_SESSION_ID";
    public static final String ACCT_LINK_COUNT = "ACCT_LINK_COUNT";
    public static final String NAS_PORT_TYPE = "NAS_PORT_TYPE";
    public static final String PORT_LIMIT = "PORT_LIMIT";
    public static final String LOGIN_LAT_PORT = "LOGIN_LAT_PORT";
    public static final String ACCT_INPUT_GIGAWORDS = "ACCT_INPUT_GIGAWORDS";
    public static final String ACCT_OUTPUT_GIGAWORDS = "ACCT_OUTPUT_GIGAWORDS";
    public static final String EVENT_TIMESTAMP = "EVENT_TIMESTAMP";
    public static final String NAS_PORT_ID = "NAS_PORT_ID";
    public static final String CONNECT_INFO = "CONNECT_INFO";
    public static final String GROUPNAME = "GROUPNAME";
    public static final String PARAM_STR1 = "PARAM_STR1";
    public static final String PARAM_STR2 = "PARAM_STR2";
    public static final String PARAM_STR3 = "PARAM_STR3";
    public static final String PARAM_STR4 = "PARAM_STR4";
    public static final String PARAM_STR5 = "PARAM_STR5";
    public static final String PARAM_INT1 = "PARAM_INT1";
    public static final String PARAM_INT2 = "PARAM_INT2";
    public static final String PARAM_INT3 = "PARAM_INT3";
    public static final String PARAM_INT4 = "PARAM_INT4";
    public static final String PARAM_INT5 = "PARAM_INT5";
    public static final String PARAM_DATE0 = "PARAM_DATE0";
    public static final String PARAM_DATE1 = "PARAM_DATE1";
    public static final String PARAM_DATE2 = "PARAM_DATE2";
    public static final String CALL_START = "CALL_START";
    public static final String CALL_END = "CALL_END";
    public static final String CREATE_DATE = "CREATE_DATE";
    public static final String LAST_MODIFIED_DATE = "LAST_MODIFIED_DATE";
    public static final String CHANGE_PLAN = "CHANGE_PLAN";

    public static final String CONFIGURATION_CACHE = "configurationCache";

    public static final String SCHEDULAR_STATUS_SUCCESS = "success";

    public static final String SCHEDULAR_STATUS_FAILURE = "failure";
    public static final String SCHEDULAR_IPPOOLTHREAD_NAME = "IPPoolThread";

    public static final String SCHEDULAR_LIVE_USER_SESSION_PURGE_NAME = "LiveUserSessionPurge";

    public static final String SCHEDULAR_MAC_RETENTION_NAME = "MacRetention";

    public static final String SCHEDULAR_UPADATE_CUSTOMER_NAME = "UpdateCustomer";
}
