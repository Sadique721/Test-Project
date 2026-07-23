//package com.savbill.integrationsystem.SOAPService.controller;
//
//import com.savbill.integrationsystem.generated.AddAccountRequest;
//import com.savbill.integrationsystem.generated.AddAccountResponse;
//import org.springframework.ws.server.endpoint.annotation.Endpoint;
//import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
//import org.springframework.ws.server.endpoint.annotation.RequestPayload;
//import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
//
//@Endpoint
//public class AddAccountServiceEndPoint {
//    private static final String NAMESPACE_URI = "http://savbill.act.com";
//
//
//    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "addAccountRequest")
//    @ResponsePayload
//    public AddAccountResponse getCountry(@RequestPayload AddAccountRequest request) {
//        AddAccountResponse response = new AddAccountResponse();
//        String requestId = request.getRequestId();
//        response.setRequestId(requestId);
//        response.setResponseMessage("SUCCESS");
//        response.setResponeCode("200");
//        return response;
//    }
//}
