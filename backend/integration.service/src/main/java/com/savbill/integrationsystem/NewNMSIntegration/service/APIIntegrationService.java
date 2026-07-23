package com.savbill.integrationsystem.NewNMSIntegration.service;

import brave.Tracer;
import com.savbill.integrationsystem.NewNMSIntegration.configuration.ApiConfig;
import com.savbill.integrationsystem.NewNMSIntegration.constants.NMSIntegrationConstant;
import com.savbill.integrationsystem.NewNMSIntegration.dto.*;
import com.savbill.integrationsystem.NewNMSIntegration.dto.*;
import com.savbill.integrationsystem.NewNMSIntegration.entity.IntegrationParameters;
import com.savbill.integrationsystem.NewNMSIntegration.entity.NmsIntegration;
import com.savbill.integrationsystem.NewNMSIntegration.message.NMSIntegrationMessage;
import com.savbill.integrationsystem.NewNMSIntegration.repository.IntegrationParametersRepository;
import com.savbill.integrationsystem.NewNMSIntegration.repository.NnmIntegrationRepository;
import com.savbill.integrationsystem.apiAudits.NMSServices.ApiAuditsService;
import com.savbill.integrationsystem.billgen.entity.StaffUser;
import com.savbill.integrationsystem.billgen.repository.StaffUserRepository;
import com.savbill.integrationsystem.core.security.dto.LoggedInUser;
import com.savbill.integrationsystem.nms.entity.*;
import com.savbill.integrationsystem.nms.entity.ConfigRepocitory;
import com.savbill.integrationsystem.nms.entity.Connfiguration;
import com.savbill.integrationsystem.nms.entity.TokenManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The type Nms integration service.
 */
@Service
public class APIIntegrationService {

    private static final Logger log = LoggerFactory.getLogger(APIIntegrationService.class);

    /**
     * The constant log.
     */
//  private static final String HANDSHAKE_URL = "http://102.209.109.2:8017/pon/oauth/handshake";

    /**
     * The Api audits service.
     */
    @Autowired
    ApiAuditsService apiAuditsService;

    /**
     * The Auth service.
     */


    @Autowired
    AuthService authService;

    @Autowired
    NmsIntegrationService nmsIntegrationService;
    /**
     * The Onu service.
     */
    @Autowired
    ONUService onuService;

    /**
     * The Staff user repository.
     */
    @Autowired
    StaffUserRepository staffUserRepository;

    /**
     * The Nnm integration repository.
     */
    @Autowired
    NnmIntegrationRepository nnmIntegrationRepository;

    /**
     * The Integration parameters repository.
     */
    @Autowired
    IntegrationParametersRepository integrationParametersRepository;

    /**
     * The Api config.
     */
    @Autowired
    private ApiConfig apiConfig;


    /**
     * The Config repocitory.
     */
    @Autowired
    private ConfigRepocitory configRepocitory;

    /**
     * The Tracer.
     */
    @Autowired
    Tracer tracer;

    /**
     * Process nms integration string.
     * @param dynamicRequestDTO the dynamic request dto
     * @param jwtToken the jwt token
     * @param baseIp the base ip
     * @param port the port
     * @return the string
     */
    public String processNMSIntegration(DynamicRequestDTO dynamicRequestDTO, String jwtToken, String baseIp, String port) {
        NMSIntegrationMessage nmsIntegrationMessage = convertDynamicRequestToNMSMessage(dynamicRequestDTO);
        return generateToken(jwtToken, nmsIntegrationMessage, baseIp, port);
    }

    /**
     * Convert dynamic request to nms message nms integration message.
     * @param dynamicRequestDTO the dynamic request dto
     * @return the nms integration message
     */
    private NMSIntegrationMessage convertDynamicRequestToNMSMessage(DynamicRequestDTO dynamicRequestDTO) {

        NMSIntegrationMessage nmsIntegrationMessage = new NMSIntegrationMessage();
        nmsIntegrationMessage.setOperation(determineOperation(dynamicRequestDTO.getApiName()));

        // Convert dynamic parameters to IntegrationSpecificParamDTO list
        List<IntegrationSpecificParamDTO> paramList = dynamicRequestDTO.getParameters().entrySet().stream()
                .map(entry -> {
                    IntegrationSpecificParamDTO paramDTO = new IntegrationSpecificParamDTO();
                    paramDTO.setParamName(entry.getKey());
                    paramDTO.setParamValue(entry.getValue());
                    return paramDTO;
                })
                .collect(Collectors.toList());

        nmsIntegrationMessage.setList(paramList);

        setAdditionalFields(nmsIntegrationMessage, dynamicRequestDTO);

        return nmsIntegrationMessage;
    }

    /**
     * Determine operation string.
     * @param apiName the api name
     * @return the string
     */
    private String determineOperation(String apiName) {
        // Switch case to determine operation based on API name
        switch (apiName) {
            case "ADD_ONU":
                return "addOnu";
            case "DELETE_ONU":
                return "deteletedOnu";
            case "LOGIN":
                return "login";
            case "HANDSHAKE":
                return "handshake";
            case "UPDATE":
                return "update";
            default:
                return "unknown";
        }
    }

