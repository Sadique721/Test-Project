package com.savbill.integrationsystem.SOAPService.changeService;

import com.savbill.integrationsystem.RestApiService.chargeService.ChangeServiceRequest;
import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.SOAPService.service.ChangeServService;
import com.savbill.integrationsystem.core.exceptions.CustomValidationException;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.generated.changeservice.ChangeService;
import com.savbill.integrationsystem.generated.changeservice.ChangeServiceResponse;
import com.savbill.integrationsystem.generated.changeservice.WsChangeService;
import com.savbill.integrationsystem.generated.changeservice.WsChangeServiceResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.NodeList;

import javax.persistence.NoResultException;
import javax.xml.soap.*;
import javax.xml.transform.dom.DOMSource;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Endpoint
public class ChangeServiceEndpoint {

    @Autowired
    private ChangeServService changeServService;
    @Autowired
    private CmsClientService cmsClientService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    public RadiusClientService radiusClient;


    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI, localPart = "wsChangeService")
    @ResponsePayload
    public com.savbill.integrationsystem.generated.newchangeservice.WsChangeServiceResponse getWsChangeService(@RequestPayload WsChangeService request, MessageContext messageContext) throws SOAPException, IOException {
        com.savbill.integrationsystem.generated.newchangeservice.WsChangeServiceResponse response = null;
        long startTime = System.currentTimeMillis();
        try {
            response = getWsChange1(request);
            log.info("Method getWsChange completed in {}ms", System.currentTimeMillis() - startTime);
            return response;
//            return generateChangeServiceSOAP11SuccessResponse(response, messageContext);
        } catch (NoResultException e) {
            String exceptionMessage = e.getMessage() != null ? e.getMessage() : "Exception was encountered during processing Request.";
            /**
             * Different response format for this case No Records Updated Via Product API[updateSubscriber] for given UserName
             */
//            return generateChangeServiceSOAP11NotUpdatedResponse(response, messageContext);
        } catch (Exception e) {
//            return generateChangeServiceSOAP11SuccessResponse(response, messageContext);
        }
        log.info("Method getWsChange completed in {}ms", System.currentTimeMillis() - startTime);
        return response;
    }

    public WsChangeServiceResponse getWsChange(WsChangeService request) {
        long startTime = System.currentTimeMillis();
        log.info("Starting method getWsChange for username: {}, serviceId: {},At: {}", request.getUserName(), request.getServiceId(), new Date(startTime));

        WsChangeServiceResponse wsDiffAAAPlanCodeResponse = new WsChangeServiceResponse();
        String requestId = (request.getRequestId() == null || request.getRequestId().trim().isEmpty()) ? "?" : request.getRequestId().trim();
        String userName = request.getUserName().trim();
        request.setUserName(userName);
        String serviceid = request.getServiceId().trim();
        Integer responseCode = HttpStatus.EXPECTATION_FAILED.value();
        String responseMessage = SoapConstants.FAILURE;
        String token = jwtUtil.generateJwtToken(SoapConstants.MVNOID);
        wsDiffAAAPlanCodeResponse.setRequestId(requestId);

        if (isNullOrEmpty(userName)) {
            log.warn("Username validation failed: Empty or null username");
            log.info("Method getWsChange completed in {}ms", System.currentTimeMillis() - startTime);
            return createErrorResponse(SoapConstants.EMPTY, "Input user name is Empty or Null.", requestId);
        }

        if (isNullOrEmpty(serviceid)) {
            log.warn("ServiceId validation failed: Empty or null serviceId for username: {}", userName);
            log.info("Method getWsChange completed in {}ms", System.currentTimeMillis() - startTime);
            return createErrorResponse(SoapConstants.EMPTY, "Input Overrides and ServiceId both are Empty or null.", requestId);
        }

        try {
            userName = userName.toLowerCase().trim();
            //TODO: Add check for plan available in CMS
            ChangeServiceRequest changeServiseRequest = new ChangeServiceRequest(request);
            log.debug("Calling CMS client service for username: {}, serviceId: {}", userName, serviceid);
            ResponseEntity<?> responseEntity = cmsClientService.changeService(changeServiseRequest, SoapConstants.MVNOID, token);
            log.debug("Integration Received Response In:{} ms,Response :{}", System.currentTimeMillis() - startTime, responseEntity.getBody());
//            GenericDataDTO genericDataDTO =radiusClient.GetBalanceApi(userName, SoapConstants.MVNOID);
//            Boolean usageExists = genericDataDTO.getResponseMessage().equalsIgnoreCase("SUCCESS");
//            Boolean checkCustomerEntryInCustTBL = !genericDataDTO.getResponseMessage().equalsIgnoreCase("Customer not found");

//            if (checkCustomerEntryInCustTBL) {
            if (((Map<String, Object>) responseEntity.getBody()).get("responseCode") != null) {
                if ((Integer) ((Map<String, Object>) responseEntity.getBody()).get("responseCode") == 204) {
                    if (((Map<String, Object>) responseEntity.getBody()).get("msg").toString().equalsIgnoreCase("QuotaDtls Not found")) {
                        log.warn("Quota details not found for username: {}", userName);
                        responseCode = SoapConstants.USER_DETAILS_NOT_FAOUND_IN_USAGE_TABLE_CODE;
                        responseMessage = "User Details not found in Usages table for Qouta Update for Given Username .";
                        wsDiffAAAPlanCodeResponse.setResponeCode(responseCode);
                        wsDiffAAAPlanCodeResponse.setResponseMessage(responseMessage);
                        wsDiffAAAPlanCodeResponse.setRequestId(requestId);
                        return wsDiffAAAPlanCodeResponse;
                    } else {
                        log.warn("No records updated for username: {}", userName);
                        responseCode = SoapConstants.NO_RECOED_UPDATE_CODE;
                        responseMessage = "No Records Updated Via Product API[updateSubscriber] for given UserName";
                    }
                }
            } else {
                try {
                    Double override = Math.abs(request.getOverrides() != null ? request.getOverrides() : 0);
                    request.setOverrides(override);
                    Boolean changeServiceValidator = changeServService.changeServiceValidator(responseEntity);
                    log.debug("Integration Received Response From changeServiceValidator In:{} ms,Response :{}", System.currentTimeMillis() - startTime, changeServiceValidator);
                    if (changeServiceValidator) {
                        log.info("Service change successful for username: {}, serviceId: {}", userName, serviceid);
                        responseCode = SoapConstants.SUCCESS_CODE;
                        responseMessage = "SUCCESS";
                    } else {
                        log.warn("Service change validation failed for username: {}", userName);
                        responseCode = SoapConstants.USER_DETAILS_NOT_FAOUND_IN_USAGE_TABLE_CODE;
                        responseMessage = "No records update via product API [updateSubscriber] for given username.";
                    }
                } catch (FeignException e) {
                    log.debug("FeignException occurred for username: {}, Error: {}", userName, e.getMessage());
                    ObjectMapper objectMapper = new ObjectMapper();
                    String message = "";
                    int status = 404;
                    try {
                        String errorMessage = e.contentUTF8();
                        JsonNode jsonNode = objectMapper.readTree(errorMessage);
                        message = jsonNode.get("msg").asText();
                        status = jsonNode.get("status").asInt();
                        if (Objects.nonNull(message)) {
                            if (message.equalsIgnoreCase("Please enter a valid service")) {
                                wsDiffAAAPlanCodeResponse.setResponeCode(SoapConstants.NOT_FOUND);
                                wsDiffAAAPlanCodeResponse.setResponseMessage("Input Service ID is not found in Policy Group Table.");
                                wsDiffAAAPlanCodeResponse.setRequestId(requestId);
                                log.warn("Please enter a valid service Id ");
                            } else {
                                wsDiffAAAPlanCodeResponse.setResponeCode(SoapConstants.NOT_FOUND);
                                wsDiffAAAPlanCodeResponse.setResponseMessage(message);
                                wsDiffAAAPlanCodeResponse.setRequestId(requestId);
                            }
                            return wsDiffAAAPlanCodeResponse;
                        }
                    } catch (JsonProcessingException je) {
                        log.error("JSON processing error for username: {}, Error: {}", userName, je.getMessage());
                        je.printStackTrace();
                        throw new RuntimeException("Error processing JSON response", je);
                    }
                    e.printStackTrace();
                    wsDiffAAAPlanCodeResponse.setResponeCode(status);
                    wsDiffAAAPlanCodeResponse.setResponseMessage(message);
                    wsDiffAAAPlanCodeResponse.setRequestId(requestId);
                    return wsDiffAAAPlanCodeResponse;
                } catch (Exception ex) {
                    log.error("Error for username: {}, Error: {}", userName, ex.getMessage(), ex);
                    responseCode = SoapConstants.NO_RECOED_UPDATE_CODE;
                    responseMessage = "No Records Updated Via Product API[updateSubscriber] for given UserName";
                }

            }
/*  } else {
                responseCode = SoapConstants.NO_RECOED_UPDATE_CODE;
                responseMessage = "No Records Updated Via Product API[updateSubscriber] for given UserName";
            }*/

            wsDiffAAAPlanCodeResponse.setResponeCode(responseCode);
            wsDiffAAAPlanCodeResponse.setResponseMessage(responseMessage);
            wsDiffAAAPlanCodeResponse.setRequestId(requestId);
        } catch (CustomValidationException e) {
            log.error("CustomValidationException for username: {}, Error: {}", userName, e.getMessage());
            wsDiffAAAPlanCodeResponse.setResponeCode(e.getErrCode());
            wsDiffAAAPlanCodeResponse.setResponseMessage(e.getMessage());
            wsDiffAAAPlanCodeResponse.setRequestId(requestId);
        } catch (Exception e) {
            log.error("Unexpected error for username: {}, Error: {}", userName, e.getMessage(), e);
            wsDiffAAAPlanCodeResponse.setResponeCode(responseCode);
            wsDiffAAAPlanCodeResponse.setResponseMessage(e.getMessage());
            wsDiffAAAPlanCodeResponse.setRequestId(requestId);
        }
        log.info("Method getWsChange completed in {} ms for username: {}", System.currentTimeMillis() - startTime, userName);
        return wsDiffAAAPlanCodeResponse;
    }

    public com.savbill.integrationsystem.generated.newchangeservice.WsChangeServiceResponse getWsChange1(WsChangeService request) {
        com.savbill.integrationsystem.generated.newchangeservice.WsChangeServiceResponse resp = new com.savbill.integrationsystem.generated.newchangeservice.WsChangeServiceResponse();
        com.savbill.integrationsystem.generated.newchangeservice.ChangeServiceResponse changeService = new com.savbill.integrationsystem.generated.newchangeservice.ChangeServiceResponse();
        long startTime = System.currentTimeMillis();
        log.info("Starting method getWsChange for username: {}, serviceId: {},At: {}", request.getUserName(), request.getServiceId(), new Date(startTime));

        String requestId = (request.getRequestId() == null || request.getRequestId().trim().isEmpty()) ? "?" : request.getRequestId().trim();
        String userName = request.getUserName().trim();
        request.setUserName(userName);
        String serviceid = request.getServiceId().trim();
        Integer responseCode = HttpStatus.EXPECTATION_FAILED.value();
        String responseMessage = SoapConstants.FAILURE;
        String token = jwtUtil.generateJwtToken(SoapConstants.MVNOID);
        changeService.setRequestId(requestId);
        if (isNullOrEmpty(userName)) {
            com.savbill.integrationsystem.generated.newchangeservice.ChangeServiceResponse errorResponse1 = createErrorResponse1(SoapConstants.EMPTY, "Input user name is Empty or Null.", requestId);
            resp.setChangeService(errorResponse1);
            log.warn("Username validation failed: Empty or null username");
            return resp;
        }

        if (isNullOrEmpty(serviceid)) {
            com.savbill.integrationsystem.generated.newchangeservice.ChangeServiceResponse errorResponse1 = createErrorResponse1(SoapConstants.EMPTY, "Input Overrides and ServiceId both are Empty or null.", requestId);
            resp.setChangeService(errorResponse1);
            log.warn("ServiceId validation failed: Empty or null serviceId for username: {}", userName);
            return resp;
        }
        try {
            userName = userName.toLowerCase().trim();
            //TODO: Add check for plan available in CMS
            log.debug("Calling Radius client service for username: {}, serviceId: {}", userName, serviceid);
            Boolean checkCustomerEntryInCustTBL = changeServService.checkCustomerEntryInCustTBL(userName);
            log.debug("Integration Received Response In:{} ms,Response :{}", System.currentTimeMillis() - startTime, checkCustomerEntryInCustTBL);

            if (checkCustomerEntryInCustTBL) {
                log.debug("Calling Radius client service To check usageExists: {}, serviceId: {}", userName, serviceid);
                Boolean usageExists = changeServService.checkCustEntryInUsageQuota(userName);
                log.debug("Integration Received Response In:{} ms,Response :{}", System.currentTimeMillis() - startTime, usageExists);

                if (!usageExists) {
                    log.warn("No usage exists in Usage Table for input User:{}", userName);
                    responseCode = SoapConstants.USER_DETAILS_NOT_FAOUND_IN_USAGE_TABLE_CODE;
                    responseMessage = "User Details not found in Usages table for Qouta Update for Given Username .";
                    changeService.setResponeCode(responseCode);
                    changeService.setResponseMessage(responseMessage);
                    changeService.setRequestId(requestId);
                    resp.setChangeService(changeService);
                    return resp;
                } else {
                    try {
                        Double override = Math.abs(request.getOverrides() != null ? request.getOverrides() : 0);
                        request.setOverrides(override);
                        ChangeServiceRequest changeServiseRequest = new ChangeServiceRequest(request);
                        log.debug("Calling CMS client for change service for username: {}, serviceId: {}", userName, serviceid);
                        ResponseEntity<?> responseEntity = cmsClientService.changeService(changeServiseRequest, SoapConstants.MVNOID, token);
                        log.debug("Integration Received Response In:{} ms,Response :{}", System.currentTimeMillis() - startTime, responseEntity.getBody());
                        Boolean changeServiceValidator = changeServService.changeServiceValidator(responseEntity);
                        log.debug("Integration Received Response From changeServiceValidator In:{} ms,Response :{}", System.currentTimeMillis() - startTime, changeServiceValidator);
                        if (changeServiceValidator) {
                            log.info("Service change successful for username: {}, serviceId: {}", userName, serviceid);
                            responseCode = SoapConstants.SUCCESS_CODE;
                            responseMessage = "SUCCESS";
                        } else {
                            log.warn("Service change validation failed for username: {}", userName);
                            responseCode = SoapConstants.USER_DETAILS_NOT_FAOUND_IN_USAGE_TABLE_CODE;
                            responseMessage = "No records update via product API [updateSubscriber] for given username.";
                        }
                    } catch (FeignException e) {
                        log.debug("FeignException occurred for username: {}, Error: {}", userName, e.getMessage());
                        ObjectMapper objectMapper = new ObjectMapper();
                        String message = "";
                        int status = 404;
                        try {
                            String errorMessage = e.contentUTF8();
                            JsonNode jsonNode = objectMapper.readTree(errorMessage);
                            message = jsonNode.get("msg").asText();
                            status = jsonNode.get("status").asInt();
                            if (Objects.nonNull(message)) {
                                if (message.equalsIgnoreCase("Please enter a valid service")) {
                                    changeService.setResponeCode(SoapConstants.NOT_FOUND);
                                    changeService.setResponseMessage("Input Service ID is not found in Policy Group Table.");
                                    changeService.setRequestId(requestId);
                                    log.warn("Input Service ID:{} is not found in Policy Group Table", serviceid);
                                } else {
                                    changeService.setResponeCode(SoapConstants.NOT_FOUND);
                                    changeService.setResponseMessage(message);
                                    changeService.setRequestId(requestId);
                                    log.warn("NOT_FOUND");
                                }
                                resp.setChangeService(changeService);
                                return resp;
                            }
                        } catch (JsonProcessingException je) {
                            // Handle specific JSON processing exceptions
                            log.error("JSON processing error for username: {}, Error: {}", userName, je.getMessage());
                            je.printStackTrace();
                            changeService.setResponeCode(status);
                            changeService.setResponseMessage(je.getMessage());
                            changeService.setRequestId(requestId);
                            resp.setChangeService(changeService);
                            return resp;
//                            throw new RuntimeException("Error processing JSON response", je);
                        }
                        e.printStackTrace();
                        changeService.setResponeCode(status);
                        changeService.setResponseMessage(message);
                        changeService.setRequestId(requestId);
                        resp.setChangeService(changeService);
                        return resp;

                    } catch (Exception ex) {
                        log.error("Error for username: {}, Error: {}", userName, ex.getMessage(), ex);
                        responseCode = SoapConstants.NO_RECOED_UPDATE_CODE;
                        responseMessage = "No Records Updated Via Product API[updateSubscriber] for given UserName";
                    }

                }
            } else {
                log.warn("No customer entry found in CUST_TBL for input User:{}", userName);
                responseCode = SoapConstants.NO_RECOED_UPDATE_CODE;
                responseMessage = "No Records Updated Via Product API[updateSubscriber] for given UserName";
            }

            changeService.setResponeCode(responseCode);
            changeService.setResponseMessage(responseMessage);
            changeService.setRequestId(requestId);
        } catch (CustomValidationException e) {
            changeService.setResponeCode(e.getErrCode());
            changeService.setResponseMessage(e.getMessage());
            changeService.setRequestId(requestId);
            log.error("CustomValidationException for username: {}, Error: {}", userName, e.getMessage());

        } catch (Exception e) {
            changeService.setResponeCode(responseCode);
            changeService.setResponseMessage(e.getMessage());
            changeService.setRequestId(requestId);
            log.error("Exception for username: {}, Error: {}", userName, e.getMessage());

        }
        resp.setChangeService(changeService);
        return resp;
    }

    /*
    public DOMSource generategetWsChangeSummerySOAPResponse(WsChangeServiceResponse response) throws SOAPException, ParserConfigurationException {
        // Create a SOAP Message factory and message
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        // Add namespace declarations
        envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.addNamespaceDeclaration("ns2", "http://api.act.com/");
        envelope.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");

        SOAPBody body = envelope.getBody();

        // Create the response element
        SOAPElement responseElement = body.addChildElement("wsChangeServiceResponse", "ns2");
        responseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");

        // Add GetUserUsageSummary element
        SOAPElement addService = responseElement.addChildElement("ChangeService");
        addService.addChildElement("requestId").addTextNode(getSafeText(response.getRequestId()));
        addService.addChildElement("responeCode").addTextNode(getSafeNumber(response.getResponeCode()));
        addService.addChildElement("responseMessage").addTextNode(getSafeText(response.getResponseMessage()));
        // Save changes to the SOAP message
        soapMessage.saveChanges();

        // Convert SOAP message to DOMSource
        Document document = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();

        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }

        // Return the DOMSource for the constructed XML
        return new DOMSource(fragment);
    }
     */

    private WsChangeServiceResponse createErrorResponse(Integer responseCode, String responseMessage, String requestId) {
        WsChangeServiceResponse response = new WsChangeServiceResponse();
        response.setResponeCode(responseCode);
        response.setResponseMessage(responseMessage);
        response.setRequestId(requestId);
        return response;
    }

    private com.savbill.integrationsystem.generated.newchangeservice.ChangeServiceResponse createErrorResponse1(Integer responseCode, String responseMessage, String requestId) {
        com.savbill.integrationsystem.generated.newchangeservice.ChangeServiceResponse changeService = new com.savbill.integrationsystem.generated.newchangeservice.ChangeServiceResponse();
        changeService.setResponeCode(responseCode);
        changeService.setResponseMessage(responseMessage);
        changeService.setRequestId(requestId);
        return changeService;
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    // Use this both success and exception response generator method without interceptor
    // First test for checking sheet response and adjust parameter value by taking from response class

    /**
     * Generates a SOAP 1.1 response indicating the successful processing of a "ChangeService" request.
     * This response is returned when the service change operation has been successfully completed.
     *
     * @param response       The response object containing any necessary data to be included in the SOAP response.
     * @param messageContext The context of the SOAP message, used for setting the response in the framework.
     * @return A DOMSource representing the SOAP message to be sent as the response.
     * @throws SOAPException If an error occurs while creating or manipulating the SOAP message.
     *                       Response Details:
     *                       - A `200` response code indicating success.
     *                       - A custom message "SUCCESS" confirming the successful execution of the request.
     *                       - The `requestId` is set to "1000" (a custom identifier for this request).
     */
    public DOMSource generateChangeServiceSOAP11SuccessResponse(WsChangeServiceResponse response, MessageContext messageContext) throws SOAPException {
        // Create a SOAP Message factory for SOAP 1.1 protocol
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        // Remove the default namespace and add custom namespaces
        SOAPBody body;
        envelope.removeNamespaceDeclaration("SOAP-ENV");
        if (String.valueOf(response.getResponeCode()).equalsIgnoreCase("502")) {
            envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
            envelope.setPrefix("soapenv");
            body = envelope.getBody();
            body.setPrefix("soapenv");
        } else {
            envelope.addNamespaceDeclaration("soap", "http://schemas.xmlsoap.org/soap/envelope/");
            envelope.setPrefix("soap");
            body = envelope.getBody();
            body.setPrefix("soap");
        }

        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode();
        }

        // Add the main response element with custom namespaces
        SOAPElement responseElement = body.addChildElement("wsChangeServiceResponse", "ns2", "http://api.act.com/");
        if (!String.valueOf(response.getResponeCode()).equalsIgnoreCase("502")) {
            responseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");
        }

        // Add the ChangeService element
        SOAPElement changeService = responseElement.addChildElement("ChangeService");

        // Add the required response fields
        changeService.addChildElement("requestId").addTextNode(response.getRequestId() != null ? response.getRequestId() : "?");  // Custom requestId
        changeService.addChildElement("responeCode").addTextNode(String.valueOf(response.getResponeCode()));  // Custom response code (this can be dynamic)
        changeService.addChildElement("responseMessage").addTextNode(response.getResponseMessage());  // Custom response message

        // Set the response in the SaajSoapMessage object
        SaajSoapMessage updateResponse = (SaajSoapMessage) messageContext.getResponse();
        updateResponse.setSaajMessage(soapMessage);
        updateResponse.getSaajMessage().saveChanges();

        // Convert SOAP message to DOMSource for further processing
        Document document = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();

        // Append all child nodes of the body to the fragment
        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }

        // Return the DOMSource for further processing
        return new DOMSource(fragment);
    }

    /**
     * Generates a SOAP 1.1 response indicating that no records were updated during a "ChangeService" request.
     * This response is returned when the product API (updateSubscriber) fails to find records for the provided username.
     *
     * @param response       The response object containing the necessary data for constructing the SOAP message.
     * @param messageContext The context of the SOAP message, typically provided by the framework, used for setting the response.
     * @return A DOMSource representing the SOAP message to be sent as the response.
     * @throws SOAPException If an error occurs while creating or manipulating the SOAP message.
     *                       Response Details:
     *                       - A `502` response code indicating failure due to "No Records Updated."
     *                       - A custom message "No Records Updated Via Product API[updateSubscriber] for given UserName" explaining the failure reason.
     *                       - The `requestId` is set to "1003" (a custom identifier for this specific request).
     */
    public DOMSource generateChangeServiceSOAP11NotUpdatedResponse(WsChangeServiceResponse response, MessageContext messageContext) throws SOAPException {
        // Create a SOAP Message factory for SOAP 1.1 protocol
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        // Remove the default namespace and add custom namespaces
        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.setPrefix("soapenv");

        SOAPBody body = envelope.getBody();
        body.setPrefix("soapenv");
        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode();
        }

        // Add the main response element with custom namespaces
        SOAPElement responseElement = body.addChildElement("wsChangeServiceResponse", "ns2", "http://api.act.com/");

        // Add the ChangeService element
        SOAPElement changeService = responseElement.addChildElement("ChangeService");

        // Add the required response fields
        changeService.addChildElement("requestId").addTextNode(response.getRequestId());  // Custom requestId
        changeService.addChildElement("responeCode").addTextNode(String.valueOf(response.getResponeCode()));  // Custom response code
        changeService.addChildElement("responseMessage").addTextNode(response.getResponseMessage());  // Custom response message

        // Set the response in the SaajSoapMessage object
        SaajSoapMessage updateResponse = (SaajSoapMessage) messageContext.getResponse();
        updateResponse.setSaajMessage(soapMessage);
        updateResponse.getSaajMessage().saveChanges();

        // Convert SOAP message to DOMSource for further processing
        Document document = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();

        // Append all child nodes of the body to the fragment
        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }

        // Return the DOMSource for further processing
        return new DOMSource(fragment);
    }

    public DOMSource generateChangeServiceSOAP11NotUpdatedResponse1(ChangeServiceResponse response, MessageContext messageContext) throws SOAPException {
        // Create a SOAP Message factory for SOAP 1.1 protocol
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        // Remove the default namespace and add custom namespaces
        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.setPrefix("soapenv");

        SOAPBody body = envelope.getBody();
        body.setPrefix("soapenv");
        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode();
        }

        // Add the main response element with custom namespaces
        SOAPElement responseElement = body.addChildElement("ChangeServiceResponse", "ns2", "http://api.act.com/");

        // Add the ChangeService element
        SOAPElement changeService = responseElement.addChildElement("ChangeService");

        // Add the required response fields
        changeService.addChildElement("requestId").addTextNode(response.getRequestId());  // Custom requestId
        changeService.addChildElement("responeCode").addTextNode(String.valueOf(response.getResponeCode()));  // Custom response code
        changeService.addChildElement("responseMessage").addTextNode(response.getResponseMessage());  // Custom response message

        // Set the response in the SaajSoapMessage object
        SaajSoapMessage updateResponse = (SaajSoapMessage) messageContext.getResponse();
        updateResponse.setSaajMessage(soapMessage);
        updateResponse.getSaajMessage().saveChanges();

        // Convert SOAP message to DOMSource for further processing
        Document document = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();

        // Append all child nodes of the body to the fragment
        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }

        // Return the DOMSource for further processing
        return new DOMSource(fragment);
    }

    public DOMSource generateChangeServiceSOAP11SuccessResponse1(ChangeServiceResponse response, MessageContext messageContext) throws SOAPException {
        // Create a SOAP Message factory for SOAP 1.1 protocol
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        // Remove the default namespace and add custom namespaces
        SOAPBody body;
        envelope.removeNamespaceDeclaration("SOAP-ENV");
        if (String.valueOf(response.getResponeCode()).equalsIgnoreCase("502")) {
            envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
            envelope.setPrefix("soapenv");
            body = envelope.getBody();
            body.setPrefix("soapenv");
        } else {
            envelope.addNamespaceDeclaration("soap", "http://schemas.xmlsoap.org/soap/envelope/");
            envelope.setPrefix("soap");
            body = envelope.getBody();
            body.setPrefix("soap");
        }

        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode();
        }

        // Add the main response element with custom namespaces
        SOAPElement responseElement = body.addChildElement("ChangeServiceResponse", "ns2", "http://api.act.com/");
        if (!String.valueOf(response.getResponeCode()).equalsIgnoreCase("502")) {
            responseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");
        }

        // Add the ChangeService element
        SOAPElement changeService = responseElement.addChildElement("ChangeService");

        // Add the required response fields
        changeService.addChildElement("requestId").addTextNode(response.getRequestId() != null ? response.getRequestId() : "?");  // Custom requestId
        changeService.addChildElement("responeCode").addTextNode(String.valueOf(response.getResponeCode()));  // Custom response code (this can be dynamic)
        changeService.addChildElement("responseMessage").addTextNode(response.getResponseMessage());  // Custom response message

        // Set the response in the SaajSoapMessage object
        SaajSoapMessage updateResponse = (SaajSoapMessage) messageContext.getResponse();
        updateResponse.setSaajMessage(soapMessage);
        updateResponse.getSaajMessage().saveChanges();

        // Convert SOAP message to DOMSource for further processing
        Document document = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();

        // Append all child nodes of the body to the fragment
        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }

        // Return the DOMSource for further processing
        return new DOMSource(fragment);
    }

    public ChangeServiceRequest changeServiceRequestMapper(ChangeService request) {
        ChangeServiceRequest changeServiceRequest = new ChangeServiceRequest();
        changeServiceRequest.setRequestId(request.getRequestId());
        changeServiceRequest.setServiceId(request.getServiceId());
        changeServiceRequest.setOverrides(request.getOverrides());
        changeServiceRequest.setUserName(request.getUserName());
        changeServiceRequest.setActionItem(request.getActionItem());
        return changeServiceRequest;
    }
}
