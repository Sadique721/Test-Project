package com.savbill.integrationsystem.Migration;

import com.savbill.integrationsystem.AirtelAppToCRM.RequestDTO.AirtelAppToCRMDTO;
import com.savbill.integrationsystem.MtnUssd.MtnBuyPlanDTO;
import com.savbill.integrationsystem.MtnUssd.MtnPlanFetchDTO;
import com.savbill.integrationsystem.MtnUssd.MtnUssdResponseDTO;
import com.savbill.integrationsystem.PaymentIntegration.DTO.CustomerPaymentDTO;
import com.savbill.integrationsystem.PaymentIntegration.DTO.ServiceAreaFetchDTO;
import com.savbill.integrationsystem.PaywayIntigration.TransactionStatusDto;
import com.savbill.integrationsystem.PostpaidPlan.CustomersMigrationPojo;
import com.savbill.integrationsystem.PostpaidPlan.PostpaidPlanMigrationPojo;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.rabbitmq.CreditDocMessage;
import com.savbill.integrationsystem.rabbitmq.CustPayDTOMessage;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@FeignClient(name = "SAVBILLCPMBSS-SERVICE",contextId = "SavbillCustomermanagementService")
public interface CMSClient {



    @PostMapping("/api/v1/cpm/migratePostpaidPlandata")
    GenericDataDTO migratePostPaidPlan(@RequestBody List<PostpaidPlanMigrationPojo> plandata,@RequestHeader("Authorization") String token);
    @PostMapping("/api/v1/cpm/migrateCustomerdata")
    GenericDataDTO migrateCustomer(@RequestBody List<CustomersMigrationPojo> custdata, @RequestHeader("Authorization") String token);

    @PostMapping("/open/mtn/ussd/planFetch")
    MtnUssdResponseDTO mtnPlanFetch(@RequestBody MtnPlanFetchDTO mtnPlanFetchDTO);

    @PostMapping("/open/mtn/ussd/buyPlan")
    MtnUssdResponseDTO mtnBuyPlan(@RequestBody MtnBuyPlanDTO mtnBuyPlanDTO);

    @GetMapping("/api/v1/cpm/customers/getCustomerByMobile")
    ResponseEntity<?> getCustomerByMobileNo(@RequestParam(name = "mobileNo") String mobileNo, @RequestHeader("Authorization") String token);

    @GetMapping("/api/v1/cpm/customers/getCustomerByMobile")
    ResponseEntity<?> getCustomerByEmail(@RequestParam(name = "email") String email, @RequestHeader("Authorization") String token);

