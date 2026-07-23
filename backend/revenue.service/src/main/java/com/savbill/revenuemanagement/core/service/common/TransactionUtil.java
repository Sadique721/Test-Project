package com.savbill.revenuemanagement.core.service.common;

import com.savbill.revenuemanagement.core.constants.CommonConstants;
import com.savbill.revenuemanagement.core.entity.ladger.CreditDebitDocMapping;
import com.savbill.revenuemanagement.core.entity.ladger.CreditDocumentDTOForAdjustment;
import com.savbill.revenuemanagement.core.repository.ledger.CreditDebtMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TransactionUtil {

    @PersistenceContext
    EntityManager entityManager;


    @Autowired
    private CreditDebtMappingRepository creditDebtMappingRepository;

    @Transactional
    public void updateCreditDocuments(List<CreditDocumentDTOForAdjustment> creditDocs) {
        if (creditDocs.isEmpty()) return;

        // Extract IDs and adjusted amounts
        Map<Integer, Double> idToAdjustedAmount = creditDocs.stream()
                .collect(Collectors.toMap(CreditDocumentDTOForAdjustment::getId, CreditDocumentDTOForAdjustment::getAdjustedAmount));

        // Perform batch update
        entityManager.createQuery(
                        "UPDATE CreditDocument c SET c.adjustedAmount = CASE c.id " +
                                idToAdjustedAmount.entrySet().stream()
                                        .map(entry -> "WHEN " + entry.getKey() + " THEN " + entry.getValue())
                                        .collect(Collectors.joining(" ")) +
                                " ELSE c.adjustedAmount END WHERE c.id IN (:ids)")
                .setParameter("ids", idToAdjustedAmount.keySet())
                .executeUpdate();
    }



    @Transactional
    public void markDebitsAsFullyPaid(List<Integer> debitIds) {
        if (!debitIds.isEmpty()) {
            entityManager.createQuery("UPDATE DebitDocument d SET d.paymentStatus = :status WHERE d.id IN :ids")
                    .setParameter("status", CommonConstants.DEBIT_DOC_STATUS.FULLY_PAID)
                    .setParameter("ids", debitIds)
                    .executeUpdate();
        }
    }

    @Transactional
    public void markDebitsAsPartiallyPaid(List<Integer> debitIds) {
        if (!debitIds.isEmpty()) {
            entityManager.createQuery("UPDATE DebitDocument d SET d.paymentStatus = :status WHERE d.id IN :ids")
                    .setParameter("status", CommonConstants.DEBIT_DOC_STATUS.PARTIALY_PAID)
                    .setParameter("ids", debitIds)
                    .executeUpdate();
        }
    }

    @Transactional
    public void markCreditsAsFullyAdjusted(List<Integer> creditIds) {
        if (!creditIds.isEmpty()) {
            entityManager.createQuery("UPDATE CreditDocument c SET c.status = :status WHERE c.id IN :ids")
                    .setParameter("status", CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED)
                    .setParameter("ids", creditIds)
                    .executeUpdate();
        }
    }

    @Transactional
    public void markCreditsAsPartiallyAdjusted(List<Integer> creditIds) {
        if (!creditIds.isEmpty()) {
            entityManager.createQuery("UPDATE CreditDocument c SET c.status = :status WHERE c.id IN :ids")
                    .setParameter("status", CommonConstants.CREDIT_DOC_STATUS.PARTIAL_ADJUSTED)
                    .setParameter("ids", creditIds)
                    .executeUpdate();
        }
    }

    @Transactional
    public void saveCreditDebitMappings(List<CreditDebitDocMapping> mappings) {
        if (!mappings.isEmpty()) {
            creditDebtMappingRepository.saveAll(mappings);
        }
    }

    @Transactional
    public void markTrailDebitsAsFullyPaid(List<Integer> debitIds) {
        if (!debitIds.isEmpty()) {
            entityManager.createQuery("UPDATE TrialDebitDocument d SET d.paymentStatus = :status WHERE d.id IN :ids")
                    .setParameter("status", CommonConstants.DEBIT_DOC_STATUS.FULLY_PAID)
                    .setParameter("ids", debitIds)
                    .executeUpdate();
        }
    }

    @Transactional
    public void markTrailDebitsAsPartiallyPaid(List<Integer> debitIds) {
        if (!debitIds.isEmpty()) {
            entityManager.createQuery("UPDATE TrialDebitDocument d SET d.paymentStatus = :status WHERE d.id IN :ids")
                    .setParameter("status", CommonConstants.DEBIT_DOC_STATUS.PARTIALY_PAID)
                    .setParameter("ids", debitIds)
                    .executeUpdate();
        }
    }

    @Transactional
    public void updateCustomerChargeHistory(List<Integer> custChargehistoryIds ,Double amount  , Double taxAmount) {
        if (!custChargehistoryIds.isEmpty()) {
            entityManager.createQuery("UPDATE CustomerChargeHistory c SET c.chargeAmount = :chargeAmount, c.taxAmount =:taxAmount  WHERE c.id IN :custChargehistoryIds")
                    .setParameter("custChargehistoryIds",custChargehistoryIds)
                    .setParameter("chargeAmount", amount)
                    .setParameter("taxAmount", taxAmount)
                    .executeUpdate();
        }
    }
}
