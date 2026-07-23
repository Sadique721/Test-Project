package com.savbill.integrationsystem.IspPayloadService;

import com.savbill.integrationsystem.apiAudits.NMSServices.ApiAuditsService;
import com.savbill.integrationsystem.core.security.constants.LogConstants;
import com.savbill.integrationsystem.core.utillity.APIConstants;
import com.savbill.integrationsystem.core.utillity.log.ApplicationLogger;
import com.savbill.integrationsystem.isp.IspMainPayload;
import com.savbill.integrationsystem.kafka.KafkaMessageData;
import com.savbill.integrationsystem.kafka.KafkaMessageSender;
import com.savbill.integrationsystem.nms.constants.NMSParamconstant;
import com.savbill.integrationsystem.nms.entity.ConfigRepocitory;
import com.savbill.integrationsystem.nms.entity.Connfiguration;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.MDC;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class IspInvoicePayloadService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    ApiAuditsService apiAuditsService;

    @Autowired
    ConfigRepocitory configRepocitory;

    @Autowired
    KafkaMessageSender kafkaMessageSender;


    public CloseableHttpResponse requestToSendIspInvoicePayload(IspMainPayload ispMainPayload) throws IOException {
        CloseableHttpResponse response = null;
        CloseableHttpClient client = HttpClients.createDefault();
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        String responseBody = "";
        String errorMessage = null;
        Long responseTime = null;
        Connfiguration connfiguration = null;
        HttpPost httpPost = null;
        try {
            String accessToken=getAuthToken(ispMainPayload);
            requestInitiationTime = LocalDateTime.now();
            connfiguration=configRepocitory.findByName(NMSParamconstant.ISP_PAYLOAD_INVOICE_URL);
            if(connfiguration!=null && connfiguration.getBaseurl()!=null && !connfiguration.getBaseurl().isEmpty()) {
                if(accessToken!=null && !accessToken.isEmpty()) {
                    httpPost = new HttpPost(connfiguration.getBaseurl());

                    httpPost.setHeader("Authorization", "Bearer " + accessToken);
                    httpPost.setHeader("Content-Type", "application/json");
                    StringEntity entity = new StringEntity(ispMainPayload.getJsonPayload());
                    entity.setContentType("application/json");
                    httpPost.setEntity(entity);

                    response = client.execute(httpPost);
                    LocalDateTime requestCompletionTime = LocalDateTime.now();
                    responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);

                    ApplicationLogger.logger.info("\n******************************ISP Payload Message******************************\n\n"+ispMainPayload.getJsonPayload()+"\n\n*******************************************************************************\n");

                    HttpEntity responseEntity = response.getEntity();
                    responseBody = EntityUtils.toString(responseEntity, "UTF-8");
                    errorMessage = null;
                    if (response != null)
                    {
                        Integer response_code = response.getStatusLine().getStatusCode();
                        if (response_code == 200)
                        {
                            JSONObject responseObject = new JSONObject(responseBody.toString());
                            if(responseObject.has("statusCode"))
                            {
                                Integer statusCode=responseObject.getInt("statusCode");
                                if(statusCode==200)
                                {
                                    if (responseObject.has("error"))
                                    {
                                        errorMessage = responseObject.getString("error");
                                        ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "NetSuite ISP Payload API " + LogConstants.REQUEST_FOR + "Error while Performing Request To Send ISP Invoice Payload:" + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + errorMessage + LogConstants.LOG_STATUS_CODE + response_code);
                                        ispMainPayload.setResponseCode("ISP Payload API Response Code " +statusCode.toString());
                                        kafkaMessageSender.send(new KafkaMessageData(ispMainPayload,ispMainPayload.getClass().getSimpleName(),"ResponseCode"));
                                    }

                                    if (responseObject.has("data"))
                                    {
                                        String successMessage = responseObject.getString("data");
                                        ApplicationLogger.logger.info(LogConstants.REQUEST_FROM + "NetSuite ISP Payload API " + LogConstants.REQUEST_FOR + successMessage + LogConstants.REQUEST_BY + "SuperAdmin" + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
                                        ispMainPayload.setResponseCode(statusCode.toString());
                                        kafkaMessageSender.send(new KafkaMessageData(ispMainPayload,ispMainPayload.getClass().getSimpleName(),"ResponseCode"));
                                    }
                                }

                                if(statusCode!=200) {
                                    if (responseObject.has("error")) {
                                        errorMessage = responseObject.getString("error");
                                        ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "NetSuite ISP Payload API " + LogConstants.REQUEST_FOR + "Error while Performing Request To Send ISP Invoice Payload:" + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + errorMessage + LogConstants.LOG_STATUS_CODE + response_code);
                                        ispMainPayload.setResponseCode("ISP Payload API Response Code " +statusCode.toString());
                                        kafkaMessageSender.send(new KafkaMessageData(ispMainPayload,ispMainPayload.getClass().getSimpleName(),"ResponseCode"));
                                    }
                                }
                            }
                        }
                        else if(response_code==422 || response_code==500) {
                            JSONObject responseObject = new JSONObject(responseBody.toString());
                            if (responseObject.has("error")) {
                                    errorMessage = responseObject.getString("error");
                                    ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "NetSuite ISP Payload API " + LogConstants.REQUEST_FOR + "Error while Performing Request To Send ISP Invoice Payload:" + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + errorMessage + LogConstants.LOG_STATUS_CODE + response_code);
                                    ispMainPayload.setResponseCode("ISP Payload API Response Code " +String.valueOf(response_code));
                                    kafkaMessageSender.send(new KafkaMessageData(ispMainPayload,ispMainPayload.getClass().getSimpleName(),"ResponseCode"));
                            }
                        }
                        else {
                            ispMainPayload.setResponseCode("ISP Payload API Response Code " +String.valueOf(response_code));
                            kafkaMessageSender.send(new KafkaMessageData(ispMainPayload,ispMainPayload.getClass().getSimpleName(),"ResponseCode"));
                        }
                        apiAuditsService.extractDataAndSavePostApiAudits(connfiguration.getBaseurl(), null, response, httpPost, null, responseTime, errorMessage, requestInitiationTime, responseBody, String.valueOf(""),1,APIConstants.PROFILE_NAME,null);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            ApplicationLogger.logger.error("Error While Performing Request To Send ISP Invoice Payload", e.getMessage());
            ispMainPayload.setResponseCode("ISP Payload API Response Exception Message " +e.getMessage());
            kafkaMessageSender.send(new KafkaMessageData(ispMainPayload,ispMainPayload.getClass().getSimpleName(),"ResponseCode"));
            if(client!=null)
                client.close();
            if(response!=null)
                response.close();
            apiAuditsService.extractDataAndSavePostApiAudits(connfiguration.getBaseurl(), null, response, httpPost, null, responseTime, errorMessage, requestInitiationTime, responseBody, String.valueOf(""),1,APIConstants.PROFILE_NAME,null);
        }
        return response;
    }


    public String getAuthToken(IspMainPayload ispMainPayload) throws IOException {
        String authToken=null;
        Connfiguration connfiguration=configRepocitory.findByName(NMSParamconstant.ISP_PAYLOAD_TOKEN_URL);
        if(connfiguration!=null) {
            String url=connfiguration.getBaseurl();
            String username=connfiguration.getUsername();
            String password=connfiguration.getPassword();
            CloseableHttpClient client = HttpClients.createDefault();
            CloseableHttpResponse response=null;
            if(url!=null && !url.isEmpty() && username!=null && !username.isEmpty() && password!=null && !password.isEmpty()) {
                try {
                    LocalDateTime requestInitiationTime = LocalDateTime.now();
                    Map<String, String> parameters = new HashMap<>();
                    parameters.put("username", username);
                    parameters.put("password", password);
                    String form = parameters.entrySet()
                            .stream()
                            .map(e -> {
                                try {
                                    return e.getKey() + "=" + URLEncoder.encode(e.getValue(), "UTF-8");
                                } catch (UnsupportedEncodingException ex) {
                                    throw new RuntimeException(ex);
                                }
                            })
                            .collect(Collectors.joining("&"));

                    HttpPost httpPost = new HttpPost(url);
                    httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
                    StringEntity entity = new StringEntity(form);
                    httpPost.setEntity(entity);
                    response = client.execute(httpPost);
                    LocalDateTime requestCompletionTime = LocalDateTime.now();
                    Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                    HttpEntity responseEntity = response.getEntity();
                    String responseBody = EntityUtils.toString(responseEntity, "UTF-8");
                    JsonNode rootNode = objectMapper.readTree(responseBody);
                    if(response.getStatusLine().getStatusCode()==200) {
                        authToken = rootNode.path("data").path("access_token").asText();
                        ApplicationLogger.logger.info("NetSuite Auth Token:- "+authToken);
                        ApplicationLogger.logger.info(LogConstants.REQUEST_FROM + "NetSuite AUTH Token API " + LogConstants.REQUEST_FOR + "NetSuite Auth token generated successfully" + LogConstants.REQUEST_BY + "SuperAdmin" + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
                    }
                    else {
                        authToken = "";
                        ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "NetSuite AUTH Token API " + LogConstants.REQUEST_FOR + " Getting NetSuite AUTH TOKEN " + LogConstants.REQUEST_BY + "SuperAdmin"  + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + "Not able to generate AUTH Token " + LogConstants.LOG_STATUS_CODE + responseBody);
                        ispMainPayload.setResponseCode("AUTH Token API ResponseCode:"+ String.valueOf(response.getStatusLine().getStatusCode()));
                        kafkaMessageSender.send(new KafkaMessageData(ispMainPayload,ispMainPayload.getClass().getSimpleName(),"ResponseCode"));
                    }
                    apiAuditsService.extractDataAndSavePostApiAudits(connfiguration.getBaseurl(), null, response, httpPost, null, responseTime, null, requestInitiationTime, responseBody, String.valueOf(""),1,APIConstants.PROFILE_NAME,null);
                } catch (Exception e) {
                    ApplicationLogger.logger.error("Error While Getting Auth Token", e.getMessage());
                    ispMainPayload.setResponseCode("AUTH Token API Exception Message:"+String.valueOf(e.getMessage()));
                    kafkaMessageSender.send(new KafkaMessageData(ispMainPayload,ispMainPayload.getClass().getSimpleName(),"ResponseCode"));
                }
                finally {
                    if(client!=null)
                        client.close();
                    if(response!=null)
                        response.close();
                }
            }
        }
        return authToken;
    }
}
