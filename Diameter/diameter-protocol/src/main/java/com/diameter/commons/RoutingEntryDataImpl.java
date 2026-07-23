package com.diameter.commons;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {})
public class RoutingEntryDataImpl implements RoutingEntryData {
  private String routingName;
  
  private String destRealm;
  
  private String applicationIds;
  
  private String originHostIp;
  
  private String originRealm;
  
  private String advancedConditionStr;
  
  private String transMapName;
  
  private int routingAction = RoutingActions.PROXY.routingAction;
  
  private boolean statefulRouting = true;
  
  private boolean attachedRedirection;
  
  private long transactionTimeOut = 3000L;
  
  private List<PeerGroupImpl> peerGroupsList;
  
  private List<DiameterFailoverConfigurationImpl> failoverDataList;
  
  private int hashCode;
  
  public RoutingEntryDataImpl(String destRealm, RoutingActions routingAction, String applications, List<PeerGroupImpl> peerGroups) {
    this.destRealm = destRealm;
    this.routingAction = routingAction.routingAction;
    this.applicationIds = applications;
    this.peerGroupsList = peerGroups;
  }
  
  public RoutingEntryDataImpl() {}
  
  @XmlElement(name = "name", type = String.class)
  public String getRoutingName() {
    return this.routingName;
  }
  
  public void setRoutingName(String routingName) {
    this.routingName = routingName;
  }
  
  @XmlElement(name = "destination-realm", type = String.class)
  public String getDestRealm() {
    return this.destRealm;
  }
  
  public void setDestRealm(String destRealm) {
    this.destRealm = destRealm;
  }
  
  @XmlElement(name = "application-ids", type = String.class)
  public String getApplicationIds() {
    return this.applicationIds;
  }
  
  public void setApplicationIds(String applicationIds) {
    this.applicationIds = applicationIds;
  }
  
  @XmlElement(name = "origin-host", type = String.class)
  public String getOriginHostIp() {
    return this.originHostIp;
  }
  
  public void setOriginHostIp(String originHostIp) {
    this.originHostIp = originHostIp;
  }
  
  @XmlElement(name = "origin-realm", type = String.class)
  public String getOriginRealm() {
    return this.originRealm;
  }
  
  public void setOriginRealm(String realm) {
    this.originRealm = realm;
  }
  
  @XmlElement(name = "ruleset", type = String.class)
  public String getAdvancedCondition() {
    return this.advancedConditionStr;
  }
  
  public void setAdvancedCondition(String advancedConditionStr) {
    this.advancedConditionStr = advancedConditionStr;
  }
  
  @XmlElement(name = "translation-mapping", type = String.class)
  public String getTransMapName() {
    return this.transMapName;
  }
  
  public void setTransMapName(String transMapName) {
    this.transMapName = transMapName;
  }
  
  @XmlElement(name = "routing-action", type = int.class, defaultValue = "2")
  public int getRoutingAction() {
    return this.routingAction;
  }
  
  public void setRoutingAction(int routingActionInt) {
    this.routingAction = routingActionInt;
  }
  
  @XmlElement(name = "stateful-routing", type = boolean.class, defaultValue = "true")
  public boolean getStatefulRouting() {
    return this.statefulRouting;
  }
  
  public void setStatefulRouting(boolean statefullRouting) {
    this.statefulRouting = statefullRouting;
  }
  
  @XmlElement(name = "attached-redirection", type = boolean.class)
  public boolean getAttachedRedirection() {
    return this.attachedRedirection;
  }
  
  public void setAttachedRedirection(boolean attachedRedirection) {
    this.attachedRedirection = attachedRedirection;
  }
  
  @XmlElement(name = "transaction-time-out", type = long.class, defaultValue = "3000")
  public long getTransActionTimeOut() {
    return this.transactionTimeOut;
  }
  
  public void setTransActionTimeOut(long transActionTimeOut) {
    this.transactionTimeOut = transActionTimeOut;
  }
  
  @XmlElementWrapper(name = "peer-groups")
  @XmlElement(name = "peer-group")
  public List<PeerGroupImpl> getPeerGroupList() {
    return this.peerGroupsList;
  }
  
  public void setPeerGroupList(List<PeerGroupImpl> peerGroupsList) {
    this.peerGroupsList = peerGroupsList;
  }
  
  @XmlElementWrapper(name = "failure-actions")
  @XmlElement(name = "failure-action")
  public List<DiameterFailoverConfigurationImpl> getFailoverDataList() {
    return this.failoverDataList;
  }
  
  public void setFailoverDataList(List<DiameterFailoverConfigurationImpl> failoverDataList) {
    if (failoverDataList != null)
      this.failoverDataList = failoverDataList; 
  }
  
  public boolean equals(Object obj) {
    if (obj == null)
      return false; 
    if (getClass() != obj.getClass())
      return false; 
    RoutingEntryData routingEntryData = (RoutingEntryData)obj;
    return routingEntryData.getRoutingName().equals(this.routingName);
  }
  
  public int hashCode() {
    if (this.hashCode == 0)
      this.hashCode = this.routingName.hashCode(); 
    return this.hashCode;
  }
  
  public String toString() {
    StringWriter stringWriter = new StringWriter();
    PrintWriter out = new PrintWriter(stringWriter);
    out.println();
    out.println("\tRouting Entry Name\t\t      = " + this.routingName);
    out.println("\tDestination Realm              = " + this.destRealm);
    out.println("\tOrigin Host-Ip                 = " + this.originHostIp);
    out.println("\tOrigin Realm                   = " + this.originRealm);
    out.println("\tTransaction Timeout            = " + this.transactionTimeOut);
    out.println("\tStateful Routing               = " + (this.statefulRouting ? "Enabled" : "Disabled"));
    out.println("\tAttached Redirection           = " + (this.attachedRedirection ? "Enabled" : "Disabled"));
    if (this.transMapName != null && this.transMapName.length() > 0) {
      out.println("\tTranslation Mapping Name   = " + this.transMapName);
    } else {
      out.println("\tTranslation Mapping Not Configured");
    } 
    out.println("\tAdvanced Condition             = " + ((this.advancedConditionStr != null) ? this.advancedConditionStr : "*"));
    out.println("\tRouting Action                 = " + RoutingActions.getActionString(this.routingAction));
    out.println("\tApplication IDs\t\t\t\t  = " + this.applicationIds);
    int failoverListSize = this.failoverDataList.size();
    for (int i = 0; i < failoverListSize; i++)
      out.println("\t" + ((DiameterFailoverConfigurationImpl)this.failoverDataList.get(i)).toString()); 
    int peerGroupLen = this.peerGroupsList.size();
    for (int j = 0; j < peerGroupLen; j++)
      out.print("\t\tDiameter Peer Group:\t" + ((PeerGroupImpl)this.peerGroupsList.get(j)).toString()); 
    return stringWriter.toString();
  }
  
  public ArrayList<String> getSubscriberRoutingTableNames() {
    return new ArrayList<>();
  }
  
  public ArrayList<SubscriberBasedRoutingTableData> getSubscriberBasedRoutingTableDataList() {
    return new ArrayList<>();
  }
}
