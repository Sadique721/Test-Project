package com.savbill.integrationsystem.mvno;

import com.savbill.integrationsystem.AirtelAppToCRM.RequestDTO.AirtelCRMRequestDTO;
import com.savbill.integrationsystem.PaymentIntegration.DTO.CustomerLedgerDtlsPojo;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.rabbitmq.CreditDocMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "SAVBILLREVENUEMANAGEMENT-SERVICE",contextId = "SAVBILLREVENUEMANAGEMENT-SERVICE")

public interface RevenueClient {

    @PostMapping("/api/v1/Revenue/isp/recordPayment")
    GenericDataDTO ispRecordPayment (@RequestBody PaymentDto paymentDto,@RequestHeader("Authorization") String token);

    @GetMapping("/api/v1/Revenue/customers/getCustomerByAccountNo")
    ResponseEntity<?> getCustomerByAccountNo(@RequestParam(name = "accountNo") String accountNo, @RequestHeader("Authorization") String token);

    @GetMapping("/api/v1/Revenue/customers/getCustomerByPhoneNumber")
    ResponseEntity<?> getCustomerByPhoneNumber(@RequestParam(name = "phoneNumber") String phoneNumber, @RequestHeader("Authorization") String token);

    @GetMapping("/api/v1/Revenue/customers/getCustomerByAccountNoAndPhoneNumber")
    ResponseEntity<?> getCustomerByAccountNoAndPhoneNumber(@RequestParam(name = "accountNo") String accountNo, @RequestParam(name = "phoneNumber") String phoneNumber, @RequestHeader("Authorization") String token);

    @GetMapping("/api/v1/Revenue/accountBalanceByAccountNumber") ResponseEntity<?> getAcountbalance(@RequestParam("accountNo") String accountNumber,@RequestParam("phoneNumber")String phoneNumber, @RequestHeader("Authorization") String token)throws Exception;

    @PostMapping("/api/v1/Revenue/saveProcessTransaction")
    boolean saveProcessTransaction(@RequestBody AirtelCRMRequestDTO airtelCRMRequestDTO, @RequestHeader("Authorization") String token);

    @GetMapping("/api/v1/Revenue/getWalletBalanceByCustId/{custId}")
    Double getWalletBalanceByCustId(@PathVariable Integer custId, @RequestHeader("Authorization") String token);

    @PostMapping("/api/v1/Revenue/walletInternal")
    ResponseEntity<?> fetchPendingAmount(@RequestBody CustomerLedgerDtlsPojo customerLedgerDtlsPojo, @RequestHeader("Authorization") String token);

    @GetMapping("/api/v1/Revenue/getPlanPriceByDebitDocId/{invoiceId}")
    Double getPlanPriceByDebitDocId(@PathVariable Integer invoiceId , @RequestHeader("Authorization")String token);

    @GetMapping("/api/v1/Revenue/getPlanNameByDebitDocId/{invoiceId}")
    String getPlanNameByDebitDocId(@PathVariable Integer invoiceId , @RequestHeader("Authorization")String token);

    @PostMapping("/api/v1/Revenue/addCreditDoc")
    Integer addCreditDoc(@RequestBody CreditDocMessage message, @RequestHeader("Authorization") String token);

}
