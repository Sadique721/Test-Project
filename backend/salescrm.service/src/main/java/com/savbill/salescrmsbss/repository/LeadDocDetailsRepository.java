package com.savbill.salescrmsbss.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.LeadDocDetails;
import com.savbill.salescrmsbss.entity.LeadMaster;

@Repository
public interface LeadDocDetailsRepository extends JpaRepository<LeadDocDetails, Long>{

	List<LeadDocDetails> findAllByLeadMasterAndIsDeleteIsFalse(LeadMaster leadMaster);

//	List<LeadDocDetails> findByDocumentNumberAndDocSubTypeAndIsDeleteFalse(String documentNumber, String docSubType);

}
