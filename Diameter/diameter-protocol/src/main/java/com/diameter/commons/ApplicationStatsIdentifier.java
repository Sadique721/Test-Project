package com.diameter.commons;

public class ApplicationStatsIdentifier {
  private long applicationId;
  
  private long vendorId;
  
  private String application;
  
  private int hash = -1;
  
  private String appIdentifierString;
  
  public ApplicationStatsIdentifier(long appId, long vendorId, String application) {
    this.applicationId = appId;
    this.vendorId = vendorId;
    this.application = application.toLowerCase();
    this.appIdentifierString = "Application: " + this.application.toUpperCase() + " (" + this.vendorId + ":" + this.applicationId + ")";
  }
  
  public String getApplication() {
    return this.application;
  }
  
  public long getApplicationId() {
    return this.applicationId;
  }
  
  public boolean equals(Object obj) {
    if (obj == null)
      return false; 
    if (getClass() != obj.getClass())
      return false; 
    ApplicationStatsIdentifier applicationStatsIdentifier = (ApplicationStatsIdentifier)obj;
    return (this.applicationId == applicationStatsIdentifier.applicationId && this.vendorId == applicationStatsIdentifier.vendorId);
  }
  
  public int hashCode() {
    if (this.hash == -1) {
      this.hash = 53 + (int)this.applicationId;
      this.hash = 53 * this.hash + (int)this.vendorId;
      if (this.hash < 0)
        return this.hash + Integer.MAX_VALUE; 
    } 
    return this.hash;
  }
  
  public String toString() {
    return this.appIdentifierString;
  }
}
