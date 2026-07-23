package com.savbill.revenuemanagement.core.controller.Customers;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.revenuemanagement.core.auditLog.service.AuditLogService;
import com.savbill.revenuemanagement.core.constants.*;
import com.savbill.revenuemanagement.core.constants.*;
import com.savbill.revenuemanagement.core.controller.invoice.postpaid.MvnoDebitDocDetailsPojo;
import com.savbill.revenuemanagement.core.dto.common.GenericDataDTO;

import com.savbill.revenuemanagement.core.dto.customer.CustomerChangePlanDueAmountDTO;
import com.savbill.revenuemanagement.core.dto.customer.CustomerDto;
import com.savbill.revenuemanagement.core.entity.customers.*;
import com.savbill.revenuemanagement.core.entity.customers.CustPlanMapppingRepository;
import com.savbill.revenuemanagement.core.entity.customers.CustomerAddress;
import com.savbill.revenuemanagement.core.entity.customers.Customers;
import com.savbill.revenuemanagement.core.entity.customers.SubscriberService;
import com.savbill.revenuemanagement.core.exceptions.CustomValidationException;
import com.savbill.revenuemanagement.core.repository.customer.CustomerAddressRepository;
import com.savbill.revenuemanagement.core.repository.customer.CustomerLedgerDtlsPojo;
import com.savbill.revenuemanagement.core.repository.customer.CustomerLedgerInfoPojo;
import com.savbill.revenuemanagement.core.repository.customer.CustomersRepository;
import com.savbill.revenuemanagement.core.security.dto.LoggedInUser;
import com.savbill.revenuemanagement.core.security.spring.SpringContext;
import com.savbill.revenuemanagement.core.service.ClientServ.domain.ClientService;
import com.savbill.revenuemanagement.core.service.ClientServ.repository.ClientServiceRepository;
import com.savbill.revenuemanagement.core.service.customeraddress.CustomerAddressService;
import com.savbill.revenuemanagement.core.service.prepaid.CustomerLedgerAllInfoPojo;
import com.savbill.revenuemanagement.core.service.prepaid.CustomerLedgerDtlsService;
import com.savbill.revenuemanagement.productmanagement.Plan.repository.PostpaidPlanRepo;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.springframework.kafka.listener.ConsumerAwareRebalanceListener.LOGGER;

@RestController
@RequestMapping(UrlConstants.BASE_API_URL)
public class CustomerController {

    private static final Logger logger = Logger.getLogger(CustomerController.class);

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private SubscriberService subscriberService;

    @Autowired
    private AmountCalService amountCalService;

    @Autowired
    ClientServiceRepository clientServiceRepository;

    @Autowired
    private CustomerAddressService customerAddressService;

    @Autowired
    private CustomerAddressRepository customerAddressRepository;

    @Autowired
    private AuditLogService auditLogService;
    @Autowired
    private Tracer tracer;

    @Autowired
    private CustPlanMapppingRepository custPlanMapppingRepository;
    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;

    public String getModuleNameForLog() {
        return "[CustomerController]";
    }

