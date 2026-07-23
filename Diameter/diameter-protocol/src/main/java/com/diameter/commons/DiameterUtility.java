package com.diameter.commons;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

public class DiameterUtility {
  private static final String MODULE = "DIA-UTILITY";
  
  public static final byte BIT_10000000 = -128;
  
  public static final byte BIT_01000000 = 64;
  
  public static final byte BIT_00100000 = 32;
  
  public static final byte BIT_00010000 = 16;
  
  public static final byte BIT_00001000 = 8;
  
  public static final byte BIT_00000100 = 4;
  
  public static final byte BIT_00000010 = 2;
  
  public static final byte BIT_00000001 = 1;
  
  public static final Splitter diaAVPIdSplitter = Splitter.on('.');
  
  public static final Pattern realmRegx = Pattern.compile("[0-9]*[a-zA-Z]([a-zA-Z0-9-]*[a-zA-Z0-9])*(\\.[a-zA-Z]([a-zA-Z0-9-]*[a-zA-Z0-9])*)+$");
  
  public static final Pattern userNameRegx = Pattern.compile("[a-zA-Z0-9!_#$%&'*+-/=?^_`{}~|\\w &&[^.]]+[.]?[a-zA-Z0-9!_#$%&'*+-/=?^_`{}~|\\w &&[^.]]+");
  
  public static byte[] intToByteArray(int integer) {
    return intToByteArray(integer, 4);
  }
  
  public static byte[] intToByteArray(int integer, int noOfBytes) {
    byte[] byteArray = new byte[noOfBytes];
    for (int n = 0; n < noOfBytes; n++)
      byteArray[noOfBytes - 1 - n] = (byte)(integer >>> n * 8); 
    return byteArray;
  }
  
  public static void intToByteArray(ByteBuffer out, int position, int integer, int noOfBytes) {
    out.position(position);
    for (int n = noOfBytes - 1; n >= 0; n--)
      out.put((byte)(integer >>> n * 8)); 
  }
  
  public static boolean isBaseProtocolPacket(int intCommandCode) {
    return (intCommandCode == CommandCode.CAPABILITIES_EXCHANGE.code || intCommandCode == CommandCode.DEVICE_WATCHDOG.code || intCommandCode == CommandCode.DISCONNECT_PEER.code);
  }
  
  private static final char[] LHEX = new char[] { 
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
      'a', 'b', 'c', 'd', 'e', 'f' };
  
  public static String bytesToHex(byte[] buf) {
    int length = buf.length;
    StringBuilder hexbuf = new StringBuilder(length << 1);
    for (int i = 0; i < length; i++) {
      hexbuf.append(LHEX[buf[i] >> 4 & 0xF]);
      hexbuf.append(LHEX[buf[i] & 0xF]);
    } 
    return hexbuf.toString();
  }
  
  public static String getMacAddress(byte[] buf) {
    int length = buf.length - 1;
    if (buf.length == 6) {
      StringBuilder hexbuf = new StringBuilder(length << 1);
      int i = 0;
      for (; i < length; i++) {
        hexbuf.append(LHEX[buf[i] >> 4 & 0xF]);
        hexbuf.append(LHEX[buf[i] & 0xF]);
        hexbuf.append(":");
      } 
      hexbuf.append(LHEX[buf[i] >> 4 & 0xF]);
      hexbuf.append(LHEX[buf[i] & 0xF]);
      return hexbuf.toString();
    } 
    System.out.println("ERROR: Invalid length of MAC address");
    return bytesToHex(buf);
  }
  
  public static int[] getAttributeIds(String strAttributeId) throws NumberFormatException {
    int[] ids = null;
    if (strAttributeId.contains(":")) {
      String[] strIds = strAttributeId.split(":");
      ids = new int[strIds.length];
      for (int i = 0; i < strIds.length; i++)
        ids[i] = Integer.parseInt(strIds[i]); 
    } else {
      ids = new int[2];
      ids[0] = 0;
      ids[1] = Integer.parseInt(strAttributeId);
    } 
    return ids;
  }
  
  public static boolean matches(String sourceString, String pattern) {
    return matches(sourceString, pattern.toCharArray());
  }
  
