package com.savbill.integrationsystem.billgen.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.savbill.integrationsystem.billgen.entity.DebitDocument;

@Repository
public interface DebitDocumentRepo extends JpaRepository<DebitDocument, Integer> {

	@Query(value = "select * from TBLTDEBITDOCUMENT t where t.is_delete = false and t.ird_sync is null or t.ird_sync = '' or t.ird_sync = :irdSync", nativeQuery = true)
	List<DebitDocument> findByIrdSyncAndIsDeleteFalse(@Param("irdSync") String irdSync);

	List<DebitDocument> findByIdAndIsDeleteFalse(Integer debitdocid);
}