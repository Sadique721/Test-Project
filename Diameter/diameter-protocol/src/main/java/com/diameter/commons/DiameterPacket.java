package com.diameter.commons;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class DiameterPacket extends Packet implements IDiameterPacket, Cloneable {
  public static final int DEFAULT_DIAMETER_VERSION = 1;
  
  private static final String MODULE = "Diameter Packet";
  
  public static final int DEFAULT_DIAMETER_PACKET_LENGTH = 20;
  
  public static final boolean INCLUDE_INFO_ATTRIBUTE = true;
  
  public static final int UNKNOWN_STREAM = -1;
  
  public static final int DIA_PCKT_MAX_EXPECTED_LENGTH = 8192;
  
  public static final int COMMAND_FLAG_REQUEST_BIT = 128;
  
  private int rcvdOnStream;
  
  private byte bVersion;
  
  private int intLength;
  
  private byte bFlag = 0;
  
  private int intCommandCode;
  
  private int intApplicationId;
  
  private int intHopByHopIdentifier;
  
  private int intEndToEndIdentifier;
  
  private int infoLength;
  
  private Map<String, ArrayList<IDiameterAVP>> avpmap;
  
  private ArrayList<IDiameterAVP> col;
  
  private Map<String, ArrayList<IDiameterAVP>> infoAVPMap;
  
  private List<IDiameterAVP> infoAVPList;
  
  private long lPacketCreationTimeMillis;
  
  private long sendTime;
  
  private ByteBuffer header;
  
  private HashMap<String, Object> parameterMap;
  
  private PeerData peerData;
  
  private long queueTime;
  
  public DiameterPacket() {
    this.lPacketCreationTimeMillis = System.currentTimeMillis();
    initialize();
  }
  
  public DiameterPacket(TimeSource timeSource) {
    this.lPacketCreationTimeMillis = timeSource.currentTimeInMillis();
    initialize();
  }
  
  protected DiameterPacket(byte[] buffer) {
    this();
    if (buffer != null)
      setBytes(buffer); 
  }
  
  private void initialize() {
    this.parameterMap = new HashMap<>();
    this.avpmap = new HashMap<>();
    this.col = new ArrayList<>();
    this.infoAVPMap = new HashMap<>();
    this.infoAVPList = new ArrayList<>();
    this.header = ByteBuffer.allocate(20);
    setVersion(1);
    this.rcvdOnStream = -1;
  }
  
  public void setPacketBytes(byte[] data) {
    InputStream in = new DataInputStream(new ByteArrayInputStream(data));
    try {
      readFrom(in);
    } catch (IOException ioException) {
      if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
        LogManager.getLogger().error("Diameter Packet", "Error while reading packet data, reason : " + ioException); 
      setVersion(1);
      setCommandCode(0);
      resetErrorBit();
      resetProxiableBit();
      resetReTransmittedBit();
      setApplicationID(0L);
      setHop_by_hopIdentifier(0);
      setEnd_to_endIdentifier(0);
      this.avpmap = new HashMap<>();
    } 
  }
  
  public void parsePacketBytes(byte[] headerBytes, byte[] avpBytes) throws ParseException {
    try {
      parsePacketHeaderBytes(headerBytes);
      parsePacketAVPBytes(avpBytes);
    } catch (Exception e) {
      throw new ParseException("Exception occured while parsing packet with HbH-ID=" + getHop_by_hopIdentifier() + " and EtE-ID=" + 
          getEnd_to_endIdentifier(), e);
    } 
  }
  
  public void parsePacketHeaderBytes(byte[] headerBytes) {
    this.header = ByteBuffer.wrap(headerBytes);
    this.bVersion = headerBytes[0];
    this.intLength = headerBytes[1] & 0xFF;
    this.intLength = this.intLength << 8 | headerBytes[2] & 0xFF;
    this.intLength = this.intLength << 8 | headerBytes[3] & 0xFF;
    this.bFlag = (byte)(headerBytes[4] & 0xFF);
    this.intCommandCode = headerBytes[5] & 0xFF;
    this.intCommandCode = this.intCommandCode << 8 | headerBytes[6] & 0xFF;
    this.intCommandCode = this.intCommandCode << 8 | headerBytes[7] & 0xFF;
    this.intApplicationId = headerBytes[8] & 0xFF;
    this.intApplicationId = this.intApplicationId << 8 | headerBytes[9] & 0xFF;
    this.intApplicationId = this.intApplicationId << 8 | headerBytes[10] & 0xFF;
    this.intApplicationId = this.intApplicationId << 8 | headerBytes[11] & 0xFF;
    this.intHopByHopIdentifier = headerBytes[12] & 0xFF;
    this.intHopByHopIdentifier = this.intHopByHopIdentifier << 8 | headerBytes[13] & 0xFF;
    this.intHopByHopIdentifier = this.intHopByHopIdentifier << 8 | headerBytes[14] & 0xFF;
    this.intHopByHopIdentifier = this.intHopByHopIdentifier << 8 | headerBytes[15] & 0xFF;
    this.intEndToEndIdentifier = headerBytes[16] & 0xFF;
    this.intEndToEndIdentifier = this.intEndToEndIdentifier << 8 | headerBytes[17] & 0xFF;
    this.intEndToEndIdentifier = this.intEndToEndIdentifier << 8 | headerBytes[18] & 0xFF;
    this.intEndToEndIdentifier = this.intEndToEndIdentifier << 8 | headerBytes[19] & 0xFF;
  }
  
  public void parsePacketAVPBytes(byte[] avpBytes) throws IOException {
    parsePacketAVPBytes(new ByteArrayInputStream(avpBytes));
  }
  
  public int parsePacketAVPBytes(InputStream ipStream) throws IOException {
    this.col.clear();
    this.avpmap.clear();
    int intAVPCode = 0;
    int bAVPFlag = 0;
    int intAVPLength = 0;
    int intVendorId = 0;
    int totalBytes = 0;
    while (totalBytes != this.intLength && (intAVPCode = ipStream.read()) != -1) {
      IDiameterAVP diameterAttribute;
      intAVPCode <<= 8;
      intAVPCode = intAVPCode << 8 | ipStream.read() & 0xFF;
      intAVPCode = intAVPCode << 8 | ipStream.read() & 0xFF;
      intAVPCode = intAVPCode << 8 | ipStream.read() & 0xFF;
      totalBytes += 4;
      bAVPFlag = ipStream.read() & 0xFF;
      intAVPLength = ipStream.read();
      intAVPLength = intAVPLength << 8 | ipStream.read() & 0xFF;
      intAVPLength = intAVPLength << 8 | ipStream.read() & 0xFF;
      totalBytes += 4;
      if ((byte)(bAVPFlag & 0xFF & 0xFFFFFF80 & 0xFF) == Byte.MIN_VALUE) {
        intVendorId = ipStream.read();
        intVendorId = intVendorId << 8 | ipStream.read() & 0xFF;
        intVendorId = intVendorId << 8 | ipStream.read() & 0xFF;
        intVendorId = intVendorId << 8 | ipStream.read() & 0xFF;
        totalBytes += 4;
        diameterAttribute = DiameterDictionary.getInstance().getAttribute(intVendorId, intAVPCode);
      } else {
        diameterAttribute = DiameterDictionary.getInstance().getAttribute(intAVPCode);
      } 
      if (diameterAttribute == null) {
        UnknownAttribute unknownAttribute = new UnknownAttribute();
        totalBytes += unknownAttribute.readFlagOnwardsFrom(ipStream);
        continue;
      } 
      diameterAttribute.setFlag(bAVPFlag);
      diameterAttribute.setLength(intAVPLength);
      try {
        totalBytes += diameterAttribute.readFlagOnwardsFrom(ipStream);
      } catch (Exception e) {
        LogManager.getLogger().error("Diameter Packet", "error in reading attribute :: " + diameterAttribute);
        LogManager.ignoreTrace(e);
      } 
      ArrayList<IDiameterAVP> values = this.avpmap.get(diameterAttribute.getAVPId());
      if (values == null) {
        values = new ArrayList<>();
        this.avpmap.put(diameterAttribute.getAVPId(), values);
      } 
      values.add(diameterAttribute);
      this.col.add(diameterAttribute);
    } 
    return totalBytes;
  }
  
  public int readFrom(InputStream ips) throws IOException {
    int totalBytes = 0;
    byte[] header = new byte[20];
    totalBytes = ips.read(header);
    if (totalBytes <= 0) {
      if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
        LogManager.getLogger().info("Diameter Packet", "Connection has been terminated, No more communication"); 
      return -1;
    } 
    parsePacketHeaderBytes(header);
    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
      LogManager.getLogger().debug("Diameter Packet", "Reading contents for the length : " + this.intLength); 
    totalBytes += parsePacketAVPBytes(ips);
    return totalBytes;
  }
  
  public void writeTo(@Nonnull ByteArrayOutputStream destinationStream) throws IOException {
    writeTo(destinationStream, false);
  }
  
  public void writeTo(@Nonnull ByteArrayOutputStream out, boolean bIncludeInfoAttr) throws IOException {
    refreshPacketHeader();
    if (bIncludeInfoAttr)
      refreshInfoPacketHeader(); 
    this.header.position(0);
    out.write(this.header.array());
    ArrayList<IDiameterAVP> col = getAVPList();
    int listSize = col.size();
    for (int i = 0; i < listSize; i++) {
      IDiameterAVP diameterAvp = col.get(i);
      diameterAvp.writeTo(out);
    } 
    if (bIncludeInfoAttr) {
      int numOfInfoAvps = this.infoAVPList.size();
      for (int currentAvpIndex = 0; currentAvpIndex < numOfInfoAvps; currentAvpIndex++) {
        IDiameterAVP diameterAvp = this.infoAVPList.get(currentAvpIndex);
        diameterAvp.writeTo(out);
      } 
    } 
  }
  
  public void setSendTime(long sendTime) {
    this.sendTime = sendTime;
  }
  
  public byte[] getBytes() {
    return getBytes(false);
  }
  
  public byte[] getBytes(boolean bIncludeInfoAttr) {
    refreshPacketHeader();
    if (bIncludeInfoAttr)
      refreshInfoPacketHeader(); 
    ByteArrayOutputStream buffer = new ByteArrayOutputStream(getLength());
    try {
      writeTo(buffer, bIncludeInfoAttr);
    } catch (Exception ie) {
      LogManager.getLogger().trace("Diameter Packet", ie);
    } 
    return buffer.toByteArray();
  }
  
  public void setBytes(byte[] data) {
    try {
      readFrom(new DataInputStream(new ByteArrayInputStream(data)));
    } catch (IOException ioException) {
      if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
        LogManager.getLogger().error("Diameter Packet", "Error while reading packet data, reason : " + ioException); 
      setVersion(0);
      setCommandCode(0);
      setApplicationID(0L);
      resetErrorBit();
      resetProxiableBit();
      resetReTransmittedBit();
      setHop_by_hopIdentifier(0);
      setEnd_to_endIdentifier(0);
      this.avpmap.clear();
    } 
  }
  
  public ArrayList<IDiameterAVP> getAVPList() {
    return this.col;
  }
  
  public ArrayList<IDiameterAVP> getAVPList(String strAVPCode) {
    return getAVPListFromAVPMap(strAVPCode, this.avpmap);
  }
  
  private ArrayList<IDiameterAVP> getAVPListFromAVPMap(String strAVPCode, Map<String, ArrayList<IDiameterAVP>> avpMap) {
    ArrayList<IDiameterAVP> avpList = avpMap.get(strAVPCode);
    if (!Collectionz.isNullOrEmpty(avpList))
      return (ArrayList<IDiameterAVP>)avpList.clone(); 
    if (Strings.isNullOrBlank(strAVPCode) || !DiameterUtility.isGroupAvpId(strAVPCode))
      return null; 
    List<String> attributeIDs = DiameterUtility.diaAVPIdSplitter.split(strAVPCode);
    List<IDiameterAVP> parentAVPs = avpMap.get(attributeIDs.get(0));
    for (int i = 1; i < attributeIDs.size(); i++) {
      if (Collectionz.isNullOrEmpty(parentAVPs))
        return null; 
      avpList = new ArrayList<>();
      for (IDiameterAVP avp : parentAVPs) {
        if (!avp.isGrouped())
          return null; 
        avpList.addAll(((AvpGrouped)avp).getSubAttributeList(attributeIDs.get(i)));
      } 
      parentAVPs = avpList;
    } 
    if (avpList.isEmpty())
      return null; 
    return (ArrayList<IDiameterAVP>)avpList.clone();
  }
  
  public ArrayList<IDiameterAVP> getAVPList(String strAVPCode, boolean bIncludeInfoAvp) {
    ArrayList<IDiameterAVP> avpList = getAVPList(strAVPCode);
    if (!bIncludeInfoAvp)
      return avpList; 
    if (avpList != null && !avpList.isEmpty()) {
      ArrayList<IDiameterAVP> tempInfoAvps = (ArrayList<IDiameterAVP>)getInfoAVPList(strAVPCode);
      if (tempInfoAvps != null && !tempInfoAvps.isEmpty()) {
        ArrayList<IDiameterAVP> finalAvpList = new ArrayList<>();
        finalAvpList.addAll(avpList);
        finalAvpList.addAll(tempInfoAvps);
        avpList = finalAvpList;
      } 
    } else {
      avpList = (ArrayList<IDiameterAVP>)getInfoAVPList(strAVPCode);
    } 
    return avpList;
  }
  
  public int getCommandCode() {
    return this.intCommandCode;
  }
  
  public void setApplicationID(long applicationId) {
    this.intApplicationId = (int)applicationId;
    DiameterUtility.intToByteArray(this.header, 8, (int)(applicationId & 0xFFFFFFFFFFFFFFFFL), 4);
  }
  
  public long getApplicationID() {
    return this.intApplicationId;
  }
  
  public void setHop_by_hopIdentifier(int hopByHopIdentifier) {
    this.intHopByHopIdentifier = hopByHopIdentifier;
    DiameterUtility.intToByteArray(this.header, 12, hopByHopIdentifier, 4);
  }
  
  public int getHop_by_hopIdentifier() {
    return this.intHopByHopIdentifier;
  }
  
  public void setEnd_to_endIdentifier(int endToEndIdentifier) {
    this.intEndToEndIdentifier = endToEndIdentifier;
    DiameterUtility.intToByteArray(this.header, 16, endToEndIdentifier, 4);
  }
  
  public int getEnd_to_endIdentifier() {
    return this.intEndToEndIdentifier;
  }
  
  public Map<String, ArrayList<IDiameterAVP>> getAvpmap() {
    return this.avpmap;
  }
  
  public void addAvp(IDiameterAVP diameterAvp) {
    if (diameterAvp != null) {
      String strAvpId = diameterAvp.getAVPId();
      ArrayList<IDiameterAVP> values = this.avpmap.get(strAvpId);
      if (values == null) {
        values = new ArrayList<>();
        this.avpmap.put(strAvpId, values);
      } 
      values.add(diameterAvp);
      if (263 == diameterAvp.getAVPCode()) {
        this.col.add(0, diameterAvp);
      } else {
        this.col.add(diameterAvp);
      } 
    } 
  }
  
  public void addInfoAvp(IDiameterAVP diameterAvp) {
    if (diameterAvp != null) {
      String strAvpId = diameterAvp.getAVPId();
      ArrayList<IDiameterAVP> values = this.infoAVPMap.get(strAvpId);
      if (values == null) {
        values = new ArrayList<>();
        this.infoAVPMap.put(strAvpId, values);
      } 
      values.add(diameterAvp);
      this.infoAVPList.add(diameterAvp);
    } 
  }
  
  public void addAvp(String strAvpCode, String value) {
    if (value != null) {
      IDiameterAVP avp = DiameterDictionary.getInstance().getAttribute(strAvpCode);
      if (avp != null) {
        avp.setStringValue(value);
        addAvp(avp);
      } 
    } 
  }
  
  public void addAvp(String strAvpCode, List<String> valueList) {
    Iterator<String> itr = valueList.iterator();
    while (itr.hasNext())
      addAvp(strAvpCode, itr.next()); 
  }
  
  public void addAvp(String strAvpCode, long value) {
    IDiameterAVP avp = DiameterDictionary.getInstance().getAttribute(strAvpCode);
    if (avp != null) {
      avp.setInteger(value);
      addAvp(avp);
    } 
  }
  
  public void addInfoAvp(String strAVPCode, @Nonnull String value) {
    IDiameterAVP avp = DiameterDictionary.getInstance().getKnownAttribute(strAVPCode);
    if (avp != null) {
      avp.setStringValue(value);
      addInfoAvp(avp);
    } 
  }
  
  public void addInfoAvp(String strAvpCode, @Nonnull List<String> valueList) {
    Iterator<String> itr = valueList.iterator();
    while (itr.hasNext())
      addInfoAvp(strAvpCode, itr.next()); 
  }
  
  public void addInfoAvp(String strAVPCode, long value) {
    IDiameterAVP avp = DiameterDictionary.getInstance().getKnownAttribute(strAVPCode);
    if (avp != null) {
      avp.setInteger(value);
      addInfoAvp(avp);
    } 
  }
  
  public void addAvps(List<IDiameterAVP> avpList) {
    if (avpList != null) {
      Iterator<IDiameterAVP> itr = avpList.iterator();
      while (itr.hasNext()) {
        IDiameterAVP diameterAVP = itr.next();
        addAvp(diameterAVP);
      } 
    } 
  }
  
  public void setVersion(int version) {
    this.bVersion = (byte)version;
    DiameterUtility.intToByteArray(this.header, 0, this.bVersion, 1);
  }
  
  public int getVersion() {
    return this.bVersion;
  }
  
  public void setLength(int length) {
    this.intLength = length;
    DiameterUtility.intToByteArray(this.header, 1, this.intLength, 3);
  }
  
  public int getLength() {
    int intLength = 20;
    if (this.col != null) {
      int listSize = this.col.size();
      for (int i = 0; i < listSize; i++) {
        IDiameterAVP attr = this.col.get(i);
        intLength += attr.getLength();
        intLength += attr.getPaddingLength();
      } 
    } 
    return intLength;
  }
  
  public int getInfoLength() {
    return this.infoLength;
  }
  
  public int getCommandFlag() {
    return this.bFlag;
  }
  
  public void setCommandCode(int type) {
    this.intCommandCode = type;
    DiameterUtility.intToByteArray(this.header, 5, type, 3);
  }
  
  public boolean isServerInitiated() {
    CommandCode commandCode = CommandCode.getCommandCode(getCommandCode());
    return commandCode.isServerInitiated;
  }
  
  public List<IDiameterAVP> getVendorSpeficAvps(long vendorID, int avpCode) {
    return getAVPList(vendorID + ":" + avpCode);
  }
  
  public IDiameterAVP getAVP(String strAvpId) {
    IDiameterAVP diameterAVP = getAVPFFromID(strAvpId, this.avpmap);
    if (diameterAVP == null) {
      String avpId = DiameterUtility.getAVPIdFromName(strAvpId);
      if (avpId != null)
        diameterAVP = getAVPFFromID(avpId, this.avpmap); 
    } 
    return diameterAVP;
  }
  
  public IDiameterAVP getAVP(String strAvpId, boolean bIncludeInfoAttr) {
    IDiameterAVP diameterAVP = getAVP(strAvpId);
    if (!bIncludeInfoAttr)
      return diameterAVP; 
    if (diameterAVP == null)
      diameterAVP = getInfoAVP(strAvpId); 
    return diameterAVP;
  }
  
  public IDiameterAVP getInfoAVP(String strAvpId) {
    IDiameterAVP diameterAVP = getAVPFFromID(strAvpId, this.infoAVPMap);
    if (diameterAVP == null) {
      String avpId = DiameterUtility.getAVPIdFromName(strAvpId);
      if (avpId != null)
        diameterAVP = getAVPFFromID(avpId, this.infoAVPMap); 
    } 
    return diameterAVP;
  }
  
  public String getInfoAVPValue(String strAvpId) {
    IDiameterAVP diameterAVP = getInfoAVP(strAvpId);
    return (diameterAVP != null) ? diameterAVP.getStringValue() : null;
  }
  
  public List<IDiameterAVP> getInfoAVPList(String strAVPCode) {
    return getAVPListFromAVPMap(strAVPCode, this.infoAVPMap);
  }
  
  private IDiameterAVP getAVPFFromID(String strAvpId, Map<String, ArrayList<IDiameterAVP>> avpMap) {
    List<IDiameterAVP> avps = avpMap.get(strAvpId);
    if (avps != null)
      return avps.get(0); 
    if (Strings.isNullOrBlank(strAvpId) || !DiameterUtility.isGroupAvpId(strAvpId))
      return null; 
    List<String> avpIds = DiameterUtility.diaAVPIdSplitter.split(strAvpId);
    IDiameterAVP currentAvp = null;
    List<IDiameterAVP> avpList = avpMap.get(avpIds.get(0));
    if (Collectionz.isNullOrEmpty(avpList))
      return null; 
    for (int j = 0; j < avpList.size(); j++) {
      currentAvp = avpList.get(j);
      for (int i = 1; i < avpIds.size(); i++) {
        if (currentAvp != null) {
          if (!currentAvp.isGrouped())
            return null; 
          currentAvp = ((AvpGrouped)currentAvp).getSubAttribute(avpIds.get(i));
        } 
      } 
      if (currentAvp != null)
        return currentAvp; 
    } 
    return null;
  }
  
  public int removeAVP(IDiameterAVP avp) {
    return remove(avp, this.avpmap, this.col);
  }
  
  private int remove(IDiameterAVP avp, Map<String, ArrayList<IDiameterAVP>> avpMap, List<IDiameterAVP> avpList) {
    if (avp == null)
      return 0; 
    IDiameterAVP diameterAvp = avp;
    ArrayList<IDiameterAVP> avps = avpMap.get(diameterAvp.getAVPId());
    if (Collectionz.isNullOrEmpty(avps))
      return 0; 
    boolean exists = avps.remove(diameterAvp);
    if (!exists)
      return 0; 
    if (avps.isEmpty())
      avpMap.remove(String.valueOf(diameterAvp.getAVPId())); 
    avpList.remove(diameterAvp);
    return 1;
  }
  
  public int removeAVP(IDiameterAVP avp, boolean bIncludeInfoAttribute) {
    int removed = 0;
    removed += removeAVP(avp);
    if (bIncludeInfoAttribute)
      removed += removeInfoAVP(avp); 
    return removed;
  }
  
  public int removeInfoAVP(IDiameterAVP avp) {
    return remove(avp, this.infoAVPMap, this.infoAVPList);
  }
  
  public boolean containsAVP(IDiameterAVP avp) {
    return this.col.contains(avp);
  }
  
  public boolean containsAVP(IDiameterAVP avp, boolean bincludeInfoAVP) {
    boolean contains = containsAVP(avp);
    if (!bincludeInfoAVP)
      return contains; 
    if (!contains)
      contains = containsInfoAVP(avp); 
    return contains;
  }
  
  public boolean containsInfoAVP(IDiameterAVP avp) {
    return this.infoAVPList.contains(avp);
  }
  
  public boolean isRequest() {
    return ((this.bFlag & 0x80) != 0);
  }
  
  public boolean isResponse() {
    return ((this.bFlag & 0x80) == 0);
  }
  
  public boolean isProxiable() {
    return ((this.bFlag & 0x40) != 0);
  }
  
  public boolean isError() {
    return ((this.bFlag & 0x20) != 0);
  }
  
  public boolean isReTransmitted() {
    return ((this.bFlag & 0x10) != 0);
  }
  
  public void setRequestBit() {
    this.bFlag = (byte)(this.bFlag | 0x80);
    this.header.put(4, this.bFlag);
  }
  
  public void setProxiableBit() {
    this.bFlag = (byte)(this.bFlag | 0x40);
    this.header.put(4, this.bFlag);
  }
  
  public void setErrorBit() {
    this.bFlag = (byte)(this.bFlag | 0x20);
    this.header.put(4, this.bFlag);
  }
  
  public void setReTransmittedBit() {
    this.bFlag = (byte)(this.bFlag | 0x10);
    this.header.put(4, this.bFlag);
  }
  
  public long creationTimeMillis() {
    return this.lPacketCreationTimeMillis;
  }
  
  private int resetBit(int commandFlag, int position) {
    int mask = 1;
    mask <<= position - 1;
    return (commandFlag ^ 0xFFFFFFFF | mask) ^ 0xFFFFFFFF;
  }
  
  public void resetRequestBit() {
    this.bFlag = (byte)resetBit(this.bFlag, 8);
    this.header.put(4, this.bFlag);
  }
  
  public void resetProxiableBit() {
    this.bFlag = (byte)resetBit(this.bFlag, 7);
    this.header.put(4, this.bFlag);
  }
  
  public void resetErrorBit() {
    this.bFlag = (byte)resetBit(this.bFlag, 6);
    this.header.put(4, this.bFlag);
  }
  
  public void resetReTransmittedBit() {
    this.bFlag = (byte)resetBit(this.bFlag, 5);
    this.header.put(4, this.bFlag);
  }
  
  public void setResponsePacketHeader(IDiameterPacket requestPacket) {
    this.intCommandCode = requestPacket.getCommandCode();
    this.intApplicationId = (int)requestPacket.getApplicationID();
    this.intHopByHopIdentifier = requestPacket.getHop_by_hopIdentifier();
    this.intEndToEndIdentifier = requestPacket.getEnd_to_endIdentifier();
  }
  
  public String toString() {
    StringWriter stringBuffer = new StringWriter();
    PrintWriter out = new PrintWriter(stringBuffer);
    out.println();
    out.print("\tVer=" + getVersion());
    out.printf(", Len=%04d", new Object[] { Integer.valueOf(getLength()) });
    out.print(", Flags=[R=" + (isRequest() ? 1 : 0) + " P=" + (isProxiable() ? 1 : 0) + " E=" + (isError() ? 1 : 0) + " T=" + (isReTransmitted() ? 1 : 0) + "]");
    out.print(", CMD=" + CommandCode.getDisplayName(getCommandCode()));
    if (isRequest()) {
      out.print("R");
    } else {
      out.print("A");
    } 
    out.print("(" + getCommandCode() + ")");
    out.print(", App=" + ApplicationIdentifier.getDisplayName(getApplicationID()));
    out.print("(" + getApplicationID() + ")");
    out.print(", H2H=" + DiameterUtility.bytesToHex(DiameterUtility.intToByteArray(getHop_by_hopIdentifier())));
    out.print(", E2E=" + DiameterUtility.bytesToHex(DiameterUtility.intToByteArray(getEnd_to_endIdentifier())));
    out.println();
    out.println("\tAVPs : ");
    int listSize = this.col.size();
    for (int i = 0; i < listSize; i++)
      out.println(((IDiameterAVP)this.col.get(i)).toString()); 
    int numOfInfoAvp = this.infoAVPList.size();
    if (numOfInfoAvp > 0) {
      out.println("\t--Info AVPs");
      for (int j = 0; j < numOfInfoAvp; j++)
        out.println(((IDiameterAVP)this.infoAVPList.get(j)).toString()); 
    } 
    out.flush();
    out.close();
    return stringBuffer.toString();
  }
  
  public void resetDiameterPacket() {
    this.avpmap = new HashMap<>();
    this.col = new ArrayList<>();
    this.infoAVPMap = new HashMap<>();
    this.infoAVPList = new ArrayList<>();
    this.header = ByteBuffer.allocate(20);
    touch();
    setVersion(1);
    setRequestBit();
  }
  
  public void refreshPacketHeader() {
    int length = 20;
    if (this.col != null)
      for (int i = 0; i < this.col.size(); i++) {
        IDiameterAVP attr = this.col.get(i);
        length += attr.getLength() + attr.getPaddingLength();
      }  
    setLength(length);
  }
  
  public void refreshInfoPacketHeader() {
    int length = 0;
    if (this.infoAVPList != null) {
      int numberOfInfoAvps = this.infoAVPList.size();
      for (int i = 0; i < numberOfInfoAvps; i++) {
        IDiameterAVP infoAvp = this.infoAVPList.get(i);
        length += infoAvp.getLength() + infoAvp.getPaddingLength();
      } 
    } 
    this.infoLength = length;
  }
  
  public String getDestinationHost() {
    ArrayList<IDiameterAVP> diameterAVPs = this.avpmap.get("0:293");
    if (diameterAVPs != null) {
      IDiameterAVP avp = diameterAVPs.get(0);
      if (avp != null)
        return avp.getStringValue(); 
    } 
    return null;
  }
  
  public String getAVPValue(String avpIdentifier) {
    return getAVPValue(avpIdentifier, false);
  }
  
  public String getAVPValue(String avpIdentifier, boolean bIncludeInfoAttr) {
    IDiameterAVP diameterAVP = getAVP(avpIdentifier, bIncludeInfoAttr);
    if (diameterAVP != null)
      return diameterAVP.getStringValue(); 
    return null;
  }
  
  public Object clone() throws CloneNotSupportedException {
    DiameterPacket clonePacket = null;
    clonePacket = (DiameterPacket)super.clone();
    clonePacket.col = new ArrayList<>();
    clonePacket.avpmap = new HashMap<>();
    clonePacket.infoAVPList = new ArrayList<>();
    clonePacket.infoAVPMap = new HashMap<>();
    clonePacket.parameterMap = new HashMap<>();
    clonePacket.rcvdOnStream = this.rcvdOnStream;
    if (this.header != null) {
      byte[] headerBytes = this.header.array();
      clonePacket.header = ByteBuffer.wrap(Arrays.copyOf(headerBytes, headerBytes.length));
    } 
    int numOfAvp = this.col.size();
    for (int i = 0; i < numOfAvp; i++) {
      IDiameterAVP diameterAVP = (IDiameterAVP)((IDiameterAVP)this.col.get(i)).clone();
      clonePacket.col.add(diameterAVP);
      ArrayList<IDiameterAVP> values = clonePacket.avpmap.get(diameterAVP.getAVPId());
      if (values == null) {
        values = new ArrayList<>();
        clonePacket.avpmap.put(diameterAVP.getAVPId(), values);
      } 
      values.add(diameterAVP);
    } 
    int numOfInfoAvps = this.infoAVPList.size();
    for (int j = 0; j < numOfInfoAvps; j++) {
      IDiameterAVP diameterAVP = (IDiameterAVP)((IDiameterAVP)this.infoAVPList.get(j)).clone();
      clonePacket.infoAVPList.add(diameterAVP);
      ArrayList<IDiameterAVP> values = clonePacket.infoAVPMap.get(diameterAVP.getAVPId());
      if (values == null) {
        values = new ArrayList<>();
        clonePacket.infoAVPMap.put(diameterAVP.getAVPId(), values);
      } 
      values.add(diameterAVP);
    } 
    return clonePacket;
  }
  
  public void touch() {
    this.lPacketCreationTimeMillis = System.currentTimeMillis();
  }
  
  public void setParameter(String key, Object parameterValue) {
    this.parameterMap.put(key, parameterValue);
  }
  
  public Object getParameter(String str) {
    return this.parameterMap.get(str);
  }
  
  public Object removeParameter(String key) {
    return this.parameterMap.remove(key);
  }
  
  public Map<String, Object> getParameters() {
    return this.parameterMap;
  }
  
  public List<IDiameterAVP> getInfoAVPList() {
    return this.infoAVPList;
  }
  
  public long getSendTime() {
    return this.sendTime;
  }
  
  public abstract DiameterRequest getAsDiameterRequest();
  
  public abstract DiameterAnswer getAsDiameterAnswer();
  
  public void removeAllAVPs(List<IDiameterAVP> avps, boolean includeInfoAVPs) {
    for (IDiameterAVP avp : avps)
      removeAVP(avp, includeInfoAVPs); 
  }
  
  @Nullable
  public String getSessionID() {
    return getAVPValue("0:263");
  }
  
  public PeerData getPeerData() {
    return this.peerData;
  }
  
  public void setPeerData(PeerData peerData) {
    this.peerData = peerData;
  }
  
  public void retain(@Nonnull Predicate<IDiameterAVP> retainFilter) {
    ArrayList<IDiameterAVP> avplist = getAVPList();
    Iterator<IDiameterAVP> avpIterator = avplist.iterator();
    ArrayList<IDiameterAVP> retainableAvpList = new ArrayList<>((int)(avplist.size() * 0.75D));
    while (avpIterator.hasNext()) {
      IDiameterAVP avp = avpIterator.next();
      if (retainFilter.apply(avp)) {
        retainableAvpList.add(avp);
        continue;
      } 
      this.avpmap.remove(avp.getAVPId());
    } 
    this.col = retainableAvpList;
  }
  
  public void addAvp(String strAvpCode, Date time) {
    IDiameterAVP avp = DiameterDictionary.getInstance().getAttribute(strAvpCode);
    if (avp != null) {
      avp.setTime(time);
      addAvp(avp);
    } 
  }
  
  public long getQueueTime() {
    return this.queueTime;
  }
  
  public void setQueueTime(long queueTime) {
    this.queueTime = queueTime;
  }
  
  public int getRcvdOnStream() {
    return this.rcvdOnStream;
  }
  
  public void setRcvdOnStream(int rcvdOnStream) {
    this.rcvdOnStream = rcvdOnStream;
  }
  
  public static DiameterPacket createPacket(byte[] headerBytes) throws MalformedPacketException {
    DiameterPacket diameterPacket;
    if (headerBytes.length < 20) {
      LogManager.getLogger().error("Diameter Packet", "Illegal header length: " + headerBytes.length);
      throw new MalformedPacketException("Illeagal header length: " + headerBytes.length);
    } 
    if (headerBytes[0] != 1) {
      LogManager.getLogger().error("Diameter Packet", "Malformed hex bytes recieved: " + DiameterUtility.bytesToHex(headerBytes));
      throw new MalformedPacketException("Unsupported diameter version: " + headerBytes[0]);
    } 
    int messageLength = headerBytes[1];
    messageLength = messageLength << 8 | headerBytes[2] & 0xFF;
    messageLength = messageLength << 8 | headerBytes[3] & 0xFF;
    if (messageLength < 20 || messageLength > 8192) {
      LogManager.getLogger().error("Diameter Packet", "Malformed Hex bytes recieved: " + DiameterUtility.bytesToHex(headerBytes));
      throw new MalformedPacketException("Unsupported Diameter Message length: " + messageLength);
    } 
    boolean isRequest = ((headerBytes[4] & 0x80) != 0);
    if (isRequest) {
      diameterPacket = new DiameterRequest(false);
    } else {
      diameterPacket = new DiameterAnswer();
    } 
    diameterPacket.parsePacketHeaderBytes(headerBytes);
    return diameterPacket;
  }
  
  public int getRcvdLength() {
    return this.intLength;
  }
}
