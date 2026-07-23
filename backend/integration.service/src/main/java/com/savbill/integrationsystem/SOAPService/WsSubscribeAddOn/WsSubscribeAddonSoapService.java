package com.savbill.integrationsystem.SOAPService.WsSubscribeAddOn;

import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.SOAPService.logOffUserSessions.LogoffUserSessionsEndPoint;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.generated.newwssubscriberaddon.SubscriptionData;
import com.savbill.integrationsystem.generated.newwssubscriberaddon.SubscriptionResponse;
import com.savbill.integrationsystem.generated.newwssubscriberaddon.SubscriptionState;
import com.savbill.integrationsystem.generated.wssubscribeaddon.WsSubscribeAddOn;
import com.savbill.integrationsystem.generated.wssubscribeaddon.WsSubscribeAddOnResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.w3c.dom.*;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.transform.dom.DOMSource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;

import static com.savbill.integrationsystem.SOAPService.CustomResponseGeneratorHandler.CustomResponseGenerator.getSafeNumber;
import static com.savbill.integrationsystem.SOAPService.CustomResponseGeneratorHandler.CustomResponseGenerator.getSafeText;

@Slf4j
@Service
public class WsSubscribeAddonSoapService {

    @Autowired
    CmsClientService cmsClientService;
    @Autowired
    JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(LogoffUserSessionsEndPoint.class);

