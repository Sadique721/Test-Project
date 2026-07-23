package com.diameter.stack;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;

import com.diameter.commons.ConnectionFactory;
import com.diameter.commons.DiameterStackAlerts;
import com.diameter.commons.ILogger;
import com.diameter.commons.INetworkConnector;
import com.diameter.commons.IStackAlertEnum;
import com.diameter.commons.IStackAlertManager;
import com.diameter.commons.LogManager;
import com.diameter.commons.ServiceRemarks;
import com.diameter.commons.SocketDetail;
import com.diameter.commons.StackAlertSeverity;
import com.diameter.commons.TransportProtocols;

public abstract class Stack {
  private static final String MODULE = "STACK";
  
  protected Map<TransportProtocols, INetworkConnector> networkConnectors = new EnumMap<>(TransportProtocols.class);
  
  private static IStackAlertManager alertManager;
  
  private SocketDetail socketDetail;
  
  protected boolean start(ConnectionFactory connectionFactory) {
	  boolean allStarted = true;
    if (!this.networkConnectors.isEmpty()) {
      int i;
      for (INetworkConnector networkConnector : this.networkConnectors.values()) {
        boolean hasStarted = networkConnector.start(connectionFactory);
        allStarted  = allStarted  && hasStarted ;
        if (hasStarted)
          this.socketDetail = networkConnector.getBondSocketDetail(); 
      } 
      if (!allStarted) {
        stop();
        return false;
      } 
    } else {
      LogManager.getLogger().error("STACK", "No Network Connector Specified");
      stop();
      return false;
    } 
    return true;
  }
  
  public String getNetworkAddress() {
    if (this.socketDetail == null)
      return null; 
    return this.socketDetail.getIPAddress();
  }
  
  public SocketDetail getSocketDetail() {
    return this.socketDetail;
  }
  
  public int getNetworkPort() {
    if (this.socketDetail == null)
      return 0; 
    return this.socketDetail.getPort();
  }
  
  public void addNetworkConnector(INetworkConnector networkConnector) {
    this.networkConnectors.put(networkConnector.getTransportProtocol(), networkConnector);
  }
  
  protected final INetworkConnector getNetworkConnector(TransportProtocols transportProtocol) {
    return this.networkConnectors.get(transportProtocol);
  }
  
  public void registerStackAlertManager(IStackAlertManager alertManager) {
    Stack.alertManager = alertManager;
  }
  
  protected boolean stop() {
    boolean hasStopped = true;
    for (INetworkConnector networkConnector : this.networkConnectors.values())
      hasStopped &= networkConnector.stop(); 
    return hasStopped;
  }
  
  public static void generateAlert(StackAlertSeverity alertSeverity, DiameterStackAlerts alertEnum, String alertGenerator, String alertMessage) {
    if (alertManager == null) {
      getLogger().warn("STACK", "Failed to generate alert: " + alertEnum + " : " + alertMessage + ". Reason: Alert Manager is not initialized");
    } else {
      alertManager.scheduleAlert(alertSeverity, (IStackAlertEnum)alertEnum, alertGenerator, alertMessage);
    } 
  }
  
  public static void generateAlert(StackAlertSeverity alertSeverity, DiameterStackAlerts alertEnum, String alertGenerator, String alertMessage, int alertIntValue, String alertStringValue) {
    if (alertManager == null) {
      getLogger().warn("STACK", "Failed to generate alert: " + alertEnum + " : " + alertMessage + ". Reason: Alert Manager is not initialized");
    } else {
      alertManager.scheduleAlert(alertSeverity, (IStackAlertEnum)alertEnum, alertGenerator, alertMessage, alertIntValue, alertStringValue);
    } 
  }
  
  private static ILogger getLogger() {
    return LogManager.getLogger();
  }
  
  public String getRemarks() {
    StringBuilder remark = new StringBuilder();
    HashSet<ServiceRemarks> set = new HashSet<>();
    for (INetworkConnector connector : this.networkConnectors.values()) {
      set.add(connector.getRemarks());
      if (connector.getRemarks() != null)
        remark.append(", ")
          .append(connector.getTransportProtocol())
          .append(": ")
          .append((connector.getRemarks()).remark); 
    } 
    String out = null;
    if (set.size() == 1) {
      ServiceRemarks next = set.iterator().next();
      if (next != null)
        out = next.remark; 
    } else if (remark.length() > 0) {
      out = remark.substring(2);
    } 
    return out;
  }
}
