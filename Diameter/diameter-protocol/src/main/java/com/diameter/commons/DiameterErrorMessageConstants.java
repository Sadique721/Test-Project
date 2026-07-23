package com.diameter.commons;

public class DiameterErrorMessageConstants {
  public static final String DEST_PEER_NOT_FOUND = "Destination Peer not found";
  
  public static final String REQUEST_TIMEOUT = "Request Timeout";
  
  public static final String ROUTE_NOT_FOUND = "Route not found";
  
  public static final String REDIRECT_PEER_NOT_FOUND = "Redirect Peer not found";
  
  public static final String DIAMETER_SESSION_NOT_FOUND = "Diameter session not found";
  
  public static final String SYSTEM_OVERLOAD = "System Overload";
  
  public static final String TPS_EXCEEDED = "TPS Exceeded";
  
  public static final String DEFAULT_RESPONSE_BEHAVIOR_APPLIED = "Default response behavior applied";
  
  public static final String INVALID_PASSWORD = "Invalid Password";
  
  public static final String USER_NOT_FOUND = "User not found";
  
  public static final String EAP_FAILURE = "EAP-Failure";
  
  public static final String AUTHENTICATION_FAILED_DUE_INVALID_PASSWORD = "Authentication failed due to Invalid password";
  
  public static final String AUTHENTICATION_FAILED_DUE_TO_INVALID_ENCRYPTION_TYPE = "Authentication failed due to Invalid encryption type";
  
  public static final String AUTHENTICATION_FAILED_DUE_TO_UNSUPPORTED_ENCRYPTION = "Authentication failed due to Unsupported encryption";
  
  public static final String AUTHENTICATION_FAILED_DUE_TO_IMPROPER_PASSWORD_ENCRYPTION_FORMAT = "Authentication failed due to Improper password encryption format";
  
  public static final String AUTHENTICATION_FAILED_DUE_TO_EXCEPTION_IN_DECRYPTION = "Authentication failed due to exception in decryption";
  
  public static final String AUTHENTICATION_FAILED_DUE_TO_EXCEPTION_IN_ENCRYPTION = "Authentication failed due to exception in encryption";
  
  public static final String AUTHENTICATION_FAILED_DUE_TO_INVALID_AVP = "Authentication failed due to Invalid AVP";
  
  public static final String UNSUPPORTED_AUTHENTICATION_METHOD = "Unsupported Authentication method";
  
  public static final String ACCOUNT_NOT_ACTIVE = "Account is not active";
  
  public static final String ACCOUNT_EXPIRED = "Account Expired";
  
  public static final String CREDIT_LIMIT_EXCEEDED = "Account Credit Limit Exceeded";
  
  public static final String INVALID_CALLING_STATION_ID = "Invalid Calling Station Id";
  
  public static final String CALLING_STATION_ID_NOT_FOUND = "Calling Station Id not found";
  
  public static final String INVALID_CALLED_STATION_ID = "Invalid Called Station Id";
  
  public static final String CALLED_STATION_ID_NOT_FOUND = "Called Station Id not found";
  
  public static final String NO_POLICY_SATISFIED = "No Policy Satisfied";
  
  public static final String MAX_LOGIN_LIMIT_REACHED = "Max Login Limit Reached";
  
  public static final String CONCURRENCY_FAILED = "Concurrency Failed";
  
  public static final String CONCURRENT_LOGIN_POLICY_NOT_FOUND = "Concurrent Login Policy Not Found";
  
  public static final String CONCURRENCY_FAILED_DUE_TO_DBOPERATION_FAILURE = "Concurrency Failed due to DB Operation Failure";
  
  public static final String SESSION_MANAGER_DB_OPERATION_FAILED = "Session Manager DB Operation Failed";
  
  public static final String NO_RULESET_SATISFIED_FOR_PROXY_COMMUNICATION = "No Ruleset Satisfied For Proxy Communication";
  
  public static final String DIAMETER_OUT_OF_SPACE = "Diameter out of space";
  
  public static String translationFailed(String translationName) {
    return "Translation: " + translationName + " failed";
  }
  
  public static String routingFailed(String routingEntryName) {
    return "Invalid configuration for routing entry - " + routingEntryName;
  }
}
