package com.savbill.integrationsystem.deviceveri.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.savbill.integrationsystem.deviceveri.domain.CreditDebitMappingData;

@Repository
public interface CreditDebitMappingRepo extends JpaRepository<CreditDebitMappingData, Long>
{
	List<CreditDebitMappingData> findByDebitdocumentidAndIsDeleted(Long debitdocumentid, Integer isDeleted);
	
	List<CreditDebitMappingData> findByCreditdocidAndIsDeleted(Long creditdocid, Integer isDeleted);
}
