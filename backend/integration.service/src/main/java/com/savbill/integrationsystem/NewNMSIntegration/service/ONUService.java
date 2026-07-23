package com.savbill.integrationsystem.NewNMSIntegration.service;

import com.savbill.integrationsystem.NewNMSIntegration.configuration.ApiConfig;
import com.savbill.integrationsystem.NewNMSIntegration.constants.NMSIntegrationConstant;
import com.savbill.integrationsystem.NewNMSIntegration.dto.*;
import com.savbill.integrationsystem.NewNMSIntegration.dto.*;
import com.savbill.integrationsystem.NewNMSIntegration.repository.IntegrationParametersRepository;
import com.savbill.integrationsystem.NewNMSIntegration.repository.NnmIntegrationRepository;
import com.savbill.integrationsystem.apiAudits.NMSServices.ApiAuditsService;
import com.savbill.integrationsystem.exceptions.BadRequestException;
import com.savbill.integrationsystem.nms.entity.Connfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.methods.HttpPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The type Onu service.
 */
@Service
@RequiredArgsConstructor
public class ONUService {

    private static final Logger log = LoggerFactory.getLogger(ONUService.class);
    /**
     * The Api config.
     */
    @Autowired
    private ApiConfig apiConfig;

    /**
     * The Required params config.
     */
    private Map<String, Set<String>> requiredParamsConfig;

    /**
     * The Repository.
     */
    @Autowired
    private NnmIntegrationRepository repository;

    /**
     * The Integration repo.
     */
    @Autowired
    private IntegrationParametersRepository integrationRepo;

    /**
     * The Api audits service.
     */
    @Autowired
    ApiAuditsService apiAuditsService;

    /**
     * Add onu onu response dto.
     * @param request the request
     * @param accessToken the access token
     * @param configService the config service
     * @param loggedInUserName the logged in user name
     * @param mvnoId the mvno id
     * @param list
     * @return the onu response dto
     */
    public ONUResponseDTO addONU(DynamicRequestDTO request, String accessToken, Connfiguration configService, String loggedInUserName, Long mvnoId, List<IntegrationSpecificParamDTO> list) throws JsonProcessingException {
//        validateRequest(request, NMSIntegrationConstant.API_CONSTANT.ADD_ONU);
        String basedUrl = configService.getBaseurl() + ":" + configService.getPort() + "/pon/onu";
        ONUResponseDTO response = processONURequest(basedUrl, request, HttpMethod.POST, accessToken, true, loggedInUserName, mvnoId, NMSIntegrationConstant.API_CONSTANT.POST, "ADD_ONU");
        return response;
    }

    private DynamicRequestDTO finalDTO(DynamicRequestDTO dynamicRequestDTO, DynamicRequestDTO staticDTO) {
        DynamicRequestDTO finalDTO = new DynamicRequestDTO();
        Map<String, String> mergedParameters = new HashMap<>(staticDTO.getParameters());
        mergedParameters.putAll(dynamicRequestDTO.getParameters());
        finalDTO.setParameters(mergedParameters);
        Map<String, Integer> mergedIntegerMap = new HashMap<>(staticDTO.getNumberParameters());
        mergedIntegerMap.putAll(dynamicRequestDTO.getNumberParameters());
        finalDTO.setNumberParameters(mergedIntegerMap);
        return finalDTO;
    }

    public DynamicRequestDTO setWANStaticDto() {
        DynamicRequestDTO staticDTO = new DynamicRequestDTO();
        staticDTO.setDynamicField("ONUIDTYPE", "MAC");
        staticDTO.setIntegerDynamicField("MODE", 1);
        staticDTO.setIntegerDynamicField("CONNTYPE", 1);
        staticDTO.setIntegerDynamicField("COS", null);
        staticDTO.setIntegerDynamicField("NAT", 1);
        staticDTO.setIntegerDynamicField("IPMODE", 2);
        staticDTO.setIntegerDynamicField("IPSTACKMODE", 0);
        staticDTO.setIntegerDynamicField("IP6SRCTYPE", 1);
        staticDTO.setIntegerDynamicField("IP6PREFIXSRCTYPE", 0);
        staticDTO.setDynamicField("WANIP", "");
        staticDTO.setDynamicField("WANMASK", "");
        staticDTO.setDynamicField("WANGATEWAY", "");
        staticDTO.setDynamicField("MASTERDNS", "");
        staticDTO.setDynamicField("SLAVEDNS", "");
        staticDTO.setDynamicField("IP6ADDRESS", "");
        staticDTO.setDynamicField("IP6GATEWAY", "");
        staticDTO.setDynamicField("IP6MASTERDNS", "");
        staticDTO.setDynamicField("IP6SLAVEDNS", "");
        staticDTO.setDynamicField("IP6STATICPREFIX ", "");
        staticDTO.setIntegerDynamicField("PPPOEPROXY", 0);
        staticDTO.setDynamicField("PPPOEUSER", "");
        staticDTO.setDynamicField("PPPOEPASSWD", "");
        staticDTO.setDynamicField("PPPOENAME", "");
        staticDTO.setIntegerDynamicField("PPPOEAUTHMODE", 3);
        staticDTO.setIntegerDynamicField("PPPOEMODE", 0);
        staticDTO.setIntegerDynamicField("PPPOEIDLETIME", 0);
        staticDTO.setIntegerDynamicField("QOS", 0);
        staticDTO.setIntegerDynamicField("UPORT", 15);
        staticDTO.setIntegerDynamicField("SSID", 255);
        staticDTO.setIntegerDynamicField("VLANMODE", 1);
        staticDTO.setIntegerDynamicField("TRANSSTATE", 0);
        staticDTO.setDynamicField("TRANSVALUE", "");
        staticDTO.setDynamicField("TRANSCOS ", "");
        staticDTO.setIntegerDynamicField("QINQSTATE", 0);
        staticDTO.setIntegerDynamicField("SVLAN", 200);
        staticDTO.setIntegerDynamicField("QINQCOS", 0);
        staticDTO.setDynamicField("DHCPREMOTEID", "");
        staticDTO.setIntegerDynamicField("TCONT", 0);
        staticDTO.setIntegerDynamicField("GEMPORT", 0);
        staticDTO.setIntegerDynamicField("UPNP", 0);
        return staticDTO;
    }

