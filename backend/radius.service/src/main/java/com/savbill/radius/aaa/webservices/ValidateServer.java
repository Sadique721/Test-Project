package com.savbill.radius.aaa.webservices;

import java.util.HashMap;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;

import com.savbill.radius.aaa.server.RadiusAAAServer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

 
@Path("validateServer")
public class ValidateServer {
	public static Logger log  = Logger.getLogger("radiuslog");

	public ValidateServer() {
		super();
	}

	@POST
	@Consumes("text/plain")
	@Produces(MediaType.APPLICATION_JSON)
	public String validateServer(@Context Request request, String parameter) {
		RadiusAAAServer server = new RadiusAAAServer();
		HashMap returnMap=new HashMap();
		
		 Gson gsonBuilder = new GsonBuilder().create();
		 returnMap.put("authPort", server.getAuthenticationService().getSocketPort());
		 returnMap.put("accttPort", server.getAccountingService().getSocketPort());
		 returnMap.put("status","1");
		 String jsonFromJavaMap = gsonBuilder.toJson(returnMap);
		 log.fine("jsonFromJavaMap:"+jsonFromJavaMap);
		 return jsonFromJavaMap;
	}
}