    /**
     * Sets additional fields.
     * @param nmsIntegrationMessage the nms integration message
     * @param dynamicRequestDTO the dynamic request dto
     */
    private void setAdditionalFields(NMSIntegrationMessage nmsIntegrationMessage, DynamicRequestDTO dynamicRequestDTO) {

        nmsIntegrationMessage.setConfigName(dynamicRequestDTO.getParameters().getOrDefault("configName", "DefaultConfig"));


        try {
            nmsIntegrationMessage.setLoggedInUserId(Integer.parseInt(
                    dynamicRequestDTO.getParameters().getOrDefault("loggedInUserId", "0")
            ));

            nmsIntegrationMessage.setMvnoId(Long.parseLong(
                    dynamicRequestDTO.getParameters().getOrDefault("mvnoId", "0")
            ));

            nmsIntegrationMessage.setCustInvenId(Long.parseLong(
                    dynamicRequestDTO.getParameters().getOrDefault("custInvenId", "0")
            ));

            nmsIntegrationMessage.setItemId(Long.parseLong(
                    dynamicRequestDTO.getParameters().getOrDefault("itemId", "0")
            ));

            nmsIntegrationMessage.setCustomerId(Long.parseLong(
                    dynamicRequestDTO.getParameters().getOrDefault("customerId", "0")
            ));
        } catch (NumberFormatException e) {
            log.warn("Error parsing numeric fields from parameters", e);
        }
    }

    /**
     * Nms integration string.
     * @param nmsIntegrationMessage the nms integration message
     * @return the string
     */
    public String nmsIntegration(NMSIntegrationMessage nmsIntegrationMessage) {
        MDC.put("type", "CREATE");
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        String serviceData = null;

        try {
            if (nmsIntegrationMessage == null) {
                log.error("LoginRequestDTO is null. Cannot proceed with NMS service activation.");
                return "Invalid request: LoginRequestDTO is null.";
            }
            Long mvnoId = nmsIntegrationMessage.getMvnoId();
            Connfiguration configService = configRepocitory.findByNameAndMvnoId(nmsIntegrationMessage.getConfigName(), mvnoId.intValue());
            if (configService == null) {
                log.error("Configuration not available for: {}", nmsIntegrationMessage.getConfigName());
                return "Base API Config not available!";
            }
            String grantType = "password";
            Optional<StaffUser> staffUserOptional = staffUserRepository.findById(nmsIntegrationMessage.getLoggedInUserId());
            String userName = null;
            if (staffUserOptional.isPresent()) {
                userName = staffUserOptional.get().getUsername();
            }
            String jwtToken = getValidJwtToken(configService, grantType, userName, nmsIntegrationMessage.getMvnoId());
            if (jwtToken == null || jwtToken.isEmpty()) {
                log.error("Failed to retrieve valid JWT token.");
                return "Failed to retrieve JWT token!";
            }
            processToCallThirdPartyAPI(jwtToken, nmsIntegrationMessage, configService, userName);
            log.info("Successfully activated NMS service for user: {}", configService.getUsername());
            return serviceData;
        } catch (Exception ex) {
            log.error("Error during nms service activation: {}", ex.getMessage(), ex);
            return "Error during nms service activation";
        }
    }

