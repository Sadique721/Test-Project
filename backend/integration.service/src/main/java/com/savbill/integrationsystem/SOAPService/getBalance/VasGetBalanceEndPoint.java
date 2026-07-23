package com.savbill.integrationsystem.SOAPService.getBalance;

import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.generated.vasnewgetbalance.*;
import com.savbill.integrationsystem.utility.CommonUtilityService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.transform.dom.DOMSource;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Slf4j
@Endpoint
public class VasGetBalanceEndPoint {

    @Autowired
    private RadiusClientService radiusClientService;
    @Autowired
    public CommonUtilityService commonUtilityService;

    @PayloadRoot(namespace = SoapConstants.NAMESPACE_ELITECORE, localPart = "wsGetBalance")
    @ResponsePayload
    public WsGetBalanceResponse getBalanceList(@RequestPayload WsGetBalance request, MessageContext messageContext) throws Exception {
        long startTime = System.currentTimeMillis();
        String username = request.getSubscriberId();
        String exceptionMessage = "";
        String faultMessage = "";
        String exceptionName = "wsGetBalanceResponse";
        log.info("getBalanceList method started at: {} for subscriberId: {}", new Date(startTime), username);
        if (username == null || username.trim().isEmpty()) {
//            return getExceptionsInResponse("generalException",
//                    "InvalidSubscriberAccountException",
//                    "Input Username is Empty or Null",
//                    "ecaaa1",messageContext);
            log.warn("InvalidSubscriberAccountException: Input Username is Empty or Null");
            WsGetBalanceResponse response = new WsGetBalanceResponse();
            GetBalanceEnquiryResponse enquiryResponse = new GetBalanceEnquiryResponse();
            enquiryResponse.setResponseCode(401);
            enquiryResponse.setResponseMessage("INPUT PARAMETER MISSING. Reason: Identity parameter missing");
            response.setReturn(enquiryResponse);
            log.info("getBalanceList method completed IN:{}MS", (System.currentTimeMillis() - startTime));
            return response;
        }
        try {
        /*    GenericDataDTO genericDataDTO = radiusClientService.getCustomerDetails(request.getSubscriberId().trim().toLowerCase(), SoapConstants.MVNOID);
            Map<String, Object> mapData = (Map<String, Object>) genericDataDTO.getData();
            if (Objects.nonNull(mapData)) {
                String userNm = mapData.get("username").toString();
                if (request.getSubscriberId().equalsIgnoreCase(userNm)) {
                    return getBalanceData(username, "wsGetBalanceResponse",messageContext);
                }else {
                    exceptionMessage = "No data found for the provided username.";
                    faultMessage = "generalException";
                    exceptionName = "InvalidSubscriberAccountException";
                    return getExceptionsInResponse(faultMessage,
                            exceptionName,
                            exceptionMessage,
                            "ecaaa1",messageContext);
                }
            }else {
                exceptionMessage = "No data found for the provided username.";
                faultMessage = "generalException";
                exceptionName = "InvalidSubscriberAccountException";
                return getExceptionsInResponse(faultMessage,
                        exceptionName,
                        exceptionMessage,
                        "ecaaa1",messageContext);
            }*/

            if (request.getSubscriberId() != null) {
                log.debug("Radius Client Calling To Check Customer Quota Details :{} ", username);
                GenericDataDTO genericDataDTO = radiusClientService.getCustQoutaDetails(username, SoapConstants.MVNOID);
                log.debug("Radius Client Retrieve Data IN:{}Ms,Response:{} ",System.currentTimeMillis()-startTime,genericDataDTO.getData());
//                Map<String, Object> mapData = (Map<String, Object>) genericDataDTO.getData();
                boolean message = false;
                if (genericDataDTO != null) {
                    if (genericDataDTO.getResponseMessage() != null) {
                        message = genericDataDTO.getResponseMessage().toString().equalsIgnoreCase("No Records Found");
                    }
                    if (message) {
                        exceptionMessage = "NOT FOUND. Reason: Subscriber not found with subscriber identity:"+username;
                        faultMessage = "generalException";
                        exceptionName = "InvalidSubscriberAccountException";
//                        return getExceptionsInResponse(faultMessage,
//                                exceptionName,
//                                exceptionMessage,
//                                "ecaaa1",messageContext);
                        log.warn("InvalidSubscriberAccountException: No data found for the provided username.");
                        WsGetBalanceResponse response = new WsGetBalanceResponse();
                        GetBalanceEnquiryResponse enquiryResponse = new GetBalanceEnquiryResponse();
                        enquiryResponse.setResponseCode(404);
                        enquiryResponse.setResponseMessage("NOT FOUND. Reason: Subscriber not found with subscriber identity:"+username);
                        response.setReturn(enquiryResponse);
                        log.info("getBalanceList method completed IN:{}MS", (System.currentTimeMillis() - startTime));
                        return response;
                    } else {
                        log.info("getBalanceList method completed IN:{}MS", (System.currentTimeMillis() - startTime));
                        return getBalanceData1(genericDataDTO, "wsGetBalanceResponse", username, messageContext);
                    }
                }

            } else {
                exceptionMessage = "NOT FOUND. Reason: Subscriber not found with subscriber identity:"+username;
                faultMessage = "generalException";
                exceptionName = "InvalidSubscriberAccountException";
//                return getExceptionsInResponse(faultMessage,
//                        exceptionName,
//                        exceptionMessage,
//                        "ecaaa1",messageContext);
                log.warn("InvalidSubscriberAccountException: No data found for the provided username.");
                WsGetBalanceResponse response = new WsGetBalanceResponse();
                GetBalanceEnquiryResponse enquiryResponse = new GetBalanceEnquiryResponse();
                enquiryResponse.setResponseCode(404);
                enquiryResponse.setResponseMessage(exceptionMessage);
                response.setReturn(enquiryResponse);
                log.info("getBalanceList method completed IN:{}MS", (System.currentTimeMillis() - startTime));
                return response;
            }

        } catch (Exception e) {
            exceptionMessage = e.getMessage() != null ? e.getMessage() : "Exception was encountered during processing Request.";
            faultMessage = "generalException";
            exceptionName = "InvalidSubscriberAccountException";
            log.error("Exception occurred in getBalanceList method: {}", exceptionMessage, e.getMessage());
        }
//        return getExceptionsInResponse(faultMessage,
//                exceptionName,
//                exceptionMessage,
//                "ecaaa1",messageContext);
        long endTime = System.currentTimeMillis();
        log.info("getBalanceList method completed in {} ms", (endTime - startTime));
        WsGetBalanceResponse response = new WsGetBalanceResponse();
        GetBalanceEnquiryResponse enquiryResponse = new GetBalanceEnquiryResponse();
        enquiryResponse.setResponseCode(400);
        enquiryResponse.setResponseMessage(exceptionMessage);
        response.setReturn(enquiryResponse);
        return response;
    }

