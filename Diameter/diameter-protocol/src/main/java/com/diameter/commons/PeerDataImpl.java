package com.diameter.commons;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

public class PeerDataImpl extends PeerConnectionDataImpl implements PeerData, Cloneable {
  private static final String MODULE = "PEER-DATA";
  
  private String realmName = "";
  
  private int watchdogIntervalMs = 30000;
  
  private List<IDiameterAVP> additionalCERAVPs;
  
  private List<IDiameterAVP> additionalDPRAVPs;
  
  private List<IDiameterAVP> additionalDWRAVPs;
  
  private int initiateConnectionDuration = 0;
  
  private boolean isSessionCleanUpOnCER = true;
  
  private boolean isSessionCleanUpOnDPR = true;
  
  private boolean isSendDPRonCloseEvent = false;
  
  private boolean isMsccBasedReservationInitialRequest = false;
  
  private RedirectHostAVPFormat redirectHostAVPFormat = RedirectHostAVPFormat.DIAMETERURI;
  
  private String uri = "";
  
  private boolean followRedirection = false;
  
  private int retransmissionCount = 0;
  
  private long requestTimeout = 3000L;
  
  private String hotlinePolicy = null;
  
  private String peerName;
  
  private String strHostIdentity = "";
  
  private String exclusiveAuthAppIDs;
  
  private String exclusiveAcctAppIDs;
  
  private long peerIndex = -1L;
  
  private boolean reTransmissionComplient = true;
  
  private String dhcpIpAddress;
  
  private String haIpAddress;
  
  private String secondaryPeerName;
  
  private PeerData.DuplicateConnectionPolicyType duplicateConnectionPolicyType = PeerData.DuplicateConnectionPolicyType.DEFAULT;
  
  public PeerDataImpl() {
    this.additionalCERAVPs = new ArrayList<>();
    this.additionalDPRAVPs = new ArrayList<>();
    this.additionalDWRAVPs = new ArrayList<>();
  }
  
  public void setHostIdentity(String strHostIdentity) {
    if (strHostIdentity != null && strHostIdentity.trim().length() > 0)
      this.strHostIdentity = strHostIdentity; 
  }
  
  public boolean isFollowRedirection() {
    return this.followRedirection;
  }
  
  public void setFollowRedirection(boolean followRedirection) {
    this.followRedirection = followRedirection;
  }
  
  public RedirectHostAVPFormat getRedirectHostAVPFormat() {
    return this.redirectHostAVPFormat;
  }
  
  public void setRedirectHostAVPFormat(RedirectHostAVPFormat redirectHostAVPFormat) {
    this.redirectHostAVPFormat = redirectHostAVPFormat;
  }
  
  public void setURI(String uri) {
    this.uri = uri;
  }
  
  public String getURI() {
    return this.uri;
  }
  
  public String getPeerName() {
    return this.peerName;
  }
  
  public void setPeerName(String peerName) {
    this.peerName = peerName;
  }
  
  public void setRealmName(String realmName) {
    this.realmName = realmName;
  }
  
  public String getRealmName() {
    return this.realmName;
  }
  
  public String getHostIdentity() {
    return this.strHostIdentity;
  }
  
  public boolean isInitConnection() {
    return (this.initiateConnectionDuration > 0);
  }
  
  public String toString() {
    StringWriter stringBuffer = new StringWriter();
    PrintWriter out = new PrintWriter(stringBuffer);
    out.println("\tPeer Index: " + this.peerIndex);
    out.println("\tPeer Name: " + this.peerName);
    out.println("\tHost Identity: " + this.strHostIdentity);
    out.println("\tRealm: " + this.realmName);
    out.println("\tURI: " + this.uri);
    out.println("\tWatch Dog Interval: " + this.watchdogIntervalMs + " (ms)");
    out.println("\tRetransmission Count: " + this.retransmissionCount);
    out.println("\tRequest Timeout: " + this.requestTimeout + " (ms)");
    out.println("\tSession CleanUp on CER is enabled: " + this.isSessionCleanUpOnCER);
    out.println("\tSession CleanUp on DPR is enabled: " + this.isSessionCleanUpOnDPR);
    out.println("\tSend DPR on Close Event is enabled: " + this.isSendDPRonCloseEvent);
    out.println("\tMSCC based reservation(Initial Request) enabled: " + this.isMsccBasedReservationInitialRequest);
    out.println("\tFollow Redirection: " + this.followRedirection);
    out.println("\tRedirect-Host AVP format: " + this.redirectHostAVPFormat);
    out.println("\tInitiate Connection Duration: " + this.initiateConnectionDuration + " (ms)");
    out.println("\tDuplicate Connection Policy Type: " + this.duplicateConnectionPolicyType);
    out.println("\t-----Connection Parameter------");
    out.println(super.toString());
    out.close();
    return stringBuffer.toString();
  }
  
