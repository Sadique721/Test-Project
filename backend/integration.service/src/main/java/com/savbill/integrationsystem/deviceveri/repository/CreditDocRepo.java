package com.savbill.integrationsystem.deviceveri.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.savbill.integrationsystem.deviceveri.domain.CreditDocData;

@Repository
public interface CreditDocRepo extends JpaRepository<CreditDocData, Long>
{
	Optional<CreditDocData> findByIdAndIsDelete(Long creditdocid, Integer isDelete);
	
	List<CreditDocData> findByPaymentdateBetweenAndIsDelete(LocalDateTime paymentDate1, LocalDateTime paymentDate2, Integer isDelete);

	List<CreditDocData> findByCustomerAndIsDeleteOrderByCreatedateDesc(Long custId, Integer isDelete);

	@Query(value = "select * from savbillintegrationsystem.TBLTCREDITDOC t where t.is_delete = false AND t.CREATEDATE  >= :paymentDate1 AND t.CREATEDATE  <= :paymentDate2" , nativeQuery = true)
	List<CreditDocData> findByDateBtw(@Param("paymentDate1")String paymentDate1, @Param("paymentDate2")String paymentDate2);
}
