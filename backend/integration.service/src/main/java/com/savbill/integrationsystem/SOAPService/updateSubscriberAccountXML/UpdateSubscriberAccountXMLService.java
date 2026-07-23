package com.savbill.integrationsystem.SOAPService.updateSubscriberAccountXML;


import com.savbill.integrationsystem.RestApiService.updateSubscriberAccountXML.ServiceSubscriptionDTO;
import com.savbill.integrationsystem.RestApiService.updateSubscriberAccountXML.UpdateSubscriberAccountXMLDTO;
import com.savbill.integrationsystem.SOAPService.AddSubscriberAccountXML.SubscriberAccount;
import com.savbill.integrationsystem.SOAPService.CustomResponseGeneratorHandler.CustomResponseGenerator;
import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.SOAPService.service.ChangeServService;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.generated.updatesubscriberaccountxml.UpdateSubscriberAccountXML;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.NodeList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.*;
import javax.xml.transform.dom.DOMSource;
import java.io.IOException;
import java.io.StringReader;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.savbill.integrationsystem.SOAPService.AddSubscriberAccountXML.AddSubscriberAccountXMLSoapService.generateSuccessResponse;

@Slf4j
@Service
public class UpdateSubscriberAccountXMLService {

    @Autowired
    CmsClientService cmsClientService;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    ChangeServService changeServService;

