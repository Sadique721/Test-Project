package com.diameter.commons;

import javax.annotation.Nonnull;

public class DiameterStatisticsEvents {
  public static final int APPLICATION_TO_PEER = 1;
  
  public static final int APPLICATION_TO_PEER_CC_WISE = 2;
  
  public static final int APPLICATION_TO_PEER_RC_WISE = 3;
  
  @Nonnull
  private ApplicationStatsIdentifier applicationIdentifier;
  
  @Nonnull
  private String peerIdentity;
  
  private int commandCode;
  
  private final int type;
  
  private long resultCode;
  
  public DiameterStatisticsEvents(@Nonnull ApplicationStatsIdentifier applicationIdentifier, @Nonnull String peerIdentity) {
    this.applicationIdentifier = (ApplicationStatsIdentifier)Preconditions.checkNotNull(applicationIdentifier, "Application Idenetifier is null.");
    this.peerIdentity = (String)Preconditions.checkNotNull(peerIdentity, "peerIdentity is null.");
    this.type = 1;
  }
  
  public DiameterStatisticsEvents(@Nonnull ApplicationStatsIdentifier applicationIdentifier, @Nonnull String peerIdentity, int commandCode) {
    this.applicationIdentifier = (ApplicationStatsIdentifier)Preconditions.checkNotNull(applicationIdentifier, "Application Idenetifier is null.");
    this.peerIdentity = (String)Preconditions.checkNotNull(peerIdentity, "peerIdentity is null.");
    this.commandCode = commandCode;
    this.type = 2;
  }
  
  public DiameterStatisticsEvents(@Nonnull ApplicationStatsIdentifier applicationIdentifier, @Nonnull String peerIdentity, long resultCode) {
    this.applicationIdentifier = (ApplicationStatsIdentifier)Preconditions.checkNotNull(applicationIdentifier, "Application Idenetifier is null.");
    this.peerIdentity = (String)Preconditions.checkNotNull(peerIdentity, "peerIdentity is null.");
    this.resultCode = resultCode;
    this.type = 3;
  }
  
  public int getType() {
    return this.type;
  }
  
  public ApplicationStatsIdentifier getApplicationIdentifier() {
    return this.applicationIdentifier;
  }
  
  public int getCommandCode() {
    return this.commandCode;
  }
  
  public String getPeerIdentity() {
    return this.peerIdentity;
  }
  
  public long getResultCode() {
    return this.resultCode;
  }
}
