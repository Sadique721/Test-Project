package com.savbill.revenuemanagement.core.repository.partner;

import com.savbill.revenuemanagement.core.service.partner.PartnerPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PartnerPaymentRepository extends JpaRepository<PartnerPayment,Long>{
    @Query(value = "select * from tblmpartnerpayment t where date(t.CREATEDATE) between :startDate AND :endDate AND t.partner_id=:partner_id",nativeQuery = true)
    List<PartnerPayment> findAllByStartDateAndEndDateAndPartnerId(@Param("startDate")LocalDate startDate, @Param("endDate")LocalDate endDate, @Param("partner_id")Integer partner_id);

    List<PartnerPayment> findAllByPartner_Id(Integer id);
}
