package com.diameter.commons;


public class DiameterAnswer extends DiameterPacket {
  private long requestReceivedTime = 0L;
  
  private String answeringHost;
  
  public DiameterAnswer(DiameterRequest diameterRequest, ResultCode resultCode) {
    setHeaderFrom(diameterRequest);
    if (!DiameterUtility.isBaseProtocolPacket(getCommandCode())) {
      cloneAndAdd(diameterRequest.getAVP("0:258"));
      cloneAndAdd(diameterRequest.getAVP("0:259"));
      cloneAndAdd(diameterRequest.getAVP("0:260"));
      cloneAndAdd(diameterRequest.getAVP("0:263"));
      if (diameterRequest.getCommandCode() == CommandCode.CREDIT_CONTROL.code) {
        cloneAndAdd(diameterRequest.getAVP("0:416"));
        cloneAndAdd(diameterRequest.getAVP("0:415"));
      } else if (diameterRequest.getCommandCode() == CommandCode.ACCOUNTING.code) {
        cloneAndAdd(diameterRequest.getAVP("0:480"));
        cloneAndAdd(diameterRequest.getAVP("0:485"));
        cloneAndAdd(diameterRequest.getAVP("0:287"));
      } else if (diameterRequest.getCommandCode() == CommandCode.SPENDING_LIMIT.code) {
        cloneAndAdd(diameterRequest.getAVP("10415:2904"));
      } else if (diameterRequest.getCommandCode() == CommandCode.DIAMETER_EAP.code || diameterRequest
        .getCommandCode() == CommandCode.AUTHENTICATION_AUTHORIZATION.code) {
        cloneAndAdd(diameterRequest.getAVP("0:274"));
      } 
    } 
    setRcvdOnStream(diameterRequest.getRcvdOnStream());
    IDiameterAVP originHost = DiameterDictionary.getInstance().getAttribute("0:264");
    originHost.setStringValue(Parameter.getInstance().getOwnDiameterIdentity());
    addAvp(originHost);
    IDiameterAVP originRealm = DiameterDictionary.getInstance().getAttribute("0:296");
    originRealm.setStringValue(Parameter.getInstance().getOwnDiameterRealm());
    addAvp(originRealm);
    if (resultCode != null) {
      IDiameterAVP resultCodeAVP = DiameterDictionary.getInstance().getAttribute("0:268");
      resultCodeAVP.setInteger(resultCode.getCode());
      addAvp(resultCodeAVP);
      if (resultCode != ResultCode.DIAMETER_SUCCESS)
        setErrorBit(); 
    } 
  }
  
  public void setHeaderFrom(DiameterRequest diameterRequest) {
    setRequestReceivedTime(diameterRequest.creationTimeMillis());
    setCommandCode(diameterRequest.getCommandCode());
    setApplicationID(diameterRequest.getApplicationID());
    setHop_by_hopIdentifier(diameterRequest.getHop_by_hopIdentifier());
    setEnd_to_endIdentifier(diameterRequest.getEnd_to_endIdentifier());
    if (diameterRequest.isProxiable())
      setProxiableBit(); 
  }
  
  private void cloneAndAdd(IDiameterAVP diameterAvp) {
    if (diameterAvp == null)
      return; 
    try {
      addAvp((IDiameterAVP)diameterAvp.clone());
    } catch (CloneNotSupportedException cloneNotSupportedException) {}
  }
  
  public DiameterAnswer(DiameterRequest diameterRequest) {
    this(diameterRequest, (ResultCode)null);
  }
  
  public DiameterAnswer() {}
  
  public DiameterRequest getAsDiameterRequest() {
    return null;
  }
  
  public DiameterAnswer getAsDiameterAnswer() {
    return this;
  }
  
  public long getRequestReceivedTime() {
    return this.requestReceivedTime;
  }
  
  public void setRequestReceivedTime(long requestReceivedTime) {
    this.requestReceivedTime = requestReceivedTime;
  }
  
  public String getAnsweringHost() {
    return this.answeringHost;
  }
  
  public void setAnsweringHost(String answeringHost) {
    this.answeringHost = answeringHost;
  }
  
  public boolean isFailureCategory() {
    IDiameterAVP resultCode = getAVP("0:268");
    if (resultCode == null)
      return false; 
    return (ResultCodeCategory.getResultCodeCategory(resultCode.getInteger())).isFailureCategory;
  }
}
