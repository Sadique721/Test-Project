package com.savbill.integrationsystem.SOAPService.AddSubscriberAccountXML;

import com.savbill.integrationsystem.RestApiService.addSubscriberAccountXML.AddSubscriberAccountXMLDTO;
import com.savbill.integrationsystem.SOAPService.CustomResponseGeneratorHandler.CustomResponseGenerator;
import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.SOAPService.changeAndApplyServicesToSubAcctNameXML.ServiceSubscription;
import com.savbill.integrationsystem.SOAPService.changeAndApplyServicesToSubAcctNameXML.ServiceSubscriptions;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.generated.addsubscriberaccountxml.AddSubscriberAccountXML;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.w3c.dom.*;
import org.w3c.dom.Node;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.*;
import javax.xml.transform.dom.DOMSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.*;

@Slf4j
@Service
public class AddSubscriberAccountXMLSoapService {

    @Autowired
    CmsClientService cmsClientService;
    @Autowired
    JwtUtil jwtUtil;

    @Value("${defaultplan}")
    String plan;
    @Value("${servicearea.name}")
    Long serviceArea;

    public DOMSource handleSubscriberSessionRequest(AddSubscriberAccountXML request, MessageContext messageContext) throws SOAPException, IOException {
        long startTime = System.currentTimeMillis();
        log.info("Starting handleSubscriberSessionRequest for XML request AT:{}", new Date(startTime));

        try {
            Object customerData = null;
//            SubscriberAccount subscriberAccount = parseSubscriberAccount(request.getString1());
            SubscriberAccount subscriberAccount = parseSubscriberAccount(request.getString1());
            Long mvnoId = SoapConstants.MVNOID;
            String token = jwtUtil.generateJwtToken(mvnoId);
            String status = subscriberAccount.getActivated().trim().toLowerCase();
            String locationLockRegex = "0:92=\"\\[\\*[^\\[\\]]+\\*?(,\\*[^\\[\\]]+\\*?)*\\]\"".trim();
            String userName = subscriberAccount.getName().trim();
            String password = subscriberAccount.getPassword().trim();

            log.debug("Validating subscriber account - Username: {}, Status: {}", userName, status);
            if (userName.trim().isEmpty() && subscriberAccount.getActivated().isEmpty()) {
                log.warn("Empty XML string detected");
                return CustomResponseGenerator.createSOAP11FaultResponse(
                        "generalException",
                        "InvalidSubscriberAccountException",
                        "Input XML String is Empty or Null",
                        "ecaaa1",
                        messageContext
                );
            } else if (userName.trim().isEmpty()) {
                log.warn("Empty username detected in XML");
                return CustomResponseGenerator.createSOAP11FaultResponse(
                        "generalException",
                        "InvalidSubscriberAccountException",
                        "UserName is Empty or null in Input XML",
                        "ecaaa1",
                        messageContext
                );
            } else if (password.isEmpty()) {
                log.warn("Empty password detected for username: {}", userName);
                return CustomResponseGenerator.createSOAP11FaultResponse(
                        "generalException",
                        "InvalidSubscriberAccountException",
                        "Password is Empty or null in Input XML",
                        "ecaaa1",
                        messageContext
                );
            } else if (subscriberAccount.getActivated().isEmpty()) {
                log.warn("Empty activation status for username: {}", userName);
                return CustomResponseGenerator.createSOAP11FaultResponse(
                        "generalException",
                        "InvalidSubscriberAccountException",
                        "Activation/Customer Status is Empty or null in Input XML",
                        "ecaaa1",
                        messageContext
                );
            } else if (!status.equals("y") && !status.equals("n") && !status.equals("suspend")) {
                log.warn("Invalid activation status: {} for username: {}", status, userName);
                return CustomResponseGenerator.createSOAP11FaultResponse(
                        "generalException",
                        "InvalidSubscriberAccountException",
                        "Invalid Activation/Customer Status",
                        "ecaaa1",
                        messageContext
                );
            } else if ((!subscriberAccount.locationLock.trim().isEmpty()) && (!subscriberAccount.locationLock.trim().matches(locationLockRegex))) {
                log.warn("Invalid location lock format for username: {}", userName);
                return CustomResponseGenerator.createSOAP11FaultResponse(
                        "generalException",
                        "InvalidServiceSubscriptionException",
                        "Invalid Location Lock Value",
                        "ecaaa1",
                        messageContext
                );
            } else if (!subscriberAccount.getServiceSubscriptions().isEmpty() && subscriberAccount.getServiceSubscriptions().get(0).getServiceId().isEmpty()) {
                log.warn("Empty service ID in subscription for username: {}", userName);
                return CustomResponseGenerator.createSOAP11FaultResponse(
                        "generalException",
                        "InvalidServiceSubscriptionException",
                        "Service ID is Empty or null in Input XML",
                        "ecaaa1",
                        messageContext
                );
            } else {
                log.debug("All validations passed,Call Cms Client proceeding with subscriber account creation for username: {}", userName);
                ResponseEntity<?> responseEntity = cmsClientService.AddSubscriberAcctXML(subscriberAccount, serviceArea, mvnoId, token, plan);
                Object response = responseEntity.getBody();
                log.debug("Integration Received Response IN:{}MS,Response:{}", System.currentTimeMillis() - startTime, response);

                if (response instanceof LinkedHashMap) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map<String, Object> responseMap = (Map<String, Object>) response;
                    customerData = responseMap.get("customer");

                    if (responseMap.containsKey("message") && responseMap.containsValue("username is already exist")) {
                        log.warn("Duplicate username detected: {}", userName);
                        return CustomResponseGenerator.createSOAP11FaultResponse(
                                "generalException",
                                "DuplicateSubscriberAccountException",
                                "UserName is available in SPR table",
                                "ecaaa1",
                                messageContext
                        );
                    }
                    if (responseMap.containsKey("message") && responseMap.containsValue("Service not available")) {
                        log.warn("Service not available for username: {}", userName);
                        return CustomResponseGenerator.createSOAP11FaultResponse(
                                "generalException",
                                "InvalidServiceSubscriptionException",
                                "Service ID is not available in System",
                                "ecaaa1",
                                messageContext
                        );
                    }
                    if (customerData != null) {
                        log.info("Successfully created subscriber account for username: {}. Time taken: {}ms",
                                userName, System.currentTimeMillis() - startTime);
                        return generateSuccessResponse("addSubscriberAccountXMLResponse", messageContext);
                    }
                }
            }
            log.warn("Service ID not available in system for username: {}", userName);
            return CustomResponseGenerator.createSOAP11FaultResponse(
                    "generalException",
                    "InvalidServiceSubscriptionException",
                    "Service ID is not available in System",
                    "ecaaa1",
                    messageContext
            );
        } catch (Exception e) {
            log.error("Error processing subscriber account request: {}", e.getMessage(), e);
            return CustomResponseGenerator.createSOAP11FaultResponse(
                    "generalException",
                    "InvalidSubscriberAccountException",
                    "Input XML String is Empty or Null",
                    "ecaaa1",
                    messageContext
            );
        } finally {
            log.info("Completed handleSubscriberSessionRequest execution in {}ms", System.currentTimeMillis() - startTime);
        }
    }


    public static DOMSource generateSuccessResponse(String localName, MessageContext messageContext) throws SOAPException {
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

        // Set the envelope prefix to "soapenv"
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

    public static SubscriberAccount parseSubscriberAccount(String cdataContent) throws Exception {
        // Use JAXB to parse the SubscriberAccount object
        JAXBContext jaxbContext = JAXBContext.newInstance(SubscriberAccount.class, ServiceSubscriptions.class, ServiceSubscription.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        SubscriberAccount subscriberAccount = (SubscriberAccount) jaxbUnmarshaller.unmarshal(new StringReader(cdataContent));

        // Manually parse and populate the ServiceSubscriptions
        ServiceSubscriptions serviceSubscriptions = parseXmlToObject(cdataContent);
        subscriberAccount.setServiceSubscriptions(serviceSubscriptions.getServiceSubscriptions());

        return subscriberAccount;
    }

    public static ServiceSubscriptions parseXmlToObject(String xmlString) throws Exception {
        // Prepare to parse XML
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputStream is = new ByteArrayInputStream(xmlString.getBytes());
        Document doc = builder.parse(is);

        // Normalize the document to remove extra spaces and ensure it's well-formed
        doc.getDocumentElement().normalize();

        // Get all ServiceSubscription nodes
        NodeList serviceSubscriptionNodes = doc.getElementsByTagName("ServiceSubscription");

        List<ServiceSubscription> serviceSubscriptions = new ArrayList<>();

        // Iterate over each ServiceSubscription node
        for (int i = 0; i < serviceSubscriptionNodes.getLength(); i++) {
            org.w3c.dom.Node node = serviceSubscriptionNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                org.w3c.dom.Element element = (Element) node;

                ServiceSubscription subscription = new ServiceSubscription();
                subscription.setServiceId(getTagValue("ServiceId", element));

                // As there are no "Override" elements in the given XML, skip override handling
                serviceSubscriptions.add(subscription);
            }
        }
        ServiceSubscriptions serviceSubscriptions1 = new ServiceSubscriptions();
        serviceSubscriptions1.setServiceSubscriptions(serviceSubscriptions);
        return serviceSubscriptions1;
    }

    private static String getTagValue(String tag, Element element) {
        NodeList nl = element.getElementsByTagName(tag);
        if (nl.getLength() == 0) return null;
        Node n = nl.item(0);
        return n.getTextContent();
    }

    public GenericDataDTO addSubscriberAccount(AddSubscriberAccountXMLDTO request) throws SOAPException, IOException {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        log.info("AddSubscriber Account Request Received: {}", request.getName());
        try {
            Object customerData = null;
            List<ServiceSubscription> subscriberAccount = request.getServiceSubscriptions();
            String locationLock = request.getLocationLock().trim();
            Long mvnoId = SoapConstants.MVNOID;
            String token = jwtUtil.generateJwtToken(mvnoId);
            String status = request.getActivated().trim().toLowerCase();
            String locationLockRegex = "0:92=\"\\[\\*[^\\[\\]]+\\*?(,\\*[^\\[\\]]+\\*?)*\\]\"".trim();
            String userName = request.getName().trim();
            String password = request.getPassword().trim();

            log.debug("Request received with userName: {}, status: {}, locationLock: {}", userName, status, locationLock);
            if (userName.trim().isEmpty() && status.isEmpty()) {
                log.warn("Both userName and status are empty or null in Input XML");
                genericDataDTO.setResponseCode(SoapConstants.EMPTY);
                genericDataDTO.setResponseMessage("Input XML String is Empty or Null");
                return genericDataDTO;
            } else if (userName.trim().isEmpty()) {
                log.warn("UserName is empty or null.");
                genericDataDTO.setResponseCode(SoapConstants.EMPTY);
                genericDataDTO.setResponseMessage("UserName is Empty or null in Input XML");
                return genericDataDTO;
            } else if (password.isEmpty()) {
                log.warn("Password is empty or null in Input XML");
                genericDataDTO.setResponseCode(SoapConstants.EMPTY);
                genericDataDTO.setResponseMessage("Password is Empty or null in Input XML");
                return genericDataDTO;
            } else if (status.isEmpty()) {
                log.warn("Activation/Customer status is empty or null in Input XML");
                genericDataDTO.setResponseCode(SoapConstants.EMPTY);
                genericDataDTO.setResponseMessage("\"Activation/Customer Status is Empty or null in Input XML");
                return genericDataDTO;
            } else if (!status.equals("y") && !status.equals("n") && !status.equals("suspend")) {
                log.warn("Invalid Activation/Customer Status provided: {}", status);
                genericDataDTO.setResponseCode(SoapConstants.STATUS_INACTIVE_CODE);
                genericDataDTO.setResponseMessage("Invalid Activation/Customer Status");
                return genericDataDTO;
            } else if ((!locationLock.isEmpty()) && (!locationLock.matches(locationLockRegex))) {
                log.warn("Invalid Location Lock value provided: {}", locationLock);
                genericDataDTO.setResponseCode(SoapConstants.VLAN_ID_AND_GEO_LOCATIONDOES_NOT_MATCH_CODE);
                genericDataDTO.setResponseMessage("Invalid Location Lock Value");
                return genericDataDTO;
            }
//            else if (!subscriberAccount.isEmpty() && subscriberAccount.get(0).getServiceId().isEmpty()) {
//                log.warn("Service ID is empty or null in the first ServiceSubscription");
//                genericDataDTO.setResponseCode(SoapConstants.EMPTY);
//                genericDataDTO.setResponseMessage("Service ID is Empty or null in Input XML");
//                return genericDataDTO;
//            }
            else {
                log.debug("Processing SubscriberAccount with username: {}", userName);
                SubscriberAccount subscriberAccountdto = new SubscriberAccount(request);
                log.info("Calling CMS Client Service userName: {}", userName);
                ResponseEntity<?> responseEntity = cmsClientService.AddSubscriberAcctXML(subscriberAccountdto, serviceArea, mvnoId, token, plan);
                Object response = responseEntity.getBody();

                if (response instanceof LinkedHashMap) {
                    log.debug("Response received from CMS Client Service: {}", response);
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map<String, Object> responseMap = (Map<String, Object>) response;
                    customerData = responseMap.get("customer");

                    if (responseMap.containsKey("message") && responseMap.containsValue("username is already exist")) {
                        log.warn("Username already exists: {}", userName);
                        genericDataDTO.setResponseCode(SoapConstants.USER_NOT_AVAILABLE_IN_SPR_TABLE_CODE);
                        genericDataDTO.setResponseMessage(SoapConstants.USERNAME_IS_AVAILABLE);
                        return genericDataDTO;
                    }
                    if (responseMap.containsKey("message") && responseMap.containsValue("Service not available")) {
                        log.warn("Service not available In SPR table : {}", subscriberAccount.get(0).getServiceId());
                        genericDataDTO.setResponseCode(SoapConstants.NOT_PRESENT);
                        genericDataDTO.setResponseMessage(SoapConstants.SERVICE_ID_NOT_AVAILABLE);
                        return genericDataDTO;
                    }
                    if (customerData != null) {
                        log.info("Create Customer data successfully");
                        genericDataDTO.setResponseCode(SoapConstants.SUCCESS_CODE);
                        genericDataDTO.setResponseMessage(SoapConstants.SUCCESS);
                        return genericDataDTO;
                    }
                }
            }

            log.warn("Service ID not available in response");
            genericDataDTO.setResponseCode(SoapConstants.NOT_PRESENT);
            genericDataDTO.setResponseMessage(SoapConstants.SERVICE_ID_NOT_AVAILABLE);
            return genericDataDTO;

        } catch (Exception e) {
            log.error("Exception occurred in addSubscriberAccount method: {}", e.getMessage(), e);
            genericDataDTO.setResponseCode(SoapConstants.EMPTY);
            genericDataDTO.setResponseMessage("Input XML String is Empty or Null");
            return genericDataDTO;
        }
    }

    private GenericDataDTO createErrorResponse(Integer responseCode, String responseMessage) {
        GenericDataDTO response = new GenericDataDTO();
        response.setResponseMessage(responseMessage);
        response.setResponseCode(responseCode);
        return response;
    }

}
