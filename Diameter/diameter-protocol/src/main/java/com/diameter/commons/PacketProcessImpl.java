package com.diameter.commons;

import java.util.Map;

import com.diameter.stack.DiameterStack;

public class PacketProcessImpl implements DiameterStack.PacketProcess {
  private Packet packet;
  
  private NetworkConnectionHandler connectionHandler;
  
  private DiameterStack stack;
  
  private final Map<String, String> callerDiagnosticsMap;
  
  public PacketProcessImpl(Packet packet, NetworkConnectionHandler connectionHandler, DiameterStack stack, Map<String, String> callerDiagnosticsMap) {
    this.packet = packet;
    this.connectionHandler = connectionHandler;
    this.stack = stack;
    this.callerDiagnosticsMap = callerDiagnosticsMap;
  }
  
  public void preSubmit() {}
  
  public void postSubmit() {
    clearMDCInformation();
  }
  
  private void copyMDCInformation() {
    //
  }
  
  public void run() {
    try {
      copyMDCInformation();
      this.stack.handleReceivedMessage(this.packet, this.connectionHandler);
    } finally {
      clearMDCInformation();
    } 
  }
  
  private void clearMDCInformation() {
    //
  }
  
  public Packet getPacket() {
    return this.packet;
  }
  
  public NetworkConnectionHandler getConnectionHandler() {
    return this.connectionHandler;
  }
}