  public static boolean matches(String sourceString, char[] pattern) {
    int stringOffset = 0;
    char[] stringCharArray = sourceString.toCharArray();
    int stringLen = stringCharArray.length;
    int patternLen = pattern.length;
    int currentPos = 0;
    try {
      for (currentPos = 0; currentPos < patternLen; currentPos++, stringOffset++) {
        if (stringOffset == stringLen) {
          while (currentPos < patternLen) {
            if (pattern[currentPos] != '*')
              return false; 
            currentPos++;
          } 
          return true;
        } 
        if (pattern[currentPos] != stringCharArray[stringOffset])
          if (pattern[currentPos] == '\\') {
            currentPos++;
            if (pattern[currentPos] != stringCharArray[stringOffset])
              return false; 
          } else if (pattern[currentPos] == '*') {
            boolean bStar = true;
            currentPos++;
            if (currentPos == patternLen)
              return true; 
            while (bStar) {
              int tmpCurrentPos = currentPos;
              while (stringCharArray[stringOffset] != pattern[tmpCurrentPos]) {
                stringOffset++;
                if (stringOffset == stringLen)
                  return false; 
              } 
              while (tmpCurrentPos < patternLen) {
                if (pattern[tmpCurrentPos] != stringCharArray[stringOffset]) {
                  if (pattern[tmpCurrentPos] == '*') {
                    bStar = false;
                    currentPos = tmpCurrentPos - 1;
                    stringOffset--;
                    break;
                  } 
                  if (pattern[tmpCurrentPos] != '?')
                    break; 
                } 
                tmpCurrentPos++;
                stringOffset++;
                if (stringOffset == stringLen) {
                  while (tmpCurrentPos < patternLen) {
                    if (pattern[tmpCurrentPos] != '*')
                      return false; 
                    tmpCurrentPos++;
                  } 
                  return true;
                } 
              } 
              if (stringOffset == stringLen && tmpCurrentPos == patternLen)
                return true; 
            } 
          } else if (pattern[currentPos] != '?') {
            return false;
          }  
      } 
    } catch (ArrayIndexOutOfBoundsException e) {
      return false;
    } 
    if (currentPos == patternLen && stringOffset == stringLen)
      return true; 
    return false;
  }
  
  public static boolean isGroupAvpId(String avpId) {
    return avpId.contains(".");
  }
  
  public static IDiameterAVP createAvp(String strAvp, String avpValue) {
    String[] avpIds = strAvp.split("\\.");
    IDiameterAVP currentAvp = null;
    for (int i = avpIds.length - 1; i >= 0; i--) {
      IDiameterAVP avp = DiameterDictionary.getInstance().getAttribute(avpIds[i]);
      if (avp.isGrouped()) {
        if (currentAvp != null) {
          ((AvpGrouped)avp).addSubAvp(currentAvp);
        } else {
          avp.setStringValue(avpValue);
        } 
      } else if (avpValue != null && avpValue.length() > 0) {
        avp.setStringValue(avpValue);
      } 
      currentAvp = avp;
    } 
    return currentAvp;
  }
  
  public static IDiameterAVP createAvp(String strAvp) {
    return createAvp(strAvp, null);
  }
  
  public static void addOrReplaceAvp(String avpId, DiameterPacket packet) {
    addOrReplaceAvp(avpId, packet, null);
  }
  
