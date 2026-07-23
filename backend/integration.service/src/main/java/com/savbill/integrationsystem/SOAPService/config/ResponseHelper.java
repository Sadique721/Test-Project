package com.savbill.integrationsystem.SOAPService.config;

import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.soap.*;
import java.util.HashMap;
import java.util.Map;

public class ResponseHelper {

    Map<String, String[]> map = new HashMap<>();

    // Constructor
    public ResponseHelper() {
        String[] innerMap = new String[4];
        innerMap[0] = "soap";
        innerMap[1] = "http://api.act.com/";
        innerMap[2] = "http://subscription.ws.nvsmx.elitecore.com/";
        innerMap[3] = "http://sessionmanagement.ws.nvsmx.elitecore.com/";

        map.put("wsAddAccountResponse", innerMap);                  //soap      DONE
        map.put("wsBalanceEnquiryResponse", innerMap);              //soap      DONE
        map.put("wsGetUserUsageSummaryResponse", innerMap);         //soap      DONE
        map.put("wsAddServiceToAccountResponse", innerMap);         //soap      DONE
        map.put("wsAuthenticateUserResponse", innerMap);            //soap      DONE
        map.put("wsChangeServiceResponse", innerMap);               //soap AND soapenv  DONE

        map.put("wsGetAccountDetailsResponse", innerMap);           //soap      DONE
        map.put("wsGetAccountNameResponse", innerMap);              //soap      DONE
        map.put("wsLoginSessionResponse", innerMap);                //soap      DONE
        map.put("wsLogoffUserSessionsResponse", innerMap);          //soap      DONE

        map.put("wsRemoveAccountResponse", innerMap);               //soap AND soapenv  DONE
        map.put("wsResetUsageForAccountResponse", innerMap);        //soap AND soapenv  DONE

        map.put("wsSessionLoginStatusResponse", innerMap);          //soap      DONE
        map.put("wsUpdateAccountResponse", innerMap);               //soap      DONE
        map.put("wsUpdateUserUsageResponse", innerMap);             //soap AND soapenv  DONE
        map.put("wsUserLoginStatusResponse", innerMap);             //soap      DONE
        map.put("wsGetUserSessionResponse", innerMap);              //soap      DONE


        map.put("wsSubscribeAddOnResponse", innerMap);              //soap      DONE
        map.put("wsSubscribeTopUpResponse", innerMap);              //soap      DONE
        map.put("wsChangeAddOnSubscriptionResponse", innerMap);     //soap      DONE
        map.put("wsChangeTopUpSubscriptionResponse", innerMap);     //soap      DONE
        map.put("wsReauthSessionsBySubscriberIdentityResponse", innerMap);//soap       DONE
        map.put("wsGetBalanceResponse", innerMap);                  //soap      DONE
        map.put("wsListAddOnSubscriptionsResponse", innerMap);              //soap      DONE
        map.put("wsListTopUpSubscriptionsResponse",innerMap);               //soap      DONE

    }

    public Map<String, String[]> getMap() {
        return map;
    }


    public void formateChangeServiceResponse(SOAPEnvelope envelope, Node firstChild,
                                             String[] strings) throws SOAPException {
        envelope.removeNamespaceDeclaration("SOAP-ENV");
        Node child = firstChild.getFirstChild();

        Node firstChild1 = child.getFirstChild();
        Node firstChild2 = firstChild1.getNextSibling().getFirstChild();
        String respcode = String.valueOf(firstChild2);

        if (respcode.contains("502")) {
            envelope.setPrefix("soapenv");
            SOAPBody body = envelope.getBody();
            body.setPrefix("soapenv");
            SOAPHeader header = envelope.getHeader();
            if (header != null) {
                header.detachNode();
            }
            Element element = (Element) firstChild;
            element.removeAttribute("xmlns:ns3");
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns2", strings[1]);
            element.setPrefix("ns2");

        } else {
            envelope.setPrefix(strings[0]);
            SOAPBody body = envelope.getBody();
            body.setPrefix(strings[0]);
            SOAPHeader header = envelope.getHeader();
            if (header != null) {
                header.detachNode();
            }

            Element element = (Element) firstChild;
            element.removeAttribute("xmlns:ns3");
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns2", strings[1]);
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns3", strings[2]);
            element.setPrefix("ns2");
        }
    }