    public DynamicRequestDTO setQinQStaticDto() {
        DynamicRequestDTO staticDTO = new DynamicRequestDTO();
        staticDTO.setDynamicField("ONUIDTYPE", "MAC");
        return staticDTO;
    }

    public DynamicRequestDTO setWifiConfigStaticDto() {
        DynamicRequestDTO staticDTO = new DynamicRequestDTO();
        staticDTO.setDynamicField("ONUIDTYPE", "MAC");
        staticDTO.setIntegerDynamicField("ENABLE", 1);
        staticDTO.setIntegerDynamicField("WIRELESS-AREA", 4);
        staticDTO.setIntegerDynamicField("WIRELESS-CHANNEL", 0);
        staticDTO.setIntegerDynamicField("WIRELESS-STANDARD", 8);
//        staticDTO.setIntegerDynamicField("WORKING-FREQUENCY", 1);
        staticDTO.setIntegerDynamicField("T-POWER", 28);
        staticDTO.setIntegerDynamicField("SSID", 2);
        staticDTO.setIntegerDynamicField("SSID-ENABLE", 1);
        staticDTO.setIntegerDynamicField("SSID-VISIBLE", 0);
        staticDTO.setIntegerDynamicField("AUTH-MODE", 6);
        staticDTO.setIntegerDynamicField("ENCRYPT-TYPE", 4);
        staticDTO.setIntegerDynamicField("UPDATEKEY-INTERVAL", 86400);
        staticDTO.setDynamicField("RADIUS-SERVER", "0.0.0.0");
        staticDTO.setIntegerDynamicField("RADIUS-PORT", 0);
        staticDTO.setDynamicField("RADIUS-KEY", "");
        staticDTO.setIntegerDynamicField("WEP-ENCRYPTIONLEVEL", 1);
        staticDTO.setIntegerDynamicField("WEP-KEYINDEX", 1);
        staticDTO.setDynamicField("WEPKEY1", "12345");
        staticDTO.setDynamicField("WEPKEY2", "12345");
        staticDTO.setDynamicField("WEPKEY3", "12345");
        staticDTO.setDynamicField("WEPKEY4", "12345");
        staticDTO.setDynamicField("WAP-IPADDRESS", "");
        staticDTO.setIntegerDynamicField("WAP-PORT", 0);
        staticDTO.setIntegerDynamicField("MAX-WIFIMAC-COUNT", 32);
        staticDTO.setIntegerDynamicField("PUBLICSSID", 0);
        staticDTO.setIntegerDynamicField("KICKSTATIONSWITCH", 0);
        staticDTO.setIntegerDynamicField("LOWERTHRESHOLD", 0);
        return staticDTO;
    }

    public DynamicRequestDTO createConfigWANRequest(DynamicRequestDTO request, List<IntegrationSpecificParamDTO> list) {
        DynamicRequestDTO configWANRequest = new DynamicRequestDTO();
        configWANRequest.setCustomerId(request.getCustomerId());
        if (!list.isEmpty()) {
            for (IntegrationSpecificParamDTO integrationSpecificParamDTO : list) {
                if (integrationSpecificParamDTO.getParamName().equalsIgnoreCase("VLAN") ||
                        integrationSpecificParamDTO.getParamName().equalsIgnoreCase("TPID")) {
                    String paramName = integrationSpecificParamDTO.getParamName();
                    Integer paramValue = Integer.valueOf(integrationSpecificParamDTO.getParamValue());
                    configWANRequest.setIntegerDynamicField(paramName, paramValue);
                } else {
                    String paramName = integrationSpecificParamDTO.getParamName();
                    String paramValue = integrationSpecificParamDTO.getParamValue();
                    configWANRequest.setDynamicField(paramName, paramValue);
                }
            }
        }
        return configWANRequest;
    }

    public DynamicRequestDTO createConfigQinQRequest(DynamicRequestDTO request, List<IntegrationSpecificParamDTO> list) {
        DynamicRequestDTO configQinQRequest = new DynamicRequestDTO();
        configQinQRequest.setCustomerId(request.getCustomerId());

        if (list != null && !list.isEmpty()) {
            for (IntegrationSpecificParamDTO param : list) {
                String paramName = param.getParamName();
                String paramValue = param.getParamValue();

                // All fields in QinQ config are Strings
                configQinQRequest.setDynamicField(paramName, paramValue);
            }
        }
        return configQinQRequest;
    }


