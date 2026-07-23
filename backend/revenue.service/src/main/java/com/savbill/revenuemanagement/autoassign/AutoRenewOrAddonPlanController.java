package com.savbill.revenuemanagement.autoassign;

import com.savbill.revenuemanagement.core.constants.APIConstants;
import com.savbill.revenuemanagement.core.constants.UrlConstants;
import com.savbill.revenuemanagement.core.dto.common.GenericDataDTO;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocument;
import com.savbill.revenuemanagement.core.entity.debitdoc.TrialDebitDocument;
import com.savbill.revenuemanagement.core.exceptions.CustomValidationException;
import com.savbill.revenuemanagement.core.repository.customer.CustomersRepository;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocRepository;
import com.savbill.revenuemanagement.core.service.prepaid.CustomerLedgerDtlsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


@RestController
@RequestMapping(UrlConstants.BASE_API_URL)
public class AutoRenewOrAddonPlanController {

    private static final Logger logger = LoggerFactory.getLogger(AutoRenewOrAddonPlanController.class);

    @Autowired
    private DebitDocRepository debitDocumentRepository;

    @Autowired
    private AutoRenewOrAddonPlanService autoRenewOrAddonPlanService;

    @Autowired
    private CustomersRepository customersRepository;
    @Autowired
    CustomerLedgerDtlsService customerLedgerDtlsService;

    @PostMapping("/autoAdjustPaymentAgainstInvoice")
    public HashMap<String, Object> autoAdjustPaymentAgainstInvoice(@RequestBody AutoAdjustPaymentRequest autoAdjustPaymentRequest, HttpServletRequest req) throws Exception {

        HashMap<String, Object> response = new HashMap<>();
        try {
            DebitDocument debitDocument = debitDocumentRepository.findById(autoAdjustPaymentRequest.getDebitDocument().getId()).get();
            autoRenewOrAddonPlanService.autoAdjustPaymentAgainstInvoice(autoAdjustPaymentRequest.getCustomerBillingMessage(),debitDocument,autoAdjustPaymentRequest.getTrialDebitDocument());
            response.put("isSaved",true);
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            response.put("isSaved",false);
        } catch (Exception ex) {
            ex.printStackTrace();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            response.put("isSaved",false);
        }
        return response;
    }

