package com.diameter.commons;


import java.util.HashMap;
import java.util.Map;

public class AvpUserEquipmentInfoValue extends AvpOctetString {
  public static final String MODULE = "AVP-USR-EQUIPMNT-INFO-VALUE";
  
  private Map<String, String> elementsMap = new HashMap<>();
  
  public static final String SVN = "SVN";
  
  public static final String SNR = "SNR";
  
  public static final String TAC = "TAC";
  
  public static final String MAC = "MAC";
  
  public static final String EUI64 = "EUI64";
  
  public static final String MODIFIED_EUI64 = "MODIFIED-EUI64";
  
  public AvpUserEquipmentInfoValue(int intAVPCode, int intVendorId, byte bAVPFlag, String strAvpId, String strAVPEncryption) {
    super(intAVPCode, intVendorId, bAVPFlag, strAvpId, strAVPEncryption);
  }
  
  public Map<String, String> getIMEISV() {
    String strIMEISV;
    byte[] valueBytes = getValueBytes();
    if (valueBytes.length >= 15) {
      strIMEISV = new String(valueBytes);
    } else {
      strIMEISV = DiameterUtility.bytesToHex(valueBytes);
    } 
    if (strIMEISV.length() >= 15) {
      this.elementsMap.put("SVN", strIMEISV.substring(14));
      this.elementsMap.put("SNR", strIMEISV.substring(8, 14));
      this.elementsMap.put("TAC", strIMEISV.substring(0, 8));
    } 
    return this.elementsMap;
  }
  
  public String getMacAddress() {
    String val = getStringValue();
    if (val != null && val.length() > 0)
      this.elementsMap.put("MAC", val); 
    return val;
  }
  
  public String getEUI64() {
    String val = getStringValue();
    if (val != null && val.length() > 0)
      this.elementsMap.put("EUI64", val); 
    return val;
  }
  
  public String getModifiedEUI64() {
    String val = getStringValue();
    if (val != null && val.length() > 0)
      this.elementsMap.put("MODIFIED-EUI64", val); 
    return val;
  }
  
  public final String getLogValue() {
    return this.elementsMap.toString();
  }
  
  public Object clone() throws CloneNotSupportedException {
    AvpUserEquipmentInfoValue clonedAvpUserEquiInfoValue = (AvpUserEquipmentInfoValue)super.clone();
    Map<String, String> elementsMap = new HashMap<>(this.elementsMap.size());
    for (Map.Entry<String, String> element : this.elementsMap.entrySet())
      elementsMap.put(element.getKey(), element.getValue()); 
    clonedAvpUserEquiInfoValue.elementsMap = elementsMap;
    return clonedAvpUserEquiInfoValue;
  }
}
