package com.savbill.integrationsystem.SOAPService.meteredVolumeUsageForSubAcctName;

import com.savbill.integrationsystem.SOAPService.GetUserUsageSummary.GetUserUsageSummaryEndPoint;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.SOAPService.logOffUserSessions.LogoffUserSessionsEndPoint;
import com.savbill.integrationsystem.SOAPService.wsGetBalance.GetBalanceDto;
import com.savbill.integrationsystem.billgen.repository.CustomerRepository;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.generated.getuserusagesummary.WsGetUserUsageSummaryResponse;
import com.savbill.integrationsystem.generated.wsmeteredvolumeusageforsubacctname.GetMeteredVolumeUsageForSubAcctName;
import com.savbill.integrationsystem.utility.CommonUtilityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.transform.dom.DOMSource;
import java.io.IOException;
import java.sql.SQLException;

@Endpoint
public class MeteredVolumeUsageForSubAcctNameEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(LogoffUserSessionsEndPoint.class);
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private RadiusClientService radiusClientService;
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private GetUserUsageSummaryEndPoint getUserUsageSummaryEndPoint;
    @Autowired
    private CommonUtilityService commonUtilityService;

    private final Logger log = LoggerFactory.getLogger(MeteredVolumeUsageForSubAcctNameEndpoint.class);
    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI_NEW, localPart = "getMeteredVolumeUsageForSubAcctName")
    @ResponsePayload
    public DOMSource getMeteredVolumeUsageForSubAcctName(@RequestPayload GetMeteredVolumeUsageForSubAcctName request, MessageContext messageContext)throws Exception {
        if(request.getString1()==null || request.getString1().isEmpty()){
            log.warn("Username is empty or null in the request.");
            return generateMeteredVolumeUsageForSubAcctNameSOAP11ExceptionResponse(
                           "generalException",
                            "InvalidSubscriberAccountException",
                            "Username is Empty or Null",
                            "ecaaa1", messageContext
                    );
        }
        log.info("Received request for metered volume usage with SubscriberId: {}", request.getString1());
        try{
            MeteredVolumeUsageForSubsAccNameDTO userUsageSummary = getUserUsageSummary(request);
            return generateMeteredVolumeUsageForSubAcctNameSuccessResponse(userUsageSummary, messageContext);
        }catch (SQLException e){
            return generateMeteredVolumeUsageForSubAcctNameSOAP11ExceptionResponse(
                            "SQLException",
                            "exceptionName",
                            "SQL Exception",
                            "ecaaa1", messageContext
                    );
        }catch (RuntimeException e){
            String exceptionMessage = e.getMessage() != null ? e.getMessage() : "Exception was encountered during processing Request.";
            return generateMeteredVolumeUsageForSubAcctNameSOAP11ExceptionResponse(
                            "Exception",
                            "Exception",
                            exceptionMessage,
                            "ecaaa1", messageContext
                    );
        }catch (Exception e){
            String exceptionMessage = e.getMessage() != null ? e.getMessage() : "Exception was encountered during processing Request.";
            return generateMeteredVolumeUsageForSubAcctNameSOAP11ExceptionResponse(
                            "generalException",
                            "InvalidSubscriberAccountException",
                            exceptionMessage,
                            "ecaaa1", messageContext
                    );
        }
    }

    public MeteredVolumeUsageForSubsAccNameDTO getUserUsageSummary(GetMeteredVolumeUsageForSubAcctName request)throws Exception {
        MeteredVolumeUsageForSubsAccNameDTO volumeUsageForSubsAccNameDTO = new MeteredVolumeUsageForSubsAccNameDTO();
        WsGetUserUsageSummaryResponse.GetUserUsageSummary response = new WsGetUserUsageSummaryResponse.GetUserUsageSummary();

        try {
            log.info("Fetching balance details for SubscriberId: {}", request.getString1());
            String SubscriberId = request.getString1();
            Long mvnoId = SoapConstants.MVNOID;
            long startTime = System.currentTimeMillis(); // Capture start time
            GenericDataDTO genericDataDTO = radiusClientService.GetBalanceApi(request.getString1(),mvnoId);
            long endTime = System.currentTimeMillis(); // Capture end time
            log.info("Balance API call completed in {} ms for SubscriberId: {}", (endTime - startTime), SubscriberId);
            GetBalanceDto dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(genericDataDTO.getData()),GetBalanceDto.class);
            if(dataMessage == null) {
                log.warn("No session data available for SubscriberId: {}", SubscriberId);
                throw new Exception("Input username is not available in session table." );
            }
            volumeUsageForSubsAccNameDTO.setPlanName(dataMessage.getPlanName());
            volumeUsageForSubsAccNameDTO.setPlanId(dataMessage.getPlanId());
            volumeUsageForSubsAccNameDTO.setAggregateBytesLimit(dataMessage.getTotalQuota());
            volumeUsageForSubsAccNameDTO.setAggregateBytesRemaining(dataMessage.getTotalQuota() - (dataMessage.getUsedQuota() + dataMessage.getCurrentSessionUsageVolume()));
            volumeUsageForSubsAccNameDTO.setAggregateBytesUsed(dataMessage.getUsedQuota());

            volumeUsageForSubsAccNameDTO.setInBytesLimit(0.0);
            volumeUsageForSubsAccNameDTO.setInBytesRemaining(0.0);
            volumeUsageForSubsAccNameDTO.setInBytesUsed(0.0);

            volumeUsageForSubsAccNameDTO.setOutBytesLimit(0.0);
            volumeUsageForSubsAccNameDTO.setOutBytesRemaining(0.0);
            volumeUsageForSubsAccNameDTO.setOutBytesUsed(0.0);
            volumeUsageForSubsAccNameDTO.setUploadOctate(Double.valueOf(dataMessage.getDownloadQuota()));
            volumeUsageForSubsAccNameDTO.setDownloadOctate(Double.valueOf(dataMessage.getUploadQuota()));
            volumeUsageForSubsAccNameDTO.setQuotaUnit(dataMessage.getQuotaUnit());
            log.info("Successfully retrieved usage summary for SubscriberId: {}", request.getString1());
        }catch (Exception e) {
            log.error("Error fetching usage summary for SubscriberId: {}", request.getString1(), e);
            throw e;
        }
        return volumeUsageForSubsAccNameDTO;
    }

    public DOMSource getSubSessionIsLoggedOnSuccess(MeteredVolumeUsageForSubsAccNameDTO request) throws SOAPException, IOException {
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();
        SOAPHeader header = envelope.getHeader();

//         Declare the required namespaces explicitly at the envelope level
        envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/encoding/");
        envelope.addNamespaceDeclaration("soapenc", SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);

        // Create SOAP body
        SOAPBody body = envelope.getBody();
        if (body.getFault() != null) {
            body.removeChild(body.getFault());
        }
        SOAPElement MeteredVolumeUsage = body.addChildElement(new QName("", "ns1:getMeteredVolumeUsageForSubAcctNameResponse"));
        Element element = MeteredVolumeUsage;
        element.setAttributeNS(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE, "soapenv:encodingStyle", SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);
        element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns1", "http://npm.redback.com");
        SOAPElement result = MeteredVolumeUsage.addChildElement("result");
        Element element1 = result;
        element1.setAttributeNS(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE, "soapenc:arrayType", "ns1:MeteredVolumeUsage[1]");
        element1.setAttributeNS(SoapConstants.XSI_NAMESPACE, "xsi:type", "soapenc:Array");
        element.setAttributeNS(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE, "soapenv:encodingStyle", SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);
        SOAPElement item = result.addChildElement("result");
        Element itemElement = item;
        itemElement.setAttribute("href", "#id0");


        SOAPElement multiRef = body.addChildElement(new QName("", "multiRef"));
        org.w3c.dom.Element multiRefElement = (org.w3c.dom.Element) multiRef;

        // Add attributes in the desired order
        multiRefElement.setAttribute("id", "id0");
        multiRefElement.setAttributeNS(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE, "soapenc:root", "0");
        multiRefElement.setAttributeNS(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE, "soapenv:encodingStyle", SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);
        multiRefElement.setAttributeNS(SoapConstants.XSI_NAMESPACE, "xsi:type", "ns2:MeteredVolumeUsage");
        multiRefElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:soapenc", SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);
        multiRefElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns2", "http://npm.redback.com");

        SOAPElement serviceId = multiRef.addChildElement("serviceId");
        Element serviceIdElement = serviceId;
        serviceIdElement.setAttribute("xsi:type", "xsd:string");
        serviceIdElement.setTextContent(request.getPlanId().toString());

        SOAPElement serviceName = multiRef.addChildElement("serviceName");
        Element serviceNameElement = serviceName;
        serviceNameElement.setAttribute("xsi:type", "xsd:string");
        serviceNameElement.setTextContent(request.getPlanName());

        SOAPElement aggregateBytesLimit = multiRef.addChildElement("aggregateBytesLimit");
        Element aggregateytesLimitElement = aggregateBytesLimit;
        aggregateytesLimitElement.setAttribute("href", "#id1");

        SOAPElement aggregateBytesRemaining = multiRef.addChildElement("aggregateBytesRemaining");
        Element aggregateBytesRemainingElement = aggregateBytesRemaining;
        aggregateBytesRemainingElement.setAttribute("href", "#id2");

        SOAPElement aggregateBytesUsed = multiRef.addChildElement("aggregateBytesUsed");
        Element aggregateBytesUsedelement = aggregateBytesUsed;
        aggregateBytesUsedelement.setAttribute("href", "#id3");

        SOAPElement inBytesLimit = multiRef.addChildElement("inBytesLimit");
        Element inBytesLimitElement = inBytesLimit;
        inBytesLimitElement.setAttribute("href", "#id4");

        SOAPElement inBytesRemaining = multiRef.addChildElement("inBytesRemaining");
        Element inBytesTemainingElement = inBytesRemaining;
        inBytesTemainingElement.setAttribute("href", "#id5");

        SOAPElement inBytesUsed = multiRef.addChildElement("inBytesUsed");
        Element inBytesUsedElement = inBytesUsed;
        inBytesUsedElement.setAttribute("href", "#id6");

        SOAPElement outBytesLimit = multiRef.addChildElement("outBytesLimit");
        Element outBytesLimitElement = outBytesLimit;
        outBytesLimitElement.setAttribute("href", "#id7");

        SOAPElement outBytesRemaining = multiRef.addChildElement("outBytesRemaining");
        Element outBytesRemainingElement = outBytesRemaining;
        outBytesRemainingElement.setAttribute("href", "#id8");

        SOAPElement outBytesUsed = multiRef.addChildElement("outBytesUsed");
        Element outBytesUsedElement = outBytesUsed;
        outBytesUsedElement.setAttribute("href", "#id9");

//        set result value

        SOAPElement aggregateBytesLimitMultiRef = body.addChildElement(new QName("", "multiRef"));
        aggregateBytesLimitMultiRef.setAttribute("id", "id1");
        aggregateBytesLimitMultiRef.setAttributeNS(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE, "soapenc:root", "0");
        aggregateBytesLimitMultiRef.setAttributeNS(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE, "soapenv:encodingStyle", SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);
        aggregateBytesLimitMultiRef.setAttributeNS(SoapConstants.XSI_NAMESPACE, "xsi:type", "xsd:long");
        aggregateBytesLimitMultiRef.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:soapenc", SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);
        aggregateBytesLimitMultiRef.setTextContent(request.getAggregateBytesLimit().toString());

        SOAPElement aggregateBytesRemainingMultiRef = body.addChildElement(new QName("", "multiRef"));
        aggregateBytesRemainingMultiRef.setAttribute("id", "id2");
        aggregateBytesRemainingMultiRef.setAttributeNS(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE, "soapenc:root", "0");
        aggregateBytesRemainingMultiRef.setAttributeNS(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE, "soapenv:encodingStyle", SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);
        aggregateBytesRemainingMultiRef.setAttributeNS(SoapConstants.XSI_NAMESPACE, "xsi:type", "xsd:long");
        aggregateBytesRemainingMultiRef.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:soapenc", SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);
        aggregateBytesRemainingMultiRef.setTextContent(request.getAggregateBytesRemaining().toString());

        SOAPElement aggregateBytesUsedMultiRef = body.addChildElement(new QName("", "multiRef"));
        aggregateBytesUsedMultiRef.setAttribute("id", "id3");
        aggregateBytesUsedMultiRef.setAttributeNS(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE, "soapenc:root", "0");
        aggregateBytesUsedMultiRef.setAttributeNS(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE, "soapenv:encodingStyle", SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);
        aggregateBytesUsedMultiRef.setAttributeNS(SoapConstants.XSI_NAMESPACE, "xsi:type", "xsd:long");
        aggregateBytesUsedMultiRef.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:soapenc", SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);
        aggregateBytesUsedMultiRef.setTextContent(request.getAggregateBytesUsed().toString());

        SOAPElement inBytesLimitMultiRef = body.addChildElement(new QName("", "multiRef"));
        inBytesLimitMultiRef.setAttribute("id", "id4");
        inBytesLimitMultiRef.setAttributeNS(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE, "soapenc:root", "0");
        inBytesLimitMultiRef.setAttributeNS(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE, "soapenv:encodingStyle", SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);
        inBytesLimitMultiRef.setAttributeNS(SoapConstants.XSI_NAMESPACE, "xsi:type", "xsd:long");
        inBytesLimitMultiRef.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:soapenc", SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);
        inBytesLimitMultiRef.setTextContent(request.getInBytesLimit().toString());

        SOAPElement inBytesRemainingMultiRef = body.addChildElement(new QName("", "multiRef"));
        inBytesRemainingMultiRef.setAttribute("id", "id5");
        inBytesRemainingMultiRef.setAttributeNS(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE, "soapenc:root", "0");
        inBytesRemainingMultiRef.setAttributeNS(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE, "soapenv:encodingStyle", SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);
        inBytesRemainingMultiRef.setAttributeNS(SoapConstants.XSI_NAMESPACE, "xsi:type", "xsd:long");
        inBytesRemainingMultiRef.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:soapenc", SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);
        inBytesRemainingMultiRef.setTextContent(request.getInBytesRemaining().toString());

        SOAPElement inBytesUsedMultiRef = body.addChildElement(new QName("", "multiRef"));
        inBytesUsedMultiRef.setAttribute("id", "id6");
        inBytesUsedMultiRef.setAttributeNS(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE, "soapenc:root", "0");
        inBytesUsedMultiRef.setAttributeNS(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE, "soapenv:encodingStyle", SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);
        inBytesUsedMultiRef.setAttributeNS(SoapConstants.XSI_NAMESPACE, "xsi:type", "xsd:long");
        inBytesUsedMultiRef.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:soapenc", SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);
        inBytesUsedMultiRef.setTextContent(request.getInBytesUsed().toString());

        SOAPElement outBytesLimitMultiRef = body.addChildElement(new QName("", "multiRef"));
        outBytesLimitMultiRef.setAttribute("id", "id7");
        outBytesLimitMultiRef.setAttributeNS(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE, "soapenc:root", "0");
        outBytesLimitMultiRef.setAttributeNS(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE, "soapenv:encodingStyle", SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);
        outBytesLimitMultiRef.setAttributeNS(SoapConstants.XSI_NAMESPACE, "xsi:type", "xsd:long");
        outBytesLimitMultiRef.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:soapenc", SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);
        outBytesLimitMultiRef.setTextContent(request.getOutBytesLimit().toString());

        SOAPElement outBytesRemainingMultiRef = body.addChildElement(new QName("", "multiRef"));
        outBytesRemainingMultiRef.setAttribute("id", "id8");
        outBytesRemainingMultiRef.setAttributeNS(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE, "soapenc:root", "0");
        outBytesRemainingMultiRef.setAttributeNS(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE, "soapenv:encodingStyle", SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);
        outBytesRemainingMultiRef.setAttributeNS(SoapConstants.XSI_NAMESPACE, "xsi:type", "xsd:long");
        outBytesRemainingMultiRef.setAttributeNS("http://www.w3.org/2000/xmlns/","xmlns:soapenc", SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);
        outBytesRemainingMultiRef.setTextContent(request.getOutBytesRemaining().toString());

        SOAPElement outBytesUsedMultiRef = body.addChildElement(new QName("", "multiRef"));
        outBytesUsedMultiRef.setAttribute("id", "id9");
        outBytesUsedMultiRef.setAttributeNS(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE, "soapenc:root", "0");
        outBytesUsedMultiRef.setAttributeNS(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE, "soapenv:encodingStyle", SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);
        outBytesUsedMultiRef.setAttributeNS(SoapConstants.XSI_NAMESPACE, "xsi:type", "xsd:long");
        outBytesUsedMultiRef.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:soapenc", SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);
        outBytesUsedMultiRef.setTextContent(request.getOutBytesUsed().toString());


        // Add the message element
//        removeNamespace(multiRef, "soapenv");


        // Save changes and verify structure
        soapMessage.saveChanges();

        // Convert body to DOMSource for return
        Document document = body.getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();
        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }
        return new DOMSource(fragment);
    }

    /**
     * Generates a SOAP 1.1 Success response for the "MeteredVolumeUsageForSubAcctName" operation.
     *
//     * @param booleanExpression   for adding data according requirement
     * @param messageContext    The MessageContext to update with the generated SOAP message.
     * @return DOMSource        The generated SOAP exception response as a DOMSource.
     * @throws SOAPException    If an error occurs during SOAP message creation.
     */
    public DOMSource generateMeteredVolumeUsageForSubAcctNameSuccessResponse(MeteredVolumeUsageForSubsAccNameDTO request,MessageContext messageContext) throws SOAPException {
        // Create a SOAP 1.1 Message factory
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        // Declare the required namespaces explicitly at the envelope level
        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
        envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        envelope.setPrefix("soapenv");

        // Create SOAP Body and Header
        SOAPBody body = envelope.getBody();
        body.setPrefix("soapenv");
        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode();
        }

        SOAPElement MeteredVolumeUsage = body.addChildElement("getMeteredVolumeUsageForSubAcctNameResponse","ns1","http://npm.redback.com");
        MeteredVolumeUsage.setAttributeNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/");
        Element element = MeteredVolumeUsage;
        SOAPElement result = MeteredVolumeUsage.addChildElement("result");
        Element element1 = result;
        element1.setAttributeNS("http://schemas.xmlsoap.org/soap/encoding/", "soapenc:arrayType", "ns1:MeteredVolumeUsage[1]");
        element1.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:type", "soapenc:Array");
        element.setAttributeNS("http://schemas.xmlsoap.org/soap/encoding/", "soapenv:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/");
        SOAPElement item = result.addChildElement("result");
        Element itemElement = item;
        itemElement.setAttribute("href", "#id0");

        SOAPElement multiRef = body.addChildElement(new QName("", "multiRef"));
        org.w3c.dom.Element multiRefElement = (org.w3c.dom.Element) multiRef;

        // Add attributes in the desired order
        multiRefElement.setAttribute("id", "id0");
        multiRefElement.setAttributeNS("http://schemas.xmlsoap.org/soap/encoding/", "soapenc:root", "0");
        multiRefElement.setAttributeNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/");
        multiRefElement.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:type", "ns2:MeteredVolumeUsage");
        multiRefElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:soapenc", "http://schemas.xmlsoap.org/soap/encoding/");
        multiRefElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns2", "http://npm.redback.com");

        SOAPElement serviceId = multiRef.addChildElement("serviceId");
        serviceId.setAttribute("xsi:type", "xsd:string");
        serviceId.setTextContent(request.getPlanName());

        SOAPElement serviceName = multiRef.addChildElement("serviceName");
        serviceName.setAttribute("xsi:type", "xsd:string");
        serviceName.setTextContent(request.getPlanName());

        multiRef.addChildElement("aggregateBytesLimit").setAttribute("href", "#id1");

        multiRef.addChildElement("aggregateBytesRemaining").setAttribute("href", "#id2");

        Element aggregateBytesUsedelement = multiRef.addChildElement("aggregateBytesUsed");
        aggregateBytesUsedelement.setAttribute("href", "#id3");

        multiRef.addChildElement("inBytesLimit").setAttribute("href", "#id4");

        multiRef.addChildElement("inBytesRemaining").setAttribute("href", "#id5");

        multiRef.addChildElement("inBytesUsed").setAttribute("href", "#id6");

        Element outBytesLimitElement = multiRef.addChildElement("outBytesLimit");
        outBytesLimitElement.setAttribute("href", "#id7");

        multiRef.addChildElement("outBytesRemaining").setAttribute("href", "#id8");

        multiRef.addChildElement("outBytesUsed").setAttribute("href", "#id9");


        // Add the multiRef elements with the correct values
        SOAPElement aggregateBytesLimitMultiRef = body.addChildElement(new QName("", "multiRef"));
        aggregateBytesLimitMultiRef.setAttribute("id", "id3");
        aggregateBytesLimitMultiRef.setAttributeNS("http://schemas.xmlsoap.org/soap/encoding/", "soapenc:root", "0");
        aggregateBytesLimitMultiRef.setAttributeNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/");
        aggregateBytesLimitMultiRef.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:type", "xsd:long");
        aggregateBytesLimitMultiRef.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:soapenc", "http://schemas.xmlsoap.org/soap/encoding/");
        Long usedQuotaByte = commonUtilityService.calCulateBytes(request.getAggregateBytesUsed(),request.getQuotaUnit());
        aggregateBytesLimitMultiRef.setTextContent(usedQuotaByte.toString());  // Set the value here

        SOAPElement aggregateBytesRemainingMultiRef = body.addChildElement(new QName("", "multiRef"));
        aggregateBytesRemainingMultiRef.setAttribute("id", "id4");
        aggregateBytesRemainingMultiRef.setAttributeNS("http://schemas.xmlsoap.org/soap/encoding/", "soapenc:root", "0");
        aggregateBytesRemainingMultiRef.setAttributeNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/");
        aggregateBytesRemainingMultiRef.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:type", "xsd:long");
        aggregateBytesRemainingMultiRef.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:soapenc", "http://schemas.xmlsoap.org/soap/encoding/");
        aggregateBytesRemainingMultiRef.setTextContent("0");  // Set the value here

        SOAPElement aggregateBytesUsedMultiRef = body.addChildElement(new QName("", "multiRef"));
        aggregateBytesUsedMultiRef.setAttribute("id", "id5");
        aggregateBytesUsedMultiRef.setAttributeNS("http://schemas.xmlsoap.org/soap/encoding/", "soapenc:root", "0");
        aggregateBytesUsedMultiRef.setAttributeNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/");
        aggregateBytesUsedMultiRef.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:type", "xsd:long");
        aggregateBytesUsedMultiRef.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:soapenc", "http://schemas.xmlsoap.org/soap/encoding/");
        aggregateBytesUsedMultiRef.setTextContent("0");  // Set the value here

        SOAPElement inBytesLimitMultiRef = body.addChildElement(new QName("", "multiRef"));
        inBytesLimitMultiRef.setAttribute("id", "id6");
        inBytesLimitMultiRef.setAttributeNS("http://schemas.xmlsoap.org/soap/encoding/", "soapenc:root", "0");
        inBytesLimitMultiRef.setAttributeNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/");
        inBytesLimitMultiRef.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:type", "xsd:long");
        inBytesLimitMultiRef.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:soapenc", "http://schemas.xmlsoap.org/soap/encoding/");
        Long uploadOctateByte = request.getUploadOctate().longValue();
        inBytesLimitMultiRef.setTextContent(uploadOctateByte.toString());  // Set the value here

        SOAPElement inBytesRemainingMultiRef = body.addChildElement(new QName("", "multiRef"));
        inBytesRemainingMultiRef.setAttribute("id", "id9");
        inBytesRemainingMultiRef.setAttributeNS("http://schemas.xmlsoap.org/soap/encoding/", "soapenc:root", "0");
        inBytesRemainingMultiRef.setAttributeNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/");
        inBytesRemainingMultiRef.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:type", "xsd:long");
        inBytesRemainingMultiRef.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:soapenc", "http://schemas.xmlsoap.org/soap/encoding/");
        Long downloadOctateByte = request.getDownloadOctate().longValue();
        inBytesRemainingMultiRef.setTextContent(downloadOctateByte.toString());  // Set the value here

        SOAPElement inBytesUsedMultiRef = body.addChildElement(new QName("", "multiRef"));
        inBytesUsedMultiRef.setAttribute("id", "id2");
        inBytesUsedMultiRef.setAttributeNS("http://schemas.xmlsoap.org/soap/encoding/", "soapenc:root", "0");
        inBytesUsedMultiRef.setAttributeNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/");
        inBytesUsedMultiRef.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:type", "xsd:long");
        inBytesUsedMultiRef.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:soapenc", "http://schemas.xmlsoap.org/soap/encoding/");
        Long remainingQuotaByte = commonUtilityService.calCulateBytes(request.getAggregateBytesRemaining(),request.getQuotaUnit());
        inBytesUsedMultiRef.setTextContent(remainingQuotaByte.toString());  // Set the value here

        SOAPElement outBytesLimitMultiRef = body.addChildElement(new QName("", "multiRef"));
        outBytesLimitMultiRef.setAttribute("id", "id1");
        outBytesLimitMultiRef.setAttributeNS("http://schemas.xmlsoap.org/soap/encoding/", "soapenc:root", "0");
        outBytesLimitMultiRef.setAttributeNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/");
        outBytesLimitMultiRef.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:type", "xsd:long");
        outBytesLimitMultiRef.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:soapenc", "http://schemas.xmlsoap.org/soap/encoding/");
        Long totalQuotaByte = commonUtilityService.calCulateBytes(request.getAggregateBytesLimit(),request.getQuotaUnit());
        outBytesLimitMultiRef.setTextContent(totalQuotaByte.toString());  // Set the value here

        SOAPElement outBytesRemainingMultiRef = body.addChildElement(new QName("", "multiRef"));
        outBytesRemainingMultiRef.setAttribute("id", "id8");
        outBytesRemainingMultiRef.setAttributeNS("http://schemas.xmlsoap.org/soap/encoding/", "soapenc:root", "0");
        outBytesRemainingMultiRef.setAttributeNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/");
        outBytesRemainingMultiRef.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:type", "xsd:long");
        outBytesRemainingMultiRef.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:soapenc", "http://schemas.xmlsoap.org/soap/encoding/");
        outBytesRemainingMultiRef.setTextContent("0");  // Set the value here

        SOAPElement outBytesUsedMultiRef = body.addChildElement(new QName("", "multiRef"));
        outBytesUsedMultiRef.setAttribute("id", "id7");
        outBytesUsedMultiRef.setAttributeNS("http://schemas.xmlsoap.org/soap/encoding/", "soapenc:root", "0");
        outBytesUsedMultiRef.setAttributeNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/");
        outBytesUsedMultiRef.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:type", "xsd:long");
        outBytesUsedMultiRef.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:soapenc", "http://schemas.xmlsoap.org/soap/encoding/");
        outBytesUsedMultiRef.setTextContent("0");



        // If the MessageContext is provided, update the response
        if (messageContext != null) {
            SaajSoapMessage updateResponse = (SaajSoapMessage) messageContext.getResponse();
            updateResponse.setSaajMessage(soapMessage);
            updateResponse.getSaajMessage().saveChanges();
        }
        // Save changes to the SOAP message
        soapMessage.saveChanges();

        // Convert the SOAP body to DOMSource for the response
        Document document = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();
        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }

        return new DOMSource(fragment);
    }

    /**
     * Generates a SOAP 1.1 exception response for the "MeteredVolumeUsageForSubAcctName" operation.
     *
     * @param faultCodeString   The fault code to include in the response.
     * @param exceptionNameString The name of the exception to include in the response.
     * @param exceptionMessage  The detailed message describing the exception.
     * @param host              The host where the exception occurred.
     * @param messageContext    The MessageContext to update with the generated SOAP message.
     * @return DOMSource        The generated SOAP exception response as a DOMSource.
     * @throws SOAPException    If an error occurs during SOAP message creation.
     */
    public static DOMSource generateMeteredVolumeUsageForSubAcctNameSOAP11ExceptionResponse(String faultCodeString, String exceptionNameString,
                                                                                         String exceptionMessage, String host, MessageContext
                                                                                                 messageContext) throws SOAPException {
        // Create a SOAP 1.1 Message factory
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        // Remove default SOAP namespaces and declare custom namespaces
        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
        envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");

        // Set the envelope prefix to "soapenv"
        envelope.setPrefix("soapenv");

        // Set up the Body and remove any pre-existing header if present
        SOAPBody body = envelope.getBody();
        body.setPrefix("soapenv");
        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode(); // Remove the header if it exists
        }

        // Add the Fault element to the SOAP body
        SOAPElement faultElement = body.addChildElement("Fault", "soapenv");

        // Add the fault code to the fault element
        SOAPElement faultcode = faultElement.addChildElement("faultcode");
        faultcode.addTextNode("soapenv:Server." + faultCodeString);

        // Add the faultstring element (typically provides a message, left empty here)
        SOAPElement faultstring = faultElement.addChildElement("faultstring");
        faultstring.addTextNode(""); // You can customize this message if needed

        // Add the Detail element, which contains more specific error information
        SOAPElement detail = faultElement.addChildElement("detail");

        // Add the SQLException reference (this example uses a custom namespace)
        SOAPElement sqlException = detail.addChildElement(exceptionNameString, "ns1", "http://npm.redback.com");
        sqlException.addAttribute(new QName("href"), "#id0");

        // Add exception name element with a namespace (usefully indicates the class name of the exception)
        SOAPElement exceptionName = detail.addChildElement("exceptionName", "ns2", "http://xml.apache.org/axis/");
        exceptionName.addTextNode("com.redback.npm." + exceptionNameString);

        // Add hostname element indicating where the exception occurred
        SOAPElement hostname = detail.addChildElement("hostname", "ns3", "http://xml.apache.org/axis/");
        hostname.addTextNode(host);

        // Add a multiRef element (for serialization or further references)
        SOAPElement multiRefElement = body.addChildElement("multiRef");

        // Set attributes for multiRef to indicate a specific type and reference
        multiRefElement.setAttribute("id", "id0");
        multiRefElement.setAttributeNS("http://schemas.xmlsoap.org/soap/encoding/", "soapenc:root", "0");
        multiRefElement.setAttributeNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/");
        multiRefElement.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:type", "ns4:" + exceptionNameString);

        // Add required namespaces for multiRef
        multiRefElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:soapenc", "http://schemas.xmlsoap.org/soap/encoding/");
        multiRefElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns4", "http://npm.redback.com");

        // Add the exception message element with the exception message
        SOAPElement message = multiRefElement.addChildElement("message");
        message.addAttribute(new QName("http://www.w3.org/2001/XMLSchema-instance", "type", "xsi"), "xsd:string");
        message.addTextNode(exceptionMessage);

        // Save changes to the SOAP message
        soapMessage.saveChanges();

        // If the MessageContext is provided, update the response with the generated SOAP message
        if (messageContext != null) {
            SaajSoapMessage updateResponse = (SaajSoapMessage) messageContext.getResponse();
            updateResponse.setSaajMessage(soapMessage);
            updateResponse.getSaajMessage().saveChanges();
        }

        // Convert the SOAP body to DOMSource for the response
        Document document = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();
        NodeList childNodes = body.getChildNodes();

        // Clone child nodes from the body and append them to the fragment
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }

        // Return the response as a DOMSource
        return new DOMSource(fragment);
    }



}