  public static void addOrReplaceAvp(String avpId, DiameterPacket packet, String value) {
    if (isGroupAvpId(avpId)) {
      IDiameterAVP avp = packet.getAVP(avpId);
      if (avp != null) {
        if (value != null)
          avp.setStringValue(value); 
        return;
      } 
      String[] avpIds = avpId.split("\\.");
      int size = avpIds.length;
      AvpGrouped groupedAvp = (AvpGrouped)packet.getAVP(avpIds[0]);
      if (groupedAvp != null) {
        int i = 1;
        for (; i < size; i++) {
          avp = groupedAvp.getSubAttribute(avpIds[i]);
          if (avp == null)
            break; 
          if (avp.isGrouped())
            groupedAvp = (AvpGrouped)avp; 
        } 
        if (i != size) {
          StringBuilder grpAvpId = new StringBuilder();
          while (i < size) {
            grpAvpId.append(avpIds[i]);
            i++;
            if (i != size)
              grpAvpId.append("."); 
          } 
          IDiameterAVP subAvp = createAvp(grpAvpId.toString(), value);
          groupedAvp.addSubAvp(subAvp);
        } 
        packet.refreshPacketHeader();
      } else {
        IDiameterAVP subAvp = createAvp(avpId, value);
        packet.addAvp(subAvp);
      } 
    } else {
      IDiameterAVP avp = packet.getAVP(avpId);
      if (avp == null) {
        avp = createAvp(avpId, value);
        packet.addAvp(avp);
      } else {
        avp.setStringValue(value);
      } 
    } 
  }
  
  public static byte[] getBytesFromHexValue(String strValue) {
    byte[] valueBytes = null;
    String hexValue = strValue.substring(2);
    if (hexValue.length() % 2 == 0) {
      int len = hexValue.length() / 2;
      valueBytes = new byte[len];
      for (int i = 0; i < len; i++)
        valueBytes[i] = (byte)Integer.parseInt(hexValue.substring(2 * i, 2 * i + 2), 16); 
    } 
    return valueBytes;
  }
  
  public static String getBytesAsString(String title, byte[] bytesToPrint) {
    StringWriter strWriter = new StringWriter();
    PrintWriter out = new PrintWriter(strWriter);
    out.print(title);
    out.print("-->");
    for (int i = 0; i < bytesToPrint.length; i++) {
      byte b = bytesToPrint[i];
      out.print(Integer.toHexString(b & 0xFF));
      out.print(" ");
    } 
    out.flush();
    out.close();
    return strWriter.toString();
  }
  
  public static String getAVPIdFromName(String avpName) {
    String avpId = "";
    if (avpName != null)
      if (isGroupAvpId(avpName)) {
        String[] avps = avpName.split("\\.");
        int level = avps.length;
        for (int currentLevel = 0; currentLevel < level; currentLevel++) {
          String currentAvpId = DiameterDictionary.getInstance().getStrAVPId(avps[currentLevel]);
          if (currentAvpId == null) {
            avpId = null;
            break;
          } 
          if (currentLevel != level - 1) {
            avpId = avpId + currentAvpId + ".";
          } else {
            avpId = avpId + currentAvpId;
          } 
        } 
      } else {
        avpId = DiameterDictionary.getInstance().getStrAVPId(avpName);
      }  
    return avpId;
  }
  
  public static ArrayList<IDiameterAVP> getDiameterAttributes(String arg, ValueProvider valueProvider) throws Exception {
    ArrayList<IDiameterAVP> diameterAVPs = null;
    if (arg != null && arg.trim().length() > 0) {
      String[] args = arg.split(",");
      diameterAVPs = new ArrayList<>();
      if (args != null && args.length > 0) {
        for (String str : args) {
          String[] strs = ParserUtility.splitKeyAndValue(str);
          if (strs != null && strs.length == 3 && strs[0].trim().length() > 0) {
            IDiameterAVP diameterAVP = DiameterDictionary.getInstance().getAttribute(strs[0].trim());
            if (strs[2].startsWith("{")) {
            	//
            } else {
              String val = valueProvider.getStringValue(strs[2]);
              if (val == null)
                continue; 
              diameterAVP.setStringValue(val);
            } 
            diameterAVPs.add(diameterAVP);
            continue;
          } 
          throw new Exception("Invalid argument configured : " + str);
        } 
      } else {
        throw new Exception("Invalid argument , Reason : AVP not configured");
      } 
    } 
    return diameterAVPs;
  }
  
  public static byte[] appendBytes(byte[] oldArrayBytes, byte[] newArrayBytes) {
    if (oldArrayBytes == null)
      return newArrayBytes; 
    byte[] tempArrayBytes = oldArrayBytes;
    if (newArrayBytes != null) {
      tempArrayBytes = new byte[oldArrayBytes.length + newArrayBytes.length];
      System.arraycopy(oldArrayBytes, 0, tempArrayBytes, 0, oldArrayBytes.length);
      System.arraycopy(newArrayBytes, 0, tempArrayBytes, oldArrayBytes.length, newArrayBytes.length);
    } 
    return tempArrayBytes;
  }
  
