package com.diameter.commons;

public enum PacketValidationCode {
  VALID_PACKET(ResultCode.DIAMETER_SUCCESS.code),
  ORIGIN_HOST_MISSING(ResultCode.DIAMETER_MISSING_AVP.code),
  ORIGIN_REALM_MISSING(ResultCode.DIAMETER_MISSING_AVP.code),
  SESSION_ID_MISSING(ResultCode.DIAMETER_MISSING_AVP.code),
  INVALID_BASE_COMMAND_CODE(ResultCode.DIAMETER_INVALID_HDR_BITS.code);
  
  public final int resultCode;
  
  PacketValidationCode(int resultCode) {
    this.resultCode = resultCode;
  }
}