    /**
     * Process to call third party api.
     * @param authToken the auth token
     * @param nmsIntegrationMessage the nms integration message
     * @param configService the config service
     * @param userName the user name
     */
    private void processToCallThirdPartyAPI(String authToken, NMSIntegrationMessage nmsIntegrationMessage, Connfiguration configService, String userName) {
        switch (nmsIntegrationMessage.getOperation()) {
            case NMSIntegrationConstant.API_CONSTANT.ADD_ONU:
                addONUIntegration(nmsIntegrationMessage);
                break;
            case NMSIntegrationConstant.API_CONSTANT.DELETE_ONU:
                delONUIntegration(nmsIntegrationMessage);
                break;
        }
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

    private DynamicRequestDTO setStaticDto() {
        DynamicRequestDTO staticDTO = new DynamicRequestDTO();
        staticDTO.setDynamicField("PONTYPE", "GPON");
        staticDTO.setDynamicField("AUTHTYPE", "MAC");
        staticDTO.setDynamicField("PWD", null);
        staticDTO.setIntegerDynamicField("ONUNO", null);
        staticDTO.setDynamicField("NAME", null);
        staticDTO.setDynamicField("DESC", null);
        staticDTO.setDynamicField("UPBW", null);
        staticDTO.setDynamicField("DOWNBW", null);
        return staticDTO;
    }

    private DynamicRequestDTO setDelStaticDto() {
        DynamicRequestDTO staticDTO = new DynamicRequestDTO();
        staticDTO.setDynamicField("ONUIDTYPE", "MAC");
        return staticDTO;
    }

    private DynamicRequestDTO setStaticDeleteDto() {
        DynamicRequestDTO staticDTO = new DynamicRequestDTO();
        staticDTO.setDynamicField("AUTHTYPE", "MAC");
        return staticDTO;
    }

    /**
     * Update nms integration message.
     * @param nmsIntegrationMessage the nms integration message
     * @param status the status
     */
    private void updateNmsIntegrationMessage(NMSIntegrationMessage nmsIntegrationMessage, String status) {
        Optional<NmsIntegration> optionalNmsIntegration = getNmsIntegrationPresent(nmsIntegrationMessage);
        if (optionalNmsIntegration != null) {
            optionalNmsIntegration.get().setStatus(status);
            nnmIntegrationRepository.save(optionalNmsIntegration.get());
        }
    }

    /**
     * Save nms integration message.
     * @param nmsIntegrationMessage the nms integration message
     * @param status the status
     */
    private void saveNmsIntegrationMessage(NMSIntegrationMessage nmsIntegrationMessage, String status) {
        NmsIntegration savedNmsIntegration = saveCommonNmsIntegration(nmsIntegrationMessage, status);

        if (nmsIntegrationMessage.getOperation().equalsIgnoreCase(NMSIntegrationConstant.API_CONSTANT.ADD_ONU)) {
            setIntegrationParameters(nmsIntegrationMessage.getList(), nmsIntegrationMessage.getItemId(), savedNmsIntegration.getId());
        }
    }

    private void saveWifiNmsIntegrationMessage(NMSIntegrationMessage nmsIntegrationMessage, String status, String ssidUserName, String ssidPassword, String workingFrequency) {
        NmsIntegration savedNmsIntegration = saveCommonNmsIntegration(nmsIntegrationMessage, status);

        if (nmsIntegrationMessage.getOperation().equalsIgnoreCase(NMSIntegrationConstant.API_CONSTANT.WIFI_CONFIG)) {
            saveIntegrationParameter(nmsIntegrationMessage.getItemId(), savedNmsIntegration.getId(), ssidUserName, NMSIntegrationConstant.API_CONSTANT.SSIDNAME);
            saveIntegrationParameter(nmsIntegrationMessage.getItemId(), savedNmsIntegration.getId(), ssidPassword, NMSIntegrationConstant.API_CONSTANT.PRESHAREDKEY);
            saveIntegrationParameter(nmsIntegrationMessage.getItemId(), savedNmsIntegration.getId(), workingFrequency, NMSIntegrationConstant.API_CONSTANT.WORKINGFREQUENCY);
        }
    }

    private NmsIntegration saveCommonNmsIntegration(NMSIntegrationMessage nmsIntegrationMessage, String status) {
        NmsIntegration nmsIntegration = new NmsIntegration();
        nmsIntegration.setConfigName(nmsIntegrationMessage.getConfigName());
        nmsIntegration.setItemId(nmsIntegrationMessage.getItemId());
        nmsIntegration.setOperation(nmsIntegrationMessage.getOperation());
        nmsIntegration.setCustomerId(nmsIntegrationMessage.getCustomerId());
        nmsIntegration.setCustInvenId(nmsIntegrationMessage.getCustInvenId());
        nmsIntegration.setStatus(status);
        nmsIntegration.setMvnoId(nmsIntegrationMessage.getMvnoId());
        nmsIntegration.setCreatedByStaff(nmsIntegrationMessage.getLoggedInUserId().longValue());
        nmsIntegration.setSerialNumber(nmsIntegrationMessage.getSerialNumber());

        return nnmIntegrationRepository.save(nmsIntegration);
    }

    private void saveIntegrationParameter(Long itemId, Long id, String paramValue, String paramName) {
        if (paramValue != null) {
            IntegrationParameters param = new IntegrationParameters();
            param.setIntegration_id(id);
            param.setParamName(paramName);
            param.setParamValue(paramValue);
            param.setItemId(itemId);
            integrationParametersRepository.save(param);
        }
    }

    /**
     * Sets integration parameters.
     * @param list the list
     * @param itemId the item id
     * @param id the id
     * @return the integration parameters
     */
    private List<IntegrationParameters> setIntegrationParameters(List<IntegrationSpecificParamDTO> list, Long itemId, Long id) {
        List<IntegrationParameters> parametersList = new ArrayList<>();
        if (!list.isEmpty()) {
            for (IntegrationSpecificParamDTO integrationSpecificParamDTO : list) {
                IntegrationParameters param = new IntegrationParameters();
                param.setIntegration_id(id);
                param.setParamName(integrationSpecificParamDTO.getParamName());
                param.setParamValue(integrationSpecificParamDTO.getParamValue());
                param.setItemId(itemId);
                parametersList.add(param);
            }
        }
        integrationParametersRepository.saveAll(parametersList);
        return parametersList;
    }

    /**
     * Gets messafe to dynamic dto.
     * @param nmsIntegrationMessage the nms integration message
     * @return the messafe to dynamic dto
     */
    private DynamicRequestDTO getMessafeToDynamicDTO(NMSIntegrationMessage nmsIntegrationMessage) {
        DynamicRequestDTO dynamicRequestDTO = new DynamicRequestDTO();
        dynamicRequestDTO.setDynamicField("NAME",nmsIntegrationMessage.getName());
        dynamicRequestDTO.setDynamicField("DESC",nmsIntegrationMessage.getDesc());
        if (!nmsIntegrationMessage.getList().isEmpty()) {
            for (IntegrationSpecificParamDTO integrationSpecificParamDTO : nmsIntegrationMessage.getList()) {
                String paramName = integrationSpecificParamDTO.getParamName();
                String paramValue = integrationSpecificParamDTO.getParamValue();
                dynamicRequestDTO.setDynamicField(paramName, paramValue);
            }
        }
        return dynamicRequestDTO;
    }

    /**
     * Generate token string.
     * @param jwtToken the jwt token
     * @param nmsIntegrationMessage the nms integration message
     * @param baseIp the base ip
     * @param port the port
     * @return the string
     */
    private String generateToken(String jwtToken, NMSIntegrationMessage nmsIntegrationMessage, String baseIp, String port) {
        String activationUrl = String.format("http://%s:%s/pon/nms/activate", baseIp, port);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            HttpEntity<NMSIntegrationMessage> request = new HttpEntity<>(nmsIntegrationMessage, headers);
            RestTemplate restTemplate = apiConfig.restTemplate();
            ResponseEntity<String> response = restTemplate.exchange(activationUrl, HttpMethod.POST, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                log.error("Failed to activate NMS service. HTTP Status: {}", response.getStatusCode());
                return "Failed to activate NMS service. Status: " + response.getStatusCode();
            }
        } catch (Exception e) {
            log.error("Error during generate token for nms integration: {}", e.getMessage(), e);
            return "Error during generate token for nms integration";
        }
    }

