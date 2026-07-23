package com.savbill.notification.utils;

public class Queries
{

	public static final String FETCH_USERNAME_FROM_DATABASE="SELECT username FROM tblmstaff WHERE username=:username";
	public static final String GET_MVNOID_FROM_USERNAME="SELECT mvnoid FROM tblmstaff WHERE username=:username";
	
	public static final String GET_USERDETIALS_FROM_USERNAME="SELECT roleid,password FROM tblmstaff WHERE username=:username";
	public static final String GET_ROLENAME_FROM_ROLEID="SELECT name FROM tblmrole WHERE roleid=:roleid";
	
	public static final String CHECK_USERNAME_PASSWORD="SELECT username FROM tblmstaff WHERE username=:username and password=:password";
	private Queries()
	{
		
	}
    public static String fetchMvnoIdQuery(String tablename,String columnName)
    {
      
    	 String fetchMvnoidQuery="SELECT mvnoid FROM " + tablename + " WHERE " + columnName +    "=:columnValue";
    	 return fetchMvnoidQuery;
    }
    
}

