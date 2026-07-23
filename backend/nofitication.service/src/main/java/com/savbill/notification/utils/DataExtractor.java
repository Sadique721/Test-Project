package com.savbill.notification.utils;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class DataExtractor implements ResultSetExtractor<Long> 
{
	

	@Override
	public Long extractData(ResultSet rs) throws SQLException, DataAccessException
	{
		Long mvnoid=null;
		
		try
		{
		  while(rs.next())
		      {
		          mvnoid= rs.getLong(1);	
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
		return mvnoid;
	}
	
}