    @PostMapping(value="/api/v1/cpm/uploadPlanUpdatebulk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Object> planUpdateBySheet(@RequestPart(value = "file") MultipartFile file, @RequestHeader("Authorization") String token);

    @GetMapping("/api/v1/cpm/downloadPlanUpdatebulk")
    Response downloadPlanSheet(@RequestHeader("Authorization") String token);

    @GetMapping("/api/v1/cpm/customer/planListByAccountNo")
    ResponseEntity<?> getCustomerPlanListByAccountNo(@RequestParam(name = "accountNo") String accountNo, @RequestHeader("Authorization") String token);

    @GetMapping("/api/v1/cpm/getmobilenumber/{custid}")
    String getMobileNumber(@PathVariable("custid") String custid, @RequestHeader("Authorization") String token);

    @PostMapping("api/v1/cpm/getcustomersByAccNumber")
    ResponseEntity<List<AirtelAppToCRMDTO>> getcustomersByAccountNumber(@RequestBody AirtelAppToCRMDTO request, @RequestHeader("Authorization") String token);

    @PostMapping("api/v1/cpm/getCustDetailsByAcctNum")
    ResponseEntity<List<AirtelAppToCRMDTO>> getCustDetailsByAcctNum(@RequestBody AirtelAppToCRMDTO request, @RequestHeader("Authorization") String token);

    @PostMapping("api/v1/cpm/getcustomersbillFetch")
    ResponseEntity<AirtelAppToCRMDTO> getcustomersbillFetch(@RequestBody AirtelAppToCRMDTO request, @RequestHeader("Authorization") String token);

    @GetMapping("api/v1/cpm/getcustomersByAccountNo/{accountNo}")
    ResponseEntity<List<AirtelAppToCRMDTO>> getcustomersByAccNumber(@PathVariable("accountNo") String accountNo,@RequestHeader("Authorization") String authToken);

    @GetMapping("/api/v1/cpm/CustomerByID/{custId}")
    ResponseEntity<List<TransactionStatusDto>> getCustomerByCustId(@PathVariable("custId") String custId, @RequestHeader("Authorization") String authToken);

    @GetMapping("/api/v1/cpm/onlinePayAudit/setUsedHash?hash={hash}")
    ResponseEntity<Map<String, Object>> getPaymentDetailsByHash(@PathVariable("hash") String hash, @RequestHeader("Authorization") String authToken);

    @PostMapping("/api/v1/cpm/customer/upgradePlanByAccountNoAndPlanName")
    ResponseEntity<?> upgradePlanByAccountNoAndPlanName(@RequestParam(name = "accountNo") String accountNo, @RequestParam(name = "packageName") String packageName, @RequestHeader("Authorization") String authToken);

    @PostMapping("/api/v1/cpm/generateAirtelRequestByAccountNumber/{accountNo}/{amount}")
    CustomerPaymentDTO generateAirtelRequestByAccountNumber(@PathVariable("accountNo") String accountNo, @PathVariable("amount") Double amount, @RequestHeader("Authorization") String authToken);

    @PostMapping("/api/v1/cpm/generateMoMoPayRequestByAccountNumber/{accountNo}/{amount}")
    CustomerPaymentDTO generateMoMoPayRequestByAccountNumber(@PathVariable("accountNo") String accountNo, @PathVariable("amount") Double amount, @RequestHeader("Authorization") String authToken);

    @GetMapping("/api/v1/cpm/customers/getAccountNoByCustId")
    Map<String, Object> getAccountNoByCustId(@RequestParam("custId") Integer custId, @RequestHeader("Authorization") String authToken);

    @GetMapping("/api/v1/cpm/customers/getplanPriceByPlanId/{planId}")
    Double getplanPriceByPlanId(@PathVariable Integer planId, @RequestHeader("Authorization") String token);

    @GetMapping("/api/v1/cpm/customers/getplanIdByCustId/{custId}")
    Integer getplanIdByCustId(@PathVariable Integer custId, @RequestHeader("Authorization") String token);
    @PostMapping("/api/v1/cpm/addCustomerPayment")
    boolean addCustomerPayment(@RequestBody CustPayDTOMessage custPayDTOMessage, @RequestHeader("Authorization") String token);

//    @PostMapping("/api/v1/cpm/addCreditDoc")
//    Integer addCreditDoc(@RequestBody CreditDocMessage creditDocMessage, @RequestHeader("Authorization") String token);

    @PostMapping("/api/v1/cpm/getAllPlansByServiceAreaAndTypeInternal")
    ResponseEntity<?> fetchPlanByParameter(@RequestBody ServiceAreaFetchDTO serviceAreaFetchDTO, @RequestHeader("Authorization") String token);

    @GetMapping("/api/v1/cpm/customers/getplanNameByPlanId/{planId}")
    String getPlanNameByPlanId(@PathVariable Integer planId, @RequestHeader("Authorization") String token);


    @GetMapping("/api/v1/cpm/customers/getPlanPriceByCustId/{custId}")
    Double getPlanPriceByCustId(@PathVariable Integer custId, @RequestHeader("Authorization") String token);

    @GetMapping("/api/v1/cpm/getCustomerByOnlyAccountNumber/{accountNumber}")
    ResponseEntity<List<AirtelAppToCRMDTO>> getCustomerByOnlyAccountNumber(@PathVariable("accountNumber") String accountNumber,
                                                                           @RequestHeader("Authorization") String token);

}