    public com.savbill.integrationsystem.generated.newwssubscriberaddon.WsSubscribeAddOnResponse handleSubscribeAddonRequest(WsSubscribeAddOn request, MessageContext messageContext) throws SOAPException, IOException {
        long startTime = System.currentTimeMillis();
        log.info("Starting method: handleSubscribeAddonRequest At:{}", new Date(startTime));

        String responseMessage = SoapConstants.FAILURE;
        int responseCode = SoapConstants.INTERNAL_ERROR;
        com.savbill.integrationsystem.generated.newwssubscriberaddon.WsSubscribeAddOnResponse wsSubscribeAddOnResponse = new com.savbill.integrationsystem.generated.newwssubscriberaddon.WsSubscribeAddOnResponse();
        SubscriptionResponse subscriptionResponse = new SubscriptionResponse();
        SubscriptionData subscriptionData = new SubscriptionData();
        if (request.getSubscriberId() == null || request.getSubscriberId().isEmpty()) {
            log.warn("Input SubscriberID IS empty or null");
            responseCode = SoapConstants.EMPTY;
            responseMessage = "INPUT PARAMETER MISSING. Reason: Identity parameter missing";
            subscriptionResponse.setResponseCode(responseCode);
            subscriptionResponse.setResponseMessage(responseMessage);
            wsSubscribeAddOnResponse.setReturn(subscriptionResponse);
            log.info("handleSubscribeAddonRequest Completed In:{}MS", System.currentTimeMillis() - startTime);
            return wsSubscribeAddOnResponse;
//            response = getExceptionInResponse(responseCode, responseMessage,messageContext);
//            return response;
        }
        if (request.getAddOnPackageName() == null || request.getAddOnPackageName().isEmpty()) {
            log.warn("AddonPackegeName Is null Or empty");
            responseCode = SoapConstants.EMPTY;
            responseMessage = "addOn package id or name must be provided";
            subscriptionResponse.setResponseCode(responseCode);
            subscriptionResponse.setResponseMessage(responseMessage);
            wsSubscribeAddOnResponse.setReturn(subscriptionResponse);
            log.info("handleSubscribeAddonRequest Completed In:{}MS", System.currentTimeMillis() - startTime);
            return wsSubscribeAddOnResponse;
//            response = getExceptionInResponse(responseCode, responseMessage,messageContext);
//            return response;
        }
        try {
            Long mvnoId = SoapConstants.MVNOID;
            String token = jwtUtil.generateJwtToken(mvnoId);

            log.debug("Calling CMS client service for addon subscription for Subscriber:{},AddOnPack:{} ", request.getSubscriberId(), request.getAddOnPackageName());
            GenericDataDTO responseEntity = cmsClientService.wsSubscribeAddon(request, mvnoId, token);
            log.debug("Integration Received Response In:{}MS ,responseMessage:{},Response:{}", System.currentTimeMillis() - startTime, responseEntity.getResponseMessage(), responseEntity.getData());


            if (responseEntity.getResponseMessage().equalsIgnoreCase("Customer not available")) {
                log.warn("Customer:{} not available for subscription", request.getSubscriberId());
                responseCode = SoapConstants.NOT_FOUND;
                responseMessage = "NOT FOUND. Reason: Subscriber not found";
                subscriptionResponse.setResponseCode(responseCode);
                subscriptionResponse.setResponseMessage(responseMessage);
                wsSubscribeAddOnResponse.setReturn(subscriptionResponse);
                return wsSubscribeAddOnResponse;
            } else if (responseEntity.getResponseMessage().equalsIgnoreCase("planData not available")) {
                log.warn("Plan data not available for subscription:{}", request.getSubscriberId());
                responseCode = SoapConstants.BAD_REQUEST;
                responseMessage = "ACTIVE addOn not found with name:" + request.getAddOnPackageName();
                subscriptionResponse.setResponseCode(responseCode);
                subscriptionResponse.setResponseMessage(responseMessage);
                wsSubscribeAddOnResponse.setReturn(subscriptionResponse);
                return wsSubscribeAddOnResponse;
//                response = getExceptionInResponse(responseCode, responseMessage,messageContext);
            }else if (responseEntity.getResponseMessage().equalsIgnoreCase("AddOnPackageName and AddOnPackageId not match")) {
                log.warn("AddOnPackageName and AddOnPackageId not match");
                responseCode = SoapConstants.BAD_REQUEST;
                responseMessage = "AddOnPackageName: " + request.getAddOnPackageName() + "  and AddOnPackageId: " + request.getAddOnPackageId() + " do not match";
                subscriptionResponse.setResponseCode(responseCode);
                subscriptionResponse.setResponseMessage(responseMessage);
                wsSubscribeAddOnResponse.setReturn(subscriptionResponse);
                return wsSubscribeAddOnResponse;
//                response = getExceptionInResponse(responseCode, responseMessage,messageContext);
            }
            else if (responseEntity.getResponseMessage().equalsIgnoreCase("Expiry date can not be less than start date!")) {
                log.warn("Invalid date range: expiry date less than start date");
                responseCode = SoapConstants.INPUT_MISSING_CODE;
                responseMessage = "INVALID INPUT PARAMETER. Reason:Unable to subscribe package(" + request.getAddOnPackageName() + ") for subscriber ID: " + request.getSubscriberId() + " Reason: End time is less or equal to current time";
                subscriptionResponse.setResponseCode(responseCode);
                subscriptionResponse.setResponseMessage(responseMessage);
                wsSubscribeAddOnResponse.setReturn(subscriptionResponse);
                return wsSubscribeAddOnResponse;
//                response = getExceptionInResponse(responseCode, responseMessage,messageContext);
            } else if (responseEntity.getResponseMessage().equalsIgnoreCase("Please select today's date or future date")) {
                log.warn("Invalid date selection: past date selected");
                responseCode = SoapConstants.INTERNAL_ERROR;
                responseMessage = SoapConstants.SELECT_DATE;
                subscriptionResponse.setResponseCode(responseCode);
                subscriptionResponse.setResponseMessage(responseMessage);
                wsSubscribeAddOnResponse.setReturn(subscriptionResponse);
                return wsSubscribeAddOnResponse;
//                response = getExceptionInResponse(responseCode, responseMessage,messageContext);
            } else if (responseEntity.getResponseMessage().equalsIgnoreCase("OK")) {
                log.info("Addon subscription successful");
                responseCode = SoapConstants.SUCCESS_CODE;
                responseMessage = SoapConstants.SUCCESS;
                responseEntity.setResponseMessage(responseMessage);
                responseEntity.setResponseCode(responseCode);
//                return generateSuccessResponse(responseEntity,messageContext);
                log.info("Method handleSubscribeAddonRequest completed successfully in {}ms", System.currentTimeMillis() - startTime);
                return generateSuccessResponse(responseEntity, request);
            }

        } catch (Exception e) {
            log.error("Error in handleSubscribeAddonRequest", e);
            responseMessage = SoapConstants.FAILURE;
            responseCode = SoapConstants.INTERNAL_ERROR;
            subscriptionResponse.setResponseCode(responseCode);
            subscriptionResponse.setResponseMessage(responseMessage);
            wsSubscribeAddOnResponse.setReturn(subscriptionResponse);
            return wsSubscribeAddOnResponse;
//            response = getExceptionInResponse(responseCode, responseMessage,messageContext);
        } finally {
            log.info("Method handleSubscribeAddonRequest completed IN{}MS", System.currentTimeMillis() - startTime);
        }
        return wsSubscribeAddOnResponse;
    }