    public void formateResetUsageForAccountResponse(SOAPEnvelope envelope, Node firstChild,
                                                    String[] strings) throws SOAPException {
        envelope.removeNamespaceDeclaration("SOAP-ENV");
        Node child = firstChild.getFirstChild();

        Node firstChild1 = child.getFirstChild();
        Node firstChild2 = firstChild1.getNextSibling().getFirstChild();
        String respcode = String.valueOf(firstChild2);

        if (respcode.contains("401")) {
            envelope.setPrefix(strings[0]);
            SOAPBody body = envelope.getBody();
            body.setPrefix(strings[0]);
            SOAPHeader header = envelope.getHeader();
            if (header != null) {
                header.detachNode();
            }
        } else {
            envelope.setPrefix("soapenv");
            SOAPBody body = envelope.getBody();
            body.setPrefix("soapenv");
            SOAPHeader header = envelope.getHeader();
            if (header != null) {
                header.detachNode();
            }
        }

        Element element = (Element) firstChild;
        element.removeAttribute("xmlns:ns3");
        element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns2", strings[1]);
//        element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns3", strings[2]);
        element.setPrefix("ns2");
    }


    public void formateUpdateUserUsageResponse(SOAPEnvelope envelope, Node firstChild,
                                               String[] strings) throws SOAPException {
        envelope.removeNamespaceDeclaration("SOAP-ENV");
        Node child = firstChild.getFirstChild();

        Node firstChild1 = child.getFirstChild();
        Node firstChild2 = firstChild1.getNextSibling().getFirstChild();
        String respcode = String.valueOf(firstChild2);

        if (respcode.contains("406")) {
            envelope.setPrefix("soapenv");
            SOAPBody body = envelope.getBody();
            body.setPrefix("soapenv");
            SOAPHeader header = envelope.getHeader();
            if (header != null) {
                header.detachNode();
            }
        } else {
            envelope.setPrefix(strings[0]);
            SOAPBody body = envelope.getBody();
            body.setPrefix(strings[0]);
            SOAPHeader header = envelope.getHeader();
            if (header != null) {
                header.detachNode();
            }
        }

        Element element = (Element) firstChild;
        element.removeAttribute("xmlns:ns3");
        element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns2", strings[1]);
        element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns3", strings[2]);
        element.setPrefix("ns2");
    }


    public void formateRemoveAccountResponse(SOAPEnvelope envelope, Node firstChild,
                                             String[] strings, String userName) throws SOAPException {
        envelope.removeNamespaceDeclaration("SOAP-ENV");
        Node child = firstChild.getFirstChild();

        Node firstChild1 = child.getFirstChild();
        Node firstChild2 = firstChild1.getNextSibling().getFirstChild();
        String respcode = String.valueOf(firstChild2);

        if (userName != null && userName.contains(" ") && respcode.contains("200")) {
            envelope.setPrefix("soapenv");
            SOAPBody body = envelope.getBody();
            body.setPrefix("soapenv");
            SOAPHeader header = envelope.getHeader();
            if (header != null) {
                header.detachNode();
            }
        } else {
            envelope.setPrefix(strings[0]);
            SOAPBody body = envelope.getBody();
            body.setPrefix(strings[0]);
            SOAPHeader header = envelope.getHeader();
            if (header != null) {
                header.detachNode();
            }
        }

        Element element = (Element) firstChild;
        element.removeAttribute("xmlns:ns3");
        element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns2", strings[1]);
        element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns3", strings[2]);
        element.setPrefix("ns2");
        userName = null;
    }

