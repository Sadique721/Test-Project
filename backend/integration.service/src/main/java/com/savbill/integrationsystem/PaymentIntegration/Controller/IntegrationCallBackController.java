package com.savbill.integrationsystem.PaymentIntegration.Controller;

import com.savbill.integrationsystem.OnlinePayAudit.OnlinePayAuditService;
import com.savbill.integrationsystem.PaymentConfig.model.PaymentGatewayConfigurationConstant;
import com.savbill.integrationsystem.PaymentConfig.service.PaymentConfigService;
import com.savbill.integrationsystem.PaymentIntegration.Model.CustomerPayment;
import com.savbill.integrationsystem.PaymentIntegration.Repository.CustomerPaymentRepository;
import com.savbill.integrationsystem.PaymentIntegration.Service.PayStackPaymentService;
import com.savbill.integrationsystem.PaymentIntegration.Service.PaymentIntegrationService;
import com.savbill.integrationsystem.apiAudits.NMSServices.ApiAuditsService;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.utillity.APIConstants;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.core.utillity.log.ApplicationLogger;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class IntegrationCallBackController {

    @Autowired
    CustomerPaymentRepository customerPaymentRepository;


    @Autowired
    PaymentIntegrationService paymentIntegrationService;

    @Autowired
    PaymentConfigService paymentConfigService;

    @Autowired
    private ApiAuditsService apiAuditsService;
    @Autowired
    private PayStackPaymentService payStackPaymentService;

    @Autowired
    OnlinePayAuditService onlinePayAuditService;

//    @PostMapping("/callback")
//    public String handlePaymentCallback(HttpServletRequest request, Model model)throws Exception  {
//
//        try{
//
//            //Extracting data from request
//
//            String code = request.getParameter("code");
//            String merchantId = request.getParameter("merchantId");
//            String transactionId = request.getParameter("transactionId");
//            String amount = request.getParameter("amount");
//            String providerReferenceId = request.getParameter("providerReferenceId");
//            String checksum = request.getParameter("checksum");
//
//            // Log incoming parameters for debugging
//            Map<String, String[]> parameterMap = request.getParameterMap();
//            parameterMap.forEach((key, value) -> {
//                System.out.println("Key: " + key + ", Value: " + String.join(", ", value));
//            });
//
//            String status = paymentIntegrationService.updatePaymentStatus(code,merchantId,transactionId,amount,providerReferenceId,checksum);
//            Map<String, String[]> mapData = request.getParameterMap();
//            TreeMap<String, String> parameters = new TreeMap<String, String>();
//
//            for (Map.Entry<String, String[]> requestParamsEntry : mapData.entrySet()) {
//                parameters.put(requestParamsEntry.getKey(), requestParamsEntry.getValue()[0]);
//            }
//            model.addAttribute("result", status);
//            model.addAttribute("parameters", parameters);
//
//            return "report";
//
//        }catch (Exception e){
//            ApplicationLogger.logger.error(e.getMessage());
//        }
//        return "report";
//    }


    @PostMapping("/callback")
    public String handlePaymentCallback(HttpServletRequest request, Model model) {
        try {
            // Extracting data from request
            String code = request.getParameter("code");
            String merchantId = request.getParameter("merchantId");
            String transactionId = request.getParameter("transactionId");
            String amount = request.getParameter("amount");
            Double finalAmount = Double.parseDouble(amount) / 100;
            String providerReferenceId = request.getParameter("providerReferenceId");
            String checksum = request.getParameter("checksum");

            // Log incoming parameters for debugging
            Map<String, String[]> parameterMap = request.getParameterMap();
            parameterMap.forEach((key, value) -> {
                System.out.println("Key: " + key + ", Value: " + String.join(", ", value));
            });

            String status = paymentIntegrationService.updatePaymentStatus(code, merchantId, transactionId, finalAmount.toString(), providerReferenceId, checksum);
            TreeMap<String, String> parameters = new TreeMap<>();

            for (Map.Entry<String, String[]> requestParamsEntry : parameterMap.entrySet()) {
                if (!requestParamsEntry.getKey().contains("param") && !requestParamsEntry.getKey().contains("checksum")) {
                    parameters.put(requestParamsEntry.getKey(), requestParamsEntry.getValue()[0]);
                    if (requestParamsEntry.getKey().equalsIgnoreCase("amount")) {
                        parameters.put(requestParamsEntry.getKey(), finalAmount.toString());
                    }
                }

            }

            CustomerPayment customerPayment = customerPaymentRepository.findByOrderId(Long.valueOf(transactionId));
            if (customerPayment != null) {
                String redirectUrl = null;
                HashMap<String, String> paymentGatewayParameter = paymentConfigService.getPaymentGatewayParameter(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.PHONEPE, customerPayment.getMvnoid());
                if (customerPayment.getCustId() != null) {
                    redirectUrl = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.PHONEPE.PHONEPE_REDIRECT_URL);
                } else if (customerPayment.getPartnerId() != null) {
                    redirectUrl = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.PHONEPE.PARTNER_REDIRECT_URL);
                }
                String redirectInSeconds = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.PHONEPE.REDIRECT_IN_SECONDS);
                model.addAttribute("homeRedirectUrl", redirectUrl);
                model.addAttribute("redirectTimeInSeconds", redirectInSeconds);
            }
            model.addAttribute("result", status);
            model.addAttribute("parameters", parameters);

            return "report";

        } catch (Exception e) {
            ApplicationLogger.logger.error(e.getMessage());
            // model.addAttribute("errorMessage", e.getMessage());
            return "report";
        }
    }

    @PostMapping("/momoCallback")
    @ResponseBody
    public GenericDataDTO handlePostMomoCallback(HttpServletRequest request, @RequestBody Object moMoPePayment) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        HttpHeaders headers = new HttpHeaders();
        headers.set("headers",request.getHeaderNames().toString());
        try {
            genericDataDTO.setResponseCode(500);
            // Retrieve the customer payment based on order ID
//            CustomerPayment customerPayment = customerPaymentRepository.findByOrderId(Long.valueOf(((LinkedHashMap) moMoPePayment).get("externalId").toString()));
//            CustomerPayment customerPayment = customerPaymentRepository.findByPgTransactionIdAndGatewayStatusNot(((LinkedHashMap) moMoPePayment).get("financialTransactionId").toString(),"Successful");
            CustomerPayment customerPayment = customerPaymentRepository.findLatestPendingPayment(((LinkedHashMap) moMoPePayment).get("externalId").toString(),Double.valueOf(((LinkedHashMap) moMoPePayment).get("amount").toString()),((LinkedHashMap) ((LinkedHashMap) moMoPePayment).get("payer")).get("partyId").toString());
            if(null==customerPayment){
                genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                genericDataDTO.setResponseMessage("No such customer found for /momoCallback");
                genericDataDTO.setData(moMoPePayment);
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),moMoPePayment,ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericDataDTO) , headers , responseTime , requestInitiationTime , null , 1, "POST" , "ERROR","MOMOPAY-CALLBACK",null);
                ApplicationLogger.logger.warn("No customer found for /momoCallback");
                return genericDataDTO;
            }

            if (((LinkedHashMap) moMoPePayment).get("status").toString().equalsIgnoreCase("SUCCESSFUL")) {
                genericDataDTO.setResponseCode(200);
                genericDataDTO.setResponseMessage("Payment done successfully");
                genericDataDTO.setData(moMoPePayment);
                ApplicationLogger.logger.info("payment done successfully in callback with payload: " + moMoPePayment);
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),moMoPePayment,ResponseEntity.status(HttpStatus.OK).body(genericDataDTO) , headers , responseTime , requestInitiationTime , customerPayment.getCustomerUsername() !=null ? customerPayment.getCustomerUsername() : null , 1, "POST" , "Success","MOMOPAY-CALLBACK",((LinkedHashMap) moMoPePayment).get("externalId").toString());
                paymentIntegrationService.updateStatusAndSendToCMS(customerPayment.getOrderId().toString(), ((LinkedHashMap) moMoPePayment).get("financialTransactionId").toString(), "SUCCESSFUL", null);
            } else {
                genericDataDTO.setResponseMessage("Payment Failed");
                genericDataDTO.setResponseCode(HttpStatus.CONFLICT.value());
                genericDataDTO.setData(moMoPePayment);
                ApplicationLogger.logger.info("payment has been failed with request in callback: " + moMoPePayment);
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),moMoPePayment,ResponseEntity.status(HttpStatus.CONFLICT).body(genericDataDTO) , headers , responseTime , requestInitiationTime , customerPayment.getCustomerUsername() !=null ? customerPayment.getCustomerUsername() : null , 1, "POST" , "FAILED","MOMOPAY-CALLBACK",((LinkedHashMap) moMoPePayment).get("externalId").toString());
                paymentIntegrationService.updateStatusAndSendToCMS(customerPayment.getOrderId().toString(), ((LinkedHashMap) moMoPePayment).get("financialTransactionId").toString(), ((LinkedHashMap) moMoPePayment).get("status").toString(),((LinkedHashMap) moMoPePayment).get("reason").toString());
            }
        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            genericDataDTO.setResponseMessage("Error processing buy callback");
            genericDataDTO.setData(moMoPePayment);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),moMoPePayment,ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericDataDTO) , headers , responseTime , requestInitiationTime , null , 1, "POST" , "ERROR","MOMOPAY-CALLBACK",null);
            ApplicationLogger.logger.error("Error processing buy callback due to: " + e.getMessage());
        }
        return genericDataDTO;
    }


    @PostMapping("/selcomWebHook")
    public String handleSelcomWebHookUrl(@RequestBody Object payload, HttpServletRequest request, Model model) throws IOException {
        MDC.put("type", "Fetch");
        ApplicationLogger.logger.info("Selcom Call Back API Hit");
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        HttpHeaders headers = new HttpHeaders();
        headers.set("headers",request.getHeaderNames().toString());
        try {
            // Convert payload to JsonNode for easy extraction
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode selcomWebHookDTO = objectMapper.valueToTree(payload);

            // Extract values from the JSON payload
            String orderId = selcomWebHookDTO.get("order_id").asText();
            String transactionalId = selcomWebHookDTO.get("transid").asText();
            String paymentStatus = selcomWebHookDTO.get("payment_status").asText();
            String amount = selcomWebHookDTO.get("amount").asText();

            // Retrieve the customer payment based on order ID
            CustomerPayment customerPayment = customerPaymentRepository.findByOrderId(Long.valueOf(orderId));

            if (paymentStatus.equalsIgnoreCase("COMPLETED")) {
                paymentStatus = "Successful";
                ApplicationLogger.logger.info("Payment done successfully in webhook with payload: " + selcomWebHookDTO);
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),selcomWebHookDTO,ResponseEntity.status(HttpStatus.OK).body(payload) , headers , responseTime , requestInitiationTime , customerPayment.getCustomerUsername() !=null ? customerPayment.getCustomerUsername() : null , 1, "POST" , "Success","SELCOM-CALLBACK",orderId);
                paymentIntegrationService.updateStatusAndSendToCMS(orderId, transactionalId, paymentStatus,null);
            } else {
                ApplicationLogger.logger.info("Payment has failed with request in callback: " + selcomWebHookDTO);
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),selcomWebHookDTO,ResponseEntity.status(HttpStatus.OK).body(payload) , headers , responseTime , requestInitiationTime , customerPayment.getCustomerUsername() !=null ? customerPayment.getCustomerUsername() : null , 1, "POST" , "Success","SELCOM-CALLBACK",orderId);
                paymentIntegrationService.updateStatusAndSendToCMS(orderId, transactionalId, paymentStatus,null);
            }


            if (customerPayment != null) {
                HashMap<String, String> getPaymentGatewayParemeter = paymentConfigService.getPaymentGatewayParameter(
                        PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.RAZORPAY, customerPayment.getMvnoid());
                String REDIRECT_CWSC_URL = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.RAZORPAY.CWSC_REDIRECT_URL);
                String REDIRECT_TIME_IN_SECONDS = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.RAZORPAY.REDIRECT_TIME_IN_SECONDS);

                // Set Model Attributes
                model.addAttribute("homeRedirectUrl", REDIRECT_CWSC_URL);
                model.addAttribute("redirectTimeInSeconds", REDIRECT_TIME_IN_SECONDS);
            }

            // Add additional model attributes
            model.addAttribute("status", paymentStatus);
            model.addAttribute("amount", amount);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),selcomWebHookDTO,ResponseEntity.status(HttpStatus.OK).body(payload) , headers , responseTime , requestInitiationTime , customerPayment.getCustomerUsername() !=null ? customerPayment.getCustomerUsername() : null , 1, "POST" , "Success","SELCOM-CALLBACK",orderId);
            return "report";

        } catch (Exception e) {
            ApplicationLogger.logger.error("Error While Handle Selcom WebHook Data.");
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),payload,ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payload) , headers , responseTime , requestInitiationTime , null , 1, "POST" , "Success","SELCOM-CALLBACK",null);
            return "report";
        }

    }


    @PostMapping("/airtelWebHook")
    public String handleAirtelWebHookUrl(@RequestBody Object payload, HttpServletRequest request, Model model) throws IOException {
        MDC.put("type", "Fetch");
        ApplicationLogger.logger.info("Airtel Call Back API Hit");
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        HttpHeaders headers = new HttpHeaders();
        headers.set("headers",request.getHeaderNames().toString());
        try {
            // Convert payload to JsonNode for easy extraction
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode airtelWebHookDTO = objectMapper.valueToTree(payload);

            // Extract values from the JSON payload
            JsonNode transaction = airtelWebHookDTO.get("transaction");
            if (transaction == null) {
                ApplicationLogger.logger.error("Transaction data missing in payload");
                return "Invalid Payload";
            }

            String orderId = transaction.get("id").asText();
            String transactionalId = transaction.get("airtel_money_id").asText();
            String paymentStatus = transaction.get("status_code").asText();
            String amount = "";

            // Retrieve the customer payment based on order ID
            CustomerPayment customerPayment = customerPaymentRepository.findByOrderId(Long.valueOf(orderId));

            if (paymentStatus.equalsIgnoreCase("TS")) {
                paymentStatus = "Successful";
                ApplicationLogger.logger.info("Payment done successfully in webhook with payload: " + airtelWebHookDTO);
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),payload,ResponseEntity.status(HttpStatus.OK).body(payload) , headers , responseTime , requestInitiationTime , customerPayment.getCustomerUsername() !=null ? customerPayment.getCustomerUsername() : null , 1, "POST" , "Success","AIRTEL-CALLBACK",orderId);
                paymentIntegrationService.updateStatusAndSendToCMS(orderId, transactionalId, paymentStatus, null);
            } else {
                ApplicationLogger.logger.info("Payment has failed with request in callback: " + airtelWebHookDTO);
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),payload,ResponseEntity.status(HttpStatus.OK).body(payload) , headers , responseTime , requestInitiationTime , customerPayment.getCustomerUsername() !=null ? customerPayment.getCustomerUsername() : null , 1, "POST" , "Failed","AIRTEL-CALLBACK",orderId);
                paymentIntegrationService.updateStatusAndSendToCMS(orderId, transactionalId, paymentStatus, null);
            }


            if (customerPayment != null) {
                HashMap<String, String> getPaymentGatewayParemeter = paymentConfigService.getPaymentGatewayParameter(
                        PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.AIRTEL, customerPayment.getMvnoid());
                String REDIRECT_CWSC_URL = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.RAZORPAY.CWSC_REDIRECT_URL);
                String REDIRECT_TIME_IN_SECONDS = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.RAZORPAY.REDIRECT_TIME_IN_SECONDS);

                // Set Model Attributes
                model.addAttribute("homeRedirectUrl", REDIRECT_CWSC_URL);
                model.addAttribute("redirectTimeInSeconds", REDIRECT_TIME_IN_SECONDS);
            }

            // Add additional model attributes
            model.addAttribute("status", paymentStatus);
            model.addAttribute("amount", customerPayment.getPayment());
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),payload,ResponseEntity.status(HttpStatus.OK.value()).body(payload) , headers , responseTime , requestInitiationTime , customerPayment.getCustomerUsername() !=null ? customerPayment.getCustomerUsername() : null , 1, "POST" , "Success","AIRTEL-CALLBACK",null);
            return "report";

        } catch (Exception e) {
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),payload,ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payload) , headers , responseTime , requestInitiationTime , null , 1, "POST" , e.getMessage(),"AIRTEL-CALLBACK",null);
            ApplicationLogger.logger.error("Error While Handle Airtel WebHook Data : " + e.getMessage());
            return "report";
        }

    }

    @PutMapping("/momoCallback")
    @ResponseBody
    public GenericDataDTO handlePutMomoCallback(HttpServletRequest request, @RequestBody Object moMoPePayment) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {

            genericDataDTO.setResponseCode(200);
            genericDataDTO.setResponseMessage("Customer Payment update successfully");
            paymentIntegrationService.updateStatusAndSendToCMS(((LinkedHashMap) moMoPePayment).get("externalId").toString(), ((LinkedHashMap) moMoPePayment).get("financialTransactinId").toString(), ((LinkedHashMap) moMoPePayment).get("status").toString(),null);
        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            genericDataDTO.setResponseMessage("Error processing change status callback put mapping");
            ApplicationLogger.logger.error("Error processing change status callback put mapping due to: " + e.getMessage());
        }
        return genericDataDTO;
    }

    @PostMapping("/waveMoneyCallBack")
    public String handleWaveMoneyWebHook(@RequestBody Object payload, HttpServletRequest request, Model model) throws IOException {
        MDC.put("type", "Fetch");
        ApplicationLogger.logger.info("::::::::::::::::::::Wave Money Call Back API Hit::::::::::::::::::::");
        ApplicationLogger.logger.info("Receiving response from wave money on call back "+payload);
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        HttpHeaders headers = new HttpHeaders();
        headers.set("headers",request.getHeaderNames().toString());
        try {
            // Convert payload to JsonNode for easy extraction
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode waveMoneyWebHookDTO = objectMapper.valueToTree(payload);

            // Extract values from the JSON payload
            String orderId = waveMoneyWebHookDTO.get("merchantReferenceId").asText();
            String transactionalId = waveMoneyWebHookDTO.get("transactionId").asText();
            String paymentStatus = waveMoneyWebHookDTO.get("status").asText();
            String amount = waveMoneyWebHookDTO.get("amount").asText();
            String paymentDescription = waveMoneyWebHookDTO.get("paymentDescription").asText();

            // Retrieve the customer payment based on order ID
            CustomerPayment customerPayment = customerPaymentRepository.findByOrderId(Long.valueOf(orderId));

            if (paymentStatus.equalsIgnoreCase("PAYMENT_CONFIRMED")) {
                System.out.println("::::::::::::::::::::::Payment status Successful on a Callback in Wave Money:::::::::::::::::");
                paymentStatus = "Successful";
                ApplicationLogger.logger.info("Payment done successfully in webhook with payload: " + waveMoneyWebHookDTO);
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),waveMoneyWebHookDTO,ResponseEntity.status(HttpStatus.OK).body(payload) , headers , responseTime , requestInitiationTime , customerPayment.getCustomerUsername() !=null ? customerPayment.getCustomerUsername() : null , 1, "POST" , "Success","WAVEMONEY-CALLBACK",orderId);
                paymentIntegrationService.updateStatusAndSendToCMS(orderId, transactionalId, paymentStatus,null);
            } else {
                ApplicationLogger.logger.info("Payment has failed with request in callback: " + waveMoneyWebHookDTO);
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),waveMoneyWebHookDTO,ResponseEntity.status(HttpStatus.OK).body(payload) , headers , responseTime , requestInitiationTime , customerPayment.getCustomerUsername() !=null ? customerPayment.getCustomerUsername() : null , 1, "POST" , "Success","WAVEMONEY-CALLBACK",orderId);
                paymentIntegrationService.updateStatusAndSendToCMS(orderId, transactionalId, paymentStatus,null);
            }


            if (customerPayment != null) {
                HashMap<String, String> getPaymentGatewayParemeter = paymentConfigService.getPaymentGatewayParameter(
                        PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.WAVE_PAY, customerPayment.getMvnoid());
                String REDIRECT_CWSC_URL = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.WAVEMONEY.WAVEMONEY_REDIRECT_URL);
                String REDIRECT_TIME_IN_SECONDS = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.WAVEMONEY.REDIRECT_TIME_IN_SECONDS);

                // Set Model Attributes
                model.addAttribute("homeRedirectUrl", REDIRECT_CWSC_URL);
                model.addAttribute("redirectTimeInSeconds", REDIRECT_TIME_IN_SECONDS);
            }

            // Add additional model attributes
            LinkedHashMap<String,Object> parameters = new LinkedHashMap<>();
            parameters.put("paymentDescription",paymentDescription);
            parameters.put("amount", amount);
            parameters.put("transactionId", transactionalId);
            parameters.put("paymentStatus", paymentStatus);
            // Add additional model attributes
            model.addAttribute("status", paymentStatus);
            model.addAttribute("amount", amount);
            model.addAttribute("parameters", parameters);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),waveMoneyWebHookDTO,ResponseEntity.status(HttpStatus.OK).body(payload) , headers , responseTime , requestInitiationTime , customerPayment.getCustomerUsername() !=null ? customerPayment.getCustomerUsername() : null , 1, "POST" , "Success","WAVEMONEY-CALLBACK",orderId);
            return "report";

        } catch (Exception e) {
            ApplicationLogger.logger.error("Error While Handle Wave Money WebHook Data.");
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),payload,ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage()) , headers , responseTime , requestInitiationTime , null , 1, "POST" , "Failed","WAVEMONEY-CALLBACK",null);
            return "report";
        }

    }
    @PostMapping("/onePayCallBack")
    public String handleOnePayWebHook(@RequestBody Object payload, HttpServletRequest request, Model model) throws IOException {
        MDC.put("type", "Fetch");
        ApplicationLogger.logger.info("::::::::::::::::::::One Pay  Call Back API Hit::::::::::::::::::::");
        ApplicationLogger.logger.info("Receiving response from One Pay Payment on call back "+payload);
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        HttpHeaders headers = new HttpHeaders();
        headers.set("headers",request.getHeaderNames().toString());
        try {
            // Convert payload to JsonNode for easy extraction
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode onePayWebHookDTO = objectMapper.valueToTree(payload);

            // Extract values from the JSON payload
            String pgtransactionid = onePayWebHookDTO.get("ReferIntegrationId").asText();
            String transactionalId = onePayWebHookDTO.get("TransactionID").asText();
            String paymentStatus = onePayWebHookDTO.get("TransactionStatus").asText();
            String amount = onePayWebHookDTO.get("Amount").asText();
            String failureReason = null;

            String originalStatus = paymentStatus;

            switch (originalStatus) {
                case "000":
                    System.out.println("::::::::::::::::::::::Payment status Successful on a Callback in OnePay Payment:::::::::::::::::");
                    paymentStatus = "Successful";
                    ApplicationLogger.logger.info("Payment done successfully in webhook with payload: " + onePayWebHookDTO);
                    break;

                case "012":
                    failureReason = "Cancel Transaction";
                    paymentStatus = "FAILED";
                    ApplicationLogger.logger.info("Payment has failed with request in callback: " + onePayWebHookDTO);
                    break;

                case "013":
                    failureReason = "Transaction Time Out";
                    paymentStatus = "FAILED";
                    ApplicationLogger.logger.info("Payment has failed with request in callback: " + onePayWebHookDTO);
                    break;

                case "014":
                    failureReason = "System Error";
                    paymentStatus = "FAILED";
                    ApplicationLogger.logger.info("Payment has failed with request in callback: " + onePayWebHookDTO);
                    break;

                default:
                    failureReason = "Transaction Failed";
                    paymentStatus = "FAILED";
                    ApplicationLogger.logger.info("Payment has failed with request in callback: " + onePayWebHookDTO);
                    break;
            }
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            String statusMessage = paymentStatus.equals("Successful") ? "Success" : "Failed";

            apiAuditsService.setAuditForCallback(
                    request.getRequestURL().toString(),
                    onePayWebHookDTO,
                    ResponseEntity.status(HttpStatus.OK).body(payload),
                    headers,
                    responseTime,
                    requestInitiationTime,
                    null,
                    1,
                    "POST",
                    statusMessage,
                    "ONEPAY-CALLBACK",
                    null
            );

            paymentIntegrationService.updateStatusAndSendToCMS(null, pgtransactionid, paymentStatus, failureReason);

//            apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),onePayWebHookDTO,ResponseEntity.status(HttpStatus.OK).body(payload) , headers , responseTime , requestInitiationTime , null , 1, "POST" , "Success","ONEPAY-CALLBACK",null);
            return "report";

        } catch (Exception e) {
            ApplicationLogger.logger.error("Error While Handle OnePay Money WebHook Data.");
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),payload,ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payload) , headers , responseTime , requestInitiationTime , null , 1, "POST" , "Failed","ONEPAY-CALLBACK",null);
            return "report";
        }

    }

    @PostMapping(value = "/kbzPayCallBack",produces = "text/plain")
    public ResponseEntity<String> handleKbzPayCallBack(@RequestBody Object payload, HttpServletRequest request) throws IOException {
        MDC.put("type", "Fetch");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonPayload = objectMapper.writeValueAsString(payload);
        System.out.println(":::::::Request Payload::::::"+jsonPayload);
        ApplicationLogger.logger.info("::::::::::::::::::::KBZ Pay Call Back API Hit::::::::::::::::::::");
        ApplicationLogger.logger.info("Receiving response from KBZ Pay on call back "+payload);
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        HttpHeaders headers = new HttpHeaders();
        headers.set("headers",request.getHeaderNames().toString());
        try {
            System.out.println("::::::::::Inside KbzPayCallBack Api:::::::::::");
            // Convert payload to JsonNode for easy extraction

            JsonNode kbzPayWebHookDTO = objectMapper.valueToTree(payload);
            JsonNode requestNode = kbzPayWebHookDTO.get("Request");
            System.out.println(":::::::Extract Request Part for KbzPay:::::::"+requestNode);
            System.out.println("::::::Extract Values from JSON for KbzPay:::::");
                // Extract values from the JSON payload
                String orderId = requestNode.get("merch_order_id").asText();
                System.out.println(":::::OrderId::::: "+orderId);
                String transactionalId = requestNode.get("mm_order_id").asText();
                System.out.println(":::::TransactionId::::: "+transactionalId);
                String paymentStatus = requestNode.get("trade_status").asText();
                System.out.println(":::::PaymentStatus::::: "+paymentStatus);
                String amount = requestNode.get("total_amount").asText();
                System.out.println(":::::Amount::::: "+amount);

            // Retrieve the customer payment based on order ID
            CustomerPayment customerPayment = customerPaymentRepository.findByOrderId(Long.valueOf(orderId));

                if (paymentStatus.equalsIgnoreCase("PAY_SUCCESS")) {
                    System.out.println("::::::::::::::::::::::Payment status Successful on a Callback in KBZ Pay:::::::::::::::::");
                    paymentStatus = "Successful";
                    ApplicationLogger.logger.info("Payment done successfully in webhook with payload for KBZ Pay: " + kbzPayWebHookDTO);
                    LocalDateTime requestCompletionTime = LocalDateTime.now();
                    Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                    apiAuditsService.setAuditForCallback(request.getRequestURL().toString(), kbzPayWebHookDTO, ResponseEntity.status(HttpStatus.OK).body(payload), headers, responseTime, requestInitiationTime, customerPayment.getCustomerUsername() !=null ? customerPayment.getCustomerUsername() : null, 1, "POST", "Success", "KBZPAY-CALLBACK", orderId);
                    paymentIntegrationService.updateStatusAndSendToCMS(orderId, transactionalId, paymentStatus, null);
                } else {
                    ApplicationLogger.logger.info("Payment has failed with request in callback for KBZ Pay: " + kbzPayWebHookDTO);
                    LocalDateTime requestCompletionTime = LocalDateTime.now();
                    Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                    apiAuditsService.setAuditForCallback(request.getRequestURL().toString(), kbzPayWebHookDTO, ResponseEntity.status(HttpStatus.OK).body(payload), headers, responseTime, requestInitiationTime, customerPayment.getCustomerUsername() !=null ? customerPayment.getCustomerUsername() : null, 1, "POST", "Success", "KBZPAY-CALLBACK", orderId);
                    paymentIntegrationService.updateStatusAndSendToCMS(orderId, transactionalId, paymentStatus, null);
                }

//            System.out.println("Get Payment Details For Order Id: "+orderId+" For KbzPay "+customerPayment);


//            if (customerPayment != null) {
//                HashMap<String, String> getPaymentGatewayParemeter = paymentConfigService.getPaymentGatewayParameter(
//                        PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.KBZ_PAY, customerPayment.getMvnoid());
//                String REDIRECT_CWSC_URL = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.KBZPAY.KBZPAY_REDIRECT_URL);
//                String REDIRECT_TIME_IN_SECONDS = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.KBZPAY.REDIRECT_TIME_IN_SECONDS);
//
//                // Set Model Attributes
//                model.addAttribute("homeRedirectUrl", REDIRECT_CWSC_URL);
//                model.addAttribute("redirectTimeInSeconds", REDIRECT_TIME_IN_SECONDS);
//            }
//
//            // Add additional model attributes
//            model.addAttribute("status", paymentStatus);
//            model.addAttribute("amount", amount);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),kbzPayWebHookDTO,ResponseEntity.status(HttpStatus.OK).body(payload) , headers , responseTime , requestInitiationTime , customerPayment.getCustomerUsername() !=null ? customerPayment.getCustomerUsername() : null , 1, "POST" , "Success","KBZPAY-CALLBACK",orderId);
            return ResponseEntity.ok("success");

        } catch (Exception e) {
            System.out.println(":::::::Exception:::::::"+e.getMessage());
            e.printStackTrace();
            System.out.println(e.getStackTrace());
            System.out.println(e);
            System.out.println(e.getMessage());
            ApplicationLogger.logger.error("Error While Handle KBZ Pay WebHook Data.");
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),payload,ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage()) , headers , responseTime , requestInitiationTime , null , 1, "POST" , "Failed","KBZPAY-CALLBACK",null);
            return ResponseEntity.ok("failed");
        }

    }


    @GetMapping(value = URLConstants.PayStackPay.PAYSTACK_PAY + URLConstants.PayStackPay.CALLBACK)
    public String handlePayStackCallBack(@RequestParam("trxref") String trxRef,@RequestParam("reference") String reference,HttpServletRequest request,Model model) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        HttpHeaders headers = new HttpHeaders();
        headers.set("headers",request.getHeaderNames().toString());
        try {
            genericDataDTO.setResponseCode(APIConstants.EXPECTATION_FAILED);
            Map<String, Object> stringObjectMap = payStackPaymentService.handleCallBackFromPayStack(reference,request);

            HashMap<String, Object> data = (HashMap<String, Object>) stringObjectMap.get("data");
            CustomerPayment customerPayment = customerPaymentRepository.findByOrderId(Long.valueOf(data.get("reference").toString()));

            if(stringObjectMap != null && stringObjectMap.isEmpty()){
                ApplicationLogger.logger.error("Error While processing paystack callback");
                stringObjectMap.put("message","transaction data not fetched successfully");
                return "report";
            }
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            long measuredResponseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(request.getRequestURL().toString(), reference, ResponseEntity.status(APIConstants.SUCCESS).body(stringObjectMap), headers, measuredResponseTime, requestInitiationTime, customerPayment.getCustomerUsername() !=null ? customerPayment.getCustomerUsername() : null, 1, "GET", null, "PAYSTACK-CALLBACK", reference);

            if(!(boolean)stringObjectMap.get("status")){
                model.addAttribute("result",stringObjectMap.get("message"));
                return "report";
            }
             requestCompletionTime = LocalDateTime.now();
             measuredResponseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            if(stringObjectMap.get("status") != null && (boolean)stringObjectMap.get("status")){
                    apiAuditsService.setAuditForCallback(request.getRequestURL().toString(), reference, ResponseEntity.status(APIConstants.SUCCESS).body(stringObjectMap), headers, measuredResponseTime, requestInitiationTime, customerPayment.getCustomerUsername() !=null ? customerPayment.getCustomerUsername() : null, 1, "GET", null, "PAYSTACK-CALLBACK", reference);
            }else {
                apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),reference,ResponseEntity.status(APIConstants.EXPECTATION_FAILED).body(stringObjectMap),headers,measuredResponseTime,requestInitiationTime,customerPayment.getCustomerUsername() !=null ? customerPayment.getCustomerUsername() : null,1,"GET",null,"PAYSTACK-CALLBACK",reference);
            }


            if (customerPayment != null) {
                HashMap<String, String> getPaymentGatewayParemeter = paymentConfigService.getPaymentGatewayParameter(
                        PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.PAYSTACK, customerPayment.getMvnoid());
                String REDIRECT_URL = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.PAYSTACK.PAYSTACK_REDIRECT_URL);
                String REDIRECT_TIME_IN_SECONDS = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.PAYSTACK.REDIRECT_TIME_IN_SECONDS);
                model.addAttribute("homeRedirectUrl", REDIRECT_URL);
                model.addAttribute("redirectTimeInSeconds", REDIRECT_TIME_IN_SECONDS);
            }
            LinkedHashMap<String,Object> parameters = new LinkedHashMap<>();
            parameters.put("transaction-status", data.get("status"));
            parameters.put("reference", reference);
            parameters.put("amount", data.get("amount"));
            model.addAttribute("parameters",parameters);
            ApplicationLogger.logger.info("paystack callback completed successfully");
            return "report";
        } catch (Exception e) {
            model.addAttribute("message",e.getMessage());
            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            genericDataDTO.setResponseMessage(e.getMessage());
            genericDataDTO.setData(genericDataDTO.getData());
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),reference,ResponseEntity.status(APIConstants.INTERNAL_SERVER_ERROR).body(genericDataDTO),headers,responseTime,requestInitiationTime,null,1,"GET",null,"PAYSTACK-CALLBACK",reference);
            ApplicationLogger.logger.error("error while handling paystack callback; Message : {}", e.getMessage(),e);
            e.printStackTrace();
            return "report";
        }
    }

    @PostMapping(value = URLConstants.Transactease.CALLBACK)
    public ResponseEntity<?> handleTransacteaseCallback(@RequestBody Object payload, HttpServletRequest request) {
        MDC.put("type", "Fetch");
        ApplicationLogger.logger.info("::::::::::::::::::::Transactease Pay Call Back API Hit::::::::::::::::::::");
        ApplicationLogger.logger.info("Receiving response from Transactease Pay on call back "+payload);
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        HttpHeaders headers = new HttpHeaders();
        headers.set("headers",request.getHeaderNames().toString());
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode transacteaseResponse = objectMapper.valueToTree(payload);
            Integer responseCode =transacteaseResponse.get("RespCode").asInt();
            String requestId = transacteaseResponse.get("RequestID").asText();
            String transactionStatus = transacteaseResponse.get("RespDescription").asText();
            if(transactionStatus.equalsIgnoreCase("SUCCESS")){
                transactionStatus = "SUCCESSFUL";
            }
            String transactionID = transacteaseResponse.get("TransactionID").asText();
            String transactionReferenceNumber = transacteaseResponse.get("TransactionReferenceNumber").asText();
            String amount = transacteaseResponse.get("Amount").asText();

            // Retrieve the customer payment based on order ID
            CustomerPayment customerPayment = customerPaymentRepository.findByOrderId(Long.valueOf(requestId));

            if(responseCode == 000){
                if(transactionStatus.equalsIgnoreCase("SUCCESSFUL") || transactionStatus.equalsIgnoreCase("SUCCESS")){
                    ApplicationLogger.logger.info("Transactease callback successfully received with response code 000 and status: SUCCESS");
                    LocalDateTime requestCompletionTime = LocalDateTime.now();
                    Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                    apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),payload,ResponseEntity.status(HttpStatus.OK).body(payload) , headers , responseTime , requestInitiationTime , customerPayment.getCustomerUsername() !=null ? customerPayment.getCustomerUsername() : null , 1, "POST" , "Success","TRANSACTEASE-CALLBACK",requestId);
                    paymentIntegrationService.updateStatusAndSendToCMS(requestId, transactionReferenceNumber, transactionStatus,null);
                    transactionStatus = "success";  // to send "success" message in ui side
                }else {
                    transactionStatus = "Failed";
                    ApplicationLogger.logger.info("Transactease callback successfully received with response code 000 and status: {}; ",transactionStatus);
                    LocalDateTime requestCompletionTime = LocalDateTime.now();
                    Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                    apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),payload,ResponseEntity.status(HttpStatus.OK).body(payload) , headers , responseTime , requestInitiationTime , customerPayment.getCustomerUsername() !=null ? customerPayment.getCustomerUsername() : null , 1, "POST" , "Success","TRANSACTEASE-CALLBACK",requestId);
                    paymentIntegrationService.updateStatusAndSendToCMS(requestId, transactionReferenceNumber, transactionStatus,null);
                }
            } else {
                transactionStatus = "Failed";
                ApplicationLogger.logger.info("Transactease callback successfully received with status: {}",transactionStatus);
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),payload,ResponseEntity.status(HttpStatus.OK).body(payload) , headers , responseTime , requestInitiationTime , customerPayment.getCustomerUsername() !=null ? customerPayment.getCustomerUsername() : null , 1, "POST" , "Success","TRANSACTEASE-CALLBACK",requestId);
                paymentIntegrationService.updateStatusAndSendToCMS(requestId, transactionReferenceNumber, transactionStatus,"Transaction Failed");
            }