    public com.savbill.integrationsystem.generated.newwssubscriberaddon.WsSubscribeAddOnResponse generateSuccessResponse(GenericDataDTO genericDataDTO, WsSubscribeAddOn request) {
        com.savbill.integrationsystem.generated.newwssubscriberaddon.WsSubscribeAddOnResponse wsSubscribeAddOnResponse = new com.savbill.integrationsystem.generated.newwssubscriberaddon.WsSubscribeAddOnResponse();
        SubscriptionResponse subscriptionResponse = new SubscriptionResponse();
        SubscriptionData subscriptionData = new SubscriptionData();

        Object data = genericDataDTO.getData();
        if (data instanceof LinkedHashMap) {
            LinkedHashMap<String, Object> dataMap = (LinkedHashMap<String, Object>) data;

            ArrayList<Object> planList = (ArrayList<Object>) dataMap.get("planList");

            if (planList != null && !planList.isEmpty()) {
                LinkedHashMap<String, Object> lastPlanMap = (LinkedHashMap<String, Object>) planList.get(planList.size() - 1);

                String startDateTimeString = lastPlanMap.get("dbStartDate").toString();
                LocalDateTime localStartDateTime = LocalDateTime.parse(startDateTimeString);
                long startDate = localStartDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

                String endDateTimeString = lastPlanMap.get("dbEndDate").toString();
                LocalDateTime localEndtDateTime = LocalDateTime.parse(endDateTimeString);
                long endtDate = localEndtDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

                subscriptionResponse.setResponseCode(200);
                subscriptionResponse.setResponseMessage(SoapConstants.SUCCESS);

                subscriptionData.setEndTime(endtDate);
                subscriptionData.setParameter1(request.getParameter1() != null ? request.getParameter1() : "");
                subscriptionData.setParameter2(request.getParameter2() != null ? request.getParameter2() : "");

                subscriptionData.setStartTime(startDate);
                subscriptionData.setSubscriberIdentity(String.valueOf(dataMap.get("username")));
                subscriptionData.setAddOnId(lastPlanMap.get("planId").toString());
                subscriptionData.setAddOnName((String) lastPlanMap.get("planName"));
                localStartDateTime = localStartDateTime.truncatedTo(ChronoUnit.HOURS);
                LocalDateTime currentDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
                String status = lastPlanMap.get("custPlanStatus").toString();
                if (localStartDateTime.equals(currentDateTime) && isStatusActive(status)) {
                    subscriptionData.setAddOnStatus(SubscriptionState.STARTED);
                    log.info("Started date: {}, Current date: {}", localStartDateTime, currentDateTime);
                } else {
                    subscriptionData.setAddOnStatus(SubscriptionState.START_SCHEDULED);
                    log.info("Started date: {}, Current date: {}", localStartDateTime, currentDateTime);
                }
                subscriptionData.setAddOnSubscriptionId(dataMap.get("custPackagId").toString());
                subscriptionData.setUsageResetTime(endtDate);

                subscriptionResponse.getAddOnSubscriptions().add(subscriptionData);
                wsSubscribeAddOnResponse.setReturn(subscriptionResponse);
            }
        }
        return wsSubscribeAddOnResponse;
    }

