package com.diameter.commons;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class DuplicateDetectionHandler {
  private static final int DEFAULT_ORIGINHOST_LIST_SIZE = 3;
  
  private static final String MODULE = "DUP-DETECTION-HNDLR";
  
  private int dupicatePacketPurgeIntervalInSec;
  
  private IDiameterStackContext stackContext;
  
  private DuplicateMessagePool currentMessagePool;
  
  private DuplicateMessagePool oldMessagePool;
  
  private DuplicateMessagePool oldestMessagePool;
  
  private boolean duplicateDetectionEnabled;
  
  public DuplicateDetectionHandler(IDiameterStackContext stackContext) {
    this.stackContext = stackContext;
    this.currentMessagePool = new DuplicateMessagePool();
    this.oldMessagePool = new DuplicateMessagePool();
    this.oldestMessagePool = new DuplicateMessagePool();
  }
  
  public void init() {
    if (!this.duplicateDetectionEnabled) {
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("DUP-DETECTION-HNDLR", "Not scheduling Duplicate Message Clean-Up Task , Reason: Duplicate detection is disabled."); 
      return;
    } 
    this.stackContext.scheduleIntervalBasedTask((IntervalBasedTask)new DuplicateMessagePurgeIntervalTask());
  }
  
  public boolean isDuplicate(DiameterRequest diameterRequest) {
    if (!this.duplicateDetectionEnabled)
      return false; 
    String originHost = diameterRequest.getAVPValue("0:264");
    DuplicateMessagePool messagePool = getDuplicateDetectionDatasMesssagePool((DiameterPacket)diameterRequest);
    if (messagePool == null) {
      List<DuplicateDetectionData> list1 = new ArrayList<>(3);
      List<DuplicateDetectionData> existingDuplicateDetectionDatas = this.currentMessagePool.putIfAbsent(Integer.valueOf(diameterRequest.getEnd_to_endIdentifier()), list1);
      if (existingDuplicateDetectionDatas != null)
        list1 = existingDuplicateDetectionDatas; 
      addToDuplicateDetectionDatas(list1, diameterRequest);
      return false;
    } 
    List<DuplicateDetectionData> duplicateDetectionDatas = messagePool.getOriginalMessagePool().get(Integer.valueOf(diameterRequest.getEnd_to_endIdentifier()));
    DuplicateDetectionData duplicateDetectionData = getDataByOriginHost(duplicateDetectionDatas, originHost);
    if (duplicateDetectionData == null) {
      addToDuplicateDetectionDatas(duplicateDetectionDatas, diameterRequest);
      return false;
    } 
    return true;
  }
  
  private void addToDuplicateDetectionDatas(List<DuplicateDetectionData> duplicateDetectionDatas, DiameterRequest diameterRequest) {
    String originHost = diameterRequest.getAVPValue("0:264");
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("DUP-DETECTION-HNDLR", "Accumulating Diameter Request with HbH-ID= " + diameterRequest
          .getHop_by_hopIdentifier() + ", EtE-ID= " + diameterRequest
          .getEnd_to_endIdentifier() + ", Origin-Host: " + originHost + " for Duplicate Detection."); 
    List<DuplicateDetectionData> duplicateDetectionDataList = duplicateDetectionDatas;
    synchronized (duplicateDetectionDataList) {
      duplicateDetectionDatas.add(new DuplicateDetectionData(originHost, diameterRequest
            
            .getHop_by_hopIdentifier()));
    } 
  }
  
  private DuplicateDetectionData getDataByOriginHost(List<DuplicateDetectionData> originHostToOriginalMessage, String originHost) {
    for (int i = 0; i < originHostToOriginalMessage.size(); i++) {
      if ((originHostToOriginalMessage.get(i)).originHost.equals(originHost) == true)
        return originHostToOriginalMessage.get(i); 
    } 
    return null;
  }
  
  public DiameterAnswer storeIfAbsent(DiameterRequest diameterRequest) {
    String originHost = diameterRequest.getAVPValue("0:264");
    DuplicateMessagePool messagePool = getDuplicateDetectionDatasMesssagePool((DiameterPacket)diameterRequest);
    if (messagePool == null)
      messagePool = this.currentMessagePool; 
    List<DuplicateDetectionData> duplicateDetectionDatas = messagePool.getOriginalMessagePool().get(Integer.valueOf(diameterRequest.getEnd_to_endIdentifier()));
    if (duplicateDetectionDatas == null) {
      if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
        LogManager.getLogger().info("DUP-DETECTION-HNDLR", "Duplicate Diameter Request detected with HbH-ID= " + diameterRequest
            .getHop_by_hopIdentifier() + ", EtE-ID= " + diameterRequest
            .getEnd_to_endIdentifier() + ", Origin-Host: " + originHost + ", Diameter Answer not yet received, Waiting for Response."); 
      return null;
    } 
    synchronized (duplicateDetectionDatas) {
      DuplicateDetectionData duplicateDetectionData = getDataByOriginHost(duplicateDetectionDatas, originHost);
      if (Objects.isNull(duplicateDetectionData) || duplicateDetectionData.diameterAnswer == null) {
        if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
          LogManager.getLogger().info("DUP-DETECTION-HNDLR", "Duplicate Diameter Request detected with HbH-ID= " + diameterRequest
              .getHop_by_hopIdentifier() + ", EtE-ID= " + diameterRequest
              .getEnd_to_endIdentifier() + ", Origin-Host: " + originHost + ", Diameter Answer not yet received, Waiting for Response."); 
        messagePool.getDuplicateRequestPool().remove(originHost + diameterRequest.getEnd_to_endIdentifier());
        messagePool.getDuplicateRequestPool().put(originHost + diameterRequest.getEnd_to_endIdentifier(), Integer.valueOf(diameterRequest.getHop_by_hopIdentifier()));
        return null;
      } 
      if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
        LogManager.getLogger().info("DUP-DETECTION-HNDLR", "Duplicate Diameter Request detected with HbH-ID= " + diameterRequest
            .getHop_by_hopIdentifier() + ", EtE-ID= " + diameterRequest
            .getEnd_to_endIdentifier() + ", Origin-Host: " + originHost + ", Responding with Diameter Answer stored previously."); 
      duplicateDetectionData.diameterAnswer.setHop_by_hopIdentifier(diameterRequest.getHop_by_hopIdentifier());
      return duplicateDetectionData.diameterAnswer;
    } 
  }
  
  private DuplicateMessagePool getDuplicateDetectionDatasMesssagePool(DiameterPacket diameterPacket) {
    List<DuplicateDetectionData> orginHostToOriginalMessageMap = this.currentMessagePool.getOriginalMessagePool().get(Integer.valueOf(diameterPacket.getEnd_to_endIdentifier()));
    if (orginHostToOriginalMessageMap != null)
      return this.currentMessagePool; 
    orginHostToOriginalMessageMap = this.oldMessagePool.getOriginalMessagePool().get(Integer.valueOf(diameterPacket.getEnd_to_endIdentifier()));
    if (orginHostToOriginalMessageMap != null)
      return this.oldMessagePool; 
    orginHostToOriginalMessageMap = this.oldestMessagePool.getOriginalMessagePool().get(Integer.valueOf(diameterPacket.getEnd_to_endIdentifier()));
    if (orginHostToOriginalMessageMap != null)
      return this.oldestMessagePool; 
    return null;
  }
  
  public void decorate(DiameterAnswer diameterAnswer) {
    if (!this.duplicateDetectionEnabled)
      return; 
    DuplicateMessagePool messagePool = getDuplicateDetectionDatasMesssagePool((DiameterPacket)diameterAnswer);
    if (messagePool == null) {
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("DUP-DETECTION-HNDLR", "Not accumulating Diameter Answer with HbH-ID= " + diameterAnswer
            .getHop_by_hopIdentifier() + ", EtE-ID= " + diameterAnswer
            .getEnd_to_endIdentifier() + ", Reason: original request pool is not available."); 
      return;
    } 
    List<DuplicateDetectionData> duplicateDetectionDatas = messagePool.getOriginalMessagePool().get(Integer.valueOf(diameterAnswer.getEnd_to_endIdentifier()));
    if (duplicateDetectionDatas == null) {
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("DUP-DETECTION-HNDLR", "Not accumulating Diameter Answer with HbH-ID= " + diameterAnswer
            .getHop_by_hopIdentifier() + ", EtE-ID= " + diameterAnswer
            .getEnd_to_endIdentifier() + ", Reason: original request is not found."); 
      return;
    } 
    synchronized (duplicateDetectionDatas) {
      DuplicateDetectionData duplicateDetectionData = null;
      for (int i = 0; i < duplicateDetectionDatas.size(); i++) {
        if ((duplicateDetectionDatas.get(i)).hopByhop == diameterAnswer.getHop_by_hopIdentifier()) {
          duplicateDetectionData = duplicateDetectionDatas.get(i);
          break;
        } 
      } 
      if (duplicateDetectionData == null) {
        if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
          LogManager.getLogger().debug("DUP-DETECTION-HNDLR", "Not accumulating Diameter Answer with HbH-ID= " + diameterAnswer
              .getHop_by_hopIdentifier() + ", EtE-ID= " + diameterAnswer
              .getEnd_to_endIdentifier() + ", Reason: original request is not found."); 
        return;
      } 
      Integer hopByhop = messagePool.getDuplicateRequestPool().remove(duplicateDetectionData.originHost + diameterAnswer.getEnd_to_endIdentifier());
      if (hopByhop == null) {
        duplicateDetectionData.diameterAnswer = diameterAnswer;
        if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
          LogManager.getLogger().debug("DUP-DETECTION-HNDLR", "Accumulating Diameter Answer with HbH-ID= " + diameterAnswer
              .getHop_by_hopIdentifier() + ", EtE-ID= " + diameterAnswer
              .getEnd_to_endIdentifier() + " for Duplicate Detection"); 
      } else {
        if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
          LogManager.getLogger().info("DUP-DETECTION-HNDLR", "Sending response for Duplicate Request with HbH-ID= " + diameterAnswer
              .getHop_by_hopIdentifier() + ", EtE-ID= " + diameterAnswer
              .getEnd_to_endIdentifier()); 
        diameterAnswer.setHop_by_hopIdentifier(hopByhop.intValue());
      } 
      duplicateDetectionData.diameterAnswer = diameterAnswer;
    } 
  }
  
  private class DuplicateMessagePurgeIntervalTask extends BaseIntervalBasedTask {
    private DuplicateMessagePurgeIntervalTask() {}
    
    public long getInterval() {
      return (DuplicateDetectionHandler.this.dupicatePacketPurgeIntervalInSec / 3);
    }
    
    public void execute(AsyncTaskContext context) {
      if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
        LogManager.getLogger().debug("DUP-DETECTION-HNDLR", "Flushing Diameter Messages stored for Duplicate Detection"); 
      DuplicateDetectionHandler.this.oldestMessagePool = DuplicateDetectionHandler.this.oldMessagePool;
      DuplicateDetectionHandler.this.oldMessagePool = DuplicateDetectionHandler.this.currentMessagePool;
      DuplicateDetectionHandler.this.currentMessagePool = new DuplicateDetectionHandler.DuplicateMessagePool();
    }
  }
  
  private class DuplicateMessagePool {
    private ConcurrentHashMap<Integer, List<DuplicateDetectionHandler.DuplicateDetectionData>> currentOriginalMessagePool = new ConcurrentHashMap<>();
    
    private Map<String, Integer> currentDuplicateRequestPool = new ConcurrentHashMap<>();
    
    public Map<String, Integer> getDuplicateRequestPool() {
      return this.currentDuplicateRequestPool;
    }
    
    public Map<Integer, List<DuplicateDetectionHandler.DuplicateDetectionData>> getOriginalMessagePool() {
      return this.currentOriginalMessagePool;
    }
    
    public List<DuplicateDetectionHandler.DuplicateDetectionData> putIfAbsent(Integer endToEnd, List<DuplicateDetectionHandler.DuplicateDetectionData> duplicateDetectionData) {
      List<DuplicateDetectionHandler.DuplicateDetectionData> existingData = this.currentOriginalMessagePool.putIfAbsent(endToEnd, duplicateDetectionData);
      if (existingData == null)
        return duplicateDetectionData; 
      return existingData;
    }
    
    private DuplicateMessagePool() {}
  }
  
  private class DuplicateDetectionData {
    private String originHost;
    
    private DiameterAnswer diameterAnswer;
    
    private int hopByhop;
    
    public DuplicateDetectionData(String originHost, int hopByhop) {
      this.originHost = originHost;
      this.hopByhop = hopByhop;
    }
  }
  
  public void setDupicatePacketPurgeIntervalInSec(int dupicatePacketPurgeIntervalInSec) {
    this.dupicatePacketPurgeIntervalInSec = dupicatePacketPurgeIntervalInSec;
  }
  
  public void duplicateDetectionEnabled(boolean duplicateDetectionEnabled) {
    this.duplicateDetectionEnabled = duplicateDetectionEnabled;
  }
}
