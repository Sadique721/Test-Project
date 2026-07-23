/**
 * $Id: IntegerAttribute.java,v 1.4 2005/09/04 22:11:03 wuttke Exp $
 * Created on 08.04.2005
 * @author Matthias Wuttke
 * @version $Revision: 1.4 $
 */
package com.savbill.radius.aaa.attribute;

import com.savbill.radius.aaa.dictionary.AttributeType;
import com.savbill.radius.aaa.util.RadiusException;

/**
 * This class represents a Radius attribute which only
 * contains a 32 bit integer.
 */
public class IntegerAttribute extends RadiusAttribute {

	/**
	 * Constructs an empty integer attribute.
	 */
	public IntegerAttribute() {
		super();
	}
	
	/**
	 * Constructs an integer attribute with the given value.
	 * @param type attribute type
	 * @param value attribute value
	 */
	public IntegerAttribute(int type, Long value) {
		setAttributeType(type);
		setAttributeValue(value);
	}
	
	/**
	 * Returns the string value of this attribute.
	 * @return a string
	 */
	public Long getAttributeValueInt() {
		byte[] data = getAttributeData();
		return (long) (((long)(data[0] & 0x0ffL) << 24) | ((long)(data[1] & 0x0ffL) << 16) | ((long)(data[2] & 0x0ffL) << 8) | ((long)(data[3] & 0x0ffL)));
	}
	
	/**
	 * Returns the value of this attribute as a string.
	 * Tries to resolve enumerations.
	 * @see RadiusAttribute#getAttributeValue()
	 */
	public String getAttributeValue() {
		Long value = getAttributeValueInt();
		AttributeType at = getAttributeTypeObject();
		if (at != null) {
			String name = at.getEnumeration(value);
			if (name != null)
				return name;
		}

		return Long.toString(value);
	}
	
	/**
	 * Sets the value of this attribute.
	 * @param value integer value
	 */
	public void setAttributeValue(Long value) {
		byte[] data = new byte[4];
		data[0] = (byte)(value >> 24 & 0x0ff);
		data[1] = (byte)(value >> 16 & 0x0ff);
		data[2] = (byte)(value >> 8 & 0x0ff);
		data[3] = (byte)(value & 0x0ff);
		setAttributeData(data);
	}
	
	/**
	 * Sets the value of this attribute.
	 * @exception NumberFormatException if value is not a number and constant cannot be resolved
	 * @see RadiusAttribute#setAttributeValue(java.lang.String)
	 */
	public void setAttributeValue(String value) {
		AttributeType at = getAttributeTypeObject();
		if (at != null) {
			Long val = at.getEnumeration(value);
			if (val != null) {
				setAttributeValue(val.longValue());
				return;
			}
		}
		
		setAttributeValue(Long.parseLong(value));
	}
	
	/**
	 * Check attribute length.
	 * @see RadiusAttribute#readAttribute(byte[], int, int)
	 */
	public void readAttribute(byte[] data, int offset, int length)
	throws RadiusException {
		if (length != 6)
			throw new RadiusException("integer attribute: expected 4 bytes data");
		super.readAttribute(data, offset, length);
	}
	
}
