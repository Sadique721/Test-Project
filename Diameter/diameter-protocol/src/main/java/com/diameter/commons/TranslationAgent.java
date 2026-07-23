package com.diameter.commons;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class TranslationAgent implements ITranslationAgent {
  private static TranslationAgent translationAgent = null;
  
  private Map<String, TranslatorPolicyData> translaterPolicyDataMap = new HashMap<>();
  
  private Map<String, Translator> translatersMap = new HashMap<>();
  
  private Map<String, CopyPacketTranslator> copyPacketTranslatorMap = new ConcurrentHashMap<>();
  
  private static final String MODULE = "TRNSLTN-AGNT";
  
  public static TranslationAgent getInstance() {
    if (translationAgent == null)
      translationAgent = new TranslationAgent(); 
    return translationAgent;
  }
  
  public void registerPolicyData(TranslatorPolicyData policyData) throws PolicyDataRegistrationFailedException {
    if (policyData == null)
      return; 
    try {
      Translator translator = this.translatersMap.get(policyData.getFromTranslatorId() + policyData.getToTranslatorId());
      if (translator == null)
        throw new PolicyDataRegistrationFailedException("The requiered translator is not registered."); 
      translator.init(policyData);
      LogManager.getLogger().info("TRNSLTN-AGNT", "Translation Configuration registered for: " + policyData.getName());
    } catch (InitializationFailedException e) {
      throw new PolicyDataRegistrationFailedException(e.getMessage());
    } 
    this.translaterPolicyDataMap.put(policyData.getName(), policyData);
  }
  
  public void registerTranslator(Translator translater) {
    if (translater == null)
      return; 
    String translatorId = translater.getFromId() + translater.getToId();
    if (!this.translatersMap.containsKey(translatorId)) {
      this.translatersMap.put(translatorId, translater);
    } else if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
      LogManager.getLogger().warn("TRNSLTN-AGNT", "Translater with ID: " + translatorId + " already registered.");
    } 
  }
  
  public void registerCopyPacketTranslator(CopyPacketTranslator translator) {
    String translatorName = translator.getName();
    if (this.copyPacketTranslatorMap.containsKey(translatorName) && 
      LogManager.getLogger().isLogLevel(LogLevel.WARN))
      LogManager.getLogger().warn("TRNSLTN-AGNT", "Copy Packet Translation Configuration: " + translatorName + " is already registered, Over-Writing Translation Configuration."); 
    try {
      translator.init();
      this.copyPacketTranslatorMap.put(translatorName, translator);
      if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
        LogManager.getLogger().info("TRNSLTN-AGNT", "Registered Copy Packet Translation Configuration: " + translatorName); 
    } catch (InitializationFailedException e) {
      LogManager.getLogger().trace(e.getCause());
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("TRNSLTN-AGNT", "Copy Packet Translation Configuration: " + translatorName + " registration failed, Reason: " + e
            .getMessage()); 
    } 
  }
  
  public void translate(String policyId, TranslatorParams params, boolean isRequest) throws TranslationFailedException {
    CopyPacketTranslator copyPacketTranslator = this.copyPacketTranslatorMap.get(policyId);
    if (copyPacketTranslator != null) {
      if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
        LogManager.getLogger().info("TRNSLTN-AGNT", "Applying Packet Translation using Policy: " + policyId); 
      if (isRequest) {
        copyPacketTranslator.translateRequest(params);
        copyPacketTranslator.postTranslateRequest(params);
      } else {
        copyPacketTranslator.translateResponse(params);
        copyPacketTranslator.postTranslateResponse(params);
      } 
      return;
    } 
    TranslatorPolicyData policy = this.translaterPolicyDataMap.get(policyId);
    if (policy == null)
      throw new TranslationFailedException("Translator Policy: " + policyId + " is not registered"); 
    String translatorId = policy.getFromTranslatorId() + policy.getToTranslatorId();
    Translator translator = this.translatersMap.get(translatorId);
    if (translator == null)
      throw new TranslationFailedException("Translator for From : " + policy.getFromTranslatorId() + " To : " + policy.getToTranslatorId() + " is not registered"); 
    try {
      if (isRequest) {
        if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
          LogManager.getLogger().info("TRNSLTN-AGNT", "Translating Request Using policy: " + policy.getName()); 
        translator.translateRequest(policy.getTransMapConfId(), params);
        translator.postTranslateRequest(policy.getTransMapConfId(), params);
      } else {
        if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
          LogManager.getLogger().info("TRNSLTN-AGNT", "Translating Response Using policy: " + policy.getName()); 
        translator.translateResponse(policy.getTransMapConfId(), params);
        translator.postTranslateResponse(policy.getTransMapConfId(), params);
      } 
    } catch (Exception e) {
      throw new TranslationFailedException(e);
    } 
  }
  
  public boolean isExists(String transMappName) {
    if (transMappName == null)
      return false; 
    boolean isExists = this.copyPacketTranslatorMap.containsKey(transMappName);
    if (isExists)
      return isExists; 
    return this.translaterPolicyDataMap.containsKey(transMappName);
  }
  
  public Map<String, String> getDummyResponseMap(String translationMapping) {
    if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
      LogManager.getLogger().info("TRNSLTN-AGNT", "Fetching Dummy Mappings for Translation Configuration: " + translationMapping); 
    CopyPacketTranslator copyPacketTranslator = this.copyPacketTranslatorMap.get(translationMapping);
    if (copyPacketTranslator != null)
      return copyPacketTranslator.getDummyMappings(); 
    TranslatorPolicyData data = this.translaterPolicyDataMap.get(translationMapping);
    if (data != null)
      return data.getDummyResponseMap(); 
    return null;
  }
}
