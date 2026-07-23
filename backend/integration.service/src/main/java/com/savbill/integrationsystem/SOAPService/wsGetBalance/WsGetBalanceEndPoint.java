package com.savbill.integrationsystem.SOAPService.wsGetBalance;

import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.generated.newwsgetbalance.*;
import com.savbill.integrationsystem.generated.wsgetbalance.WsBalanceEnquiryResponse;
import com.savbill.integrationsystem.generated.wsgetbalance.WsGetBalance;
import com.savbill.integrationsystem.generated.wsgetbalance.WsGetBalanceResponse;
import com.savbill.integrationsystem.utility.CommonUtilityService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
//import org.apache.commons.text.StringEscapeUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.NodeList;

import javax.xml.soap.*;
import javax.xml.transform.dom.DOMSource;

import static com.savbill.integrationsystem.SOAPService.CustomResponseGeneratorHandler.CustomResponseGenerator.*;

@Slf4j
@Endpoint
public class WsGetBalanceEndPoint {

    @Autowired
    public RadiusClientService radiusClientService;

    @Autowired
    public CommonUtilityService commonUtilityService;


    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI, localPart = "wsGetBalance")
    @ResponsePayload
    public com.savbill.integrationsystem.generated.newwsgetbalance.WsBalanceEnquiryResponse getWsGetBalanceResponse(@RequestPayload WsGetBalance request, MessageContext messageContext) throws SOAPException, IOException {
        com.savbill.integrationsystem.generated.newwsgetbalance.WsBalanceEnquiryResponse response = null;
//        WsGetBalanceResponse
        long startTime = System.currentTimeMillis();
        log.info("getWsGetBalanceResponse Method Call for subscriber:{} At:{}", request.getSubscriberId(), new Date(startTime));
        try {
//            response = getBalance(request);
//            return generateWsGetBalanceSOAP11SuccessResponse(response, messageContext);
            response = getBalanceForSuccess(request);
        } catch (NullPointerException e) {
            log.error("Null pointer encountered during processing", e.getMessage());
            String exceptionMessage = e.getMessage() != null ? e.getMessage() : "A null pointer was encountered during processing.";
//            return generateWsGetBalanceSOAP11ErrorResponse(response.get(0), messageContext);
//            return getBalanceError(response.get(0));
//            response = getBalanceForSuccess(request);
            e.printStackTrace();
        } catch (Exception e) {
//            return generateWsGetBalanceSOAP11ErrorResponse(response.get(0), messageContext);
//            response = getBalanceForSuccess(request);
            e.printStackTrace();
        }
        return response;
    }

    public static DOMSource generateWsGetBalanceResponse(WsGetBalanceResponse wsResponse) throws Exception {
        // Create a SOAP Message
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        // Declare namespaces
        envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.addNamespaceDeclaration("ns2", "http://api.act.com/");

        // Create SOAP Body
        SOAPBody body = envelope.getBody();

        // Create main response element
        SOAPElement wsBalanceEnquiryResponse = body.addChildElement("wsBalanceEnquiryResponse", "ns2");
        wsBalanceEnquiryResponse.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");
        // Create nested elements inside the response
        SOAPElement getBalance = wsBalanceEnquiryResponse.addChildElement("getBalance");
        if (wsResponse.getWsBalanceEnquiryResponse() != null) {
            getBalance.addChildElement("parameter1").addTextNode(getSafeText(wsResponse.getWsBalanceEnquiryResponse().getGetBalance().getParameter1()));
            getBalance.addChildElement("parameter2").addTextNode(getSafeText(wsResponse.getWsBalanceEnquiryResponse().getGetBalance().getParameter2()));
            getBalance.addChildElement("responseCode").addTextNode(getSafeNumber(wsResponse.getWsBalanceEnquiryResponse().getGetBalance().getResponseCode()));
            getBalance.addChildElement("responseMessage").addTextNode(getSafeText(wsResponse.getWsBalanceEnquiryResponse().getGetBalance().getResponseMessage()));
            getBalance.addChildElement("requestId").addTextNode(getSafeText(wsResponse.getWsBalanceEnquiryResponse().getGetBalance().getRequestId()));

            // Add subscriptionInfo element if present
            WsBalanceEnquiryResponse.GetBalance.SubscriptionInfo subscriptionInfo1 = wsResponse.getWsBalanceEnquiryResponse().getGetBalance().getSubscriptionInfo();
            if (subscriptionInfo1 != null) {
                SOAPElement subscriptionInfoElement = getBalance.addChildElement("subscriptionInfo");
                subscriptionInfoElement.addChildElement("packageId").addTextNode(getSafeText(subscriptionInfo1.getPackageId()));
                subscriptionInfoElement.addChildElement("packageName").addTextNode(getSafeText(subscriptionInfo1.getPackageName()));
                subscriptionInfoElement.addChildElement("packageType").addTextNode(getSafeText(subscriptionInfo1.getPackageType()));

                // Add quotaProfileBalances if present
                if (wsResponse.getWsBalanceEnquiryResponse().getGetBalance().getSubscriptionInfo().getQuotaProfileBalances() != null) {
                    WsBalanceEnquiryResponse.GetBalance.SubscriptionInfo subscriptionInfo = wsResponse.getWsBalanceEnquiryResponse().getGetBalance().getSubscriptionInfo();
                    WsBalanceEnquiryResponse.GetBalance.SubscriptionInfo.QuotaProfileBalances quotaProfileBalances = subscriptionInfo.getQuotaProfileBalances();
                    SOAPElement quotaProfileBalancesElement = subscriptionInfoElement.addChildElement("quotaProfileBalances");

                    // Add allServiceBalance
                    if (quotaProfileBalances.getAllServiceBalance() != null) {
                        WsBalanceEnquiryResponse.GetBalance.SubscriptionInfo.QuotaProfileBalances.AllServiceBalance allServiceBalance = quotaProfileBalances.getAllServiceBalance();
                        SOAPElement allServiceBalanceElement = quotaProfileBalancesElement.addChildElement("allServiceBalance");
                        allServiceBalanceElement.addChildElement("aggregationKey").addTextNode(getSafeText(allServiceBalance.getAggregationKey()));

                        // Add balance
                        if (allServiceBalance.getBalance() != null) {
                            WsBalanceEnquiryResponse.GetBalance.SubscriptionInfo.QuotaProfileBalances.AllServiceBalance.Balance balance = allServiceBalance.getBalance();
                            SOAPElement balanceElement = allServiceBalanceElement.addChildElement("balance");
                            balanceElement.addChildElement("downloadOctets").addTextNode(getSafeText(balance.getDownloadOctets()));
                            balanceElement.addChildElement("time").addTextNode(getSafeText(balance.getTime()));
                            balanceElement.addChildElement("totalOctets").addTextNode(getSafeNumberLong(balance.getTotalOctetsLong()));
                            balanceElement.addChildElement("uploadOctets").addTextNode(getSafeText(balance.getUploadOctets()));
                        }

                        // Add currentUsage
                        if (allServiceBalance.getCurretUsage() != null) {
                            WsBalanceEnquiryResponse.GetBalance.SubscriptionInfo.QuotaProfileBalances.AllServiceBalance.CurretUsage curretUsage = wsResponse.getWsBalanceEnquiryResponse().getGetBalance().getSubscriptionInfo().getQuotaProfileBalances().getAllServiceBalance().getCurretUsage();
                            SOAPElement currentUsageElement = allServiceBalanceElement.addChildElement("curretUsage");
                            currentUsageElement.addChildElement("downloadOctets").addTextNode(getSafeText(curretUsage.getDownloadOctets()));
                            currentUsageElement.addChildElement("time").addTextNode(getSafeText(curretUsage.getTime()));
                            currentUsageElement.addChildElement("totalOctets").addTextNode(getSafeNumberLong(curretUsage.getTotalOctetsLong()));
                            currentUsageElement.addChildElement("uploadOctets").addTextNode(getSafeText(curretUsage.getUploadOctets()));
                        }
                        if (allServiceBalance.getHSQLimit() != null) {
                            WsBalanceEnquiryResponse.GetBalance.SubscriptionInfo.QuotaProfileBalances.AllServiceBalance.HSQLimit hsqLimit = wsResponse.getWsBalanceEnquiryResponse().getGetBalance().getSubscriptionInfo().getQuotaProfileBalances().getAllServiceBalance().getHSQLimit();
                            SOAPElement currentUsageElement = allServiceBalanceElement.addChildElement("HSQLimit");
                            currentUsageElement.addChildElement("downloadOctets").addTextNode(getSafeText(hsqLimit.getDownloadOctets()));
                            currentUsageElement.addChildElement("time").addTextNode(getSafeText(hsqLimit.getTime()));
                            currentUsageElement.addChildElement("totalOctets").addTextNode(getSafeNumberLong(hsqLimit.getTotalOctetsLong()));
                            currentUsageElement.addChildElement("uploadOctets").addTextNode(getSafeText(hsqLimit.getUploadOctets()));
                        }
                        allServiceBalanceElement.addChildElement("serviceId").addTextNode(getSafeText(allServiceBalance.getServiceId()));
                        allServiceBalanceElement.addChildElement("serviceName").addTextNode(getSafeText(allServiceBalance.getServiceName()));
                    }

                    quotaProfileBalancesElement.addChildElement("quotaProfileId").addTextNode(getSafeText(quotaProfileBalances.getQuotaProfileId()));
                    quotaProfileBalancesElement.addChildElement("quotaProfileName").addTextNode(getSafeText(quotaProfileBalances.getQuotaProfileName()));
                }
                subscriptionInfoElement.addChildElement("carryForword").addTextNode(getSafeNumber(wsResponse.getWsBalanceEnquiryResponse().getGetBalance().getSubscriptionInfo().getCarryForword()));
            }
        } else {
            getBalance.addChildElement("responseCode").addTextNode(getSafeNumber(wsResponse.getWsBalanceEnquiryErrorResponse().getResponseCode()));
            getBalance.addChildElement("responseMessage").addTextNode(getSafeText(wsResponse.getWsBalanceEnquiryErrorResponse().getResponseMessage()));
            getBalance.addChildElement("requestId").addTextNode(getSafeText(wsResponse.getWsBalanceEnquiryErrorResponse().getRequestId()));
        }

        soapMessage.saveChanges();

        // Extract DOM Document and convert to DocumentFragment
        Document document = body.getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();

        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }

        // Return as DOMSource
        return new DOMSource(fragment);
    }

    public com.savbill.integrationsystem.generated.newwsgetbalance.WsBalanceEnquiryResponse getBalanceForSuccess(WsGetBalance request) {
        com.savbill.integrationsystem.generated.newwsgetbalance.WsBalanceEnquiryResponse successResponseList = new com.savbill.integrationsystem.generated.newwsgetbalance.WsBalanceEnquiryResponse();
        String userName = request.getSubscriberId() != null ? request.getSubscriberId().trim() : "";
        String packageName = request.getPackageName() != null ? request.getPackageName().trim() : "";
        String requestId = (request.getRequestId() == null || request.getRequestId().trim().isEmpty()) ? "?" : request.getRequestId().trim();
        long startTime = System.currentTimeMillis();
        log.info("getBalance Method Calling For Subscriber: {} At: {}", request.getSubscriberId(), new Date(startTime));
        try {
            if (userName.isEmpty()) {
                com.savbill.integrationsystem.generated.newwsgetbalance.WsBalanceEnquiryResponse successResponse = new com.savbill.integrationsystem.generated.newwsgetbalance.WsBalanceEnquiryResponse();
                GetBalanceEnquiryResponse getBalance = new GetBalanceEnquiryResponse();
                log.warn("subscriber ID Is Empty Or Null");
                getBalance.setResponseCode(SoapConstants.EMPTY);
                getBalance.setResponseMessage(SoapConstants.INPUT_MISSING);
                getBalance.setRequestId(requestId);

//                successResponse.setGetBalance(getBalance);

                successResponse.setGetBalance(getBalance);
                log.info("Balance retrieval completed in {} ms", (System.currentTimeMillis() - startTime));
                return successResponse;
            } else {
                // Fetch balance details
                GetBalanceRadiusDTO getBalanceRadiusDTO = new GetBalanceRadiusDTO();
                getBalanceRadiusDTO.setSubscriberId(userName);
                if (!packageName.isEmpty()) getBalanceRadiusDTO.setPlanName(packageName);
                if (request.getPackageId() != null) getBalanceRadiusDTO.setPlanId(request.getPackageId().toString());
                getBalanceRadiusDTO.setMvnoId(SoapConstants.MVNOID);
                log.debug("Radius Client Calling getBalance for:{}", userName);
                GenericDataDTO genericDataDTOList = radiusClientService.GetBalanceApiList(getBalanceRadiusDTO);
                log.debug("Integration Received Response IN:{}MS: Get Data From Radius For GetBalance:{}",System.currentTimeMillis()-startTime, genericDataDTOList.getDataList());
                List<GetBalanceDto> dataList = new ObjectMapper().readValue(
                        new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(genericDataDTOList.getDataList()),
                        new TypeReference<List<GetBalanceDto>>() {
                        });

                if (dataList != null && !dataList.isEmpty()) {
                    log.debug("Successfully retrieved {} balance records", dataList.size());

                    com.savbill.integrationsystem.generated.newwsgetbalance.WsBalanceEnquiryResponse successResponse =
                            new com.savbill.integrationsystem.generated.newwsgetbalance.WsBalanceEnquiryResponse();
                    GetBalanceEnquiryResponse getBalance = new GetBalanceEnquiryResponse();
                    getBalance.setParameter1("");
                    getBalance.setParameter2("");
                    getBalance.setResponseCode(SoapConstants.SUCCESS_CODE);
                    getBalance.setResponseMessage(SoapConstants.SUCCESS);
                    getBalance.setRequestId(requestId);  // Set requestId from schema

                    log.debug("Retrieve balance from list for Account:{}",userName);
                    for (GetBalanceDto dataMessage : dataList) {
                        // Create the main response object
                        SubscriptionInfoData subscriptionInfo = new SubscriptionInfoData();
                        // Set response details
                        // Subscription Info
                        subscriptionInfo.setPackageId(dataMessage.getPlanId().toString());
                        subscriptionInfo.setPackageName(dataMessage.getPlanName());

                        // Determine Package Type
                        String packageType = "BASE";
                        if ("Bandwidthbooster".equalsIgnoreCase(dataMessage.getPlanType())) {
                            packageType = "ADDON";
                        } else if ("Volume Booster".equalsIgnoreCase(dataMessage.getPlanType())) {
                            packageType = "SPARETOPUP";
                        }
                        subscriptionInfo.setPackageType(packageType);

                        // Set carryForword (as per XSD requirement, it's a String)
                        subscriptionInfo.setCarryForword("0");

                        // Quota Profile Balances

                        QuotaProfileBalance quotaProfileBalances = new QuotaProfileBalance();
                        quotaProfileBalances.setQuotaProfileId(dataMessage.getPlanId().toString());
                        quotaProfileBalances.setQuotaProfileName(dataMessage.getUsageQuotaType() + "_QP");

                        // All Service Balance
//                        WsBalanceEnquiryResponse.GetBalance.SubscriptionInfo.QuotaProfileBalances.AllServiceBalance allServiceBalance =
//                                new WsBalanceEnquiryResponse.GetBalance.SubscriptionInfo.QuotaProfileBalances.AllServiceBalance();

                        UsageInfo usageInfo = new UsageInfo();

                        usageInfo.setAggregationKey("Billing Cycle");
                        // Balance Mapping
//                        WsBalanceEnquiryResponse.GetBalance.SubscriptionInfo.QuotaProfileBalances.AllServiceBalance.Balance balance =
//                                new WsBalanceEnquiryResponse.GetBalance.SubscriptionInfo.QuotaProfileBalances.AllServiceBalance.Balance();
//
                        Usage balence = new Usage();

                        balence.setDownloadOctets(Long.valueOf("-1"));
                        balence.setTime(Long.valueOf("-1"));
                        balence.setTotalOctets(commonUtilityService.calCulateBytes(dataMessage.getTotalQuota() - (dataMessage.getUsedQuota() + dataMessage.getCurrentSessionUsageVolume()), dataMessage.getQuotaUnit()));
                        balence.setUploadOctets(Long.valueOf("-1"));
                        usageInfo.setBalance(balence);

                        // Current Usage Mapping
//                        WsBalanceEnquiryResponse.GetBalance.SubscriptionInfo.QuotaProfileBalances.AllServiceBalance.CurretUsage currentUsage =
//                                new WsBalanceEnquiryResponse.GetBalance.SubscriptionInfo.QuotaProfileBalances.AllServiceBalance.CurretUsage();

//                        currentUsage.setDownloadOctets(dataMessage.getDownloadQuota());
//                        currentUsage.setTime(dataMessage.getCurrentSessionUsageTime().toString());
//                        currentUsage.setTotalOctetsLong(commonUtilityService.calCulateBytes(
//                                dataMessage.getUsedQuota() + dataMessage.getCurrentSessionUsageVolume(),
//                                dataMessage.getQuotaUnit()));
//                        currentUsage.setUploadOctets(dataMessage.getUploadQuota());


                        Usage currentUsage = new Usage();
                        currentUsage.setDownloadOctets(dataMessage.getDownloadQuota() != null ? Long.valueOf(dataMessage.getDownloadQuota()) : 0L);
                        currentUsage.setTime(Long.valueOf("0"));
                        currentUsage.setTotalOctets(commonUtilityService.calCulateBytes(dataMessage.getUsedQuota() + dataMessage.getCurrentSessionUsageVolume(), dataMessage.getQuotaUnit()));
                        currentUsage.setUploadOctets(dataMessage.getUploadQuota() != null ? Long.valueOf(dataMessage.getUploadQuota()) : 0L);
                        usageInfo.setCurretUsage(currentUsage);

                        // HSQLimit Mapping
//                        WsBalanceEnquiryResponse.GetBalance.SubscriptionInfo.QuotaProfileBalances.AllServiceBalance.HSQLimit hsqLimit =
//                                new WsBalanceEnquiryResponse.GetBalance.SubscriptionInfo.QuotaProfileBalances.AllServiceBalance.HSQLimit();

//                        hsqLimit.setDownloadOctets("-1");
//                        hsqLimit.setTime("-1");
//                        hsqLimit.setTotalOctetsLong(commonUtilityService.calCulateBytesLong(
//                                dataMessage.getTotalQuotaLong(),
//                                dataMessage.getQuotaUnit()));
//                        hsqLimit.setUploadOctets("-1");

                        Usage hsqUsage = new Usage();

                        hsqUsage.setDownloadOctets(Long.valueOf("-1"));
                        hsqUsage.setTime(Long.valueOf("-1"));
                        hsqUsage.setTotalOctets(commonUtilityService.calCulateBytesLong(dataMessage.getTotalQuotaLong(), dataMessage.getQuotaUnit()));
                        hsqUsage.setUploadOctets(Long.valueOf("-1"));
                        usageInfo.setHSQLimit(hsqUsage);

                        usageInfo.setServiceId(dataMessage.getPlanName());
                        usageInfo.setServiceName("All-Service");

                        quotaProfileBalances.setAllServiceBalance(usageInfo);
                        subscriptionInfo.getQuotaProfileBalances().add(quotaProfileBalances);


                        getBalance.getSubscriptionInfo().add(subscriptionInfo);
//                    getBalance.getSubscriptionInformations().add(subscriptionInformationData);
                        // Add getBalance to successResponse
                        successResponse.setGetBalance(getBalance);

                        // Add to response list
//                        successResponse.add(successResponse);
                    }
                    log.debug("Successfully retrieved Total ResponseMessage:{},responseCode:{}", getBalance.getResponseMessage(),getBalance.getResponseCode());
                    log.info("Balance retrieval Method completed in {} ms", (System.currentTimeMillis() - startTime));
                    return successResponse;
                } else {
                    // No data found scenario

                    com.savbill.integrationsystem.generated.newwsgetbalance.WsBalanceEnquiryResponse successResponse =
                            new com.savbill.integrationsystem.generated.newwsgetbalance.WsBalanceEnquiryResponse();

                    GetBalanceEnquiryResponse getBalance = new GetBalanceEnquiryResponse();
                    log.warn("No balance records found for subscriber: {}", userName);

                    // Set response details
                    getBalance.setResponseCode(404);
                    getBalance.setResponseMessage("NOT FOUND. Reason: Subscriber not found with subscriber "+ userName);
                    getBalance.setRequestId(requestId);  // Set requestId from schema

                    successResponse.setGetBalance(getBalance);
                    log.info("Balance retrieval Method completed in {} ms", (System.currentTimeMillis() - startTime));
                    return successResponse;
                    // Add to response list
//                    successResponseList.add(successResponse);

                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            log.error("JsonProcessingException occurred while performing Get Balance Operation");
            GetBalanceEnquiryResponse getBalance = new GetBalanceEnquiryResponse();
            getBalance.setResponseCode(SoapConstants.EMPTY);
            getBalance.setResponseMessage("JSON PROCESSING FAILED");
            getBalance.setRequestId(requestId);
            successResponseList.setGetBalance(getBalance);
            log.info("Balance retrieval Method completed in {} ms", (System.currentTimeMillis() - startTime));
            return successResponseList;
        } catch (NullPointerException e) {
            e.printStackTrace();
            log.error("NullPointerException occurred while performing Get Balance Operation");
            GetBalanceEnquiryResponse getBalance = new GetBalanceEnquiryResponse();
            getBalance.setResponseCode(SoapConstants.EMPTY);
            getBalance.setResponseMessage("NULL POINTER OCCURRED:: " + e.getMessage());
            getBalance.setRequestId(requestId);
            successResponseList.setGetBalance(getBalance);
            log.info("Balance retrieval Method completed in {} ms", (System.currentTimeMillis() - startTime));
            return successResponseList;
        } catch (Exception e) {
            log.error("Exception occurred while performing Get Balance Operation");
            e.printStackTrace();
            GetBalanceEnquiryResponse getBalance = new GetBalanceEnquiryResponse();
            getBalance.setResponseCode(SoapConstants.EMPTY);
            getBalance.setResponseMessage("EXCEPTION OCCURRED:: " + e.getMessage());
            getBalance.setRequestId(requestId);
            successResponseList.setGetBalance(getBalance);
            log.info("Balance retrieval Method completed in {} ms", (System.currentTimeMillis() - startTime));
            return successResponseList;
        }
    }


    public List<com.savbill.integrationsystem.generated.newwsgetbalance.WsBalanceEnquiryResponse> getBalanceError(com.savbill.integrationsystem.generated.newwsgetbalance.WsBalanceEnquiryResponse response) {
        com.savbill.integrationsystem.generated.newwsgetbalance.WsBalanceEnquiryResponse successResponse =
                new com.savbill.integrationsystem.generated.newwsgetbalance.WsBalanceEnquiryResponse();

        List<com.savbill.integrationsystem.generated.newwsgetbalance.WsBalanceEnquiryResponse> successResponseList = new ArrayList<>();

        GetBalanceEnquiryResponse getBalance = new GetBalanceEnquiryResponse();

        // Set response details
        getBalance.setResponseCode(response.getGetBalance().getResponseCode());
        getBalance.setResponseMessage(response.getGetBalance().getResponseMessage());
        getBalance.setRequestId(response.getGetBalance().getRequestId());  // Set requestId from schema

        successResponse.setGetBalance(getBalance);

        // Add to response list
        successResponseList.add(successResponse);
        return successResponseList;
    }

    // Use this both success and exception response generator method without interceptor
    // First test for checking sheet response and adjust parameter value by taking from response class

    /**
     * Generates a SOAP 1.1 response message for the GetBalance operation indicating success.
     * This method constructs a SOAP message with custom response code, message, and detailed balance information,
     * and returns the resulting message as a DOMSource for further processing.
     * <p>
     * //     * @param response       the {@link WsGetBalanceResponse} containing the response data for the balance enquiry
     *
     * @param messageContext the {@link MessageContext} used to update the response message context with the new SOAP message
     * @return a {@link DOMSource} containing the SOAP response message indicating success for the GetBalance operation
     * @throws SOAPException if there is an error in creating or processing the SOAP message
     */
    public DOMSource generateWsGetBalanceSOAP11SuccessResponse(List<WsBalanceEnquiryResponse> dto, MessageContext messageContext) throws SOAPException {
        // Create a SOAP Message factory for SOAP 1.1 protocol
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        // Remove the default namespace and add custom namespaces
        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.addNamespaceDeclaration("soap", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.setPrefix("soap");

        SOAPBody body = envelope.getBody();
        body.setPrefix("soap");
        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode();
        }
        Document documents = soapPart.getEnvelope().getOwnerDocument();
        // Add the main response element with custom namespaces
        SOAPElement responseElement = body.addChildElement("wsBalanceEnquiryResponse", "ns2", "http://api.act.com/");
        responseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");

        // Add the getBalance element
        SOAPElement getBalance = responseElement.addChildElement("getBalance");
        getBalance.addChildElement("parameter1").addTextNode(""); // Empty parameter1
        getBalance.addChildElement("parameter2").addTextNode("");
        getBalance.addChildElement("responseCode").addTextNode(dto.get(0).getGetBalance().getResponseCode().toString()); // Custom Response Code
        getBalance.addChildElement("responseMessage").addTextNode(dto.get(0).getGetBalance().getResponseMessage()); // Custom Response Message
        getBalance.addChildElement("requestId").addTextNode(dto.get(0).getGetBalance().getRequestId() != null ? dto.get(0).getGetBalance().getRequestId() : "?");
        for (WsBalanceEnquiryResponse response : dto) {
            // Add the balance enquiry response fields
            // Empty parameter2
            // Empty requestId

            // Add subscriptionInfo element
            SOAPElement subscriptionInfo = getBalance.addChildElement("subscriptionInfo");

            // Add package details
            subscriptionInfo.addChildElement("packageId").addTextNode(response.getGetBalance().getSubscriptionInfo().getPackageId());
            String packageName = response.getGetBalance().getSubscriptionInfo().getPackageName();
            subscriptionInfo.addChildElement("packageName").addTextNode(packageName);
            subscriptionInfo.addChildElement("packageType").addTextNode(response.getGetBalance().getSubscriptionInfo().getPackageType());

            // Add quotaProfileBalances
            SOAPElement quotaProfileBalances = subscriptionInfo.addChildElement("quotaProfileBalances");

            // Add allServiceBalance details
            SOAPElement allServiceBalance = quotaProfileBalances.addChildElement("allServiceBalance");
            allServiceBalance.addChildElement("aggregationKey").addTextNode(response.getGetBalance().getSubscriptionInfo().getQuotaProfileBalances().getAllServiceBalance().getAggregationKey() != null ? response.getGetBalance().getSubscriptionInfo().getQuotaProfileBalances().getAllServiceBalance().getAggregationKey() : "0");

            // Add balance details
            SOAPElement balance = allServiceBalance.addChildElement("balance");
            balance.addChildElement("downloadOctets").addTextNode(response.getGetBalance().getSubscriptionInfo().getQuotaProfileBalances().getAllServiceBalance().getBalance().getDownloadOctets() != null ? response.getGetBalance().getSubscriptionInfo().getQuotaProfileBalances().getAllServiceBalance().getBalance().getDownloadOctets() : "0");
            balance.addChildElement("time").addTextNode("-1");
            balance.addChildElement("totalOctets").addTextNode(response.getGetBalance().getSubscriptionInfo().getQuotaProfileBalances().getAllServiceBalance().getBalance().getTotalOctetsLong().toString() != null ? response.getGetBalance().getSubscriptionInfo().getQuotaProfileBalances().getAllServiceBalance().getBalance().getTotalOctetsLong().toString() : "0");
            balance.addChildElement("uploadOctets").addTextNode(response.getGetBalance().getSubscriptionInfo().getQuotaProfileBalances().getAllServiceBalance().getBalance().getUploadOctets() != null ? response.getGetBalance().getSubscriptionInfo().getQuotaProfileBalances().getAllServiceBalance().getBalance().getUploadOctets() : "0");

            // Add current usage details
            SOAPElement currentUsage = allServiceBalance.addChildElement("curretUsage");
            currentUsage.addChildElement("downloadOctets").addTextNode(response.getGetBalance().getSubscriptionInfo().getQuotaProfileBalances().getAllServiceBalance().getCurretUsage().getDownloadOctets() != null ? response.getGetBalance().getSubscriptionInfo().getQuotaProfileBalances().getAllServiceBalance().getCurretUsage().getDownloadOctets() : "0");
            currentUsage.addChildElement("time").addTextNode("0");
            currentUsage.addChildElement("totalOctets").addTextNode(response.getGetBalance().getSubscriptionInfo().getQuotaProfileBalances().getAllServiceBalance().getCurretUsage().getTotalOctetsLong().toString() != null ? response.getGetBalance().getSubscriptionInfo().getQuotaProfileBalances().getAllServiceBalance().getCurretUsage().getTotalOctetsLong().toString() : "0");
            currentUsage.addChildElement("uploadOctets").addTextNode(response.getGetBalance().getSubscriptionInfo().getQuotaProfileBalances().getAllServiceBalance().getCurretUsage().getUploadOctets() != null ? response.getGetBalance().getSubscriptionInfo().getQuotaProfileBalances().getAllServiceBalance().getCurretUsage().getUploadOctets() : "0");

            // Add HSQLimit details
            SOAPElement hsqlimit = allServiceBalance.addChildElement("HSQLimit");
            hsqlimit.addChildElement("downloadOctets").addTextNode(response.getGetBalance().getSubscriptionInfo().getQuotaProfileBalances().getAllServiceBalance().getHSQLimit().getDownloadOctets() != null ? response.getGetBalance().getSubscriptionInfo().getQuotaProfileBalances().getAllServiceBalance().getHSQLimit().getDownloadOctets() : "0");
            hsqlimit.addChildElement("time").addTextNode("-1");
            hsqlimit.addChildElement("totalOctets").addTextNode(response.getGetBalance().getSubscriptionInfo().getQuotaProfileBalances().getAllServiceBalance().getHSQLimit().getTotalOctetsLong().toString() != null ? response.getGetBalance().getSubscriptionInfo().getQuotaProfileBalances().getAllServiceBalance().getHSQLimit().getTotalOctetsLong().toString() : "0");
            hsqlimit.addChildElement("uploadOctets").addTextNode(response.getGetBalance().getSubscriptionInfo().getQuotaProfileBalances().getAllServiceBalance().getHSQLimit().getUploadOctets() != null ? response.getGetBalance().getSubscriptionInfo().getQuotaProfileBalances().getAllServiceBalance().getHSQLimit().getUploadOctets() : "0");

            // Add service details
            allServiceBalance.addChildElement("serviceId").addTextNode(response.getGetBalance().getSubscriptionInfo().getQuotaProfileBalances().getAllServiceBalance().getServiceId());
            allServiceBalance.addChildElement("serviceName").addTextNode(response.getGetBalance().getSubscriptionInfo().getQuotaProfileBalances().getAllServiceBalance().getServiceName());

            // Add quotaProfile details
            quotaProfileBalances.addChildElement("quotaProfileId").addTextNode(response.getGetBalance().getSubscriptionInfo().getQuotaProfileBalances().getQuotaProfileId());
            quotaProfileBalances.addChildElement("quotaProfileName").addTextNode(response.getGetBalance().getSubscriptionInfo().getQuotaProfileBalances().getQuotaProfileName());

            // Add carryForword
            subscriptionInfo.addChildElement("carryForword").addTextNode(response.getGetBalance().getSubscriptionInfo().getCarryForword().toString());

        }

        // Set the response in the SaajSoapMessage object
        SaajSoapMessage updateResponse = (SaajSoapMessage) messageContext.getResponse();
        updateResponse.setSaajMessage(soapMessage);
        updateResponse.getSaajMessage().saveChanges();

        // Convert SOAP message to DOMSource for further processing
        Document document = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();

        // Append all child nodes of the body to the fragment
        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }

        // Return the DOMSource for further processing
        return new DOMSource(fragment);
    }

    /**
     * Generates a SOAP 1.1 error response message for the GetBalance operation indicating failure.
     * This method constructs a SOAP message with custom error response code, message, and requestId,
     * and returns the resulting message as a DOMSource for further processing.
     *
     * @param response       the {@link WsGetBalanceResponse} containing the response data for the balance enquiry
     * @param messageContext the {@link MessageContext} used to update the response message context with the new SOAP message
     * @return a {@link DOMSource} containing the SOAP response message indicating an error for the GetBalance operation
     * @throws SOAPException if there is an error in creating or processing the SOAP message
     */
    public DOMSource generateWsGetBalanceSOAP11ErrorResponse(WsBalanceEnquiryResponse response, MessageContext messageContext) throws SOAPException {
        // Create a SOAP Message factory for SOAP 1.1 protocol
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        // Remove the default namespace and add custom namespaces
        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.addNamespaceDeclaration("soap", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.setPrefix("soap");

        SOAPBody body = envelope.getBody();
        body.setPrefix("soap");
        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode();
        }

        // Add the main response element with custom namespaces
        SOAPElement responseElement = body.addChildElement("wsBalanceEnquiryResponse", "ns2", "http://api.act.com/");
        responseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");

        // Add the getBalance element
        SOAPElement getBalance = responseElement.addChildElement("getBalance");

        // Add the error response fields (custom response code and message)
        getBalance.addChildElement("responseCode").addTextNode(String.valueOf(response.getGetBalance().getResponseCode())); // Custom Response Code
        getBalance.addChildElement("responseMessage").addTextNode(response.getGetBalance().getResponseMessage()); // Custom Message
        getBalance.addChildElement("requestId").addTextNode(response.getGetBalance().getRequestId()); // Custom requestId (hardcoded here)

        // Set the response in the SaajSoapMessage object
        SaajSoapMessage updateResponse = (SaajSoapMessage) messageContext.getResponse();
        updateResponse.setSaajMessage(soapMessage);
        updateResponse.getSaajMessage().saveChanges();

        // Convert SOAP message to DOMSource for further processing
        Document document = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();

        // Append all child nodes of the body to the fragment
        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }

        // Return the DOMSource for further processing
        return new DOMSource(fragment);
    }

    // Method to replace &amp; with &
    public String replaceEscapedAmpersand(String input) {
        if (input == null || input.isEmpty()) {
            return input; // Return input as is if null or empty
        }

        return input.replaceAll("&amp;", "&");
    }
}