//            if (customerPayment != null) {
//                HashMap<String, String> getPaymentGatewayParemeter = paymentConfigService.getPaymentGatewayParameter(
//                        PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.TRANSACTEASE, customerPayment.getMvnoid());
//                String REDIRECT_CWSC_URL = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.TRANSACTEASEPAY.TRANSACTEASE_REDIRECT_URL);
//                String REDIRECT_TIME_IN_SECONDS = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.TRANSACTEASEPAY.REDIRECT_TIME_IN_SECONDS);
//
//                // Set Model Attributes
//                model.addAttribute("homeRedirectUrl", REDIRECT_CWSC_URL);
//                model.addAttribute("redirectTimeInSeconds", REDIRECT_TIME_IN_SECONDS);
//            }
            LinkedHashMap<String,Object> parameters = new LinkedHashMap<>();
            parameters.put("amount", amount);
            parameters.put("orderId", requestId);
            parameters.put("transactionStatus", transactionStatus);
            parameters.put("currency", transacteaseResponse.get("Currency").asText());
            parameters.put("payment-method", transacteaseResponse.get("PaymentMethod").asText());
            // Add additional model attributes
//            model.addAttribute("status", transactionStatus);
//            model.addAttribute("amount", amount);
//            model.addAttribute("parameters", parameters);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),payload,ResponseEntity.status(HttpStatus.OK).body(payload) , headers , responseTime , requestInitiationTime , customerPayment.getCustomerUsername() !=null ? customerPayment.getCustomerUsername() : null , 1, "POST" , "Success","TRANSACTEASE-CALLBACK",requestId);
            return new ResponseEntity<String>(transactionStatus,HttpStatus.OK);
        } catch (Exception e) {
            ApplicationLogger.logger.error("Error While Handle Transactease Pay callback Data.");
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),payload,ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payload) , headers , responseTime , requestInitiationTime , null , 1, "POST" , e.getMessage(),"TRANSACTEASE-CALLBACK",null);
            return new ResponseEntity<String>("Failed",HttpStatus.OK);
        } finally {
            MDC.clear();
        }
    }


    @PostMapping(value = URLConstants.MpesaUrlConstants.RESULT_CALLBACK_URL)
    public String handleMpesaResultCallback(@RequestBody Object payload, HttpServletRequest request,Model model) {
        MDC.put("type", "Fetch");
        ApplicationLogger.logger.info("::::::::::::::::::::MPESA Result Call Back API Hit::::::::::::::::::::");
        ApplicationLogger.logger.info("Receiving response from Mpesa B2C on result call back "+payload);
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        HttpHeaders headers = new HttpHeaders();
        headers.set("headers",request.getHeaderNames().toString());
        try {
            // Convert payload to JsonNode for easy extraction
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode mpesaResultDTO = objectMapper.valueToTree(payload);

            // Extract values from the JSON payload
            JsonNode resultNode = mpesaResultDTO.get("Result");

            Integer resultCode = resultNode.get("ResultCode").asInt();
            String paymentStatus = resultNode.get("ResultDesc").asText();
            String transactionId = resultNode.get("TransactionID").asText();
            String orderId = resultNode.get("OriginatorConversationID").asText();
            Integer amount = null;

            JsonNode resultParamsArray  = resultNode.path("ResultParameters").path("ResultParameter");
            if (resultParamsArray.isArray()) {
                for (JsonNode param : resultParamsArray) {
                    String key = param.get("Key").asText();
                    String value = param.get("Value").asText();
                    if ("TransactionAmount".equalsIgnoreCase(key)) {
                        amount = Integer.valueOf(value);
                    }
                }
            }

            // Retrieve the customer payment based on order ID
            CustomerPayment customerPayment = customerPaymentRepository.findByOrderId(Long.valueOf(orderId));

            if(resultCode == 0){
                ApplicationLogger.logger.info("Mpesa result callback successfully received with response code 0 and status: SUCCESS");
                paymentStatus="Successful";
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),payload,ResponseEntity.status(HttpStatus.OK).body(payload) , headers , responseTime , requestInitiationTime , customerPayment.getCustomerUsername() !=null ? customerPayment.getCustomerUsername() : null , 1, "POST" , "Success","MPESA-RESULT-CALLBACK",orderId);
                paymentIntegrationService.updateStatusAndSendToCMS(orderId, transactionId, paymentStatus,null);

            } else {
                ApplicationLogger.logger.info("Mpesa result callback successfully received with status: {}",paymentStatus);
                String failureReason = resultNode.get("ResultDesc").asText();
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),payload,ResponseEntity.status(HttpStatus.OK).body(payload) , headers , responseTime , requestInitiationTime , customerPayment.getCustomerUsername() !=null ? customerPayment.getCustomerUsername() : null , 1, "POST" , "Success","MPESA-RESULT-CALLBACK",orderId);
                paymentIntegrationService.updateStatusAndSendToCMS(orderId, transactionId, paymentStatus,failureReason);
            }

            if (customerPayment != null) {
                HashMap<String, String> getPaymentGatewayParemeter = paymentConfigService.getPaymentGatewayParameter(
                        PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.TRANSACTEASE, customerPayment.getMvnoid());
                String REDIRECT_CWSC_URL = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.TRANSACTEASEPAY.TRANSACTEASE_REDIRECT_URL);
                String REDIRECT_TIME_IN_SECONDS = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.TRANSACTEASEPAY.REDIRECT_TIME_IN_SECONDS);

                // Set Model Attributes
                model.addAttribute("homeRedirectUrl", REDIRECT_CWSC_URL);
                model.addAttribute("redirectTimeInSeconds", REDIRECT_TIME_IN_SECONDS);
            }
            LinkedHashMap<String,Object> parameters = new LinkedHashMap<>();
            parameters.put("transactionId", transactionId);
            parameters.put("amount", amount);
            parameters.put("status", paymentStatus);
            // Add additional model attributes
            model.addAttribute("status", paymentStatus);
            model.addAttribute("amount", amount);
            model.addAttribute("parameters", parameters);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),mpesaResultDTO,ResponseEntity.status(HttpStatus.OK).body(payload) , headers , responseTime , requestInitiationTime , customerPayment.getCustomerUsername() !=null ? customerPayment.getCustomerUsername() : null , 1, "POST" , "Success","MPESA-RESULT-CALLBACK",orderId);
            return "report";
        } catch (Exception e) {
            ApplicationLogger.logger.error("Error While Handle MPESA Result callback Data.");
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),payload,ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payload) , headers , responseTime , requestInitiationTime , null , 1, "POST" , e.getMessage(),"MPESA-RESULT-CALLBACK",null);
            return "report";
        } finally {
            MDC.clear();
        }
    }

    @PostMapping(value = URLConstants.MpesaUrlConstants.QUEUE_CALLBACK_URL)
    public String handleMpesaQueueTimeOutCallback(@RequestBody Object payload, HttpServletRequest request,Model model) {
        MDC.put("type", "Fetch");
        ApplicationLogger.logger.info("::::::::::::::::::::MPESA Queue Time Out Call Back API Hit::::::::::::::::::::");
        ApplicationLogger.logger.info("Receiving response from Mpesa B2C on Queue Timeout call back "+payload);
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        HttpHeaders headers = new HttpHeaders();
        headers.set("headers",request.getHeaderNames().toString());
        try {
            // Convert payload to JsonNode for easy extraction
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode mpesaResultDTO = objectMapper.valueToTree(payload);

            // Extract values from the JSON payload
            JsonNode resultNode = mpesaResultDTO.get("Result");

            Integer resultCode = resultNode.get("ResultCode").asInt();
            String paymentStatus = resultNode.get("ResultDesc").asText();
            String transactionId = resultNode.get("TransactionID").asText();
            String orderId = resultNode.get("OriginatorConversationID").asText();


            // Retrieve the customer payment based on order ID
            CustomerPayment customerPayment = customerPaymentRepository.findByOrderId(Long.valueOf(orderId));

            if( resultCode == 2001){
                ApplicationLogger.logger.info("Mpesa Queue Timeout callback successfully received with response code 2001 and status: FAILED");
                paymentStatus="FAILED";
                String failureReason = resultNode.get("ResultDesc").asText();
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),payload,ResponseEntity.status(HttpStatus.OK).body(payload) , headers , responseTime , requestInitiationTime , customerPayment.getCustomerUsername() !=null ? customerPayment.getCustomerUsername() : null , 1, "POST" , "Success","MPESA-QUEUE-TIMEOUT-CALLBACK",orderId);
                paymentIntegrationService.updateStatusAndSendToCMS(orderId, transactionId, paymentStatus,failureReason);
            }else {
                ApplicationLogger.logger.info("Mpesa Queue Timeout callback successfully received with response code 000 and status: {}; ",paymentStatus);
                paymentStatus="Successful";
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),payload,ResponseEntity.status(HttpStatus.OK).body(payload) , headers , responseTime , requestInitiationTime , customerPayment.getCustomerUsername() !=null ? customerPayment.getCustomerUsername() : null , 1, "POST" , "Success","MPESA-QUEUE-TIMEOUT-CALLBACK",orderId);
                paymentIntegrationService.updateStatusAndSendToCMS(orderId, transactionId, paymentStatus,null);
            }


            if (customerPayment != null) {
                HashMap<String, String> getPaymentGatewayParemeter = paymentConfigService.getPaymentGatewayParameter(
                        PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.TRANSACTEASE, customerPayment.getMvnoid());
                String REDIRECT_CWSC_URL = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.TRANSACTEASEPAY.TRANSACTEASE_REDIRECT_URL);
                String REDIRECT_TIME_IN_SECONDS = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.TRANSACTEASEPAY.REDIRECT_TIME_IN_SECONDS);

                // Set Model Attributes
                model.addAttribute("homeRedirectUrl", REDIRECT_CWSC_URL);
                model.addAttribute("redirectTimeInSeconds", REDIRECT_TIME_IN_SECONDS);
            }
            LinkedHashMap<String,Object> parameters = new LinkedHashMap<>();
            parameters.put("transactionId", transactionId);
            parameters.put("status", paymentStatus);

            // Add additional model attributes
            model.addAttribute("parameters", parameters);
            model.addAttribute("status", paymentStatus);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),mpesaResultDTO,ResponseEntity.status(HttpStatus.OK).body(payload) , headers , responseTime , requestInitiationTime , customerPayment.getCustomerUsername() !=null ? customerPayment.getCustomerUsername() : null , 1, "POST" , "Success","MPESA-QUEUE-TIMEOUT-CALLBACK",orderId);
            return "report";
        } catch (Exception e) {
            ApplicationLogger.logger.error("Error While Handle MPESA Result callback Data.");
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),payload,ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payload) , headers , responseTime , requestInitiationTime , null , 1, "POST" , e.getMessage(),"MPESA-QUEUE-TIMEOUT-CALLBACK",null);
            return "report";
        } finally {
            MDC.clear();
        }
    }

    @PostMapping(value = URLConstants.MpesaUrlConstants.EXPRESS_SIMULATE_CALLBACK)
    @ResponseBody
    public String handleMpesaExpressSimulateCallback(@RequestBody String payload ,HttpServletRequest request) throws Exception {
        MDC.put("type", "Fetch");
        ApplicationLogger.logger.info("::::::::::::::::::::MPESA EXPRESS SIMULATE Call Back API Hit::::::::::::::::::::");
        ApplicationLogger.logger.info("Receiving response from Mpesa EXPRESS SIMULATE call back "+payload);
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        HttpHeaders headers = new HttpHeaders();
        headers.set("headers",request.getHeaderNames().toString());
        ObjectMapper objectMapper = new ObjectMapper();
        String transactionStatus = null;
        LocalDateTime requestCompletionTime = LocalDateTime.now();
        LinkedHashMap<String,Object> parameters = new LinkedHashMap<>();
        HashMap<String,String>response=new HashMap<>();
        response.put("status","success");
        try {
            JsonNode jsonNode = objectMapper.readTree(payload.toString());
            JsonNode callbackResponse = jsonNode.path("Body").path("stkCallback");
            JsonNode responseItem = callbackResponse.path("CallbackMetadata").path("Item");
            Integer resultCode = callbackResponse.path("ResultCode").asInt();
            String resultMessage = callbackResponse.path("ResultDesc").asText();
            String pgTranscationId = callbackResponse.path("CheckoutRequestID").asText();
            String merchantRequestID = callbackResponse.path("MerchantRequestID").asText();

            HashMap<String, Object> itemsMap = new HashMap<>();
            if(responseItem.isArray()){
                for(JsonNode item : responseItem){
                    String key = item.path("Name").asText();
                    String value = item.path("Value").asText();
                    itemsMap.put(key, value);
                }
            }

            CustomerPayment customerPayment = new CustomerPayment();
            if(pgTranscationId != null){
                List<CustomerPayment> customerPaymentList = customerPaymentRepository.findAllByCheckoutRequestId(pgTranscationId);
                if(customerPaymentList != null && !customerPaymentList.isEmpty()){
                    customerPayment = customerPaymentList.get(0);
                    HashMap<String, String> getPaymentGatewayParemeter = paymentConfigService.getPaymentGatewayParameter(
                            PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.MPESA, customerPayment.getMvnoid());
                    String REDIRECT_URL = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.MPESA.CWSC_REDIRECT_URL);
                    String REDIRECT_TIME_IN_SECONDS = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.MPESA.REDIRECT_TIME_IN_SECONDS);

                    // Set Model Attributes
//                    model.addAttribute("homeRedirectUrl", REDIRECT_URL);
//                    model.addAttribute("redirectTimeInSeconds", REDIRECT_TIME_IN_SECONDS);
                }else{
                    parameters.put("Message","No transaction found");
//                    model.addAttribute("parameters",parameters);
                    return objectMapper.writeValueAsString(response);
                }

            }

            System.out.println("itemsMap" + itemsMap);
            if(resultCode == 0){
                ApplicationLogger.logger.info("Mpesa EXPRESS SIMULATE CALLBACK successfully received with response code 0");
                String receipt = itemsMap.get("MpesaReceiptNumber").toString();
                transactionStatus ="Successful";
                requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),payload,ResponseEntity.status(HttpStatus.OK).body(payload) , headers , responseTime , requestInitiationTime , customerPayment.getCustomerUsername() !=null ? customerPayment.getCustomerUsername() : null , 1, "POST" , null,"MPESA-EXPRESS-SIMULATE-CALLBACK",customerPayment.getOrderId().toString());
                paymentIntegrationService.updateStatusAndSendToCMS(customerPayment.getOrderId().toString(), receipt, transactionStatus,null);
            }else if(resultCode == 1032){
                ApplicationLogger.logger.info("Mpesa EXPRESS SIMULATE CALLBACK successfully received with response code 1032");
                transactionStatus ="Declined";
                requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),payload,ResponseEntity.status(HttpStatus.OK).body(payload) , headers , responseTime , requestInitiationTime , customerPayment.getCustomerUsername() !=null ? customerPayment.getCustomerUsername() : null , 1, "POST" , resultMessage,"MPESA-EXPRESS-SIMULATE-CALLBACK",customerPayment.getOrderId().toString());                paymentIntegrationService.updateStatusAndSendToCMS(customerPayment.getOrderId().toString(), pgTranscationId, transactionStatus,resultMessage);
            }
            else if(resultCode == 1019){
                ApplicationLogger.logger.info("Mpesa EXPRESS SIMULATE CALLBACK successfully received with response code 1032");
                transactionStatus ="Declined";
                requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),payload,ResponseEntity.status(HttpStatus.OK).body(payload) , headers , responseTime , requestInitiationTime , customerPayment.getCustomerUsername() !=null ? customerPayment.getCustomerUsername() : null , 1, "POST" , resultMessage,"MPESA-EXPRESS-SIMULATE-CALLBACK",customerPayment.getOrderId().toString());                paymentIntegrationService.updateStatusAndSendToCMS(customerPayment.getOrderId().toString(), pgTranscationId, transactionStatus,resultMessage);
            }
            else if(resultCode == 1037){
                ApplicationLogger.logger.info("Mpesa EXPRESS SIMULATE CALLBACK successfully received with response code 1032");
                transactionStatus ="Declined";
                requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),payload,ResponseEntity.status(HttpStatus.OK).body(payload) , headers , responseTime , requestInitiationTime , customerPayment.getCustomerUsername() !=null ? customerPayment.getCustomerUsername() : null , 1, "POST" , resultMessage,"MPESA-EXPRESS-SIMULATE-CALLBACK",customerPayment.getOrderId().toString());                paymentIntegrationService.updateStatusAndSendToCMS(customerPayment.getOrderId().toString(), pgTranscationId, transactionStatus,resultMessage);
            }
            else {
                ApplicationLogger.logger.info("Mpesa EXPRESS SIMULATE CALLBACK successfully received ");
                transactionStatus ="Failed";
                requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),payload,ResponseEntity.status(HttpStatus.OK).body(payload) , headers , responseTime , requestInitiationTime , customerPayment.getCustomerUsername() !=null ? customerPayment.getCustomerUsername() : null , 1, "POST" , resultMessage,"MPESA-EXPRESS-SIMULATE-CALLBACK",customerPayment.getOrderId().toString());                paymentIntegrationService.updateStatusAndSendToCMS(customerPayment.getOrderId().toString(), pgTranscationId, transactionStatus,resultMessage);
            }
            parameters.put("Transcation-Id", pgTranscationId);
            parameters.put("Transaction-Status", transactionStatus);
            if(itemsMap !=null && itemsMap.size()>0){
                parameters.put("Amount", itemsMap.get("Amount").toString());
                parameters.put("PhoneNumber",itemsMap.get("PhoneNumber").toString());
                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
                LocalDateTime dateTime = LocalDateTime.parse(itemsMap.get("TransactionDate").toString(), inputFormatter);
                parameters.put("TransactionDate", dateTime.format(outputFormatter));
            }else {
                parameters.put("Message",resultMessage);
            }