    /**
     * Gets valid jwt token.
     * @param configService the config service
     * @param grantType the grant type
     * @param userName the user name
     * @param mvnoId the mvno id
     * @return the valid jwt token
     */
    public String getValidJwtToken(Connfiguration configService, String grantType, String userName, Long mvnoId) throws Exception {
        log.info("Start To Excetute Get Token API Request");
        try {
            TokenManager tokenManager = TokenManager.getInstance();
            if (tokenManager.isTokenExpired()) {
                LoginRequestDTO loginRequestDTO = getLoginRequestDto(grantType, configService);
                ONUResponseDTO login = authService.login(loginRequestDTO, userName, mvnoId);
                if (login != null) {
                    String authToken = authService.getAccessToken(login);
                    String expires = authService.getExpires(login);
                    String accessToken = authToken;
                    Integer expiresIn = Integer.valueOf(expires);
                    LocalDateTime expiryTime = LocalDateTime.now().plusSeconds(expiresIn);
                    TokenManager.getInstance().updateToken(accessToken, expiryTime);
                }
            } else {
                String jwtToken = tokenManager.getJwtToken();
                boolean handshake = onuService.handshake(jwtToken, configService, userName, mvnoId);
                if (handshake) {
                    return jwtToken;
                } else {
                    LoginRequestDTO loginRequestDTO = getLoginRequestDto(grantType, configService);
                    ONUResponseDTO login = authService.login(loginRequestDTO, userName, mvnoId);
                    if (login != null) {
                        String authToken = authService.getAccessToken(login);
                        String expires = authService.getExpires(login);
                        String accessToken = authToken;
                        Integer expiresIn = Integer.valueOf(expires);
                        LocalDateTime expiryTime = LocalDateTime.now().plusSeconds(expiresIn);
                        TokenManager.getInstance().updateToken(accessToken, expiryTime);
                    }
                }
            }
            return tokenManager.getJwtToken();
        } catch (Exception ex) {
            log.error("Error during get token api request for nms integration: " + ex.getMessage(), ex);
            return "Error during get token api request for nms integration";
        }
    }

    /**
     * Gets login request dto.
     * @param grantType the grant type
     * @param configService the config service
     * @return the login request dto
     */
    private LoginRequestDTO getLoginRequestDto(String grantType, Connfiguration configService) {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setGrantType(grantType);
        loginRequestDTO.setUserName(configService.getUsername());
        loginRequestDTO.setValue(configService.getPassword());
        loginRequestDTO.setBaseURL(configService.getBaseurl());
        loginRequestDTO.setPort(configService.getPort());
        return loginRequestDTO;
    }

