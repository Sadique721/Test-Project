package com.diameter.commons;

import java.util.HashMap;
import java.util.Map;

public enum ResultCode {
  DIAMETER_MULTI_ROUND_AUTH(1001, ResultCodeCategory.RC1XXX, 0L),
  DIAMETER_SUCCESS(2001, ResultCodeCategory.RC2XXX, 0L),
  DIAMETER_LIMITED_SUCCESS(2002, ResultCodeCategory.RC2XXX, 0L),
  DIAMETER_FIRST_REGISTRATION(2003, ResultCodeCategory.RC2XXX, 0L),
  DIAMETER_SUBSEQUENT_REGISTRATION(2004, ResultCodeCategory.RC2XXX, 0L),
  DIAMETER_UNREGISTERED_SERVICE(2005, ResultCodeCategory.RC2XXX, 0L),
  DIAMETER_SUCCESS_SERVER_NAME_NOT_STORED(2006, ResultCodeCategory.RC2XXX, 0L),
  DIAMETER_SERVER_SELECTION(2007, ResultCodeCategory.RC2XXX, 0L),
  DIAMETER_SUCCESS_AUTH_SENT_SERVER_NOT_STORED(2008, ResultCodeCategory.RC2XXX, 0L),
  DIAMETER_COMMAND_UNSUPPORTED(3001, ResultCodeCategory.RC3XXX, 0L),
  DIAMETER_UNABLE_TO_DELIVER(3002, ResultCodeCategory.RC3XXX, 0L),
  DIAMETER_REALM_NOT_SERVED(3003, ResultCodeCategory.RC3XXX, 0L),
  DIAMETER_TOO_BUSY(3004, ResultCodeCategory.RC3XXX, 0L),
  DIAMETER_LOOP_DETECTED(3005, ResultCodeCategory.RC3XXX, 0L),
  DIAMETER_REDIRECT_INDICATION(3006, ResultCodeCategory.RC3XXX, 0L),
  DIAMETER_APPLICATION_UNSUPPORTED(3007, ResultCodeCategory.RC3XXX, 0L),
  DIAMETER_INVALID_HDR_BITS(3008, ResultCodeCategory.RC3XXX, 0L),
  DIAMETER_INVALID_AVP_BITS(3009, ResultCodeCategory.RC3XXX, 0L),
  DIAMETER_UNKNOWN_PEER(3010, ResultCodeCategory.RC3XXX, 0L),
  DIAMETER_INVALID_PROXY_PATH_STACK(3501, ResultCodeCategory.RC3XXX, 0L),
  DIAMETER_AUTHENTICATION_REJECTED(4001, ResultCodeCategory.RC4XXX, 0L),
  DIAMETER_OUT_OF_SPACE(4002, ResultCodeCategory.RC4XXX, 0L),
  ELECTION_LOST(4003, ResultCodeCategory.RC4XXX, 0L),
  DIAMETER_ERROR_MIP_REPLY_FAILURE(4005, ResultCodeCategory.RC4XXX, 0L),
  DIAMETER_ERROR_HA_NOT_AVAILABLE(4006, ResultCodeCategory.RC4XXX, 0L),
  DIAMETER_ERROR_BAD_KEY(4007, ResultCodeCategory.RC4XXX, 0L),
  DIAMETER_ERROR_MIP_FILTER_NOT_SUPPORTED(4008, ResultCodeCategory.RC4XXX, 0L),
  DIAMETER_END_USER_SERVICE_DENIED(4010, ResultCodeCategory.RC4XXX, 0L),
  DIAMETER_CREDIT_CONTROL_NOT_APPLICABLE(4011, ResultCodeCategory.RC4XXX, 0L),
  DIAMETER_CREDIT_LIMIT_REACHED(4012, ResultCodeCategory.RC4XXX, 0L),
  DIAMETER_USER_NAME_REQUIRED(4013, ResultCodeCategory.RC4XXX, 0L),
  DIAMETER_ER_NOT_AVAILABLE(4501, ResultCodeCategory.RC4XXX, 0L),
  DIAMETER_PEER_NOT_FOUND(4998, ResultCodeCategory.RC4XXX, 21067L),
  DIAMETER_REQUEST_TIMEOUT(4999, ResultCodeCategory.RC4XXX, 21067L),
  DIAMETER_AVP_UNSUPPORTED(5001, ResultCodeCategory.RC5XXX, 0L),
  DIAMETER_UNKNOWN_SESSION_ID(5002, ResultCodeCategory.RC5XXX, 0L),
  DIAMETER_AUTHORIZATION_REJECTED(5003, ResultCodeCategory.RC5XXX, 0L),
  DIAMETER_INVALID_AVP_VALUE(5004, ResultCodeCategory.RC5XXX, 0L),
  DIAMETER_MISSING_AVP(5005, ResultCodeCategory.RC5XXX, 0L),
  DIAMETER_RESOURCES_EXCEEDED(5006, ResultCodeCategory.RC5XXX, 0L),
  DIAMETER_CONTRADICTING_AVPS(5007, ResultCodeCategory.RC5XXX, 0L),
  DIAMETER_AVP_NOT_ALLOWED(5008, ResultCodeCategory.RC5XXX, 0L),
  DIAMETER_AVP_OCCURS_TOO_MANY_TIMES(5009, ResultCodeCategory.RC5XXX, 0L),
  DIAMETER_NO_COMMON_APPLICATION(5010, ResultCodeCategory.RC5XXX, 0L),
  DIAMETER_UNSUPPORTED_VERSION(5011, ResultCodeCategory.RC5XXX, 0L),
  DIAMETER_UNABLE_TO_COMPLY(5012, ResultCodeCategory.RC5XXX, 0L),
  DIAMETER_INVALID_BIT_IN_HEADER(5013, ResultCodeCategory.RC5XXX, 0L),
  DIAMETER_INVALID_AVP_LENGTH(5014, ResultCodeCategory.RC5XXX, 0L),
  DIAMETER_INVALID_MESSAGE_LENGTH(5015, ResultCodeCategory.RC5XXX, 0L),
  DIAMETER_INVALID_AVP_BIT_COMBO(5016, ResultCodeCategory.RC5XXX, 0L),
  DIAMETER_NO_COMMON_SECURITY(5017, ResultCodeCategory.RC5XXX, 0L),
  DIAMETER_RADIUS_AVP_UNTRANSLATABLE(5018, ResultCodeCategory.RC5XXX, 0L),
  DIAMETER_ERROR_NO_FOREIGN_HA_SERVICE(5024, ResultCodeCategory.RC5XXX, 0L),
  DIAMETER_ERROR_END_TO_END_MIP_KEY_ENCRYPTION(5025, ResultCodeCategory.RC5XXX, 0L),
  DIAMETER_USER_UNKNOWN(5030, ResultCodeCategory.RC5XXX, 0L),
  DIAMETER_RATING_FAILED(5031, ResultCodeCategory.RC5XXX, 0L),
  DIAMETER_ERROR_USER_UNKNOWN(5032, ResultCodeCategory.RC5XXX, 0L),
  DIAMETER_ERROR_IDENTITIES_DONT_MATCH(5033, ResultCodeCategory.RC5XXX, 0L),
  DIAMETER_ERROR_IDENTITY_NOT_REGISTERED(5034, ResultCodeCategory.RC5XXX, 0L),
  DIAMETER_ERROR_ROAMING_NOT_ALLOWED(5035, ResultCodeCategory.RC5XXX, 0L),
  DIAMETER_ERROR_IDENTITY_ALREADY_REGISTERED(5036, ResultCodeCategory.RC5XXX, 0L),
  DIAMETER_ERROR_AUTH_SCHEME_NOT_SUPPORTED(5037, ResultCodeCategory.RC5XXX, 0L),
  DIAMETER_ERROR_IN_ASSIGNMENT_TYPE(5038, ResultCodeCategory.RC5XXX, 0L),
  DIAMETER_ERROR_TOO_MUCH_DATA(5039, ResultCodeCategory.RC5XXX, 0L),
  DIAMETER_ERROR_NOT_SUPPORTED_USER_DATA(5040, ResultCodeCategory.RC5XXX, 0L),
  DIAMETER_REQUESTED_SERVICE_NOT_AUTHORIZED(5063, ResultCodeCategory.RC5XXX, 10415L),
  DIAMETER_IP_CAN_SESSION_NOT_AVAILABLE(5065, ResultCodeCategory.RC5XXX, 10415L);
  
  private static final Map<Integer, ResultCode> map;
  
  protected static final ResultCode[] RESULT_CODES;
  
  public final int code;
  
  public final ResultCodeCategory category;
  
  public final long vendorId;
  
  static {
    RESULT_CODES = values();
    map = new HashMap<>();
    for (ResultCode type : RESULT_CODES)
      map.put(Integer.valueOf(type.code), type); 
  }
  
  ResultCode(int code, ResultCodeCategory category, long vendorId) {
    this.code = code;
    this.category = category;
    this.vendorId = vendorId;
  }
  
  public int getCode() {
    return this.code;
  }
  
  public static boolean isValid(int value) {
    ResultCodeCategory resultCodeCategory = ResultCodeCategory.getResultCodeCategory(value);
    return (resultCodeCategory != ResultCodeCategory.RC6XXX);
  }
  
  public static ResultCode fromCode(int value) {
    return map.get(Integer.valueOf(value));
  }
  
  public static void main(String[] args) {
    System.out.println(DIAMETER_SUCCESS);
    System.out.println(isValid(DIAMETER_SUCCESS.getCode()));
  }
}