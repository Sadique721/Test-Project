package com.savbill.radius.utils;

public class ValidateCrudTransactionData 
{
	public static boolean validateStringTypeFieldValue(String fieldValue)
	{
		try
		{
			if(fieldValue == null || fieldValue.trim().isEmpty() || fieldValue.equalsIgnoreCase(RadiusConstants.BLANK_STRING))
			{
				return false;
			}
			return true;
		}
		catch (Throwable e) 
		{
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public static boolean validateLongTypeFieldValue(Long fieldValue)
	{
		try
		{
			if(fieldValue == null || fieldValue == 0)
			{
				return false;
			}
			return true;
		}
		catch (Throwable e) 
		{
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public static boolean validateIntegerTypeFieldValue(Integer fieldValue)
	{
		try
		{
			if(fieldValue == null || fieldValue == 0)
			{
				return false;
			}
			return true;
		}
		catch (Throwable e) 
		{
			throw new RuntimeException(e.getMessage());
		}
	}
	public String getClassName(Object obj)
	{
		Class<?> class1 = obj.getClass();
		String className = class1.getSimpleName();
		return className;
	}
	public static Integer validateMvnoId(Integer mvnoId)
	{
		try
		{
			if(mvnoId == null)
			{
				return 2;
			}
			return mvnoId;
		}
		catch (Throwable e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}
}
