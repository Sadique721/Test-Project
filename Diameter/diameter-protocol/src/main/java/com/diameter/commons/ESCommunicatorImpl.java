package com.diameter.commons;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Nullable;

import org.apache.commons.collections.buffer.CircularFifoBuffer;

public abstract class ESCommunicatorImpl implements ESCommunicator {
  public static final String MODULE = "ES-COMM";
  
  private final List<ESIEventListener<ESCommunicator>> eventListnerList;
  
  private boolean alive = true;
  
  private StatusScannerTask statusScanner;
  
  @Nullable
  private final TaskScheduler taskScheduler;
  
  private boolean isInitialized = false;
  
  private ESIStatisticsImpl esiStatistics;
  
  private List<AlertListener> alertListeners;
  
  private final int[] alignment = new int[] { 0, 0 };
  
  @Nullable
  private Future<?> statusScannerFuture;
  
  public ESCommunicatorImpl(@Nullable TaskScheduler scheduler) {
    this.eventListnerList = new CopyOnWriteArrayList<>();
    this.taskScheduler = scheduler;
    this.esiStatistics = createESIStatistics();
    this.alertListeners = new ArrayList<>();
  }
  
  public void init() throws InitializationFailedException {
    if (this.isInitialized)
      return; 
    scheduleStatusScannerTask();
    initESIStatistics();
    this.isInitialized = true;
  }
  
  public void addESIEventListener(ESIEventListener<ESCommunicator> eventListener) {
    this.eventListnerList.add(eventListener);
    if (!isAlive())
      eventListener.dead(this); 
  }
  
  public boolean isAlive() {
    return this.alive;
  }
  
  public final ESIStatistics getStatistics() {
    return this.esiStatistics;
  }
  
  public synchronized void markDead() {
    if (!isAlive()) {
      if (LogManager.getLogger().isInfoLogLevel())
        LogManager.getLogger().info("ES-COMM", getName() + " is already dead"); 
      return;
    } 
    long currentTimeInMillies = System.currentTimeMillis();
    if (currentTimeInMillies - this.esiStatistics.getLastMarkDeadTimestamp() <= 10000L) {
      if (LogManager.getLogger().isWarnLogLevel())
        LogManager.getLogger().warn("ES-COMM", "Skipping markDead operation. Reason:" + 
            getName() + " markDead is called more than one time in less than 10sec, last markDead Time " + (new Date(this.esiStatistics
              .getLastMarkDeadTimestamp())).toString()); 
      return;
    } 
    updateLastMarkDeadTimestamp();
    boolean isAliveForFallback = checkForFallback();
    if (isAliveForFallback) {
      if (LogManager.getLogger().isWarnLogLevel())
        LogManager.getLogger().warn("ES-COMM", "Check for fallback is true. Marking " + getName() + " as Alive"); 
      markAlive();
      return;
    } 
    incrementTotalDeadCount();
    updateLastDeadTimestamp();
    this.alive = false;
    generateDownAlert();
    for (ESIEventListener<ESCommunicator> eventListner : this.eventListnerList)
      eventListner.dead(this); 
    if (LogManager.getLogger().isErrorLogLevel())
      LogManager.getLogger().error("ES-COMM", "Marking " + getName() + " as Dead"); 
  }
  
  public synchronized void markAlive() {
    if (isAlive()) {
      if (LogManager.getLogger().isInfoLogLevel())
        LogManager.getLogger().info("ES-COMM", getName() + " is already alive"); 
      return;
    } 
    this.alive = true;
    generateUpAlert();
    for (ESIEventListener<ESCommunicator> eventListner : this.eventListnerList)
      eventListner.alive(this); 
    if (LogManager.getLogger().isWarnLogLevel())
      LogManager.getLogger().warn("ES-COMM", "Marked " + getName() + " as Alive"); 
  }
  
  public void removeESIEventListener(ESIEventListener<ESCommunicator> eventListener) {
    this.eventListnerList.remove(eventListener);
  }
  
  private class StatusScannerTask extends BaseIntervalBasedTask {
    private StatusScannerTask() {}
    
    public void execute(AsyncTaskContext context) {
      if (!ESCommunicatorImpl.this.isAlive()) {
        if (LogManager.getLogger().isDebugLogLevel())
          LogManager.getLogger().debug("ES-COMM", "Status Scanner Thread started for ESI"); 
        if (LogManager.getLogger().isInfoLogLevel())
          LogManager.getLogger().info("ES-COMM", "Scanning for aliveness"); 
        ESCommunicatorImpl.this.scan();
      } 
      ESCommunicatorImpl.this.updateLastStatusScanTimestamp();
    }
    
