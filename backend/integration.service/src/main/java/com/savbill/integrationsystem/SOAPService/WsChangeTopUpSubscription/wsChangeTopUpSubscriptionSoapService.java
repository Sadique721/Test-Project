package com.savbill.integrationsystem.SOAPService.WsChangeTopUpSubscription;

import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.generated.newchangetopupsubscription.SubscriptionState;
import com.savbill.integrationsystem.generated.newchangetopupsubscription.TopUpSubscriptionData;
import com.savbill.integrationsystem.generated.newchangetopupsubscription.TopUpSubscriptionResponse;
import com.savbill.integrationsystem.generated.newchangetopupsubscription.WsChangeTopUpSubscriptionResponse;
import com.savbill.integrationsystem.generated.newwschangeaddonsubscription.SubscriptionData;
import com.savbill.integrationsystem.generated.newwschangeaddonsubscription.SubscriptionResponse;
import com.savbill.integrationsystem.generated.newwschangeaddonsubscription.WsChangeAddOnSubscriptionResponse;
import com.savbill.integrationsystem.generated.wschangeaddonsubscription.WsChangeAddOnSubscription;
import com.savbill.integrationsystem.generated.wschangetopupsubscription.WsChangeTopUpSubscription;
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
import java.util.Objects;

@Slf4j
@Service
public class wsChangeTopUpSubscriptionSoapService {

    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    CmsClientService cmsClientService;

