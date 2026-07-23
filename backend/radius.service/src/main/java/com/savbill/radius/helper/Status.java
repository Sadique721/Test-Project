package com.savbill.radius.helper;

public enum Status {

	ACTIVE("Active"), INACTIVE("Inactive");
	
	String value;

	private Status(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
}
