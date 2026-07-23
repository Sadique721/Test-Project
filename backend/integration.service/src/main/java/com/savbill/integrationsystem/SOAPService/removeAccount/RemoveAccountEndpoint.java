package com.savbill.integrationsystem.SOAPService.removeAccount;

import com.savbill.integrationsystem.RestApiService.removeAccount.RemoveAccountRequest;
import com.savbill.integrationsystem.SOAPService.Interface.CmsClient;
import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.SOAPService.service.RemoveAccountService;
import com.savbill.integrationsystem.billgen.repository.CustomerRepository;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.generated.wsremoveaccount.RemoveAccount;
import com.savbill.integrationsystem.generated.wsremoveaccount.RemoveAccountResponse;
import com.savbill.integrationsystem.generated.wsremoveaccount.WsRemoveAccount;
import com.savbill.integrationsystem.generated.wsremoveaccount.WsRemoveAccountResponse;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
import java.util.Map;
import java.util.Objects;

@Slf4j
@Endpoint
public class RemoveAccountEndpoint {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CmsClient cmsClient;

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private RadiusClientService radiusClientService;
    @Autowired
    private RemoveAccountService removeAccountService;

    @Autowired
    private CmsClientService cmsClientService;

    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI, localPart = "wsRemoveAccount")
    @ResponsePayload
    public com.savbill.integrationsystem.generated.newremoveaccount.WsRemoveAccountResponse getRemoveAccountResponse(@RequestPayload WsRemoveAccount request, MessageContext messageContext) throws SOAPException, IOException {
        long startTime = System.currentTimeMillis();
        log.info("Starting method: getRemoveAccountResponse At:{}", new Date(startTime));

        String requestId = (request.getRequestId() == null || request.getRequestId().trim().isEmpty()) ? "?" : request.getRequestId().trim();
        com.savbill.integrationsystem.generated.newremoveaccount.WsRemoveAccountResponse responseDTO = new com.savbill.integrationsystem.generated.newremoveaccount.WsRemoveAccountResponse();
        com.savbill.integrationsystem.generated.newremoveaccount.RemoveAccountResponse accountResponse = new com.savbill.integrationsystem.generated.newremoveaccount.RemoveAccountResponse();
        String userName = request.getUserName().trim();
        Integer responseCode = HttpStatus.EXPECTATION_FAILED.value();
        String responseMessage = SoapConstants.FAILURE;
        accountResponse.setRequestId(requestId);

        if (userName == null || userName.isEmpty()) {
            log.warn("Username is empty or null");
            responseCode = SoapConstants.EMPTY;
            responseMessage = "Input UserName is Empty or Null.";
            accountResponse.setResponeCode(responseCode);
            accountResponse.setResponseMessage(responseMessage);
            accountResponse.setRequestId(requestId);
            responseDTO.setRemoveAccount(accountResponse);
            log.info("Method getRemoveAccountResponse completed in {}ms", System.currentTimeMillis() - startTime);
            return responseDTO;
        }
        try {
            log.debug("Call Radius Client To Fetch customer details for username:{}", userName);
            GenericDataDTO genericDataDTO = radiusClientService.getCustomerDetails(userName, SoapConstants.MVNOID);
            Map<String, Object> mapData = (Map<String, Object>) genericDataDTO.getData();
            log.debug("Integration Received Response In:{} ms From Radius Clinet Response:{}", System.currentTimeMillis() - startTime, mapData);

            if (Objects.nonNull(mapData)) {
                String userNm = mapData.get("username").toString();
                if (request.getUserName().equals(userNm)) {
                    log.debug("Username match found, processing remove account:{}", userNm);
                    return getRemoveAccount(request);
                } else {
                    return getRemoveAccount(request);
                }
            } else {
                log.warn("Username: {} not found in system", userName);
                responseCode = SoapConstants.NOT_FOUND;
                responseMessage = "Input UserName Is Not Available.";
                accountResponse.setResponeCode(responseCode);
                accountResponse.setResponseMessage(responseMessage);
                accountResponse.setRequestId(requestId);
                responseDTO.setRemoveAccount(accountResponse);
                log.info("Method getRemoveAccountResponse completed in {}ms", System.currentTimeMillis() - startTime);
                return responseDTO;
            }
        } catch (FeignException e) {
            log.error("FeignException occurred during operation Message:{}", e.getMessage());
            String exceptionMessage = "Exception was encountered during processing Request.";
            accountResponse.setResponeCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            accountResponse.setResponseMessage(exceptionMessage);
            accountResponse.setRequestId(requestId);
            responseDTO.setRemoveAccount(accountResponse);
            log.info("Method getRemoveAccountResponse completed in {}ms", System.currentTimeMillis() - startTime);
            return responseDTO;
        } catch (UsernameNotFoundException e) {
            log.error("Username not found exception", e.getMessage());
            String exceptionMessage = "Exception was encountered during processing Request.";
            accountResponse.setResponeCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            accountResponse.setResponseMessage(exceptionMessage);
            accountResponse.setRequestId(requestId);
            responseDTO.setRemoveAccount(accountResponse);
            log.info("Method getRemoveAccountResponse completed in {}ms", System.currentTimeMillis() - startTime);
            return responseDTO;
        } catch (RuntimeException e) {
            log.error("RuntimeException error occurred:{}", e.getMessage());
            String exceptionMessage = "Exception was encountered during processing Request.";
            accountResponse.setResponeCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            accountResponse.setResponseMessage(exceptionMessage);
            accountResponse.setRequestId(requestId);
            responseDTO.setRemoveAccount(accountResponse);
            log.info("Method getRemoveAccountResponse completed in {}ms", System.currentTimeMillis() - startTime);
            return responseDTO;
        } catch (Exception e) {
            log.error("Unexpected error occurred", e);
            String exceptionMessage = "Exception was encountered during processing Request.";
            accountResponse.setResponeCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            accountResponse.setResponseMessage(exceptionMessage);
            accountResponse.setRequestId(requestId);
            responseDTO.setRemoveAccount(accountResponse);
            log.info("Method getRemoveAccountResponse completed in {}ms", System.currentTimeMillis() - startTime);
            return responseDTO;
        }
    }
