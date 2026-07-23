package com.diameter.commons;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

public class GroupedStatistics {
  private Map<Integer, CounterTuple> commandCodeCountersMap;
  
  private Map<Integer, ResultCodeTuple> resultCodeCountersMap;
  
  private Map<Integer, ConcurrentHashMap<Integer, ResultCodeTuple>> cmdWiseResultCodeCountersMap;
  
  private CounterTuple totalCounters;
  
  private Date resetTime;
  
  private ResultCodeTupleFactory resultCodeTupleFactory;
  
  private CounterTupleFactory counterTupleFactory;
  
  private static ThreadLocal<SimpleDateFormat> simpleDateFormatPool = new ThreadLocal<SimpleDateFormat>() {
      protected SimpleDateFormat initialValue() {
        return new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
      }
    };
  
  public GroupedStatistics() {
    this.commandCodeCountersMap = new ConcurrentHashMap<>(8, 0.75F, 4);
    this.resultCodeCountersMap = new ConcurrentHashMap<>(8, 0.75F, 4);
    this.cmdWiseResultCodeCountersMap = new ConcurrentHashMap<>(8, 0.75F, 4);
    this.totalCounters = new CounterTuple();
    this.resetTime = new Date();
    this.resultCodeTupleFactory = new ResultCodeTupleFactory();
    this.counterTupleFactory = new CounterTupleFactory();
  }
  
  public void incrementInputStatistics(DiameterPacket packet) {
    if (packet.isRequest()) {
      incrementRequestInStatistics(packet);
    } else {
      incrementAnswerInStatistics(packet);
    } 
  }
  
  public void incrementOutputStatistics(DiameterPacket packet) {
    if (packet.isRequest()) {
      incrementRequestOutStatistics(packet);
    } else {
      incrementAnswerOutStatistics(packet);
    } 
  }
  
  private void incrementAnswerOutStatistics(DiameterPacket answer) {
    CounterTuple counterTuple = getCommandCodeCounterTuple(answer);
    counterTuple.incrementAnswerOutCount(answer);
    this.totalCounters.incrementAnswerOutCount(answer);
    IDiameterAVP resultCodeAvp = answer.getAVP("0:268");
    if (resultCodeAvp == null) {
      AvpGrouped experimentalResultCode = (AvpGrouped)answer.getAVP("0:297");
      if (experimentalResultCode == null || experimentalResultCode.getGroupedAvp().size() == 0)
        return; 
      resultCodeAvp = experimentalResultCode.getSubAttribute("0:298");
      if (resultCodeAvp == null)
        return; 
    } 
    int resultCode = (int)resultCodeAvp.getInteger();
    ResultCodeTuple resultCodeTuple = getResultCodeTuple(answer.getCommandCode(), resultCode);
    resultCodeTuple.incrementResultCodeOut(answer);
    resultCodeTuple = getCmdWiseResultCodeTuple(answer.getCommandCode(), resultCode);
    resultCodeTuple.incrementResultCodeOut(answer);
    int resultCodeCategory = (ResultCodeCategory.getResultCodeCategory(resultCode)).value;
    resultCodeTuple = getResultCodeTuple(answer.getCommandCode(), resultCodeCategory);
    resultCodeTuple.incrementResultCodeOut(answer);
    resultCodeTuple = getCmdWiseResultCodeTuple(answer.getCommandCode(), resultCodeCategory);
    resultCodeTuple.incrementResultCodeOut(answer);
  }
  
  public void incrementUnknownH2HDropCount(DiameterAnswer answer) {
    CounterTuple counterTuple = getCommandCodeCounterTuple((DiameterPacket)answer);
    counterTuple.incrementUnknownHbHAnswerDroppedCount();
    this.totalCounters.incrementUnknownHbHAnswerDroppedCount();
  }
  
  private void incrementRequestOutStatistics(DiameterPacket request) {
    CounterTuple counterTuple = getCommandCodeCounterTuple(request);
    this.totalCounters.incrementRequestOutCount(request);
    counterTuple.incrementRequestOutCount(request);
    if (request.isReTransmitted()) {
      this.totalCounters.incrementRequestsRetransmittedCount();
      counterTuple.incrementRequestsRetransmittedCount();
    } 
  }
  