    public WsChangeTopUpSubscriptionResponse handleChangeTopUpSubscriptionRequest(WsChangeTopUpSubscription request, MessageContext messageContext) throws Exception {
        long startTime = System.currentTimeMillis();
        log.info("Starting method: handleChangeTopUpSubscriptionRequest At:{}", new Date(startTime));
        WsChangeTopUpSubscriptionSoapResponse responseEntity = null;
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        WsChangeTopUpSubscriptionResponse wsChangeTopUpSubscriptionResponse = new WsChangeTopUpSubscriptionResponse();
        TopUpSubscriptionResponse topUpSubscriptionResponse = new TopUpSubscriptionResponse();
        TopUpSubscriptionData topUpSubscriptionData = new TopUpSubscriptionData();
        if (request.getSubscriberId() == null || request.getSubscriberId().trim().isEmpty()) {

            topUpSubscriptionResponse.setResponseCode(401);
            topUpSubscriptionResponse.setResponseMessage("INPUT PARAMETER MISSING. Reason: Identity parameter missing");
            wsChangeTopUpSubscriptionResponse.setReturn(topUpSubscriptionResponse);
            return wsChangeTopUpSubscriptionResponse;
        }

        if (request.getTopUpSubscriptionId() == null || request.getTopUpSubscriptionId().equals(0)) {
            topUpSubscriptionResponse.setResponseCode(404);
            topUpSubscriptionResponse.setResponseMessage("TopUp subscription id or topUp name not received");
            wsChangeTopUpSubscriptionResponse.setReturn(topUpSubscriptionResponse);
            return wsChangeTopUpSubscriptionResponse;
        }
        Integer responseCode = SoapConstants.INTERNAL_ERROR;

        try {
            Long mvnoId = SoapConstants.MVNOID;

            String token = jwtUtil.generateJwtToken(mvnoId);
            log.debug("Call CMS Client Change Top-Up For Subscriber:{}", request.getSubscriberId());

            genericDataDTO = cmsClientService.changeTopUpSubscription(request, mvnoId, token);
            String responseMessage = genericDataDTO.getResponseMessage();
            log.debug("Integration Received response In:{}MS, Response Data:{}", System.currentTimeMillis() - startTime, genericDataDTO.getResponseMessage());

            if (responseMessage.contains("Plan already expired")) {
                log.warn("Plan already expired for planId:{}", request.getTopUpSubscriptionId());
                log.info("Method handleChangeTopUpSubscriptionRequest completed successfully in {}ms", System.currentTimeMillis() - startTime);
                return createSOAPResponse(false, responseEntity, request, responseMessage, genericDataDTO);
            }
            if (responseMessage.contains("AddOn subcription not found by subscriberId: ")) {
                return createSOAPResponse(false, responseEntity, request, responseMessage, genericDataDTO);
            }
            if (responseMessage.contains("TopUp subcription not found by subscriberId:")) {
                return createSOAPResponse(false, responseEntity, request, responseMessage, genericDataDTO);
            }
            if (responseMessage.contains("TopUp subcription not found by subscriberId And Plan:")) {
                return createSOAPResponse(false, responseEntity, request, responseMessage, genericDataDTO);
            }
            if (Objects.nonNull(genericDataDTO.getData())) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                responseEntity = objectMapper.convertValue(genericDataDTO.getData(), WsChangeTopUpSubscriptionSoapResponse.class);

                if (responseEntity != null) {
                    responseEntity.setTopUpStatus(statusValue(Integer.valueOf(request.getSubscriptionStatusValue())));
                    log.info("Method handleChangeTopUpSubscriptionRequest completed successfully in {}ms", System.currentTimeMillis() - startTime);
                    return createSOAPResponse(true, responseEntity, request, responseMessage, genericDataDTO);
                }
            }
            log.warn("No Result Found from Radius for Subscriber:{}", request.getSubscriberId());
            log.info("Method handleChangeTopUpSubscriptionRequest completed successfully in {}ms", System.currentTimeMillis() - startTime);
            return createSOAPResponse(false, responseEntity, request, responseMessage, genericDataDTO);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error occurred while calling CMS Client for Change Top-Up For Subscriber:{}", request.getSubscriberId(), e);
            log.info("Method handleChangeTopUpSubscriptionRequest completed successfully in {}ms", System.currentTimeMillis() - startTime);
            if(genericDataDTO.getResponseMessage() == null || genericDataDTO.getResponseMessage().isEmpty()){
                return createSOAPResponse(false, responseEntity, request, SoapConstants.FAILURE, genericDataDTO);
            }
            return createSOAPResponse(false, responseEntity, request, genericDataDTO.getResponseMessage(), genericDataDTO);
        }
    }


    public WsChangeTopUpSubscriptionResponse createSOAPResponse(boolean isSuccess, WsChangeTopUpSubscriptionSoapResponse dataMessage, WsChangeTopUpSubscription request, String responseMessage, GenericDataDTO genericDataDTO) throws SOAPException {

        WsChangeTopUpSubscriptionResponse wsChangeTopUpSubscriptionResponse = new WsChangeTopUpSubscriptionResponse();
        TopUpSubscriptionResponse topUpSubscriptionResponse = new TopUpSubscriptionResponse();
        TopUpSubscriptionData topUpSubscriptionData = new TopUpSubscriptionData();

        if (isSuccess) {
            // Populate `addOnSubscriptions` elements
            String startDateTimeString = dataMessage.getEndTime().toString();
            // Parse the string into a LocalDateTime object
            LocalDateTime localStartDateTime = LocalDateTime.parse(startDateTimeString);
            // Convert LocalDateTime to milliseconds since epoch
            long endTime = localStartDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();


            topUpSubscriptionResponse.setResponseCode(200);
            topUpSubscriptionResponse.setResponseMessage(SoapConstants.SUCCESS);
//            topUpSubscriptionResponse.setParameter1(request.getParameter1() != null ? request.getParameter1() : "");
//            topUpSubscriptionResponse.setParameter2(request.getParameter2() != null ? request.getParameter2() : "");
            topUpSubscriptionData.setTopUpId(dataMessage.getTopUpId().toString());
            topUpSubscriptionData.setTopUpName(dataMessage.getTopUpName());
            String status = dataMessage.getTopUpStatus();
            if (isStatusActive(status)) {
                topUpSubscriptionData.setTopUpStatus(com.savbill.integrationsystem.generated.newchangetopupsubscription.SubscriptionState.STARTED);
            } else {
                topUpSubscriptionData.setTopUpStatus(SubscriptionState.UNSUBSCRIBED);
            }
            topUpSubscriptionData.setTopUpSubscriptionId(dataMessage.getTopUpSubscriptionId().toString());
            topUpSubscriptionData.setEndTime(endTime);
            if (request.getParameter1() != null) {
                topUpSubscriptionData.setParameter1(request.getParameter1());
            }
            if (request.getParameter2() != null) {
                topUpSubscriptionData.setParameter2(request.getParameter2());
            }
            topUpSubscriptionData.setSubscriberIdentity(dataMessage.getSubscriberIdentity());
            topUpSubscriptionData.setUsageResetTime(endTime);


            topUpSubscriptionResponse.getTopUpSubscriptions().add(topUpSubscriptionData);
            wsChangeTopUpSubscriptionResponse.setReturn(topUpSubscriptionResponse);

        } else {
            if (responseMessage.contains("Plan already expired")) {
                topUpSubscriptionResponse.setResponseCode(404);
                topUpSubscriptionResponse.setResponseMessage(responseMessage);

            } else if (responseMessage.contains("Invalid subscription status received")) {
                topUpSubscriptionResponse.setResponseCode(404);
                topUpSubscriptionResponse.setResponseMessage("TopUp subcription not found by subscriberId: " + request.getSubscriberId());
            } else if (responseMessage.contains("Username is not available in SPR Table")) {
                topUpSubscriptionResponse.setResponseCode(404);
                topUpSubscriptionResponse.setResponseMessage("TopUp subscription not found with susbcriberId( " + request.getSubscriberId() + ") and subscriptionId(" + request.getTopUpSubscriptionId() + ")");
            } else if (responseMessage.contains("TopUp subcription not found by subscriberId:")) {
                topUpSubscriptionResponse.setResponseCode(400);
                topUpSubscriptionResponse.setResponseMessage("TopUp subcription not found by subscriberId: " + request.getSubscriberId());
            } else if (responseMessage.contains("TopUp subcription not found by subscriberId And Plan:")) {
                topUpSubscriptionResponse.setResponseCode(400);
                topUpSubscriptionResponse.setResponseMessage("TopUp name: " + genericDataDTO.getPlanName() + " from subscription and provided topUp name: " + request.getTopUpName() + " do not match");
            }
            else {
                topUpSubscriptionResponse.setResponseCode(404);
                topUpSubscriptionResponse.setResponseMessage(responseMessage);
            }
            wsChangeTopUpSubscriptionResponse.setReturn(topUpSubscriptionResponse);
        }
        return wsChangeTopUpSubscriptionResponse;
    }


    public DOMSource generateRauthSessionsSOAPResponse(boolean isSuccess, WsChangeTopUpSubscriptionSoapResponse responseEntity, String responseMessage, MessageContext messageContext , Integer responseCode,GenericDataDTO genericDataDTO,WsChangeTopUpSubscription request) throws SOAPException, IOException {

        SOAPMessage soapMessage = createSOAP12SuccessResponse(responseEntity, responseMessage, isSuccess,responseCode,genericDataDTO,request);
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

    public SOAPMessage createSOAP12SuccessResponse(WsChangeTopUpSubscriptionSoapResponse dataMessage, String responseMessage, boolean isSuccess,Integer responseCode , GenericDataDTO genericDataDTO,WsChangeTopUpSubscription request) throws SOAPException {

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
        SOAPElement responseElement = body.addChildElement("wsChangeTopUpSubscriptionResponse", "ns2", "http://subscription.ws.nvsmx.elitecore.com/");
        SOAPElement returnElement = responseElement.addChildElement("return");

        if (isSuccess) {

            returnElement.addChildElement("responseCode").setTextContent("200");
            returnElement.addChildElement("responseMessage").setTextContent("SUCCESS");
            // Success case
            SOAPElement addOnSubscriptions = returnElement.addChildElement("topUpSubscriptions");

//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//            LocalDate localDate = LocalDate.parse(dataMessage.getEndTime().toString(), formatter);
//            LocalDateTime localStartDateTime = localDate.atStartOfDay(); // Set time to 00:00
//            long endTime = localStartDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

            String startDateTimeString = dataMessage.getEndTime().toString();
            // Parse the string into a LocalDateTime object
            LocalDateTime localStartDateTime = LocalDateTime.parse(startDateTimeString);
            // Convert LocalDateTime to milliseconds since epoch
            long endTime = localStartDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();


            // Populate `addOnSubscriptions` elements
            addOnSubscriptions.addChildElement("endTime").setTextContent(String.valueOf(endTime)); // Assuming milliseconds are derived appropriately
            addOnSubscriptions.addChildElement("subscriberIdentity").setTextContent(dataMessage.getSubscriberIdentity());
            addOnSubscriptions.addChildElement("topUpId").setTextContent(dataMessage.getTopUpId().toString());
            addOnSubscriptions.addChildElement("topUpName").setTextContent(dataMessage.getTopUpName());
            addOnSubscriptions.addChildElement("topUpStatus").setTextContent("UNSUBSCRIBED");
            addOnSubscriptions.addChildElement("topUpSubscriptionId").setTextContent(dataMessage.getTopUpSubscriptionId().toString());
            addOnSubscriptions.addChildElement("usageResetTime").setTextContent(String.valueOf(endTime)); // Assuming milliseconds are derived appropriately


        } else if (responseMessage.contains("Plan already expired")) {
            returnElement.addChildElement("responseCode").setTextContent("400");
            returnElement.addChildElement("responseMessage").setTextContent("Plan already expired");
        }
        else if (responseMessage.contains("Username is not available in SPR Table")) {
            returnElement.addChildElement("responseCode").setTextContent("404");
            returnElement.addChildElement("responseMessage").setTextContent("TopUp subscription not found with susbcriberId("+ request.getSubscriberId()+") and subscriptionId("+request.getTopUpSubscriptionId()+")");
        }
        else if(responseMessage.contains("TopUp subcription not found by subscriberId And Plan:")) {
            returnElement.addChildElement("responseCode").setTextContent("400");
            returnElement.addChildElement("responseMessage").setTextContent("TopUp name: " + genericDataDTO.getPlanName() +
            " from subscription and provided topUp name: " + request.getTopUpName() + " do not match");
        }
        else if(responseMessage.contains("TopUp subcription not found by subscriberId:")) {
            returnElement.addChildElement("responseCode").setTextContent("404");
            returnElement.addChildElement("responseMessage").setTextContent("TopUp subcription not found by subscriberId:"+request.getSubscriberId());
        }else if(responseMessage.contains("TopUp subscription id or topUp name not received")) {
            returnElement.addChildElement("responseCode").setTextContent("401");
            returnElement.addChildElement("responseMessage").setTextContent("TopUp subscription id or topUp name not received");
        }else if(responseMessage.contains("INPUT PARAMETER MISSING. Reason: Identity parameter missing")) {
            returnElement.addChildElement("responseCode").setTextContent("401");
            returnElement.addChildElement("responseMessage").setTextContent("INPUT PARAMETER MISSING. Reason: Identity parameter missing");
        }
         else {
            returnElement.addChildElement("responseCode").setTextContent("400");
            returnElement.addChildElement("responseMessage").setTextContent(responseMessage);
        }
        soapMessage.saveChanges(); // Save changes to the SOAP message

        return soapMessage;
    }

    public String statusValue(Integer status) {
        long startTime = System.currentTimeMillis();
        log.info("Starting method: statusValue At:{}", new Date(startTime));
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
        log.info("Method statusValue completed successfully in {}ms",
                System.currentTimeMillis() - startTime);
        return statusName;
    }

    private static boolean isStatusActive(String status) {
        return status != null && !status.isEmpty() && status.equalsIgnoreCase("Active");
    }
}
