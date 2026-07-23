package com.diameter.commons;

import java.util.List;

public interface RoutingFailureAction {
  FailureActionResult process(DiameterAnswer paramDiameterAnswer, DiameterSession paramDiameterSession, DiameterRequest paramDiameterRequest1, DiameterRequest paramDiameterRequest2, String paramString1, String paramString2);
  
  void init();
  
  List<String> getWarnings();
}