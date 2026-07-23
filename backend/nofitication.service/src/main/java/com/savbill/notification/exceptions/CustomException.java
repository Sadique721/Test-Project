package com.savbill.notification.exceptions;

public class CustomException extends Exception 
{
	private int statuscode=400;
	private String customMessage="Bad Request";

	public CustomException()
    {
		super();
	
	}

	public CustomException(String customMessage,int statuscode) 
	{
		super(customMessage);
		this.statuscode=statuscode;
		this.customMessage=customMessage;
	}

	public int getStatuscode()
    {
		return statuscode;
	}
	
}
