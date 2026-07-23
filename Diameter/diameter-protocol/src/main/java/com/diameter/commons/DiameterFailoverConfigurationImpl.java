package com.diameter.commons;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {})
public class DiameterFailoverConfigurationImpl implements DiameterFailoverConfiguration {
  private static final String MODULE = "DIA-FAILOVER-CONF";
  
  private String errorCodes;
  
  private DiameterFailureConstants failoverAction = DiameterFailureConstants.PASSTHROUGH;
  
  private String failoverArgs;
  
  private int actionInt = DiameterFailureConstants.PASSTHROUGH.failureAction;
  
  public void setFailoverArguments(String failoverArgs) {
    this.failoverArgs = failoverArgs;
  }
  
  public void setErrorCodes(String errorCodes) {
    this.errorCodes = errorCodes;
  }
  
  @XmlTransient
  public DiameterFailureConstants getFailoverAction() {
    return this.failoverAction;
  }
  
  @XmlElement(name = "failure-argument", type = String.class)
  public String getFailoverArguments() {
    return this.failoverArgs;
  }
  
  @XmlElement(name = "action", type = int.class)
  public int getAction() {
    return this.actionInt;
  }
  
  public void setAction(int failoverInt) {
    DiameterFailureConstants failureAction = DiameterFailureConstants.fromDiameterFailureAction(failoverInt);
    if (failureAction != null) {
      this.actionInt = failoverInt;
      this.failoverAction = failureAction;
    } else if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
      LogManager.getLogger().warn("DIA-FAILOVER-CONF", "Invalid Failure Action: " + failoverInt + ". Taking FailoverAction: " + DiameterFailureConstants.PASSTHROUGH);
    } 
  }
  
  public String toString() {
    StringWriter stringBuffer = new StringWriter();
    PrintWriter out = new PrintWriter(stringBuffer);
    out.println("    ");
    out.println("  -- Diameter Failover  Configuration -- ");
    out.println(" \t Failover ErroCode      = " + this.errorCodes);
    out.println(" \t Failover Action        = " + this.failoverAction);
    out.println(" \t Failover Parameters    = " + this.failoverArgs);
    out.close();
    return stringBuffer.toString();
  }
  
  @XmlElement(name = "error-code", type = String.class)
  public String getErrorCodes() {
    return this.errorCodes;
  }
}