    @GetMapping("/getAddresses/{customerId}")
    public GenericDataDTO getCustomerAddresses(@PathVariable(name = "customerId") Integer customerId) {
        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            logger.info("Fetching addresses for customer id {}");
            List<CustomerAddress> customerDto = subscriberService.getCustomerAddresses(customerId);
            genericDataDTO.setData(customerDto);  // Assuming you have a setData method for a single object
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Successfully fetched customer addresses");
            logger.info("Successfully fetched addresses for customer id {}: Response : {}");
            return genericDataDTO;
           } catch (Exception e) {
            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            logger.error("Unable to fetch addresses for customer id {}: Response : {}; error: {}; exception: {}");
           } finally {
            MDC.remove("type");
           }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.prepaid_invoice_master+  "\" ,\"" + MenuConstants.postpaid_invoice_master +  "\" ,\"" + MenuConstants.pre_cust_invoices  + "\",\"" + MenuConstants.post_cust_invoices  +  "\")")
    @PostMapping("/mvnoInvoice/exportList/{invoiceId}")
    public GenericDataDTO invoiceExportListOfMvnoCustomer(@PathVariable Integer invoiceId, HttpServletRequest request) {
        TraceContext traceContext =tracer.currentSpan().context();
        org.apache.log4j.MDC.put("type", "Fetch");
        org.apache.log4j.MDC.put("userName", getLoggedInUser().getUsername());
        org.apache.log4j.MDC.put("traceId",traceContext.traceIdString());
        org.apache.log4j.MDC.put("spanId",traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        List<MvnoDebitDocDetailsPojo> mvnoDebitDocDetailsPojos = null;
        GenericDataDTO dataDTO=new GenericDataDTO();
        try {
            mvnoDebitDocDetailsPojos = subscriberService.exportCustomerList(invoiceId);
            RESP_CODE = APIConstants.SUCCESS;
            dataDTO.setDataList(mvnoDebitDocDetailsPojos);
            dataDTO.setResponseMessage("Success");
            dataDTO.setResponseCode(RESP_CODE);
            logger.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch Mvno Invoice List by invoiceId "+ invoiceId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }  catch (Exception e) {
            RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            response.put(APIConstants.ERROR_TAG, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            dataDTO.setResponseCode(RESP_CODE);
            dataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            logger.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch Mvno Invoice List by invoiceId "+ invoiceId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }
        finally {
            org.apache.log4j.MDC.remove("type");
            org.apache.log4j.MDC.remove("userName");
            org.apache.log4j.MDC.remove("traceId");
            org.apache.log4j.MDC.remove("spanId");
        }
        return dataDTO;
    }
    public LoggedInUser getLoggedInUser() {
        LoggedInUser user = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                user = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            user = null;
        }
        return user;
    }

    public ResponseEntity<?> apiResponse(Integer responseCode, HashMap<String, Object> response) {
        return apiResponse(responseCode, response);
    }

    @ApiOperation(value = "This API will fetch customer with only required data")
    @GetMapping("/customers/getCustomerByAccountNo")
    public ResponseEntity<?> getCustomerByAccountNo(@RequestParam(name = "accountNo") String accountNo,HttpServletRequest req) throws Exception {
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        org.apache.log4j.MDC.put("type", "Fetch");
        org.apache.log4j.MDC.put("userName",getLoggedInUser().getUsername());
        org.apache.log4j.MDC.put("spanId",traceContext.spanIdString());
        try {
            CustomerDto customerDto = subscriberService.getCustomerByAccountNo(accountNo, getLoggedInUser().getMvnoId());
            ClientService clientService = clientServiceRepository.findByNameAndMvnoId("MOBILE_NUMBER",  getLoggedInUser().getMvnoId());

            CustomerLedgerDtlsPojo pojo= new CustomerLedgerDtlsPojo();
            CustomerLedgerDtlsService customerLedgerDtlsService = SpringContext
                    .getBean(CustomerLedgerDtlsService.class);
            pojo.setCustId(customerDto.getId());
            CustomerLedgerInfoPojo infoPojo = customerLedgerDtlsService.getByTime(pojo);
            CustomerLedgerAllInfoPojo ledgerAllInfoPojo = customerLedgerDtlsService.custInfoBytime(pojo.getCustId(),
                    infoPojo);

            response.put("AccountNo", accountNo);

            List<Object[]> activePlans = getlatestPlanListByCustomerId(customerDto.getId());
            Object[] latestPlan = activePlans.get(0);
            BigDecimal value = (BigDecimal) latestPlan[3];
            LocalDateTime planExpiryDate = ((Timestamp) latestPlan[4]).toLocalDateTime();
            long epochMillis = planExpiryDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

            Double planPrice = value != null ? value.doubleValue() : null;
            Double amount = 0.0;

            if(ledgerAllInfoPojo.getCustomerLedgerInfoPojo()!=null){
                if(ledgerAllInfoPojo.getCustomerLedgerInfoPojo().getClosingBalance() == 0.0){
                    amount = planPrice;
                } else if((-ledgerAllInfoPojo.getCustomerLedgerInfoPojo().getClosingBalance()) > 0.0 && ((-ledgerAllInfoPojo.getCustomerLedgerInfoPojo().getClosingBalance()) < planPrice)) {
                    amount = (planPrice - (-ledgerAllInfoPojo.getCustomerLedgerInfoPojo().getClosingBalance()));
                } else {
                    amount = planPrice;
                }
                response.put("Amount", amount);
            } else {
                response.put("Amount", 0.0);
            }
            response.put("Active",true);
            response.put("Name",customerDto.getFirstname() + " " + customerDto.getLastname());
            response.put("DueDate",epochMillis);
            response.put("mobileNumber", clientService.getValue());

            return ResponseEntity.ok(response);
        } catch (CustomValidationException ce) {
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") +
                    LogConstants.REQUEST_FOR + "fetch all Customer" +
                    LogConstants.REQUEST_BY + getLoggedInUser().getUsername() +
                    LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +
                    LogConstants.LOG_ERROR + ce.getMessage() +
                    LogConstants.LOG_STATUS_CODE + RESP_CODE);
            response.put(APIConstants.ERROR, ce.getMessage());
            return ResponseEntity.status(ce.getErrCode()).body(response);
        } catch (Exception e) {
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") +
                    LogConstants.REQUEST_FOR + "fetch all Customer" +
                    LogConstants.REQUEST_BY + getLoggedInUser().getUsername() +
                    LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +
                    LogConstants.LOG_ERROR + e.getMessage() +
                    LogConstants.LOG_STATUS_CODE + RESP_CODE);
            response.put(APIConstants.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } finally {
            org.apache.log4j.MDC.remove("type");
            org.apache.log4j.MDC.remove("userName");
            org.apache.log4j.MDC.remove("traceId");
            org.apache.log4j.MDC.remove("spanId");
        }
    }


    @ApiOperation(value = "This API will fetch customer with only required data")
    @GetMapping("/customers/getCustomerByPhoneNumber")
    public ResponseEntity<?> getCustomerByPhoneNumber(@RequestParam(name = "phoneNumber") String phoneNumber,HttpServletRequest req) throws Exception {
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        org.apache.log4j.MDC.put("type", "Fetch");
        org.apache.log4j.MDC.put("userName",getLoggedInUser().getUsername());
        org.apache.log4j.MDC.put("spanId",traceContext.spanIdString());
        try {
            CustomerDto customerDto = subscriberService.getCustomerByPhoneNumber(phoneNumber);
            response.put("AccountNo", customerDto.getAcctno());
            response.put("Amount", customerDto.getWalletbalance());

            return ResponseEntity.ok(response);
        } catch (CustomValidationException ce) {
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") +
                    LogConstants.REQUEST_FOR + "fetch all Customer" +
                    LogConstants.REQUEST_BY + getLoggedInUser().getUsername() +
                    LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +
                    LogConstants.LOG_ERROR + ce.getMessage() +
                    LogConstants.LOG_STATUS_CODE + RESP_CODE);
            response.put(APIConstants.ERROR, ce.getMessage());
            return ResponseEntity.status(ce.getErrCode()).body(response);
        } catch (Exception e) {
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") +
                    LogConstants.REQUEST_FOR + "fetch all Customer" +
                    LogConstants.REQUEST_BY + getLoggedInUser().getUsername() +
                    LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +
                    LogConstants.LOG_ERROR + e.getMessage() +
                    LogConstants.LOG_STATUS_CODE + RESP_CODE);
            response.put(APIConstants.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } finally {
            org.apache.log4j.MDC.remove("type");
            org.apache.log4j.MDC.remove("userName");
            org.apache.log4j.MDC.remove("traceId");
            org.apache.log4j.MDC.remove("spanId");
        }
    }

    @ApiOperation(value = "This API will fetch customer with only required data")
    @GetMapping("/customers/getCustomerByAccountNoAndPhoneNumber")
    public ResponseEntity<?> getCustomerByAccountNoAndPhoneNumber(@RequestParam(name = "accountNo") String accountNo, @RequestParam(name = "phoneNumber") String phoneNumber, HttpServletRequest req) throws Exception {
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        org.apache.log4j.MDC.put("type", "Fetch");
        org.apache.log4j.MDC.put("userName",getLoggedInUser().getUsername());
        org.apache.log4j.MDC.put("spanId",traceContext.spanIdString());
        try {
            CustomerDto customerDto = subscriberService.getCustomerByAccountNoAndPhoneNumber(accountNo, phoneNumber);
            response.put("AccountNo", customerDto.getAcctno());
            response.put("Amount", customerDto.getWalletbalance());

            return ResponseEntity.ok(response);
        } catch (CustomValidationException ce) {
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") +
                    LogConstants.REQUEST_FOR + "fetch all Customer" +
                    LogConstants.REQUEST_BY + getLoggedInUser().getUsername() +
                    LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +
                    LogConstants.LOG_ERROR + ce.getMessage() +
                    LogConstants.LOG_STATUS_CODE + RESP_CODE);
            response.put(APIConstants.ERROR, ce.getMessage());
            return ResponseEntity.status(ce.getErrCode()).body(response);
        } catch (Exception e) {
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") +
                    LogConstants.REQUEST_FOR + "fetch all Customer" +
                    LogConstants.REQUEST_BY + getLoggedInUser().getUsername() +
                    LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +
                    LogConstants.LOG_ERROR + e.getMessage() +
                    LogConstants.LOG_STATUS_CODE + RESP_CODE);
            response.put(APIConstants.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } finally {
            org.apache.log4j.MDC.remove("type");
            org.apache.log4j.MDC.remove("userName");
            org.apache.log4j.MDC.remove("traceId");
            org.apache.log4j.MDC.remove("spanId");
        }
    }
    @GetMapping("/accountBalanceByAccountNumber")
    public ResponseEntity<?> getAccountBalance(
            @RequestParam("accountNo") String accountNo,
            @RequestParam(value = "phoneNumber", required=false) String phoneNumber,
            @RequestHeader("Authorization") String token) {
//        HashMap<String, Object> response = new HashMap<>();
        logger.info("Received request to fetch account balance for accountNo: " + accountNo +
                (phoneNumber != null ? ", phoneNumber: " + phoneNumber : ""));
        try {
            List<Customers> customers = customersRepository.findByAcctnoAndMvnoId(accountNo, getLoggedInUser().getMvnoId());
            ClientService clientService = clientServiceRepository.findByNameAndMvnoId("MOBILE_NUMBER",  getLoggedInUser().getMvnoId());
            if (customers.isEmpty()) {
                logger.warn("Customer not found for accountNo: " + accountNo);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found.");
            } else if (customers.size() > 1) {
                logger.warn("Multiple accounts found for accountNo: " + accountNo);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body( "Duplicate Account found.");
            }
            Customers customerDto = customers.get(0);
            if (phoneNumber.startsWith("256")) {
                phoneNumber = phoneNumber.substring(3);
            }
            if (phoneNumber != null && !phoneNumber.equalsIgnoreCase(customerDto.getMobile())) {
                logger.warn("Phone Number is not valid: " + phoneNumber);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Phone Number is not valid.");
            }
            CustomerLedgerDtlsPojo pojo= new CustomerLedgerDtlsPojo();
            CustomerLedgerDtlsService customerLedgerDtlsService = SpringContext
                    .getBean(CustomerLedgerDtlsService.class);
            pojo.setCustId(customerDto.getId());
            CustomerLedgerInfoPojo infoPojo = customerLedgerDtlsService.getByTime(pojo);
            CustomerLedgerAllInfoPojo ledgerAllInfoPojo = customerLedgerDtlsService.custInfoBytime(pojo.getCustId(),
                    infoPojo);
            logger.info("Customer found: " + customerDto.getId());
            AccountBalanceDTO accountBalanceDTO = new AccountBalanceDTO();
            accountBalanceDTO.setAccountNo(customerDto.getAcctno());
            if(ledgerAllInfoPojo.getCustomerLedgerInfoPojo()!=null){
                accountBalanceDTO.setBalance(-ledgerAllInfoPojo.getCustomerLedgerInfoPojo().getClosingBalance());
            } else {
                accountBalanceDTO.setBalance(0.0);
            }
            accountBalanceDTO.setMobileNumber(clientService.getValue());

            List<Object[]> activePlans = getlatestPlanListByCustomerId(customerDto.getId());
            Object[] latestPlan = activePlans.get(0);
            accountBalanceDTO.setPackage(String.valueOf(latestPlan[1]));
            List<LocalDateTime> expiryDates = custPlanMapppingRepository.findLatestExpiryDatesByCustId(customerDto.getId());
            if (!expiryDates.isEmpty()) {
                LocalDateTime latestExpiryDateByCustId = expiryDates.get(0);
                long epochMillis = latestExpiryDateByCustId.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                accountBalanceDTO.setPaymentDue(epochMillis);
                logger.info("Payment due timestamp set: " + epochMillis);
            }

            logger.info("Successfully retrieved account balance for accountNo: " + accountNo);
            return ResponseEntity.status(HttpStatus.OK).body(accountBalanceDTO);
        } catch (Exception e) {
            logger.error("Error occurred while fetching account balance for accountNo: " + accountNo, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong. Please try again later.");
        }
    }

    public List<Object[]> getActivePlanListByCustomerId(Integer custId) {
        List<Object[]> result = custPlanMapppingRepository.findActivePlanDetails(custId,
                Arrays.asList(SubscriberConstants.PLAN_PURCHASE_RENEW,
                        SubscriberConstants.PLAN_PURCHASE_REGISTRATION_AND_RENEWWAL,
                        SubscriberConstants.PLAN_PURCHASE_NEW));
        return result;
    }

    public List<Object[]> getlatestPlanListByCustomerId(Integer custId) {
        List<Object[]> result = custPlanMapppingRepository.findLatestPlanDetails(custId,
                Arrays.asList(SubscriberConstants.PLAN_PURCHASE_RENEW,
                        SubscriberConstants.PLAN_PURCHASE_REGISTRATION_AND_RENEWWAL,
                        SubscriberConstants.PLAN_PURCHASE_NEW));
        logger.info("*************Found for custid " + custId + " latest plan " + result +"*************");
        return result;
    }

    @ApiOperation(value = "This API will fetch customer due amount for change plan")
    @PostMapping("/customers/getCustomerChangePlanDueAmount")
    public ResponseEntity<?> getCustomerChangePlanDueAmount(@RequestBody CustomerChangePlanDueAmountDTO customerChangePlanDueAmountDTO, HttpServletRequest req) throws Exception {
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        org.apache.log4j.MDC.put("type", "Fetch");
        org.apache.log4j.MDC.put("userName",getLoggedInUser().getUsername());
        org.apache.log4j.MDC.put("spanId",traceContext.spanIdString());
        try {
//             Double dueAmount = subscriberService.getDueAmountFromChangePlan(customerChangePlanDueAmountDTO);
             Integer dueAmount = amountCalService.viewAmountForChangePlanOrRenew(customerChangePlanDueAmountDTO);
             response.put("Amount", dueAmount);
             response.put("status", HttpStatus.OK.value());
             response.put("msg", "Due amount fetch successfully");

            return ResponseEntity.ok(response);
        } catch (CustomValidationException ce) {
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") +
                    LogConstants.REQUEST_FOR + "customer due payment " +
                    LogConstants.REQUEST_BY + getLoggedInUser().getUsername() +
                    LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +
                    LogConstants.LOG_ERROR + ce.getMessage() +
                    LogConstants.LOG_STATUS_CODE + RESP_CODE);
            response.put(APIConstants.ERROR, ce.getMessage());
            return ResponseEntity.status(ce.getErrCode()).body(response);
        } catch (Exception e) {
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") +
                    LogConstants.REQUEST_FOR + "customer due payment " +
                    LogConstants.REQUEST_BY + getLoggedInUser().getUsername() +
                    LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +
                    LogConstants.LOG_ERROR + e.getMessage() +
                    LogConstants.LOG_STATUS_CODE + RESP_CODE);
            response.put(APIConstants.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } finally {
            org.apache.log4j.MDC.remove("type");
            org.apache.log4j.MDC.remove("userName");
            org.apache.log4j.MDC.remove("traceId");
            org.apache.log4j.MDC.remove("spanId");
        }
    }
}

