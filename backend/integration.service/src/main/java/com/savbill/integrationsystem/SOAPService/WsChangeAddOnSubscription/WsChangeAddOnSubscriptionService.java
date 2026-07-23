package com.savbill.integrationsystem.SOAPService.WsChangeAddOnSubscription;

import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.generated.newwschangeaddonsubscription.SubscriptionData;
import com.savbill.integrationsystem.generated.newwschangeaddonsubscription.SubscriptionResponse;
import com.savbill.integrationsystem.generated.newwschangeaddonsubscription.SubscriptionState;
import com.savbill.integrationsystem.generated.newwschangeaddonsubscription.WsChangeAddOnSubscriptionResponse;
import com.savbill.integrationsystem.generated.wschangeaddonsubscription.WsChangeAddOnSubscription;
import com.savbill.integrationsystem.utility.CommonUtilityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.NodeList;

import javax.xml.soap.*;
import javax.xml.transform.dom.DOMSource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Service
public class WsChangeAddOnSubscriptionService {

    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    CmsClientService cmsClientService;

    @Autowired
    public CommonUtilityService commonUtilityService;

    public WsChangeAddOnSubscriptionResponse handleChangeAddOnSubscriptionRequest(WsChangeAddOnSubscription request, MessageContext messageContext) throws Exception {
        long startTime = System.currentTimeMillis();
        log.info("Starting handleChangeAddOnSubscriptionRequest At: {}", new Date(startTime));
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        com.savbill.integrationsystem.generated.newwschangeaddonsubscription.WsChangeAddOnSubscriptionResponse wsChangeAddOnSubscriptionResponse = new com.savbill.integrationsystem.generated.newwschangeaddonsubscription.WsChangeAddOnSubscriptionResponse();
        SubscriptionResponse subscriptionResponse = new SubscriptionResponse();
        SubscriptionData subscriptionData = new SubscriptionData();
        if ((request.getSubscriberId() == null || request.getSubscriberId().trim().isEmpty())) {
            log.warn("Input SubscriberID Is null or empty");
            subscriptionResponse.setResponseCode(401);
            subscriptionResponse.setResponseMessage("INPUT PARAMETER MISSING. Reason: Identity parameter missing");
            wsChangeAddOnSubscriptionResponse.setReturn(subscriptionResponse);
            return wsChangeAddOnSubscriptionResponse;
        }
        boolean hasAddOnSubscriptionId = (request.getAddOnSubscriptionId() == null || request.getAddOnSubscriptionId().equals(0));
        boolean hasAddOnName = (request.getAddOnName() != null && !request.getAddOnName().trim().isEmpty());
        if (hasAddOnSubscriptionId) {
            log.warn("Input AddOn subscriptionId or AddonName is null or empty");
            subscriptionResponse.setResponseCode(401);
            subscriptionResponse.setResponseMessage("AddOn subscription id or addOn name not received");
            wsChangeAddOnSubscriptionResponse.setReturn(subscriptionResponse);
            return wsChangeAddOnSubscriptionResponse;
        }

        WsChangeAddOnSubscriptionResponseDto responseEntity = null;
        try {
//            WsChangeAddOnSubscriptionResponse wsChangeAddOnSubscriptionResponse = new WsChangeAddOnSubscriptionResponse();
            if (request.getSubscriptionStatusValue() == null) {
                log.warn("SubscriptionStatusValue is null");
                return createSOAPResponse(responseEntity, false, "SubscriptionStatusValue should not null or empty", request, genericDataDTO);
            } else {

                Long mvnoId = SoapConstants.MVNOID;
                String token = jwtUtil.generateJwtToken(mvnoId);
                log.debug("Calling CmsClient changeAddOnSubscription for AddOn Subscription: {}", request.getAddOnName());
                genericDataDTO = cmsClientService.changeAddOnSubscription(request, mvnoId, token);
                log.debug("Integration Received Response In:{}MS From Radius response:{}", System.currentTimeMillis() - startTime, genericDataDTO.getResponseMessage());

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                if (genericDataDTO.getResponseMessage().contains("AddOn subcription not found by subscriberId:")) {
                    log.warn("AddOn subscription not found: {}", genericDataDTO.getResponseMessage());
                    return createSOAPResponse(responseEntity, false, genericDataDTO.getResponseMessage(), request, genericDataDTO);
                } else if (genericDataDTO.getResponseMessage().contains("Plan already expired")) {
                    log.warn("Plan expired error: {}", genericDataDTO.getResponseMessage());
                    return createSOAPResponse(responseEntity, false, genericDataDTO.getResponseMessage(), request, genericDataDTO);

                } else {
                    responseEntity = objectMapper
                            .readValue(
                                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(genericDataDTO.getData()),
                                    WsChangeAddOnSubscriptionResponseDto.class
                            );
                    if (responseEntity != null) {
                        log.info("Successfully processed changeAddOnSubscription request");
                        return createSOAPResponse(responseEntity, true, genericDataDTO.getResponseMessage(), request, genericDataDTO);

                    } else {
                        log.warn("Response entity is null after processing");
                        return createSOAPResponse(responseEntity, false, genericDataDTO.getResponseMessage(), request, genericDataDTO);

                    }
                }
            }
        } catch (Exception e) {
            log.error("Error processing changeAddOnSubscription request", e);
            e.printStackTrace();
            return createSOAPResponse(responseEntity, false, genericDataDTO.getResponseMessage(), request, genericDataDTO);

        } finally {
            long endTime = System.currentTimeMillis();
            log.info("handleChangeAddOnSubscriptionRequest completed IN:{} MS", (endTime - startTime));
        }

    }

