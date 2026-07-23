package com.savbill.integrationsystem.CDATA.Services;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.integrationsystem.CDATA.Pojo.CdataCustDetailsPojo;
import com.savbill.integrationsystem.CDATA.Pojo.ServiceProvisonPojo;
import com.savbill.integrationsystem.CDATA.Pojo.TemplateCreatePojo;
import com.savbill.integrationsystem.apiAudits.NMSServices.ApiAuditsService;
import com.savbill.integrationsystem.core.security.constants.LogConstants;
import com.savbill.integrationsystem.core.utillity.APIConstants;
import com.savbill.integrationsystem.kafka.KafkaMessageData;
import com.savbill.integrationsystem.kafka.KafkaMessageSender;
import com.savbill.integrationsystem.nms.NmsService;
import com.savbill.integrationsystem.nms.constants.NMSParamconstant;
import com.savbill.integrationsystem.nms.entity.ConfigRepocitory;
import com.savbill.integrationsystem.nms.entity.Connfiguration;
import com.savbill.integrationsystem.nms.entity.NMSCustDetails;
import com.savbill.integrationsystem.nms.entity.UuidDataDTO;
import com.savbill.integrationsystem.nms.repository.NMSCustDetailsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.MDC;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.HashMap;

@Service
public class CdataServices {

    @Autowired
    NMSCustDetailsRepository nmsCustDetailsRepository;

    @Autowired
    private ConfigRepocitory configRepocitory;

    @Autowired
    ApiAuditsService apiAuditsService;

    @Autowired
    Tracer tracer;

//    @Autowired
//    MessageSender messageSender;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    NmsService nmsService;

    private static final Logger logger = LoggerFactory.getLogger("CdataServices.class");

    private static CloseableHttpClient createHttpClient() throws Exception {
        return HttpClients.custom()
                .setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .build();
    }
    private static HttpPost createHttpPost(String url) {
        return new HttpPost(URI.create(url));
    }

    private static HttpPut createHttpPut(String url) {
        return new HttpPut(URI.create(url));
    }


    public String generateName(String customerName){
        LocalDateTime now = LocalDateTime.now();
        long milliseconds = now.toInstant(ZoneOffset.UTC).toEpochMilli();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("WAN");
        stringBuilder.append(" ");
        stringBuilder.append("Profile");
        stringBuilder.append("-");
        stringBuilder.append(customerName.substring(0,4));
        stringBuilder.append("-");
        stringBuilder.append(milliseconds);
        return String.valueOf(stringBuilder);
    }

