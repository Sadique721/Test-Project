package com.diameter.commons;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class StackApplicationContainer implements ApplicationContainer {
  private static final String MODULE = "STACK-APP-CONTAINER";
  
  private Set<ApplicationEnum> applications;
  
  private IDiameterStackContext stackContext;
  
  private ServiceTypes serviceType;
  
  public StackApplicationContainer(IDiameterStackContext stackContext, ServiceTypes serviceType) {
    this.stackContext = stackContext;
    this.serviceType = serviceType;
  }
  
  public Set<ApplicationEnum> getApplications() {
    if (this.applications == null)
      this.applications = fetchApplicationsOfType(this.serviceType); 
    StringBuilder localAppLst = new StringBuilder("Local " + this.serviceType + " Applications:[");
    for (ApplicationEnum applicationEnum : this.applications) {
      localAppLst.append(applicationEnum.getApplicationType());
      localAppLst.append('=');
      localAppLst.append(applicationEnum.getVendorId());
      localAppLst.append(':');
      localAppLst.append(applicationEnum.getApplicationId());
      localAppLst.append(',');
    } 
    localAppLst.deleteCharAt(localAppLst.length() - 1);
    localAppLst.append(']');
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("STACK-APP-CONTAINER", localAppLst.toString()); 
    return this.applications;
  }
  
  public Set<ApplicationEnum> getCommonApplications(Set<ApplicationEnum> remoteApplications) {
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("STACK-APP-CONTAINER", "Finding Common " + this.serviceType + " Applications"); 
    if (this.applications == null)
      this.applications = fetchApplicationsOfType(this.serviceType); 
    if (this.applications.isEmpty()) {
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("STACK-APP-CONTAINER", "Local " + this.serviceType + " Applications Not Found"); 
      return Collections.emptySet();
    } 
    HashSet<ApplicationEnum> commonApplications = new HashSet<>();
    StringBuilder localAppLst = new StringBuilder("Local " + this.serviceType + " Applications:[");
    for (ApplicationEnum applicationEnum : this.applications) {
      localAppLst.append(applicationEnum.getApplicationType());
      localAppLst.append('=');
      localAppLst.append(applicationEnum.getVendorId());
      localAppLst.append(':');
      localAppLst.append(applicationEnum.getApplicationId());
      localAppLst.append(',');
    } 
    localAppLst.deleteCharAt(localAppLst.length() - 1);
    localAppLst.append(']');
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("STACK-APP-CONTAINER", localAppLst.toString()); 
    StringBuilder remoteAppLst = new StringBuilder("Remote " + this.serviceType + " Applications:[");
    StringBuilder commonAppLst = new StringBuilder("Common " + this.serviceType + " Applications:[");
    for (ApplicationEnum remoteDiaAppEnum : remoteApplications) {
      remoteAppLst.append(remoteDiaAppEnum.getApplicationType());
      remoteAppLst.append('=');
      remoteAppLst.append(remoteDiaAppEnum.getVendorId());
      remoteAppLst.append(':');
      remoteAppLst.append(remoteDiaAppEnum.getApplicationId());
      remoteAppLst.append(',');
      for (ApplicationEnum localDiaAppEnum : this.applications) {
        if (localDiaAppEnum.getVendorId() == remoteDiaAppEnum.getVendorId() && localDiaAppEnum
          .getApplicationId() == remoteDiaAppEnum.getApplicationId() && localDiaAppEnum
          .getApplicationType() == remoteDiaAppEnum.getApplicationType()) {
          commonAppLst.append(remoteDiaAppEnum.getApplicationType());
          commonAppLst.append('=');
          commonAppLst.append(remoteDiaAppEnum.getVendorId());
          commonAppLst.append(':');
          commonAppLst.append(remoteDiaAppEnum.getApplicationId());
          commonAppLst.append(',');
          commonApplications.add(remoteDiaAppEnum);
        } 
      } 
    } 
    if (!commonApplications.isEmpty())
      remoteAppLst.deleteCharAt(remoteAppLst.length() - 1); 
    if (!commonApplications.isEmpty())
      commonAppLst.deleteCharAt(commonAppLst.length() - 1); 
    remoteAppLst.append(']');
    commonAppLst.append(']');
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("STACK-APP-CONTAINER", remoteAppLst.toString()); 
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("STACK-APP-CONTAINER", commonAppLst.toString()); 
    return commonApplications;
  }
  
  private Set<ApplicationEnum> fetchApplicationsOfType(final ServiceTypes serviceType) {
    Set<ApplicationEnum> selectedApplications = new HashSet<>();
    for (ApplicationEnum appEnum : this.stackContext.getApplicationsIdentifiersList()) {
      if (appEnum.getApplicationType() == serviceType) {
        selectedApplications.add(appEnum);
        continue;
      } 
      if (appEnum.getApplicationType() == ServiceTypes.BOTH)
        selectedApplications.add(new ApplicationEnum() {
              public long getVendorId() {
                return appEnum.getVendorId();
              }
              
              public ServiceTypes getApplicationType() {
                return serviceType;
              }
              
              public long getApplicationId() {
                return appEnum.getApplicationId();
              }
              
              public Application getApplication() {
                return appEnum.getApplication();
              }
              
              public String toString() {
                return 
                  getVendorId() + ":" + 
                  
                  getApplicationId() + " [" + 
                  getApplication().getDisplayName() + "]";
              }
            }); 
    } 
    return selectedApplications;
  }
}