//    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI, localPart = "RemoveAccount")
//    @ResponsePayload
//    public DOMSource getRemoveAccountResponse1(@RequestPayload RemoveAccount request, MessageContext messageContext) throws SOAPException, IOException {
//        String requestId = (request.getRequestId() == null || request.getRequestId().trim().isEmpty()) ? "?" : request.getRequestId().trim();
//        RemoveAccountResponse response = null;
//        RemoveAccountResponse responseDTO = new RemoveAccountResponse();
//        String userName = request.getUserName().trim();
//        Integer responseCode = HttpStatus.EXPECTATION_FAILED.value();
//        String responseMessage = SoapConstants.FAILURE;
//        responseDTO.setRequestId(requestId);
//        if (userName == null || userName.isEmpty()) {
//            responseCode = SoapConstants.EMPTY;
//            responseMessage = "Input UserName is Empty or Null.";
//            responseDTO.setResponeCode(responseCode);
//            responseDTO.setResponseMessage(responseMessage);
//            responseDTO.setRequestId(requestId);
//            return generateRemoveAccountSOAP11SuccessResponse1(responseDTO, messageContext);
//        }
//        try {
//            GenericDataDTO genericDataDTO = radiusClientService.getCustomerDetails(userName, SoapConstants.MVNOID);
//            Map<String, Object> mapData = (Map<String, Object>) genericDataDTO.getData();
//            if (Objects.nonNull(mapData)) {
//                String userNm = mapData.get("username").toString();
//                if (request.getUserName().equals(userNm)) {
//                    response = getRemoveAccount1(request);
//                    return generateRemoveAccountSOAP11SuccessResponse1(response, messageContext);
//                } else {
//                    response = getRemoveAccount1(request);
//                    return generateRemoveAccountSOAP11SuccessResponseCaseSenstive1(response, messageContext);
//                }
//            } else {
//                responseCode = SoapConstants.NOT_FOUND;
//                responseMessage = "Input UserName Is Not Available.";
//                responseDTO.setResponeCode(responseCode);
//                responseDTO.setResponseMessage(responseMessage);
//                responseDTO.setRequestId(requestId);
//                return generateRemoveAccountSOAP11UseNameUpperCaseResponse1(responseDTO, messageContext);
//            }
//        }catch (FeignException e) {
//            String exceptionMessage =  "Exception was encountered during processing Request.";
//            responseDTO.setResponeCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//            responseDTO.setResponseMessage(exceptionMessage);
//            responseDTO.setRequestId(requestId);
//            return generateRemoveAccountSOAP11UseNameUpperCaseResponse1(responseDTO, messageContext);
//        }catch (UsernameNotFoundException e) {
//            String exceptionMessage = "Exception was encountered during processing Request.";
//            responseDTO.setResponeCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//            responseDTO.setResponseMessage(exceptionMessage);
//            responseDTO.setRequestId(requestId);
//            return generateRemoveAccountSOAP11UseNameUpperCaseResponse1(responseDTO, messageContext);
//        }catch (RuntimeException e) {
//            String exceptionMessage = "Exception was encountered during processing Request.";
//            responseDTO.setResponeCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//            responseDTO.setResponseMessage(exceptionMessage);
//            responseDTO.setRequestId(requestId);
//            return generateRemoveAccountSOAP11UseNameUpperCaseResponse1(responseDTO, messageContext);
//        }catch (Exception e) {
//            String exceptionMessage = "Exception was encountered during processing Request.";
//            responseDTO.setResponeCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//            responseDTO.setResponseMessage(exceptionMessage);
//            responseDTO.setRequestId(requestId);
//            return generateRemoveAccountSOAP11UseNameUpperCaseResponse1(responseDTO, messageContext);
//        }
//    }


    private DOMSource generateRemoveAccountSOAP11SuccessResponseCaseSenstive(WsRemoveAccountResponse response, MessageContext messageContext) throws Exception {
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.setPrefix("soapenv");

        SOAPBody body = envelope.getBody();
        body.setPrefix("soapenv");
        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode();
        }

        SOAPElement responseElement = body.addChildElement("wsRemoveAccountResponse", "ns2", "http://api.act.com/");
        responseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");

        SOAPElement removeAccount = responseElement.addChildElement("RemoveAccount");

        removeAccount.addChildElement("requestId").addTextNode(response.getRequestId()); // Replace with dynamic value if needed
        removeAccount.addChildElement("responeCode").addTextNode(String.valueOf(response.getResponeCode()));
        removeAccount.addChildElement("responseMessage").addTextNode(response.getResponseMessage());

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

    public com.savbill.integrationsystem.generated.newremoveaccount.WsRemoveAccountResponse getRemoveAccount(WsRemoveAccount request) {
        com.savbill.integrationsystem.generated.newremoveaccount.WsRemoveAccountResponse wsRemoveAccountResponse = new com.savbill.integrationsystem.generated.newremoveaccount.WsRemoveAccountResponse();
        com.savbill.integrationsystem.generated.newremoveaccount.RemoveAccountResponse accountResponse = new com.savbill.integrationsystem.generated.newremoveaccount.RemoveAccountResponse();
        long startTime = System.currentTimeMillis();
        log.info("Starting method: getRemoveAccount");
        String requestId = (request.getRequestId() == null || request.getRequestId().trim().isEmpty()) ? "?" : request.getRequestId().trim();
        Integer responseCode = HttpStatus.EXPECTATION_FAILED.value();
        String responseMessage = SoapConstants.FAILURE;
        Long mvnoId = SoapConstants.MVNOID;
        String token = jwtUtil.generateJwtToken(mvnoId);
        // Validate the userName upfront
        String userName = request.getUserName();
        if (userName == null || userName.isEmpty()) {
            log.warn("Username is empty or null in getRemoveAccount");
            responseCode = SoapConstants.EMPTY;
            responseMessage = "Input UserName is Empty or Null.";
            accountResponse.setResponeCode(responseCode);
            accountResponse.setResponseMessage(responseMessage);
            accountResponse.setRequestId(requestId);
            wsRemoveAccountResponse.setRemoveAccount(accountResponse);
            log.info("Method getRemoveAccount completed in {}ms", System.currentTimeMillis() - startTime);
            return wsRemoveAccountResponse;
        }

        try {
            userName = userName.toLowerCase().trim();
            log.debug("Fetching customer details From radius in getRemoveAccount for Account:{}", userName);
            GenericDataDTO genericDataDTO = radiusClientService.getCustomerDetails(userName, SoapConstants.MVNOID);
            log.debug("Integration Received Response In:{} ms From Radius Clinet Response:{}", System.currentTimeMillis() - startTime, genericDataDTO.getResponseMessage());

            if (genericDataDTO.getData() != null) {
                Map<String, Object> mapData = (Map<String, Object>) genericDataDTO.getData();
                if (userName.equalsIgnoreCase(mapData.get("username").toString())) {
                    log.debug("Call Cms Client To Processing remove account request for:{}", userName);
                    RemoveAccountRequest accountRequest = new RemoveAccountRequest(request);
                    ResponseEntity<?> response = cmsClientService.removeCustomerStatus(accountRequest, SoapConstants.MVNOID, token);
                    Map<String, Object> objectMap = (Map<String, Object>) response.getBody();
                    log.debug("Integration Received In:{}ms Response:{} ", System.currentTimeMillis() - startTime, objectMap);

                    if (objectMap.get("terminationCheck") != null && objectMap.get("terminationCheck").equals("Success")) {
                        log.info("Account removal successful");
                        responseCode = SoapConstants.SUCCESS_CODE;
                        responseMessage = "User is deleted from SPR table and Usages Summary Table";
                    } else {
                        log.info("Account removal status: {}", objectMap.get("terminationCheck"));
                        responseCode = SoapConstants.SUCCESS_CODE;
                        responseMessage = objectMap.get("terminationCheck").toString();
                    }
                }
            } else {
                log.warn("No data found for username: {} in getRemoveAccount", userName);
                responseCode = SoapConstants.EMPTY;
                responseMessage = "No data found for the provided username.";
            }
        } catch (FeignException e) {
            log.error("FeignException occurred:{}", e.getMessage());
            responseCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            responseMessage = "Exception was encountered during processing Request.";
        } catch (RuntimeException e) {
            log.error("RuntimeException occurred:{}", e.getMessage());
            responseCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            responseMessage = "Exception was encountered during processing Request.";
        } catch (Exception e) {
            log.error("Error in getRemoveAccount", e);
            responseCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            responseMessage = "An error occurred while processing the request.";
        }

        accountResponse.setResponeCode(responseCode);
        accountResponse.setResponseMessage(responseMessage);
        accountResponse.setRequestId(requestId);
        wsRemoveAccountResponse.setRemoveAccount(accountResponse);
        log.info("Method getRemoveAccount completed in {}ms", System.currentTimeMillis() - startTime);
        return wsRemoveAccountResponse;
    }