  public static boolean isNAIDecorated(String nai) {
    return nai.contains("@");
  }
  
  public static boolean isValidRealmAccordingToABNF(String realm) {
    return realmRegx.matcher(realm).matches();
  }
  
  public static boolean isValidForProxy(String nai) {
    return (nai.contains("!") && nai.indexOf('!') != 0);
  }
  
  public static String getProxyRealm(String nai) {
    if (nai.contains("!") && nai.indexOf('!') != 0) {
      String otherRealm = nai.substring(0, nai.indexOf('!'));
      return otherRealm;
    } 
    return null;
  }
  
  public static String transformNAI(String originalNAI) throws MalformedNAIException {
    StringBuffer buffer = new StringBuffer();
    String otherRealm = originalNAI.substring(originalNAI.lastIndexOf("@") + 1, originalNAI.length());
    int startIndex = 0;
    String[] tokens = originalNAI.split("!");
    for (int i = 0; i < tokens.length - 1; i++) {
      if (tokens[i].trim().length() != 0) {
        startIndex += tokens[i].length() + 1;
        otherRealm = tokens[i];
        if (!isValidRealmAccordingToABNF(otherRealm))
          throw new MalformedNAIException("Realm Name : " + otherRealm + " is Invalid according to RFC 4282"); 
        break;
      } 
      for (int j = i; j < tokens.length - 1; j++) {
        if (tokens[j].trim().length() != 0)
          throw new MalformedNAIException("NAI is malformed."); 
      } 
    } 
    buffer.append(originalNAI.substring(startIndex, originalNAI.lastIndexOf('@') + 1) + otherRealm);
    return new String(buffer);
  }
  
  public static boolean isValidUserAccordingToABNF(String nai) {
    String user = nai;
    if (nai.contains("@"))
      user = nai.substring(0, nai.indexOf('@')); 
    return userNameRegx.matcher(user).matches();
  }
  
  public static boolean isIPRange(String ipRange) {
    if (ipRange != null)
      return (ipRange.contains("-") || ipRange.contains("/")); 
    return false;
  }
  
  public static List<String> getAvailableIPs(String ipRange) throws NumberFormatException {
    if (ipRange == null)
      return null; 
    if (ipRange.contains("-"))
      return getAddressFromIPRange(ipRange); 
    if (ipRange.contains("/"))
      return getAddressFromNetworkMask(ipRange); 
    return null;
  }
  
  private static List<String> getAddressFromIPRange(String ipRange) throws NumberFormatException {
    String[] ips = ipRange.split("\\-");
    if (ips.length != 2)
      throw new NumberFormatException("Invalid IP range format '" + ipRange + "', should be: xx.xx.xx.xx-xx.xx.xx.xx"); 
    String[] strIP1 = ips[0].split("\\.");
    if (strIP1.length != 4)
      throw new NumberFormatException("Invalid IP format '" + ips[0] + "', should be: xx.xx.xx.xx"); 
    String[] strIP2 = ips[1].split("\\.");
    if (strIP2.length != 4)
      throw new NumberFormatException("Invalid IP format '" + ips[1] + "', should be: xx.xx.xx.xx"); 
    String tempString = "";
    int index = -1;
    for (int i = 0; i < 4; i++) {
      if (strIP1[i].equals(strIP2[i])) {
        tempString = tempString + strIP1[i] + ".";
      } else {
        index = i;
        break;
      } 
    } 
    if (index != 3)
      throw new NumberFormatException("Only Class C Network Addresses Allowed"); 
    List<String> ipAddressList = new ArrayList<>();
    int ip1 = Integer.parseInt(strIP1[index]);
    int ip2 = Integer.parseInt(strIP2[index]);
    if (ip1 < 0 || ip1 > 255 || ip2 <= 0 || ip2 > 255)
      throw new NumberFormatException("Invalid IP Range"); 
    for (int j = ip1; j <= ip2; j++) {
      String str = tempString + j;
      ipAddressList.add(str);
    } 
    return ipAddressList;
  }
  