    public void formateVasGetBalanceResponse(SOAPEnvelope envelope, Node firstChild,
                                             String[] strings, SaajSoapMessage saajSoapMessage) throws SOAPException {
        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.setPrefix(strings[0]);
        SOAPBody body = envelope.getBody();
        body.setPrefix(strings[0]);

        // Create a new SOAP message with SOAP 1.2 protocol
        MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        SOAPMessage newSoapMessage = messageFactory.createMessage();
        SOAPEnvelope newEnvelope = newSoapMessage.getSOAPPart().getEnvelope();

        // Remove old namespace and set a new one
        NodeList subscriptionInfoElements = ((Element) firstChild).getElementsByTagName("subscriptionInformations");

        // Process each subscriptionInformations element
        for (int i = 0; i < subscriptionInfoElements.getLength(); i++) {
            Element element = (Element) subscriptionInfoElements.item(i);

            // Remove the xsi:type attribute
            element.removeAttribute("xsi:type");

            // Remove the xmlns:xsi attribute
            element.removeAttribute("xmlns:xsi");
        }

        newEnvelope.removeNamespaceDeclaration(newEnvelope.getPrefix());
        newEnvelope.addNamespaceDeclaration("soap", "http://www.w3.org/2003/05/soap-envelope"); // Set SOAP 1.2 namespace
        newEnvelope.setPrefix("soap");
        SOAPHeader header = newEnvelope.getHeader();
        if (header != null) {
            header.detachNode();
        }
        // Update SOAP Body namespace
        SOAPBody newBody = newEnvelope.getBody();
        newBody.setPrefix("soap");

        // Import and modify the existing body content
//        Node importedElement = newSoapMessage.getSOAPPart().importNode(body.getFirstChild(), true);
        Element element = (Element) firstChild;
        element.removeAttribute("xmlns:ns3");
        element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns2", strings[2]);
        element.setPrefix("ns2");
        body.getFirstChild();
        newBody.addDocument(body.extractContentAsDocument());
        saajSoapMessage.setSaajMessage(newSoapMessage);

        // Save changes
        newSoapMessage.saveChanges();
        saajSoapMessage.setSaajMessage(newSoapMessage);
    }

    public void formateVasApiResponse(SOAPEnvelope envelope, Node firstChild,
                                      String[] strings, SaajSoapMessage saajSoapMessage) throws SOAPException {
        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.setPrefix(strings[0]);
        SOAPBody body = envelope.getBody();
        body.setPrefix(strings[0]);

        // Create a new SOAP message with SOAP 1.2 protocol
        MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        SOAPMessage newSoapMessage = messageFactory.createMessage();
        SOAPEnvelope newEnvelope = newSoapMessage.getSOAPPart().getEnvelope();

        // Remove old namespace and set a new one
        newEnvelope.removeNamespaceDeclaration(newEnvelope.getPrefix());
        newEnvelope.addNamespaceDeclaration("soap", "http://www.w3.org/2003/05/soap-envelope"); // Set SOAP 1.2 namespace
        newEnvelope.setPrefix("soap");
        SOAPHeader header = newEnvelope.getHeader();
        if (header != null) {
            header.detachNode();
        }
        // Update SOAP Body namespace
        SOAPBody newBody = newEnvelope.getBody();
        newBody.setPrefix("soap");

        // Import and modify the existing body content
//        Node importedElement = newSoapMessage.getSOAPPart().importNode(body.getFirstChild(), true);
        Element element = (Element) firstChild;
        element.removeAttribute("xmlns:ns3");
        element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns2", strings[2]);
        element.setPrefix("ns2");
        body.getFirstChild();
        newBody.addDocument(body.extractContentAsDocument());
        saajSoapMessage.setSaajMessage(newSoapMessage);

        // Save changes
        newSoapMessage.saveChanges();
        saajSoapMessage.setSaajMessage(newSoapMessage);
    }

