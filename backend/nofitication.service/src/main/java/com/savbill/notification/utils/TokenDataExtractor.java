package com.savbill.notification.utils;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import com.savbill.notification.entity.LoggedInUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import com.savbill.notification.exceptions.AuthException;
import com.savbill.notification.exceptions.CustomException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;

@Component
public class TokenDataExtractor
{

     	
     	
	public static final String BEARER="Bearer ";
    @Value("${jwt.secret}")
    public String client_secret;
    
    @Autowired
    Dao dao;
    
    
    public long getUsermvnoId(HttpServletRequest request) throws AuthException, CustomException
    {
      long usermvnoid=0;
      
    	try
    	{
    	String username=getUsernameFromToken(request);	
    	String usernameFromDb=dao.checkUsernameInDatabase(username);
    	
    	
    	   if(usernameFromDb==null)
    	    {
    	 	   throw new AuthException(MessageConstants.NO_SUCH_USERNAME_PRESENT,HttpStatus.SC_UNAUTHORIZED);
    	    }
    	   
    	   usermvnoid=dao.getMvnoId(usernameFromDb);
    	}
        catch(CustomException customException)
    	{
        	
        	throw customException;
    	}
    	catch(AuthException exception)
    	{
    	  	
    	  throw exception; 
    	}
    
    	return usermvnoid;
    }
    
    
	
	public String getUsernameFromToken(HttpServletRequest request) throws AuthException
	{
		String username=null;
		
	    try
	    {
		String authorizationHeader=request.getHeader("Authorization");
		
		if(authorizationHeader==null)
		   {
			throw new AuthException(MessageConstants.TOKEN_NOT_PRESENT_MESSAGE, HttpStatus.SC_UNAUTHORIZED);
		   }
	    
		checkBearer(authorizationHeader); 
	    
		 username=extractUsername(authorizationHeader);
	    
	    }
	    catch(AuthException authexception)
	    {
	    	
	    	throw authexception;
	    }
	    
	    return username;
	}
	
	
	public void checkBearer(String header) throws AuthException
	{
		try
		{
			if(!header.startsWith(BEARER) )
			{
			   throw new AuthException(MessageConstants.BEARER_STRING_NOT_PRESENT_MESSAGE, HttpStatus.SC_UNAUTHORIZED);	
			}
			
		}
		catch(AuthException authException)
		{
			
			throw authException;
		}
	}
	
	
//	public String extractUsername(String header) throws AuthException
//	{
//		String token=header.substring(7);
//		String username=null;
//
//        try
//        {
////        	 JwtParser  key=Jwts.parser().setSigningKey(client_secret);
////    		 Jws<Claims> jwsclaims=key.parseClaimsJws(token);
////
////             Claims claimsobtained=jwsclaims.getBody();
//			String subject = Jwts.parser()
//					.setSigningKey(client_secret)
//					.parseClaimsJws(token.replace(NotificationConstants.AUTHORIZATION_TOKEN_PREFIX, ""))
//					.getBody()
//					.getSubject();
//     		 username= new ObjectMapper().readValue(subject, LoggedInUser.class).getUsername();
//
//        }
//        catch(ExpiredJwtException expired)
//        {
//
//        	throw new AuthException(MessageConstants.TOKEN_EXPIRY_MESSAGE, HttpStatus.SC_UNAUTHORIZED);
//        }
//        catch(Exception exception)
//        {
//
//            throw new AuthException(MessageConstants.INVALID_TOKEN_MESSAGE, HttpStatus.SC_UNAUTHORIZED);
//        }
//
//        return username;
//	}
private String extractUsername (String header) {

	String token = header.substring(7);
//		String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ7XCJmaXJzdE5hbWVcIjpcImFkbWluXCIsXCJsYXN0TmFtZVwiOlwiYWRtaW5cIixcInVzZXJJZFwiOjIsXCJwYXJ0bmVySWRcIjoxLFwicm9sZXNMaXN0XCI6XCIxXCIsXCJzZXJ2aWNlQXJlYUlkXCI6bnVsbCxcIm12bm9JZFwiOjIsXCJzZXJ2aWNlQXJlYUlkTGlzdFwiOlsxLDIsNCw1LDYsNyw4LDksMTAsMTMsMTQsMTUsMTYsMTcsMTgsMTksMjIsMjMsMjQsMjUsMjcsMjgsMzAsMzEsMzIsMzMsNDcsNDgsNDksNTAsNTEsNTIsNTMsNTQsNTUsNTYsNTcsNTgsNTksNjAsNjEsNjIsNjUsNjYsNjcsNjgsNjksNzYsNzcsNzgsNzksODEsODIsODMsODQsOTBdLFwic3RhZmZJZFwiOjIsXCJidUlkc1wiOltdLFwibGNvXCI6ZmFsc2V9IiwiZXhwIjoxNjc2MDUyNzYzfQ.UdeQCUG6wQPA7tsdJDWie0GpcTgC6H5Mh1KIwiRdeB8";
	Key hmacKey = new SecretKeySpec(java.util.Base64.getDecoder().decode(NotificationConstants.SECRET),
			SignatureAlgorithm.HS256.getJcaName());


	if (token != null) {
		String subject = Jwts.parser()
				.setSigningKey(hmacKey)
				.parseClaimsJws(token.replace(NotificationConstants.AUTHORIZATION_TOKEN_PREFIX, ""))
				.getBody()
				.getSubject();

		if (subject != null) {
			LoggedInUser user = null;
			try {
				user = new ObjectMapper().readValue(subject, LoggedInUser.class);
			} catch (Exception e) {
				//ApplicationLogger.logger.error(e.getMessage(), e);
				System.out.println(e.getMessage());
			}
			return user.getUsername();
		}

		return null;
	}

	return null;
}

	public String getDecoded(String encodedToken) throws UnsupportedEncodingException {
		String[] pieces = encodedToken.split("\\.");
		String b64payload = pieces[1];
		String jsonString = new String(Base64.decodeBase64(b64payload), "UTF-8");
		return jsonString;
	}
	public Long getMvnoId(String encodedToken) throws IOException {
		String decodedToken = getDecoded(encodedToken);
		Long mavnoId = null;
		if (decodedToken != null) {
			JSONObject primaryObject = new JSONObject(decodedToken);
			JSONObject mainObj = new JSONObject(primaryObject.get("sub").toString());
			mavnoId = mainObj.getLong("mvnoId");
		}
		return mavnoId;
	}


	public String getUserName(String encodedToken) throws IOException {
		String decodedToken = getDecoded(encodedToken);
		String userName = null;
		if (decodedToken != null) {
			JSONObject primaryObject = new JSONObject(decodedToken);
			JSONObject mainObj = new JSONObject(primaryObject.get("sub").toString());
			userName = mainObj.getString("username");
		}
		return userName;
	}
	public List<Long> getBUId(String encodedToken) throws UnsupportedEncodingException {
		String decodedToken = getDecoded(encodedToken);
		List<Long> buId = new ArrayList<>();
		if (decodedToken != null) {
			JSONObject primaryObject = new JSONObject(decodedToken);
			JSONObject mainObj = new JSONObject(primaryObject.get("sub").toString());
			JSONArray buIds = mainObj.getJSONArray("buIds");
			if(buIds != null && buIds.length() > 0) {
				for(int i=0;i<buIds.length();i++ ){
					buId.add(buIds.getLong(i));
				}

			}
		}
		return buId;
	}
}
