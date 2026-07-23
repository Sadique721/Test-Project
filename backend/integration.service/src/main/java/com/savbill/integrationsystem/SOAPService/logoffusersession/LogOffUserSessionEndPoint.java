package com.savbill.integrationsystem.SOAPService.logoffusersession;

import com.savbill.integrationsystem.SOAPService.GetUserUsageSummary.GetUserSessionresponseDto;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.billgen.repository.CustomerRepository;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.generated.logoffusersession.WsLogoffUserSession;
import com.savbill.integrationsystem.generated.logoffusersessions.WsLogoffUserSessionsResponse;
import com.savbill.integrationsystem.generated.newlogoffusersession.LogoffUserSessionResponse;
import com.savbill.integrationsystem.generated.newlogoffusersession.WsLogoffUserSessionResponse;
import com.savbill.integrationsystem.generated.newlogoffusersessions.LogoffUserSessionsResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import javax.xml.soap.*;
import javax.xml.transform.dom.DOMSource;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;


@Slf4j
@Endpoint
public class LogOffUserSessionEndPoint {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private RadiusClientService radiusClientService;
    @Autowired
    private JwtUtil jwtUtil;

    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI, localPart = "wsLogoffUserSession")
    @ResponsePayload
    public WsLogoffUserSessionResponse getWsLogOffUserSession(@RequestPayload WsLogoffUserSession request, MessageContext messageContext) throws SOAPException, IOException {
        WsLogoffUserSessionResponse wsLogoffUserSessionsResponse = new WsLogoffUserSessionResponse();
        LogoffUserSessionResponse logoffUserSessions = new LogoffUserSessionResponse();
        Integer responsecode = HttpStatus.EXPECTATION_FAILED.value();
        String responseMessage = "FAILURE";
        Boolean result = false;
        Long mvnoId = SoapConstants.MVNOID;
        String token = jwtUtil.generateJwtToken(mvnoId);

        long startTime = System.currentTimeMillis();
        log.info("Starting getWsLogOffUserSession AT:{}", new Date(startTime));

        String ipAddress = request.getIpAddress();
        if (ipAddress == null || ipAddress.isEmpty()) {
            log.warn("IP address validation failed: empty or null value");
            responsecode = SoapConstants.EMPTY;
            responseMessage = "Input UserName is Empty or Null";
            logoffUserSessions.setResponeCode(responsecode);
            logoffUserSessions.setResponseMessage(responseMessage);
            logoffUserSessions.setRequestId(request.getRequestId());
            wsLogoffUserSessionsResponse.setLogoffUserSession(logoffUserSessions);
            return wsLogoffUserSessionsResponse;
        }
        try {
            ipAddress = ipAddress.toLowerCase().trim();
            log.debug("Call Radius Client Fetching user session for IP: {}", ipAddress);
            GenericDataDTO genericDataDTO = radiusClientService.GetUserSessionApi(ipAddress, SoapConstants.MVNOID);
            GetUserSessionresponseDto dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(genericDataDTO.getData()), GetUserSessionresponseDto.class);
            log.debug("Integration Received Response IN:{}MS", System.currentTimeMillis() - startTime);
//            if (genericDataDTO.getData() instanceof Map) {
//                Map<String, Object> dataMessage = (Map<String, Object>) genericDataDTO.getData();
            if (Objects.nonNull(dataMessage)) {
                Long cdrId = dataMessage.getCdrID();
                if (cdrId != null) {
                    log.debug("Call Radius Client To Perform LogOffSubsession Operation for ip:{}", ipAddress);
                    ResponseEntity<?> responseEntity = radiusClientService.logOffUserSession(cdrId, SoapConstants.MVNOID, token);
                    log.debug("Integration Received Response IN:{},response:{}", System.currentTimeMillis() - startTime, responseEntity.getBody());

                    if (responseEntity.getStatusCode().value() == HttpStatus.OK.value()) {
                        log.info("Successfully logged off session for IP: {}", ipAddress);
                        responsecode = SoapConstants.SUCCESS_CODE;
                        responseMessage = "LOGOUT Session successfully";
                        result = true;
                    } else {
                        log.warn("Failed to log off session for IP: {}, status code: {}",
                                ipAddress, responseEntity.getStatusCode().value());
                        responsecode = SoapConstants.SUCCESS_CODE;
                        responseMessage = "LOGOUT not heppend due to some Technical issue";
                        result = false;
                    }
                }
            } else {
                log.warn("No session data found for IP: {}", ipAddress);
                responsecode = SoapConstants.SUCCESS_CODE;
                responseMessage = "LOGOUT not heppend due to some Technical issue";
                result = false;
            }
//            } else {
//                responsecode = SoapConstants.SUCCESS_CODE;
//                responseMessage = "LOGOUT not heppend due to some Technical issue";
//                result = false;
//            }
            logoffUserSessions.setRequestId(request.getRequestId());
            logoffUserSessions.setResponseMessage(responseMessage);
            logoffUserSessions.setResponeCode(responsecode); // Fixed typo: 'responeCode' to 'responseCode'
            logoffUserSessions.setResult(result);
            wsLogoffUserSessionsResponse.setLogoffUserSession(logoffUserSessions);
            return wsLogoffUserSessionsResponse;
        } catch (Exception e) {
            log.error("Error processing logoff request for IP {}: {}", ipAddress, e.getMessage(), e);
            logoffUserSessions.setRequestId(request.getRequestId());
            logoffUserSessions.setResponseMessage("Exception occurred while processing the request");
            logoffUserSessions.setResponeCode(responsecode);
            logoffUserSessions.setResult(result);
            wsLogoffUserSessionsResponse.setLogoffUserSession(logoffUserSessions);
        } finally {
            log.info("Completed getWsLogOffUserSession for IP: {} in {}ms. Response code: {}, Message: {}",
                    ipAddress, System.currentTimeMillis() - startTime, responsecode, responseMessage);
        }
        return wsLogoffUserSessionsResponse;
    }

    /**
     * Generates a SOAP 1.1 success response for the WsLogoffUserSession API, including request details.
     *
     * @param messageContext The message context that holds the response information.
     * @param response       The response object containing details such as requestId, responseCode, etc.
     * @return DOMSource containing the generated SOAP success response.
     * @throws SOAPException If an error occurs while creating the SOAP message.
     */
    public DOMSource generateWsLogoffUserSessionSOAP11SuccessAndExceptionResponse(MessageContext messageContext, WsLogoffUserSessionsResponse response) throws SOAPException {
        // Create the SOAP Message
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        // Remove default namespace (SOAP-ENV) and add required namespaces
        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.addNamespaceDeclaration("soap", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.setPrefix("soap");

        // Create the SOAP body
        SOAPBody body = envelope.getBody();
        body.setPrefix("soap");

        // Remove any existing Fault
        if (body.getFault() != null) {
            body.removeChild(body.getFault());
        }
        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode();
        }

        // Create the wsLogoffUserSessionResponse element
        SOAPElement wsLogoffUserSessionResponse = body.addChildElement("wsLogoffUserSessionResponse", "ns2", "http://api.act.com/");
        wsLogoffUserSessionResponse.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns3", "http://subscription.ws.nvsmx.elitecore.com/");


        // Add the LogoffUserSession element inside wsLogoffUserSessionResponse
        SOAPElement logoffUserSession = wsLogoffUserSessionResponse.addChildElement("LogoffUserSession");

        // Add the requestId, responseCode, responseMessage, and result elements
        SOAPElement requestId = logoffUserSession.addChildElement("requestId");
        requestId.addTextNode(response.getRequestId());  // Placeholder value for requestId

        SOAPElement responseCode = logoffUserSession.addChildElement("responeCode");
        responseCode.addTextNode(String.valueOf(response.getResponeCode()));

        SOAPElement responseMessage = logoffUserSession.addChildElement("responseMessage");
        responseMessage.addTextNode(response.getResponseMessage());

        SOAPElement result = logoffUserSession.addChildElement("result");
        result.addTextNode(String.valueOf(response.isResult()));

        // Save the changes to the SOAP message
        soapMessage.saveChanges();

        // Set the response message in the context
        SaajSoapMessage updateResponse = (SaajSoapMessage) messageContext.getResponse();
        updateResponse.setSaajMessage(soapMessage);
        updateResponse.getSaajMessage().saveChanges();

        // Extract the body content and create a DOMSource for the response
        Document document = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();
        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }

        // Return the SOAP response as a DOMSource
        return new DOMSource(fragment);
    }
}
