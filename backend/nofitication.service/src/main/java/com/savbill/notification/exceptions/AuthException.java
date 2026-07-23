package com.savbill.notification.exceptions;

public class AuthException extends Exception
{
	private int statuscode=401;
    private String customMessage="Authorization Failed"; 
	
	public AuthException()
	{
		super();
	}

	public AuthException(String customMessage, int statuscode)
	{
		super(customMessage);
		this.customMessage=customMessage;
		this.statuscode=statuscode;
		
	}

	public int getStatuscode() 
	{
		return statuscode;
	}

}