    public long getInterval() {
      return ESCommunicatorImpl.this.getStatusCheckDuration();
    }
    
    public long getInitialDelay() {
      return 60L;
    }
    
    public boolean isFixedDelay() {
      return true;
    }
  }
  
  private void scheduleStatusScannerTask() {
    if (getStatusCheckDuration() <= 0 || this.taskScheduler == null)
      return; 
    this.statusScanner = new StatusScannerTask();
    this.statusScannerFuture = this.taskScheduler.scheduleIntervalBasedTask(this.statusScanner);
  }
  
  protected TaskScheduler getTaskScheduler() {
    return this.taskScheduler;
  }
  
  private void initESIStatistics() {
    this.esiStatistics.init();
  }
  
  public void stop() {
    if (LogManager.getLogger().isDebugLogLevel())
      LogManager.getLogger().debug("ES-COMM", "Stopping " + getName() + " communicator"); 
    if (this.statusScannerFuture != null)
      this.statusScannerFuture.cancel(false); 
    this.esiStatistics.stop();
  }
  
  protected void updateAverageResponseTime(long value) {
    this.esiStatistics.updateAverageResponseTime(value);
  }
  
  protected void incrementTotalRequests() {
    this.esiStatistics.incrementTotalRequests();
  }
  
  protected void incrementTotalSuccess() {
    this.esiStatistics.incrementTotalSuccess();
  }
  
  protected void incrementTotalErrorResponses() {
    this.esiStatistics.incrementTotalErrorResponses();
  }
  
  protected void incrementTotalTimedoutResponses() {
    this.esiStatistics.incrementTotalTimedoutResponses();
  }
  
  private void updateLastDeadTimestamp() {
    this.esiStatistics.updateLastDeadTimestamp();
  }
  
  private void updateLastMarkDeadTimestamp() {
    this.esiStatistics.updateLastMarkDeadTimestamp();
  }
  
  private void incrementTotalDeadCount() {
    this.esiStatistics.incrementTotalDeadCount();
  }
  
  private void updateLastStatusScanTimestamp() {
    this.esiStatistics.updateLastStatusScanTimestamp();
  }
  
  protected ESIStatisticsImpl createESIStatistics() {
    return new ESIStatisticsImpl();
  }
  
  protected class ESIStatisticsImpl implements ESIStatistics {
    protected SimpleDateFormat dateFormat = new SimpleDateFormat("E M dd HH:mm:ss yyyy");
    
    private volatile long lastStatusScanTimestamp = 0L;
    
    private volatile long lastDeadTimestamp = 0L;
    
    private volatile long lastMarkDeadDeadTimestamp = 0L;
    
    private AtomicLong deadCount = new AtomicLong(0L);
    
    private AtomicLong totalRequests = new AtomicLong(0L);
    
    private AtomicLong totalSuccessResponse = new AtomicLong(0L);
    
    private AtomicLong totalErrorResponse = new AtomicLong(0L);
    
    private AtomicLong totalTimeOuts = new AtomicLong(0L);
    
    private ReentrantLock averageLock = new ReentrantLock();
    
    private float lastMinAverageResponseTime = 0.0F;
    
    private float lastTenMinAverageResponseTime = 0.0F;
    
    private float lastHourAverageResponseTime = 0.0F;
    
    private float newAverageResponseTime = 0.0F;
    
    private long lastMinRequestsReceivedCount = 0L;
    
    private Future<?> averageResTimeCalcTaskFuture;
    
    private void init() {
      if (ESCommunicatorImpl.this.taskScheduler == null) {
        if (LogManager.getLogger().isWarnLogLevel())
          LogManager.getLogger().warn("ES-COMM", "Skipping initializing statistics calculation of " + getName() + ". Reason: Task scheduler is null"); 
        return;
      } 
      AverageResponseTimeCalculator averageResTimeCalcTask = new AverageResponseTimeCalculator();
      this.averageResTimeCalcTaskFuture = ESCommunicatorImpl.this.taskScheduler.scheduleIntervalBasedTask((IntervalBasedTask)averageResTimeCalcTask);
    }
    
    public String currentStatus() {
      return ESCommunicatorImpl.this.isAlive() ? "ALIVE" : "DEAD";
    }
    
    public float getLastMinAvgResponseTime() {
      return this.lastMinAverageResponseTime;
    }
    
    public long getLastScanTimestamp() {
      return this.lastStatusScanTimestamp;
    }
    