  private void incrementAnswerInStatistics(DiameterPacket answer) {
    CounterTuple counterTuple = getCommandCodeCounterTuple(answer);
    this.totalCounters.incrementAnswerInCount(answer);
    counterTuple.incrementAnswerInCount(answer);
    IDiameterAVP resultCodeAvp = answer.getAVP("0:268");
    if (resultCodeAvp == null) {
      AvpGrouped experimentalResultCode = (AvpGrouped)answer.getAVP("0:297");
      if (experimentalResultCode == null || experimentalResultCode.getGroupedAvp().size() == 0)
        return; 
      resultCodeAvp = experimentalResultCode.getSubAttribute("0:298");
      if (resultCodeAvp == null)
        return; 
    } 
    int resultCode = (int)resultCodeAvp.getInteger();
    ResultCodeTuple resultCodeTuple = getResultCodeTuple(answer.getCommandCode(), resultCode);
    resultCodeTuple.incrementResultCodeIn(answer);
    resultCodeTuple = getCmdWiseResultCodeTuple(answer.getCommandCode(), resultCode);
    resultCodeTuple.incrementResultCodeIn(answer);
    int resultCodeCategory = (ResultCodeCategory.getResultCodeCategory(resultCode)).value;
    resultCodeTuple = getResultCodeTuple(answer.getCommandCode(), resultCodeCategory);
    resultCodeTuple.incrementResultCodeIn(answer);
    resultCodeTuple = getCmdWiseResultCodeTuple(answer.getCommandCode(), resultCodeCategory);
    resultCodeTuple.incrementResultCodeIn(answer);
  }
  
  private void incrementRequestInStatistics(DiameterPacket request) {
    CounterTuple counterTuple = getCommandCodeCounterTuple(request);
    this.totalCounters.incrementRequestInCount(request);
    counterTuple.incrementRequestInCount(request);
  }
  
  public void incrementMalformedPacketCount(DiameterPacket packet) {
    CounterTuple counterTuple = getCommandCodeCounterTuple(packet);
    counterTuple.incrementMalformedPacketReceivedCount();
    this.totalCounters.incrementMalformedPacketReceivedCount();
  }
  
  public void incrementTimeoutRequestCount(DiameterRequest request) {
    CounterTuple counterTuple = getCommandCodeCounterTuple((DiameterPacket)request);
    counterTuple.incrementTimeoutRequestStatistics();
    this.totalCounters.incrementTimeoutRequestStatistics();
  }
  
  public void incrementDuplicatePacketCount(DiameterPacket packet) {
    CounterTuple counterTuple = getCommandCodeCounterTuple(packet);
    if (packet.isRequest()) {
      this.totalCounters.incrementDuplicateRequestCount();
      counterTuple.incrementDuplicateRequestCount();
    } else {
      this.totalCounters.incrementDuplicateEtEAnswerCount();
      counterTuple.incrementDuplicateEtEAnswerCount();
    } 
  }
  
  public void incrementPacketDroppedCount(DiameterPacket packet) {
    CounterTuple counterTuple = getCommandCodeCounterTuple(packet);
    if (packet.isRequest()) {
      counterTuple.incrementRequestDroppedCount();
      this.totalCounters.incrementRequestDroppedCount();
    } else {
      counterTuple.incrementAnswerDroppedCount();
      this.totalCounters.incrementAnswerDroppedCount();
    } 
  }
  
  public long getRequestInCount(int commandCode) {
    CounterTuple counter = this.commandCodeCountersMap.get(Integer.valueOf(commandCode));
    if (counter != null)
      return counter.getRequestInCount(); 
    return 0L;
  }
  
  public long getTotalPendingRequestsCount() {
    return this.totalCounters.getPendingRequestCount();
  }
  
  public long getTotalRequestInCount() {
    return this.totalCounters.getRequestInCount();
  }
  
