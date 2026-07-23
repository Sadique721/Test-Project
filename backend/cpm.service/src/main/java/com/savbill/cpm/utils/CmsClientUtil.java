package com.savbill.cpm.utils;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.kafka.KafkaMessageData;
import com.savbill.cpm.kafka.KafkaMessageSender;
import com.savbill.cpm.model.postpaid.CustPlanMappping;
import com.savbill.cpm.model.postpaid.CustPlanMapppingRepository;
import com.savbill.cpm.model.postpaid.CustQuotaDetails;
import com.savbill.cpm.modules.InventoryManagement.CustomerInventoryMapping.CustInvParams;
import com.savbill.cpm.modules.InventoryManagement.CustomerInventoryMapping.CustInvParamsDto;
import com.savbill.cpm.modules.InventoryManagement.CustomerInventoryMapping.CustInvParamsMapper;
import com.savbill.cpm.modules.InventoryManagement.CustomerInventoryMapping.CustInvParamsRepo;
import com.savbill.cpm.rabbitMq.MessageSender;
import com.savbill.cpm.repository.common.CustQuotaRepository;
import com.savbill.cpm.service.feignService.SpecificParameterInventoryConsumer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class CmsClientUtil {
    private final Logger log = LoggerFactory.getLogger(CmsClientUtil.class);

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private SpecificParameterInventoryConsumer consumer;

    @Autowired
    private MessageSender messageSender;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    private CustInvParamsRepo custInvParamsRepo;

    @Autowired
    private CustInvParamsMapper custInvParamsMapper;
    @Autowired
    private CustQuotaRepository custQuotaRepository;

    @Autowired
    private CustPlanMapppingRepository custPlanMapppingRepository;




    private final ObjectMapper objectMapper = new ObjectMapper();



    public ResponseEntity<?> getInventoryParameterByCust(List<Long> invMappIds, HttpServletRequest req) {


        InvenotryCustParamsDto custParamsDto = new InvenotryCustParamsDto();
        custParamsDto.setSerializedItemIds(invMappIds);

        RestTemplate restTemplate = new RestTemplate();
        // Specify the API endpoint URL
        String baseUrl = getMicroserviceDetails("SAVBILLINVENTORYMANAGEMENT-SERVICE");
        String apiUrl = baseUrl + "api/v1/SavbillInventoryManagement/inventorySpecification/customerParam";

        // Set up headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", req.getHeader("Authorization"));
        headers.setContentType(MediaType.APPLICATION_JSON);
        log.info("Request for Invenotory Param URL: "+apiUrl+" payload: "+custParamsDto.toString());
        // Create HttpEntity with headers and request body
        HttpEntity<InvenotryCustParamsDto> httpEntity = new HttpEntity<>(custParamsDto, headers);

        try {
            // Make the POST request using exchange for more flexibility
            ResponseEntity<String> responseEntityStr = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    httpEntity,
                    String.class
            );

            log.info("Request for Invenotory Param URL: "+apiUrl+" Resposne: "+responseEntityStr);
            // Process the response
            if (responseEntityStr.getStatusCode().is2xxSuccessful()) {
                // The request was successful, handle the response
                JsonNode root = objectMapper.readTree(responseEntityStr.getBody());
                System.out.println("Success - " + responseEntityStr.getStatusCodeValue() + " - " + root.toString());
                return ResponseEntity.ok(root);
            } else {
                // Handle the error response
                String errorMessage = responseEntityStr.getBody();
                String statusCodeWithMessage = responseEntityStr.getStatusCode() + " - " + errorMessage;
                System.err.println(statusCodeWithMessage);
                return ResponseEntity.status(responseEntityStr.getStatusCode()).body(errorMessage);
            }
        } catch (RestClientException | IOException e) {
            // Handle RestClientException (e.g., timeout, connectivity issues)
            System.err.println("Error making API call: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error making API call");
        }
    }

    public List<CustInvParamsDto> getCustInvParamsByCustSerMappId(Integer custServiceId) {
        List<CustInvParamsDto> custInvParamsDtos = new ArrayList<>();
        List<CustInvParams> custInvParams = custInvParamsRepo.findAllByCustSerMapId(Long.valueOf(custServiceId));
        if(!CollectionUtils.isEmpty(custInvParams)) {
            custInvParamsDtos = custInvParamsMapper.domainToDTO(custInvParams, new CycleAvoidingMappingContext());
        }
        return custInvParamsDtos;
    }

    public String getMicroserviceDetails(String appName) {
        StringBuilder details = new StringBuilder();
        String url = "";
        // Get application details
        List<ServiceInstance> serviceInstance = discoveryClient.getInstances(appName);
        if(!org.springframework.util.CollectionUtils.isEmpty(serviceInstance)) {
            url = ((EurekaDiscoveryClient.EurekaServiceInstance) serviceInstance.get(0)).getInstanceInfo().getHomePageUrl();
        }

        return url;
    }


    public ResponseEntity<?> sendInventoryDataToIntegration(List<CustInvParamsDto> custInvParamsDtos, Integer custId, Integer custServiceMapId, String username, String getLoggedInUser, Integer getLoggedInMvnoId) {
        // Create RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        // Create NMSServiceActivationDTO
        NMSServiceActivationDTO nmsServiceActivationDTO = new NMSServiceActivationDTO();

        nmsServiceActivationDTO.setConfigName("SAVBILL-NMS");
        nmsServiceActivationDTO.setCustId(custId);
        CustPlanMappping custPlanMappping=custPlanMapppingRepository.findByCustServiceMappingId(custServiceMapId);
        CustQuotaDetails custQuotaDetails=custQuotaRepository.findByCustPlanMappping_Id(custPlanMappping.getId());
        nmsServiceActivationDTO.setCustServiceMapId(custServiceMapId);
        nmsServiceActivationDTO.setUsername(getLoggedInUser);
        nmsServiceActivationDTO.setMvnoId(getLoggedInMvnoId);
        CustInvParamsDto connection_name = new CustInvParamsDto();
        connection_name.setParamName("Connection_name");
        connection_name.setParamValue(username);
        custInvParamsDtos.add(connection_name);
        if(custQuotaDetails.getUpstreamprofileuid()!=null && custQuotaDetails.getDownstreamprofileuid()!=null ) {
            nmsServiceActivationDTO.setUpstreamprofileuuid(custQuotaDetails.getUpstreamprofileuid());
            nmsServiceActivationDTO.setDownstreamprofileuuid(custQuotaDetails.getDownstreamprofileuid());
        }
        nmsServiceActivationDTO.setCustInvParams(custInvParamsDtos);
        try {
            kafkaMessageSender.send(new KafkaMessageData(nmsServiceActivationDTO,NMSServiceActivationDTO.class.getSimpleName()));
//            messageSender.send(nmsServiceActivationDTO, RabbitMqConstants.QUEUE_CMS_CONFIGURATION_INTIGRATION);
        } catch (Exception e) {
            // Handle RestClientException (e.g., timeout, connectivity issues)
            System.err.println("Error making API call: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error making API call");
        }
        return null;
    }

    public ResponseEntity<?> sendInventoryDataToIntegration1(List<ProductParameterDefaultValueMappingDTO> productParameterDefaultValueMappingDTO, HttpServletRequest request) {
        // Create RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();



        // Create NMSServiceActivationDTO
        NMSServiceActivationDTO nmsServiceActivationDTO = new NMSServiceActivationDTO();
        nmsServiceActivationDTO.setConfigName("SAVBILL-NMS");
        nmsServiceActivationDTO.setParameters(productParameterDefaultValueMappingDTO);


        // Specify the API endpoint URL
        String baseUrl = getMicroserviceDetails("SAVBILLINTEGRATIONSYSTEM-SERVICE");
        String apiUrl = baseUrl + "SavbillIntegrationSystem/nmsMaster/activateService";

        // Set up headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", request.getHeader("Authorization"));
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create HttpEntity with headers and request body
        HttpEntity<NMSServiceActivationDTO> httpEntity = new HttpEntity<>(nmsServiceActivationDTO, headers);

        try {
            // Make the POST request using exchange for more flexibility
            ResponseEntity<String> responseEntityStr = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    httpEntity,
                    String.class
            );

            // Process the response
            if (responseEntityStr.getStatusCode().is2xxSuccessful()) {
                // The request was successful, handle the response
                JsonNode root = objectMapper.readTree(responseEntityStr.getBody());
                System.out.println("Success - " + responseEntityStr.getStatusCodeValue() + " - " + root.toString());
                return ResponseEntity.ok(root);
            } else {
                // Handle the error response
                String errorMessage = responseEntityStr.getBody();
                String statusCodeWithMessage = responseEntityStr.getStatusCode() + " - " + errorMessage;
                System.err.println(statusCodeWithMessage);
                return ResponseEntity.status(responseEntityStr.getStatusCode()).body(errorMessage);
            }
        } catch (RestClientException | IOException e) {
            // Handle RestClientException (e.g., timeout, connectivity issues)
            System.err.println("Error making API call: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error making API call");
        }
    }



}
