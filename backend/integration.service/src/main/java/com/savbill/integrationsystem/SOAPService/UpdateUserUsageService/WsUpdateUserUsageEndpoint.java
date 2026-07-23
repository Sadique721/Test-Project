package com.savbill.integrationsystem.SOAPService.UpdateUserUsageService;

import com.savbill.integrationsystem.RestApiService.UpdateUserUsage.UpdateUserUsageService;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.billgen.repository.CustomerRepository;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;

import com.savbill.integrationsystem.generated.newupdateuserusage.UpdateUserUsageResponse;
import com.savbill.integrationsystem.generated.wsupdateuserusage.UpdateUserUsage;
import com.savbill.integrationsystem.generated.wsupdateuserusage.WsUpdateUserUsage;
import com.savbill.integrationsystem.generated.wsupdateuserusage.WsUpdateUserUsageResponse;
//import org.omg.CORBA.DynAnyPackage.Invalid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class WsUpdateUserUsageEndpoint {
    @Autowired
    RadiusClientService radiusClientService;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private UpdateUserUsageService handleUpdateUserUsages;

    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI, localPart = "wsUpdateUserUsage")
    @ResponsePayload
    public com.savbill.integrationsystem.generated.newupdateuserusage.WsUpdateUserUsageResponse handleUpdateUserUsageRequest(@RequestPayload WsUpdateUserUsage request, MessageContext messageContext) throws SOAPException, IOException {
        com.savbill.integrationsystem.generated.newupdateuserusage.WsUpdateUserUsageResponse response = new com.savbill.integrationsystem.generated.newupdateuserusage.WsUpdateUserUsageResponse();
        long startTime = System.currentTimeMillis();
        try {
            response = handleUpdateUserUsage(request);
            log.info("handleUpdateUserUsage Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
            return response;
        } catch (Exception e) {
            response = handleUpdateUserUsage(request);
            log.info("handleUpdateUserUsage Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
            return response;
        }
    }


    //    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI, localPart = "UpdateUserUsage")
//    @ResponsePayload
//    public DOMSource handleUpdateUserUsageRequest1(@RequestPayload UpdateUserUsage request, MessageContext messageContext) throws SOAPException, IOException {
//        WsUpdateUserUsageResponse response = null;
//        try {
//            WsUpdateUserUsage userUsage = mapProperties(request);
//            return handleUpdateUserUsageRequest(userUsage, messageContext);
//        } catch (Exception e) {
//            return generateUpdateUserUsageSOAP11InvalidUserIdResponse(response, messageContext);
//        }
//    }
    private WsUpdateUserUsage mapProperties(UpdateUserUsage request) {
        WsUpdateUserUsage userUsage = new WsUpdateUserUsage();
        userUsage.setRequestId(request.getRequestId());
        userUsage.setActionItem(request.getActionItem());
        userUsage.setUserName(request.getUserName());
        userUsage.setUsageBytes(request.getUsageBytes());
        return userUsage;
    }

    public com.savbill.integrationsystem.generated.newupdateuserusage.WsUpdateUserUsageResponse handleUpdateUserUsage(WsUpdateUserUsage request) {
        com.savbill.integrationsystem.generated.newupdateuserusage.WsUpdateUserUsageResponse response = new com.savbill.integrationsystem.generated.newupdateuserusage.WsUpdateUserUsageResponse();
        long startTime = System.currentTimeMillis();
        log.info("Starting method: handleUpdateUserUsage AT:{}MS", new Date(startTime));
        UpdateUserUsageResponse updateUserUsage = new UpdateUserUsageResponse();
        updateUserUsage.setRequestId(request.getRequestId());
        String userName = request.getUserName().trim();
        try {
            if (!userName.trim().equals("")) {
                double bytes = request.getUsageBytes();
                if (bytes <= 0) {
                    updateUserUsage.setResponeCode(SoapConstants.EMPTY);
                    updateUserUsage.setResponseMessage("Input Usage Bytes Empty or Null.");
                    response.setUpdateUserUsage(updateUserUsage);
                    log.warn("Input byte is empty or null");
                    return response;
                }
                try {
                    log.debug("Call Radius client to UpdateUerUsage:{} ", userName);
                    GenericDataDTO genericDataDTO = radiusClientService.UpdateUerUsage(userName, SoapConstants.MVNOID, request.getUsageBytes());
                    log.debug("Integration received Response IN:{}MS,RESPONSE:{}", System.currentTimeMillis() - startTime, genericDataDTO.getResponseMessage());
                    if (Objects.nonNull(genericDataDTO.getData())) {
//                        if(genericDataDTO.getResponseCode() == 422){
//                            updateUserUsage.setResponeCode(422);
//                            updateUserUsage.setResponseMessage("Usage exceeds total quota.");
//                            response.setUpdateUserUsage(updateUserUsage);
//                            return response;
//                        }
                        if (genericDataDTO != null && genericDataDTO.getData() instanceof Map) {
                            log.info("Successfully Update Usage for:{}", userName);
                            updateUserUsage.setResponeCode(SoapConstants.SUCCESS_CODE);
                            updateUserUsage.setResponseMessage(SoapConstants.SUCCESS);
                            response.setUpdateUserUsage(updateUserUsage);
                            return response;
                        }
                    } else {
                        log.warn("Input User Not Exist In System:{}", userName);
                        updateUserUsage.setResponeCode(SoapConstants.NotAcceptable);
                        updateUserUsage.setResponseMessage(SoapConstants.USER_NOT_EXIST_SPR);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("Exception while updating user usage", e);
                    updateUserUsage.setResponeCode(SoapConstants.INTERNAL_ERROR);
                    updateUserUsage.setResponseMessage(SoapConstants.ERROR_RADIUS_CLIENT + e.getMessage());
                }
            } else {
                log.warn("Input Username is empty or null");
                updateUserUsage.setResponeCode(SoapConstants.EMPTY);
                updateUserUsage.setResponseMessage(SoapConstants.INPUT_USERNAME_NULL_Empty);
            }

            response.setUpdateUserUsage(updateUserUsage);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            updateUserUsage.setResponeCode(SoapConstants.INTERNAL_ERROR);
            updateUserUsage.setResponseMessage(SoapConstants.INTERNAL_ERROR + e.getMessage());
            response.setUpdateUserUsage(updateUserUsage);
            log.error("Internal Error while updating user usage");
            return response;
        }
    }
    /*
    public DOMSource generateUpdateUserUsageSOAPResponse(WsUpdateUserUsageResponse response) throws SOAPException, ParserConfigurationException {
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
        SOAPElement responseElement = body.addChildElement("wsUpdateUserUsageResponse", "ns2");
        responseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");

        // Add UpdateUserUsage element
        SOAPElement updateUserUsageElement = responseElement.addChildElement("UpdateUserUsage");

        // Add child elements to UpdateUserUsage
        if(response.getUpdateUserUsage().getRequestId()==null || response.getUpdateUserUsage().getRequestId().equals("?") || response.getUpdateUserUsage().getRequestId().equals("") || response.getUpdateUserUsage().getRequestId().equals(" ")){
            updateUserUsageElement.addChildElement("requestId").addTextNode("?");
        }else {
            updateUserUsageElement.addChildElement("requestId").addTextNode(getSafeText(response.getUpdateUserUsage().getRequestId()));
        }
        updateUserUsageElement.addChildElement("responeCode").addTextNode(getSafeNumber(response.getUpdateUserUsage().getResponeCode()));
        updateUserUsageElement.addChildElement("responseMessage").addTextNode(getSafeText(response.getUpdateUserUsage().getResponseMessage()));

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

    /**
     * Creates a SOAP 1.1 success response for updating user usage.
     * The response contains a success code and message indicating the update was successful.
     *
     * @param response       The response object containing update user usage details.
     * @param messageContext The message context for the current request.
     * @return DOMSource containing the SOAP response for updating user usage successfully.
     * @throws SOAPException If there is an error in creating the SOAP message.
     */
    public DOMSource generateUpdateUserUsageSOAP11SuccessResponse(WsUpdateUserUsageResponse response, MessageContext messageContext) throws SOAPException {
        // Create a SOAP Message factory for SOAP 1.1 protocol
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        envelope.removeNamespaceDeclaration("SOAP-ENV");
        SOAPBody body = envelope.getBody();
        if (String.valueOf(response.getUpdateUserUsage().getResponeCode()).equalsIgnoreCase("406")) {
            envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
            envelope.setPrefix("soapenv");
            body.setPrefix("soapenv");
        } else {
            envelope.addNamespaceDeclaration("soap", "http://schemas.xmlsoap.org/soap/envelope/");
            envelope.setPrefix("soap");
            body.setPrefix("soap");
        }

        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode();
        }

        SOAPElement updateUserUsageResponseElement = body.addChildElement("wsUpdateUserUsageResponse", "ns2", "http://api.act.com/");
        updateUserUsageResponseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");

        SOAPElement updateUserUsage = updateUserUsageResponseElement.addChildElement("UpdateUserUsage");

        updateUserUsage.addChildElement("requestId").addTextNode(response.getUpdateUserUsage().getRequestId() != null && !response.getUpdateUserUsage().getRequestId().isEmpty() ? response.getUpdateUserUsage().getRequestId() : "?");
        updateUserUsage.addChildElement("responeCode").addTextNode(String.valueOf(response.getUpdateUserUsage().getResponeCode()));
        updateUserUsage.addChildElement("responseMessage").addTextNode(response.getUpdateUserUsage().getResponseMessage());

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

    /**
     * Creates a SOAP 1.1 response for updating user usage with an invalid user ID.
     * The response contains a success code and a message indicating the operation was successful.
     *
     * @param response       The response object containing update user usage details.
     * @param messageContext The message context for the current request.
     * @return DOMSource containing the SOAP response for updating user usage with an invalid user ID.
     * @throws SOAPException If there is an error in creating the SOAP message.
     */
    public DOMSource generateUpdateUserUsageSOAP11InvalidUserIdResponse(WsUpdateUserUsageResponse response, MessageContext messageContext) throws SOAPException {
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

        SOAPElement updateUserUsageResponseElement = body.addChildElement("wsUpdateUserUsageResponse", "ns2", "http://api.act.com/");
        updateUserUsageResponseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");

        SOAPElement updateUserUsage = updateUserUsageResponseElement.addChildElement("UpdateUserUsage");

        updateUserUsage.addChildElement("requestId").addTextNode(response.getUpdateUserUsage().getRequestId());
        updateUserUsage.addChildElement("responeCode").addTextNode(String.valueOf(response.getUpdateUserUsage().getResponeCode()));
        updateUserUsage.addChildElement("responseMessage").addTextNode(response.getUpdateUserUsage().getResponseMessage());

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

