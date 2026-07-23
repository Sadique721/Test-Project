package com.savbill.notification.utils;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class RoleNameExtractor implements ResultSetExtractor<String>
{	
	
	
	
	@Override
	public String extractData(ResultSet rs) throws SQLException, DataAccessException 
	{
	
      String role=null;	
     try
     {
      while(rs.next())
      {
    	  role =rs.getString(1);
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
	
	return role;

   }
}