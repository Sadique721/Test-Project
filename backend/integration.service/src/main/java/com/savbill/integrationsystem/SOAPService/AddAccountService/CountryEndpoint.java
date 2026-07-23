//package com.savbill.integrationsystem.SOAPService.controller;
//
//
//import com.savbill.integrationsystem.generated.GetCountryRequest;
//import com.savbill.integrationsystem.generated.GetCountryResponse;
//import org.springframework.ws.server.endpoint.annotation.Endpoint;
//import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
//import org.springframework.ws.server.endpoint.annotation.RequestPayload;
//import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
//
//@Endpoint
//public class CountryEndpoint {
//	private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";
//
//
//	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getCountryRequest")
//	@ResponsePayload
//	public GetCountryResponse getCountry(@RequestPayload GetCountryRequest request) {
//		// Print "Hello World" to the console
//		GetCountryResponse response = new GetCountryResponse();
//		String name = request.getName();
//		response.setGreeting("Hello, " + name + "! Welcome to our SOAP service.");
//
//
//		return response;
//	}
//}
