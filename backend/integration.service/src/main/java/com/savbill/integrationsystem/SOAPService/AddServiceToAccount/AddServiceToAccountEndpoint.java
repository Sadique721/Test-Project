package com.savbill.integrationsystem.SOAPService.AddServiceToAccount;

import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.generated.addservicetoaccount.AddServiceToAccount;
import com.savbill.integrationsystem.generated.addservicetoaccount.WsAddServiceToAccount;
import com.savbill.integrationsystem.generated.addservicetoaccount.WsAddServiceToAccountResponse;
import com.savbill.integrationsystem.generated.newaddservicetoaccount.AddServiceToAccountResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

import javax.xml.soap.*;
import javax.xml.transform.dom.DOMSource;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Endpoint
public class AddServiceToAccountEndpoint {
    @Autowired
    CmsClientService cmsClientService;

    @Autowired
    private JwtUtil jwtUtil;

    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI, localPart = "wsAddServiceToAccount")
    @ResponsePayload
    public com.savbill.integrationsystem.generated.newaddservicetoaccount.WsAddServiceToAccountResponse getWsAddServiceToAccountResponse(@RequestPayload WsAddServiceToAccount request, MessageContext messageContext) throws SOAPException, IOException {
        long startTime = System.currentTimeMillis();
        try {
            return getWsAddServiceToAccount(request);

//            return generateAddServiceToAccountSOAP11SuccessResponse(response, messageContext);
        } catch (Exception e) {
            String exceptionMessage = e.getMessage() != null ? e.getMessage() : "Exception was encountered during processing Request.";
            return getWsAddServiceToAccount(request);
        }
    }

    //    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI, localPart = "AddServiceToAccount")
