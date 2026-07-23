//package com.savbill.integrationsystem.nms.service;
//
//import com.savbill.integrationsystem.nms.entity.NMSIntegrationDTO;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.lang.reflect.Field;
//import java.util.Map;
//
//@Service
//public class NMSSchemaService {
//
//    private static final String URL = "http://102.209.109.2:8017/pon/onu";
//
//    public String addOnu(Map<String, Object> requestParams) {
//        try {
//            // Initialize the DTO and dynamically populate its fields from requestParams
//            NMSIntegrationDTO dto = new NMSIntegrationDTO();
//
//            // Dynamically populate the fields of DTO using reflection
//            for (Map.Entry<String, Object> entry : requestParams.entrySet()) {
//                Field field = NMSIntegrationDTO.class.getDeclaredField(entry.getKey());
//                field.setAccessible(true);
//                field.set(dto, entry.getValue());
//            }
//
//            // Prepare headers and body for the API call
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            headers.set("accessToken", (String) requestParams.get("accessToken"));
//
//            // Use Jackson ObjectMapper to convert the DTO to JSON dynamically
//            ObjectMapper objectMapper = new ObjectMapper();
//            String jsonBody = objectMapper.writeValueAsString(dto);
//
//            // Create HTTP entity with headers and body
//            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
//            RestTemplate restTemplate = new RestTemplate();
//
//            // Call the API using RestTemplate
//            ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.POST, entity, String.class);
//
//            // Handle the response
//            if (response.getStatusCode() == HttpStatus.CREATED) {
//                return "ONU added successfully: " + response.getBody();
//            } else {
//                return "ONU addition failed with status: " + response.getStatusCode();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Error occurred while adding ONU: " + e.getMessage();
//        }
//    }
//
//
//    public String deleteOnu(NMSIntegrationDTO requestDTO) {
//        try {
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("accessToken", requestDTO.getAccessToken());
//
//            // Dynamically build the URL with query parameters
//            StringBuilder deleteUrl = new StringBuilder(URL);
//            deleteUrl.append("?");
//
//            ObjectMapper objectMapper = new ObjectMapper();
//            Map<String, Object> params = objectMapper.convertValue(requestDTO, Map.class);
//
//            for (Map.Entry<String, Object> entry : params.entrySet()) {
//                if (entry.getValue() != null) {
//                    deleteUrl.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
//                }
//            }
//
//            // Remove the trailing '&' or '?' if no parameters were appended
//            if (deleteUrl.charAt(deleteUrl.length() - 1) == '&' || deleteUrl.charAt(deleteUrl.length() - 1) == '?') {
//                deleteUrl.deleteCharAt(deleteUrl.length() - 1);
//            }
//
//            // Create the HTTP entity with headers (no body needed for DELETE)
//            HttpEntity<Void> entity = new HttpEntity<>(headers);
//            RestTemplate restTemplate = new RestTemplate();
//
//
//            ResponseEntity<String> response = restTemplate.exchange(deleteUrl.toString(), HttpMethod.DELETE, entity, String.class);
//
//
//            if (response.getStatusCode() == HttpStatus.OK) {
//                return "ONU deleted successfully: " + response.getBody();
//            } else {
//                return "ONU deletion failed with status: " + response.getStatusCode();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Error occurred while deleting ONU: " + e.getMessage();
//        }
//    }
//
//}