  public long getRequestOutCount(int commandCode) {
    CounterTuple counter = this.commandCodeCountersMap.get(Integer.valueOf(commandCode));
    if (counter != null)
      return counter.getRequestOutCount(); 
    return 0L;
  }
  
  public long getPendingRequestsCount(int commandCode) {
    CounterTuple counter = this.commandCodeCountersMap.get(Integer.valueOf(commandCode));
    if (counter != null)
      return counter.getPendingRequestCount(); 
    return 0L;
  }
  
  public long getTotalRequestOutCount() {
    return this.totalCounters.getRequestOutCount();
  }
  
  public long getAnswerInCount(int commandCode) {
    CounterTuple counter = this.commandCodeCountersMap.get(Integer.valueOf(commandCode));
    if (counter != null)
      return counter.getAnswerInCount(); 
    return 0L;
  }
  
  public long getTotalAnswerInCount() {
    return this.totalCounters.getAnswerInCount();
  }
  
  public long getAnswerOutCount(int commandCode) {
    CounterTuple counter = this.commandCodeCountersMap.get(Integer.valueOf(commandCode));
    if (counter != null)
      return counter.getAnswerOutCount(); 
    return 0L;
  }
  
  public long getTotalAnswerOutCount() {
    return this.totalCounters.getAnswerOutCount();
  }
  
  public long getRequestDroppedCount(int commandCode) {
    CounterTuple counter = this.commandCodeCountersMap.get(Integer.valueOf(commandCode));
    if (counter != null)
      return counter.getRequestDroppedCount(); 
    return 0L;
  }
  
  public long getTotalRequestDroppedCount() {
    return this.totalCounters.getRequestDroppedCount();
  }
  
  public long getAnswerDroppedCount(int commandCode) {
    CounterTuple counter = this.commandCodeCountersMap.get(Integer.valueOf(commandCode));
    if (counter != null)
      return counter.getAnswerDroppedCount(); 
    return 0L;
  }
  
  public long getTotalAnswerDroppedCount() {
    return this.totalCounters.getAnswerDroppedCount();
  }
  
  public long getUnknownHbHAnswerDroppedCount(int commandCode) {
    CounterTuple counter = this.commandCodeCountersMap.get(Integer.valueOf(commandCode));
    if (counter != null)
      return counter.getUnknownHbHAnswerDroppedCount(); 
    return 0L;
  }
  
  public long getTotalUnknownHbHAnswerDroppedCount() {
    return this.totalCounters.getUnknownHbHAnswerDroppedCount();
  }
  
  public long getDuplicateEtEAnswerCount(int commandCode) {
    CounterTuple counter = this.commandCodeCountersMap.get(Integer.valueOf(commandCode));
    if (counter != null)
      return counter.getDuplicateEtEAnswerCount(); 
    return 0L;
  }
  
  public long getTotalDuplicateEtEAnswerCount() {
    return this.totalCounters.getDuplicateEtEAnswerCount();
  }
  
  public long getDuplicateRequestCount(int commandCode) {
    CounterTuple counter = this.commandCodeCountersMap.get(Integer.valueOf(commandCode));
    if (counter != null)
      return counter.getDuplicateRequestCount(); 
    return 0L;
  }
  
  public long getTotalDuplicateRequestCount() {
    return this.totalCounters.getDuplicateRequestCount();
  }
  
  public long getMalformedPacketInCount(int commandCode) {
    CounterTuple counter = this.commandCodeCountersMap.get(Integer.valueOf(commandCode));
    if (counter != null)
      return counter.getMalformedPacketReceivedCount(); 
    return 0L;
  }
  
  public long getTotalMalformedPacketInCount() {
    return this.totalCounters.getMalformedPacketReceivedCount();
  }
  
  public long getRequestsRetransmittedCount(int commandCode) {
    CounterTuple counter = this.commandCodeCountersMap.get(Integer.valueOf(commandCode));
    if (counter != null)
      return counter.getRequestsRetransmittedCount(); 
    return 0L;
  }
  
