package com.savbill.integrationsystem.paymentStatus.Service;

import com.savbill.integrationsystem.AirtelIntigration.AirtelApiAuthorizationResponseDTO;
import com.savbill.integrationsystem.AirtelIntigration.AirtelAuthorizationRequestDTO;
import com.savbill.integrationsystem.AirtelIntigration.AirtelIntigrationService;
import com.savbill.integrationsystem.PaymentConfig.model.PaymentGatewayConfigurationConstant;
import com.savbill.integrationsystem.PaymentConfig.service.PaymentConfigService;
import com.savbill.integrationsystem.PaymentIntegration.DTO.*;
import com.savbill.integrationsystem.PaymentIntegration.DTO.*;
import com.savbill.integrationsystem.PaymentIntegration.Model.CustomerPayment;
import com.savbill.integrationsystem.PaymentIntegration.Repository.CustomerPaymentRepository;
import com.savbill.integrationsystem.PaymentIntegration.Service.*;
import com.savbill.integrationsystem.PaymentIntegration.Service.*;
import com.savbill.integrationsystem.kbzIntegration.KbzIntegrationService;
import com.savbill.integrationsystem.kbzIntegration.KbzPayPayload;
import com.savbill.integrationsystem.kbzIntegration.KbzPayRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PaymentStatusService {
    @Autowired
    private CustomerPaymentRepository customerPaymentRepository;

    @Autowired
    private AirtelIntigrationService airtelIntigrationService;
    @Autowired
    private PaymentConfigService paymentConfigService;

    @Autowired
    private MoMoPePaymentService moMoPePaymentService;

    @Autowired
    private SelecomPaymentService selecomPaymentService;

    @Autowired
    private KbzIntegrationService kbzIntegrationService;

    private static final Logger logger = LoggerFactory.getLogger(PaymentStatusService.class);
    @Autowired
    private PayStackPaymentService payStackPaymentService;
    @Autowired
    private TransacteasePaymentService transacteasePaymentService;
    @Autowired
    private OnePayIntegrationService onePayIntegrationService;
    private final ObjectMapper objectMapper = new ObjectMapper();


    public List<CustomerPayment> getOnlinePayAuditListByOrderId(Long orderId) {
        logger.info("::::::::::::::::: Inside getOnlinePayAuditListByOrderId Method for Order ID: {}", orderId);

        try {
            List<CustomerPayment> onlinePayAudits = customerPaymentRepository.findCustomerPaymentByOrderId(orderId);

            if (onlinePayAudits == null || onlinePayAudits.isEmpty()) {
                logger.info("No payment records found for Order ID: {}", orderId);
                return Collections.emptyList();
            }

            CustomerPayment firstPayment = onlinePayAudits.get(0);
            String merchantName = firstPayment.getMerchantName();

            try {
                logger.info("::::::::::::::::: Inside getOnlinePayAuditListByOrderId Method merchantName : {}", merchantName);
                switch (merchantName) {
                    case PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.AIRTEL:
                    case PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.AIRTEL_USSD_PUSH:
                        logger.info("::::::::::::::::: Process AIRTEL : {}", merchantName);
                        processAirtelPayment(firstPayment);
                        break;
                    case PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.MOMO_PAY:
                    case PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.MOMO_PAY_USSD_PUSH:
                        logger.info("::::::::::::::::: Process MOMO : {}", merchantName);
                        processMoMoPayPayment(firstPayment);
                        break;
                    case PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.SELCOM:
                        logger.info("::::::::::::::::: Process SELCOM : {}", merchantName);
                        processSelcomPayment(firstPayment);
                        break;
                    case PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.PAYSTACK:
                        logger.info("::::::::::::::::process PAYSTACK : {}", merchantName);
                        payStackPaymentService.verifyTransaction(orderId.toString());
                        break;
                    case PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.KBZ_PAY:
                        logger.info(":::::::::::::::: Process KBZPAY : {}", merchantName);
                        processKbzPayPayment(firstPayment);
                        break;
                    case PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.TRANSACTEASE:
                        logger.info(":::::::::::::::: Process Transactease : {}", merchantName);
                        processTransacteasePayment(firstPayment);
                        break;
                    case PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.ONE_PAY:
                        logger.info(":::::::::::::::: Process OnePay : {}", merchantName);
                        processOnePayPayment(firstPayment);
                        break;
                    default:
                        logger.info("Unknown merchant: {} for Order ID: {}", merchantName, orderId);
                }
            } catch (Exception e) {
                logger.error("Error processing payment for Order ID: {}: {}", orderId, e.getMessage(), e);
                return Collections.emptyList();
            }

            return customerPaymentRepository.findCustomerPaymentByOrderId(orderId);
        } catch (Exception e) {
            logger.error("Error retrieving online payment audit list for Order ID: {}: {}", orderId, e.getMessage(), e);
            return Collections.emptyList();
        }
    }
    public void processAirtelPayment(CustomerPayment payment) {
        try {
            HashMap<String, String> paymentParameters = paymentConfigService.getPaymentGatewayParameter(
                    PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.AIRTEL, payment.getMvnoid());

            AirtelAuthorizationRequestDTO authorizationRequest = airtelIntigrationService.createAirtelAuthorizationPayload(paymentParameters);
            AirtelApiAuthorizationResponseDTO authorizationResponse = airtelIntigrationService.getTokenfromAirtelMoney(
                    authorizationRequest, paymentParameters, payment.getMvnoid(), payment);

            airtelIntigrationService.checkTransactionStatusApi(payment, paymentParameters, authorizationResponse);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error processing Airtel payment for Order ID: {}: {}", payment.getOrderId(), e.getMessage(), e);
        }
    }

    public void processMoMoPayPayment(CustomerPayment payment) {
        try {
            HashMap<String, String> paymentGatewayParameter = paymentConfigService.getPaymentGatewayParameter(
                    PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.MOMO_PAY, payment.getMvnoid());
            String gatewayUrl = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.MOMOPE.MOMOPE_GATEWAY_URL);
            String subscriptionKey = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.MOMOPE.MOMOPE_SUBSCRIPTION_KEY);
//            String callBackUrl = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.MOMOPE.MOMOPE_CALLBACK_URL);
            String targetEnviroment = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.MOMOPE.MOMOPE_TARGET_ENVIROMENT);