    public WsChangeAddOnSubscriptionResponse createSOAPResponse(WsChangeAddOnSubscriptionResponseDto dataMessage, boolean isSuccess, String message, WsChangeAddOnSubscription request, GenericDataDTO genericDataDTO) throws SOAPException {
        com.savbill.integrationsystem.generated.newwschangeaddonsubscription.WsChangeAddOnSubscriptionResponse wsChangeAddOnSubscriptionResponse = new com.savbill.integrationsystem.generated.newwschangeaddonsubscription.WsChangeAddOnSubscriptionResponse();
        SubscriptionResponse subscriptionResponse = new SubscriptionResponse();
        SubscriptionData subscriptionData = new SubscriptionData();

        if (isSuccess) {
            // Populate `addOnSubscriptions` elements
            String startDateTimeString = dataMessage.getEndTime().toString();
            // Parse the string into a LocalDateTime object
            LocalDateTime localStartDateTime = LocalDateTime.parse(startDateTimeString);
            // Convert LocalDateTime to milliseconds since epoch
            long endTime = localStartDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();


            subscriptionData.setAddOnId(dataMessage.getAddOnId().toString());
            subscriptionData.setAddOnName(dataMessage.getAddOnName());
            String status = request.getSubscriptionStatusValue().toString();
            if (isStatusActive(status)) {
                subscriptionData.setAddOnStatus(com.savbill.integrationsystem.generated.newwschangeaddonsubscription.SubscriptionState.STARTED);
            } else {
                subscriptionData.setAddOnStatus(SubscriptionState.UNSUBSCRIBED);
            }
            subscriptionData.setAddonSubscriptionId(dataMessage.getAddonSubscriptionId().toString());
            subscriptionData.setEndTime(endTime);
            if (request.getParameter1() != null) {
                subscriptionData.setParameter1(request.getParameter1());
            }
            if (request.getParameter2() != null) {
                subscriptionData.setParameter2(request.getParameter2());
            }
            subscriptionData.setSubscriberIdentity(dataMessage.getSubscriberIdentity());
            subscriptionData.setUsageResetTime(endTime);


            subscriptionResponse.setResponseCode(200);
            subscriptionResponse.setResponseMessage(SoapConstants.SUCCESS);

            subscriptionResponse.getAddOnSubscriptions().add(subscriptionData);
            wsChangeAddOnSubscriptionResponse.setReturn(subscriptionResponse);

        } else {
            if (message.contains("AddOn subcription not found by subscriberId:")) {
                subscriptionResponse.setResponseCode(400);
                subscriptionResponse.setResponseMessage("AddOn name: " + genericDataDTO.getPlanName() + " from subscription and provided addOn name: " + request.getAddOnName() + " do not match");
            } else if (message.contains("Plan already expired")) {
                subscriptionResponse.setResponseCode(400);
                subscriptionResponse.setResponseMessage(message);
            } else if (message.contains("Invalid subscription status received")) {
                subscriptionResponse.setResponseCode(404);
                subscriptionResponse.setResponseMessage("NOT FOUND. Reason:Unable to update subscription for subscription ID: " + request.getAddOnSubscriptionId() + ". Reason: Active subscription not found with ID: " + request.getAddOnSubscriptionId());
            } else if (message.contains("Username is not available in SPR Table")) {
                subscriptionResponse.setResponseCode(404);
                subscriptionResponse.setResponseMessage("AddOn subscription not found with susbcriberId( " + request.getSubscriberId() + ") and subscriptionId(" + request.getAddOnSubscriptionId() + ")");
                wsChangeAddOnSubscriptionResponse.setReturn(subscriptionResponse);
            } else {
                subscriptionResponse.setResponseCode(400);
                subscriptionResponse.setResponseMessage(message);
            }
            wsChangeAddOnSubscriptionResponse.setReturn(subscriptionResponse);
        }

        return wsChangeAddOnSubscriptionResponse;
    }

