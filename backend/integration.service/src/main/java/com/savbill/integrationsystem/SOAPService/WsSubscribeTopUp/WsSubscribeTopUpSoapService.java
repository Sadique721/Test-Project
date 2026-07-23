package com.savbill.integrationsystem.SOAPService.WsSubscribeTopUp;

import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.generated.newwssubscribetopup.SubscriptionState;
import com.savbill.integrationsystem.generated.newwssubscribetopup.TopUpSubscriptionData;
import com.savbill.integrationsystem.generated.newwssubscribetopup.TopUpSubscriptionResponse;
import com.savbill.integrationsystem.generated.wssubscribeaddon.WsSubscribeAddOn;
import com.savbill.integrationsystem.generated.wssubscribeaddon.WsSubscribeAddOnResponse;
import com.savbill.integrationsystem.generated.wssubscribetopup.WsSubscribeTopUp;
import com.savbill.integrationsystem.generated.wssubscribetopup.WsSubscribeTopUpResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.savbill.integrationsystem.SOAPService.CustomResponseGeneratorHandler.CustomResponseGenerator.getSafeNumber;
import static com.savbill.integrationsystem.SOAPService.CustomResponseGeneratorHandler.CustomResponseGenerator.getSafeText;

@Slf4j
@Service
public class WsSubscribeTopUpSoapService {
    @Autowired
    CmsClientService cmsClientService;
    @Autowired
    JwtUtil jwtUtil;