    public long getLastDeadTimestamp() {
      return this.lastDeadTimestamp;
    }
    
    public long getLastMarkDeadTimestamp() {
      return this.lastMarkDeadDeadTimestamp;
    }
    
    public void stop() {
      if (LogManager.getLogger().isWarnLogLevel())
        LogManager.getLogger().warn("ES-COMM", "Stopping statistics calculation of " + getName()); 
      if (this.averageResTimeCalcTaskFuture != null)
        this.averageResTimeCalcTaskFuture.cancel(false); 
    }
    
    public long getTotalTimedouts() {
      return this.totalTimeOuts.get();
    }
    
    public long getTotalRequests() {
      return this.totalRequests.get();
    }
    
    public long getTotalSuccesses() {
      return this.totalSuccessResponse.get();
    }
    
    public long getTotalErrors() {
      return this.totalErrorResponse.get();
    }
    
    public String getName() {
      return ESCommunicatorImpl.this.getName();
    }
    
    public String getTypeName() {
      return ESCommunicatorImpl.this.getTypeName();
    }
    
    public long getDeadCount() {
      return this.deadCount.get();
    }
    
    public float getLastHourAvgResponseTime() {
      return this.lastHourAverageResponseTime;
    }
    
    public float getLastTenMinAvgResponseTime() {
      return this.lastTenMinAverageResponseTime;
    }
    
    protected void updateAverageResponseTime(double value) {
      this.averageLock.lock();
      this.newAverageResponseTime = (float)(this.newAverageResponseTime + value);
      this.lastMinRequestsReceivedCount++;
      this.averageLock.unlock();
    }
    
    protected void incrementTotalRequests() {
      this.totalRequests.incrementAndGet();
    }
    
    protected void incrementTotalSuccess() {
      this.totalSuccessResponse.incrementAndGet();
    }
    
    protected void incrementTotalErrorResponses() {
      this.totalErrorResponse.incrementAndGet();
    }
    
    protected void incrementTotalTimedoutResponses() {
      this.totalTimeOuts.incrementAndGet();
    }
    
    private void updateLastDeadTimestamp() {
      this.lastDeadTimestamp = System.currentTimeMillis();
    }
    
    private void updateLastMarkDeadTimestamp() {
      this.lastMarkDeadDeadTimestamp = System.currentTimeMillis();
    }
    
    private void updateLastStatusScanTimestamp() {
      this.lastStatusScanTimestamp = System.currentTimeMillis();
    }
    
    private void incrementTotalDeadCount() {
      this.deadCount.incrementAndGet();
    }
    
    public String toString() {
      int[] width = { 35, 30 };
      String[] header = new String[0];
      TableFormatter esiStatsTableFormatter = new TableFormatter(header, width, 2);
      esiStatsTableFormatter.addRecord(new String[] { "ESI Name", ":" + getName() }, ESCommunicatorImpl.this.alignment);
      esiStatsTableFormatter.addRecord(new String[] { "ESI Type", ":" + getTypeName() }, ESCommunicatorImpl.this.alignment);
      esiStatsTableFormatter.addRecord(new String[] { "ESI Status", ":" + currentStatus() }, ESCommunicatorImpl.this.alignment);
      esiStatsTableFormatter.addRecord(new String[] { "ESI Name", ":" + getName() }, ESCommunicatorImpl.this.alignment);
      esiStatsTableFormatter.addRecord(new String[] { "Total Requests", ":" + String.valueOf(this.totalRequests) }, ESCommunicatorImpl.this.alignment);
      esiStatsTableFormatter.addRecord(new String[] { "Total Error Response", ":" + String.valueOf(this.totalErrorResponse) }, ESCommunicatorImpl.this.alignment);
      esiStatsTableFormatter.addRecord(new String[] { "Total Success Response", ":" + String.valueOf(this.totalSuccessResponse) }, ESCommunicatorImpl.this.alignment);
      esiStatsTableFormatter.addRecord(new String[] { "Total Timed out Response", ":" + String.valueOf(this.totalTimeOuts) }, ESCommunicatorImpl.this.alignment);
      esiStatsTableFormatter.addRecord(new String[] { "Total Dead Count", ":" + String.valueOf(this.deadCount) }, ESCommunicatorImpl.this.alignment);
      esiStatsTableFormatter.addRecord(new String[] { "Last Scan Timestamp", ":" + this.dateFormat.format(new Date(this.lastStatusScanTimestamp)) }, ESCommunicatorImpl.this.alignment);
      esiStatsTableFormatter.addRecord(new String[] { "Last Dead Timestamp", ":" + this.dateFormat.format(new Date(this.lastDeadTimestamp)) }, ESCommunicatorImpl.this.alignment);
      esiStatsTableFormatter.addRecord(new String[] { "Last Minute Avg. Response Time", ":" + String.valueOf(this.lastMinAverageResponseTime) }, ESCommunicatorImpl.this.alignment);
      esiStatsTableFormatter.addRecord(new String[] { "Last 10 Minute Avg. Response Time", ":" + String.valueOf(this.lastTenMinAverageResponseTime) }, ESCommunicatorImpl.this.alignment);
      esiStatsTableFormatter.addRecord(new String[] { "Last Hour Avg. Response Time", ":" + String.valueOf(this.lastHourAverageResponseTime) }, ESCommunicatorImpl.this.alignment);
      return esiStatsTableFormatter.getFormattedValues();
    }
    
