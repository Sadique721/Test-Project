package com.savbill.integrationsystem.deviceveri.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.savbill.integrationsystem.deviceveri.domain.CustomerPackageRelData;

@Repository
public interface CustomerPackckageRelRepo extends JpaRepository<CustomerPackageRelData, Long>
{
	List<CustomerPackageRelData> findByCustidAndStartdateBeforeAndEnddateAfterAndIsDeleteFalse(Long custid, LocalDateTime startDate, LocalDateTime endDate);

	List<CustomerPackageRelData> findByCustservicemappingidAndIsDeleteFalse(Long custservicemappingid);

	List<CustomerPackageRelData> findByDebitdocidAndIsDeleteFalse(Long debitdocid);
	
	List<CustomerPackageRelData> findByCustpackageidAndIsDeleteFalse(Long custPackageRelId);

	@Query(value = "select * from tblcustpackagerel where custid=:custId AND service in :serviceNameList AND invoice_type=:invoiceType",nativeQuery = true)
	List<CustomerPackageRelData> findByCustomerIdAndServiceNameListLong(@Param("serviceNameList") List<String> serviceNameList,@Param("custId") Long custId,@Param("invoiceType") String invoiceType);
}