    public DOMSource getBalanceData(GenericDataDTO genericDataDTO, String localName, MessageContext messageContext) throws Exception {
        try {
            // Get data from service
//            GenericDataDTO genericDataDTO = radiusClientService.getCustQoutaDetails(username, SoapConstants.MVNOID);

            long startTime = System.currentTimeMillis();
            log.info("getBalanceData method started with startTime: {}",startTime);

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

            // Build wsGetBalanceResponse
            SOAPElement wsGetBalanceResponse = body.addChildElement(new QName("", "ns2:".concat(localName)));
            wsGetBalanceResponse.setAttribute("xmlns:ns2", "http://subscription.ws.nvsmx.elitecore.com/");
            SOAPElement returnElement = wsGetBalanceResponse.addChildElement("return");
            addChildElement(returnElement, "parameter1", "?");
            addChildElement(returnElement, "parameter2", "?");
            // Add response code and message
            addChildElement(returnElement, "responseCode", "200");
            addChildElement(returnElement, "responseMessage", "SUCCESS");
            // Add subscription information
            for (WsGetBalanceRequestDTO dataMessage : dataMessageList) {
                SOAPElement subscriptionInformations = returnElement.addChildElement("subscriptionInformations");
                long totalQoutaInBytesForHSQ = 0l;
                long totalQoutaInBytesForCurrent = 0l;
                long totalQoutaInBytesForBalance = 0l;
                double hsqLimit = 0.0;
                double current = 0.0;
                double balanceL = 0.0;
                hsqLimit = Double.parseDouble(dataMessage.getHsqLimit());
                current = (dataMessage.getUsedQuota() + dataMessage.getCurrentSessionUsageVolume());
                balanceL = dataMessage.getTotalQuota() - (dataMessage.getUsedQuota() + dataMessage.getCurrentSessionUsageVolume());
                if (!dataMessage.getPackageType().isEmpty() && (dataMessage.getPackageType().equalsIgnoreCase("SPARETOPUP")
                        || dataMessage.getPackageType().equalsIgnoreCase("ADDON"))) {
                    addChildElement(subscriptionInformations, "addOnStatus", dataMessage.getAddOnStatus().toString()!= null ? dataMessage.getAddOnStatus():"");
                    addChildElement(subscriptionInformations, "addonSubscriptionId", dataMessage.getCprId()!=null ? dataMessage.getCprId().toString():"");
                    addChildElement(subscriptionInformations, "endTime", dataMessage.getEndTime()!=null ? String.valueOf(dataMessage.getEndTime()): "");
                    addChildElement(subscriptionInformations, "startTime", dataMessage.getStartTime());
                }
                // Static or dynamic values
                addChildElement(subscriptionInformations, "packageId", dataMessage.getPackageId());
                addChildElement(subscriptionInformations, "packageName", dataMessage.getPackageName() != null ? dataMessage.getPackageName() : "");
                addChildElement(subscriptionInformations, "packageType", dataMessage.getPackageType() != null ? dataMessage.getPackageType() : "");

                // Add quotaProfileBalances
                SOAPElement quotaProfileBalances = subscriptionInformations.addChildElement("quotaProfileBalances");

                // Add allServiceBalance
                SOAPElement allServiceBalance = quotaProfileBalances.addChildElement("allServiceBalance");
                addChildElement(allServiceBalance, "aggregationKey", "Billing Cycle");
                totalQoutaInBytesForHSQ = commonUtilityService.calCulateBytes(hsqLimit, dataMessage.getQuotaUnit());
                totalQoutaInBytesForCurrent = commonUtilityService.calCulateBytes(current, dataMessage.getQuotaUnit());
                totalQoutaInBytesForBalance = commonUtilityService.calCulateBytes(balanceL, dataMessage.getQuotaUnit());
                SOAPElement balance = allServiceBalance.addChildElement("balance");
                addChildElement(balance, "downloadOctets", "-1");
                addChildElement(balance, "time", "-1");
                addChildElement(balance, "totalOctets", String.valueOf(totalQoutaInBytesForBalance));
                addChildElement(balance, "uploadOctets", "-1");

                // Add curretUsage
                SOAPElement currentUsage = allServiceBalance.addChildElement("curretUsage");
                addChildElement(currentUsage, "downloadOctets", dataMessage.getDownloadOctet());
                addChildElement(currentUsage, "time", "-1");
                addChildElement(currentUsage, "totalOctets", String.valueOf(totalQoutaInBytesForCurrent));
                addChildElement(currentUsage, "uploadOctets", dataMessage.getUploadOctet());

                // Add HSQLimit
                SOAPElement HSQLimit = allServiceBalance.addChildElement("HSQLimit");
                addChildElement(HSQLimit, "downloadOctets", "-1");
                addChildElement(HSQLimit, "time", "-1");
                addChildElement(HSQLimit, "totalOctets", String.valueOf(totalQoutaInBytesForHSQ));
                addChildElement(HSQLimit, "uploadOctets", "-1");

                addChildElement(allServiceBalance, "serviceId", dataMessage.getPackageId() != null ? dataMessage.getPackageId() : "");
                addChildElement(allServiceBalance, "serviceName", "All _Service ");

                addChildElement(quotaProfileBalances, "quotaProfileId", dataMessage.getQuotaProfileId() != null ? dataMessage.getQuotaProfileId() : "");
                addChildElement(quotaProfileBalances, "quotaProfileName", dataMessage.getQuotaProfileName() != null ? dataMessage.getQuotaProfileName() : "");
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

            long endTime = System.currentTimeMillis();
            log.info("getBalanceData method completed in {} ms", (endTime - startTime));
            return new DOMSource(fragment);

        } catch (Exception e) {
            log.error("Exception occurred in getBalanceData method: {}", e.getMessage(), e);
            e.printStackTrace();
            throw new Exception("Error generating SOAP response", e);
        }
    }


    public WsGetBalanceResponse getBalanceData1(GenericDataDTO genericDataDTO, String localName, String username,MessageContext messageContext) throws Exception {
        long startTime = System.currentTimeMillis();
        log.info("getBalanceData1 method started At:{}MS", new Date(startTime));
        try {
            // Get data from service
//            GenericDataDTO genericDataDTO = radiusClientService.getCustQoutaDetails(username, SoapConstants.MVNOID);

            WsGetBalanceResponse response = new WsGetBalanceResponse();
            GetBalanceEnquiryResponse enquiryResponse = new GetBalanceEnquiryResponse();
            enquiryResponse.setResponseCode(200);
            enquiryResponse.setResponseMessage("SUCCESS");

            log.debug("Parsing genericDataDTO to List<WsGetBalanceRequestDTO>");
            List<WsGetBalanceRequestDTO> dataMessageList = new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .readValue(
                            new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(genericDataDTO.getData()),
                            new TypeReference<List<WsGetBalanceRequestDTO>>() {
                            });

            // Initialize SOAP Message


            if (dataMessageList != null) {
                log.info("Processing {} data messages", dataMessageList.size());
                for (WsGetBalanceRequestDTO dataMessage : dataMessageList) {

                    log.debug("Processing dataMessage with PackageId: {}", dataMessage.getPackageId());
                    if(dataMessage.getPackageType().equalsIgnoreCase("BASE")){
                        SubscriptionInfoData subscriptionInformations = new SubscriptionInfoData();
                        UsageInfo info = new UsageInfo();
                        QuotaProfileBalance quotaProfileBalance = new QuotaProfileBalance();
                        long totalQoutaInBytesForHSQ = 0l;
                        long totalQoutaInBytesForCurrent = 0l;
                        long totalQoutaInBytesForBalance = 0l;
                        double hsqLimit = 0.0;
                        double current = 0.0;
                        double balanceL = 0.0;
                        hsqLimit = Double.parseDouble(dataMessage.getHsqLimit());
                        current = (dataMessage.getUsedQuota() + dataMessage.getCurrentSessionUsageVolume());
                        balanceL = dataMessage.getTotalQuota() - (dataMessage.getUsedQuota() + dataMessage.getCurrentSessionUsageVolume());
                        if (!dataMessage.getPackageType().isEmpty() && (dataMessage.getPackageType().equalsIgnoreCase("SPARETOPUP")
                                || dataMessage.getPackageType().equalsIgnoreCase("ADDON"))) {
                            subscriptionInformations.setAddOnStatus(SubscriptionState.valueOf((dataMessage.getAddOnStatus().toUpperCase())));
                            subscriptionInformations.setPackageId(dataMessage.getPackageId());
                            subscriptionInformations.setPackageName(dataMessage.getPackageName() != null ? dataMessage.getPackageName() : "");
                            subscriptionInformations.setPackageType(dataMessage.getPackageType() != null ? dataMessage.getPackageType() : "");
                            subscriptionInformations.setStartTime(dataMessage.getStartTime() != null ? Long.valueOf(dataMessage.getStartTime()) : -1L);
//                    addChildElement(subscriptionInformations, "startTime", dataMessage.getStartTime());
                        }
                        // Static or dynamic values
                        subscriptionInformations.setPackageId(dataMessage.getPackageId());
                        subscriptionInformations.setPackageName(dataMessage.getPackageName() != null ? dataMessage.getPackageName() : "");
                        subscriptionInformations.setPackageType(dataMessage.getPackageType() != null ? dataMessage.getPackageType() : "");
                        // Add quotaProfileBalances

                        // Add allServiceBalance
                        info.setAggregationKey("Billing Cycle");

                        Usage balance = new Usage();
                        totalQoutaInBytesForHSQ = commonUtilityService.calCulateBytes(hsqLimit, dataMessage.getQuotaUnit());
                        totalQoutaInBytesForCurrent = commonUtilityService.calCulateBytes(current, dataMessage.getQuotaUnit());
                        totalQoutaInBytesForBalance = commonUtilityService.calCulateBytes(balanceL, dataMessage.getQuotaUnit());

                        balance.setDownloadOctets(-1L);
                        balance.setTime(-1L);
                        balance.setTotalOctets(totalQoutaInBytesForBalance);
                        balance.setUploadOctets(-1L);
                        info.setBalance(balance);

                        // Add curretUsage
                        Usage currentUsage = new Usage();
                        currentUsage.setDownloadOctets(dataMessage.getDownloadOctet() != null ? Long.valueOf(dataMessage.getDownloadOctet()) : -1L);
                        currentUsage.setTime(-1L);
                        currentUsage.setTotalOctets(totalQoutaInBytesForCurrent);
                        currentUsage.setUploadOctets(dataMessage.getUploadOctet() != null ? Long.valueOf(dataMessage.getUploadOctet()) : -1L);
                        info.setCurretUsage(currentUsage);

                        // Add HSQLimit
                        Usage HSQLimit = new Usage();
                        HSQLimit.setDownloadOctets(-1L);
                        HSQLimit.setTime(-1L);
                        HSQLimit.setTotalOctets(totalQoutaInBytesForHSQ);
                        HSQLimit.setUploadOctets(-1L);
                        info.setHSQLimit(HSQLimit);

                        info.setServiceId(dataMessage.getPackageId() != null ? dataMessage.getPackageId() : "");
                        info.setServiceName("All _Service ");
                        quotaProfileBalance.setQuotaProfileId(dataMessage.getQuotaProfileId() != null ? dataMessage.getQuotaProfileId() : "");
                        quotaProfileBalance.setQuotaProfileName(dataMessage.getQuotaProfileName() != null ? dataMessage.getQuotaProfileName() : "");
                        quotaProfileBalance.setAllServiceBalance(info);
                        subscriptionInformations.getQuotaProfileBalances().add(quotaProfileBalance);
                        enquiryResponse.getSubscriptionInformations().add(subscriptionInformations);
                    } else if (dataMessage.getPackageType().equalsIgnoreCase("SPARETOPUP")){
                        SubscriptionInfoData subscriptionInformations = new SubscriptionInfoData();
                        UsageInfo info = new UsageInfo();

                        String addOnStatus = dataMessage.getAddOnStatus();
                        String startDateTimeString = dataMessage.getStartTime();
                        Instant instant = Instant.ofEpochMilli(Long.parseLong(startDateTimeString));
                        LocalDateTime localStartDateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
//                        LocalDateTime localStartDateTime = LocalDateTime.parse(startDateTimeString);
                        localStartDateTime = localStartDateTime.truncatedTo(ChronoUnit.HOURS);
                        LocalDateTime currentDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
                        if ((localStartDateTime.isBefore(currentDateTime) || localStartDateTime.equals(currentDateTime)) && isStatusActive(addOnStatus)) {
                            subscriptionInformations.setAddOnStatus(SubscriptionState.STARTED);
                            log.info("Started date: {}, Current date: {}", localStartDateTime, currentDateTime);
                        } else {
                            subscriptionInformations.setAddOnStatus(SubscriptionState.START_SCHEDULED);
                            log.info("Started date: {}, Current date: {}", localStartDateTime, currentDateTime);
                        }



//                        subscriptionInformations.setAddOnStatus(SubscriptionState.STARTED);
                        subscriptionInformations.setAddonSubscriptionId(dataMessage.getAddonSubscriptionId());
                        QuotaProfileBalance quotaProfileBalance = new QuotaProfileBalance();
                        long totalQoutaInBytesForHSQ = 0l;
                        long totalQoutaInBytesForCurrent = 0l;
                        long totalQoutaInBytesForBalance = 0l;
                        double hsqLimit = 0.0;
                        double current = 0.0;
                        double balanceL = 0.0;
                        hsqLimit = Double.parseDouble(dataMessage.getHsqLimit());
                        current = (dataMessage.getUsedQuota() + dataMessage.getCurrentSessionUsageVolume());
                        balanceL = dataMessage.getTotalQuota() - (dataMessage.getUsedQuota() + dataMessage.getCurrentSessionUsageVolume());
                        if (!dataMessage.getPackageType().isEmpty() && (dataMessage.getPackageType().equalsIgnoreCase("SPARETOPUP")
                                || dataMessage.getPackageType().equalsIgnoreCase("ADDON"))) {
//                            subscriptionInformations.setAddOnStatus(SubscriptionState.STARTED);
                            subscriptionInformations.setAddonSubscriptionId(dataMessage.getAddonSubscriptionId() != null ? dataMessage.getAddonSubscriptionId() : "");
                            subscriptionInformations.setEndTime(dataMessage.getEndTime() != null ? Long.valueOf(dataMessage.getEndTime()) : -1L);
                            subscriptionInformations.setPackageDescription("");
                            subscriptionInformations.setPackageId(dataMessage.getPackageId());
                            subscriptionInformations.setPackageName(dataMessage.getPackageName() != null ? dataMessage.getPackageName() : "");
                            subscriptionInformations.setPackageType(dataMessage.getPackageType() != null ? dataMessage.getPackageType() : "");
                            subscriptionInformations.setStartTime(dataMessage.getStartTime() != null ? Long.valueOf(dataMessage.getStartTime()) : -1L);
//                    addChildElement(subscriptionInformations, "startTime", dataMessage.getStartTime());
                        }
                        // Static or dynamic values
                        subscriptionInformations.setPackageId(dataMessage.getPackageId());
                        subscriptionInformations.setPackageName(dataMessage.getPackageName() != null ? dataMessage.getPackageName() : "");
                        subscriptionInformations.setPackageType(dataMessage.getPackageType() != null ? dataMessage.getPackageType() : "");
                        // Add quotaProfileBalances

                        // Add allServiceBalance
                        info.setAggregationKey("Billing Cycle");

                        Usage balance = new Usage();
                        totalQoutaInBytesForHSQ = commonUtilityService.calCulateBytes(hsqLimit, dataMessage.getQuotaUnit());
                        totalQoutaInBytesForCurrent = commonUtilityService.calCulateBytes(current, dataMessage.getQuotaUnit());
                        totalQoutaInBytesForBalance = commonUtilityService.calCulateBytes(balanceL, dataMessage.getQuotaUnit());

                        balance.setDownloadOctets(-1L);
                        balance.setTime(-1L);
                        balance.setTotalOctets(totalQoutaInBytesForBalance);
                        balance.setUploadOctets(-1L);
                        info.setBalance(balance);

                        // Add curretUsage
                        Usage currentUsage = new Usage();
                        currentUsage.setDownloadOctets(dataMessage.getDownloadOctet() != null ? Long.valueOf(dataMessage.getDownloadOctet()) : -1L);
                        currentUsage.setTime(-1L);
                        currentUsage.setTotalOctets(totalQoutaInBytesForCurrent);
                        currentUsage.setUploadOctets(dataMessage.getUploadOctet() != null ? Long.valueOf(dataMessage.getUploadOctet()) : -1L);
                        info.setCurretUsage(currentUsage);

                        // Add HSQLimit
                        Usage HSQLimit = new Usage();
                        HSQLimit.setDownloadOctets(-1L);
                        HSQLimit.setTime(-1L);
                        HSQLimit.setTotalOctets(totalQoutaInBytesForHSQ);
                        HSQLimit.setUploadOctets(-1L);
                        info.setHSQLimit(HSQLimit);

                        info.setServiceId(dataMessage.getPackageId() != null ? dataMessage.getPackageId() : "");
                        info.setServiceName("All _Service ");
                        quotaProfileBalance.setQuotaProfileId(dataMessage.getQuotaProfileId() != null ? dataMessage.getQuotaProfileId() : "");
                        quotaProfileBalance.setQuotaProfileName(dataMessage.getQuotaProfileName() != null ? dataMessage.getQuotaProfileName() : "");
                        quotaProfileBalance.setAllServiceBalance(info);
                        subscriptionInformations.getQuotaProfileBalances().add(quotaProfileBalance);
                        subscriptionInformations.setStartTime(
                                dataMessage.getStartTime() != null && !dataMessage.getStartTime().isEmpty()
                                        ? Long.valueOf(dataMessage.getStartTime())
                                        : 0L
                        );
                        subscriptionInformations.setEndTime(
                                dataMessage.getEndTime() != null && !dataMessage.getEndTime().isEmpty()
                                        ? Long.valueOf(dataMessage.getEndTime())
                                        : 0L
                        );
                        subscriptionInformations.setUsageResetTime(
                                dataMessage.getEndTime() != null && !dataMessage.getEndTime().isEmpty()
                                        ? Long.valueOf(dataMessage.getEndTime())
                                        : 0L
                        );
                        enquiryResponse.getSubscriptionInformations().add(subscriptionInformations);

                    }else {
                        SubscriptionInfoData subscriptionInformations = new SubscriptionInfoData();
                        UsageInfo info = new UsageInfo();

                        String addOnStatus = dataMessage.getAddOnStatus();
                        String startDateTimeString = dataMessage.getStartTime();
                        Instant instant = Instant.ofEpochMilli(Long.parseLong(startDateTimeString));
                        LocalDateTime localStartDateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
//                        LocalDateTime localStartDateTime = LocalDateTime.parse(startDateTimeString);
                        localStartDateTime = localStartDateTime.truncatedTo(ChronoUnit.HOURS);
                        LocalDateTime currentDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
                        if ((localStartDateTime.isBefore(currentDateTime) || localStartDateTime.equals(currentDateTime)) && isStatusActive(addOnStatus)) {
                            subscriptionInformations.setAddOnStatus(SubscriptionState.STARTED);
                            log.info("Started date: {}, Current date: {}", localStartDateTime, currentDateTime);
                        } else {
                            subscriptionInformations.setAddOnStatus(SubscriptionState.START_SCHEDULED);
                            log.info("Started date: {}, Current date: {}", localStartDateTime, currentDateTime);
                        }



//                        subscriptionInformations.setAddOnStatus(SubscriptionState.STARTED);
                        subscriptionInformations.setAddonSubscriptionId(dataMessage.getAddonSubscriptionId());
                        QuotaProfileBalance quotaProfileBalance = new QuotaProfileBalance();
                        long totalQoutaInBytesForHSQ = 0l;
                        long totalQoutaInBytesForCurrent = 0l;
                        long totalQoutaInBytesForBalance = 0l;
                        double hsqLimit = 0.0;
                        double current = 0.0;
                        double balanceL = 0.0;
                        hsqLimit = Double.parseDouble(dataMessage.getHsqLimit());
                        current = (dataMessage.getUsedQuota() + dataMessage.getCurrentSessionUsageVolume());
                        balanceL = dataMessage.getTotalQuota() - (dataMessage.getUsedQuota() + dataMessage.getCurrentSessionUsageVolume());
                        if (!dataMessage.getPackageType().isEmpty() && (dataMessage.getPackageType().equalsIgnoreCase("SPARETOPUP")
                                || dataMessage.getPackageType().equalsIgnoreCase("ADDON"))) {
//                            subscriptionInformations.setAddOnStatus(SubscriptionState.STARTED);
                            subscriptionInformations.setAddonSubscriptionId(dataMessage.getAddonSubscriptionId() != null ? dataMessage.getAddonSubscriptionId() : "");
                            subscriptionInformations.setEndTime(dataMessage.getEndTime() != null ? Long.valueOf(dataMessage.getEndTime()) : -1L);
                            subscriptionInformations.setPackageDescription("");
                            subscriptionInformations.setPackageId(dataMessage.getPackageId());
                            subscriptionInformations.setPackageName(dataMessage.getPackageName() != null ? dataMessage.getPackageName() : "");
                            subscriptionInformations.setPackageType(dataMessage.getPackageType() != null ? dataMessage.getPackageType() : "");
                            subscriptionInformations.setStartTime(dataMessage.getStartTime() != null ? Long.valueOf(dataMessage.getStartTime()) : -1L);
//                    addChildElement(subscriptionInformations, "startTime", dataMessage.getStartTime());
                        }
                        // Static or dynamic values
                        subscriptionInformations.setPackageId(dataMessage.getPackageId());
                        subscriptionInformations.setPackageName(dataMessage.getPackageName() != null ? dataMessage.getPackageName() : "");
                        subscriptionInformations.setPackageType(dataMessage.getPackageType() != null ? dataMessage.getPackageType() : "");
                        // Add quotaProfileBalances

                        // Add allServiceBalance
                        info.setAggregationKey("Billing Cycle");

                        Usage balance = new Usage();
                        totalQoutaInBytesForHSQ = commonUtilityService.calCulateBytes(hsqLimit, dataMessage.getQuotaUnit());
                        totalQoutaInBytesForCurrent = commonUtilityService.calCulateBytes(current, dataMessage.getQuotaUnit());
                        totalQoutaInBytesForBalance = commonUtilityService.calCulateBytes(balanceL, dataMessage.getQuotaUnit());

                        balance.setDownloadOctets(-1L);
                        balance.setTime(-1L);
                        balance.setTotalOctets(totalQoutaInBytesForBalance);
                        balance.setUploadOctets(-1L);
                        info.setBalance(balance);

                        // Add curretUsage
                        Usage currentUsage = new Usage();
                        currentUsage.setDownloadOctets(dataMessage.getDownloadOctet() != null ? Long.valueOf(dataMessage.getDownloadOctet()) : -1L);
                        currentUsage.setTime(-1L);
                        currentUsage.setTotalOctets(totalQoutaInBytesForCurrent);
                        currentUsage.setUploadOctets(dataMessage.getUploadOctet() != null ? Long.valueOf(dataMessage.getUploadOctet()) : -1L);
                        info.setCurretUsage(currentUsage);

                        // Add HSQLimit
                        Usage HSQLimit = new Usage();
                        HSQLimit.setDownloadOctets(-1L);
                        HSQLimit.setTime(-1L);
                        HSQLimit.setTotalOctets(totalQoutaInBytesForHSQ);
                        HSQLimit.setUploadOctets(-1L);
                        info.setHSQLimit(HSQLimit);

                        info.setServiceId(dataMessage.getPackageId() != null ? dataMessage.getPackageId() : "");
                        info.setServiceName("All _Service ");
                        quotaProfileBalance.setQuotaProfileId(dataMessage.getQuotaProfileId() != null ? dataMessage.getQuotaProfileId() : "");
                        quotaProfileBalance.setQuotaProfileName(dataMessage.getQuotaProfileName() != null ? dataMessage.getQuotaProfileName() : "");
                        quotaProfileBalance.setAllServiceBalance(info);
                        subscriptionInformations.getQuotaProfileBalances().add(quotaProfileBalance);
                        subscriptionInformations.setStartTime(
                                dataMessage.getStartTime() != null && !dataMessage.getStartTime().isEmpty()
                                        ? Long.valueOf(dataMessage.getStartTime())
                                        : 0L
                        );
                        subscriptionInformations.setEndTime(
                                dataMessage.getEndTime() != null && !dataMessage.getEndTime().isEmpty()
                                        ? Long.valueOf(dataMessage.getEndTime())
                                        : 0L
                        );
                        subscriptionInformations.setUsageResetTime(
                                dataMessage.getEndTime() != null && !dataMessage.getEndTime().isEmpty()
                                        ? Long.valueOf(dataMessage.getEndTime())
                                        : 0L
                        );
                        enquiryResponse.getSubscriptionInformations().add(subscriptionInformations);
                        log.debug("Added subscription information for PackageId: {}", dataMessage.getPackageId());

                    }
                }
                log.info("Get Balance fetch Successfully for user: {}", localName);
                response.setReturn(enquiryResponse);
                return response;
            } else {
                log.warn("No data Found For Input SubscriberId");
                enquiryResponse.setResponseCode(404);
                enquiryResponse.setResponseMessage("NOT FOUND. Reason: Subscriber not found with subscriber identity:"+username);
                response.setReturn(enquiryResponse);
                return response;
            }
        } catch (Exception e) {
            log.error("Exception occurred while performing the request", e);
            WsGetBalanceResponse response = new WsGetBalanceResponse();
            GetBalanceEnquiryResponse enquiryResponse = new GetBalanceEnquiryResponse();
            enquiryResponse.setResponseCode(400);
            enquiryResponse.setResponseMessage("ERROR OCCURRED: " + e.getMessage());
            response.setReturn(enquiryResponse);
            return response;
        } finally {
            long endTime = System.currentTimeMillis();
            log.info("getBalanceData1 method completed in {}ms", (endTime - startTime));
        }
    }

    // Utility method to add child elements with text content
    private void addChildElement(SOAPElement parent, String name, String value) throws SOAPException {
        SOAPElement child = parent.addChildElement(name);
        child.setTextContent(value);
    }

    public DOMSource getExceptionsInResponse(String faultCodeString, String exceptionNameString, String exceptionMessage, String host, MessageContext messageContext) throws SOAPException, IOException {
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.removeNamespaceDeclaration("env");
        // Declare the required namespaces explicitly at the envelope level
        envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/encoding/");
        envelope.addNamespaceDeclaration("soapenc", SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);

        // Create SOAP body
        envelope.setPrefix("soapenv");
        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode();
        }
        SOAPBody body = envelope.getBody();
        body.setPrefix("soapenv");
        SOAPFault fault = body.addFault();

        // Set fault code
//        fault.setFaultCode(new QName("http://schemas.xmlsoap.org/soap/envelope/", "Server." + faultCodeString, "soapenv"));

        SOAPElement faultElement = body.addChildElement("Fault", "soapenv");
        SOAPElement faultcode = faultElement.addChildElement("faultcode");
        faultcode.addTextNode("soapenv:Server." + faultCodeString);
        SOAPElement faultstring = faultElement.addChildElement("faultstring");
        faultstring.addTextNode("");        // Set empty fault string
        fault.setFaultString("");

        // Create detail element
        Detail detail = fault.addDetail();

        // Add InvalidIPAddressException element
        DetailEntry invalidIPAddressException = detail.addDetailEntry(new QName("http://npm.redback.com/", exceptionNameString, "ns1"));
        invalidIPAddressException.addAttribute(new QName("href"), "#id0");

        // Add exceptionName element
        DetailEntry exceptionName = detail.addDetailEntry(new QName("http://xml.apache.org/axis/", "exceptionName", "ns2"));
        exceptionName.addTextNode("com.redback.npm." + exceptionNameString);

        // Add hostname element
        DetailEntry hostname = detail.addDetailEntry(new QName("http://xml.apache.org/axis/", "hostname", "ns3"));
        hostname.addTextNode(host);

        SOAPElement multiRef = body.addChildElement(new QName("", "multiRef"));
        org.w3c.dom.Element multiRefElement = (org.w3c.dom.Element) multiRef;
        multiRefElement.setAttribute("id", "id0");
        multiRefElement.setAttributeNS(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE, "soapenc:root", "0");
        multiRefElement.setAttributeNS("http://schemas.xmlsoap.org/soap/encoding/", "soapenc:root", "0"); // Second
        multiRefElement.setAttributeNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/"); // Third
        multiRefElement.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:type", "ns4:" + exceptionNameString); // Fourth

        SOAPElement message = multiRef.addChildElement("message");
        message.addAttribute(new QName(SoapConstants.XSI_NAMESPACE, "type", "xsi"), "xsd:string");
        message.addTextNode(exceptionMessage);

        // Save changes and verify structure
        if (messageContext != null) {
            SaajSoapMessage updateResponse = (SaajSoapMessage) messageContext.getResponse();
            updateResponse.setSaajMessage(soapMessage);
            updateResponse.getSaajMessage().saveChanges();
        }

        // Convert body to DOMSource for return
        Document document = body.getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();
        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }
        return new DOMSource(fragment);
    }

    private static boolean isStatusActive(String status) {
        return status != null && !status.isEmpty() && status.equalsIgnoreCase("Active");
    }
}