  public boolean equals(Object obj) {
    if (obj == this)
      return true; 
    if (Objects.isNull(obj))
      return false; 
    if (getClass() != obj.getClass())
      return false; 
    try {
      PeerDataImpl peer = (PeerDataImpl)obj;
      if (peer != null)
        return peer.peerName.equals(this.peerName); 
    } catch (ClassCastException e) {
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("PEER-DATA", "Peer conversion is not possible."); 
      LogManager.getLogger().error("PEER-DATA", "Peer conversion is not possible. Reason " + e.getMessage(), e);
    } 
    return false;
  }
  
  public void setWatchdogInterval(int watchdogIntervalMs) {
    this.watchdogIntervalMs = watchdogIntervalMs;
  }
  
  public void setCERAVPString(String cerAVPString) {
    if (cerAVPString == null || cerAVPString.trim().length() == 0) {
      this.additionalCERAVPs = new ArrayList<>(0);
      return;
    } 
    try {
      List<IDiameterAVP> cerAVPs = DiameterUtility.getDiameterAttributes(cerAVPString, new StaticValueProvider());
      if (!Collectionz.isNullOrEmpty(cerAVPs))
        this.additionalCERAVPs = cerAVPs; 
    } catch (Exception e) {
      if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
        LogManager.getLogger().error("PEER-DATA", "Failed to parse CER Avps for Peer: " + this.strHostIdentity + " , Reason :" + e.getMessage(), e); 
    } 
  }
  
  public void setDPRAVPString(String dprAVPString) {
    if (dprAVPString == null || dprAVPString.trim().length() == 0) {
      this.additionalDPRAVPs = new ArrayList<>(0);
      return;
    } 
    try {
      List<IDiameterAVP> dprAVPs = DiameterUtility.getDiameterAttributes(dprAVPString, new StaticValueProvider());
      if (!Collectionz.isNullOrEmpty(dprAVPs))
        this.additionalDPRAVPs = dprAVPs; 
    } catch (Exception e) {
      if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
        LogManager.getLogger().error("PEER-DATA", "Failed to parse DPR Avps for Peer: " + this.strHostIdentity + " , Reason :" + e.getMessage(), e); 
    } 
  }
  
  public void setDWRAVPString(String dwrAVPString) {
    if (dwrAVPString == null || dwrAVPString.trim().length() == 0) {
      this.additionalDWRAVPs = new ArrayList<>(0);
      return;
    } 
    try {
      List<IDiameterAVP> dwrAVPs = DiameterUtility.getDiameterAttributes(dwrAVPString, new StaticValueProvider());
      if (!Collectionz.isNullOrEmpty(dwrAVPs))
        this.additionalDWRAVPs = dwrAVPs; 
    } catch (Exception e) {
      if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
        LogManager.getLogger().error("PEER-DATA", "Failed to parse DWR Avps for Peer: " + this.strHostIdentity + " , Reason :" + e.getMessage(), e); 
    } 
  }
  
  public int getWatchdogInterval() {
    return this.watchdogIntervalMs;
  }
  
  public List<IDiameterAVP> getAdditionalCERAvps() {
    return getClonedAdditionalAvps("CER", this.additionalCERAVPs);
  }
  
  public List<IDiameterAVP> getAdditionalDPRAvps() {
    return getClonedAdditionalAvps("DPR", this.additionalDPRAVPs);
  }
  
  public void setInitiateConnectionDuration(Integer initiateConnectionDuration) {
    this.initiateConnectionDuration = initiateConnectionDuration.intValue();
  }
  
  public void setRetransmissionCount(Integer retransmissionCount) {
    if (retransmissionCount.intValue() < 0) {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("PEER-DATA", "Considering 0 as retransmission count for Peer " + this.peerName + ".Reason: Invalid Retry Count: " + retransmissionCount + "."); 
      this.retransmissionCount = 0;
    } else if (retransmissionCount.intValue() > 3) {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("PEER-DATA", "Considering 3 as retransmission count for Peer " + this.peerName + ".Reason: Retry count more than " + '\003' + " not receommended."); 
      this.retransmissionCount = 3;
    } else {
      this.retransmissionCount = retransmissionCount.intValue();
    } 
  }
  
