package com.savbill.integrationsystem.nms;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.integrationsystem.apiAudits.NMSServices.ApiAuditsService;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.constants.Constants;
import com.savbill.integrationsystem.core.security.constants.LogConstants;
import com.savbill.integrationsystem.core.utillity.APIConstants;
import com.savbill.integrationsystem.kafka.KafkaMessageData;
import com.savbill.integrationsystem.kafka.KafkaMessageSender;
import com.savbill.integrationsystem.nms.constants.NMSParamconstant;
import com.savbill.integrationsystem.nms.entity.*;
import com.savbill.integrationsystem.nms.entity.*;
import com.savbill.integrationsystem.nms.repository.NMSCustDetailsRepository;
import com.savbill.integrationsystem.pojo.NMSServiceActivationDTO;
import com.savbill.integrationsystem.pojo.ProductParameterDefaultValueMappingDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
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
import org.springframework.util.CollectionUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NmsService {
    static final String NMS_SERVER_IP = "27.34.251.54";
    static final String NMS_SERVER_PORT = "446";
    private static final Logger logger = LoggerFactory.getLogger("NmsService.class");

    @Autowired
    private NMSCustDetailsRepository nmsCustDetailsRepository;
    @Autowired
    ApiAuditsService apiAuditsService;

    @Autowired
    private ConfigRepocitory configRepocitory;

//    @Autowired
//    MessageSender messageSender;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;
    @Autowired
    Tracer tracer;

    private static CloseableHttpClient createHttpClient() throws Exception {
        return HttpClients.custom()
                .setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .build();
    }

    public static HttpPost createHttpPost(String url) {
        return new HttpPost(URI.create(url));
    }

    public static String extractJwtToken(String response) {
        int start = response.indexOf("\"jwt\":\"") + 7; // Length of "\"jwt\":\""
        int end = response.indexOf("\"", start);

        if (start >= 7 && end > start) {
            return response.substring(start, end);
        }

        return null;
    }

    private static Map<String, Object> buildRequestBody(int ethNodeEdgePointCount, int id, String ontMode, String serialNumber, int potsNodeEdgePointCount, String ponNodeName, String ponCiSiPn, String ponIsLct, String upstreamMappingType, String pbitControl) {
        Map<String, Object> requestBody = new HashMap<>();

        Map<String, Object> preprovisionData = new HashMap<>();
        preprovisionData.put("eth-circuitpack-preprovision-data", buildCircuitPackData(ethNodeEdgePointCount));
        //preprovisionData.put("id", id);
        preprovisionData.put("ont-mode", ontMode);
        preprovisionData.put("serial-number", serialNumber);
        preprovisionData.put("pots-circuitpack-preprovision-data", buildCircuitPackData(potsNodeEdgePointCount));

        Map<String, Object> ponNodeEdgePointRef = new HashMap<>();
        Map<String, Object> node = new HashMap<>();
        node.put("name", ponNodeName);

        Map<String, Object> nodeEdgePoint = new HashMap<>();
        nodeEdgePoint.put("ci-si-pn", "1-1-11");
        nodeEdgePoint.put("is-lct", ponIsLct);

        ponNodeEdgePointRef.put("node", node);
        ponNodeEdgePointRef.put("node-edge-point", nodeEdgePoint);

        preprovisionData.put("pon-node-edge-point-ref", ponNodeEdgePointRef);

        preprovisionData.put("upstream-mapping-type", upstreamMappingType);
        preprovisionData.put("pbit-control", pbitControl);

        requestBody.put("preprovision-data", preprovisionData);

        return requestBody;
    }

    private static Map<String, Object> buildCircuitPackData(int nodeEdgePointCount) {
        Map<String, Object> circuitPackData = new HashMap<>();
        circuitPackData.put("node-edge-point-count", nodeEdgePointCount);

        return circuitPackData;
    }

    private static HttpGet createHttpGet(String url) {
        return new HttpGet(url);
    }

    private static Map<String, Object> buildCircuitPackDataForCreateService(int count) {
        Map<String, Object> circuitPackData = new HashMap<>();
        circuitPackData.put("count", count);
        return circuitPackData;
    }


    public CloseableHttpResponse addOnt(String token, List<ProductParameterDefaultValueMappingDTO> params, String serialNum, String baseIP, String port,String loggedInUser, Integer loggedInUserMvnoId, String gponPort,NMSServiceActivationDTO nmsServiceActivationDTO) throws Exception {
        CloseableHttpClient httpClient = createHttpClient();
        String userNameForAudit  = getProfileUserName(params,nmsServiceActivationDTO);
        String oltIp = getValueFromName(params, NMSParamconstant.OLT_IP);
        String oltName = getValueFromName(params, NMSParamconstant.OLT_NAME);
        String strUR = "https://" + baseIP + ":" + port + "/nmsnbi-rest/tapi/data/context/physical-context/device/equipment/preprovision";
        Integer response_code = HttpStatus.SC_EXPECTATION_FAILED;
        Map<String, Object> requestBody = buildRequestBody(
                2, 20, "HYBRID", serialNum, 0, oltName, gponPort, "false", "VID", "DISABLED");
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        HttpPost httpPost = new HttpPost();
        CloseableHttpResponse response = null;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(requestBody);
            System.out.println("Add Ont Payload: "+jsonString);
//            logger.info("NMS Request get for add ONT strurl "+strUR+" payload: "+jsonString);
            httpPost = createHttpPost(strUR);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Authorization", "Bearer " + token);
            StringEntity requestEntity = new StringEntity(jsonString.toString());
            requestEntity.setContentType("application/json");
            httpPost.setEntity(requestEntity);

            try {
                CloseableHttpClient httpClient1 = HttpClients.createDefault();
                response = httpClient.execute(httpPost);
                System.out.println("Add Ont Response: "+response);
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
                    apiAuditsService.extractDataAndSavePostApiAudits(strUR,null,response,httpPost,null,responseTime,null,requestInitiationTime,responseBody,loggedInUser,loggedInUserMvnoId,userNameForAudit,null);
                }else{
                    logger.info(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "ONT Added Successfully"+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
                    apiAuditsService.extractDataAndSavePostApiAudits(strUR,null,response,httpPost,null,responseTime,null,requestInitiationTime,responseBody,loggedInUser,loggedInUserMvnoId,userNameForAudit,null);
                }
                return response;

            } catch (Exception e) {
                logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Error while Adding ont: "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +LogConstants.LOG_ERROR +e.getMessage()+LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                e.printStackTrace();
                apiAuditsService.extractDataAndSavePostApiAudits(strUR,null,response,httpPost,null,null,e.getMessage(),requestInitiationTime,null,loggedInUser,loggedInUserMvnoId,userNameForAudit,null);

            }
        } catch (Exception e) {
            logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Error while Adding ONT: "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +LogConstants.LOG_ERROR +e.getMessage()+LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
            e.printStackTrace();
            apiAuditsService.extractDataAndSavePostApiAudits(strUR,null,response,httpPost,null,null,e.getMessage(),requestInitiationTime,null,loggedInUser,loggedInUserMvnoId,userNameForAudit,null);
        }
        return null;

    }

    private List<UpstreamProfileDetails> getUpstreamBWProfile(String token, String profileName, List<ProductParameterDefaultValueMappingDTO> params,String loggedInUser, Integer loggedInUserMvnoId ) throws Exception {
        List<UpstreamProfileDetails> details=new ArrayList<>();
        CloseableHttpResponse response = null;
        LocalDateTime requestInitTime = LocalDateTime.now();
        String strUR = "https://" + NMS_SERVER_IP + ":" + NMS_SERVER_PORT +
                "/nmsnbi-rest/tapi/data/context/connectivity-context/upstream-bw-profile?continue=0&size=500&profileName="+profileName;
        HttpGet httpGet = new HttpGet();
        try {

            httpGet = createHttpGet(strUR);
            CloseableHttpClient httpClient = createHttpClient();

            // Set headers
            httpGet.setHeader("Content-Type", "application/json");
            httpGet.setHeader("Authorization", "Bearer " + token);
            logger.info("NMS Request for upstream BW strurl "+strUR);
            try {
                response = httpClient.execute(httpGet);
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitTime,requestCompletionTime);

                // Check the response status
                int statusCode = response.getStatusLine().getStatusCode();
                logger.info("NMS Response for upstream BW strurl "+strUR+" response: "+response.toString());

                // Read and print the response body
                String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
                if (statusCode == 200) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    List<UpstreamBandwidthProfile> profiles = objectMapper.readValue(
                            responseBody,
                            new TypeReference<List<UpstreamBandwidthProfile>>() {
                            }
                    );
                    details = profiles.stream().map(UpstreamBandwidthProfile::getProfileDetails).collect(Collectors.toList());
                    apiAuditsService.extractDataAndSaveGetApiAudits(strUR,null,response,httpGet,responseTime,responseBody.toString(),requestInitTime,responseBody,loggedInUser,loggedInUserMvnoId,null);

                }

                // Handle different response codes if needed
                if (statusCode >= 400) {
                    apiAuditsService.extractDataAndSaveGetApiAudits(strUR,null,response,httpGet,responseTime,responseBody.toString(),requestInitTime,responseBody,loggedInUser,loggedInUserMvnoId,null);
                }

            } catch (Exception e) {
                e.printStackTrace();
                apiAuditsService.extractDataAndSaveGetApiAudits(strUR,null,response,httpGet,null,e.getMessage(),requestInitTime,null,loggedInUser,loggedInUserMvnoId,null);

            }

        } catch (Exception e) {
            logger.info(e.getMessage());
            e.printStackTrace();
            apiAuditsService.extractDataAndSaveGetApiAudits(strUR,null,response,httpGet,null,e.getMessage(),requestInitTime,null,loggedInUser,loggedInUserMvnoId,null);
        }
        return details;
    }

    private List<DownstreamBandwidthProfile> getDownStreamBWProfile(String token, String profileType, List<ProductParameterDefaultValueMappingDTO> params, String loggedInUser, Integer loggedInUserMvnoId) throws Exception {
        List<DownstreamBandwidthProfile> profiles=new ArrayList<>();
        HttpGet httpGet = new HttpGet();
        CloseableHttpResponse response = null;
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        String strUR = "https://" + NMS_SERVER_IP + ":" + NMS_SERVER_PORT +
                "/nmsnbi-rest/tapi/data/context/connectivity-context/downstream-bandwidth-profile?continue=0&size=100&profileName="+profileType;
        try {
            httpGet = createHttpGet(strUR);
            CloseableHttpClient httpClient = createHttpClient();

            // Set headers
            httpGet.setHeader("Content-Type", "application/json");
            httpGet.setHeader("Authorization", "Bearer " + token);
            logger.info("NMS Request for downstream BW strurl "+strUR);
            try  {
                response = httpClient.execute(httpGet);
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime,requestCompletionTime);

                // Check the response status
                int statusCode = response.getStatusLine().getStatusCode();


                // Read and print the response body
                String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
                logger.info("NMS Response for downstream BW strurl "+strUR+" response: "+response.toString());
                if (statusCode == 200) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    profiles = objectMapper.readValue(
                            responseBody,
                            new TypeReference<List<DownstreamBandwidthProfile>>() {
                            }
                    );
                    apiAuditsService.extractDataAndSaveGetApiAudits(strUR,null,response,httpGet,responseTime,responseBody.toString(),requestInitiationTime,responseBody,loggedInUser,loggedInUserMvnoId,null);
                }

                if (statusCode >= 400) {
                    apiAuditsService.extractDataAndSaveGetApiAudits(strUR,null,response,httpGet,responseTime,responseBody.toString(),requestInitiationTime,responseBody,loggedInUser,loggedInUserMvnoId,null);
                }
            } catch (Exception e) {
                e.printStackTrace();
                apiAuditsService.extractDataAndSaveGetApiAudits(strUR,null,response,httpGet,null,e.getMessage(),requestInitiationTime,null,loggedInUser,loggedInUserMvnoId,null);

            }

        } catch (Exception e) {
            e.printStackTrace();
            apiAuditsService.extractDataAndSaveGetApiAudits(strUR,null,response,httpGet,null,e.getMessage(),requestInitiationTime,null,loggedInUser,loggedInUserMvnoId,null);
        }
        return profiles;
    }



    //lastApi
    private static final ObjectMapper objectMapper = new ObjectMapper();
    public static Map<String, Object> buildDynamicRequestBody(String upstreamprofileUuid,String downstreamprofileUuid, String serialNum, String username, String oltIp, String gponPort, String ontOnuPort, String svlan, String cvlan) {

        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> connectivityService = new HashMap<>();
        List<Map<String, Object>> nameList = new ArrayList<>();
        Map<String, Object> nameMap = new HashMap<>();
        List<Map<String, Object>> endPointList = new ArrayList<>();
        List<Map<String, Object>> valuenameList=buildAdditionalInformationList("ConnectionEndPoint",oltIp+gponPort);
        Map<String, Object> endPointMap1 = buildEndPointMap("ConnectionEndPoint", oltIp+gponPort, "ETH", downstreamprofileUuid, false, "EP_Dot1ad_with_Dot1q", "0x8100",null,null,null, svlan, cvlan);
        Map<String, Object> endPointMap2 = buildEndPointMap("SerialNumber", serialNum, "ETH", null, true, null, null,upstreamprofileUuid,"ONTEthPortInstance",ontOnuPort, svlan, cvlan);
//        List<Map<String, Object>> additionalInformationList = buildAdditionalInformationList("HSI", "FC", "QAD", "QOS_DOMAIN", "GPON_SINGLETON");
        List<Map<String, Object>> additionalInformationList = buildAdditionalInformationList(
                "gponServiceType", "HSI",
                "CustomerName", "FC",
                "TechType", "QAD",
                "Domain", "QOS_DOMAIN",
                "gponType", "GPON_SINGLETON"
        );

        // Build the structure dynamically
        nameMap.put("value-name", "Connection");
        nameMap.put("value", username);
        nameList.add(nameMap);

        connectivityService.put("name", nameList);
        connectivityService.put("connectivity-constraint", Collections.singletonMap("service-type", "MULTIPOINT_CONNECTIVITY"));
        endPointList.add(endPointMap1);
        endPointList.add(endPointMap2);
        connectivityService.put("end-point", endPointList);
        connectivityService.put("layer-protocol-name", "ETH");
        connectivityService.put("additional-information", additionalInformationList);
        connectivityService.put("direction", "BIDIRECTIONAL");

        requestBody.put("connectivity-service", connectivityService);

        return requestBody;
    }


    private static Map<String, Object> buildEndPointMap(String valueName, String value, String layerProtocolName,
                                                        String downstreamBandwidthProfileUuid, boolean untaggedAndPrioTaggedIncluded,
                                                        String epType, String outerTpId,String upstreamstreamBandwidthProfileUuid,String valueName1, String value1,String svlan, String cvlan ) {
        Map<String, Object> endPointMap = new HashMap<>();
        List<Map<String, Object>> nameList = new ArrayList<>();
        Map<String, Object> nameMap = new HashMap<>();
        Map<String, Object> nrpCarrierEthMap = new HashMap<>();
        Map<String, Object> ceVlanIdListAndUntagMap = new HashMap<>();
        List<Map<String, Object>> svlanIdList = new ArrayList<>();
        List<Map<String, Object>> cvlanIdList = new ArrayList<>();
        Map<String, Object> svlanIdMap = new HashMap<>();
        Map<String, Object> cvlanIdMap = new HashMap<>();
        Map<String, Object> sVlanIdListMap = new HashMap<>();
        List<Map<String, Object>> sVlanIdList = new ArrayList<>();
        Map<String, Object> downstreamBandwidthProfile = new HashMap<>();
        Map<String, Object> upstreamstreamBandwidthProfile = new HashMap<>();
        List<Map<String, Object>> additionalInformationList = buildAdditionalInformationList(
                "ep-type", "EP_Dot1ad_with_Dot1q",
                "outer-tp-id", "0x8100"
        );
//        Map<String, Object> entry1 = new HashMap<>();
        nameMap.put("value-name", valueName);
        nameMap.put("value", value);
//        nameList.add(entry1);
        if(valueName1!=null){
            Map<String, Object> entry2 = new HashMap<>();
            entry2.put("value-name", valueName1);
            entry2.put("value", value1);
            nameList.add(entry2);
        }
        nameList.add(nameMap);


        cvlanIdMap.put("vlan-id", Integer.valueOf(cvlan));
        cvlanIdList.add(cvlanIdMap);

        ceVlanIdListAndUntagMap.put("vlan-id", cvlanIdList);
        ceVlanIdListAndUntagMap.put("vlan-id-mapping-type", "LIST");
        ceVlanIdListAndUntagMap.put("untagged-and-prio-tagged-included", untaggedAndPrioTaggedIncluded);

        svlanIdMap.put("vlan-id", Integer.valueOf(svlan));
        svlanIdList.addAll(Collections.singleton(svlanIdMap));
        sVlanIdListMap.put("vlan-id-list", Collections.singleton(svlanIdMap));
        sVlanIdListMap.put("type", "LIST");
        if(downstreamBandwidthProfileUuid != null) {
            nrpCarrierEthMap.put("ce-vlan-id-list-and-untag", ceVlanIdListAndUntagMap);
            nrpCarrierEthMap.put("cos-identifier-list", Collections.singletonList(Collections.singletonMap("cos-name", "0")));
            nrpCarrierEthMap.put("s-vlan-id-list", sVlanIdListMap);
            downstreamBandwidthProfile.put("downstream-bandwidth-profile-uuid", downstreamBandwidthProfileUuid);
        } else if( upstreamstreamBandwidthProfileUuid != null) {
            nrpCarrierEthMap.put("ce-vlan-id-list-and-untag", ceVlanIdListAndUntagMap);
            nrpCarrierEthMap.put("cos-identifier-list", Collections.singletonList(Collections.singletonMap("cos-name", "0")));
            upstreamstreamBandwidthProfile.put("upstream-bandwidth-profile-uuid",upstreamstreamBandwidthProfileUuid);
        }


//        additionalInformationList.add(Collections.singletonMap("value-name", "ep-type"));
//        additionalInformationList.add(Collections.singletonMap("value", epType));
//        additionalInformationList.add(Collections.singletonMap("value-name", "outer-tp-id"));
//        additionalInformationList.add(Collections.singletonMap("value", outerTpId));

        endPointMap.put("name", nameList);
        endPointMap.put("layer-protocol-name", layerProtocolName);
        endPointMap.put("nrp-carrier-eth-connectivity-end-point-resource", nrpCarrierEthMap);
        if(downstreamBandwidthProfileUuid != null)
            endPointMap.put("downstream-bandwidth-profile", downstreamBandwidthProfile);
        if(upstreamstreamBandwidthProfileUuid != null)
            endPointMap.put("upstream-bandwidth-profile", upstreamstreamBandwidthProfile);
        endPointMap.put("additional-information", additionalInformationList);

        return endPointMap;
    }
    private static List<Map<String, Object>> buildAdditionalInformationList(String... keyValuePairs) {
        List<Map<String, Object>> additionalInformationList = new ArrayList<>();

        if (keyValuePairs.length % 2 == 0) {
            for (int i = 0; i < keyValuePairs.length; i += 2) {
                Map<String, Object> additionalInfoMap = new HashMap<>();
                additionalInfoMap.put("value-name", keyValuePairs[i]);
                additionalInfoMap.put("value", keyValuePairs[i + 1]);
                additionalInformationList.add(additionalInfoMap);
            }
        } else {
            // Handle an odd number of parameters (invalid input)
            throw new IllegalArgumentException("Odd number of parameters provided");
        }

        return additionalInformationList;
    }