    public String addONUIntegration(NMSIntegrationMessage nmsIntegrationMessage) {
        log.info("Start to execute Add ONU Integration Method for Serial Number " + nmsIntegrationMessage.getSerialNumber());
        try {
            if (nmsIntegrationMessage == null) {
                log.error("LoginRequestDTO is null. Cannot proceed with NMS service activation.");
                return "Invalid request: LoginRequestDTO is null.";
            }
            Optional<NmsIntegration> optionalNmsIntegration = getNmsIntegrationPresent(nmsIntegrationMessage);
            if (optionalNmsIntegration != null && optionalNmsIntegration.isPresent()) {
                optionalNmsIntegration.get().setStatus(NMSIntegrationConstant.API_CONSTANT.PENDING);
                nnmIntegrationRepository.save(optionalNmsIntegration.get());
            } else {
                /** Save NMS Integration Data */
                saveNmsIntegrationMessage(nmsIntegrationMessage, NMSIntegrationConstant.API_CONSTANT.PENDING);
            }
            Long mvnoId = nmsIntegrationMessage.getMvnoId();
            Connfiguration configService = configRepocitory.findByNameAndMvnoId(nmsIntegrationMessage.getConfigName(), mvnoId.intValue());
            if (configService == null) {
                log.error("Configuration not available for: " + nmsIntegrationMessage.getConfigName());
                return "Base API Config not available!";
            }
            String grantType = "password";
            Optional<StaffUser> staffUserOptional = staffUserRepository.findById(nmsIntegrationMessage.getLoggedInUserId());
            String loggedInUserName = null;
            if (staffUserOptional.isPresent()) {
                loggedInUserName = staffUserOptional.get().getUsername();
            }
            String jwtToken = getValidJwtToken(configService, grantType, loggedInUserName, nmsIntegrationMessage.getMvnoId());
            if (jwtToken == null || jwtToken.isEmpty()) {
                log.error("Failed to retrieve valid JWT token.");
                return "Failed to retrieve JWT token!";
            }
            DynamicRequestDTO dynamicRequestDTO = getMessafeToDynamicDTO(nmsIntegrationMessage);
            DynamicRequestDTO staticDTO = setStaticDto();
            DynamicRequestDTO finalDTO = finalDTO(dynamicRequestDTO, staticDTO);
            ONUResponseDTO onuResponseDTO = onuService.addONU(finalDTO, jwtToken, configService, loggedInUserName, nmsIntegrationMessage.getMvnoId(), nmsIntegrationMessage.getList());
            String resultId = onuResponseDTO.getResultId();
            if (resultId.equalsIgnoreCase("0")) {
                updateNmsIntegrationMessage(nmsIntegrationMessage, NMSIntegrationConstant.API_CONSTANT.COMPLETED);
            } else {
                updateNmsIntegrationMessage(nmsIntegrationMessage, NMSIntegrationConstant.API_CONSTANT.FAILED);
            }
            if (resultId.equalsIgnoreCase("0") || resultId.equalsIgnoreCase("IRAE")) {
                DynamicRequestDTO dynamicWANRequestDTO = onuService.createConfigWANRequest(dynamicRequestDTO, nmsIntegrationMessage.getList());
                DynamicRequestDTO staticWANDTO = onuService.setWANStaticDto();
                DynamicRequestDTO finalWANDTO = finalDTO(dynamicWANRequestDTO, staticWANDTO);
                ONUResponseDTO configWANResponse = onuService.processWANONURequest(configService.getBaseurl() + ":" + configService.getPort() + "/pon/serviceCfg/wanService",
                        finalWANDTO, HttpMethod.POST,
                        jwtToken, true, loggedInUserName, mvnoId,
                        NMSIntegrationConstant.API_CONSTANT.POST, "ADD_ONU");
                String wanresultId = configWANResponse.getResultId();
                String handleApiResponse = onuService.handleApiResponse(wanresultId);
                log.info("WAN response message: " + handleApiResponse);


                //Added for QINQ API
                if (wanresultId.equalsIgnoreCase("0") || wanresultId.equalsIgnoreCase("IRAE")) {
                    // Build QinQ DTO
                    DynamicRequestDTO dynamicQinQRequestDTO = onuService.createConfigQinQRequest(dynamicWANRequestDTO, nmsIntegrationMessage.getList());
                    DynamicRequestDTO staticQinQDTO = onuService.setQinQStaticDto();
                    DynamicRequestDTO finalQinQDTO = finalDTO(dynamicQinQRequestDTO, staticQinQDTO);

                    ONUResponseDTO configQinQResponse = onuService.processQinQONURequest(configService.getBaseurl() + ":" + configService.getPort() + "/pon/ponVlan",
                            finalQinQDTO, HttpMethod.POST,
                            jwtToken, true, loggedInUserName, mvnoId,
                            NMSIntegrationConstant.API_CONSTANT.POST,"ADD_ONU");
                    String qinqResultId = configQinQResponse.getResultId();
                    handleApiResponse = onuService.handleApiResponse(qinqResultId);
                    log.info("QinQ response message: " + handleApiResponse);
                }

                return handleApiResponse;
            } else {
                String handleApiResponse = onuService.handleApiResponse(resultId);
                log.info("Add ONU response message: " + handleApiResponse);
                return handleApiResponse;
            }
        } catch (Exception ex) {
            log.error("Error during add onu for nms integration: {}", ex.getMessage(), ex);
            return "Error during add onu for nms integration";
        }
    }

    public String updateWANConfig(NMSIntegrationMessage nmsIntegrationMessage) {
        log.info("Start to execute Add ONU Integration Method for Serial Number " + nmsIntegrationMessage.getSerialNumber());
        try {
            if (nmsIntegrationMessage == null) {
                log.error("LoginRequestDTO is null. Cannot proceed with NMS service activation.");
                return "Invalid request: LoginRequestDTO is null.";
            }
            Long mvnoId = nmsIntegrationMessage.getMvnoId();
            Connfiguration configService = configRepocitory.findByNameAndMvnoId(nmsIntegrationMessage.getConfigName(), mvnoId.intValue());
            if (configService == null) {
                log.error("Configuration not available for: " + nmsIntegrationMessage.getConfigName());
                return "Base API Config not available!";
            }
            String grantType = "password";
            Optional<StaffUser> staffUserOptional = staffUserRepository.findById(nmsIntegrationMessage.getLoggedInUserId());
            String loggedInUserName = null;
            if (staffUserOptional.isPresent()) {
                loggedInUserName = staffUserOptional.get().getUsername();
            }
            String jwtToken = getValidJwtToken(configService, grantType, loggedInUserName, nmsIntegrationMessage.getMvnoId());
            if (jwtToken == null || jwtToken.isEmpty()) {
                log.error("Failed to retrieve valid JWT token.");
                return "Failed to retrieve JWT token!";
            }
            DynamicRequestDTO dynamicRequestDTO = getMessafeToDynamicDTO(nmsIntegrationMessage);
            DynamicRequestDTO dynamicWANRequestDTO = onuService.createConfigWANRequest(dynamicRequestDTO, nmsIntegrationMessage.getList());
            DynamicRequestDTO staticWANDTO = onuService.setWANStaticDto();
            DynamicRequestDTO finalWANDTO = finalDTO(dynamicWANRequestDTO, staticWANDTO);
            ONUResponseDTO configWANResponse = onuService.processWANONURequest(configService.getBaseurl() + ":" + configService.getPort() + "/pon/serviceCfg/wanService",
                    finalWANDTO, HttpMethod.POST,
                    jwtToken, true, loggedInUserName, mvnoId,
                    NMSIntegrationConstant.API_CONSTANT.POST, NMSIntegrationConstant.API_CONSTANT.WAN_CONFIG);
            String wanresultId = configWANResponse.getResultId();
            if (wanresultId.equalsIgnoreCase("0") || wanresultId.equalsIgnoreCase("IRAE")) {
                String handleApiResponse = onuService.handleApiResponse(wanresultId);
                log.info("WAN response message: " + handleApiResponse);
                return handleApiResponse;
            } else {
                String handleApiResponse = onuService.handleApiResponse(wanresultId);
                log.info("WAN response message: " + handleApiResponse);
                return handleApiResponse;
            }
        } catch (Exception ex) {
            log.error("Error during update wan config for nms integration: {}", ex.getMessage(), ex);
            return "Error during update wan config for nms integration";
        }
    }

