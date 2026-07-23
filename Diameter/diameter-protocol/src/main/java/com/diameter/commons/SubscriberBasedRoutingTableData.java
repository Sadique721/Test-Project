package com.diameter.commons;

public interface SubscriberBasedRoutingTableData {
  String getName();
  
  PeerCommunicatorGroupSelector createSelector(RouterContext paramRouterContext);
}