  public int getInitiateConnectionDuration() {
    return this.initiateConnectionDuration;
  }
  
  public int getRetryCount() {
    return this.retransmissionCount;
  }
  
  public boolean isSessionCleanUpOnCER() {
    return this.isSessionCleanUpOnCER;
  }
  
  public boolean isSessionCleanUpOnDPR() {
    return this.isSessionCleanUpOnDPR;
  }
  
  public void setSessionCleanUpOnCER(boolean isSessionCleanUpOnCER) {
    this.isSessionCleanUpOnCER = isSessionCleanUpOnCER;
  }
  
  public void setSessionCleanUpOnDPR(boolean isSessionCleanUpOnDPR) {
    this.isSessionCleanUpOnDPR = isSessionCleanUpOnDPR;
  }
  
  public void setSendDPRonCloseEvent(boolean sendDPRonCloseEnabled) {
    this.isSendDPRonCloseEvent = sendDPRonCloseEnabled;
  }
  
  public boolean isSendDPRonCloseEvent() {
    return this.isSendDPRonCloseEvent;
  }
  
  public boolean isMsccBasedReservationInitialRequest() {
    return this.isMsccBasedReservationInitialRequest;
  }
  
  public void setMsccBasedReservationInitialRequest(boolean msccBasedReservationInitialRequest) {
    this.isMsccBasedReservationInitialRequest = msccBasedReservationInitialRequest;
  }
  
  public List<IDiameterAVP> getAdditionalDWRAvps() {
    return getClonedAdditionalAvps("DWR", this.additionalDWRAVPs);
  }
  
  private List<IDiameterAVP> getClonedAdditionalAvps(String type, List<IDiameterAVP> additionalAvps) {
    if (!Collectionz.isNullOrEmpty(additionalAvps)) {
      List<IDiameterAVP> clonedAVPs = new ArrayList<>();
      for (IDiameterAVP diameterAVP : additionalAvps) {
        try {
          clonedAVPs.add((IDiameterAVP)diameterAVP.clone());
        } catch (CloneNotSupportedException e) {
          LogManager.getLogger().error("PEER-DATA", "Additional AVP : " + diameterAVP.getAVPId() + " will not be add to " + type + ", Reason :" + e.getMessage(), e);
        } 
      } 
      return clonedAVPs;
    } 
    return Collections.emptyList();
  }
  
  public long getRequestTimeout() {
    return this.requestTimeout;
  }
  
  public void setRequestTimeout(long requestTimeoutMS) {
    if (requestTimeoutMS < 1000L) {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("PEER-DATA", "Request timeout: " + requestTimeoutMS + "ms. Timeout less than ms is not recommended, Considering  ms as timeout"); 
      this.requestTimeout = 1000L;
    } else if (requestTimeoutMS > 10000L) {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("PEER-DATA", "Request timeout: 10000ms. Timeout greater than 10000ms is not recommended, Considering 10000ms as timeout"); 
      this.requestTimeout = 10000L;
    } else {
      if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
        LogManager.getLogger().info("PEER-DATA", "Request timeout: " + requestTimeoutMS + "ms"); 
      this.requestTimeout = requestTimeoutMS;
    } 
  }
  
  @Nullable
  public String getHotlinePolicy() {
    return this.hotlinePolicy;
  }
  
  public void setHotlinePolicy(String hotlinePolicy) {
    this.hotlinePolicy = hotlinePolicy;
  }
  
  private class StaticValueProvider implements ValueProvider {
    private StaticValueProvider() {}
    
    public String getStringValue(String identifier) {
      return identifier;
    }
  }
  
  public int hashCode() {
    return this.peerName.hashCode();
  }
  
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
  
  public void setExclusiveAuthAppIDs(String strExclusiveAuthAppIds) {
    this.exclusiveAuthAppIDs = strExclusiveAuthAppIds;
  }
  
  public void setExclusiveAcctAppIDs(String strExclusiveAcctAppIds) {
    this.exclusiveAcctAppIDs = strExclusiveAcctAppIds;
  }
  
  public String getExclusiveAuthAppIDs() {
    return this.exclusiveAuthAppIDs;
  }
  
  public String getExclusiveAcctAppIDs() {
    return this.exclusiveAcctAppIDs;
  }
  
  public long getPeerIndex() {
    return this.peerIndex;
  }
  