    public String delONUIntegration(NMSIntegrationMessage nmsIntegrationMessage) {
        try {
            if (nmsIntegrationMessage == null) {
                log.error("LoginRequestDTO is null. Cannot proceed with NMS service activation.");
                return "Invalid request: LoginRequestDTO is null.";
            }
            Optional<NmsIntegration> optionalNmsIntegration = getNmsIntegrationPresent(nmsIntegrationMessage);
            if (optionalNmsIntegration != null && optionalNmsIntegration.isPresent()) {
                optionalNmsIntegration.get().setStatus(NMSIntegrationConstant.API_CONSTANT.PENDING);
                nnmIntegrationRepository.save(optionalNmsIntegration.get());
            } else {
                /** Save NMS Integration Data */
                saveNmsIntegrationMessage(nmsIntegrationMessage, NMSIntegrationConstant.API_CONSTANT.PENDING);
            }
            Long mvnoId = nmsIntegrationMessage.getMvnoId();
            Connfiguration configService = configRepocitory.findByNameAndMvnoId(nmsIntegrationMessage.getConfigName(), mvnoId.intValue());
            if (configService == null) {
                log.error("Configuration not available for: {}", nmsIntegrationMessage.getConfigName());
                return "Base API Config not available!";
            }
            String grantType = "password";
            Optional<StaffUser> staffUserOptional = staffUserRepository.findById(nmsIntegrationMessage.getLoggedInUserId());
            String loggedInUserName = null;
            if (staffUserOptional.isPresent()) {
                loggedInUserName = staffUserOptional.get().getUsername();
            }
            String jwtToken = getValidJwtToken(configService, grantType, loggedInUserName, nmsIntegrationMessage.getMvnoId());
            if (jwtToken == null || jwtToken.isEmpty()) {
                log.error("Failed to retrieve valid JWT token.");
                return "Failed to retrieve JWT token!";
            }
            DynamicRequestDTO dynamicRequestDTO = getMessafeToDynamicDTO(nmsIntegrationMessage);
            DynamicRequestDTO staticDTO = setDelStaticDto();
            DynamicRequestDTO finalDTO = finalDTO(dynamicRequestDTO, staticDTO);
            ONUResponseDTO onuResponseDTO = onuService.deleteONU(finalDTO, jwtToken, configService, loggedInUserName, nmsIntegrationMessage.getMvnoId());
            String resultId = onuResponseDTO.getResultId();
            if (resultId.equalsIgnoreCase("0")) {
                updateNmsIntegrationMessage(nmsIntegrationMessage, NMSIntegrationConstant.API_CONSTANT.COMPLETED);
                String handleApiResponse = onuService.handleApiResponse(resultId);
                log.info("Delete ONU response message: " + handleApiResponse);
                return handleApiResponse;
            } else {
                updateNmsIntegrationMessage(nmsIntegrationMessage, NMSIntegrationConstant.API_CONSTANT.FAILED);
                String handleApiResponse = onuService.handleApiResponse(resultId);
                log.info("Delete ONU response message: " + handleApiResponse);
                return handleApiResponse;
            }
        } catch (Exception ex) {
            log.error("Error during delete onu for nms integration: {}", ex.getMessage(), ex);
            return "Error during delete onu for nms integration";
        }
    }

    private Optional<NmsIntegration> getNmsIntegrationPresent(NMSIntegrationMessage nmsIntegrationMessage) {
        List<NmsIntegration> nmsIntegrationList = nnmIntegrationRepository.findAll();
        Optional<NmsIntegration> optionalNmsIntegration = null;
        if (!nmsIntegrationList.isEmpty()) {
            optionalNmsIntegration = nmsIntegrationList.stream()
                    .filter(nmsIntegration ->
                            nmsIntegration.getItemId().equals(nmsIntegrationMessage.getItemId()) &&
                                    nmsIntegration.getCustomerId().equals(nmsIntegrationMessage.getCustomerId()) &&
                                    nmsIntegration.getCustInvenId().equals(nmsIntegrationMessage.getCustInvenId()) &&
                                    nmsIntegration.getMvnoId().equals(nmsIntegrationMessage.getMvnoId()) &&
                                    nmsIntegration.getOperation().equals(nmsIntegrationMessage.getOperation()) &&
                                    nmsIntegration.getStatus().equalsIgnoreCase(NMSIntegrationConstant.API_CONSTANT.PENDING)
                    )
                    .findFirst();
        }
        return optionalNmsIntegration;
    }


