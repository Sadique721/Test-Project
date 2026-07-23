package com.savbill.notification.utils;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.savbill.notification.helper.UserDto;

@Component
public class UserDetailsExtractor implements ResultSetExtractor<UserDto>
{

	  	
	
	@Override
	public UserDto extractData(ResultSet rs) throws SQLException, DataAccessException 
	{
	   UserDto userdto=new UserDto();
		
		try
		{
		 while(rs.next())
	     {
			 userdto.setRoleid(rs.getLong(1));
			 userdto.setPassword(rs.getString(2));
	     }
		}
		catch(SQLException sqlexception)
		{
	      
		  throw sqlexception;
			
		}
		catch(DataAccessException dataAccessException)
		{
			
			throw dataAccessException;
		}
		
		return userdto;
	}

}
