package com.savbill.integrationsystem.etims.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.savbill.integrationsystem.PaymentConfig.model.PaymentGatewayConfigurationConstant;
import com.savbill.integrationsystem.apiAudits.NMSServices.ApiAuditsService;
import com.savbill.integrationsystem.core.dto.KRAGenericResponseDTO;
import com.savbill.integrationsystem.core.dto.KRAGenericResponseDTOMessage;
import com.savbill.integrationsystem.core.utillity.APIConstants;
import com.savbill.integrationsystem.etims.DTO.*;
import com.savbill.integrationsystem.etims.KRAConstant;
import com.savbill.integrationsystem.etims.KRAUtils;
import com.savbill.integrationsystem.integrationMenu.ThirdPartyIntegrationMenuService;
import com.savbill.integrationsystem.integrationMenu.ThirdPartyIntigrationConstant;
    import com.savbill.integrationsystem.kafka.KafkaMessageData;
import com.savbill.integrationsystem.kafka.KafkaMessageSender;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class KRAETimsService {

    private static final Log log = LogFactory.getLog(KRAETimsService.class);

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;
    @Autowired
    KRAUtils kraUtils;

    @Autowired
    private ThirdPartyIntegrationMenuService thirdPartyIntegrationMenuService;

    @Autowired
    private ApiAuditsService apiAuditsService;

    public KRAGenericResponseDTO processEtimsAddCustomer(ETimsCustomerDTO customerDTO) {

        KRAGenericResponseDTO dataDTO = new KRAGenericResponseDTO();
        dataDTO.setCustomerNo(customerDTO.getCustomerNo());
        LocalDateTime startTime=null;
        String url=null;
        String apiKey=null;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            //  Prepare Request Body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("customerNo", customerDTO.getCustomerNo());
            requestBody.put("customerTin", customerDTO.getCustomerTin());
            requestBody.put("customerName", customerDTO.getCustomerName());
            requestBody.put("address", customerDTO.getAddress());
            requestBody.put("telNo", customerDTO.getTelNo());
            requestBody.put("email", customerDTO.getEmail());
            requestBody.put("faxNo", customerDTO.getFaxNo());
            requestBody.put("isUsed", customerDTO.getIsUsed());
            requestBody.put("remark", customerDTO.getRemark());

            String json = objectMapper.writeValueAsString(requestBody);
            HashMap<String ,String> intigrationParameters = thirdPartyIntegrationMenuService.getIntigrationParameter(ThirdPartyIntigrationConstant.EventList.INVOICE_INTIGRATION,ThirdPartyIntigrationConstant.IntigrationList.KRA_Integration,customerDTO.getMvnoId());
            String baseUrl =  intigrationParameters.get(ThirdPartyIntigrationConstant.KRA_Integration.KRA_API);
            apiKey = intigrationParameters.get(ThirdPartyIntigrationConstant.KRA_Integration.KRA_AUTH);
            // API Call
            url = baseUrl + "/Api/AddCustomerV2";
            HttpPost httpPost = new HttpPost(url);

            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Key", apiKey);

            httpPost.setEntity(new StringEntity(json));
             startTime = LocalDateTime.now();
            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
            LocalDateTime requestCompletionTime = LocalDateTime.now();

            // Response Read
            String responseString = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
            System.out.println("eTIMS Response: " + responseString);
            if (responseString == null || responseString.trim().isEmpty()) {
                dataDTO.setResponseMessage("Empty response from eTIMS AddCustomer");
                dataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
                Long responseTime = apiAuditsService.measureResponseTime(startTime, requestCompletionTime);
                HttpHeaders auditHeaders = new HttpHeaders();
                auditHeaders.set("Content-Type", "application/json");
                auditHeaders.set("Accept", "application/json");
                auditHeaders.set("Key", apiKey);
                apiAuditsService.setAuditForCallback(url, customerDTO, ResponseEntity.status(dataDTO.getResponseCode()).body(dataDTO), auditHeaders, responseTime, startTime, null, customerDTO.getMvnoId(), "POST", dataDTO.getResponseMessage(), PaymentGatewayConfigurationConstant.AUDITCONSTANT.KRA_ETIMS_ADD_CUSTOMER, customerDTO.getCustomerName() != null ? customerDTO.getCustomerName() : "");
                return dataDTO;
            }

            Map<String, Object> responseMap = objectMapper.readValue(responseString, new TypeReference<Map<String, Object>>() {});

            //  Handle Response
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                Boolean status = (Boolean) responseMap.get("status");
                String message = (String) responseMap.get("message");
                if (Boolean.TRUE.equals(status)) {
                    dataDTO.setResponseMessage("Success");
                    dataDTO.setResponseCode(APIConstants.SUCCESS);
                    dataDTO.setData(responseMap);
                } else if (message != null && message.contains("Already Registered")) {
                    dataDTO.setResponseMessage("Already Registered");
                    dataDTO.setResponseCode(APIConstants.SUCCESS);
                    dataDTO.setData(responseMap);
                } else {
                    dataDTO.setResponseMessage(message);
                    dataDTO.setResponseCode(APIConstants.EXPECTATION_FAILED);
                    dataDTO.setData(responseMap);
                }
            } else {
                dataDTO.setResponseMessage(httpResponse.getStatusLine().getReasonPhrase());
                dataDTO.setResponseCode(httpResponse.getStatusLine().getStatusCode());
            }
            Long responseTime = apiAuditsService.measureResponseTime(startTime, requestCompletionTime);
            HttpHeaders auditHeaders = new HttpHeaders();
            auditHeaders.set("Content-Type", "application/json");
            auditHeaders.set("Accept", "application/json");
            auditHeaders.set("Key", apiKey);
            apiAuditsService.setAuditForCallback(url, customerDTO, ResponseEntity.status(dataDTO.getResponseCode()).body(dataDTO),auditHeaders, responseTime, startTime, null, customerDTO.getMvnoId(), "POST", "Success", PaymentGatewayConfigurationConstant.AUDITCONSTANT.KRA_ETIMS_ADD_CUSTOMER, customerDTO.getCustomerName() != null ? customerDTO.getCustomerName(): "");
        } catch (Exception e) {
            e.printStackTrace();
            dataDTO.setResponseMessage(e.getMessage());
            dataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(startTime, requestCompletionTime);
            HttpHeaders auditHeaders = new HttpHeaders();
            auditHeaders.set("Content-Type", "application/json");
            auditHeaders.set("Accept", "application/json");
            auditHeaders.set("Key", apiKey);
            apiAuditsService.setAuditForCallback(url, customerDTO, ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(dataDTO), auditHeaders, responseTime, startTime, null, customerDTO.getMvnoId(), "POST", e.getMessage(), PaymentGatewayConfigurationConstant.AUDITCONSTANT.KRA_ETIMS_ADD_CUSTOMER, customerDTO.getCustomerName() != null ? customerDTO.getCustomerName() : "");
        }
        return dataDTO;
    }

    public KRAGenericResponseDTO processEtimsAddItemsList(List<ETimsItemDTO> itemList) {

        KRAGenericResponseDTO dataDTO = new KRAGenericResponseDTO();
        LocalDateTime startTime = null;
        String url = null;
        String apiKey=null;
        String itemClassifiCode=null;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

          
            HashMap<String ,String> intigrationParameters = thirdPartyIntegrationMenuService.getIntigrationParameter(ThirdPartyIntigrationConstant.EventList.INVOICE_INTIGRATION,ThirdPartyIntigrationConstant.IntigrationList.KRA_Integration,itemList.get(0).getMvnoId());
            String baseUrl =  intigrationParameters.get(ThirdPartyIntigrationConstant.KRA_Integration.KRA_API);
            apiKey = intigrationParameters.get(ThirdPartyIntigrationConstant.KRA_Integration.KRA_AUTH);
            boolean hasChargeItems = itemList.stream().anyMatch(item -> item.getItemCode() != null && item.getItemCode().startsWith("Charge_"));
            boolean hasPlanItems = itemList.stream().anyMatch(item -> item.getItemCode() == null || !item.getItemCode().startsWith("Charge_"));
            itemClassifiCode = hasPlanItems ? intigrationParameters.get(ThirdPartyIntigrationConstant.KRA_Integration.KRA_itemClassifiCode) : null;
            String chargeItemClassifiCode = hasChargeItems ? intigrationParameters.get(ThirdPartyIntigrationConstant.KRA_Integration.KRA_chargeItemClassifiCode) : null;
            final String finalItemClassifiCode = itemClassifiCode;
            final String finalChargeItemClassifiCode = chargeItemClassifiCode;
            itemList.parallelStream().forEach(item -> {
                if (item.getItemCode() != null && item.getItemCode().startsWith("Charge_")) {
                    item.setItemClassifiCode(finalChargeItemClassifiCode);
                } else {
                    item.setItemClassifiCode(finalItemClassifiCode);
                }
            });
            //  Convert List to JSON
            String json = objectMapper.writeValueAsString(itemList);
            //  API URL
            url = baseUrl + "/Api/AddItemsListV2";
            HttpPost httpPost = new HttpPost(url);
            //  Headers
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Key", apiKey);
            httpPost.setEntity(new StringEntity(json));
            // resource handling
            startTime = LocalDateTime.now();
            try (CloseableHttpResponse httpResponse = httpClient.execute(httpPost)) {
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                int statusCode = httpResponse.getStatusLine().getStatusCode();
                String responseString = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
                System.out.println("eTIMS Item Response: " + responseString);
                if (responseString == null || responseString.trim().isEmpty()) {
                    dataDTO.setResponseMessage("Empty response from eTIMS AddItemsList");
                    dataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
                    Long responseTime = apiAuditsService.measureResponseTime(startTime, requestCompletionTime);
                    HttpHeaders auditHeaders = new HttpHeaders();
                    auditHeaders.set("Content-Type", "application/json");
                    auditHeaders.set("Accept", "application/json");
                    auditHeaders.set("Key", apiKey);
                    apiAuditsService.setAuditForCallback(url, itemList, ResponseEntity.status(dataDTO.getResponseCode()).body(dataDTO), auditHeaders, responseTime, startTime, null, itemList.get(0).getMvnoId(), "POST", dataDTO.getResponseMessage(), PaymentGatewayConfigurationConstant.AUDITCONSTANT.KRA_ETIMS_ADD_ITEMS_LIST, itemList != null && !itemList.isEmpty() && itemList.get(0).getItemCode() != null ? itemList.get(0).getItemCode() : "");
                    return dataDTO;
                }
                Map<String, Object> responseMap = objectMapper.readValue(responseString, new TypeReference<Map<String, Object>>() {});
                //  Response Handling
                if (statusCode == 200) {
                    Boolean status = (Boolean) responseMap.get("status");
                    String message = (String) responseMap.get("message");
                    if (Boolean.TRUE.equals(status)) {
                        dataDTO.setResponseMessage("Success");
                        dataDTO.setResponseCode(APIConstants.SUCCESS);
                        dataDTO.setData(responseMap);
                    } else if (message != null && message.toLowerCase().contains("already")) {
                        // Case insensitive check
                        dataDTO.setResponseMessage("Item Already Exists");
                        dataDTO.setResponseCode(APIConstants.SUCCESS);
                        dataDTO.setData(responseMap);
                    } else {
                        dataDTO.setResponseMessage(message);
                        dataDTO.setResponseCode(APIConstants.EXPECTATION_FAILED);
                        dataDTO.setData(responseMap);
                    }

                } else {
                    dataDTO.setResponseMessage(httpResponse.getStatusLine().getReasonPhrase());
                    dataDTO.setResponseCode(statusCode);
                }
                // Audit
                Long responseTime = apiAuditsService.measureResponseTime(startTime, requestCompletionTime);
                HttpHeaders auditHeaders = new HttpHeaders();
                auditHeaders.set("Content-Type", "application/json");
                auditHeaders.set("Accept", "application/json");
                auditHeaders.set("Key", apiKey);
                apiAuditsService.setAuditForCallback(url, itemList, ResponseEntity.status(dataDTO.getResponseCode()).body(dataDTO), auditHeaders, responseTime, startTime, null, itemList.get(0).getMvnoId(), "POST", "Success", PaymentGatewayConfigurationConstant.AUDITCONSTANT.KRA_ETIMS_ADD_ITEMS_LIST, itemList != null && !itemList.isEmpty() && itemList.get(0).getItemCode() != null ? itemList.get(0).getItemCode() : "");
            }

        } catch (Exception e) {
            e.printStackTrace();
            dataDTO.setResponseMessage(e.getMessage());
            dataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(startTime, requestCompletionTime);
            HttpHeaders auditHeaders = new HttpHeaders();
            auditHeaders.set("Content-Type", "application/json");
            auditHeaders.set("Accept", "application/json");
            auditHeaders.set("Key", apiKey);
            apiAuditsService.setAuditForCallback(url, itemList, ResponseEntity.status(HttpStatus.OK).body(dataDTO), auditHeaders, responseTime, startTime, null, itemList.get(0).getMvnoId(), "POST", "Success", PaymentGatewayConfigurationConstant.AUDITCONSTANT.KRA_ETIMS_ADD_ITEMS_LIST, itemList != null && !itemList.isEmpty() && itemList.get(0).getItemCode() != null ? itemList.get(0).getItemCode() : "");
        }
        return dataDTO;
    }

    public KRAGenericResponseDTO processEtimsUpdateItem(ETimsItemDTO itemDTO) {

        KRAGenericResponseDTO dataDTO = new KRAGenericResponseDTO();
        LocalDateTime startTime=null;
        String url=null;
        String apiKey=null;


        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            // Prepare Request Body (Map → same as your pattern)
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("itemCode", itemDTO.getItemCode());
            requestBody.put("itemClassifiCode", itemDTO.getItemClassifiCode());
            requestBody.put("itemTypeCode", itemDTO.getItemTypeCode());
            requestBody.put("itemName", itemDTO.getItemName());
            requestBody.put("itemStrdName", itemDTO.getItemStrdName());
            requestBody.put("countryCode", itemDTO.getCountryCode());
            requestBody.put("pkgUnitCode", itemDTO.getPkgUnitCode());
            requestBody.put("qtyUnitCode", itemDTO.getQtyUnitCode());
            requestBody.put("taxTypeCode", itemDTO.getTaxTypeCode());
            requestBody.put("batchNo", itemDTO.getBatchNo());
            requestBody.put("barcode", itemDTO.getBarcode());
            requestBody.put("unitPrice", itemDTO.getUnitPrice());
            requestBody.put("group1UnitPrice", itemDTO.getGroup1UnitPrice());
            requestBody.put("group2UnitPrice", itemDTO.getGroup2UnitPrice());
            requestBody.put("group3UnitPrice", itemDTO.getGroup3UnitPrice());
            requestBody.put("group4UnitPrice", itemDTO.getGroup4UnitPrice());
            requestBody.put("group5UnitPrice", itemDTO.getGroup5UnitPrice());
            requestBody.put("additionalInfo", itemDTO.getAdditionalInfo());
            requestBody.put("saftyQuantity", itemDTO.getSaftyQuantity());
            requestBody.put("isInrcApplicable", itemDTO.getIsInrcApplicable());
            requestBody.put("isUsed", itemDTO.getIsUsed());
            requestBody.put("packageQuantity", itemDTO.getPackageQuantity());

            String json = objectMapper.writeValueAsString(requestBody);
            HashMap<String ,String> intigrationParameters = thirdPartyIntegrationMenuService.getIntigrationParameter(ThirdPartyIntigrationConstant.EventList.INVOICE_INTIGRATION,ThirdPartyIntigrationConstant.IntigrationList.KRA_Integration,itemDTO.getMvnoId());
            String baseUrl =  intigrationParameters.get(ThirdPartyIntigrationConstant.KRA_Integration.KRA_API);
            apiKey = intigrationParameters.get(ThirdPartyIntigrationConstant.KRA_Integration.KRA_AUTH);

             url = baseUrl + "/Api/UpdateItemV2";
            HttpPost httpPost = new HttpPost(url);

            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Key", apiKey);

            httpPost.setEntity(new StringEntity(json));

             startTime = LocalDateTime.now();
            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
            LocalDateTime requestCompletionTime = LocalDateTime.now();

            // Response Read
            String responseString = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
            System.out.println("eTIMS Update Item Response: " + responseString);
            if (responseString == null || responseString.trim().isEmpty()) {
                dataDTO.setResponseMessage("Empty response from eTIMS UpdateItem");
                dataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
                Long responseTime = apiAuditsService.measureResponseTime(startTime, requestCompletionTime);
                HttpHeaders auditHeaders = new HttpHeaders();
                auditHeaders.set("Content-Type", "application/json");
                auditHeaders.set("Accept", "application/json");
                auditHeaders.set("Key", apiKey);
                apiAuditsService.setAuditForCallback(url, itemDTO, ResponseEntity.status(dataDTO.getResponseCode()).body(dataDTO), auditHeaders, responseTime, startTime, null, null, "POST", dataDTO.getResponseMessage(), PaymentGatewayConfigurationConstant.AUDITCONSTANT.KRA_ETIMS_UPDATE_ITEM, itemDTO.getItemCode() != null ? itemDTO.getItemCode() : "");
                return dataDTO;
            }

            Map<String, Object> responseMap = objectMapper.readValue(
                    responseString,
                    new TypeReference<Map<String, Object>>() {}
            );

            // Handle Response (same logic)
            if (httpResponse.getStatusLine().getStatusCode() == 200) {

                Boolean status = (Boolean) responseMap.get("status");
                String message = (String) responseMap.get("message");

                if (Boolean.TRUE.equals(status)) {

                    dataDTO.setResponseMessage("Item Updated Successfully");
                    dataDTO.setResponseCode(APIConstants.SUCCESS);
                    dataDTO.setData(responseMap);

                } else {

                    dataDTO.setResponseMessage(message);
                    dataDTO.setResponseCode(APIConstants.EXPECTATION_FAILED);
                    dataDTO.setData(responseMap);
                }

            } else {
                dataDTO.setResponseMessage(httpResponse.getStatusLine().getReasonPhrase());
                dataDTO.setResponseCode(httpResponse.getStatusLine().getStatusCode());
            }
            Long responseTime = apiAuditsService.measureResponseTime(startTime, requestCompletionTime);
            HttpHeaders auditHeaders = new HttpHeaders();
            auditHeaders.set("Content-Type", "application/json");
            auditHeaders.set("Accept", "application/json");
            auditHeaders.set("Key", apiKey);
            apiAuditsService.setAuditForCallback(url, itemDTO, ResponseEntity.status(dataDTO.getResponseCode()).body(dataDTO), auditHeaders, responseTime, startTime, null, null, "POST", "Success", PaymentGatewayConfigurationConstant.AUDITCONSTANT.KRA_ETIMS_UPDATE_ITEM, itemDTO.getItemCode() != null ? itemDTO.getItemCode() : "");

        } catch (Exception e) {
            e.printStackTrace();
            dataDTO.setResponseMessage(e.getMessage());
            dataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(startTime, requestCompletionTime);
            HttpHeaders auditHeaders = new HttpHeaders();
            auditHeaders.set("Content-Type", "application/json");
            auditHeaders.set("Accept", "application/json");
            auditHeaders.set("Key", apiKey);
            apiAuditsService.setAuditForCallback(url, itemDTO, ResponseEntity.status(dataDTO.getResponseCode()).body(dataDTO), auditHeaders, responseTime, startTime, null, null, "POST", e.getMessage(), PaymentGatewayConfigurationConstant.AUDITCONSTANT.KRA_ETIMS_ADD_ITEMS_LIST, itemDTO.getItemCode() != null ? itemDTO.getItemCode() : "");
        }

        return dataDTO;
    }

    public KRAGenericResponseDTO processEtimsAddSale(ETimsSaleDTO saleDTO) {

        KRAGenericResponseDTO dataDTO = new KRAGenericResponseDTO();
        dataDTO.setTraderInvoiceNo(saleDTO.getTraderInvoiceNo());
        LocalDateTime startTime=null;
        String url=null;
        String apiKey=null;
        System.out.println("KRA AddSale requested traderInvoiceNo=" + saleDTO.getTraderInvoiceNo()
                + ", mvnoId=" + saleDTO.getMvnoId());
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Prepare Request Body
            String json = objectMapper.writeValueAsString(saleDTO);
            HashMap<String ,String> intigrationParameters = thirdPartyIntegrationMenuService.getIntigrationParameter(ThirdPartyIntigrationConstant.EventList.INVOICE_INTIGRATION,ThirdPartyIntigrationConstant.IntigrationList.KRA_Integration,saleDTO.getMvnoId());
            String baseUrl =  intigrationParameters.get(ThirdPartyIntigrationConstant.KRA_Integration.KRA_API);
             apiKey = intigrationParameters.get(ThirdPartyIntigrationConstant.KRA_Integration.KRA_AUTH);
            //  API Call
             url = baseUrl + "/Api/AddSaleV2";
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Key", apiKey);
            httpPost.setEntity(new StringEntity(json));
             startTime = LocalDateTime.now();
            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            // Response Read
            String responseString = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
            System.out.println("eTIMS SALE Response: " + responseString);
            if (responseString == null || responseString.trim().isEmpty()) {
                dataDTO.setResponseMessage("Empty response from eTIMS AddSale");
                dataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
                Long responseTime = apiAuditsService.measureResponseTime(startTime, requestCompletionTime);
                HttpHeaders auditHeaders = new HttpHeaders();
                auditHeaders.set("Content-Type", "application/json");
                auditHeaders.set("Accept", "application/json");
                auditHeaders.set("Key", apiKey);
                apiAuditsService.setAuditForCallback(url, saleDTO, ResponseEntity.status(dataDTO.getResponseCode()).body(dataDTO), auditHeaders, responseTime, startTime, null, null, "POST", dataDTO.getResponseMessage(), PaymentGatewayConfigurationConstant.AUDITCONSTANT.KRA_ETIMS_ADD_SALE, saleDTO.getTraderInvoiceNo() != null ? saleDTO.getTraderInvoiceNo() : "");
                return dataDTO;
            }
            Map<String, Object> responseMap = objectMapper.readValue(
                    responseString,
                    new TypeReference<Map<String, Object>>() {}
            );
            // Handle Response
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                Boolean status = (Boolean) responseMap.get("status");
                String message = (String) responseMap.get("message");
                if (Boolean.TRUE.equals(status)) {
                    dataDTO.setResponseMessage("Invoice Created Successfully");
                    dataDTO.setResponseCode(APIConstants.SUCCESS);
                    dataDTO.setData(responseMap);
                    String scuQrCode = null;
                    if (responseMap.get("responseData") instanceof Map) {
                        Map<String, Object> responseData = (Map<String, Object>) responseMap.get("responseData");

                        Object linkObj = responseData.get("scuqrCode");
                        if (linkObj != null) {
                            scuQrCode = kraUtils.linkToQr(linkObj.toString());
                            dataDTO.setInvoiceQR(scuQrCode);
                        }
                        Object invoiceId=responseData.get("invoiceNo");
                        if(invoiceId!=null){
                            dataDTO.setKRAInvoiceId(invoiceId.toString());
                        }
                        Object traderInvoiceNo=responseData.get("traderInvoiceNo");
                        if (traderInvoiceNo!=null){
                            dataDTO.setTraderInvoiceNo(traderInvoiceNo.toString());
                        }
                    }

                }
                else {
                    dataDTO.setResponseMessage(message);
                    dataDTO.setResponseCode(APIConstants.EXPECTATION_FAILED);
                    dataDTO.setData(responseMap);
                }
            } else {
                dataDTO.setResponseMessage(httpResponse.getStatusLine().getReasonPhrase());
                dataDTO.setResponseCode(httpResponse.getStatusLine().getStatusCode());
            }
            // Audit - success path
            Long responseTime = apiAuditsService.measureResponseTime(startTime, requestCompletionTime);
            HttpHeaders auditHeaders = new HttpHeaders();
            auditHeaders.set("Content-Type", "application/json");
            auditHeaders.set("Accept", "application/json");
            auditHeaders.set("Key", apiKey);
            apiAuditsService.setAuditForCallback(url, saleDTO, ResponseEntity.status(dataDTO.getResponseCode()).body(dataDTO), auditHeaders, responseTime, startTime, null, null, "POST", "Success", PaymentGatewayConfigurationConstant.AUDITCONSTANT.KRA_ETIMS_ADD_SALE, saleDTO.getTraderInvoiceNo() != null ? saleDTO.getTraderInvoiceNo() : "");
        } catch (Exception e) {
            e.printStackTrace();
            dataDTO.setResponseMessage(e.getMessage());
            dataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(startTime, requestCompletionTime);
            HttpHeaders auditHeaders = new HttpHeaders();
            auditHeaders.set("Content-Type", "application/json");
            auditHeaders.set("Accept", "application/json");
            auditHeaders.set("Key", apiKey);
            apiAuditsService.setAuditForCallback(url, saleDTO, ResponseEntity.status(dataDTO.getResponseCode()).body(dataDTO), auditHeaders, responseTime, startTime, null, null, "POST", dataDTO.getResponseMessage(), PaymentGatewayConfigurationConstant.AUDITCONSTANT.KRA_ETIMS_ADD_SALE, saleDTO.getTraderInvoiceNo() != null ? saleDTO.getTraderInvoiceNo() : "");
        }
        return dataDTO;
    }

    public KRAGenericResponseDTO processEtimsAddCreditNote(ETimsCreditNoteDTO creditNoteDTO) {

        KRAGenericResponseDTO dataDTO = new KRAGenericResponseDTO();
        dataDTO.setTraderInvoiceNo(creditNoteDTO.getTraderInvoiceNo());
        LocalDateTime startTime=null;
        String url=null;
        String apiKey=null;
        System.out.println("KRA AddCreditNote requested traderInvoiceNo=" + creditNoteDTO.getTraderInvoiceNo()
                + ", mvnoId=" + creditNoteDTO.getMvnoId());
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            //  Prepare Request Body
            String json = objectMapper.writeValueAsString(creditNoteDTO);
            HashMap<String ,String> intigrationParameters = thirdPartyIntegrationMenuService.getIntigrationParameter(ThirdPartyIntigrationConstant.EventList.INVOICE_INTIGRATION,ThirdPartyIntigrationConstant.IntigrationList.KRA_Integration,creditNoteDTO.getMvnoId());
            String baseUrl =  intigrationParameters.get(ThirdPartyIntigrationConstant.KRA_Integration.KRA_API);
            apiKey = intigrationParameters.get(ThirdPartyIntigrationConstant.KRA_Integration.KRA_AUTH);
            //      API Call
             url = baseUrl + "/Api/AddSaleCreditNoteV2";
            HttpPost httpPost = new HttpPost(url);

            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Key", apiKey);

            httpPost.setEntity(new StringEntity(json));

             startTime = LocalDateTime.now();
            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
            LocalDateTime requestCompletionTime = LocalDateTime.now();

            //  Response Read
            String responseString = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
            System.out.println("eTIMS Credit Note Response: " + responseString);
            if (responseString == null || responseString.trim().isEmpty()) {
                dataDTO.setResponseMessage("Empty response from eTIMS CreditNote");
                dataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
                Long responseTime = apiAuditsService.measureResponseTime(startTime, requestCompletionTime);
                HttpHeaders auditHeaders = new HttpHeaders();
                auditHeaders.set("Content-Type", "application/json");
                auditHeaders.set("Accept", "application/json");
                auditHeaders.set("Key", apiKey);
                apiAuditsService.setAuditForCallback(url, creditNoteDTO, ResponseEntity.status(dataDTO.getResponseCode()).body(dataDTO), auditHeaders, responseTime, startTime, null, null, "POST", dataDTO.getResponseMessage(), PaymentGatewayConfigurationConstant.AUDITCONSTANT.KRA_ETIMS_ADD_CREDIT_NOTE, creditNoteDTO.getTraderInvoiceNo() != null ? creditNoteDTO.getTraderInvoiceNo() : "");
                return dataDTO;
            }

            Map<String, Object> responseMap = objectMapper.readValue(
                    responseString,
                    new TypeReference<Map<String, Object>>() {}
            );

            //  Handle Response (same pattern)
            if (httpResponse.getStatusLine().getStatusCode() == 200) {

                Boolean status = (Boolean) responseMap.get("status");
                String message = (String) responseMap.get("message");

                if (Boolean.TRUE.equals(status)) {

                    dataDTO.setResponseMessage("Success");
                    dataDTO.setResponseCode(APIConstants.SUCCESS);
                    dataDTO.setData(responseMap);
                    String scuQrCode = null;
                    if (responseMap.get("responseData") instanceof Map) {
                        Map<String, Object> responseData = (Map<String, Object>) responseMap.get("responseData");
                        Object linkObj = responseData.get("scuqrCode");
                        if (linkObj != null) {
                            scuQrCode = kraUtils.linkToQr(linkObj.toString());
                            dataDTO.setInvoiceQR(scuQrCode);
                        }
                        Object traderInvoiceNo=responseData.get("traderInvoiceNo");
                        if (traderInvoiceNo!=null){
                            dataDTO.setTraderInvoiceNo(traderInvoiceNo.toString());
                        }
                    }

                } else if (message != null && message.contains("Already")) {

                    dataDTO.setResponseMessage("Credit Note Already Exists");
                    dataDTO.setResponseCode(APIConstants.SUCCESS);
                    dataDTO.setData(responseMap);

                } else {

                    dataDTO.setResponseMessage(message);
                    dataDTO.setResponseCode(APIConstants.EXPECTATION_FAILED);
                    dataDTO.setData(responseMap);
                }

            } else {
                dataDTO.setResponseMessage(httpResponse.getStatusLine().getReasonPhrase());
                dataDTO.setResponseCode(httpResponse.getStatusLine().getStatusCode());
            }
            Long responseTime = apiAuditsService.measureResponseTime(startTime, requestCompletionTime);
            HttpHeaders auditHeaders = new HttpHeaders();
            auditHeaders.set("Content-Type", "application/json");
            auditHeaders.set("Accept", "application/json");
            auditHeaders.set("Key", apiKey);
            apiAuditsService.setAuditForCallback(url, creditNoteDTO, ResponseEntity.status(dataDTO.getResponseCode()).body(dataDTO), auditHeaders, responseTime, startTime, null, null, "POST", "Success", PaymentGatewayConfigurationConstant.AUDITCONSTANT.KRA_ETIMS_ADD_CREDIT_NOTE, creditNoteDTO.getTraderInvoiceNo() != null ? creditNoteDTO.getTraderInvoiceNo() : "");

        } catch (Exception e) {
            e.printStackTrace();
            dataDTO.setResponseMessage(e.getMessage());
            dataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(startTime, requestCompletionTime);
            HttpHeaders auditHeaders = new HttpHeaders();
            auditHeaders.set("Content-Type", "application/json");
            auditHeaders.set("Accept", "application/json");
            auditHeaders.set("Key", apiKey);
            apiAuditsService.setAuditForCallback(url, creditNoteDTO, ResponseEntity.status(dataDTO.getResponseCode()).body(dataDTO), auditHeaders, responseTime, startTime, null, null, "POST", dataDTO.getResponseMessage(), PaymentGatewayConfigurationConstant.AUDITCONSTANT.KRA_ETIMS_ADD_CREDIT_NOTE, creditNoteDTO.getTraderInvoiceNo() != null ? creditNoteDTO.getTraderInvoiceNo() : "");

        }

        return dataDTO;
    }

    public KRAGenericResponseDTO processGetSalesByTraderInvoiceNo(ETimsGetSalesDTO salesDTO) {

        KRAGenericResponseDTO dataDTO = new KRAGenericResponseDTO();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HashMap<String ,String> intigrationParameters = thirdPartyIntegrationMenuService.getIntigrationParameter(ThirdPartyIntigrationConstant.EventList.INVOICE_INTIGRATION,ThirdPartyIntigrationConstant.IntigrationList.KRA_Integration,salesDTO.getMvnoId());
            String baseUrl =  intigrationParameters.get(ThirdPartyIntigrationConstant.KRA_Integration.KRA_API);
            String apiKey = intigrationParameters.get(ThirdPartyIntigrationConstant.KRA_Integration.KRA_AUTH);
            String encodedInvoiceNo = URLEncoder.encode(salesDTO.getTraderInvoiceNo(), String.valueOf(StandardCharsets.UTF_8));
            //  Prepare URL with query param
            String url = baseUrl + "/Api/GetSalesByTraderInvoiceNoV2?TraderInvoiceNo=" + salesDTO.getTraderInvoiceNo();

            HttpGet httpGet = new HttpGet(url);

            httpGet.setHeader("Content-Type", "application/json");
            httpGet.setHeader("Accept", "application/json");
            httpGet.setHeader("Key", apiKey);

            LocalDateTime startTime = LocalDateTime.now();
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            LocalDateTime endTime = LocalDateTime.now();

            //  Response Read
            String responseString = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
            System.out.println("eTIMS Get Sales Response: " + responseString);
            if (responseString == null || responseString.trim().isEmpty()) {
                dataDTO.setResponseMessage("Empty response from eTIMS GetSalesByTraderInvoiceNo");
                dataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
                return dataDTO;
            }

            Map<String, Object> responseMap = objectMapper.readValue(
                    responseString,
                    new TypeReference<Map<String, Object>>() {}
            );

            //  Handle Response
            if (httpResponse.getStatusLine().getStatusCode() == 200) {

                Object status = responseMap.get("status");
                String message = (String) responseMap.get("message");

                if (status == null || Boolean.TRUE.equals(status)) {
                    //  ETIMS sometimes returns status=null but still success
                    dataDTO.setResponseMessage("Success");
                    dataDTO.setResponseCode(APIConstants.SUCCESS);
                    dataDTO.setData(responseMap);

                } else {
                    dataDTO.setResponseMessage(message);
                    dataDTO.setResponseCode(APIConstants.EXPECTATION_FAILED);
                    dataDTO.setData(responseMap);
                }

            } else {
                dataDTO.setResponseMessage(httpResponse.getStatusLine().getReasonPhrase());
                dataDTO.setResponseCode(httpResponse.getStatusLine().getStatusCode());
            }

        } catch (Exception e) {
            e.printStackTrace();
            dataDTO.setResponseMessage(e.getMessage());
            dataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
        }

        return dataDTO;
    }


    /**
     * Method to process and add customer in KRA in bulk
     * @param customerDTOList
     * @return
     */
    public List<KRAGenericResponseDTO> processEtimsAddCustomer(List<ETimsCustomerDTO> customerDTOList) {

        List<KRAGenericResponseDTO> responseList = Collections.synchronizedList(new ArrayList<>());

        int batchSize = 50;
        List<List<ETimsCustomerDTO>> batches = Lists.partition(customerDTOList, batchSize);

        batches.parallelStream().forEach(batch -> {
            for (ETimsCustomerDTO dto : batch) {
                try {
                    if (dto == null || dto.getCustomerNo() == null || dto.getCustomerTin() == null) {
                        KRAGenericResponseDTO response = new KRAGenericResponseDTO();
                        response.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setResponseMessage("CustomerNo and CustomerTIN are required!");
                        responseList.add(response);
                        continue;
                    }

                    KRAGenericResponseDTO response = processEtimsAddCustomer(dto);

                    if (response == null) {
                        response = new KRAGenericResponseDTO();
                        response.setResponseCode(APIConstants.NULL_VALUE);
                        response.setResponseMessage("No response from eTIMS!");
                        response.setData(null);
                    }

                    responseList.add(response);

                } catch (Exception ex) {
                    KRAGenericResponseDTO response = new KRAGenericResponseDTO();
                    response.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
                    response.setResponseMessage("Error while processing eTIMS customer");
                    response.setData(ex.getMessage());
                    responseList.add(response);
                }
            }
        });
        KRAGenericResponseDTOMessage kraGenericResponseDTOMessage=new KRAGenericResponseDTOMessage();
        kraGenericResponseDTOMessage.setResponseDTO(responseList);
        kafkaMessageSender.send(new KafkaMessageData(kraGenericResponseDTOMessage, kraGenericResponseDTOMessage.getClass().getSimpleName(),KRAConstant.ADDCUSTOMER));
        return responseList;
    }

    /**
     * Method to process and add plan in KRA in bulk
     * @param itemList
     * @return
     */
    public List<KRAGenericResponseDTO> processEtimsAddItemsListBatch(List<ETimsItemDTO> itemList) {

        List<KRAGenericResponseDTO> responseList = Collections.synchronizedList(new ArrayList<>());

        int batchSize = 50;
        List<List<ETimsItemDTO>> batches = Lists.partition(itemList, batchSize);

        batches.parallelStream().forEach(batch -> {
            try {
                KRAGenericResponseDTO response = processEtimsAddItemsList(batch);

                if (response == null) {
                    response = new KRAGenericResponseDTO();
                    response.setResponseCode(APIConstants.NULL_VALUE);
                    response.setResponseMessage("No response from eTIMS!");
                    response.setData(null);
                }

                responseList.add(response);

            } catch (Exception ex) {
                KRAGenericResponseDTO response = new KRAGenericResponseDTO();
                response.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
                response.setResponseMessage("Error while processing eTIMS Add Items");
                response.setData(ex.getMessage());
                responseList.add(response);
            }
        });
        KRAGenericResponseDTOMessage kraGenericResponseDTOMessage=new KRAGenericResponseDTOMessage();
        kraGenericResponseDTOMessage.setResponseDTO(responseList);
        kafkaMessageSender.send(new KafkaMessageData(kraGenericResponseDTOMessage, kraGenericResponseDTOMessage.getClass().getSimpleName(), KRAConstant.ADDITEMS));
        return responseList;
    }

    /**
     * Method to process and add invoice in KRA in bulk
     * @param saleDTOList
     * @return
     */
    public List<KRAGenericResponseDTO> processEtimsAddSale(List<ETimsSaleDTO> saleDTOList) {

        List<KRAGenericResponseDTO> responseList = Collections.synchronizedList(new ArrayList<>());

        int batchSize = 50;
        List<List<ETimsSaleDTO>> batches = Lists.partition(saleDTOList, batchSize);

        batches.parallelStream().forEach(batch -> {
            for (ETimsSaleDTO saleDTO : batch) {
                try {

                    if (saleDTO == null || saleDTO.getCustomerTin() == null || saleDTO.getSaleItemList() == null) {
                        KRAGenericResponseDTO response = new KRAGenericResponseDTO();
                        response.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setResponseMessage("CustomerTIN and Sale Items are required!");
                        responseList.add(response);
                        continue;
                    }

                    if (saleDTO.getSaleItemList().isEmpty()) {
                        KRAGenericResponseDTO response = new KRAGenericResponseDTO();
                        response.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setResponseMessage("Sale item list cannot be empty!");
                        responseList.add(response);
                        continue;
                    }

                    KRAGenericResponseDTO response = processEtimsAddSale(saleDTO);

                    if (response == null) {
                        response = new KRAGenericResponseDTO();
                        response.setResponseCode(APIConstants.NULL_VALUE);
                        response.setResponseMessage("No response from eTIMS!");
                        response.setData(null);
                    }
                    responseList.add(response);

                } catch (Exception ex) {
                    KRAGenericResponseDTO response = new KRAGenericResponseDTO();
                    response.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
                    response.setResponseMessage("Error while processing eTIMS Add Sale");
                    response.setData(ex.getMessage());
                    responseList.add(response);
                }
            }
        });
        KRAGenericResponseDTOMessage kraGenericResponseDTOMessage=new KRAGenericResponseDTOMessage();
        kraGenericResponseDTOMessage.setResponseDTO(responseList);
        kafkaMessageSender.send(new KafkaMessageData(kraGenericResponseDTOMessage, kraGenericResponseDTOMessage.getClass().getSimpleName(),KRAConstant.ADDINVOICE));
        return responseList;
    }

    /**
     * Method to process and add Credit Note in KRA in bulk
     * @param creditNoteDTOList
     * @return
     */
    public List<KRAGenericResponseDTO> processEtimsAddCreditNote(List<ETimsCreditNoteDTO> creditNoteDTOList) {

        List<KRAGenericResponseDTO> responseList = Collections.synchronizedList(new ArrayList<>());

        int batchSize = 50;
        List<List<ETimsCreditNoteDTO>> batches = Lists.partition(creditNoteDTOList, batchSize);

        batches.parallelStream().forEach(batch -> {
            for (ETimsCreditNoteDTO creditNoteDTO : batch) {
                try {

                    if (creditNoteDTO == null || creditNoteDTO.getOrgInvoiceNo() == null || creditNoteDTO.getCreditNoteItemsList() == null) {
                        KRAGenericResponseDTO response = new KRAGenericResponseDTO();
                        response.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setResponseMessage("OrgInvoiceNo and Items are required!");
                        responseList.add(response);
                        continue;
                    }

                    if (creditNoteDTO.getCreditNoteItemsList().isEmpty()) {
                        KRAGenericResponseDTO response = new KRAGenericResponseDTO();
                        response.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setResponseMessage("Credit note item list cannot be empty!");
                        responseList.add(response);
                        continue;
                    }

                    KRAGenericResponseDTO response = processEtimsAddCreditNote(creditNoteDTO);

                    if (response == null) {
                        response = new KRAGenericResponseDTO();
                        response.setResponseCode(APIConstants.NULL_VALUE);
                        response.setResponseMessage("No response from eTIMS!");
                        response.setData(null);
                    }

                    responseList.add(response);

                } catch (Exception ex) {
                    KRAGenericResponseDTO response = new KRAGenericResponseDTO();
                    response.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
                    response.setResponseMessage("Error while processing Credit Note");
                    response.setData(ex.getMessage());
                    responseList.add(response);
                }
            }
        });
        KRAGenericResponseDTOMessage kraGenericResponseDTOMessage=new KRAGenericResponseDTOMessage();
        kraGenericResponseDTOMessage.setResponseDTO(responseList);
        kafkaMessageSender.send(new KafkaMessageData(kraGenericResponseDTOMessage, kraGenericResponseDTOMessage.getClass().getSimpleName(), KRAConstant.ADDCREDIT));

        return responseList;
    }

}