//            model.addAttribute("parameters",parameters);
            requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),payload,ResponseEntity.status(HttpStatus.OK).body(payload) , headers , responseTime , requestInitiationTime , customerPayment.getCustomerUsername() !=null ? customerPayment.getCustomerUsername() : null , 1, "POST" , null,"MPESA-EXPRESS-SIMULATE-CALLBACK",customerPayment.getOrderId().toString());
        } catch (Exception e) {
            e.printStackTrace();
            requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),payload,ResponseEntity.status(HttpStatus.OK).body(payload) , headers , responseTime , requestInitiationTime , null , 1, "POST" , e.getMessage(),"MPESA-EXPRESS-SIMULATE-CALLBACK",null);
        }
        return objectMapper.writeValueAsString(response);
    }

    @GetMapping("/FetchPaymentReceipt")
    @ResponseBody
    public GenericDataDTO FetchPaymentReceipt(@RequestParam("pgtransactionid")  String pgTransactionId) {
        GenericDataDTO response = new GenericDataDTO();

        try {
            if (pgTransactionId == null || pgTransactionId.isEmpty()) {
                response.setResponseCode(HttpStatus.BAD_REQUEST.value());
                response.setResponseMessage("Transaction ID is required");
                return response;
            }

            // Fetch DTO from service
            GenericDataDTO receipt = onlinePayAuditService.fetchPaymentReceipt(pgTransactionId);

            if (receipt != null) {
                response.setResponseCode(HttpStatus.OK.value());
                response.setResponseMessage("Fetched successfully");
                response.setData(receipt);
            } else {
                response.setResponseCode(HttpStatus.NOT_FOUND.value());
                response.setResponseMessage("No data found for transaction ID: " + pgTransactionId);
            }

        } catch (Exception e) {
            response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseMessage("Error: " + e.getMessage());
        }
        return response;
    }

}