    public DynamicRequestDTO createWifiConfigRequest(DynamicRequestDTO request, List<IntegrationSpecificParamDTO> list, String ssidUserName, String ssidPassword, String workingFrequency) {
        DynamicRequestDTO configWANRequest = new DynamicRequestDTO();
        configWANRequest.setCustomerId(request.getCustomerId());
        if (!list.isEmpty()) {
            for (IntegrationSpecificParamDTO integrationSpecificParamDTO : list) {
                if (integrationSpecificParamDTO.getParamName().equalsIgnoreCase(NMSIntegrationConstant.API_CONSTANT.SSIDNAME)) {
                    String paramName = integrationSpecificParamDTO.getParamName();
                    String paramValue = ssidUserName;
                    configWANRequest.setDynamicField(paramName, paramValue);
                } else if (integrationSpecificParamDTO.getParamName().equalsIgnoreCase(NMSIntegrationConstant.API_CONSTANT.PRESHAREDKEY)) {
                    String paramName = integrationSpecificParamDTO.getParamName();
                    String paramValue = ssidPassword;
                    configWANRequest.setDynamicField(paramName, paramValue);
                } else if (integrationSpecificParamDTO.getParamName().equalsIgnoreCase(NMSIntegrationConstant.API_CONSTANT.FREQUENCYBANDWIDTH)) {
                    String paramName = integrationSpecificParamDTO.getParamName();
                    Integer paramValue = Integer.valueOf(integrationSpecificParamDTO.getParamValue());
                    configWANRequest.setIntegerDynamicField(paramName, paramValue);
                } else if (integrationSpecificParamDTO.getParamName().equalsIgnoreCase(NMSIntegrationConstant.API_CONSTANT.WORKINGFREQUENCY)) {
                    String paramName = integrationSpecificParamDTO.getParamName();
                    Integer paramValue = Integer.valueOf(integrationSpecificParamDTO.getParamValue());
                    if (paramValue.equals("2.4G")) {
                        configWANRequest.setIntegerDynamicField(paramName, 0);
                    } else if (paramValue.equals("5G")){
                        configWANRequest.setIntegerDynamicField(paramName, 1);
                    } else {
                        configWANRequest.setIntegerDynamicField(paramName, paramValue);
                    }
                } else {
                    String paramName = integrationSpecificParamDTO.getParamName();
                    String paramValue = integrationSpecificParamDTO.getParamValue();
                    configWANRequest.setDynamicField(paramName, paramValue);
                }
            }
        }
        return configWANRequest;
    }

    /**
     * Delete onu onu response dto.
     * @param request the request
     * @param accessToken the access token
     * @param configService the config service
     * @param username the username
     * @param mvnoId the mvno id
     * @return the onu response dto
     */
    public ONUResponseDTO deleteONU(DynamicRequestDTO request, String accessToken, Connfiguration configService, String username, Long mvnoId) throws JsonProcessingException {
        validateRequest(request, NMSIntegrationConstant.API_CONSTANT.DELETE_ONU);
        ONUResponseDTO response = processDelRequest(configService.getBaseurl() + ":" + configService.getPort() + "/pon/onu", request, HttpMethod.DELETE, accessToken, true, username, mvnoId, NMSIntegrationConstant.API_CONSTANT.DELETE, "DELETE_ONU");
        return response;
    }

    /**
     * Handshake onu response dto.
     * @param accessToken the access token
     * @param configService the config service
     * @param userName the user name
     * @param mvnoId the mvno id
     * @return the onu response dto
     */
    public boolean handshake(String accessToken, Connfiguration configService, String userName, Long mvnoId) {
        DynamicRequestDTO request = new DynamicRequestDTO();
        boolean handsshak = processHandShakRequest(configService.getBaseurl() + ":" + configService.getPort() + "/pon/oauth/handshake", request, HttpMethod.POST, accessToken, false, userName, mvnoId, NMSIntegrationConstant.API_CONSTANT.GET, "HANDSSHAK");
        return handsshak;
    }

