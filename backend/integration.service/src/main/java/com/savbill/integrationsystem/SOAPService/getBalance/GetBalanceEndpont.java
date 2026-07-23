package com.savbill.integrationsystem.SOAPService.getBalance;

import com.savbill.integrationsystem.RestApiService.getBalance.WsGetBalanceRequest;
import com.savbill.integrationsystem.SOAPService.Interface.CmsClient;
import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.SOAPService.authenticateUserService.AuthenticatUserEndpoint;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.NodeList;

import javax.xml.soap.*;
import javax.xml.transform.dom.DOMSource;
import java.util.Date;
import java.util.List;

import static com.savbill.integrationsystem.SOAPService.removeSubscriberAccount.RemoveSubscriberAccountEndpoint.getExceptionsInResponse;
@Slf4j
@Endpoint
public class GetBalanceEndpont {

    private final Logger logger = LoggerFactory.getLogger(AuthenticatUserEndpoint.class);

    @Autowired
    private CmsClientService cmsClientService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    public CmsClient cmsClient;

    @Autowired
    private RadiusClientService radiusClientService;

    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI_NEW, localPart = "wsGetBalance")
    @ResponsePayload
    public DOMSource getBalanceList(@RequestPayload WsGetBalanceRequest request) throws Exception {
        long startTime = System.currentTimeMillis(); // Capture start time
        String username = request.getSubscriberId().trim();
        log.info("Started getBalanceList for username: {} at {}", username, new Date(startTime));
        String exceptionMessage = "";
        String faultMessage = "";
        String exceptionName = "wsGetBalanceResponse";

        if (username == null || username.isEmpty()) {
            log.warn("Input Username is Empty or Null for request at: {}", new Date(startTime));
            return getExceptionsInResponse("generalException",
                    "InvalidSubscriberAccountException",
                    "Input Username is Empty or Null",
                    "ecaaa1");
        }
        try {
            log.info("Completed getBalanceList in {} ms", System.currentTimeMillis() - startTime);
            return getBalanceData(username, "wsGetBalanceResponse");

        } catch (Exception e) {
            exceptionMessage = e.getMessage() != null ? e.getMessage() : "Exception was encountered during processing Request.";
            faultMessage = "generalException";
            exceptionName = "InvalidSubscriberAccountException";
            log.error("Error encountered in getBalanceList for username: {}. Error: {}", username, exceptionMessage);
        }
        log.info("Completed getBalanceList with exception in {} ms", System.currentTimeMillis() - startTime);
        return getExceptionsInResponse(faultMessage,
                exceptionName,
                exceptionMessage,
                "ecaaa1");
    }

    public DOMSource getBalanceData(String username, String localName) throws Exception {
        long startTime = System.currentTimeMillis(); // Capture start time
        log.info("Started getBalanceData for username: {} at {}", username, new Date(startTime));

        try {
            // Get data from service
            log.debug("Radius Client Calling To Check Customer Quota Details :{} ",username);
            GenericDataDTO genericDataDTO = radiusClientService.getCustQoutaDetails(username, SoapConstants.MVNOID);
            log.debug("Radius Client Retrieve Data :{} ",genericDataDTO.getData());

            List<WsGetBalanceRequestDTO> dataMessageList = new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .readValue(
                            new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(genericDataDTO.getData()),
                            new TypeReference<List<WsGetBalanceRequestDTO>>() {
                            });

            // Initialize SOAP Message
            MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
            SOAPMessage soapMessage = factory.createMessage();
            SOAPPart soapPart = soapMessage.getSOAPPart();
            SOAPEnvelope envelope = soapPart.getEnvelope();
            envelope.addNamespaceDeclaration("ns2", "http://subscription.ws.nvsmx.elitecore.com/");
            envelope.addNamespaceDeclaration("soap", "http://www.w3.org/2003/05/soap-envelope");
            SOAPBody body = envelope.getBody();

            // Build wsGetBalanceResponse
            SOAPElement wsGetBalanceResponse = body.addChildElement(localName, "ns2");
            SOAPElement returnElement = wsGetBalanceResponse.addChildElement("return");

            // Add response code and message
            addChildElement(returnElement, "responseCode", "200");
            addChildElement(returnElement, "responseMessage", "SUCCESS");

            // Add subscription information
            for (WsGetBalanceRequestDTO dataMessage : dataMessageList) {
                SOAPElement subscriptionInformations = returnElement.addChildElement("subscriptionInformations");
                // Static or dynamic values
                addChildElement(subscriptionInformations, "packageId", dataMessage.getPackageId().toString());
                addChildElement(subscriptionInformations, "packageName", dataMessage.getPackageName());
                addChildElement(subscriptionInformations, "packageType", dataMessage.getPackageType());

                if (!dataMessage.getPackageType().isEmpty() && (dataMessage.getPackageType().equalsIgnoreCase("SPARETOPUP")
                        || dataMessage.getPackageType().equalsIgnoreCase("ADDON"))) {
                    addChildElement(subscriptionInformations, "addOnStatus", dataMessage.getAddOnStatus().toString());
                    addChildElement(subscriptionInformations, "addonSubscriptionId", dataMessage.getAddonSubscriptionId());
                    addChildElement(subscriptionInformations, "endTime", dataMessage.getEndTime());
                    addChildElement(subscriptionInformations, "startTime", dataMessage.getStartTime());
                }

                // Add quotaProfileBalances
                SOAPElement quotaProfileBalances = subscriptionInformations.addChildElement("quotaProfileBalances");

                // Add allServiceBalance
                SOAPElement allServiceBalance = quotaProfileBalances.addChildElement("allServiceBalance");
                addChildElement(allServiceBalance, "aggregationKey", "Billing Cycle");

                SOAPElement balance = allServiceBalance.addChildElement("balance");
                addChildElement(balance, "downloadOctets", "150310449223");
                addChildElement(balance, "time", "-1");
                addChildElement(balance, "totalOctets", "-1");
                addChildElement(balance, "uploadOctets", "-1");

                // Add curretUsage
                SOAPElement currentUsage = allServiceBalance.addChildElement("curretUsage");
                addChildElement(currentUsage, "downloadOctets", "13406137");
                addChildElement(currentUsage, "time", "-1");
                addChildElement(currentUsage, "totalOctets", "-1");
                addChildElement(currentUsage, "uploadOctets", "-1");

                // Add HSQLimit
                SOAPElement HSQLimit = allServiceBalance.addChildElement("HSQLimit");
                addChildElement(HSQLimit, "downloadOctets", "150323855360");
                addChildElement(HSQLimit, "time", "-1");
                addChildElement(HSQLimit, "totalOctets", "-1");
                addChildElement(HSQLimit, "uploadOctets", "-1");

                addChildElement(allServiceBalance, "serviceId", "SERVICE_TYPE_1");
                addChildElement(allServiceBalance, "serviceName", "All-Service");

                addChildElement(quotaProfileBalances, "quotaProfileId", "d0a469f4-5da8-476e-8d84-a5877544a54b");
                addChildElement(quotaProfileBalances, "quotaProfileName", "DOWNLOAD_QP");
            }
            soapMessage.saveChanges();

            Document document = body.getOwnerDocument();
            DocumentFragment fragment = document.createDocumentFragment();
            NodeList childNodes = body.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                fragment.appendChild(childNodes.item(i).cloneNode(true));
            }
            log.info("Completed getBalanceData in {} ms for username: {}", System.currentTimeMillis() - startTime, username);
            return new DOMSource(fragment);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error generating SOAP response for username: {}. Error: {}", username, e.getMessage());
            throw new Exception("Error generating SOAP response", e);
        }
    }

    // Utility method to add child elements with text content
    private void addChildElement(SOAPElement parent, String name, String value) throws SOAPException {
        SOAPElement child = parent.addChildElement(name);
        child.setTextContent(value);
    }
}
