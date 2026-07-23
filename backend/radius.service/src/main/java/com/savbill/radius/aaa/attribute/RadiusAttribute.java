/**
 * $Id: RadiusAttribute.java,v 1.4 2006/02/20 23:37:38 wuttke Exp $
 * Created on 07.04.2005
 * Released under the terms of the LGPL
 * @author Matthias Wuttke
 * @version $Revision: 1.4 $
 */
package com.savbill.radius.aaa.attribute;

import com.savbill.radius.aaa.dictionary.AttributeType;
import com.savbill.radius.aaa.dictionary.DefaultDictionary;
import com.savbill.radius.aaa.dictionary.Dictionary;
import com.savbill.radius.aaa.util.RadiusException;
import com.savbill.radius.aaa.util.RadiusUtil;

/**
 * This class represents a generic Radius attribute. Subclasses implement
 * methods to access the fields of special attributes.
 */
public class RadiusAttribute {

	/**
	 * Constructs an empty Radius attribute.
	 */
	public RadiusAttribute() {	
	}
	
	/**
	 * Constructs a Radius attribute with the specified
	 * type and data.
	 * @param type attribute type, see AttributeTypes.*
	 * @param data attribute data
	 */
	public RadiusAttribute(int type, byte[] data) {
		setAttributeType(type);
		setAttributeData(data);
	}
	
	/**
	 * Returns the data for this attribute.
	 * @return attribute data
	 */
	public byte[] getAttributeData() {
		return attributeData;
	}
	
	/**
	 * Sets the data for this attribute.
	 * @param attributeData attribute data
	 */
	public void setAttributeData(byte[] attributeData) {
		if (attributeData == null)
			throw new NullPointerException("attribute data is null");
		this.attributeData = attributeData;
	}

	/**
	 * Returns the type of this Radius attribute.
	 * @return type code, 0-255
	 */
	public int getAttributeType() {
		return attributeType;
	}
	
	/**
	 * Sets the type of this Radius attribute.
	 * @param attributeType type code, 0-255
	 */
	public void setAttributeType(int attributeType) {
		if (attributeType < 0 || attributeType > 2566)
			throw new IllegalArgumentException("attribute type invalid: " + attributeType);
		this.attributeType = attributeType;
	}
	
	/**
	 * Sets the value of the attribute using a string.
	 * @param value value as a string
	 */
	public void setAttributeValue(String value) {
		throw new RuntimeException("cannot set the value of attribute " + attributeType + " as a string");
	}
	
	/**
	 * Gets the value of this attribute as a string.
	 * @return value
	 * @exception RadiusException if the value is invalid
	 */
	public String getAttributeValue() {
		return RadiusUtil.getHexString(getAttributeData());
	}
	
	/**
	 * Gets the Vendor-Id of the Vendor-Specific attribute this
	 * attribute belongs to. Returns -1 if this attribute is not
	 * a sub attribute of a Vendor-Specific attribute.
	 * @return vendor ID
	 */
	public int getVendorId() {
		return vendorId;
	}
	
	/**
	 * Sets the Vendor-Id of the Vendor-Specific attribute this
	 * attribute belongs to. The default value of -1 means this attribute
	 * is not a sub attribute of a Vendor-Specific attribute.
	 * @param vendorId vendor ID
	 */	
	public void setVendorId(int vendorId) {
		this.vendorId = vendorId;
	}

	/**
	 * Returns the dictionary this Radius attribute uses.
	 * @return Dictionary instance
	 */
	public Dictionary getDictionary() {
		return dictionary;
	}
	
	/**
	 * Sets a custom dictionary to use. If no dictionary is set,
	 * the default dictionary is used.
	 * @param dictionary Dictionary class to use
	 * @see DefaultDictionary
	 */
	public void setDictionary(Dictionary dictionary) {
		this.dictionary = dictionary;
	}
	
	
	
	
	public byte getTag() {
		return tag;
	}

	public void setTag(byte tag) {
		this.tag = tag;
	}

