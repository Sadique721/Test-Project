package com.diameter.commons;

public interface IStackAlertManager {
  void scheduleAlert(StackAlertSeverity paramStackAlertSeverity, IStackAlertEnum paramIStackAlertEnum, String paramString1, String paramString2);
  
  void scheduleAlert(StackAlertSeverity paramStackAlertSeverity, IStackAlertEnum paramIStackAlertEnum, String paramString1, String paramString2, int paramInt, String paramString3);
}