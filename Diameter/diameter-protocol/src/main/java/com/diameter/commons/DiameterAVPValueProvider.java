package com.diameter.commons;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

public class DiameterAVPValueProvider extends MappedValueProvider implements AttributeValueProvider {
  
	@Nonnull
  private final DiameterPacket diameterPacket;
  
  public DiameterAVPValueProvider(@Nonnull DiameterPacket diameterPacket) {
    super(diameterPacket.getParameters());
    this.diameterPacket = diameterPacket;
  }
  
  public String getStringValue(String identifier) {
    try {
		return getStringValue(identifier, false);
	} catch (MissingIdentifierException e) {
		e.printStackTrace();
	}
    return null;
  }
  
  public long getLongValue(String identifier) throws InvalidTypeCastException, MissingIdentifierException {
    IDiameterAVP diameterAvp = this.diameterPacket.getAVP(identifier, true);
    if (diameterAvp != null)
      return diameterAvp.getInteger(); 
    if ("CommandCode".equalsIgnoreCase(identifier))
      return this.diameterPacket.getCommandCode(); 
    if ("ApplicationId".equalsIgnoreCase(identifier))
      return this.diameterPacket.getApplicationID(); 
    if ("ErrorFlag".equalsIgnoreCase(identifier))
      return DiameterUtility.booleanToInt(this.diameterPacket.isError()); 
    if ("RetransmittedFlag".equalsIgnoreCase(identifier))
      return DiameterUtility.booleanToInt(this.diameterPacket.isReTransmitted()); 
    if ("ProxyFlag".equalsIgnoreCase(identifier))
      return DiameterUtility.booleanToInt(this.diameterPacket.isProxiable()); 
    throw new MissingIdentifierException("Configured identifier not found: " + identifier);
  }
  
  public List<String> getStringValues(String identifier) throws InvalidTypeCastException, MissingIdentifierException {
    List<IDiameterAVP> diameterAVPList = this.diameterPacket.getAVPList(identifier, true);
    if (diameterAVPList != null) {
      List<String> stringValues = new ArrayList<>();
      for (IDiameterAVP iDiameterAVP : diameterAVPList)
        stringValues.add(iDiameterAVP.getStringValue()); 
      return stringValues;
    } 
    if ("CommandCode".equalsIgnoreCase(identifier)) {
      List<String> stringValues = new ArrayList<>(2);
      stringValues.add(String.valueOf(this.diameterPacket.getCommandCode()));
      return stringValues;
    } 
    if ("ApplicationId".equalsIgnoreCase(identifier)) {
      List<String> stringValues = new ArrayList<>(2);
      stringValues.add(String.valueOf(this.diameterPacket.getApplicationID()));
      return stringValues;
    } 
    if ("ErrorFlag".equalsIgnoreCase(identifier)) {
      List<String> stringValues = new ArrayList<>(2);
      stringValues.add(String.valueOf(DiameterUtility.booleanToInt(this.diameterPacket.isError())));
      return stringValues;
    } 
    if ("ProxyFlag".equalsIgnoreCase(identifier)) {
      List<String> stringValues = new ArrayList<>(2);
      stringValues.add(String.valueOf(DiameterUtility.booleanToInt(this.diameterPacket.isProxiable())));
      return stringValues;
    } 
    if ("RetransmittedFlag".equalsIgnoreCase(identifier)) {
      List<String> stringValues = new ArrayList<>(2);
      stringValues.add(String.valueOf(DiameterUtility.booleanToInt(this.diameterPacket.isReTransmitted())));
      return stringValues;
    } 
    throw new MissingIdentifierException("Configured identifier not found: " + identifier);
  }
  
  public List<Long> getLongValues(String identifier) throws InvalidTypeCastException, MissingIdentifierException {
    List<IDiameterAVP> diameterAVPList = this.diameterPacket.getAVPList(identifier, true);
    if (diameterAVPList != null) {
      List<Long> longValues = new ArrayList<>();
      for (IDiameterAVP iDiameterAVP : diameterAVPList)
        longValues.add(Long.valueOf(iDiameterAVP.getInteger())); 
      return longValues;
    } 
    if ("CommandCode".equalsIgnoreCase(identifier)) {
      List<Long> longValues = new ArrayList<>(2);
      longValues.add(Long.valueOf(this.diameterPacket.getCommandCode()));
      return longValues;
    } 
    if ("ApplicationId".equalsIgnoreCase(identifier)) {
      List<Long> longValues = new ArrayList<>(2);
      longValues.add(Long.valueOf(this.diameterPacket.getApplicationID()));
      return longValues;
    } 
    if ("ErrorFlag".equalsIgnoreCase(identifier)) {
      List<Long> longValues = new ArrayList<>(2);
      longValues.add(Long.valueOf(DiameterUtility.booleanToInt(this.diameterPacket.isError())));
      return longValues;
    } 
    if ("ProxyFlag".equalsIgnoreCase(identifier)) {
      List<Long> longValues = new ArrayList<>(2);
      longValues.add(Long.valueOf(DiameterUtility.booleanToInt(this.diameterPacket.isProxiable())));
      return longValues;
    } 
    if ("RetransmittedFlag".equalsIgnoreCase(identifier)) {
      List<Long> longValues = new ArrayList<>(2);
      longValues.add(Long.valueOf(DiameterUtility.booleanToInt(this.diameterPacket.isReTransmitted())));
      return longValues;
    } 
    throw new MissingIdentifierException("Configured identifier not found: " + identifier);
  }
  
  public String getDictionaryKey(String identifier) throws MissingIdentifierException, InvalidTypeCastException {
    return getStringValue(identifier, false);
  }
  
  private String getStringValue(String identifier, boolean useDictionaryValue) throws MissingIdentifierException {
    IDiameterAVP diameterAvp = this.diameterPacket.getAVP(identifier, true);
    if (diameterAvp != null)
      return diameterAvp.getStringValue(useDictionaryValue); 
    if ("CommandCode".equalsIgnoreCase(identifier))
      return String.valueOf(this.diameterPacket.getCommandCode()); 
    if ("ApplicationId".equalsIgnoreCase(identifier))
      return String.valueOf(this.diameterPacket.getApplicationID()); 
    if ("ErrorFlag".equalsIgnoreCase(identifier))
      return String.valueOf(DiameterUtility.booleanToInt(this.diameterPacket.isError())); 
    if ("RetransmittedFlag".equalsIgnoreCase(identifier))
      return String.valueOf(DiameterUtility.booleanToInt(this.diameterPacket.isReTransmitted())); 
    if ("ProxyFlag".equalsIgnoreCase(identifier))
      return String.valueOf(DiameterUtility.booleanToInt(this.diameterPacket.isProxiable())); 
    throw new MissingIdentifierException("Configured identifier not found: " + identifier);
  }
}
