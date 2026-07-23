package com.diameter.commons;

import java.util.EnumSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ResponseListener {
  public static final ResponseListener NO_RESPONSE_LISTENER = new ResponseListener() {
      private static final String MODULE = "NO-RES-LSTNR";
      
      public void responseReceived(DiameterAnswer diameterAnswer, String hostIdentity, DiameterSession session) {
        if (LogManager.getLogger().isDebugLogLevel())
          LogManager.getLogger().debug("NO-RES-LSTNR", "Response recieved from host: " + hostIdentity); 
      }
      
      public void requestTimedout(String hostIdentity, DiameterSession session) {
        if (LogManager.getLogger().isDebugLogLevel())
          LogManager.getLogger().debug("NO-RES-LSTNR", "Request timedout from host: " + hostIdentity); 
      }
    };
  
  public static class RetryableResultCode {
    private ResultCode resultCode;
    
    public RetryableResultCode(DiameterAnswer answer) {
      IDiameterAVP resultCodeAVP = answer.getAVP("0:268");
      if (resultCodeAVP != null)
        this.resultCode = ResultCode.fromCode((int)resultCodeAVP.getInteger()); 
    }
    
    public boolean isRetryable() {
      return ResponseListener.RETRYABLE_RESULT_CODES.contains(this.resultCode);
    }
    
    @Nullable
    public ResultCode getResultCode() {
      return this.resultCode;
    }
  }
  
  public static final EnumSet<ResultCode> RETRYABLE_RESULT_CODES = EnumSet.of(ResultCode.DIAMETER_TOO_BUSY);
  
  void requestTimedout(@Nonnull String paramString, @Nonnull DiameterSession paramDiameterSession);
  
  void responseReceived(@Nonnull DiameterAnswer paramDiameterAnswer, @Nonnull String paramString, @Nonnull DiameterSession paramDiameterSession);
}
