package com.diameter.commons;

import java.util.ArrayList;
import java.util.List;

public class RecordFailureAction implements RoutingFailureAction {
  private static final String MODULE = "RECORD-FLR-ACT";
  
  private String failureArgs;
  
  private List<String> warnings;
  
  private RouterContext routerContext;
  
  private CDRDriver<DiameterPacket> cdrDriver;
  
  public RecordFailureAction(RouterContext routerContext, String failureArgs) {
    this.failureArgs = failureArgs;
    this.routerContext = routerContext;
    this.warnings = new ArrayList<>();
  }
  
  public void init() {
    if (Strings.isNullOrEmpty(this.failureArgs)) {
      this.warnings.add("No CDR Driver Name found for " + DiameterFailureConstants.RECORD + "  Failure Action");
      return;
    } 
    this.failureArgs = this.failureArgs.trim();
    try {
      this.cdrDriver = this.routerContext.getDiameterCDRDriver(this.failureArgs);
    } catch (DriverInitializationFailedException exception) {
      LogManager.getLogger().warn("RECORD-FLR-ACT", "Error while initializing Driver : " + this.failureArgs + ". Reason : " + exception
          .getMessage());
      LogManager.getLogger().trace("RECORD-FLR-ACT", (Throwable)exception);
      this.warnings.add("Error: " + exception.getMessage() + " initializing CDR Driver: " + this.failureArgs + " in " + DiameterFailureConstants.RECORD + " Failure Action");
    } catch (DriverNotFoundException e) {
      LogManager.getLogger().warn("RECORD-FLR-ACT", "Error while getting Driver : " + this.failureArgs + ". Reason : " + e
          .getMessage());
      this.warnings.add("CSV Driver Configuration with Name: " + this.failureArgs + " is not found");
    } catch (TypeNotSupportedException ex) {
      LogManager.getLogger().warn("RECORD-FLR-ACT", "Driver type is not supported for  : " + this.failureArgs + ". Driver Type Should be Classic CSV Driver. Reason : " + ex
          .getMessage());
      this.warnings.add("CSV Driver Type mismatches for  Name: " + this.failureArgs + " . Driver Type should be Classic CSV Driver");
    } 
  }
  
  public FailureActionResult process(DiameterAnswer failureAnswer, DiameterSession routingSession, DiameterRequest originRequest, DiameterRequest remoteRequest, String remotePeerHostIdentity, String originPeerName) {
    if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
      LogManager.getLogger().info("RECORD-FLR-ACT", "Performing " + DiameterFailureConstants.RECORD + " Failure Action with  Driver : " + this.cdrDriver
          .getDriverName() + " for Session-ID=" + failureAnswer
          .getAVPValue("0:263") + " HbH-ID=" + failureAnswer
          .getHop_by_hopIdentifier()); 
    try {
      this.cdrDriver.handleRequest(originRequest);
    } catch (DriverProcessFailedException exception) {
      LogManager.getLogger().error("RECORD-FLR-ACT", "Error while recording Diameter Request with HbH-ID=" + originRequest
          .getHop_by_hopIdentifier() + ".");
      LogManager.getLogger().trace("RECORD-FLR-ACT", (Throwable)exception);
    } 
    return new FailureActionResult(FailureActionResultCodes.SEND_ANSWER_TO_ORIGINATOR, (DiameterPacket)failureAnswer);
  }
  
  public List<String> getWarnings() {
    return this.warnings;
  }
}