	/**
	 * Returns this attribute encoded as a byte array.
	 * @return attribute
	 */
	public byte[] writeAttribute() {
		byte[] attr=null;
		try {
			
			if (getAttributeType() == -1)
				throw new IllegalArgumentException("attribute type not set");
			if (attributeData == null)
				throw new NullPointerException("attribute data not set");
			if(getAttributeType()<255) {
//							System.out.println("Get Attibute Type:"+getAttributeType()+":Vendor:"+getVendorId()+":Lengh:"+attributeData.length);
					if(getAttributeType()==140 && getVendorId()==4874) {
						attr = new byte[2 + attributeData.length];
						attr[0] = (byte) getAttributeType();
						attr[1] = (byte)(2 + attributeData.length);
						attributeData[0]=01;
						System.arraycopy(attributeData, 0, attr, 2, attributeData.length);
					}
					else if(getAttributeType()==8 && getVendorId()==4874) {
						int length=attributeData.length+1;
						attr = new byte[2 + length];
						attr[0] = (byte) getAttributeType();
						attr[1] = (byte)(2 + length);
						attr[2]=01;
						System.arraycopy(attributeData, 0, attr, 3, attributeData.length);
					}
					else if(getAttributeType()==9 && getVendorId()==4874) {
						int length=attributeData.length+1;
						attr = new byte[2 + length];
						attr[0] = (byte) getAttributeType();
						attr[1] = (byte)(2 + length);
						attr[2]=01;
						System.arraycopy(attributeData, 0, attr, 3, attributeData.length);
					}
					else if(getAttributeType()==33 && getVendorId()==4874) {
						attr = new byte[2 + attributeData.length];
						attr[0] = (byte) getAttributeType();
						attr[1] = (byte)(2 + attributeData.length);
						attributeData[0]=01;
						System.arraycopy(attributeData, 0, attr, 2, attributeData.length);
					}
					else if(getAttributeType()==65 && getVendorId()==4874) {
						byte tag=getTag();
						int length=attributeData.length+1;
						attr = new byte[2 + length];
						attr[0] = (byte) getAttributeType();
						attr[1] = (byte)(2 + length);
						attr[2]=tag;
						System.arraycopy(attributeData, 0, attr, 3, attributeData.length);
					}
					else if(getAttributeType()==67 && getVendorId()==4874) {
						attr = new byte[2 + attributeData.length];
						attr[0] = (byte) getAttributeType();
						attr[1] = (byte)(2 + attributeData.length);
						attributeData[0]=01;
						System.arraycopy(attributeData, 0, attr, 2, attributeData.length);
					}
					else if(getAttributeType()==68 && getVendorId()==4874) {
						attr = new byte[2 + attributeData.length];
						attr[0] = (byte) getAttributeType();
						attr[1] = (byte)(2 + attributeData.length);
						attributeData[0]=01;
						System.arraycopy(attributeData, 0, attr, 2, attributeData.length);
					}
					else if(getAttributeType()==69 && getVendorId()==4874) {
						attr = new byte[2 + attributeData.length];
						attr[0] = (byte) getAttributeType();
						attr[1] = (byte)(2 + attributeData.length);
						attributeData[0]=01;
						System.arraycopy(attributeData, 0, attr, 2, attributeData.length);
					}
					else if(getAttributeType()==179 && getVendorId()==4874) {
						attr = new byte[2 + attributeData.length];
						attr[0] = (byte) getAttributeType();
						attr[1] = (byte)(2 + attributeData.length);
						attributeData[0]=01;
						System.arraycopy(attributeData, 0, attr, 2, attributeData.length);
					}
					else if(getAttributeType()==180 && getVendorId()==4874) {
						int length=attributeData.length+1;
						attr = new byte[2 + length];
						attr[0] = (byte) getAttributeType();
						attr[1] = (byte)(2 + length);
						attr[2]=01;
						System.arraycopy(attributeData, 0, attr, 3, attributeData.length);
					}
					else {
						attr = new byte[2 + attributeData.length];
						attr[0] = (byte) getAttributeType();
						attr[1] = (byte)(2 + attributeData.length);
						System.arraycopy(attributeData, 0, attr, 2, attributeData.length);
					}
			}
			else {
				// 2 Byte Atrribute Id detail
				// JK Need to work
				
				if(getAttributeType()==1801) {
						attr = new byte[3 + attributeData.length];
						attr[0] = 07;
						attr[1] = 9;
						////		System.out.println("Setting Attribute byte:"+(byte)(getAttributeType())+":Plain:"+getAttributeType());
						attr[2] = (byte)(3 + attributeData.length);
						System.arraycopy(attributeData, 0, attr, 3, attributeData.length);
				}
				
				if(getAttributeType()==1803) 
				{
					attr = new byte[3 + attributeData.length];
					attr[0] = 07;
					attr[1] = 11;
					////		System.out.println("Setting Attribute byte:"+(byte)(getAttributeType())+":Plain:"+getAttributeType()+":"+attr[1]);
					attr[2] = (byte)(3 + attributeData.length);
					System.arraycopy(attributeData, 0, attr, 3, attributeData.length);
				} 
				

				if(getAttributeType()==1804) 
				{
					attr = new byte[3 + attributeData.length];
					attr[0] = 07;
					attr[1] = 12;
					////		System.out.println("Setting Attribute byte:"+(byte)(getAttributeType())+":Plain:"+getAttributeType()+":"+attr[1]);
					attr[2] = (byte)(3 + attributeData.length);
					System.arraycopy(attributeData, 0, attr, 3, attributeData.length);
				} 				

				if(getAttributeType()==1805) 
				{
					attr = new byte[3 + attributeData.length];
					attr[0] = 07;
					attr[1] = 13;
					////		System.out.println("Setting Attribute byte:"+(byte)(getAttributeType())+":Plain:"+getAttributeType()+":"+attr[1]);
					attr[2] = (byte)(3 + attributeData.length);
					System.arraycopy(attributeData, 0, attr, 3, attributeData.length);
				} 

				if(getAttributeType()==1806) 
				{
					attr = new byte[3 + attributeData.length];
					attr[0] = 07;
					attr[1] = 14;
					////		System.out.println("Setting Attribute byte:"+(byte)(getAttributeType())+":Plain:"+getAttributeType()+":"+attr[1]);
					attr[2] = (byte)(3 + attributeData.length);
					System.arraycopy(attributeData, 0, attr, 3, attributeData.length);
				} 				

				if(getAttributeType()==1807) 
				{
					attr = new byte[3 + attributeData.length];
					attr[0] = 07;
					attr[1] = 15;
					////		System.out.println("Setting Attribute byte:"+(byte)(getAttributeType())+":Plain:"+getAttributeType()+":"+attr[1]);
					attr[2] = (byte)(3 + attributeData.length);
					System.arraycopy(attributeData, 0, attr, 3, attributeData.length);
				} 				

				if(getAttributeType()==1808) 
				{
						attr = new byte[3 + attributeData.length];
						attr[0] = 07;
						attr[1] = 16;
						////		System.out.println("Setting Attribute byte:"+(byte)(getAttributeType())+":Plain:"+getAttributeType()+":"+attr[1]);
						attr[2] = (byte)(3 + attributeData.length);
						System.arraycopy(attributeData, 0, attr, 3, attributeData.length);
				}

				if(getAttributeType()==1809) 
				{
					attr = new byte[3 + attributeData.length];
					attr[0] = 07;
					attr[1] = 17;
					////		System.out.println("Setting Attribute byte:"+(byte)(getAttributeType())+":Plain:"+getAttributeType()+":"+attr[1]);
					attr[2] = (byte)(3 + attributeData.length);
					System.arraycopy(attributeData, 0, attr, 3, attributeData.length);
				} 				

				if(getAttributeType()==1810) 
				{
					attr = new byte[3 + attributeData.length];
					attr[0] = 07;
					attr[1] = 18;
					////		System.out.println("Setting Attribute byte:"+(byte)(getAttributeType())+":Plain:"+getAttributeType()+":"+attr[1]);
					attr[2] = (byte)(3 + attributeData.length);
					System.arraycopy(attributeData, 0, attr, 3, attributeData.length);
				} 				

				if(getAttributeType()==1811) 
				{
					attr = new byte[3 + attributeData.length];
					attr[0] = 07;
					attr[1] = 19;
					////		System.out.println("Setting Attribute byte:"+(byte)(getAttributeType())+":Plain:"+getAttributeType()+":"+attr[1]);
					attr[2] = (byte)(3 + attributeData.length);
					System.arraycopy(attributeData, 0, attr, 3, attributeData.length);
				}

				if(getAttributeType()==1812) 
				{
					attr = new byte[3 + attributeData.length];
					attr[0] = 07;
					attr[1] = 20;
					////		System.out.println("Setting Attribute byte:"+(byte)(getAttributeType())+":Plain:"+getAttributeType()+":"+attr[1]);
					attr[2] = (byte)(3 + attributeData.length);
					System.arraycopy(attributeData, 0, attr, 3, attributeData.length);
				} 				

				if(getAttributeType()==1813) 
				{
					attr = new byte[3 + attributeData.length];
					attr[0] = 07;
					attr[1] = 21;
					////		System.out.println("Setting Attribute byte:"+(byte)(getAttributeType())+":Plain:"+getAttributeType()+":"+attr[1]);
					attr[2] = (byte)(3 + attributeData.length);
					System.arraycopy(attributeData, 0, attr, 3, attributeData.length);
				} 


				if(getAttributeType()==1814) 
				{
					attr = new byte[3 + attributeData.length];
					attr[0] = 07;
					attr[1] = 22;
					////		System.out.println("Setting Attribute byte:"+(byte)(getAttributeType())+":Plain:"+getAttributeType()+":"+attr[1]);
					attr[2] = (byte)(3 + attributeData.length);
					System.arraycopy(attributeData, 0, attr, 3, attributeData.length);
				} 				

				if(getAttributeType()==1815) 
				{
					attr = new byte[3 + attributeData.length];
					attr[0] = 07;
					attr[1] = 23;
					////		System.out.println("Setting Attribute byte:"+(byte)(getAttributeType())+":Plain:"+getAttributeType()+":"+attr[1]);
					attr[2] = (byte)(3 + attributeData.length);
					System.arraycopy(attributeData, 0, attr, 3, attributeData.length);
				} 

				if(getAttributeType()==1816) 
				{
					attr = new byte[3 + attributeData.length];
					attr[0] = 07;
					attr[1] = 24;
					////		System.out.println("Setting Attribute byte:"+(byte)(getAttributeType())+":Plain:"+getAttributeType()+":"+attr[1]);
					attr[2] = (byte)(3 + attributeData.length);
					System.arraycopy(attributeData, 0, attr, 3, attributeData.length);
				} 				

				if(getAttributeType()==1820) 
				{
					attr = new byte[3 + attributeData.length];
					attr[0] = 07;
					attr[1] = 28;
					////		System.out.println("Setting Attribute byte:"+(byte)(getAttributeType())+":Plain:"+getAttributeType()+":"+attr[1]);
					attr[2] = (byte)(3 + attributeData.length);
					System.arraycopy(attributeData, 0, attr, 3, attributeData.length);
				} 

				if(getAttributeType()==1821) 
				{
					attr = new byte[3 + attributeData.length];
					attr[0] = 07;
					attr[1] = 29;
					////		System.out.println("Setting Attribute byte:"+(byte)(getAttributeType())+":Plain:"+getAttributeType()+":"+attr[1]);
					attr[2] = (byte)(3 + attributeData.length);
					System.arraycopy(attributeData, 0, attr, 3, attributeData.length);
				}

				if(getAttributeType()==1822) 
				{
					attr = new byte[3 + attributeData.length];
					attr[0] = 07;
					attr[1] = 30;
					////		System.out.println("Setting Attribute byte:"+(byte)(getAttributeType())+":Plain:"+getAttributeType()+":"+attr[1]);
					attr[2] = (byte)(3 + attributeData.length);
					System.arraycopy(attributeData, 0, attr, 3, attributeData.length);
				} 				

				if(getAttributeType()==1823) 
				{
					attr = new byte[3 + attributeData.length];
					attr[0] = 07;
					attr[1] = 31;
					////		System.out.println("Setting Attribute byte:"+(byte)(getAttributeType())+":Plain:"+getAttributeType()+":"+attr[1]);
					attr[2] = (byte)(3 + attributeData.length);
					System.arraycopy(attributeData, 0, attr, 3, attributeData.length);
				} 				

				if(getAttributeType()==1824) 
				{
					attr = new byte[3 + attributeData.length];
					attr[0] = 07;
					attr[1] = 32;
					////		System.out.println("Setting Attribute byte:"+(byte)(getAttributeType())+":Plain:"+getAttributeType()+":"+attr[1]);
					attr[2] = (byte)(3 + attributeData.length);
					System.arraycopy(attributeData, 0, attr, 3, attributeData.length);
				} 				

				if(getAttributeType()==1825) 
				{
					attr = new byte[3 + attributeData.length];
					attr[0] = 07;
					attr[1] = 33;
					////		System.out.println("Setting Attribute byte:"+(byte)(getAttributeType())+":Plain:"+getAttributeType()+":"+attr[1]);
					attr[2] = (byte)(3 + attributeData.length);
					System.arraycopy(attributeData, 0, attr, 3, attributeData.length);
				}
				
				if(getAttributeType()==1826) 
				{
					attr = new byte[3 + attributeData.length];
					attr[0] = 07;
					attr[1] = 34;
					////		System.out.println("Setting Attribute byte:"+(byte)(getAttributeType())+":Plain:"+getAttributeType()+":"+attr[1]);
					attr[2] = (byte)(3 + attributeData.length);
					System.arraycopy(attributeData, 0, attr, 3, attributeData.length);
				}

				if(getAttributeType()==1827) 
				{
					attr = new byte[3 + attributeData.length];
					attr[0] = 07;
					attr[1] = 35;
					////		System.out.println("Setting Attribute byte:"+(byte)(getAttributeType())+":Plain:"+getAttributeType()+":"+attr[1]);
					attr[2] = (byte)(3 + attributeData.length);
					System.arraycopy(attributeData, 0, attr, 3, attributeData.length);
				} 


				if(getAttributeType()==1828) 
				{
					attr = new byte[3 + attributeData.length];
					attr[0] = 07;
					attr[1] = 36;
					////		System.out.println("Setting Attribute byte:"+(byte)(getAttributeType())+":Plain:"+getAttributeType()+":"+attr[1]);
					attr[2] = (byte)(3 + attributeData.length);
					System.arraycopy(attributeData, 0, attr, 3, attributeData.length);
				} 				

				if(getAttributeType()==1829) 
				{
					attr = new byte[3 + attributeData.length];
					attr[0] = 07;
					attr[1] =37 ;
					////		System.out.println("Setting Attribute byte:"+(byte)(getAttributeType())+":Plain:"+getAttributeType()+":"+attr[1]);
					attr[2] = (byte)(3 + attributeData.length);
					System.arraycopy(attributeData, 0, attr, 3, attributeData.length);
				} 				

				if(getAttributeType()==1830) 
				{
					attr = new byte[3 + attributeData.length];
					attr[0] = 07;
					attr[1] = 38;
					////		System.out.println("Setting Attribute byte:"+(byte)(getAttributeType())+":Plain:"+getAttributeType()+":"+attr[1]);
					attr[2] = (byte)(3 + attributeData.length);
					System.arraycopy(attributeData, 0, attr, 3, attributeData.length);
				} 				

				if(getAttributeType()==1831) 
				{
					attr = new byte[3 + attributeData.length];
					attr[0] = 07;
					attr[1] =39 ;
					////		System.out.println("Setting Attribute byte:"+(byte)(getAttributeType())+":Plain:"+getAttributeType()+":"+attr[1]);
					attr[2] = (byte)(3 + attributeData.length);
					System.arraycopy(attributeData, 0, attr, 3, attributeData.length);
				} 				

				if(getAttributeType()==1832) 
				{
					attr = new byte[3 + attributeData.length];
					attr[0] = 07;
					attr[1] = 40;
					////		System.out.println("Setting Attribute byte:"+(byte)(getAttributeType())+":Plain:"+getAttributeType()+":"+attr[1]);
					attr[2] = (byte)(3 + attributeData.length);
					System.arraycopy(attributeData, 0, attr, 3, attributeData.length);
				} 				
	
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
				return attr;
	}
	
	
	public byte[] writeAttributeProxy() {
		if (getAttributeType() == -1)
			throw new IllegalArgumentException("attribute type not set");
		if (attributeData == null)
			throw new NullPointerException("attribute data not set");

		byte[] attr = new byte[2 + attributeData.length];
		attr[0] = (byte) getAttributeType();
		attr[1] = (byte) (2 + attributeData.length);
		System.arraycopy(attributeData, 0, attr, 2, attributeData.length);
		return attr;
	}
	
	public static String print(byte[] bytes) {
	    StringBuilder sb = new StringBuilder();
	    sb.append("[ ");
	    for (byte b : bytes) {
	        sb.append(String.format("0x%02X ", b));
	    }
	    sb.append("]");
	    return sb.toString();
	}
	/**
	 * Reads in this attribute from the passed byte array.
	 * @param data
	 */
	public void readAttribute(byte[] data, int offset, int length) 
	throws RadiusException {
		if (length < 2)
			throw new RadiusException("attribute length too small: " + length);
		int attrType = data[offset] & 0x0ff;
		int attrLen = data[offset + 1] & 0x0ff;
		byte[] attrData = new byte[attrLen - 2];
		System.arraycopy(data, offset + 2, attrData, 0, attrLen - 2);
		setAttributeType(attrType);
		setAttributeData(attrData);
		setAttributeLength(attrLen);
	}
	
	//New method created to support reading two bytes
	public void readAttribute(boolean bMultiByteSubType,byte[] data, int offset, int length) throws RadiusException {
		if (length < 2)
			throw new RadiusException("attribute length too small: " + length);
		
		int attrType =-1;
		int attrLen = -1;
		if(bMultiByteSubType) {
			int val1=data[offset] & 0x0ff;
			int val2=data[(offset + 1)] & 0x0ff;  
			attrType = (16*16)*val1+val2;
			attrLen = data[offset + 2] & 0x0ff;
		}else {
			attrType = data[offset] & 0x0ff;
			attrLen = data[offset + 1] & 0x0ff;
		}
		byte[] attrData = new byte[attrLen - 3];
		System.arraycopy(data, offset + 3, attrData, 0, attrLen - 3);
		setAttributeType(attrType);
		setAttributeData(attrData);
	}
	
	/**
	 * String representation for debugging purposes.
	 * @see Object#toString()
	 */
	public String toString() {
		String name;
		
		// determine attribute name
		AttributeType at = getAttributeTypeObject();
		if (at != null)
			name = at.getName();
		else if (getVendorId() != -1)
			name = "Unknown-Sub-Attribute-" + getAttributeType();
		else
			name = "Unknown-Attribute-" + getAttributeType();
		
		// indent sub attributes
		if (getVendorId() != -1)
			name = "  " + name;
		
		return name + ": " + getAttributeValue();
	}
	
	/**
	 * Retrieves an AttributeType object for this attribute.
	 * @return AttributeType object for (sub-)attribute or null
	 */
	public AttributeType getAttributeTypeObject() {
		if (getVendorId() != -1)
			return dictionary.getAttributeTypeByCode(getVendorId(), getAttributeType());
		else
			return dictionary.getAttributeTypeByCode(getAttributeType());
	}
	
	/**
	 * Creates a RadiusAttribute object of the appropriate type.
	 * @param dictionary Dictionary to use
	 * @param vendorId vendor ID or -1
	 * @param attributeType attribute type
	 * @return RadiusAttribute object
	 */
	public static RadiusAttribute createRadiusAttribute(Dictionary dictionary, int vendorId, int attributeType) {
		RadiusAttribute attribute = new RadiusAttribute();
		
		AttributeType at = dictionary.getAttributeTypeByCode(vendorId, attributeType);
		if (at != null && at.getAttributeClass() != null) {
			try {
				attribute = (RadiusAttribute)at.getAttributeClass().newInstance();
			} catch (Exception e) {
				// error instantiating class - should not occur
			}
		}
		
		attribute.setAttributeType(attributeType);
		attribute.setDictionary(dictionary);
		attribute.setVendorId(vendorId);
		
		return attribute;
	}

	/**
	 * Creates a Radius attribute, including vendor-specific
	 * attributes. The default dictionary is used.
	 * @param vendorId vendor ID or -1
	 * @param attributeType attribute type
	 * @return RadiusAttribute instance
	 */
	public static RadiusAttribute createRadiusAttribute(int vendorId, int attributeType) {
		Dictionary dictionary = DefaultDictionary.getDefaultDictionary();
		return createRadiusAttribute(dictionary, vendorId, attributeType);
	}
		
	/**
	 * Creates a Radius attribute. The default dictionary is
	 * used.
	 * @param attributeType attribute type
	 * @return RadiusAttribute instance
	 */
	public static RadiusAttribute createRadiusAttribute(int attributeType) {
		Dictionary dictionary = DefaultDictionary.getDefaultDictionary();
		return createRadiusAttribute(dictionary, -1, attributeType);
	}

	//TODO: have to set this for all inheritors of this class.
	public void setAttributeLength(int attributeLength) {
		this.attributeLength = attributeLength;
	}

	public int getAttributeLength() {
		return attributeLength;
	}


	/**
	 * Dictionary to look up attribute names.
	 */
	private Dictionary dictionary = DefaultDictionary.getDefaultDictionary();

	private int attributeLength;

	/**
	 * Attribute type
	 */
	private int attributeType = -1;
	
	/**
	 * Vendor ID, only for sub-attributes of Vendor-Specific attributes.
	 */
	private int vendorId = -1;

	private byte tag = 01;

	/**
	 * Attribute data
	 */
	private byte[] attributeData = null;

}