    //call this service from controller
//    public void generatePaylodForCDATA(String customerName, String custUserName, String custPassword,String configName,Integer custServMappingId,String loggedInUser, Integer loggedInUserMvnoId,String serialNumber){
//
//        TemplateCreatePojo templateCreatePojo = new TemplateCreatePojo();
//
//        TemplateCreatePojo.ParamObject paramObject = new TemplateCreatePojo.ParamObject();
//
//        TemplateCreatePojo.ParamObject.Wan wan = new TemplateCreatePojo.ParamObject.Wan();
//
//        TemplateCreatePojo.ParamObject.Wan.Ppp ppp = new TemplateCreatePojo.ParamObject.Wan.Ppp();
//
//
//
//        //set profileName
//        templateCreatePojo.setName(generateName(customerName));
//
//        //set Type
//        templateCreatePojo.setType("wan");
//
//        //set description
//        templateCreatePojo.setTmplDesc("Testing creation through API");
//
//        //set wan parameters
//        wan.setEnableQos(0);
//        wan.setAdminStatus(1);
//        wan.setEnableNapt(1);
//        wan.setEnableVlan(1);
//        wan.setVlanId(200);
//        wan.set_8021Mark(0);
//        wan.setServiceType(2);
//        wan.setConnectionType(2);
//        wan.setIpProtocol(1);
//        wan.setMtu(1492);
//        wan.setEnableIgmpMldProxy(0);
//        wan.setMulticastVlanId(null);
//
//        //set port binding
//        TemplateCreatePojo.ParamObject.Wan.PortBinding portBinding = new TemplateCreatePojo.ParamObject.Wan.PortBinding();
//        portBinding.setLan(Collections.emptyList());
//        portBinding.setG24(Collections.emptyList());
//        portBinding.setG5(Collections.emptyList());
//        wan.setPortBinding(portBinding);
//
//        // Set PPP parameters
//        ppp.setUsername(custUserName); // customer username
//        ppp.setPassword(custPassword); // customer password
//        ppp.setType(0);
//        ppp.setIdleTime(null);
//        ppp.setAuthentication(0);
//        ppp.setAcName(null);
//        ppp.setServiceName(null);
//        wan.setPpp(ppp);
//
//        // Set IPv4 parameters
//        TemplateCreatePojo.ParamObject.Wan.Ipv4 ipv4 = new TemplateCreatePojo.ParamObject.Wan.Ipv4();
//        ipv4.setEnableNapt(1);
//        ipv4.setDhcpEnable(1);
//        ipv4.setRequestDns(1);
//        wan.setIpv4(ipv4);
//
//        // Add the wan object to the paramObject
//        paramObject.setWan(Collections.singletonList(wan));
//        paramObject.setWlan(null);
//        paramObject.setVoip(null);
//        templateCreatePojo.setParamObject(paramObject);
//
//        // Convert the POJO to JSON
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        try {
//            String jsonPayload = objectMapper.writeValueAsString(templateCreatePojo);
//            System.out.println(jsonPayload);
//
//            // Send the JSON payload to the API
//            Connfiguration configService = configRepocitory.findByName(configName);
//            if(configService!=null){
//                String baseIp = configService.getBaseurl();
//                String port = configService.getPort();
//                sendPostRequest(jsonPayload, custServMappingId,baseIp,port,generateName(customerName),loggedInUser,loggedInUserMvnoId,serialNumber);
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public String generatePaylodForCDATA(CdataCustDetailsPojo cdataCustDetailsPojo) {

        String apiStatus = "";
        TemplateCreatePojo templateCreatePojo = new TemplateCreatePojo();

        // Set profile name
        templateCreatePojo.setName(generateName(cdataCustDetailsPojo.getCustomerName()));

        // Set type
        templateCreatePojo.setType("wan");

        // Set description
        templateCreatePojo.setTmplDesc("Testing creation through API");

        // Set items (assuming it's null based on your original payload example)
        templateCreatePojo.setItems(null);

        // Create ParamObject and Wan object
        TemplateCreatePojo.ParamObject paramObject = new TemplateCreatePojo.ParamObject();
        TemplateCreatePojo.Wan wan = new TemplateCreatePojo.Wan();

        // Set WAN parameters
        wan.setEnableQos(0);
        wan.setAdminStatus(1);
        wan.setEnableNapt(1);
        wan.setEnableVlan(1);
        wan.setVlanId(200);
        wan.set_802_1Mark(0);
        wan.setServiceType(2);
        wan.setConnectionType(2);
        wan.setIpProtocol(1);
        wan.setMtu(1492);
        wan.setEnableIgmpMldProxy(0);
        wan.setMulticastVlanId(null);

        // Set port binding
        TemplateCreatePojo.PortBinding portBinding = new TemplateCreatePojo.PortBinding();
        portBinding.setLan(Collections.emptyList());
        portBinding.setG24(Collections.emptyList());
        portBinding.setG5(Collections.emptyList());
        wan.setPortBinding(portBinding);

        // Set PPP parameters
        TemplateCreatePojo.Ppp ppp = new TemplateCreatePojo.Ppp();
        ppp.setUsername(cdataCustDetailsPojo.getCustUserName()); // customer username
        ppp.setPassword(cdataCustDetailsPojo.getCustPassword()); // customer password
        ppp.setType(0);
        ppp.setIdleTime(null);
        ppp.setAuthentication(0);
        ppp.setAcName(null);
        ppp.setServiceName(null);
        wan.setPpp(ppp);

        // Set IPv4 parameters
        TemplateCreatePojo.Ipv4 ipv4 = new TemplateCreatePojo.Ipv4();
        ipv4.setEnableNapt(1);
        ipv4.setDhcpEnable(1);
        ipv4.setRequestDns(1);
        wan.setIpv4(ipv4);

        // Set IPv6 (assuming it's null based on your original payload example)
        wan.setIpv6(null);

        // Add Wan object to ParamObject
        paramObject.setWan(Collections.singletonList(wan));
        paramObject.setWlan(null); // Assuming wlan and voip are null
        paramObject.setVoip(null);
        templateCreatePojo.setParamObject(paramObject);

        // Convert the POJO to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonPayload = objectMapper.writeValueAsString(templateCreatePojo);
            System.out.println(jsonPayload);

            // Send the JSON payload to the API
            Connfiguration configService = configRepocitory.findByName(cdataCustDetailsPojo.getConfigName());
            if (configService != null) {
                String baseIp = configService.getBaseurl();
                String port = configService.getPort();
                try{
                    apiStatus = sendPostRequest(jsonPayload, cdataCustDetailsPojo.getCustServMappingId(), baseIp, port, generateName(cdataCustDetailsPojo.getCustomerName()), cdataCustDetailsPojo.getLoggedInUser(),
                            cdataCustDetailsPojo.getLoggedInUserMvnoId(), cdataCustDetailsPojo.getSerialNumber(), cdataCustDetailsPojo.getManufacturer(),cdataCustDetailsPojo.getCustomerId());
                    return  apiStatus;
                }catch (Exception e){
                    apiStatus = "Error : something went wrong !! "+ e.getMessage();
                    e.printStackTrace();
                    return apiStatus;


                }

            }

        } catch (IOException e) {
            apiStatus = "Error : something went wrong !! "+ e.getMessage();
            e.printStackTrace();
            return apiStatus;
        }
        return apiStatus;
    }



