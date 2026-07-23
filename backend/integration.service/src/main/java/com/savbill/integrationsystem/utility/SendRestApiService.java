package com.savbill.integrationsystem.utility;

import com.savbill.integrationsystem.NewNMSIntegration.configuration.ApiConfig;
import com.savbill.integrationsystem.PaymentIntegration.Model.CustomerPayment;
import com.savbill.integrationsystem.apiAudits.NMSServices.ApiAuditsService;
import com.savbill.integrationsystem.core.security.constants.LogConstants;
import com.savbill.integrationsystem.core.utillity.APIConstants;
import com.savbill.integrationsystem.core.utillity.log.ApplicationLogger;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class SendRestApiService {

    @Autowired
    private ApiAuditsService apiAuditsService;
    @Autowired
    private ApiConfig apiConfig;


    public String sendPatchRequest(String jsonPayload , String endpoint) {
        try {
            // Convert JSON string to JSONObject
            JSONObject jsonObject = new JSONObject(jsonPayload);

            RestTemplate restTemplate = apiConfig.restTemplate();
            // Set up headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create the request
            HttpEntity<String> request = new HttpEntity<>(jsonObject.toString(), headers);

            // URL of the external API
            String url = endpoint;

            // Send the request
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PATCH, request, String.class);

            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    public String sendPostRequest(String jsonPayload , String endpoint, String authenticators) {
        try {
            // Convert JSON string to JSONObject
            JSONObject jsonObject = new JSONObject(jsonPayload);

            RestTemplate restTemplate = new RestTemplate();

            // Set up headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            if(Objects.nonNull(authenticators)){
                headers.set("Authorization" , authenticators);
            }

            // Create the request
            HttpEntity<String> request = new HttpEntity<>(jsonObject.toString(), headers);

            // URL of the external API
            String url = endpoint;

            // Send the request
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    public String sendHttpPostRequest(String endPoint , String payload , String authorization,Integer mvnoId) {
        CloseableHttpResponse response = null;
        String finalResponse = "";
        try {
            LocalDateTime requestInitiationTime = LocalDateTime.now();
                    CloseableHttpClient client = HttpClients.createDefault();
                    HttpPost httpPost = new HttpPost(endPoint);

                    if(authorization != null){
                      httpPost.setHeader("Authorization", authorization);
                    }
                    httpPost.setHeader("Content-Type", "application/json");
                    StringEntity entity = new StringEntity(payload);
                    entity.setContentType("application/json");
                    httpPost.setEntity(entity);

                    response = client.execute(httpPost);
                    LocalDateTime requestCompletionTime = LocalDateTime.now();
                    Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);


                    org.apache.http.HttpEntity responseEntity = response.getEntity();
                    String responseBody = EntityUtils.toString(responseEntity, "UTF-8");
                    finalResponse =responseBody;
                    String errorMessage = null;
                    if (response != null) {
                        Integer response_code = response.getStatusLine().getStatusCode();
                        if (response_code == 200) {
                            JSONObject responseObject = new JSONObject(responseBody.toString());
                                    if (responseObject.has("error")) {
                                        errorMessage = responseObject.getString("error");
                                        ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "API " + LogConstants.REQUEST_FOR + "Error while Performing Request To Payload: with status " + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + errorMessage + LogConstants.LOG_STATUS_CODE + response_code);
                                    } else {
                                        ApplicationLogger.logger.info(LogConstants.REQUEST_FROM + "Payload API " + LogConstants.REQUEST_FOR + responseObject + LogConstants.REQUEST_BY + "SuperAdmin" + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
                                    }
                            apiAuditsService.extractDataAndSavePostApiAudits(endPoint, null, response, httpPost, null, responseTime, errorMessage, requestInitiationTime, responseBody, String.valueOf(""), mvnoId , APIConstants.TRA_INVOICE,null);
                        }
                        else{
                            errorMessage = response.toString();
                            apiAuditsService.extractDataAndSavePostApiAudits(endPoint, null, response, httpPost, null, responseTime, errorMessage, requestInitiationTime, responseBody, String.valueOf(""), mvnoId , APIConstants.TRA_INVOICE,null);
                            ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "API " + LogConstants.REQUEST_FOR + "Error while Performing Request To Payload: with status " + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + errorMessage + LogConstants.LOG_STATUS_CODE + response_code);
                        }
                    }
                    response.close();
        } catch (Exception e) {
            ApplicationLogger.logger.error("Error While Performing Request To Send ISP Invoice Payload", e.getMessage());
        }
        return finalResponse;
    }

    public String sendHttpAirtelPostRequest(String endPoint , String payload , String authorization, String countryId , String currency, Integer mvnoId, CustomerPayment customerPayment) {
        CloseableHttpResponse response = null;
        String finalResponse = "";
        String orderId = null;
        if(customerPayment != null){
          orderId = customerPayment.getOrderId().toString();
        }
        try {
            LocalDateTime requestInitiationTime = LocalDateTime.now();
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(endPoint);

            if(authorization != null){
                httpPost.setHeader("Authorization", authorization);
            }
            httpPost.setHeader("Content-Type", "application/json");
            if(countryId != null) {
                httpPost.setHeader("X-Country", countryId);
            }
            if(currency != null) {
                httpPost.setHeader("X-Currency", currency);
            }
            StringEntity entity = new StringEntity(payload);
            entity.setContentType("application/json");
            httpPost.setEntity(entity);

            response = client.execute(httpPost);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);


            org.apache.http.HttpEntity responseEntity = response.getEntity();
            String responseBody = EntityUtils.toString(responseEntity, "UTF-8");
            finalResponse =responseBody;
            String errorMessage = null;
            if (response != null) {
                Integer response_code = response.getStatusLine().getStatusCode();
                if (response_code == 200) {
                    JSONObject responseObject = new JSONObject(responseBody.toString());
                    if (responseObject.has("error")) {
                        errorMessage = responseObject.getString("error");
                        ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "API " + LogConstants.REQUEST_FOR + "Error while Performing Request To Payload: with status " + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + errorMessage + LogConstants.LOG_STATUS_CODE + response_code);
                    } else {
                        ApplicationLogger.logger.info(LogConstants.REQUEST_FROM + "Payload API " + LogConstants.REQUEST_FOR + responseObject + LogConstants.REQUEST_BY + "SuperAdmin" + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
                    }
                    apiAuditsService.extractDataAndSavePostApiAudits(endPoint, null, response, httpPost, null, responseTime, errorMessage, requestInitiationTime, responseBody, String.valueOf(""), mvnoId , APIConstants.AIRTEL,customerPayment.getOrderId().toString());
                }
                else{
                    errorMessage = response.toString();
                    apiAuditsService.extractDataAndSavePostApiAudits(endPoint, null, response, httpPost, null, responseTime, errorMessage, requestInitiationTime, responseBody, String.valueOf(""), mvnoId , APIConstants.AIRTEL,customerPayment.getOrderId().toString());
                    ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "API " + LogConstants.REQUEST_FOR + "Error while Performing Request To Payload: with status " + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + errorMessage + LogConstants.LOG_STATUS_CODE + response_code);
                }
            }
            response.close();
        } catch (Exception e) {
            ApplicationLogger.logger.error("Error While Performing Request To Send ISP Invoice Payload", e.getMessage());
        }
        return finalResponse;
    }
}
