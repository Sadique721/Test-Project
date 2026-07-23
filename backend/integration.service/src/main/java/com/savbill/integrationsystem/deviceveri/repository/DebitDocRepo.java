package com.savbill.integrationsystem.deviceveri.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.savbill.integrationsystem.deviceveri.domain.DebitDocumentData;

@Repository
public interface DebitDocRepo extends JpaRepository<DebitDocumentData, Long>
{
	List<DebitDocumentData> findByCustpackrelidAndIsDelete(Long custpackrelid, Integer isDelete);
	
	List<DebitDocumentData> findByDebitdocumentidAndIsDelete(Long debitdocumentid, Integer isDelete);

	List<DebitDocumentData> findByInventoryMappingIdAndIsDelete(Long inventoryMappingId, Integer isDelete);

	List<DebitDocumentData> findBySubscriberid(Long subscriberId);
}