    public DOMSource UpdateSubscriberAccount(UpdateSubscriberAccountXML request, MessageContext messageContext) throws SOAPException, IOException, SQLException {
        long startTime = System.currentTimeMillis();
        log.info("Method UpdateSubscriberAccount Started At:{}", new Date(startTime));
        String userName = request.getString1().trim();

        if (userName == null || userName.isEmpty()) {
            log.warn("UserName is Empty or null in Input XML");
            return CustomResponseGenerator.createSOAP11FaultResponse(
                    "generalException",
                    "InvalidSubscriberAccountException",
                    "UserName is Empty or null in Input XML",
                    "ecaaa1",
                    messageContext
            );
        }
        try {
            Long mvnoId = SoapConstants.MVNOID;
            String token = jwtUtil.generateJwtToken(mvnoId);
            SubscriberAccount subscriberAccount = parseSubscriberAccount(userName);
            String locationLockRegex = "0:92=\"\\[\\*[^\\[\\]]+\\*?(,\\*[^\\[\\]]+\\*?)*\\]\"";
            String status = subscriberAccount.getActivated() != null ? subscriberAccount.getActivated().trim() : "";
            String locationLock = subscriberAccount.getLocationLock();
            String password = subscriberAccount.getPassword();
            if ((status == null || status.trim().isEmpty()) &&
                    (locationLock == null || locationLock.trim().isEmpty()) &&
                    (password == null || password.trim().isEmpty())) {
                log.warn("Input XML String is Empty or Null");
                return CustomResponseGenerator.createSOAP11FaultResponse(
                        "generalException",
                        "InvalidSubscriberAccountException",
                        "Input XML String is Empty or Null",
                        "ecaaa1",
                        messageContext
                );
            }
            if (subscriberAccount.getLocationLock() != null && (!subscriberAccount.getLocationLock().isEmpty()) && (!subscriberAccount.getLocationLock().matches(locationLockRegex))) {
                log.warn("Invalid Location Lock Value: {}", locationLock);
                return CustomResponseGenerator.createSOAP11FaultResponse(
                        "generalException",
                        "InvalidSubscriberAccountException",
                        "Invalid Location Lock Value",
                        "ecaaa1",
                        messageContext
                );
            } else if (subscriberAccount.getName() == null || subscriberAccount.getName().isEmpty()) {
                log.warn("UserName is Empty or null in Input XML");
                return CustomResponseGenerator.createSOAP11FaultResponse(
                        "generalException",
                        "InvalidSubscriberAccountException",
                        "UserName is Empty or null in Input XML",
                        "ecaaa1",
                        messageContext
                );

            } else if ((subscriberAccount.getPassword() == null || subscriberAccount.getPassword().isEmpty()) && (subscriberAccount.getActivated() == null || subscriberAccount.getActivated().isEmpty()) && (subscriberAccount.getLocationLock() == null || subscriberAccount.getLocationLock().isEmpty()) && (subscriberAccount.getServiceSubscriptions() == null || subscriberAccount.getServiceSubscriptions().isEmpty())) {
                log.warn("No Parameters(null, empty or missing) available for update in Input XML");
                return CustomResponseGenerator.createSOAP11FaultResponse(
                        "generalException",
                        "InvalidSubscriberAccountException",
                        "No Parameters(null,empty or missing) available for update in Input XML",
                        "ecaaa1",
                        messageContext
                );
            } else if ((subscriberAccount.getPassword() == null || subscriberAccount.getPassword().isEmpty()) && (subscriberAccount.getActivated() == null || subscriberAccount.getActivated().isEmpty()) && (subscriberAccount.getLocationLock() == null || subscriberAccount.getLocationLock().isEmpty())) {
                log.warn("Input XML String Password,Status,Loacationlock Null or empty");
                return CustomResponseGenerator.createSOAP11FaultResponse(
                        "generalException",
                        "InvalidSubscriberAccountException",
                        "Input XML String is Empty or Null",
                        "ecaaa1",
                        messageContext
                );
            }
           /* else if (subscriberAccount.getActivated() == null || subscriberAccount.getActivated().isEmpty()) {
                return CustomResponseGenerator.createSOAP11FaultResponse(
                        "generalException",
                        "InvalidSubscriberAccountException",
                        "Activation/Customer Status is Empty or null in Input XML",
                        "ecaaa1",
                        messageContext
                );
            }*/
            else if (subscriberAccount.getActivated() != null && !subscriberAccount.getActivated().isEmpty() && !subscriberAccount.getActivated().equalsIgnoreCase("y") && !subscriberAccount.getActivated().equalsIgnoreCase("n") && !subscriberAccount.getActivated().equalsIgnoreCase("suspend")) {
                log.warn("Invalid Activation/Customer Status: {}", status);
                return CustomResponseGenerator.createSOAP11FaultResponse(
                        "generalException",
                        "InvalidSubscriberAccountException",
                        "Invalid Activation/Customer Status",
                        "ecaaa1",
                        messageContext
                );
            }/* else if (subscriberAccount.getServiceSubscriptions() == null || subscriberAccount.getServiceSubscriptions().isEmpty()) {
                return CustomResponseGenerator.createSOAP11FaultResponse(
                        "generalException",
                        "InvalidSubscriberAccountException",
                        "Service ID is Empty or null in Input XML",
                        "ecaaa1",
                        messageContext
                );
            }*/ else {
//                Boolean checkCustomerEntryInCustTBL = changeServService.checkCustomerEntryInCustTBL(subscriberAccount.getName());
//                if (checkCustomerEntryInCustTBL) {
                log.debug("Call Cms Client for update Subsriber:{}", request.getString1());
                ResponseEntity<?> responseEntity = cmsClientService.UpdateSubscriberAccount(subscriberAccount, mvnoId, token);
                Object response = responseEntity.getBody();
                log.debug("Integration Received Response In:{}MS,response:{}", System.currentTimeMillis() - startTime, response);
                if (response instanceof Map) {
                    Map<String, Object> map = (Map<String, Object>) response;
                    String responseCode = map.get("status").toString();
                    if (responseCode.equals("200")) {
                        log.info("Subscriber account updated successfully for username: {}", userName);
                        return generateSuccessResponse("updateSubscriberAccountXMLResponse", messageContext);
                    } else if (responseCode.equals("204")) {
                        log.warn("Username not available in SPR for username: {}", userName);
                        return CustomResponseGenerator.createSOAP11FaultResponse(
                                "generalException",
                                "InvalidSubscriberAccountException",
                                "Username is not available in SPR",
                                "ecaaa1",
                                messageContext
                        );
                    }
                }
//                } else {
//                    return CustomResponseGenerator.createSOAP11FaultResponse(
//                            "generalException",
//                            "InvalidSubscriberAccountException",
//                            "Username is not available in SPR",
//                            "ecaaa1",
//                            messageContext
//                    );
//                }
            }

        } catch (RemoteException e) {
            log.error("RemoteException: Invalid Input XML String", e);
            return CustomResponseGenerator.createSOAP11FaultResponse(
                    "RemoteException",
                    "RemoteException",
                    "AxisFault Exception due to technical issue",
                    "ecaaa1",
                    messageContext
            );
        } catch (RuntimeException e) {
            log.error("RuntimeException: Invalid Input XML String", e);
            return createSOAP11Fault(
                    "userException",
                    "Exception",
                    "java.rmi.RemoteException: Invalid Input XML String",
                    "ecaaa1",
                    messageContext
            );
        } catch (SQLException e) {
            log.error("SQLException: Invalid Input XML String", e);
            return CustomResponseGenerator.createSOAP11FaultResponse(
                    "SQLException",
                    "SQLException",
                    "SQL Exception",
                    "ecaaa1",
                    messageContext
            );
        } catch (Exception e) {
            log.error("Exception: Invalid Input XML String", e);
            return CustomResponseGenerator.createSOAP11FaultResponse(
                    "Exception",
                    "Exception",
                    "Exception",
                    "ecaaa1",
                    messageContext
            );
        } finally {
            long endTime = System.currentTimeMillis();
            log.info("handleSubscriberAccountRequest completed in {} ms for username: {}",endTime - startTime, userName);
        }
        log.error("Falling through to default error response ");
        return CustomResponseGenerator.createSOAP11FaultResponse(
                "Exception",
                "Exception",
                "Exception",
                "ecaaa1",
                messageContext
        );

    }