//    @ResponsePayload
//    public DOMSource getAddServiceToAccountResponse(@RequestPayload AddServiceToAccount request, MessageContext messageContext) throws SOAPException, IOException {
//        AddServiceToAccountResponse response = null;
//        try {
//            response = getAddServiceToAccount1(request);
//            return generateAddServiceToAccountSOAP11SuccessResponse1(response, messageContext);
//        } catch (Exception e) {
//            String exceptionMessage = e.getMessage() != null ? e.getMessage() : "Exception was encountered during processing Request.";
//            return generateAddServiceToAccountSOAP11SuccessResponse1(response, messageContext);
//        }
//    }
    public com.savbill.integrationsystem.generated.newaddservicetoaccount.WsAddServiceToAccountResponse getWsAddServiceToAccount(WsAddServiceToAccount request) {
        com.savbill.integrationsystem.generated.newaddservicetoaccount.WsAddServiceToAccountResponse response = new com.savbill.integrationsystem.generated.newaddservicetoaccount.WsAddServiceToAccountResponse();
//        WsAddServiceToAccountResponse.AddServiceToAccount addServiceToAccountResponse = new WsAddServiceToAccountResponse.AddServiceToAccount();
        AddServiceToAccountResponse addServiceToAccountResponse = new AddServiceToAccountResponse();

        //        com.savbill.integrationsystem.generated.newaddservicetoaccount.WsAddServiceToAccountResponse
        String requestId = request.getRequestId();
        addServiceToAccountResponse.setRequestId((requestId != null && !requestId.trim().isEmpty()) ? requestId : "?");
        String responseMessage = SoapConstants.FAILURE;
        int responseCode = SoapConstants.INTERNAL_ERROR;
        String userName = request.getUserName().trim();
        String serviceId = request.getServiceId().trim();
        long startTime = System.currentTimeMillis();
        log.info("Starting getWsAddServiceToAccount At:{}", new Date(startTime));
        try {
            if (userName.isEmpty() || userName == null) {
                responseMessage = SoapConstants.Input_Username_is_Empty_or_null;
                responseCode = SoapConstants.EMPTY;
                addServiceToAccountResponse.setResponseMessage(responseMessage);
                addServiceToAccountResponse.setResponeCode(responseCode);
                addServiceToAccountResponse.setRequestId((requestId != null && !requestId.trim().isEmpty()) ? requestId : "?");
//                response.set(respo///nse1);
                response.setAddServiceToAccount(addServiceToAccountResponse);
                log.warn("Input UserName Is Null Or Empty");
                log.info("Method getWsAddServiceToAccount completed IN:{}MS", System.currentTimeMillis() - startTime);
                return response;
            } else if (serviceId.isEmpty() || serviceId == null) {
                responseMessage = SoapConstants.Input_ServiceId_is_Empty_or_null;
                responseCode = SoapConstants.EMPTY;
                addServiceToAccountResponse.setResponseMessage(responseMessage);
                addServiceToAccountResponse.setResponeCode(responseCode);
                addServiceToAccountResponse.setRequestId((requestId != null && !requestId.trim().isEmpty()) ? requestId : "?");
                response.setAddServiceToAccount(addServiceToAccountResponse);
                log.warn("Input ServiceId Is Null Or Empty for userName{}", userName);
                log.info("Method getWsAddServiceToAccount completed IN:{}MS", System.currentTimeMillis() - startTime);
                return response;
            } else {
                Long mvnoId = SoapConstants.MVNOID;
                String token = jwtUtil.generateJwtToken(mvnoId);
                log.debug("Call CMS Client to Add Service:{} To Account:{}", serviceId, userName);
                ResponseEntity<?> responseEntity = cmsClientService.AddServiceToAccountAccount(request, mvnoId, token);
                Object responseData = responseEntity.getBody();
                log.debug("Integration Received Response IN:{}MS,Response:{}", System.currentTimeMillis() - startTime, responseData);
                if (responseData instanceof LinkedHashMap) {
                    Map<String, Object> responseMap = (Map<String, Object>) responseData;
                    if (responseMap.containsKey("message") && responseMap.containsValue("Username Not available")) {
                        responseMessage = SoapConstants.NOT_UPDATED_RECORD_IN_SPR_TABLE_DUE_TO_TECHNICAL_ISSUES;
                        responseCode = SoapConstants.NO_RECOED_UPDATE_CODE;
                        addServiceToAccountResponse.setResponseMessage(responseMessage);
                        addServiceToAccountResponse.setResponeCode(responseCode);
                        addServiceToAccountResponse.setRequestId((requestId != null && !requestId.trim().isEmpty()) ? requestId : "?");
                        response.setAddServiceToAccount(addServiceToAccountResponse);
                        log.warn("Input User Not Found IN System:{}", userName);
                        log.info("Method getWsAddServiceToAccount completed IN:{}MS", System.currentTimeMillis() - startTime);
                        return response;
                    } else if (responseMap.containsKey("message") && responseMap.containsValue("ServiceId Not available")) {
//                        responseMessage = SoapConstants.SERVICE_ID_NOT_AVAILABLE;
//                        responseCode = SoapConstants.NOT_FOUND;
                        addServiceToAccountResponse.setResponseMessage("Not Updated Record in SPR table due to Technical Issue Via Product API[updateSubscriber]");
                        addServiceToAccountResponse.setResponeCode(502);
                        addServiceToAccountResponse.setRequestId((requestId != null && !requestId.trim().isEmpty()) ? requestId : "?");
                        response.setAddServiceToAccount(addServiceToAccountResponse);
                        log.warn("Input ServiceId Not Found IN System:{}", serviceId);
                        log.info("Method getWsAddServiceToAccount completed IN:{}MS", System.currentTimeMillis() - startTime);
                        return response;
                    } else if (responseMap.get("deActivateResponse") != null) {
                        responseMessage = SoapConstants.CUSTOMER_UPDATED_IN_SPR_TABLE;
                        responseCode = SoapConstants.SUCCESS_CODE;
                        addServiceToAccountResponse.setResponseMessage(responseMessage);
                        addServiceToAccountResponse.setResponeCode(responseCode);
                        addServiceToAccountResponse.setRequestId((requestId != null && !requestId.trim().isEmpty()) ? requestId : "?");
                        response.setAddServiceToAccount(addServiceToAccountResponse);
                        log.info("Customer Updated Successfully for user:{} bind ServiceId{}", userName, serviceId);
                        log.info("Method getWsAddServiceToAccount completed IN:{}MS", System.currentTimeMillis() - startTime);
                        return response;
                    }
                }
            }
        } catch (FeignException e) {
            log.debug("FeignException Exception:during operation for Account:{},ServiceID:{}", userName, serviceId);
            ObjectMapper objectMapper = new ObjectMapper();
            String message = "";
            int status = 404;

            try {
                String errorMessage = e.contentUTF8();
                JsonNode jsonNode = objectMapper.readTree(errorMessage);
                if (jsonNode.has("msg")) {
                    message = jsonNode.get("msg").asText();
                }
                if (jsonNode.has("status")) {
                    status = jsonNode.get("status").asInt();
                }
                if (Objects.nonNull(message) && message.equalsIgnoreCase("Please enter a valid service")) {
                    log.warn("You Are Trying to Bind plan BandwidthBooster or VolumeBooster Witch is incorrect please Bind Normal ServiceID for Account:{}", userName);
                    addServiceToAccountResponse.setResponeCode(417);
                    addServiceToAccountResponse.setResponseMessage("Base plan Can't change with Bandwidth booster and volume booster.");
                    addServiceToAccountResponse.setRequestId(requestId);
                    response.setAddServiceToAccount(addServiceToAccountResponse);
                    log.info("Method getWsAddServiceToAccount completed IN:{}MS", System.currentTimeMillis() - startTime);
                    return response;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                responseCode = SoapConstants.INTERNAL_ERROR;
                responseMessage = "An error occurred while processing the request";
                log.error("Error Accruing while performing Add Service To Account withe Error:{}", e.getMessage());
            }
        } catch (Exception e) {
            responseMessage = SoapConstants.NOT_UPDATED_RECORD_IN_SPR_TABLE_DUE_TO_TECHNICAL_ISSUES;
            responseCode = SoapConstants.NO_RECOED_UPDATE_CODE;
            log.error("Error Accruing while performing Add Service To Account withe Error:{}", e.getMessage());
        }

        addServiceToAccountResponse.setResponseMessage(responseMessage);
        addServiceToAccountResponse.setResponeCode(responseCode);
        addServiceToAccountResponse.setRequestId((requestId != null && !requestId.trim().isEmpty()) ? requestId : "?");
        response.setAddServiceToAccount(addServiceToAccountResponse);
        log.info("Method getWsAddServiceToAccount completed IN:{}MS", System.currentTimeMillis() - startTime);
        return response;
    }
//    public AddServiceToAccountResponse getAddServiceToAccount1(AddServiceToAccount request) {
//        AddServiceToAccountResponse response = new AddServiceToAccountResponse();
//        AddServiceToAccountResponse.AddServiceToAccount addServiceToAccountResponse = new AddServiceToAccountResponse.AddServiceToAccount();
//
//        String requestId = request.getRequestId();
//        addServiceToAccountResponse.setRequestId((requestId != null && !requestId.trim().isEmpty()) ? requestId : "?");
//        String responseMessage = SoapConstants.FAILURE;
//        int responseCode = SoapConstants.INTERNAL_ERROR;
//        String userName = request.getUserName().trim();
//        String serviceId = request.getServiceId().trim();
//        WsAddServiceToAccount map = mapProperties(request);
//        try {
//            if (userName.isEmpty() || userName == null) {
//                responseMessage = SoapConstants.Input_Username_is_Empty_or_null;
//                responseCode = SoapConstants.EMPTY;
//            } else if (serviceId.isEmpty() || serviceId == null) {
//                responseMessage = SoapConstants.Input_ServiceId_is_Empty_or_null;
//                responseCode = SoapConstants.EMPTY;
//            } else {
//                Long mvnoId = SoapConstants.MVNOID;
//                String token = jwtUtil.generateJwtToken(mvnoId);
//                ResponseEntity<?> responseEntity = cmsClientService.AddServiceToAccountAccount(map, mvnoId, token);
//                Object responseData = responseEntity.getBody();
//
//                if (responseData instanceof LinkedHashMap) {
//                    Map<String, Object> responseMap = (Map<String, Object>) responseData;
//                    if (responseMap.containsKey("message") && responseMap.containsValue("Username Not available")) {
//                        responseMessage = "UserName is available in SPR table";
//                        responseCode = SoapConstants.NO_RECOED_UPDATE_CODE;
//                    } else if (responseMap.containsKey("message") && responseMap.containsValue("ServiceId Not available")) {
//                        responseMessage = SoapConstants.SERVICE_ID_NOT_AVAILABLE;
//                        responseCode = SoapConstants.NOT_FOUND;
//                    } else if (responseMap.get("deActivateResponse") != null) {
//                        responseMessage = SoapConstants.CUSTOMER_UPDATED_IN_SPR_TABLE;
//                        responseCode = SoapConstants.SUCCESS_CODE;
//                    }
//                }
//            }
//        } catch (FeignException e) {
//            ObjectMapper objectMapper = new ObjectMapper();
//            String message = "";
//            int status = 404;
//
//            try {
//                String errorMessage = e.contentUTF8();
//                JsonNode jsonNode = objectMapper.readTree(errorMessage);
//                if (jsonNode.has("msg")) {
//                    message = jsonNode.get("msg").asText();
//                }
//                if (jsonNode.has("status")) {
//                    status = jsonNode.get("status").asInt();
//                }
//                if (Objects.nonNull(message) && message.equalsIgnoreCase("Please enter a valid service")) {
//                    addServiceToAccountResponse.setResponeCode(417);
//                    addServiceToAccountResponse.setResponseMessage("Base plan Can't change with Bandwidth booster and volume booster.");
//                    addServiceToAccountResponse.setRequestId(requestId);
//                    response.setAddServiceToAccount(addServiceToAccountResponse);
//                    return response;
//                }
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                responseCode = SoapConstants.INTERNAL_ERROR;
//                responseMessage = "An error occurred while processing the request";
//            }
//        } catch (Exception e) {
//            responseMessage = SoapConstants.NOT_UPDATED_RECORD_IN_SPR_TABLE_DUE_TO_TECHNICAL_ISSUES;
//            responseCode = SoapConstants.NO_RECOED_UPDATE_CODE;
//        }
//
//        addServiceToAccountResponse.setResponseMessage(responseMessage);
//        addServiceToAccountResponse.setResponeCode(responseCode);
//        addServiceToAccountResponse.setRequestId((requestId != null && !requestId.trim().isEmpty()) ? requestId : "?");
//        response.setAddServiceToAccount(addServiceToAccountResponse);
//
//        return response;
//    }

    private WsAddServiceToAccount mapProperties(AddServiceToAccount request) {
        WsAddServiceToAccount account = new WsAddServiceToAccount();
        account.setUserName(request.getUserName());
        account.setServiceId(request.getServiceId());
        account.setActionItem(request.getActionItem());
        account.setRequestId(request.getRequestId());
        return account;
    }

    /*
    public DOMSource generategetWsAddServiceToAccountSummerySOAPResponse(WsAddServiceToAccountResponse response) throws SOAPException, ParserConfigurationException {
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
        SOAPElement responseElement = body.addChildElement("wsAddServiceToAccountResponse", "ns2");
        responseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");

        // Add GetUserUsageSummary element
        SOAPElement addService = responseElement.addChildElement("AddServiceToAccount");
        addService.addChildElement("requestId").addTextNode(getSafeText(response.getAddServiceToAccount().getRequestId()));
        addService.addChildElement("responeCode").addTextNode(getSafeNumber(response.getAddServiceToAccount().getResponeCode()));
        addService.addChildElement("responseMessage").addTextNode(getSafeText(response.getAddServiceToAccount().getResponseMessage()));

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

    // Use this both success and exception response generator method without interceptor
    // First test for checking sheet response and adjust parameter value by taking from response class

    /**
     * Generates a SOAP 1.1 response message for the AddServiceToAccount operation indicating success.
     * This method constructs a SOAP message with a custom response code and message, and returns the
     * resulting message as a DOMSource for further processing.
     *
     * @param response       the {@link WsAddServiceToAccountResponse} containing the response data for adding a service to an account
     * @param messageContext the {@link MessageContext} used to update the response message context with the new SOAP message
     * @return a {@link DOMSource} containing the SOAP response message indicating success for the AddServiceToAccount operation
     * @throws SOAPException if there is an error in creating or processing the SOAP message
     */
    public DOMSource generateAddServiceToAccountSOAP11SuccessResponse(WsAddServiceToAccountResponse response, MessageContext messageContext) throws SOAPException {
        // Create a SOAP Message factory for SOAP 1.1 protocol
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.addNamespaceDeclaration("soap", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.setPrefix("soap");

        SOAPBody body = envelope.getBody();
        body.setPrefix("soap");
        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode();
        }

        SOAPElement responseElement = body.addChildElement("wsAddServiceToAccountResponse", "ns2", "http://api.act.com/");
        responseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");

        SOAPElement addServiceToAccount = responseElement.addChildElement("AddServiceToAccount");
        addServiceToAccount.addChildElement("requestId").addTextNode(response.getAddServiceToAccount().getRequestId());
        addServiceToAccount.addChildElement("responeCode").addTextNode(String.valueOf(response.getAddServiceToAccount().getResponeCode()));
        addServiceToAccount.addChildElement("responseMessage").addTextNode(response.getAddServiceToAccount().getResponseMessage());

        SaajSoapMessage updateResponse = (SaajSoapMessage) messageContext.getResponse();
        updateResponse.setSaajMessage(soapMessage);
        updateResponse.getSaajMessage().saveChanges();

        Document document = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();

        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }

        return new DOMSource(fragment);
    }
