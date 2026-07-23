package com.diameter.commons;

import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

public class AvpDiameterURI extends AvpOctetString {
  private static final String SCHEME_SEPARATOR = "://";
  
  private static final String PARAMS_SEPARATOR = ";";
  
  private static final int DEFAULT_PORT = 3868;
  
  private String strScheme;
  
  private String strHost;
  
  private int iPort = -1;
  
  private String strTransport = "sctp";
  
  private String strProtocol = "diameter";
  
  public AvpDiameterURI(int intAVPCode, int intVendorId, byte bAVPFlag, String strAvpId, String strAVPEncryption) {
    super(intAVPCode, intVendorId, bAVPFlag, strAvpId, strAVPEncryption);
  }
  
  public void setStringValue(String data) throws URISyntaxException {
    if (data == null)
      throw new URISyntaxException(); 
    parseURI(data);
    super.setStringValue(data);
  }
  
  public String getStringValue() throws URISyntaxException {
    String data;
    byte[] valueBuffer = null;
    valueBuffer = getValueBytes();
    try {
      data = new String(valueBuffer, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      LogManager.getLogger().trace("", e);
      data = new String(valueBuffer);
    } 
    parseURI(data);
    return data;
  }
  
  public String getFQDN() {
    return this.strHost;
  }
  
  public int getPort() {
    return this.iPort;
  }
  
  public String getTransport() {
    return this.strTransport;
  }
  
  public String getProtocol() {
    return this.strProtocol;
  }
  
  public String getScheme() {
    return this.strScheme;
  }
  
  public void parseURI(String strURI) {
    int schemeStartIndex = strURI.indexOf("://");
    if (schemeStartIndex == -1)
      throw new URISyntaxException("Protocol scheme not found"); 
    this.strScheme = strURI.substring(0, schemeStartIndex);
    int schemeEndIndex = schemeStartIndex + 3;
    schemeStartIndex = strURI.indexOf(';', schemeEndIndex);
    if (schemeStartIndex == -1) {
      this.strHost = strURI.substring(schemeEndIndex);
    } else {
      this.strHost = strURI.substring(schemeEndIndex, schemeStartIndex);
    } 
    int sepIndex = this.strHost.indexOf(':');
    if (sepIndex != -1) {
      this.iPort = Integer.parseInt(this.strHost.substring(sepIndex + 1));
      this.strHost = this.strHost.substring(0, sepIndex);
    } else {
      this.iPort = 3868;
    } 
    if (schemeStartIndex != -1) {
      String strRest = strURI.substring(schemeStartIndex + 1);
      StringTokenizer strToken = new StringTokenizer(strRest, ";");
      while (strToken.hasMoreTokens()) {
        String strNextToken = strToken.nextToken();
        String[] strValues = strNextToken.split("=");
        if (strValues.length == 2) {
          if (strValues[0].equals("transport")) {
            this.strTransport = strValues[1];
            continue;
          } 
          if (strValues[0].equals("protocol"))
            this.strProtocol = strValues[1]; 
        } 
      } 
    } 
    if (this.strTransport.equals("udp") && this.strProtocol.equals("diameter"))
      throw new URISyntaxException("UDP Transport can not be used with Diameter"); 
  }
}
