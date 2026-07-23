package com.diameter.commons;

public class SessionReleaseIndicatorFactory {
  private static SessionReleaseIndiactor defaultSessionReleaseIndiactor = new AppDefaultSessionReleaseIndicator();
  
  public static SessionReleaseIndiactor getDefaultSessionReleaseIndiactor() {
    return defaultSessionReleaseIndiactor;
  }
}
