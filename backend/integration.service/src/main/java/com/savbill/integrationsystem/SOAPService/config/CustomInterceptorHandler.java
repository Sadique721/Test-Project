package com.savbill.integrationsystem.SOAPService.config;

import com.savbill.integrationsystem.Customer.CustomersController;
import com.savbill.integrationsystem.soapAudit.SoapAuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.servlet.http.HttpServletRequest;
import javax.xml.soap.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@EnableWs
@Configuration
public class CustomInterceptorHandler extends WsConfigurerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomersController.class);

    ResponseHelper resp = new ResponseHelper();

    @Autowired
    private SoapAuditService interceptorService;
    String userName = null;



    //        @Autowired
//    private AsyncInterceptorService asyncInterceptorService;
    @Override
    public void addInterceptors(List<EndpointInterceptor> interceptors) {
        LOGGER.info("Registering custom interceptor...");
        interceptors.add(new EndpointInterceptor() {
            @Override
            public boolean handleRequest(MessageContext messageContext, Object endpoint) throws Exception {
                LOGGER.info("Request Interceptor triggered.....");
                SaajSoapMessage saajSoapMessage = (SaajSoapMessage) messageContext.getRequest();
                SOAPMessage soapMessage = saajSoapMessage.getSaajMessage();
                SOAPEnvelope envelope = soapMessage.getSOAPPart().getEnvelope();



                SOAPBody body = envelope.getBody();
                Iterator<?> it = body.getChildElements();
                while (it.hasNext()) {
                    Node node = (Node) it.next();
                    if (node.getLocalName() != null && node.getLocalName().equals("wsRemoveAccount")) {
                        // Find userName element within wsRemoveAccount
                        NodeList children = node.getChildNodes();
                        for (int i = 0; i < children.getLength(); i++) {
                            Node child = children.item(i);
                            if (child.getLocalName() != null && child.getLocalName().equals("userName")) {
                                userName = child.getTextContent();
                                break;
                            }
                        }
                        break;
                    }
                }
                if (!envelope.getNamespaceURI().equals(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE)) {
                    LOGGER.info("############### (Soap 1.1  formate Request coming successfully) ###############");
                } else {
                    LOGGER.info("############### (Soap 1.2  formate Request coming successfully) ###############");
                }


                return true;
            }

            @Override
            public boolean handleResponse(MessageContext messageContext, Object endpoint) throws Exception {
                LOGGER.info("Request Interceptor triggered.....");

                // Get the SOAP message from the response
                SaajSoapMessage saajSoapMessage = (SaajSoapMessage) messageContext.getResponse();
                SOAPMessage soapMessage = saajSoapMessage.getSaajMessage();
                SOAPEnvelope envelope = soapMessage.getSOAPPart().getEnvelope();

                if (!envelope.getNamespaceURI().equals(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE)) {
                    formateRespons(envelope,saajSoapMessage);
                    LOGGER.info("############### (Soap 1.1  formate return Success Response successfully) ###############");
                } else {
                    LOGGER.info("############### (Soap 1.2  formate return Success Response successfully) ###############");
                }

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                soapMessage.writeTo(byteArrayOutputStream);
                String soapMessageContent = byteArrayOutputStream.toString();

                String formattedXml = formatXml(soapMessageContent);
                LOGGER.info("\n (:Received Response XML Content:) \n" + formattedXml);

                LOGGER.info("\n : Success Response Return successfully.....");
                return true;
            }


            @Override
            public boolean handleFault(MessageContext messageContext, Object endpoint) throws Exception {
                LOGGER.info("handleFault interceptor triggered....");
                SaajSoapMessage saajSoapMessage = (SaajSoapMessage) messageContext.getResponse();
                SOAPMessage soapMessage = saajSoapMessage.getSaajMessage();
                SOAPEnvelope envelope = soapMessage.getSOAPPart().getEnvelope();

                if (!envelope.getNamespaceURI().equals(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE)) {
                    LOGGER.info("############### (Soap 1.1  formate return Exception Response successfully) ###############");
                } else {
                    LOGGER.info("############### (Soap 1.2  formate return Exception Response successfully) ###############");
                }


                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                soapMessage.writeTo(byteArrayOutputStream);
                String soapMessageContent = byteArrayOutputStream.toString();

                String formattedXml = formatXml(soapMessageContent);
                LOGGER.info("\n (:Received fault XML Content:) \n" + formattedXml);

                LOGGER.info("\n :Fault Response Return successfully.....");
                return true;
            }

            @Override
            public void afterCompletion(MessageContext messageContext, Object endpoint, Exception ex) {
                try {
                    String clientIp = "UNKNOWN";
                    try {
                        // Get the current HTTP request
                        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                        LOGGER.info("ServletRequestAttributes Values:: {}",attrs);
                        if (attrs != null) {
                            HttpServletRequest request = attrs.getRequest();

                            // Extract Client IP Address
                            clientIp = request.getHeader("X-Forwarded-For"); // Check if behind proxy
                            if (clientIp == null || clientIp.isEmpty()) {
                                clientIp = request.getRemoteAddr(); // Direct IP
                            }

                            // If multiple IPs in X-Forwarded-For, take the first one
                            if (clientIp != null && clientIp.contains(",")) {
                                clientIp = clientIp.split(",")[0].trim();
                            }
                        }

                        LOGGER.info("Client IP Address in afterCompletion: " + clientIp);
                    }catch (Exception e){
                        LOGGER.error("ERROR ENCOUNTERED while getting IP:: {}", e.getMessage());
                    }

                    interceptorService.saveAudit(messageContext, clientIp);
                    LOGGER.info("Audit saved successfully.");
                } catch (Exception e) {
                    LOGGER.error("error occured while saving audit");
                }
                LOGGER.info("Interceptor afterCompletion triggered.....");
            }
        });
    }

    private String formatXml(String unformulatedXml) throws Exception {
        StringReader stringReader = new StringReader(unformulatedXml);
        StreamSource source = new StreamSource(stringReader);

        // Prepare the output for the formatted XML
        StringWriter stringWriter = new StringWriter();
        StreamResult result = new StreamResult(stringWriter);

        // Create a Transformer for formatting the XML
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty("indent", "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        transformer.transform(source, result);
        return stringWriter.toString();
    }


    public void formateRespons(SOAPEnvelope envelope, SaajSoapMessage saajSoapMessage) throws SOAPException {
        Node firstChild = envelope.getBody().getFirstChild();
        Map<String, String[]> map = resp.getMap();
        String[] strings = map.getOrDefault(firstChild.getLocalName(), new String[0]);
        switch (firstChild.getLocalName()) {
            case "wsAddAccountResponse":                //Done
                resp.formateSoapResponse(envelope, firstChild, strings);
                break;
            case "wsBalanceEnquiryResponse":            //Done
                resp.formateSoapResponse(envelope, firstChild, strings);
                break;
            case "wsGetUserUsageSummaryResponse":       //Done
                resp.formateSoapResponse(envelope, firstChild, strings);
                break;
            case "wsAddServiceToAccountResponse":       //Done
                resp.formateAddServiceToAccountResponse(envelope, firstChild, strings);
                break;
            case "wsAuthenticateUserResponse":          //Done
                resp.formateSoapResponse(envelope, firstChild, strings);
                break;
            case "wsGetAccountDetailsResponse":         //Done
                resp.formateSoapResponse(envelope, firstChild, strings);
                break;
            case "wsGetAccountNameResponse":            //Done
                resp.formateSoapResponse(envelope, firstChild, strings);
                break;
            case "wsLoginSessionResponse":              //Done
                resp.formateSoapResponse(envelope, firstChild, strings);
                break;
            case "wsLogoffUserSessionsResponse":        //Done
                resp.formateSoapResponse(envelope, firstChild, strings);
                break;
            case "wsSessionLoginStatusResponse":        //Done
                resp.formateSoapResponse(envelope, firstChild, strings);
                break;
            case "wsUpdateAccountResponse":             //Done
                resp.formateUpdateAccountResponse(envelope, firstChild, strings);
                break;
            case "wsUserLoginStatusResponse":           //Done
                resp.formateSoapResponse(envelope, firstChild, strings);
                break;
            case "wsGetUserSessionResponse":            //Done
                resp.formateSoapResponse(envelope, firstChild, strings);
                break;
            case "wsRemoveAccountResponse":             //Done
                resp.formateRemoveAccountResponse(envelope, firstChild, strings, userName);
                userName = null;
                break;
            case "wsResetUsageForAccountResponse":      //Done
                resp.formateResetUsageForAccountResponse(envelope, firstChild, strings);
                break;
            case "wsUpdateUserUsageResponse":           //Done
                resp.formateUpdateUserUsageResponse(envelope, firstChild, strings);
                break;
            case "wsChangeServiceResponse":             //Done
                resp.formateChangeServiceResponse(envelope, firstChild, strings);
                break;
            case "wsSubscribeTopUpResponse":             //Done
                resp.formateVasApiResponse(envelope, firstChild, strings, saajSoapMessage);
                break;
            case "wsSubscribeAddOnResponse":             //Done
                resp.formateVasApiResponse(envelope, firstChild, strings, saajSoapMessage);
                break;
            case "wsChangeAddOnSubscriptionResponse":    //Done
                resp.formateVasApiResponse(envelope, firstChild, strings, saajSoapMessage);
                break;
            case "wsChangeTopUpSubscriptionResponse":    //Done
                resp.formateVasApiResponse(envelope, firstChild, strings, saajSoapMessage);
                break;
            case "wsReauthSessionsBySubscriberIdentityResponse":        //Done
                resp.formateReauthSessionsBySubscriberIdentityResponse(envelope, firstChild, strings, saajSoapMessage);
                break;
            case "wsGetBalanceResponse":                                //Done
                resp.formateVasGetBalanceResponse(envelope, firstChild, strings, saajSoapMessage);
                break;
            case "wsListAddOnSubscriptionsResponse":    //Done
                resp.formateVasApiResponse(envelope, firstChild, strings, saajSoapMessage);
                break;
            case "wsListTopUpSubscriptionsResponse":    //Done
                resp.formateVasApiResponse(envelope, firstChild, strings, saajSoapMessage);
                break;


            default:
                break;
        }
    }
}
