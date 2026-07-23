package com.savbill.notification.utils;

import java.util.*;

import com.savbill.notification.helper.LoginDto;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.savbill.notification.helper.UserDto;


import com.savbill.notification.exceptions.CustomException;


@Component
public class Dao
{
	  

	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	@Autowired
	DataExtractor dataExtractor;
	
	@Autowired
	DataExtractorForUsernameExists usernameExtractor;
	
	@Autowired
	UserDetailsExtractor userDetailsExtractor;
	
	@Autowired
	RoleNameExtractor rolenamextractor;
	
	
	@Autowired
	UserNamePasswordExtractor usernamepasswordextractor;
	
	public Long getMvnoId(Object columnValue,String tableName,String columnName) throws CustomException
	{
		Long mvnoId=null;
		
		Map<String,Object> parameters= new HashMap<>();
		parameters.put("columnValue",columnValue);
		
		try
		{
		 
		 String sqlQuery=Queries.fetchMvnoIdQuery(tableName, columnName);	
		 mvnoId=namedParameterJdbcTemplate.query(sqlQuery,parameters,dataExtractor);
		 //log.info("======================================================Fired the Query to Database :- " + sqlQuery);
		}
		catch(Exception exception)
		{
		 // log.error("Database Connection failed");
	      throw new CustomException(MessageConstants.DATABASE_EXCEPTION_MESSAGE, HttpStatus.SC_INTERNAL_SERVER_ERROR);
		}
	    return mvnoId;	
	}
	
	public String  checkUsernameInDatabase(String username) throws CustomException
	{
	  String usernamefromDatabase=null;
	  
	  Map<String,Object> parameters = new HashMap<>();
	  parameters.put("username",username);
	  
	  try
	  {
		  usernamefromDatabase= namedParameterJdbcTemplate.query(Queries.FETCH_USERNAME_FROM_DATABASE,parameters, usernameExtractor);   
	     // log.info("======================================================Fired this Query to Database :- " + Queries.FETCH_USERNAME_FROM_DATABASE);
	  }
	  catch(Exception e)
	  {
		 // log.error("Database Connection failed");
		  throw new CustomException(MessageConstants.DATABASE_EXCEPTION_MESSAGE,HttpStatus.SC_INTERNAL_SERVER_ERROR );
	  }
	  
	  return usernamefromDatabase;
	}	
	
	public Long getMvnoId(String username) throws CustomException
	{
		Long mvnoId=null;
		
		Map<String,Object> parameters  = new HashMap<>();
		parameters.put("username",username);
		
		try
		{
			mvnoId=namedParameterJdbcTemplate.query(Queries.GET_MVNOID_FROM_USERNAME,parameters,dataExtractor);
			//log.info("======================================================Fired Query to Database :- " + Queries.GET_MVNOID_FROM_USERNAME);
		}
		catch(Exception e)
		{
			//  log.error("Database Connection failed");
			  throw new CustomException(MessageConstants.DATABASE_EXCEPTION_MESSAGE,HttpStatus.SC_INTERNAL_SERVER_ERROR);
		}
		
		return mvnoId;
	}
	
	
	
	public UserDto getUserDetailsFromUsername(String username) 
	{
        UserDto userDto=null; 
		
	    Map<String,Object> parameters=new HashMap<>();
	    parameters.put("username",username);
	    try
		{ 
	    	userDto=namedParameterJdbcTemplate.query(Queries.GET_USERDETIALS_FROM_USERNAME, parameters,userDetailsExtractor);
	        if(userDto!=null)
	        {
	    	userDto.setUsername(username);
	        }
	    	//log.info("================================Fired this Query to the Database :-" +Queries.GET_USERDETIALS_FROM_USERNAME);
		}
		catch(Exception e)
		{
		  
		}

	  return userDto ;
	}
	
   public String getRoleFromRoleId(Long roleid) 
   {
	   String role=null;
	   Map<String,Object> parameters=new HashMap<>();
	   parameters.put("roleid",roleid );
	   try
	   {
		   role=namedParameterJdbcTemplate.query(Queries.GET_ROLENAME_FROM_ROLEID,parameters,rolenamextractor);
	      // log.error("===========================FIRED query to db :- " + Queries.GET_ROLENAME_FROM_ROLEID);
	   }
	   catch(Exception exception)
	   {
	   
	   }
	   return role;
   }
   
   public boolean checkUserNamePasswordIndb(LoginDto loginDto) throws CustomException
	{
		boolean checker=false;
		Map<String,Object> parameters= new HashMap<>();
		parameters.put("username", loginDto.getUserName());
		parameters.put("password", loginDto.getPassword());
		
		try
		{
			checker=namedParameterJdbcTemplate.query(Queries.CHECK_USERNAME_PASSWORD,parameters,usernamepasswordextractor);
			
		}
		catch(Exception e)
		{
			throw new CustomException(MessageConstants.DATABASE_EXCEPTION_MESSAGE,HttpStatus.SC_INTERNAL_SERVER_ERROR );	
		}
		return checker;
	}
   
   
}
