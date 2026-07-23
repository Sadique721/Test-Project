package com.diameter.commons;

public class RealmIdentifier {
  private String dbpRealmMessageRouteRealm;
  
  private long dbpRealmMessageRouteApp;
  
  private ServiceTypes dbpRealmMessageRouteType;
  
  private RoutingActions dbpRealmMessageRouteAction;
  
  private int hash = -1;
  
  public RealmIdentifier(String realmName, long realmApp, ServiceTypes appType, RoutingActions routeAction) {
    this.dbpRealmMessageRouteRealm = realmName;
    this.dbpRealmMessageRouteApp = realmApp;
    this.dbpRealmMessageRouteType = appType;
    this.dbpRealmMessageRouteAction = routeAction;
  }
  
  public String getDbpRealmMessageRouteRealm() {
    return this.dbpRealmMessageRouteRealm;
  }
  
  public long getDbpRealmMessageRouteApp() {
    return this.dbpRealmMessageRouteApp;
  }
  
  public ServiceTypes getDbpRealmMessageRouteType() {
    return this.dbpRealmMessageRouteType;
  }
  
  public RoutingActions getDbpRealmMessageRouteAction() {
    return this.dbpRealmMessageRouteAction;
  }
  
  public boolean equals(Object obj) {
    if (obj == null)
      return false; 
    if (obj == this)
      return true; 
    if (getClass() != obj.getClass())
      return false; 
    try {
      RealmIdentifier realmIdentifier = (RealmIdentifier)obj;
      if (this.dbpRealmMessageRouteAction == realmIdentifier.dbpRealmMessageRouteAction && this.dbpRealmMessageRouteApp == realmIdentifier.dbpRealmMessageRouteApp && this.dbpRealmMessageRouteType == realmIdentifier.dbpRealmMessageRouteType && this.dbpRealmMessageRouteRealm
        
        .equalsIgnoreCase(realmIdentifier.dbpRealmMessageRouteRealm))
        return true; 
    } catch (ClassCastException classCastException) {}
    return false;
  }
  
  public int hashCode() {
    if (this.hash == -1) {
      int hash = this.dbpRealmMessageRouteRealm.hashCode();
      hash = 31 * hash + this.dbpRealmMessageRouteAction.hashCode();
      hash = 31 * hash + (int)this.dbpRealmMessageRouteApp;
      hash = 31 * hash + this.dbpRealmMessageRouteType.hashCode();
      if (hash < 0)
        return hash + Integer.MAX_VALUE; 
    } 
    return this.hash;
  }
  
  public String getRealmName() {
    return this.dbpRealmMessageRouteRealm + "(" + this.dbpRealmMessageRouteAction.routingActionStr + ") " + this.dbpRealmMessageRouteApp + " (" + this.dbpRealmMessageRouteType.serviceTypeStr + ")";
  }
}