  public long getTotalRequestsRetransmittedCount() {
    return this.totalCounters.getRequestsRetransmittedCount();
  }
  
  public long getTimeoutRequestCount(int commandCode) {
    CounterTuple counter = this.commandCodeCountersMap.get(Integer.valueOf(commandCode));
    if (counter != null)
      return counter.getTimeoutRequestStatistics(); 
    return 0L;
  }
  
  public long getTotalTimeoutRequestCount() {
    return this.totalCounters.getTimeoutRequestStatistics();
  }
  
  public Date getLastResetTime() {
    return this.resetTime;
  }
  
  CounterTuple getCommandCodeCounterTuple(DiameterPacket packet) {
    int commandCode = packet.getCommandCode();
    CounterTuple commandCodeCounter = this.commandCodeCountersMap.get(Integer.valueOf(commandCode));
    if (commandCodeCounter == null)
      synchronized (this.commandCodeCountersMap) {
        commandCodeCounter = this.commandCodeCountersMap.get(Integer.valueOf(commandCode));
        if (commandCodeCounter == null) {
          commandCodeCounter = this.counterTupleFactory.getCounterTuple(packet);
          this.commandCodeCountersMap.put(Integer.valueOf(commandCode), commandCodeCounter);
        } 
      }  
    return commandCodeCounter;
  }
  
  public long getResultCodeInCount(int resultCode) {
    ResultCodeTuple counter = this.resultCodeCountersMap.get(Integer.valueOf(resultCode));
    if (counter != null)
      return counter.getResultCodeIn(); 
    return 0L;
  }
  
  public long getResultCodeOutCount(int resultCode) {
    ResultCodeTuple counter = this.resultCodeCountersMap.get(Integer.valueOf(resultCode));
    if (counter != null)
      return counter.getResultCodeOut(); 
    return 0L;
  }
  
  ResultCodeTuple getResultCodeTuple(int commandCode, int resultCode) {
    ResultCodeTuple resultCodeCouter = this.resultCodeCountersMap.get(Integer.valueOf(resultCode));
    if (resultCodeCouter == null)
      synchronized (this.resultCodeCountersMap) {
        resultCodeCouter = this.resultCodeCountersMap.get(Integer.valueOf(resultCode));
        if (resultCodeCouter == null) {
          resultCodeCouter = this.resultCodeTupleFactory.getResultCodeTuple(commandCode);
          this.resultCodeCountersMap.put(Integer.valueOf(resultCode), resultCodeCouter);
        } 
      }  
    return resultCodeCouter;
  }
  
  private ResultCodeTuple getCmdWiseResultCodeTuple(int commandCode, int resultCode) {
    ConcurrentHashMap<Integer, ResultCodeTuple> resultCodeCounters = this.cmdWiseResultCodeCountersMap.get(Integer.valueOf(commandCode));
    if (resultCodeCounters == null)
      synchronized (this.cmdWiseResultCodeCountersMap) {
        resultCodeCounters = this.cmdWiseResultCodeCountersMap.get(Integer.valueOf(commandCode));
        if (resultCodeCounters == null) {
          resultCodeCounters = new ConcurrentHashMap<>(8, 0.75F, 4);
          this.cmdWiseResultCodeCountersMap.put(Integer.valueOf(commandCode), resultCodeCounters);
        } 
      }  
    ResultCodeTuple resultCodeCouter = resultCodeCounters.get(Integer.valueOf(resultCode));
    if (resultCodeCouter == null)
      synchronized (resultCodeCounters) {
        resultCodeCouter = resultCodeCounters.get(Integer.valueOf(resultCode));
        if (resultCodeCouter == null) {
          resultCodeCouter = this.resultCodeTupleFactory.getResultCodeTuple(commandCode);
          resultCodeCounters.put(Integer.valueOf(resultCode), resultCodeCouter);
        } 
      }  
    return resultCodeCouter;
  }
  