    private class AverageResponseTimeCalculator extends BaseIntervalBasedTask {
      private CircularFifoBuffer tenMinsAvgBuffer = new CircularFifoBuffer(10);
      
      private CircularFifoBuffer hourAvgBuffer = new CircularFifoBuffer(60);
      
      public long getInterval() {
        return 60L;
      }
      
      public long getInitialDelay() {
        return 60L;
      }
      
      public boolean isFixedDelay() {
        return true;
      }
      
      public TimeUnit getTimeUnit() {
        return TimeUnit.SECONDS;
      }
      
      public void execute(AsyncTaskContext context) {
        ESCommunicatorImpl.ESIStatisticsImpl.this.averageLock.lock();
        refreshPerMinuteAvgResponseStatitics();
        this.tenMinsAvgBuffer.add(Float.valueOf(ESCommunicatorImpl.ESIStatisticsImpl.this.lastMinAverageResponseTime));
        this.hourAvgBuffer.add(Float.valueOf(ESCommunicatorImpl.ESIStatisticsImpl.this.lastMinAverageResponseTime));
        refreshTenMinsAvgResponseStatistics();
        refreshHourAvgResponseStatistics();
        ESCommunicatorImpl.ESIStatisticsImpl.this.averageLock.unlock();
      }
      
      private void refreshPerMinuteAvgResponseStatitics() {
        if (ESCommunicatorImpl.ESIStatisticsImpl.this.newAverageResponseTime > 0.0F && ESCommunicatorImpl.ESIStatisticsImpl.this.lastMinRequestsReceivedCount > 0L) {
          ESCommunicatorImpl.ESIStatisticsImpl.this.lastMinAverageResponseTime = ESCommunicatorImpl.ESIStatisticsImpl.this.newAverageResponseTime / (float)ESCommunicatorImpl.ESIStatisticsImpl.this.lastMinRequestsReceivedCount;
        } else {
          ESCommunicatorImpl.ESIStatisticsImpl.this.lastMinAverageResponseTime = 0.0F;
        } 
        ESCommunicatorImpl.ESIStatisticsImpl.this.newAverageResponseTime = 0.0F;
        ESCommunicatorImpl.ESIStatisticsImpl.this.lastMinRequestsReceivedCount = 0L;
      }
      
      private void refreshTenMinsAvgResponseStatistics() {
        float totalAverageRespTime = 0.0F;
        for (Object f : this.tenMinsAvgBuffer)
          totalAverageRespTime += ((Float)f).floatValue(); 
        ESCommunicatorImpl.ESIStatisticsImpl.this.lastTenMinAverageResponseTime = totalAverageRespTime / this.tenMinsAvgBuffer.size();
      }
      
      private void refreshHourAvgResponseStatistics() {
        float totalAverageRespTime = 0.0F;
        for (Object f : this.hourAvgBuffer)
          totalAverageRespTime += ((Float)f).floatValue(); 
        ESCommunicatorImpl.ESIStatisticsImpl.this.lastHourAverageResponseTime = totalAverageRespTime / this.hourAvgBuffer.size();
      }
      
      private AverageResponseTimeCalculator() {}
    }
  }
  
  public void reInit() throws InitializationFailedException {}
  
  protected boolean checkForFallback() {
    return false;
  }
  
  public void generateUpAlert() {}
  
  public void generateDownAlert() {}
  
  public void registerAlertListener(AlertListener alertListener) {
    this.alertListeners.add(alertListener);
  }
  
  protected void notifyAlertListeners(Events event, String message) {
    for (AlertListener alertListener : this.alertListeners)
      alertListener.generateAlert(event, message); 
  }
  
  protected abstract int getStatusCheckDuration();
}
