package com.savbill.notification.utils;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class UserNamePasswordExtractor implements ResultSetExtractor<Boolean>
{

	@Override
	public Boolean extractData(ResultSet rs) throws SQLException, DataAccessException {
		
		Boolean checker=false;
		while(rs.next())
		{
			checker=true;
			
		}
		return checker;
	}

	
}
