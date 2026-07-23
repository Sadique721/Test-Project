package com.savbill.integrationsystem.SOAPService.logOnSubSession;

import com.savbill.integrationsystem.RestApiService.logOnSubSession.LogOnSubSessionDTO;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClient;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.transform.dom.DOMSource;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class LogOnSubSessionService {
    @Autowired
    RadiusClient radiusClient;
    @Autowired
    private RadiusClientService radiusClientService;
    @Autowired
    JwtUtil jwtUtil;

    //    public String getLocationLockStatus(Map<String, String> payload, Integer mvnoId, String token) throws FeignException {
//        ResponseEntity<Map<String,Object>> responseEntity = radiusClient.getLocationLockStatus(payload, Math.toIntExact(mvnoId),token);
//        Object data = responseEntity.getBody();
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        String messsage ="";
//        try{
//            if (data instanceof Map){
//                Map<String,Object> map = (Map<String,Object>) data;
//                String status= map.get("status").toString();
//                if (status.equalsIgnoreCase("200")){
//                    return "SUCCESS";
//                }
//            }
//        }catch (FeignException e){
//            messsage = e.getMessage();
//        }catch (Exception e){
//            messsage = e.getMessage();
//        }
//
//        return messsage;
//    }
//    public Boolean checkLiveUser(String username) {
//        GenericDataDTO genericDataDTO = radiusClient.getLiveUserLoginStatus(username, SoapConstants.MVNOID);
//        if(genericDataDTO.getData() instanceof Map){
//            Map<String, Object> dataMap = (Map<String, Object>) genericDataDTO.getData();
//            if(dataMap != null && Objects.nonNull(dataMap)){
//                return true;
//            }
//        }
//        return false;
//    }
    public static DOMSource getExceptionInResponse(String faultCodeString, String exceptionNameString, String exceptionMessage, Integer resCode) throws SOAPException, IOException {
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        // Declare the required namespaces explicitly at the envelope level
        envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/encoding/");
        envelope.addNamespaceDeclaration("soapenc", SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);

        // Create SOAP body
        SOAPBody body = envelope.getBody();

        // Add InvalidIPAddressException element
        SOAPElement logonSubSessionResponse = body.addChildElement(new QName("", "ns1:logonSubSessionResponse"));
        logonSubSessionResponse.setAttributeNS(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE, "soapenv:encodingStyle", SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);

        SOAPElement result = logonSubSessionResponse.addChildElement(new QName("", "result"));
        result.addAttribute(new QName("href"), "#id0");


        // Manually create the multiRef element
        SOAPElement multiRef = body.addChildElement(new QName("", "multiRef"));
        org.w3c.dom.Element multiRefElement = (org.w3c.dom.Element) multiRef;

        // Add attributes in the desired order
        multiRefElement.setAttribute("id", "id0");
        multiRefElement.setAttributeNS(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE, "soapenc:root", "0");
        multiRefElement.setAttributeNS(SoapConstants.XSI_NAMESPACE, "xsi:type", "ns2:" + "LogonSubSessionReply");
        multiRefElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:soapenc", SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);
        multiRefElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns2", "http://npm.redback.com");
        multiRefElement.setAttributeNS(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE, "soapenv:encodingStyle", SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);

        // Add the responsecode element
        SOAPElement responseCode = multiRef.addChildElement("responseCode");
        responseCode.addAttribute(new QName(SoapConstants.XSI_NAMESPACE, "type", "xsi"), "xsd:string");
        responseCode.addTextNode(String.valueOf(resCode));
        // Add the message element
        SOAPElement message = multiRef.addChildElement("responseMsg");
        message.addAttribute(new QName(SoapConstants.XSI_NAMESPACE, "type", "xsi"), "xsd:string");
        message.addTextNode(exceptionMessage);

        // Save changes and verify structure
        soapMessage.saveChanges();

        // Convert body to DOMSource for return
        Document document = body.getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();
        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }
        return new DOMSource(fragment);
    }

    public static DOMSource generateSuccessResponse(String localName, Integer reponseCode, String responseMessage, MessageContext messageContext) throws SOAPException {
        // Create a SOAP message
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        // Define namespaces
        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
        envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        envelope.setPrefix("soapenv");

        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode(); // Remove header if present
        }

        // Create SOAP Body
        SOAPBody body = envelope.getBody();
        body.setPrefix("soapenv");

        // Add `addServiceToSubAcctNameResponse` element
        SOAPElement responseElement = body.addChildElement(localName, "ns1", "http://npm.redback.com");
        responseElement.setEncodingStyle("http://schemas.xmlsoap.org/soap/encoding/");

        SOAPElement result = responseElement.addChildElement(new QName("", "result"));
        result.addAttribute(new QName("href"), "#id0");

        SOAPElement multiRef = body.addChildElement(new QName("", "multiRef"));
        org.w3c.dom.Element multiRefElement = (org.w3c.dom.Element) multiRef;

        // Add attributes in the desired order
        multiRefElement.setAttribute("id", "id0");
        multiRefElement.setAttributeNS(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE, "soapenc:root", "0");
        multiRefElement.setAttributeNS(SoapConstants.XSI_NAMESPACE, "xsi:type", "ns2:" + "LogonSubSessionReply");
        multiRefElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:soapenc", SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);
        multiRefElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns2", "http://npm.redback.com");
        multiRefElement.setAttributeNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:encodingStyle", SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);

        SOAPElement responseCode = multiRef.addChildElement("responseCode");
        responseCode.addAttribute(new QName(SoapConstants.XSI_NAMESPACE, "type", "xsi"), "xsd:string");
        responseCode.addTextNode(String.valueOf(reponseCode));
        // Add the message element
        SOAPElement message = multiRef.addChildElement("responseMsg");
        message.addAttribute(new QName(SoapConstants.XSI_NAMESPACE, "type", "xsi"), "xsd:string");
        message.addTextNode(responseMessage);


        // Save changes to the SOAP message
        soapMessage.saveChanges();

        // If the MessageContext is provided, update the response with the generated SOAP message
        if (messageContext != null) {
            SaajSoapMessage updateResponse = (SaajSoapMessage) messageContext.getResponse();
            updateResponse.setSaajMessage(soapMessage);
            updateResponse.getSaajMessage().saveChanges();
        }

        // Convert the SOAP body to DOMSource for the response
        Document document = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();
        NodeList childNodes = body.getChildNodes();

        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }

        return new DOMSource(fragment);
    }

    public Boolean checkTableConcurrentUser(String ipAddress) {
        GenericDataDTO genericDataDTO = radiusClientService.LoggOffSubSession(ipAddress, SoapConstants.MVNOID);
        if (genericDataDTO.getResponseCode() == 200) {
            return true;
        }
        return false;

    }

    public static DOMSource createSOAP11FaultResponse(
            String faultCodeString,
            String exceptionNameString,
            String exceptionMessage,
            Integer resCode,
            MessageContext messageContext
    ) throws SOAPException, IOException {

        // Create a SOAP 1.1 Message factory
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        // Declare custom namespaces at the envelope level
        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
        envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");

        // Set the envelope prefix to "soapenv"
        envelope.setPrefix("soapenv");

        // Set up the Body and remove any pre-existing header if present
        SOAPBody body = envelope.getBody();
        body.setPrefix("soapenv");

        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode(); // Remove header if present
        }

        // Add the logonSubSessionResponse element
        SOAPElement logonSubSessionResponse = body.addChildElement(new QName("http://npm.redback.com", "logonSubSessionResponse", "ns1"));
        logonSubSessionResponse.setAttributeNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/");

        // Add the result element and set href attribute
        SOAPElement result = logonSubSessionResponse.addChildElement(new QName("result"));
        result.addAttribute(new QName("href"), "#id0");

        // Create the multiRef element
        SOAPElement multiRef = body.addChildElement(new QName("multiRef"));
        org.w3c.dom.Element multiRefElement = (org.w3c.dom.Element) multiRef;

        // Set attributes for multiRef element
        multiRefElement.setAttribute("id", "id0");
        multiRefElement.setAttributeNS("http://schemas.xmlsoap.org/soap/encoding/", "soapenc:root", "0");
        multiRefElement.setAttributeNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/");
        multiRefElement.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:type", "ns2:LogonSubSessionReply");

        // Add required namespaces for multiRef (without redeclaring soapenv)
        multiRefElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:soapenc", "http://schemas.xmlsoap.org/soap/encoding/");
        multiRefElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns2", "http://npm.redback.com");

        // Add the responseCode element
        SOAPElement responseCode = multiRef.addChildElement("responseCode");
        responseCode.addAttribute(new QName("http://www.w3.org/2001/XMLSchema-instance", "type", "xsi"), "xsd:string");
        responseCode.addTextNode(String.valueOf(resCode));

        // Add the responseMsg element
        SOAPElement responseMsg = multiRef.addChildElement("responseMsg");
        responseMsg.addAttribute(new QName("http://www.w3.org/2001/XMLSchema-instance", "type", "xsi"), "xsd:string");
        responseMsg.addTextNode(exceptionMessage);

        // Save changes to the SOAP message
        soapMessage.saveChanges();

        // If the MessageContext is provided, update the response with the generated SOAP message
        if (messageContext != null) {
            SaajSoapMessage updateResponse = (SaajSoapMessage) messageContext.getResponse();
            updateResponse.setSaajMessage(soapMessage);
            updateResponse.getSaajMessage().saveChanges();
        }

        // Convert the SOAP body to DOMSource for the response
        Document document = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();
        NodeList childNodes = body.getChildNodes();

        // Clone child nodes from the body and append them to the fragment
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }

        // Return the response as a DOMSource
        return new DOMSource(fragment);
    }

    public GenericDataDTO getLogOnSubSession(LogOnSubSessionDTO request) {
        String ipAddress = request.getString1().trim();
        String userName = request.getString2().trim();
        String password = request.getString3().trim();
        String token = jwtUtil.generateJwtToken(SoapConstants.MVNOID);
        String responseMessage = SoapConstants.FAILURE;
        Integer responseCode = HttpStatus.EXPECTATION_FAILED.value();
        Map<String, String> payload = new HashMap<String, String>();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        log.info("Processing logon for user: {}", userName);
        if (ipAddress == null || ipAddress.isEmpty()) {
            log.warn("IPAddress is Empty or Null");
            responseMessage = "IPAddress is Empty or Null";
            responseCode = SoapConstants.EMPTY;
            genericDataDTO.setResponseCode(responseCode);
            genericDataDTO.setResponseMessage(responseMessage);
            return genericDataDTO;
        } else if (userName == null || userName.isEmpty()) {
            log.warn("Username is Empty or Null");
            responseMessage = "Username is Empty or Null";
            responseCode = SoapConstants.EMPTY;
            genericDataDTO.setResponseCode(responseCode);
            genericDataDTO.setResponseMessage(responseMessage);
            return genericDataDTO;
        } else if (password == null || password.isEmpty()) {
            log.warn("Password is Empty or Null");
            responseMessage = "Password is Empty or Null";
            responseCode = SoapConstants.EMPTY;
            genericDataDTO.setResponseCode(responseCode);
            genericDataDTO.setResponseMessage(responseMessage);
            return genericDataDTO;
        }
        if (!isValidIPAddress(ipAddress)) {
            log.warn("Input IpAddress Formate Invalid : {}", ipAddress);
            responseMessage = SoapConstants.INPUT_IP_ADDRESS_FORMATE_INVALID;
            responseCode = 306;
            genericDataDTO.setResponseCode(responseCode);
            genericDataDTO.setResponseMessage(responseMessage);
            return genericDataDTO;
        }
        try {
            log.info("Calling radius client service for logon sub session");
            GenericDataDTO logOnSubSession = radiusClientService.logOnSubSessionRadius(request, SoapConstants.MVNOID);
            Object data = logOnSubSession.getData();
            if (data instanceof Map) {
                if (Objects.nonNull(data)) {
                    Map<String, Object> customerdata = (Map<String, Object>) data;
                    String liveUsername = customerdata.get("username").toString();
                    String livePassword = customerdata.get("password").toString();
                    if (userName.equalsIgnoreCase(liveUsername) && password.equalsIgnoreCase(livePassword)) {
                        log.info("User credentials match, proceeding with location lock check.");
                        payload.put("username", userName);
                        payload.put("password", password);
                        payload.put("name", "mtik");
                        payload.put("sa", "2");
                        payload.put("framed-ip-address", ipAddress);
                    }
                    Map<String, Object> locationLockResponse = getLocationLockResponse(payload, SoapConstants.MVNOID, token);
                    boolean checkLocationLock = false;
                    if (locationLockResponse.get("data") != null) {
                        checkLocationLock = (boolean) locationLockResponse.get("data");
                    }

                    if (!checkLocationLock) {
                        log.warn("User is not allowed service at this Geo location.responseMessage: {} responseCode: {}",responseMessage,responseCode);
                        responseCode = SoapConstants.USER_NOT_ALLOW_CODE;
                        responseMessage = "User is not allow service at This Geo location.";
                        if (locationLockResponse.get("status") != null) {
                            responseCode = (Integer) locationLockResponse.get("status");
                            if (responseCode == 412) {
                                log.warn("VLAN_ID or GEO_Location does not match for logged user.responseMessage: {} responseCode: {}",responseMessage,responseCode);
                                responseMessage = SoapConstants.VLAN_ID_NOT_GEO_LOCATION_NOT_MATCH;
                                responseCode = SoapConstants.VLAN_ID_NOT_GEO_LOCATION_NOT_MATCH_CODE;
                                genericDataDTO.setResponseCode(responseCode);
                                genericDataDTO.setResponseMessage(responseMessage);
                                return genericDataDTO;
                            }
                        }
                        if (locationLockResponse.get("message") != null) {
                            responseMessage = (String) locationLockResponse.get("message");
                        }
                        genericDataDTO.setResponseCode(responseCode);
                        genericDataDTO.setResponseMessage(responseMessage);
                        return genericDataDTO;
                    }
                    log.info("COA processed successfully.responseMessage: {} responseCode: {}",responseMessage,responseCode);
                    responseCode = SoapConstants.SUCCESS_CODE;
                    responseMessage = "COA successfully";
                    genericDataDTO.setResponseCode(responseCode);
                    genericDataDTO.setResponseMessage(responseMessage);
                    return genericDataDTO;
                } else {
                    log.warn("Received empty or invalid data for logon sub session.responseMessage: {} responseCode: {}",responseMessage,responseCode);
                    responseMessage = logOnSubSession.getResponseMessage();
                    responseCode = logOnSubSession.getResponseCode();
                    genericDataDTO.setResponseCode(responseCode);
                    genericDataDTO.setResponseMessage(responseMessage);
                    return genericDataDTO;
                }
            } else {
                log.warn("NAS_IDENTIFIER does not exist for given ipaddress or login action. responseMessage: {} responseCode: {}",responseMessage,responseCode);
                responseMessage = logOnSubSession.getResponseMessage();
                responseCode = logOnSubSession.getResponseCode();
                genericDataDTO.setResponseCode(responseCode);
                genericDataDTO.setResponseMessage(responseMessage);
                return genericDataDTO;
            }
        } catch (SQLException e) {
            log.error("SQL Exception generated for logged user", e.getMessage());
            responseMessage = "SQL Exception generated for logged user";
            responseCode = SoapConstants.SQL_EXCPTION_CODE;
            genericDataDTO.setResponseCode(responseCode);
            genericDataDTO.setResponseMessage(responseMessage);
            return genericDataDTO;
        } catch (FeignException e) {
            log.error("FeignException occurred", e.getMessage());
            if (e.getMessage().equalsIgnoreCase("Invalid location lock")) {
                responseMessage = "VLAN_ID or GEO_Location does not match for logged user";
                responseCode = SoapConstants.VLAN_ID_AND_GEO_LOCATIONDOES_NOT_MATCH_CODE;
                genericDataDTO.setResponseCode(responseCode);
                genericDataDTO.setResponseMessage(responseMessage);
                return genericDataDTO;
            } else {
                ObjectMapper objectMapper = new ObjectMapper();
                String message = "";
                int status = 404;
                try {
                    String errorMessage = e.contentUTF8();
                    JsonNode jsonNode = objectMapper.readTree(errorMessage);
                    message = jsonNode.get("msg").asText();
                    status = jsonNode.get("status").asInt();
                    if (Objects.nonNull(message)) {
                        responseCode = SoapConstants.NOT_FOUND;
                        responseMessage = message;
                        genericDataDTO.setResponseCode(responseCode);
                        genericDataDTO.setResponseMessage(responseMessage);
                        return genericDataDTO;
                    }
                } catch (JsonProcessingException je) {
                    log.error("Error processing JSON response", je.getMessage());
                    throw new RuntimeException("Error processing JSON response", je);
                }
                responseCode = SoapConstants.NOT_FOUND;
                responseMessage = message;
                genericDataDTO.setResponseCode(responseCode);
                genericDataDTO.setResponseMessage(responseMessage);
                return genericDataDTO;
            }
        } catch (RemoteException e) {
            log.error("RemoteException occurred", e.getMessage());
            responseMessage = SoapConstants.GENERAL_EXCEPETION;
            genericDataDTO.setResponseCode(responseCode);
            genericDataDTO.setResponseMessage(responseMessage);
            return genericDataDTO;
        } catch (RuntimeException e) {
            log.error("RuntimeException occurred", e.getMessage());
            responseMessage = "Remote Exception generated";
            responseCode = SoapConstants.REMOTE_EXCEPTION_GENERATED_CODE;
            genericDataDTO.setResponseCode(responseCode);
            genericDataDTO.setResponseMessage(responseMessage);
            return genericDataDTO;
        } catch (Exception e) {
            log.error("Unexpected Exception occurred", e.getMessage());
            responseMessage = "Exception";
            responseCode = 200;
            genericDataDTO.setResponseCode(responseCode);
            genericDataDTO.setResponseMessage(responseMessage);
            return genericDataDTO;
        }
    }

    public boolean isValidIPAddress(String ip) {
        String ipv4Pattern = "^((25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})\\.){3}(25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})$";
        String ipv6Pattern = "^([0-9a-fA-F]{1,4}:){7}([0-9a-fA-F]{1,4})$";
        return ip.matches(ipv4Pattern) || ip.matches(ipv6Pattern);
    }

    public Map<String, Object> getLocationLockResponse(Map<String, String> payload, Long mvnoId, String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (payload.get("framed-ip-address") != null) {
                ResponseEntity<Map<String, Object>> genericDataDTO = radiusClient.getLocationLockStatus(payload, Math.toIntExact(mvnoId), token, payload.get("framed-ip-address"));
                Map<String, Object> objectMap = genericDataDTO.getBody();
                String status = objectMap.get("status").toString();
                if (status != null && status.equalsIgnoreCase("200")) {
                    response.put("status", 200);
                    response.put("message", "COA successfully");
                    response.put("data", true);
                    return response;
                }
            } else {
                ResponseEntity<Map<String, Object>> genericDataDTO = radiusClient.getLocationLockStatus(payload, Math.toIntExact(mvnoId), token);
                Map<String, Object> objectMap = genericDataDTO.getBody();
                String status = objectMap.get("status").toString();
                if (status != null && status.equalsIgnoreCase("200")) {
                    response.put("status", 200);
                    response.put("message", "COA successfully");
                    response.put("data", true);
                    return response;
                }
            }
        } catch (FeignException e) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                Map<String, Object> result = mapper.readValue(e.contentUTF8(), Map.class);
                String message = (String) result.get("message");
                int status = (Integer) result.get("status");
                if (message.contains("location lock")) {
                    response.put("status", SoapConstants.VLAN_ID_NOT_GEO_LOCATION_NOT_MATCH_CODE);
                    response.put("message", SoapConstants.VLAN_ID_NOT_GEO_LOCATION_NOT_MATCH);
                    response.put("data", false);
                } else if (message.contains("Duplicate Login attempt by userName")) {
                    response.put("status", SoapConstants.NO_DATA_FOUND_IN_TBLMCONCURRENTUSERS_CODE);
                    response.put("message", SoapConstants.NO_DATA_FOUND_IN_TBLMCONCURRENTUSERS);
                    response.put("data", false);
                } else {
                    response.put("status", status);
                    response.put("message", message);
                    response.put("data", false);
                }
                System.out.printf("Status: " + status + " message: " + message);
            } catch (JsonProcessingException ex) {
                response.put("status", 400);
                response.put("message", "Invalid Data");
                response.put("data", false);
            }
            return response;
        } catch (Exception e) {
            response.put("status", 400);
            response.put("message", "Invalid Data");
            response.put("data", false);
        }
        return response;
    }

}