//    public RemoveAccountResponse getRemoveAccount1(RemoveAccount request) {
//        RemoveAccountResponse wsRemoveAccountResponse = new RemoveAccountResponse();
//        String requestId = (request.getRequestId() == null || request.getRequestId().trim().isEmpty()) ? "?" : request.getRequestId().trim();
//        Integer responseCode = HttpStatus.EXPECTATION_FAILED.value();
//        String responseMessage = SoapConstants.FAILURE;
//        Long mvnoId = SoapConstants.MVNOID;
//        String token = jwtUtil.generateJwtToken(mvnoId);
//        // Validate the userName upfront
//        String userName = request.getUserName();
//        if (userName == null || userName.isEmpty()) {
//            responseCode = SoapConstants.EMPTY;
//            responseMessage = "Input UserName is Empty or Null.";
//            wsRemoveAccountResponse.setResponeCode(responseCode);
//            wsRemoveAccountResponse.setResponseMessage(responseMessage);
//            wsRemoveAccountResponse.setRequestId(requestId);
//            return wsRemoveAccountResponse;
//        }
//
//        try {
//            userName = userName.toLowerCase().trim();
//            GenericDataDTO genericDataDTO = radiusClientService.getCustomerDetails(userName, SoapConstants.MVNOID);
//            if (genericDataDTO.getData() != null) {
//                Map<String, Object> mapData = (Map<String, Object>) genericDataDTO.getData();
//                if (userName.equalsIgnoreCase(mapData.get("username").toString())) {
//                    RemoveAccountRequest accountRequest = removeAccountMapper(request);
//
//                    ResponseEntity<?> response = cmsClientService.removeCustomerStatus(accountRequest, SoapConstants.MVNOID, token);
//                    Map<String, Object> objectMap = (Map<String, Object>) response.getBody();
//                    if (objectMap.get("terminationCheck") != null && objectMap.get("terminationCheck").equals("Success")) {
//                        responseCode = SoapConstants.SUCCESS_CODE;
//                        responseMessage = "User is deleted from SPR table and Usages Summary Table";
//                    } else {
//                        responseCode = SoapConstants.SUCCESS_CODE;
//                        responseMessage = objectMap.get("terminationCheck").toString();
//                    }
//                }
//            } else {
//                responseCode = SoapConstants.EMPTY;
//                responseMessage = "No data found for the provided username.";
//            }
//        } catch (FeignException e) {
//            responseCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
//            responseMessage = "Exception was encountered during processing Request.";
//        } catch (RuntimeException e) {
//            responseCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
//            responseMessage = "Exception was encountered during processing Request.";
//        }catch (Exception e) {
//            responseCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
//            responseMessage = "An error occurred while processing the request.";
//        }
//
//        wsRemoveAccountResponse.setResponeCode(responseCode);
//        wsRemoveAccountResponse.setResponseMessage(responseMessage);
//        wsRemoveAccountResponse.setRequestId(requestId);
//        return wsRemoveAccountResponse;
//    }
//    private DOMSource generateRemoveAccountSOAP11SuccessResponseCaseSenstive1(RemoveAccountResponse response, MessageContext messageContext) throws Exception {
//        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
//        SOAPMessage soapMessage = factory.createMessage();
//        SOAPPart soapPart = soapMessage.getSOAPPart();
//        SOAPEnvelope envelope = soapPart.getEnvelope();
//
//        envelope.removeNamespaceDeclaration("SOAP-ENV");
//        envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
//        envelope.setPrefix("soapenv");
//
//        SOAPBody body = envelope.getBody();
//        body.setPrefix("soapenv");
//        SOAPHeader header = envelope.getHeader();
//        if (header != null) {
//            header.detachNode();
//        }
//
//        SOAPElement responseElement = body.addChildElement("wsRemoveAccountResponse", "ns2", "http://api.act.com/");
//        responseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");
//
//        SOAPElement removeAccount = responseElement.addChildElement("RemoveAccount");
//
//        removeAccount.addChildElement("requestId").addTextNode(response.getRequestId()); // Replace with dynamic value if needed
//        removeAccount.addChildElement("responeCode").addTextNode(String.valueOf(response.getResponeCode()));
//        removeAccount.addChildElement("responseMessage").addTextNode(response.getResponseMessage());
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


    /*
    public DOMSource generateRemoveAccountSOAPResponse(WsRemoveAccountResponse response) throws SOAPException, ParserConfigurationException {
        // Create a SOAP Message factory and message
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        // Add namespace declarations
        envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.addNamespaceDeclaration("ns2", "http://api.act.com/");

        SOAPBody body = envelope.getBody();

        // Create the response element
        SOAPElement responseElement = body.addChildElement("wsRemoveAccountResponse", "ns2");
        responseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");

        // Add RemoveAccount element
        SOAPElement removeAccountElement = responseElement.addChildElement("RemoveAccount");

        // Add child elements to RemoveAccount
        if(response.getRequestId()==null || response.getRequestId().equals("?") || response.getRequestId().equals("") || response.getRequestId().equals(" ")){
            removeAccountElement.addChildElement("requestId").addTextNode("?");
        }else {
            removeAccountElement.addChildElement("requestId").addTextNode(getSafeText(response.getRequestId()));
        }
        removeAccountElement.addChildElement("responeCode").addTextNode(getSafeNumber(response.getResponeCode()));
        removeAccountElement.addChildElement("responseMessage").addTextNode(getSafeText(response.getResponseMessage()));

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

    // Use this both success and exception and upperCaseName response generator method without interceptor
    // First test for checking sheet response and adjust parameter value by taking from response class

    /**
     * Creates a SOAP 1.1 response for RemoveAccount with details about account removal.
     *
     * @param response       The response object containing account removal details.
     * @param messageContext The message context for the current request.
     * @return DOMSource containing the SOAP response for remove account.
     * @throws SOAPException If there is an error in creating the SOAP message.
     */
    public DOMSource generateRemoveAccountSOAP11SuccessResponse(WsRemoveAccountResponse response, MessageContext messageContext) throws SOAPException {
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

        SOAPElement responseElement = body.addChildElement("wsRemoveAccountResponse", "ns2", "http://api.act.com/");
        responseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");

        SOAPElement removeAccount = responseElement.addChildElement("RemoveAccount");

        removeAccount.addChildElement("requestId").addTextNode(response.getRequestId()); // Replace with dynamic value if needed
        removeAccount.addChildElement("responeCode").addTextNode(String.valueOf(response.getResponeCode()));
        removeAccount.addChildElement("responseMessage").addTextNode(response.getResponseMessage());

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

    public DOMSource generateRemoveAccountSOAP11SuccessResponse1(RemoveAccountResponse response, MessageContext messageContext) throws SOAPException {
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

        SOAPElement responseElement = body.addChildElement("RemoveAccountResponse", "ns2", "http://api.act.com/");
        responseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");

        SOAPElement removeAccount = responseElement.addChildElement("RemoveAccount");

        removeAccount.addChildElement("requestId").addTextNode(response.getRequestId()); // Replace with dynamic value if needed
        removeAccount.addChildElement("responeCode").addTextNode(String.valueOf(response.getResponeCode()));
        removeAccount.addChildElement("responseMessage").addTextNode(response.getResponseMessage());

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

    public RemoveAccountRequest removeAccountMapper(RemoveAccount request) {
        RemoveAccountRequest account = new RemoveAccountRequest();
        account.setRequestId(request.getRequestId());
        account.setActionItem(request.getRequestId());
        account.setUserName(request.getUserName());
        return account;
    }

    /**
     * Creates a SOAP 1.1 response for RemoveAccount with the response message indicating account removal.
     * This response uses an uppercase username in the message.
     *
     * @param response       The response object containing account removal details.
     * @param messageContext The message context for the current request.
     * @return DOMSource containing the SOAP response for remove account with uppercase username.
     * @throws SOAPException If there is an error in creating the SOAP message.
     */
    public DOMSource generateRemoveAccountSOAP11UseNameUpperCaseResponse(WsRemoveAccountResponse response, MessageContext messageContext) throws SOAPException {
        // Create a SOAP Message factory for SOAP 1.1 protocol
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.setPrefix("soapenv");

        SOAPBody body = envelope.getBody();
        body.setPrefix("soapenv");
        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode();
        }

        SOAPElement responseElement = body.addChildElement("wsRemoveAccountResponse", "ns2", "http://api.act.com/");
        responseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");

        SOAPElement removeAccount = responseElement.addChildElement("RemoveAccount");

        removeAccount.addChildElement("requestId").addTextNode(response.getRequestId()); // Replace with dynamic value if needed
        removeAccount.addChildElement("responeCode").addTextNode(String.valueOf(response.getResponeCode()));
        removeAccount.addChildElement("responseMessage").addTextNode(response.getResponseMessage());

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

    public DOMSource generateRemoveAccountSOAP11UseNameUpperCaseResponse1(RemoveAccountResponse response, MessageContext messageContext) throws SOAPException {
        // Create a SOAP Message factory for SOAP 1.1 protocol
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.setPrefix("soapenv");

        SOAPBody body = envelope.getBody();
        body.setPrefix("soapenv");
        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode();
        }

        SOAPElement responseElement = body.addChildElement("RemoveAccountResponse", "ns2", "http://api.act.com/");
        responseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");

        SOAPElement removeAccount = responseElement.addChildElement("RemoveAccount");

        removeAccount.addChildElement("requestId").addTextNode(response.getRequestId()); // Replace with dynamic value if needed
        removeAccount.addChildElement("responeCode").addTextNode(String.valueOf(response.getResponeCode()));
        removeAccount.addChildElement("responseMessage").addTextNode(response.getResponseMessage());

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

}