    public static SubscriberAccount parseSubscriberAccount(String cdataContent) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(SubscriberAccount.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return (SubscriberAccount) jaxbUnmarshaller.unmarshal(new StringReader(cdataContent));
    }


    public static DOMSource createSOAP11Fault(
            String faultCodeString,
            String exceptionNameString,
            String exceptionMessage,
            String host,
            MessageContext messageContext
    ) throws SOAPException, IOException {
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
        faultstring.addTextNode(exceptionMessage); // You can customize this message if needed

        // Add the Detail element, which contains more specific error information
        SOAPElement detail = faultElement.addChildElement("detail");


        // Add hostname element indicating where the exception occurred
        SOAPElement hostname = detail.addChildElement("hostname", "ns1", "http://xml.apache.org/axis/");
        hostname.addTextNode(host);

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

    public GenericDataDTO update(UpdateSubscriberAccountXMLDTO request) {
        String userName = request.getName().trim();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<ServiceSubscriptionDTO> subscriberAccount = request.getServiceSubscriptions();

        String serviceId = subscriberAccount.get(0).getServiceId();
        String locationLock = request.getLocationLock();
        String status = request.getActivated() != null ? request.getActivated().trim() : "";
        String password = request.getPassword();

        log.info("Received update request for username: {}", userName);
        if (userName == null || userName.isEmpty()) {
            log.warn("UserName is Empty or null in Input XML");
            return createErrorResponse(SoapConstants.EMPTY, "UserName is Empty or null in Input XML");
        }
        try {
            Long mvnoId = SoapConstants.MVNOID;
            String token = jwtUtil.generateJwtToken(mvnoId);
            String locationLockRegex = "0:92=\"\\[\\*[^\\[\\]]+\\*?(,\\*[^\\[\\]]+\\*?)*\\]\"";
            if ((status == null || status.trim().isEmpty()) &&
                    (locationLock == null || locationLock.trim().isEmpty()) &&
                    (password == null || password.trim().isEmpty())) {
                log.warn("Input XML String is Empty or Null");
                genericDataDTO.setResponseMessage("Input XML String is Empty or Null");
                genericDataDTO.setResponseCode(SoapConstants.EMPTY);
                return genericDataDTO;
            }
            if (locationLock != null && (!locationLock.isEmpty()) && (!locationLock.matches(locationLockRegex))) {
                log.warn("Invalid Location Lock Value: {}", locationLock);
                genericDataDTO.setResponseMessage("Invalid Location Lock Value");
                genericDataDTO.setResponseCode(SoapConstants.VLAN_ID_AND_GEO_LOCATIONDOES_NOT_MATCH_CODE);
                return genericDataDTO;
            } else if (userName == null || userName.isEmpty()) {
                log.warn("UserName is Empty or null in Input XML");
                genericDataDTO.setResponseMessage("UserName is Empty or null in Input XML");
                genericDataDTO.setResponseCode(SoapConstants.EMPTY);
                return genericDataDTO;
            } else if ((password == null || password.isEmpty())
                    && (password == null || password.isEmpty())
                    && (locationLock == null || locationLock.isEmpty())
                    && (request.getServiceSubscriptions() == null || request.getServiceSubscriptions().isEmpty())) {
                log.warn("No Parameters(null, empty or missing) available for update in Input XML");
                genericDataDTO.setResponseMessage("No Parameters(null, empty or missing) available for update in Input XML");
                genericDataDTO.setResponseCode(SoapConstants.NOT_PRESENT);
                return genericDataDTO;
            } else if ((password == null || password.isEmpty())
                    && (status == null || status.isEmpty())
                    && (locationLock == null || locationLock.isEmpty())) {
                log.warn("Input XML String Password,Status,Loacationlock Null or empty");
                genericDataDTO.setResponseMessage("Input XML String is Empty or Null");
                genericDataDTO.setResponseCode(SoapConstants.EMPTY);
                return genericDataDTO;
            } else if (status != null && !status.isEmpty()
                    && !status.equalsIgnoreCase("y")
                    && !status.equalsIgnoreCase("n")
                    && !status.equalsIgnoreCase("suspend")) {
                log.warn("Invalid Activation/Customer Status: {}", status);
                genericDataDTO.setResponseMessage("Invalid Activation/Customer Status");
                genericDataDTO.setResponseCode(SoapConstants.InvalidActivation);
                return genericDataDTO;
            } else {
                Boolean checkCustomerEntryInCustTBL = changeServService.checkCustomerEntryInCustTBL(userName);
                if (checkCustomerEntryInCustTBL) {
                    log.info("Customer found in CustTBL for username: {}", userName);
                    SubscriberAccount subscriberAccountdto = new SubscriberAccount(request);
                    ResponseEntity<?> responseEntity = cmsClientService.UpdateSubscriberAccount(subscriberAccountdto, mvnoId, token);
                    Object response = responseEntity.getBody();
                    if (response instanceof Map) {
                        Map<String, Object> map = (Map<String, Object>) response;
                        String responseCode = map.get("status").toString();
                        if (responseCode.equals("200")) {
                            log.info("Subscriber account updated successfully for username: {}", userName);
                            genericDataDTO.setResponseMessage(SoapConstants.SUCCESS);
                            genericDataDTO.setResponseCode(SoapConstants.SUCCESS_CODE);
                            return genericDataDTO;
                        } else {
                            log.error("Failed to update subscriber account for username: {}", userName);
                        }
                    }
                } else {
                    log.warn("Username not available in SPR for username: {}", userName);
                    genericDataDTO.setResponseMessage("Username is not available in SPR");
                    genericDataDTO.setResponseCode(SoapConstants.USER_NOT_AVAILABLE_IN_SPR_TABLE_CODE);
                    return genericDataDTO;
                }
            }

        } catch (RemoteException e) {
            log.error("RemoteException: Invalid Input XML String", e);
            genericDataDTO.setResponseMessage("java.rmi.RemoteException: Invalid Input XML String");
            genericDataDTO.setResponseCode(SoapConstants.REMOTE_EXCEPTION_GENERATED_CODE);
            return genericDataDTO;
        } catch (RuntimeException e) {
            log.error("RuntimeException: AxisFault Exception due to technical issue", e);
            genericDataDTO.setResponseMessage("AxisFault Exception due to technical issue");
            genericDataDTO.setResponseCode(SoapConstants.JAXBException);
            return genericDataDTO;
        } catch (SQLException e) {
            log.error("SQLException", e);
            genericDataDTO.setResponseMessage(SoapConstants.SQL_EXCEPTION);
            genericDataDTO.setResponseCode(SoapConstants.SQL_EXCPTION_CODE);
            return genericDataDTO;
        } catch (Exception e) {
            log.error("Exception occurred", e);
            genericDataDTO.setResponseMessage("Exception");
            genericDataDTO.setResponseCode(500);
            return genericDataDTO;
        }
        log.error("Unknown error occurred during subscriber account update");
        genericDataDTO.setResponseMessage("Exception");
        genericDataDTO.setResponseCode(500);
        return genericDataDTO;
    }

    private GenericDataDTO createErrorResponse(Integer responseCode, String responseMessage) {
        GenericDataDTO response = new GenericDataDTO();
        response.setResponseMessage(responseMessage);
        response.setResponseCode(responseCode);
        return response;
    }
}