  private static List<String> getAddressFromNetworkMask(String ipInCIDRFormat) throws NumberFormatException {
    String[] st = ipInCIDRFormat.split("\\/");
    if (st.length != 2)
      throw new NumberFormatException("Invalid CIDR format '" + ipInCIDRFormat + "', should be: xx.xx.xx.xx/xx"); 
    String symbolicIP = st[0];
    String symbolicCIDR = st[1];
    Integer numericCIDR = new Integer(symbolicCIDR);
    if (numericCIDR.intValue() < 24)
      throw new NumberFormatException("Netmask CIDR can not be less than 24, Reason: Only Class C Network Addresses Allowed"); 
    if (numericCIDR.intValue() > 32)
      throw new NumberFormatException("CIDR can not be greater than 32"); 
    st = symbolicIP.split("\\.");
    if (st.length != 4)
      throw new NumberFormatException("Invalid IP address: " + symbolicIP); 
    int i = 24;
    int baseIPnumeric = 0;
    for (int n = 0; n < st.length; n++) {
      int value = Integer.parseInt(st[n]);
      if (value != (value & 0xFF))
        throw new NumberFormatException("Invalid IP address: " + symbolicIP); 
      baseIPnumeric += value << i;
      i -= 8;
    } 
    int netmaskNumeric = -1;
    netmaskNumeric <<= 32 - numericCIDR.intValue();
    ArrayList<String> ipAddressList = new ArrayList<>();
    int numberOfBits;
    for (numberOfBits = 0; numberOfBits < 32 && 
      netmaskNumeric << numberOfBits != 0; numberOfBits++);
    Integer numberOfIPs = Integer.valueOf(0);
    for (int k = 0; k < 32 - numberOfBits; k++) {
      numberOfIPs = Integer.valueOf(numberOfIPs.intValue() << 1);
      numberOfIPs = Integer.valueOf(numberOfIPs.intValue() | 0x1);
    } 
    Integer baseIP = Integer.valueOf(baseIPnumeric & netmaskNumeric);
    for (int j = 1; j < numberOfIPs.intValue(); j++) {
      Integer ourIP = Integer.valueOf(baseIP.intValue() + j);
      String ip = convertNumericIpToSymbolic(ourIP);
      ipAddressList.add(ip);
    } 
    return ipAddressList;
  }
  
  private static String convertNumericIpToSymbolic(Integer ip) {
    StringBuffer sb = new StringBuffer(15);
    for (int shift = 24; shift > 0; shift -= 8) {
      sb.append(Integer.toString(ip.intValue() >>> shift & 0xFF));
      sb.append('.');
    } 
    sb.append(Integer.toString(ip.intValue() & 0xFF));
    return sb.toString();
  }
  
  public static ApplicationEnum createApplicationEnumStrictly(long applicationId, long vendorId, ServiceTypes serviceType) {
    ApplicationIdentifier applicationIdentifier = ApplicationIdentifier.fromApplicationIdentifiers(applicationId);
    if (applicationIdentifier != null && applicationIdentifier
      .getApplicationType() == serviceType && applicationIdentifier
      .getVendorId() == vendorId)
      return (ApplicationEnum)applicationIdentifier; 
    return createApplicationEnum(applicationId, vendorId, serviceType, (applicationIdentifier != null) ? applicationIdentifier
        .getApplication() : Application.UNKNOWN);
  }
  
  public static ApplicationEnum createApplicationEnumLeniently(long applicationId, long vendorId, ServiceTypes defaultServiceType, Application defaultApplication) {
    ApplicationIdentifier applicationIdentifier = ApplicationIdentifier.fromApplicationIdentifiers(applicationId);
    if (applicationIdentifier != null)
      return (ApplicationEnum)applicationIdentifier; 
    return createApplicationEnum(applicationId, vendorId, defaultServiceType, defaultApplication);
  }
  
