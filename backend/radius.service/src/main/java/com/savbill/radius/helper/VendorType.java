package com.savbill.radius.helper;

public enum VendorType {
	VENDOR(1,"VENDOR"),
	STANDARD(2,"STANDARD");
	
	private int number;
	
	private String name;
	
	VendorType(int number,String name) {
		this.number = number;
		this.name = name;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
