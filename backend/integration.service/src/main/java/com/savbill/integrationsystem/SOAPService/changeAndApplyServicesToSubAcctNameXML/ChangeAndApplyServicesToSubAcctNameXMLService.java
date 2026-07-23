package com.savbill.integrationsystem.SOAPService.changeAndApplyServicesToSubAcctNameXML;

import com.savbill.integrationsystem.SOAPService.CustomResponseGeneratorHandler.CustomResponseGenerator;
import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.SOAPService.service.ChangeServService;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.generated.changeandapplyservicestosubacctnamexml.ChangeAndApplyServicesToSubAcctNameXML;
import com.savbill.integrationsystem.generated.changeandapplyservicestosubacctnamexml.GetChangeAndApplyServicesToSubAcctNameXMLResponse;
import com.savbill.integrationsystem.generated.changeservice.WsChangeServiceResponse;
import com.savbill.integrationsystem.generated.updatesubscriberaccountxml.UpdateSubscriberAccountXML;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.SOAPException;
import javax.xml.transform.dom.DOMSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.*;

import static com.savbill.integrationsystem.SOAPService.AddSubscriberAccountXML.AddSubscriberAccountXMLSoapService.generateSuccessResponse;

@Slf4j
@Service
public class ChangeAndApplyServicesToSubAcctNameXMLService {
    @Autowired
    private ChangeServService changeServService;
    @Autowired
    private CmsClientService cmsClientService;
    @Autowired
    private JwtUtil jwtUtil;

