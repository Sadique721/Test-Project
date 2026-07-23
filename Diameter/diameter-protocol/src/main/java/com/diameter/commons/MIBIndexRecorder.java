package com.diameter.commons;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class MIBIndexRecorder {
  private static final String MODULE = "MIB-INDX-RECORDER";
  
  private static final String INDEX_FILE = "_mib_indices.ser";
  
  private MIBIndexAllocator peerMibSerializeData = new MIBIndexAllocator();
  
  private String basePath;
  
  public void recordIndexFor(PeerData peerData) {
    HashMap<String, Long> serializedHashMap = this.peerMibSerializeData.getKeyToMIBIndexMap();
    String peerIdentifier = getPeerIdentfier(peerData);
    if (!serializedHashMap.containsKey(peerIdentifier)) {
      long index = peerData.getPeerIndex();
      if (index <= 0L)
        index = this.peerMibSerializeData.getNextMIBIndex().longValue(); 
      serializedHashMap.put(peerIdentifier, Long.valueOf(index));
    } 
    peerData.setPeerIndex(((Long)serializedHashMap.get(peerIdentifier)).longValue());
  }
  
  private String getPeerIdentfier(PeerData peerData) {
    return peerData.getHostIdentity();
  }
  
  public void build(String basePath) throws FileAllocatorException {
    this.basePath = basePath;
    if (Strings.isNullOrBlank(basePath))
      throw new FileAllocatorException("Base Path for Serialized File: _mib_indices.ser not found"); 
    basePath = basePath.trim();
    File file = new File(basePath + File.separator + "_mib_indices.ser");
    if (!file.exists()) {
      this.peerMibSerializeData = new MIBIndexAllocator();
    } else {
      ObjectInputStream ois = null;
      try {
        ois = new ObjectInputStream(new FileInputStream(file));
        this.peerMibSerializeData = (MIBIndexAllocator)ois.readObject();
      } catch (Exception e) {
        LogManager.getLogger().warn("MIB-INDX-RECORDER", "Unable to de-serialize Diameter MIB Indices, Reason " + e
            .getMessage() + ". This may effect SNMP Index Management");
        LogManager.getLogger().trace(e);
        this.peerMibSerializeData = new MIBIndexAllocator();
      } finally {
        FileUtil.closeQuietly(ois);
      } 
    } 
  }
  
  public void store() throws FileAllocatorException {
    if (Strings.isNullOrBlank(this.basePath))
      throw new FileAllocatorException("Base Path for Serialization not found"); 
    this.basePath = this.basePath.trim();
    ObjectOutputStream oos = null;
    try {
      oos = new ObjectOutputStream(new FileOutputStream(this.basePath + File.separator + "_mib_indices.ser"));
      oos.writeObject(this.peerMibSerializeData);
    } catch (Exception e) {
      LogManager.getLogger().warn("MIB-INDX-RECORDER", "Unable to serialize Diameter MIB Indices, Reason " + e
          .getMessage() + ". This may effect SNMP Index Management");
      LogManager.getLogger().trace(e);
    } finally {
      FileUtil.closeQuietly(oos);
    } 
  }
}