//    private static List<Map<String, Object>> buildAdditionalInformationList(String gponServiceType, String customerName,
//                                                                            String techType, String domain, String gponType) {
//        List<Map<String, Object>> additionalInformationList = new ArrayList<>();
//        additionalInformationList.add(Collections.singletonMap("value-name", "gponServiceType"));
//        additionalInformationList.add(Collections.singletonMap("value", gponServiceType));
//        additionalInformationList.add(Collections.singletonMap("value-name", "CustomerName"));
//        additionalInformationList.add(Collections.singletonMap("value", customerName));
//        additionalInformationList.add(Collections.singletonMap("value-name", "TechType"));
//        additionalInformationList.add(Collections.singletonMap("value", techType));
//        additionalInformationList.add(Collections.singletonMap("value-name", "Domain"));
//        additionalInformationList.add(Collections.singletonMap("value", domain));
//        additionalInformationList.add(Collections.singletonMap("value-name", "gponType"));
//        additionalInformationList.add(Collections.singletonMap("value", gponType));
//        return additionalInformationList;
//    }

    public CloseableHttpResponse  CerateAndActivateService(String token,String upstreamprofileUuid,String downstreamprofileUuid, List<ProductParameterDefaultValueMappingDTO> params, String serialNum, String profileName, Integer customerServiceMappingId, String baseIp, String port, String gponPort,String loggedInUser, Integer loggedinUserMvnoId) throws Exception {
        CloseableHttpClient httpClient = createHttpClient();
        String strUR = "https://" + baseIp + ":" + port + "/nmsnbi-rest/tapi/data/context/connectivity-context/connectivity-service?command=CreateAndActivateService";
        Integer response_code = HttpStatus.SC_EXPECTATION_FAILED;
        String username = getValueFromName(params, NMSParamconstant.CONNECTION_NAME);
        String oltIp = getValueFromName(params, NMSParamconstant.OLT_IP);
        String ontOnuPort = getValueFromName(params, NMSParamconstant.ONT_UNI_Port);
        String svlan = getValueFromName(params, NMSParamconstant.SVLAN);
        String cvlan = getValueFromName(params, NMSParamconstant.CVLAN);
        username = username +"-"+customerServiceMappingId+ "-" + profileName;
        if(gponPort != null)
            gponPort = gponPort.replaceAll("-","|");

        Map<String, Object> requestBody = buildDynamicRequestBody(upstreamprofileUuid,downstreamprofileUuid, serialNum, username, oltIp,gponPort,ontOnuPort,svlan,cvlan);
        CloseableHttpResponse response = null;
        HttpPost httpPost = new HttpPost();
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        try {
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            headers.setBearerAuth(token);
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(requestBody);
            System.out.println("Active Service Payload: "+jsonString);
            httpPost = createHttpPost(strUR);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Authorization", "Bearer " + token);
            StringEntity requestEntity = new StringEntity(jsonString.toString());
            requestEntity.setContentType("application/json");
            httpPost.setEntity(requestEntity);
//            CloseableHttpResponse response = httpClient.execute(httpPost);
            try {

                CloseableHttpClient httpClient1 = HttpClients.createDefault();
                response = httpClient.execute(httpPost);
                System.out.println("Active Service Response: "+response);
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime,requestCompletionTime);
                response_code = response.getStatusLine().getStatusCode();

                HttpEntity responseEntity = response.getEntity();
                String responseBody = EntityUtils.toString(responseEntity, "UTF-8");

                if(response_code.equals(200)) {
                    apiAuditsService.extractDataAndSavePostApiAudits(strUR,null,response,httpPost,null,responseTime,responseBody.toString(),requestInitiationTime,responseBody,loggedInUser,loggedinUserMvnoId,username,null);
                    // generate uuid for profileName
                    JSONObject jsonObject = new JSONArray(responseBody).getJSONObject(0).getJSONObject("connectivity-service");
                    String uuid = jsonObject.getString("uuid");
                    //rabbitmq call for sending uuid to save it against customerServiceMappingId
                    sendUuidToCMS(customerServiceMappingId,uuid);
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
                    apiAuditsService.extractDataAndSavePostApiAudits(strUR,null,response,httpPost,null,null,responseBody.toString(),requestInitiationTime,responseBody,loggedInUser,loggedinUserMvnoId,username,null);

                }else{
                    logger.info(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Error while Creating Service: "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
                }
                return response;

            } catch (Exception e) {
                logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Error while Creating Service: "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +LogConstants.LOG_ERROR +e.getMessage()+LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                e.printStackTrace();
                apiAuditsService.extractDataAndSavePostApiAudits(strUR,null,response,httpPost,null,null,e.getMessage(),requestInitiationTime,null,loggedInUser,loggedinUserMvnoId,username,null);

            }
        } catch (Exception e) {
            logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Error while Creating Service: "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +LogConstants.LOG_ERROR +e.getMessage()+LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
            e.printStackTrace();
            apiAuditsService.extractDataAndSavePostApiAudits(strUR,null,response,httpPost,null,null,e.getMessage(),requestInitiationTime,null,loggedInUser,loggedinUserMvnoId,username,null);
        }
        return  null;
    }