    public void formateReauthSessionsBySubscriberIdentityResponse(SOAPEnvelope envelope, Node firstChild,
                                                                  String[] strings, SaajSoapMessage saajSoapMessage) throws SOAPException {
        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.setPrefix(strings[0]);
        SOAPBody body = envelope.getBody();
        body.setPrefix(strings[0]);

        // Create a new SOAP message with SOAP 1.2 protocol
        MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        SOAPMessage newSoapMessage = messageFactory.createMessage();
        SOAPEnvelope newEnvelope = newSoapMessage.getSOAPPart().getEnvelope();

        // Remove old namespace and set a new one
        newEnvelope.removeNamespaceDeclaration(newEnvelope.getPrefix());
        newEnvelope.addNamespaceDeclaration("soap", "http://www.w3.org/2003/05/soap-envelope"); // Set SOAP 1.2 namespace
        newEnvelope.setPrefix("soap");
        SOAPHeader header = newEnvelope.getHeader();
        if (header != null) {
            header.detachNode();
        }
        // Update SOAP Body namespace
        SOAPBody newBody = newEnvelope.getBody();
        newBody.setPrefix("soap");

        // Import and modify the existing body content
//        Node importedElement = newSoapMessage.getSOAPPart().importNode(body.getFirstChild(), true);
        Element element = (Element) firstChild;
        element.removeAttribute("xmlns:ns3");
        element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns2", strings[3]);
        element.setPrefix("ns2");
        body.getFirstChild();
        newBody.addDocument(body.extractContentAsDocument());
        saajSoapMessage.setSaajMessage(newSoapMessage);

        // Save changes
        newSoapMessage.saveChanges();
        saajSoapMessage.setSaajMessage(newSoapMessage);
    }

    public void formateSoapResponse(SOAPEnvelope envelope, Node firstChild,
                                    String[] strings) throws SOAPException {
        envelope.removeNamespaceDeclaration("SOAP-ENV");
        Node child = firstChild.getFirstChild();

        envelope.setPrefix(strings[0]);
        SOAPBody body = envelope.getBody();
        body.setPrefix(strings[0]);
        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode();
        }

        Element element = (Element) firstChild;
        element.removeAttribute("xmlns:ns3");
        element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns2", strings[1]);
        element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns3", strings[2]);
        element.setPrefix("ns2");
    }


    public void formateUpdateAccountResponse(SOAPEnvelope envelope, Node firstChild,
                                                   String[] strings) throws SOAPException {
        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.setPrefix(strings[0]);


        Node child = firstChild.getFirstChild();

        Node firstChild1 = child.getFirstChild();
        Node firstChild2 = firstChild1.getNextSibling().getFirstChild();
        String respcode = String.valueOf(firstChild2);

        if (respcode.contains("200")) {
            envelope.setPrefix("soapenv");
            SOAPBody body = envelope.getBody();
            body.setPrefix("soapenv");
            SOAPHeader header = envelope.getHeader();
            if (header != null) {
                header.detachNode();
            }
            Element element = (Element) firstChild;
            element.removeAttribute("xmlns:ns3");
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns2", strings[1]);
            element.setPrefix("ns2");
        } else {
            envelope.setPrefix(strings[0]);
            SOAPBody body = envelope.getBody();
            body.setPrefix(strings[0]);
            SOAPHeader header = envelope.getHeader();
            if (header != null) {
                header.detachNode();
            }
            Element element = (Element) firstChild;
            element.removeAttribute("xmlns:ns3");
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns2", strings[1]);
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns3", strings[2]);
            element.setPrefix("ns2");

        }
    }


    public void formateAddServiceToAccountResponse(SOAPEnvelope envelope, Node firstChild,
                                                   String[] strings) throws SOAPException {
        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.setPrefix(strings[0]);


        Node child = firstChild.getFirstChild();

        Node firstChild1 = child.getFirstChild();
        Node firstChild2 = firstChild1.getNextSibling().getFirstChild();
        String respcode = String.valueOf(firstChild2);

        if (respcode.contains("502")) {
            envelope.setPrefix("soapenv");
            SOAPBody body = envelope.getBody();
            body.setPrefix("soapenv");
            SOAPHeader header = envelope.getHeader();
            if (header != null) {
                header.detachNode();
            }
            Element element = (Element) firstChild;
            element.removeAttribute("xmlns:ns3");
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns2", strings[1]);
            element.setPrefix("ns2");
        } else {
            envelope.setPrefix(strings[0]);
            SOAPBody body = envelope.getBody();
            body.setPrefix(strings[0]);
            SOAPHeader header = envelope.getHeader();
            if (header != null) {
                header.detachNode();
            }
            Element element = (Element) firstChild;
            element.removeAttribute("xmlns:ns3");
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns2", strings[1]);
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns3", strings[2]);
            element.setPrefix("ns2");

        }
    }


}

