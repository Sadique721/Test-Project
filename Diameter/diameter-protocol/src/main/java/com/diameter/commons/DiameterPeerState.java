package com.diameter.commons;

import java.util.HashMap;
import java.util.Map;

public enum DiameterPeerState implements IStateEnum {
  Closed(true),
  Wait_Conn_Ack(true),
  Wait_I_CEA(true),
  Elect(true),
  Wait_Returns(true),
  R_Open(false),
  I_Open(false),
  Closing(true),
  Wait_Conn_Ack_Elect(true);
  
  public final boolean sync;
  
  private static Map<Integer, DiameterPeerState> diameterPeerStates;
  
  static {
    diameterPeerStates = new HashMap<>();
    for (DiameterPeerState state : values())
      diameterPeerStates.put(Integer.valueOf(state.ordinal()), state); 
  }
  
  DiameterPeerState(boolean sync) {
    this.sync = sync;
  }
  
  public IStateEnum getNextState(IEventEnum event) {
    return getNextState(this, event);
  }
  
  public static DiameterPeerState getNextState(IStateEnum state, IEventEnum event) {
    if (state == Closed) {
      if (event == DiameterPeerEvent.Start)
        return Wait_Conn_Ack; 
      if (event == DiameterPeerEvent.RConnCER)
        return R_Open; 
      return null;
    } 
    if (state == R_Open) {
      if (event == DiameterPeerEvent.SendMessage)
        return R_Open; 
      if (event == DiameterPeerEvent.RrcvMessage)
        return R_Open; 
      if (event == DiameterPeerEvent.RRcvDWR)
        return R_Open; 
      if (event == DiameterPeerEvent.RRcvDWA)
        return R_Open; 
      if (event == DiameterPeerEvent.RConnCER)
        return R_Open; 
      if (event == DiameterPeerEvent.Stop)
        return Closing; 
      if (event == DiameterPeerEvent.RRcvDPR)
        return Closed; 
      if (event == DiameterPeerEvent.RPeerDisc)
        return Closed; 
      if (event == DiameterPeerEvent.RRcvCER)
        return R_Open; 
      if (event == DiameterPeerEvent.RRcvCEA)
        return R_Open; 
      if (event == DiameterPeerEvent.IPeerDisc)
        return Closed; 
      return null;
    } 
    if (state == Closing) {
      if (event == DiameterPeerEvent.IRcvDPA)
        return Closed; 
      if (event == DiameterPeerEvent.RRcvDPA)
        return Closed; 
      if (event == DiameterPeerEvent.Timeout)
        return Closed; 
      if (event == DiameterPeerEvent.IPeerDisc)
        return Closed; 
      if (event == DiameterPeerEvent.RPeerDisc)
        return Closed; 
      if (event == DiameterPeerEvent.Start)
        return Closed; 
      if (event == DiameterPeerEvent.Stop)
        return Closed; 
      return null;
    } 
    if (state == Wait_Conn_Ack_Elect) {
      if (event == DiameterPeerEvent.IRcvConnAck)
        return Wait_I_CEA; 
      if (event == DiameterPeerEvent.IRcvConnNack)
        return Closed; 
      if (event == DiameterPeerEvent.RConnCER)
        return Wait_Conn_Ack_Elect; 
      if (event == DiameterPeerEvent.Timeout)
        return Closed; 
      if (event == DiameterPeerEvent.Start)
        return Closed; 
      if (event == DiameterPeerEvent.IPeerDisc)
        return Closed; 
      if (event == DiameterPeerEvent.RPeerDisc)
        return Closed; 
      return null;
    } 
    if (state == Wait_I_CEA) {
      if (event == DiameterPeerEvent.IRcvCEA)
        return I_Open; 
      if (event == DiameterPeerEvent.RConnCER)
        return Wait_Returns; 
      if (event == DiameterPeerEvent.IPeerDisc)
        return Closed; 
      if (event == DiameterPeerEvent.IRcvNonCEA)
        return Closed; 
      if (event == DiameterPeerEvent.Timeout)
        return Closed; 
      if (event == DiameterPeerEvent.Start)
        return Closed; 
      if (event == DiameterPeerEvent.RPeerDisc)
        return Closed; 
      return null;
    } 
    if (state == Elect) {
      if (event == DiameterPeerEvent.IRcvConnAck)
        return Wait_Returns; 
      if (event == DiameterPeerEvent.IRcvConnNack)
        return R_Open; 
      if (event == DiameterPeerEvent.RPeerDisc)
        return Wait_Conn_Ack; 
      if (event == DiameterPeerEvent.RConnCER)
        return Wait_Conn_Ack_Elect; 
      if (event == DiameterPeerEvent.Timeout)
        return Closed; 
      if (event == DiameterPeerEvent.Start)
        return Closed; 
      if (event == DiameterPeerEvent.IPeerDisc)
        return Closed; 
      return null;
    } 
    if (state == Wait_Returns) {
      if (event == DiameterPeerEvent.WinElection)
        return R_Open; 
      if (event == DiameterPeerEvent.IPeerDisc)
        return R_Open; 
      if (event == DiameterPeerEvent.IRcvCEA)
        return I_Open; 
      if (event == DiameterPeerEvent.RPeerDisc)
        return Wait_I_CEA; 
      if (event == DiameterPeerEvent.RConnCER)
        return Wait_Returns; 
      if (event == DiameterPeerEvent.Timeout)
        return Closed; 
      if (event == DiameterPeerEvent.Start)
        return Closed; 
      return null;
    } 
    if (state == I_Open) {
      if (event == DiameterPeerEvent.SendMessage)
        return I_Open; 
      if (event == DiameterPeerEvent.IrcvMessage)
        return I_Open; 
      if (event == DiameterPeerEvent.IRcvDWR)
        return I_Open; 
      if (event == DiameterPeerEvent.IRcvDWA)
        return I_Open; 
      if (event == DiameterPeerEvent.RConnCER)
        return I_Open; 
      if (event == DiameterPeerEvent.Stop)
        return Closing; 
      if (event == DiameterPeerEvent.IRcvDPR)
        return Closed; 
      if (event == DiameterPeerEvent.IPeerDisc)
        return Closed; 
      if (event == DiameterPeerEvent.IRcvCER)
        return I_Open; 
      if (event == DiameterPeerEvent.IRcvCEA)
        return I_Open; 
      if (event == DiameterPeerEvent.RPeerDisc)
        return Closed; 
      return null;
    } 
    if (state == Wait_Conn_Ack) {
      if (event == DiameterPeerEvent.IRcvConnAck)
        return Wait_I_CEA; 
      if (event == DiameterPeerEvent.IRcvConnNack)
        return Closed; 
      if (event == DiameterPeerEvent.RConnCER)
        return Wait_Conn_Ack_Elect; 
      if (event == DiameterPeerEvent.IPeerDisc)
        return Closed; 
      if (event == DiameterPeerEvent.IRcvNonCEA)
        return Closed; 
      if (event == DiameterPeerEvent.Timeout)
        return Closed; 
      if (event == DiameterPeerEvent.Start)
        return Closed; 
      if (event == DiameterPeerEvent.RPeerDisc)
        return Closed; 
      return null;
    } 
    return null;
  }
  
  public boolean isSync() {
    return this.sync;
  }
  
  public int stateOrdinal() {
    return ordinal();
  }
  
  public static DiameterPeerState fromStateOrdinal(int ordinalVal) {
    return diameterPeerStates.get(Integer.valueOf(ordinalVal));
  }
}
