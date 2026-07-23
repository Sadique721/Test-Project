package com.savbill.integrationsystem.SOAPService.ListTopUpSubscriptions;

import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.generated.wslisttopupsubscriptions.*;
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
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ListTopUpSubscriptionsService {

    @Autowired
    private RadiusClientService client;


    public DOMSource dataInDomSource(WsListTopUpSubscriptions request, MessageContext messageContext) throws Exception {
        WsListTopUpSubscriptionsResponse data = getData(request);
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
        String localName = "wsListTopUpSubscriptionsResponse";
        SOAPElement topUp = body.addChildElement(new QName("", "ns2:".concat(localName)));
        topUp.setAttribute("xmlns:ns2", "http://subscription.ws.nvsmx.elitecore.com/");
        SOAPElement returnElement = topUp.addChildElement("return");
        // Add response code and message
        List<TopUpSubscriptionData> topUpSubscriptions1 = data.getReturn().getTopUpSubscriptions();
        if (topUpSubscriptions1 != null) {
            addChildElement(returnElement, "responseCode", String.valueOf(data.getReturn().getResponseCode()));
            addChildElement(returnElement, "responseMessage", data.getReturn().getResponseMessage());
            for (TopUpSubscriptionData topUpSubscription : topUpSubscriptions1) {
                SOAPElement topUpSubscriptions = returnElement.addChildElement("topUpSubscriptions");
                addChildElement(topUpSubscriptions, "endTime", String.valueOf(topUpSubscription.getEndTime()));
                addChildElement(topUpSubscriptions, "startTime", String.valueOf(topUpSubscription.getStartTime()));
                addChildElement(topUpSubscriptions, "subscriberIdentity", topUpSubscription.getSubscriberIdentity());
                addChildElement(topUpSubscriptions, "topUpId", topUpSubscription.getTopUpId());
                addChildElement(topUpSubscriptions, "topUpName", topUpSubscription.getTopUpName());
                addChildElement(topUpSubscriptions, "topUpStatus", String.valueOf(topUpSubscription.getTopUpStatus()));
                addChildElement(topUpSubscriptions, "topUpSubscriptionId", topUpSubscription.getTopUpSubscriptionId());
                addChildElement(topUpSubscriptions, "usageResetTime", String.valueOf(topUpSubscription.getUsageResetTime()));
            }
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


    public WsListTopUpSubscriptionsResponse getData(WsListTopUpSubscriptions request) throws Exception {
        long startTime = System.currentTimeMillis();
        log.info("Method: ListTopUpSubscriptionsService.getData started at: {}", new Date(startTime));
        try {
            WsListTopUpSubscriptionsResponse subscriptions = new WsListTopUpSubscriptionsResponse();
            TopUpSubscriptionResponse data = new TopUpSubscriptionResponse();
            if (request.getSubscriberId() == null || (request.getSubscriberId().trim().equalsIgnoreCase("") || request.getSubscriberId().trim().equalsIgnoreCase("?"))) {
                log.warn("Input SubscriberId Is null or empty ");
                data.setResponseCode(400);
                data.setResponseMessage("EMPTY OR NULL SUBSCRIBER_ID");
                subscriptions.setReturn(data);
                return subscriptions;
            }

            //Radius Call
            log.debug("Fetching top-up subscriptions for subscriberId: {}", request.getSubscriberId());
            List<Object> subscriberAccountDetails = client.GetListTopUpSubscriptions(request.getSubscriberId(), 2L);
            log.debug("Integration Received Response In:{}MS,Data:{}", System.currentTimeMillis() - startTime, subscriberAccountDetails);

            TopUpSubscriptionListDto dto = new TopUpSubscriptionListDto();

            // If it's a Map or JSON string, convert it to TopUpSubscriptionListDto
            ArrayList<TopUpSubscriptionListDto> topUpSubscriptionListDtos = new ArrayList<>();
            if (subscriberAccountDetails != null && !subscriberAccountDetails.isEmpty()) {
                log.info("Found {} top-up subscriptions for subscriberId: {}", subscriberAccountDetails.size(), request.getSubscriberId());
                for (Object subscriberAccountDetail : subscriberAccountDetails) {
                    TopUpSubscriptionListDto topUpSubscriptionListDto = new TopUpSubscriptionListDto();
                    if (subscriberAccountDetail instanceof Map) {
                        Map subscriberAccountDetail1 = (Map) subscriberAccountDetail;
                        log.debug("Processing subscription: {}", subscriberAccountDetail1);
                        topUpSubscriptionListDto.setEndTime(LocalDateTime.parse((String) subscriberAccountDetail1.get("endTime")));
                        topUpSubscriptionListDto.setStartTime(LocalDateTime.parse((String) subscriberAccountDetail1.get("startTime")));
                        topUpSubscriptionListDto.setSubscriberIdentity((String) subscriberAccountDetail1.get("subscriberIdentity"));
                        topUpSubscriptionListDto.setTopUpId((Integer) subscriberAccountDetail1.get("topUpId"));

                        topUpSubscriptionListDto.setTopUpName((String) subscriberAccountDetail1.get("topUpName"));
                        topUpSubscriptionListDto.setTopUpStatus((String) subscriberAccountDetail1.get("topUpStatus"));
                        topUpSubscriptionListDto.setSubscriberIdentity((String) subscriberAccountDetail1.get("subscriberIdentity"));
                        topUpSubscriptionListDto.setTopUpSubscriptionId(Long.valueOf((Integer) subscriberAccountDetail1.get("topUpSubscriptionId")));
                        String s = (String) subscriberAccountDetail1.get("usageResetTime");
                        if (s != null) {
                            topUpSubscriptionListDto.setUsageResetTime(LocalDateTime.parse(s));
                        }
                        topUpSubscriptionListDtos.add(topUpSubscriptionListDto);
                    }

                }
                data.setResponseCode(200);
                data.setResponseMessage("SUCCESS");
                subscriptions.setReturn(data);

                // Add actual data from the service response

                log.debug("Preparing response with {} subscriptions", topUpSubscriptionListDtos.size());
                for (TopUpSubscriptionListDto topUp : topUpSubscriptionListDtos) {
                    TopUpSubscriptionData data1 = new TopUpSubscriptionData();
                    data1.setEndTime(topUp.getEndTime() != null ? topUp.getEndTime().toEpochSecond(ZoneOffset.UTC) : 0L);
                    data1.setStartTime(topUp.getStartTime() != null ? topUp.getStartTime().toEpochSecond(ZoneOffset.UTC) : 0L);
                    data1.setSubscriberIdentity(topUp.getSubscriberIdentity());
                    data1.setTopUpId(String.valueOf(topUp.getTopUpId()));
                    data1.setTopUpName(topUp.getTopUpName());
                    data1.setTopUpStatus(SubscriptionState.valueOf(topUp.getTopUpStatus().toUpperCase()));
                    data1.setTopUpSubscriptionId(String.valueOf(topUp.getTopUpSubscriptionId()));
                    data1.setUsageResetTime(topUp.getUsageResetTime() != null ? topUp.getUsageResetTime().toEpochSecond(ZoneOffset.UTC) : 0L);

                    data.getTopUpSubscriptions().add(data1);
                }
            } else {
                log.warn("No data found for subscriberId: {}", request.getSubscriberId());
                data.setResponseCode(200);
                data.setResponseMessage("NO DATA FOUND with given user_name: " + request.getSubscriberId());
                subscriptions.setReturn(data);
            }
            log.info("Method execution completed successfully for subscriberId: {}", request.getSubscriberId());
            subscriptions.setReturn(data);
            return subscriptions;
        } catch (Exception e) {
            log.error("Exception occurred while fetching top-up subscriptions", e);
            WsListTopUpSubscriptionsResponse subscriptions = new WsListTopUpSubscriptionsResponse();
            TopUpSubscriptionResponse data = new TopUpSubscriptionResponse();
            data.setResponseCode(200);
            data.setResponseMessage("NO DATA FOUND with given user_name: " + request.getSubscriberId());
            subscriptions.setReturn(data);
            return subscriptions;
        } finally {
            long endTime = System.currentTimeMillis();
            log.info("Method: ListTopUpSubscriptionsService.getData completed in {}ms", (endTime - startTime));
        }
    }

}
