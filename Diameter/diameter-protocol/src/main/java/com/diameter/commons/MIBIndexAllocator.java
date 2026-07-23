package com.diameter.commons;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

class MIBIndexAllocator implements Serializable {
  private static final long serialVersionUID = 1L;
  
  private static final String MODULE = "MIB-INDX-ALLOCATOR";
  
  private static final long initialValue = 4294967294L;
  
  private AtomicLong MIBCurrentIndex = new AtomicLong(4294967294L);
  
  private HashMap<String, Long> keyToMIBIndexMap = new HashMap<>();
  
  public Long getNextMIBIndex() {
    Long index = Long.valueOf(this.MIBCurrentIndex.decrementAndGet());
    if (index.longValue() < 1L) {
      LogManager.getLogger().warn("MIB-INDX-ALLOCATOR", "All Indices for Dynamic Peers, ranging from: 4294967294 to 1 are used, resetting to MIB Max Value: 4294967294");
      this.MIBCurrentIndex = new AtomicLong(4294967294L);
    } 
    return index;
  }
  
  public HashMap<String, Long> getKeyToMIBIndexMap() {
    return this.keyToMIBIndexMap;
  }
}
