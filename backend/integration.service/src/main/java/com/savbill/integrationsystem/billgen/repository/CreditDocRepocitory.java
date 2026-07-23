package com.savbill.integrationsystem.billgen.repository;

import com.savbill.integrationsystem.billgen.entity.CreditDocumentData;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditDocRepocitory extends JpaRepository<CreditDocumentData, Integer> {
	@Query (value = "select * from tbltcreditdoc t where LOWER(t.type) = :type and t.is_delete = false and t.ird_sync is null or t.ird_sync = '' or t.ird_sync = :irdSync", nativeQuery = true)
	List<CreditDocumentData> findNotSynched(@Param("irdSync") String irdSync, @Param("type") String type);

	@Query(value = "select * from TBLTCREDITDOC t where t.CREDITDOCID IN :creditDocId order by CREATEDATE DESC", nativeQuery = true)
	List<CreditDocumentData> findAllByCreditDocIdsInCreatedDateDescOrder(@Param("creditDocId") List<Integer> creditDocIds);

	List<CreditDocumentData> findAllByCustomerAndType(Integer customer, String type);
}
