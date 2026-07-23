package com.savbill.cpm.modules.RvenueClient;

import com.savbill.cpm.model.postpaid.CustomerLedgerDtlsPojo;
import com.savbill.cpm.model.postpaid.DebitDocument;
import com.savbill.cpm.model.postpaid.TrialDebitDocument;
import com.savbill.cpm.modules.DebitDocumentInventoryRel.DebitDocNumberMappingPojo;
import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "SAVBILLREVENUEMANAGEMENT-SERVICE",contextId = "SavbillRevenuemanagementMicroService",fallback = RevenueClientFallback.class)
public interface RevenueClient {
    @PostMapping("/api/v1/Revenue/cafWallet")
  public   ResponseEntity<?> getCafWalletAmount(@RequestHeader("Authorization") String token, @RequestBody CustomerLedgerDtlsPojo pojo);
    @Retryable(
            value = FeignException.class,
            maxAttempts = 1,
            backoff = @Backoff(delay = 1000)
    )
    @GetMapping("/api/v1/Revenue/getIspDebitdocNumbers")
    public List<DebitDocNumberMappingPojo> getDebitDocNumber(@RequestHeader("Authorization") String token);

  @GetMapping("/api/v1/Revenue/getDebitDocument/{custId}")
  public List<DebitDocument> getDebitDocumentByCustId(@PathVariable Integer custId, @RequestHeader("Authorization") String token);

  @GetMapping("/api/v1/Revenue/getTrailDebitDocument/{custId}")
  public List<TrialDebitDocument> getTrailDebitDocumentByCustId(@PathVariable Integer custId, @RequestHeader("Authorization") String token);

  @PostMapping("/api/v1/Revenue/wallet")
  public ResponseEntity<Map<String, Object>> getWalletAmount(@RequestBody CustomerLedgerDtlsPojo pojo, @RequestHeader("Authorization") String token);

//  @GetMapping("/api/v1/Revenue/getCurrentWalletAmountByCustId/{customerId}")
//  public Double getWalletBalanceByCustId(@PathVariable Integer customerId, @RequestHeader("Authorization") String token);

  @PostMapping("/api/v1/Revenue/wallets/list")
  ResponseEntity<Map<String, Object>> getWalletAmounts(
          @RequestBody List<CustomerLedgerDtlsPojo> pojoList,
          @RequestHeader("Authorization") String token
  );
  @PostMapping("/api/v1/Revenue/taxPercentage")
  ResponseEntity<Map<Integer, List<Double>>> getTaxPercentagesByCustomers(
          @RequestHeader("Authorization") String token,
          @RequestBody List<Integer> customerIds
  );

  @PostMapping("/api/v1/Revenue/getDebitdocNumbers")
  ResponseEntity<Map<Integer, List<String>>> getDebitDocNumber(
          @RequestHeader("Authorization") String token,
          @RequestBody List<Integer> debitdocIds
  );

}