    public String wifiConfig(NMSIntegrationMessage nmsIntegrationMessage, String ssidUserName, String ssidPassword, String workingFrequency) {
        log.info("Start to execute Add ONU Integration Method for Serial Number " + nmsIntegrationMessage.getSerialNumber());
        try {
            if (nmsIntegrationMessage == null) {
                log.error("LoginRequestDTO is null. Cannot proceed with NMS service activation.");
                return "Invalid request: LoginRequestDTO is null.";
            }
            Optional<NmsIntegration> optionalNmsIntegration = getNmsIntegrationPresent(nmsIntegrationMessage);
            if (optionalNmsIntegration != null && optionalNmsIntegration.isPresent()) {
                optionalNmsIntegration.get().setStatus(NMSIntegrationConstant.API_CONSTANT.PENDING);
                nnmIntegrationRepository.save(optionalNmsIntegration.get());
            } else {
                /** Save NMS Integration Data */
                saveWifiNmsIntegrationMessage(nmsIntegrationMessage, NMSIntegrationConstant.API_CONSTANT.PENDING, ssidUserName, ssidPassword, workingFrequency);
            }
            Long mvnoId = nmsIntegrationMessage.getMvnoId();
            Connfiguration configService = configRepocitory.findByNameAndMvnoId(nmsIntegrationMessage.getConfigName(), mvnoId.intValue());
            if (configService == null) {
                log.error("Configuration not available for: " + nmsIntegrationMessage.getConfigName());
                return "Base API Config not available!";
            }
            String grantType = NMSIntegrationConstant.API_CONSTANT.GRANTTYPE;
            Optional<StaffUser> staffUserOptional = staffUserRepository.findById(nmsIntegrationMessage.getLoggedInUserId());
            String loggedInUserName = null;
            if (staffUserOptional.isPresent()) {
                loggedInUserName = staffUserOptional.get().getUsername();
            }
            String jwtToken = getValidJwtToken(configService, grantType, loggedInUserName, nmsIntegrationMessage.getMvnoId());
            if (jwtToken == null || jwtToken.isEmpty()) {
                log.error("Failed to retrieve valid JWT token.");
                return "Failed to retrieve JWT token!";
            }
            DynamicRequestDTO dynamicRequestDTO = getMessafeToDynamicDTO(nmsIntegrationMessage);
            addSSIDUserNameParam(nmsIntegrationMessage.getList(), ssidUserName);
            addSSIDUserPasswordParam(nmsIntegrationMessage.getList(), ssidPassword);
            addWorkingFrequencyParam(nmsIntegrationMessage.getList(), workingFrequency);
            DynamicRequestDTO dynamicWANRequestDTO = onuService.createWifiConfigRequest(dynamicRequestDTO, nmsIntegrationMessage.getList(), ssidUserName, ssidPassword, workingFrequency);
            DynamicRequestDTO staticWANDTO = onuService.setWifiConfigStaticDto();
            DynamicRequestDTO finalWANDTO = finalDTO(dynamicWANRequestDTO, staticWANDTO);
            ONUResponseDTO wifiConfigDto = onuService.processWifiConfigRequest(configService.getBaseurl() + ":" + configService.getPort() + "/pon/serviceCfg/wifiServiceCfg",
                    finalWANDTO, HttpMethod.POST,
                    jwtToken, true, loggedInUserName, mvnoId,
                    NMSIntegrationConstant.API_CONSTANT.POST);
            String resultId = wifiConfigDto.getResultId();
            if (resultId.equalsIgnoreCase("0")) {
                updateNmsIntegrationMessage(nmsIntegrationMessage, NMSIntegrationConstant.API_CONSTANT.COMPLETED);
                String handleApiResponse = onuService.handleApiResponse(resultId);
                log.info("Wifi Config API message: " + handleApiResponse);
                return handleApiResponse;
            } else {
                updateNmsIntegrationMessage(nmsIntegrationMessage, NMSIntegrationConstant.API_CONSTANT.FAILED);
                String handleApiResponse = onuService.handleApiResponse(resultId);
                log.info("Wifi Config API message: " + handleApiResponse);
                return handleApiResponse;
            }
        } catch (Exception ex) {
            log.error("Error during wifi configuration for nms integration: {}", ex.getMessage(), ex);
            return "Error during wifi configuration for nms integration";
        }
    }

    private void addWorkingFrequencyParam(List<IntegrationSpecificParamDTO> list, String workingFrequency) {
        IntegrationSpecificParamDTO serialNumberParam = new IntegrationSpecificParamDTO();
        serialNumberParam.setParamName(NMSIntegrationConstant.API_CONSTANT.WORKINGFREQUENCY);
        serialNumberParam.setParamValue(workingFrequency);
        list.add(serialNumberParam);
    }

    private void addSSIDUserPasswordParam(List<IntegrationSpecificParamDTO> list, String ssidPassword) {
        IntegrationSpecificParamDTO serialNumberParam = new IntegrationSpecificParamDTO();
        serialNumberParam.setParamName(NMSIntegrationConstant.API_CONSTANT.PRESHAREDKEY);
        serialNumberParam.setParamValue(ssidPassword);
        list.add(serialNumberParam);
    }

    private void addSSIDUserNameParam(List<IntegrationSpecificParamDTO> list, String ssidUserName) {
        IntegrationSpecificParamDTO serialNumberParam = new IntegrationSpecificParamDTO();
        serialNumberParam.setParamName(NMSIntegrationConstant.API_CONSTANT.SSIDNAME);
        serialNumberParam.setParamValue(ssidUserName);
        list.add(serialNumberParam);
    }