//            String currency = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.MOMOPE.MOMOPE_CURRENCY);
//            String payerMessage = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.MOMOPE.MOMOPE_PAYERMESSAGE);
//            String payeeNote = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.MOMOPE.MOMOPE_PAYEENOTE);
            String apiKey = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.MOMOPE.MOMOPE_API_KEY);
            String apiUser = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.MOMOPE.MOMOPE_API_USER);
            MoMoPeDTO moMoPeDTO = new MoMoPeDTO();
            moMoPeDTO.setGatewayUrl(gatewayUrl);
            moMoPeDTO.setApiKey(apiKey);
            moMoPeDTO.setApiUser(apiUser);
            moMoPeDTO.setSubscriptionKey(subscriptionKey);
            moMoPeDTO.setTargetEnvironment(targetEnviroment);
            /*fetch token to perform payment status api for momo pay*/
            String accessToken = moMoPePaymentService.getAccessToken(moMoPeDTO, null, payment.getMvnoid(), payment);
            moMoPePaymentService.singlePaymentStatus(moMoPeDTO, accessToken, payment);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error processing MoMo Pay payment for Order ID: {}: {}", payment.getOrderId(), e.getMessage(), e);
        }
    }

    public void processSelcomPayment(CustomerPayment payment) {
        try {
            logger.info("*************** Inside processSelcomPayment for orderId : " + payment.getOrderId());
            HashMap<String, String> paymentGatewayParameter = paymentConfigService.getPaymentGatewayParameter(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.SELCOM, payment.getMvnoid());
            String apiKey = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.SELCOMPAY.SELCOM_API_KEY);
            String secretKey = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.SELCOMPAY.SELCOM_SECRET_KEY);
            String gatewayUrl = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.SELCOMPAY.SELCOM_GATEWAY_URL);

            ObjectMapper objectMapper = new ObjectMapper();

            ObjectNode jsonNode = objectMapper.createObjectNode();
            jsonNode.put("order_id", payment.getOrderId());
            String jsonPayload = objectMapper.writeValueAsString(jsonNode);
            SelcomPayDTO dto = new SelcomPayDTO();
            dto.setApiKey(apiKey);
            dto.setSecretKey(secretKey);
            dto.setGatewayUrl(gatewayUrl);
            dto.setJsonPayload(jsonPayload);
            selecomPaymentService.orderStatus(dto);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error processing SELCOM Pay payment for Order ID: {}: {}", payment.getOrderId(), e.getMessage(), e);
        }
        logger.info("*************** completed call processSelcomPayment *****************");
    }
    public void processTransacteasePayment(CustomerPayment payment) {
        try {
            CustomerPaymentDTO customerPaymentDTO = new CustomerPaymentDTO();
            customerPaymentDTO.setMvnoId(payment.getMvnoid());
            customerPaymentDTO.setOrderId(payment.getOrderId().toString());
            TransacteasePayDTO transacteasePayDTO = transacteasePaymentService.fetchGatewayParameters(payment.getMerchantName(), payment.getMvnoid());
            transacteasePaymentService.callLoginApi(customerPaymentDTO, transacteasePayDTO, payment.getOrderId().toString(),payment);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("error processing selcom payment for Order ID: {}: {}", payment.getOrderId(), e.getMessage(), e);
        }
    }

    public void processKbzPayPayment(CustomerPayment payment) throws Exception {
        try {
            logger.info("*************** Inside processKbzPayPayment for orderId : " + payment.getOrderId());
            HashMap<String, String> paymentGatewayParameter = paymentConfigService.getPaymentGatewayParameter(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.KBZ_PAY, payment.getMvnoid());
            String merchantId = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.KBZPAY.KBZPAY_MERCHANT_CODE);
            String secretKey = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.KBZPAY.KBZPAY_APP_ID);
            String appKey = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.KBZPAY.KBZPAY_APP_KEY);
            String gatewayUrl = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.KBZPAY.KBZPAY_GATEWAY_URL);
            KbzPayRequest kbzPayRequest = new KbzPayRequest();
            kbzPayRequest.setAppKey(appKey);
            kbzPayRequest.setGatewayUrl(gatewayUrl);
            // set KbzPayload to call query order api
            KbzPayPayload kbzPayPayload = new KbzPayPayload();
            KbzPayPayload.BizContent bizContent = new KbzPayPayload.BizContent();
            bizContent.setAppId(secretKey);
            bizContent.setMerchantCode(merchantId);
            bizContent.setMerchOrderId(payment.getOrderId().toString());
            kbzPayPayload.setBizContent(bizContent);
            kbzPayRequest.setKbzPayPayload(kbzPayPayload);

            kbzIntegrationService.queryOrder(kbzPayRequest, payment.getMvnoid(),payment);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error processing KBZPay payment for Order ID: {}: {}", payment.getOrderId(), e.getMessage(), e);

        }
    }
    public void processOnePayPayment(CustomerPayment payment) {
        try {
            CustomerPaymentDTO customerPaymentDTO = new CustomerPaymentDTO();
            customerPaymentDTO.setMvnoId(payment.getMvnoid());
            customerPaymentDTO.setOrderId(payment.getOrderId().toString());
            customerPaymentDTO.setPayerMobileNumber(payment.getPayerMobileNumber());
            OnePayDto onePayDto = onePayIntegrationService.fetchGatewayParameters(customerPaymentDTO);
            String jsonPayload = onePayDto.getJsonPayload(); // This is a JSON string
            JsonNode rootNode = objectMapper.readTree(jsonPayload);

            // Now extract individual values
            String merchantId = rootNode.path("MerchantUserId").asText();
            String channel = rootNode.path("Channel").asText();
            String onepayPhoneNo = rootNode.path("OnepayPhoneNo").asText();
            onePayDto.setChannel(channel);
            onePayDto.setOnepayPhoneNo(onepayPhoneNo);
            onePayDto.setMerchantUserId(merchantId);

            onePayIntegrationService.orderStatus(customerPaymentDTO,onePayDto,payment.getPgTransactionId());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("error processing OnePay payment for Order ID: {}: {}", payment.getOrderId(), e.getMessage(), e);
        }
    }

}