    public static DOMSource generateSuccessResponse(GenericDataDTO genericDataDTO, MessageContext messageContext) throws SOAPException {
        // Create a SOAP message
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.setPrefix("soap");
        envelope.removeNamespaceDeclaration("xsd");
        envelope.removeNamespaceDeclaration("xsi");

        envelope.addNamespaceDeclaration("soap", "http://www.w3.org/2003/05/soap-envelope");
        envelope.removeNamespaceDeclaration("env");
        SOAPBody body = envelope.getBody();
        body.setPrefix("soap");
        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode();
        }
        QName responseQName = new QName("http://subscription.ws.nvsmx.elitecore.com/", "wsSubscribeAddOnResponse", "ns2");
        SOAPElement responseElement = body.addChildElement(responseQName);

        SOAPElement returnElement = responseElement.addChildElement("return");

        returnElement.addChildElement("responseCode").addTextNode(getSafeNumber(genericDataDTO.getResponseCode()));

        returnElement.addChildElement("responseMessage").addTextNode(getSafeText(genericDataDTO.getResponseMessage()));
        WsSubscribeAddOnResponse.Return.AddOnSubscriptions response = new WsSubscribeAddOnResponse.Return.AddOnSubscriptions();
        Object data = genericDataDTO.getData();
        if (data instanceof LinkedHashMap) {
            LinkedHashMap<String, Object> dataMap = (LinkedHashMap<String, Object>) data;

            ArrayList<Object> planList = (ArrayList<Object>) dataMap.get("planList");

            if (planList != null && !planList.isEmpty()) {
                LinkedHashMap<String, Object> lastPlanMap = (LinkedHashMap<String, Object>) planList.get(planList.size() - 1);

                String startDateTimeString = lastPlanMap.get("dbStartDate").toString();
                // Parse the string into a LocalDateTime object
                LocalDateTime localStartDateTime = LocalDateTime.parse(startDateTimeString);
                // Convert LocalDateTime to milliseconds since epoch
                long startDate = localStartDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

                String endDateTimeString = lastPlanMap.get("dbEndDate").toString();
                // Parse the string into a LocalDateTime object
                LocalDateTime localEndtDateTime = LocalDateTime.parse(endDateTimeString);
                // Convert LocalDateTime to milliseconds since epoch
                long endtDate = localEndtDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
//                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");
//                LocalDateTime localStartDateTime = LocalDateTime.parse(lastPlanMap.get("startDate").toString(), formatter);
//                long startDate = localStartDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
//                LocalDateTime localEndDateTime = LocalDateTime.parse(lastPlanMap.get("endDate").toString(), formatter);
//                long endtDate = localEndDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

                response.setStartTime(String.valueOf(startDate));
                response.setParameter1("");
                response.setParameter2("");
                response.setEndTime(String.valueOf(endtDate));
                response.setSubscriberIdentity(String.valueOf(dataMap.get("username")));
                response.setAddOnId(lastPlanMap.get("planId").toString());
                response.setAddOnName((String) lastPlanMap.get("planName"));
                localStartDateTime = localStartDateTime.truncatedTo(ChronoUnit.HOURS);
                LocalDateTime currentDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
                String status = lastPlanMap.get("custPlanStatus").toString();
                if (localStartDateTime.equals(currentDateTime) && isStatusActive(status)) {
                    response.setAddOnStatus("STARTED");
                    log.info("Started date: {}, Current date: {}", localStartDateTime, currentDateTime);
                } else {
                    response.setAddOnStatus("Start Scheduled");
                    log.info("Started date: {}, Current date: {}", localStartDateTime, currentDateTime);
                }
                response.setAddOnSubscriptionId(dataMap.get("custPackagId").toString());
                response.setUsageResetTime(String.valueOf(endtDate));

            }
        }
        SOAPElement addOnSubscriptionsElement = returnElement.addChildElement("addOnSubscriptions");

