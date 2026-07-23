package com.savbill.partnermanagement.core.service.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


public class PasswordGenerator 
{
	private static Log logger = LogFactory.getLog(PasswordGenerator.class);
	public static void main(String[] args) throws Exception 
	{

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String password = "admin123";
		String encodedPassword = encoder.encode(password);
		logger.info(encodedPassword);
	}
	
	public String encryptPassword(String password)
	{
		try
		{
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			return encoder.encode(password);
		}
		catch (Exception e) 
		{
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public boolean isPasswordMatched(String password, String encodedPassword)
	{
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder.matches(password, encodedPassword);
	}
}
