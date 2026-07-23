package com.savbill.integrationsystem.SOAPService.WsSubscribeTopUp;

import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.generated.wssubscribetopup.WsSubscribeTopUp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class WsSubscribeTopUpEndpoint {

    @Autowired
    private WsSubscribeTopUpSoapService wsSubscribeTopUpSoapService;

    @PayloadRoot(namespace = SoapConstants.NAMESPACE_ELITECORE
            , localPart = "wsSubscribeTopUp")
    @ResponsePayload
    public com.savbill.integrationsystem.generated.newwssubscribetopup.WsSubscribeTopUpResponse handleRequest(@RequestPayload WsSubscribeTopUp request, MessageContext messageContext) throws Exception {
        return wsSubscribeTopUpSoapService.handleSubscribeTopUpRequest(request,messageContext);
//        return wsSubscribeTopUpSoapService.generateRauthSessionsSOAPResponse(request,messageContext);

    }
/*

    public DOMSource generateRauthSessionsSOAPResponse(MessageContext messageContext) throws SOAPException, IOException {
        SOAPMessage soapMessage = createSOAP12SuccessResponse(
                    "200",
                    "SUCCESS",
                    "1521743400000",
                    "1516645800000",
                    "wrtest4",
                    "506fdff8-f6a3-48f7-963c-4e75be95878d",
                    "SPARE500MB",
                    "STARTED",
                    "df54d551-bb68-48c5-973b-2df0258fa955",
                    "1519237800000");
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

    public SOAPMessage createSOAP12SuccessResponse(String responseCode, String responseMessage,
                                                   String endTime, String startTime,
                                                   String subscriberIdentity, String topUpId,
                                                   String topUpName, String topUpStatus,
                                                   String topUpSubscriptionId, String usageResetTime) throws SOAPException {

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
        returnElement.addChildElement("responseCode").addTextNode(responseCode);
        returnElement.addChildElement("responseMessage").addTextNode(responseMessage);

        // Add the topUpSubscriptions element
        SOAPElement topUpSubscriptionsElement = returnElement.addChildElement("topUpSubscriptions");

        // Add dynamic child elements under topUpSubscriptions
        topUpSubscriptionsElement.addChildElement("endTime").addTextNode(endTime);
        topUpSubscriptionsElement.addChildElement("startTime").addTextNode(startTime);
        topUpSubscriptionsElement.addChildElement("subscriberIdentity").addTextNode(subscriberIdentity);
        topUpSubscriptionsElement.addChildElement("topUpId").addTextNode(topUpId);
        topUpSubscriptionsElement.addChildElement("topUpName").addTextNode(topUpName);
        topUpSubscriptionsElement.addChildElement("topUpStatus").addTextNode(topUpStatus);
        topUpSubscriptionsElement.addChildElement("topUpSubscriptionId").addTextNode(topUpSubscriptionId);
        topUpSubscriptionsElement.addChildElement("usageResetTime").addTextNode(usageResetTime);

        soapMessage.saveChanges(); // Save changes to the SOAP message

        return soapMessage;
    }
*/

}
