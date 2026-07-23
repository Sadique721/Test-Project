package com.diameter.commons;

public class StatusListenerRegistrationFailException extends Exception {
  private ListenerRegFailResultCode resultCode;
  
  private static final long serialVersionUID = 1L;
  
  public StatusListenerRegistrationFailException(ListenerRegFailResultCode resultCode) {
    setResultCode(resultCode);
  }
  
  public StatusListenerRegistrationFailException(String message, ListenerRegFailResultCode resultCode) {
    super(message);
    setResultCode(resultCode);
  }
  
  public StatusListenerRegistrationFailException(String message, Throwable cause, ListenerRegFailResultCode resultCode) {
    super(message, cause);
    setResultCode(resultCode);
  }
  
  public StatusListenerRegistrationFailException(Throwable cause, ListenerRegFailResultCode resultCode) {
    super(cause);
    setResultCode(resultCode);
  }
  
  public String getMessage() {
    return super.getMessage() + ", Result-Code: " + this.resultCode;
  }
  
  private void setResultCode(ListenerRegFailResultCode resultCode) {
    this.resultCode = resultCode;
  }
  
  public ListenerRegFailResultCode getResultCode() {
    return this.resultCode;
  }
  
  public enum ListenerRegFailResultCode {
    STACK_NOT_INITIALIZED("STACK NOT INITIALIZED", StatusListenerRegistrationFailException.ResultCodeCategory.TEMPORARY_CATEGORY),
    STARTUP_IN_PROGRESS("STARTUP IN PROGRESS", StatusListenerRegistrationFailException.ResultCodeCategory.TEMPORARY_CATEGORY),
    STOP_CALLED("STOP CALLED", StatusListenerRegistrationFailException.ResultCodeCategory.PERMENENT_CATEGORY),
    PEER_NOT_FOUND("PEER NOT FOUND", StatusListenerRegistrationFailException.ResultCodeCategory.PERMENENT_CATEGORY),
    OTHER("OTHER", StatusListenerRegistrationFailException.ResultCodeCategory.PERMENENT_CATEGORY),
    UNKNOWN("UNKNOWN", StatusListenerRegistrationFailException.ResultCodeCategory.PERMENENT_CATEGORY);
    
    public final String value;
    
    public final StatusListenerRegistrationFailException.ResultCodeCategory category;
    
    ListenerRegFailResultCode(String value, StatusListenerRegistrationFailException.ResultCodeCategory resultCodeCategory) {
      this.value = value;
      this.category = resultCodeCategory;
    }
  }
  
  public enum ResultCodeCategory {
    PERMENENT_CATEGORY, TEMPORARY_CATEGORY;
  }
}
