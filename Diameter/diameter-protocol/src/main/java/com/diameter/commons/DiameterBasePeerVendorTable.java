package com.diameter.commons;

public class DiameterBasePeerVendorTable {
  private int dbpPeerVendorIndex;
  
  private String dbpPeerVendorId = "";
  
  private StorageTypes dbpPeerVendorStorageType = StorageTypes.NON_VOLATILE;
  
  private RowStatus dbpPeerVendorRowStatus = RowStatus.CREATE_AND_GO;
  
  public DiameterBasePeerVendorTable(int dbpPeerVendorIndex, String dbpPeerVendorId, StorageTypes dbpPeerVendorStorageType, RowStatus dbpPeerVendorRowStatus) {
    this.dbpPeerVendorIndex = dbpPeerVendorIndex;
    if (dbpPeerVendorId != null)
      this.dbpPeerVendorId = dbpPeerVendorId; 
    this.dbpPeerVendorStorageType = dbpPeerVendorStorageType;
    this.dbpPeerVendorRowStatus = dbpPeerVendorRowStatus;
  }
  
  public int getDbpPeerVendorIndex() {
    return this.dbpPeerVendorIndex;
  }
  
  public String getDbpPeerVendorId() {
    return this.dbpPeerVendorId;
  }
  
  public int getDbpPeerVendorStorageType() {
    return this.dbpPeerVendorStorageType.code;
  }
  
  public int getDbpPeerVendorRowStatus() {
    return this.dbpPeerVendorRowStatus.code;
  }
}
