package com.savbill.integrationsystem.RestApiService.resetMeteredUsageForSubAccountName;

import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.generated.resetmeteredusageforsubacctname.ResetMeteredUsageForSubAcctName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.transform.dom.DOMSource;
import java.sql.SQLException;
import java.util.Date;

@Slf4j
@Service
public class ReserMeteredUsageForSubAccNameService {
    @Autowired
    private CmsClientService cmsClientService;
    @Autowired
    private JwtUtil jwtUtil;

    public GenericDataDTO resetMeteredUsageForSubAccName(@RequestPayload ResetMeteredUsageForSubAcctNameDTO request) throws Exception {
        long startTime = System.currentTimeMillis();
        log.info("Start Method: resetMeteredUsageForSubAccName At:{}", new Date(startTime));
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        String token = jwtUtil.generateJwtToken(SoapConstants.MVNOID);
        String userName = request.getUserName().trim();
        if (userName == null || userName.isEmpty()) {
            log.warn("Username is empty or null.");
            genericDataDTO.setResponseMessage("Username is Empty or Null");
            genericDataDTO.setResponseCode(SoapConstants.EMPTY);
            log.info("End Method: resetMeteredUsageForSubAccName At:{}MS", System.currentTimeMillis() - startTime);
            return genericDataDTO;
        }
        try {
            userName = userName.toLowerCase().trim();
            log.info("Calling CMS Client Service to reset metered usage for sub-account: {}", userName);
            Boolean resetValidate = cmsClientService.resetUsageForAccount(userName, SoapConstants.MVNOID, token);
            log.info("Integration Received Response In:{}ms,Response:{}MS", System.currentTimeMillis() - startTime, resetValidate);
            if (resetValidate) {
                log.info("Successfully reset metered usage for sub-account: {}", userName);
                genericDataDTO.setResponseMessage(SoapConstants.SUCCESS);
                genericDataDTO.setResponseCode(SoapConstants.SUCCESS_CODE);
                log.info("End Method: resetMeteredUsageForSubAccName At:{}MS", System.currentTimeMillis() - startTime);
                return genericDataDTO;
            }
            log.warn("Username: {} is not available in SPR Table.", userName);
            genericDataDTO.setResponseMessage("Username is not available in SPR Table");
            genericDataDTO.setResponseCode(SoapConstants.USER_NOT_AVAILABLE_IN_SPR_TABLE_CODE);
            log.info("End Method: resetMeteredUsageForSubAccName At:{}MS", System.currentTimeMillis() - startTime);
            return genericDataDTO;

        } catch (SQLException e) {
            log.error("SQL Exception occurred while resetting metered usage for sub-account: {}",userName, e.getMessage());
            genericDataDTO.setResponseMessage(SoapConstants.SQL_EXCEPTION);
            genericDataDTO.setResponseCode(SoapConstants.SQL_EXCPTION_CODE);
            log.info("End Method: resetMeteredUsageForSubAccName At:{}", System.currentTimeMillis() - startTime);
            return genericDataDTO;
        } catch (Exception e) {
            log.error("Exception occurred while resetting metered usage for sub-account: {}",userName, e.getMessage());
            genericDataDTO.setResponseMessage("Exception");
            genericDataDTO.setResponseCode(SoapConstants.INTERNAL_ERROR);
            log.info("End Method: resetMeteredUsageForSubAccName At:{}MS", System.currentTimeMillis() - startTime);
            return genericDataDTO;
        }
    }

    public DOMSource resetMeteredUsageForSubAccName(@RequestPayload ResetMeteredUsageForSubAcctName request, MessageContext messageContext) throws Exception {
        long startTime = System.currentTimeMillis();
        log.info("Start Method: resetMeteredUsageForSubAccName At:{}", new Date(startTime));
        String token = jwtUtil.generateJwtToken(SoapConstants.MVNOID);
        String userName = request.getString1().trim();
        if (userName == null || userName.isEmpty()) {
            log.warn("Username is empty or null.");
            return generateResetMeterUsageForSubAcctNameSOAP11ExceptionResponse
                    ("generalException",
                            "InvalidSubscriberAccountException",
                            "Username is Empty or Null",
                            "ecaaa1", messageContext);
        }
        try {
            userName = userName.toLowerCase().trim();
            log.info("Calling CMS Client Service to reset metered usage for sub-account: {}", userName);
            Boolean resetValidate = cmsClientService.resetUsageForAccount(userName, SoapConstants.MVNOID, token);
            log.info("Integration Received Response In:{}ms,Response:{}", System.currentTimeMillis() - startTime, resetValidate);
            if (resetValidate) {
                log.info("Successfully reset metered usage for sub-account: {}", userName);
                return generateResetMeterUsageForSubAcctNameSOAP11SuccessResponse("resetMeteredUsageForSubAcctNameResponse", messageContext);

            }
            log.warn("Username: {} is not available in SPR Table.", userName);
            return generateResetMeterUsageForSubAcctNameSOAP11ExceptionResponse
                    ("generalException",
                            "InvalidSubscriberAccountException",
                            "Username is not available in SPR Table",
                            "ecaaa1", messageContext);


        } catch (SQLException e) {
            log.error("SQL Exception occurred while resetting metered usage for sub-account: {}", userName, e.getMessage());
            return generateResetMeterUsageForSubAcctNameSOAP11ExceptionResponse
                    ("SQLException",
                            "SQLException",
                            "SQL Exception",
                            "ecaaa1", messageContext);
        } catch (Exception e) {
            log.error("Exception occurred while resetting metered usage for sub-account: {}", userName, e.getMessage());
            return generateResetMeterUsageForSubAcctNameSOAP11ExceptionResponse
                    ("Exception",
                            "Exception",
                            "Exception",
                            "ecaaa1", messageContext);
        } finally {
            log.info("End Method: resetMeteredUsageForSubAccName At:{}MS", System.currentTimeMillis() - startTime);
        }

    }

