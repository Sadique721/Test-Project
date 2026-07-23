package com.savbill.notification.snmp;

public class OIDConstant {
    private OIDConstant() {
    	
    }
    
    public static final String PDU_RESET = ".1.3.6.1.4.1.58278.1.6.14";
    //EmailConfig
    public static final String GET_EMAIL_CONFIG_LIST_SUCCESS_OID = ".1.3.6.1.4.1.58278.1.6.2";
    public static final String GET_EMAIL_CONFIG_LIST_FAILURE_OID = ".1.3.6.1.4.1.58278.1.6.3";
    public static final String UPDATE_EMAIL_CONFIG_SUCCESS_OID = ".1.3.6.1.4.1.58278.1.6.4";
    public static final String UPDATE_EMAIL_CONFIG_FAILURE_OID = ".1.3.6.1.4.1.58278.1.6.5";
    public static final String CREATE_EMAIL_CONFIG_SUCCESS_OID = ".1.3.6.1.4.1.58278.1.6.6";
    public static final String CREATE_EMAIL_CONFIG_FAILURE_OID = ".1.3.6.1.4.1.58278.1.6.7";
  
    //SMSConfig
    public static final String GET_SMS_CONFIG_LIST_SUCCESS_OID = ".1.3.6.1.4.1.58278.1.6.8";
    public static final String GET_SMS_CONFIG_LIST_FAILURE_OID = ".1.3.6.1.4.1.58278.1.6.9";
    public static final String UPDATE_SMS_CONFIG_SUCCESS_OID = ".1.3.6.1.4.1.58278.1.6.10";
    public static final String UPDATE_SMS_CONFIG_FAILURE_OID = ".1.3.6.1.4.1.58278.1.6.11";
    public static final String CREATE_SMS_CONFIG_SUCCESS_OID = ".1.3.6.1.4.1.58278.1.6.12";
    public static final String CREATE_SMS_CONFIG_FAILURE_OID = ".1.3.6.1.4.1.58278.1.6.13";
    
    //Email
    public static final String SEND_EMAIL_SUCCESS_OID = ".1.3.6.1.4.1.58278.1.6.14";
    public static final String SEND_EMAIL_FAILURE_OID = ".1.3.6.1.4.1.58278.1.6.15";
    public static final String GET_EMAIL_LIST_SUCCESS_OID = ".1.3.6.1.4.1.58278.1.6.16";
    public static final String GET_EMAIL_LIST_FAILURE_OID = ".1.3.6.1.4.1.58278.1.6.17";
    public static final String GET_EMAIL_BY_ID_SUCCESS_OID = ".1.3.6.1.4.1.58278.1.6.18";
    public static final String GET_EMAIL_BY_ID_FAILURE_OID = ".1.3.6.1.4.1.58278.1.6.19";
    public static final String GET_EMAIL_BY_SOURCENAME_SUCCESS_OID = ".1.3.6.1.4.1.58278.1.6.20";
    public static final String GET_EMAIL_BY_SOURCENAME_FAILURE_OID = ".1.3.6.1.4.1.58278.1.6.21";
    public static final String CREATE_EMAIL_SUCCESS_OID = ".1.3.6.1.4.1.58278.1.6.22";
    public static final String CREATE_EMAIL_FAILURE_OID = ".1.3.6.1.4.1.58278.1.6.23";
    public static final String UPDATE_EMAIL_SUCCESS_OID = ".1.3.6.1.4.1.58278.1.6.24";
    public static final String UPDATE_EMAIL_FAILURE_OID = ".1.3.6.1.4.1.58278.1.6.25";
    public static final String DELETE_EMAIL_SUCCESS_OID = ".1.3.6.1.4.1.58278.1.6.26";
    public static final String DELETE_EMAIL_FAILURE_OID = ".1.3.6.1.4.1.58278.1.6.27";

