package com.diameter.commons;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ApplicationProviderFactory {
  private static final String MODULE = "APP-PROVIER-FACT";
  
  private static final String NO_APP_INDICATOR = "0x0";
  
  private static final String RELAY_APPICATION_ID = "0xFFFFFFFF";
  
  private IDiameterStackContext stackContext;
  
  private static ApplicationProviderFactory applicationProviderFactory;
  
  public ApplicationProviderFactory(IDiameterStackContext stackContext) {
    this.stackContext = stackContext;
  }
  
  public static ApplicationProviderFactory getInstance(IDiameterStackContext stackContext) {
    if (applicationProviderFactory == null)
      applicationProviderFactory = new ApplicationProviderFactory(stackContext); 
    return applicationProviderFactory;
  }
  
  public ApplicationContainer createApplicationContainer(String exclusiveAppIDStr, ServiceTypes serviceType) {
    ApplicationContainerType applicationContainerType = getApplicationContainerType(exclusiveAppIDStr);
    switch (applicationContainerType) {
      case EMPTY:
        return new EmptyApplicationContainer();
      case STACK:
        return new StackApplicationContainer(this.stackContext, serviceType);
      case EXCLUSIVE:
        return new ExclusiveApplicationContainer(buildExclusiveApplications(exclusiveAppIDStr, serviceType));
    } 
    return null;
  }
  
  private ApplicationContainerType getApplicationContainerType(String exclusiveAppIDStr) {
    if (exclusiveAppIDStr == null || exclusiveAppIDStr.trim().length() == 0)
      return ApplicationContainerType.STACK; 
    if (exclusiveAppIDStr.contains("0x0") || exclusiveAppIDStr
      .contains("0x0".toUpperCase()))
      return ApplicationContainerType.EMPTY; 
    return ApplicationContainerType.EXCLUSIVE;
  }
  
  private Set<ApplicationEnum> buildExclusiveApplications(String exclusiveApplicationStr, ServiceTypes serviceType) {
    String[] exclusiveAppIds = ParserUtility.splitString(exclusiveApplicationStr, new char[] { ',', ';' });
    Set<ApplicationEnum> exclusiveApplicationSet = new HashSet<>();
    for (int i = 0; i < exclusiveAppIds.length; i++) {
      String appId = exclusiveAppIds[i].trim();
      if (appId == null || appId.trim().length() == 0)
        continue; 
      appId = appId.trim();
      if ("0xFFFFFFFF".equalsIgnoreCase(appId)) {
        exclusiveApplicationSet.add(DiameterUtility.createApplicationEnumStrictly(ApplicationIdentifier.RELAY
              .getApplicationId(), ApplicationIdentifier.RELAY
              .getVendorId(), serviceType));
        continue;
      } 
      String[] application = appId.split(":");
      try {
        long applicationId;
        long vendorId;
        if (application.length == 1) {
          vendorId = ApplicationIdentifier.BASE.getVendorId();
          applicationId = Long.parseLong(application[0]);
        } else {
          vendorId = Long.parseLong(application[0]);
          applicationId = Long.parseLong(application[1]);
          if (vendorId < 0L) {
            if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
              LogManager.getLogger().warn("APP-PROVIER-FACT", "Skipping " + serviceType + " Application-ID: " + appId + ", Reason: Invalid Vendor ID: " + vendorId); 
            continue;
          } 
        } 
        if (applicationId <= 0L) {
          if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
            LogManager.getLogger().warn("APP-PROVIER-FACT", "Skipping " + serviceType + " Application-ID: " + appId + ", Reason: Invalid Application ID: " + applicationId); 
        } else {
          exclusiveApplicationSet.add(DiameterUtility.createApplicationEnumStrictly(applicationId, vendorId, serviceType));
        } 
      } catch (NumberFormatException e) {
        if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
          LogManager.getLogger().warn("APP-PROVIER-FACT", "Skipping " + serviceType + " Application-ID: " + appId + ", Reason: Unable to parse " + e
              .getMessage()); 
      } 
      continue;
    } 
    return exclusiveApplicationSet;
  }
  
  private static class EmptyApplicationContainer implements ApplicationContainer {
    private static final String MODULE = "EMPTY-APP-CONTAINER";
    
    private EmptyApplicationContainer() {}
    
    public Set<ApplicationEnum> getApplications() {
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("EMPTY-APP-CONTAINER", "No Applications found."); 
      return Collections.emptySet();
    }
    
    public Set<ApplicationEnum> getCommonApplications(Set<ApplicationEnum> remoteApplications) {
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("EMPTY-APP-CONTAINER", "No Applications found."); 
      return Collections.emptySet();
    }
  }
  
  private enum ApplicationContainerType {
    STACK, EMPTY, EXCLUSIVE;
  }
}
