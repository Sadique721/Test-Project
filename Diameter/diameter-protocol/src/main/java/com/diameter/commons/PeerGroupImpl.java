package com.diameter.commons;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"peerList", "advancedConditionStr"})
public class PeerGroupImpl implements PeerGroup {
  private LogicalExpression ruleSet = null;
  
  private List<PeerInfoImpl> peerInfoList;
  
  private String advancedConditionStr = "";
  
  public PeerGroupImpl() {
    this.peerInfoList = new ArrayList<>();
  }
  
  public void setPeerAdvancedCondition(LogicalExpression ruleSet) {
    this.ruleSet = ruleSet;
  }
  
  public void setPeerInfoList(List<PeerInfoImpl> peerInfoList) {
    if (peerInfoList != null)
      this.peerInfoList = peerInfoList; 
  }
  
  @XmlTransient
  public LogicalExpression getRuleSet() {
    return this.ruleSet;
  }
  
  @XmlElement(name = "peer")
  public List<PeerInfoImpl> getPeerList() {
    return this.peerInfoList;
  }
  
  @XmlElement(name = "ruleset", type = String.class)
  public String getAdvancedConditionStr() {
    return this.advancedConditionStr;
  }
  
  public void setAdvancedConditionStr(String advancedConditionStr) {
    this.advancedConditionStr = advancedConditionStr;
  }
  
  public String toString() {
    StringWriter stringWriter = new StringWriter();
    PrintWriter out = new PrintWriter(stringWriter);
    out.println("\n\t\t\tAdvanced Condition = " + ((this.advancedConditionStr != null && this.advancedConditionStr.trim().length() > 0) ? this.advancedConditionStr : "*"));
    if (this.peerInfoList.size() == 0) {
      out.println("\t\t\tNo Peer is Defined For this  Peer Group.");
    } else {
      out.println("\t\t\tPeers: " + this.peerInfoList);
    } 
    return stringWriter.toString();
  }
}