        // Adding each field of the response as a child element
        addOnSubscriptionsElement.addChildElement("endTime").addTextNode(response.getEndTime());
        addOnSubscriptionsElement.addChildElement("parameter1").addTextNode(response.getParameter1());
        addOnSubscriptionsElement.addChildElement("parameter2").addTextNode(response.getParameter2());
        addOnSubscriptionsElement.addChildElement("startTime").addTextNode(response.getStartTime());
        addOnSubscriptionsElement.addChildElement("subscriberIdentity").addTextNode(response.getSubscriberIdentity());
        addOnSubscriptionsElement.addChildElement("addOnId").addTextNode(response.getAddOnId());
        addOnSubscriptionsElement.addChildElement("addOnName").addTextNode(response.getAddOnName());
        addOnSubscriptionsElement.addChildElement("addOnStatus").addTextNode(response.getAddOnStatus());
        addOnSubscriptionsElement.addChildElement("addOnSubscriptionId").addTextNode(response.getAddOnSubscriptionId());
        addOnSubscriptionsElement.addChildElement("usageResetTime").addTextNode(response.getUsageResetTime());

        // Save the changes to the SOAP message
        SaajSoapMessage updateResponse = (SaajSoapMessage) messageContext.getResponse();
        updateResponse.setSaajMessage(soapMessage);
        updateResponse.getSaajMessage().saveChanges();
        // Extract the body content as a DOMSource
        Document document = body.getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();
        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }

        // Return the DOMSource containing the SOAP message body
        return new DOMSource(fragment);
    }


    public static DOMSource getExceptionInResponse(int responseCode, String responseMessage, MessageContext messageContext) throws SOAPException {
        // Create a new SOAP message
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        // Remove unnecessary namespaces
        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.setPrefix("soap");
        envelope.removeNamespaceDeclaration("xsd");
        envelope.removeNamespaceDeclaration("xsi");

        // Add namespaces
        envelope.addNamespaceDeclaration("soap", "http://www.w3.org/2003/05/soap-envelope");
        envelope.removeNamespaceDeclaration("env");
        // Create SOAP body
        SOAPBody body = envelope.getBody();
        body.setPrefix("soap");
        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode();
        }
        QName responseQName = new QName("http://subscription.ws.nvsmx.elitecore.com/", "wsSubscribeAddOnResponse", "ns2");
        SOAPElement responseElement = body.addChildElement(responseQName);

        SOAPElement returnElement = responseElement.addChildElement("return");

        returnElement.addChildElement("responseCode").addTextNode(getSafeNumber(responseCode));

        returnElement.addChildElement("responseMessage").addTextNode(getSafeText(responseMessage));
        SaajSoapMessage updateResponse = (SaajSoapMessage) messageContext.getResponse();
        updateResponse.setSaajMessage(soapMessage);
        updateResponse.getSaajMessage().saveChanges();
        soapMessage.saveChanges();

        // Extract the body content as a DOMSource
        Document document = body.getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();
        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }

        // Return the DOMSource containing the SOAP message body
        return new DOMSource(fragment);
    }


    public DOMSource generateRauthSessionsSOAPResponse(GenericDataDTO responseEntity, MessageContext messageContext) throws SOAPException, IOException {

        SOAPMessage soapMessage = createSOAP12SuccessResponse(responseEntity);
        SaajSoapMessage response = (SaajSoapMessage) messageContext.getResponse();
        response.setSaajMessage(soapMessage);
        response.getSaajMessage().saveChanges();

        SOAPBody body = soapMessage.getSOAPPart().getEnvelope().getBody();
        Document document = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();

        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }

        return new DOMSource(fragment);
    }

    public SOAPMessage createSOAP12SuccessResponse(GenericDataDTO responseEntity) throws SOAPException {

        // Create a SOAP Message factory for SOAP 1.2
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPEnvelope envelope = soapMessage.getSOAPPart().getEnvelope();

        envelope.removeNamespaceDeclaration("env");
        envelope.addNamespaceDeclaration("soap", "http://www.w3.org/2003/05/soap-envelope");
        envelope.setPrefix("soap");
        envelope.getBody().setPrefix("soap");

        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode();
        }

        SOAPBody body = envelope.getBody();