  public static ApplicationEnum createApplicationEnum(final long applicationId, final long vendorId, final ServiceTypes serviceType, final Application application) {
    return new ApplicationEnum() {
        public long getVendorId() {
          return vendorId;
        }
        
        public ServiceTypes getApplicationType() {
          return serviceType;
        }
        
        public long getApplicationId() {
          return applicationId;
        }
        
        public Application getApplication() {
          return application;
        }
        
        public String toString() {
          return getVendorId() + ":" + getApplicationId() + " [" + 
            getApplication().getDisplayName() + "]";
        }
      };
  }
  
  public static void updateHeaderInfoAVPs(DiameterPacket diameterPacket) {
    IDiameterAVP diameterAVP = diameterPacket.getInfoAVP("21067:65537");
    if (diameterAVP != null && diameterAVP.isGrouped() && Collectionz.isNullOrEmpty(diameterAVP.getGroupedAvp())) {
      AvpGrouped avpGrouped = (AvpGrouped)diameterAVP;
      updateHeaderRequestBitInfoAVP(diameterPacket, avpGrouped);
      updateHeaderProxyBitInfoAVP(diameterPacket, avpGrouped);
      updateHeaderErrorBitInfoAVP(diameterPacket, avpGrouped);
      updateHeaderReTransmitedBitInfoAVP(diameterPacket, avpGrouped);
    } 
    updateHeaderCommandCodeInfoAVP(diameterPacket);
    updateHeaderApplicationInfoAVP(diameterPacket);
    diameterAVP = diameterPacket.getInfoAVP("21067:65540");
    if (diameterAVP != null)
      diameterAVP.setValueBytes(intToByteArray(diameterPacket.getHop_by_hopIdentifier())); 
    diameterAVP = diameterPacket.getInfoAVP("21067:65541");
    if (diameterAVP != null)
      diameterAVP.setValueBytes(intToByteArray(diameterPacket.getEnd_to_endIdentifier())); 
  }
  
  public static void updateHeaderReTransmitedBitInfoAVP(DiameterPacket diameterPacket, AvpGrouped avpGrouped) {
    IDiameterAVP subAvp = avpGrouped.getSubAttribute("21067:65545");
    if (subAvp != null)
      subAvp.setInteger(booleanToInt(diameterPacket.isReTransmitted())); 
  }
  
  public static void updateHeaderErrorBitInfoAVP(DiameterPacket diameterPacket, AvpGrouped avpGrouped) {
    IDiameterAVP subAvp = avpGrouped.getSubAttribute("21067:65544");
    if (subAvp != null)
      subAvp.setInteger(booleanToInt(diameterPacket.isError())); 
  }
  
  public static void updateHeaderProxyBitInfoAVP(DiameterPacket diameterPacket, AvpGrouped avpGrouped) {
    IDiameterAVP subAvp = avpGrouped.getSubAttribute("21067:65543");
    if (subAvp != null)
      subAvp.setInteger(booleanToInt(diameterPacket.isProxiable())); 
  }
  
  public static int booleanToInt(boolean flag) {
    return flag ? 1 : 0;
  }
  
  public static void updateHeaderRequestBitInfoAVP(DiameterPacket diameterPacket, AvpGrouped avpGrouped) {
    IDiameterAVP subAvp = avpGrouped.getSubAttribute("21067:65542");
    if (subAvp != null)
      subAvp.setInteger(booleanToInt(diameterPacket.isRequest())); 
  }
  
  public static void updateHeaderCommandCodeInfoAVP(DiameterPacket diameterPacket) {
    IDiameterAVP diameterAVP = diameterPacket.getInfoAVP("21067:65538");
    if (diameterAVP != null)
      diameterAVP.setStringValue(String.valueOf(diameterPacket.getCommandCode())); 
  }
  
  public static void updateHeaderApplicationInfoAVP(DiameterPacket diameterPacket) {
    IDiameterAVP diameterAVP = diameterPacket.getInfoAVP("21067:65539");
    if (diameterAVP != null)
      diameterAVP.setStringValue(String.valueOf(diameterPacket.getApplicationID())); 
  }
  