    //SMS
    public static final String SEND_SMS_SUCCESS_OID = ".1.3.6.1.4.1.58278.1.6.28";
    public static final String SEND_SMS_FAILURE_OID = ".1.3.6.1.4.1.58278.1.6.29";
    public static final String GET_SMS_LIST_SUCCESS_OID = ".1.3.6.1.4.1.58278.1.6.30";
    public static final String GET_SMS_LIST_FAILURE_OID = ".1.3.6.1.4.1.58278.1.6.31";
    public static final String GET_SMS_BY_ID_SUCCESS_OID = ".1.3.6.1.4.1.58278.1.6.32";
    public static final String GET_SMS_BY_ID_FAILURE_OID = ".1.3.6.1.4.1.58278.1.6.33";
    public static final String GET_SMS_BY_SOURCENAME_SUCCESS_OID = ".1.3.6.1.4.1.58278.1.6.34";
    public static final String GET_SMS_BY_SOURCENAME_FAILURE_OID = ".1.3.6.1.4.1.58278.1.6.35";
    public static final String CREATE_SMS_SUCCESS_OID = ".1.3.6.1.4.1.58278.1.6.36";
    public static final String CREATE_SMS_FAILURE_OID = ".1.3.6.1.4.1.58278.1.6.37";
    public static final String UPDATE_SMS_SUCCESS_OID = ".1.3.6.1.4.1.58278.1.6.38";
    public static final String UPDATE_SMS_FAILURE_OID = ".1.3.6.1.4.1.58278.1.6.39";
    public static final String DELETE_SMS_SUCCESS_OID = ".1.3.6.1.4.1.58278.1.6.40";
    public static final String DELETE_SMS_FAILURE_OID = ".1.3.6.1.4.1.58278.1.6.41";

    //EMAIL config
    public static final String TOTAL_GET_EMAIL_CONFIG_LIST_OID=".1.3.6.1.4.1.58278.1.6.42";
    public static final String TOTAL_CREATE_EMAIL_CONFIG_OID=".1.3.6.1.4.1.58278.1.6.43";
    public static final String TOTAL_UPDATE_EMAIL_CONFIG_OID=".1.3.6.1.4.1.58278.1.6.44";
    public static final String UPDATE_EMAIL_CONFIG_PASSWORD_SUCCESS_OID=".1.3.6.1.4.1.58278.1.6.45";
    public static final String UPDATE_EMAIL_CONFIG_PASSWORD_FAILURE_OID=".1.3.6.1.4.1.58278.1.6.46";
    public static final String TOTAL_UPDATE_EMAIL_CONFIG_PASSWORD_OID=".1.3.6.1.4.1.58278.1.6.47";
    public static final String TOTAL_GET_EMAIL_BY_ID_OID=".1.3.6.1.4.1.58278.1.6.48";

    //SMS config
    public static final String TOTAL_GET_SMS_CONFIG_LIST_OID=".1.3.6.1.4.1.58278.1.6.49";
    public static final String TOTAL_CREATE_SMS_CONFIG_OID=".1.3.6.1.4.1.58278.1.6.50";
    public static final String TOTAL_UPDATE_SMS_CONFIG_OID=".1.3.6.1.4.1.58278.1.6.51";
    public static final String TOTAL_GET_SMS_BY_ID_OID=".1.3.6.1.4.1.58278.1.6.52";

    //SMS
    public static final String TOTAL_GET_SMS_LIST_OID=".1.3.6.1.4.1.58278.1.6.53";
    public static final String TOTAL_CREATE_SMS_LIST_OID=".1.3.6.1.4.1.58278.1.6.54";
    public static final String TOTAL_UPDATE_SMS_LIST_OID=".1.3.6.1.4.1.58278.1.6.55";
    public static final String TOTAL_DELETE_SMS_LIST_OID=".1.3.6.1.4.1.58278.1.6.56";
    public static final String TOTAL_GET_SMS_LIST_BY_SOURCENAME_OID=".1.3.6.1.4.1.58278.1.6.57";
    public static final String TOTAL_SEND_SMS_OID=".1.3.6.1.4.1.58278.1.6.58";

    //Email
    public static final String TOTAL_GET_EMAIL_LIST_OID=".1.3.6.1.4.1.58278.1.6.59";
    public static final String TOTAL_CREATE_EMAIL_LIST_OID=".1.3.6.1.4.1.58278.1.6.60";
    public static final String TOTAL_UPDATE_EMAIL_LIST_OID=".1.3.6.1.4.1.58278.1.6.61";
    public static final String TOTAL_DELETE_EMAIL_LIST_OID=".1.3.6.1.4.1.58278.1.6.62";
    public static final String TOTAL_GET_EMAIL_LIST_BY_SOURCENAME_OID=".1.3.6.1.4.1.58278.1.6.63";
    public static final String TOTAL_SEND_EMAIL_OID=".1.3.6.1.4.1.58278.1.6.64";



}

