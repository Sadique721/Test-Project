package com.savbill.cpm.modules.RvenueClient;

import com.savbill.cpm.model.postpaid.CustomerLedgerDtlsPojo;
import com.savbill.cpm.model.postpaid.DebitDocument;
import com.savbill.cpm.model.postpaid.TrialDebitDocument;
import com.savbill.cpm.modules.DebitDocumentInventoryRel.DebitDocNumberMappingPojo;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class RevenueClientFallback implements RevenueClient {
    @Override
    public ResponseEntity<?> getCafWalletAmount(String token, CustomerLedgerDtlsPojo pojo) {
        return null;
    }

    @Override
    public List<DebitDocNumberMappingPojo> getDebitDocNumber(String token) {
        return Collections.emptyList();
    }

    @Override
    public List<DebitDocument> getDebitDocumentByCustId(Integer custId, String token) {
        return Collections.emptyList();
    }

    @Override
    public List<TrialDebitDocument> getTrailDebitDocumentByCustId(Integer custId, String token) {
        return Collections.emptyList();
    }

    @Override
    public ResponseEntity<Map<String, Object>> getWalletAmount(CustomerLedgerDtlsPojo pojo, String token) {
        return null;
    }

    @Override
    public ResponseEntity<Map<String, Object>> getWalletAmounts(@RequestBody List<CustomerLedgerDtlsPojo> pojoList, @RequestHeader("Authorization") String token) {
        return null;
    }

    @Override
    public ResponseEntity<Map<Integer, List<Double>>>getTaxPercentagesByCustomers(
            @RequestHeader("Authorization") String token,
            @RequestBody List<Integer> customerIds) {
        return null;
    }

    @Override
    public ResponseEntity<Map<Integer, List<String>>>getDebitDocNumber(
            @RequestHeader("Authorization") String token,
            @RequestBody List<Integer> debitdocIds) {
        return null;
    }

}
