package com.savbill.cpm.modules.CaseCustomerDetails.repository;

import com.savbill.cpm.modules.CaseCustomerDetails.model.CaseCustomerDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseCustomerDetailsRepository extends JpaRepository<CaseCustomerDetails,Integer> {
    List<CaseCustomerDetails> findByCaseId(Integer caseId);

    List<CaseCustomerDetails>findByCustomerId(Integer customerId);
}
