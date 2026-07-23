package com.savbill.integrationsystem.schedulers;

import com.savbill.integrationsystem.AirtelIntigration.AirtelIntigrationService;
import com.savbill.integrationsystem.PaymentConfig.model.PaymentGatewayConfigurationConstant;
import com.savbill.integrationsystem.PaymentConfig.service.PaymentConfigService;
import com.savbill.integrationsystem.PaymentIntegration.Model.CustomerPayment;
import com.savbill.integrationsystem.PaymentIntegration.Repository.CustomerPaymentRepository;
import com.savbill.integrationsystem.billgen.model.CreditDocumentDTO;
import com.savbill.integrationsystem.billgen.model.CustomerDTO;
import com.savbill.integrationsystem.billgen.model.DebitDocumentDTO;
import com.savbill.integrationsystem.billgen.service.CreditdocService;
import com.savbill.integrationsystem.billgen.service.CustomerService;
import com.savbill.integrationsystem.billgen.service.DebitDocumentService;
import com.savbill.integrationsystem.core.CommonConstant;
import com.savbill.integrationsystem.core.utillity.log.ApplicationLogger;
import com.savbill.integrationsystem.paymentStatus.Service.PaymentStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

@Log4j2
@Component
@RequiredArgsConstructor
public class SavbillIntegrationSchedulers {

    private final DebitDocumentService debitDocumentService;

    private final CustomerService customerService;

    private final CreditdocService creditdocService;

    @Qualifier("sendBillThreadPoolExecutor")
    private final ThreadPoolExecutor sendBillThreadPoolExecutor;

    @Qualifier("sendCreditNoteThreadPoolExecutor")
    private final ThreadPoolExecutor sendCreditNoteThreadPoolExecutor;

    @Autowired
    private CustomerPaymentRepository customerPaymentRepository;

    @Autowired
    private AirtelIntigrationService airtelIntigrationService;

    @Autowired
    private PaymentConfigService paymentConfigService;
    @Autowired
    private PaymentStatusService paymentStatusService;

    @Scheduled(cron = "${cronjobtimeforsendbill}")
    public synchronized void sendBill() {
        log.debug("Inside sendBill");
        List<DebitDocumentDTO> listNotSynched = debitDocumentService.findByIrdSyncAndIsDeleteFalse(CommonConstant.IRD_SYNCH_NO);
        for (DebitDocumentDTO debitDocumentDTO : listNotSynched) {
            SendBillThread sendBillThread = new SendBillThread(debitDocumentDTO, debitDocumentService);
            sendBillThreadPoolExecutor.execute(sendBillThread);
        }
        while (sendBillThreadPoolExecutor.getActiveCount() != 0) {
            try {
                log.debug("sendBillThreadPoolExecutor Active Count: {}", sendBillThreadPoolExecutor.getActiveCount());
                Thread.sleep(10000);
            } catch (InterruptedException exception) {
                log.error("Exception occurred while puttin thread on sleep", exception);
            }
        }
        log.debug("Leaving sendBill");
    }

    @Scheduled(cron = "${cronjobtimeforsendcreditnote}")
    public void sendCreditNote() {
        log.debug("Inside sendCreditNote");
        List<CreditDocumentDTO> listNotSynched = creditdocService.findByIrdSyncAndTypeAndIsDeleteFalse(CommonConstant.IRD_SYNCH_NO, CommonConstant.CREDITDOC_TYPE_CREDIT_NOTE);
        for (CreditDocumentDTO creditDocumentDTO : listNotSynched) {
            SendCreditNoteThread sendCreditNoteThread = new SendCreditNoteThread(creditDocumentDTO, debitDocumentService, customerService, creditdocService);
            sendCreditNoteThreadPoolExecutor.execute(sendCreditNoteThread);
        }

        while (sendCreditNoteThreadPoolExecutor.getActiveCount() != 0) {
            try {
                log.debug("sendCreditNoteThreadPoolExecutor Active Count: {}", sendCreditNoteThreadPoolExecutor.getActiveCount());
                Thread.sleep(10000);
            } catch (InterruptedException exception) {
                log.error("Exception occurred while puttin thread on sleep", exception);
            }
        }
        log.debug("Leaving sendCreditNote");
    }

