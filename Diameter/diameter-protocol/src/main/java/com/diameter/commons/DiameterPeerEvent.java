package com.diameter.commons;

public enum DiameterPeerEvent implements IEventEnum {
  Start(true),
  RConnCER(true),
  IRcvConnAck(true),
  IRcvConnNack(true),
  Timeout(true),
  IRcvCEA(true),
  IPeerDisc(true),
  IRcvNonCEA(true),
  RPeerDisc(true),
  WinElection(true),
  SendMessage(false),
  RrcvMessage(false),
  IrcvMessage(false),
  RRcvDWR(false),
  RRcvDWA(false),
  Stop(true),
  RRcvDPR(true),
  RRcvCER(false),
  RRcvCEA(false),
  IRcvDWR(false),
  IRcvDWA(false),
  IRcvDPR(true),
  IRcvCER(false),
  IRcvDPA(true),
  RRcvDPA(true);
  
  public final boolean sync;
  
  private static final DiameterPeerEvent[] DIAMETER_PEER_EVENTS;
  
  DiameterPeerEvent(boolean sync) {
    this.sync = sync;
  }
  
  public boolean isSync() {
    return this.sync;
  }
  
  public int eventOrdinal() {
    return ordinal();
  }
  
  static {
    DIAMETER_PEER_EVENTS = values();
  }
  
  public static DiameterPeerEvent getByEventOrdinal(int ordinal) {
    return DIAMETER_PEER_EVENTS[ordinal];
  }
}
