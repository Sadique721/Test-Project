package com.savbill.revenuemanagement.core.repository.customer;

import com.savbill.revenuemanagement.core.entity.customers.CustChargeDetails;
import com.savbill.revenuemanagement.core.entity.customers.Customers;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface CustChargeDetailsRepository extends JpaRepository<CustChargeDetails, Integer> {
    List<CustChargeDetails> findAllByCustPlanMapppingIdIn(List<Integer> custPlanIds);
    List<CustChargeDetails> findAllByCustomerId(Integer custId);
    CustChargeDetails findAllByDebitdocid(Long deitDocId);

    List<CustChargeDetails> findAllByCustomerInAndIsUsed(List<Customers> customers, boolean isUsed);

    @Modifying
    @Transactional
    @Query("UPDATE CustChargeDetails c SET " +
            "c.nextInstallmentDate = :nextInstallmentDate, " +
            "c.lastInstallmentDate = :lastInstallmentDate, " +
            "c.installmentNo = :installmentNo " +
            "WHERE c.id = :custChargeId")
    void updateInstallmentDatesAndNo(@Param("nextInstallmentDate") LocalDate nextInstallmentDate,
                                     @Param("lastInstallmentDate") LocalDate lastInstallmentDate,
                                     @Param("installmentNo") Integer installmentNo,
                                     @Param("custChargeId") Integer custChargeId);

}