    public com.savbill.integrationsystem.generated.newwssubscribetopup.WsSubscribeTopUpResponse handleSubscribeTopUpRequest(WsSubscribeTopUp request, MessageContext messageContext) throws SOAPException, IOException {
        DOMSource response = null;
        String responseMessage = SoapConstants.FAILURE;
        com.savbill.integrationsystem.generated.newwssubscribetopup.WsSubscribeTopUpResponse wsSubscribeTopUpResponse = new com.savbill.integrationsystem.generated.newwssubscribetopup.WsSubscribeTopUpResponse();
        TopUpSubscriptionResponse topUpSubscriptionResponse = new TopUpSubscriptionResponse();
        TopUpSubscriptionData topUpSubscriptionData = new TopUpSubscriptionData();
        int responseCode = SoapConstants.INTERNAL_ERROR;
        long startTime = System.currentTimeMillis();
        log.info("Started handleSubscribeTopUpRequest AT:{}", new Date(startTime));
        try {
            Long mvnoId = SoapConstants.MVNOID;
            String token = jwtUtil.generateJwtToken(mvnoId);
            if (request.getSubscriberId() == null || request.getSubscriberId().isEmpty()) {
                log.warn("Input SubscriberID Is Null or Empty");
                responseCode = SoapConstants.EMPTY;
                responseMessage = "INPUT PARAMETER MISSING. Reason: Identity parameter missing";
                topUpSubscriptionResponse.setResponseCode(responseCode);
                topUpSubscriptionResponse.setResponseMessage(responseMessage);
                wsSubscribeTopUpResponse.setReturn(topUpSubscriptionResponse);
                return wsSubscribeTopUpResponse;
            }
            if (request.getTopUpPackageName() == null || request.getTopUpPackageName().isEmpty()) {
                log.warn("Input TopUpPackageName Is Null or Empty");
                responseCode = SoapConstants.EMPTY;
                responseMessage = "TopUp package id or name must be provided";
                topUpSubscriptionResponse.setResponseCode(responseCode);
                topUpSubscriptionResponse.setResponseMessage(responseMessage);
                wsSubscribeTopUpResponse.setReturn(topUpSubscriptionResponse);
                return wsSubscribeTopUpResponse;
            }
            log.info("Calling CMS client service for subscribe top-up request for Account: {}", request);
            GenericDataDTO responseEntity = cmsClientService.wsSubscribeTopUp(request, mvnoId, token);
            log.info("Integration Received Response IN:{}MS ms for Response: {}", (System.currentTimeMillis() - startTime), responseEntity);

//            Map<String,Object> responsedata = (Map<String, Object>) responseEntity.getData();
//            if (Objects.nonNull(responsedata)){
//                LocalDate localStartDate = (request.getStartTime() == 0)
//                        ? LocalDate.now() // Default to current date
//                        : Instant.ofEpochMilli(request.getStartTime())
//                        .atZone(ZoneId.systemDefault())
//                        .toLocalDate();
//                if (localStartDate.equals(LocalDate.now())){
//                    responsedata.
//                }
//
//            }

            if (responseEntity.getResponseMessage().equalsIgnoreCase("Customer not available")) {
                log.warn("Customer not available for Account: {}", request.getSubscriberId());
                responseCode = SoapConstants.NOT_FOUND;
                responseMessage =  "NOT FOUND. Reason: Subscriber not found";;
                topUpSubscriptionResponse.setResponseCode(responseCode);
                topUpSubscriptionResponse.setResponseMessage(responseMessage);
                wsSubscribeTopUpResponse.setReturn(topUpSubscriptionResponse);
                return wsSubscribeTopUpResponse;

            } else if (responseEntity.getResponseMessage().equalsIgnoreCase("Please select today's date or future date")) {
                responseCode = SoapConstants.INTERNAL_ERROR;
                responseMessage = "INVALID INPUT PARAMETER. Reason:Unable to subscribe package(" + request.getTopUpPackageName() + ") for subscriber ID: " + request.getSubscriberId() + " Reason: End time is less or equal to current time";
                topUpSubscriptionResponse.setResponseCode(responseCode);
                topUpSubscriptionResponse.setResponseMessage(responseMessage);
                wsSubscribeTopUpResponse.setReturn(topUpSubscriptionResponse);
                log.warn("Please select today's date or future date");
                return wsSubscribeTopUpResponse;
            } else if (responseEntity.getResponseMessage().equalsIgnoreCase("planData not available")) {
                log.warn("Input plan Is Not Active:{}", request.getTopUpPackageName());
                responseCode = SoapConstants.BAD_REQUEST;
                responseMessage = "ACTIVE topUp not found with name: " + request.getTopUpPackageName();
                topUpSubscriptionResponse.setResponseCode(responseCode);
                topUpSubscriptionResponse.setResponseMessage(responseMessage);
                wsSubscribeTopUpResponse.setReturn(topUpSubscriptionResponse);
                return wsSubscribeTopUpResponse;
            }else if (responseEntity.getResponseMessage().equalsIgnoreCase("AddOnPackageName and AddOnPackageId not match")) {
                log.warn("AddOnPackageName and AddOnPackageId not match");
                responseCode = SoapConstants.BAD_REQUEST;
                responseMessage = "AddOnPackageName: " + request.getTopUpPackageName() + "  and AddOnPackageId: " + request.getTopUpPackageId() + " do not match";
                topUpSubscriptionResponse.setResponseCode(responseCode);
                topUpSubscriptionResponse.setResponseMessage(responseMessage);
                wsSubscribeTopUpResponse.setReturn(topUpSubscriptionResponse);
                return wsSubscribeTopUpResponse;
//                response = getExceptionInResponse(responseCode, responseMessage,messageContext);
            }else if (responseEntity.getResponseMessage().equalsIgnoreCase("Expiry date can not be less than start date!")) {
                log.info("Expiry date can not be less than start date: {}", responseEntity);
                responseCode = SoapConstants.INPUT_MISSING_CODE;
                responseMessage = "INVALID INPUT PARAMETER. Reason:Unable to subscribe package(" + request.getTopUpPackageName() + ") for subscriber ID: " + request.getSubscriberId() + " Reason: End time is less or equal to current time";
                topUpSubscriptionResponse.setResponseCode(responseCode);
                topUpSubscriptionResponse.setResponseMessage(responseMessage);
                wsSubscribeTopUpResponse.setReturn(topUpSubscriptionResponse);
                return wsSubscribeTopUpResponse;
            } else if (responseEntity.getResponseMessage().equalsIgnoreCase("OK")) {
                log.info("Top-up successfully processed for Account: {}", responseEntity);
                responseCode = SoapConstants.SUCCESS_CODE;
                responseMessage = SoapConstants.SUCCESS;
                return generateSubscriberTopUpSuccessResponse(responseEntity, request);
//                 return generateSubscriberTopUpSuccessResponse(responseEntity,messageContext, request);
//                return generateSuccessResponse(responseCode,responseMessage,responseEntity);
            }

        } catch (Exception e) {
            log.error("Error processing subscribe top-up request: {}", e.getMessage());
            topUpSubscriptionResponse.setResponseCode(responseCode);
            topUpSubscriptionResponse.setResponseMessage(responseMessage);
            wsSubscribeTopUpResponse.setReturn(topUpSubscriptionResponse);
            return wsSubscribeTopUpResponse;
        } finally {
            log.info("Method: handleSubscribeTopUpRequest Completed IN:{}MS", System.currentTimeMillis() - startTime);
        }
        return wsSubscribeTopUpResponse;
    }