  public String toString() {
    TableFormatter output = new TableFormatter(new String[0], new int[] { 70 }, 3);
    output.add("Reset Time    : " + ((SimpleDateFormat)simpleDateFormatPool.get()).format(this.resetTime), 0);
    output.addNewLine();
    TableFormatter formatter1 = new TableFormatter(new String[] { "CMD", "R-Rx", "A-Tx", "R-Tx", "A-Rx", "R-Rt", "R-To" }, new int[] { 5, 10, 10, 10, 10, 10, 10 }, new int[] { 0, 2, 2, 2, 2, 2, 2 }, 2);
    TableFormatter formatter2 = new TableFormatter(new String[] { "CMD", "R-Dr", "A-Dr", "A-Un", "R-Du", "A-Du", "Mf-Msg", "R-Pn" }, new int[] { 5, 9, 9, 8, 8, 8, 8, 8 }, new int[] { 0, 2, 2, 2, 2, 2, 2, 2 }, 2);
    formatter1.addRecord(new String[] { "Total", 
          
          getDisplayValue(this.totalCounters.getRequestInCount(), 10), 
          getDisplayValue(this.totalCounters.getAnswerOutCount(), 10), 
          getDisplayValue(this.totalCounters.getRequestOutCount(), 10), 
          getDisplayValue(this.totalCounters.getAnswerInCount(), 10), 
          getDisplayValue(this.totalCounters.getRequestsRetransmittedCount(), 10), 
          getDisplayValue(this.totalCounters.getTimeoutRequestStatistics(), 10) });
    formatter2.addRecord(new String[] { "Total", 
          
          getDisplayValue(this.totalCounters.getRequestDroppedCount(), 9), 
          getDisplayValue(this.totalCounters.getAnswerDroppedCount(), 9), 
          getDisplayValue(this.totalCounters.getUnknownHbHAnswerDroppedCount(), 8), 
          getDisplayValue(this.totalCounters.getDuplicateRequestCount(), 8), 
          getDisplayValue(this.totalCounters.getDuplicateEtEAnswerCount(), 8), 
          getDisplayValue(this.totalCounters.getMalformedPacketReceivedCount(), 8), 
          getDisplayValue(this.totalCounters.getPendingRequestCount(), 8) });
    for (Map.Entry<Integer, CounterTuple> entry : this.commandCodeCountersMap.entrySet()) {
      formatter1.addRecord(new String[] { CommandCode.getDisplayName(((Integer)entry.getKey()).intValue()), 
            getDisplayValue(((CounterTuple)entry.getValue()).getRequestInCount(), 10), 
            getDisplayValue(((CounterTuple)entry.getValue()).getAnswerOutCount(), 10), 
            getDisplayValue(((CounterTuple)entry.getValue()).getRequestOutCount(), 10), 
            getDisplayValue(((CounterTuple)entry.getValue()).getAnswerInCount(), 10), 
            getDisplayValue(((CounterTuple)entry.getValue()).getRequestsRetransmittedCount(), 10), 
            getDisplayValue(((CounterTuple)entry.getValue()).getTimeoutRequestStatistics(), 10) });
      formatter2.addRecord(new String[] { CommandCode.getDisplayName(((Integer)entry.getKey()).intValue()), 
            getDisplayValue(((CounterTuple)entry.getValue()).getRequestDroppedCount(), 9), 
            getDisplayValue(((CounterTuple)entry.getValue()).getAnswerDroppedCount(), 9), 
            getDisplayValue(((CounterTuple)entry.getValue()).getUnknownHbHAnswerDroppedCount(), 8), 
            getDisplayValue(((CounterTuple)entry.getValue()).getDuplicateRequestCount(), 8), 
            getDisplayValue(((CounterTuple)entry.getValue()).getDuplicateEtEAnswerCount(), 8), 
            getDisplayValue(((CounterTuple)entry.getValue()).getMalformedPacketReceivedCount(), 8), 
            getDisplayValue(((CounterTuple)entry.getValue()).getPendingRequestCount(), 8) });
    } 
    output.add("Counter Details", 3);
    output.add(formatter1.getFormattedValues());
    output.addNewLine();
    output.add("Error Message Details", 3);
    output.add(formatter2.getFormattedValues());
    output.addNewLine();
    formatter1 = new TableFormatter(new String[] { "Result-Code Category", "", "Rx", "Tx" }, new int[] { 20, 4, 10, 10 }, new int[] { 0, 0, 2, 2 }, 2);
    if (this.resultCodeCountersMap.size() == 0) {
      output.add(formatter1.getFormattedValues());
      output.addRecord(new String[] { "No Statistics Available." });
    } else {
      output.add(getResultCodeCategoryStatisticSummary(formatter1, 19));
    } 
    return output.getFormattedValues();
  }
  
