package com.diameter.commons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PeerApplicationProvider {
  private static final String MODULE = "PEER-APP-PROVIDER";
  
  private ApplicationContainer authApplicationContainer;
  
  private ApplicationContainer acctApplicationContainer;
  
  private Set<ApplicationEnum> remoteAuthApplications;
  
  private Set<ApplicationEnum> remoteAcctApplications;
  
  private boolean isRelayAgent;
  
  private PeerData peerData;
  
  private ApplicationProviderFactory applicationProviderFactory;
  
  public PeerApplicationProvider(IDiameterStackContext stackContext, PeerData peerData) {
    this(peerData, ApplicationProviderFactory.getInstance(stackContext));
  }
  
  public PeerApplicationProvider(PeerData peerData, ApplicationProviderFactory applicationProviderFactory) {
    this.peerData = peerData;
    this.applicationProviderFactory = applicationProviderFactory;
    this.remoteAcctApplications = new HashSet<>();
    this.remoteAuthApplications = new HashSet<>();
  }
  
  public void init() {
    this.authApplicationContainer = this.applicationProviderFactory.createApplicationContainer(this.peerData
        .getExclusiveAuthAppIDs(), ServiceTypes.AUTH);
    this.acctApplicationContainer = this.applicationProviderFactory.createApplicationContainer(this.peerData
        .getExclusiveAcctAppIDs(), ServiceTypes.ACCT);
  }
  
  public Set<ApplicationEnum> getApplications() {
    Set<ApplicationEnum> enabledApplications = new HashSet<>();
    enabledApplications.addAll(this.authApplicationContainer.getApplications());
    enabledApplications.addAll(this.acctApplicationContainer.getApplications());
    return enabledApplications;
  }
  
  public Set<ApplicationEnum> getCommonApplications() {
    Set<ApplicationEnum> commonApplications = new HashSet<>();
    if (this.isRelayAgent) {
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("PEER-APP-PROVIDER", "Adding All Enabled Applications, As Peer: " + this.peerData.getHostIdentity() + " is Relay Agent"); 
      commonApplications.addAll(this.authApplicationContainer.getApplications());
      commonApplications.addAll(this.acctApplicationContainer.getApplications());
    } else {
      commonApplications.addAll(this.authApplicationContainer.getCommonApplications(this.remoteAuthApplications));
      commonApplications.addAll(this.acctApplicationContainer.getCommonApplications(this.remoteAcctApplications));
    } 
    return commonApplications;
  }
  
  public void clear() {
    this.remoteAuthApplications = new HashSet<>();
    this.remoteAcctApplications = new HashSet<>();
    this.isRelayAgent = false;
  }
  
  public void addRemoteApplication(DiameterPacket diameterPacket) {
    this.remoteAuthApplications = buildRemoteApplication(diameterPacket, "0:258", ServiceTypes.AUTH);
    this.remoteAcctApplications = buildRemoteApplication(diameterPacket, "0:259", ServiceTypes.ACCT);
  }
  
  public Set<ApplicationEnum> getRemoteApplications() {
    Set<ApplicationEnum> remoteApplications = new HashSet<>();
    remoteApplications.addAll(this.remoteAuthApplications);
    remoteApplications.addAll(this.remoteAcctApplications);
    return remoteApplications;
  }
  
  private Set<ApplicationEnum> buildRemoteApplication(DiameterPacket diameterPacket, String applicationIdAVPCode, ServiceTypes serviceType) {
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("PEER-APP-PROVIDER", "Adding Remote " + serviceType.serviceTypeStr + " Applications for Peer: " + this.peerData
          .getHostIdentity()); 
    if (this.isRelayAgent) {
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("PEER-APP-PROVIDER", "Peer: " + this.peerData.getHostIdentity() + " is Relay Agent"); 
      return Collections.emptySet();
    } 
    StringBuilder remoteAppLst = new StringBuilder();
    Set<ApplicationEnum> remoteApplicationIdsNew = new HashSet<>();
    ArrayList<IDiameterAVP> authApplications = diameterPacket.getAVPList(applicationIdAVPCode);
    if (authApplications != null && authApplications.size() > 0)
      for (IDiameterAVP authApplicationId : authApplications) {
        if (authApplicationId.getInteger() == 4294967295L) {
          this.isRelayAgent = true;
          break;
        } 
        ApplicationEnum applicationEnum = DiameterUtility.createApplicationEnumStrictly(authApplicationId
            .getInteger(), ApplicationIdentifier.BASE
            .getVendorId(), serviceType);
        remoteAppLst.append(applicationEnum.getVendorId());
        remoteAppLst.append(':');
        remoteAppLst.append(applicationEnum.getApplicationId());
        remoteAppLst.append(',');
        remoteApplicationIdsNew.add(applicationEnum);
      }  
    if (this.isRelayAgent) {
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("PEER-APP-PROVIDER", "Peer: " + this.peerData.getHostIdentity() + " is Relay Agent"); 
      return Collections.emptySet();
    } 
    ArrayList<IDiameterAVP> vendorSpecificApplicationIds = diameterPacket.getAVPList("0:260");
    if (vendorSpecificApplicationIds != null && !vendorSpecificApplicationIds.isEmpty())
      for (IDiameterAVP vendorSpecificAttr : vendorSpecificApplicationIds) {
        long vendorId = 0L;
        long appId = -1L;
        if(vendorSpecificAttr.getGroupedAvp() !=null) {
        	for (IDiameterAVP attr : vendorSpecificAttr.getGroupedAvp()) {
                if (attr.getAVPId().equals("0:266")) {
                  vendorId = attr.getInteger();
                  continue;
                } 
                if (attr.getAVPId().equals(applicationIdAVPCode))
                  appId = attr.getInteger(); 
              } 
        }
        
        if (appId >= 0L) {
          ApplicationEnum applicationEnum = DiameterUtility.createApplicationEnumStrictly(appId, vendorId, serviceType);
          remoteAppLst.append(applicationEnum.getVendorId());
          remoteAppLst.append(':');
          remoteAppLst.append(applicationEnum.getApplicationId());
          remoteAppLst.append(',');
          remoteApplicationIdsNew.add(applicationEnum);
        } 
      }  
    if (!remoteApplicationIdsNew.isEmpty())
      remoteAppLst.deleteCharAt(remoteAppLst.length() - 1); 
    remoteAppLst.append(']');
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("PEER-APP-PROVIDER", "Remote " + serviceType.serviceTypeStr + " Applications: [" + remoteAppLst.toString()); 
    return remoteApplicationIdsNew;
  }
}
