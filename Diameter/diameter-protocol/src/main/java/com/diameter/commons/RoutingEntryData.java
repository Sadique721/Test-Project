package com.diameter.commons;

import java.util.ArrayList;
import java.util.List;

public interface RoutingEntryData {
  String getRoutingName();
  
  String getDestRealm();
  
  String getApplicationIds();
  
  String getOriginHostIp();
  
  String getOriginRealm();
  
  String getAdvancedCondition();
  
  String getTransMapName();
  
  int getRoutingAction();
  
  boolean getStatefulRouting();
  
  boolean getAttachedRedirection();
  
  long getTransActionTimeOut();
  
  List<PeerGroupImpl> getPeerGroupList();
  
  ArrayList<String> getSubscriberRoutingTableNames();
  
  List<DiameterFailoverConfigurationImpl> getFailoverDataList();
  
  ArrayList<SubscriberBasedRoutingTableData> getSubscriberBasedRoutingTableDataList();
}