//    public String deacctivate(String user, String password,String uuid) throws Exception {
//        CloseableHttpClient httpClient = createHttpClient();
//        String url=getJwtTokenfromUrl(user,password);
//        String strUR = "https://" + NMS_SERVER_IP + ":" + NMS_SERVER_PORT + "/nmsnbi-rest/tapi/data/context/connectivity-context/connectivity-services?uuids="+uuid+"&command=DeactivateAndDelete";
//        HttpDelete httpDelete=createHttpDelete(strUR);
//        httpDelete.setHeader("Content-Type", "application/json");
//        httpDelete.setHeader("Authorization", "Bearer " + url);
//        CloseableHttpResponse response = null;
//        LocalDateTime requestInitiationTime = LocalDateTime.now();
//        try  {
//            CloseableHttpClient httpClient1 = HttpClients.createDefault();
//            response = httpClient.execute(httpDelete);
//            LocalDateTime requestCompletionTime = LocalDateTime.now();
//            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime,requestCompletionTime);
//
//            System.out.println("Response Code: " +  response.getStatusLine().getStatusCode());
//
//            HttpEntity responseEntity = response.getEntity();
//            String responseBody = EntityUtils.toString(responseEntity, "UTF-8");
//            System.out.println("Response Body: " + responseBody);
//            apiAuditsService.extractDataAndSaveDeleteApiAudits(strUR,null,response,httpDelete,responseTime,responseBody.toString(),requestInitiationTime);
//            // Handle different response codes if needed
//            if ( response.getStatusLine().getStatusCode() >= 400) {
//                System.out.println("Response Body: " + responseBody.toString());
//                apiAuditsService.extractDataAndSaveDeleteApiAudits(strUR,null,response,httpDelete,responseTime,responseBody.toString(),requestInitiationTime);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            apiAuditsService.extractDataAndSaveDeleteApiAudits(strUR,null,response,httpDelete,null,e.getMessage(),requestInitiationTime);
//        }
//       return null;
//    }


    public String getJwtTokenfromUrl(String user, String password, String baseIp, String port,String loggedInUser, Integer loggedInUserMvnoId,String userNameForAudit) {
        MDC.put("type", "FETCH");
        String strUR = "https://"+baseIp+":"+port+"/nmsnbi-rest/tapi/data/context/auth-context/auth-token";
        HttpPost httpPost = new HttpPost();
        CloseableHttpResponse response = null;
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        try {
            // Create a custom HttpClient with relaxed SSL verification
            CloseableHttpClient httpClient = createHttpClient();

            // Create an HttpPost with the URL and headers
            httpPost = createHttpPost(strUR);
            httpPost.setHeader("User", user);
            httpPost.setHeader("Password", password);
            logger.info("Request for token from url: "+strUR+" for user: "+user);
            // Execute the request and get the response
            response = httpClient.execute(httpPost);

            LocalDateTime requestCompletionTime = LocalDateTime.now();

            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime,requestCompletionTime);

            try {
                // Read and handle the response entity
                HttpEntity entity = response.getEntity();
                String token = extractJwtToken(EntityUtils.toString(entity));

                //String responseBody = EntityUtils.toString(entity, "UTF-8");
                apiAuditsService.extractDataAndSavePostApiAudits(strUR,null,response,httpPost,null, responseTime,null,requestInitiationTime,null,loggedInUser,loggedInUserMvnoId,userNameForAudit,null);
                logger.info("NMS successfully get Token for username: "+user);
                return token;
            } catch (Exception ex) {
                logger.info("NMS Exception to get Token for username: "+user);
                return "NMS Exception to get Token for username: "+user;
            }finally {
                response.close();
            }
        } catch (Exception e) {
            apiAuditsService.extractDataAndSavePostApiAudits(strUR,null,response,httpPost,null, null,e.getMessage(),requestInitiationTime,null,loggedInUser,loggedInUserMvnoId,userNameForAudit,null);
            e.printStackTrace(); // Handle exceptions appropriately in your application
            return "Error occurred: " + e.getMessage();
        }

    }

