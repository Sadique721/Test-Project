package com.diameter.commons;

public abstract class DiameterRouterScript {
  protected DiameterScriptContext scriptContext;
  
  public DiameterRouterScript(DiameterScriptContext scriptContext) {
    this.scriptContext = scriptContext;
  }
  
  public final void init() throws InitializationFailedException {
    initGroovy();
  }
  
  public final void preRequest(String routingTableName, DiameterRequest originRequest, String reqOriginatorPeerId) {
    try {
      preRequestRouting(routingTableName, originRequest, reqOriginatorPeerId);
    } catch (Exception ex) {
      LogManager.getLogger().trace(ex);
      LogManager.getLogger().error(getName(), "Error in executing \"preRequest\" method of routing script: " + getName() + ". Reason: " + ex
          .getMessage());
    } 
  }
  
  public final void postRequest(String routingTableName, String routingEntryName, DiameterRequest originRequest, String reqOriginatorPeerId, DiameterRequest destinationRequest, String destPeerId) {
    try {
      postRequestRouting(routingTableName, routingEntryName, originRequest, reqOriginatorPeerId, destinationRequest, destPeerId);
    } catch (Exception ex) {
      LogManager.getLogger().trace(ex);
      LogManager.getLogger().error(getName(), "Error in executing \"postRequest\" method of routing script: " + getName() + ". Reason: " + ex
          .getMessage());
    } 
  }
  
  public final void preAnswer(String routingTableName, String routingEntryName, DiameterRequest originRequest, String ansOriginitorPeerId, DiameterRequest destinationRequest, DiameterAnswer originAnswer) {
    try {
      preAnswerRouting(routingTableName, routingEntryName, originRequest, ansOriginitorPeerId, destinationRequest, originAnswer);
    } catch (Exception ex) {
      LogManager.getLogger().trace(ex);
      LogManager.getLogger().error(getName(), "Error in executing \"preAnswer\" method of routing script: " + getName() + ". Reason: " + ex
          .getMessage());
    } 
  }
  
  public final void postAnswer(String routingTableName, String routingEntryName, DiameterRequest originRequest, String ansOriginatorPeerId, DiameterRequest destinationRequest, DiameterAnswer originAnswer, DiameterAnswer destinationAnswer, String destAnsPeerId) {
    try {
      postAnswerRouting(routingTableName, routingEntryName, originRequest, ansOriginatorPeerId, destinationRequest, originAnswer, destinationAnswer, destAnsPeerId);
    } catch (Exception ex) {
      LogManager.getLogger().trace(ex);
      LogManager.getLogger().error(getName(), "Error in executing \"postAnswer\" method of routing script: " + getName() + ". Reason: " + ex
          .getMessage());
    } 
  }
  
  protected abstract String getName();
  
  public abstract void initGroovy() throws InitializationFailedException;
  
  protected abstract void preRequestRouting(String paramString1, DiameterRequest paramDiameterRequest, String paramString2);
  
  protected abstract void postRequestRouting(String paramString1, String paramString2, DiameterRequest paramDiameterRequest1, String paramString3, DiameterRequest paramDiameterRequest2, String paramString4);
  
  protected abstract void preAnswerRouting(String paramString1, String paramString2, DiameterRequest paramDiameterRequest1, String paramString3, DiameterRequest paramDiameterRequest2, DiameterAnswer paramDiameterAnswer);
  
  protected abstract void postAnswerRouting(String paramString1, String paramString2, DiameterRequest paramDiameterRequest1, String paramString3, DiameterRequest paramDiameterRequest2, DiameterAnswer paramDiameterAnswer1, DiameterAnswer paramDiameterAnswer2, String paramString4);
}
