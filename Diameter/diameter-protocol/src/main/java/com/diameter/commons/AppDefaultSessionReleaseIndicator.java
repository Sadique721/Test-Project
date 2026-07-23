package com.diameter.commons;

import java.util.Objects;

public class AppDefaultSessionReleaseIndicator implements SessionReleaseIndiactor {
  public static final String MODULE = "APP-DEFAULT-SESS-RELEASE-INDICATOR";
  
  public boolean isEligible(DiameterPacket diameterPacket) {
    if (diameterPacket.isRequest())
      return false; 
    DiameterAnswer diameterAnswer = (DiameterAnswer)diameterPacket;
    if (diameterAnswer.isServerInitiated())
      return false; 
    boolean result = false;
    switch (CommandCode.getCommandCode(diameterPacket.getCommandCode())) {
      case CREDIT_CONTROL:
        result = checkCCResponseForSessionRemoval(diameterAnswer);
        break;
      case ACCOUNTING:
        result = checkAccountingResponseForSessionRemoval(diameterAnswer);
        break;
      case SESSION_TERMINATION:
        if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
          LogManager.getLogger().debug("APP-DEFAULT-SESS-RELEASE-INDICATOR", "Eligible to remove session. Reason: Command-Code is ST(" + CommandCode.SESSION_TERMINATION.code + ")"); 
        return true;
    } 
    return result ? true : checkResultCodeForSessionRemoval(diameterAnswer);
  }
  
  protected boolean checkCCResponseForSessionRemoval(DiameterAnswer diameterPacket) {
    IDiameterAVP requestType = diameterPacket.getAVP("0:416");
    if (requestType != null) {
      if (requestType.getInteger() == 3L) {
        if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
          LogManager.getLogger().debug("APP-DEFAULT-SESS-RELEASE-INDICATOR", "Eligible to remove session. Reason: CC-Request-Type is TERMINATION (3)"); 
        return true;
      } 
    } else {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("APP-DEFAULT-SESS-RELEASE-INDICATOR", "Eligible to remove session. Reason: Request-Type AVP not found in CC response for Session ID: " + diameterPacket.getAVPValue("0:263")); 
      return true;
    } 
    return false;
  }
  
  protected boolean checkAccountingResponseForSessionRemoval(DiameterAnswer diameterPacket) {
    IDiameterAVP recordType = diameterPacket.getAVP("0:480");
    if (recordType != null) {
      if (recordType.getInteger() == 4L) {
        if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
          LogManager.getLogger().debug("APP-DEFAULT-SESS-RELEASE-INDICATOR", "Eligible to remove session. Reason: Accounting-Record-Type is STOP-RECORD(4)"); 
        return true;
      } 
    } else {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("APP-DEFAULT-SESS-RELEASE-INDICATOR", "Eligible to remove session. Reason: Accounting-Record-Type AVP not found in Accounting response for Session ID: " + diameterPacket.getAVPValue("0:263")); 
      return true;
    } 
    return false;
  }
  
  protected boolean checkResultCodeForSessionRemoval(DiameterAnswer diameterAnswer) {
    IDiameterAVP resultCode = diameterAnswer.getAVP("0:268");
    ResultCodeCategory resultCodeCategory = null;
    if (Objects.nonNull(resultCode))
      resultCodeCategory = ResultCodeCategory.getResultCodeCategory(resultCode.getInteger()); 
    IDiameterAVP experimentalResultCode = null;
    if (Objects.isNull(resultCodeCategory)) {
      AvpGrouped experimentalResult = (AvpGrouped)diameterAnswer.getAVP("0:297");
      if (Objects.nonNull(experimentalResult))
        experimentalResultCode = experimentalResult.getSubAttribute("0:298"); 
      if (Objects.nonNull(experimentalResultCode))
        resultCodeCategory = ResultCodeCategory.getResultCodeCategory(experimentalResultCode.getInteger()); 
    } 
    if (ResultCodeCategory.RC2XXX == resultCodeCategory || ResultCodeCategory.RC1XXX == resultCodeCategory)
      return false; 
    if (Objects.isNull(resultCodeCategory)) {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("APP-DEFAULT-SESS-RELEASE-INDICATOR", "Eligible to remove session. Reason: Result-Code and Experimental-Result-Code AVP not found for Session ID: " + diameterAnswer.getAVPValue("0:263")); 
      return true;
    } 
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("APP-DEFAULT-SESS-RELEASE-INDICATOR", "Eligible to remove session. Reason: Result-Code or Experimental-Result-Code category is not 2XXX or 1XXX"); 
    return true;
  }
}
