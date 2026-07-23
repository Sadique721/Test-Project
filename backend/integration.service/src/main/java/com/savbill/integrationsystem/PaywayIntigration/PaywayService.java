package com.savbill.integrationsystem.PaywayIntigration;

import com.savbill.integrationsystem.AirtelAppToCRM.RequestDTO.AirtelAppToCRMDTO;
import com.savbill.integrationsystem.AirtelAppToCRM.RequestDTO.AirtelCRMRequestDTO;
import com.savbill.integrationsystem.AirtelAppToCRM.constant.AirtelValidateConstant;
import com.savbill.integrationsystem.AirtelAppToCRM.service.AirtelValidateTxServiceImpl;
import com.savbill.integrationsystem.Migration.CMSClient;
import com.savbill.integrationsystem.PaymentIntegration.DTO.OnlineInvoicePaymentDTO;
import com.savbill.integrationsystem.PaymentIntegration.Model.CustomerPayment;
import com.savbill.integrationsystem.PaymentIntegration.Repository.CustomerPaymentRepository;
import com.savbill.integrationsystem.PaymentIntegration.Service.CustomerPaymentService;
import com.savbill.integrationsystem.PaymentIntegration.Service.PaymentIntegrationService;
import com.savbill.integrationsystem.TradelanceIntigration.ForWardPaymentRequest;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.kafka.KafkaMessageSender;
import com.savbill.integrationsystem.mvno.RevenueClient;
import com.savbill.integrationsystem.rabbitmq.CustPayDTOMessage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.codec.DecodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class PaywayService {
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
    private AirtelValidateTxServiceImpl validateTxService;

    @Autowired
    private RevenueClient revenueClient;

    @Autowired
    private PaymentIntegrationService paymentIntegrationService;

    private final Logger logger = LoggerFactory.getLogger(PaywayService.class);

    public ResponseEntity<?> processForwardPayment(ForWardPaymentRequest paymentRequest, String apikey) {
        logger.info("processForwardPayment method start - TransactionId: {}, AccountNo: {}", paymentRequest.getTransactionId(), paymentRequest.getAccountNo());
        Map<String, Object> response = new HashMap<String, Object>();
        if (paymentRequest.getAccountNo() == null || paymentRequest.getAccountNo().isEmpty()
                || paymentRequest.getTransactionId() == null || paymentRequest.getTransactionId().isEmpty()) {
            logger.warn("Invalid request data: AccountNo or TransactionId is null");
            response.put("error", "AccountNo and PhoneNumber parameters are required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            logger.info("Checking for existing transaction with ID: {}", paymentRequest.getTransactionId());
            List<CustomerPayment> existingPayments = customerPaymentRepository.findAllByPgTransactionId(paymentRequest.getTransactionId());
            if (!existingPayments.isEmpty()) {
                logger.warn("Duplicate transaction detected for TransactionId: {}", paymentRequest.getTransactionId());
                response.put("message", "Transaction received");
                response.put("StatusCode", "DUPLICATE");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }

            logger.info("Fetching customer details for AccountNo: {}", paymentRequest.getAccountNo());
            ResponseEntity<List<AirtelAppToCRMDTO>> customerResponse = client.getcustomersByAccNumber(paymentRequest.getAccountNo(), apikey);
            List<AirtelAppToCRMDTO> customers = customerResponse.getBody();
            Map<Integer, AirtelAppToCRMDTO> customerMap = customers.stream()
                    .collect(Collectors.toMap(AirtelAppToCRMDTO::getCustId, Function.identity()));

            AirtelAppToCRMDTO customer = customerMap.values().stream().findFirst()
                    .orElseThrow(() -> new RuntimeException("Customer data not found"));

            Long orderId = airtelValidateTxService.generateId(customer.getCustId().longValue());

            validateMobileNumber(customerMap, paymentRequest.getPhoneNumber());
            if (customer == null || customer.getAccountNo().isEmpty()) {
                logger.warn("customer data not found for AccountNo: {}", paymentRequest.getAccountNo());
                response.put("error", "Account not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Double walletAmount = 0.0;
            Double planPrice = 0.0;
            Integer planId = 0;
            planId =  client.getplanIdByCustId(customer.getCustId(),apikey);
            if(planId != 0) {
                walletAmount = revenueClient.getWalletBalanceByCustId(customer.getCustId(),apikey);
                planPrice =  client.getplanPriceByPlanId(planId,apikey);
            }

            CustPayDTOMessage custPayDTOMessage = new CustPayDTOMessage();
            custPayDTOMessage.setOrderId(orderId);
            custPayDTOMessage.setCustomerUsername(customer.getUsername());
            custPayDTOMessage.setStatus(AirtelValidateConstant.INITIATE);
            custPayDTOMessage.setGatewayStatus(AirtelValidateConstant.INITIATE);
            custPayDTOMessage.setAccountNumber(paymentRequest.getAccountNo());
            custPayDTOMessage.setCustId(customer.getCustId());
            custPayDTOMessage.setPgTransactionId("");
            custPayDTOMessage.setPayment(paymentRequest.getAmount());
            custPayDTOMessage.setBuid(customer.getBuId());
            custPayDTOMessage.setMvnoid(customer.getMvnoId());
            custPayDTOMessage.setMerchantName(AirtelValidateConstant.PAYWAY_PAYMENT + "_" + paymentRequest.getChannel());
            custPayDTOMessage.setPaymentGatewayName(AirtelValidateConstant.PAYWAY_PAYMENT + "_" + paymentRequest.getChannel());
            custPayDTOMessage.setPaymentDate(LocalDateTime.now().toString());
            custPayDTOMessage.setTransactionDate(LocalDateTime.now().toString());
            custPayDTOMessage.setWalletAmount(walletAmount);
            custPayDTOMessage.setPlanPrice(planPrice);
            custPayDTOMessage.setPlanId(planId);
            custPayDTOMessage.setPayerMobileNumber(paymentRequest.getPhoneNumber());

            logger.info("Saving payment transaction for OrderId: {}", orderId);
            CustomerPayment customerPayment = customerPaymentService.convertMessageToEntity(custPayDTOMessage);
//            customerPayment.setId(airtelValidateTxService.getLatestId());
            customerPayment.setCreatedById(jwtUtil.getLoggedInUser().getUserId());
            customerPayment.setCreatedByName(jwtUtil.getLoggedInUser().getUsername());

            customerPayment = customerPaymentRepository.save(customerPayment);

            AirtelCRMRequestDTO airtelCRMRequestDTO = new AirtelCRMRequestDTO();
            airtelCRMRequestDTO.setAirtelAppToCRMDTO(customer);
            airtelCRMRequestDTO.setCustPayDTOMessage(custPayDTOMessage);

            boolean isSaved = client.addCustomerPayment(custPayDTOMessage, apikey);

            if(isSaved){
                paymentIntegrationService.updateStatusAndSendToCMS(orderId.toString(), paymentRequest.getTransactionId(), paymentRequest.getStatus(), null);
                response.put("message", "Transaction received");
                response.put("StatusCode", "SUCCESS");
            } else {
                response.put("message", "Transaction received");
                response.put("StatusCode", "FAILED");
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
            response.put("error", e.getMessage());
            String errorMessage = e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(errorMessage));
        } catch (Exception e) {
            logger.error("Unexpected error in processForwardPayment: {}", e.getMessage(), e);
            String errorMessage = e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(errorMessage));
        }
    }

    public OnlineInvoicePaymentDTO convertCustomerPaymentToOnlinePaymentDTO(CustomerPayment customerPayment){
        OnlineInvoicePaymentDTO onlineInvoicePaymentDTO = new OnlineInvoicePaymentDTO();
        onlineInvoicePaymentDTO.setPaymentGatewayName(customerPayment.getMerchantName());
        onlineInvoicePaymentDTO.setCustId(customerPayment.getCustId());
        onlineInvoicePaymentDTO.setAmount(customerPayment.getPayment());
        onlineInvoicePaymentDTO.setIsLco(false);
        onlineInvoicePaymentDTO.setMvnoId(customerPayment.getMvnoid());
        if(customerPayment.getBuid() != null) {
            onlineInvoicePaymentDTO.setBuId(Collections.singletonList(customerPayment.getBuid().longValue()));
        }
        onlineInvoicePaymentDTO.setCreatedById(customerPayment.getCreatedById());
        onlineInvoicePaymentDTO.setCreatedByName(customerPayment.getCreatedByName());
        onlineInvoicePaymentDTO.setInvoiceId(customerPayment.getInvoiceId());
        onlineInvoicePaymentDTO.setPartnerId(customerPayment.getPartnerId());
        onlineInvoicePaymentDTO.setTransactionNumber(customerPayment.getOrderId());
        return onlineInvoicePaymentDTO;

    }

    public ResponseEntity<?> processTransactionStatus(TransactionRequestDTO transaction, String apikey) {
        logger.info("Processing transaction status - TransactionId: {}", transaction.getTransactionId());
        Map<String, Object> response = new HashMap<>();
        try {
            // Fetch Transaction Details
            List<CustomerPayment> customerPaymentList = customerPaymentRepository.findAllByPgTransactionId(transaction.getTransactionId());
            if (customerPaymentList.isEmpty() || customerPaymentList == null) {
                response.put("error", "Transaction not found");
                response.put("StatusCode", "FAILED");
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(response);
            }
            if (customerPaymentList.size() > 1) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponseDTO(" Duplicate pgTransactionId found: " + transaction.getTransactionId()));

            }
            // Prepare TransactionStatusDto
            CustomerPayment customerPayment = customerPaymentList.get(0);
            TransactionStatusDto transactionStatusDto = new TransactionStatusDto();
            transactionStatusDto.setTransactionId(customerPayment.getPgTransactionId());
            transactionStatusDto.setAmount(customerPayment.getPayment());
            transactionStatusDto.setChannel(customerPayment.getMerchantName());
            transactionStatusDto.setStatus("SUCCESS");
            transactionStatusDto.setTransactionTime(String.valueOf(customerPayment.getTransactionDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
            transactionStatusDto.setForwarded(String.valueOf(customerPayment.getPaymentDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
            try {
                ResponseEntity<List<TransactionStatusDto>> customerData = client.getCustomerByCustId(String.valueOf(customerPayment.getCustId()), apikey);
                List<TransactionStatusDto> cus = customerData.getBody();
                if (customerData != null) {
                    transactionStatusDto.setAccountNo(cus.get(0).getAccountNo());
                    transactionStatusDto.setPhoneNumber(cus.get(0).getPhoneNumber());
                    transactionStatusDto.setName(cus.get(0).getName());
                    transactionStatusDto.setEmail(cus.get(0).getEmail());
                }
                transactionStatusDto.setStatusCode("SUCCESS");
            } catch (FeignException.NotFound e) {
                logger.error("“error”: “No payment with specified id/reference " + transaction.getTransactionId());
                String errorMessage = extractErrorMessage(e);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO(errorMessage));
            } catch (DecodeException e) {
                logger.error("Failed to decode Feign Client response: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body(new ErrorResponseDTO("Invalid response received from customer service"));
            } catch (FeignException e) {
                String errorMessage = extractErrorFromFeignException(e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(errorMessage));
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.error("Unexpected error fetching customer data: {}", ex.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ErrorResponseDTO("Error retrieving customer data"));
            }
            return ResponseEntity.status(HttpStatus.OK).body(transactionStatusDto);
        } catch (IllegalStateException e) {
            logger.error("Transaction processing error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponseDTO(e.getMessage()));
        } catch (Exception e) {
            logger.error("Internal server error while processing transaction {}: {}",
                    transaction.getTransactionId(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponseDTO("Internal server error occurred"));
        }
    }
    public ResponseEntity<?>processReconciliationstatement(ReconciliationReqDTO reconciliationReqDTO, String apikey) {
        Map<String, Object> response = new HashMap<>();

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // Convert String dates to LocalDateTime using parseDateTime
            LocalDateTime paidAfter = parseDateTime(reconciliationReqDTO.getPaidAfter(), formatter);
            LocalDateTime paidBefore = parseDateTime(reconciliationReqDTO.getPaidBefore(), formatter);
            LocalDateTime forwardedAfter = parseDateTime(reconciliationReqDTO.getForwardedAfter(), formatter);
            LocalDateTime forwardedBefore = parseDateTime(reconciliationReqDTO.getForwardedBefore(), formatter);

            boolean hasPaidRange = (paidAfter != null && paidBefore != null);
            boolean hasForwardedRange = (forwardedAfter != null && forwardedBefore != null);
            if ((hasPaidRange && paidAfter.isAfter(paidBefore)) || (hasForwardedRange && forwardedAfter.isAfter(forwardedBefore))) {
                if (hasPaidRange && paidAfter.isAfter(paidBefore) && hasForwardedRange && forwardedAfter.isAfter(forwardedBefore)) {
                    response.put("error", "PaidAfter date cannot be greater than PaidBefore date, and ForwardedAfter date cannot be greater than ForwardedBefore date.");
                } else if (hasPaidRange && paidAfter.isAfter(paidBefore)) {
                    response.put("error", "PaidAfter date cannot be greater than PaidBefore date.");
                } else if (hasForwardedRange && forwardedAfter.isAfter(forwardedBefore)) {
                    response.put("error", "ForwardedAfter date cannot be greater than ForwardedBefore date.");
                }
                response.put("StatusCode", "FAILED");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            if (!hasPaidRange && !hasForwardedRange) {
                response.put("error","Request must contain either PaidAfter-PaidBefore or ForwardedAfter-ForwardedBefore.");
                response.put("StatusCode","FAILED");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            List<CustomerPayment> customerPaymentList = customerPaymentRepository.findPaymentsByDateRange(
                    hasPaidRange ? paidAfter : null,
                    hasPaidRange ? paidBefore : null,
                    hasForwardedRange ? forwardedAfter : null,
                    hasForwardedRange ? forwardedBefore : null
            );
            if (customerPaymentList == null || customerPaymentList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body(Collections.emptyList());
            }
            List<TransactionStatusDto> transactionStatusDtoList = customerPaymentList.stream().map(customerPayment -> {
                TransactionStatusDto transactionStatusDto = new TransactionStatusDto();
                transactionStatusDto.setTransactionId(customerPayment.getPgTransactionId());
                transactionStatusDto.setAmount(customerPayment.getPayment());
                transactionStatusDto.setChannel(customerPayment.getMerchantName());
                transactionStatusDto.setStatus(customerPayment.getStatus());
                transactionStatusDto.setMessage("");
                transactionStatusDto.setTransactionTime(String.valueOf(customerPayment.getTransactionDate()
                        .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
                transactionStatusDto.setForwarded(String.valueOf(customerPayment.getPaymentDate()
                        .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
//                 Fetch customer details from anoter microservice
                ResponseEntity<List<TransactionStatusDto>> customerDataResponse = client.getCustomerByCustId(
                        String.valueOf(customerPayment.getCustId()), apikey
                );
                if (customerDataResponse.getStatusCode().is2xxSuccessful() && customerDataResponse.getBody() != null && !customerDataResponse.getBody().isEmpty()) {
                    TransactionStatusDto customerDetails = customerDataResponse.getBody().get(0);
                    transactionStatusDto.setAccountNo(customerDetails.getAccountNo());
                    transactionStatusDto.setPhoneNumber(customerDetails.getPhoneNumber());
                    transactionStatusDto.setName(customerDetails.getName());
                    transactionStatusDto.setEmail(customerDetails.getEmail());
                }
                transactionStatusDto.setStatusCode("SUCCESS");
                return transactionStatusDto;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(transactionStatusDtoList);

        } catch (FeignException.NotFound e) {
            String errorMessage = extractErrorMessage(e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO(errorMessage));
        } catch (DecodeException e) {
            logger.error("Failed to decode Feign Client response: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(new ErrorResponseDTO("Invalid response received from customer service"));
        } catch (FeignException e) {
            String errorMessage = extractErrorFromFeignException(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(errorMessage));
        } catch (IllegalStateException e) {
            logger.error("Transaction processing error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponseDTO(e.getMessage()));

        }catch (DateTimeParseException e) {
            logger.error("Transaction processing error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error fetching customer data: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponseDTO(e.getMessage()));
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
                    return bodyStr;
                }
            }
        } catch (IOException ioException) {
            return "Error decoding response body: " + ioException.getMessage();
        }
        return "Error response body is empty or null";
    }

    private String extractErrorFromFeignException(FeignException e) {
        try {
            String responseBody = e.contentUTF8(); // Get the content of the exception
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            return rootNode.path("error").asText("Something went wrong while processing the request");
        } catch (Exception ex) {
            logger.error("Failed to extract error from FeignException: " + ex.getMessage(), ex);
            return "An unexpected error occurred";
        }
    }

    private ResponseEntity<?> validateMobileNumber(Map<Integer, AirtelAppToCRMDTO> customerData, String phoneNumber) {
        AirtelAppToCRMDTO customer = customerData.values().stream().findFirst().orElse(null);

        if (customer == null || customer.getMobileNumber() == null || customer.getMobileNumber().isEmpty()) {
            throw new IllegalArgumentException("No valid mobile number found in customer data.");
        }
//Below code remove Reason is Add Regex , startLine--->
      //  String fetchedMobileNo = customer.getMobileNumber();
       // String s1 = String.valueOf(phoneNumber.length());
//        if (!fetchedMobileNo.equals(s1)) {
//            throw new IllegalArgumentException("Invalid mobile number. Input value in Digit : " + fetchedMobileNo);
//        } else {
//            customerData.remove(customer.getMobileNumber());
//            return ResponseEntity.ok(customerData);
//        }

        //<<----closeLine

        String regexPattern = customer.getMobileNumber().replace("\\\\", "\\");
        if (!phoneNumber.matches(regexPattern)) {
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
    private static LocalDateTime parseDateTime(String input, DateTimeFormatter formatter) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        try {
            if (input.matches("\\d{13}")) {
                return Instant.ofEpochMilli(Long.parseLong(input))
                        .atZone(ZoneId.of("UTC"))
                        .toLocalDateTime();
            } else {
                return LocalDate.parse(input, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        .atStartOfDay();
            }
        } catch (NumberFormatException | DateTimeParseException e) {
            throw new DateTimeParseException("Invalid date format: " + input, input, 0);
        }
    }
}