    public boolean processHandShakRequest(String baseUrl, DynamicRequestDTO request, HttpMethod method, String accessToken, boolean bodyRequired, String loggedInUsername, Long mvnoId, String methodType, String operation) {
        HttpPost httpPost = getHttpPost(accessToken);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("accessToken", accessToken);
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        try {
            if (!bodyRequired) {
                HttpEntity entity = new HttpEntity<>(headers);
                RestTemplate restTemplate = apiConfig.restTemplate();
                log.info("Hand Shak Request URL: " + baseUrl);
                ResponseEntity<ONUResponseDTO> response = restTemplate.exchange(
                        baseUrl,
                        method,
                        entity,
                        ONUResponseDTO.class
                );
                apiAuditsService.saveAudit(baseUrl, response.getStatusCode().toString(), httpPost, requestInitiationTime, loggedInUsername, mvnoId.intValue(), methodType, null, null);
                return response.getStatusCode() == HttpStatus.OK;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("HandShak API call failed: " + e.getMessage(), e);
            apiAuditsService.saveAudit(baseUrl, HttpStatus.EXPECTATION_FAILED.toString(), httpPost, requestInitiationTime, loggedInUsername, mvnoId.intValue(), methodType, e.getMessage(), null);
            throw new BadRequestException("Failed to process request");
        }
        return false;
    }

    /**
     * Process onu request onu response dto.
     * @param baseUrl the base url
     * @param request the request
     * @param method the method
     * @param accessToken the access token
     * @param bodyRequired the body required
     * @param loggedInUsername the logged in username
     * @param mvnoId the mvno id
     * @param methodType the method type
     * @param
     * @return the onu response dto
     */
    public ONUResponseDTO processONURequest(String baseUrl, DynamicRequestDTO request, HttpMethod method, String accessToken, boolean bodyRequired, String loggedInUsername, Long mvnoId, String methodType, String operation) throws JsonProcessingException {
        log.info("Starte Execute Process ONU Request");
        HttpPost httpPost = getHttpPost(accessToken);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("accessToken", accessToken);
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        String jsonRequestBody = null;
        try {
            // Convert map to DTO
            AddONURequestBodyDTO addONURequestBodyDTO = new AddONURequestBodyDTO(
                    request.getParameters().get("SERIALNO"),
                    request.getParameters().get("OLTID"),
                    request.getParameters().get("PONID"),
                    request.getParameters().get("PONTYPE"),
                    request.getParameters().get("AUTHTYPE"),
                    request.getParameters().get("ONUID"),
                    request.getParameters().get("PWD"),
                    request.getParameters().get("NAME"),
                    request.getParameters().get("ONUTYPE"),
                    request.getParameters().get("DESC"),
                    request.getParameters().get("UPBW"),
                    request.getParameters().get("DOWNBW")
            );
            ObjectMapper objectMapper = new ObjectMapper();
            jsonRequestBody = objectMapper.writeValueAsString(addONURequestBodyDTO);
            HttpEntity<String> entity = new HttpEntity<>(jsonRequestBody, headers);
            RestTemplate restTemplate = apiConfig.restTemplate();
            log.info("Add ONU URL: " + baseUrl);
            log.info("Payload for Add ONU Request: " + jsonRequestBody);
            ResponseEntity<ONUResponseDTO> response = restTemplate.exchange(
                    baseUrl,
                    method,
                    entity,
                    ONUResponseDTO.class
            );
            apiAuditsService.saveAudit(baseUrl, response.getStatusCode().toString(), httpPost, requestInitiationTime, loggedInUsername, mvnoId.intValue(), methodType, null, jsonRequestBody);
            return response.getBody();
        } catch (HttpClientErrorException exe) {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = exe.getMessage().substring(exe.getMessage().indexOf("["));

            List<Map<String, String>> responseList = objectMapper.readValue(jsonResponse, List.class);

            // Extract RESUTID
            if (!responseList.isEmpty()) {
                ONUResponseDTO onuResponseDTO = processApiResponse(responseList);
                apiAuditsService.saveAudit(baseUrl, onuResponseDTO.getStatusCode().toString(), httpPost, requestInitiationTime, loggedInUsername, mvnoId.intValue(), methodType, null, jsonRequestBody);
                return onuResponseDTO;
            }
            apiAuditsService.saveAudit(baseUrl, HttpStatus.EXPECTATION_FAILED.toString(), httpPost, requestInitiationTime, loggedInUsername, mvnoId.intValue(), methodType, null, jsonRequestBody);
            return new ONUResponseDTO();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("API call failed: {}", e.getMessage(), e);
            apiAuditsService.saveAudit(baseUrl, HttpStatus.EXPECTATION_FAILED.toString(), httpPost, requestInitiationTime, loggedInUsername, mvnoId.intValue(), methodType, e.getMessage(), null);
            throw new BadRequestException("Failed to process request");
        }
    }

    public ONUResponseDTO processApiResponse(List<Map<String, String>> responseList) {
        if (responseList.isEmpty()) {
            throw new RuntimeException("API response list is empty");
        }
        String resultId = responseList.get(0).get("RESUTID");
        ONUResponseDTO onuResponseDTO = new ONUResponseDTO();
        onuResponseDTO.setData(null);
        onuResponseDTO.setResultId(resultId);

        switch (resultId) {
            case "IRNE":
                onuResponseDTO.setStatusCode(417);
                onuResponseDTO.setResultdesc("Resource does not exist");
                break;

            case "IRAE":
                onuResponseDTO.setStatusCode(417);
                onuResponseDTO.setResultdesc("Resource already exists");
                break;

            case "IANE":
                onuResponseDTO.setStatusCode(417);
                onuResponseDTO.setResultdesc("The alarm does not exist");
                break;

            case "IMP":
                onuResponseDTO.setStatusCode(417);
                onuResponseDTO.setResultdesc("Missing parameter");
                break;

            case "IIPF":
                onuResponseDTO.setStatusCode(417);
                onuResponseDTO.setResultdesc("Invalid parameter format");
                break;

            case "IIPE":
                onuResponseDTO.setStatusCode(417);
                onuResponseDTO.setResultdesc("Input parameter error");
                break;

            case "DDNS":
                onuResponseDTO.setStatusCode(417);
                onuResponseDTO.setResultdesc("Device may not support this operation");
                break;

            case "DDOF":
                onuResponseDTO.setStatusCode(417);
                onuResponseDTO.setResultdesc("Device operation failed");
                break;

            case "DDB":
                onuResponseDTO.setStatusCode(417);
                onuResponseDTO.setResultdesc("Device is busy");
                break;

            case "SENS":
                onuResponseDTO.setStatusCode(417);
                onuResponseDTO.setResultdesc("EMS may not support this operation");
                break;

            case "SEOF":
                onuResponseDTO.setStatusCode(417);
                onuResponseDTO.setResultdesc("EMS operation failed");
                break;

            case "EEEH":
                onuResponseDTO.setStatusCode(417);
                onuResponseDTO.setResultdesc("EMS exception happens");
                break;

            case "TUB":
                onuResponseDTO.setStatusCode(417);
                onuResponseDTO.setResultdesc("User is busy");
                break;

            case "TUT":
                onuResponseDTO.setStatusCode(417);
                onuResponseDTO.setResultdesc("User is testing");
                break;

            case "TTMB":
                onuResponseDTO.setStatusCode(417);
                onuResponseDTO.setResultdesc("Test module is busy");
                break;

            default:
                onuResponseDTO.setStatusCode(417);
                onuResponseDTO.setResultdesc("Unknown error code: " + resultId);
                break;
        }
        return onuResponseDTO;
    }


    public ONUResponseDTO processDelRequest(String baseUrl, DynamicRequestDTO request, HttpMethod method, String accessToken, boolean bodyRequired, String loggedInUsername, Long mvnoId, String methodType, String operation) throws JsonProcessingException {
        log.info("Starte Execute Process ONU Request");
        HttpPost httpPost = getHttpPost(accessToken);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("accessToken", accessToken);
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        String jsonRequestBody = null;
        try {
            // Convert map to DTO
            DelRequestDTO delRequestDTO = new DelRequestDTO(
                    request.getParameters().get("SERIALNO"),
                    request.getParameters().get("OLTID"),
                    request.getParameters().get("PONID"),
                    request.getParameters().get("ONUIDTYPE"),
                    request.getParameters().get("ONUID")
            );
            ObjectMapper objectMapper = new ObjectMapper();
            jsonRequestBody = objectMapper.writeValueAsString(delRequestDTO);
            HttpEntity<String> entity = new HttpEntity<>(jsonRequestBody, headers);
            log.info("Del ONU URL: " + baseUrl);
            log.info("Payload for Del ONU Request: " + jsonRequestBody);
            RestTemplate restTemplate = apiConfig.restTemplate();
            ResponseEntity<ONUResponseDTO> response = restTemplate.exchange(
                    baseUrl,
                    method,
                    entity,
                    ONUResponseDTO.class
            );
            apiAuditsService.saveAudit(baseUrl, response.getStatusCode().toString(), httpPost, requestInitiationTime, loggedInUsername, mvnoId.intValue(), methodType, null, jsonRequestBody);
//            handleApiResponse(response.getStatusCode().value(), request);
            return response.getBody();
        } catch (HttpClientErrorException exe) {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = exe.getMessage().substring(exe.getMessage().indexOf("["));

            List<Map<String, String>> responseList = objectMapper.readValue(jsonResponse, List.class);

            // Extract RESUTID
            if (!responseList.isEmpty()) {
                ONUResponseDTO onuResponseDTO = processApiResponse(responseList);
                apiAuditsService.saveAudit(baseUrl, onuResponseDTO.getStatusCode().toString(), httpPost, requestInitiationTime, loggedInUsername, mvnoId.intValue(), methodType, null, jsonRequestBody);
                return onuResponseDTO;
            }
            apiAuditsService.saveAudit(baseUrl, HttpStatus.EXPECTATION_FAILED.toString(), httpPost, requestInitiationTime, loggedInUsername, mvnoId.intValue(), methodType, null, jsonRequestBody);
            return new ONUResponseDTO();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("API call failed: {}", e.getMessage(), e);
            apiAuditsService.saveAudit(baseUrl, HttpStatus.EXPECTATION_FAILED.toString(), httpPost, requestInitiationTime, loggedInUsername, mvnoId.intValue(), methodType, e.getMessage(), null);
            throw new BadRequestException("Failed to process request");
        }
    }

    public ONUResponseDTO processWANONURequest(String baseUrl, DynamicRequestDTO request, HttpMethod method, String accessToken, boolean bodyRequired, String loggedInUsername, Long mvnoId, String methodType, String operation) throws JsonProcessingException {
        HttpPost httpPost = getHttpPost(accessToken);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("accessToken", accessToken);
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        String jsonRequestBody = null;
        try {
            WANConfigReqBodyDTO wanConfigReqBodyDTO = new WANConfigReqBodyDTO(
                    request.getParameters().get("SERIALNO"),
                    request.getParameters().get("OLTID"),
                    request.getParameters().get("PONID"),
                    request.getParameters().get("ONUIDTYPE"),
                    request.getParameters().get("ONUID"),
                    request.getNumberParameters().get("MODE"),
                    request.getNumberParameters().get("CONNTYPE"),
                    request.getNumberParameters().get("VLAN"),
                    request.getNumberParameters().get("NAT"),
                    request.getNumberParameters().get("IPMODE"),
                    request.getNumberParameters().get("IPSTACKMODE"),
                    request.getNumberParameters().get("IP6SRCTYPE"),
                    request.getNumberParameters().get("IP6PREFIXSRCTYPE"),
                    request.getParameters().get("WANIP"),
                    request.getParameters().get("WANMASK"),
                    request.getParameters().get("WANGATEWAY"),
                    request.getParameters().get("MASTERDNS"),
                    request.getParameters().get("SLAVEDNS"),
                    request.getParameters().get("IP6ADDRESS"),
                    request.getParameters().get("IP6GATEWAY"),
                    request.getParameters().get("IP6MASTERDNS"),
                    request.getParameters().get("IP6SLAVEDNS"),
                    request.getParameters().get("IP6STATICPREFIX"),
                    request.getNumberParameters().get("PPPOEPROXY"),
                    request.getParameters().get("PPPOEUSER"),
                    request.getParameters().get("PPPOEPASSWD"),
                    request.getParameters().get("PPPOENAME"),
                    request.getNumberParameters().get("PPPOEAUTHMODE"),
                    request.getNumberParameters().get("PPPOEMODE"),
                    request.getNumberParameters().get("PPPOEIDLETIME"),
                    request.getNumberParameters().get("QOS"),
                    request.getNumberParameters().get("UPORT"),
                    request.getNumberParameters().get("SSID"),
                    request.getNumberParameters().get("VLANMODE"),
                    request.getNumberParameters().get("TRANSSTATE"),
                    request.getNumberParameters().get("QINQSTATE"),
                    request.getNumberParameters().get("TPID"),
                    request.getNumberParameters().get("QINQCOS"),
                    request.getParameters().get("DHCPREMOTEID"),
                    request.getNumberParameters().get("TCONT"),
                    request.getNumberParameters().get("GEMPORT"),
                    request.getNumberParameters().get("UPNP")
            );

            ObjectMapper objectMapper = new ObjectMapper();
            jsonRequestBody = objectMapper.writeValueAsString(wanConfigReqBodyDTO);
            HttpEntity<String> entity = new HttpEntity<>(jsonRequestBody, headers);
            log.info("WAN Config URL: " + baseUrl);
            log.info("Payload for WAN Config Request: " + jsonRequestBody);
            RestTemplate restTemplate = apiConfig.restTemplate();
            ResponseEntity<ONUResponseDTO> response = restTemplate.exchange(
                    baseUrl,
                    method,
                    entity,
                    ONUResponseDTO.class
            );
            log.warn("Response ::::: " + response.toString());
            apiAuditsService.saveAudit(baseUrl, response.getStatusCode().toString(), httpPost, requestInitiationTime, loggedInUsername, mvnoId.intValue(), methodType, null, jsonRequestBody);
//            handleApiResponse(response.getStatusCode().value(), request);
            return response.getBody();
        } catch (HttpClientErrorException exe) {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = exe.getMessage().substring(exe.getMessage().indexOf("["));

            List<Map<String, String>> responseList = objectMapper.readValue(jsonResponse, List.class);

            // Extract RESUTID
            if (!responseList.isEmpty()) {
                ONUResponseDTO onuResponseDTO = processApiResponse(responseList);
                apiAuditsService.saveAudit(baseUrl, onuResponseDTO.getStatusCode().toString(), httpPost, requestInitiationTime, loggedInUsername, mvnoId.intValue(), methodType, null, jsonRequestBody);
                return onuResponseDTO;
            }
            apiAuditsService.saveAudit(baseUrl, HttpStatus.EXPECTATION_FAILED.toString(), httpPost, requestInitiationTime, loggedInUsername, mvnoId.intValue(), methodType, null, jsonRequestBody);
            return new ONUResponseDTO();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("API call failed: {}", e.getMessage(), e);
            apiAuditsService.saveAudit(baseUrl, HttpStatus.EXPECTATION_FAILED.toString(), httpPost, requestInitiationTime, loggedInUsername, mvnoId.intValue(), methodType, e.getMessage(), null);
            throw new BadRequestException("Failed to process request");
        }
    }


    public ONUResponseDTO processQinQONURequest(String baseUrl, DynamicRequestDTO request, HttpMethod method, String accessToken, boolean bodyRequired, String loggedInUsername, Long mvnoId, String methodType, String operation) throws JsonProcessingException {
        HttpPost httpPost = getHttpPost(accessToken);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("accessToken", accessToken);
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        String jsonRequestBody = null;
        try {
            QinQConfigReqBodyDTO qinQConfigReqBodyDTO = new QinQConfigReqBodyDTO(
                    trim(request.getParameters().get("SERIALNO")),
                    trim(request.getParameters().get("OLTID")),
                    trim(request.getParameters().get("PONID")),
                    trim(request.getParameters().get("ONUIDTYPE")),
                    trim(request.getParameters().get("ONUID")),
                    trim(request.getParameters().get("SVLAN")),
                    trim(request.getParameters().get("CVLAN")),
                    trim(request.getParameters().get("UV"))
            );

            ObjectMapper objectMapper = new ObjectMapper();
            jsonRequestBody = objectMapper.writeValueAsString(qinQConfigReqBodyDTO).replaceAll("\\s*:\\s*", ":");
            HttpEntity<String> entity = new HttpEntity<>(jsonRequestBody, headers);

            log.info("QinQ Config URL: " + baseUrl);
            log.info("Payload for QinQ Config Request: " + jsonRequestBody);

            RestTemplate restTemplate = apiConfig.restTemplate();
            ResponseEntity<ONUResponseDTO> response = restTemplate.exchange(
                    baseUrl,
                    method,
                    entity,
                    ONUResponseDTO.class
            );

            apiAuditsService.saveAudit(baseUrl, response.getStatusCode().toString(),httpPost, requestInitiationTime, loggedInUsername,mvnoId.intValue(), methodType, null, jsonRequestBody);
            return response.getBody();
        } catch (HttpClientErrorException exe) {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = exe.getMessage().substring(exe.getMessage().indexOf("["));
            List<Map<String, String>> responseList = objectMapper.readValue(jsonResponse, List.class);

            if (!responseList.isEmpty()) {
                ONUResponseDTO onuResponseDTO = processApiResponse(responseList);
                apiAuditsService.saveAudit(baseUrl, onuResponseDTO.getStatusCode().toString(), httpPost, requestInitiationTime, loggedInUsername, mvnoId.intValue(), methodType, null, jsonRequestBody);
                return onuResponseDTO;
            }

            apiAuditsService.saveAudit(baseUrl, HttpStatus.EXPECTATION_FAILED.toString(), httpPost, requestInitiationTime, loggedInUsername, mvnoId.intValue(), methodType, null, jsonRequestBody);
            return new ONUResponseDTO();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("QinQ API call failed: {}", e.getMessage(), e);
            apiAuditsService.saveAudit(baseUrl, HttpStatus.EXPECTATION_FAILED.toString(), httpPost, requestInitiationTime, loggedInUsername, mvnoId.intValue(), methodType, e.getMessage(), null);
            throw new BadRequestException("Failed to process QinQ request");
        }
    }


    /**
     * Gets http post.
     * @param accessToken the access token
     * @return the http post
     */
    private HttpPost getHttpPost(String accessToken) {
        HttpPost httpPost = new HttpPost();
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Authorization", accessToken);
        return httpPost;
    }

    /**
     * Validate request.
     * @param request the request
     * @param operation the operation
     */
    private void validateRequest(DynamicRequestDTO request, String operation) {
        Map<String, Set<String>> requiredParamsConfig = apiConfig.requiredParamsConfig(operation);
        Set<String> requiredParams = requiredParamsConfig.getOrDefault(operation, Collections.emptySet());
        Set<String> providedParams = request.getParameters().keySet().stream().map(String::toLowerCase).collect(Collectors.toSet());
        ;
        Set<String> missingParams = new HashSet<>(requiredParams);
        missingParams.removeAll(providedParams);
        if (!missingParams.isEmpty()) {
            throw new BadRequestException("Missing required parameters: " + missingParams);
        }
    }

    public String handleApiResponse(String resultId) {
        if (resultId == null) {
            throw new RuntimeException("API response is null");
        }
        switch (resultId) {
            case "0":
                return "Success";

            case "IRNE":
                return "resource does not exist";

            case "IRAE":
                return "resource already exist";

            case "IANE":
                return "the alarm does not exist";

            case "IMP":
                return "missing parameter";

            case "IIPF":
                return "invalid parameter format";

            case "IIPE":
                return "input parameter error";

            case "DDNS":
                return "device may not support this operation";

            case "DDOF":
                return "device operation failed";

            case "DDB":
                return "device is busy";

            case "SENS":
                return "EMS may not support this operation";

            case "SEOF":
                return "EMS operation failed";

            case "EEEH":
                return "EMS exception happens";

            case "TUB":
                return "user is busy";

            case "TUT":
                return "user is testing";

            case "TTMB":
                return "test module is busy";
        }
        return "Unknown error code: " + resultId;
    }

    public ONUResponseDTO processWifiConfigRequest(String baseUrl, DynamicRequestDTO request, HttpMethod method, String accessToken, boolean bodyRequired, String loggedInUsername, Long mvnoId, String methodType) throws JsonProcessingException {
        HttpPost httpPost = getHttpPost(accessToken);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("accessToken", accessToken);
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        String jsonRequestBody = null;
        try {
            WiFiConfigDTO wiFiConfigDTO = new WiFiConfigDTO(
                    request.getParameters().get("SERIALNO"),
                    request.getParameters().get("OLTID"),
                    request.getParameters().get("PONID"),
                    request.getParameters().get("ONUIDTYPE"),
                    request.getParameters().get("ONUID"),
                    request.getNumberParameters().get("ENABLE"),
                    request.getNumberParameters().get("WIRELESS-AREA"),
                    request.getNumberParameters().get("WIRELESS-CHANNEL"),
                    request.getNumberParameters().get("WIRELESS-STANDARD"),
                    request.getNumberParameters().get("WORKING-FREQUENCY"),
                    request.getNumberParameters().get("T-POWER"),
                    request.getNumberParameters().get("FREQUENCY-BANDWIDTH"),
                    request.getNumberParameters().get("SSID"),
                    request.getNumberParameters().get("SSID-ENABLE"),
                    request.getParameters().get("SSID-NAME"),
                    request.getNumberParameters().get("SSID-VISIBLE"),
                    request.getNumberParameters().get("AUTH-MODE"),
                    request.getNumberParameters().get("ENCRYPT-TYPE"),
                    request.getParameters().get("PRESHARED-KEY"),
                    request.getNumberParameters().get("UPDATEKEY-INTERVAL"),
                    request.getParameters().get("RADIUS-SERVER"),
                    request.getNumberParameters().get("RADIUS-PORT"),
                    request.getParameters().get("RADIUS-KEY"),
                    request.getNumberParameters().get("WEP-ENCRYPTIONLEVEL"),
                    request.getNumberParameters().get("WEP-KEYINDEX"),
                    request.getParameters().get("WEPKEY1"),
                    request.getParameters().get("WEPKEY2"),
                    request.getParameters().get("WEPKEY3"),
                    request.getParameters().get("WEPKEY4"),
                    request.getParameters().get("WAP-IPADDRESS"),
                    request.getNumberParameters().get("WAP-PORT"),
                    request.getNumberParameters().get("MAX-WIFIMAC-COUNT"),
                    request.getNumberParameters().get("PUBLICSSID"),
                    request.getNumberParameters().get("KICKSTATIONSWITCH"),
                    request.getNumberParameters().get("LOWERTHRESHOLD")
            );

            ObjectMapper objectMapper = new ObjectMapper();
            jsonRequestBody = objectMapper.writeValueAsString(wiFiConfigDTO);
            HttpEntity<String> entity = new HttpEntity<>(jsonRequestBody, headers);
            log.info("Wifi Config URL: " + baseUrl);
            log.info("Payload for Wifi Config Request: " + jsonRequestBody);
            RestTemplate restTemplate = apiConfig.restTemplate();
            ResponseEntity<ONUResponseDTO> response = restTemplate.exchange(
                    baseUrl,
                    method,
                    entity,
                    ONUResponseDTO.class
            );
            log.warn("Response ::::: " + response.toString());
            apiAuditsService.saveAudit(baseUrl, response.getStatusCode().toString(), httpPost, requestInitiationTime, loggedInUsername, mvnoId.intValue(), methodType, null, jsonRequestBody);
            return response.getBody();
        } catch (HttpClientErrorException exe) {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = exe.getMessage().substring(exe.getMessage().indexOf("["));

            List<Map<String, String>> responseList = objectMapper.readValue(jsonResponse, List.class);

            // Extract RESUTID
            if (!responseList.isEmpty()) {
                ONUResponseDTO onuResponseDTO = processApiResponse(responseList);
                apiAuditsService.saveAudit(baseUrl, onuResponseDTO.getStatusCode().toString(), httpPost, requestInitiationTime, loggedInUsername, mvnoId.intValue(), methodType, null, jsonRequestBody);
                return onuResponseDTO;
            }
            apiAuditsService.saveAudit(baseUrl, HttpStatus.EXPECTATION_FAILED.toString(), httpPost, requestInitiationTime, loggedInUsername, mvnoId.intValue(), methodType, null, jsonRequestBody);
            return new ONUResponseDTO();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("API call failed: {}", e.getMessage(), e);
            apiAuditsService.saveAudit(baseUrl, HttpStatus.EXPECTATION_FAILED.toString(), httpPost, requestInitiationTime, loggedInUsername, mvnoId.intValue(), methodType, e.getMessage(), null);
            throw new BadRequestException("Failed to process request");
        }
    }

//    public ONUResponseDTO wifiConfigData(DynamicRequestDTO request, String accessToken, Connfiguration configService, String loggedInUserName, Long mvnoId, List<IntegrationSpecificParamDTO> list) {
//
//        String basedUrl = configService.getBaseurl() + ":" + configService.getPort() + "/pon/serviceCfg/wifiServiceCfg";
//        ONUResponseDTO response = processWifiConfigRequest(basedUrl, request, HttpMethod.POST, accessToken, true, loggedInUserName, mvnoId, NMSIntegrationConstant.API_CONSTANT.POST, "WIFI_CONFIG");
//        return response;
//    }

    public DynamicRequestDTO DeleteConfigWANRequest(DynamicRequestDTO request, List<IntegrationSpecificParamDTO> list) {
        DynamicRequestDTO configWANRequest = new DynamicRequestDTO();
        configWANRequest.setCustomerId(request.getCustomerId());
        if (!list.isEmpty()) {
            for (IntegrationSpecificParamDTO integrationSpecificParamDTO : list) {
                if (integrationSpecificParamDTO.getParamName().equalsIgnoreCase("VLAN")) {
                    String paramName = integrationSpecificParamDTO.getParamName();
                    Integer paramValue = Integer.valueOf(integrationSpecificParamDTO.getParamValue());
                    configWANRequest.setIntegerDynamicField(paramName, paramValue);
                } else {
                    String paramName = integrationSpecificParamDTO.getParamName();
                    String paramValue = integrationSpecificParamDTO.getParamValue();
                    configWANRequest.setDynamicField(paramName, paramValue);
                }
            }
        }
        return configWANRequest;
    }

    public DynamicRequestDTO setDeleteWANStaticDto() {
        DynamicRequestDTO staticDTO = new DynamicRequestDTO();
        staticDTO.setDynamicField("ONUIDTYPE", "MAC");
        staticDTO.setIntegerDynamicField("MODE", 1);
        staticDTO.setIntegerDynamicField("CONNTYPE", 1);
        staticDTO.setIntegerDynamicField("UPORT", 1);
        staticDTO.setIntegerDynamicField("SSID", 1);
        return staticDTO;
    }

    public ONUResponseDTO processDeleteWANONURequest(String baseUrl, DynamicRequestDTO request, HttpMethod method, String accessToken, boolean bodyRequired, String loggedInUsername, Long mvnoId, String methodType, String operation) {
        HttpPost httpPost = getHttpPost(accessToken);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("accessToken", accessToken);
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        try {
            DeleteWANConfigRequestDTO deletewanConfigReqDTO = new DeleteWANConfigRequestDTO(
                    request.getParameters().get("SERIALNO"),
                    request.getParameters().get("OLTID"),
                    request.getParameters().get("PONID"),
                    request.getParameters().get("ONUIDTYPE"),
                    request.getParameters().get("ONUID"),
                    request.getNumberParameters().get("MODE"),
                    request.getNumberParameters().get("CONNTYPE"),
                    request.getNumberParameters().get("UPORT"),
                    request.getNumberParameters().get("SSID")
            );
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonRequestBody = objectMapper.writeValueAsString(deletewanConfigReqDTO);
            HttpEntity<String> entity = new HttpEntity<>(jsonRequestBody, headers);
            RestTemplate restTemplate = apiConfig.restTemplate();
            ResponseEntity<ONUResponseDTO> response = restTemplate.exchange(
                    baseUrl,
                    method,
                    entity,
                    ONUResponseDTO.class
            );
            log.info("DeleteWAN Config URL: " + baseUrl);
            log.info("Payload for DeleteWAN Config Request: " + jsonRequestBody);
            log.warn("Response ::::: " + response.toString());
            apiAuditsService.saveAudit(baseUrl, response.getStatusCode().toString(), httpPost, requestInitiationTime, loggedInUsername, mvnoId.intValue(), methodType, null, jsonRequestBody);
//            handleApiResponse(response.getStatusCode().value(), request);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("API call failed: {}", e.getMessage(), e);
            apiAuditsService.saveAudit(baseUrl, HttpStatus.EXPECTATION_FAILED.toString(), httpPost, requestInitiationTime, loggedInUsername, mvnoId.intValue(), methodType, e.getMessage(), null);
            throw new BadRequestException("Failed to process request");
        }
    }

    /**
     * Helper Method to Trim spaces
     * @param value
     * @return
     */
    private String trim(String value) {
        return (value != null) ? value.trim() : null;
    }
}
