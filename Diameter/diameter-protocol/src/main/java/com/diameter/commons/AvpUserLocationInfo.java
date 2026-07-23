package com.diameter.commons;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AvpUserLocationInfo extends AvpOctetString {
  public static final String LOCATION_TYPE = "Location-Type";
  
  public static final String CGI_MCC = "CGI-MCC";
  
  public static final String CGI_MNC = "CGI-MNC";
  
  public static final String CGI_LAC = "CGI-LAC";
  
  public static final String CGI_CI = "CGI-CI";
  
  public static final String SAI_MCC = "SAI-MCC";
  
  public static final String SAI_MNC = "SAI-MNC";
  
  public static final String SAI_LAC = "SAI-LAC";
  
  public static final String SAI_SAC = "SAI-SAC";
  
  public static final String RAI_MCC = "RAI-MCC";
  
  public static final String RAI_MNC = "RAI-MNC";
  
  public static final String RAI_LAC = "RAI-LAC";
  
  public static final String RAI_RAC = "RAI-RAC";
  
  public static final String TAC_MCC = "TAC-MCC";
  
  public static final String TAC_MNC = "TAC-MNC";
  
  public static final String TAC_TAC = "TAC-TAC";
  
  public static final String ECGI_MCC = "ECGI-MCC";
  
  public static final String ECGI_MNC = "ECGI-MNC";
  
  public static final String ECGI_SPARE = "ECGI-SPARE";
  
  public static final String ECGI_ECI = "ECGI-ECI";
  
  private static final CgiField cgiField = new CgiField();
  
  private static final SaiField saiField = new SaiField();
  
  private static final RaiField raiField = new RaiField();
  
  private static final TaiField taiField = new TaiField();
  
  private static final EcgiField ecgiField = new EcgiField();
  
  private static final TaiAndEcgiField taiAndEcgiField = new TaiAndEcgiField();
  
  private int iType = 0;
  
  private boolean isSupportedField = true;
  
  private Map<String, Integer> fieldsMap;
  
  public AvpUserLocationInfo(int intAVPCode, int intVendorId, byte bAVPFlag, String strAvpId, String strAVPEncryption) {
    super(intAVPCode, intVendorId, bAVPFlag, strAvpId, strAVPEncryption);
    this.fieldsMap = new HashMap<>();
  }
  
  private void setFields() {
    byte[] valueBuffer = getValueBytes();
    this.iType = valueBuffer[0] & 0xFF;
    switch (TGPPLocationField.getField(this.iType)) {
      case CGI:
        this.fieldsMap.putAll(cgiField.getFieldValueMap(valueBuffer));
        break;
      case SAI:
        this.fieldsMap.putAll(saiField.getFieldValueMap(valueBuffer));
        break;
      case RAI:
        this.fieldsMap.putAll(raiField.getFieldValueMap(valueBuffer));
        break;
      case TAI:
        this.fieldsMap.putAll(taiField.getFieldValueMap(valueBuffer));
        break;
      case ECGI:
        this.fieldsMap.putAll(ecgiField.getFieldValueMap(valueBuffer));
        break;
      case TAI_AND_ECGI:
        this.fieldsMap.putAll(taiAndEcgiField.getFieldValueMap(valueBuffer));
        break;
      default:
        this.isSupportedField = false;
        break;
    } 
    this.fieldsMap.put("Location-Type", Integer.valueOf(this.iType));
  }
  
  public int readFlagOnwardsFrom(InputStream sourceStream) {
    int iBytes = super.readFlagOnwardsFrom(sourceStream);
    setFields();
    return iBytes;
  }
  
  public void setValueBytes(byte[] valueBuffer) {
    super.setValueBytes(valueBuffer);
    setFields();
  }
  
  public String getKeyStringValue(String key) {
    String value = String.valueOf(this.fieldsMap.get(key));
    if (value == null || value.equalsIgnoreCase("NULL")) {
      byte[] valueByte = getValueBytes();
      byte[] result = new byte[valueByte.length - 1];
      System.arraycopy(valueByte, 1, result, 0, valueByte.length - 1);
      return DiameterUtility.bytesToHex(result);
    } 
    return value;
  }
  
  public final String getLogValue() {
    return "Location-Type = " + TGPPLocationField.fieldName(this.iType) + " (" + this.iType + "),Location-Info = " + (this.isSupportedField ? this.fieldsMap : getStringValue(true));
  }
  
  public Set<String> getKeySet() {
    return this.fieldsMap.keySet();
  }
}