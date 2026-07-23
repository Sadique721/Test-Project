package com.savbill.integrationsystem.TradelanceIntigration;

import com.savbill.integrationsystem.AirtelAppToCRM.RequestDTO.AirtelAppToCRMDTO;
import com.savbill.integrationsystem.AirtelAppToCRM.RequestDTO.AirtelCRMRequestDTO;
import com.savbill.integrationsystem.AirtelAppToCRM.constant.AirtelValidateConstant;
import com.savbill.integrationsystem.AirtelAppToCRM.service.AirtelValidateTxServiceImpl;
import com.savbill.integrationsystem.Migration.CMSClient;
import com.savbill.integrationsystem.NewNMSIntegration.constants.NMSIntegrationConstant;
import com.savbill.integrationsystem.NewNMSIntegration.dto.WifiConfigGetDetailDTO;
import com.savbill.integrationsystem.NewNMSIntegration.entity.NmsIntegration;
import com.savbill.integrationsystem.NewNMSIntegration.message.NMSIntegrationMessage;
import com.savbill.integrationsystem.NewNMSIntegration.repository.IntegrationParametersRepository;
import com.savbill.integrationsystem.NewNMSIntegration.repository.NnmIntegrationRepository;
import com.savbill.integrationsystem.NewNMSIntegration.service.APIIntegrationService;
import com.savbill.integrationsystem.PaymentIntegration.Model.CustomerPayment;
import com.savbill.integrationsystem.PaymentIntegration.Repository.CustomerPaymentRepository;
import com.savbill.integrationsystem.PaymentIntegration.Service.CustomerPaymentService;
import com.savbill.integrationsystem.PaymentIntegration.Service.PaymentIntegrationService;
import com.savbill.integrationsystem.PaywayIntigration.ErrorResponseDTO;
import com.savbill.integrationsystem.apiAudits.NMSServices.ApiAuditsService;
import com.savbill.integrationsystem.core.security.constants.LogConstants;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.core.utillity.APIConstants;
import com.savbill.integrationsystem.core.utillity.log.ApplicationLogger;
import com.savbill.integrationsystem.kafka.KafkaMessageData;
import com.savbill.integrationsystem.kafka.KafkaMessageSender;
import com.savbill.integrationsystem.mvno.RevenueClient;
import com.savbill.integrationsystem.rabbitmq.CustPayDTOMessage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TradelanceService {
    private final Logger logger = LoggerFactory.getLogger(TradelanceService.class);
    @Autowired
    private CustomerPaymentRepository customerPaymentRepository;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;
    @Autowired
    private CustomerPaymentService customerPaymentService;
    @Autowired
    private CMSClient client;
    @Autowired
    private AirtelValidateTxServiceImpl airtelValidateTxService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private NnmIntegrationRepository nnmIntegrationRepository;
    @Autowired
    private IntegrationParametersRepository integrationParametersRepository;
    @Autowired
    private APIIntegrationService apiIntegrationService;
    @Autowired
    private AirtelValidateTxServiceImpl validateTxService;
    @Autowired
    private RevenueClient revenueClient;
    @Value("${tradelance.callback-url}")
    private String tradelanceCallBackURL;
    @Autowired
    private ApiAuditsService apiAuditsService;
    @Autowired
    private PaymentIntegrationService paymentIntegrationService;

    public ResponseEntity<?> processForwardPayment(ForWardPaymentRequest paymentRequest, HttpServletRequest request) {
        logger.info("processForwardPayment method start - TransactionId: {}, AccountNo: {}", paymentRequest.getTransactionId(), paymentRequest.getAccountNo());
        Map<String, Object> response = new HashMap<String, Object>();
        String authToken = request.getHeader("apikey");
        if (authToken == null || authToken.isEmpty()) {
            logger.warn("Authorization token is missing in the request");
            response.put("error", "Authorization failed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        if ((paymentRequest.getAccountNo() == null || paymentRequest.getAccountNo().isEmpty())
                || (paymentRequest.getTransactionId() == null || paymentRequest.getTransactionId().isEmpty())) {
            logger.warn("Invalid request data: AccountNo or TransactionId is null");
            response.put("error", "Bad Request. Correct clientname and apikey are required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            logger.info("Checking for existing transaction with ID: {}", paymentRequest.getTransactionId());
            List<CustomerPayment> existingPayments = customerPaymentRepository.findAllByPgTransactionId(paymentRequest.getTransactionId());
            if (!existingPayments.isEmpty()) {
                logger.warn("Duplicate transaction detected for TransactionId: {}", paymentRequest.getTransactionId());
                response.put("message", "Transaction received");
                response.put("StatusCode", "DUPLICATE – Payment with same ID already exists");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }

            logger.info("Fetching customer details for AccountNo: {}", paymentRequest.getAccountNo());
            ResponseEntity<List<AirtelAppToCRMDTO>> customerResponse = client.getcustomersByAccNumber(paymentRequest.getAccountNo(), authToken);
            List<AirtelAppToCRMDTO> customers = customerResponse.getBody();

            Map<Integer, AirtelAppToCRMDTO> customerMap = customers.stream()
                    .collect(Collectors.toMap(AirtelAppToCRMDTO::getCustId, Function.identity()));


            AirtelAppToCRMDTO customer = customerMap.values().stream().findFirst()
                    .orElseThrow(() -> new RuntimeException("Account not found"));
            validateMobileNumber(customerMap, paymentRequest.getPhoneNumber());
            Long orderId = airtelValidateTxService.generateId(customer.getCustId().longValue());
            CustPayDTOMessage custPayDTOMessage = new CustPayDTOMessage();
            custPayDTOMessage.setOrderId(orderId);
            custPayDTOMessage.setCustomerUsername(customer.getUsername());
            custPayDTOMessage.setStatus(paymentRequest.getStatus());
            custPayDTOMessage.setGatewayStatus(paymentRequest.getStatus());
            custPayDTOMessage.setAccountNumber(paymentRequest.getAccountNo());
            custPayDTOMessage.setCustId(customer.getCustId());
            custPayDTOMessage.setPayment(paymentRequest.getAmount());
            custPayDTOMessage.setPgTransactionId(paymentRequest.getTransactionId());
            custPayDTOMessage.setBuid(customer.getBuId());
            custPayDTOMessage.setMvnoid(customer.getMvnoId());
            custPayDTOMessage.setMerchantName(AirtelValidateConstant.TRADELANCE_PAYMENT + "_" + paymentRequest.getChannel());
            custPayDTOMessage.setPaymentGatewayName(AirtelValidateConstant.TRADELANCE_PAYMENT + "_" + paymentRequest.getChannel());
            custPayDTOMessage.setPaymentDate(LocalDateTime.now().toString());
            custPayDTOMessage.setTransactionDate(LocalDateTime.now().toString());
            custPayDTOMessage.setPayerMobileNumber(paymentRequest.getPhoneNumber());

            logger.info("Saving payment transaction for OrderId: {}", orderId);
            CustomerPayment customerPayment = customerPaymentService.convertMessageToEntity(custPayDTOMessage);
//            customerPayment.setId(airtelValidateTxService.getLatestId());
            customerPayment.setCreatedById(jwtUtil.getLoggedInUser().getUserId());
            customerPayment.setCreatedByName(jwtUtil.getLoggedInUser().getUsername());

//            logger.info("Sending payment data to Kafka for TransactionId: {}", paymentRequest.getTransactionId());
//            kafkaMessageSender.send(new KafkaMessageData(custPayDTOMessage, custPayDTOMessage.getClass().getSimpleName()));

            customerPayment = customerPaymentRepository.save(customerPayment);

            boolean isRecordSaved = false;
            AirtelCRMRequestDTO airtelCRMRequestDTO = new AirtelCRMRequestDTO();
            airtelCRMRequestDTO.setAirtelAppToCRMDTO(customer);
            airtelCRMRequestDTO.setCustPayDTOMessage(custPayDTOMessage);
            isRecordSaved = client.addCustomerPayment(custPayDTOMessage, authToken);

            if (isRecordSaved) {
                paymentIntegrationService.updateStatusAndSendToCMS(orderId.toString(), paymentRequest.getTransactionId(), "SUCCESSFUL", null);
                customerPayment.setStatus(AirtelValidateConstant.SUCCESSFUL);
                customerPaymentRepository.save(customerPayment);
                custPayDTOMessage.setStatus(AirtelValidateConstant.SUCCESSFUL);
                custPayDTOMessage.setTransactionDate(LocalDateTime.now().toString());
                ApplicationLogger.logger.info("Send SUCCESSFUL Request of Payway Data to CMS for referenceId: " + customerPayment.getOrderId());
                kafkaMessageSender.send(new KafkaMessageData(custPayDTOMessage, custPayDTOMessage.getClass().getSimpleName()));
                response.put("message", "Transaction received");
                response.put("statusCode", "SUCCESS – Payment processed successfully");
            } else {
                custPayDTOMessage.setTransactionDate(LocalDateTime.now().toString());
                ApplicationLogger.logger.info("Send Failed Request of Payway Data to CMS for referenceId: " + customerPayment.getOrderId());
                kafkaMessageSender.send(new KafkaMessageData(custPayDTOMessage, custPayDTOMessage.getClass().getSimpleName()));
                response.put("message", "Transaction received");
                response.put("statusCode", "FAILED – Payment Processing failed");
            }
            logger.info("processForwardPayment method completed for TransactionId: {}", customerPayment.getOrderId());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid argument exception: {}", e.getMessage(), e);
            String errorMessage = e.getMessage();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(errorMessage));
        } catch (FeignException e) {
            String errorMessage = extractErrorMessageFromFeignException(e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(errorMessage));
        } catch (DataAccessException e) {
            logger.error("Database access error: {}", e.getMessage(), e);
            String errorMessage = e.getMessage();
            response.put("error", "Something went wrong. Please try again later");
            response.put("message", errorMessage);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(response.toString()));
        } catch (Exception e) {
            logger.error("Unexpected error in processForwardPayment: {}", e.getMessage(), e);
            String errorMessage = e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(errorMessage));
        }
    }

    private String extractErrorMessage(FeignException e) {
        try {
            if (e.responseBody().isPresent()) {
                byte[] byteArray = new byte[e.responseBody().get().remaining()];
                e.responseBody().get().get(byteArray);
                String bodyStr = new String(byteArray, StandardCharsets.UTF_8);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode errorNode = objectMapper.readTree(bodyStr);
                if (errorNode.has("error")) {
                    return errorNode.get("error").asText();
                } else {
                    return "Unknown error occurred";
                }
            } else {
                return "Error response body is empty or null";
            }
        } catch (IOException ioException) {
            return "Error decoding response body: " + ioException.getMessage();
        }
    }

    public ResponseEntity<?> changeWiFiSSIDPassword(@Valid ChangeWiFiPasswordRequest paymentRequest, String apikey) {
        logger.info("changeWiFiSSIDPassword method start - AccountNo: {}", paymentRequest.getAccountNo());
        Map<String, Object> response = new HashMap<>();

        try {
            logger.info("Fetching customer details for AccountNo: {}", paymentRequest.getAccountNo());
            ResponseEntity<List<AirtelAppToCRMDTO>> customerResponse = client.getcustomersByAccNumber(paymentRequest.getAccountNo(), apikey);
            List<AirtelAppToCRMDTO> customers = customerResponse.getBody();

            if (customers == null || customers.isEmpty()) {
                logger.warn("No customer found for AccountNo: {}", paymentRequest.getAccountNo());
                response.put("error", "Account not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            long customerId = customers.get(0).getCustId().longValue();
            logger.info("Customer found - CustId: {}", customerId);

            logger.info("Fetching NMS Integration details for CustomerId: {}", customerId);
            List<NmsIntegration> nmsIntegrationList = nnmIntegrationRepository.findAllNmsIntegrationsByCustomerIdAndOperationAndStatusOrderByIdDesc(customerId, NMSIntegrationConstant.API_CONSTANT.ADD_ONU, NMSIntegrationConstant.API_CONSTANT.COMPLETED);

            if (nmsIntegrationList.isEmpty()) {
                logger.warn("No completed NMS integrations found for CustomerId: {}", customerId);
                response.put("error", "Something went wrong. Please try again later");
                response.put("message", "No completed NMSIntegrations found for customer id");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            List<WifiConfigGetDetailDTO> wifiConfigDetails = nmsIntegrationList.stream()
                    .map(nmsintegration -> {
                        WifiConfigGetDetailDTO dto = new WifiConfigGetDetailDTO();
                        dto.setSsidPassword(paymentRequest.getPassword());
                        dto.setSsidUsername(paymentRequest.getSsid());
                        dto.setItemId(nmsintegration.getItemId());
                        dto.setCustomerId(customerId);
                        dto.setCustInvenId(nmsintegration.getCustInvenId());
                        dto.setSerialNumber(nmsintegration.getSerialNumber());
                        return dto;
                    })
                    .collect(Collectors.toList());

            logger.info("Preparing WiFi configuration update for {} devices", wifiConfigDetails.size());

            for (WifiConfigGetDetailDTO wifiConfigGetDetailDTO : wifiConfigDetails) {
                logger.info("Processing WiFi config update for SerialNumber: {}", wifiConfigGetDetailDTO.getSerialNumber());

                NMSIntegrationMessage nmsIntegrationMessage = apiIntegrationService.getNMSIntegrationMessage(wifiConfigGetDetailDTO);
                if (nmsIntegrationMessage == null) {
                    logger.error("Failed to retrieve NMSIntegrationMessage for SerialNumber: {}", wifiConfigGetDetailDTO.getSerialNumber());
                    response.put("error", "Something went wrong. Please try again later");
                    response.put("message", "NMSIntegrationMessage not found.");
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                }

                logger.info("Sending WiFi config update request to NMS for SerialNumber: {}", wifiConfigGetDetailDTO.getSerialNumber());
                apiIntegrationService.wifiConfig(nmsIntegrationMessage, wifiConfigGetDetailDTO.getSsidUsername(), wifiConfigGetDetailDTO.getSsidPassword(), wifiConfigGetDetailDTO.getWorkingFrequency());
                logger.info("WiFi config update successful for SerialNumber: {}", wifiConfigGetDetailDTO.getSerialNumber());
            }

            response.put("message", "Message confirming successful update of customer device WiFi");
            logger.info("WiFi configuration update completed successfully for AccountNo: {}", paymentRequest.getAccountNo());
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (FeignException fe) {
            logger.error("FeignException: Error while calling Feign Client - Status: {}, Message: {}", fe.status(), fe.getMessage());
            response.put("error", "Sorry. The operation could not be completed. Please contact customer support for assistance");
            response.put("message", "Service unavailable. Please try again later.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO("Service is temporarily unavailable."));

        } catch (HttpClientErrorException httpClientErrorException) {
            logger.error("HttpClientErrorException: Client error - Status: {}, Message: {}", httpClientErrorException.getStatusCode(), httpClientErrorException.getMessage());
            response.put("error", "Something went wrong. Please try again later");
            response.put("message", "Sorry. The operation could not be completed. Please contact customer support for assistance");
            return ResponseEntity.status(httpClientErrorException.getStatusCode()).body(new ErrorResponseDTO(httpClientErrorException.getMessage()));

        } catch (Exception e) {
            logger.error("Exception occurred while processing WiFi configuration update: {}", e.getMessage(), e);
            response.put("error", "Something went wrong. Please try again later");
            response.put("message", "Sorry. The operation could not be completed. Please contact customer support for assistance");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private ResponseEntity<?> validateMobileNumber(Map<Integer, AirtelAppToCRMDTO> customerData, String phoneNumber) {
        AirtelAppToCRMDTO customer = customerData.values().stream().findFirst().orElse(null);

        if (customer == null || customer.getMobileNumber() == null || customer.getMobileNumber().isEmpty()) {
            throw new IllegalArgumentException("No valid mobile number found in customer data.");
        }
        //Below code remove Reason is Add Regex , startLine--->
//        String fetchedMobileNo = customer.getMobileNumber();
//        String s1 = String.valueOf(phoneNumber.length());
//        if (!fetchedMobileNo.equals(s1)) {
//            throw new IllegalArgumentException("Invalid mobile number. Input value in Digit : " + fetchedMobileNo);
//        } else {
//            customerData.remove(customer.getMobileNumber());
//            return ResponseEntity.ok(customerData);
//        }
        //<<----closeLine
        String normalizedPhoneNumber = phoneNumber != null ? phoneNumber.trim() : phoneNumber;
        if (normalizedPhoneNumber==null) {
            throw new IllegalArgumentException("Invalid mobile number.");
        }
        //String regexPattern = customer.getMobileNumber();
        if ((normalizedPhoneNumber.startsWith("256") && normalizedPhoneNumber.length() == 12) ||
                (normalizedPhoneNumber.startsWith("+256") && normalizedPhoneNumber.length() == 13)) {
            normalizedPhoneNumber = normalizedPhoneNumber.replaceFirst("^\\+?256", "");
        }

        String regexPattern = customer.getMobileNumber().replace("\\\\", "\\");
        if (!normalizedPhoneNumber.matches(regexPattern)) {
            throw new IllegalArgumentException("Invalid mobile number. Input '" + phoneNumber +
                    "' does not match regex pattern: " + regexPattern);
        }
        return ResponseEntity.ok(customerData);
    }

    private String extractErrorMessageFromFeignException(FeignException ex) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode errorJson = objectMapper.readTree(ex.contentUTF8());
            if (errorJson.has("trace")) {
                String trace = errorJson.get("trace").asText();
                String extractedMessage = extractCustomValidationMessage(trace);
                if (extractedMessage != null) {
                    return extractedMessage;
                }
            }
            if (errorJson.has("error")) {
                return errorJson.get("error").asText();
            } else if (errorJson.has("message")) {
                return errorJson.get("message").asText();
            }
        } catch (Exception e) {
            logger.error("Error extracting message from FeignException: {}", e.getMessage());
        }

        return "An error occurred while processing the request";
    }

    private String extractCustomValidationMessage(String trace) {
        try {
            Pattern pattern = Pattern.compile("CustomValidationException: (.*?)(\\r|\\n|$)");
            Matcher matcher = pattern.matcher(trace);
            if (matcher.find()) {
                return matcher.group(1).trim();
            }
        } catch (Exception e) {
            logger.error("Error extracting CustomValidationException message: {}", e.getMessage());
        }
        return null;
    }

    public void sendPaymentStatus(String status, String refId, String transactionId, double amount, String accountNo, Integer mvnoId) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("Status", status);
        requestBody.put("RefId", refId);
        requestBody.put("TransactionId", transactionId);
        requestBody.put("Amount", amount);
        requestBody.put("AccountNo", accountNo);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

//        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {


            ObjectMapper mapper = new ObjectMapper();
            String payload = mapper.writeValueAsString(requestBody);
            if(status.equalsIgnoreCase("Successful")) {
                sendHttpPostRequest(tradelanceCallBackURL, payload, null, mvnoId, transactionId);
            }else{
                logger.info("Ignore tradelance call back for status: {} refId:{} transactionId:{} accountNo:{} mvnoId:{}",status,refId,transactionId,accountNo,mvnoId);
            }

        } catch (Exception e) {
            System.err.println("Error sending payment status: " + e.getMessage());
        }
    }

    public String sendHttpPostRequest(String endPoint, String payload, String authorization, Integer mvnoId,String referenceNumber) {
        CloseableHttpResponse response = null;
        String finalResponse = "";
        try {
            LocalDateTime requestInitiationTime = LocalDateTime.now();
            CloseableHttpClient client = HttpClients.createDefault();
            ApplicationLogger.logger.warn("Tradlance endPoint ::::::::::: {}", endPoint);
            HttpPost httpPost = new HttpPost(endPoint);

            if (authorization != null) {
                httpPost.setHeader("Authorization", authorization);
            }
            httpPost.setHeader("Content-Type", "application/json");
            ApplicationLogger.logger.warn("::::::: Tradlance payload ::::::::::: {}", payload);
            StringEntity entity = new StringEntity(payload);
            entity.setContentType("application/json");
            httpPost.setEntity(entity);

            response = client.execute(httpPost);
            ApplicationLogger.logger.warn("::::::: Tradlance response ::::::::::: {}", response);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);


            org.apache.http.HttpEntity responseEntity = response.getEntity();
            ApplicationLogger.logger.warn("::::::: Tradlance responseEntity ::::::::::: {}", responseEntity);
            String responseBody = EntityUtils.toString(responseEntity, "UTF-8");
            ApplicationLogger.logger.warn("::::::: Tradlance responseBody ::::::::::: {}", responseBody);
            finalResponse = responseBody;
            String errorMessage = null;
            if (response != null) {
                Integer response_code = response.getStatusLine().getStatusCode();
                if (response_code == 200) {
//                    JSONObject responseObject = null;
//                    if (!responseBody.isEmpty()){
//                        responseObject = new JSONObject(responseBody.toString());
//                    }
//                    if (responseObject != null && responseObject.has("error")) {
//                        errorMessage = responseObject.getString("error");
//                        ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "API " + LogConstants.REQUEST_FOR + "Error while Performing Request To Payload: with status " + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + errorMessage + LogConstants.LOG_STATUS_CODE + response_code);
//                    } else {
//                        ApplicationLogger.logger.info(LogConstants.REQUEST_FROM + "Payload API " + LogConstants.REQUEST_FOR + responseObject + LogConstants.REQUEST_BY + "SuperAdmin" + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
//                    }
                    apiAuditsService.extractDataAndSavePostApiAudits(endPoint, null, response, httpPost, null, responseTime, errorMessage, requestInitiationTime, responseBody, String.valueOf(""), mvnoId, APIConstants.TRADELANCE,referenceNumber);
                } else {
                    errorMessage = response.toString();
                    apiAuditsService.extractDataAndSavePostApiAudits(endPoint, null, response, httpPost, null, responseTime, errorMessage, requestInitiationTime, responseBody, String.valueOf(""), mvnoId, APIConstants.TRADELANCE,referenceNumber);
                    ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "API " + LogConstants.REQUEST_FOR + "Error while Performing Request To Payload: with status " + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + errorMessage + LogConstants.LOG_STATUS_CODE + response_code);
                }
            }
            response.close();
        } catch (Exception e) {
            e.printStackTrace();
            ApplicationLogger.logger.error("Error While Performing Request To Send ISP Invoice Payload", e.getMessage());
        }
        return finalResponse;
    }

}
