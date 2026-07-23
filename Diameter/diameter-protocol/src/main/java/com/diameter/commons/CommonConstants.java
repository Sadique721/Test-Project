package com.diameter.commons;

public class  CommonConstants {
	public static final String UTF8 = "UTF-8";

	/** This is No of seconds From 1st jan 00:00 1900 to 1st jan 00:00  1971 (Ref: Time Protocol RFC-868) */
	public static final long EPOCH1900_1970 = 2208988800L;
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	public static final String PATH_SEPARATOR = System.getProperty("path.separator");
	public static final String FILE_SEPARATOR = System.getProperty("file.separator");

	public static final String POLICY_OLD_REPLY_ITEM = "policy.oldreplyitem";
    public static final int MAX_RESPONSE_TIME_MS = 100;

    // Here we want to use value of Java's DEFAULT_LOAD_FACTOR only but as it is not visible, here it is created to be used in our product
    public static final float DEFAULT_LOAD_FACTOR = 0.75f;


    public static final String RESERVED_IPV_4_ADDRESS = "254.255.255.254";
    public static final String POLICY_FAILED = "Policy Failed";
    public static final int QUERY_TIMEOUT_SEC = 1; //Second
    public static final int NO_QUERY_TIMEOUT = 0; //Second

    // Socket Ip/ port value
    public static final String UNIVERSAL_IP = "0.0.0.0";
    public static final int ANY_PORT = 0;
    public static final int MAX_PORT = 65535;

    public static final char COLON = ':';
    public static final char COMMA = ',';
    public static final Splitter COMMA_SPLITTER = Strings.splitter(COMMA).trimTokens();
    public static final String FORWARD_SLASH="/";
    public static final String CDR_STREAM_JSON_FORMAT = "cdr_stream_json_format";
    public static final String SERVICE_NAME_BASE_API_CONFIGURATION = "service_name_base_api_configuration";
    public static final String CDRTIMESTAMP = "CDRTIMESTAMP";
    public static final String CUSTOM_PCC_KEYS ="CUSTOM_PCC_KEYS";
    
	public static final String MALFORMED_PACKET = "MALFORMED_PACKET";
	public static final String DIAMETER_STACK_IDENTIFIER = "DIA-STACK";

    public interface CUST_QUOTA_TYPE {
        String SHAREABLE = "shareable";
        String INDIVIDUAL = "individual";

    }
}

