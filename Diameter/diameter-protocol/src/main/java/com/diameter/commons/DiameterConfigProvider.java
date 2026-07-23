package com.diameter.commons;

import java.util.Map;

public interface DiameterConfigProvider {
  String getAllPeerConfigSummary();
  
  String getPeerConfigSummary(String paramString);
  
  String getDCCPeerConfig(String paramString, DiameterPeerConfig paramDiameterPeerConfig);
  
  String getAllDCCPeerConfigSummary();
  
  String getDCCPeerConfig(String paramString);
  
  Map<String, DiameterPeerConfig> getPeerConfigMap();
  
  DiameterPeerConfig getPeerConfig(String paramString);
  
  DiameterPeerState getPeerState(String paramString);
}