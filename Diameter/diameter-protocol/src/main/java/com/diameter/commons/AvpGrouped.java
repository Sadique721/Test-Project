package com.diameter.commons;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class AvpGrouped extends BaseDiameterAVP {
  private ArrayList<IDiameterAVP> subAvpList = new ArrayList<>();
  
  private static final String MODULE = "AVP-GROUPED";
  
  public AvpGrouped(int intAVPCode, int intVendorId, byte bAVPFlag, String strAvpId, String strAVPEncryption, ArrayList<AvpRule> fixedArrayList, ArrayList<AvpRule> requiredaArrayList, ArrayList<AvpRule> optionalArrayList) {
    super(intAVPCode, intVendorId, bAVPFlag, strAvpId, strAVPEncryption);
  }
  
  public void setGroupedAvp(ArrayList<IDiameterAVP> childAttr) {
    this.subAvpList = childAttr;
  }
  
  public ArrayList<IDiameterAVP> getGroupedAvp() {
    return this.subAvpList;
  }
  
  private String getLogValue(String newLineString) {
    ArrayList<IDiameterAVP> temp = getGroupedAvp();
    String data = new String();
    IDiameterAVP diameterAvp = null;
    int listSize = temp.size();
    newLineString = newLineString + "\t";
    for (int i = 0; i < listSize; i++) {
      diameterAvp = temp.get(i);
      if (diameterAvp.isGrouped()) {
        data = data + newLineString + DiameterDictionary.getInstance().getAttributeName(diameterAvp.getVendorId(), diameterAvp.getAVPCode()) + "(" + diameterAvp.getAVPCode() + ")" + (diameterAvp.isMandatory() ? " [M]" : "") + (diameterAvp.isVendorSpecificAttribute() ? ("[V-" + diameterAvp.getVendorId() + "]") : "") + (diameterAvp.isProtected() ? "[P]" : "") + " = " + ((AvpGrouped)diameterAvp).getLogValue(newLineString);
      } else {
        data = data + newLineString + DiameterDictionary.getInstance().getAttributeName(diameterAvp.getVendorId(), diameterAvp.getAVPCode()) + "(" + diameterAvp.getAVPCode() + ")" + (diameterAvp.isMandatory() ? " [M]" : "") + (diameterAvp.isVendorSpecificAttribute() ? ("[V-" + diameterAvp.getVendorId() + "]") : "") + (diameterAvp.isProtected() ? "[P]" : "") + " = " + diameterAvp.getLogValue();
      } 
    } 
    return data;
  }
  
  public final String getLogValue() {
    ArrayList<IDiameterAVP> temp = getGroupedAvp();
    String data = new String();
    IDiameterAVP diameterAvp = null;
    int listSize = temp.size();
    for (int i = 0; i < listSize; i++) {
      diameterAvp = temp.get(i);
      if (diameterAvp.isGrouped()) {
        data = data + "\n\t\t\t" + DiameterDictionary.getInstance().getAttributeName(diameterAvp.getVendorId(), diameterAvp.getAVPCode()) + "(" + diameterAvp.getAVPCode() + ")" + (diameterAvp.isMandatory() ? " [M]" : "") + (diameterAvp.isVendorSpecificAttribute() ? ("[V-" + diameterAvp.getVendorId() + "]") : "") + (diameterAvp.isProtected() ? "[P]" : "") + " = " + ((AvpGrouped)diameterAvp).getLogValue("\n\t\t\t");
      } else {
        data = data + "\n\t\t\t" + DiameterDictionary.getInstance().getAttributeName(diameterAvp.getVendorId(), diameterAvp.getAVPCode()) + "(" + diameterAvp.getAVPCode() + ")" + (diameterAvp.isMandatory() ? " [M]" : "") + (diameterAvp.isVendorSpecificAttribute() ? (" [V-" + diameterAvp.getVendorId() + "]") : "") + (diameterAvp.isProtected() ? "[P]" : "") + " = " + diameterAvp.getLogValue();
      } 
    } 
    return data;
  }
  
  public String getStringValue() {
    return null;
  }
  
  public boolean isGrouped() {
    return true;
  }
  
  public void setValueBytes(byte[] valueBuffer) {
    parseGroupedAttribute(valueBuffer);
  }
  
  public int getLength() {
    int intAVPLength;
    if (isVendorSpecificAttribute()) {
      intAVPLength = 12;
    } else {
      intAVPLength = 8;
    } 
    if (!Collectionz.isNullOrEmpty(this.subAvpList))
      for (int i = 0; i < this.subAvpList.size(); i++) {
        IDiameterAVP attr = this.subAvpList.get(i);
        intAVPLength += attr.getLength();
        intAVPLength += attr.getPaddingLength();
      }  
    return intAVPLength;
  }
  
  public List<IDiameterAVP> getSubAttributeList(String iAttrId) {
    List<IDiameterAVP> subAttributeList = new ArrayList<>();
    if (Strings.isNullOrBlank(iAttrId))
      return subAttributeList; 
    if (!DiameterUtility.isGroupAvpId(iAttrId)) {
      for (IDiameterAVP diameterAVP : this.subAvpList) {
        if (diameterAVP.getAVPId().equals(iAttrId))
          subAttributeList.add(diameterAVP); 
      } 
      return subAttributeList;
    } 
    return getSubAttributeList(DiameterUtility.diaAVPIdSplitter.split(iAttrId));
  }
  
  private List<IDiameterAVP> getSubAttributeList(List<String> avpIds) {
    List<IDiameterAVP> subAttributeList = new ArrayList<>();
    if (Collectionz.isNullOrEmpty(avpIds))
      return subAttributeList; 
    if (avpIds.size() == 1)
      return getSubAttributeList(avpIds.get(0)); 
    for (IDiameterAVP mainSubAvp : this.subAvpList) {
      if (mainSubAvp.getAVPId().equals(avpIds.get(0))) {
        if (!mainSubAvp.isGrouped())
          break; 
        List<IDiameterAVP> childAvps = ((AvpGrouped)mainSubAvp).getSubAttributeList(avpIds.subList(1, avpIds.size()));
        if (!Collectionz.isNullOrEmpty(childAvps))
          subAttributeList.addAll(childAvps); 
      } 
    } 
    return subAttributeList;
  }
  
  public IDiameterAVP getSubAttribute(String strAvpId) {
    if (Strings.isNullOrBlank(strAvpId))
      return null; 
    if (!DiameterUtility.isGroupAvpId(strAvpId))
      for (IDiameterAVP diameterAVP : this.subAvpList) {
        if (diameterAVP.getAVPId().equals(strAvpId))
          return diameterAVP; 
      }  
    return getSubAttribute(DiameterUtility.diaAVPIdSplitter.split(strAvpId));
  }
  
  private IDiameterAVP getSubAttribute(List<String> avpIds) {
    for (IDiameterAVP mainSubAvp : this.subAvpList) {
      if (mainSubAvp.getAVPId().equals(avpIds.get(0))) {
        if (avpIds.size() == 1)
          return mainSubAvp; 
        if (!mainSubAvp.isGrouped())
          return null; 
        IDiameterAVP currentAvp = ((AvpGrouped)mainSubAvp).getSubAttribute(avpIds.subList(1, avpIds.size()));
        if (currentAvp != null)
          return currentAvp; 
      } 
    } 
    return null;
  }
  
  public IDiameterAVP getSubAttribute(int vendorId, int avpCode) {
    return getSubAttribute(vendorId + ":" + avpCode);
  }
  
  public IDiameterAVP getSubAttribute(int avpCode) {
    return getSubAttribute("0:" + avpCode);
  }
  
  public void addSubAvp(IDiameterAVP diameterAVP) {
    if (diameterAVP == null)
      return; 
    if (this.subAvpList == null)
      this.subAvpList = new ArrayList<>(); 
    this.subAvpList.add(diameterAVP);
  }
  
  public void addSubAvps(List<IDiameterAVP> subAVPs) {
    if (Collectionz.isNullOrEmpty(subAVPs))
      return; 
    if (this.subAvpList == null)
      this.subAvpList = new ArrayList<>(); 
    this.subAvpList.addAll(subAVPs);
  }
  
  public void addSubAvp(String avpCode, String val) {
    IDiameterAVP subAvp = DiameterDictionary.getInstance().getAttribute(avpCode);
    subAvp.setStringValue(val);
    this.subAvpList.add(subAvp);
  }
  
  public void addSubAvp(String avpCode, long val) {
    addSubAvp(avpCode, String.valueOf(val));
  }
  
  public void addSubAvp(String strAvpCode, Date time) {
    IDiameterAVP avp = DiameterDictionary.getInstance().getAttribute(strAvpCode);
    if (avp != null) {
      avp.setTime(time);
      this.subAvpList.add(avp);
    } 
  }
  
  public void setStringValue(String data) {
    
  }
  
  public byte[] getValueBytes() {
    int len = 0;
    IDiameterAVP baseDiameterAVP = null;
    for (int i = 0; i < this.subAvpList.size(); i++) {
      baseDiameterAVP = this.subAvpList.get(i);
      len += baseDiameterAVP.getLength() + baseDiameterAVP.getPaddingLength();
    } 
    ByteArrayOutputStream out = new ByteArrayOutputStream(len);
    for (int j = 0; j < this.subAvpList.size(); j++) {
      try {
        ((IDiameterAVP)this.subAvpList.get(j)).writeTo(out);
      } catch (IOException e) {
        LogManager.ignoreTrace(e);
        throw new AssertionError(e.getMessage());
      } 
    } 
    return out.toByteArray();
  }
  
  public void writeTo(OutputStream out) throws IOException {
    setLength(getLength());
    super.writeTo(out);
  }
  
  protected void parseGroupedAttribute(byte[] valueBuffer) {
    int avpLength = 0;
    int cnt = 0, iAvpLength = 0, iAvpVendorId = 0;
    this.subAvpList = new ArrayList<>();
    ByteBuffer sourceByteBuffer = ByteBuffer.wrap(valueBuffer);
    while (cnt != valueBuffer.length) {
      IDiameterAVP diameterAvp;
      int iAvpCode = sourceByteBuffer.getInt(cnt);
      cnt += 4;
      byte bAvpFlag = sourceByteBuffer.get(cnt++);
      avpLength = sourceByteBuffer.get(cnt++);
      avpLength = avpLength << 8 | sourceByteBuffer.get(cnt++) & 0xFF;
      avpLength = avpLength << 8 | sourceByteBuffer.get(cnt++) & 0xFF;
      if ((bAvpFlag & 0x80) == 128) {
        iAvpVendorId = sourceByteBuffer.getInt(cnt);
        diameterAvp = DiameterDictionary.getInstance().getAttribute(iAvpVendorId, iAvpCode);
        cnt += 4;
        iAvpLength = avpLength - 12;
      } else {
        diameterAvp = DiameterDictionary.getInstance().getAttribute(iAvpCode);
        iAvpLength = avpLength - 8;
      } 
      byte[] temp = new byte[iAvpLength];
      sourceByteBuffer.position(cnt);
      sourceByteBuffer.get(temp);
      cnt += iAvpLength;
      int restByte = 0;
      if (iAvpLength % 4 != 0) {
        int remender = iAvpLength % 4;
        restByte = 4 - remender;
        cnt += restByte;
      } 
      diameterAvp.setLength(avpLength);
      diameterAvp.setFlag(bAvpFlag);
      diameterAvp.setValueBytes(temp);
      this.subAvpList.add(diameterAvp);
    } 
    setLength(getLength());
  }
  
  public Object clone() throws CloneNotSupportedException {
    AvpGrouped clonedAVPGrouped = (AvpGrouped)super.clone();
    ArrayList<IDiameterAVP> subAvpList = new ArrayList<>(this.subAvpList.size());
    for (IDiameterAVP diameteAVP : this.subAvpList)
      subAvpList.add((IDiameterAVP)diameteAVP.clone()); 
    clonedAVPGrouped.subAvpList = subAvpList;
    return clonedAVPGrouped;
  }
  
  public int hashCode() {
    return Objects.hash(new Object[] { Integer.valueOf(super.hashCode()), this.subAvpList });
  }
  
  public void refreshAVPHeader() {
    if (this.subAvpList == null)
      return; 
    for (int i = 0; i < this.subAvpList.size(); i++)
      ((IDiameterAVP)this.subAvpList.get(i)).refreshAVPHeader(); 
    setLength(getLength());
  }
  
  public boolean equals(Object obj) {
    if (obj == null)
      return false; 
    if (getClass() != obj.getClass())
      return false; 
    try {
      AvpGrouped avp = (AvpGrouped)obj;
      if (getAVPCode() == avp.getAVPCode() && 
        getVendorId() == avp.getVendorId() && 
        getLength() == avp.getLength() && 
        getGroupedAvp().size() == avp.getGroupedAvp().size()) {
        for (int i = 0; i < this.subAvpList.size(); i++) {
          if (!((IDiameterAVP)this.subAvpList.get(i)).equals(avp.getGroupedAvp().get(i)))
            return false; 
        } 
        return true;
      } 
    } catch (ClassCastException e) {
      LogManager.ignoreTrace(e);
    } 
    return false;
  }
}