    @Scheduled(cron = "${cronjobtimeforairtelpaymentstatus}")
    public void airtelPaymentStatusCheck() {
        log.warn("**************** Started Airtel Payment Status Cheking ****************");
        try{
//            List<CustomerPayment> paymentList = customerPaymentRepository.findAllByPgTransactionIdAndIsScheduled();
            List<CustomerPayment> paymentList = customerPaymentRepository.findPendingPgTransactionPayments();

            if(!paymentList.isEmpty()) {
                log.warn("Payment list found with pending status and isscheduled false with size: "+paymentList.size());




//                HashMap<String, String> getPaymentParameter = paymentConfigService.getPaymentGatewayParameter(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.AIRTEL, paymentList.get(0).getMvnoid());
//                AirtelAuthorizationRequestDTO airtelAuthorizationRequestDTO = airtelIntigrationService.createAirtelAuthorizationPayload(getPaymentParameter);
//                AirtelApiAuthorizationResponseDTO airtelApiAuthorizationResponseDTO = airtelIntigrationService.getTokenfromAirtelMoney(airtelAuthorizationRequestDTO, getPaymentParameter, paymentList.get(0).getMvnoid());
                paymentList.forEach(payment -> payment.setIsScheduled(true));
                customerPaymentRepository.saveAll(paymentList);
                for (CustomerPayment payment : paymentList) {
                    switch (payment.getMerchantName()) {
                        case PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.AIRTEL:
                        case PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.AIRTEL_USSD_PUSH:
                            paymentStatusService.processAirtelPayment(payment);
                            break;
                        case PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.MOMO_PAY:
                        case PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.MOMO_PAY_USSD_PUSH:
                            paymentStatusService.processMoMoPayPayment(payment);
                            break;
                        default:
                            log.info("Unknown merchant: {} for Order ID: {}", payment.getMerchantName(), payment.getOrderId());
                    }
//                    airtelIntigrationService.checkTransactionStatusApi(payment, getPaymentParameter, airtelApiAuthorizationResponseDTO);
                }
            }
            else{
                log.warn("**************** No payment is found for ***************");
            }

        }catch (Exception e){
            ApplicationLogger.logger.error("Failed to fetch MomoPe Transaction status " + e.getMessage());
        }
        log.warn("**************** End Airtel Payment Status Cheking ****************");
    }

    @RequiredArgsConstructor
    public class SendBillThread implements Runnable {

        private final DebitDocumentDTO debitDocumentDTO;
        private final DebitDocumentService debitDocumentServiceThread;

        @Override
        public void run() {
            log.debug("Inside run debitDocumentDTOid: {} ", debitDocumentDTO.getId());
            List<CustomerDTO> listCustomerDTOS = customerService.findById(debitDocumentDTO.getCustomerId());
            if (CollectionUtils.isEmpty(listCustomerDTOS)) {
                log.debug("Leaving run debitDocumentDTOid: {} listCustomerDTOS: {}", debitDocumentDTO.getId(), listCustomerDTOS.size());
                return;
            }
            CustomerDTO customerDTO = listCustomerDTOS.get(0);
            debitDocumentServiceThread.sendBillTOGovernment(customerDTO, debitDocumentDTO, Boolean.FALSE);
            log.debug("Leaving run debitDocumentDTOid: {} ", debitDocumentDTO.getId());
        }
    }

    @RequiredArgsConstructor
    public class SendCreditNoteThread implements Runnable {

        private final CreditDocumentDTO creditDocumentDTO;
        private final DebitDocumentService debitDocumentService;
        private final CustomerService customerService;
        private final CreditdocService creditdocService;

        @Override
        public void run() {
            log.debug("Inside run creditDocumentDTOid: {} customer: {}", creditDocumentDTO.getId(), creditDocumentDTO.getCustomer());
            List<DebitDocumentDTO> listDebitDocumentDTOS = debitDocumentService.findByDebitdocumentidAndIsDeleteFalse(creditDocumentDTO.getInvoiceId());
            if (CollectionUtils.isEmpty(listDebitDocumentDTOS)) {
                log.debug("Leaving run creditDocumentDTO id: {} listDebitDocumentDTOS: {}", creditDocumentDTO.getId(), listDebitDocumentDTOS.size());
                return;
            }
            DebitDocumentDTO debitDocumentDTO = listDebitDocumentDTOS.get(0);
            List<CustomerDTO> listCustomerDTOS = customerService.findById(creditDocumentDTO.getCustomer());
            if (CollectionUtils.isEmpty(listCustomerDTOS)) {
                log.debug("Leaving run creditDocumentDTO id: {} listCustomerDTOS: {}", creditDocumentDTO.getId(), listCustomerDTOS.size());
                return;
            }
            CustomerDTO customerDTO = listCustomerDTOS.get(0);
            creditdocService.sendCreditToGovernment(customerDTO, debitDocumentDTO, creditDocumentDTO, Boolean.FALSE);
            log.debug("Leaving run creditDocumentDTO id: {}", creditDocumentDTO.getId());

        }
    }
}