    /*
        public static DOMSource generateSuccessResponse(Integer responseCode,String responseMessage,GenericDataDTO genericDataDTO) throws SOAPException {
            // Create a SOAP message
            MessageFactory factory = MessageFactory.newInstance();
            SOAPMessage soapMessage = factory.createMessage();
            SOAPPart soapPart = soapMessage.getSOAPPart();
            SOAPEnvelope envelope = soapPart.getEnvelope();

            envelope.removeNamespaceDeclaration("SOAP-ENV");
            envelope.setPrefix("soap");
            envelope.removeNamespaceDeclaration("xsd");
            envelope.removeNamespaceDeclaration("xsi");

            envelope.addNamespaceDeclaration("soap", "http://www.w3.org/2003/05/soap-envelope");

            SOAPBody body = envelope.getBody();

            QName responseQName = new QName("http://subscription.ws.nvsmx.elitecore.com/", "wsSubscribeAddOnResponse", "ns2");
            SOAPElement responseElement = body.addChildElement(responseQName);

            SOAPElement returnElement = responseElement.addChildElement("return");

            returnElement.addChildElement("responseCode").addTextNode(getSafeNumber(responseCode));

            returnElement.addChildElement("responseMessage").addTextNode(getSafeText(responseMessage));
            WsSubscribeTopUpResponse.Return.TopUpSubscriptions response = new WsSubscribeTopUpResponse.Return.TopUpSubscriptions();
            Object data = genericDataDTO.getData();
            if (data instanceof LinkedHashMap) {
                LinkedHashMap<String, Object> dataMap = (LinkedHashMap<String, Object>) data;

                ArrayList<Object> planList = (ArrayList<Object>) dataMap.get("planList");

                if (planList != null && !planList.isEmpty()) {
                    LinkedHashMap<String, Object> lastPlanMap = (LinkedHashMap<String, Object>) planList.get(planList.size() - 1);

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");
                    LocalDateTime localStartDateTime = LocalDateTime.parse(lastPlanMap.get("startDate").toString(), formatter);
                    long startDate = localStartDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                    LocalDateTime localEndDateTime = LocalDateTime.parse(lastPlanMap.get("endDate").toString(), formatter);
                    long endtDate = localEndDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                    Integer planIdInteger = (Integer) lastPlanMap.get("planId");

                    response.setStartTime(startDate);
                    response.setEndTime(endtDate);
                    response.setSubscriberIdentity(String.valueOf(dataMap.get("username")));
                    response.setTopUpId(planIdInteger.longValue());
                    response.setTopUpName((String) lastPlanMap.get("planName"));
                    response.setTopUpStatus((String) lastPlanMap.get("custPlanStatus"));
                    response.setTopUpSubscriptionId(dataMap.get("custPackagId").toString());
                    response.setUsageResetTime(endtDate);

                }
            }
            SOAPElement addOnSubscriptionsElement = returnElement.addChildElement("addOnSubscriptions");

            // Adding each field of the response as a child element
            addOnSubscriptionsElement.addChildElement("startTime").addTextNode(String.valueOf(response.getStartTime()));
            addOnSubscriptionsElement.addChildElement("endTime").addTextNode(String.valueOf(response.getEndTime()));
            addOnSubscriptionsElement.addChildElement("subscriberIdentity").addTextNode(response.getSubscriberIdentity());
            addOnSubscriptionsElement.addChildElement("topUpId").addTextNode(String.valueOf(response.getTopUpId()));
            addOnSubscriptionsElement.addChildElement("topUpName").addTextNode(response.getTopUpName());
            addOnSubscriptionsElement.addChildElement("topUpStatus").addTextNode(response.getTopUpStatus());
            addOnSubscriptionsElement.addChildElement("topUpSubscriptionId").addTextNode(response.getTopUpSubscriptionId());
            addOnSubscriptionsElement.addChildElement("usageResetTime").addTextNode(String.valueOf(response.getUsageResetTime()));

            // Save the changes to the SOAP message
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
    */
    public com.savbill.integrationsystem.generated.newwssubscribetopup.WsSubscribeTopUpResponse generateSubscriberTopUpSuccessResponse(GenericDataDTO responseEntity, WsSubscribeTopUp request) {
        com.savbill.integrationsystem.generated.newwssubscribetopup.WsSubscribeTopUpResponse wsSubscribeTopUpResponse = new com.savbill.integrationsystem.generated.newwssubscribetopup.WsSubscribeTopUpResponse();
        TopUpSubscriptionResponse topUpSubscriptionResponse = new TopUpSubscriptionResponse();
        TopUpSubscriptionData topUpSubscriptionData = new TopUpSubscriptionData();

        Object data = responseEntity.getData();
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

                Integer planIdInteger = (Integer) lastPlanMap.get("planId");

                topUpSubscriptionResponse.setResponseCode(200);
                topUpSubscriptionResponse.setResponseMessage(SoapConstants.SUCCESS);

                topUpSubscriptionData.setStartTime(startDate);
                topUpSubscriptionData.setEndTime(endtDate);
                topUpSubscriptionData.setParameter1(request.getParameter1() != null ? request.getParameter1() : "");
                topUpSubscriptionData.setParameter2(request.getParameter2() != null ? request.getParameter2() : "");
                topUpSubscriptionData.setSubscriberIdentity(String.valueOf(dataMap.get("username")));
                topUpSubscriptionData.setTopUpId(planIdInteger.toString());
                topUpSubscriptionData.setTopUpName((String) lastPlanMap.get("planName"));
                String status = String.valueOf(lastPlanMap.get("custPlanStatus")); // Safer conversion to String
                localStartDateTime = localStartDateTime.truncatedTo(ChronoUnit.HOURS);
                LocalDateTime currentDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);