    public DOMSource generateRauthSessionsSOAPResponse(boolean isSuccess, WsChangeAddOnSubscriptionResponseDto responseEntity, MessageContext messageContext, String message, WsChangeAddOnSubscription request) throws SOAPException, IOException {

        SOAPMessage soapMessage = createSOAP12SuccessResponse(responseEntity, isSuccess, message, request);
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

    public SOAPMessage createSOAP12SuccessResponse(WsChangeAddOnSubscriptionResponseDto dataMessage, boolean isSuccess, String message, WsChangeAddOnSubscription request) throws SOAPException {

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
        SOAPElement responseElement = body.addChildElement("wsChangeAddOnSubscriptionResponse", "ns2", "http://subscription.ws.nvsmx.elitecore.com/");
        SOAPElement returnElement = responseElement.addChildElement("return");


        if (isSuccess) {
            // Success case
            SOAPElement addOnSubscriptions = returnElement.addChildElement("addOnSubscriptions");


            // Populate `addOnSubscriptions` elements
            String startDateTimeString = dataMessage.getEndTime().toString();
            // Parse the string into a LocalDateTime object
            LocalDateTime localStartDateTime = LocalDateTime.parse(startDateTimeString);
            // Convert LocalDateTime to milliseconds since epoch
            long endTime = localStartDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            addOnSubscriptions.addChildElement("addOnId").setTextContent(dataMessage.getAddOnId().toString());
            addOnSubscriptions.addChildElement("addOnName").setTextContent(dataMessage.getAddOnName());
            addOnSubscriptions.addChildElement("addOnStatus").setTextContent(commonUtilityService.statusValue(request.getSubscriptionStatusValue().intValue()));
            addOnSubscriptions.addChildElement("addonSubscriptionId").setTextContent(dataMessage.getAddonSubscriptionId().toString());
            addOnSubscriptions.addChildElement("endTime").setTextContent(String.valueOf(endTime)); // Assuming milliseconds are derived appropriately
            addOnSubscriptions.addChildElement("parameter1").setTextContent("?");
            addOnSubscriptions.addChildElement("parameter2").setTextContent("?");
            addOnSubscriptions.addChildElement("subscriberIdentity").setTextContent(dataMessage.getSubscriberIdentity());
            addOnSubscriptions.addChildElement("usageResetTime").setTextContent(String.valueOf(endTime)); // Assuming milliseconds are derived appropriately

            returnElement.addChildElement("responseCode").setTextContent("200");
            returnElement.addChildElement("responseMessage").setTextContent("SUCCESS");
        } else {
            if (message.contains("AddOn subcription not found by subscriberId:")) {
                returnElement.addChildElement("responseCode").setTextContent("404");
                returnElement.addChildElement("responseMessage").setTextContent(message);
            } else if (message.contains("Plan already expired")) {
                returnElement.addChildElement("responseCode").setTextContent("400");
                returnElement.addChildElement("responseMessage").setTextContent(message);
            } else {
                returnElement.addChildElement("responseCode").setTextContent("400");
                returnElement.addChildElement("responseMessage").setTextContent(message);
            }
        }

        soapMessage.saveChanges(); // Save changes to the SOAP message

        return soapMessage;
    }

    private static boolean isStatusActive(String status) {
        return status != null && !status.isEmpty() && status.equalsIgnoreCase("Active");
    }

}