//    public String createService(String user, String password) throws Exception {
//        String token=getJwtTokenfromUrl(user,password);
//        String serviceData=null;
////        if(!token.isEmpty()) {
////            Integer addont = addOnt(token);
////
////            String profileName = "HSI_V20_200M";
////            UpstreamProfileDetails upstreamProfileDetails = getUpstreamBWProfile(token, profileName);
////            List<DownstreamBandwidthProfile> downstreamBandwidthProfiles = getDownStreamBWProfile(token, "HSI");
////            serviceData= CerateAndActivateService(token, "HSI_V20_200M", "TejNMS-IngressTCP-2580");
////        }
//        return  serviceData;
//    }


    public String activateNMSServices(NMSServiceActivationDTO nmsServiceActivationData) {
        String serviceData=null;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("userName",nmsServiceActivationData.getUsername());
        MDC.put("traceId",traceContext.traceIdString());
        MDC.put("spanId",traceContext.spanIdString());
        MDC.put("type","CREATE");
        try {
            Connfiguration configService = configRepocitory.findByName(nmsServiceActivationData.getConfigName());
    //        logger.info("Request get for Activate NMS services: "+nmsServiceActivationData.toString());
            if(configService != null) {
                String username=configService.getUsername();
                String password=configService.getPassword();
                String baseIp = configService.getBaseurl();
                String port = configService.getPort();
                if(nmsServiceActivationData != null && !CollectionUtils.isEmpty(nmsServiceActivationData.getCustInvParams())) {
                    List<ProductParameterDefaultValueMappingDTO> params = nmsServiceActivationData.getCustInvParams().stream().map(custInvParamsDto ->
                            new ProductParameterDefaultValueMappingDTO(custInvParamsDto.getId(), custInvParamsDto.getCustSerMapId(), custInvParamsDto.getParamName(), custInvParamsDto.getParamValue())).collect(Collectors.toList());
                    //Activate Customer service
                    serviceData = serviceData + activateNMSService(params, username, password, nmsServiceActivationData,baseIp,port);
                 //   logger.info(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Service AcSccess: "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
                    return  serviceData;
                } else {
                    logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "No data Found: "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +LogConstants.LOG_ERROR +serviceData.toString()+LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                    return "Getting null Data!";
                }
            } else {
                logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Configuration not available: "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +LogConstants.LOG_ERROR +serviceData.toString()+LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                return "Base API Config not available!";
            }


        } catch (Exception ex) {
            logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Error while activate NMS service: "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +LogConstants.LOG_ERROR +ex.getMessage()+LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
            throw new RuntimeException("Error while activate NMS service: "+ex.getMessage());
        } finally {
            MDC.remove("userName");
            MDC.remove("type");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }

    public String activateNMSService(List<ProductParameterDefaultValueMappingDTO> params, String username, String password, NMSServiceActivationDTO nmsServiceActivationDTO,String baseIP, String port) {
        String serviceData=null;
        String stage = NMSParamconstant.NMSCUSTDETAILCONST.INITIAL_STAGE;
        String userNameForAudit = getProfileUserName(params,nmsServiceActivationDTO);
        NMSCustDetails nmsCustDetails = null;
        try {
            if(params != null ) {
                String token=getJwtTokenfromUrl(username,password,baseIP,port,nmsServiceActivationDTO.getUsername(), nmsServiceActivationDTO.getMvnoId(),userNameForAudit);
                if(!token.isEmpty()) {
                    nmsCustDetails = nmsCustDetailsRepository.findByCustServMapId(Long.valueOf(nmsServiceActivationDTO.getCustServiceMapId()));
                    if(nmsCustDetails == null) {
                        nmsCustDetails = new NMSCustDetails();
                        nmsCustDetails.setStage(stage);
                    }
                    String serialNum = getValueFromName(params, NMSParamconstant.SERIAL_NUMBER);
                    params=  setUpstreamandDownstreamuuid(params,nmsServiceActivationDTO);
                    String gponPort = getValueFromName(params, NMSParamconstant.PON_PORT);
                    if(gponPort.contains("GPON")) {
                        gponPort = gponPort.replaceAll("GPON","");
                    }
                    if(nmsCustDetails.getStage().equalsIgnoreCase(NMSParamconstant.NMSCUSTDETAILCONST.INITIAL_STAGE)) {
                        try {
                            CloseableHttpResponse ontResponse = addOnt(token, params, serialNum,baseIP,port,nmsServiceActivationDTO.getUsername(),2,gponPort,nmsServiceActivationDTO);
                            if(ontResponse != null) {
                                int ontResponseCode = ontResponse.getStatusLine().getStatusCode();

                                if(ontResponseCode == 200) {
                                    stage = NMSParamconstant.NMSCUSTDETAILCONST.ONT_ADDED_STAGE;
                                    nmsCustDetails.setStage(NMSParamconstant.NMSCUSTDETAILCONST.ONT_ADDED_STAGE);
                                    nmsCustDetails.setUsernameForAudit(userNameForAudit);
                                    stage = NMSParamconstant.NMSCUSTDETAILCONST.ONT_ADDED_STAGE;
                                    serviceData="success..!!";
                                    logger.info(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Error while Creating Service: "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
                                }else{
                                    serviceData="error..!!";
                                }
                            } else {
                                logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Error while Creating Service: "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +LogConstants.LOG_ERROR +ontResponse.toString()+LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                                return "ONT NOT Added.., !"+ontResponse.toString();
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Error while Creating Service: "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +LogConstants.LOG_ERROR +ex.getMessage()+LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                        }
                    }
                    if(nmsCustDetails.getStage().equalsIgnoreCase(NMSParamconstant.NMSCUSTDETAILCONST.ONT_ADDED_STAGE)) {
                        stage = NMSParamconstant.NMSCUSTDETAILCONST.ONT_ADDED_STAGE;
                        String profileName = getValueFromName(params, NMSParamconstant.PROFILENAME);
//                        UpstreamProfileDetails upstreamProfileDetails = getUpstreamBWProfile(token, profileName, params);
//                        List<DownstreamBandwidthProfile> downstreamBandwidthProfiles = getDownStreamBWProfile(token, "DS_PROFILE_100M", params); //TODO: remove constant
                        try {

                            CloseableHttpResponse serviceResponse = CerateAndActivateService(token, nmsServiceActivationDTO.getUpstreamprofileuuid(), nmsServiceActivationDTO.getDownstreamprofileuuid(), params, serialNum, profileName,nmsServiceActivationDTO.getCustServiceMapId(),baseIP,port,gponPort,username,null);
                            if(serviceResponse != null) {
                                LocalDateTime requestCompletionTime = LocalDateTime.now();
                                int ontResponseCode = serviceResponse.getStatusLine().getStatusCode();
                                HttpEntity responseEntity = serviceResponse.getEntity();
                                String errorMessage = "";

                                if(ontResponseCode == 200) {
                                    stage = NMSParamconstant.NMSCUSTDETAILCONST.SERVICE_ACTIVATED_STAGE;
                                    nmsCustDetails.setStage(NMSParamconstant.NMSCUSTDETAILCONST.SERVICE_ACTIVATED_STAGE);
                                    nmsCustDetails.setUsernameForAudit(userNameForAudit);
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
                                logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Error while Creating Service: "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +LogConstants.LOG_ERROR + serviceResponse.toString()+LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                                return String.format("Service Not Activated Response :%s", serviceResponse.toString());
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Error while Creating Service: "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +LogConstants.LOG_ERROR +ex.getMessage()+LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                        }
                    }

                } else {
                    logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Token is null"+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_ERROR +LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                }
                return  serviceData;
            } else {
                logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "No data Found"+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_ERROR +LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                return "Getting null Data!";
            }

        } catch (Exception ex) {
            logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Error while activate NMS service"+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_ERROR +ex.getMessage()+LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
            throw new RuntimeException("Error while activate NMS service: "+ex.getMessage());
        }finally {
            updateNMSCustDetails(Long.valueOf(nmsServiceActivationDTO.getCustId()), Long.valueOf(nmsServiceActivationDTO.getCustServiceMapId()), stage, nmsCustDetails,userNameForAudit);
        }
    }

    private List<ProductParameterDefaultValueMappingDTO> setUpstreamandDownstreamuuid(List<ProductParameterDefaultValueMappingDTO> params, NMSServiceActivationDTO nmsServiceActivationDTO) {
        params.forEach(param -> {
            String paramName = param.getParamName();
            if (paramName.contains(NMSParamconstant.UPSTREAM_PROFILE)) {
                param.setDefaultValue(nmsServiceActivationDTO.getUpstreamprofileuuid());
            } else if (paramName.contains(NMSParamconstant.DOWNSTREAM_PROFILE)) {
                param.setDefaultValue(nmsServiceActivationDTO.getDownstreamprofileuuid());
            }
        });
        return params;
    }

    public void updateNMSCustDetails(Long custId, Long custSerMapId, String stage, NMSCustDetails nmsCustDetails, String usernameForAudit) {
        try {
            LocalDateTime currentTime = LocalDateTime.now();
            nmsCustDetails.setCustId(custId);
            nmsCustDetails.setCustServMapId(custSerMapId);
            nmsCustDetails.setStage(stage);
            if(stage.equalsIgnoreCase(NMSParamconstant.NMSCUSTDETAILCONST.SERVICE_ACTIVATED_STAGE)) {
                nmsCustDetails.setIsCompleted(Boolean.TRUE);
                nmsCustDetails.setStatus(NMSParamconstant.NMSCUSTDETAILCONST.ACTIVATED_STATUS);
            } else {
                nmsCustDetails.setIsCompleted(Boolean.FALSE);
                nmsCustDetails.setStatus(NMSParamconstant.NMSCUSTDETAILCONST.PENDING_STATUS);
            }
            nmsCustDetails.setModifyDate(currentTime.toString());
            nmsCustDetails.setUsernameForAudit(usernameForAudit);
            nmsCustDetailsRepository.save(nmsCustDetails);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Token is null!!");
//            System.out.println("Exception at updateNMSCustDetails: "+ex.getMessage());
        }

    }

    public String getValueFromName(List<ProductParameterDefaultValueMappingDTO> params, String key) {
        Optional<ProductParameterDefaultValueMappingDTO> downStreamData = params.stream().filter(p ->
                p.getParamName().equalsIgnoreCase(key)).findFirst();
        if(downStreamData.isPresent()) {
            return downStreamData.get().getDefaultValue();
        }
        return null;
    }


    public String getUUIDFromProfileName(String profileName, String baseIp, String port, String token) {

        try {
            CloseableHttpClient httpClient = createHttpClient();
            // Construct URI with parameters using URIBuilder
            URIBuilder uriBuilder = new URIBuilder("https://"+baseIp+":"+port+"/nmsnbi-rest/tapi/data/context/connectivity-context/connectivity-services/filter");
            uriBuilder.setParameter("configState", "true")
                    .setParameter("csType", "Ethernet")
                    .setParameter("continue", "0")
                    .setParameter("userLabel", profileName)
                    .setParameter("size", "10")
                    .setParameter("fields", "uuid,endpoints");

            URI uri = uriBuilder.build();

            // Create GET request with the constructed URI
            HttpGet httpGet = new HttpGet(uri);

            httpGet.setHeader("Content-Type", "application/json");
            httpGet.setHeader("Authorization", "Bearer " + token);

            // Execute the request and get the response
            try {
                CloseableHttpResponse response = httpClient.execute(httpGet);
                // Handle the response as needed
                HttpEntity entity = response.getEntity();
                String responseBody = EntityUtils.toString(entity);
                JSONObject jsonObject = new JSONArray(responseBody).getJSONObject(0).getJSONObject("connectivity-service");
                String uuid = jsonObject.getString("uuid");
//                System.out.println("Response Code: " + response.getStatusLine().getStatusCode());
                // Process the response entity, if required
                return  uuid;
            }catch (Exception e){
                logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Unable to fetch uuid from profile"+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_ERROR +LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                e.printStackTrace();
            }
        } catch (Exception e) {
            logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Unable to fetch uuid from profile"+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_ERROR +LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
            e.printStackTrace();
        }
        return null;
    }


    public void sendUuidToCMS(Integer customerServiceMappingId, String uuid){
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("userName","RabbitMq");
        MDC.put("traceId",traceContext.traceIdString());
        MDC.put("spanId",traceContext.spanIdString());
        try {
            UuidDataDTO uuidDataDTO = new UuidDataDTO(customerServiceMappingId,uuid,null,null,null,null,null);
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


    public void deleteNMSService(UuidDataDTO uuidDataDTO) throws URISyntaxException {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("userName",uuidDataDTO.getUserName());
        MDC.put("traceId",traceContext.traceIdString());
        MDC.put("spanId",traceContext.spanIdString());
        NMSCustDetails nmsCustDetails  = nmsCustDetailsRepository.findByCustServMapId(uuidDataDTO.getCustomerServiceMappingId().longValue());
        String usernameForAudit = "";
        if(nmsCustDetails!=null){
            usernameForAudit = nmsCustDetails.getUsernameForAudit();
        }
        Connfiguration configService = configRepocitory.findByName(uuidDataDTO.getConfigName());
        if (configService != null) {
            String username = configService.getUsername();
            String password = configService.getPassword();
            String baseIp = configService.getBaseurl();
            String port = configService.getPort();

            CloseableHttpResponse response = null;
            HttpDelete httpDelete = new HttpDelete();
            LocalDateTime requestInitiationTime = LocalDateTime.now();
            String reqUrl ="";

            String token=getJwtTokenfromUrl(username,password,baseIp,port,null, null,null);

            URIBuilder uriBuilder  = new URIBuilder("https://" + baseIp + ":" + port + "/nmsnbi-rest/tapi/data/context/connectivity-context/connectivity-services");
            uriBuilder.setParameter("uuids", uuidDataDTO.getUuid());
            uriBuilder.setParameter("command", "DeactivateAndDelete");
            URI uri = uriBuilder.build();
            httpDelete = new HttpDelete(uri);

            try {

                httpDelete.setHeader("Content-Type", "application/json");
                httpDelete.setHeader("Authorization", "Bearer " + token);
                // Execute the request and get the response
                try  {
                    CloseableHttpClient httpClient = createHttpClient();
                    //execute request
                    response = httpClient.execute(httpDelete);
                    // Close the HttpClient
                    LocalDateTime requestCompletionTime = LocalDateTime.now();
                    Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime,requestCompletionTime);
                    if(response.getStatusLine().getStatusCode()>400){
                        String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
                        JSONObject responseObject = new JSONObject(responseBody.toString());
                        String errorMessage = "";
                        if(responseObject.has("errors")) {
                            JSONArray errorsArray = responseObject.getJSONArray("errors");
                            JSONObject errorObject = errorsArray.getJSONObject(0);
                            // Extract the message
                            errorMessage = errorObject.getString("message");
                        }
                        logger.info(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Nms service deleted Successfully "+ uuidDataDTO.getUuid()+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_ERROR+errorMessage +LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                        apiAuditsService.extractDataAndSaveDeleteApiAudits(uri.toString(),null,response,httpDelete,responseTime,response.getStatusLine().toString(),requestInitiationTime,username,null,usernameForAudit);

                    }else if(response.getStatusLine().getStatusCode() == 200){
                        apiAuditsService.extractDataAndSaveDeleteApiAudits(uri.toString(),null,response,httpDelete,responseTime,null,requestInitiationTime,username,null,usernameForAudit);
                       //call delete ont request
                       deleteNMSServiceONT(uuidDataDTO,token,usernameForAudit);
                        logger.info(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Nms service deleted Successfully "+ uuidDataDTO.getUuid()+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

                    }

//                    System.out.println("Response Code: " + response.getStatusLine().getStatusCode());
       //             System.out.println("Service Deleted Successfully with uuid : " + uuidDataDTO.getUuid());
                }catch (Exception e){
//                    System.out.println(e.getMessage());
                    logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Failed to delete nms service with uuid"+ uuidDataDTO.getUuid()+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_ERROR +e.getMessage()+LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                    apiAuditsService.extractDataAndSaveDeleteApiAudits(uri.toString(),null,response,httpDelete,null,response.getStatusLine().toString(),requestInitiationTime,username,null,usernameForAudit);

                }
            } catch (Exception e) {
                logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Failed to delete nms service with uuid"+ uuidDataDTO.getUuid()+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_ERROR +e.getMessage()+LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
//                System.out.println("Failed to delete nms service with uuid : " + uuidDataDTO.getUuid() + " Error : " + e.getMessage());
                //Log.info("Failed to delete nms service with uuid : " + uuidDataDTO.getUuid() + " Error : " + e.getMessage());
                apiAuditsService.extractDataAndSaveDeleteApiAudits(uri.toString(),null,response,httpDelete,null,response.getStatusLine().toString(),requestInitiationTime,username,null,usernameForAudit);
            }


        }
        MDC.remove("traceId");
        MDC.remove("spanId");
    }

//    public void deleteNMSService(UuidDataDTO uuidDataDTO) throws URISyntaxException {
//        try {
//            TraceContext traceContext = tracer.currentSpan().context();
//            MDC.put("userName", "RabbitMq");
//            MDC.put("traceId", traceContext.traceIdString());
//            MDC.put("spanId", traceContext.spanIdString());
//            CloseableHttpResponse response = null;
//            CloseableHttpClient httpClient = null;
//            HttpDelete httpDelete = new HttpDelete();
//            URI uri = null;
//
//            Connfiguration configService = configRepocitory.findByName(uuidDataDTO.getConfigName());
//            if (configService != null) {
//                String username = configService.getUsername();
//                String password = configService.getPassword();
//                String baseIp = configService.getBaseurl();
//                String port = configService.getPort();
//                URIBuilder uriBuilder = new URIBuilder("https://" + baseIp + ":" + port + "/nmsnbi-rest/tapi/data/context/connectivity-context/connectivity-services");
//                uriBuilder.setParameter("uuids", uuidDataDTO.getUuid())
//                        .setParameter("command", "DeactivateAndDelete");
//                uri = uriBuilder.build();
//                LocalDateTime requestInitiationTime = LocalDateTime.now();
//                try {
//                    httpClient = HttpClients.createDefault();
//                    httpDelete = new HttpDelete(uri);
//                    try {
//                        response = executeRequest(httpClient, httpDelete);
//                        LocalDateTime requestCompletionTime = LocalDateTime.now();
//                        Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
//
//                        if (response.getStatusLine().getStatusCode() > 400) {
//                            apiAuditsService.extractDataAndSaveDeleteApiAudits(uri.toString(), null, response, httpDelete, responseTime, response.getStatusLine().toString(), requestInitiationTime, null, null);
//                        } else if (response.getStatusLine().getStatusCode() == 200) {
//                            apiAuditsService.extractDataAndSaveDeleteApiAudits(uri.toString(), null, response, httpDelete, responseTime, null, requestInitiationTime, null, null);
//                            // call delete ont request
//                            deleteNMSServiceONT(uuidDataDTO);
//                        }
//
//                        System.out.println("Service Deleted Successfully with uuid : " + uuidDataDTO.getUuid());
//                    } catch (Exception e) {
//                        logger.info("Failed to delete nms service with uuid" + uuidDataDTO.getUuid() + e.getMessage());
//                        apiAuditsService.extractDataAndSaveDeleteApiAudits(uri.toString(), null, response, httpDelete, null, response.getStatusLine().toString(), requestInitiationTime, null, null);
//                    }
//                }catch (Exception e){
//                    logger.info("Failed to delete nms service with uuid" + uuidDataDTO.getUuid() + e.getMessage());
//                    apiAuditsService.extractDataAndSaveDeleteApiAudits(uri.toString(), null, response, httpDelete, null, response.getStatusLine().toString(), requestInitiationTime, null, null);
//
//                }
//            }
//        } catch (Exception e) {
//            logger.info("Failed to delete nms service with uuid" + uuidDataDTO.getUuid() + e.getMessage());
//        } finally {
//            MDC.remove("traceId");
//            MDC.remove("spanId");
//        }
//    }
//
//    private CloseableHttpResponse executeRequest(CloseableHttpClient httpClient, HttpUriRequest request) throws Exception {
//        return httpClient.execute(request);
//    }


    public GenericDataDTO getUpstreamBandwidthProfileType(String profileType) {
        GenericDataDTO genericDataDTO=new GenericDataDTO();
        try{
            Connfiguration configuration=configRepocitory.findByName(Constants.SAVBILL_NMS);
        String token=getJwtTokenfrom(configuration);
        if(!token.isEmpty()){
            List<UpstreamProfileDetails> upstreamProfileDetails=getUpstreamBWProfileDetails(token,profileType,configuration);
            if(Objects.nonNull(upstreamProfileDetails)){
                genericDataDTO.setDataList(upstreamProfileDetails);
                genericDataDTO.setResponseMessage("Upstream Bandwidth profile Data fetched Successfully");
                genericDataDTO.setResponseCode(APIConstants.SUCCESS);
                logger.info(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Upstream Bandwidth profile Data fetched Successfully "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
            }else{
                genericDataDTO.setData(upstreamProfileDetails);
                genericDataDTO.setResponseMessage("Unable to fetch Upstream Bandwidth profile data");
                genericDataDTO.setResponseCode(APIConstants.FAIL);
                logger.info(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "No data Found"+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
            }
        }else{
            genericDataDTO.setResponseMessage("Invalid Token /Expired Token");
            genericDataDTO.setResponseCode(APIConstants.FAIL);
            logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Invalid JWT Token "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +LogConstants.LOG_ERROR +"Invalid Token"+LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
        }
        }catch(Exception e){
            e.printStackTrace();
            genericDataDTO.setResponseMessage(e.getMessage());
            logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Invalid JWT Token "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +LogConstants.LOG_ERROR +e.getMessage()+LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
        }
        return genericDataDTO;
    }

    public GenericDataDTO getUpstreamBandwidthProfileName(String profileName, String loggedInUser, Integer loggedInUserId) {
        GenericDataDTO genericDataDTO=new GenericDataDTO();
        try{
            Connfiguration configuration=configRepocitory.findByName(Constants.SAVBILL_NMS);
            String token=getJwtTokenfrom(configuration);
            if(!token.isEmpty()){
                List<UpstreamProfileDetails> upstreamProfileDetails=getUpstreamBWProfile(token,profileName,null, loggedInUser,loggedInUserId);
                if(Objects.nonNull(upstreamProfileDetails)){
                    genericDataDTO.setDataList(upstreamProfileDetails);
                    genericDataDTO.setResponseMessage("Upstream Bandwidth profile Data fetched Successfully");
                    genericDataDTO.setResponseCode(APIConstants.SUCCESS);
                    logger.info(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Upstream Bandwidth profile Data fetched Successfully "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
                }else{
                    genericDataDTO.setData(upstreamProfileDetails);
                    genericDataDTO.setResponseMessage("Unable to fetch Upstream Bandwidth profile data");
                    genericDataDTO.setResponseCode(APIConstants.FAIL);
                    logger.info(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "No data Found"+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
                }
            }else{
                genericDataDTO.setResponseMessage("Invalid Token /Expired Token");
                genericDataDTO.setResponseCode(APIConstants.FAIL);
                logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Invalid JWT Token "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
            }
        }catch(Exception e){
            e.printStackTrace();
            genericDataDTO.setResponseMessage(e.getMessage());
            logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Invalid JWT Token "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
        }
        return genericDataDTO;
    }

    public GenericDataDTO getDownstreamBandwidthProfileName(String profileName, String loggedInUser, Integer loggedInUserId) {
        GenericDataDTO genericDataDTO=new GenericDataDTO();
        try{
            Connfiguration configuration=configRepocitory.findByName(Constants.SAVBILL_NMS);
            String token=getJwtTokenfrom(configuration);
            if(!token.isEmpty()){
                List<DownstreamBandwidthProfile> downstreamBandwidthProfileList=getDownStreamBWProfile(token,profileName,null, loggedInUser,loggedInUserId);
                if(Objects.nonNull(downstreamBandwidthProfileList)){
                    genericDataDTO.setDataList(downstreamBandwidthProfileList);
                    genericDataDTO.setResponseMessage("Upstream Bandwidth profile Data fetched Successfully");
                    genericDataDTO.setResponseCode(APIConstants.SUCCESS);
                    logger.info(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Upstream Bandwidth profile Data fetched Successfully "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
                }else{
                    genericDataDTO.setData(downstreamBandwidthProfileList);
                    genericDataDTO.setResponseMessage("Unable to fetch Upstream Bandwidth profile data");
                    genericDataDTO.setResponseCode(APIConstants.FAIL);
                    logger.info(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "No data Found"+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
                }
            }else{
                genericDataDTO.setResponseMessage("Invalid Token /Expired Token");
                genericDataDTO.setResponseCode(APIConstants.FAIL);
                logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Invalid JWT Token "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
            }
        }catch(Exception e){
            e.printStackTrace();
            genericDataDTO.setResponseMessage(e.getMessage());
            logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Invalid JWT Token "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
        }
        return genericDataDTO;
    }


    public String getJwtTokenfrom(Connfiguration configuration) {
        String strUR = "https://"+configuration.getBaseurl()+":"+configuration.getPort()+"/nmsnbi-rest/tapi/data/context/auth-context/auth-token";
        try {
            CloseableHttpClient httpClient = createHttpClient();
            HttpPost httpPost = createHttpPost(strUR);
            httpPost.setHeader("User", configuration.getUsername());
            httpPost.setHeader("Password", configuration.getPassword());
            CloseableHttpResponse response = httpClient.execute(httpPost);

            try {
                HttpEntity entity = response.getEntity();
                String token = extractJwtToken(EntityUtils.toString(entity));
//                logger.info("NMS succefully get Token for username: "+configuration.getUsername());
                return token;
            } finally {
                response.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Unable to fetch Upstream profile "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +LogConstants.LOG_ERROR +e.getMessage()+LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
            return "Error occurred: " + e.getMessage();
        }
    }
    private List<UpstreamProfileDetails> getUpstreamBWProfileDetails(String token, String profileName, Connfiguration connfiguration) throws Exception {
        List<UpstreamProfileDetails> details=new ArrayList<>();
        String strUR = "https://" + connfiguration.getBaseurl() + ":" + connfiguration.getPort() +
                "/nmsnbi-rest/tapi/data/context/connectivity-context/upstream-bw-profile?continue=0&size=500&profileType="+profileName;
        CloseableHttpResponse response =null;
        HttpGet httpGet = new HttpGet();
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        try {
            httpGet = createHttpGet(strUR);
            CloseableHttpClient httpClient = createHttpClient();
            httpGet.setHeader("Content-Type", "application/json");
            httpGet.setHeader("Authorization", "Bearer " + token);
//            logger.info("NMS Request for upstream BW strurl "+strUR);

            try {
                response = httpClient.execute(httpGet);
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime,requestCompletionTime);
                int statusCode = response.getStatusLine().getStatusCode();
//                logger.info("NMS Response for upstream BW strurl "+strUR+" response: "+response.toString());

                String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
                if (statusCode == 200) {
                    logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Unable to fetch Upstream profile "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
                    ObjectMapper objectMapper = new ObjectMapper();
                    List<UpstreamBandwidthProfile> profiles = objectMapper.readValue(
                            responseBody,
                            new TypeReference<List<UpstreamBandwidthProfile>>() {
                            }
                    );
                    for (UpstreamBandwidthProfile profile : profiles) {
                        details .add( profile.getProfileDetails());
//                        System.out.println("Profile Name: ");
                    }
                    apiAuditsService.extractDataAndSaveGetApiAudits(strUR,null,response,httpGet,responseTime,responseBody.toString(),requestInitiationTime,responseBody,null,null,null);
                }
                if (statusCode >= 400) {
                    logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Unable to fetch Upstream profile "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +LogConstants.LOG_ERROR +responseBody.toString()+LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                    apiAuditsService.extractDataAndSaveGetApiAudits(strUR,null,response,httpGet,responseTime,responseBody.toString(),requestInitiationTime,responseBody,null,null,null);
                }
            } catch (Exception e) {
                logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Unable to fetch Upstream profile "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +LogConstants.LOG_ERROR +e.getMessage()+LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                e.printStackTrace();
                apiAuditsService.extractDataAndSaveGetApiAudits(strUR,null,response,httpGet,null,e.getMessage(),requestInitiationTime,null,null,null,null);
            }

        } catch (Exception e) {
            logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Unable to fetch Upstream profile "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +LogConstants.LOG_ERROR +e.getMessage()+LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
            e.printStackTrace();
            apiAuditsService.extractDataAndSaveGetApiAudits(strUR,null,response,httpGet,null,e.getMessage(),requestInitiationTime,null,null,null,null);
        }
        return details;
    }


    public GenericDataDTO getDownStreamBandwidthProfile(String profiletype) {
    GenericDataDTO genericDataDTO=new GenericDataDTO();
    try {
        Connfiguration connfiguration=configRepocitory.findByName(Constants.SAVBILL_NMS);
        String token=getJwtTokenfrom(connfiguration);
        List<DownstreamBandwidthProfile>downstreamBandwidthProfiles=getDownStreamProfileData(token,profiletype,connfiguration);
        if(downstreamBandwidthProfiles.size()>0){
            genericDataDTO.setDataList(downstreamBandwidthProfiles);
            genericDataDTO.setResponseCode(APIConstants.SUCCESS);
            logger.info(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Unable to fetch Downstream profile: "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
        }else{
            genericDataDTO.setResponseMessage("No Data Found");
            genericDataDTO.setResponseCode(APIConstants.NOT_FOUND);
            logger.info(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Unable to fetch Downstream profile: "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
        }
    }catch (Exception e){
        e.printStackTrace();
        genericDataDTO.setResponseMessage(e.getMessage());
        genericDataDTO.setResponseCode(APIConstants.FAIL);
        logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Unable to fetch Downstream profile: "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +LogConstants.LOG_ERROR +e.getMessage()+LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
    }
    return  genericDataDTO;
    }

    private List<DownstreamBandwidthProfile> getDownStreamProfileData(String token, String profileType, Connfiguration connfiguration) throws Exception {
        List<DownstreamBandwidthProfile> profiles=new ArrayList<>();
        HttpGet httpGet = new HttpGet();
        CloseableHttpResponse response = null;
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        String strUR = "https://" + connfiguration.getBaseurl() + ":" + connfiguration.getPort() +
                "/nmsnbi-rest/tapi/data/context/connectivity-context/downstream-bandwidth-profile?continue=0&size=100&profileType="+profileType;
        try {

            httpGet = createHttpGet(strUR);
            CloseableHttpClient httpClient = createHttpClient();

            // Set headers
            httpGet.setHeader("Content-Type", "application/json");
            httpGet.setHeader("Authorization", "Bearer " + token);
//            logger.info("NMS Request for downstream BW strurl "+strUR);
            try {
                response = httpClient.execute(httpGet);

                LocalDateTime requestCompletionTime = LocalDateTime.now();

                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime,requestCompletionTime);

                // Check the response status
                int statusCode = response.getStatusLine().getStatusCode();
//                System.out.println("Response Code: " + statusCode);

                // Read and print the response body
                String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
//                System.out.println("Response Body: " + responseBody);
//                logger.info("NMS Response for downstream BW strurl "+strUR+" response: "+response.toString());
                if (statusCode == 200) {
                    logger.info(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Downstream Profile fetched successfully: "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_SUCCESS + APIConstants.SUCCESS);
                    ObjectMapper objectMapper = new ObjectMapper();
                    profiles = objectMapper.readValue(
                            responseBody,
                            new TypeReference<List<DownstreamBandwidthProfile>>() {
                            }
                    );
                    apiAuditsService.extractDataAndSaveGetApiAudits(strUR,null,response,httpGet,responseTime,responseBody.toString(),requestInitiationTime,responseBody,null,null,null);
                }

                if (statusCode >= 400) {
                    logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Unable to fetch Downstream profile: "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +LogConstants.LOG_ERROR +responseBody.toString()+LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                    apiAuditsService.extractDataAndSaveGetApiAudits(strUR,null,response,httpGet,responseTime,responseBody.toString(),requestInitiationTime,responseBody,null,null,null);
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Unable to fetch Downstream profile: "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +LogConstants.LOG_ERROR +e.getMessage()+LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                apiAuditsService.extractDataAndSaveGetApiAudits(strUR,null,response,httpGet,null,e.getMessage(),requestInitiationTime,null,null,null,null);
            }

        } catch (Exception e) {
            logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Unable to fetch Downstream profile: "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +LogConstants.LOG_ERROR +e.getMessage()+LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
            e.printStackTrace();
        }
        return profiles;
    }



    public List<UpstreamProfileDetails> getAllUpstreamBWProfileDetails(String token, String profileName, String baseIp, String port) throws Exception {
        List<UpstreamProfileDetails> details=new ArrayList<>();
        String strUR = "https://" + baseIp + ":" + port +
                "/nmsnbi-rest/tapi/data/context/connectivity-context/upstream-bw-profile?continue=0&size=500&profileType="+profileName;
        CloseableHttpResponse response =null;
        HttpGet httpGet = new HttpGet();
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        try {
            httpGet = createHttpGet(strUR);
            CloseableHttpClient httpClient = createHttpClient();
            httpGet.setHeader("Content-Type", "application/json");
            httpGet.setHeader("Authorization", "Bearer " + token);
//            logger.info("NMS Request for upstream BW strurl "+strUR);

            try {
                response = httpClient.execute(httpGet);
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime,requestCompletionTime);
                int statusCode = response.getStatusLine().getStatusCode();
//                logger.info("NMS Response for upstream BW strurl "+strUR+" response: "+response.toString());

                String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
                if (statusCode == 200) {
                    logger.info(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "fetching Upstream profile: "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
                    ObjectMapper objectMapper = new ObjectMapper();
                    List<UpstreamBandwidthProfile> profiles = objectMapper.readValue(
                            responseBody,
                            new TypeReference<List<UpstreamBandwidthProfile>>() {
                            }
                    );
                    for (UpstreamBandwidthProfile profile : profiles) {
                        details .add( profile.getProfileDetails());
//                        System.out.println("Profile Name: ");
                    }
                    apiAuditsService.extractDataAndSaveGetApiAudits(strUR,null,response,httpGet,responseTime,null,requestInitiationTime,responseBody,null,null,null);
                }
                if (statusCode >= 400) {
                    logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Unable to fetch Upstream profile: "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +LogConstants.LOG_ERROR +responseBody.toString()+LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                    apiAuditsService.extractDataAndSaveGetApiAudits(strUR,null,response,httpGet,responseTime,responseBody.toString(),requestInitiationTime,responseBody,null,null,null);
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Unable to fetch Upstream profile: "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +LogConstants.LOG_ERROR +e.getMessage()+LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                apiAuditsService.extractDataAndSaveGetApiAudits(strUR,null,response,httpGet,null,e.getMessage(),requestInitiationTime,null,null,null,null);
            }

        } catch (Exception e) {
            logger.error(LogConstants.REQUEST_FROM +"gui"+ LogConstants.REQUEST_FOR + "Unable to fetch Upstream profile: "+ LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +LogConstants.LOG_ERROR +e.getMessage()+LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
            e.printStackTrace();
            apiAuditsService.extractDataAndSaveGetApiAudits(strUR,null,response,httpGet,null,e.getMessage(),requestInitiationTime,null,null,null,null);
        }
        return details;
    }

    public void deleteNMSServiceONT(UuidDataDTO uuidDataDTO, String token, String usernameForAudit) throws URISyntaxException {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("userName","RabbitMq");
        MDC.put("traceId",traceContext.traceIdString());
        MDC.put("spanId",traceContext.spanIdString());

        Connfiguration configService = configRepocitory.findByName(uuidDataDTO.getConfigName());
        if (configService != null) {
            String username = configService.getUsername();
            String password = configService.getPassword();
            String baseIp = configService.getBaseurl();
            String port = configService.getPort();
            CloseableHttpClient httpClient = null;
            CloseableHttpResponse response = null;
            HttpDelete httpDelete = new HttpDelete();
            LocalDateTime requestInitiationTime = LocalDateTime.now();
            //https://27.34.251.54:446/nmsnbi-rest/tapi/data/context/physical-context/device/equipment=TJNW95A04F20?command=DeactivateAndDelete

            List<ProductParameterDefaultValueMappingDTO> params = uuidDataDTO.getCustInvParamsDtoList().stream().map(custInvParamsDto ->
                    new ProductParameterDefaultValueMappingDTO(custInvParamsDto.getId(), custInvParamsDto.getCustSerMapId(), custInvParamsDto.getParamName(), custInvParamsDto.getParamValue())).collect(Collectors.toList());
            String serialNumber = getValueFromName(params, NMSParamconstant.SERIAL_NUMBER);
            URIBuilder uriBuilder = new URIBuilder("https://" + baseIp + ":" + port + "/nmsnbi-rest/tapi/data/context/physical-context/device/equipment="+serialNumber+"?"+"command=DeactivateAndDelete");
//            uriBuilder.setParameter("uuids", uuidDataDTO.getUuid())
//                    .setParameter("command", "DeactivateAndDelete");
            URI uri = uriBuilder.build();

            try {
                httpDelete = new HttpDelete(uri);
                // Execute the request and get the response
                try  {
                    httpDelete.setHeader("Content-Type", "application/json");
                    httpDelete.setHeader("Authorization", "Bearer " + token);
                    httpClient = createHttpClient();
                    response = httpClient.execute(httpDelete);
                    LocalDateTime requestCompletionTime = LocalDateTime.now();
                    Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime,requestCompletionTime);

                    if(response.getStatusLine().getStatusCode()>400){
                        apiAuditsService.extractDataAndSaveDeleteApiAudits(uri.toString(),null,response,httpDelete,responseTime,response.getStatusLine().toString(),requestInitiationTime,null,null,usernameForAudit);

                    }else if(response.getStatusLine().getStatusCode() == 200){
                        apiAuditsService.extractDataAndSaveDeleteApiAudits(uri.toString(),null,response,httpDelete,responseTime,null,requestInitiationTime,null,null,usernameForAudit);
                        //call delete ont request

                    }

//                    System.out.println("Response Code: " + response.getStatusLine().getStatusCode());
                    System.out.println("ONT Deleted Successfully with uuid : " + uuidDataDTO.getUuid());
                }catch (Exception e){
//                    System.out.println(e.getMessage());
                    logger.info("Failed to delete nms ONT with uuid"+ uuidDataDTO.getUuid()+e.getMessage());
                    apiAuditsService.extractDataAndSaveDeleteApiAudits(uri.toString(),null,response,httpDelete,null,response.getStatusLine().toString(),requestInitiationTime,null,null,usernameForAudit);

                }
            } catch (Exception e) {
                logger.info("Failed to delete nms ONT with uuid"+ uuidDataDTO.getUuid()+e.getMessage());
//                System.out.println("Failed to delete nms service with uuid : " + uuidDataDTO.getUuid() + " Error : " + e.getMessage());
                //Log.info("Failed to delete nms service with uuid : " + uuidDataDTO.getUuid() + " Error : " + e.getMessage());
            }


        }
        MDC.remove("traceId");
        MDC.remove("spanId");
    }

    private static HttpDelete createHttpDelete(String url) {
        return new HttpDelete(url);
    }


    public String getProfileUserName(List<ProductParameterDefaultValueMappingDTO> params , NMSServiceActivationDTO nmsServiceActivationDTO){
        String username = getValueFromName(params, NMSParamconstant.CONNECTION_NAME);
        String profileName = getValueFromName(params, NMSParamconstant.PROFILENAME);
        Integer custServMappingId = nmsServiceActivationDTO.getCustServiceMapId();
        return username +"-"+custServMappingId+ "-" + profileName ;
    }






}