    public String sendPostRequest(String jsonPayload,Integer custServMappingId,String baseIp,String port, String profileName, String loggedInUser, Integer loggedInUserMvnoId, String serialNumber,String manufacturer,Integer customerId ){
        String serviceData=null;
        String stage = NMSParamconstant.NMSCUSTDETAILCONST.INITIAL_STAGE;
        NMSCustDetails nmsCustDetails = null;
        HashMap<String, String> responseMap  =null;


        try{
            nmsCustDetails = nmsCustDetailsRepository.findByCustServMapId(Long.valueOf(custServMappingId));
            if(nmsCustDetails == null) {
                nmsCustDetails = new NMSCustDetails();
                nmsCustDetails.setStage(stage);
            }
            if(nmsCustDetails.getStage().equalsIgnoreCase(NMSParamconstant.NMSCUSTDETAILCONST.INITIAL_STAGE)){
                try{
                     responseMap  =  createTemplate(jsonPayload,baseIp,port,profileName,loggedInUser,loggedInUserMvnoId,custServMappingId);
                    if(responseMap.get("response") != null) {
                        int ontResponseCode = Integer.parseInt(responseMap.get("responseCode"));
                        if(ontResponseCode == 200) {
                            stage = NMSParamconstant.NMSCUSTDETAILCONST.TEMPLATE_ADDED_STAGE;
                            nmsCustDetails.setStage(NMSParamconstant.NMSCUSTDETAILCONST.TEMPLATE_ADDED_STAGE);
                            nmsCustDetails.setUsernameForAudit(profileName);
                            stage = NMSParamconstant.NMSCUSTDETAILCONST.TEMPLATE_ADDED_STAGE;
                            serviceData="success..!!";
                            logger.info(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Error while Creating Service: "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
                        }else{
                            serviceData="error..!!";
                        }
                    } else {
                        logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Error while Creating Service: "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +LogConstants.LOG_ERROR +responseMap.get("response")+LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                        return "Template NOT Added.., !"+responseMap.get("response");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Error while Creating Service: "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +LogConstants.LOG_ERROR +e.getMessage()+LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                }

            }if(nmsCustDetails.getStage().equalsIgnoreCase(NMSParamconstant.NMSCUSTDETAILCONST.TEMPLATE_ADDED_STAGE)){
                stage = NMSParamconstant.NMSCUSTDETAILCONST.TEMPLATE_ADDED_STAGE;
                try{
                    if(responseMap.get("response")!=null){
                        CloseableHttpResponse serviceProvisionResponse  = generateServiceProviosionPayload(responseMap.get("templateId"),serialNumber,manufacturer,custServMappingId,baseIp,null,loggedInUser,loggedInUserMvnoId,profileName);
                        if(serviceProvisionResponse != null) {
                            LocalDateTime requestCompletionTime = LocalDateTime.now();
                            int ontResponseCode = serviceProvisionResponse.getStatusLine().getStatusCode();
                            HttpEntity responseEntity = serviceProvisionResponse.getEntity();
                            String errorMessage = "";

                            if(ontResponseCode == 200) {
                                stage = NMSParamconstant.NMSCUSTDETAILCONST.SERVICE_ACTIVATED_STAGE;
                                nmsCustDetails.setStage(NMSParamconstant.NMSCUSTDETAILCONST.SERVICE_ACTIVATED_STAGE);
                                nmsCustDetails.setUsernameForAudit(profileName);
                                stage = NMSParamconstant.NMSCUSTDETAILCONST.SERVICE_ACTIVATED_STAGE;
                            }else{
                                String responseBody = EntityUtils.toString(responseEntity, "UTF-8");
                                JSONObject responseObject = new JSONObject(responseBody.toString());
                                if(responseObject.has("errors")) {
                                    JSONArray errorsArray = responseObject.getJSONArray("errors");
                                    JSONObject errorObject = errorsArray.getJSONObject(0);
                                    // Extract the message
                                    errorMessage = errorObject.getString("message");
                                }
                                logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Error while Creating Service: "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +LogConstants.LOG_ERROR +errorMessage+LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                            }
                        } else {
                            logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Error while Creating Service: "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +LogConstants.LOG_ERROR + serviceProvisionResponse.toString()+LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                            return String.format("Service Not Activated Response :%s", serviceProvisionResponse.toString());
                        }
                    }


                }catch (Exception e){
                    logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Error while activate NMS service"+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_ERROR +e.getMessage()+LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                    throw new RuntimeException("Error while activate NMS service: "+e.getMessage());
                }
                finally {
                    nmsService.updateNMSCustDetails(customerId.longValue(), custServMappingId.longValue(), stage, nmsCustDetails,profileName);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Error while Creating Service: "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +LogConstants.LOG_ERROR +e.getMessage()+LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
        }
        return  serviceData;
    }

    public HashMap<String, String> createTemplate(String jsonPayload, String baseIp, String port, String profileName, String loggedInUser, Integer loggedInUserMvnoId, Integer custServMappingId){
        CloseableHttpResponse response = null;
        String url =null;
        HttpPost httpPost = new HttpPost();
        LocalDateTime requestInitiationTime = null;
        HashMap<String, String> responseMap = new HashMap<>();
        try{
            CloseableHttpClient closeableHttpClient = createHttpClient();
            url  = "http://"+baseIp+"/v1/openapi/cpm/deploy/tmpl/tr069";
            Integer response_code = HttpStatus.SC_EXPECTATION_FAILED;
            requestInitiationTime= LocalDateTime.now();
            logger.debug("\n\n\n C-DATA Add Template Payload: "+jsonPayload+"\n\n\n");
            logger.debug("\n\n\n C-DATA url::::: "+url+"\n\n\n");
            httpPost = createHttpPost(url);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("X-Token","aee1f423ebf34f1eb749e1dee043a710");
            StringEntity requestEntity = new StringEntity(jsonPayload);
            requestEntity.setContentType("application/json");

            httpPost.setEntity(requestEntity);

            try{
                CloseableHttpClient httpClient1 = HttpClients.createDefault();
                response = closeableHttpClient.execute(httpPost);
                System.out.println("Add Template Response: "+response);
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime,requestCompletionTime);
                HttpEntity responseEntity = response.getEntity();
                String responseBody = EntityUtils.toString(responseEntity, "UTF-8");
                String errorMessage=null;
                if(response!=null) {
                    response_code = response.getStatusLine().getStatusCode();
                    if(response_code != 200) {
                        JSONObject responseObject = new JSONObject(responseBody.toString());
                        if(responseObject.has("errors")) {
                            JSONArray errorsArray = responseObject.getJSONArray("errors");
                            JSONObject errorObject = errorsArray.getJSONObject(0);
                            errorMessage=errorObject.getString("message");
                        }
                    }
                }
                // Extract the message

                // Handle different response codes if needed
                if (response_code >= 400) {
                    logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Error while Adding ONT: "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +LogConstants.LOG_ERROR +errorMessage+LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                    apiAuditsService.extractDataAndSavePostApiAudits(url,null,response,httpPost,null,responseTime,null,requestInitiationTime,responseBody,loggedInUser,loggedInUserMvnoId,profileName,null);
                }else{
                    JSONObject jsonObject = new JSONObject(responseBody);
                    String cdataServiceTemplate = jsonObject.getString("data");
                    responseMap.put("templateId",cdataServiceTemplate);
                    responseMap.put("response",response.toString());
                    responseMap.put("responseCode","200");
                    //rabbitmq call for sending uuid to save it against customerServiceMappingId
                    sendCDATAServiceOrTemplateUuidToCMS(custServMappingId,null,cdataServiceTemplate);
                    logger.info(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Template Added Successfully"+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
                    apiAuditsService.extractDataAndSavePostApiAudits(url,null,response,httpPost,null,responseTime,null,requestInitiationTime,responseBody,loggedInUser,loggedInUserMvnoId,profileName,null);
                }
                return responseMap;

            }catch (Exception e){
                logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Error while Adding ont: "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +LogConstants.LOG_ERROR +e.getMessage()+LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                e.printStackTrace();
                apiAuditsService.extractDataAndSavePostApiAudits(url,null,response,httpPost,null,null,e.getMessage(),requestInitiationTime,null,loggedInUser,loggedInUserMvnoId,profileName,null);

            }

        }catch (Exception e){
            logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Error while Adding ONT: "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +LogConstants.LOG_ERROR +e.getMessage()+LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
            e.printStackTrace();
            apiAuditsService.extractDataAndSavePostApiAudits(url,null,response,httpPost,null,null,e.getMessage(),requestInitiationTime,null,loggedInUser,loggedInUserMvnoId,profileName,null);

        }
        return  responseMap;
    }

    public CloseableHttpResponse generateServiceProviosionPayload(String templateId, String serialNum, String manufacturer, Integer customerServiceMappingId, String baseIp, String port, String loggedInUser, Integer loggedinUserMvnoId,String profileName){

        // Create an instance of the payload
        ServiceProvisonPojo payload = new ServiceProvisonPojo();
        payload.setTmplId(templateId);

        ServiceProvisonPojo.ConditionContent conditionContent = new ServiceProvisonPojo.ConditionContent();
        conditionContent.setSn(serialNum);
        conditionContent.setManufacturer(manufacturer);
        //conditionContent.setModel("FD504GW-DX-R461");
        payload.setConditionContent(conditionContent);

        payload.setConfigDesc("Provisioning customer3");

        // Convert the POJO to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);
            System.out.println(jsonPayload);
            // Send the JSON payload to the API
            CloseableHttpResponse response = CerateAndActivateService(jsonPayload,customerServiceMappingId,baseIp,port,loggedInUser,loggedinUserMvnoId,profileName);
            return  response;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }



    public CloseableHttpResponse CerateAndActivateService(String jsonPayload, Integer customerServiceMappingId, String baseIp, String port, String loggedInUser, Integer loggedinUserMvnoId, String profileName) throws Exception {
        CloseableHttpClient httpClient = createHttpClient();
        String strUR = "http://" + baseIp + "/v1/openapi/cpm/provisioning";
        CloseableHttpResponse response = null;
        HttpPut httpPut = new HttpPut();
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        Integer response_code = HttpStatus.SC_EXPECTATION_FAILED;
        try {
            System.out.println("Active Service Payload: "+jsonPayload);
            httpPut = createHttpPut(strUR);
            httpPut.setHeader("Content-Type", "application/json");
            httpPut.setHeader("X-Token", "aee1f423ebf34f1eb749e1dee043a710");
            StringEntity requestEntity = new StringEntity(jsonPayload);
            logger.debug("\n\n\n C-DATA Add Service Provisoning Payload: "+jsonPayload+"\n\n\n");
            logger.debug("\n\n\n C-DATA url::::: "+strUR+"\n\n\n");
            requestEntity.setContentType("application/json");
            httpPut.setEntity(requestEntity);
            try {
                CloseableHttpClient httpClient1 = HttpClients.createDefault();
                response = httpClient.execute(httpPut);
                System.out.println("\n\n\n Active Service Response: "+response+"\n\n\n");
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime,requestCompletionTime);
                response_code = response.getStatusLine().getStatusCode();

                HttpEntity responseEntity = response.getEntity();
                String responseBody = EntityUtils.toString(responseEntity, "UTF-8");

                if(response_code.equals(200)) {
                    apiAuditsService.extractDataAndSavePostApiAudits(strUR,null,response,httpPut,null,responseTime,responseBody.toString(),requestInitiationTime,responseBody,loggedInUser,loggedinUserMvnoId,profileName,null);
                    // generate uuid for profileName
                    JSONObject jsonObject = new JSONObject(responseBody);
                    String cdataServiceUuid = jsonObject.getString("data");
                    //rabbitmq call for sending uuid to save it against customerServiceMappingId
                    sendCDATAServiceOrTemplateUuidToCMS(customerServiceMappingId,cdataServiceUuid,null);
                }

                // Handle different response codes if needed
                if (response_code >= 400) {
                    JSONObject responseObject = new JSONObject(responseBody.toString());
                    String errorMessage = "";
                    if(responseObject.has("errors")) {
                        JSONArray errorsArray = responseObject.getJSONArray("errors");
                        JSONObject errorObject = errorsArray.getJSONObject(0);
                        errorMessage = errorObject.getString("message");
                    }
                    logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Error while Creating Service: "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +LogConstants.LOG_ERROR +errorMessage+LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                    apiAuditsService.extractDataAndSavePostApiAudits(strUR,null,response,httpPut,null,null,responseBody.toString(),requestInitiationTime,responseBody,loggedInUser,loggedinUserMvnoId,profileName,null);

                }else{
                    logger.info(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Error while Creating Service: "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
                }
                return response;

            } catch (Exception e) {
                logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Error while Creating Service: "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +LogConstants.LOG_ERROR +e.getMessage()+LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                e.printStackTrace();
                apiAuditsService.extractDataAndSavePostApiAudits(strUR,null,response,httpPut,null,null,e.getMessage(),requestInitiationTime,null,loggedInUser,loggedinUserMvnoId,profileName,null);

            }
        } catch (Exception e) {
            logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Error while Creating Service: "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +LogConstants.LOG_ERROR +e.getMessage()+LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
            e.printStackTrace();
            apiAuditsService.extractDataAndSavePostApiAudits(strUR,null,response,httpPut,null,null,e.getMessage(),requestInitiationTime,null,loggedInUser,loggedinUserMvnoId,profileName,null);
        }
        return  null;
    }

    public void sendCDATAServiceOrTemplateUuidToCMS(Integer customerServiceMappingId, String serviceUuid,String templateid){
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("userName","RabbitMq");
        MDC.put("traceId",traceContext.traceIdString());
        MDC.put("spanId",traceContext.spanIdString());
        UuidDataDTO uuidDataDTO =new UuidDataDTO();
        try {
            if(serviceUuid!=null && templateid==null){
                uuidDataDTO = new UuidDataDTO(customerServiceMappingId,serviceUuid,null);
            }else if(serviceUuid==null && templateid!=null){
                uuidDataDTO = new UuidDataDTO(customerServiceMappingId,null,templateid);
            }
//            messageSender.send(uuidDataDTO, RabbitMqConstants.QUEUE_SEND_UUID_DATA_TO_CMS);
            kafkaMessageSender.send(new KafkaMessageData(uuidDataDTO, uuidDataDTO.getClass().getSimpleName()));
//            logger.info("Uuid data send successfully to cms");
        }catch (Exception e){
            //System.out.println("Error while sending uuid data to cms : "+e.getMessage());
            logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Unable send Uuid data send  to cms"+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_ERROR +LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
            logger.info("Unable send Uuid data send  to cms");
        }
        finally {
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

    }



}
