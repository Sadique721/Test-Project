package com.diameter.commons;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RoutingEntry {
  private static final String MODULE = "ROUTING-ENTRY";
  
  private ApplicationEnum[] applicationIds;
  
  private RoutingActions routingAction;
  
  private LogicalExpression advancedCondition;
  
  private Map<Integer, RoutingFailureAction> failureHandlerMap;
  
  private RoutingEntryData routingEntryData;
  
  private RouterContext routerContext;
  
  private String warning;
  
  private PeerSelector peerSelector;
  
  private String[] destRealms;
  
  private String[] originRealms;
  
  private String[] originHostIps;
  
  private String[] destExpRealms;
  
  private String[] originExpRealms;
  
  private static final String ASTERISK_STR = "*";
  
  private static final String DEFAULT_APP_ID = "0";
  
  private static final long DEFAULT_VENDOR_ID_LONG = 0L;
  
  private boolean ALLOW_ALL_DEST_REALMS = false;
  
  private boolean ALLOW_ALL_ORIGIN_REALMS = false;
  
  private boolean ALLOW_ALL_ORIGIN_IPS = false;
  
  private ITranslationAgent translationAgent;
  
  public RoutingEntry(RoutingEntryData routingEntryData, RouterContext diameterRouterContext, ITranslationAgent translationAgent) {
    this.routingEntryData = routingEntryData;
    this.routerContext = diameterRouterContext;
    this.translationAgent = translationAgent;
    this.failureHandlerMap = new HashMap<>();
  }
  
  public void init() throws InitializationFailedException {
    List<String> warnings = new ArrayList<>();
    initDestRealms();
    initOriginRealms();
    initOriginHostIps();
    this.routingAction = RoutingActions.fromRoutingAction(this.routingEntryData.getRoutingAction());
    if (this.routingEntryData.getTransMapName() != null && this.routingEntryData.getTransMapName().trim().length() > 0 && 
      !this.translationAgent.isExists(this.routingEntryData.getTransMapName())) {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("ROUTING-ENTRY", "Configured translation policy: " + this.routingEntryData.getTransMapName() + " for Routing Entry: " + this.routingEntryData
            .getRoutingName() + " is not registered"); 
      warnings.add("Configured translation policy: " + this.routingEntryData.getTransMapName() + " is not registered");
    } 
    initApplicationIds(warnings);
    initAdvancedCondition(warnings);
    initPeerSelector(warnings);
    initFailureActions(warnings);
    this.warning = buildWarningMsg(warnings);
  }
  
  private void initDestRealms() {
    String[] tempDestRealms = ParserUtility.splitString(this.routingEntryData.getDestRealm(), new char[] { ',', ';' });
    List<String> staticDestRealms = new ArrayList<>();
    List<String> expDestRealms = new ArrayList<>();
    if (tempDestRealms == null || 
      containsString(tempDestRealms, "*") || tempDestRealms.length == 0) {
      this.ALLOW_ALL_DEST_REALMS = true;
    } else {
      for (int i = 0; i < tempDestRealms.length; i++) {
        if (ParserUtility.containsChar(tempDestRealms[i].toCharArray(), '*')) {
          expDestRealms.add(tempDestRealms[i].trim());
        } else {
          staticDestRealms.add(tempDestRealms[i].trim());
        } 
      } 
      this.destRealms = staticDestRealms.<String>toArray(new String[staticDestRealms.size()]);
      this.destExpRealms = expDestRealms.<String>toArray(new String[expDestRealms.size()]);
    } 
  }
  
  private void initOriginRealms() {
    String[] tempOriginRealms = ParserUtility.splitString(this.routingEntryData.getOriginRealm(), new char[] { ',', ';' });
    List<String> staticOriginRealms = new ArrayList<>();
    List<String> expOriginRealms = new ArrayList<>();
    if (tempOriginRealms == null || 
      containsString(tempOriginRealms, "*") || tempOriginRealms.length == 0) {
      this.ALLOW_ALL_ORIGIN_REALMS = true;
    } else {
      for (int i = 0; i < tempOriginRealms.length; i++) {
        if (ParserUtility.containsChar(tempOriginRealms[i].toCharArray(), '*')) {
          expOriginRealms.add(tempOriginRealms[i].trim());
        } else {
          staticOriginRealms.add(tempOriginRealms[i].trim());
        } 
      } 
    } 
    this.originRealms = staticOriginRealms.<String>toArray(new String[staticOriginRealms.size()]);
    this.originExpRealms = expOriginRealms.<String>toArray(new String[expOriginRealms.size()]);
  }
  
  private void initOriginHostIps() {
    this.originHostIps = ParserUtility.splitString(this.routingEntryData.getOriginHostIp(), new char[] { ',', ';' });
    if (this.originHostIps == null || 
      containsString(this.originHostIps, "*") || this.originHostIps.length == 0) {
      this.ALLOW_ALL_ORIGIN_IPS = true;
    } else {
      for (int i = 0; i < this.originHostIps.length; i++)
        this.originHostIps[i] = this.originHostIps[i].trim(); 
    } 
  }
  
  private void initPeerSelector(List<String> warnings) {
    ChainPeerGroupSelector chainPeerGroupSelector = new ChainPeerGroupSelector();
    if (this.routingEntryData.getSubscriberBasedRoutingTableDataList() != null)
      for (SubscriberBasedRoutingTableData subscriberBasedRoutingTableData : this.routingEntryData.getSubscriberBasedRoutingTableDataList())
        chainPeerGroupSelector.add(subscriberBasedRoutingTableData.createSelector(this.routerContext));  
    chainPeerGroupSelector.add((PeerCommunicatorGroupSelector)new RuleBasedPeerGroupSelector(this.routingEntryData
          .getPeerGroupList(), this.routerContext));
    boolean addlistener = true;
    if (this.routingAction == RoutingActions.REDIRECT)
      addlistener = this.routingEntryData.getAttachedRedirection(); 
    try {
      chainPeerGroupSelector.init(addlistener);
    } catch (InitializationFailedException e) {
      LogManager.ignoreTrace((Exception)e);
      warnings.add(e.getMessage());
    } 
    this.peerSelector = new PeerSelector((PeerCommunicatorGroupSelector)chainPeerGroupSelector, this.routerContext);
  }
  
  private void initFailureActions(List<String> warnings) {
    if (this.routingEntryData.getFailoverDataList() == null) {
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("ROUTING-ENTRY", "No Failure Configuartion defined for RoutingEntry: " + 
            getRoutingEntryName()); 
      return;
    } 
    for (DiameterFailoverConfiguration failureConfiguration : this.routingEntryData.getFailoverDataList()) {
      FailoverFailureAction failoverFailureAction;
      DropFailureAction dropFailureAction;
      PassthroughFailureAction passthroughFailureAction;
      RedirectFailureAction redirectFailureAction;
      TranslateFailureAction translateFailureAction;
      RecordFailureAction recordFailureAction = null;
      String failureArgs, errorCode = failureConfiguration.getErrorCodes();
      RoutingFailureAction failureAction = null;
      switch (failureConfiguration.getFailoverAction()) {
        case FAILOVER:
          failoverFailureAction = new FailoverFailureAction(this.routerContext, failureConfiguration.getFailoverArguments(), this.routingEntryData.getTransActionTimeOut(), this.peerSelector);
          break;
        case DROP:
          dropFailureAction = new DropFailureAction();
          break;
        case PASSTHROUGH:
          passthroughFailureAction = new PassthroughFailureAction();
          break;
        case REDIRECT:
          redirectFailureAction = new RedirectFailureAction(this.routerContext, failureConfiguration.getFailoverArguments(), this.routingEntryData.getAttachedRedirection(), this.peerSelector);
          break;
        case TRANSLATE:
          failureArgs = failureConfiguration.getFailoverArguments();
          if (failureArgs == null)
            failureArgs = this.routingEntryData.getTransMapName(); 
          translateFailureAction = new TranslateFailureAction(failureArgs, this.translationAgent);
          break;
        case RECORD:
          failureArgs = failureConfiguration.getFailoverArguments();
          recordFailureAction = new RecordFailureAction(this.routerContext, failureArgs);
          break;
        default:
          if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
            LogManager.getLogger().warn("ROUTING-ENTRY", "Invalid Failure Action: " + failureConfiguration.getFailoverAction() + " configured for Error Code(s): " + errorCode + " in Routing Entry: " + this.routingEntryData
                .getRoutingName()); 
          warnings.add("Invalid Failure Action: " + failureConfiguration.getFailoverAction() + " for Error Code(s): " + errorCode);
          break;
      } 
      if (recordFailureAction != null) {
        recordFailureAction.init();
        if (recordFailureAction.getWarnings() != null)
          warnings.addAll(recordFailureAction.getWarnings()); 
      } 
      if (errorCode != null && errorCode.trim().length() > 0) {
        String[] errorCodesArray = errorCode.split(",");
        for (int s = 0; s < errorCodesArray.length; s++) {
          try {
            int errorCodeValue = Integer.parseInt(errorCodesArray[s].trim());
            if (errorCodeValue < 0) {
              if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
                LogManager.getLogger().warn("ROUTING-ENTRY", "Invalid Error Code: " + errorCodesArray[s] + " in Routing Entry: " + this.routingEntryData
                    .getRoutingName()); 
              warnings.add("Invalid Error code: " + errorCodesArray[s]);
            } else {
              this.failureHandlerMap.put(Integer.valueOf(errorCodeValue), recordFailureAction);
            } 
          } catch (NumberFormatException e) {
            if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
              LogManager.getLogger().warn("ROUTING-ENTRY", "Invalid Error Code: " + errorCodesArray[s] + " in Routing Entry: " + this.routingEntryData.getRoutingName()); 
            warnings.add("Invalid Error code: " + errorCodesArray[s]);
          } 
        } 
        continue;
      } 
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("ROUTING-ENTRY", "No Error code configured for " + failureConfiguration.getFailoverAction() + " in Routing Entry: " + this.routingEntryData
            .getRoutingName()); 
      warnings.add("No Error code configured for " + failureConfiguration.getFailoverAction());
    } 
  }
  
  private void initApplicationIds(List<String> warnings) {
    Set<ApplicationEnum> diameterApps = new HashSet<>();
    String applicationIdValue = this.routingEntryData.getApplicationIds();
    if (applicationIdValue == null || applicationIdValue.trim().length() == 0) {
      if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
        LogManager.getLogger().info("ROUTING-ENTRY", "Routing Entry: " + this.routingEntryData.getRoutingName() + " will work for any Application, Reason: Application ID is not configured"); 
      return;
    } 
    String[] strApplicationIds = applicationIdValue.split(",");
    for (int k = 0; k < strApplicationIds.length; k++) {
      strApplicationIds[k] = strApplicationIds[k].trim();
      if ("0".equals(strApplicationIds[k])) {
        if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
          LogManager.getLogger().info("ROUTING-ENTRY", "Routing Entry: " + this.routingEntryData.getRoutingName() + " will work for any Application, Reason: Application ID: " + "0" + " is configured."); 
        return;
      } 
      long lAppId = -1L;
      try {
        lAppId = Long.parseLong(strApplicationIds[k]);
        if (lAppId < 0L) {
          if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
            LogManager.getLogger().warn("ROUTING-ENTRY", "Invalid Application ID: " + strApplicationIds[k] + " is configured for Routing Entry: " + this.routingEntryData
                .getRoutingName()); 
          warnings.add("Invalid Application ID: " + strApplicationIds[k]);
        } else {
          ApplicationEnum applicationEnum = null;
          ApplicationIdentifier applicationIdentifier = ApplicationIdentifier.fromApplicationIdentifiers(lAppId);
          if (applicationIdentifier == null) {
            final long applicationID = lAppId;
            applicationEnum = new ApplicationEnum() {
                public long getVendorId() {
                  return 0L;
                }
                
                public long getApplicationId() {
                  return applicationID;
                }
                
                public Application getApplication() {
                  return Application.UNKNOWN;
                }
                
                public ServiceTypes getApplicationType() {
                  return ServiceTypes.BOTH;
                }
                
                public String toString() {
                  return 
                    getVendorId() + ":" + 
                    
                    getApplicationId() + " [" + 
                    getApplication().getDisplayName() + "]";
                }
              };
          } 
          diameterApps.add(applicationEnum);
        } 
      } catch (NumberFormatException numberFormatException) {
        if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
          LogManager.getLogger().warn("ROUTING-ENTRY", "Invalid Application ID: " + strApplicationIds[k] + " is configured for Routing Entry: " + this.routingEntryData.getRoutingName()); 
        warnings.add("Invalid Application ID: " + strApplicationIds[k]);
      } 
    } 
    this.applicationIds = new ApplicationEnum[diameterApps.size()];
    this.applicationIds = diameterApps.<ApplicationEnum>toArray(this.applicationIds);
  }
  
  private void initAdvancedCondition(List<String> warnings) throws InitializationFailedException {
    String advancedCondition = this.routingEntryData.getAdvancedCondition();
    if (advancedCondition == null || advancedCondition.trim().length() == 0) {
      if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
        LogManager.getLogger().info("ROUTING-ENTRY", "No Advanced Condition configured for Routing Entry: " + this.routingEntryData.getRoutingName()); 
      return;
    } 
  }
  
  public boolean isApplicable(DiameterPacket diameterPacket) {
    if (!this.ALLOW_ALL_DEST_REALMS) {
      String packetDestRealm = diameterPacket.getAVPValue("0:283");
      if (packetDestRealm != null) {
        if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
          LogManager.getLogger().trace("ROUTING-ENTRY", "Comparing Destination Realm(Packet): " + packetDestRealm + " with Destination Realms(Routing Entry): " + this.routingEntryData
              .getDestRealm()); 
        if (!containsString(this.destRealms, packetDestRealm.trim()) && 
          !containsPattern(this.destExpRealms, packetDestRealm.trim()))
          return false; 
      } 
    } 
    if (this.applicationIds != null) {
      boolean isApplicationSatisfied = false;
      for (ApplicationEnum applicationIdentifier : this.applicationIds) {
        if (diameterPacket.getApplicationID() == applicationIdentifier.getApplicationId()) {
          isApplicationSatisfied = true;
          break;
        } 
      } 
      if (!isApplicationSatisfied) {
        if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
          LogManager.getLogger().trace("ROUTING-ENTRY", "Application: " + diameterPacket.getApplicationID() + " not served by Routing Entry: " + this.routingEntryData
              .getRoutingName() + " for Diameter Request with Session-ID=" + diameterPacket
              .getAVPValue("0:263")); 
        return false;
      } 
    } 
    if (!this.ALLOW_ALL_ORIGIN_REALMS) {
      String packetOriginRealm = diameterPacket.getAVPValue("0:296");
      if (packetOriginRealm != null) {
        if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
          LogManager.getLogger().trace("ROUTING-ENTRY", "Comparing Origin Realm(Packet): " + packetOriginRealm + " with Origin Realms(Routing Entry): " + this.routingEntryData
              .getOriginRealm()); 
        if (!containsString(this.originRealms, packetOriginRealm) && 
          !containsPattern(this.originExpRealms, packetOriginRealm))
          return false; 
      } 
    } 
    if (!this.ALLOW_ALL_ORIGIN_IPS) {
      String packetOriginHostIp = diameterPacket.getAVPValue("21067:65546", true);
      if (packetOriginHostIp != null && packetOriginHostIp.trim().length() > 0) {
        if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
          LogManager.getLogger().trace("ROUTING-ENTRY", "Comparing Originator Host IP(Packet): " + packetOriginHostIp + " with Originator Host IPs (Routing Entry): " + this.routingEntryData
              .getOriginHostIp()); 
        if (!containsString(this.originHostIps, packetOriginHostIp))
          return false; 
      } 
    } 
    if (this.advancedCondition != null && !this.advancedCondition.evaluate((ValueProvider)new DiameterAVPValueProvider(diameterPacket))) {
      if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
        LogManager.getLogger().trace("ROUTING-ENTRY", "Ruleset not satisfied of Routing Entry: " + this.routingEntryData
            .getRoutingName() + " for Diameter Request with Session-ID=" + diameterPacket
            .getAVPValue("0:263")); 
      return false;
    } 
    String destHostIdentity = diameterPacket.getAVPValue("0:293");
    if (destHostIdentity != null && !this.peerSelector.isKnown(destHostIdentity)) {
      if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
        LogManager.getLogger().trace("ROUTING-ENTRY", "Required Destination-Host(Packet): " + destHostIdentity + " is not available in Routing Entry: " + this.routingEntryData
            .getRoutingName()); 
      return false;
    } 
    return true;
  }
  
  private String buildWarningMsg(List<String> warnings) {
    if (warnings.size() == 0)
      return null; 
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    int count = 1;
    printWriter.println();
    for (String warning : warnings)
      printWriter.println("\t" + count++ + ". " + warning); 
    return stringWriter.toString();
  }
  
  public String getRoutingEntryName() {
    return this.routingEntryData.getRoutingName();
  }
  
  public RoutingActions getRoutingAction() {
    return this.routingAction;
  }
  
  public String getTranslationMapping() {
    return this.routingEntryData.getTransMapName();
  }
  
  public PeerSelector getPeerSelector() {
    return this.peerSelector;
  }
  
  public boolean isStatefulRoutingEnabled() {
    return this.routingEntryData.getStatefulRouting();
  }
  
  public RoutingFailureAction getFailureAction(int resultCode) {
    RoutingFailureAction failureAction = this.failureHandlerMap.get(Integer.valueOf(resultCode));
    if (failureAction == null) {
      ResultCodeCategory category = ResultCodeCategory.getResultCodeCategory(resultCode);
      failureAction = this.failureHandlerMap.get(Integer.valueOf(category.value));
    } 
    return failureAction;
  }
  
  private boolean containsPattern(String[] strings, String lookupString) {
    if (lookupString != null)
      for (int i = 0; i < strings.length; i++) {
        if (strings[i] != null && DiameterUtility.matches(lookupString, strings[i]))
          return true; 
      }  
    return false;
  }
  
  private boolean containsString(String[] strings, String lookupString) {
    if (lookupString != null)
      for (int i = 0; i < strings.length; i++) {
        if (strings[i] != null && strings[i].equals(lookupString))
          return true; 
      }  
    return false;
  }
  
  public ApplicationEnum[] getSupportedApplications() {
    return this.applicationIds;
  }
  
  public boolean isRoutingEntryExecutable() {
    if (this.warning == null)
      return true; 
    if (LogManager.getLogger().isWarnLogLevel())
      LogManager.getLogger().warn("ROUTING-ENTRY", "Routing Entry: " + this.routingEntryData.getRoutingName() + " is not being applied due to following configuration Error(s): " + this.warning); 
    return false;
  }
}
