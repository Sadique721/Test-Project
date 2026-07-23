package com.diameter.commons;

import java.util.List;

public interface PeerGroup {
  LogicalExpression getRuleSet();
  
  List<PeerInfoImpl> getPeerList();
  
  String getAdvancedConditionStr();
}