  public String toCSV() {
    TableFormatter output = new TableFormatter(new String[0], new int[] { 286 }, 6, ",");
    output.addRecord(new String[] { "Reset Time: " + ((SimpleDateFormat)simpleDateFormatPool.get()).format(this.resetTime) });
    TableFormatter formatter = new TableFormatter(new String[] { 
          "CommandCode", "Req-Recieved", "Ans-Transmitted", "Req-Transmitted", "Ans-Recieved", "Req-Retransmitted", "Req-Timeout", "Req-Dropped", "Ans-Dropped", "Ans-Unknown", 
          "Req-Duplicate", "Ans-Duplicate", "Malformed-Message", "Req-Pending" }, new int[] { 
          20, 19, 19, 19, 19, 19, 19, 19, 19, 19, 
          19, 19, 19, 19 }, 6, ",");
    formatter.addRecord(new String[] { 
          "Total", 
          
          String.valueOf(this.totalCounters.getRequestInCount()), 
          String.valueOf(this.totalCounters.getAnswerOutCount()), 
          String.valueOf(this.totalCounters.getRequestOutCount()), 
          String.valueOf(this.totalCounters.getAnswerInCount()), 
          String.valueOf(this.totalCounters.getRequestsRetransmittedCount()), 
          String.valueOf(this.totalCounters.getTimeoutRequestStatistics()), 
          String.valueOf(this.totalCounters.getRequestDroppedCount()), 
          String.valueOf(this.totalCounters.getAnswerDroppedCount()), 
          String.valueOf(this.totalCounters.getUnknownHbHAnswerDroppedCount()), 
          String.valueOf(this.totalCounters.getDuplicateRequestCount()), 
          String.valueOf(this.totalCounters.getDuplicateEtEAnswerCount()), 
          String.valueOf(this.totalCounters.getMalformedPacketReceivedCount()), 
          String.valueOf(this.totalCounters.getPendingRequestCount()) });
    for (Map.Entry<Integer, CounterTuple> entry : this.commandCodeCountersMap.entrySet()) {
      formatter.addRecord(new String[] { 
            CommandCode.fromCode(((Integer)entry.getKey()).intValue()), 
            String.valueOf(((CounterTuple)entry.getValue()).getRequestInCount()), 
            String.valueOf(((CounterTuple)entry.getValue()).getAnswerOutCount()), 
            String.valueOf(((CounterTuple)entry.getValue()).getRequestOutCount()), 
            String.valueOf(((CounterTuple)entry.getValue()).getAnswerInCount()), 
            String.valueOf(((CounterTuple)entry.getValue()).getRequestsRetransmittedCount()), 
            String.valueOf(((CounterTuple)entry.getValue()).getTimeoutRequestStatistics()), 
            String.valueOf(((CounterTuple)entry.getValue()).getRequestDroppedCount()), 
            String.valueOf(((CounterTuple)entry.getValue()).getAnswerDroppedCount()), 
            String.valueOf(((CounterTuple)entry.getValue()).getUnknownHbHAnswerDroppedCount()), 
            String.valueOf(((CounterTuple)entry.getValue()).getDuplicateRequestCount()), 
            String.valueOf(((CounterTuple)entry.getValue()).getDuplicateEtEAnswerCount()), 
            String.valueOf(((CounterTuple)entry.getValue()).getMalformedPacketReceivedCount()), 
            String.valueOf(((CounterTuple)entry.getValue()).getPendingRequestCount()) });
    } 
    output.add(formatter.getFormattedValues());
    formatter = new TableFormatter(new String[] { "Result-Code Category", "Category Type", "Recieved", "Transmitted" }, new int[] { 20, 4, 19, 19 }, 6, ",");
    output.add(getResultCodeCategoryStatisticSummary(formatter, 19));
    return output.getFormattedValues();
  }
  
