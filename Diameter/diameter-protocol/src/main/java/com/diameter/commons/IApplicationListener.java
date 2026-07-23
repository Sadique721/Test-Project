package com.diameter.commons;

public interface IApplicationListener {
  ApplicationEnum[] getApplicationEnum();
  
  void init() throws AppListenerInitializationFaildException;
  
  void handleApplicationRequest(Session paramSession, DiameterRequest paramDiameterRequest);
}