//    public DOMSource generateAddServiceToAccountSOAP11SuccessResponse1(AddServiceToAccountResponse response, MessageContext messageContext) throws SOAPException {
//        // Create a SOAP Message factory for SOAP 1.1 protocol
//        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
//        SOAPMessage soapMessage = factory.createMessage();
//        SOAPPart soapPart = soapMessage.getSOAPPart();
//        SOAPEnvelope envelope = soapPart.getEnvelope();
//
//        envelope.removeNamespaceDeclaration("SOAP-ENV");
//        envelope.addNamespaceDeclaration("soap", "http://schemas.xmlsoap.org/soap/envelope/");
//        envelope.setPrefix("soap");
//
//        SOAPBody body = envelope.getBody();
//        body.setPrefix("soap");
//        SOAPHeader header = envelope.getHeader();
//        if (header != null) {
//            header.detachNode();
//        }
//
//        SOAPElement responseElement = body.addChildElement("AddServiceToAccountResponse", "ns2", "http://api.act.com/");
//        responseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");
//
//        SOAPElement addServiceToAccount = responseElement.addChildElement("AddServiceToAccount");
//        addServiceToAccount.addChildElement("requestId").addTextNode(response.getAddServiceToAccount().getRequestId());
//        addServiceToAccount.addChildElement("responeCode").addTextNode(String.valueOf(response.getAddServiceToAccount().getResponeCode()));
//        addServiceToAccount.addChildElement("responseMessage").addTextNode(response.getAddServiceToAccount().getResponseMessage());
//
//        SaajSoapMessage updateResponse = (SaajSoapMessage) messageContext.getResponse();
//        updateResponse.setSaajMessage(soapMessage);
//        updateResponse.getSaajMessage().saveChanges();
//
//        Document document = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
//        DocumentFragment fragment = document.createDocumentFragment();
//
//        NodeList childNodes = body.getChildNodes();
//        for (int i = 0; i < childNodes.getLength(); i++) {
//            fragment.appendChild(childNodes.item(i).cloneNode(true));
//        }
//
//        return new DOMSource(fragment);
//    }

}