  public void setPeerIndex(long peerIndex) {
    this.peerIndex = peerIndex;
  }
  
  public boolean isReTransmissionCompliant() {
    return this.reTransmissionComplient;
  }
  
  public void setHaIpAddress(String haIpAddress) {
    this.haIpAddress = haIpAddress;
  }
  
  public void setDhcpIpAddress(String dhcpIpAddress) {
    this.dhcpIpAddress = dhcpIpAddress;
  }
  
  public String getDHCPAddress() {
    return this.dhcpIpAddress;
  }
  
  public String getHAAddress() {
    return this.haIpAddress;
  }
  
  public String getSecondaryPeerName() {
    return this.secondaryPeerName;
  }
  
  public void setSecondaryPeerName(String secondaryPeerName) {
    this.secondaryPeerName = secondaryPeerName;
  }
  
  public void setAdditionalCERAvps(List<IDiameterAVP> additionalCERAVPs) {
    if (additionalCERAVPs != null)
      this.additionalCERAVPs = additionalCERAVPs; 
  }
  
  public void setAdditionalDWRAvps(List<IDiameterAVP> additionalDWRAVPs) {
    if (additionalDWRAVPs != null)
      this.additionalDWRAVPs = additionalDWRAVPs; 
  }
  
  public void setAdditionalDPRAvps(List<IDiameterAVP> additionalDPRAVPs) {
    if (additionalDPRAVPs != null)
      this.additionalDPRAVPs = additionalDPRAVPs; 
  }
  
  public void reload(PeerData updatedPeerData) {
    setPeerTimeout(updatedPeerData.getPeerTimeout());
    setRequestTimeout(updatedPeerData.getRequestTimeout());
    setSessionCleanUpOnCER(updatedPeerData.isSessionCleanUpOnCER());
    setSessionCleanUpOnDPR(updatedPeerData.isSessionCleanUpOnDPR());
    setAdditionalCERAvps(updatedPeerData.getAdditionalCERAvps());
    setAdditionalDPRAvps(updatedPeerData.getAdditionalDPRAvps());
    setAdditionalDWRAvps(updatedPeerData.getAdditionalDWRAvps());
    setExclusiveAcctAppIDs(updatedPeerData.getExclusiveAcctAppIDs());
    setExclusiveAuthAppIDs(updatedPeerData.getExclusiveAuthAppIDs());
    setSendDPRonCloseEvent(updatedPeerData.isSendDPRonCloseEvent());
    setMsccBasedReservationInitialRequest(updatedPeerData.isMsccBasedReservationInitialRequest());
    setSocketReceiveBufferSize(updatedPeerData.getSocketReceiveBufferSize());
    setSocketSendBufferSize(updatedPeerData.getSocketSendBufferSize());
    setTCPNagleAlgo(updatedPeerData.isNagleAlgoEnabled());
    setWatchdogInterval(updatedPeerData.getWatchdogInterval());
    setInitiateConnectionDuration(Integer.valueOf(updatedPeerData.getInitiateConnectionDuration()));
    setSecurityStandard(updatedPeerData.getSecurityStandard());
    setSSLParameter(updatedPeerData.getSSLParameter());
    setTransportProtocol(updatedPeerData.getTransportProtocol());
    setFollowRedirection(updatedPeerData.isFollowRedirection());
    setDhcpIpAddress(updatedPeerData.getDHCPAddress());
    setHaIpAddress(updatedPeerData.getHAAddress());
    setRedirectHostAVPFormat(updatedPeerData.getRedirectHostAVPFormat());
    setLocalInetAddress(updatedPeerData.getLocalInetAddress());
    setLocalIPAddress(updatedPeerData.getLocalIPAddress());
    setLocalPort(updatedPeerData.getLocalPort());
    setSecondaryPeerName(updatedPeerData.getSecondaryPeerName());
    setRetransmissionCount(Integer.valueOf(updatedPeerData.getRetryCount()));
    setDuplicateConnectionPolicyType(updatedPeerData.getDuplicateConnectionPolicyType());
  }
  
  public PeerData.DuplicateConnectionPolicyType getDuplicateConnectionPolicyType() {
    return this.duplicateConnectionPolicyType;
  }
  
  public void setDuplicateConnectionPolicyType(PeerData.DuplicateConnectionPolicyType duplicateConnectionPolicyType) {
    this.duplicateConnectionPolicyType = duplicateConnectionPolicyType;
  }
}