  private String getDisplayValue(long counter, int wrapDigts) {
    if (counter > Math.pow(10.0D, wrapDigts) - 1.0D)
      return (counter / 1000L) + "k"; 
    return String.valueOf(counter);
  }
  
  private String getResultCodeCategoryStatisticSummary(TableFormatter formatter, int counterShrinkSize) {
    if (this.resultCodeCountersMap.size() == 0) {
      formatter.add("No Statistics Available.", 0);
      return formatter.getFormattedValues();
    } 
    for (ResultCodeCategory category : ResultCodeCategory.values()) {
      ResultCodeTuple tuple = this.resultCodeCountersMap.get(Integer.valueOf(category.value));
      if (tuple != null)
        formatter.addRecord(new String[] { category.category, category.categoryType, 
              
              getDisplayValue(tuple.getResultCodeIn(), counterShrinkSize), 
              getDisplayValue(tuple.getResultCodeOut(), counterShrinkSize) }); 
    } 
    return formatter.getFormattedValues();
  }
  
  public String getResultCodeStatisticSummary() {
    TableFormatter formatter = new TableFormatter(new String[] { "ResultCode", "", "Rx", "Tx" }, new int[] { 20, 6, 12, 12 }, new int[] { 0, 0, 2, 2 }, 3);
    formatter.addNewLine();
    getResultCodeStatisticSummary(formatter);
    return formatter.getFormattedValues();
  }
  
  public Map<Integer, ResultCodeTuple> getCmdWiseResultCodeStatistic(int commandCode) {
    return this.cmdWiseResultCodeCountersMap.get(Integer.valueOf(commandCode));
  }
  
  private void getResultCodeStatisticSummary(TableFormatter formatter) {
    TreeSet<Integer> resultCodeSet = new TreeSet<>(this.resultCodeCountersMap.keySet());
    int isEmpty = resultCodeSet.size();
    if (isEmpty == 0)
      formatter.add("No Result Code Details Available.", 0); 
    int category = 0;
    while (isEmpty != 0) {
      if (resultCodeSet.subSet(Integer.valueOf(category + 1), Integer.valueOf(category + 1000)).size() > 0) {
        ResultCodeCategory resultCodeCategory = ResultCodeCategory.getResultCodeCategory(category);
        ResultCodeTuple tuple = this.resultCodeCountersMap.get(Integer.valueOf(category));
        formatter.addRecord(new String[] { resultCodeCategory.category, resultCodeCategory.categoryType, 
              
              getDisplayValue(tuple.getResultCodeIn(), 12), 
              getDisplayValue(tuple.getResultCodeOut(), 12) });
        formatter.add("----------------------------------------------------------\n");
        isEmpty--;
        for (Iterator<Integer> iterator = resultCodeSet.subSet(Integer.valueOf(category + 1), Integer.valueOf(category + 1000)).iterator(); iterator.hasNext(); ) {
          int resultCode = ((Integer)iterator.next()).intValue();
          tuple = this.resultCodeCountersMap.get(Integer.valueOf(resultCode));
          formatter.addRecord(new String[] { String.valueOf(resultCode), "", 
                
                getDisplayValue(tuple.getResultCodeIn(), 12), 
                getDisplayValue(tuple.getResultCodeOut(), 12) });
          isEmpty--;
        } 
        if (isEmpty != 0)
          formatter.addNewLine(); 
      } 
      category += 1000;
    } 
  }
  
  public Map<Integer, CounterTuple> getCommandCodeCountersMap() {
    return this.commandCodeCountersMap;
  }
  
  public Map<Integer, ResultCodeTuple> getResultCodeCountersMap() {
    return this.resultCodeCountersMap;
  }
}