    public DOMSource applyServicesToSubAcct(ChangeAndApplyServicesToSubAcctNameXML request, MessageContext messageContext) throws SOAPException, IOException {
        long startTime = System.currentTimeMillis();
        log.info("Starting Method: applyServicesToSubAcct AT:{}, username: {}", new Date(startTime),request.getString1());

        try {
            GetChangeAndApplyServicesToSubAcctNameXMLResponse resp = new GetChangeAndApplyServicesToSubAcctNameXMLResponse();
            ServiceSubscriptions serviceSubscriptions = parseXmlToObject(request.getString2());
            String serviceId = serviceSubscriptions.getServiceSubscriptions().get(0).getServiceId();
            String userName = request.getString1().trim();
            List<Override> overridesValue = serviceSubscriptions.getServiceSubscriptions().get(0).getOverrides();
            String token = jwtUtil.generateJwtToken(SoapConstants.MVNOID);
            userName = userName.toLowerCase().trim();
            HashMap<String, Object> response = new HashMap<>();
            Integer responseCode = HttpStatus.EXPECTATION_FAILED.value();
            String responseMessage = SoapConstants.FAILURE;
            if (userName == null || userName.isEmpty()) {
                log.warn("Username validation failed: empty or null value");
                return CustomResponseGenerator.createSOAP11FaultResponse(
                        "generalException",
                        "InvalidSubscriberAccountException",
                        "Input Username is Empty or Null",
                        "ecaaa1",
                        messageContext
                );
            } else if (userName != null && userName.contains(SoapConstants.INVALID_USERNAME)) {
                log.warn("Invalid username detected containing invalid pattern: {}", userName);
                return CustomResponseGenerator.createSOAP11FaultResponse(
                        "generalException",
                        "InvalidSubscriberAccountException",
                        "Invalid Package Configure with Empty or Null OCSCORELATION ID",
                        "ecaaa1",
                        messageContext
                );
            } else if (overridesValue == null || overridesValue.isEmpty()) {
                log.warn("Empty overrides value in XML for username: {}", userName);
                return CustomResponseGenerator.createSOAP11FaultResponse(
                        "generalException",
                        "InvalidSubscriberAccountException",
                        "Input XML String is Empty or Null",
                        "ecaaa1",
                        messageContext
                );
            } else if (serviceId == null || serviceId.isEmpty()) {
                log.warn("Service ID validation failed: empty or null value for username: {}", userName);
                return CustomResponseGenerator.createSOAP11FaultResponse(
                        "generalException",
                        "InvalidServiceSubscriptionException",
                        "Service ID is Empty or Null",
                        "ecaaa1",
                        messageContext
                );
            } else {
              /*  Boolean checkCustomerEntryInCustTBL = changeServService.checkCustomerEntryInCustTBL(userName);
                if (!checkCustomerEntryInCustTBL) {
                    return CustomResponseGenerator.createSOAP11FaultResponse(
                            "generalException",
                            "InvalidSubscriberAccountException",
                            "No Records Found for Given Username.",
                            "ecaaa1",
                            messageContext
                    );
                }*/
//                if (checkCustomerEntryInCustTBL) {
               /*     Boolean usageExists = changeServService.checkCustEntryInUsageQuota(userName);
                    if (!usageExists) {
                        return CustomResponseGenerator.createSOAP11FaultResponse(
                                "generalException",
                                "InvalidSubscriberAccountException",
                                "User Details not found in Usages table for Quota Update for Given Username.",
                                "ecaaa1",
                                messageContext
                        );
                    }*/
//                    else {
                request.setString1(request.getString1().trim());
                ChangeServiceSubRequest changeServiceSubRequest = new ChangeServiceSubRequest(request, serviceSubscriptions);
                try {
                    log.debug("Initiating service change for username: {} with serviceId: {}", userName, serviceId);
                    ResponseEntity<?> responseEntity = cmsClientService.changeSubService(changeServiceSubRequest, SoapConstants.MVNOID, token);
                    log.debug("Integration Received Response IN:{}MS response:{}", System.currentTimeMillis() - startTime, responseEntity.getBody());

                    if (((Map<String, Object>) responseEntity.getBody()).get("responseCode") != null) {
                        if ((Integer) ((Map<String, Object>) responseEntity.getBody()).get("responseCode") == 204) {
                            if (((Map<String, Object>) responseEntity.getBody()).get("msg").toString().equalsIgnoreCase("QuotaDtls Not found")) {
                                log.warn("Customer QuotaDtls Not Found In System:{}", serviceId);
                                return CustomResponseGenerator.createSOAP11FaultResponse(
                                        "generalException",
                                        "InvalidSubscriberAccountException",
                                        "User Details not found in Usages table for Quota Update for Given Username.",
                                        "ecaaa1",
                                        messageContext
                                );
                            } else if (((Map<String, Object>) responseEntity.getBody()).get("msg").toString().equalsIgnoreCase("customer not found")) {
                                log.warn("Customer Not Found In System:{}", userName);
                                return CustomResponseGenerator.createSOAP11FaultResponse(
                                        "generalException",
                                        "InvalidSubscriberAccountException",
                                        "No Records Found for Given Username.",
                                        "ecaaa1",
                                        messageContext
                                );
                            }
                        }
                    } else {

                        Boolean changeServiceValidator = changeServService.changeServiceValidator(responseEntity);
                        log.debug("Integration Received Response IN:{}MS response:{}", System.currentTimeMillis() - startTime, changeServiceValidator);

                        if (changeServiceValidator) {
                            log.info("Successfully changed service for username: {}. Time taken: {}ms",
                                    userName, System.currentTimeMillis() - startTime);
                            return generateSuccessResponse("changeAndApplyServicesToSubAcctNameXMLResponse", messageContext);
                        } else {
                            log.warn("Service change validation failed for username: {} - HSQ=0", userName);
                            return CustomResponseGenerator.createSOAP11FaultResponse(
                                    "generalException",
                                    "InvalidServiceSubscriptionException",
                                    "Service ID is not Configured Properly HSQ = 0",
                                    "ecaaa1",
                                    messageContext
                            );
                        }
                    }
                } catch (FeignException e) {
                    log.debug("FeignException occurred while processing service change for username {}",userName);
                    ObjectMapper objectMapper = new ObjectMapper();
                    String message = "";
                    int status = 404;
                    try {
                        String errorMessage = e.contentUTF8();
                        JsonNode jsonNode = objectMapper.readTree(errorMessage);
                        message = jsonNode.get("msg").asText();
                        status = jsonNode.get("status").asInt();
                        if (Objects.nonNull(message)) {
                            log.warn("Service ID is not Configured in system:{}",serviceId);
                            return CustomResponseGenerator.createSOAP11FaultResponse(
                                    "generalException",
                                    "InvalidSubscriberAccountException",
                                    message,
                                    "ecaaa1",
                                    messageContext
                            );
                        }

                    } catch (JsonProcessingException je) {
                        // Handle specific JSON processing exceptions
                        je.printStackTrace();
                        log.error("Error processing JSON response for username {}: {}", userName, je.getMessage(), je);
                        throw new RuntimeException("Error processing JSON response", je);
                    }
                    e.printStackTrace();
                    return CustomResponseGenerator.createSOAP11FaultResponse(
                            "generalException",
                            "InvalidSubscriberAccountException",
                            message,
                            "ecaaa1",
                            messageContext
                    );
                } catch (Exception e) {
                    log.error("Unexpected error during service change for username {}: {}", userName, e.getMessage(), e);
                    return CustomResponseGenerator.createSOAP11FaultResponse(
                            "generalException",
                            "InvalidServiceSubscriptionException",
                            "Service ID is not Configured in system",
                            "ecaaa1",
                            messageContext
                    );
                }
//                    }
//                }
               /* else {
                    return CustomResponseGenerator.createSOAP11FaultResponse(
                            "generalException",
                            "InvalidSubscriberAccountException",
                            "Usages Details Not Found in Usages Table",
                            "ecaaa1",
                            messageContext
                    );
                }*/
            }
        } catch (JAXBException e) {
            e.printStackTrace();
            log.error("JAXBException while parsing XML for quota provision: {}", e.getMessage());
            return CustomResponseGenerator.createSOAP11FaultResponse(
                    "generalException",
                    "InvalidServiceException",
                    "Invalid Input XML String - Quota Provision",
                    "ecaaa1",
                    messageContext
            );
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("SQLException during package change: {}", e.getMessage());
            return CustomResponseGenerator.createSOAP11FaultResponse(
                    "generalException",
                    "InvalidServiceException",
                    "Invalid Input XML String - Change Package",
                    "ecaaa1",
                    messageContext
            );
        } catch (RemoteException e) {
            e.printStackTrace();
            log.error("RemoteException during service operation: {}", e.getMessage());
            return CustomResponseGenerator.createSOAP11FaultResponse(
                    "generalException",
                    "RemoteException",
                    "SubscriberProfileWebServiceException Exception due to technical issue",
                    "ecaaa1",
                    messageContext
            );
        } catch (Exception e) {
            log.error("Unexpected exception during service operation: {}", e.getMessage(), e);
            return CustomResponseGenerator.createSOAP11FaultResponse(
                    "generalException",
                    "InvalidServiceException",
                    "Invalid Package Configured",
                    "ecaaa1",
                    messageContext
            );
        } finally {
            log.info("Completed ChangeAndApplyServicesToSubAcct execution in {}ms", System.currentTimeMillis() - startTime);
        }
        return null;
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
            Node node = serviceSubscriptionNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                ServiceSubscription subscription = new ServiceSubscription();
                subscription.setServiceId(getTagValue("ServiceId", element).trim());

                // Get overrides
                NodeList overrideNodes = element.getElementsByTagName("Override");
                List<Override> overrides = new ArrayList<>();
                for (int j = 0; j < overrideNodes.getLength(); j++) {
                    Node overrideNode = overrideNodes.item(j);
                    if (overrideNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element overrideElement = (Element) overrideNode;
                        Override override = new Override();
                        override.setName(overrideElement.getAttribute("name"));
                        override.setValue(overrideElement.getTextContent());
                        overrides.add(override);
                    }
                }
                subscription.setOverrides(overrides);

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
}