    @PostMapping("/getCurrentWalletAmountByCustId/{customerId}")
    public HashMap<String, Object> getCurrentWalletAmountByCustId(@PathVariable Integer customerId,HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            CustomerWalletPojo walletPojo = autoRenewOrAddonPlanService.getCurrentWalletAmountByCustomerId(customerId.longValue());
            RESP_CODE = APIConstants.SUCCESS;
            response.put("walletPojo",walletPojo);
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
              } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            }
        finally {
        }
        return response;
    }


    @GetMapping(value = "/getFuturePlanList/{customerId}")
    public GenericDataDTO getFuturePlanList(@PathVariable Integer customerId, @RequestParam(name = "isNotChangePlan", required = true, defaultValue = "false") Boolean isNotChangePlan) {
        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            return GenericDataDTO.getGenericDataDTO(autoRenewOrAddonPlanService.getFuturePlanListByCustomerId(customerId));
        } catch (Exception ex) {
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @GetMapping(value = "/getActivePlanList/{customerId}")
    public GenericDataDTO getActivePlanList(@PathVariable Integer customerId, @RequestParam(name = "isNotChangePlan", required = true, defaultValue = "false") Boolean isNotChangePlan) {
        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            return GenericDataDTO.getGenericDataDTO(autoRenewOrAddonPlanService.getActivePlanListByCustomerId(customerId));
        } catch (Exception ex) {
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @GetMapping(value = "/getExpirePlanList/{customerId}")
    public GenericDataDTO getExpirePlanList(@PathVariable Integer customerId, @RequestParam(name = "isNotChangePlan", required = true, defaultValue = "false") Boolean isNotChangePlan) {
        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            return GenericDataDTO.getGenericDataDTO(autoRenewOrAddonPlanService.getExpirePlanListByCustomerId(customerId));
        } catch (Exception ex) {
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @GetMapping(value = "/getPlanPriceByPlanId/{planId}")
    public Double getPlanPriceByPlanId(@PathVariable Integer planId, @RequestParam(name = "isNotChangePlan", required = true, defaultValue = "false") Boolean isNotChangePlan) {
        MDC.put("type", "Fetch");
        Double finalAmount = 0.0;
        try {
            finalAmount = autoRenewOrAddonPlanService.getPlanPriceByPlanId(planId,null);
            return finalAmount;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        MDC.remove("type");
        return finalAmount;
    }

    @PostMapping(value = "/saveProcessTransaction")
    public boolean saveProcessTransaction(@RequestBody AirtelCRMRequestDTO airtelCRMRequestDTO, @RequestHeader("Authorization") String token){
        logger.info("********** Inside saveProcessTransaction method **********");
        try {
           return autoRenewOrAddonPlanService.saveProcessTransaction(airtelCRMRequestDTO.getAirtelAppToCRMDTO(), airtelCRMRequestDTO.getCustPayDTOMessage(), token);
        } catch (CustomValidationException ce) {
           logger.error("Validation Error While saveProcessTransaction: {}", ce.getMessage());
           throw ce;
        } catch (Exception ex) {
           logger.error("Unexpected Error While saveProcessTransaction: {}", ex.getMessage());
           throw new RuntimeException("Unexpected error occurred", ex);
        }
    }

    @GetMapping(value = "/getWalletBalanceByCustId/{custId}")
    public Double getWalletBalanceByCustId(@PathVariable Integer custId, @RequestHeader("Authorization") String token){
        logger.info("********** Inside getWalletBalanceByCustId method **********");
        try {
            return autoRenewOrAddonPlanService.checkWalletBalanceByCustId(custId);
        } catch (CustomValidationException ce) {
            logger.error("Validation Error While checkWalletBalanceByCustId: {}", ce.getMessage());
            throw ce;
        } catch (Exception ex) {
            logger.error("Unexpected Error While checkWalletBalanceByCustId: {}", ex.getMessage());
            throw new RuntimeException("Unexpected error occurred", ex);
        }
    }

    @GetMapping(value = "/getDebitDocument/{custId}")
    public List<DebitDocument> getDebitDocumentByCustId(@PathVariable Integer custId) {
        MDC.put("type", "Fetch");
        List<DebitDocument> debitDocuments = new ArrayList<>();
        try {
            debitDocuments = autoRenewOrAddonPlanService.getDebitDocumentByCustId(custId);
        } catch (Exception ex) {
            logger.error("ERROR getting error while getDebitDocumentByCustId method start with custId : {} ", custId);
        }
        return debitDocuments;
    }

    @GetMapping(value = "/getTrailDebitDocument/{custId}")
    public List<TrialDebitDocument> getTrailDebitDocumentByCustId(@PathVariable Integer custId) {
        MDC.put("type", "Fetch");
        List<TrialDebitDocument> trialDebitDocuments = new ArrayList<>();
        try {
            trialDebitDocuments = autoRenewOrAddonPlanService.getTrailDebitDocumentByCustId(custId);
        } catch (Exception ex) {
            logger.error("ERROR getting error while getDebitDocumentByCustId method start with custId : {} ", custId);
        }
        return trialDebitDocuments;
    }

    @PostMapping("/getWalletAmountsByCustomerIds")
    public ResponseEntity<Map<Long, CustomerWalletPojo>> getWalletAmountsByCustomerIds(@RequestBody List<Long> customerIds) {
        if (customerIds == null || customerIds.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.emptyMap());
        }

        try {
            Map<Long, CustomerWalletPojo> walletData = autoRenewOrAddonPlanService.getWalletAmountsByAllCustomerId(customerIds);
            return ResponseEntity.ok(walletData);
        } catch (Exception ex) {
            logger.error("Error fetching wallet balances for customer IDs: " + customerIds, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyMap());
        }


    }

    @RestController
    @RequestMapping("/wallet")
    public class WalletController {

        private final CustomerLedgerDtlsService customerLedgerDtlsService;

        @Autowired
        public WalletController(CustomerLedgerDtlsService customerLedgerDtlsService) {
            this.customerLedgerDtlsService = customerLedgerDtlsService;
        }

    }


    @GetMapping(value = "/getPlanPriceByDebitDocId/{invoiceId}")
    public Double getPlanPriceByDebitDocId(@PathVariable Integer invoiceId, @RequestHeader("Authorization") String token){
        logger.info("********** Inside getPlanPriceByDebitDocId method **********");
        try {
            return autoRenewOrAddonPlanService.getPlanPriceByDebitDocId(invoiceId);
        } catch (CustomValidationException ce) {
            logger.error("Validation Error While getPlanPriceByDebitDocId: {}", ce.getMessage());
            throw ce;
        } catch (Exception ex) {
            logger.error("Unexpected Error While getPlanPriceByDebitDocId: {}", ex.getMessage());
            throw new RuntimeException("Unexpected error occurred", ex);
        }
    }

    @GetMapping(value = "/getPlanNameByDebitDocId/{invoiceId}")
    public String getPlanNameByDebitDocId(@PathVariable Integer invoiceId, @RequestHeader("Authorization") String token){
        logger.info("********** Inside getPlanPriceByDebitDocId method **********");
        try {
            return autoRenewOrAddonPlanService.getPlanNameByDebitDocId(invoiceId);
        } catch (CustomValidationException ce) {
            logger.error("Validation Error While getPlanPriceByDebitDocId: {}", ce.getMessage());
            throw ce;
        } catch (Exception ex) {
            logger.error("Unexpected Error While getPlanPriceByDebitDocId: {}", ex.getMessage());
            throw new RuntimeException("Unexpected error occurred", ex);
        }
    }

}
