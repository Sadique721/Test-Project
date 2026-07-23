package com.savbill.integrationsystem.core.security.spring;//package com.savbill.ticketmanagement.core.security.spring;//package com.savbill.radius.spring;
//
//import java.net.InetAddress;
//import java.net.UnknownHostException;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.javers.spring.auditable.CommitPropertiesProvider;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//@Configuration
//public class JaversConfiguration{
//
//	private static Log logger = LogFactory.getLog(JaversConfiguration.class);
//	
//	public LoggedInUser getLoggedInUser() {
//	    	LoggedInUser loggedInUser=null;
//	    	try {
//		    	SecurityContext securityContext = SecurityContextHolder.getContext();
//		    	if(null != securityContext.getAuthentication()) {
//		    		loggedInUser=((LoggedInUser)securityContext.getAuthentication().getPrincipal());
//		    	}
//	    	}catch(Exception e) {
//	    		logger.error(e.getMessage());
//	    	}
//	    	return loggedInUser; 
//	    }
//	    
//	      
//	    @Bean
//	    public CommitPropertiesProvider commitPropertiesProvider() {
//	        return new CommitPropertiesProvider() {
//	            @Override
//	            public Map<String, String> provide() {
//	                Map<String, String> props = new HashMap<>();
//	                if(getLoggedInUser() != null) {
//	                	props.put("staffid",String.valueOf(getLoggedInUser().getUserId())); 
//		                props.put("username",getLoggedInUser().getUsername()); 
//		                try {
//							props.put("ipaddress",InetAddress.getLocalHost().getHostAddress());
//						} catch (UnknownHostException e) {
//							logger.error("Fail to get Ipaddress of host", e);
//						}
//	                }
//	                return props;   
//	            }               
//	        };  
//	    }
//}
