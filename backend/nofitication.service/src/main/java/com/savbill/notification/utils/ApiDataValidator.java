package com.savbill.notification.utils;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.savbill.notification.exceptions.AuthException;
import com.savbill.notification.exceptions.CustomException;

@Component
public class ApiDataValidator
{

	
	  
    @Autowired
	Dao dao;
	  
	public void validateApiData(Long userMvnoId,Long mvnoSupplied,Object columnValue,String tableName,String columnName  ) throws CustomException, AuthException
	{
	
		
		/** First Validate the userMvnoId **/
	  try
	  { 
		  
		if(userMvnoId==0 || userMvnoId==null)
		{
			throw new CustomException(MessageConstants.INVALID_MVNOID_MESSAGE, HttpStatus.SC_BAD_REQUEST);
		}
		else
		{
			if(!authorize(userMvnoId,mvnoSupplied,columnValue,tableName,columnName))
			{
			  throw new AuthException(MessageConstants.GENRAL_AUTHORIZATION_FAILURE, HttpStatus.SC_FORBIDDEN);
			}
		}
		
	 }	
	  catch(AuthException authException)
	  {
		  
		throw authException;  
	  }
	  catch(CustomException customException)
	  {
		
		throw customException;  
	  }
	catch(Exception e)
	 {
		  
		  throw e;
	 } 
   }
	
	/** mvnoSupplied is the mvno supplied in @RequestParam **/
   public boolean authorize(Long userMvnoId,Long mvnoSupplied,Object columnValue,String tableName,String columnName) throws CustomException
   {
		boolean isAuthorized=false;
		
	    try
	    {
		Long mvnoId=dao.getMvnoId(columnValue, tableName, columnName);
	
		
	    if(mvnoId!=null)
		{
	    	if(mvnoId.equals(mvnoSupplied))
	    	{
			 isAuthorized=MvnoValidator.validateMvno(userMvnoId, mvnoId);
	    	}
	    	else
	    	{
	    		throw new CustomException(MessageConstants.MVNO_MISMATCH_MESSAGE, HttpStatus.SC_BAD_REQUEST);
	    	}
	    }
		
		else
		{	
		  throw new CustomException(MessageConstants.INAVLID_DATA_MESSAGE + columnValue, HttpStatus.SC_BAD_REQUEST);	
		}
	    }
	    catch(Exception e)
	    {
	    	
	    	throw e;
	    }
	    
	    return isAuthorized;
   }
	
}