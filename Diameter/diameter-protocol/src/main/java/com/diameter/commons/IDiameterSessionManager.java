package com.diameter.commons;


import java.util.List;

import javax.annotation.Nullable;

public interface IDiameterSessionManager {
  public static final String MARK_FOR_DROP_REQUEST = "markForDropRequest";
  
  public static final String FURTHER_PROCESSING_REQUIRED = "isFurtherProcessingRequired";
  
  public static final String PROCESSING_COMPLETED = "processingCompleted";
  
  void init() throws InitializationFailedException;
  
  List<SessionData> locate(DiameterRequest paramDiameterRequest, @Nullable DiameterAnswer paramDiameterAnswer);
  
  int save(DiameterRequest paramDiameterRequest, DiameterAnswer paramDiameterAnswer);
  
  int update(DiameterRequest paramDiameterRequest, DiameterAnswer paramDiameterAnswer);
  
  int delete(DiameterRequest paramDiameterRequest, DiameterAnswer paramDiameterAnswer);
  
  int updateOrSave(DiameterRequest paramDiameterRequest, DiameterAnswer paramDiameterAnswer, List<SessionData> paramList);
  
  int delete(List<SessionData> paramList);
  
  int truncate();
}
