package com.savbill.notification.utils;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class DataExtractorForUsernameExists implements ResultSetExtractor<String>
{
	
	
	@Override
	public String extractData(ResultSet rs) throws SQLException, DataAccessException 
	{
	    String username=null;
	    
	    try
	    {
		   while(rs.next())
		    {
		       username=rs.getString(1);
		      
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
	
		return username;
	}

}
