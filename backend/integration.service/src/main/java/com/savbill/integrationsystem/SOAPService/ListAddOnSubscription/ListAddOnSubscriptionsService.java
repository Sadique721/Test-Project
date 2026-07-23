package com.savbill.integrationsystem.SOAPService.ListAddOnSubscription;

import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.generated.wslistaddonsubscriptions.*;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ListAddOnSubscriptionsService {

    @Autowired
    private RadiusClientService client;

    public DOMSource dataInDomSource(WsListAddOnSubscriptions request, MessageContext messageContext) throws Exception {
        WsListAddOnSubscriptionsResponse data = getData(request);
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.removeNamespaceDeclaration("env");
        envelope.addNamespaceDeclaration("soap", "http://www.w3.org/2003/05/soap-envelope");
        envelope.setPrefix("soap");
        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode();
        }
        SOAPBody body = envelope.getBody();
        body.setPrefix("soap");
        String localName = "wsListAddOnSubscriptionsResponse";
        SOAPElement addOn = body.addChildElement(new QName("", "ns2:".concat(localName)));
        addOn.setAttribute("xmlns:ns2", "http://subscription.ws.nvsmx.elitecore.com/");
        SOAPElement returnElement = addOn.addChildElement("return");
        // Add response code and message
        List<SubscriptionData> addOnSubscriptions = data.getReturn().getAddOnSubscriptions();
        if (addOnSubscriptions != null) {
            for (SubscriptionData subscriptionData : addOnSubscriptions) {
                SOAPElement addOnSubscription = returnElement.addChildElement("addOnSubscriptions");
                addChildElement(addOnSubscription, "addOnId", subscriptionData.getAddOnId());
                addChildElement(addOnSubscription, "addOnName", subscriptionData.getAddOnName());
                addChildElement(addOnSubscription, "addOnStatus", subscriptionData.getAddOnStatus().value());
                addChildElement(addOnSubscription, "addonSubscriptionId", subscriptionData.getAddonSubscriptionId());
                addChildElement(addOnSubscription, "endTime", String.valueOf(subscriptionData.getEndTime()));
                addChildElement(addOnSubscription, "startTime", String.valueOf(subscriptionData.getStartTime()));
                addChildElement(addOnSubscription, "subscriberIdentity", subscriptionData.getSubscriberIdentity());
                addChildElement(addOnSubscription, "usageResetTime", String.valueOf(subscriptionData.getUsageResetTime()));
            }
            addChildElement(returnElement, "responseCode", String.valueOf(data.getReturn().getResponseCode()));
            addChildElement(returnElement, "responseMessage", data.getReturn().getResponseMessage());
        } else {
            addChildElement(returnElement, "responseCode", String.valueOf(data.getReturn().getResponseCode()));
            addChildElement(returnElement, "responseMessage", data.getReturn().getResponseMessage());
        }

        if (messageContext != null) {
            SaajSoapMessage updateResponse = (SaajSoapMessage) messageContext.getResponse();
            updateResponse.setSaajMessage(soapMessage);
            updateResponse.getSaajMessage().saveChanges();
        }
        Document document = body.getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();
        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }
        return new DOMSource(fragment);
    }


    private void addChildElement(SOAPElement parent, String name, String value) throws SOAPException {
        SOAPElement child = parent.addChildElement(name);
        child.setTextContent(value);
    }


    public WsListAddOnSubscriptionsResponse getData(WsListAddOnSubscriptions request) throws Exception {
        long MethodStartTime = System.currentTimeMillis();
        log.info("ListAddOnSubscriptionsService.getData Started AT:{} ", new Date(MethodStartTime));

        try {
            WsListAddOnSubscriptionsResponse subscriptions = new WsListAddOnSubscriptionsResponse();
            SubscriptionResponse response = new SubscriptionResponse();

            if (request.getSubscriberId() == null || (request.getSubscriberId().trim().equalsIgnoreCase("") || request.getSubscriberId().trim().equalsIgnoreCase("?"))) {
                log.warn("Input SubscriberId Is Null Or Empty");
                response.setResponseCode(404);
                response.setResponseMessage("NOT FOUND");
                subscriptions.setReturn(response);
                return subscriptions;
            }

            log.debug("Call Radius Client: Fetching subscriber account details for subscriberId:{}", request.getSubscriberId());
            List<Object> subscriberAccountDetails = client.GetListAddOnSubscriptions(request.getSubscriberId(), 2L);
            log.debug("Integration Received Response In:{}MS,data:{}", System.currentTimeMillis() - MethodStartTime, subscriberAccountDetails);
//            List<Object> subscriberAccountDetails = new ArrayList<>();
//            subscriberAccountDetails.add(new Object());
//            AddOnSubscriptionListDto addOnSubscriptionListDto = new AddOnSubscriptionListDto();
            if (subscriberAccountDetails != null && !subscriberAccountDetails.isEmpty()) {
                log.info("Found {} add-on subscriptions for subscriber ID: {}", subscriberAccountDetails.size(), request.getSubscriberId());
                response.setResponseCode(200);
                response.setResponseMessage("SUCCESS");

                for (Object obj : subscriberAccountDetails) {
                    if (obj instanceof Map) {
                        Map<String, Object> subscriberAccountDetail = (Map<String, Object>) obj;
                        log.debug("Processing subscription details: {}", subscriberAccountDetail);

                        SubscriptionData data = new SubscriptionData();
                        data.setAddOnId(subscriberAccountDetail.get("addOnId").toString());
                        data.setAddOnName(String.valueOf(subscriberAccountDetail.get("addOnName")));

                        String status = String.valueOf(subscriberAccountDetail.get("addOnStatus"));
                        log.debug("Subscription status: {}", status);

//                            if ("ACTIVE".equalsIgnoreCase(status)) {
//                                data.setAddOnStatus(SubscriptionState.START_SCHEDULED);
//                            }
                        String startDateTimeString = subscriberAccountDetail.get("startTime").toString();
                        LocalDateTime localStartDateTime = LocalDateTime.parse(startDateTimeString);
                        localStartDateTime = localStartDateTime.truncatedTo(ChronoUnit.HOURS);
                        LocalDateTime currentDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
                        if ((localStartDateTime.isBefore(currentDateTime) || localStartDateTime.equals(currentDateTime)) && isStatusActive(status)) {
                            data.setAddOnStatus(SubscriptionState.STARTED);
                            log.info("Started date: {}, Current date: {}", localStartDateTime, currentDateTime);
                        } else {
                            data.setAddOnStatus(SubscriptionState.START_SCHEDULED);
                            log.info("Started date: {}, Current date: {}", localStartDateTime, currentDateTime);
                        }

                        data.setAddonSubscriptionId(String.valueOf(subscriberAccountDetail.get("addonSubscriptionId")));
                        String startTime = String.valueOf(subscriberAccountDetail.get("startTime"));
                        LocalDateTime parse = LocalDateTime.parse(startTime);
                        long epochMillis = parse.atZone(ZoneId.of("Asia/Kolkata")).toInstant().toEpochMilli();
                        data.setStartTime(epochMillis);

                        String endTime = (String) subscriberAccountDetail.get("endTime");
                        LocalDateTime endTime1 = LocalDateTime.parse(endTime);
                        long epochMillis1 = endTime1.atZone(ZoneId.of("Asia/Kolkata")).toInstant().toEpochMilli();

                        data.setEndTime(epochMillis1);
//                            data.setParameter1((String) subscriberAccountDetail.get("parameter1"));
//                            data.setParameter2((String) subscriberAccountDetail.get("parameter2"));
                        data.setSubscriberIdentity((String.valueOf(subscriberAccountDetail.get("subscriberIdentity"))));

                        data.setUsageResetTime(epochMillis1);
                        response.getAddOnSubscriptions().add(data);
                    }
                }


            } else {
                log.warn("No subscriptions found for subscriber ID: {}", request.getSubscriberId());
                response.setResponseCode(404);
                response.setResponseMessage("NOT FOUND");
                subscriptions.setReturn(response);
                return subscriptions;
            }

            subscriptions.setReturn(response);
            log.info("Returning response for subscriber ID: {}", request.getSubscriberId());
            return subscriptions;
        } catch (Exception e) {
            log.error("Exception occurred while processing request: {}", e.getMessage(), e);
            WsListAddOnSubscriptionsResponse subscriptions = new WsListAddOnSubscriptionsResponse();
            SubscriptionResponse data = new SubscriptionResponse();
            data.setResponseCode(404);
            data.setResponseMessage("Exception Occurred: " + e.getMessage());
            subscriptions.setReturn(data);
            return subscriptions;
        } finally {
            log.info("ListAddOnSubscriptionsService.getData Completed IN:{}MS", System.currentTimeMillis() - MethodStartTime);
        }
    }

    private static boolean isStatusActive(String status) {
        return status != null && !status.isEmpty() && status.equalsIgnoreCase("Active");
    }
}