                if (localStartDateTime.equals(currentDateTime) && isStatusActive(status)) {
                    topUpSubscriptionData.setTopUpStatus(SubscriptionState.STARTED);
                    log.info("Started date: {}, Current date: {}", localStartDateTime, currentDateTime);
                } else {
                    topUpSubscriptionData.setTopUpStatus(SubscriptionState.START_SCHEDULED);
                }
                topUpSubscriptionData.setTopUpSubscriptionId(dataMap.get("custPackagId").toString());
                topUpSubscriptionData.setUsageResetTime(endtDate);

                topUpSubscriptionResponse.getTopUpSubscriptions().add(topUpSubscriptionData);
                wsSubscribeTopUpResponse.setReturn(topUpSubscriptionResponse);

            }
        }

        return wsSubscribeTopUpResponse;
    }

    public static DOMSource getExceptionInResponse(int responseCode, String responseMessage) throws SOAPException {
        // Create a new SOAP message
        MessageFactory factory = MessageFactory.newInstance();
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

        // Create SOAP body
        SOAPBody body = envelope.getBody();

        QName responseQName = new QName("http://subscription.ws.nvsmx.elitecore.com/", "wsSubscribeAddOnResponse", "ns2");
        SOAPElement responseElement = body.addChildElement(responseQName);

        SOAPElement returnElement = responseElement.addChildElement("return");

        returnElement.addChildElement("responseCode").addTextNode(getSafeNumber(responseCode));

        returnElement.addChildElement("responseMessage").addTextNode(getSafeText(responseMessage));

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


    public DOMSource generateSubscriberTopUpSuccessResponse(GenericDataDTO responseEntity, MessageContext messageContext, WsSubscribeTopUp request) throws SOAPException, IOException {

        SOAPMessage soapMessage = createSOAP12SuccessResponse(responseEntity, request);
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

    public SOAPMessage createSOAP12SuccessResponse(GenericDataDTO responseEntity, WsSubscribeTopUp request) throws SOAPException {

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
        SOAPElement responseElement = body.addChildElement("wsSubscribeTopUpResponse", "ns2", "http://subscription.ws.nvsmx.elitecore.com/");
        SOAPElement returnElement = responseElement.addChildElement("return");

        // Add dynamic response code and message
        returnElement.addChildElement("responseCode").addTextNode("200");
        returnElement.addChildElement("responseMessage").addTextNode(SoapConstants.SUCCESS);

        WsSubscribeTopUpResponse.Return.TopUpSubscriptions response = new WsSubscribeTopUpResponse.Return.TopUpSubscriptions();
        Object data = responseEntity.getData();
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
//                LocalDateTime dbEndDate  = LocalDateTime.parse(lastPlanMap.get("dbEndDate").toString(), formatter);
//                long l = localStartDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
//                LocalDateTime  = LocalDateTime.parse(lastPlanMap.get("dbEndDate").toString(), formatter);
//                long endtDate = localEndDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                Integer planIdInteger = (Integer) lastPlanMap.get("planId");

                response.setStartTime(startDate);
                response.setEndTime(endtDate);
                response.setSubscriberIdentity(String.valueOf(dataMap.get("username")));
                response.setTopUpId(planIdInteger.longValue());
                response.setTopUpName((String) lastPlanMap.get("planName"));
                String status = String.valueOf(lastPlanMap.get("custPlanStatus")); // Safer conversion to String
                localStartDateTime = localStartDateTime.truncatedTo(ChronoUnit.HOURS);
                LocalDateTime currentDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);

                if (localStartDateTime.equals(currentDateTime) && isStatusActive(status)) {
                    response.setTopUpStatus("STARTED");
                    log.info("Started date: {}, Current date: {}", localStartDateTime, currentDateTime);
                } else {
                    response.setTopUpStatus("Start Scheduled");
                }
                response.setTopUpSubscriptionId(dataMap.get("custPackagId").toString());
                response.setUsageResetTime(endtDate);

            }
        }
        SOAPElement addOnSubscriptionsElement = returnElement.addChildElement("topUpSubscriptions");

        // Adding each field of the response as a child element
        addOnSubscriptionsElement.addChildElement("endTime").addTextNode(String.valueOf(response.getEndTime()));
        addOnSubscriptionsElement.addChildElement("startTime").addTextNode(String.valueOf(response.getStartTime()));
        addOnSubscriptionsElement.addChildElement("subscriberIdentity").addTextNode(response.getSubscriberIdentity());
        addOnSubscriptionsElement.addChildElement("topUpId").addTextNode(String.valueOf(response.getTopUpId()));
        addOnSubscriptionsElement.addChildElement("topUpName").addTextNode(response.getTopUpName());
        addOnSubscriptionsElement.addChildElement("topUpStatus").addTextNode(response.getTopUpStatus());
        addOnSubscriptionsElement.addChildElement("topUpSubscriptionId").addTextNode(response.getTopUpSubscriptionId());
        addOnSubscriptionsElement.addChildElement("usageResetTime").addTextNode(String.valueOf(response.getUsageResetTime()));


        soapMessage.saveChanges(); // Save changes to the SOAP message

        return soapMessage;
    }

    public String statusValue(Integer status) {
        String statusName = "";
        switch (status.toString()) {
            case "0":
                statusName = "Subscribed";
                break;
            case "1":
                statusName = "Start Scheduled";
                break;
            case "2":
                statusName = "Active";
                break;
            case "3":
                statusName = "Expiry Scheduled";
                break;
            case "4":
                statusName = "Expired";
                break;
            case "5":
                statusName = "Unsubscribed";
                break;
            case "6":
                statusName = "Approval Pending";
                break;
            case "7":
                statusName = "Rejected";
                break;
        }
        return statusName;
    }

    private boolean isStatusActive(String status) {
        return status != null && !status.isEmpty() && status.equalsIgnoreCase("Active");
    }
}
