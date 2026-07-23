package com.savbill.radius.aaa.data;

import java.io.Serializable;

public class ReplyItemData implements Serializable{
	private String attribute;
	private String attributeValue;
	
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	public String getAttributeValue() {
		return attributeValue;
	}
	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}
	
	public String toString() {
	    return "DATA : Atttibute:"+attribute+":Value:"+attributeValue+":";
	}
	
	
}