    public DynamicRequestDTO setWifiConfigDto() {
        DynamicRequestDTO staticWifi = new DynamicRequestDTO();
        staticWifi.setDynamicField("ONUIDTYPE", "MAC");
        staticWifi.setDynamicField("RADIUS-KEY ", "");
        staticWifi.setDynamicField("WEPKEY1 ", "");
        staticWifi.setDynamicField("WEPKEY2 ", "");
        staticWifi.setDynamicField("WEPKEY3 ", "");
        staticWifi.setDynamicField("WEPKEY4 ", "");
        staticWifi.setIntegerDynamicField("ENABLE  ", 1);
        staticWifi.setIntegerDynamicField("IRELESS-AREA", null);
        staticWifi.setIntegerDynamicField("WIRELESS-CHANNEL", null);
        staticWifi.setIntegerDynamicField("WIRELESS-STANDARD", 8);
        staticWifi.setIntegerDynamicField("WORKING-FREQUENCY", 1);
        staticWifi.setIntegerDynamicField("T-POWER", 28);
        staticWifi.setIntegerDynamicField("SSID ", 1);
        staticWifi.setIntegerDynamicField("SSID-ENABLE ", 1);
        staticWifi.setIntegerDynamicField("SSID-VISIBLE ", null);
        staticWifi.setIntegerDynamicField("AUTH-MODE ", 6);
        staticWifi.setIntegerDynamicField("ENCRYPT-TYPE ", 4);
        staticWifi.setIntegerDynamicField("UPDATEKEY-INTERVAL ", 86400);
        staticWifi.setIntegerDynamicField("RADIUS-PORT ", null);
        staticWifi.setIntegerDynamicField("WEP-ENCRYPTIONLEVEL ", 1);
        staticWifi.setIntegerDynamicField("WEP-KEYINDEX ", 1);
        staticWifi.setIntegerDynamicField("WAP-IPADDRESS  ", null);
        staticWifi.setIntegerDynamicField("WAP-PORT  ", null);
        staticWifi.setIntegerDynamicField("MAX-WIFIMAC-COUNT ", 32);
        staticWifi.setIntegerDynamicField("PUBLICSSID ", null);
        staticWifi.setIntegerDynamicField("KICKSTATIONSWITCH ", null);
        staticWifi.setIntegerDynamicField("LOWERTHRESHOLD ", null);

        return staticWifi;
    }

    public NMSIntegrationMessage getNMSIntegrationMessage(WifiConfigGetDetailDTO wifiConfigGetDetailDTO) {
        try {
            List<NmsIntegration> nmsIntegrationList = nnmIntegrationRepository.findByItemIdAndCustomerIdAndCustInvenIdAndOperationAndStatusAndSerialNumberAndMvnoId(
                    wifiConfigGetDetailDTO.getItemId(),
                    wifiConfigGetDetailDTO.getCustomerId(),
                    wifiConfigGetDetailDTO.getCustInvenId(),
                    NMSIntegrationConstant.API_CONSTANT.ADD_ONU,
                    NMSIntegrationConstant.API_CONSTANT.COMPLETED,
                    wifiConfigGetDetailDTO.getSerialNumber(),
                    getMvnoIdFromCurrentStaff().longValue());
            if (!nmsIntegrationList.isEmpty()) {
                // Get the last element in the list
                NmsIntegration nmsIntegration = nmsIntegrationList.get(nmsIntegrationList.size() - 1);
                List<IntegrationParameters> integrationParameters = integrationParametersRepository.findByIntegrationId(nmsIntegration.getId());
                List<IntegrationSpecificParamDTO> integrationSpecificParamDTOS = new ArrayList<>();
                for (IntegrationParameters parameters : integrationParameters) {
                    IntegrationSpecificParamDTO integrationSpecificParamDTO = new IntegrationSpecificParamDTO();
                    integrationSpecificParamDTO.setParamName(parameters.getParamName());
                    integrationSpecificParamDTO.setParamValue(parameters.getParamValue());
                    integrationSpecificParamDTOS.add(integrationSpecificParamDTO);
                }
                NMSIntegrationMessage dto = new NMSIntegrationMessage();
                dto.setList(integrationSpecificParamDTOS);
                dto.setOperation(NMSIntegrationConstant.API_CONSTANT.WIFI_CONFIG);
                dto.setCustomerId(wifiConfigGetDetailDTO.getCustomerId());
                dto.setItemId(wifiConfigGetDetailDTO.getItemId());
                dto.setConfigName("Inventory");
                dto.setCustInvenId(wifiConfigGetDetailDTO.getCustInvenId());
                dto.setMvnoId(getMvnoIdFromCurrentStaff().longValue());
                dto.setLoggedInUserId(getLoggedInUserId());
                dto.setSerialNumber(wifiConfigGetDetailDTO.getSerialNumber());
                return dto;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error Get NMS Integration: " + e.getMessage(), e);
            throw new RuntimeException("Error Get NMS Integration: " + e.getMessage());
        }
    }

    public int getLoggedInUserId() {
        int loggedInUserId = -1;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUserId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getUserId();
            }
        } catch (Exception e) {
            loggedInUserId = -1;
        }
        return loggedInUserId;
    }

    public Integer getMvnoIdFromCurrentStaff() {
        Integer mvnoId = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                mvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
            }
        } catch (Exception e) {
            //        ApplicationLogger.logger.error("MVNO - getMvnoIdFromCurrentStaff" + e.getMessage(), e);
        }
        return mvnoId;
    }
}



