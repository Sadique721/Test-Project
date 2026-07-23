package com.diameter.commons;

import java.util.Set;

public class ExclusiveApplicationContainer implements ApplicationContainer {
  private static final String MODULE = "EXCLUSIVE-APP-CONTAINER";
  
  Set<ApplicationEnum> applications;
  
  private String exclusiveApplicationString;
  
  public ExclusiveApplicationContainer(Set<ApplicationEnum> applications) {
    this.applications = applications;
    StringBuilder exclusiveAppLst = new StringBuilder("Exclusive Applications:[");
    for (ApplicationEnum applicationEnum : this.applications) {
      exclusiveAppLst.append(applicationEnum.getApplicationType());
      exclusiveAppLst.append('=');
      exclusiveAppLst.append(applicationEnum.getVendorId());
      exclusiveAppLst.append(':');
      exclusiveAppLst.append(applicationEnum.getApplicationId());
      exclusiveAppLst.append(',');
    } 
    if (!this.applications.isEmpty())
      exclusiveAppLst.deleteCharAt(exclusiveAppLst.length() - 1); 
    exclusiveAppLst.append(']');
    this.exclusiveApplicationString = exclusiveAppLst.toString();
  }
  
  public Set<ApplicationEnum> getApplications() {
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("EXCLUSIVE-APP-CONTAINER", this.exclusiveApplicationString); 
    return this.applications;
  }
  
  public Set<ApplicationEnum> getCommonApplications(Set<ApplicationEnum> remoteApplications) {
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("EXCLUSIVE-APP-CONTAINER", this.exclusiveApplicationString); 
    return this.applications;
  }
}