package com.savbill.integrationsystem.SOAPService.SoapConstant;

public class SoapConstants {

    public static final String NAMESPACE_URI = "http://api.act.com/";
    public static final String NAMESPACE_URI_NEW = "http://npm.redback.com";
    public static final String NAMESPACE_ELITECORE ="http://subscription.ws.nvsmx.elitecore.com/";
    public static final String NAMESPACE_URI_NEW_SES = "http://sessionmanagement.ws.nvsmx.elitecore.com/";
    public static final String WS = "/ws";
    public static final String XSI_NAMESPACE = "http://www.w3.org/2001/XMLSchema-instance";
    public static final String XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
    public static final String SOAP_ENCODING_STYLE_NAMESPACE = "http://schemas.xmlsoap.org/soap/encoding/";
    public static final String INPUT_MISSING = "INPUT PARAMETER MISSING. Reason: Identity parameter missing";

    public static final int SUCCESS_CODE = 200;

    public static final int BAD_REQUEST = 400;
    public static final int UNKNOWN_PARAM = 201;
    public static final int NOT_AVAILABLE = 407;
    public static final int EMPTY = 401;
    public static final int InvalidActivation = 402;
    public static final int NotAcceptable = 406;
    public static final int INTERNAL_ERROR = 500;
    public static final int NOT_FOUND = 404;
    public static final int INPUT_MISSING_CODE = 400;
    public static final int UNKNOWN = 203;
    public static final int NOT_PRESENT = 205;

    public static final int INPUT_NOT_MATCH_CODE= 410;
    public static final int USER_NOT_ALLOW_CODE= 412;

    public static final int NO_RECOED_UPDATE_CODE = 502;

    public static final int USER_NOT_AVAILABLE_IN_SPR_TABLE_CODE= 503;

    public static final int USER_DETAILS_NOT_FAOUND_IN_USAGE_TABLE_CODE= 215;
    public static final int STATUS_INACTIVE_CODE= 411;

    public static final int USAGE_NOT_RESECT_CODE= 416;
    public static final int USER_DOSE_NOT_EXIST_CODE = 301;
    public static final int NO_SESSION_FOUND_CODE = 323;
    public static final int IP_ADDRESS_NOT_VALID_FOR_LOGGEDIN_USER_CODE = 306;
    public static final int NO_OF_ALLOWED_CONCURRENT_USER_COUNT_HAS_BEEN_EXCEEDED_CODE = 305;
    public static final int NES_IDENTIFIER_DOSE_NOT_EXIST_FOR_GIVEN_IP_CODE = 311;
    public static final int USERNAME_AND_PASSWORD_DOES_MATCH_CODE = 303;
    public static final int GIVEN_USERNAME_IS_NOT_ACTIVE_IN_THE_SYSTEM_CODE = 304;
    public static final int SQL_EXCPTION_CODE = 304;
    public static final int VLAN_ID_AND_GEO_LOCATIONDOES_NOT_MATCH_CODE = 308;
    public static final int REMOTE_EXCEPTION_GENERATED_CODE = 308;
    public static final int DATA_NOT_FOUND_TBLMCUNCURRECTUSERS_TO_UPDATE_USER_CODE = 313;


    public static final String RESPONSEMESSAGE = "responseMessage";
    public static final String RESPONSECODE = "responseCode";
    public static final String REQUESTID = "requestId";


    public static final String  FAILURE ="FAILURE";
    public static final String SUCCESS = "SUCCESS";

    public static final String SERVICE_ID_NOT_AVAILABLE = "Service ID is not available in System";
    public static final String INVALID_ACTIVATION_WITH_STATUS_NULL = "Invalid Activation/Customer Status in Input(y/n/suspend) : null";

    public static final String INVALID_ACTIVATION_WITH_STATUS = "Invalid Activation/Customer Status in Input(y/n/suspend) :";
    public static final String INVALID_ACTIVATION_WITH_STATUS_H = "Invalid Activation/Customer Status in Input(y/n/suspend) : H";
    public static final String CUSTOMERSTATUS = "CUSTOMERSTATUS";

    public static final String PARAM1 = "PARAM1";
    public static final String PARAM6  = "PARAM6";

    public static final String PARAM2 = "PARAM2";

    public static final String CONCURRENTLOGINPOLICY = "CONCURRENTLOGINPOLICY";
    public static final String ADDITIONALPOLICY = "ADDITIONALPOLICY";
    public static final String PARAM4 = "PARAM4";
    public static final String Input_Username_is_Empty_or_null = "Input UserName is Empty or Null.";
    public static final String Input_ServiceId_is_Empty_or_null = "Input ServiceId is Empty or Null.";

    //addServiceToAccount
    public static final String NOT_UPDATED_RECORD_IN_SPR_TABLE_DUE_TO_TECHNICAL_ISSUES= "Not Updated Record in SPR table due to Technical Issue Via Product API[updateSubscriber]";
    public static final String CUSTOMER_UPDATED_IN_SPR_TABLE = "Customer updated in SPR table.";
    public  static final String NO_RECORD_FOUND_IN_SESSION_TABLE_FOR_GIVEN_IP= "No Records Found in session table for give IPAddress.";
    public static final long MVNOID= 2;

