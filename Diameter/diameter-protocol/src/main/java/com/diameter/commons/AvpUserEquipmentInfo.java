package com.diameter.commons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AvpUserEquipmentInfo extends AvpGrouped {
  private Map<String, String> elementsMap;
  
  public AvpUserEquipmentInfo(int intAVPCode, int intVendorId, byte bAVPFlag, String strAvpId, String strAVPEncryption, ArrayList<AvpRule> fixedArrayList, ArrayList<AvpRule> requiredaArrayList, ArrayList<AvpRule> optionalArrayList) {
    super(intAVPCode, intVendorId, bAVPFlag, strAvpId, strAVPEncryption, fixedArrayList, requiredaArrayList, optionalArrayList);
    this.elementsMap = new HashMap<>();
  }
  
  protected void parseGroupedAttribute(byte[] valueBuffer) {
    super.parseGroupedAttribute(valueBuffer);
    IDiameterAVP equipmentInfoType = getSubAttribute("0:459");
    AvpUserEquipmentInfoValue equipmentInfoValue = (AvpUserEquipmentInfoValue)getSubAttribute("0:460");
    if (equipmentInfoType != null && equipmentInfoValue != null) {
      String strValue = null;
      switch ((int)equipmentInfoType.getInteger()) {
        case 0:
          this.elementsMap.putAll(equipmentInfoValue.getIMEISV());
          break;
        case 1:
          strValue = equipmentInfoValue.getMacAddress();
          if (strValue != null && strValue.length() > 0)
            this.elementsMap.put("MAC", strValue); 
          break;
        case 2:
          strValue = equipmentInfoValue.getEUI64();
          if (strValue != null && strValue.length() > 0)
            this.elementsMap.put("EUI64", equipmentInfoValue.getEUI64()); 
          break;
        case 3:
          strValue = equipmentInfoValue.getModifiedEUI64();
          if (strValue != null && strValue.length() > 0)
            this.elementsMap.put("MODIFIED_EUI64", equipmentInfoValue.getModifiedEUI64()); 
          break;
      } 
    } 
  }
  
  public String getKeyStringValue(String key) {
    String value = this.elementsMap.get(key);
    if (value == null || value.equalsIgnoreCase("NULL")) {
      byte[] valueByte = getValueBytes();
      byte[] result = new byte[valueByte.length - 1];
      System.arraycopy(valueByte, 1, result, 0, valueByte.length - 1);
      return DiameterUtility.bytesToHex(result);
    } 
    return value;
  }
  
  public Set<String> getKeySet() {
    return this.elementsMap.keySet();
  }
  
  public Object clone() throws CloneNotSupportedException {
    AvpUserEquipmentInfo clonedAvpUserEquiInfo = (AvpUserEquipmentInfo)super.clone();
    Map<String, String> elementsMap = new HashMap<>(this.elementsMap.size());
    for (Map.Entry<String, String> element : this.elementsMap.entrySet())
      elementsMap.put(element.getKey(), element.getValue()); 
    clonedAvpUserEquiInfo.elementsMap = elementsMap;
    return clonedAvpUserEquiInfo;
  }
}