  public static void addFailedAVP(@Nonnull DiameterAnswer answer, @Nonnull IDiameterAVP offendingAvp) {
    AvpGrouped failedAVP = (AvpGrouped)DiameterDictionary.getInstance().getKnownAttribute("0:279");
    if (failedAVP == null)
      return; 
    failedAVP.addSubAvp(offendingAvp);
    answer.addAvp((IDiameterAVP)failedAVP);
  }
  
  public static void addFailedAVPList(@Nonnull DiameterAnswer answer, @Nonnull List<IDiameterAVP> offendingAvps) {
    AvpGrouped failedAVP = (AvpGrouped)answer.getAVP("0:279");
    if (failedAVP == null) {
      failedAVP = (AvpGrouped)DiameterDictionary.getInstance().getKnownAttribute("0:279");
      if (failedAVP == null)
        return; 
      answer.addAvp((IDiameterAVP)failedAVP);
    } 
    failedAVP.addSubAvps(offendingAvps);
  }
  
  public static DiameterAnswer newAnswerWithHeaderOf(DiameterRequest request) {
    DiameterAnswer diameterAnswer = new DiameterAnswer();
    diameterAnswer.setHeaderFrom(request);
    return diameterAnswer;
  }
  
  public static String formatMsisdn(String msisdn, int msisdnLength, String mcc) {
    if (msisdn == null)
      return null; 
    int mncIndex = msisdn.length() - msisdnLength;
    if (mncIndex < 0)
      return null; 
    if (mncIndex == 0)
      return msisdn; 
    if (mcc == null)
      return msisdn.substring(mncIndex); 
    int mccIndex = mncIndex - mcc.length();
    if (mccIndex < 0)
      return null; 
    if (msisdn.regionMatches(mccIndex, mcc, 0, mcc.length()))
      return msisdn.substring(mncIndex); 
    return null;
  }
  
  public static String getIMSIFromIdentity(String identity) {
    if (identity == null)
      return identity; 
    String imsi = null, domain = null, msin = identity;
    try {
      if (identity.indexOf('@') != -1) {
        msin = identity.substring(0, identity.indexOf('@'));
        domain = identity.substring(identity.indexOf('@') + 1);
      } 
      if (msin.length() == 16 && UserIdentityPrefixTypes.isPrefixed(msin)) {
        imsi = msin.substring(1);
      } else if (msin.length() < 11 && domain != null) {
        String mcc = "";
        String mnc = "";
        int mccIndex = domain.indexOf("mcc");
        if (mccIndex != -1)
          mcc = domain.substring(mccIndex + 3, mccIndex + 6); 
        int mncIndex = domain.indexOf("mnc");
        if (mncIndex != -1) {
          mnc = domain.substring(mncIndex + 3, mncIndex + 6);
          if (mnc.startsWith("0"))
            mnc = mnc.substring(1); 
        } 
        imsi = mcc + mnc + msin;
        if (imsi.length() < 15) {
          int noOfZeros = 15 - imsi.length();
          imsi = mcc + mnc;
          for (int i = 0; i < noOfZeros; i++)
            imsi = imsi + "0"; 
          imsi = imsi + msin;
        } 
      } else {
        imsi = msin;
      } 
    } catch (Exception e) {
      if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
        LogManager.getLogger().warn("DIA-UTILITY", "Failed to generate IMSI from Identity: " + identity + ". Reason: " + e.getMessage()); 
      LogManager.getLogger().trace("DIA-UTILITY", e);
      return identity;
    } 
    return imsi;
  }
  
  public enum UserIdentityPrefixTypes {
    AKA_PERMANENT("0"),
    SIM_PERMANENT("1"),
    AKA_PRIME_PERMANENT("6");
    
    public final String identifier;
    
    UserIdentityPrefixTypes(String identifier) {
      this.identifier = identifier;
    }
    
    public static boolean isPrefixed(String userIdentity) {
      if (Strings.isNullOrBlank(userIdentity))
        return false; 
      return (userIdentity.startsWith(AKA_PERMANENT.identifier) || userIdentity
        .startsWith(SIM_PERMANENT.identifier) || userIdentity
        .startsWith(AKA_PRIME_PERMANENT.identifier));
    }
  }
}