    //getAccountName
    public static final String UNKNOWN_USERNAME_FOUND_AGAINST_INPUT_IP_ADDRESS_FOR_LOGIN_SESSION ="UNKNOWN username found against Input Ip Address for Login Session.";
    public static final String ENDDATE_LESS_STARTDATE = "INVALID INPUT PARAMETER. Reason:Unable to subscribe package(FPTL30D200) for subscriber ID: ipoe@bngtest.com Reason: End time is less or equal to current time";
    public static final String UNKNOWN_DATA = "UNKNOWN";
    public static final String INPUT_USERNAME_UNKNOWN = "Input username is UNKNOWN";
    public static final String INPUT_USERNAME_NULL = "Username is Empty or Null";

    public static final String INPUT_USERNAME_NOT_AVAILABLE = "Username Not Available in Request";

    public static final String ADDACCOUNT_USERNAME_EMPTY = "Input Username Id is Empty or Null.";

    public static final String INPUT_PASSWORD_NOT_AVAILABLE = "Password Not Available in Request";
    public static final String USERNAME_IS_AVAILABLE = "UserName is available in SPR table";

    public static final String INVALID_LOCATION_LOCK = "Input Location Lock is not in Proper Format- 0:92=\"[val1,val2...]\" :Y";
    public static final String SUBACCT_NAME_IS_LOGGEDIN = "customer is loggedIn";
    public static final String SUBACCT_NAME_IS_NOT_LOGGEDIN = "customer is not loggedIn";
    public static final String INPUT_IP_ADDRESS_INVALID ="Input Ip Address is not valid.";
    public static final String INPUT_IP_ADDRESS_NULL= "Input IP Address is Empty or Null";

    public static final String SQL_EXCEPTION = "SQL Exception";
    public static final String CUSTOMER_NOT_AVAILABLE = "NOT FOUND. Reason: Subscriber not found";
    public static final String PLAN_DATA_NOT_AVAILABLE = "Plan data not available in system";


    public static final String NOTFOUND_REAUTH = "NOT FOUND. Unable to re-auth session(s) by subscriber Id :";
    public static final String REASON = " Reason: Session not found while performing Re-Auth for Id :";
    public static final String SELECT_DATE = " Please select today's date or future date";

    public static final String INVALID_SUBSCRIPTION_STATUS_RECEIVED = "Invalid subscription status received";
    public static final String GENERAL_EXCEPETION = "generalException";
    public static final String USER_NOT_AVAILABLE_IN_SPR_TABLE = "Input username is not available in SPR Table via Product API[findByUserIdentity]";
    public static final String INVALID_USERNAME = "@gmail.com";
    public static final String INVALID_USERNAME_MSG = "Invalid Package Configure with Empty or Null OCSCORELATION ID";
    public static final int JAXBException = 404;

    public static final String INPUT_IP_ADDRESS_NULL_DOT= "Input IpAddress is Empty or Null.";
    public static final String UNKNOWN_USERNAME_FOUND_AGAINST_INPUT_IP_ADDRESS_FOR_LOGIN_SESSION_WITHOUT_DOT ="UNKNOWN username found against Input Ip Address for Login Session";

    public static final String SUSPEND = "SUSPEND";
    public static final String ACTIVE = "Y";
    public static final String INACTIVE = "N";
    public static final String INVALID_ACTIVATION = "H";
    public static final String  STATUS= "status";
    public static final String BAD_REQUEST_MESSAGE = "Bad Request";
    public static final String INPUT_IP_ADDRESS_FORMATE_INVALID ="Input Ip Address Formate is not valid.";
    public static final String INVALID_MAC_IN_INPUT ="Invalid MAC in Input(Y/N)";
    public static final String USER_NOT_EXIST_SPR ="User doesn't exist in SPR table";
    public static final String ERROR_RADIUS_CLIENT ="Error RadiusClientService : ";
    public static final String INPUT_USERNAME_NULL_Empty ="Input user name is Empty or Null.";


    public static final String IP_NOT_AVAILABLE_IN_SESSION_OR_UNKOWN = "IP is not available in session table with Actual Username or UNKNOWN : ERICSSON";
    public static final String  USER_NOT_AVAILABLE = "user not available";

    public static final String  VLAN_ID_NOT_GEO_LOCATION_NOT_MATCH= "VLAN_ID or GEO_Location does not match for logged user";
    public static final int  VLAN_ID_NOT_GEO_LOCATION_NOT_MATCH_CODE= 308;
    public static final String  NO_DATA_FOUND_IN_TBLMCONCURRENTUSERS ="No data found in TBLMCONCURRENTUSERS to update username";
    public static final int  NO_DATA_FOUND_IN_TBLMCONCURRENTUSERS_CODE= 313;
    public static final String ENDDATE_CANNOT_BE_LESS_THEN_STARTDATE = "Expiry date can not be less than start date";

    public static final String FEING_CLIENT_EXCEPTION = "Feign Client Exception : service unavailable";


    public static final String MACVALIDATION = "MACVALIDATION";
    public static final String PASSWORD = "PASSWORD";

}