//        SOAPElement responseElement = body.addChildElement("wsSubscribeAddOnResponse ", "ns2", "http://subscription.ws.nvsmx.elitecore.com/");
        SOAPElement responseElement = body.addChildElement(new QName("", "ns2:wsSubscribeAddOnResponse"));
        responseElement.addNamespaceDeclaration("ns2", "http://subscription.ws.nvsmx.elitecore.com/");
        SOAPElement returnElement = responseElement.addChildElement("return");

        // Add dynamic response code and message
        returnElement.addChildElement("responseCode").addTextNode("200");
        returnElement.addChildElement("responseMessage").addTextNode(SoapConstants.SUCCESS);

        WsSubscribeAddOnResponse.Return.AddOnSubscriptions response = new WsSubscribeAddOnResponse.Return.AddOnSubscriptions();
        Object data = responseEntity.getData();
        if (data instanceof LinkedHashMap) {
            LinkedHashMap<String, Object> dataMap = (LinkedHashMap<String, Object>) data;

            ArrayList<Object> planList = (ArrayList<Object>) dataMap.get("planList");

            if (planList != null && !planList.isEmpty()) {
                LinkedHashMap<String, Object> lastPlanMap = (LinkedHashMap<String, Object>) planList.get(planList.size() - 1);

                DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                        .parseCaseInsensitive() // Enables case-insensitivity
                        .appendPattern("dd-MM-yyyy hh:mm a")
                        .toFormatter(Locale.ENGLISH);
                LocalDateTime localStartDateTime = LocalDateTime.parse(lastPlanMap.get("startDate").toString(), formatter);
                long startDate = localStartDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                LocalDateTime localEndDateTime = LocalDateTime.parse(lastPlanMap.get("endDate").toString(), formatter);
                long endtDate = localEndDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

                response.setStartTime(String.valueOf(startDate));
                response.setParameter1("");
                response.setParameter2("");
                response.setEndTime(String.valueOf(endtDate));
                response.setSubscriberIdentity(String.valueOf(dataMap.get("username")));
                response.setAddOnId(lastPlanMap.get("planId").toString());
                response.setAddOnName((String) lastPlanMap.get("planName"));
                response.setAddOnStatus((String) lastPlanMap.get("custPlanStatus"));
                response.setAddOnSubscriptionId(dataMap.get("custPackagId").toString());
                response.setUsageResetTime(String.valueOf(endtDate));

            }
        }
        SOAPElement addOnSubscriptionsElement = returnElement.addChildElement("addOnSubscriptions");

        // Adding each field of the response as a child element
        addOnSubscriptionsElement.addChildElement("endTime").addTextNode(response.getEndTime());
        addOnSubscriptionsElement.addChildElement("parameter1").addTextNode(response.getParameter1());
        addOnSubscriptionsElement.addChildElement("parameter2").addTextNode(response.getParameter2());
        addOnSubscriptionsElement.addChildElement("startTime").addTextNode(response.getStartTime());
        addOnSubscriptionsElement.addChildElement("subscriberIdentity").addTextNode(response.getSubscriberIdentity());
        addOnSubscriptionsElement.addChildElement("addOnId").addTextNode(response.getAddOnId());
        addOnSubscriptionsElement.addChildElement("addOnName").addTextNode(response.getAddOnName());
        addOnSubscriptionsElement.addChildElement("addOnStatus").addTextNode("STARTED");
        addOnSubscriptionsElement.addChildElement("addOnSubscriptionId").addTextNode(response.getAddOnSubscriptionId());
        addOnSubscriptionsElement.addChildElement("usageResetTime").addTextNode(response.getUsageResetTime());

        soapMessage.saveChanges(); // Save changes to the SOAP message

        return soapMessage;
    }

    private static boolean isStatusActive(String status) {
        return status != null && !status.isEmpty() && status.equalsIgnoreCase("Active");
    }
}
