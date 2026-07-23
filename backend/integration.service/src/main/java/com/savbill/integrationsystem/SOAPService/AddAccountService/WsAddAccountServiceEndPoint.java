package com.savbill.integrationsystem.SOAPService.AddAccountService;

import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.exceptions.NumberParsingException;
import com.savbill.integrationsystem.generated.newaddaccount.AddAccountResponse;
import com.savbill.integrationsystem.generated.wsaddaccount.WsAddAccountResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.*;
import javax.xml.transform.dom.DOMSource;
import java.io.IOException;
import java.util.*;

import static com.savbill.integrationsystem.SOAPService.CustomResponseGeneratorHandler.CustomResponseGenerator.getSafeNumber;

@Slf4j
@Endpoint
public class WsAddAccountServiceEndPoint {


    @Autowired
    CmsClientService cmsClientService;

    @Autowired
    JwtUtil jwtUtil;
    @Value("${servicearea.name}")
    Long serviceArea;

    @Value("${defaultplan}")
    String plan;

    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI, localPart = "wsAddAccount")
    @ResponsePayload
    public com.savbill.integrationsystem.generated.newaddaccount.WsAddAccountResponse getWsAddAccountResponse(@RequestPayload wsAddAccount request, MessageContext messageContext) throws SOAPException, IOException, ParserConfigurationException {
        long startTime = System.currentTimeMillis();
        log.info("Starting getWsAddAccountResponse for Add Account: {} At: {}", request.getUserName(), new Date(startTime));
        WsAddAccountResponse response = null;
        try {
//            updateWordWithNumber(request);
            response = getWsAddAccount(request);
//            return mapProperties(response);
        } catch (NumberParsingException e) {
            log.error("NumberParsingException in getWsAddAccountResponse: {}", e.getMessage());
            String exceptionMessage = e.getMessage() != null ? e.getMessage() : "Exception was encountered converting CONCURRENTLOGINPOLICY value to int.";
//            return generateAddAccountSOAP11SuccessResponse(response, messageContext);

        } catch (Exception e) {
            log.error("Exception in getWsAddAccountResponse: {}", e.getMessage());
            String exceptionMessage = e.getMessage() != null ? e.getMessage() : "Exception was encountered during processing Request.";
//            return generateAddAccountSOAP11ExceptionResponse(response, messageContext);
        }
        log.info("Completed getWsAddAccount in {}ms", System.currentTimeMillis() - startTime);
        return mapProperties(response);
    }

    private static com.savbill.integrationsystem.generated.newaddaccount.WsAddAccountResponse mapProperties(WsAddAccountResponse response) {
        com.savbill.integrationsystem.generated.newaddaccount.WsAddAccountResponse resp = new com.savbill.integrationsystem.generated.newaddaccount.WsAddAccountResponse();
        AddAccountResponse accountResponse = new AddAccountResponse();
        accountResponse.setRequestId(response.getRequestId());
        accountResponse.setResponseMessage(response.getResponseMessage());
        accountResponse.setResponeCode(response.getResponeCode());
        resp.setAddAccount(accountResponse);
        return resp;
    }

    public WsAddAccountResponse getWsAddAccount(wsAddAccount request) {
        long startTime = System.currentTimeMillis();
        log.info("Starting getWsAddAccount Method At: {}",new Date(startTime));
        WsAddAccountResponse response = new WsAddAccountResponse();
        String requestId = (request.getRequestId() == null || request.getRequestId().trim().isEmpty()) ? "?" : request.getRequestId().trim();
        String userName = request.getUserName().trim();
        String password = request.getPassword().trim();
        String responseMessage = SoapConstants.FAILURE;
        int responseCode = SoapConstants.INTERNAL_ERROR;
        Object customerData = null;
        response.setRequestId(requestId);


        if (userName == null || userName.isEmpty()) {
            log.warn("Empty Or Null username received for requestId: {}", requestId);
            response.setResponseMessage(SoapConstants.ADDACCOUNT_USERNAME_EMPTY);
            response.setResponeCode(SoapConstants.EMPTY);
            response.setRequestId(requestId);
            return response;
        }
        if (password == null || password.isEmpty()) {
            log.warn("Empty Or Null password received for Account Name: {}", userName);
            response.setResponseMessage(SoapConstants.INPUT_PASSWORD_NOT_AVAILABLE);
            response.setResponeCode(SoapConstants.EMPTY);
            response.setRequestId(requestId);
            return response;
        }
        try {
            List<wsAddAccount.Item> items = request.getItem();
            String[] keysToCheck = {"CUSTOMERSTATUS", "PARAM1", "PARAM2"};

            if (items != null && !items.isEmpty()) {
                boolean isAnyKeyPresent = items.stream()
                        .map(wsAddAccount.Item::getKey) // Extract keys
                        .anyMatch(key -> key != null &&
                                (key.equals("CUSTOMERSTATUS") ||
                                        key.equals("PARAM1") ||
                                        key.equals("PARAM2")));
                boolean customerStatusExists = false;
                String resMessage = "";
                if (isAnyKeyPresent) {
                    for (wsAddAccount.Item item : items) {
                        if (SoapConstants.CUSTOMERSTATUS.equals(item.getKey())) {
                            customerStatusExists = true;
                            String reqStatus = item.getValue();
                            if (!reqStatus.equalsIgnoreCase("n") && !reqStatus.equalsIgnoreCase("y") && !reqStatus.equalsIgnoreCase("suspend")) {
                                log.warn("Activation status is Null or Invalid: {} for customer: {}", item.getValue(), userName);
                                responseMessage = SoapConstants.INVALID_ACTIVATION_WITH_STATUS + " " + item.getValue();
                                responseCode = SoapConstants.InvalidActivation;
                                break;
                            }
                        }
                    }
                } else {
                    log.warn("No required keys found in items for Account: {}", userName);
                    responseMessage = SoapConstants.INVALID_ACTIVATION_WITH_STATUS_NULL;
                    responseCode = SoapConstants.InvalidActivation;
                }
//                if (!customerStatusExists) {
//                    if (request.getServiceId().isEmpty()) {
//                        responseCode = SoapConstants.NOT_FOUND;
//                        responseMessage = SoapConstants.SERVICE_ID_NOT_AVAILABLE;
//                    }
//                }
            }
            if (responseCode == SoapConstants.INTERNAL_ERROR) {
                log.debug("Proceeding with CMS client service call for AddCustomer : {}", userName);
                Long mvnoId = SoapConstants.MVNOID;
                String token = jwtUtil.generateJwtToken(mvnoId);
                ResponseEntity<?> responseEntity = cmsClientService.AddAccount(request, serviceArea, mvnoId, token, plan);
                Object response1 = responseEntity.getBody();
                log.debug("Integration Get Data From CmS: {}", response1);
                if (response1 instanceof LinkedHashMap) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map<String, Object> responseMap = (Map<String, Object>) response1;
                    if (responseMap.containsKey("message") && responseMap.containsValue("Service not available")) {
                        log.warn("Service not available for Account: {},ServiceId:{}", userName, request.getServiceId());
                        responseMessage = SoapConstants.SERVICE_ID_NOT_AVAILABLE;
                        responseCode = SoapConstants.NOT_FOUND;
                    } else if (responseMap.containsKey("message") && responseMap.containsValue("username is already exist")) {
                        log.warn("userName:{} is already exists In System: {}", userName);
                        responseMessage = SoapConstants.USERNAME_IS_AVAILABLE;
                        responseCode = SoapConstants.NOT_FOUND;
                    } else if (responseMap.containsKey("message") && responseMap.containsValue("invalid location lock")) {
                        log.warn("Invalid location lock for Account: {}", userName);
                        responseMessage = SoapConstants.INVALID_LOCATION_LOCK;
                        responseCode = SoapConstants.InvalidActivation;
                    } else {
                        customerData = responseMap.get("customer");

                        if (customerData != null) {
                            responseMessage = SoapConstants.SUCCESS;
                            responseCode = SoapConstants.SUCCESS_CODE;
                            log.info("Successfully processed account creation for Account With userName:{} ServiceId:{} And Status: {}",
                                    userName, request.getServiceId(), responseMessage);
                        } else {
                            responseMessage = responseMessage;
                            responseCode = responseCode;
                        }
                    }
                }
            }
        } catch (FeignException e) {
            log.debug("FeignException occurred for Add Account With: {}", userName, e.getMessage());
            ObjectMapper objectMapper = new ObjectMapper();
            String message = "An error occurred while processing the request";
            int status = 404;
            try {
                String errorMessage = e.contentUTF8();
                JsonNode jsonNode = objectMapper.readTree(errorMessage);
                message = jsonNode.get("ERROR").asText();
                status = jsonNode.get("status").asInt();
                if (Objects.nonNull(message) && message.equalsIgnoreCase("Package not Available")) {
                    log.debug("This: {} Service Id Not Available in System For Add Account: {}", plan, userName);
                    response.setResponseMessage("Service ID is not available in System");
                    response.setResponeCode(404);
                    response.setRequestId(requestId);
                    return response;
                } else if (Objects.nonNull(message) && message.equalsIgnoreCase("Status field is mandatory, Please add Status")) {
                    log.debug("In Item Status field is mandatory Please add Status As well for Account:{}", userName);
                    response.setResponseMessage("Status field is mandatory, Please add Status");
                    response.setResponeCode(404);
                    response.setRequestId(requestId);
                    return response;
                }
//                else if (Objects.nonNull(message) && message.contains("Param1 already exists")) {
//                  log.debug("Param1 already exists Account:{}",request.getItem());
//                    response.setResponseMessage("Param1 already exists");
//                    response.setResponeCode(404);
//                    response.setRequestId(requestId);
//                    return response;
//                }
            } catch (JsonProcessingException je) {
                log.error("Error processing JSON response for Account: {}", userName, je.getMessage());
                je.printStackTrace();
                throw new RuntimeException("Error processing JSON response", je);
            }
            e.printStackTrace();
            response.setResponeCode(status);
            response.setResponseMessage(message);
            response.setRequestId(requestId);
            return response;
        } catch (Exception e) {
            log.error("Unexpected error for requestId: {}", requestId, e);
            e.printStackTrace();
            response.setResponeCode(500);
            response.setResponseMessage("An error occurred while processing the request");
            response.setRequestId(requestId);
            return response;
        }

        response.setResponseMessage(responseMessage);
        response.setResponeCode(responseCode);
        response.setRequestId(requestId);
        return response;
    }


    private void updateWordWithNumber(wsAddAccount request) {
        Optional<wsAddAccount.Item> first = request.getItem()
                .stream()
                .filter(item -> "CONCURRENTLOGINPOLICY".equals(item.getKey())) // Find the item with the target key
                .findFirst();
        if (first.isPresent() && !first.get().getValue().isEmpty()) {
            String value = first.get().getValue();
            int intNumber = WordToNumberConverter.convertWordToNumber(value);
            first.ifPresent(item -> item.setValue(getSafeNumber(intNumber))); // Update the value if the item is found
            log.info("CONCURRENTLOGINPOLICY received value {} updated with {}.", value, intNumber);
        }
    }


    /**
     * Generates a SOAP 1.1 response message indicating a successful AddAccount operation.
     * This method creates a SOAP message with a success response containing fixed values
     * such as a status code of 200 and a success message. It then processes the response
     * and constructs a DOMSource containing the SOAP message.
     *
     * @param response       the {@link WsAddAccountResponse} containing the details of the account addition response
     * @param messageContext the {@link MessageContext} used to update the response message context with the new SOAP message
     * @return a {@link DOMSource} containing the SOAP response message indicating success
     * @throws SOAPException                if there is an error in creating or processing the SOAP message
     * @throws ParserConfigurationException if there is a configuration error in parsing the XML
     */
    public DOMSource generateAddAccountSOAP11SuccessResponse(WsAddAccountResponse response, MessageContext messageContext) throws SOAPException, ParserConfigurationException {
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

        SOAPElement responseElement = body.addChildElement("wsAddAccountResponse", "ns2", "http://api.act.com/");
        responseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");

        SOAPElement addAccount = responseElement.addChildElement("AddAccount");
        addAccount.addChildElement("requestId").addTextNode(response.getRequestId());
        addAccount.addChildElement("responeCode").addTextNode(response.getResponeCode().toString());
        addAccount.addChildElement("responseMessage").addTextNode(response.getResponseMessage());


        SaajSoapMessage updateResponse = (SaajSoapMessage) messageContext.getResponse();
        updateResponse.setSaajMessage(soapMessage);
        updateResponse.getSaajMessage().saveChanges();

        // Convert SOAP message to DOMSource
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
     * Generates a SOAP 1.1 response message for the AddAccount operation.
     * This method creates a SOAP message from the provided response object and populates it
     * with the appropriate data. It processes the response and constructs a DOMSource
     * containing the SOAP message.
     *
     * @param response       the {@link WsAddAccountResponse} containing the details of the account addition response
     * @param messageContext the {@link MessageContext} used to update the response message context with the new SOAP message
     * @return a {@link DOMSource} containing the SOAP response message with the account addition details
     * @throws SOAPException                if there is an error in creating or processing the SOAP message
     * @throws ParserConfigurationException if there is a configuration error in parsing the XML
     */
    public DOMSource generateAddAccountSOAP11ExceptionResponse(WsAddAccountResponse response, MessageContext messageContext) throws SOAPException, ParserConfigurationException {
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

        SOAPElement responseElement = body.addChildElement("wsAddAccountResponse", "ns2", "http://api.act.com/");
        responseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");

        SOAPElement addAccount = responseElement.addChildElement("AddAccount");
        addAccount.addChildElement("requestId").addTextNode(response.getRequestId());
        addAccount.addChildElement("responeCode").addTextNode(String.valueOf(response.getResponeCode()));
        addAccount.addChildElement("responseMessage").addTextNode(response.getResponseMessage());


        SaajSoapMessage updateResponse = (SaajSoapMessage) messageContext.getResponse();
        updateResponse.setSaajMessage(soapMessage);
        updateResponse.getSaajMessage().saveChanges();

        // Convert SOAP message to DOMSource
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

}