    /**
     * Creates a SOAP 1.1 exception response with detailed fault information.
     *
     * @param exceptionMessage Detailed message about the exception.
     * @param host             Hostname where the exception occurred.
     * @param messageContext   Optional MessageContext to update with the generated response.
     * @return DOMSource       SOAP exception response as a DOMSource.
     * @throws SOAPException If an error occurs during message creation.
     */
    public static DOMSource generateResetMeterUsageForSubAcctNameSOAP11ExceptionResponse(String faultCodeString, String exceptionNameString,
                                                                                         String exceptionMessage, String host, MessageContext
                                                                                                 messageContext) throws SOAPException {
        // Create a SOAP 1.1 Message factory
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        // Remove default SOAP namespaces and declare custom namespaces
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
            header.detachNode(); // Remove the header if it exists
        }

        // Add the Fault element to the SOAP body
        SOAPElement faultElement = body.addChildElement("Fault", "soapenv");

        // Add the fault code to the fault element
        SOAPElement faultcode = faultElement.addChildElement("faultcode");
        faultcode.addTextNode("soapenv:Server." + faultCodeString);

        // Add the faultstring element (typically provides a message, left empty here)
        SOAPElement faultstring = faultElement.addChildElement("faultstring");
        faultstring.addTextNode(""); // You can customize this message if needed

        // Add the Detail element, which contains more specific error information
        SOAPElement detail = faultElement.addChildElement("detail");

        // Add the SQLException reference (this example uses a custom namespace)
        SOAPElement sqlException = detail.addChildElement(exceptionNameString, "ns1", "http://npm.redback.com");
        sqlException.addAttribute(new QName("href"), "#id0");

        // Add exception name element with a namespace (usefully indicates the class name of the exception)
        SOAPElement exceptionName = detail.addChildElement("exceptionName", "ns2", "http://xml.apache.org/axis/");
        exceptionName.addTextNode("com.redback.npm." + exceptionNameString);

        // Add hostname element indicating where the exception occurred
        SOAPElement hostname = detail.addChildElement("hostname", "ns3", "http://xml.apache.org/axis/");
        hostname.addTextNode(host);

        // Add a multiRef element (for serialization or further references)
        SOAPElement multiRefElement = body.addChildElement("multiRef");

        // Set attributes for multiRef to indicate a specific type and reference
        multiRefElement.setAttribute("id", "id0");
        multiRefElement.setAttributeNS("http://schemas.xmlsoap.org/soap/encoding/", "soapenc:root", "0");
        multiRefElement.setAttributeNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/");
        multiRefElement.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:type", "ns4:" + exceptionNameString);

        // Add required namespaces for multiRef
        multiRefElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:soapenc", "http://schemas.xmlsoap.org/soap/encoding/");
        multiRefElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns4", "http://npm.redback.com");

        // Add the exception message element with the exception message
        SOAPElement message = multiRefElement.addChildElement("message");
        message.addAttribute(new QName("http://www.w3.org/2001/XMLSchema-instance", "type", "xsi"), "xsd:string");
        message.addTextNode(exceptionMessage);

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

    /**
     * Generates a SOAP 1.1 success response with a custom child element in the SOAP body.
     *
     * @param messageContext Optional MessageContext to update with the generated SOAP message.
     * @return DOMSource       SOAP success response as a DOMSource.
     * @throws SOAPException If an error occurs during SOAP message creation.
     */
    DOMSource generateResetMeterUsageForSubAcctNameSOAP11SuccessResponse(String setFaultChildName, MessageContext messageContext) throws SOAPException {
        // Create a SOAP 1.1 Message factory
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        // Remove default SOAP namespaces and declare custom namespaces
        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
        envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");

        envelope.setPrefix("soapenv");

        // Set the Body and Header
        SOAPBody body = envelope.getBody();
        body.setPrefix("soapenv");
        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode(); // Remove the header if any
        }

        // Add Fault Element
        SOAPElement childElement = body.addChildElement(setFaultChildName, "ns1", "http://npm.redback.com");
        childElement.setAttributeNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/");

        // Save changes to the SOAP message
        soapMessage.saveChanges();

        // If the MessageContext is provided, update the response
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

        // Return the response as a DOMSource
        return new DOMSource(fragment);
    }

}
